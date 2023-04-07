package org.example;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;


public class ResultView extends Application {

    Scene mainScene;

    Color tranparentColor = new Color(1,0,0,0.0);
    Color orangeColor = new Color(0.8,0.55,0,1);
    Stop[] orangeStops = new Stop [] {new Stop(0, this.orangeColor), new Stop(1, Color.BLACK)} ;

    Rectangle button;

    Text buttonText;


    public static void main(String[] args) {
        launch(args);
    }

    Text rankingText[];
    final float buttonSizeRatio=9;
    final float winTextHeightToScreenRatio=12;

    Group root;

    GuiController guiController;
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            this.iniit(primaryStage);





            primaryStage.setScene(mainScene);
             //primaryStage.setFullScreen(true);






            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    void iniit(Stage primaryStage) throws IOException {

        root = new Group();

        mainScene = new Scene(root, 1250, 720,true, SceneAntialiasing.BALANCED);

        this.setupRanking();
        this.setupButton();




        this.updateBackground();



        this.updateOnSize();
        this.addListiners(primaryStage);
        this.updateBackground();
    }

    private void setupRanking() {
        List<String> ranking = this.getResults();
        this.rankingText = new Text[ranking.size()];
        int i;
        for (i=0;i<ranking.size();i++)
        {
            this.rankingText[i]=new Text((i+1) + ". " +ranking.get(i));
            this.rankingText[i].setFill(Color.WHITE);
            this.root.getChildren().add(this.rankingText[i]);
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





    public ResultView(GuiController guiController) {
        this.guiController=guiController;
    }

    void addListiners(Stage primaryStage) {

        this.mainScene.widthProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        updateOnSize();
                    }
                }
        );


        this.mainScene.heightProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        updateOnSize();
                    }
                }

        );

        primaryStage.fullScreenProperty().addListener(
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
        this.updateTextSize();
        this.updateButtonSize();
        this.updateBackground();
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

    private void updateTextSize() {
        double bigFontSize=(this.mainScene.getHeight()<this.mainScene.getWidth()?this.mainScene.getHeight():this.mainScene.getWidth())/(1.5 * this.rankingText.length);
        Font bigFont=new Font("Arial",bigFontSize);
        Font smallFont=new Font("Arial",bigFontSize/2);
        this.rankingText[0].setFont(bigFont);
        this.rankingText[0].setX(this.mainScene.getWidth()/6);
        this.rankingText[0].setY(0+ this.rankingText[0].getLayoutBounds().getHeight());
    int i;
        for(i=1;i<this.rankingText.length;i++)
        {
            this.rankingText[i].setFont(smallFont);
            this.rankingText[i].setX(this.mainScene.getWidth()/6);
            this.rankingText[i].setY(0+ this.rankingText[i-1].getLayoutBounds().getMaxY() + this.rankingText[i-1].getLayoutBounds().getHeight()/1.5);
        }

    }


    void updateBackground()
    {
        RadialGradient gradient = new RadialGradient(0, 0, mainScene.getWidth() / 2, mainScene.getHeight() / 2, mainScene.getHeight() > mainScene.getWidth() ? mainScene.getHeight() * 2 : mainScene.getWidth() * 2, false, CycleMethod.NO_CYCLE, this.orangeStops);
        mainScene.setFill(gradient);
    }


    List<String> getResults()
    {
        List<String> results= this.guiController.clientApp.getResults();

        return results;
    }


    private void onMoveOnButton()
    {
        //System.out.println("on button");
        this.button.setFill(Color.WHITE);
        this.buttonText.setFill(Color.BLACK);
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







}
