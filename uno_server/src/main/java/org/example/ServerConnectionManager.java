package org.example;

import javax.print.DocFlavor;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ServerConnectionManager {

   static boolean isServerRunning = true;
    ServerApp serverApp;
    ServerSocket serverSocket;

    ServerConnectionManager(ServerApp serverApp) {
        this.serverApp = serverApp;
    }

    static void sendMessage(ObjectOutputStream objectOutputStream, MessageFormat messageFormat) throws IOException {
        objectOutputStream.writeObject(messageFormat);
        objectOutputStream.flush();
        objectOutputStream.reset();

        System.out.println("send message");
        System.out.println(messageFormat);
    }

    static void sendMessage(PlayerData pLayerData, MessageFormat messageFormat) throws IOException {
        sendMessage(pLayerData.objectOutputStream,messageFormat);

    }

    static void sendConfirmationMessage(ObjectOutputStream objectOutputStream) throws IOException {
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type = MessageFormat.messegeTypes.CONFIRM;
        ServerConnectionManager.sendMessage(objectOutputStream, messageFormat);
    }

    static MessageFormat getMesseage(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException, SocketException {
        MessageFormat messageFormat = new MessageFormat();
        messageFormat = (MessageFormat) objectInputStream.readObject();
        System.out.println("get messegae");
        System.out.println(messageFormat);
        return messageFormat;
    }

    void stopServer() {
        ServerConnectionManager.isServerRunning = false;
        System.out.println("stopoyn");
    }

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
            sendMessage(objectOutStream, confirmationMessege);
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        serverSocket.close();
    }


    void sendToAll(MessageFormat messageFormat)
    {
        for (PlayerData player:
             this.serverApp.nicks) {
            try {
                ServerConnectionManager.sendMessage(player.objectOutputStream, messageFormat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void hadleMesseage(PlayerData playerData, MessageFormat messageFormat)
    {
        switch (messageFormat.type) {
            case READY:
                this.sendExclusice(messageFormat, playerData);
                if(messageFormat.number[0]==1)
                serverApp.setPlayersReady(serverApp.getPlayersReady()+1);
                else
                    serverApp.setPlayersReady(serverApp.getPlayersReady()-1);
                break;
        }
    }


    void sendExclusice(MessageFormat messageFormat, PlayerData playerExclued)
    {
        for (PlayerData player:
                this.serverApp.nicks) {
            try {
                if(player!=playerExclued)
                ServerConnectionManager.sendMessage(player.objectOutputStream, messageFormat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
