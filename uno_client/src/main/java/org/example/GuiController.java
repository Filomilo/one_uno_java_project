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

    GameView gameView;
    MainVew mainVew;
    ResultView resultView;
    RankView rankView;
    InstructionView instructionView;

    static enum SCENES{
        MAIN,
        INSRTUCTION,
        GAME,
        RANK,
        RESULT
    };

    GuiController.SCENES activeScenes=SCENES.MAIN;
    GuiController.SCENES prevScene=SCENES.MAIN;

    Scene mainScene;
    Group root;
    static String nick="";
    String ip="localhost";
    String port="25565";

    ClientApp clientApp= new ClientApp(this);

    BooleanProperty isStarted= new SimpleBooleanProperty(false);

    Scene previousScene;
    private Group prevRoot;


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


            this.root = new Group();
            mainScene = new Scene(this.root, 1250, 720,true, SceneAntialiasing.BALANCED);
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



            this.addListineres();



            this.mainVew.textFields[0].setText(GuiController.nick);
            this.mainVew.textFields[1].setText(this.ip);
            this.mainVew.textFields[2].setText(this.port);
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


    void updateOnSize()
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
        }



        if(this.activeScenes == SCENES.MAIN)
        this.mainVew.updateOnSize();
        if(this.activeScenes == SCENES.RANK)
            this.rankView.updateOnSize();
    }

    void addListineres()
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
                        System.out.printf("ROOT CHANED\n");
                        updateOnSize();
                    }
                }
        );


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
                        resultView = new ResultView(guiController,mainScene);
                        try {
                            resultView.iniit(primaryStage);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        activeScenes= SCENES.RESULT;
                        mainScene.setRoot(resultView.root);
                        gameView=null;

                    }
                }


        );

    }

    public void switchScenetoMain() {

        mainVew.buttons[2].setFill(mainVew.tranparentColor);
        mainVew.buttonTitles[2].setFill(Color.WHITE);
        this.activeScenes=SCENES.MAIN;
                        this.mainScene.setRoot(mainVew.root);


    }






    private void setScaneToRanking()
    {
        System.out.printf("CHANGE SCNE TO RANKING");
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


