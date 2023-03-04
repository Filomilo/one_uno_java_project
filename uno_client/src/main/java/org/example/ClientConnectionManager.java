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

    void connectToServer() throws IOException, ClassNotFoundException {
        this.socket = new Socket("localhost", 25565);
        this.outStream= socket.getOutputStream();
        this.objectOutStream= new ObjectOutputStream(this.outStream);
        this.inStream= socket.getInputStream();
        this.objectInStream= new ObjectInputStream(this.inStream);


        MessageFormat message=new MessageFormat();
        message.type=MessageFormat.messegeTypes.CONNECT;
        message.text=this.nick;
        this.sendMessage(message);
        message=null;
        message=this.getMesseage();
        reciverHandler= new ReciverHandler(this);
        reciverHandler.start();

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


    void testFunction() throws IOException, ClassNotFoundException {


        Scanner scaner= new Scanner(System.in);


        this.nick=scaner.nextLine();

        this.connectToServer();

        String text;
        MessageFormat messageFormat = new MessageFormat();
        while(true)
        {

            text=scaner.nextLine();
            if(text.equals("EXIT"))
            {
                break;
            }

            messageFormat.text=text;
            messageFormat.type=MessageFormat.messegeTypes.CONNECT;

            this.sendMessage(messageFormat);

        }
    try {

        this.disconnetFromServer();
    } catch (InterruptedException e) {
       System.out.println(e.getStackTrace());
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





}
