package org.example;

public class PlayerData {
    String nick;
    int amountOfCards=0;


    public PlayerData(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getAmountOfCards() {
        return amountOfCards;
    }

    public void setAmountOfCards(int amountOfCards) {
        this.amountOfCards = amountOfCards;
    }

    @Override
    public String toString() {
        return nick+", " + amountOfCards;
    }
}
