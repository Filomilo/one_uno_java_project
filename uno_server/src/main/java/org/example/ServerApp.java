package org.example;

import java.io.IOException;
import java.util.*;

public class ServerApp {
    int port;
    ServerConnectionManager connectionManger;

    int playersConnected=0;
    int playersReady=0;

    List<PlayerData> nicks =new ArrayList<PlayerData>();
    DataBaseMangaer dataBaseMangaer= new DataBaseMangaer();

    public void setPort(int port) {
        this.port = port;
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


    void stopServer()
    {
        connectionManger.isServerRunning=false;
        System.out.println("STOP");
    }

    boolean addPlayer(PlayerData pLayerData)
    {
        // TODO: 08.03.2023 add check if nick is alaredy in databse 
        this.nicks.add(pLayerData);
        Collections.sort(this.nicks);
        boolean res= this.dataBaseMangaer.addPlayer(pLayerData.nick);
        this.playersConnected++;
        return  true;
    }

    void disconnectPlayer(PlayerData pLayerData)
    {
        this.nicks.remove(pLayerData);
        this.playersConnected--;
    }

    void hadleMesseage(PlayerData playerData, MessageFormat messageFormat)
    {
        System.out.println(playerData);
        System.out.println(messageFormat);
    }


    @Override
    public String toString() {
        return "ServerApp{" +
                "playersConnected=" + playersConnected +
                ", playersReady=" + playersReady +
                ", nicks=" + nicks +
                '}';
    }
}
