package org.example;

import java.io.Serializable;

public class MessageFormat implements Serializable {
    static enum messegeTypes{
        MESSAGE,
        CONNECT,
        DISCONNECT,
        SUCCES,

    }
    messegeTypes type;
    int integerVal;
    String text;


}
