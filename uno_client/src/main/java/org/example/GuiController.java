package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class
GuiController extends Application {

    GameView gameView;
    MainVew mainVew;

    Scene mainScene;

    static String nick="";
    String ip="localhost";
    String port="25565";

    ClientApp clientApp= new ClientApp(this);

    BooleanProperty isStarted= new SimpleBooleanProperty(false);


    public static void main(String[] args) {


        if(args.length>0)
        GuiController.nick=args[0];
        else
            GuiController.nick="-";



        launch(args);
    }



     Stage primaryStage;
    @Override
    public void start(Stage primaryStage) {

    this.addListineres();
        try{
            this.primaryStage=primaryStage;
            this.mainVew= new MainVew(this);


            this.gameView= new GameView(this);
            this.gameView.iniit(primaryStage);

            mainVew.iniit(primaryStage);// mainScene = new Scene(mainVew.root, 1250, 720,true, SceneAntialiasing.BALANCED);


            this.mainVew.textFields[0].setText(GuiController.nick);
            this.mainVew.textFields[1].setText(this.ip);
            this.mainVew.textFields[2].setText(this.port);


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




    void addListineres()
    {

    }

    public void startGame() {
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        primaryStage.setScene(gameView.mainScene);
                    }
                }

        );

    }

}
