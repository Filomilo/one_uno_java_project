package org.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayerData implements Comparable<PlayerData> {
    String nick;
    boolean isReady=false;
    boolean confirmedMesseage=false;
    boolean inGame=true;
    Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    ClientHandler clientHandler;


    public PlayerData(String nick, Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.nick = nick;
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }

    public PlayerData(String nick) {
        this.nick = nick;
    }


    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public String toString() {
        return "PLayerData{" +
                "nick='" + nick + '\'' +
                ", isReady=" + isReady +
                '}';
    }





    public boolean isConfirmedMesseage() {
        return confirmedMesseage;
    }

    public void setConfirmedMesseage(boolean confirmedMesseage) {
        synchronized (this) {
            this.confirmedMesseage = confirmedMesseage;
        }
    }

    @Override
    public int compareTo(PlayerData o) {
        return this.nick.compareTo(o.nick);
    }


}
