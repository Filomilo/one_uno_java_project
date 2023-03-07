package org.example;

import java.io.IOException;
import java.util.Scanner;

public class ServerApp {
    int port;
    ServerConnectionManager connectionManger;
    DataBaseMangaer dataBaseMangaer= new DataBaseMangaer();

    public void setPort(int port) {
        this.port = port;
    }

    ServerApp(String[] arg)
    {
        this.port=Integer.parseInt(arg[0]);
        startServer();
    }
    ServerApp(int port)
    {
        this.port=port;
        startServer();
    }
    ServerApp()
    {


    }

    void  startServer()
    {
        connectionManger = new ServerConnectionManager(this);
        try {
            connectionManger.setupServerConnections(this.port);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    //TODO: create stop server funciotn
    void stopServer()
    {
        connectionManger.isServerRunning=false;
        System.out.println("STOP");
    }


}
