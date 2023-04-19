package org.example;

import java.io.Serializable;
import java.util.Arrays;

public class MessageFormat implements Serializable {
    enum messegeTypes{
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
    public messegeTypes type;
    public int[] number;
    public String[] text;
    public UnoCard unoCard;

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
