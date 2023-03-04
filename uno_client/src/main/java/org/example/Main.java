package org.example;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException {




        ClientConnectionManager connManger = new ClientConnectionManager();
        try {
            connManger.testFunction();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }



    }
}