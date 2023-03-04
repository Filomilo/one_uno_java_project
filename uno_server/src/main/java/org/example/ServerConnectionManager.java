package org.example;

import com.sun.net.httpserver.Authenticator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionManager {

    ServerApp serverApp;
    ServerConnectionManager(ServerApp serverApp)
    {
        this.serverApp=serverApp;
    }

    static void sendMessage(ObjectOutputStream objectOutputStream, MessageFormat messageFormat) throws IOException {
        objectOutputStream.writeObject(messageFormat);
    }

    static MessageFormat getMesseage(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        MessageFormat messageFormat = new MessageFormat();
        messageFormat= (MessageFormat)objectInputStream.readObject();
        return messageFormat;
    }


    void setupServerConnections(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(25565);
        while(true)
        {
            Socket socket= null;
            try{
                socket = serverSocket.accept();
                InputStream inStream= socket.getInputStream();
                OutputStream outStream= socket.getOutputStream();

                ObjectInputStream objectInStream= new ObjectInputStream(inStream);
                ObjectOutputStream objectOutStream= new ObjectOutputStream(outStream);



                MessageFormat messageFormat=ServerConnectionManager.getMesseage(objectInStream);
                if(messageFormat.type== MessageFormat.messegeTypes.CONNECT) {
                    System.out.println("User " + new String(messageFormat.text) + " connected to server");
                    ClientHandler clienthandler = new ClientHandler(socket, objectInStream, objectOutStream,new String(messageFormat.text));
                    messageFormat.type= MessageFormat.messegeTypes.SUCCES;
                    messageFormat.text=new String("Succesfuly connected");
                    ServerConnectionManager.sendMessage(objectOutStream,messageFormat);
                    clienthandler.start();
                }
                else
                {
                    System.out.println("invalid data sent to server");
                    objectOutStream.close();
                    objectInStream.close();
                    inStream.close();
                    outStream.close();
                    socket.close();
                }

            }
            catch(IOException e)
            {
            socket.close();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }





}
