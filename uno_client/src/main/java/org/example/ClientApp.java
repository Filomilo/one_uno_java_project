package org.example;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import sun.misc.Lock;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ClientApp {

    String nick;
    List<PlayerData> playersInORder= new ArrayList<PlayerData>();
    int readyPlayers;
    int connectedPlayers;
    boolean isConnected=false;
    boolean isReady;

    Stack<ChatMesseage> chatLogs= new Stack<ChatMesseage>();

    String rankingNicks[]={};

    int rankingwinsAmt[] ={};

    BooleanProperty isRankingLoaded= new SimpleBooleanProperty(false);


    List<String>lastReults;

    boolean isGameStarted= false;
    ClientConnectionManager clientConnectionManager = new ClientConnectionManager(this);

    String turn = new String();

    String ip;
    int port;

    final Lock confirmLock= new Lock();

    GuiController guiController;

    public ClientApp(GuiController guiController) {
        this.guiController=guiController;
    }

    public ClientApp() {

    }


    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.chatLogs= new Stack<ChatMesseage>();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("----------------------------STARTING------------------------");
        this.guiController.startGame();
       // isGameStarted = gameStarted;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getReadyPlayers() {
        return readyPlayers;
    }

    public void setReadyPlayers(int readyPlayers) {
        this.readyPlayers = readyPlayers;
        this.guiController.mainVew.setPlayersReady(this.readyPlayers, this.connectedPlayers);
    }

    public int getConnectedPlayers() {
        return connectedPlayers;
    }

    public void setConnectedPlayers(int connectedPlayers) {
        System.out.println("\n\n\n\n\n\n " + this.readyPlayers + "___" + this.readyPlayers);
        this.connectedPlayers = connectedPlayers;
        this.guiController.mainVew.setPlayersReady(this.readyPlayers, this.connectedPlayers);
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public UnoCard getCardOntop() {
        return cardOntop;
    }

    public void setCardOntop(UnoCard cardOntop) {
        this.guiController.gameView.setCardOnTable(cardOntop);
        this.cardOntop = cardOntop;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {

        isReady = ready;


        if(ready){
        this.setReadyPlayers(this.getReadyPlayers()+1);
            this.clientConnectionManager.sendReady(ready);
        }
        else
        {

            this.setReadyPlayers(this.getReadyPlayers()-1);
            this.clientConnectionManager.sendReady(false);
        }

    }

    boolean connectWithServer()
    {
        boolean res=false;
        try {
           res= this.clientConnectionManager.connectToServer(this.getIp(), this.getPort(), this.nick);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return  false;
        }
        return res;

    }

    boolean vaidateCard(UnoCard unoCard)
    {
        if(unoCard.getType()== UnoCard.UNO_TYPE.COLOR || unoCard.getType()== UnoCard.UNO_TYPE.PLUS4)
            return true;

        if(unoCard.getType()!= UnoCard.UNO_TYPE.REGULAR)
        {
            if(unoCard.getType()== this.cardOntop.getType())
            {
                return  true;
            }
        }
        else
        {
           if( unoCard.getNumb()==this.cardOntop.getNumb())
               return true;
        }

        if(unoCard.getColor()== UnoCard.UNO_COLOR.BLACK)
            return true;
        else
        {
            if(unoCard.getColor()==this.cardOntop.getColor())
            {
                return true;
            }
        }

        return false;
    }
    void playCard(int numbCard, UnoCard card )
    {
        if(!vaidateCard(this.cardsInHand.get(numbCard-1)) )
        {
            System.out.println("You cant play this card");
            System.out.println(card);
            System.exit(-1);
        }

        this.cardsInHand.remove( numbCard-1);

        MessageFormat messageForma = new MessageFormat();


        messageForma.type= MessageFormat.messegeTypes.PLAYCARD;
        messageForma.number= new int[1];
        messageForma.number[0]=numbCard;
       messageForma.unoCard=card;

        try {
            this.clientConnectionManager.sendMessage(messageForma);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.cardOntop=card;
    }

    public boolean getIsGameReady() {
        boolean result=false;
        if(this.readyPlayers==this.connectedPlayers && this.connectedPlayers>1)
            result=true;
        return result;
    }


    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {

                        guiController.gameView.setTurn(turn);
                    }
                }
        );

    }

    @Override
    public String toString() {
        String string= "ClientApp{" +
                "nick='" + nick + '\n' +
                ", playersInORder=" + playersInORder + '\n'+
                ", readyPlayers=" + readyPlayers +
                ", connectedPlayers=" + connectedPlayers +
                ", playerData=" + playerData + '\n' +
                ", cardOntop=" + cardOntop + '\n' +
                "Tura: " + this. getTurn() + '\n' +
                '}' + '\n'+'\n';
        int i=1;
        for (UnoCard card: this.cardsInHand
             ) {
            string= string + i++ +", " + card + '\n';

        }
    return string;
    }

//////////////////////////////////////////////////////// GAME

    List<PlayerData> playerData= new ArrayList<PlayerData>();
    List<UnoCard> cardsInHand= new ArrayList<UnoCard>();

    UnoCard cardOntop=null;


    void procesPlaycard(String nick, UnoCard card)
    {
        this.setCardOntop(card);
        int index=0;
        for (PlayerData player: this.playersInORder
             ) {
            if(player.getNick().equals(nick))
                break;
            index++;
        }

        this.guiController.gameView.playCardFromOppoent(index, card);


    }


    public void discconct() throws IOException, ClassNotFoundException, InterruptedException {
        this.clientConnectionManager.disconnetFromServer();
    }

    public void reciveCard(UnoCard unoCard) {
        this.guiController.getCard(unoCard);
        this.cardsInHand.add(unoCard);
    }

    public void giveCardToOpponent(String s) {
        int i=0;
        for (PlayerData player: playersInORder
        ) {
            if(player.getNick().equals(s)) {
                player.amountOfCards++;
                break;
            }
            i++;
        }
        if(i>=this.playersInORder.size())
        {
            System.out.println(i +"----" + this.playersInORder.size());
            System.exit(-1);
        }

        this.guiController.giveCardToOpponent(i);
    }

    public void managePlayerFinale(String s) {
     int indexOfPlayer=0;
        for (PlayerData player: this.playersInORder
             ) {
            if(player.getNick().equals(s))
                break;
            indexOfPlayer++;
        }
        if(!s.equals(this.nick))
        this.guiController.gameView.setPlayerEmptyPile(indexOfPlayer);


    }

    public void resetGame()
    {
        this.setReady(false);
        this.readyPlayers=0;
        this.cardOntop=null;
        this.cardsInHand=new ArrayList<UnoCard>();
        this.playersInORder= new ArrayList<PlayerData>();
        this.guiController.mainVew.isReady=false;
        this.guiController.mainVew.setButtonReady();
        this.guiController.mainVew.updateOnSize();

    }

    public void finishGame(String arrayResult[]) {


        this.lastReults= new ArrayList<String>();
        Collections.addAll(lastReults, arrayResult);

        this.resetGame();


        this.guiController.switchSceneToResult();
    }

    public List<String> getResults() {
        return this.lastReults;
    }

    public void surrender() {
        MessageFormat messageFormat= new MessageFormat();
        messageFormat.type =MessageFormat.messegeTypes.SURRENDER;
        try {
            this.clientConnectionManager.sendMessage(messageFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    int getIndexOfPlayer(String nick)
    {
        int indexOfPlayer=0;
        for (PlayerData player: this.playersInORder
        ) {
            if(player.getNick().equals(nick))
                break;
            indexOfPlayer++;
        }
        return indexOfPlayer;
    }
    public void handleSurrender(String s) {
        int index= getIndexOfPlayer(s);
        this.guiController.gameView.handleSurrender(index);


    }

    public void handleDisconnect(String disconnectedNikc, int wasReady) {

        this.connectedPlayers--;
        boolean isInGame=true;

        if(wasReady==1 )
        {
            this.readyPlayers--;
        }

        this.guiController.mainVew.setPlayersReady(this.readyPlayers,this.connectedPlayers);
    }

    public void requestRanking()
    {
        try {
            this.isRankingLoaded.set(false);
            MessageFormat messageFormat= new MessageFormat();
            messageFormat.type = MessageFormat.messegeTypes.RANKING;
            this.clientConnectionManager.sendMessage(messageFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void handleRankingRecived(String[] text, int[] number) {


            this.rankingNicks = text;
            this.rankingwinsAmt = number;
            this.isRankingLoaded.set(true);
        System.out.printf("hadnle RANKING RECIeVd \n");
    }

    public void handleSwapTurn() {
        this.guiController.gameView.swapTurn();
    }

    public void sendChatMesseage(String text) {
        this.addChatMesseage(new ChatMesseage(this.getNick(),text));
        try {
        MessageFormat messageFormat= new MessageFormat();
        messageFormat.type= MessageFormat.messegeTypes.MESSAGE;
        messageFormat.text = new String[1];
        messageFormat.text[0] = text;
            this.clientConnectionManager.sendMessage(messageFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void addChatMesseage(ChatMesseage chatMesseage) {
        this.guiController.gameView.addChatLog(chatMesseage);
    }

    public void reciveChatMesseage(String nick, String mess) {
        this.addChatMesseage(new ChatMesseage(nick,mess));
    }

    public void handleConncetionError(MessageFormat messageFormat) {
    }


    public void handleTooManyPlayers(MessageFormat messageFormat) {
        this.guiController.mainVew.communicatText.setText("Sorry play lismit on server reached");
    }

    public void hadleGameAlradyStared(MessageFormat messageFormat) {
        this.guiController.mainVew.communicatText.setText("Sorry game on this server alrady started");

    }

    public void startWait(String s) {
        this.guiController.gameView.startWaiting(s);
    }

    public void updateWaiting(String s, int i) {
        if (i==0)
            this.guiController.gameView.stopWaiting(s);
        else
            this.guiController.gameView.updateWaitText(s,i);

    }


    public void handleShutDown() {
        this.guiController.switchScenetoMain();
        this.guiController.mainVew.communicatText.setText("Someone discocnect while setting up game");
        this.resetGame();
        this.guiController.gameView=null;
        this.guiController.isGameLoaded=false;
    }

    public boolean isGameLoaded() {
        return guiController.isGameLoaded;
    }

    public void catchup() {
        this.setGameStarted(true);
    }

    public void stopWait(String nick) {
        this.guiController.gameView.stopWaiting(nick);
    }

    public boolean isWaiting() {
        return this.guiController.gameView.isWaitingForPlayer;
    }
}


