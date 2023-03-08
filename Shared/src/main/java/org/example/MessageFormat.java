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
    }
    messegeTypes type;
    int[] number;
    String[] text;


    @Override
    public String toString() {
        return "MessageFormat{" +
                "type=" + type +
                ", number=" + Arrays.toString(number) +
                ", text=" + Arrays.toString(text) +
                '}';
    }
}
