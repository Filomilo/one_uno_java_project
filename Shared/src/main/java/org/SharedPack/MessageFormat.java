package org.SharedPack;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a class that both server and clients are suing for communiaton
 */
public class MessageFormat implements Serializable {
    /**
     * enum that hold all typed of messeages sent betwwen server and clients
     */
    public enum messegeTypes{
        MESSAGE,
        CONNECT,
        DISCONNECT,
        CONFIRM,
        READY,
        START,
        NEWPLAYER,
        DEALCARDS,
        RECIVECARDS,
        RECIVEVARDCOMMUNICAT,
        ORDER,
        TOPCARD,
        PLAYCARD,
        TURN,
        SHUFFLE,
        FINAL,
        ENDGAME,
        SURRENDER,
        RANKING,
        SWAPTURN,
        TOOMANYPLAYERS,
        WAIT,
        WAITSTART,
        GAMESATRTED,
        CATCHUP,
        STOPWAIT,
        REGISTER,
        LOGIN,
        NICKTAKEN,
        WRONGDATA,
        ALRADYLOGGED,
        SHUTGAME

    }

    /**
     * varaible that store messeage type of meeage, dpending on type of messeage reciver might exepct difffrent or none additional variables
     */
    public messegeTypes type;
    /**
     * potenial varaible to store numbers in messge
     */
    public int[] number;
    /**
     * potenial variable to store strings in messeage
     */
    public String[] text;
    /**
     * potenial varaible to store uno card in messegae
     */
    public UnoCard unoCard;

    /**
     * an overwirttern method to print data about messeage
     * @return
     */
    @Override
    public String toString() {
        return "MessageFormat{" +
                "type=" + type +
                ", number=" + Arrays.toString(number) +
                ", text=" + Arrays.toString(text) +
                ", unoCard=" + unoCard +
                '}';
    }
}
