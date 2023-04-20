
package org.ClientPack;

import org.SharedPack.MessageFormat;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * a class that handles client connections with server
 */
public class ClientConnectionManager {
    /**
     * this variable holds socket for server connection
     */
    private  Socket socket;
    /**
     * this variable hold output stream to server
     */
    private  OutputStream outStream;
    /**
     * this variable hold object output stream to server
     */
    private  ObjectOutputStream objectOutStream;
    /**
     * this variable hold input stream to server
     */
    private   InputStream inStream;
    /**
     * this variable hold object input stream to server
     */
    private   ObjectInputStream objectInStream;
    /**
     * this varabile stores theread of recvier ot recive messege while server is running
     */
    private  ReciverHandler reciverHandler;

    /**
     * this varaible dtermines if messeage was confirmed
     */
    private  boolean confirmedMesseage=false;
    /**
     * this variable hold referacne to main class
     */
    public  ClientApp clientApp;
    /**
     * this boolean determins if conection with serer was succseful
     */
    private  boolean conectionResult;


    /**
     * this constructor setups base class with referacne to main client app
     * @param clientApp
     */
    public ClientConnectionManager(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    /**
     * this method check if conenction result was succesdful
     * @return
     */
    public boolean isConectionResult() {
        return conectionResult;
    }

    /**
     * this method sets connection result
     * @param conectionResult
     */
    public void setConectionResult(boolean conectionResult) {
        this.conectionResult = conectionResult;
    }

    /**
     * this emthod chekcs if client recinved confimed messeage
     * @return
     */
    public boolean isConfirmedMesseage() {
        return confirmedMesseage;
    }

    /**
     * this emthod sends specificly confimed messeage to server
     * @param confirmedMesseage
     */
    public void setConfirmedMesseage(boolean confirmedMesseage) {
        synchronized (clientApp.confirmLock) {
            this.confirmedMesseage = confirmedMesseage;
            this.clientApp.confirmLock.notifyAll();
        }
    }

    /**
     * this methid sends provided messege to server
     * @param messageFormat
     * @throws IOException
     */
    public   void sendMessage(MessageFormat messageFormat) throws IOException {
        this.objectOutStream.writeObject(messageFormat);
        this.objectOutStream.flush();
        this.objectOutStream.reset();
        System.out.println("_____________________________________________send Messegae_______________________________");
        System.out.println(messageFormat);

    }

    public MessageFormat getMesseage() throws IOException, ClassNotFoundException {
        MessageFormat messageFormat= (MessageFormat)this.objectInStream.readObject();
       System.out.println("_____________________________________________get Messegae_______________________________");
       System.out.println(messageFormat);
        if(messageFormat.type != MessageFormat.messegeTypes.CONFIRM && messageFormat.type != MessageFormat.messegeTypes.DISCONNECT&& messageFormat.type != MessageFormat.messegeTypes.TOOMANYPLAYERS && messageFormat.type != MessageFormat.messegeTypes.GAMESATRTED)
        sendConfirm();

        return messageFormat;
    }

    /**
     * this method tries login or register to server and setup conenction thread if it was ssuccesful
     * @param ip
     * @param port
     * @param nick
     * @param pass
     * @param choice
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public boolean connectToServer(String ip, int port, String nick, String pass, int choice) throws  IOException, ClassNotFoundException {
        boolean result = false;
        this.socket = new Socket(ip, port);
        socket.setSoTimeout(100);
        this.outStream = socket.getOutputStream();
        this.objectOutStream = new ObjectOutputStream(this.outStream);
        this.inStream = socket.getInputStream();
        this.objectInStream = new ObjectInputStream(this.inStream);


        MessageFormat message = new MessageFormat();
        message.type = MessageFormat.messegeTypes.CONNECT;
        message.text = new String[2];
        message.text[0] = nick;
        message.text[1]= pass;
        message.number = new int[1];
        message.number[0]=choice;
        this.sendMessage(message);


        reciverHandler = new ReciverHandler(this);
        reciverHandler.start();

        this.waitTillconfirmed();
        System.out.print("FISNED WIOTNG FOR CONFIRM \n");
            result = this.conectionResult;
            if(!result)
            {
                reciverHandler.shoudldRun=false;
            }
            this.setConfirmedMesseage(false);
            return result;
    }


    /**
     * this methdd send disconnect messegage to server and closes all conenctions and connection threads
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public void disconnetFromServer() throws IOException, ClassNotFoundException, InterruptedException {
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

    /**
     * this method goes into loop until gets confimation messeaeg
     */
    private void waitTillconfirmed()
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

    /**
     * this method sends ready status to server
     * @param type
     * @return
     */
    public boolean sendReady(boolean type)
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

    /**
     * this method sens cofiramtion messeage to server
     */
    private void sendConfirm()  {
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.CONFIRM;
        try {
            this.sendMessage(messageFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method hnadles recived messeage and call specific method depending on messeage type
     * @param messageFormat
     */
    public void handleMesseage(MessageFormat messageFormat) {
        switch (messageFormat.type) {
            case CONFIRM:
                this.setConfirmedMesseage(true);

                break;
            case NICKTAKEN:
                this.setConfirmedMesseage(true);
                this.clientApp.guiController.loginView.setRegisterCommunicat("NICK TAKEn");
                break;
            case ALRADYLOGGED:
                this.setConfirmedMesseage(true);
                this.clientApp.guiController.loginView.setRegisterCommunicat("Player alrady logged");
                break;
            case WRONGDATA:
                this.setConfirmedMesseage(true);
                this.clientApp.guiController.loginView.setLoginCommuncat("wrong nick or password");
                break;
            case CONNECT:
                this.setConectionResult(messageFormat.number[0] == 1);
                if (this.isConectionResult()) {
                    clientApp.setReadyPlayers(messageFormat.number[1]);
                    clientApp.setConnectedPlayers(messageFormat.number[2]);
                }
                this.setConfirmedMesseage(true);
                this.clientApp.guiController.succesfulLogin();
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
                if(this.clientApp.isGameLoaded())
                this.clientApp.reciveCard(messageFormat.unoCard);

                break;
            case RECIVEVARDCOMMUNICAT:
                if(this.clientApp.isGameLoaded())
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
                if(this.clientApp.isGameLoaded())
                this.clientApp.setCardOntop(messageFormat.unoCard);

                break;
            case START:
                this.clientApp.setGameStarted(true);
        break;
            case TURN:
                if(this.clientApp.isGameLoaded())
                this.clientApp.setTurn(messageFormat.text[0]);

                break;
            case TOOMANYPLAYERS:

                this.clientApp.handleTooManyPlayers(messageFormat);
                this.setConfirmedMesseage(true);
                this.setConectionResult(false);
                    break;

            case GAMESATRTED:

                this.clientApp.hadleGameAlradyStared(messageFormat);
                this.setConfirmedMesseage(true);
                this.setConectionResult(false);
                break;


            case PLAYCARD:
                if(this.clientApp.isGameLoaded())
                clientApp.procesPlaycard(messageFormat.text[0], messageFormat.unoCard);
                break;
            case FINAL:
                    if(this.clientApp.isGameLoaded())
                    clientApp.managePlayerFinale(messageFormat.text[0]);
                    break;
            case ENDGAME:
                this.clientApp.finishGame(messageFormat.text);
                break;
            case RANKING:

                this.clientApp.handleRankingRecived(messageFormat.text,messageFormat.number);
                break;
            case SURRENDER:
                if(this.clientApp.isGameLoaded())
                this.clientApp.handleSurrender(messageFormat.text[0]);
                break;
            case SWAPTURN:
                if(this.clientApp.isGameLoaded())
                this.clientApp.handleSwapTurn();
                break;
            case MESSAGE:
                if(this.clientApp.isGameLoaded())
                this.clientApp.reciveChatMesseage(messageFormat.text[0],messageFormat.text[1]);
                break;
            case WAITSTART:
                if(this.clientApp.isGameLoaded())
                this.clientApp.startWait(messageFormat.text[0],messageFormat.number[0]);
                break;
            case WAIT:
                if(this.clientApp.isGameLoaded() && this.clientApp.isWaiting())
                this.clientApp.updateWaiting(messageFormat.text[0], messageFormat.number[0]);
                break;
            case SHUTGAME:
                this.clientApp.handleShutDown();
            case CATCHUP:
                this.clientApp.catchup();
                break;
            case STOPWAIT:
                if(this.clientApp.isGameLoaded())
                this.clientApp.stopWait(messageFormat.text[0]);
                break;
            case REGISTER:
                this.clientApp.handleRegistration(messageFormat);
                break;


        }
      //  System.out.println(this.clientApp);

    }

    }





