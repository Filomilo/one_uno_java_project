package org.example;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URISyntaxException;

import static java.lang.Math.abs;


public class InstructionView extends Application {
    Scene mainScene;


    final int startH=720;
    final int startW=1280;
    static final String resDir = "uno_client\\src\\main\\resources\\";

    Color tranparentColor = new Color(1,0,0,0.0);
    Color blueColor = new Color(0,0.1,0.6,1);
    Stop[] blueStops = new Stop [] {new Stop(0, this.blueColor), new Stop(1, Color.BLACK)} ;
    Rectangle button;
    Text buttonText;
    Rectangle rankViewBase;
    double scrollAmount=0;
    private RadialGradient gradient;
    private Shape mask;



    String instructions= "In order to play this game, you have to be connected with at least 1 other person. The maximum number of players during the game is 8. Status and amount of players connected to the same server as you can be seen in the bottom right corner of the main menu. The game will begin automatically once everyone connected is ready.\n" +
            "\n" +
            "The order of players is determined alphabetically, and you begin the game in clockwise order.\n" +
            "\n" +
            "During the game, you can get different types of cards with one of 4 colors or special black color\n" +
            "\n" +
            "The most basic cards are numbered cards, those come only in 4 basic colors and don't have any special behaviors\n" +
            "\n" +
            "Secondly, you have colored special cards, those include:\n" +
            "    * block card - the next player skip turn without any play\n" +
            "    * reverse card - reverses order of players\n" +
            "    * plus 2 cards - forces next player to draw 2 cards from deck\n" +
            "    \n" +
            "And we have 2 types of blacks card\n" +
            "    * color choice - it allows what color should be placed on top of table stack\n" +
            "    * plus 4 cards - similar to plus 2 cards it forces next player to draw cards but this time 4 cards, on top of that it also allows choosing new color on table\n" +
            "    \n" +
            "    \n" +
            "At the beginning, players will get randomly 7 cards\n" +
            "Once it is your turn, you will have to play one of the cards in your hand. To be able to play cards, they have to be the same type or the same color as a card on the table.\n" +
            "If you don't have any validated cards, you will automatically draw cards until you get one that can be played.\n" +
            "Overall there are 112 cards in the deck, once you run out of cards the game will automatically re shuffle your deck.\n" +
            "\n" +
            "During the game, you have the ability to surrender.\n" +
            "The Game will finish once there will be only one active player. After game finish, you can see ranking of players with winners of this game\n" +
            "\t\n";
    Text instructionText;

    public InstructionView(GuiController guiController) {
        this.guiController = guiController;
    }

    public static void main(String[] args) {
        launch(args);
    }



    Group root;

    GuiController guiController;
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {

            this.iniit(primaryStage,null);
            primaryStage.setScene(this.mainScene);
            primaryStage.setHeight(startH);
            primaryStage.setWidth(startW);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.updateOnSize();
    }


    void iniit(Stage primaryStage, Scene mainScene) throws IOException, URISyntaxException {
        root = new Group();

        this.mainScene =mainScene;
        this.updateBackground();

        this.setupButton();
        this.setupInstruction();
        this.addListiners(primaryStage);


        this.updateOnSize();


    }

    private void setupInstruction() {
        this.instructionText = new Text();
        this.instructionText.setText(this.instructions);
        this.instructionText.setFill(Color.WHITE);
        //this.instructionTextArea.(0,0,(int)this.mainScene.getWidth(),(int)this.mainScene.getHeight());
        this.root.getChildren().add(this.instructionText);
    }




    void addListiners(Stage primaryStage) {

        this.mainScene.widthProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        updateOnSize();
                                    }
                                }
                        );
                    }
                }
        );


        this.mainScene.heightProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        updateOnSize();
                                    }
                                }
                        );



                    }
                }

        );

        primaryStage.fullScreenProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        updateOnSize();
                                    }
                                }
                        );
                    }
                }

        );

        primaryStage.iconifiedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        updateOnSize();
                    }
                }
        );


        primaryStage.maximizedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        updateOnSize();
                    }
                }
        );

        primaryStage.iconifiedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        updateOnSize();
                    }
                }
        );

        primaryStage.maxHeightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateOnSize();
            }
        });

        primaryStage.maximizedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        updateOnSize();
                    }
                }
        );

        primaryStage.setOnShown(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        updateOnSize();
                    }
                }

        );








        primaryStage.maximizedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                        Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        updateOnSize();
                                    }
                                }
                        );

                    }
                }
        );


        this.button.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOnButton();
                    }
                }

        );


        this.button.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOutsideButton();
                    }
                }
        );

        this.button.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonPush();
                    }
                }
        );

        this.button.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRelease();
                    }
                }
        );



        this.buttonText.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOnButton();
                    }
                }

        );


        this.buttonText.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOutsideButton();
                    }
                }
        );

        this.buttonText.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonPush();
                    }
                }
        );
        this.buttonText.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRelease();
                    }
                }
        );



    }


    void updateOnSize()
    {
        int miliSec=10;
        PauseTransition delay= new PauseTransition(Duration.millis(miliSec));
        delay.setOnFinished(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        updateBackground();
                        updateButtonSize();
                        updateInstructionText();


                    }
                }

        );

        delay.play();
        delay.setDuration(Duration.millis(miliSec*100));
        delay.play();


    }


    private void updateInstructionText()
    {
        double scaleGuide= this.mainScene.getWidth()<this.mainScene.getHeight()?this.mainScene.getHeight():this.mainScene.getWidth();
        this.instructionText.setY(this.button.getLayoutBounds().getMaxY()*1.5);
        Font font = new Font("Lucida Console", scaleGuide/65);
        this.instructionText.setFont(font);
        this.instructionText.setWrappingWidth(this.mainScene.getWidth());
        if(this.instructionText.getLayoutBounds().getHeight()>this.mainScene.getHeight())
        {
            Font newfont = new Font("Lucida Console", font.getSize()/(this.instructionText.getLayoutBounds().getHeight()/this.mainScene.getHeight()));
            this.instructionText.setFont(newfont);
        }
    }
    void updateBackground()
    {
        if(this.guiController.activeScenes== GuiController.SCENES.INSRTUCTION) {

            this.gradient = new RadialGradient(0, 0, mainScene.getWidth() / 2, mainScene.getHeight() / 2, mainScene.getHeight() > mainScene.getHeight() ? mainScene.getHeight() * 4 : mainScene.getWidth() * 2, false, CycleMethod.NO_CYCLE, this.blueStops);


            mainScene.setFill(gradient);
        }
    }

    private void setupButton() {
        this.button=new Rectangle();
        this.button.setFill(this.tranparentColor);
        this.button.setStroke(Color.WHITE);
        this.button.setStrokeWidth(2);

        this.buttonText = new Text("Main menu");
        this.buttonText.setFill(Color.WHITE);
        this.root.getChildren().add(this.button);
        this.root.getChildren().add(this.buttonText);


    }
    private void updateButtonSize() {
        double buttonHeight=(this.mainScene.getHeight()<this.mainScene.getWidth()?this.mainScene.getHeight():this.mainScene.getWidth())/20;
        this.button.setHeight(buttonHeight);
        this.button.setWidth(buttonHeight*10);
        this.button.setY(0+buttonHeight/10);
        this.button.setX(this.mainScene.getWidth()-this.button.getWidth()-buttonHeight/10);
        this.button.setArcHeight(buttonHeight/2);
        this.button.setArcWidth(buttonHeight/2);


        Font font=new Font("Lucida Console",buttonHeight/1.5);
        this.buttonText.setFont(font);
        this.buttonText.setX(this.button.getX() + this.button.getWidth()/2 - this.buttonText.getLayoutBounds().getWidth()/2 );
        this.buttonText.setY(this.button.getY() + this.button.getHeight()/2 + this.buttonText.getLayoutBounds().getHeight()/2.5 );


    }
    private void onMoveOutsideButton()
    {
        this.button.setFill(this.tranparentColor);
        this.buttonText.setFill(Color.WHITE);
        //System.out.println("outside Button");
    }

    private void onButtonPush()
    {
        this.button.setFill(Color.LIGHTGRAY);
        //System.out.println("push");
    }

    private void onButtonRelease()
    {
        //System.out.println("realse");
        this.button.setFill(Color.WHITE);
        this.guiController.ReturnScene();
    }

    private void onMoveOnButton()
    {
        //System.out.println("on button");
        this.button.setFill(Color.WHITE);
        this.buttonText.setFill(Color.BLACK);
    }


}
