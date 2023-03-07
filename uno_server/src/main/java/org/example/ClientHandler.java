package org.example;


import java.io.*;

public class ClientHandler extends  Thread{

    PlayerData playerData;
    ServerConnectionManager serverConnectionManager;
    boolean connectionActive=true;

    public ClientHandler(PlayerData playerData, ServerConnectionManager serverConnectionManager) {
        this.playerData = playerData;
        this.serverConnectionManager = serverConnectionManager;
        this.playerData.clientHandler=this;
    }

    @Override
    public void run() {
        super.run();

        while(connectionActive && ServerConnectionManager.isServerRunning)
        {
            try{
                MessageFormat messageFormat;
                messageFormat= ServerConnectionManager.getMesseage(this.playerData.objectInputStream);
                System.out.println(messageFormat);



            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }








    }

/*
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




     */

   }


