package org.ClientPack;

/**
 * a class that holds data abount how is clinet connected
 */
public class PlayerData {
    /**
     * a varaible taht stores nick taht client ins connected as
     */
    String nick;
    /**
     * varaible that stores info about how many cards are in hand of plyer
     */
    int amountOfCards=0;

    /**
     * a constructor that adds nick to object
     * @param nick
     */
    public PlayerData(String nick) {
        this.nick = nick;
    }

    /**
     * getter for nick variable
     * @return
     */
    public String getNick() {
        return nick;
    }


    /**
     * a method for printing player data
     * @return
     */
    @Override
    public String toString() {
        return nick+", " + amountOfCards;
    }
}
