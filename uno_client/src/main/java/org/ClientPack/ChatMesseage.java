package org.ClientPack;

/**
 * a class to store information about chat message
 */
public class ChatMesseage {

    /**
     * variable to store information about nick of player that send this message
     */
    private final String nick;
    /**
     * a variable to store content of messeage
     */
    private final String messeage;

    /**
     * getter for nick variable
     */
    public String getNick() {
        return nick;
    }

    /**
     * a getter to get content of messegae
     */

    public String getMesseage() {
        return messeage;
    }

    /**
     * a constructot to set nick and messeage variable
     */
    public ChatMesseage(String nick, String messeage) {
        this.nick = nick;
        this.messeage = messeage;
    }

    /**
     * to strong method to print messege with nick
     */
    @Override
    public String toString() {
        return getNick() + " >> " + getMesseage();
    }
}
