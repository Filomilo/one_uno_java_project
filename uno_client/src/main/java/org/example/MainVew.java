package org.example;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.omg.CORBA.WStringSeqHelper;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;


public class MainVew extends Application {

    Scene mainScene;

    boolean isConnected=false;
    Boolean isReady=false;
    final int startH=720;
    final int startW=1280;
    static final String resDir = "uno_client\\src\\main\\resources\\";

    Color tranparentColor = new Color(1,0,0,0.0);
    Color blueColor = new Color(0,0.1,0.6,1);
    Stop[] blueStops = new Stop [] {new Stop(0, this.blueColor), new Stop(1, Color.BLACK)} ;


    boolean activeControles [];




    private Text connectionStatusText;
     Text communicatText;
    private Text readyStatusText;


    public static void main(String[] args) {
        launch(args);
    }

    Image unoLogo;
    ImageView unoLogoView;


  ;

    String buttonsTexts[]={"Connect","Ready","Ranking","Exit"};
    Text buttonTitles[]= new Text[buttonsTexts.length];
    Rectangle buttons[] = new Rectangle[buttonsTexts.length];
    final float buttonSizeRatio=9;
    final float buttonHeightToScreenRatio=12;



    Rectangle helpButton;
    Text helpButtonText;





    Group root;

    GuiController guiController;
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {

            this.iniit(primaryStage,null);
            primaryStage.setScene(this.mainScene);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.updateOnSize();

    }


    void iniit(Stage primaryStage,Scene mainScene) throws IOException, URISyntaxException {


        this.mainScene=mainScene;


        root = new Group();

       // mainScene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight(),true, SceneAntialiasing.BALANCED);
        this.setupLock();
        this.updateBackground();
        this.setupImages();
        this.setupButtons();
        this.setupHelpButton();
        this.setupStatusText();
        this.addListiners(primaryStage);
        this.updateOnSize();
        this.updateLocks();
    }

    private void setupHelpButton() {
        this.helpButton = new Rectangle();
        this.helpButton.setFill(this.tranparentColor);
        this.helpButton.setStrokeWidth(1);
        this.helpButtonText= new Text("?");
        this.helpButtonText.setFill(Color.WHITE);
        this.helpButtonText.setBoundsType(TextBoundsType.VISUAL);
        this.helpButton.setStroke(Color.WHITE);

        this.root.getChildren().addAll(this.helpButton,this.helpButtonText);
        this.updateHelpButtonSize();

    }

    private void updateHelpButtonSize() {

        double screenRation=25;
        double size=this.mainScene.getHeight()>this.mainScene.getWidth()?this.mainScene.getWidth()/screenRation:this.mainScene.getHeight()/screenRation;
        this.helpButton.setHeight(size);
        this.helpButton.setWidth(size);
        this.helpButton.setStrokeWidth(size/20);
        this.helpButton.setX(this.helpButton.getStrokeWidth());
        this.helpButton.setY(this.helpButton.getStrokeWidth());
        this.helpButton.setArcHeight(size/2);
        this.helpButton.setArcWidth(size/2);

        Font font = new Font("Arial", size);
        this.helpButtonText.setFont(font);
        this.helpButtonText.setX(this.helpButtonText.getLayoutBounds().getWidth()/2+this.helpButton.getX() );
        this.helpButtonText.setY(this.helpButton.getHeight()/2 +  this.helpButton.getY() + this.helpButtonText.getLayoutBounds().getHeight()/2);

    }

    private void setupLock() {
       this.activeControles = new boolean[]{true, true, true, true, false, false, true};
    //    this.activeControles = new boolean[]{false, false, false, false, false, false, false};
       // this.updateLocks();
    }

    public void updateLocks() {

        System.out.println("LOCK UPDATE: " + Arrays.toString(this.activeControles) + "\n");
        for(int i = 0;i<4;i++) {

            if (!activeControles[i + 3]) {
                this.buttonTitles[i].setFill(Color.GREY);
                this.buttons[i].setStroke(Color.GREY);
            } else {
                this.buttonTitles[i].setFill(Color.WHITE);
                this.buttons[i].setStroke(Color.WHITE);
            }
        }



    }

    private void setupStatusText() {

        this.connectionStatusText= new Text();
        this.readyStatusText= new Text();
        this.communicatText= new Text("");

        this.connectionStatusText.setFill(Color.WHITE);
        this.readyStatusText.setFill(Color.WHITE);
        this.communicatText.setFill(Color.GREENYELLOW);

        this.root.getChildren().addAll(this.connectionStatusText,this.readyStatusText,this.communicatText);

        this.setPlayersReady(0,0);
        this.setStatusDiscconnted();

        
    }


    void setStatusConnected()
    {
        this.isConnected=true;
        this.activeControles[0]=false;
        this.activeControles[1]=false;
        this.activeControles[2]=false;
        this.activeControles[4]=true;
        this.activeControles[5]=true;
        this.updateLocks();


        this.connectionStatusText.setText("Connected");
        this.connectionStatusText.setFill(Color.GREEN);
        this.buttonTitles[0].setText("Disconnect");

        this.updateButtonsSize();

    }

    void setStatusConnecting()
    {
        this.connectionStatusText.setText("Trying to connect");
        this.connectionStatusText.setFill(Color.YELLOW);
    }

    void setStatusDiscconnted()
    {
        this.isConnected=false;
        this.activeControles[0]=true;
        this.activeControles[1]=true;
        this.activeControles[2]=true;
        this.activeControles[4]=false;
        this.activeControles[5]=false;
        this.updateLocks();


        this.connectionStatusText.setText("Disconnected");
        this.connectionStatusText.setFill(Color.RED);
        this.buttonTitles[0].setText("Connect");
        this.updateButtonsSize();
    }




    void setupTextFiledTitle(Text text)
    {
    text.setFill(Color.WHITE);
    }

    


    void setPlayersReady(int ready, int connected)
    {
        System.out.println("SET PLAYER \n\n\n" + ready + "\n\n\n\n");
        this.readyStatusText.setText("Players ready " + ready + "/" + connected);
    }

    void setupTextField(TextField textField)
    {
        textField.setStyle("-fx-text-fill: white;");
       textField.setBackground(Background.EMPTY);
        textField.setAlignment(Pos.CENTER);
    }

    void setupLine(Line line)
    {
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(2);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
    }

    private void setupImages() throws IOException, URISyntaxException {
        //FileInputStream fileInputStream= new FileInputStream(getResource("one_logo.png"));
        InputStream inputStream=getClass().getClassLoader().getResourceAsStream("one_logo.png");
        this.unoLogo = new Image(inputStream);

        this.unoLogoView= new ImageView(unoLogo);
        this.unoLogoView.setPreserveRatio(true);
        this.updateImagesSize();
        this.unoLogoView.setSmooth(true);
        this.updateImagesSize();


        this.root.getChildren().add(this.unoLogoView);


    }


    public MainVew(GuiController guiController) {
        this.guiController=guiController;
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



        int i=0;
        for (Rectangle button: this.buttons
             ) {
            
            button.setOnMouseMoved(

                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            onButtonMoved(button);
                        }
                    }
            );
            this.buttonTitles[i].setOnMouseMoved(

                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            onButtonMoved(button);
                        }
                    }
            );



            button.setOnMouseExited(

                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            onButtonMovedOutside(button);
                        }
                    }

            );

            this.buttonTitles[i].setOnMouseExited(

                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            onButtonMovedOutside(button);
                        }
                    }

            );



            button.setOnMousePressed(

                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            onButtonBasicClick(button);
                        }
                    }
            );
            this.buttonTitles[i].setOnMousePressed(

                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            onButtonBasicClick(button);
                        }
                    }
            );
            i++;


        }

        this.buttons[0].setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonConnectClick();
                        onButtonMoved(buttons[0]);
                    }
                }
        );

        this.buttonTitles[0].setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonConnectClick();
                        onButtonMoved(buttons[0]);
                    }
                }
        );

        this.buttons[1].setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonReadyClick();
                        onButtonMoved(buttons[1]);
                    }
                }
        );
        this.buttonTitles[1].setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonReadyClick();
                        onButtonMoved(buttons[1]);
                    }
                }
        );


        this.buttons[2].setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRankingClick();
                        onButtonMoved(buttons[2]);
                    }
                }
        );
        this.buttonTitles[2].setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRankingClick();
                        onButtonMoved(buttons[2]);
                    }
                }
        );

        this.buttons[3].setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonExitClick();
                        onButtonMoved(buttons[3]);
                    }
                }
        );
        this.buttonTitles[3].setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonExitClick();
                        onButtonMoved(buttons[3]);
                    }
                }
        );







        helpButton.setOnMouseMoved(

                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onHelpMoved();
                    }
                }
        );
        helpButtonText.setOnMouseMoved(

                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onHelpMoved();
                    }
                }
        );



        helpButton.setOnMouseExited(

                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onHelpMovedOutside();
                    }
                }

        );

        helpButtonText.setOnMouseExited(

                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onHelpMovedOutside();
                    }
                }

        );



        helpButton.setOnMousePressed(

                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onHelpBasicClick();
                    }
                }
        );
        this.helpButton.setOnMousePressed(

                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onHelpBasicClick();
                    }
                }
        );

        this.helpButtonText.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onHelpReelased();
            }
        });

        this.helpButton.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onHelpReelased();
            }
        });




    }


    void updateOnSize()
    {
        int miliSec=10;
        PauseTransition delay= new PauseTransition(Duration.millis(miliSec));
                delay.setOnFinished(
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                updateImagesSize();
                                updateBackground();
                                updateButtonsSize();
                                updateStatusText();
                                updateHelpButtonSize();



                            }
                        }

                );

delay.play();
        delay.setDuration(Duration.millis(miliSec*100));
        delay.play();


    }

    private void updateStatusText() {
        double fontSize= this.buttonTitles[0].getFont().getSize()/2;
        Font font=new Font("Arial",fontSize );
                this.connectionStatusText.setFont(font);
        this.readyStatusText.setFont(font);
        this.communicatText.setFont(font);


        this.readyStatusText.setY(this.mainScene.getHeight() - this.readyStatusText.getBoundsInParent().getHeight()/2);
        this.readyStatusText.setX(this.mainScene.getWidth() -this.readyStatusText.getBoundsInParent().getWidth() );

        this.connectionStatusText.setX((this.buttons[0].getBoundsInParent().getMinX() +this.buttons[0].getBoundsInParent().getMaxX())/2 - this.connectionStatusText.getBoundsInParent().getWidth()/2  );
        this.connectionStatusText.setY(this.buttons[0].getBoundsInParent().getMaxY()+ this.connectionStatusText.getBoundsInParent().getHeight()*1.3 );


        this.communicatText.setX( this.connectionStatusText.getLayoutBounds().getMinX() +this.connectionStatusText.getLayoutBounds().getWidth()/2- this.communicatText.getLayoutBounds().getWidth()/2 );
        this.communicatText.setY( this.connectionStatusText.getLayoutBounds().getMaxY() + this.connectionStatusText.getLayoutBounds().getHeight());



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
        double offset=10;

        double initalX=this.mainScene.getWidth()-buttonWIdth-buttonWIdth/5;
        double initalY=this.mainScene.getHeight()/1.65- ((offset+buttonHeight)*buttons.length)/1.5;

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





        //update text on button
        iterator=0;
        double size=this.buttons[0].getHeight()/1.5;
        Font font=new Font("Arial",size);
        for (Text text: this.buttonTitles
             ) {
            text.setFont(font);
            text.setX(this.buttons[iterator].getX() + this.buttons[iterator].getWidth()/2 - text.getLayoutBounds().getWidth()/2);
            text.setY(this.buttons[iterator].getY() + text.getLayoutBounds().getHeight()/2 + this.buttons[iterator].getHeight()/3);


            iterator++;
        }


    }


    void setupButtons()
    {
        for(int i=0;i<buttons.length;i++){
            buttons[i]=new Rectangle();
            this.setupButtonshape(buttons[i]);
            this.root.getChildren().add(buttons[i]);

            this.buttonTitles[i]= new Text(this.buttonsTexts[i]);
            setupTextParams( this.buttonTitles[i]);
            this.root.getChildren().add(this.buttonTitles[i]);
        }
        this.updateButtonsSize();

    }

    void setupTextParams(Text text)
    {
text.setFill(Color.WHITE);
    }
    void setupButtonshape(Rectangle rectangle)
    {
        rectangle.setStrokeWidth(1.5);
        rectangle.setStroke(Color.WHITE);
        rectangle.setFill(tranparentColor);
    }

    void updateBackground()
    {

        if(this.guiController.activeScenes== GuiController.SCENES.MAIN || this.guiController.activeScenes== GuiController.SCENES.LOGIN) {
            RadialGradient gradient = new RadialGradient(0, 0, this.guiController.mainScene.getWidth() / 2, this.guiController.mainScene.getHeight() / 2, this.guiController.mainScene.getHeight() > this.guiController.mainScene.getWidth() ? this.guiController.mainScene.getHeight() * 4 : this.guiController.mainScene.getWidth() * 2, false, CycleMethod.NO_CYCLE, this.blueStops);


            mainScene.setFill(gradient);
        }
    }

    int getButtonINdex(Rectangle button)
    {
        int index=0;
        for(index=0;index<this.buttons.length;index++)
        {
            if(button.equals(this.buttons[index]))
                break;

        }
        return index;
    }
    void onButtonMoved(Rectangle button)
    {


        int index=getButtonINdex(button);
        if(this.activeControles[index+3]) {
            if(button.getFill()!=Color.WHITE)
            this.guiController.isOnButton.set(true);
            button.setFill(Color.WHITE);
            this.buttonTitles[index].setFill(Color.BLACK);
        }
    }

    void onButtonMovedOutside(Rectangle button)
    {
        this.guiController.isOnButton.set(false);

        int index=getButtonINdex(button);
        if(this.activeControles[index+3]) {
            button.setFill(this.tranparentColor);
            this.buttonTitles[index].setFill(Color.WHITE);
        }
    }

    void onButtonBasicClick(Rectangle button)
    {
        int index=getButtonINdex(button);
        if(this.activeControles[index+3]) {
            this.guiController.soundPlayer.playOnButtonClick();

            button.setFill(Color.LIGHTGRAY);
        }

    }

    void onButtonConnectClick()
    {
        if(this.activeControles[3]) {
            this.communicatText.setText("");
            if (!isConnected) {
                this.guiController.switchScenetoLogin();
                /*
                this.setStatusConnecting();
                this.updateOnSize();
                System.out.println("CONNECT");
                if (this.guiController.connectTosServer()) {
                    this.guiController.soundPlayer.playSucces();
                    this.setStatusConnected();
                }
                else {
                    this.guiController.soundPlayer.playFailed();
                    this.setStatusDiscconnted();
                    if (this.communicatText.getText().equals("")) {
                        this.communicatText.setText("Failed to connect");
                    }
                }
                */
            } else {
                this.setStatusDiscconnted();
                this.guiController.disconnectFromServer();
                this.buttonTitles[1].setText("Ready");
                this.updateButtonsSize();
                this.isReady = false;
            }
        }
    }


    void setButtonReady()
    {
        this.buttonTitles[1].setText("Ready");
        this.updateButtonsSize();
    }

    void setButtonNotReady()
    {
        this.buttonTitles[1].setText("Not Ready");
        this.updateButtonsSize();
    }


    void onButtonReadyClick()
    {
        if(this.activeControles[4]) {

            this.communicatText.setText("");
            // guiController.changeSceneToGame();
            if (isReady) {
                setButtonReady();
                this.guiController.sendNotReady();
                this.isReady = false;
            } else {
                setButtonNotReady();

                this.guiController.sendReady();
                this.isReady = true;
            }
            System.out.println("READY");
        }
    }

    void onButtonRankingClick()
    {
        if(this.activeControles[5]) {
            System.out.println("RANKING");
        this.guiController.clientApp.requestRanking();

        }

        }

    void onButtonExitClick()
    {
        if(this.activeControles[6]) {
            //System.out.println("EXIT");
            System.exit(1);
        }
    }




    void onHelpMoved()
    {
            helpButton.setFill(Color.WHITE);
            helpButtonText.setFill(Color.BLACK);

    }

    void onHelpMovedOutside()
    {


            helpButton.setFill(this.tranparentColor);
            this.helpButtonText.setFill(Color.WHITE);

    }

    void onHelpBasicClick()
    {

            this.guiController.soundPlayer.playOnButtonClick();
            helpButton.setFill(Color.LIGHTGRAY);


    }





void onHelpReelased()
{
    this.guiController.switchSceneToInstruction();
    onHelpMovedOutside();

}


}
