package org.example;

import java.io.IOException;
import java.util.Scanner;

public class ServerApp {
    int port;
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
        System.out.println("On Waht port would you like to start Server: ");
        Scanner scanner=new Scanner(System.in);
        this.port=scanner.nextInt();
        startServer();

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


}
