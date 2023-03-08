package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;


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
            System.out.println("send message: ");
            System.out.println(messageFormat);
            System.out.println(" ");
        }
    }

    //static funciton fo receving object messege form chosen stream
    static MessageFormat getMesseage(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException, SocketException {
            MessageFormat messageFormat = new MessageFormat();
            messageFormat = (MessageFormat) objectInputStream.readObject();
            System.out.println("get messegae");
            System.out.println(messageFormat);
            return messageFormat;

    }


//method to get meege form specif player, after getting messege auntamtl send confimation messee to seder
     MessageFormat getMesseage(PlayerData playerData) throws IOException, ClassNotFoundException, SocketException {
        System.out.println("from: " + playerData.getNick());
        MessageFormat messageFormat = ServerConnectionManager.getMesseage(playerData.getObjectInputStream());
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
    waitConfirm(playerData);


    }



    void playCard(PlayerData playerData, UnoCard  unoCard)
    {
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.PLAYCARD;
        messageFormat.text= new String[1];
        messageFormat.text[0]= playerData.nick;
        messageFormat.unoCard = unoCard;
        List<UnoCard> cards= this.serverApp.dataBaseMangaer.selectFromHand(playerData.getNick());
        UnoCard card= cards.get(0);



        try {
            this.sendExclusice(messageFormat, playerData);
            this.serverApp.giveCard(unoCard,playerData);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    // this method is my client thread in order to proces te meessege they received
    public void handleMesseage(PlayerData playerData, MessageFormat messageFormat) throws IOException, ClassNotFoundException {
        switch (messageFormat.type)
        {
            case CONNECT:


                break;
            case CONFIRM:
                playerData.setConfirmedMesseage(true);


                break;

            case READY:

                if(messageFormat.number[0]==1)
                    serverApp.setPlayersReady(serverApp.getPlayersReady()+1);
                else
                    serverApp.setPlayersReady(serverApp.getPlayersReady()-1);

                messageFormat.number = new int[1];
                messageFormat.number[0]=serverApp.getPlayersReady();
                this.sendToAll(messageFormat);

                break;
             case PLAYCARD:
                 List<UnoCard> cards= this.serverApp.dataBaseMangaer.selectFromHand(playerData.getNick());
                 UnoCard card= cards.get(abs(messageFormat.number[0] - cards.size()));
                 this.serverApp.dataBaseMangaer.playCard(playerData.getNick(),abs(messageFormat.number[0] - cards.size()));
                 this.playCard(playerData, card);





                 break;




        }


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

            ClientHandler clientHandler= new ClientHandler(pLayerData,this);
            pLayerData.setClientHandler(clientHandler);
            clientHandler.start();

            boolean res= serverApp.addPlayer(pLayerData);



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
}
