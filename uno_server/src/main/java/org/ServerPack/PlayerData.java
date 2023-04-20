package org.ServerPack;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * a class that store infomariton about the player and connection with that player
 */
public class PlayerData implements Comparable<PlayerData> {
    /**
     * a varaible that store this player nick
     */
    public String nick;
    /**
     * a variables that store whether or not player is ready
     */
    private  boolean isReady=false;
    /**
     * a varaivle taht store if sent meesaae is confimed
     */
    private boolean confirmedMesseage=false;
    /**
     * a variable taht store wheateher or not player is in gmae
     */
    private  boolean inGame=true;
    /**
     * a varaible to store this player socket
     */
    public Socket socket;
    /**
     * a varaible to store this player output stream
     */
    private final ObjectOutputStream objectOutputStream;
    /**
     * a variable to store this player input stream
     */
    private final ObjectInputStream objectInputStream;
    /**
     * a varaible to store client handle referacne
     */
    public ClientHandler clientHandler;


    /**
     * a constructor taht sets basic neccesery componets
     * @param nick
     * @param socket
     * @param objectOutputStream
     * @param objectInputStream
     */

    public PlayerData(String nick, Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.nick = nick;
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }



    /**
     * a method that return wheather ot not player is in game
     * @return
     */
    public boolean isInGame() {
        return inGame;
    }

    /**
     * a method that sets if player is in game
     * @param inGame
     */
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    /**
     * a method taht return nick of player
     * @return
     */
    public String getNick() {
        return nick;
    }

    /**
     * a method taht sets nick of player
     * @param nick
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * a method that return wheather or not plyaer is ready
     * @return
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * a method that set ready varaible in player
     * @param ready
     */
    public void setReady(boolean ready) {
        isReady = ready;
    }


    /**
     * a method that returns socket for this player
     * @return
     */
    public Socket getSocket() {
        return socket;
    }


    /**
     * a method taht return ovject output stream for this player
     * @return
     */
    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }



    /**
     * a method that return objectinput stream of this player
     * @return
     */
    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }


    /**
     * a method taht set clinent handler for player
     * @param clientHandler
     */
    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * an overwriteen method to print player data
     * @return
     */
    @Override
    public String toString() {
        return "PLayerData{" +
                "nick='" + nick + '\'' +
                ", isReady=" + isReady +
                '}';
    }


    /**
     * a method that check if messeage is cofirmed
     * @return
     */
    public boolean isConfirmedMesseage() {
        return confirmedMesseage;
    }

    /**
     * a method to set confimedMesseage with synchornization
     * @param confirmedMesseage
     */
    public void setConfirmedMesseage(boolean confirmedMesseage) {
        synchronized (this) {
            this.confirmedMesseage = confirmedMesseage;
        }
    }

    /**
     * a overwritten method that compares player data
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(PlayerData o) {
        return this.nick.compareTo(o.nick);
    }


}
