package org.example;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;


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




    String[] textFieldsTexts={"Nick","Ip","Port"};
    Text textFieldsTitles[]= new Text[textFieldsTexts.length];
    TextField[] textFields = new TextField[textFieldsTexts.length];
    Line[] textFieldsLine=new Line[textFieldsTexts.length];
    private Text connectionStatusText;
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

    Group root;

    GuiController guiController;
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {

            this.iniit(primaryStage);
            primaryStage.setScene(this.mainScene);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.updateOnSize();
    }


    void iniit(Stage primaryStage) throws IOException {

        root = new Group();

        mainScene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight(),true, SceneAntialiasing.BALANCED);
        this.updateBackground();
        this.setupImages();
        this.setupButtons();
        this.setupTextFields();
        this.setupStatusText();
        this.addListiners(primaryStage);
        this.updateOnSize();
    }

    private void setupStatusText() {

        this.connectionStatusText= new Text();
        this.readyStatusText= new Text();

        this.connectionStatusText.setFill(Color.WHITE);
        this.readyStatusText.setFill(Color.WHITE);

        this.root.getChildren().addAll(this.connectionStatusText,this.readyStatusText);

        this.setPlayersReady(0,0);
        this.setStatusDiscconnted();

        
    }


    void setStatusConnected()
    {
        this.isConnected=true;
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
        this.connectionStatusText.setText("Disconnected");
        this.connectionStatusText.setFill(Color.RED);
        this.buttonTitles[0].setText("Connect");
        this.updateButtonsSize();
    }



    private void setupTextFields() {
    for(int i=0;i<this.textFieldsTexts.length;i++)
    {
        this.textFieldsTitles[i] = new Text(this.textFieldsTexts[i]);
        this.textFields[i] = new TextField();
        setupTextField(this.textFields[i]);

        this.textFieldsLine[i] = new Line();
        setupLine(this.textFieldsLine[i]);

        this.textFieldsTitles[i] = new Text(this.textFieldsTexts[i]);
        setupTextFiledTitle(this.textFieldsTitles[i]);

     this.root.getChildren().add( this.textFieldsTitles[i])  ;
        this.root.getChildren().add(this.textFields[i]);
        this.root.getChildren().add(this.textFieldsLine[i]);
    }
    this.updateTextFieldsSize();
    }

    void setupTextFiledTitle(Text text)
    {
    text.setFill(Color.WHITE);
    }

    
    void updateTextFieldsSize()
    {
        double width=this.buttons[0].getWidth()/1.8;
        double height=this.buttons[0].getHeight();
        double offesetGap=20;

        this.textFields[0].setLayoutX(this.buttonTitles[0].getX() + this.buttonTitles[0].getLayoutBounds().getWidth()/2 - width/2);
        this.textFields[0].setLayoutY(this.buttonTitles[0].getY() - this.buttonTitles[0].getLayoutBounds().getHeight() * 3*2.5);

        //this.textFields[1].setLayoutX(this.buttonTitles[0].getX() + this.buttonTitles[0].getLayoutBounds().getWidth()/2 - width/2);
        this.textFields[1].setLayoutX(this.textFields[0].getLayoutX() - width/2 -  offesetGap );
        this.textFields[1].setLayoutY(this.textFields[0].getLayoutY()+height*2.5);

        this.textFields[2].setLayoutX(this.textFields[0].getLayoutX() + width/2 + offesetGap);
        this.textFields[2].setLayoutY(this.textFields[0].getLayoutY()+height*2.5);

        double fontSize= this.textFields[0].getFont().getSize()/2;

        Font font=new Font("Arial",fontSize);
        for(int i=0;i<this.textFieldsTexts.length;i++)
        {
            this.textFields[i].setPrefSize(width,height);
            this.textFields[i].setFont(this.buttonTitles[0].getFont());


            this.textFieldsLine[i].setStartX(this.textFields[i].getLayoutBounds().getMinX()+this.textFields[i].getLayoutX());
            this.textFieldsLine[i].setStartY(this.textFields[i].getLayoutBounds().getMaxY()+this.textFields[i].getLayoutY());
            this.textFieldsLine[i].setEndX(this.textFields[i].getLayoutBounds().getMaxX()+this.textFields[i].getLayoutX());
            this.textFieldsLine[i].setEndY(this.textFields[i].getLayoutBounds().getMaxY()+this.textFields[i].getLayoutY());

            this.textFieldsTitles[i].setFont(font);
            this.textFieldsTitles[i].setX(this.textFieldsLine[i].getStartX());
            this.textFieldsTitles[i].setY(this.textFieldsLine[i].getEndY()+ this.textFieldsTitles[i].getLayoutBounds().getHeight()/1);

        }


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


    public MainVew(GuiController guiController) {
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




    }


    void updateOnSize()
    {

        this.updateImagesSize();
    this.updateBackground();
    this.updateButtonsSize();
    this.updateStatusText();
        this.updateTextFieldsSize();

    }

    private void updateStatusText() {
        double fontSize= this.textFields[0].getFont().getSize()/2;
        Font font=new Font("Arial",fontSize );
                this.connectionStatusText.setFont(font);
        this.readyStatusText.setFont(font);


        this.readyStatusText.setY(this.mainScene.getHeight() - this.readyStatusText.getBoundsInParent().getHeight()/2);
        this.readyStatusText.setX(this.mainScene.getWidth() -this.readyStatusText.getBoundsInParent().getWidth() );

        this.connectionStatusText.setX((this.buttons[0].getBoundsInParent().getMinX() +this.buttons[0].getBoundsInParent().getMaxX())/2 - this.connectionStatusText.getBoundsInParent().getWidth()/2  );
        this.connectionStatusText.setY(this.buttons[0].getBoundsInParent().getMaxY()+ this.connectionStatusText.getBoundsInParent().getHeight()*1.3 );
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

        RadialGradient gradient = new RadialGradient(0,0,mainScene.getWidth()/2,mainScene.getHeight()/2,mainScene.getHeight()>mainScene.getHeight()?mainScene.getHeight()*4:mainScene.getWidth()*2, false, CycleMethod.NO_CYCLE,this.blueStops);


        mainScene.setFill(gradient);
    }

    void onButtonMoved(Rectangle button)
    {
        button.setFill(Color.WHITE);
        int index= Arrays.asList(this.buttons).indexOf(button);
        this.buttonTitles[index].setFill(Color.BLACK);
    }

    void onButtonMovedOutside(Rectangle button)
    {
        button.setFill(this.tranparentColor);
        int index= Arrays.asList(this.buttons).indexOf(button);
        this.buttonTitles[index].setFill(Color.WHITE);
    }

    void onButtonBasicClick(Rectangle button)
    {
        button.setFill(Color.LIGHTGRAY);

    }

    void onButtonConnectClick()
    {
        if(!isConnected) {
            this.setStatusConnecting();
            System.out.println("CONNECT");
            if (this.guiController.connectTosServer())
                this.setStatusConnected();
            else
                this.setStatusDiscconnted();
        }
        else
        {
            this.setStatusDiscconnted();
            this.guiController.disconnectFromServer();
        }
    }

    void onButtonReadyClick()
    {
       // guiController.changeSceneToGame();
        if(isReady)
        {
            this.buttonTitles[1].setText("Ready");
            this.updateButtonsSize();
            this.guiController.sendNotReady();
            this.isReady=false;
        }
        else
        {
            this.buttonTitles[1].setText("Not Ready");
            this.updateButtonsSize();
            this.guiController.sendReady();
            this.isReady=true;
        }
        System.out.println("READY");
    }

    void onButtonRankingClick()
    {

        System.out.println("RANKING");
        this.setStatusConnected();
        this.setPlayersReady(4,8);
    }

    void onButtonExitClick()
    {
        //System.out.println("EXIT");
        System.exit(1);
    }



}
