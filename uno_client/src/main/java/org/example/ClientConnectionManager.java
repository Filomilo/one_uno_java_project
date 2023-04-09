//todo: repaier rproblmeoccutign hwen clint starts to connec t discconr ctonnt and then discconr again

package org.example;

import com.sun.xml.internal.ws.api.model.MEP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
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

    boolean confirmedMesseage=false;

    ClientApp clientApp;

    boolean conectionResult;


    public ClientConnectionManager(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    public boolean isConectionResult() {
        return conectionResult;
    }

    public void setConectionResult(boolean conectionResult) {
        this.conectionResult = conectionResult;
    }

    public boolean isConfirmedMesseage() {
        return confirmedMesseage;
    }

    public void setConfirmedMesseage(boolean confirmedMesseage) {
        synchronized (clientApp.confirmLock) {
            this.confirmedMesseage = confirmedMesseage;
            this.clientApp.confirmLock.notifyAll();
        }
    }

    void sendMessage(MessageFormat messageFormat) throws IOException {
        this.objectOutStream.writeObject(messageFormat);
        this.objectOutStream.flush();
        this.objectOutStream.reset();
        System.out.println("_____________________________________________send Messegae_______________________________");
        System.out.println(messageFormat);

    }

    MessageFormat getMesseage() throws SocketTimeoutException, IOException, ClassNotFoundException {
        MessageFormat messageFormat= (MessageFormat)this.objectInStream.readObject();
       System.out.println("_____________________________________________get Messegae_______________________________");
       System.out.println(messageFormat);
        if(messageFormat.type != MessageFormat.messegeTypes.CONFIRM && messageFormat.type != MessageFormat.messegeTypes.DISCONNECT)
        sendConfirm();

        return messageFormat;
    }

    boolean connectToServer(String ip, int port, String nick) throws IOException, ClassNotFoundException {
        boolean result = false;
        this.socket = new Socket(ip, port);
        socket.setSoTimeout(100);
        this.nick = nick;
        this.outStream = socket.getOutputStream();
        this.objectOutStream = new ObjectOutputStream(this.outStream);
        this.inStream = socket.getInputStream();
        this.objectInStream = new ObjectInputStream(this.inStream);


        MessageFormat message = new MessageFormat();
        message.type = MessageFormat.messegeTypes.CONNECT;
        message.text = new String[1];
        message.text[0] = this.nick;
        this.sendMessage(message);

        reciverHandler = new ReciverHandler(this);
        reciverHandler.start();

        this.waitTillconfirmed();

            result = this.conectionResult;
            this.setConfirmedMesseage(false);
            return result;
    }

    void disconnetFromServer() throws IOException, ClassNotFoundException, InterruptedException {
        MessageFormat message=new MessageFormat();
        message.type=MessageFormat.messegeTypes.DISCONNECT;
        this.sendMessage(message);
        this.reciverHandler.setShoudldRun(false);
        this.reciverHandler.join();
       // System.out.println("waiting");
        objectOutStream.close();
        objectInStream.close();
        inStream.close();
        outStream.close();
        socket.close();
    }


    void sendConfirmation()
    {
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.CONFIRM;
        try {
            this.sendMessage(messageFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void waitTillconfirmed()
    {
        while(!isConfirmedMesseage())
        {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    boolean sendReady(boolean type)
    {

        boolean result=false;
        MessageFormat messageFormat= new MessageFormat();
        messageFormat.type=MessageFormat.messegeTypes.READY;
        messageFormat.number= new int[1];
        messageFormat.number[0]= type?1:0;
        try {
            this.sendMessage(messageFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
this.waitTillconfirmed();

        return  result;
    }

    void sendConfirm()  {
        MessageFormat messageFormat = new MessageFormat();
        ;messageFormat.type= MessageFormat.messegeTypes.CONFIRM;
        try {
            this.sendMessage(messageFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMesseage(MessageFormat messageFormat) {
        switch (messageFormat.type) {
            case CONFIRM:
                this.setConfirmedMesseage(true);

                break;
            case CONNECT:
                this.setConectionResult(messageFormat.number[0] == 1);
                if (this.isConectionResult()) {
                    clientApp.setReadyPlayers(messageFormat.number[1]);
                    clientApp.setConnectedPlayers(messageFormat.number[2]);
                }
                this.setConfirmedMesseage(true);
                break;
            case NEWPLAYER:
                if (messageFormat.number[0]==1)
                    this.clientApp.setConnectedPlayers(this.clientApp.getConnectedPlayers() + 1);
                else
                    this.clientApp.setConnectedPlayers(this.clientApp.getConnectedPlayers() - 1);
                this.sendConfirm();
                break;
            case READY:

                this.clientApp.setReadyPlayers(messageFormat.number[0]);


                    break;
            case RECIVECARDS:
                this.clientApp.reciveCard(messageFormat.unoCard);

                break;
            case RECIVEVARDCOMMUNICAT:
                this.clientApp.giveCardToOpponent(messageFormat.text[0]);

                break;
            case ORDER:
                for (String nick:
                     messageFormat.text) {
                 //   System.out.println("-------------------------" + nick);
                    PlayerData playerData= new PlayerData(nick);
                    if(nick!= clientApp.getNick())
                    this.clientApp.playersInORder.add(playerData);
                //    System.out.println(  this.clientApp.playersInORder);
                }
                
                System.out.println("\n\n\n\n\n");
                System.out.println(this.clientApp.playersInORder);
                break;
            case DISCONNECT:
                this.clientApp.handleDisconnect(messageFormat.text[0],messageFormat.number[0]);
                break;
            case TOPCARD:
                this.clientApp.setCardOntop(messageFormat.unoCard);

                break;
            case START:
                this.clientApp.setGameStarted(true);
        break;
            case TURN:

                this.clientApp.setTurn(messageFormat.text[0]);

                break;

            case PLAYCARD:
                clientApp.procesPlaycard(messageFormat.text[0], messageFormat.unoCard);
                break;
                case FINAL:
                    clientApp.managePlayerFinale(messageFormat.text[0]);
                    break;
            case ENDGAME:
                this.clientApp.finishGame(messageFormat.text);
                break;
            case RANKING:
                this.clientApp.handleRankingRecived(messageFormat.text,messageFormat.number);
                break;
            case SURRENDER:
                this.clientApp.handleSurrender(messageFormat.text[0]);
                break;
            case SWAPTURN:
                this.clientApp.handleSwapTurn();
                break;


        }
      //  System.out.println(this.clientApp);

    }






    }





