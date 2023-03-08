package org.example;

import java.io.Serializable;
import java.util.Arrays;

public class MessageFormat implements Serializable {
    static enum messegeTypes{
        MESSAGE,
        CONNECT,
        DISCONNECT,
        SUCCES,
        CONFIRM,
        READY,
        START,
        NEWPLAYER,
        DEALCARDS,
        RECIVECARDS,
        RECIVEVARDCOMMUNICAT,
    }
    messegeTypes type;
    int[] number;
    String[] text;
    UnoCard unoCard;

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
