package org.example;

import java.io.IOException;
import java.sql.Connection;
import java.util.*;

public class ServerApp {
    int port;
    ServerConnectionManager connectionManger;

    int playersConnected=0;
    int playersReady=0;

    List<PlayerData> nicks =new ArrayList<PlayerData>();
    DataBaseMangaer dataBaseMangaer= new DataBaseMangaer();

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

    boolean addPlayer(PlayerData pLayerData)
    {
        this.nicks.add(pLayerData);
        Collections.sort(this.nicks);
        boolean res= this.dataBaseMangaer.addPlayer(pLayerData.nick);
        this.playersConnected++;
        return  true;
    }

    public void setPort(int port) {
        this.port = port;
    }

    //method that shoudl be run when ineting STOP commnad in server terminal to close server
    void stopServer()
    {
        connectionManger.isServerRunning =false;
        System.out.println("STOP");
    }


    public int getPlayersConnected() {
        return playersConnected;
    }

    public void setPlayersConnected(int playersConnected) {
        this.playersConnected = playersConnected;
    }

    public int getPlayersReady() {
        return playersReady;
    }

    public void setPlayersReady(int playersReady) {
        this.playersReady = playersReady;
        System.out.println("PLAYERS READY " + this.playersReady);
        System.out.println("PLAYERS connected " + this.playersConnected);
        if(this.playersReady==this.playersConnected && this.playersConnected>1)
        {
            this.startGame();
        }

    }

    private void startGame() {
    }


/*






    void disconnectPlayer(PlayerData pLayerData)
    {
        this.nicks.remove(pLayerData);
        this.playersConnected--;
    }



    @Override
    public String toString() {
        return "ServerApp{" +
                "playersConnected=" + playersConnected +
                ", playersReady=" + playersReady +
                ", nicks=" + nicks +
                '}';
    }




 */



    ////////////////////////////////////////// GAME
/*

    void giveCard(UnoCard card, PlayerData player) throws IOException, ClassNotFoundException {
        MessageFormat messageFormat = new MessageFormat();
        MessageFormat messageFormatToAll= new MessageFormat();

        messageFormat.type=MessageFormat.messegeTypes.RECIVECARDS;
        messageFormat.unoCard=card;

        messageFormatToAll.type=MessageFormat.messegeTypes.RECIVEVARDCOMMUNICAT;
        messageFormatToAll.text= new String[1];
        messageFormatToAll.text[0]= player.nick;

        this.sendMessage(player,messageFormat);
        this.sendExclusice(messageFormatToAll,player);

    }

    void dealCards() throws IOException, ClassNotFoundException {
        System.out.println("preaping DECK");
        this.dataBaseMangaer.preapreDeck();
        System.out.println("DELAING CARDs");
        this.dataBaseMangaer.dealCards();
        MessageFormat messageFormat= new MessageFormat();
        MessageFormat messageFormatToPlayer= new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.DEALCARDS;
        this.connectionManger.sendToAll(messageFormat);

System.out.println("giving cards");

        for(PlayerData player: this.nicks)
        {
            System.out.println(player.getNick());
            List<UnoCard> unoCardList= this.dataBaseMangaer.selectFromHand(player.getNick());
            for(UnoCard card: unoCardList) {
              this.giveCard(card, player);
            }





        }


    }

    void createGame() {
        this.dataBaseMangaer.createNewGame(this.nicks.get(0).getNick());
        for(int i=1;i<this.nicks.size();i++)
        {
            this.dataBaseMangaer.addPlayerToGame(this.nicks.get(i).getNick());
        }
    }


    void startGame() throws IOException, ClassNotFoundException {
        Collections.sort(this.nicks);
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type=MessageFormat.messegeTypes.START;
        connectionManger.sendToAll(messageFormat);
        this.sendPlayerOrder();
        this.createGame();
        this.dealCards();

    }

     void sendPlayerOrder() throws IOException, ClassNotFoundException {
         for (PlayerData player:this.nicks) {
             List<String> nicks= this.dataBaseMangaer.selectOrderFromPlayer(player.getNick());
             MessageFormat  messageFormat= new MessageFormat();
             messageFormat.type= MessageFormat.messegeTypes.ORDER;
             messageFormat.text= new String[nicks.size()];
             messageFormat.text= nicks.toArray(messageFormat.text);
             ServerConnectionManager.sendMessage(player,messageFormat);

         }



    }

*/
}
