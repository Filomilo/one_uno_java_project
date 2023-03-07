package org.example;

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
    }

    static void sendMessage(PlayerData pLayerData, MessageFormat messageFormat) throws IOException {
        pLayerData.objectOutputStream.writeObject(messageFormat);
    }

    static void sendConfirmationMessage(ObjectOutputStream objectOutputStream) throws IOException {
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type = MessageFormat.messegeTypes.CONFIRM;
        ServerConnectionManager.sendMessage(objectOutputStream, messageFormat);
    }

    static MessageFormat getMesseage(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException, SocketException {
        System.out.println("waiting for messegae");
        MessageFormat messageFormat = new MessageFormat();
        messageFormat = (MessageFormat) objectInputStream.readObject();
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
            PlayerData pLayerData = new PlayerData(messageFormat.text[0], socket, objectOutStream, objectInStream);
            MessageFormat confirmationMessege = new MessageFormat();
            confirmationMessege.type = MessageFormat.messegeTypes.CONFIRM;
            confirmationMessege.number = new int[2];
            confirmationMessege.number[0] = serverApp.playersReady;
            confirmationMessege.number[1] = serverApp.playersConnected;

            System.out.println(pLayerData);


        }

    }


    void setupServerConnections(int port) throws IOException {

        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setSoTimeout(1000);
        while (isServerRunning) {
            try {
                this.connectWithNewPlyer();
            } catch (SocketTimeoutException e) {
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        serverSocket.close();
    }


}
