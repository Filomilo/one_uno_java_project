package org.example;

import java.io.IOException;
import java.util.Scanner;

public class ServerApp {
    int port;
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
        ServerConnectionManager connectionManger = new ServerConnectionManager(this);
        try {
            connectionManger.setupServerConnections(this.port);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    //TODO: create stop server funciotn
    void stopServer()
    {
        System.out.println("STOP");
    }


}
