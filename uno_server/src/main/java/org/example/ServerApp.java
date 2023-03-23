package org.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServerApp {
    int port;
    ServerConnectionManager connectionManger;

    int playersConnected=0;
    int playersReady=0;

    boolean clockOrder=true;
    int turn=0;
    UnoCard topCard;

    public UnoCard getTopCard() {
        return topCard;
    }

    public void setTopCard(UnoCard topCard) {
        this.topCard = topCard;
    }

    List<PlayerData> nicks =new ArrayList<PlayerData>();
    DataBaseMangaer dataBaseMangaer= new DataBaseMangaer();
    private boolean gameStarted=false;

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
        if(this.playersReady==this.playersConnected && this.playersConnected>1 && this.getPlayersReady()>1 && !this.gameStarted)
        {
            try {
                this.gameStarted=true;
                this.startGame();
            } catch (IOException | ClassNotFoundException e) {
                this.gameStarted=false;
                e.printStackTrace();
            }
        }

    }







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











    ////////////////////////////////////////// GAME


    void giveCard(UnoCard card, PlayerData player) throws IOException, ClassNotFoundException {
        MessageFormat messageFormat = new MessageFormat();
        MessageFormat messageFormatToAll= new MessageFormat();

        messageFormat.type=MessageFormat.messegeTypes.RECIVECARDS;
        messageFormat.unoCard=card;

        messageFormatToAll.type=MessageFormat.messegeTypes.RECIVEVARDCOMMUNICAT;
        messageFormatToAll.text= new String[1];
        messageFormatToAll.text[0]= player.nick;

        this.connectionManger.sendMessage(player,messageFormat);
        this.connectionManger.sendExclusice(messageFormatToAll,player);

    }

    void dealCards() throws IOException, ClassNotFoundException, InterruptedException {
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

            //  TimeUnit.SECONDS.sleep(2);

            System.out.println(player.getNick());
            List<UnoCard> unoCardList= this.dataBaseMangaer.selectFromHand(player.getNick());
            for(UnoCard card: unoCardList) {
                TimeUnit.MILLISECONDS.sleep(200);
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

        this.createGame();
        this.sendPlayerOrder();
        connectionManger.sendToAll(messageFormat);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        try {
            this.dealCards();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.setTopCard();

        this.setTurn();

    }

    void setTopCard()
    {
        List<UnoCard> unoCardsTable = new ArrayList<UnoCard>();
        try {
           unoCardsTable = this.dataBaseMangaer.selectTableStack();
            MessageFormat messageForm = new MessageFormat();
            messageForm.type = MessageFormat.messegeTypes.TOPCARD;
            messageForm.unoCard = unoCardsTable.get(0);
            this.connectionManger.sendToAll(messageForm);
            setTopCard(unoCardsTable.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (IndexOutOfBoundsException e)
        {
            System.out.printf("unoCardsTable: " + unoCardsTable.size());
        }

    }


    void setTurn() throws IOException {
        System.out.println("TURA: " + this.turn);
        PlayerData player= this.nicks.get(this.turn);
            validateHand(player);
            MessageFormat messageFormat = new MessageFormat();
            messageFormat.type = MessageFormat.messegeTypes.TURN;
            messageFormat.text = new String[1];
            messageFormat.text[0] = player.nick;
            this.connectionManger.sendToAll(messageFormat);

            /////////////////////////////temporary
        //this.connectionManger.finishGame();
    }

    void sendPlayerOrder() throws IOException, ClassNotFoundException {
        for (PlayerData player : this.nicks) {
            List<String> nicks = this.dataBaseMangaer.selectOrderFromPlayer(player.getNick());
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + nicks);
            MessageFormat messageFormat = new MessageFormat();
            messageFormat.type = MessageFormat.messegeTypes.ORDER;
            messageFormat.text = new String[nicks.size()];

            for(int i=0;i<nicks.size();i++)
            {
                messageFormat.text[i]=nicks.get(i);
            }
            System.out.println(nicks);
            this.connectionManger.sendMessage(player, messageFormat);

        }
    }



        void incrTurn()
    {
        this.turn++;
        if(this.turn>=this.nicks.size())
            this.turn=0;
    }
    void decrTurn()
    {
        this.turn--;
        if(this.turn<0)
            this.turn=this.nicks.size()-1;
    }
    void nextTurn()
    {
        if(this.clockOrder)
            incrTurn();
        else
            this.decrTurn();
    }


    void validateHand(PlayerData playerData)
    {
        UnoCard topCard=this.getTopCard();
        int  validation=1;
        while (true)
        {
            List<UnoCard> hand= this.dataBaseMangaer.selectFromHand(playerData.getNick());
                for (UnoCard card:hand
                     ) {
                    System.out.println(topCard +"====="+ card);



                    if( card.getColor()== UnoCard.UNO_COLOR.BLACK ) {
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  BLACK COLOR");
                        validation = 0;
                        break;
                    }



                    if( topCard.getType()==card.getType() && topCard.getType()!= UnoCard.UNO_TYPE.REGULAR) {
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@TYPE");
                        validation = 0;
                        break;
                    }
                    if(topCard.getType()==UnoCard.UNO_TYPE.REGULAR && card.getType()==UnoCard.UNO_TYPE.REGULAR && topCard.getNumb()==card.getNumb() )
                    {
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ NUMB");
                        validation = 0;
                        break;
                    }
                    if(topCard.getColor()==card.getColor())
                    {
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@COLOR");
                        validation = 0;
                        break;
                    }
                }



            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Validation: "+ validation);
            if(validation==0)
            {
              break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.drawCard(playerData);



        }

    }

    private void drawCard(PlayerData playerData) {
        try {


            if( this.dataBaseMangaer.getNmOnStack()==0)
                this.reshuffleDeck();



            System.out.println("################################################"+this.dataBaseMangaer.selectMainStack());
            this.giveCard(this.dataBaseMangaer.selectMainStack().get(0),playerData );
            this.dataBaseMangaer.drawCard(playerData.getNick());
        } catch (IOException | ClassNotFoundException e) {
           e.printStackTrace();
        }


    }

    private void reshuffleDeck() {
        this.dataBaseMangaer.reshuffleDeck();

        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.SHUFFLE;
        try {
            this.connectionManger.sendToAll(messageFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void surrender(PlayerData playerData) throws IOException {
        this.dataBaseMangaer.surrender(playerData.getNick());
        MessageFormat messageFormat= new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.SURRENDER;
        messageFormat.text = new String[1];
        messageFormat.text[0]=playerData.getNick();
        try {
            this.connectionManger.sendExclusice(messageFormat,playerData);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        playerData.setInGame(false);
        if(this.turn==this.nicks.indexOf(playerData))
        {
            this.nextTurn();
            this.setTurn();
        }



    }
}
