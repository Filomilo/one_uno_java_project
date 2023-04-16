package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;
import static java.lang.Math.exp;


public class ServerConnectionManager {

   static final Object messeageLocker= new Object();
    ServerApp serverApp;
    ServerSocket serverSocket;
    //variable that change should close thrad and server
    boolean isServerRunning=true;
    //basic construcor for class
    ServerConnectionManager(ServerApp serverApp) {
        this.serverApp = serverApp;
    }


    //static function for sending object of messeafe to chosen stream
    static void sendMessage(ObjectOutputStream objectOutputStream, MessageFormat messageFormat) throws IOException {
        synchronized (ServerConnectionManager.messeageLocker) {
            objectOutputStream.writeObject(messageFormat);
            objectOutputStream.flush();
            objectOutputStream.reset();
            if(messageFormat.type!= MessageFormat.messegeTypes.CONFIRM){
            System.out.println("send message: ");
            System.out.println(messageFormat);
            System.out.println(" ");}
        }
    }

    //static funciton fo receving object messege form chosen stream
    static MessageFormat getMesseage(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException, SocketException,SocketTimeoutException {

        MessageFormat messageFormat = new MessageFormat();
        try {
            messageFormat = (MessageFormat) objectInputStream.readObject();
            if (messageFormat.type != MessageFormat.messegeTypes.CONFIRM) {
                System.out.println("get messegae");
                System.out.println(messageFormat);
                System.out.println(" ");
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
            return messageFormat;

    }


//method to get meege form specif player, after getting messege auntamtl send confimation messee to seder
     MessageFormat getMesseage(PlayerData playerData) throws IOException, ClassNotFoundException, SocketException {
        System.out.println("from: " + playerData.getNick());
         ;
        MessageFormat messageFormat = ServerConnectionManager.getMesseage(playerData.getObjectInputStream());
         System.out.println(messageFormat);
        if(messageFormat.type!= org.example.MessageFormat.messegeTypes.CONFIRM)
        {
            sendConfirm(playerData);
        }
         return messageFormat;
     }


    // funcion to send Confirmation messege to sender to commnunciate proper communication
    // this function should be always send after receving mesegae
    void sendConfirm(PlayerData playerData) throws IOException {
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.CONFIRM;
        this.sendMessage(playerData, messageFormat);
    }

    //method to wait for cofnirmation from specifc player
    void waitConfirm(PlayerData playerData)
    {
        while(!playerData.isConfirmedMesseage())
        {


            System.out.println("################### waiting for confirmation");
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
    }

    // funciton to send messege to specifcly connected player
    // this method should be used instead of the static to automaticly wait for confirmation method
    void sendMessage(PlayerData playerData, MessageFormat messageFormat) throws IOException {
        System.out.println("To: " + playerData.getNick());
    ServerConnectionManager.sendMessage(playerData.getObjectOutputStream(),messageFormat);
    if(messageFormat.type!= MessageFormat.messegeTypes.TOOMANYPLAYERS && messageFormat.type!= MessageFormat.messegeTypes.GAMESATRTED)
    waitConfirm(playerData);


    }



    void playCard(PlayerData playerData, UnoCard  unoCard , int num) throws IOException, ClassNotFoundException {
        System.out.println(
                this.serverApp.dataBaseMangaer.getPlayerAmtOfCards(playerData.getNick()) + " - " + num +" = " + (this.serverApp.dataBaseMangaer.getPlayerAmtOfCards(playerData.getNick()) - num) + "\n");


        this.serverApp.dataBaseMangaer.playCard(playerData.getNick(), this.serverApp.dataBaseMangaer.getPlayerAmtOfCards(playerData.getNick()) - num +1);

        System.out.println("()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()"+this.serverApp.dataBaseMangaer.getAmtInHand(playerData.getNick()));
        if(this.serverApp.dataBaseMangaer.getAmtInHand(playerData.getNick())==0)
            this.procesFinished(playerData);



        this.checkFinishGame();






        if(unoCard.getType()== UnoCard.UNO_TYPE.REVERSE)
        {
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ REVERSE");
            this.serverApp.clockOrder=!this.serverApp.clockOrder;
            MessageFormat messageFormat =new MessageFormat();
            messageFormat.type=MessageFormat.messegeTypes.SWAPTURN;
            this.sendToAll(messageFormat);

        }





        this.setupNextTurn();

            if (unoCard.getType() == UnoCard.UNO_TYPE.BLOCK)
                this.setupNextTurn();



            MessageFormat messageFormat = new MessageFormat();
            messageFormat.type = MessageFormat.messegeTypes.PLAYCARD;
            messageFormat.text = new String[1];
            messageFormat.text[0] = playerData.getNick();
            messageFormat.unoCard = unoCard;





        try {
            this.serverApp.setTopCard(messageFormat.unoCard);
            this.sendExclusice(messageFormat, playerData);
            switch (unoCard.getType())
            {
                case PLUS4:
                    for(int i=0;i<2;i++) {
                        this.serverApp.giveCard(this.serverApp.dataBaseMangaer.selectMainStack().get(0), this.serverApp.nicks.get(this.serverApp.turn));
                        this.serverApp.dataBaseMangaer.drawCard(this.serverApp.nicks.get(this.serverApp.turn).getNick());
                    }
                case PLUS2:
                    for(int i=0;i<2;i++) {
                        this.serverApp.giveCard(this.serverApp.dataBaseMangaer.selectMainStack().get(0), this.serverApp.nicks.get(this.serverApp.turn));
                        this.serverApp.dataBaseMangaer.drawCard(this.serverApp.nicks.get(this.serverApp.turn).getNick());
                    }
                    break;

            }
            this.serverApp.setTurn();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void checkFinishGame() {
        System.out.printf("Check finsh gmeae \n\n");
        if(this.serverApp.dataBaseMangaer.getAmtActivePlayers()==1)
        {
            this.finishGame();
        }
    }



    public void finishGame() {
        System.out.printf("FINSHIND GMAE \n\n\n\n");
        for (PlayerData player: this.serverApp.nicks
             ) {
            System.out.printf("check finish game: " + player.getNick() + "\n\n");
            if(player.isInGame())
            {
                System.out.printf("set rank: " + player.getNick() + "\n\n" );

                serverApp.dataBaseMangaer.setRank(player.getNick());
            }
        }
        List<String> results= serverApp.dataBaseMangaer.getResults();


        MessageFormat messageFormat= new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.ENDGAME;
        messageFormat.text = new String[results.size()];

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int i=0;
        for (String nick: results
             ) {
            messageFormat.text[i]=nick;
            i++;
        }


        try {
            this.sendToAll(messageFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.serverApp.gameStarted=false;

    }

    private void setupNextTurn() {
        if (this.serverApp.clockOrder)
        this.serverApp.incrTurn();
    else
        this.serverApp.decrTurn();
    if(!this.serverApp.nicks.get(this.serverApp.turn).isInGame())
        this.setupNextTurn();
    }


    private void procesFinished(PlayerData playerData) throws IOException {
        this.serverApp.dataBaseMangaer.setRank(playerData.getNick());
        playerData.setInGame(false);
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.FINAL;
        messageFormat.text=new String[1];
        messageFormat.text[0]= playerData.getNick();
        this.sendToAll(messageFormat);

    }

    // this method is my client thread in order to proces te meessege they received
    public Boolean handleMesseage(PlayerData playerData, MessageFormat messageFormat) throws IOException, ClassNotFoundException {
        switch (messageFormat.type)
        {
            case CONNECT:


                break;
            case CONFIRM:
                playerData.setConfirmedMesseage(true);


                break;

            case READY:

                if(messageFormat.number[0]==1) {
                    serverApp.setPlayersReady(serverApp.getPlayersReady() + 1);
                    playerData.setReady(true);
                }
                else {
                    serverApp.setPlayersReady(serverApp.getPlayersReady() - 1);
                    playerData.setReady(false);
                }

                messageFormat.number = new int[1];
                messageFormat.number[0]=serverApp.getPlayersReady();
                this.sendToAll(messageFormat);

                break;
             case PLAYCARD:
                 this.playCard(playerData,messageFormat.unoCard, messageFormat.number[0]);

                 break;

            case DISCONNECT:
                return false;

            case TOOMANYPLAYERS:
                return false;

            case SURRENDER:
                this.serverApp.surrender(playerData);
                break;

            case RANKING:
                this.serverApp.sendRanking(playerData);
                break;
            case MESSAGE:
                this.serverApp.sendChatMess(playerData,messageFormat.text[0]);
                break;




        }

        return true;
    }

//method that setups connections and data with new connecetd client
    void connectWithNewPlyer() throws IOException, ClassNotFoundException {
        Socket socket = serverSocket.accept();

        InputStream inStream = socket.getInputStream();
        OutputStream outStream = socket.getOutputStream();
        ObjectInputStream objectInStream = new ObjectInputStream(inStream);
        ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);

        MessageFormat messageFormat = getMesseage(objectInStream);
        if (messageFormat.type == MessageFormat.messegeTypes.CONNECT) {
            System.out.println(messageFormat.text[0] + "Connected");
            PlayerData pLayerData = new PlayerData(messageFormat.text[0], socket, objectOutStream, objectInStream);
            boolean res=true;
            ClientHandler clientHandler= new ClientHandler(pLayerData,this);
            pLayerData.setClientHandler(clientHandler);

            System.out.printf("this.serverApp.nicks.size(): " + this.serverApp.nicks.size() + "\n");
            if(this.serverApp.nicks.size() >=8)
            {
                MessageFormat messageFormatDenyAmt=new MessageFormat();
                messageFormatDenyAmt.type= MessageFormat.messegeTypes.TOOMANYPLAYERS;
                this.sendMessage(pLayerData,messageFormatDenyAmt);
                return;
            }
            if(this.serverApp.gameStarted && !this.findIfInWaitList(messageFormat.text[0]))
            {
                MessageFormat messageFormatDenyAmt=new MessageFormat();
                messageFormatDenyAmt.type= MessageFormat.messegeTypes.GAMESATRTED;
                this.sendMessage(pLayerData,messageFormatDenyAmt);
                return;
            }


            clientHandler.start();
        if(res)
            res= serverApp.addPlayer(pLayerData);



            System.out.println(pLayerData);
            MessageFormat confirmationMessege = new MessageFormat();
            confirmationMessege.type = MessageFormat.messegeTypes.CONNECT;

            if(res==false)
            {
                confirmationMessege.number = new int[1];
                confirmationMessege.number[0]=0;
            }
            else {
                confirmationMessege.number = new int[3];
                confirmationMessege.number[0] = 1;
                confirmationMessege.number[1] = serverApp.playersReady;
                confirmationMessege.number[2] = serverApp.playersConnected;

                MessageFormat newPlayerCommunicat = new MessageFormat();
                newPlayerCommunicat.type= MessageFormat.messegeTypes.NEWPLAYER;
                newPlayerCommunicat.number= new int[1];
                newPlayerCommunicat.number[0]=1;

                this.sendExclusice(newPlayerCommunicat,pLayerData);

            }
            sendMessage(pLayerData, confirmationMessege);
            if(this.serverApp.gameStarted)
            {
                this.serverApp.catchUp(pLayerData);
            }
        }

    }

// mrthod that send the same messege to all connected players
    void sendToAll(MessageFormat messageFormat) throws IOException {
        for (PlayerData player:
                this.serverApp.nicks) {
                this.sendMessage(player, messageFormat);
            }
        }
    //method that sends messeages to everyone exlcuding the chosen player
    void sendExclusice(MessageFormat messageFormat, PlayerData playerExclued) throws IOException, ClassNotFoundException {
        for (PlayerData player:
                this.serverApp.nicks) {
            if(player!=playerExclued) {
                this.sendMessage(player, messageFormat);
            }

        }
    }


    void setupServerConnections(int port) throws IOException {

        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setSoTimeout(1000);

        while (isServerRunning) {
            try {
                this.connectWithNewPlyer();
            } catch (SocketTimeoutException e) {
                //e.printStackTrace();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        serverSocket.close();
    }

    private int secWaitLimir=20;

    List<String> waitList= new ArrayList<String>();
    List<Boolean> waitListCheck= new ArrayList<Boolean>();


    int findIndexOfWaitList(String nick)
    {
        int i;
        for(i=0;i<waitList.size();i++)
        {
            if(waitList.get(i).equals(nick))
            {
                break;
            }
        }
    return i;
    }

    boolean findIfInWaitList(String nick)
    {
        int i;
        boolean res=false;
        for(i=0;i<waitList.size();i++)
        {
            if(waitList.get(i).equals(nick))
            {
                res=true;
                break;
            }
        }
        return res;
    }

    public void handleWaitForPlayer(PlayerData playerData) {

        try {
            MessageFormat messageFormat = new MessageFormat();
            messageFormat.type= MessageFormat.messegeTypes.WAITSTART;
            messageFormat.text = new String[1];
            messageFormat.text[0] = playerData.getNick();
            this.sendToAll(messageFormat);
            waitList.add(playerData.nick);
            waitListCheck.add(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int i=0;
        while(true)
        {
            try {
                TimeUnit.SECONDS.sleep(1);



                MessageFormat messageFormat = new MessageFormat();
                messageFormat.type= MessageFormat.messegeTypes.WAIT;
                messageFormat.text = new String[1];
                messageFormat.text[0] = playerData.getNick();
                messageFormat.number = new int[1];
                messageFormat.number[0]= secWaitLimir-i;
                this.sendToAll(messageFormat);
                i++;

                int indx=this.findIndexOfWaitList(playerData.nick);
                if(waitListCheck.get(indx))
                    break;


            if(i>secWaitLimir) {
                this.serverApp.surrender(playerData);
                break;
            }
            System.out.printf(i+": Waiting for " + playerData.getNick() + "\n");
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        int indx=this.findIndexOfWaitList(playerData.nick);
        waitList.remove(indx);
        waitListCheck.remove(indx);
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.STOPWAIT;
        messageFormat.text = new String[1];
        messageFormat.text[0]= playerData.getNick();
        try {
            this.sendExclusice(messageFormat,playerData);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
