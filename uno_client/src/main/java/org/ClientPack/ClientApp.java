package org.ClientPack;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.SharedPack.MessageFormat;
import org.SharedPack.UnoCard;
import sun.misc.Lock;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * this class is main clas for managing game system
 */
public class ClientApp {

    /**
     * this varaible stores nick by what this client is connected with server
     */
    private   String nick;
    /**
     * this list hold list of player in clokc iwse order after this client player
     */
    public  List<PlayerData> playersInORder= new ArrayList<PlayerData>();
    /**
     * this varaibles stires info abount amount of ready players
     */
    private   int readyPlayers;
    /**
     * this varaible stores info abount connected players
     */
    public   int connectedPlayers;
    /**
     * this varaibles stoes wheateer or not player of this client is ready
     */
    public  boolean isReady;
    /**
     * this stack olds chat tmeesege that show in pop ups
     */
    public  Stack<ChatMesseage> chatLogs= new Stack<ChatMesseage>();
    /**
     * this varaible stores nick for tanking view
     */
    public  String[] rankingNicks ={};
    /**
     * this varaible hold ranking wins amount for raning view
     */
    public  int[] rankingwinsAmt ={};
    /**
     * this barable stores infomation if raning data is laoded
     */
    public  BooleanProperty isRankingLoaded= new SimpleBooleanProperty(false);

    /**
     * this method holds list of nicks in order of result from last game
     */
    private  List<String>lastReults;
    /**
     * this varailbes stores class for managing connection with server
     */
    private final ClientConnectionManager clientConnectionManager = new ClientConnectionManager(this);
    /**
     * this varaible stores nick of active player turn
     */
    private  String turn = "";

    /**
     * this varaible stores ip for server connection
     */
    private  String ip;
    /**
     * this varaible stores port for server connection
     */
    private  int port;
    /**
     * this varaible stores password for server connection
     */
    private  String pass;
    /**
     * this lock is used for synchoronizaion with confimed messaege
     */
    public  final Lock confirmLock= new Lock();
    /**
     * this varaible store gui controller referance
     */
    public GuiController guiController;

    /**
     * this construcotr setups main gui controler
     * @param guiController
     */
    public ClientApp(GuiController guiController) {
        this.guiController=guiController;
    }

    /**
     * this methos sets if game started varaiable
     * @param gameStarted
     */
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

    /**
     * this method returns nick that client is connected with server
     * @return
     */
    public String getNick() {
        return nick;
    }

    /**
     * this method sets nick with which to connect with client app
     * @param nick
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * this method retursn amount of ready players
     * @return
     */
    public int getReadyPlayers() {
        return readyPlayers;
    }

    /**
     * this meethos sets amount of ready players
     * @param readyPlayers
     */
    public void setReadyPlayers(int readyPlayers) {
        this.readyPlayers = readyPlayers;
        this.guiController.mainVew.setPlayersReady(this.readyPlayers, this.connectedPlayers);
    }

    /**
     * this methos returns amount of connected players
     * @return
     */
    public int getConnectedPlayers() {
        return connectedPlayers;
    }

    /**
     * thi methos sets amount of connected players
     * @param connectedPlayers
     */
    public void setConnectedPlayers(int connectedPlayers) {
        System.out.println("\n\n\n\n\n\n " + this.readyPlayers + "___" + this.readyPlayers);
        this.connectedPlayers = connectedPlayers;
        this.guiController.mainVew.setPlayersReady(this.readyPlayers, this.connectedPlayers);
    }

    /**
     * this method returns ip for server connection
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     * this method sets ip for server connection
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * this method returns port for server connection
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * this method sets port for server connection
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * thi method returns card on top
     * @return
     */
    public UnoCard getCardOntop() {
        return cardOntop;
    }

    /**
     * this method set card on top table
     * @param cardOntop
     */
    public void setCardOntop(UnoCard cardOntop) {
        this.guiController.gameView.setCardOnTable(cardOntop);
        this.cardOntop = cardOntop;
    }

    /**
     * this method set ready status and informs server about it
     * @param ready
     */
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

    /**
     * this mehod tries to connect with server and returns reuslut of this connection
     * @param choice
     * @return
     */
    public  boolean connectWithServer(int choice)
    {
        boolean res=false;
        try {
           res= this.clientConnectionManager.connectToServer(this.getIp(), this.getPort(),this.nick,this.encrypt(pass),choice);
        } catch (IOException | ClassNotFoundException e) {
            return  false;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return res;

    }

    /**
     * this method validates card if this card can be player and returns boolean
     * @param unoCard
     * @return
     */
    public boolean vaidateCard(UnoCard unoCard)
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
            return unoCard.getColor() == this.cardOntop.getColor();
        }
    }

    /**
     * this method handles playing card from client and sends messeage about it to server
     * @param numbCard
     * @param card
     */
    public  void playCard(int numbCard, UnoCard card )
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

    /**
     * this methos return turn number
     * @return
     */
    public String getTurn() {
        return turn;
    }

    /**
     * this  method andles seting tunr for sepcific nick
     * @param turn
     */
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

    /**
     * this method allows to print this class for debugging purpoeses
     * @return
     */
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

    /**
     * this method lhold list of players in game
     */
    private final List<PlayerData> playerData= new ArrayList<PlayerData>();
    /**
     * this method holds list of cards ind hand
     */
    public List<UnoCard> cardsInHand= new ArrayList<UnoCard>();
    /**
     * this variable hold uno card on top of table stack
     */
    private UnoCard cardOntop=null;

    /**
     * this method handles card being played in game
     * @param nick
     * @param card
     */
    public void procesPlaycard(String nick, UnoCard card)
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

    /**
     * this method handles disconnecting from server
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public void discconct() throws IOException, ClassNotFoundException, InterruptedException {
        this.clientConnectionManager.disconnetFromServer();
    }

    /**
     * this method handles recive card messeage from server to get new card
     * @param unoCard
     */
    public void reciveCard(UnoCard unoCard) {
        this.guiController.getCard(unoCard);
        this.cardsInHand.add(unoCard);
    }

    /**
     * this method handles messeae to give card from stack to one of the opponenets
     * @param s
     */
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

    /**
     * this method handles operation when other player finished game before it ended
     * @param s
     */
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

    /**
     * this method reset all variables in clinet app to be clean for next game
     */
    public void resetGame()
    {
        this.readyPlayers=0;
        this.cardOntop=null;
        this.cardsInHand=new ArrayList<UnoCard>();
        this.playersInORder= new ArrayList<PlayerData>();
        this.guiController.mainVew.isReady=false;
        this.guiController.mainVew.setButtonReady();
        this.guiController.mainVew.updateOnSize();

    }

    /**
     * this method handles finihsing gmae with provided results from this game
     * @param arrayResult
     */
    public void finishGame(String[] arrayResult) {
        this.stopAllWait();
        this.lastReults= new ArrayList<String>();
        Collections.addAll(lastReults, arrayResult);
        this.resetGame();

        this.guiController.switchSceneToResult();
        this.setReady(false);


    }

    /**
     * this method stops waiitng for every player in wiatlist
     */
    private void stopAllWait() {
        for (String nick: this.guiController.gameView.nicksWaiting
             ) {
            stopWait(nick);
        }

    }

    /**
     * this emthos return list of players ranking from last game
     * @return
     */
    public List<String> getResults() {
        return this.lastReults;
    }

    /**
     * this method handles surred of this clinet player
     */
    public void surrender() {
        MessageFormat messageFormat= new MessageFormat();
        messageFormat.type =MessageFormat.messegeTypes.SURRENDER;
        try {
            this.clientConnectionManager.sendMessage(messageFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * this method returns index of player specified arguments nick from list of players
     * @param nick
     * @return
     */
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

    /**
     * this method handled surredner of player privieded in arugments
     * @param s
     */
    public void handleSurrender(String s) {
        int index= getIndexOfPlayer(s);
        this.guiController.gameView.handleSurrender(index);


    }

    /**
     * this meethod handles messeage from server
     * @param disconnectedNikc
     * @param wasReady
     */
    public void handleDisconnect(String disconnectedNikc, int wasReady) {

        this.connectedPlayers--;
        boolean isInGame=true;

        if(wasReady==1 )
        {
            this.readyPlayers--;
        }

        this.guiController.mainVew.setPlayersReady(this.readyPlayers,this.connectedPlayers);
    }

    /**
     * this method send to server request to recive ranking data
     */
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

    /**
     * this method handles recived rankng data form server
     * @param text
     * @param number
     */
    public synchronized void handleRankingRecived(String[] text, int[] number) {


            this.rankingNicks = text;
            this.rankingwinsAmt = number;
            this.isRankingLoaded.set(true);
        System.out.print("hadnle RANKING RECIeVd \n");
    }

    /**
     * this handle sap turn order messeage by caling view to chage visual indication of turn order
     */
    public void handleSwapTurn() {
        this.guiController.gameView.swapTurn();
    }

    /**
     * this method sends provided text as a caht message to server
     * @param text
     */
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

    /**
     * this mehod add chat messge to game view
     * @param chatMesseage
     */
    private void addChatMesseage(ChatMesseage chatMesseage) {
        this.guiController.gameView.addChatLog(chatMesseage);
    }

    /**
     * this method handles recinvg chat messeage from server and caht messege to game view
     * @param nick
     * @param mess
     */
    public void reciveChatMesseage(String nick, String mess) {
        this.addChatMesseage(new ChatMesseage(nick,mess));
    }

    /**
     * this method handles error whee game alrady started when trying to login
     * @param messageFormat
     */
    public void handleTooManyPlayers(MessageFormat messageFormat) {
        this.guiController.loginView.setLoginCommuncat("Sorry play lismit on server reached");
    }

    /**
     * this method handles error whee game alrady started when trying to login
     * @param messageFormat
     */
    public void hadleGameAlradyStared(MessageFormat messageFormat) {
        this.guiController.loginView.setLoginCommuncat("Sorry game on this server alrady started");

    }

    /**
     * this method locks game cotrols and setups locker for wiating fro player
     * @param s
     * @param i
     */
    public void startWait(String s, int i) {
        this.guiController.gameView.startWaiting(s);
    }

    /**
     * this method updated waiting text communicat
     * @param s
     * @param i
     */
    public void updateWaiting(String s, int i) {
        if (i==0)
            this.guiController.gameView.stopWaiting(s);
        else
            this.guiController.gameView.updateWaitText(s,i);

    }

    /**
     * this method handles unexpected shut down of game
     */
    public void handleShutDown() {
        this.guiController.switchScenetoMain();
        this.guiController.mainVew.communicatText.setText("Someone discocnect while setting up game");
        this.resetGame();
        this.guiController.gameView=null;
        this.guiController.isGameLoaded=false;
    }

    /**
     * this method return wheater or gane is loaded
     * @return
     */
    public boolean isGameLoaded() {
        return guiController.isGameLoaded;
    }

    /**
     * this method handles catchup messeage
     */
    public void catchup() {
        this.setGameStarted(true);
    }

    /**
     * this method set stop wait for player of provided nick
     * @param nick
     */
    public void stopWait(String nick) {
        this.guiController.gameView.stopWaiting(nick);
    }

    /**
     * this gets if game is waiting for any players
     * @return
     */
    public boolean isWaiting() {
        return this.guiController.gameView.isWaitingForPlayer;
    }

    /**
     * this method handles succedu registration
     */
        private void succesfulRegistration() {
        System.out.println("SUCCESFULY REGISTE \n");
    }

    /**
     * this method handled failed registraton by settinf registration communicat
     */
    private void failedRegistration() {
        System.out.println("FAIELD REGISTRATION \n");
        this.guiController.loginView.setRegisterCommunicat("Nick alrady taken");
    }

    /**
     * this method encrytps provided string wiht md5 algortih
     * @param pass
     * @return
     * @throws NoSuchAlgorithmException
     */
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

    /**
     * this method handles regstreation result from server
     * @param messageFormat
     */
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

    /**
     * set passworrd varaible in class
     * @param text
     */
    public void setPass(String text) {
        this.pass=text;
    }
}


