package org.example;

import sun.misc.Lock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClientApp {

    String nick;
    List<PlayerData> playersInORder= new ArrayList<PlayerData>();
    int readyPlayers;
    int connectedPlayers;
    boolean isConnected=false;
    boolean isReady;

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

    boolean vaidateCard(UnoCard unoCard, boolean changedColor)
    {
        if(changedColor)
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
    void playCard(int numbCard, UnoCard card, boolean changedColor)
    {
        if(!vaidateCard(this.cardsInHand.get(numbCard-1),changedColor) )
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
            if(player.getNick()==nick)
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
            if(player.getNick()== s) {
                player.amountOfCards++;
                break;
            }
            i++;
        }

        this.guiController.giveCardToOpponent(i);
    }
}


