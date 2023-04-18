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
Boolean isLogged=false;



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
        //this.handleLogin();

        System.out.println("stared client handler for  "  + this.playerData.getNick() + " " + this.playerData.getSocket().toString());
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

    private Boolean handleLogin() {
            System.out.println("HNADLE LOGIN \n");
        MessageFormat messageFormat;
        try {

                messageFormat = this.serverConnectionManager.getMesseage(playerData);
                if (messageFormat.type == MessageFormat.messegeTypes.LOGIN || messageFormat.type == MessageFormat.messegeTypes.REGISTER) {
                    System.out.println("STARTING LOGGING");
                    if(messageFormat.type == MessageFormat.messegeTypes.REGISTER) {
                        Boolean validation = this.serverConnectionManager.serverApp.validateRegistration(messageFormat.text[0],messageFormat.text[1]);
                        MessageFormat registrationResult=new MessageFormat();
                        registrationResult.type= MessageFormat.messegeTypes.REGISTER;
                        registrationResult.number = new int[1];
                        if(validation)
                        {
                            registrationResult.number[0] =1;
                            isLogged=true;
                        }
                        else
                            registrationResult.number[0] =0;
                        this.serverConnectionManager.sendMessage(this.playerData,registrationResult);

                }
            }
        } catch (IOException | ClassNotFoundException e) {

        }


        return false;
    }


}


