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
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameView extends Application {
    Scene mainScene;



    final int startH = 720;
    final int startW = 1280;
    Color tranparentBlack = new Color(0, 0, 0, 0.5);
    Color tranparentColor = new Color(1, 0, 0, 0.0);
    Color greenColor = new Color(0, 0.4, 0.1, 1);
    Stop[] greenStops = new Stop[]{new Stop(0, this.greenColor), new Stop(1, Color.BLACK)};
    GuiController guiController;

    String cardPrefix=  "uno_client\\src\\main\\resources\\Cards\\unoCards_";

    Image cardImages[]= new Image[56];



    ImageView[] emptyCards = new ImageView[2+this.getAmtOfOpponets()];

    Text nickText[]=new Text[this.getAmtOfOpponets()+1];
    Rectangle textBox[]=new Rectangle[this.getAmtOfOpponets()+1];

    Text amtOfCardsText[]= new Text[this.getAmtOfOpponets()];

    double cardWidth=100;
    double cardHandPosY=0;

    List<ImageView> cardsInHand = new ArrayList<ImageView>();

    Rectangle button = new Rectangle();
    Text buttonText= new Text("Surrender");



    public GameView(GuiController guiController) {
        this.guiController=guiController;
    }

    public GameView()
    {

    }

    public static void main(String[] args) {
        launch(args);
    }




    Group root;


    void setupEmptyCardsPostion()
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


    void iniit(Stage primaryStage) throws FileNotFoundException {

        root = new Group();
        mainScene = new Scene(root, 1250, 720, true, SceneAntialiasing.BALANCED);
        this.loadImages();

        this.setupEmptyCardsPostion();
        this.setupButtonShape();
        this.setupNicks();
        this.looadCardsInHand();
        this.updateBackground();
        this.addListiners(primaryStage);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            this.iniit(primaryStage);

           primaryStage.setScene(mainScene);
            //   primaryStage.setFullScreen(true);






            primaryStage.show();
            this.updateOnSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupNicks() {
        this.nickText[0]= new Text(this.getPlayerNick());
        this.nickText[0].setX(0);
        this.nickText[0].setFill(Color.WHITE);


        this.textBox[0]= new Rectangle();
        this.textBox[0].setFill(this.tranparentBlack);
        this.textBox[0].setX(0);
        this.root.getChildren().add(   this.textBox[0]);
        this.root.getChildren().add(this.nickText[0]);
        List<String> nicks = getNick();
        for(int i=1;i<this.nickText.length;i++){

            this.nickText[i]= new Text(nicks.get(i-1));
            this.nickText[i].setFill(Color.WHITE);

            this.textBox[i] = new Rectangle();
            this.textBox[i].setFill(this.tranparentBlack);

            this.root.getChildren().add(this.textBox[i]);
            this.root.getChildren().add(this.nickText[i]);

        }




        // amount of cards number
        int [] amtOfCards=this.getAmtOfCards();
        for (int i=0;i<this.amtOfCardsText.length;i++)
        {
            String txt="x";
            txt+=amtOfCards[i];
            this.amtOfCardsText[i]=new Text(txt);
            this.amtOfCardsText[i].setFill(Color.WHITE);

            this.root.getChildren().add(  this.amtOfCardsText[i]);
        }

    }

    private void loadImages() throws FileNotFoundException {
        int iterator=0;

        for(int i=0;i<4;i++)
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
                String url=this.cardPrefix + color + "_" + type + ".png";
                loadCard(url,iterator);

                iterator++;

            }



        }
        loadCard(this.cardPrefix+"Black_choice.png", iterator++);
        loadCard(this.cardPrefix+"Black_plus4.png", iterator++);
        loadCard(this.cardPrefix+"CardBack.png", iterator++);
        loadCard(this.cardPrefix+"Empty.png", iterator++);
      //  System.out.println("Size: " + iterator);



    }

    void loadCard(String url, int positonInTable)
    {
        try{
            FileInputStream fileInputStream = new FileInputStream(url);
            this.cardImages[positonInTable] = new Image(fileInputStream);
        }
        catch (Exception e)
        {
            System.out.println(url);
        }

    }

    void updateCardScale()
{
    double scaleFactor=7;
    this.cardWidth=this.mainScene.getHeight()*1.5>this.mainScene.getWidth()?this.mainScene.getWidth()/scaleFactor/2:this.mainScene.getHeight()/scaleFactor;





}



    int getAmtOfOpponets()
    {
        return 4;
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


    }


    void updateOnSize() {
        this.updateBackground();
        this.updateCardScale();
        this.updateEmptyCard();
        this.updateCardsInHandScale();
        this.upadateButtonShape();
        this.upadateText();
    }

    private void setupButtonShape()
    {
        this.buttonText.setFill(Color.WHITE);
        this.button.setStrokeWidth(1.5);
        this.button.setStroke(Color.WHITE);
        this.button.setFill(tranparentColor);
        this.root.getChildren().add(this.button);
        this.root.getChildren().add(this.buttonText);

    }

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

    private void updateCardsInHandScale() {
        int i=0;
        double width=this.mainScene.getWidth()/3;
        double startPostion=this.mainScene.getWidth()/2-width/2;
        double stepMove=width/this.cardsInHand.size();
        this.cardHandPosY=this.mainScene.getHeight()-this.cardWidth*0.7;
        for (ImageView card: cardsInHand
             ) {
            card.setFitWidth(this.cardWidth);

            card.setX(startPostion+stepMove*i);
            card.setY(cardHandPosY);
            i++;
        }

    }

    private void upadateText() {
        double fontSizeparm=5;
        Font font= new Font("Arial",cardWidth/fontSizeparm);
        this.nickText[0].setFont(font);
        this.nickText[0].setY(this.mainScene.getHeight()- this.nickText[0].getLayoutBounds().getHeight()*0.2);
        this.textBox[0].setY(this.nickText[0].getLayoutBounds().getMinY());
        this.textBox[0].setHeight(this.nickText[0].getLayoutBounds().getHeight());
        this.textBox[0].setWidth(this.nickText[0].getLayoutBounds().getWidth()*1.1);

        for(int i=1;i<this.nickText.length;i++)
        {
            this.nickText[i].setFont(font);
            this.nickText[i].setX((this.emptyCards[i+1].getBoundsInParent().getMinX()+this.emptyCards[i+1].getBoundsInParent().getMaxX())/2-this.nickText[i].getLayoutBounds().getWidth()/2);
            this.nickText[i].setY(this.emptyCards[i+1].getBoundsInParent().getMinY());

            this.textBox[i].setY(this.nickText[i].getLayoutBounds().getMinY());
            this.textBox[i].setX(this.nickText[i].getLayoutBounds().getMinX());
            this.textBox[i].setHeight(this.nickText[i].getLayoutBounds().getHeight());
            this.textBox[i].setWidth(this.nickText[i].getLayoutBounds().getWidth()*1.1);

        }


        for (int i=0;i<this.amtOfCardsText.length;i++)
        {
            //System.out.println(i);
            this.amtOfCardsText[i].setFont(font);
            this.amtOfCardsText[i].setX(this.nickText[i+1].getLayoutBounds().getMinX()+this.nickText[i+1].getLayoutBounds().getWidth()/2);
            this.amtOfCardsText[i].setY(this.nickText[i+1].getLayoutBounds().getMaxY()+cardWidth*2);


        }



    }

    private void updateEmptyCard() {
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


    void updateBackground() {

        RadialGradient gradient = new RadialGradient(0, 0, mainScene.getWidth() / 2, mainScene.getHeight() / 2, mainScene.getHeight() > mainScene.getWidth() ? mainScene.getHeight() * 2 : mainScene.getWidth() * 2, false, CycleMethod.NO_CYCLE, this.greenStops);
        mainScene.setFill(gradient);
    }

    List<String> getNick()
    {
        String nickArr[]={"nick5561","nick5556562","nic5656k1","nick2","nick1","nick2","nick1","nick2","nick2" };
        List<String> nicks= new ArrayList<String>(Arrays.asList(nickArr));
        return nicks;
    }

    String getPlayerNick()
    {
        return "Player";
    }

    int[] getAmtOfCards()
    {
        int array[]={1,2,4,5,6,7,8,9,5,4,7,5,};
        return array;
    }


    void looadCardsInHand()
    {
        List<UnoCard> cards;
        cards = new ArrayList<UnoCard>();
        cards.add(new UnoCard(UnoCard.UNO_TYPE.REGULAR, UnoCard.UNO_COLOR.GREEN,0));
        cards.add(new UnoCard(UnoCard.UNO_TYPE.BLOCK, UnoCard.UNO_COLOR.YELLOW,0));
        cards.add(new UnoCard(UnoCard.UNO_TYPE.COLOR, UnoCard.UNO_COLOR.BLACK,0));
        cards.add(new UnoCard(UnoCard.UNO_TYPE.PLUS2, UnoCard.UNO_COLOR.BLUE,0));
        cards.add(new UnoCard(UnoCard.UNO_TYPE.PLUS4, UnoCard.UNO_COLOR.BLACK,0));
        cards.add(new UnoCard(UnoCard.UNO_TYPE.REGULAR, UnoCard.UNO_COLOR.RED,5));

        for (UnoCard card: cards
             ) {
            this.addCard(card);
        }


    }

    void addCard(UnoCard card)
    {
        ImageView cardView = new ImageView(this.cardImages[this.getIndexOfmage(card)]);
        cardView.setPreserveRatio(true);
        this.cardsInHand.add(cardView);
        this.root.getChildren().add(cardView);



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

    int getIndexOfmage(UnoCard card)
    {
        switch (card.getType())
        {
            case COLOR: return 13*4+0;
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

    void onMouseOnCard(ImageView card)
    {
        card.setY(this.cardHandPosY-this.cardWidth/1.5);
      //  System.out.println("OnCard");
    }
    void onMouseOutsideCard(ImageView card)
    {
        card.setY(this.cardHandPosY);
        //System.out.println("OutisideCard");
    }

    void onCardClick(ImageView card)
    {
        this.cardsInHand.remove(card);




        this.root.getChildren().remove(card);
        this.updateCardsInHandScale();
       // System.out.println("Click");
    }

void onMouseOnButton()
{
    this.button.setFill(Color.WHITE);
    this.buttonText.setFill(Color.BLACK);
  //  System.out.println("On btton");

}


    void onMouseOutsideButton()
    {
        this.button.setFill(this.tranparentColor);
        this.buttonText.setFill(Color.WHITE);
      //  System.out.println("outiseed buttp");

    }

    void onButtonPresed()
    {
        this.button.setFill(Color.LIGHTGRAY);

    }

    void onButtonRealsed()
    {
        onMouseOnButton();
        //this.addCard(new UnoCard(UnoCard.UNO_TYPE.REGULAR, UnoCard.UNO_COLOR.GREEN,5));
       // this.updateCardsInHandScale();
        setOpponentsHand(2,true);
      //  System.out.println("click");
    }

    void setStackStable(boolean isFilled)
    {
        if(isFilled)
        {
            this.emptyCards[0].setImage(this.cardImages[this.cardImages.length-2]);
        }
        else
        {
            this.emptyCards[0].setImage(this.cardImages[this.cardImages.length-1]);
        }
    }

    void setStackPile(boolean isFilled)
    {
        if(isFilled)
        {
            this.emptyCards[1].setImage(this.cardImages[this.cardImages.length-2]);
        }
        else
        {
            this.emptyCards[1].setImage(this.cardImages[this.cardImages.length-1]);
        }
    }

    void setOpponentsHand(int nbOfOppoenent, boolean isFilled)
    {
        if(isFilled)
        {
            this.emptyCards[nbOfOppoenent+2].setImage(this.cardImages[this.cardImages.length-2]);
        }
        else
        {
            this.emptyCards[nbOfOppoenent+2].setImage(this.cardImages[this.cardImages.length-1]);
        }
    }


}
