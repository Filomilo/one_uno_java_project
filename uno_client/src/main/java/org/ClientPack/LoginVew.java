package org.ClientPack;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * a class that handles login view
 */
public class LoginVew extends Application {

    /**
     * a varaible taht store main scene referance
     */
    private  Scene mainScene;

    /**
     * a varaible that stores transparent color for buttons
     */
    private final Color tranparentColor = new Color(1,0,0,0.0);
    /**
     * a varaible that sotes blue color for gradeint base
     */
    private final Color blueColor = new Color(0,0.1,0.6,1);
    /**
     * a varaible that stores gradient stops for background
     */
   private final Stop[] blueStops = new Stop [] {new Stop(0, this.blueColor), new Stop(1, Color.BLACK)} ;



    /**
     * a varaivle that stoes refence to gui contorller
     */
    private final GuiController guiController;

    /**
     * this contructor creates elemnt with gui cotnroller
     * @param guiController
     */
    public LoginVew(GuiController guiController) {
        this.guiController=guiController;
    }


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * this varaible holds shape of return button
     */
    private  Rectangle returnButton;
    /**
     * this varaible holds text elemnt got return button
     */
    private   Text returnButtonText;
    /**
     * this varaible hold line guides for login sector
     */
    private   Line[] loginLines;
    /**
     * this varaible holds login text fields
     */
    private  TextField[] loginFields;
    /**
     * this varaible hold text guides for login fields
     */
    private  Text[] loginGuides;
    /**
     * this varaible holds login button shape
     */
    private    Rectangle loginButton;
    /**
     * this varaible holds text login button text
     */
    private  Text loginButtonText;
    /**
     * this varaible hold line guides for register sector
     */
    private  Line[] registerLines;
    /**
     * this varaible holds text fields of register sector
     */
    private  TextField[] registerFields;
    /**
     * this varaible holds text elemnts with register lines
     */
    private   Text[] registerGuides;
    /**
     * this varaible holds shape of register button
     */
    private   Rectangle registerButton;
    /**
     * this varaible holds text elemnt of button register button
     */
    private  Text registerButtonText;
    /**
     * this varaible holds text elemnt of login communicat
     */
    private  Text loginCommunicat;
    /**
     * this varaible holds text elemnt of register communicat
     */
    private  Text registerCommunicat;

    /**
     * this varaible hold text elemnt of server adress line guides
     */
    private  Line[] serverAdressLines;
    /**
     * this varaible hold text elemnt of server adress fields text
     */
    private  Text[] serverAdressGuides;
    /**
     * this varaible hold text elemnt of server adress fields
     */
    private  TextField[] serverAdressFields;
    /**
     * this varaible hold text elemnt of server adress communicat
     */
    private  Text serverAdressCommunicat;



    /**
     * a variable taht stores root group foe this view
     */
    public   Group root;

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            this.root=new Group();
            mainScene= new Scene(root,1270,620);
           this.iniit(primaryStage,mainScene);
            primaryStage.setScene(this.mainScene);
            mainScene.setRoot(this.root);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //this.updateOnSize();

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

        this.updateBackground();

        this.setupAdressField();
        this.setupRegisterSector();
        this.setupReturnButton();

        this.setupLoginSector();
        this.addListiners(primaryStage);
        this.updateOnSize();




        this.registerFields[1].setText("");
        this.registerFields[2].setText("");


        this.loginFields[1].setText("");


    }

    /**
     * this method setup adress field for login view
     */
    private void setupAdressField() {
        this.serverAdressLines = new Line[2];
        this.serverAdressLines[0] = new Line();
        this.serverAdressLines[1] = new Line();
        this.serverAdressLines[0].setStroke(Color.WHITE);
        this.serverAdressLines[1].setStroke(Color.WHITE);
        this.serverAdressLines[0].setStrokeWidth(2);
        this.serverAdressLines[1].setStrokeWidth(2);

        this.serverAdressCommunicat= new Text();
        this.serverAdressCommunicat.setFill(Color.GOLDENROD);

        this.serverAdressFields= new TextField[2];
        this.serverAdressFields[0] = new TextField();
        this.serverAdressFields[1] = new TextField();
        this.serverAdressFields[0].setBackground(Background.EMPTY);
        this.serverAdressFields[0].setStyle("-fx-text-fill: white;");
        this.serverAdressFields[0] .setAlignment(Pos.BOTTOM_CENTER);
        this.serverAdressFields[1] = new TextField();
        this.serverAdressFields[1].setBackground(Background.EMPTY);
        this.serverAdressFields[1].setStyle("-fx-text-fill: white;");
        this.serverAdressFields[1] .setAlignment(Pos.BOTTOM_CENTER);
        this.serverAdressGuides = new Text[2];
        this.serverAdressGuides[0]= new Text("adress");
        this.serverAdressGuides[1] = new Text("port");
        this.serverAdressGuides[0].setFill(Color.WHITE);
        this.serverAdressGuides[1].setFill(Color.WHITE);


        this.root.getChildren().addAll(this.serverAdressLines);
        this.root.getChildren().addAll(this.serverAdressCommunicat);
        this.root.getChildren().addAll( this.serverAdressFields);
        this.root.getChildren().addAll(  this.serverAdressGuides);
    }
    /**
     * this method setup login sector of login view
     */
    private void setupLoginSector() {
        loginCommunicat = new Text();
        loginCommunicat.setFill(Color.GOLDENROD);
        loginLines=new Line[2];
        loginLines[0] = new Line();
        loginLines[1] = new Line();
       loginLines[0].setStroke(Color.WHITE);
        loginLines[0].setStrokeWidth(2);
        loginLines[1].setStroke(Color.WHITE);
        loginLines[1].setStrokeWidth(2);


        loginFields = new TextField[2];
        loginFields[0] = new TextField();
        loginFields[1] = new PasswordField();
        loginFields[0].setAlignment(Pos.BOTTOM_CENTER);
        loginFields[1].setAlignment(Pos.BOTTOM_CENTER);
        loginFields[0].setBackground(Background.EMPTY);
        loginFields[1].setBackground(Background.EMPTY);
        loginFields[0].setStyle("-fx-text-fill: white;");
        loginFields[1].setStyle("-fx-text-fill: white;");


        loginGuides = new Text[2];
        loginGuides[0] = new Text("Nick");
        loginGuides[1] = new Text("Passowrd");

        loginGuides[0].setFill(Color.WHITE);
        loginGuides[1].setFill(Color.WHITE);

        loginGuides[0].setBoundsType(TextBoundsType.VISUAL);
        loginGuides[1].setBoundsType(TextBoundsType.VISUAL);
        loginButton = new Rectangle();
        loginButtonText = new Text("Login");
        loginButtonText.setBoundsType(TextBoundsType.VISUAL);
        loginButton.setFill(Color.TRANSPARENT);
        loginButton.setStroke(Color.WHITE);
        loginButton.setStrokeWidth(2);
        loginButtonText.setFill(Color.WHITE);



        this.root.getChildren().addAll(loginLines);
        this.root.getChildren().addAll(loginFields);
        this.root.getChildren().addAll(loginGuides);
        this.root.getChildren().addAll(loginButton,loginButtonText,loginCommunicat);
    }

    /**
     * this method setup reigsiter sector of login view
     */
    private void setupRegisterSector() {
        registerCommunicat= new Text();
        registerCommunicat.setFill(Color.GOLDENROD);

        registerLines=new Line[3];
        registerFields = new TextField[3];
        registerGuides = new Text[3];


        for (int i=0;i<3;i++)
        {
            registerLines[i] = new Line();
            if(i>0)
                registerFields[i] = new PasswordField();
            else
                registerFields[i] = new TextField();
            registerLines[i].setStroke(Color.WHITE);
            registerLines[i].setStrokeWidth(2);
            registerFields[i].setBackground(Background.EMPTY);
            registerFields[i].setStyle("-fx-text-fill: white;");
            registerFields[i] .setAlignment(Pos.BOTTOM_CENTER);


            registerGuides[i] = new Text();
            registerGuides[i].setFill(Color.WHITE);
            registerGuides[i].setBoundsType(TextBoundsType.VISUAL);
        }
        registerGuides[0].setText("Nick");
        registerGuides[1].setText("Password");
        registerGuides[2].setText("Repeat Password");


        registerButton = new Rectangle();
        registerButtonText = new Text("Register");
        registerButtonText.setBoundsType(TextBoundsType.VISUAL);
        registerButton.setFill(Color.TRANSPARENT);
        registerButton.setStroke(Color.WHITE);
        registerButton.setStrokeWidth(2);
        registerButtonText.setFill(Color.WHITE);



        this.root.getChildren().addAll(registerFields);
        this.root.getChildren().addAll(registerLines);
        this.root.getChildren().addAll(registerGuides);
        this.root.getChildren().addAll(registerButton,registerButtonText,registerCommunicat);
    }

    /**
     * this method calls all update size method when chaning size of window
     */
    public  void updateOnSize()
    {


        int miliSec=100;
        PauseTransition delay= new PauseTransition(Duration.millis(miliSec));
        delay.setOnFinished(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        updateBackground();
                        updateReturnButtonSize();
                        updateSectors();
                        updateAress();

                    }
                }

        );

        delay.play();
        delay.setDuration(Duration.millis(miliSec*100));
        delay.play();







    }

    /**
     * this method update adress areas size and postion based on windwo size
     */
    private void updateAress() {
        double fieldWidth=this.mainScene.getWidth()/5;
        double fieldHeight=this.mainScene.getHeight()/8;

        this.serverAdressLines[0].setStartX(fieldWidth/6);
        this.serverAdressLines[0].setEndX(this.serverAdressLines[0].getStartX()+fieldWidth);
        this.serverAdressLines[1].setStartX(this.serverAdressLines[0].getEndX()+fieldWidth/4);
        this.serverAdressLines[1].setEndX(this.serverAdressLines[1].getStartX()+fieldWidth);

        this.serverAdressLines[0].setStartY(fieldHeight);
        this.serverAdressLines[0].setEndY(fieldHeight);
        this.serverAdressLines[1].setStartY(fieldHeight);
        this.serverAdressLines[1].setEndY(fieldHeight);

        Font font = new Font("Arial",fieldHeight/3);
        this.serverAdressFields[0].setPrefSize(fieldWidth,fieldHeight);
        this.serverAdressFields[0].setFont(font);
        this.serverAdressFields[0].setTranslateX(this.serverAdressLines[0].getStartX());
        this.serverAdressFields[0].setTranslateY(this.serverAdressLines[0].getStartY()- this.serverAdressFields[0].getHeight());

        this.serverAdressFields[1].setPrefSize(fieldWidth,fieldHeight);
        this.serverAdressFields[1].setFont(font);
        this.serverAdressFields[1].setTranslateX(this.serverAdressLines[1].getStartX());
        this.serverAdressFields[1].setTranslateY(this.serverAdressLines[1].getStartY()- this.serverAdressFields[0].getHeight());


        Font fontGuide = new Font("Arial",fieldHeight/4);
        this.serverAdressGuides[0].setFont(fontGuide);
        this.serverAdressGuides[1].setFont(fontGuide);

        this.serverAdressGuides[0].setX(this.serverAdressLines[0].getStartX());
        this.serverAdressGuides[0].setY(this.serverAdressLines[0].getStartY()+this.serverAdressGuides[0].getLayoutBounds().getHeight());
        this.serverAdressGuides[1].setX(this.serverAdressLines[1].getStartX());
        this.serverAdressGuides[1].setY(this.serverAdressLines[1].getStartY()+this.serverAdressGuides[1].getLayoutBounds().getHeight());

        this.serverAdressCommunicat.setFont(fontGuide);
        this.serverAdressCommunicat.setY(this.serverAdressGuides[1].getLayoutBounds().getMaxY()+this.serverAdressCommunicat.getLayoutBounds().getHeight());
        this.serverAdressCommunicat.setX((this.serverAdressLines[0].getStartX()+this.serverAdressLines[1].getEndX())/2 - this.serverAdressCommunicat.getLayoutBounds().getWidth()/2);

    }

    /**
     * update size and position of login and register size
     */
    private void updateSectors() {


        double fieldWidth=this.mainScene.getWidth()/3;
        double fieldHeight=this.mainScene.getHeight()/8;

        Font textFieldFont = new Font("Arial", fieldHeight/2);
        Font textgudieont = new Font("Arial", fieldHeight/3);
        loginGuides[0].setFont(textgudieont);
        loginGuides[1].setFont(textgudieont);


        loginLines[0].setStartX(fieldWidth/3);
        loginLines[0].setEndX(fieldWidth/3+fieldWidth);
        loginLines[0].setStartY(this.mainScene.getHeight()/2-fieldHeight/2);
        loginLines[0].setEndY(this.mainScene.getHeight()/2-fieldHeight/2);
        loginFields[0].setTranslateX(loginLines[0].getStartX());
        loginFields[0].setPrefSize(fieldWidth,fieldHeight);

        loginFields[0].setTranslateY(loginLines[0].getStartY()- loginFields[0].getHeight());
        loginFields[0].setFont(textFieldFont);



        loginLines[1].setStartX(fieldWidth/3);
        loginLines[1].setEndX(fieldWidth/3+fieldWidth);
        loginLines[1].setStartY(this.mainScene.getHeight()/2+fieldHeight*1.5);
        loginLines[1].setEndY(this.mainScene.getHeight()/2+fieldHeight*1.5);
        loginFields[1].setTranslateX(loginLines[1].getStartX());
        loginFields[1].setPrefSize(fieldWidth,fieldHeight);
        loginFields[1].setTranslateY(loginLines[1].getStartY()-loginFields[1].getHeight());

        loginFields[1].setFont(textFieldFont);


        loginGuides[0].setY(this.loginLines[0].getStartY()+ loginGuides[0].getLayoutBounds().getHeight()*1.5);
        loginGuides[0].setX((this.loginLines[0].getStartX()+this.loginLines[0].getEndX())/2-this.loginGuides[0].getLayoutBounds().getWidth()/2);

        loginGuides[1].setY(this.loginLines[1].getStartY()+ loginGuides[1].getLayoutBounds().getHeight()*1.5);
        loginGuides[1].setX((this.loginLines[1].getStartX()+this.loginLines[1].getEndX())/2-this.loginGuides[1].getLayoutBounds().getWidth()/2);


        loginButton.setX(loginLines[1].getStartX());
        loginButton.setY(this.loginLines[1].getEndY()+ fieldHeight);
        loginButton.setHeight(fieldHeight/1.2);
        loginButton.setWidth(fieldWidth);

        Font buttonFont= new Font("Arial", loginButton.getHeight()>loginButton.getWidth()?loginButton.getWidth()/3: loginButton.getHeight()/3);
        loginButtonText.setFont(buttonFont);
        loginButtonText.setY(loginButton.getY()+loginButton.getHeight()/2+loginButtonText.getLayoutBounds().getHeight()/2);
        loginButtonText.setX(loginButton.getX()+loginButton.getWidth()/2-loginButtonText.getLayoutBounds().getWidth()/2);
        loginButton.setArcHeight(buttonFont.getSize());
        loginButton.setArcWidth(buttonFont.getSize());


        loginCommunicat.setFont(buttonFont);
        loginCommunicat.setY(loginButton.getY()+loginButton.getHeight()+loginCommunicat.getLayoutBounds().getHeight());
        loginCommunicat.setX(loginButton.getX()+loginButton.getWidth()/2 -loginCommunicat.getLayoutBounds().getWidth()/2 );

        for(int i=0;i<this.registerFields.length;i++)
        {

            this.registerLines[i].setEndX(loginLines[0].getEndX()+fieldWidth/2+fieldWidth);
            this.registerLines[i].setStartX(loginLines[0].getEndX()+fieldWidth/2);
            this.registerLines[i].setStartY(this.mainScene.getHeight()/2 + (i-1) * fieldHeight*2 - fieldHeight/2);
            this.registerLines[i].setEndY( registerLines[i].getStartY());



            this.registerGuides[i].setFont(textgudieont);
            this.registerGuides[i].setY(this.registerLines[i].getStartY()+registerGuides[i].getLayoutBounds().getHeight()*1.5);
            this.registerGuides[i].setX((this.registerLines[i].getStartX() + this.registerLines[i].getEndX())/2-registerGuides[i].getLayoutBounds().getWidth()/2);

            this.registerFields[i].setFont(textFieldFont);
            this.registerFields[i].setPrefSize(fieldWidth,fieldHeight);
            this.registerFields[i].setTranslateX(this.registerLines[i].getStartX());
            this.registerFields[i].setTranslateY(this.registerLines[i].getStartY()-this.registerFields[i].getHeight());



             }

        this.registerButton.setArcWidth(buttonFont.getSize());
        this.registerButton.setArcHeight(buttonFont.getSize());
        this.registerButton.setHeight(buttonFont.getSize());
        this.registerButton.setHeight(this.loginButton.getHeight());
        this.registerButton.setWidth(this.loginButton.getWidth());
        this.registerButton.setY(this.registerLines[2].getEndY()+ fieldHeight);
        this.registerButton.setX(this.registerLines[2].getStartX());

        registerButtonText.setFont(buttonFont);
        registerButtonText.setY(registerButton.getY()+registerButton.getHeight()/2+registerButtonText.getLayoutBounds().getHeight()/2);
        registerButtonText.setX(registerButton.getX()+registerButton.getWidth()/2-registerButtonText.getLayoutBounds().getWidth()/2);

        registerCommunicat.setFont(buttonFont);
        registerCommunicat.setY(registerButton.getY()+registerButton.getHeight()+registerCommunicat.getLayoutBounds().getHeight());
        registerCommunicat.setX(registerButton.getX()+registerButton.getWidth()/2 -registerCommunicat.getLayoutBounds().getWidth()/2 );


    }

    /**
     * this method add listiners to login view
     * @param primaryStage
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



        this.returnButton.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOnReturnButton();
                    }
                }

        );


        this.returnButton.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOutsideReturnButton();
                    }
                }
        );

        this.returnButton.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonReturnPush();
                    }
                }
        );

        this.returnButton.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonReturnRelease();
                    }
                }
        );



        this.returnButtonText.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOnReturnButton();
                    }
                }

        );


        this.returnButtonText.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOutsideReturnButton();
                    }
                }
        );

        this.returnButtonText.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonReturnPush();
                    }
                }
        );
        this.returnButtonText.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonReturnRelease();
                    }
                }
        );



        this.registerButton.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOnRegisterButton();
                    }
                }

        );


        this.registerButton.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOutsideRegisterButton();
                    }
                }
        );

        this.registerButton.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRegisterPush();
                    }
                }
        );

        this.registerButton.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRegisterRelease();
                    }
                }
        );



        this.registerButtonText.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOnRegisterButton();
                    }
                }

        );


        this.registerButtonText.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOutsideRegisterButton();
                    }
                }
        );

        this.registerButtonText.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRegisterPush();
                    }
                }
        );
        this.registerButtonText.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRegisterRelease();
                    }
                }
        );









        this.loginButton.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOnLoginButton();
                    }
                }

        );


        this.loginButton.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOutsideLoginButton();
                    }
                }
        );

        this.loginButton.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonLoginPush();
                    }
                }
        );

        this.loginButton.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonLoginRelease();
                    }
                }
        );



        this.loginButtonText.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOnLoginButton();
                    }
                }

        );


        this.loginButtonText.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMoveOutsideLoginButton();
                    }
                }
        );

        this.loginButtonText.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonLoginPush();
                    }
                }
        );
        this.loginButtonText.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonLoginRelease();
                    }
                }
        );






    }


    /**
     * this method update backgournd size based on window size
     */
    private  void updateBackground()
    {
        if(this.guiController.activeScenes== GuiController.SCENES.LOGIN) {

            RadialGradient gradient = new RadialGradient(0, 0, this.mainScene.getWidth() / 2, this.mainScene.getHeight() / 2, this.mainScene.getHeight() > this.mainScene.getWidth() ? this.mainScene.getHeight() * 4 : this.mainScene.getWidth() * 2, false, CycleMethod.NO_CYCLE, this.blueStops);

            mainScene.setFill(gradient);
        }
    }

    /**
     * this method set ups return button
     */
    private void setupReturnButton() {
        this.returnButton=new Rectangle();
        this.returnButton.setFill(this.tranparentColor);
        this.returnButton.setStroke(Color.WHITE);
        this.returnButton.setStrokeWidth(2);

        this.returnButtonText = new Text("Main menu");
        this.returnButtonText.setFill(Color.WHITE);
        this.root.getChildren().add(this.returnButton);
        this.root.getChildren().add(this.returnButtonText);


    }

    /**
     * this method updateds return button size and position based on window size
     */
    private void updateReturnButtonSize() {
        double buttonHeight=(this.mainScene.getHeight()<this.mainScene.getWidth()?this.mainScene.getHeight():this.mainScene.getWidth())/20;
        this.returnButton.setHeight(buttonHeight);
        this.returnButton.setWidth(buttonHeight*10);
        this.returnButton.setY(0+buttonHeight/10);
        this.returnButton.setX(this.mainScene.getWidth()-this.returnButton.getWidth()-buttonHeight/10);
        this.returnButton.setArcHeight(buttonHeight/2);
        this.returnButton.setArcWidth(buttonHeight/2);


        Font font=new Font("Lucida Console",buttonHeight/1.5);
        this.returnButtonText.setFont(font);
        this.returnButtonText.setX(this.returnButton.getX() + this.returnButton.getWidth()/2 - this.returnButtonText.getLayoutBounds().getWidth()/2 );
        this.returnButtonText.setY(this.returnButton.getY() + this.returnButton.getHeight()/2 + this.returnButtonText.getLayoutBounds().getHeight()/2.5 );


    }

    /**
     * this method is called when move outside return button
     */
    private void onMoveOutsideReturnButton()
    {
        this.returnButton.setFill(this.tranparentColor);
        this.returnButtonText.setFill(Color.WHITE);
        //System.out.println("outside Button");
    }

    /**
     * this method is called when push return button
     */
    private void onButtonReturnPush()
    {
        this.guiController.soundPlayer.playOnButtonClick();
        this.returnButton.setFill(Color.LIGHTGRAY);
        //System.out.println("push");
    }

    /**
     * this method is called when release return button
     */
    private void onButtonReturnRelease()
    {
        this.resetCommnicats();
        //System.out.println("realse");
        this.returnButton.setFill(Color.WHITE);

        this.guiController.switchScenetoMain();
    }

    /**
     * this method is called when  move on return button
     */
    private void onMoveOnReturnButton()
    {
        //System.out.println("on button");
        this.returnButton.setFill(Color.WHITE);
        this.returnButtonText.setFill(Color.BLACK);
    }


    /**
     * this method is called when  move outside login button
     */
    private void onMoveOutsideLoginButton()
    {
        this.loginButton.setFill(this.tranparentColor);
        this.loginButtonText.setFill(Color.WHITE);
        //System.out.println("outside Button");
    }

    /**
     * this method is called when  pushed login button
     */
    private void onButtonLoginPush()
    {
        this.guiController.soundPlayer.playOnButtonClick();
        this.loginButton.setFill(Color.LIGHTGRAY);
        //System.out.println("push");
    }

    /**
     * this method is called when  released login button
     */
    private void onButtonLoginRelease()
    {
       this.resetCommnicats();
        this.updateOnSize();
        //System.out.println("realse");
        this.loginButton.setFill(Color.WHITE);


            this.guiController.clientApp.setIp(this.serverAdressFields[0].getText());
            this.guiController.clientApp.setPort(Integer.parseInt(this.serverAdressFields[1].getText()));
            this.guiController.clientApp.setNick(this.loginFields[0].getText());
            this.guiController.clientApp.setPass(this.loginFields[1].getText());

            Boolean res =this.guiController.clientApp.connectWithServer(1);
            if(!res)
            {
                this.setServerAdressCommunicat("some problem with connection");
                this.updateOnSize();
            }

        // this.guiController.ReturnScene();


    }

    /**
     * this method is called when move on login button
     */
    private void onMoveOnLoginButton()
    {
        //System.out.println("on button");
        this.loginButton.setFill(Color.WHITE);
        this.loginButtonText.setFill(Color.BLACK);
    }



    /**
     * this method is called when move outisede register button
     */
    private void onMoveOutsideRegisterButton()
    {
        this.registerButton.setFill(this.tranparentColor);
        this.registerButtonText.setFill(Color.WHITE);
        //System.out.println("outside Button");
    }
    /**
     * this method is called when pushed register button
     */
    private void onButtonRegisterPush()
    {
        this.guiController.soundPlayer.playOnButtonClick();
        this.registerButton.setFill(Color.LIGHTGRAY);
        //System.out.println("push");
    }

    /**
     * this method is called when released register button
     */
    private void onButtonRegisterRelease()
    {
        this.resetCommnicats();
        this.updateOnSize();
        //System.out.println("realse");
        this.registerButton.setFill(Color.WHITE);
        Boolean validation=this.validateRegistration();
        if(validation)
        {
            this.guiController.clientApp.setIp(this.serverAdressFields[0].getText());
            this.guiController.clientApp.setPort(Integer.parseInt(this.serverAdressFields[1].getText()));
            this.guiController.clientApp.setNick(this.registerFields[0].getText());
            this.guiController.clientApp.setPass(this.registerFields[1].getText());

         Boolean res =this.guiController.clientApp.connectWithServer(0);
         if(!res)
         {
             this.setServerAdressCommunicat("some problem with connection");
             this.updateOnSize();
         }
        }
        else
        {
            this.setRegisterCommunicat("Passwords dont match");
        }
        // this.guiController.ReturnScene();
    }

    /**
     * this mehtod checks if password in registrer field macth
     * @return
     */
    private Boolean validateRegistration() {
        return this.registerFields[1].getText().equals( this.registerFields[2].getText());

    }

    /**
     * this method is called then moved outisde of register button
     */
    private void onMoveOnRegisterButton()
    {
        //System.out.println("on button");
        this.registerButton.setFill(Color.WHITE);
        this.registerButtonText.setFill(Color.BLACK);
    }

    /**
     * this method set login communicat
     * @param s
     */
    public void setLoginCommuncat(String s)
    {
        loginCommunicat.setText(s);
        this.updateOnSize();
    }

    /**
     * this methos sets resigster communicat
     * @param s
     */
    public void setRegisterCommunicat(String s)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                registerCommunicat.setText(s);
                updateOnSize();
            }
        });

    }

    /**
     * this method sets sever adress comuniacat
     * @param s
     */
    private void setServerAdressCommunicat(String s)
    {
        if(!s.equals(""))
        this.guiController.soundPlayer.playFailed();
        this.serverAdressCommunicat.setText(s);
        this.updateOnSize();
    }

    /**
     * this method reset all comuniacats in login view
     */
    private  void resetCommnicats()
    {
        this.setServerAdressCommunicat("");
        this.setLoginCommuncat("");
        this.setRegisterCommunicat("");
    }

    /**
     * reset password fields when enetring again to login view
     */
    public void resetPassords() {
        this.registerFields[1].setText("");
        this.registerFields[2].setText("");
        this.loginFields[1].setText("");
    }
}
