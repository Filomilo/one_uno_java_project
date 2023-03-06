package org.example;


import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ClientHandler extends  Thread{

    static Socket[] socket=new Socket[8];
    static   ObjectOutputStream[] outStream=new  ObjectOutputStream[8];
    static   ObjectInputStream[] inStream=new  ObjectInputStream[8];
    static String[] nicks= new String[8];
    static int PlayersAmount=0;
    int ActivePlayer=0;
    ClientHandler(Socket socket,  ObjectInputStream inStream,  ObjectOutputStream outStream, String nick)
    {
        
        this.ActivePlayer=ClientHandler.PlayersAmount;
        ClientHandler.inStream[this.ActivePlayer]=inStream;
        ClientHandler.outStream[this.ActivePlayer]=outStream;
        ClientHandler.socket[this.ActivePlayer]=socket;
        ClientHandler.nicks[this.ActivePlayer]=nick;
        ClientHandler.PlayersAmount++;
     
    }

    @Override
   public void run()
   {
       String recivedString;

       while(true)
       {
           try{
               MessageFormat messageFormat=new MessageFormat();
               messageFormat=ServerConnectionManager.getMesseage(inStream[this.ActivePlayer]);
              if(messageFormat.type==MessageFormat.messegeTypes.DISCONNECT) {
                  System.out.println("User " + ClientHandler.nicks[this.ActivePlayer]+ " Disconnected from Server");
                  messageFormat.type= MessageFormat.messegeTypes.SUCCES;
                  messageFormat.text="succesully disconected";
                  ServerConnectionManager.sendMessage(ClientHandler.outStream[this.ActivePlayer],messageFormat);
                      ClientHandler.socket[this.ActivePlayer].close();
                      break;

              }
             else
              {

                  System.out.println(new String("recived messege: " + messageFormat.text));
                  String forwardMessege=ClientHandler.nicks[this.ActivePlayer] + ": " + messageFormat.text;
                  messageFormat.type= MessageFormat.messegeTypes.MESSAGE;
                  messageFormat.text=forwardMessege;
                  this.senndMessageExcluded(messageFormat);
              }

           }
           catch(IOException | ClassNotFoundException e)
           {
               e.printStackTrace();
               break;
           }
       }

       try{
           ClientHandler.inStream[this.ActivePlayer].close();
           ClientHandler.outStream[this.ActivePlayer].close();
       }
       catch(IOException ex)
       {
           ex.printStackTrace();
       }
   }
    
   void senndMessagetoall(MessageFormat mes) throws IOException {
       for(int i=0;i<ClientHandler.PlayersAmount;i++) {
           if(ClientHandler.outStream[i]!=null) {
               ServerConnectionManager.sendMessage(ClientHandler.outStream[i], mes);
           }
       }

       }

    void senndMessageExcluded(MessageFormat mes) throws IOException {
        for(int i=0;i<ClientHandler.PlayersAmount;i++) {
            if(ClientHandler.outStream[i]!=null && i!=this.ActivePlayer) {
                ServerConnectionManager.sendMessage(ClientHandler.outStream[i], mes);
            }
        }

    }


   }


