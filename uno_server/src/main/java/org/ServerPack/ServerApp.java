package org.ServerPack;

import org.SharedPack.MessageFormat;
import org.SharedPack.UnoCard;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * main server class that handles main operaiton for mangaing database, users nad game
 */
public class ServerApp {
    /**
     * a variables to store port that server connection runs on
     */
    private  int port;
    /**
     * a varaible to store connection manager class
     */
    private ServerConnectionManager connectionManger;
    /**
     * a varaivle to store amount of conncted players
     */
    public  int playersConnected=0;
    /**
     * a variable to store amount od players readt
     */
    public  int playersReady=0;

    /**
     * a boolan that says if turn order is clockwise or counter clockwise
     */
    public  boolean clockOrder=true;
    /**
     * a varaible to store currecnt turn
     */
    public int turn=0;
    /**
     * a varaiable to store top card of table stack
     */
    private UnoCard topCard;

    /**
     * gets top card from table stack
     * @return
     */
    public UnoCard getTopCard() {
        return topCard;
    }

    /**
     * sets topCard of table stack
     * @param topCard
     */
    public void setTopCard(UnoCard topCard) {
        this.topCard = topCard;
    }

    /**
     * a list of player connected to server
     */
    public List<PlayerData> nicks =new ArrayList<PlayerData>();

    /**
     * a varaible to store referacne to data base manager
     */
    public  DataBaseMangaer dataBaseMangaer= new DataBaseMangaer();
    /**
     * a varaible to store if game startes
     */
    public boolean gameStarted=false;

    ServerApp()
    {
    }

    /**
     * a method to start server and server connection
     */
    public  void  startServer()
    {
        connectionManger = new ServerConnectionManager(this);
        try {
            connectionManger.setupServerConnections(this.port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("COULS NOT ESTABLISH CONNECTIONS\n");
            System.exit(-1);
        }
    }


    /**
     * a method to add player to connected players list
     * @param pLayerData
     * @return
     */
    public boolean addPlayer(PlayerData pLayerData)
        {

            System.out.println("add player PLAYERS: "+ this.nicks + "\n");
            this.nicks.add(pLayerData);
            Collections.sort(this.nicks);
            this.playersConnected++;
            return  true;


        }

    /**
     * a method to set port of ther server
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * a method that actiates afer writing STOP in cosole by player
     */

    void stopServer()
    {
        connectionManger.isServerRunning =false;
        System.out.println("STOP");
    }


    /**
     * return amount of players ready
     * @return
     */
    public int getPlayersReady() {
        return playersReady;
    }

    /**
     * sets amount playres ready for game
     * @param playersReady
     */
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


    /**
     * a method handles diconnting player and staritng waiting if the player was active in game
     * @param pLayerData
     * @throws IOException
     */
    public  void disconnectPlayer(PlayerData pLayerData) throws IOException {
        this.nicks.remove(pLayerData);
        this.playersConnected--;

        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.DISCONNECT;
        messageFormat.text= new String[1];
        messageFormat.text[0]= pLayerData.getNick();
        messageFormat.number = new int[1];
        int newAmtReady=this.getPlayersReady();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~playeres reasy: " + newAmtReady);
        if(pLayerData.isReady())
        {
            messageFormat.number[0]=1;
            newAmtReady-=1;
        }
        else {
            messageFormat.number[0] = 0;
        }
        this.connectionManger.sendToAll(messageFormat);
        setPlayersReady(newAmtReady);
    }


    /**
     * an overwrittenr method to used to print server that for debbuging
     * @return
     */
    @Override
    public String toString() {
        return "ServerApp{" +
                "playersConnected=" + playersConnected +
                ", playersReady=" + playersReady +
                ", nicks=" + nicks +
                '}';
    }











    ////////////////////////////////////////// GAME

    /**
     * a method to give card to a specific player and infrom him and onther pplayer about it
     * @param card
     * @param player
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public  void giveCard(UnoCard card, PlayerData player) throws IOException, ClassNotFoundException {
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

    /**
     * a method to deal cards to all players in data base and get card for each player and inform the player about getting this card
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    private void dealCards() throws IOException, ClassNotFoundException, InterruptedException {
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
                System.out.println("deakung cards " + player.getNick() +"\n");
                TimeUnit.MILLISECONDS.sleep(200);
              this.giveCard(card, player);
            }





        }


    }

    /**
     * a mthod to create new game in data base and add players to it
     */
    private void createGame() {
        this.dataBaseMangaer.createNewGame(this.nicks.get(0).getNick());
        for(int i=1;i<this.nicks.size();i++)
        {
            this.dataBaseMangaer.addPlayerToGame(this.nicks.get(i).getNick());
        }
    }

    public boolean isInStratingProces=false;

    /**
     * a mthod taht prepares game for play and infroms players to also prepere game
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private  void startGame() throws IOException, ClassNotFoundException {
        isInStratingProces=true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Collections.sort(this.nicks);
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type=MessageFormat.messegeTypes.START;

        for (PlayerData player: this.nicks
             ) {
            player.setInGame(true);
        }

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
        turn=0;
        this.setTurn();
        isInStratingProces=false;
    }

    /**
     * a method taht set new card on table stack and informs players about it
     */
    private  void setTopCard()
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

    /**
     * a method that set new turn and iforms players about it
     * @throws IOException
     */
    public  void setTurn() throws IOException {



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

    /**
     * a method taht sends to all playres nick of players that takes part in this game
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private  void sendPlayerOrder() throws IOException, ClassNotFoundException {
        System.out.println("NICKS: " + this.nicks + "\n");
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


    /**
     * a method that inreases turn number
     */
    public   void incrTurn()
    {
        this.turn++;
        if(this.turn>=this.nicks.size())
            this.turn=0;
    }

    /**
     * a method that decresing turn number
     */
    public  void decrTurn()
    {
        this.turn--;
        if(this.turn<0)
            this.turn=this.nicks.size()-1;
    }

    /**
     * a method that caclucates new turn in game
     */
    private  void nextTurn()
    {


        if(this.clockOrder)
            incrTurn();
        else
            this.decrTurn();

        this.connectionManger.checkFinishGame();
        if(!this.nicks.get(this.turn).isInGame())
            nextTurn();
    }

    /**
     * a method that checks if player has some cards to play and if not draws for him cards from deck
     * @param playerData
     */
    private void validateHand(PlayerData playerData)
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

            this.drawCard(playerData);

            try {
                TimeUnit.MILLISECONDS.sleep(450);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    /**
     * a method that draws card for a scpecific player
     * @param playerData
     */
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

    /**
     * a mthod taht resshudle deck in data base and infomrs other player about it
     */
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

    /**
     * a method that handles player surreneding from game
     * @param playerData
     * @throws IOException
     */
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

        }
        this.connectionManger.checkFinishGame();
        if(this.gameStarted)
        this.setTurn();




    }

    /**
     * a method that sennds ranking data to player that equested it
     * @param playerData
     */
    public void sendRanking(PlayerData playerData) {

        List<Integer> numbers= this.dataBaseMangaer.getAmtOfWinsRaning();
        List<String> nicks = this.dataBaseMangaer.getListofNicksRanking();

        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.RANKING;
        messageFormat.number = new int[numbers.size()];
        messageFormat.text = new String[nicks.size()];

        for(int i=0;i<nicks.size();i++)
        {
            messageFormat.number[i]= numbers.get(i);
            messageFormat.text[i]= nicks.get(i);
        }

        try {
            this.connectionManger.sendMessage(playerData, messageFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * a method that send communiact to specific players about messseage sent abount antoher player
     * @param playerData
     * @param s
     */
    public void sendChatMess(PlayerData playerData, String s) {
        try {

        MessageFormat messageFormat = new MessageFormat();
        messageFormat.type = MessageFormat.messegeTypes.MESSAGE;
        messageFormat.text = new String[2];
        messageFormat.text[0] = playerData.getNick();
        messageFormat.text[1] = s;
            this.connectionManger.sendExclusice(messageFormat,playerData);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * a method that sends to shut game messeage to all the players when problem occured
     */
    public void shutGame() {

        try {
            MessageFormat messageFormat = new MessageFormat();
            messageFormat.type= MessageFormat.messegeTypes.SHUTGAME;
            this.connectionManger.sendToAll(messageFormat);
            this.gameStarted=false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * a method that send messeages required for a player to have the same data as the other players after reconnecting
     * @param pLayerData
     */
    public void catchUp(PlayerData pLayerData) {

        try {
            int indx=this.connectionManger.findIndexOfWaitList(pLayerData.nick);
            this.connectionManger.waitListCheck.set(indx,true);
            System.out.print("CATCHUP: \n");
            System.out.println(this.nicks);
            MessageFormat messageFormat= new MessageFormat();
            this.sendPlayerOrder();
            messageFormat.type= MessageFormat.messegeTypes.CATCHUP;
            this.connectionManger.sendMessage(pLayerData,messageFormat);
            this.sendCardsInHand(pLayerData);
            this.setTopCard();
            this.setTurn();
           // this.nicks.add(pLayerData);
            pLayerData.setInGame(true);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * a method that send to chosen player messeages with all the cards taht it holds in hand
     * @param pLayerData
     */
    private void sendCardsInHand(PlayerData pLayerData) {
        List<UnoCard> cards= this.dataBaseMangaer.selectFromHand(pLayerData.nick);
        for (UnoCard card: cards
             ) {

            try {
                MessageFormat messageFormat = new MessageFormat();
                messageFormat.type= MessageFormat.messegeTypes.RECIVECARDS;
                messageFormat.unoCard=card;
                this.connectionManger.sendMessage(pLayerData,messageFormat);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * a method to check if nick of user i alrady taken
     * @param nick
     * @param pass
     * @return
     */
    public Boolean validateRegistration(String nick, String pass) {
        Boolean isNotTaken=this.dataBaseMangaer.validateNick(nick);
        if(!isNotTaken)
            return false;

        this.dataBaseMangaer.addPlayer(nick,pass);


        return true;
    }

    /**
     * a method to vlaidate nick and passowrd login
     * @param nick
     * @param pass
     * @return
     */
    public Boolean validateLogin(String nick, String pass) {
        return this.dataBaseMangaer.validatePass(nick,pass);
    }

    /**
     * method to check if a player of that nick is alray logged to server
     * @param nick
     * @return
     */
    public boolean checkIfAlradyLogged(String nick)
    {
        Boolean res=false;
        for (PlayerData player: this.nicks
             ) {
            if(player.getNick().equals(nick))
            {
                res=true;
                for(String nickWaitList: this.connectionManger.waitList)
                {
                    if(nickWaitList.equals(player.getNick()))
                    {
                        res=false;
                        break;
                    }
                }


                break;
            }
        }
        return res;
    }
}
