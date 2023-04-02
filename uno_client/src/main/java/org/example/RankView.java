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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.omg.CORBA.INTERNAL;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;


public class RankView extends Application {
    Scene mainScene;

    boolean isConnected=false;
    Boolean isReady=false;
    final int startH=720;
    final int startW=1280;
    static final String resDir = "uno_client\\src\\main\\resources\\";

    Color tranparentColor = new Color(1,0,0,0.0);
    Color blueColor = new Color(0,0.1,0.6,1);
    Stop[] blueStops = new Stop [] {new Stop(0, this.blueColor), new Stop(1, Color.BLACK)} ;

    Rectangle button;
    Text buttonText;

    Text columnsHeadersText[] = new Text[3];
    String columnsTextFill[] = new String[] {"nb.", "Nick","wins"};

    Text nicksRank[];
    Text amtOfWinsText[];


    Rectangle rankViewBase;
    double scrollAmount=0;
    private RadialGradient gradient;
    private Shape mask;


    public static void main(String[] args) {
        launch(args);
    }



    Group root;

    GuiController guiController;
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {

            this.iniit(primaryStage);
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


    void iniit(Stage primaryStage) throws IOException, URISyntaxException {
        root = new Group();

        mainScene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight(),true, SceneAntialiasing.BALANCED);
        this.updateBackground();

        this.setupRankView();
        this.setupMask();
        this.setupButton();
        this.addListiners(primaryStage);


        this.updateOnSize();


    }

    private void setupMask() {
        this.mask = new Rectangle();
        this.mask.setFill(Color.RED);
        this.root.getChildren().add(this.mask)
;    }

    private void setupRankView() {

        this.rankViewBase= new Rectangle();
        this.rankViewBase.setFill(tranparentColor);
        this.rankViewBase.setStrokeWidth(2);
        this.rankViewBase.setStroke(Color.WHITE);
        this.root.getChildren().add(rankViewBase);




        for (int i=0;i<this.columnsHeadersText.length;i++)
        {

            this.columnsHeadersText[i] = new Text(this.columnsTextFill[i]);
            this.columnsHeadersText[i].setFill(Color.WHITE);
            this.columnsHeadersText[i].setX(500);
            this.columnsHeadersText[i].setY(500);


            this.root.getChildren().addAll( this.columnsHeadersText[i]);
        }


        String nicks[] =this.getNicksArray();
        int amtOfwins[]= this.getScoreArray();
        this.nicksRank = new Text[nicks.length];
        this.amtOfWinsText = new Text[nicks.length];
        for(int i=0; i<nicks.length;i++)
        {
            this.nicksRank[i] = new Text(nicks[i]);
            this.amtOfWinsText[i] = new Text(Integer.toString(amtOfwins[i]) );
            this.nicksRank[i].setFill(Color.WHITE);
            this.amtOfWinsText[i].setFill(Color.WHITE);
            this.root.getChildren().add( this.nicksRank[i]);
            this.root.getChildren().add( this.amtOfWinsText[i]);
        }


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


        this.rankViewBase.setOnScroll(
                new EventHandler<ScrollEvent>() {
                    @Override
                    public void handle(ScrollEvent event) {
                        onScrollMainRank(event);
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
                        updateMainRank();
                        updateMask();


                    }
                }

        );

        delay.play();
        delay.setDuration(Duration.millis(miliSec*100));
        delay.play();


    }

    void updateMainRank()
    {
        double scaleY=0.9;
        double scaleX=0.9;
        this.rankViewBase.setHeight(this.mainScene.getHeight()*scaleY);
        this.rankViewBase.setWidth(this.mainScene.getWidth()*scaleX);
        this.rankViewBase.setX((this.mainScene.getWidth()-(this.mainScene.getWidth()*scaleX))/2);
        this.rankViewBase.setY((this.mainScene.getHeight()-(this.mainScene.getHeight()*scaleY))/2+(this.mainScene.getHeight()*(0.92-scaleY)));
        this.rankViewBase.setArcWidth(this.rankViewBase.getWidth()/20);
        this.rankViewBase.setArcHeight(this.rankViewBase.getWidth()/20);
        this.updateHeaderTextSize();
        this.updateRecordsTextSize();

    }

    private void updateMask()
    {
        int index = this.root.getChildren().indexOf(this.mask);
        Shape shape= new Rectangle(this.mainScene.getWidth()+100,this.mainScene.getHeight()+100);

        this.mask = Shape.subtract(shape,this.rankViewBase);
        mask.setFill(this.gradient);
        this.root.getChildren().set(index,this.mask);
    }

    private void updateRecordsTextSize() {
        Font font = this.columnsHeadersText[0].getFont();
        for (Text tx: this.nicksRank
             ) {
            tx.setFont(font);
        }

        for (Text tx: this.amtOfWinsText
        ) {
            tx.setFont(font);
        }

    }

    private void updateHeaderTextSize() {
        double scaleGuide=this.mainScene.getWidth()>this.mainScene.getHeight()?this.mainScene.getHeight():this.mainScene.getWidth();
        double fontSize= scaleGuide/1.2;
        Font font = new Font("Arial", fontSize);
        for (Text tx: this.columnsHeadersText
             ) {
            tx.setFont(font);
        }
        
        
    }


    void updateBackground()
    {

        this.gradient = new RadialGradient(0,0,mainScene.getWidth()/2,mainScene.getHeight()/2,mainScene.getHeight()>mainScene.getHeight()?mainScene.getHeight()*4:mainScene.getWidth()*2, false, CycleMethod.NO_CYCLE,this.blueStops);


        mainScene.setFill(gradient);
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


        Font font=new Font("Arial",buttonHeight/1.5);
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
        this.guiController.switchScenetoMain();
    }

    private void onMoveOnButton()
    {
        //System.out.println("on button");
        this.button.setFill(Color.WHITE);
        this.buttonText.setFill(Color.BLACK);
    }

    private void onScrollMainRank(ScrollEvent event)
    {
        this.scrollAmount+=event.getDeltaY();
       // System.out.println("scrollonign: " + this.scrollAmount);
    }

    int[] getScoreArray()
    {
        return new int[]{10,5,8,4,1,2,8,1000};
    }

    String[] getNicksArray()
    {
        return new String[]{"sdfdsfdsf","asdasdwwww","~~~~~~~~~~~~~~~~~~~~~~~~~~~~","4","dfsssss","dfgdfg","fgdfg","ggggg"};
    }

}
