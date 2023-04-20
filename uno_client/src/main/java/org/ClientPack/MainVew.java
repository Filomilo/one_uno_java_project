package org.ClientPack;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;


import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * a class that handles main view
 */
public class MainVew extends Application {

    /**
     * a varaible taht store main scene referance
     */
    private Scene mainScene;
    /**
     * a varaibles that stores information if players connected to server
     */
    private boolean isConnected=false;
    /**
     * a varaibles that stores information if playres declared him self as ready
     */
    public  Boolean isReady=false;

    /**
     * a varaible that stores transparent color for buttons
     */
    public  Color tranparentColor = new Color(1,0,0,0.0);
    /**
     * a varaible that sotes blue color for gradeint base
     */
    private final Color blueColor = new Color(0,0.1,0.6,1);

    /**
     * a varaible that stores gradient stops for background
     */
    private final Stop[] blueStops = new Stop [] {new Stop(0, this.blueColor), new Stop(1, Color.BLACK)} ;

    /**
     * a that stores locks for each controller
     */
    private boolean[] activeControles;



    /**
     * a araible taht stores communicat status text
     */
    private Text connectionStatusText;
    /**
     * a araible taht stores communicat text
     */
    public  Text communicatText;
    /**
     * a varaible that stores status text
     */
    private Text readyStatusText;


    public static void main(String[] args) {
        launch(args);
    }
    /**
     * a araible that stores logo image
     */
    private ImageView unoLogoView;

    /**
     * a araible taht stores buttons text for titles
     */
    public  String[] buttonsTexts ={"Connect","Ready","Ranking","Exit"};
    /**
     * a araible taht stores buttons titles
     */
    public Text[] buttonTitles = new Text[buttonsTexts.length];
    /**
     * a araible taht stores buttons shapes
     */
    public  Rectangle[] buttons = new Rectangle[buttonsTexts.length];

    /**
     * a varialbe that stores help button shape
     */
    private  Rectangle helpButton;
    /**
     * a varaible that stores txet of help button
     */
    private Text helpButtonText;



    /**
     * a variable taht stores root group foe this view
     */

    public Group root;
    /**
     * a varaivle that stoes refence to gui contorller
     */
    private final GuiController guiController;
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

    /**
     * a mthod that setuups alle elemnts of this view
     * @param primaryStage
     * @throws IOException
     */
    public void iniit(Stage primaryStage,Scene mainScene) throws IOException, URISyntaxException {


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

    /**
     * a mehtod that setup help button elemnt
     */
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

    /**
     * a method that updats size of help button based on window size
     */
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

    /**
     * a method that setups lock for buttons
     */
    private void setupLock() {
       this.activeControles = new boolean[]{true, true, true, true, false, false, true};
    //    this.activeControles = new boolean[]{false, false, false, false, false, false, false};
       // this.updateLocks();
    }

    /**
     * a method taht updates look of buttons based on if they are locked
     */
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

    /**
     * a mtohed that setup status txt elements
     */
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

    /**
     * a method that setups sets status as connected
     */
    public  void setStatusConnected()
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
    /**
     * a method that setups sets status as connecting
     */
    public  void setStatusConnecting()
    {
        this.connectionStatusText.setText("Trying to connect");
        this.connectionStatusText.setFill(Color.YELLOW);
    }

    /**
     * a method that setups sets status as disconnected
     */
    private void setStatusDiscconnted()
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





    /**
     * a method taht sets players ready and players connect in bottom right corner
     * @param ready
     * @param connected
     */

    public void setPlayersReady(int ready, int connected)
    {
        System.out.println("SET PLAYER \n\n\n" + ready + "\n\n\n\n");
        this.readyStatusText.setText("Players ready " + ready + "/" + connected);
    }


    /**
     * a method taht loads and setups images
     * @throws IOException
     * @throws URISyntaxException
     */
    private void setupImages() throws IOException, URISyntaxException {
        //FileInputStream fileInputStream= new FileInputStream(getResource("one_logo.png"));
        InputStream inputStream=getClass().getClassLoader().getResourceAsStream("one_logo.png");
        Image unoLogo = new Image(inputStream);

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
    /**
     * method that runs all update size method when scaling window
     */
    private  void addListiners(Stage primaryStage) {

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

    /**
     * method that runs all update size method when scaling window
     */
    public  void updateOnSize()
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

    /**
     * a method called to udpate status text size and postion based n window size
     */
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

    /**
     * a method that updates image size and postioin based window size
     */
    private  void updateImagesSize()
    {
       this.unoLogoView.setFitHeight(mainScene.getHeight()<mainScene.getWidth()?mainScene.getHeight()/2:mainScene.getWidth()/5.0);
       this.unoLogoView.setY(mainScene.getHeight()/2-unoLogoView.getFitHeight()/2);
       this.unoLogoView.setX(mainScene.getWidth()/50.0);
    }

    /**
     * a method taht updates buttons size and postioins based on window size
     */
    private  void updateButtonsSize()
    {
        double buttonHeight=0;

        float buttonHeightToScreenRatio = 12;
        if(this.mainScene.getHeight()<this.mainScene.getWidth()) {
            buttonHeight=(this.mainScene.getHeight() / buttonHeightToScreenRatio);
        }
        else
        {
            buttonHeight=(this.mainScene.getWidth() / buttonHeightToScreenRatio);
        }
        float buttonSizeRatio = 9;
        double buttonWIdth=buttonHeight * buttonSizeRatio;
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


    /**
     * a method that setup buttons shapes
     */

    private  void setupButtons()
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

    /**
     * a method that setups text parametrs for provided text elements
     * @param text
     */
    private  void setupTextParams(Text text)
    {
text.setFill(Color.WHITE);
    }

    /**
     * a method that sets up buttons shapes
     * @param rectangle
     */
    private  void setupButtonshape(Rectangle rectangle)
    {
        rectangle.setStrokeWidth(1.5);
        rectangle.setStroke(Color.WHITE);
        rectangle.setFill(tranparentColor);
    }

    /**
     * a method that updates background size based window sizee
     */
    private  void updateBackground()
    {

        if(this.guiController.activeScenes== GuiController.SCENES.MAIN || this.guiController.activeScenes== GuiController.SCENES.LOGIN) {
            RadialGradient gradient = new RadialGradient(0, 0, this.guiController.mainScene.getWidth() / 2, this.guiController.mainScene.getHeight() / 2, this.guiController.mainScene.getHeight() > this.guiController.mainScene.getWidth() ? this.guiController.mainScene.getHeight() * 4 : this.guiController.mainScene.getWidth() * 2, false, CycleMethod.NO_CYCLE, this.blueStops);


            mainScene.setFill(gradient);
        }
    }

    /**
     * a method that returns index of bututon to determines action on general button methos
     * @param button
     * @return
     */
    private int getButtonINdex(Rectangle button)
    {
        int index=0;
        for(index=0;index<this.buttons.length;index++)
        {
            if(button.equals(this.buttons[index]))
                break;

        }
        return index;
    }

    /**
     * a method called when mouse moved on any button
     * @param button
     */
    private  void onButtonMoved(Rectangle button)
    {


        int index=getButtonINdex(button);
        if(this.activeControles[index+3]) {
            if(button.getFill()!=Color.WHITE)
            this.guiController.isOnButton.set(true);
            button.setFill(Color.WHITE);
            this.buttonTitles[index].setFill(Color.BLACK);
        }
    }

    /**
     * a method called mouse moved outiside every button
     * @param button
     */
    private void onButtonMovedOutside(Rectangle button)
    {
        this.guiController.isOnButton.set(false);

        int index=getButtonINdex(button);
        if(this.activeControles[index+3]) {
            button.setFill(this.tranparentColor);
            this.buttonTitles[index].setFill(Color.WHITE);
        }
    }

    /**
     * a mehtod called on every button when clicked
     * @param button
     */
    private void onButtonBasicClick(Rectangle button)
    {
        int index=getButtonINdex(button);
        if(this.activeControles[index+3]) {
            this.guiController.soundPlayer.playOnButtonClick();

            button.setFill(Color.LIGHTGRAY);
        }

    }

    /**
     * a method called on connect button click
     */
    private void onButtonConnectClick()
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

    /**
     * a method that sets button not ready to ready
     */
    public void setButtonReady()
    {
        this.buttonTitles[1].setText("Ready");
        this.updateButtonsSize();
    }

    /**
     * a method set button ready to not ready
     */
    private void setButtonNotReady()
    {
        this.buttonTitles[1].setText("Not Ready");
        this.updateButtonsSize();
    }


    /**
     * a method called on button ready click
     */
    private void onButtonReadyClick()
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

    /**
     * a method called on ranking click
     */
    private void onButtonRankingClick()
    {
        if(this.activeControles[5]) {
            System.out.println("RANKING");
        this.guiController.clientApp.requestRanking();

        }

        }

    /**
     * a method called on button exitr clik
     */
    private  void onButtonExitClick()
    {
        if(this.activeControles[6]) {
            //System.out.println("EXIT");
            System.exit(1);
        }
    }


    /**
     * a method called when mouse moved on help button
     */

    private  void onHelpMoved()
    {
            helpButton.setFill(Color.WHITE);
            helpButtonText.setFill(Color.BLACK);

    }

    /**
     * a method called when mouse moved outide helpp button
     */
    private  void onHelpMovedOutside()
    {


            helpButton.setFill(this.tranparentColor);
            this.helpButtonText.setFill(Color.WHITE);

    }

    /**
     * a method called when help clicked
     */
    private  void onHelpBasicClick()
    {

            this.guiController.soundPlayer.playOnButtonClick();
            helpButton.setFill(Color.LIGHTGRAY);


    }


    /**
     * a method that runs when mouse released from help button
     */
    private void onHelpReelased()
{
    this.guiController.switchSceneToInstruction();
    onHelpMovedOutside();

}


}
