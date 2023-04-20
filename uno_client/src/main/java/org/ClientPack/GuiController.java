package org.ClientPack;



import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
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
import org.SharedPack.UnoCard;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
/**
 * main class to manage all gui scenes in program
 */
public class GuiController extends Application {

    /**
     * a varaible store class object of game view
     */
    public   GameView gameView;
    /**
     * a varaible store class object of main view
     */
    public  MainVew mainVew;
    /**
     * a varaible store class object of result view
     */
    private   ResultView resultView;
    /**
     * a varaible store class object of rank view
     */
    private  RankView rankView;
    /**
     * a varaible store class object of Instruction view
     */
    private  InstructionView instructionView;
    /**
     * a varaible store class object of soundPlayer
     */
    public  SoundPlayer soundPlayer;
    /**
     * a varaible store class object of login view
     */
    public  LoginVew loginView;

    /**
     * a method called when person succesfully logined to server
     */
    public void succesfulLogin() {
        this.switchScenetoMain();
        this.mainVew.setStatusConnecting();
        System.out.println("CONNECT");
              this.soundPlayer.playSucces();
            this.mainVew.setStatusConnected();
        }


    /**
     * enum that names of scene taht can be used to store information about which scene is loaded or should be loaded
     */
    enum SCENES{
        MAIN,
        INSRTUCTION,
        GAME,
        RANK,
        LOGIN, RESULT
    }

    /**
     * a varaible to determine acitve scene in gui from enum
     */
    public   GuiController.SCENES activeScenes=SCENES.MAIN;

    /**
     * a varaible used to hold information about previous scene from enum
     */
    private   GuiController.SCENES prevScene=SCENES.MAIN;
    /**
     * a property used to determined if mosue is on any of the button
     */
    public  BooleanProperty isOnButton= new SimpleBooleanProperty(false);

    /**
     *java fx varaiable to store main scene
     */
    public  Scene mainScene;
    /**
     * a varaible to store nick of conected player
     */
    private  static String nick="";

    /**
     * a varaible to store referance to clientapp that mangaes all main operations
     */
    public ClientApp clientApp= new ClientApp(this);

    /**
     * a variable to hold group root of preveoious scene to be able to return back to correct scene
      */
    private Group prevRoot;
    /**
     * a boolean varaible that says if game finished loading
     */
    public Boolean isGameLoaded=false;

    /**
     * a method called to set paramentes of program from arguments and call java fx launch method
     * @param args
     */
    public static void main(String[] args) {


        System.out.println("STARTING \n");
        if(args.length>0)
        GuiController.nick=args[0];
        else
            GuiController.nick="-";



        launch(args);
    }



     Stage primaryStage;

    /**
     * start method from javafx that includes intinlization of whole program
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set. The primary stage will be embedded in
     * the browser if the application was launched as an applet.
     * Applications may create other stages, if needed, but they will not be
     * primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) {


        try{


            Group root = new Group();
            mainScene = new Scene(root, 360, 360,true, SceneAntialiasing.BALANCED);
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


    /**
     * a mehtod used to dicoonet from the server after pushing button
     */
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

    /**
     * a method used send meseeage to server about being ready for game
     */
    public void sendReady()
    {
        this.clientApp.setReady(true);
        this.updatePlayerAmt();
    }

    /**
     * a method used send meseeage to server about being not ready for game
     */
    public void sendNotReady()
    {
        this.clientApp.setReady(false);
        this.updatePlayerAmt();
    }


    /**
     * updates amount of players in main view
     */
    private void updatePlayerAmt()
    {
        this.mainVew.setPlayersReady(this.clientApp.getReadyPlayers(), this.clientApp.getConnectedPlayers());
    }


    /**
     * a method called to update size when scaling window
     */
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

    /**
     * a emthod to create global listiner for whole gui
     */
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

    /**
     * a mathod used setup and load asseets for game view
     */
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

    /**
     * a method used to call anmation of reciving card in game view
     */
    public void getCard(UnoCard unoCard) {
        this.gameView.addCard(unoCard);
    }

    /**
     * a method used to call anmation of giving card in game view
     */
    public void giveCardToOpponent(int nmbOFOppoennt) {
        this.gameView.giveCardToOpponent(nmbOFOppoennt);
    }

    /**
     * a method to switch root of a view with root conating result view roor
     */
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

    /**
     * a method to switch root of a view with root conating main view roor
     */
    public void switchScenetoMain() {

        mainVew.buttons[2].setFill(mainVew.tranparentColor);
        mainVew.updateLocks();
        this.activeScenes=SCENES.MAIN;
                        this.mainScene.setRoot(mainVew.root);


    }

    /**
     * a method to switch root of a view with root conating login view roor
     */
    public void switchScenetoLogin() {

        mainVew.buttons[2].setFill(mainVew.tranparentColor);
        mainVew.buttonTitles[2].setFill(Color.WHITE);


            this.activeScenes=SCENES.LOGIN;
            this.mainScene.setRoot( this.loginView.root);

            this.updateOnSize();



    }


    /**
     * a method to switch root of a view with root conating ranking view roor
     */
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

    /**
     * a method called to return scene that was saved while switch to instruction view to return to coorect view
     */
    public void ReturnScene()
    {

        this.activeScenes=this.prevScene;
        this.mainVew.updateOnSize();
        this.mainScene.setRoot(this.prevRoot);
    }

    /**
     * a method to switch root of a view with root conating insruction view roor
     */
    public void switchSceneToInstruction() {

        this.prevScene=this.activeScenes;
        this.activeScenes=SCENES.INSRTUCTION;
        this.prevRoot= (Group) this.mainScene.getRoot();
        this.mainScene.setRoot(this.instructionView.root);
    }
}


