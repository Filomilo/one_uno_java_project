package org.example;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import sun.misc.Lock;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    String pass;
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
        System.out.println("^^^^^^^^^^^^^^^^^ SET READ\n");
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

    boolean connectWithServer(int choice)
    {
        boolean res=false;
        try {
           res= this.clientConnectionManager.connectToServer(this.getIp(), this.getPort(),this.nick,this.encrypt(pass),choice);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return  false;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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
        System.out.println("REST GAME TEST: 1\n");
        //this.setReady(false);
        System.out.println("REST GAME TEST: 2\n");
        this.readyPlayers=0;
        System.out.println("REST GAME TEST: 3\n");
        this.cardOntop=null;
        System.out.println("REST GAME TEST: 4\n");
        this.cardsInHand=new ArrayList<UnoCard>();
        System.out.println("REST GAME TEST: 5\n");
        this.playersInORder= new ArrayList<PlayerData>();
        System.out.println("REST GAME TEST: 6\n");
        this.guiController.mainVew.isReady=false;
        System.out.println("REST GAME TEST: 7\n");
        this.guiController.mainVew.setButtonReady();
        System.out.println("REST GAME TEST: 8\n");
        this.guiController.mainVew.updateOnSize();
        System.out.println("REST GAME TEST: 9\n");

    }

    public void finishGame(String arrayResult[]) {
        this.stopAllWait();
        System.out.println("Test 1 \n");
        this.lastReults= new ArrayList<String>();
        System.out.println("Test 2 \n");
        Collections.addAll(lastReults, arrayResult);
        System.out.println("Test 3 \n");
        this.resetGame();
        System.out.println("Test 4 \n");

        this.guiController.switchSceneToResult();
        System.out.println("Test 5 \n");
        this.setReady(false);
        //this.setReadyPlayers(0);

    }

    private void stopAllWait() {
        for (String nick: this.guiController.gameView.nicksWaiting
             ) {
            stopWait(nick);
        }

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

    public void startWait(String s, int i) {
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

    public void register(String nick, String pass) {
        try {
            String encryped = this.encrypt(pass);
            System.out.println("REGeISTERING : " + nick + "::::" + encryped + "\n");

            MessageFormat messageFormat = new MessageFormat();
            messageFormat.type = MessageFormat.messegeTypes.CONNECT;
            messageFormat.text = new String[2];
            messageFormat.text[0] = nick;
            messageFormat.text[1] = encryped;
            messageFormat.number = new int[1];
            messageFormat.number[0] = 0;
            this.clientConnectionManager.sendMessage(messageFormat);

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

        private void succesfulRegistration() {
        System.out.println("SUCCESFULY REGISTE \n");
    }

    private void failedRegistration() {
        System.out.println("FAIELD REGISTRATION \n");
        this.guiController.loginView.setRegisterCommunicat("Nick alrady taken");
    }

    private String encrypt(String pass) throws NoSuchAlgorithmException {
        MessageDigest m=  MessageDigest.getInstance("MD5");
        m.update(pass.getBytes());
        byte[] bytes=m.digest();
        StringBuilder encrypted= new StringBuilder();
        for (byte byt: bytes
             ) {
            encrypted.append(String.format("%02x",byt));
        }
        return encrypted.toString();

    }

    public void handleRegistration(MessageFormat messageFormat) {
        System.out.println("REGISTRATION\n");
            if(messageFormat.number[0]==1)
            {
                this.succesfulRegistration();
            }
            else
            {
                failedRegistration();
            }


    }

    public void setPass(String text) {
        this.pass=text;
    }
}


