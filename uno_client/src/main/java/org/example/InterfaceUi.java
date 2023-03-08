package org.example;

import java.util.Scanner;

public class InterfaceUi {

   ClientApp clientApp= new ClientApp();
    Scanner scanner=new Scanner(System.in);

    String nick;
        InterfaceUi()
        {

        }
        InterfaceUi(String nick)
        {
            this.nick=nick;
        }

    void clearTerminal()
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    boolean getYesNo()
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

    void ConnectToerver()
    {
        String input;
        while(true)
        {
            System.out.println("please provide ip addres for server would you like to connect ");
            // input=scanner.next();
            input="localhost";
            this.clientApp.setIp(input);
            System.out.println("please provide port  for server would you like to connect ");
            //input=scanner.next();
            input="25565";
            this.clientApp.setPort(Integer.parseInt(input));
            boolean res=this.clientApp.connectWithServer();
            this.clearTerminal();
            if(res)
            {
                break;
            }
            else
            {
                System.out.println("Could not ocnect with server, there might alrady uer with tji nick");
            }
        }
    }

    void starUi()
    {
        String input;
        System.out.println("-------------Welcome to Uno terminal app ----------");
        System.out.println("How woul you like to be called in this game");
       // input=this.scanner.next();
        input=nick;
        clientApp.setNick(input);
        System.out.println("Welcome  "+ clientApp.nick);
        System.out.println("In order to play you will have to connect to Uno server app");
        this.ConnectToerver();
        System.out.println("Congrats on succesful connteion with server");
        System.out.println("now you need to wait for others playres to start to game");
        this.app();
    }

    void app()
    {
        String input;
        boolean isReady;
        while(clientApp.isReady()==false)
        {
            System.out.println(clientApp);
            System.out.println("Are you ready to play?");
            isReady=getYesNo();
           // bool=true;
            if(isReady)
            {
                clientApp.setReady(true);
            }
        }
        System.out.println("Now you have to wait for other players");

        while(this.clientApp.isReady())
        {


        }
    }

}
