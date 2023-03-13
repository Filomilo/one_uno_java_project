package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

import java.util.ServiceConfigurationError;
import java.util.concurrent.TimeUnit;

public class
GuiController extends Application {

    GameView gameView;
    MainVew mainVew;

    Scene mainScene;



    ClientApp clientApp= new ClientApp();


    public static void main(String[] args) {
        launch(args);
    }
    Stage primaryStage;
    @Override
    public void start(Stage primaryStage) {

        try{
            this.primaryStage=primaryStage;
            this.mainVew= new MainVew(this);

            this.gameView= new GameView(this);
            this.gameView.iniit(primaryStage);

            mainVew.iniit(primaryStage);// mainScene = new Scene(mainVew.root, 1250, 720,true, SceneAntialiasing.BALANCED);
            primaryStage.setScene(mainVew.mainScene);

            primaryStage.show();
            mainVew.updateOnSize();




        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    void changeSceneToMain()
    {

    }

    void changeSceneToGame()
    {
        this.primaryStage.setScene(gameView.mainScene);
        this.gameView.updateOnSize();
    }

    boolean connectTosServer()
    {
        try {
            String nick = this.mainVew.textFields[0].getText();
            String ip = this.mainVew.textFields[1].getText();
            String port = this.mainVew.textFields[2].getText();

            this.clientApp.setNick(nick);
            this.clientApp.setPort(Integer.parseInt(port));
            this.clientApp.setIp(ip);

            System.out.println(nick + " " + ip + " " + " " + port);
            Boolean res=this.clientApp.connectWithServer();
            if(res)
                    this.updatePlayerAmt();
            return res ;
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            return false;
        }
    }

    void disconnectFromServer()
    {
        try {
            this.clientApp.discconct();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        this.mainVew.setPlayersReady(0,0);
    }


    void sendReady()
    {
        this.clientApp.setReady(true);
        this.updatePlayerAmt();
    }

    void sendNotReady()
    {
        this.clientApp.setReady(false);
        this.updatePlayerAmt();
    }


    void updatePlayerAmt()
    {
        this.mainVew.setPlayersReady(this.clientApp.getReadyPlayers(), this.clientApp.getConnectedPlayers());
    }



}
