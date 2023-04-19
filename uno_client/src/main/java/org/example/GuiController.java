package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jdk.nashorn.internal.runtime.regexp.JoniRegExp;
import sun.java2d.windows.GDIRenderer;

import javax.swing.text.View;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

public class
GuiController extends Application {

    public   GameView gameView;
    public  MainVew mainVew;
    private   ResultView resultView;
    private  RankView rankView;
    private  InstructionView instructionView;

    public  SoundPlayer soundPlayer;
    public  LoginVew loginView;

    public void succesfulLogin() {
        this.switchScenetoMain();
        this.mainVew.setStatusConnecting();
        System.out.println("CONNECT");
              this.soundPlayer.playSucces();
            this.mainVew.setStatusConnected();
        }




    enum SCENES{
        MAIN,
        INSRTUCTION,
        GAME,
        RANK,
        LOGIN, RESULT
    }

    public   GuiController.SCENES activeScenes=SCENES.MAIN;
    private   GuiController.SCENES prevScene=SCENES.MAIN;
    public  BooleanProperty isOnButton= new SimpleBooleanProperty(false);

    public  Scene mainScene;
    private  Group root;
    private  static String nick="";
    private  String ip="localhost";
    private  String port="25565";

    public ClientApp clientApp= new ClientApp(this);

    private  BooleanProperty isStarted= new SimpleBooleanProperty(false);

    private Scene previousScene;
    private Group prevRoot;
    public Boolean isGameLoaded=false;


    public static void main(String[] args) {


        System.out.println("STARTING \n");
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


            this.root = new Group();
            mainScene = new Scene(this.root, 360, 360,true, SceneAntialiasing.BALANCED);
            this.primaryStage=primaryStage;
            primaryStage.setScene(mainScene);
            primaryStage.show();

            this.primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("one_icon.png")));

            this.mainVew= new MainVew(this);
            this.mainVew.iniit(primaryStage,mainScene);

            this.mainVew.updateOnSize();
            //this.mainScene.seton

            this.instructionView= new InstructionView(this);
            this.instructionView.iniit(primaryStage,mainScene);
            this.loginView= new LoginVew(this);
            this.loginView.iniit(this.primaryStage,mainScene);

            this.addListineres();

            this.mainScene.setRoot(this.mainVew.root);
            //primaryStage.setO

            /*
            this.primaryStage=primaryStage;


            this.mainVew= new MainVew(this);

            this.instructionView = new InstructionView(this);
            this.instructionView.iniit(primaryStage);


            mainVew.iniit(primaryStage);// mainScene = new Scene(mainVew.root, 1250, 720,true, SceneAntialiasing.BALANCED);










            //primaryStage.setWidth(640);
            //primaryStage.setHeight(360);

            this.mainScene.setRoot(mainVew.root);


*/
            soundPlayer = new SoundPlayer();
           // this.switchScenetoLogin();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    private  void changeSceneToMain()
    {

    }

    private void changeSceneToGame()
    {
        this.primaryStage.setScene(gameView.mainScene);
        this.gameView.updateOnSize();
    }


    public  void disconnectFromServer()
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


    public void sendReady()
    {
        this.clientApp.setReady(true);
        this.updatePlayerAmt();
    }

    public void sendNotReady()
    {
        this.clientApp.setReady(false);
        this.updatePlayerAmt();
    }


    private void updatePlayerAmt()
    {
        this.mainVew.setPlayersReady(this.clientApp.getReadyPlayers(), this.clientApp.getConnectedPlayers());
    }


    private void updateOnSize()
    {
        System.out.printf("update Size: "+ this.activeScenes + "\n");



        switch (this.activeScenes)
        {

            case GAME: this.gameView.updateOnSize();
                break;
            case MAIN: this.mainVew.updateOnSize();
                break;
            case RANK: this.rankView.updateOnSize();
                break;
            case RESULT: this.resultView.updateOnSize();
                break;
            case INSRTUCTION: this.instructionView.updateOnSize();
                break;
            case LOGIN: this.loginView.updateOnSize();
                break;
        }



        if(this.activeScenes == SCENES.MAIN)
        this.mainVew.updateOnSize();
        if(this.activeScenes == SCENES.RANK)
            this.rankView.updateOnSize();
    }

    private void addListineres()
    {


        primaryStage.setOnCloseRequest(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        System.exit(1);
                    }
                }
        );




        this.primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            System.out.println("KEY PResed: "+ event.getCode());
            if(KeyCode.F10.equals(event.getCode()))
            {

                primaryStage.setFullScreen(true);
            }

        });

        this.mainScene.rootProperty().addListener(
                new ChangeListener<Parent>() {
                    @Override
                    public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                        System.out.print("ROOT CHANED\n");
                        updateOnSize();
                    }
                }
        );


        System.out.println("ADDDL ISTINER BOOLEAN");
            this.clientApp.isRankingLoaded.addListener(

                    new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            System.out.print("RANKING RECIVED\\n\n\n\n\n");
                            if (newValue && !oldValue) {

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
        this.gameView= new GameView(this,mainScene);
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
                            activeScenes=SCENES.GAME;
                            mainScene.setRoot(gameView.root);
                            gameView.updateOnSize();
                        }
                    }
                }

        );
    this.gameView.updateOnSize();
    this.isGameLoaded=true;
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
                        soundPlayer.playfinish();
                        resultView = new ResultView(guiController,mainScene);
                        try {
                            resultView.iniit(primaryStage);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        activeScenes= SCENES.RESULT;
                        mainScene.setRoot(resultView.root);
                        gameView=null;
                        isGameLoaded=false;
                    }
                }


        );

    }

    public void switchScenetoMain() {

        mainVew.buttons[2].setFill(mainVew.tranparentColor);
        mainVew.updateLocks();
        this.activeScenes=SCENES.MAIN;
                        this.mainScene.setRoot(mainVew.root);


    }
    public void switchScenetoLogin() {

        mainVew.buttons[2].setFill(mainVew.tranparentColor);
        mainVew.buttonTitles[2].setFill(Color.WHITE);


            this.activeScenes=SCENES.LOGIN;
            this.mainScene.setRoot( this.loginView.root);

            this.updateOnSize();



    }





    private void setScaneToRanking()
    {
        System.out.print("CHANGE SCNE TO RANKING");
        try {



            this.activeScenes=SCENES.RANK;
            rankView = new RankView(this,mainScene);
            rankView.iniit(this.primaryStage);
            this.activeScenes=SCENES.RANK;
            this.mainScene.setRoot(rankView.root);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public void ReturnScene()
    {

        this.activeScenes=this.prevScene;
        this.mainVew.updateOnSize();
        this.mainScene.setRoot(this.prevRoot);
    }

    public void switchSceneToInstruction() {

        this.prevScene=this.activeScenes;
        this.activeScenes=SCENES.INSRTUCTION;
        this.prevRoot= (Group) this.mainScene.getRoot();
        this.mainScene.setRoot(this.instructionView.root);
    }
}


