package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jdk.nashorn.internal.runtime.regexp.JoniRegExp;

import javax.swing.text.View;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

public class
GuiController extends Application {

    GameView gameView;
    MainVew mainVew;
    ResultView resultView;

    RankView rankView;

    InstructionView instructionView;

    Scene mainScene;

    static String nick="";
    String ip="localhost";
    String port="25565";

    ClientApp clientApp= new ClientApp(this);

    BooleanProperty isStarted= new SimpleBooleanProperty(false);

    Scene previousScene;


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


        try{




            this.primaryStage=primaryStage;
            this.addListineres();
            primaryStage.setOnCloseRequest(
                    new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(1);
                        }
                    }
            );

            this.mainVew= new MainVew(this);
            this.primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("one_icon.png")));
            this.instructionView = new InstructionView(this);
            this.instructionView.iniit(primaryStage);


            mainVew.iniit(primaryStage);// mainScene = new Scene(mainVew.root, 1250, 720,true, SceneAntialiasing.BALANCED);


            this.mainVew.textFields[0].setText(GuiController.nick);
            this.mainVew.textFields[1].setText(this.ip);
            this.mainVew.textFields[2].setText(this.port);


            primaryStage.setScene(mainVew.mainScene);
            primaryStage.setWidth(640);
            primaryStage.setHeight(360);
            primaryStage.show();




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
        this.primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            System.out.println("KEY PResed: "+ event.getCode());
            if(KeyCode.F10.equals(event.getCode()))
            {

                primaryStage.setFullScreen(true);
            }

        });

        System.out.println("ADDDL ISTINER BOOLEAN");
            this.clientApp.isRankingLoaded.addListener(

                    new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            System.out.printf("RANKING RECIVED\\n\n\n\n\n");
                            if (newValue == true && oldValue == false) {

                                Platform.runLater(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                setScaneToRanking();
                                                clientApp.isRankingLoaded.set(false);
                                            }
                                        }
                                );


                            }
                        }
                    });

    }

    public void startGame() {
        this.gameView= new GameView(this);
        try {

            this.gameView.iniit(this.primaryStage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        if(gameView.isAssetLoaded) {
                            gameView.playersAtStart = clientApp.getReadyPlayers();
                            gameView.playersAtStart = clientApp.connectedPlayers;
                            primaryStage.setScene(gameView.mainScene);
                            gameView.updateOnSize();
                        }
                    }
                }

        );
    this.gameView.updateOnSize();
    }

    public void getCard(UnoCard unoCard) {
        this.gameView.addCard(unoCard);
    }

    public void giveCardToOpponent(int nmbOFOppoennt) {
        this.gameView.giveCardToOpponent(nmbOFOppoennt);
    }

    public void switchSceneToResult()
    {
        GuiController guiController=  this;
        System.out.println();
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        resultView = new ResultView(guiController);
                        try {
                            resultView.iniit(primaryStage);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        primaryStage.setScene(resultView.mainScene);
                    }
                }


        );

    }

    public void switchScenetoMain() {

        mainVew.buttons[2].setFill(mainVew.tranparentColor);
        mainVew.buttonTitles[2].setFill(Color.WHITE);
                        primaryStage.setScene(mainVew.mainScene);


    }






    private void setScaneToRanking()
    {
        System.out.printf("CHANGE SCNE TO RANKING");
        try {

            rankView = new RankView(this);
            rankView.iniit(this.primaryStage);
            this.primaryStage.setScene(rankView.mainScene);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public void ReturnScene()
    {
        this.primaryStage.setScene(this.previousScene);
    }

    public void switchSceneToInstruction() {
        this.previousScene= this.primaryStage.getScene();
        this.primaryStage.setScene(this.instructionView.mainScene);

    }
}


