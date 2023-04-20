package org.ClientPack;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.SharedPack.UnoCard;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
/**
 * a class that handles game view
 */
public class GameView extends Application {
    /**
     * a varaible taht store main scene referance
     */
    public Scene mainScene;
    /**
     * this boolean hold wheater or not assets are already loaded
     */
    public  Boolean isAssetLoaded=false;

    /**
     * this boolean hold wheater or not its this player turn
     */
    private   boolean isYourTurn=false;
    /**
     * a varaible that stores half transparent black
     */
    private final Color tranparentBlack = new Color(0, 0, 0, 0.5);
    /**
     * a varaible that stores transparent color for buttons
     */
    private final Color tranparentColor = new Color(1, 0, 0, 0.0);
    /**
     * a varaible that holds  green color for gradeint base
     */
    private final Color greenColor = new Color(0, 0.4, 0.1, 1);
    /**
     * a varaible that holds  dark green color for gradeint base
     */
    private final Color darkGreenColor = new Color(0.2, 0.28, 0.01, 1);
    /**
     * this varialbe holds  green gradient
     */
    private final Stop[] greenStops = new Stop[]{new Stop(0, this.greenColor), new Stop(1, Color.BLACK)};
    /**
     * this varialbe holds dark green gradient
     */
    private final Stop[] darkGreenStops = new Stop[]{new Stop(0, darkGreenColor), new Stop(1, Color.BLACK)};
    /**
     * this varaible holds active backgoudn gradeint stops
     */
    private   Stop[] backGroundStops = darkGreenStops;
    /**
     * a varaivle that stoes refence to gui contorller
     */
    private final GuiController guiController;

    /**
     * this array holds images of all uno cards types
     */
    private final Image[] cardImages = new Image[56];

    /**
     * this methos holds shape of help button
     */
    private   Rectangle helpButton;
    /**
     * this varaible hold text of help button
     */
    private    Text helpButtonText;

    /**
     * this array hild image view of all cards stacks in game
     */
    private  ImageView[] emptyCards ;

    /**
     * this array holds text elemnts to show nick of players in game
     */
    private   Text[] nickText;
    /**
     * this array holds rectangle elemnts that works as backgorund for texts
     */
    private  Rectangle[] textBox;
    /**
     * this array hold number of amount of cards held by each plaer
     */
    private  Integer[] amtOfOpponetsCards;

    /**
     * this variable holds array of text elemnts represeting amount of cards held by other players
     */
    private   Text[] amtOfCardsText;

    /**
     * this variable holds current card size ration
     */
    private   double cardWidth=100;
    /**
     * this variable holds current card postion offset from bottom
     */
    private  double cardHandPosY=0;

    /**
     * this list conatting cards hold by this client player
     */
    private final List<ImageView> cardsInHand = new ArrayList<ImageView>();

    /**
     * this varaible holds elemt of button shpae
     */
    private final Rectangle button = new Rectangle();
    /**
     * this variable holds text element of button text
     */
    private final Text buttonText= new Text("Surrender");

    /**
     * this boolean determins if player is now choosing color
     */
    private   Boolean isChoosingColor=false;

    /**
     * this varialbe holds amoutn of player at start of game
     */
    public  int playersAtStart=0;

    /**
     * this method setup neccesery object referances
     * @param guiController
     * @param mainScene
     */

    public GameView(GuiController guiController,Scene mainScene) {
        this.guiController=guiController;
        this.mainScene=mainScene;
    }


    public static void main(String[] args) {
        launch(args);
    }



    /**
     * a variable taht stores root group foe this view
     */
    public  Group root;

    /**
     * this method setup card piles in game
     */
    private  void setupEmptyCardsPostion()
    {
       for(int i=0;i<this.emptyCards.length;i++){
           try {
               this.emptyCards[i] = new ImageView(this.cardImages[55]);
               this.emptyCards[i].setPreserveRatio(true);
               this.root.getChildren().add(this.emptyCards[i]);
           }
           catch (Exception e)
           {
               e.printStackTrace();
           }
        }

    }

    /**
     * a mthod that setuups alle elemnts of this view
     * @param primaryStage
     */
    public  void iniit(Stage primaryStage) throws FileNotFoundException {

        root = new Group();
        //mainScene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight(), true, SceneAntialiasing.BALANCED);
        this.loadImages();

        this.emptyCards=  new ImageView[this.getAmtOfOpponets()+2];
        this.setupEmptyCardsPostion();
        this.setupButtonShape();
        this.setupNicks();
        this.setupHelpButton();
        this.setupGuides();
        this.setupChat();
        this.setTopGlow(new UnoCard(UnoCard.UNO_TYPE.REGULAR, UnoCard.UNO_COLOR.BLACK, 1));


        //this.looadCardsInHand();
        this.updateBackground();
        this.addListiners(primaryStage);
        this.setStackPile(true);
        this.isAssetLoaded=true;
        this.updateOnSize();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            this.iniit(primaryStage);

           primaryStage.setScene(mainScene);
               //primaryStage.setFullScreen(true);






            primaryStage.show();
            this.updateOnSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * this methos sets image of pile to be empty
     * @param imageView
     */
    private  void setEmpty(ImageView imageView)
    {
        imageView.setImage(this.cardImages[this.cardImages.length-1]);
    }

    /**
     * this methos sets image of pile to be basic uno card
     * @param imageView
     */
    private   void setNotEmpty(ImageView imageView)
    {
        imageView.setImage(this.cardImages[this.cardImages.length-2]);
    }

    /**
     * thi method updates amount of cards text elments of game view
     */
    private   void updateAmtOfCards()
    {
        Integer[] amtOfCards = this.getAmtOfCards();
        for (int i = 0; i < this.amtOfCardsText.length; i++) {
            String txt = "x";
            txt += amtOfCards[i];
            System.out.println("\n @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@card: " + txt);
            this.amtOfCardsText[i].setText(txt);


        }

        this.updateOnSize();
    }

    /**
     * this method setups nick Text elements for game view
     */
    private void setupNicks() {
        try {
            this.textBox=new Rectangle[this.getAmtOfOpponets()+1];
            this.nickText = new Text[this.getAmtOfOpponets() + 1];
            this.amtOfCardsText = new Text[this.getAmtOfOpponets()];
            this.amtOfOpponetsCards = new Integer[this.getAmtOfOpponets()];
            this.nickText[0] = new Text(this.getPlayerNick());
            this.nickText[0].setX(0);
            this.nickText[0].setFill(Color.WHITE);
            if (this.nickText.length <= 1) {
                System.out.println("this.nickText.length): "+this.nickText.length);
                System.out.println("this.getAmtOfOpponets(): " + this.getAmtOfOpponets() );
                System.out.println("this.nickText[0]: " + this.nickText[0]);
                System.out.println("-------");

                System.exit(-1);
            }

            this.textBox[0] = new Rectangle();
            this.textBox[0].setFill(this.tranparentBlack);
            this.textBox[0].setX(0);
            this.root.getChildren().add(this.textBox[0]);
            this.root.getChildren().add(this.nickText[0]);
            while(getNick().size()<1)
            {
                System.out.println("waiting for nick");
                try {
                    TimeUnit.MILLISECONDS.sleep(11);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            List<String> nicks = getNick();

            for (int i = 1; i < this.nickText.length; i++) {

                try {
                    this.setNotEmpty(this.emptyCards[i+1]);
                    this.nickText[i] = new Text(nicks.get(i - 1));
                    this.nickText[i].setFill(Color.WHITE);

                    this.textBox[i] = new Rectangle();
                    this.textBox[i].setFill(this.tranparentBlack);

                    this.root.getChildren().add(this.textBox[i]);
                    this.root.getChildren().add(this.nickText[i]);
                } catch (IndexOutOfBoundsException e )
                {
                    e.printStackTrace();
                    System.out.println("this.nickText: "+ this.nickText.length);
                    System.out.println("nicks size: " + nicks.size());
                    System.out.println("nicks: "  + nicks);
                    System.out.println("this.textBox: " + this.textBox.length);
                    System.out.println("i: " + i);
                    System.exit(-1);
                }

            }


            // amount of cards number
            Integer[] amtOfCards = this.getAmtOfCards();
            for (int i = 0; i < this.amtOfCardsText.length; i++) {
                String txt = "x";
                txt += amtOfCards[i];
                this.amtOfCardsText[i] = new Text(txt);
                this.amtOfCardsText[i].setFill(Color.WHITE);

                this.root.getChildren().add(this.amtOfCardsText[i]);


                this.amtOfOpponetsCards[i]=0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            System.out.println("this.nickText: "+ this.nickText.length);
            System.out.println();
            System.exit(-1);
        }

    }

    /**
     * this method load all images to from reserauces to game view
     * @throws FileNotFoundException
     */
    private void loadImages() throws FileNotFoundException {
        int iterator=0;

        String cardPrefix = "Cards/unoCards_";
        for(int i = 0; i<4; i++)
        {
            String color="";
            switch (i) {
                case 0:
                    color += "Blue";
                    break;
                case 1:
                    color += "Green";
                    break;
                case 2:
                    color += "Red";
                    break;
                case 3:
                    color += "Yellow";
                    break;
            }

            for(int j=0;j<13;j++)
            {
                String type="";
                type+=j;
                switch (j)
                {
                    case 10:
                        type = "block";
                        break;
                    case 11:
                        type = "plus2";
                        break;
                    case 12:
                        type = "swap";
                        break;
                }
                String url= cardPrefix + color + "_" + type + ".png";
                loadCard(url,iterator);

                iterator++;

            }



        }
        loadCard(cardPrefix +"Black_choice.png", iterator++);
        loadCard(cardPrefix +"Black_plus4.png", iterator++);
        loadCard(cardPrefix +"CardBack.png", iterator++);
        loadCard(cardPrefix +"Empty.png", iterator++);
      //  System.out.println("Size: " + iterator);

        String colorPrefix = "ColorChoicePanel/Circle_";
        String url= colorPrefix + "Blue.png";
        try {
            InputStream inputStream=getClass().getClassLoader().getResourceAsStream(url);
            this.colorChoicePanel = new Image[4];
            this.colorChoicePanel[0] = new Image(inputStream);
            url= colorPrefix + "Green.png";
            inputStream=getClass().getClassLoader().getResourceAsStream(url);
            this.colorChoicePanel[1] = new Image(inputStream);
            url= colorPrefix + "Red.png";
            inputStream=getClass().getClassLoader().getResourceAsStream(url);
            this.colorChoicePanel[2] = new Image(inputStream);
            url= colorPrefix + "Yellow.png";
            inputStream=getClass().getClassLoader().getResourceAsStream(url);
            this.colorChoicePanel[3] = new Image(inputStream);
        }
        catch (Exception e)
        {
            System.out.println("problem laoding: " + url);
            System.exit(-1);
            e.printStackTrace();
        }



    }

    /**
     * this method load image of card provide in url to a specific postion in iimage table
     * @param url
     * @param positonInTable
     */
    private   void loadCard(String url, int positonInTable)
    {
        try{
            InputStream inputStream=getClass().getClassLoader().getResourceAsStream(url);
           // FileInputStream fileInputStream = new FileInputStream(url);
            this.cardImages[positonInTable] = new Image(inputStream);
         //   System.out.println(url);
        }
        catch (Exception e)
        {

            e.printStackTrace();
            System.out.println("ERROO LODIAN CARD: ");
            System.out.println(url);
           // System.exit(-1);
        }

    }

    /**
     * this methos updates card global scale based on window size
     */
    private   void updateCardScale()
{
    double scaleFactor=7;
    this.cardWidth=this.mainScene.getHeight()*1.5>this.mainScene.getWidth()?this.mainScene.getWidth()/scaleFactor/2:this.mainScene.getHeight()/scaleFactor;





}


    /**
     * this method returns amount of opponetsn for game view setup
     * @return
     */
    private  int getAmtOfOpponets()
    {
        int amt=0;
        if(this.emptyCards!=null)
        amt=this.emptyCards.length-2;
        if(amt==0) {
            try {
                amt = this.guiController.clientApp.getConnectedPlayers() - 1;
                if (amt == 0) {
                    System.out.println("Amount of playeers: " + amt);
                    System.exit(-1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return amt;
    }


    /**
     * this method set ups listiners for game view
     * @param primaryStage
     */
    private void addListiners(Stage primaryStage) {

        this.mainScene.widthProperty().addListener(
                (observable, oldValue, newValue) -> updateOnSize()
        );


        this.mainScene.heightProperty().addListener(
                (observable, oldValue, newValue) -> updateOnSize()

        );

        primaryStage.fullScreenProperty().addListener(
                (observable, oldValue, newValue) -> updateOnSize()

        );

        primaryStage.iconifiedProperty().addListener(
                (observable, oldValue, newValue) -> updateOnSize()
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


        this.button.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMouseOnButton();
                    }
                }
        );


        this.button.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMouseOutsideButton();
                    }
                }
        );

        this.button.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonPresed();
                    }
                }
        );
        this.button.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRealsed();
                    }
                }
        );





        this.buttonText.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMouseOnButton();
                    }
                }
        );


        this.buttonText.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMouseOutsideButton();
                    }
                }
        );

        this.buttonText.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        guiController.soundPlayer.playOnButtonClick();
                        onButtonPresed();
                    }
                }
        );
        this.buttonText.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onButtonRealsed();
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
                        guiController.soundPlayer.playOnButtonClick();
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


        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()== KeyCode.BACK_QUOTE)
                {
                    onTyldaButton();
                }
                if(event.getCode()== KeyCode.ENTER)
                {
                    sendMessage();
                }
            }
        });


    }

    /**
     * this method calls all update size method when channging scale of window
     */
    public  void updateOnSize() {
        this.updateBackground();
        this.updateCardScale();
        this.updateEmptyCard();
        this.updateCardsInHandScale();
        this.upadateButtonShape();
        this.updateHelpButtonSize();
        this.upadateText();
        this.updateColorPanelScale();
        this.updateGuidesSize();
        this.updateChatSize();
        this.updateWaitSize();
    }

    /**
     * this method setups button elemnts in game view
     */
    private void setupButtonShape()
    {
        this.buttonText.setFill(Color.WHITE);
        this.button.setStrokeWidth(1.5);
        this.button.setStroke(Color.WHITE);
        this.button.setFill(tranparentColor);
        this.root.getChildren().add(this.button);
        this.root.getChildren().add(this.buttonText);

    }

    /**
     * this method updates button shape and postion based on window size
     */
    private void upadateButtonShape() {
        double buttonWidth=this.mainScene.getWidth()/8;

        this.button.setWidth(buttonWidth);
        this.button.setHeight(buttonWidth/7);

        this.button.setX(this.mainScene.getWidth()-this.button.getWidth());

        this.button.setArcWidth(buttonWidth/15);
        this.button.setArcHeight(buttonWidth/15);
        Font font= new Font("Arial", buttonWidth/15);


        this.buttonText.setFont(font);
        this.buttonText.setX(this.button.getLayoutBounds().getMinX() + this.button.getLayoutBounds().getWidth()/2 - this.buttonText.getLayoutBounds().getWidth()/2);
        this.buttonText.setY(this.button.getLayoutBounds().getHeight()/2 + this.buttonText.getLayoutBounds().getHeight()/3);

    }

    /**
     * this method updates scale and position of cards in hand based on window size
     */
    private void updateCardsInHandScale() {
        int i=0;
        double width=this.mainScene.getWidth()/3;
        double startPostion=this.mainScene.getWidth()/2-width/2;
        double stepMove=width/this.cardsInHand.size();
        this.cardHandPosY=this.mainScene.getHeight()-this.cardWidth*0.7;
        for (ImageView card: cardsInHand
             ) {
            card.setFitWidth(this.cardWidth);

            TranslateTransition translateTransition= new TranslateTransition();
            translateTransition.setNode(card);
            translateTransition.setDuration(Duration.millis(20));
            translateTransition.setToX(startPostion+stepMove*i);
            translateTransition.setToY(cardHandPosY);
            translateTransition.play();
            i++;
        }

    }

    /**
     * this emthod update postion and size of text elemts based on window size
     */
    private void upadateText() {
        double fontSizeparm=5;
        Font font= new Font("Arial",cardWidth/fontSizeparm);
        this.nickText[0].setFont(font);
        this.nickText[0].setX(this.mainScene.getWidth()- this.nickText[0].getLayoutBounds().getWidth()*1.5);
        this.nickText[0].setY(this.mainScene.getHeight()- this.nickText[0].getLayoutBounds().getHeight()*0.2);
        this.textBox[0].setX(this.nickText[0].getLayoutBounds().getMinX());
        this.textBox[0].setY(this.nickText[0].getLayoutBounds().getMinY());
        this.textBox[0].setHeight(this.nickText[0].getLayoutBounds().getHeight());
        this.textBox[0].setWidth(this.nickText[0].getLayoutBounds().getWidth()*1.1);

        for(int i=1;i<this.nickText.length;i++)
        {
            try {
                this.nickText[i].setFont(font);
                this.nickText[i].setX((this.emptyCards[i + 1].getBoundsInParent().getMinX() + this.emptyCards[i + 1].getBoundsInParent().getMaxX()) / 2 - this.mainScene.getWidth() / 200);
                this.nickText[i].setY(this.emptyCards[i + 1].getBoundsInParent().getMinY());

                this.textBox[i].setY(this.nickText[i].getLayoutBounds().getMinY());
                this.textBox[i].setX(this.nickText[i].getLayoutBounds().getMinX());
                this.textBox[i].setHeight(this.nickText[i].getLayoutBounds().getHeight());
                this.textBox[i].setWidth(this.nickText[i].getLayoutBounds().getWidth() * 1.1);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                e.printStackTrace();
                System.out.println("this.nickText.length: " + this.nickText.length);
                System.out.println("this.emptyCards: " + this.emptyCards.length);
                System.out.println("i: " + i);
                System.exit(-1);
            }

        }


        for (int i=0;i<this.amtOfCardsText.length;i++)
        {
            //System.out.println(i);
            this.amtOfCardsText[i].setFont(font);
            this.amtOfCardsText[i].setX(this.nickText[i+1].getLayoutBounds().getMinX()+this.nickText[i+1].getLayoutBounds().getWidth()/2);
            this.amtOfCardsText[i].setY(this.nickText[i+1].getLayoutBounds().getMaxY()+cardWidth*2);


        }



    }

    /**
     * this method updates card places based on winddow size
     */
    private void updateEmptyCard() {
        if(this.emptyCards.length<=2)
        {
            System.out.println("INALVID ARRAY SIZE\n");
            System.out.println("getAmtOfOpponets(): "+ getAmtOfOpponets());
            System.exit(-1);
        }
        this.emptyCards[0].setFitWidth(cardWidth);
        this.emptyCards[0].setX(this.mainScene.getWidth()/2-cardWidth/2);
        this.emptyCards[0].setY(this.mainScene.getHeight()/2- this.emptyCards[0].getLayoutBounds().getHeight()/2);


        this.emptyCards[1].setFitWidth(cardWidth);
        this.emptyCards[1].setX(this.emptyCards[0].getX() + cardWidth+cardWidth/8);
        this.emptyCards[1].setY(this.emptyCards[0].getY()+this.emptyCards[0].getLayoutBounds().getHeight()/5);


        double circleX= this.mainScene.getWidth()/2;
        double circleY= this.mainScene.getHeight()/2+cardWidth*7;
        double deegreRange=78.75;
        double deggreStep=deegreRange/this.getAmtOfOpponets();
        double deg=0-deegreRange/2+deggreStep/2;
        for(int i=2;i<this.emptyCards.length;i++)
        {
            //System.out.println(deg);
            this.emptyCards[i].setFitWidth(cardWidth);
            this.emptyCards[i].setX(this.emptyCards[0].getX());
            this.emptyCards[i].setY(this.emptyCards[0].getY() - this.emptyCards[0].getLayoutBounds().getHeight()*1.4);

            this.emptyCards[i].getTransforms().clear();
            this.emptyCards[i].getTransforms().add(new Rotate(deg,circleX,circleY ));

            deg+=deggreStep;

        }
    }

    /**
     * this method updates background sze based on window size
     */
    private  void updateBackground() {
        if(this.guiController.activeScenes== GuiController.SCENES.GAME) {

            RadialGradient gradient = new RadialGradient(0, 0, mainScene.getWidth() / 2, mainScene.getHeight() / 2, mainScene.getHeight() > mainScene.getWidth() ? mainScene.getHeight() * 2 : mainScene.getWidth() * 2, false, CycleMethod.NO_CYCLE, this.backGroundStops);
            mainScene.setFill(gradient);
        }
    }

    /**
     * this emthod updates guide color  to show color of card on table stack
     * @param card
     */
    private  void setTopGlow(UnoCard card)
    {

        Color color = Color.WHITE;
        switch (card.getColor())
        {
            case GREEN: color= Color.DARKGREEN;
            break;
            case RED: color= Color.DARKRED;
                break;
            case YELLOW: color= Color.GOLD;
                break;
            case BLUE: color= Color.DARKBLUE;
                break;
            case BLACK: color= Color.BLACK;
                break;
        }
        //DropShadow shadow=new DropShadow(10,color);
        this.setGuideColor(color);


    }


    /**
     * this method gets nick of all players in game
     * @return
     */
    private  List<String> getNick()
    {
        List<String> nicks= new ArrayList<String>();
        for (PlayerData player: this.guiController.clientApp.playersInORder
             ) {
            nicks.add(player.getNick());
        }

        return nicks;
    }

    /**
     * this method get nick of this client player
     * @return
     */
    private  String getPlayerNick()
    {
        return this.guiController.clientApp.getNick();
    }

    /**
     * this mehhod returns how many cards each player has
     * @return
     */
    private  Integer[] getAmtOfCards()
    {
        return this.amtOfOpponetsCards;
    }

    /**
     * this method plays animation to recive new card from pile
     * @param card
     */
    public  void addCard(UnoCard card)
    {
        this.guiController.soundPlayer.playDrawCard();
        ImageView cardView = new ImageView(cardImages[getIndexOfmage(card)]);
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {

                        cardView.setPreserveRatio(true);
                        cardView.setFitWidth(cardWidth);
                        cardView.setTranslateX(emptyCards[1].getX());
                        cardView.setTranslateY(emptyCards[1].getY());
                        // cardView.setY(this.emptyCards[1].getY());
                        cardsInHand.add(cardView);
                        root.getChildren().add(cardView);
                        updateCardsInHandScale();

                    }
                }
        );



        cardView.setOnMouseMoved(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMouseOnCard(cardView);
                    }
                }
        );

        cardView.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onMouseOutsideCard(cardView);
                    }
                }
        );





        cardView.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onCardClick(cardView);
                    }
                }
        );

    }

    /**
     * this method return indesx of card in image list
     * @param card
     * @return
     */
    private int getIndexOfmage(UnoCard card)
    {
        switch (card.getType())
        {
            case COLOR: return 13 * 4;
            case PLUS4: return 13*4+1;
        }
        int col=0;
        switch (card.getColor())
        {
            case BLUE: col=0; break;
            case GREEN: col=1; break;
            case RED: col=2; break;
            case YELLOW: col=3; break;
        }
        switch (card.getType())
        {
            case BLOCK: return col*13+10;
            case PLUS2: return col*13+11;
            case REVERSE: return col*13+12;
        }
        return col*13+card.getNumb();

    }

    /**
     * this methos updates card postioin after entering card area
     * @param card
     */
    private void onMouseOnCard(ImageView card)
    {
        TranslateTransition translateTransition= new TranslateTransition();
        translateTransition.setNode(card);
        translateTransition.setDuration(Duration.millis(5));
        translateTransition.setToY((this.cardHandPosY-this.cardWidth/1.5));
       translateTransition.play();
      //  System.out.println("OnCard");
    }

    /**
     * this mehod updates card position after leaving card area
     * @param card
     */
    private  void onMouseOutsideCard(ImageView card)
    {
        TranslateTransition translateTransition= new TranslateTransition();
        translateTransition.setNode(card);
        translateTransition.setDuration(Duration.millis(50));
        translateTransition.setToY(this.cardHandPosY);
        translateTransition.play();
        //System.out.println("OutisideCard");
    }

    /**
     * this method is called when clicked card. It gets card from hand and calls play card method
     * @param card
     */
    private  void onCardClick(ImageView card)
    {
        Image playedImage=card.getImage();
        int index=0;
        for (Image image: this.cardImages
             ) {
            if(image==playedImage)
                break;
            index++;
        }

        if(index>13*4-1)
            this.playBlackCard(card);
        else
            this.playCard(card);

       // System.out.println("Click");
    }

    /**
     * this  method is called also when playing card but its black so also shows color choice panel
     * @param card
     */
    private void playBlackCard(ImageView card) {
        if(!isYourTurn)
            this.playCard(card);
        else
            if(!isChoosingColor)
          this.showChoiceColor(card);
    }

    /**
     * this method is called when card is clicked and attepts too play on table
     * @param card
     */
    private void playCard(ImageView card) {

        if(!isChoosingColor && !this.isWaitingForPlayer) {
            int index = this.cardsInHand.indexOf(card);

            UnoCard unoCard = this.guiController.clientApp.cardsInHand.get(index);
            if (this.CanBePlayed(unoCard) && this.isYourTurn) {

                this.guiController.soundPlayer.playDrawCard();
                this.guiController.clientApp.playCard(index + 1, unoCard);
                double duration = 250;
                ImageView tmpCard = new ImageView(card.getImage());
                tmpCard.setPreserveRatio(true);
                tmpCard.setFitWidth(this.cardWidth);
                tmpCard.setTranslateX(card.getTranslateX());
                tmpCard.setTranslateY(card.getTranslateY());

                TranslateTransition transition = new TranslateTransition();
                transition.setDuration(Duration.millis(duration));
                transition.setNode(tmpCard);
                transition.setToX(this.emptyCards[0].getX());
                transition.setToY(this.emptyCards[0].getY());
                transition.play();

                this.root.getChildren().add(tmpCard);


                transition.statusProperty().addListener(
                        new ChangeListener<Animation.Status>() {
                            @Override
                            public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                                if (newValue == Animation.Status.STOPPED)
                                    root.getChildren().remove(tmpCard);
                                setCardOnTable(guiController.clientApp.getCardOntop());
                            }
                        }
                );


                this.cardsInHand.remove(card);


                this.root.getChildren().remove(card);
                this.updateCardsInHandScale();


            } else {
                double duration = 50;
                TranslateTransition transition = new TranslateTransition();
                transition.setDuration(Duration.millis(duration));
                transition.setNode(this.cardsInHand.get(index));
                transition.setByY(-cardWidth / 3);
                transition.setAutoReverse(true);
                transition.setCycleCount(2);
                transition.play();
/*
                transition.statusProperty().addListener(
                        new ChangeListener<Animation.Status>() {
                            @Override
                            public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                                if (newValue == Animation.Status.STOPPED)

                            }
                        }
                );

 */


            }


        }
    }

    /**
     * this method check if this uno card can be played on table
     * @param card
     * @return
     */
    private boolean CanBePlayed(UnoCard card) {
        return this.guiController.clientApp.vaidateCard(card);
    }

    /**
     * this method udpates button look when mouse enters area of button
     */
    private  void onMouseOnButton()
    {

    this.button.setFill(Color.WHITE);
    this.buttonText.setFill(Color.BLACK);
  //  System.out.println("On btton");

}

    /**
     * this method updates button look when mouse moved outised area of button
     */
    private  void onMouseOutsideButton()
    {
        this.button.setFill(this.tranparentColor);
        this.buttonText.setFill(Color.WHITE);
      //  System.out.println("outiseed buttp");

    }

    /**
     * this method updates button look when pressed
     */
    private  void onButtonPresed()
    {
        this.guiController.soundPlayer.playOnButtonClick();
        this.button.setFill(Color.LIGHTGRAY);

    }

    /**
     * this method updates button look when released
     */
    private  void onButtonRealsed()
    {
        onMouseOnButton();
        this.surrenderButton();
    }


    /**
     * thi smethod is called when hiting surrender button.
     * it sends al cards into stack and informs server about it
     */
    private void surrenderButton() {
        if(!this.isWaitingForPlayer) {
            this.guiController.clientApp.surrender();

            for (ImageView card : this.cardsInHand
            ) {
                TranslateTransition transition = new TranslateTransition();
                transition.setToY(this.emptyCards[1].getY());
                transition.setToX(this.emptyCards[1].getX());
                transition.setNode(card);
                transition.setDuration(Duration.millis(200));
                transition.play();

                transition.statusProperty().addListener(
                        new ChangeListener<Animation.Status>() {
                            @Override
                            public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                                if (newValue == Animation.Status.STOPPED) {
                                    root.getChildren().remove(card);
                                    cardsInHand.remove(card);
                                    updateCardsInHandScale();
                                }
                            }
                        }
                );


            }

        }
    }


    /**
     * this method sets stack pile to be emtoy or filled
     * @param isFilled
     */
    private  void setStackPile(boolean isFilled)
    {
        try {
            if (isFilled) {
                this.emptyCards[1].setImage(this.cardImages[this.cardImages.length - 2]);
            } else {
                this.emptyCards[1].setImage(this.cardImages[this.cardImages.length - 1]);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * this method sets image of card on top of table stack
     * @param card
     */
    public void setCardOnTable(UnoCard card)
    {
        this.emptyCards[0].setImage(this.cardImages[this.getIndexOfmage(card)]);
        this.setTopGlow(card);
    }


    /**
     * this method play animation to add card to opponent in specific postioin an updates number of cards
     * @param nbOfOpponent
     */
    public void giveCardToOpponent(int nbOfOpponent)
    {
        double duration=250;

        ImageView emptyCard= new ImageView(this.cardImages[(this.cardImages.length-2)]);
        emptyCard.setPreserveRatio(true);
        emptyCard.setFitWidth(this.cardWidth);
        emptyCard.setTranslateX(this.emptyCards[1].getX());
        emptyCard.setTranslateY(this.emptyCards[1].getY());

        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            root.getChildren().add(emptyCard);

                            TranslateTransition translateTransition = new TranslateTransition();
                            translateTransition.setNode(emptyCard);
                            translateTransition.setToY(emptyCards[nbOfOpponent +2].getBoundsInParent().getMinY());
                            translateTransition.setToX(emptyCards[nbOfOpponent +2].getBoundsInParent().getMinX());
                            translateTransition.setDuration(Duration.millis(duration));

                            translateTransition.statusProperty().addListener(
                                    new ChangeListener<Animation.Status>() {
                                        @Override
                                        public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                                            if (newValue == Animation.Status.STOPPED)
                                                root.getChildren().remove(emptyCard);
                                        }
                                    }
                            );

                            translateTransition.play();
                            amtOfOpponetsCards[nbOfOpponent]=amtOfOpponetsCards[nbOfOpponent]+1;

                            System.out.println("Amount of cards " + nbOfOpponent + " : :\n"+amtOfOpponetsCards[nbOfOpponent]);
                            updateAmtOfCards();

                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            System.out.println("nbOfOpponent: " + nbOfOpponent);
                            System.out.println("amtOfOpponetsCards.: " + amtOfOpponetsCards.length);
                            System.out.println("emptyCards: " + emptyCards.length);
                            System.out.println("getAmtOfOpponets: " + getAmtOfOpponets());
                            System.out.println("Players in order: " +   guiController.clientApp.playersInORder);
                            System.exit(-1);

                        }
                    }

                }
        );







      // rotateTransition.play();

    }






    //////////////////////// Color Choice panel
    /**
     * this variable contains loades color panel images
     */
    private  Image[] colorChoicePanel;
    /**
     * this varaible contains images to view color panel
     */
    private final List<ImageView> colorPanel= new ArrayList<ImageView>();

    /**
     * this method shows color choice panel and locks other elements of gui
     * @param card
     * @return
     */
    private  int showChoiceColor(ImageView card)
    {
if(!this.isWaitingForPlayer) {
    this.isChoosingColor = true;
    ImageView blue = new ImageView(this.colorChoicePanel[0]);
    ImageView green = new ImageView(this.colorChoicePanel[1]);
    ImageView red = new ImageView(this.colorChoicePanel[2]);
    ImageView yellow = new ImageView(this.colorChoicePanel[3]);


    blue.setPreserveRatio(true);
    green.setPreserveRatio(true);
    red.setPreserveRatio(true);
    yellow.setPreserveRatio(true);

    this.colorPanel.add(blue);
    this.colorPanel.add(green);
    this.colorPanel.add(red);
    this.colorPanel.add(yellow);

    this.updateColorPanelScale();

    int i = 0;
    for (ImageView panel : this.colorPanel
    ) {
        panel.setOnMouseEntered(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onPanelEntterd(panel);
                    }
                }

        );
        panel.setOnMouseExited(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onPanelExited(panel);
                    }
                }

        );

        panel.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        onPanelClicked(panel, card);
                    }
                }

        );


    }


    this.root.getChildren().addAll(blue, green, red, yellow);


}


        return 0;

    }


    /**
     * this method updates scale of color panel based on window size
     */
    private void updateColorPanelScale() {
        if(this.isChoosingColor) {
            double centerX = mainScene.getWidth() / 2;
            double centerY = mainScene.getHeight() / 2;
            double size=cardWidth*2;


            this.colorPanel.get(0).setX(centerX-size);
            this.colorPanel.get(0).setY(centerY-size);
            this.colorPanel.get(0).setFitWidth(size);

            this.colorPanel.get(1).setFitWidth(size);
            this.colorPanel.get(1).setX(centerX);
            this.colorPanel.get(1).setY(centerY-size);

            this.colorPanel.get(2).setFitWidth(size);
            this.colorPanel.get(2).setX(centerX-size);
            this.colorPanel.get(2).setY(centerY);

            this.colorPanel.get(3).setFitWidth(size);
            this.colorPanel.get(3).setX(centerX);
            this.colorPanel.get(3).setY(centerY);

        }



    }


    /**
     * this method add slecting effects when mouse entred color panel
     * @param panel
     */
    private   void onPanelEntterd(ImageView panel)
    {
       panel.setEffect(new Bloom());
    }

    /**
     * this method removes selcting effect when exited color panel
     * @param panel
     */
    private  void onPanelExited(ImageView panel)
    {
        panel.setEffect(null);
    }

    /**
     * this method id called when person clicked on a color chocie panel and set chosen color
     * @param panel
     * @param card
     */
    private  void onPanelClicked(ImageView panel, ImageView card)
    {
        int clikcedPanel = this.colorPanel.indexOf(panel);

        for(int i=0;i<this.colorPanel.size();i++)
        {
            this.root.getChildren().remove(this.colorPanel.get(i));
        }
        this.colorPanel.clear();


        int index=this.cardsInHand.indexOf(card);

        UnoCard playedCard=this.guiController.clientApp.cardsInHand.get(index);
        switch (clikcedPanel)
        {
            case 0: playedCard.setColor(UnoCard.UNO_COLOR.BLUE);
                break;
            case 1: playedCard.setColor(UnoCard.UNO_COLOR.GREEN);
                break;
            case 2: playedCard.setColor(UnoCard.UNO_COLOR.RED);
                break;
            case 3: playedCard.setColor(UnoCard.UNO_COLOR.YELLOW);
                break;
        }
        this.isChoosingColor=false;
        this.playCard(card);


    }

    /**
     * this method plays animation nad updates cards when card played from provided player
     * @param nbOfOppoonent
     * @param card
     */
    public  void playCardFromOppoent(int nbOfOppoonent, UnoCard card)
    {
        try {
            double duration = 200;
            ImageView cardTmp = new ImageView(this.cardImages[this.cardImages.length - 2]);
            cardTmp.setPreserveRatio(true);
            cardTmp.setFitWidth(this.cardWidth);
            cardTmp.setTranslateX(this.emptyCards[nbOfOppoonent+2].getBoundsInParent().getMinX());
            cardTmp.setTranslateY(this.emptyCards[nbOfOppoonent +2].getBoundsInParent().getMinY());

            Platform.runLater(
                    new Runnable() {
                        @Override
                        public void run() {
                            root.getChildren().add(cardTmp);

                            TranslateTransition translate = new TranslateTransition();
                            translate.setNode(cardTmp);
                            translate.setToX(emptyCards[0].getX());
                            translate.setToY(emptyCards[0].getY());
                            translate.setDuration(Duration.millis(duration));
                            translate.play();

                            translate.statusProperty().addListener(
                                    new ChangeListener<Animation.Status>() {
                                        @Override
                                        public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                                            if (newValue == Animation.Status.STOPPED) {
                                                root.getChildren().remove(cardTmp);
                                                setCardOnTable(card);
                                            }
                                        }
                                    }
                            );
try {

    amtOfOpponetsCards[nbOfOppoonent]=amtOfOpponetsCards[nbOfOppoonent]-1;
    updateAmtOfCards();
}
catch (ArrayIndexOutOfBoundsException e)
{
    e.printStackTrace();
    System.out.println("amtOfOpponetsCards: " + amtOfOpponetsCards.length);
    System.out.println("nbOfOppoonent: " + nbOfOppoonent);
    System.exit(-1);
}


                        }
                    }
            );
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            System.out.println("nbOfOppoonent: " + nbOfOppoonent);
            System.out.println("emptyCards.length: " + emptyCards.length);
            System.exit(-1);
        }





    }

    /**
     * this method set glow to text of specific index
     * @param index
     */
    private  void setTurnGlow(int index)
    {
        int i;
        for(i=0;i<this.textBox.length;i++)
        {
            this.textBox[i].setEffect(null);
        }
        DropShadow dropShadow = new DropShadow(cardWidth/3,Color.WHITE);
        this.textBox[index].setEffect(dropShadow);

    }

    /**
     * this method set turn to player provide in arugment including seting guide turn and glow
     * @param nick
     */
    public  void setTurn(String nick)
    {
        int index=0;

        for(index=0;index<this.nickText.length;index++)
        {
            if(this.nickText[index].getText().equals(nick)) {
                this.setGuideNickText(this.nickText[index].getText());
                break;
            }
            }

        if(index==0) {
            this.isYourTurn = true;
            this.backGroundStops=greenStops;

        }
        else {
            this.isYourTurn = false;
            this.backGroundStops=darkGreenStops;
        }
        this.updateBackground();
        this.setTurnGlow(index);

    }

    /**
     * this method sets guide turn text to be provided text
     * @param txt
     */
    private  void setGuideNickText(String txt)
    {
        this.turnText.setText(txt);
        updateGuidesSize();
    }

    /**
     * this method set image of player pile to be empty
     * @param indexPLayer
     */
    public void setPlayerEmptyPile(int indexPLayer) {
    this.setEmpty(this.emptyCards[2+indexPLayer]);
    }

    /**
     * this method handles operation of player surrending inculidng chagnig is amount of cards and playing animation
     * @param index
     */
    public void handleSurrender(int index) {
        this.amtOfOpponetsCards[index]=0;
        this.updateAmtOfCards();
        ImageView tmpCard= new ImageView(this.cardImages[this.cardImages.length-2]);
        tmpCard.setX(emptyCards[index+2].getX());
        tmpCard.setY(emptyCards[index+2].getY());
        root.getChildren().remove(tmpCard);

        TranslateTransition transition = new TranslateTransition();
        transition.setToX(emptyCards[1].getX());
        transition.setToY(emptyCards[1].getY());
        transition.setNode(tmpCard);
        transition.setDuration(Duration.millis(200));
        transition.statusProperty().addListener(
                new ChangeListener<Animation.Status>() {
                    @Override
                    public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                        if(newValue == Animation.Status.PAUSED)
                        {
                            root.getChildren().remove(tmpCard);
                        }
                    }
                }
        );

        this.setEmpty(emptyCards[index+2]);


    }


///////////////////////////////////// help button
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


        helpButton.setFill(Color.LIGHTGRAY);


    }
    /**
     * a method that runs when mouse released from help button
     */
    private   void onHelpReelased()
    {
        this.guiController.switchSceneToInstruction();
        onHelpMovedOutside();


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


//////////////////////////////////////// setup guides


    /**
     * this method hold lement of base for guides
     */
    private  Polygon guideBaseTop;

    /**
     * this mehtod hold polygon element of color for guide
     */
    private  Polygon guideColor;
    /**
     * this variable holds arrow polygon element of javafx
     */
    private Polygon guideArrow;
    /**
     * this method holds elemnts of text turn in guide element in game view
     */
    private Text turnText;


    /**
     * this method updates guides size and position based on window size
     */
    private void updateGuidesSize()
    {
        try {
            this.guideBaseTop.getTransforms().clear();
            this.guideColor.getTransforms().clear();
            this.guideArrow.getTransforms().clear();
        }
        catch (Exception e)
        {

        }
        double scale=mainScene.getWidth()/720;
        double deltaX=( this.mainScene.getWidth()/2- (this.guideWidht/2)  );
        this.guideBaseTop.setTranslateX(deltaX);


        Scale scaling= new Scale();
        scaling.setPivotX(this.guideWidht/2);
        scaling.setPivotY(0);
        scaling.setX(scale);
        scaling.setY(scale);

        this.guideBaseTop.getTransforms().add(scaling);





        this.guideColor.setTranslateX(this.guideBaseTop.getTranslateX());
        this.guideColor.getTransforms().add(scaling);
        // this.guideColor.setScaleY(scale);

        this.guideArrow.setTranslateX(deltaX);


        if(!this.isTurnInOrder)
        {
            Scale scaleMirror= new Scale();
            scaleMirror.setPivotX(this.guideWidht/2);
            scaleMirror.setX(-1);
            this.guideArrow.getTransforms().add(scaleMirror);
        }

        this.guideArrow.getTransforms().add(scaling);



        Font font= new Font("Arial", (this.guideColor.getLayoutBounds().getHeight()*scale)*0.6);
        this.turnText.setFont(font);


        this.turnText.setX( this.mainScene.getWidth()/2-this.turnText.getLayoutBounds().getWidth()/2);
        this.turnText.setY((guideHeight* scale)  -  this.turnText.getLayoutBounds().getHeight()/2);




    }

    /**
     * this varaible dtermins wideth size ration for game guides
     */
    final private double guideWidht=200;
    /**
     * this varaible dtermins height size ration for game guides
     */
    final private double guideHeight=10;

    /**
     * this boolean golds order of turns for guide
     */
    private  boolean isTurnInOrder=true;

    /**
     * this method negeate turn order for guides
     */
    private void swapTurnGuide()
    {
        this.isTurnInOrder=!isTurnInOrder;
        updateGuidesSize();
    }

    /**
     * this method setups guide elemnts in game view
     */
    private void setupGuides()
    {

        double guideTrapezoidDiff = 12.5;
        Double[] points = new Double[]{
                0.0,0.0,
                guideWidht,0.0,
                guideWidht- guideTrapezoidDiff,guideHeight,
                0+ guideTrapezoidDiff *2,guideHeight,
        };

        Double[] arrowPointsp = new Double[]{
                guideTrapezoidDiff *2, guideTrapezoidDiff /3,
                guideTrapezoidDiff *2,guideHeight- guideTrapezoidDiff /3,
                guideTrapezoidDiff *-1,guideHeight- guideTrapezoidDiff /3,
                guideTrapezoidDiff *-1,guideHeight*0.8,
                guideTrapezoidDiff *-1.5,guideHeight/2,
                guideTrapezoidDiff *-1,guideHeight*(1-0.8),
                guideTrapezoidDiff *-1, guideTrapezoidDiff /3,

                // 0.0,0.0

        };

        guideBaseTop= new Polygon();
        guideBaseTop.setStroke(Color.WHITE);
        guideBaseTop.setFill(this.tranparentColor);
        guideBaseTop.setStrokeWidth(2);
        guideBaseTop.setStrokeLineCap(StrokeLineCap.ROUND);
        guideBaseTop.getPoints().addAll(points);

        guideArrow= new Polygon();
        guideArrow.setStroke(Color.WHITE);
        guideArrow.setFill(Color.WHITE);
        guideArrow.setStrokeWidth(2);
        guideArrow.setStrokeLineCap(StrokeLineCap.ROUND);
        guideArrow.getPoints().addAll(arrowPointsp);



        this.turnText = new Text();
        this.turnText.setText("-");
        this.turnText.setFill(Color.WHITE);
        this.turnText.setBoundsType(TextBoundsType.VISUAL);

        guideColor= new Polygon();
        guideColor.setFill(Color.WHITE);
        guideColor.setStrokeLineCap(StrokeLineCap.ROUND);
        guideColor.getPoints().addAll(points);
        this.root.getChildren().add(this.guideArrow);
        this.root.getChildren().add(guideColor);
        this.root.getChildren().add(guideBaseTop);
        this.root.getChildren().add(this.turnText);

//

    }

    /**
     * this method sets guide color in top guides of game view
     * @param col
     */
    private  void setGuideColor(Color col)
    {
        this.guideColor.setFill(col);
        if(col==Color.GOLD)
            this.turnText.setFill(Color.BLACK);
        else
            this.turnText.setFill(Color.WHITE);
    }


    /**
     * this method swaps arrow direction on in top guides of game view
     */

    public void swapTurn() {
        this.swapTurnGuide();
    }

///////////////////////////////////// chat
    /**
     * this varaible holds javafx element of text field to input chat messeage
     */
    private  TextField chatArea;
    /**
     * this varaible hold javafx elemnts wiht chat logs
     */
    private  TextArea chatLogs;
    /**
     * this varaible dtermins if player is currently writiing in char
     */
    private boolean isWritingInChat=false;

    /**
     * this stack holds all meesegae sent during active game
     */
    private  Stack<Text> popupMess;
    /**
     * this varaible determins chat messeage limit
     */
    private final int limitChat=40;

    /**
     * this method setups chat window for game view
     */
    private  void setupChat()
    {
        mainScene.getStylesheets().add("Style.css");
        popupMess = new Stack<Text>();
        chatArea = new TextField();
        this.setupChatFormatting();
        chatLogs = new TextArea();
        chatLogs.setWrapText(true);
        chatLogs.setEditable(false);


        hideChat();
        this.root.getChildren().add(chatArea);
        this.root.getChildren().add(chatLogs);


    }

    /**
     * this method is run on tylda button which hides or show chat
     */
    private   void onTyldaButton()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(isWritingInChat)
                {
                    hideChat();
                }
                else
                {
                    showChat();
                }
                isWritingInChat=!isWritingInChat;
            }
        });

    }

    /**
     * this method show chat view and hides popup messegees
     */
    private void showChat()
    {

        chatArea.setVisible(true);
        chatLogs.setVisible(true);
        chatArea.requestFocus();


        for (Text popup: this.popupMess
             ) {
            popup.setVisible(false);
        }
    }

    /**
     * this method hides chat and set view to popup messeages
     */
    private void hideChat()
    {
        chatArea.setVisible(false);
        chatLogs.setVisible(false);


        for (Text popup: this.popupMess
        ) {
            popup.setVisible(true);

        }

    }

    /**
     * this method sets formatting for chat
     */
    private  void setupChatFormatting()
    {

        TextFormatter textFormatter = new TextFormatter<>(change -> {

            String txt=change.getControlNewText();

            if (txt.length() > this.limitChat) {
                return null;

            }

            if(change.getControlNewText().endsWith("`")||change.getControlNewText().endsWith("\n"))
            {
                return null;
            }

            return change;
        });

        this.chatArea.setTextFormatter(textFormatter);


    }

    /**
     * this method sends mesege from meessegae window to server
     */
    private  void sendMessage()
    {
        if(this.chatArea.getLength()>0) {
            this.guiController.clientApp.sendChatMesseage(this.chatArea.getText());
            this.chatArea.setText("");

        }
    }

    /**
     * this method updates size an postion of chat elements based on window size
     */
    private  void updateChatSize()
    {
        double height= this.mainScene.getHeight()/45;

        Font font = new Font("Arial", height);
        this.chatArea.setFont(font);
        this.chatArea.setPrefSize(this.mainScene.getWidth()/4,height);
        this.chatArea.setTranslateY(this.mainScene.getHeight()-this.chatArea.getHeight());

        this.chatLogs.setFont(font);
        this.chatLogs.setPrefSize(this.mainScene.getWidth()/4,height*10);
        this.chatLogs.setTranslateY(this.mainScene.getHeight()-this.chatArea.getHeight()-this.chatLogs.getHeight());
        //    chatLogs.setBackground(new Background(new BackgroundFill(Color.BLACK,null,null)));

        int i=popupMess.size();

        for (Text popup: this.popupMess
        ) {
            popup.setFont(font);
            double popX = (this.mainScene.getWidth()/400);
            double popY = (this.mainScene.getHeight() - height * (i));

            TranslateTransition translateTransition= new TranslateTransition();

            translateTransition.setToY(popY);
            translateTransition.setToX(popX);
            translateTransition.setNode(popup);
            translateTransition.setDuration(Duration.millis(200));
            translateTransition.play();


            i--;
        }



    }

    /**
     * this method adds messegaes to chat log after pressing ~
     * @param chatMesseage
     */

      public void addChatLog(ChatMesseage chatMesseage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                guiController.soundPlayer.playmesseagePop();
                chatLogs.appendText(chatMesseage + "\n");
                addPopupMesseage(chatMesseage);
            }
        });

    }

    /**
     * this method adds popup messeage after reciving messeage from server
     * @param chatMesseage
     */
    private   void addPopupMesseage(ChatMesseage chatMesseage)
    {

        Text text = new Text(chatMesseage.toString());
        if(this.isWritingInChat)
            text.setVisible(false);
        text.setX(0);
        text.setY(0);
        text.setTranslateY(this.mainScene.getHeight());

        text.setFill(Color.WHITE);
        this.popupMess.push(text);
        this.root.getChildren().add(text);
        updateChatSize();

        int popupTime = 2;
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(popupTime), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removePopup(text);
            }
        }));
        timeline.play();

    }

    /**
     * a method called to remove text element from scene after defined time amount
     * @param text
     */
    private void removePopup(Text text)
    {
        double fadeTime = 2;
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(fadeTime));
        fadeTransition.setNode(text);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();


        fadeTransition.statusProperty().addListener(new ChangeListener<Animation.Status>() {
            @Override
            public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                if(newValue== Animation.Status.STOPPED)
                {
                    root.getChildren().remove(text);
                    popupMess.remove(text);



                }
            }
        });
    }

//////////////////////////////////////////////// waitig processing


    /**
     * boolean variable to deteremine if server is still waiting for some players
     */
    public   boolean isWaitingForPlayer=false;
    /**
     * a list to containg dark backgrounds elemnts for waiting lock
     */
    private final List<Rectangle> waitBackGrounds = new ArrayList<Rectangle>();
    /**
     * a list conataing javafx text elemnents that serer waits for
     */
    private final List<Text> waitTexts = new ArrayList<Text>();
    /**
     * wait list containng nick of players that peapole wait for
     */
    public  List<String> nicksWaiting = new ArrayList<String>();


    /**
     * a method used to start waiting process for specific player
     * @param nick
     */
    public  void startWaiting(String nick)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                isWaitingForPlayer=true;
                nicksWaiting.add(nick);
                Text text = new Text();
                text.setFill(Color.WHITE);
                waitTexts.add(text);

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(new Color(0,0,0,0.7));
                waitBackGrounds.add(rectangle);
                root.getChildren().addAll(rectangle,text);
                System.out.printf("Started Waitng for " + nick + "\n");
                updateWaitSize();


            }
        });

    }

    /**
     * a method used to update size of wait list lock
     */
    private  void updateWaitSize()
    {
        int amtOfwaits=waitBackGrounds.size();
        double rowHeight=this.mainScene.getHeight()/(8*2);
        double finalHeight=rowHeight*amtOfwaits+((rowHeight/2)*(amtOfwaits-1));
        Font font = new Font("Arial", rowHeight/2);
        for(int i=0;i<amtOfwaits;i++)
        {
            this.waitBackGrounds.get(i).setWidth(this.mainScene.getWidth());
            this.waitBackGrounds.get(i).setHeight(rowHeight);
            this.waitBackGrounds.get(i).setY(this.mainScene.getHeight()/2-(finalHeight/2) + i *(rowHeight*1.5));

            this.waitTexts.get(i).setFont(font);
            this.waitTexts.get(i).setY( this.waitBackGrounds.get(i).getY()+this.waitBackGrounds.get(i).getHeight()/2 + this.waitTexts.get(i).getLayoutBounds().getHeight()/2);
            this.waitTexts.get(i).setX( this.waitBackGrounds.get(i).getX()+this.waitBackGrounds.get(i).getWidth()/2 - this.waitTexts.get(i).getLayoutBounds().getWidth()/2);

        }
    }

    /**
     * a method usd to determiine wait index in wait list based on nick
     * @param nick
     * @return
     */
    private  int findIndexOfWait(String nick)
    {
        int indx=0;
        for(indx=0;indx<this.nicksWaiting.size();indx++)
        {
            if(this.nicksWaiting.get(indx).equals(nick))
                break;
        }
        return indx;
    }

    /**
     * a method to update text on wait screen
     * @param nick
     * @param sec
     */
    public void updateWaitText(String nick,int sec)
    {
        this.guiController.soundPlayer.playClock();
        int indx=this.findIndexOfWait(nick);
        this.waitTexts.get(indx).setText("Watiing for player "+ nick + ": " + sec + "s");
        updateWaitSize();
    }

    /**
     * a method to stop waiting proces from specifc player
     * @param nick
     */
    public void stopWaiting(String nick) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {


                int indx = findIndexOfWait(nick);
                root.getChildren().remove(waitBackGrounds.get(indx));
                root.getChildren().remove(waitTexts.get(indx));

                waitBackGrounds.remove(indx);
                waitTexts.remove(indx);
                nicksWaiting.remove(indx);
                System.out.println("check Waiting\n");
                if (nicksWaiting.size() == 0) {
                    System.out.println("Stop Waiting\n");
                    isWaitingForPlayer = false;
                }
                }
                catch (IndexOutOfBoundsException e)
                {

                }

            }

        });
    }
}

