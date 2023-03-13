package org.example;

import org.omg.CORBA.TIMEOUT;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException {

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
      //  InterfaceUi interfaceUi = new InterfaceUi(args[0]);
    //interfaceUi.starUi();



    }
}