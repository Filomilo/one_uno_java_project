package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientApp {

    String nick;
    List<String> playersInORder= new ArrayList<String>();
    int readyPlayers;
    int connectedPlayers;
    boolean isConnected=false;

    boolean isReady;

    ClientConnectionManager clientConnectionManager = new ClientConnectionManager();

    String ip;
    int port;


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

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    boolean connectWithServer()
    {
        try {
            this.clientConnectionManager.connectToServer(this.getIp(), this.getPort());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return  false;
        }
        return true;

    }

    public boolean getIsGameReady() {
        boolean result=false;
        if(this.readyPlayers==this.connectedPlayers && this.connectedPlayers>1)
            result=true;
        return result;
    }
}


