package org.ServerPack;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * this class handles user console interaface fro server
 */
public class UiInterface {

    /**
     * varaible to store main classof server app
     */
    private final ServerApp serverApp = new ServerApp();
    /**
     * scanner to handle user console iinput
     */
    private final Scanner scanner= new Scanner(System.in);
    /**
     * thread class to handle input while server is running
     */

    private ServerRunThread serverRunThread;

    /**
     * method to clean terminal on windows
     */
    private  void clearTerminal()
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * a method to get yes or no in user console interface
     */
    private  boolean getYesNo()
    {
        while(true) {
            System.out.println("y/n");
            String input = scanner.next();
            if (input.equals("n"))
            {
                return false;
            }
            if (input.equals("y"))
            {
                return true;
            }
        }

    }

    /**
     * user interface for setting app connections
     */
    private void createDataBaseConectionUi()
    {

        String input;
        while (true)
        {
            System.out.println("what ip of your Oracle Data Base server");
            //input="localhost";
            input=scanner.next();
            this.serverApp.dataBaseMangaer.setDataBaseAdres(input);

            System.out.println("What is port of your Oracel Dataa Base sever");
           // input="1521";
            input=scanner.next();
            this.serverApp.dataBaseMangaer.setDataBasePort(input);

            System.out.println("What is name of your Data base");
           // input="xe";
            input=scanner.next();
            this.serverApp.dataBaseMangaer.setDataBaseName(input);

            System.out.println("What username that you would like to connect to Data base");
           // input="test";
            input=scanner.next();
            this.serverApp.dataBaseMangaer.setDataBaseUserName(input);

            System.out.println("What password that you would like to connect to Data base");
           // input="test";
            input=scanner.next();

            this.serverApp.dataBaseMangaer.setDataBasePass(input);

            boolean res= this.serverApp.dataBaseMangaer.connectWithDataBase();
            this.clearTerminal();

            if(!res)
            {
                System.out.println("Conenction with dataBase could not be established would you like to try again");
                res=getYesNo();
                if(!res)
                {
                    System.exit(1);
                }
            }
            else
            {
                System.out.println("Connection with Data base established correctly");
                break;
            }

        }
    }

    /**
     * a method to ask whether or not to reset data base
     */
    private  boolean resetDataBase()
    {
        System.out.println("Would you like to reset data Base");
        boolean res=this.getYesNo();

        if(res)
        {
            this.serverApp.dataBaseMangaer.resetDataBase();
            return true;
        }
        return false;
    }

    /**
     * a method to check if database is correct
     */
    private void checkDataBae() throws SQLException {
        boolean dataBaseVaidity=this.serverApp.dataBaseMangaer.checkTable();
        if(!dataBaseVaidity)
        {
            System.out.println("It seems your dataBase does not have correct tables");
            boolean res= this.resetDataBase();
            if(!res)
                System.exit(1);
        }
        else
        {
            System.out.println("It seems your dataBase has correct tables");
            this.resetDataBase();
        }
    }

    /**
     * a method to start main server interface
     */
    public void strartInterface()
{
    int input;
    while (true) {
        try {
            System.out.println("-------- Welocome to Uno Server --------");
            System.out.println("First we need you need to connect to DataBase");
            this.createDataBaseConectionUi();
            this.checkDataBae();
            this.clearTerminal();
            System.out.println("Data base setup finished");
            System.out.println("On what port would you like to start uno Server");
            //input=25565;
            input=scanner.nextInt();

            this.serverApp.setPort(input);
            serverRunThread = new ServerRunThread(this.serverApp);
            serverRunThread.start();
            System.out.println("Uno server running");
            waitForExit();
            break;
        }
        catch (Exception e)
        {
            System.out.println("Somethink went wrong!");
            e.printStackTrace();
        }
        }

}

    /**
     * a mthod to wait for exit strign to stop server from running
     */
    private void waitForExit() throws InterruptedException {
        while(true) {
            String input = scanner.next();
            if (input.equals("STOP"))
            {
                this.serverApp.stopServer();
                this.serverRunThread.join();
                System.exit(1);
            }
        }
    }

}

