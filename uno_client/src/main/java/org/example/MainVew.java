package org.example;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;


import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;


public class MainVew extends Application {

    Scene mainScene;

    final int startH=720;
    final int startW=1280;
    static final String resDir = "uno_client\\src\\main\\resources\\";

    Color tranparentColor = new Color(1,0,0,0.0);
    Color blueColor = new Color(0,0.1,0.6,1);
    Stop[] blueStops = new Stop [] {new Stop(0, this.blueColor), new Stop(1, Color.BLACK)} ;
    public static void main(String[] args) {
        launch(args);
    }

    Image unoLogo;
    ImageView unoLogoView;


    String buttonsTexts[]={"Connect","Ready","Ranking","Exit"};
    Rectangle buttons[] = new Rectangle[buttonsTexts.length];
    final float buttonSizeRatio=9;
    final float buttonHeightToScreenRatio=12;

    Group root;
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {


            root = new Group();

            mainScene = new Scene(root, 1250, 720,true, SceneAntialiasing.BALANCED);




            this.addListiners(primaryStage);


           this.setBackground();
            primaryStage.setScene(mainScene);
         //   primaryStage.setFullScreen(true);


            this.setupImages();
            this.setupButtons();

            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void setupImages() throws IOException {
        FileInputStream fileInputStream= new FileInputStream(MainVew.resDir + "one_logo.png");
        this.unoLogo = new Image(fileInputStream);

        this.unoLogoView= new ImageView(unoLogo);
        this.unoLogoView.setPreserveRatio(true);
        this.updateImagesSize();
        this.unoLogoView.setSmooth(true);
        this.updateImagesSize();


        this.root.getChildren().add(this.unoLogoView);


    }




    private void addListiners(Stage primaryStage) {

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
/*
     this.buttonConnect.setOnMouseMoved(
             new EventHandler<MouseEvent>() {
                 @Override
                 public void handle(MouseEvent event) {
                     onButtonMoved(buttonConnect);
                 }
             }
     );

     this.buttonConnect.setOnMouseExited(

             new EventHandler<MouseEvent>() {
                 @Override
                 public void handle(MouseEvent event) {
                     onButtonMovedOutside(buttonConnect);
                 }
             }
     );


 */





    }


    void updateOnSize()
    {
        this.updateImagesSize();
    this.setBackground();
    this.updateButtonsSize();
    }

    void updateImagesSize()
    {
       this.unoLogoView.setFitHeight(mainScene.getHeight()<mainScene.getWidth()?mainScene.getHeight()/2:mainScene.getWidth()/5.0);
       this.unoLogoView.setY(mainScene.getHeight()/2-unoLogoView.getFitHeight()/2);
       this.unoLogoView.setX(mainScene.getWidth()/50.0);
    }

    void updateButtonsSize()
    {
        double buttonHeight=0;

        if(this.mainScene.getHeight()<this.mainScene.getWidth()) {
            buttonHeight=(this.mainScene.getHeight() / this.buttonHeightToScreenRatio);
        }
        else
        {
            buttonHeight=(this.mainScene.getWidth() / this.buttonHeightToScreenRatio  );
        }
        double buttonWIdth=buttonHeight * this.buttonSizeRatio;
        double offset=5;

        double initalX=this.mainScene.getWidth()-buttonWIdth-buttonWIdth/5;
        double initalY=this.mainScene.getHeight()/1.65- ((offset+buttonHeight)*buttons.length)/2;

        int iterator=0;
        for (Rectangle button:this.buttons) {
                button.setHeight(buttonHeight );
            button.setWidth(buttonWIdth);
            button.setArcHeight(0.5* button.getHeight());
            button.setArcWidth(0.5* button.getHeight());

            button.setX(initalX);
            button.setY(initalY + (button.getHeight()+offset)*iterator);

            if(iterator==0)
                iterator++;
            iterator++;
        }
    }


    void setupButtons()
    {
        for(int i=0;i<buttons.length;i++){
            buttons[i]=new Rectangle();
            System.out.println("button");
            this.setupButtonshape(buttons[i]);
            this.root.getChildren().add(buttons[i]);
        }
        this.updateButtonsSize();

    }
    void setupButtonshape(Rectangle rectangle)
    {
        rectangle.setStrokeWidth(1);
        rectangle.setStroke(Color.WHITE);
        rectangle.setFill(tranparentColor);
    }

    void setBackground()
    {

        RadialGradient gradient = new RadialGradient(0,0,mainScene.getWidth()/2,mainScene.getHeight()/2,mainScene.getHeight()>mainScene.getHeight()?mainScene.getHeight()*4:mainScene.getWidth()*4, false, CycleMethod.NO_CYCLE,this.blueStops);


        mainScene.setFill(gradient);
    }

    void onButtonMoved(Rectangle button)
    {

        button.setFill(Color.WHITE);
    }

    void onButtonMovedOutside(Rectangle button)
    {

        button.setFill(this.tranparentColor);
    }

}
