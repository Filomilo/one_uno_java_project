package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class ClientConnectionManager {

    String nick;
    Socket socket;
    OutputStream outStream;
    ObjectOutputStream objectOutStream;
    InputStream inStream;
    ObjectInputStream objectInStream;

    ReciverHandler reciverHandler;




    void sendMessage(MessageFormat messageFormat) throws IOException {
        this.objectOutStream.writeObject(messageFormat);
        this.objectOutStream.flush();
        this.objectOutStream.reset();

    }

    MessageFormat getMesseage() throws IOException, ClassNotFoundException {
        MessageFormat messageFormat= (MessageFormat)this.objectInStream.readObject();
        return messageFormat;
    }

    void connectToServer(String ip, int port) throws IOException, ClassNotFoundException {
        this.socket = new Socket(ip, port);
        this.outStream= socket.getOutputStream();
        this.objectOutStream= new ObjectOutputStream(this.outStream);
        this.inStream= socket.getInputStream();
        this.objectInStream= new ObjectInputStream(this.inStream);


        MessageFormat message=new MessageFormat();
        message.type=MessageFormat.messegeTypes.CONNECT;
        message.text=new String[1];
        message.text[0]=this.nick;
        this.sendMessage(message);

    }

    void disconnetFromServer() throws IOException, ClassNotFoundException, InterruptedException {
        MessageFormat message=new MessageFormat();
        message.type=MessageFormat.messegeTypes.DISCONNECT;
        this.sendMessage(message);

        this.reciverHandler.join();
        System.out.println("waiting");
        objectOutStream.close();
        objectInStream.close();
        inStream.close();
        outStream.close();
        socket.close();
    }



/*
        for(int i=0;i<10;i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch ( InterruptedException ex)
            {
                System.out.println(ex.getStackTrace());
            }
            String text =new String("a meesage nr" + i);
            doutStream.writeUTF(text);
            System.out.println("Wrtiirng " + text);
        }
        String text =new String("EXIT");
        doutStream.writeUTF(text);
        */

    }





