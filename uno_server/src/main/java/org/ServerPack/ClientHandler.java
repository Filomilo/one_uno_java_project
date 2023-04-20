package org.ServerPack;


import org.SharedPack.MessageFormat;

import java.io.*;
import java.net.SocketException;

/**
 * a class that run thread to handle meseeaes form specific users
 */
public class ClientHandler extends  Thread{

    /**
     * a varaivle to store data about player this thread will handle
     */
    private final PlayerData playerData;
    /**
     * a variable to store referance to instance of server connrtion manager to be able to send and recive messegaes
     */
    private final ServerConnectionManager serverConnectionManager;

    /**
     * a constructor taht set up data for player that instance of thiss class wil hadnle
     * @param playerData
     * @param serverConnectionManager
     */

    public ClientHandler(PlayerData playerData, ServerConnectionManager serverConnectionManager) {
        this.playerData = playerData;
        this.serverConnectionManager = serverConnectionManager;
        this.playerData.clientHandler=this;

    }

    /**
     * a overwrittern method run for the thread
     * ti runs in loop  reciving messeages and from specific player this thread handles and handles messeages in server app
     * the loop will stop when lost connection
     */

    @Override
    public void run() {
        super.run();


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
            catch (SocketException e)
            {
        break;
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
               break;
            }


        }
        try {
            this.playerData.socket.close();
            boolean connectionActive = false;
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


