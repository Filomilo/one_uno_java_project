package org.example;

import com.sun.net.httpserver.Authenticator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerConnectionManager {

    boolean isServerRunning=true;
    ServerApp serverApp;
    ServerConnectionManager(ServerApp serverApp)
    {
        this.serverApp=serverApp;
    }

    static void sendMessage(ObjectOutputStream objectOutputStream, MessageFormat messageFormat) throws IOException {
        objectOutputStream.writeObject(messageFormat);
    }

    static MessageFormat getMesseage(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        System.out.println("waiting for messegae");
        MessageFormat messageFormat = new MessageFormat();
        messageFormat= (MessageFormat)objectInputStream.readObject();
        return messageFormat;
    }

    void stopServer()
    {
        this.isServerRunning=false;
        System.out.println("stopoyn");
    }
    void setupServerConnections(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(25565);
        serverSocket.setSoTimeout(1000);
        while(isServerRunning)
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
                    isServerRunning=false;
                }

            }
            catch (SocketTimeoutException e)
            {

            }
            catch(IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        serverSocket.close();
    }





}
