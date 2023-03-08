package org.example;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.Object;
import sun.misc.Lock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClientApp {

    String nick;
    List<PlayerData> playersInORder= new ArrayList<PlayerData>();
    int readyPlayers;
    int connectedPlayers;
    boolean isConnected=false;
    boolean isReady;

    boolean isGameStarted= false;
    ClientConnectionManager clientConnectionManager = new ClientConnectionManager(this);

    String ip;
    int port;

    final Lock confirmLock= new Lock();


    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getReadyPlayers() {
        return readyPlayers;
    }

    public void setReadyPlayers(int readyPlayers) {
        this.readyPlayers = readyPlayers;
    }

    public int getConnectedPlayers() {
        return connectedPlayers;
    }

    public void setConnectedPlayers(int connectedPlayers) {
        this.connectedPlayers = connectedPlayers;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public UnoCard getCardOntop() {
        return cardOntop;
    }

    public void setCardOntop(UnoCard cardOntop) {
        this.cardOntop = cardOntop;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {

        isReady = ready;
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.clientConnectionManager.sendReady(ready);
        this.setReadyPlayers(this.getReadyPlayers()+1);
    }

    boolean connectWithServer()
    {
        boolean res=false;
        try {
           res= this.clientConnectionManager.connectToServer(this.getIp(), this.getPort(), this.nick);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return  false;
        }
        return res;

    }

    boolean vaidateCard(UnoCard unoCard)
    {
        if(unoCard.getColor()== UnoCard.UNO_COLOR.BLACK)
            return true;
        if(unoCard.getNumb()==this.cardOntop.getNumb() || unoCard.getColor()==this.cardOntop.getColor())
        {
            return true;
        }
        return false;
    }
    void playCard(int numbCard)
    {
        if(!vaidateCard(this.cardsInHand.get(numbCard-1)))
        {
            System.out.println("You cant play this card");
            return;
        }

        this.cardsInHand.remove(numbCard-1);

        MessageFormat messageForma = new MessageFormat();
        messageForma.type= MessageFormat.messegeTypes.PLAYCARD;
        messageForma.number= new int[1];
        messageForma.number[0]=numbCard;


        try {
            this.clientConnectionManager.sendMessage(messageForma);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getIsGameReady() {
        boolean result=false;
        if(this.readyPlayers==this.connectedPlayers && this.connectedPlayers>1)
            result=true;
        return result;
    }

    @Override
    public String toString() {
        String string= "ClientApp{" +
                "nick='" + nick + '\n' +
                ", playersInORder=" + playersInORder + '\n'+
                ", readyPlayers=" + readyPlayers +
                ", connectedPlayers=" + connectedPlayers +
                ", playerData=" + playerData + '\n' +
                ", cardOntop=" + cardOntop + '\n' +
                '}' + '\n'+'\n';
        int i=1;
        for (UnoCard card: this.cardsInHand
             ) {
            string= string + i++ +", " + card + '\n';

        }
    return string;
    }

//////////////////////////////////////////////////////// GAME

    List<PlayerData> playerData= new ArrayList<PlayerData>();
    List<UnoCard> cardsInHand= new ArrayList<UnoCard>();

    UnoCard cardOntop=null;



}


