package org.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayerData implements Comparable<PlayerData> {
    String nick;
    boolean isReady=false;

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



    @Override
    public String toString() {
        return "PLayerData{" +
                "nick='" + nick + '\'' +
                ", isReady=" + isReady +
                '}';
    }

    @Override
    public int compareTo(PlayerData o) {
        return this.nick.compareTo(o.nick);
    }
}
