package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        /*
        // ServerApp serverApp=new ServerApp();
        DataBaseMangaer dataBaseMangaer= new DataBaseMangaer();
        dataBaseMangaer.setDataBaseName("orcl");
        dataBaseMangaer.setDataBaseAdres("localhost");
        dataBaseMangaer.setDataBasePort("1521");
        dataBaseMangaer.setDataBaseUserName("test");
        dataBaseMangaer.setDataBasePass("test");

        dataBaseMangaer.connectWithDataBase();
        try {
            System.out.println(  dataBaseMangaer.checkTable());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataBaseMangaer.dropTables();
        dataBaseMangaer.createTables();


        dataBaseMangaer.deleteAll();
        dataBaseMangaer.createViews();
        dataBaseMangaer.dropSeq();
        dataBaseMangaer.createSeq();
        dataBaseMangaer.createFunctions();
        dataBaseMangaer.createProcedures();


        dataBaseMangaer.addPlayer("nick");
        dataBaseMangaer.addPlayer("player");
        dataBaseMangaer.addPlayer("Filip");
        dataBaseMangaer.addPlayer("Bartek");

        dataBaseMangaer.createBaseCards();


        dataBaseMangaer.createNewGame("nick");
        dataBaseMangaer.addPlayerToGame("player");
        dataBaseMangaer.addPlayerToGame("Filip");
        dataBaseMangaer.preapreDeck();
        dataBaseMangaer.dealCards();


        dataBaseMangaer.playCard("Filip",2);

        dataBaseMangaer.drawCard("Filip");
        dataBaseMangaer.drawCard("Filip");
        dataBaseMangaer.drawCard("Filip");
        dataBaseMangaer.drawCard("Filip");
        dataBaseMangaer.drawCard("Filip");
        dataBaseMangaer.drawCard("Filip");
        dataBaseMangaer.drawCard("Filip");

        dataBaseMangaer.dropSeq();
        dataBaseMangaer.createSeq();
        System.out.println(dataBaseMangaer.getPlayerCount());
        System.out.println(dataBaseMangaer.getNumbOntheTable());
        System.out.println(dataBaseMangaer.getPlayerAmtOfCards("Filip"));



        List<UnoCard> cardStack= dataBaseMangaer.selectFromHand("Filip");
        int i=1;
        for (UnoCard card: cardStack) {
            System.out.println(i+"   "+card);
            i++;
        }


        List<String> players= dataBaseMangaer.selectOrderFromPlayer("nick");
        i=1;
        for (String player: players) {
            System.out.println(i+"   "+player);
            i++;
        }


         */
        UiInterface uiInterface= new UiInterface();
        uiInterface.strartInterface();
    }
        // System.out.println("Hello world from server!"
    }