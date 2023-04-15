package org.example;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;

import javax.swing.text.StyledEditorKit;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ClientHandler extends  Thread{

    PlayerData playerData;
    ServerConnectionManager serverConnectionManager;
boolean connectionActive=true;



    public PlayerData getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    public ServerConnectionManager getServerConnectionManager() {
        return serverConnectionManager;
    }

    public void setServerConnectionManager(ServerConnectionManager serverConnectionManager) {
        this.serverConnectionManager = serverConnectionManager;
    }

    public boolean isConnectionActive() {
        return connectionActive;
    }

    public void setConnectionActive(boolean connectionActive) {
        this.connectionActive = connectionActive;
    }

    public ClientHandler(PlayerData playerData, ServerConnectionManager serverConnectionManager) {
        this.playerData = playerData;
        this.serverConnectionManager = serverConnectionManager;
        this.playerData.clientHandler=this;

    }

    @Override
    public void run() {
        super.run();

        while(true)
        {
            try{
                synchronized (playerData) {
                    MessageFormat messageFormat;
                    System.out.println("WAITING FOR MEESSEAGE FROM: " + playerData.getNick());
                    messageFormat = this.serverConnectionManager.getMesseage(this.playerData);
                    Boolean res= this.serverConnectionManager.handleMesseage(playerData, messageFormat);
                    if(!res) {
                        System.out.println("CLOSING CONNETINO WITH " + playerData.getNick());
                        break;
                    }
                }
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
               break;
            }

        }
        try {
            this.playerData.socket.close();
            this.connectionActive=false;
            serverConnectionManager.serverApp.disconnectPlayer(playerData);
            if(this.serverConnectionManager.serverApp.isInStratingProces)
            {
                this.serverConnectionManager.serverApp.shutGame();
            }
            if(this.serverConnectionManager.serverApp.gameStarted)
            this.serverConnectionManager.handleWaitForPlayer(playerData);
        } catch (IOException e) {
         e.printStackTrace();
        }
        System.out.println("END LOOP ");






    }




}


