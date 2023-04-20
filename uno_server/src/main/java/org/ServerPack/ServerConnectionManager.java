package org.ServerPack;

import org.SharedPack.MessageFormat;
import org.SharedPack.UnoCard;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

/**
 * this class is used to control conenctions between server and clients
 */
public class ServerConnectionManager {

    /**
     * this variable holds referacne to serverApp main class
     */
    public ServerApp serverApp;
    /**
     * this varaible holds socket for server
     */
    private ServerSocket serverSocket;

    /**
     * this varaible holds if server is ruuning
     */
    public boolean isServerRunning=true;

    /**
     * construct that sets up serverapp referance for this class
     * @param serverApp
     */
    public ServerConnectionManager(ServerApp serverApp) {
        this.serverApp = serverApp;
    }


    /**
     * this object is used for synrhonization send messegage
     */
    private static final Object sendMessegeLocker= new Object();

    /**
     * this method send messsage to specific stream
     * @param objectOutputStream
     * @param messageFormat
     * @throws IOException
     */
     public static void sendMessage(ObjectOutputStream objectOutputStream, MessageFormat messageFormat) throws IOException {
        synchronized (sendMessegeLocker) {
            objectOutputStream.writeObject(messageFormat);
            objectOutputStream.flush();
            objectOutputStream.reset();
            if(messageFormat.type!= MessageFormat.messegeTypes.CONFIRM){
            System.out.println("send message: ");
            System.out.println(messageFormat);
            System.out.println(" ");}
        }
    }

    /**
     * this method sends messege to specifc stream but with send lock
     * @param objectOutputStream
     * @param messageFormat
     * @throws IOException
     */
    private  void sendMessageWithoutLock(ObjectOutputStream objectOutputStream, MessageFormat messageFormat) throws IOException {
        synchronized (sendMessegeLocker) {
            objectOutputStream.writeObject(messageFormat);
            objectOutputStream.flush();
            objectOutputStream.reset();
            if (messageFormat.type != MessageFormat.messegeTypes.CONFIRM) {
                System.out.println("send message: ");
                System.out.println(messageFormat);
                System.out.println(" ");
            }
        }

    }

    /**
     * this method recives message but from specific stream
     * @param objectInputStream
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private  static MessageFormat getMesseage(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {

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

        }
            return messageFormat;

    }



    /**
     * this method hadles reciving messeage from specific player
     * @param playerData
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public  MessageFormat getMesseage(PlayerData playerData) throws IOException, ClassNotFoundException {
         //synchronized (ServerConnectionManager.messeageLocker) {
             System.out.println("from: " + playerData.getNick());
         MessageFormat messageFormat = ServerConnectionManager.getMesseage(playerData.getObjectInputStream());

             System.out.println(messageFormat);
             System.out.println("$$$$$$$$$$$$$$$$$$ sedning CONFIRm\n");
             if (messageFormat.type != MessageFormat.messegeTypes.CONFIRM) {
                 System.out.println("$$$$$$$$$$$$$$$$$$ sedning CONFIRm\n");
                 sendConfirm(playerData);
             }
             return messageFormat;
      //  }
     }


    // funcion to send Confirmation messege to sender to commnunciate proper communication
    // this function should be always send after receving mesegae

    /**
     * this method send to player specificly confirmation messeage
     * @param playerData
     * @throws IOException
     */
    private void sendConfirm(PlayerData playerData) throws IOException {
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.CONFIRM;
        this.sendMessageWithoutLock(playerData.getObjectOutputStream(), messageFormat);
    }

    //method to wait for cofnirmation from specifc player

    /**
     * this method waits until server get confirmation messegae
     * @param playerData
     */
    private void waitConfirm(PlayerData playerData)
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

    /**
     * this method provides ability to send messege to specific player
     * @param playerData
     * @param messageFormat
     * @throws IOException
     */
    public  void sendMessage(PlayerData playerData, MessageFormat messageFormat) throws IOException {
        synchronized (sendMessegeLocker) {
            System.out.println("Sending To: " + playerData.getNick() + ": " + messageFormat);
            ServerConnectionManager.sendMessage(playerData.getObjectOutputStream(), messageFormat);
            System.out.println("Messege succefuly sent\n");
            if (messageFormat.type != MessageFormat.messegeTypes.TOOMANYPLAYERS && messageFormat.type != MessageFormat.messegeTypes.GAMESATRTED)
                waitConfirm(playerData);
        }


    }


    /**
     * this method hadnles operatino of playing card by player. It updated infomation in databse and also informs other players about pplayed cards
     * and check if it was the last card played by this card
     * @param playerData
     * @param unoCard
     * @param num
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void playCard(PlayerData playerData, UnoCard unoCard , int num) throws IOException, ClassNotFoundException {
        System.out.println(
                this.serverApp.dataBaseMangaer.getPlayerAmtOfCards(playerData.getNick()) + " - " + num +" = " + (this.serverApp.dataBaseMangaer.getPlayerAmtOfCards(playerData.getNick()) - num) + "\n");


        this.serverApp.dataBaseMangaer.playCard(playerData.getNick(), this.serverApp.dataBaseMangaer.getPlayerAmtOfCards(playerData.getNick()) - num +1);

        if(this.serverApp.dataBaseMangaer.getAmtInHand(playerData.getNick())==0)
            this.procesFinished(playerData);



        this.checkFinishGame();






        if(unoCard.getType()== UnoCard.UNO_TYPE.REVERSE)
        {
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


    /**
     * this method checks amount of cards in each hand to determin if game is finished. If it is it starts process of finishing game
     */
    public void checkFinishGame() {
        System.out.print("Check finsh gmeae \n\n");
        if(this.serverApp.dataBaseMangaer.getAmtActivePlayers()==1)
        {
            this.finishGame();
        }
    }


    /**
     * thihs method handles operations to finish now played game
     */
    public void finishGame() {
        System.out.print("FINSHIND GMAE \n\n\n\n");
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
        //this.serverApp.setEveryoneNotReady();

    }

    /**
     * this method calcluates new turn based on order of turns and active players
     */
    private void setupNextTurn() {
        if (this.serverApp.clockOrder)
        this.serverApp.incrTurn();
    else
        this.serverApp.decrTurn();
    if(!this.serverApp.nicks.get(this.serverApp.turn).isInGame())
        this.setupNextTurn();
    }


    /**
     * this method actived specingifc actions in databse and connection to handle player finishing game
     * @param playerData
     * @throws IOException
     */
    private void procesFinished(PlayerData playerData) throws IOException {
        this.serverApp.dataBaseMangaer.setRank(playerData.getNick());
        playerData.setInGame(false);
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.FINAL;
        messageFormat.text=new String[1];
        messageFormat.text[0]= playerData.getNick();
        this.sendToAll(messageFormat);

    }

    /**
     * this method handle messeges activate speecifc method depending on types of messege it recives
     * @param playerData
     * @param messageFormat
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * method setups connecton
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void connectWithNewPlyer() throws IOException, ClassNotFoundException {
        Socket socket = serverSocket.accept();

        InputStream inStream = socket.getInputStream();
        OutputStream outStream = socket.getOutputStream();
        ObjectInputStream objectInStream = new ObjectInputStream(inStream);
        ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);

        MessageFormat messageFormat = getMesseage(objectInStream);
        if (messageFormat.type == MessageFormat.messegeTypes.CONNECT) {
           System.out.println(messageFormat.text[0] + "Connected");
            Boolean validationResult = false;
            if (messageFormat.number[0] == 0) {
                validationResult = this.serverApp.validateRegistration(messageFormat.text[0], messageFormat.text[1]);
                if (!validationResult) {
                    MessageFormat loginValidation = new MessageFormat();
                    loginValidation.type = MessageFormat.messegeTypes.NICKTAKEN;
                    sendMessage(objectOutStream, loginValidation);
                }
            } else {
                validationResult = this.serverApp.validateLogin(messageFormat.text[0], messageFormat.text[1]);
                if (!validationResult) {

                    MessageFormat loginValidation = new MessageFormat();
                    loginValidation.type = MessageFormat.messegeTypes.WRONGDATA;
                    sendMessage(objectOutStream, loginValidation);




                }
            }
            if(validationResult)
            {
                Boolean playerConnectedValidation=this.serverApp.checkIfAlradyLogged(messageFormat.text[0]);
                if(playerConnectedValidation)
                {
                    MessageFormat loginValidation = new MessageFormat();
                    loginValidation.type = MessageFormat.messegeTypes.ALRADYLOGGED;
                    sendMessage(objectOutStream, loginValidation);
                }
                validationResult=!playerConnectedValidation;
            }


            if (validationResult) {

                System.out.println("SUCCESFULY VALIDATED PLAYER\n");
                PlayerData pLayerData = new PlayerData("", socket, objectOutStream, objectInStream);
                boolean res = true;
                ClientHandler clientHandler = new ClientHandler(pLayerData, this);
                pLayerData.setClientHandler(clientHandler);
                pLayerData.setNick(messageFormat.text[0]);
                if (this.serverApp.nicks.size() >= 8) {
                    MessageFormat messageFormatDenyAmt = new MessageFormat();
                    messageFormatDenyAmt.type = MessageFormat.messegeTypes.TOOMANYPLAYERS;
                    this.sendMessage(pLayerData, messageFormatDenyAmt);
                    return;
                }
                if (this.serverApp.gameStarted && !this.findIfInWaitList(messageFormat.text[0])) {
                    MessageFormat messageFormatDenyAmt = new MessageFormat();
                    messageFormatDenyAmt.type = MessageFormat.messegeTypes.GAMESATRTED;
                    this.sendMessage(pLayerData, messageFormatDenyAmt);
                    return;
                }




                if (res)
                    res= serverApp.addPlayer(pLayerData);


                System.out.println("Player connected: " + pLayerData + " \n");
                MessageFormat confirmationMessege = new MessageFormat();
                confirmationMessege.type = MessageFormat.messegeTypes.CONNECT;

                if (!res) {
                    confirmationMessege.number = new int[1];
                    confirmationMessege.number[0] = 0;
                } else {

                    confirmationMessege.number = new int[3];
                    confirmationMessege.number[0] = 1;
                    confirmationMessege.number[1] = serverApp.playersReady;
                    confirmationMessege.number[2] = serverApp.playersConnected;

                    MessageFormat newPlayerCommunicat = new MessageFormat();
                    newPlayerCommunicat.type = MessageFormat.messegeTypes.NEWPLAYER;
                    newPlayerCommunicat.number = new int[1];
                    newPlayerCommunicat.number[0] = 1;

                    System.out.println("sending neplay communicat " + newPlayerCommunicat + "((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((\n");
                    this.sendExclusice(newPlayerCommunicat, pLayerData);

                }
                System.out.println("sending confimatopn messwegae " + confirmationMessege + "********************************************\n\n");
                sendMessageWithoutLock(pLayerData.getObjectOutputStream(), confirmationMessege);
                clientHandler.start();
                if (this.serverApp.gameStarted) {
                    this.serverApp.catchUp(pLayerData);
                }
            }
        }
    }


    /**
     * this mehod sends messege to every connected player
     * @param messageFormat
     * @throws IOException
     */
    public void sendToAll(MessageFormat messageFormat) throws IOException {
        for (PlayerData player:
                this.serverApp.nicks) {
                this.sendMessage(player, messageFormat);
            }
        }

    /**
     * method sends messege to every connected client except for the one provided in argument
     * @param messageFormat
     * @param playerExclued
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void sendExclusice(MessageFormat messageFormat, PlayerData playerExclued) throws IOException, ClassNotFoundException {
        for (PlayerData player:
                this.serverApp.nicks) {
            if(player!=playerExclued) {
                this.sendMessage(player, messageFormat);
            }

        }
    }


    /**
     * method setup serer connections on port provided in argument
     * @param port
     * @throws IOException
     */
    public void setupServerConnections(int port) throws IOException {

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

    /**
     * dynamic list containing nicks of player waiter for by sever
     */
    List<String> waitList= new ArrayList<String>();
    /**
     *  dynamic list conating wiat list status to check if specific player conncted back into game
     */
     List<Boolean> waitListCheck= new ArrayList<Boolean>();


    /**
     * finds and returns index of nick on waitlist
     * @param nick
     * @return
     */
    public int findIndexOfWaitList(String nick)
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

    /**
     * check if players with specific nick is on waitlist
     * @param nick
     * @return
     */
    private boolean findIfInWaitList(String nick)
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

    /**
     * Method setup wait for player. It goes in llop where it sends wait counter to clinents
     * if time runs up then it is treated as a player surrended game
     * if player connets it time limit than it returns to game
     * @param playerData
     */
    public void handleWaitForPlayer(PlayerData playerData) {
        int secWaitLimir = 15;
        try {
            MessageFormat messageFormat = new MessageFormat();
            messageFormat.type= MessageFormat.messegeTypes.WAITSTART;
            messageFormat.text = new String[1];
            messageFormat.text[0] = playerData.getNick();
            messageFormat.number = new int[1];
            messageFormat.number[0]= secWaitLimir;
            this.sendToAll(messageFormat);
            waitList.add(playerData.nick);
            waitListCheck.add(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int i=0;
        boolean res=true;
        while(true)
        {
            try {
                TimeUnit.SECONDS.sleep(1);
                MessageFormat messageFormat = new MessageFormat();
                messageFormat.type= MessageFormat.messegeTypes.WAIT;
                messageFormat.text = new String[1];
                messageFormat.text[0] = playerData.getNick();
                messageFormat.number = new int[1];
                messageFormat.number[0]= secWaitLimir -i;
                this.sendToAll(messageFormat);
                i++;
                int indx=this.findIndexOfWaitList(playerData.nick);
                if(waitListCheck.get(indx)) {
                    break;
                }
            if(i> secWaitLimir) {
                res = false;
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
        stopWaitMesseage(playerData);
        if(!res) {
            try {
                this.serverApp.surrender(playerData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Method sends Messege to clients to stop wiaitng for specific player
     * @param playerData
     */
    private void stopWaitMesseage( PlayerData playerData)
    {
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
