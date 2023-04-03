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

import static java.lang.Math.abs;


public class RankView extends Application {
    Scene mainScene;


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
    Line linesMainSeparetor[];
    Text nicksRank[];
    Text amtOfWinsText[];
    Text rankNbText[];
    Line rowLineSeperator[];


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



        String nicks[] =this.getNicksArray();
        int amtOfwins[]= this.getScoreArray();
        this.nicksRank = new Text[nicks.length];
        this.amtOfWinsText = new Text[nicks.length];
        this.rankNbText = new Text[nicks.length];
        this.rowLineSeperator = new Line[nicks.length];

        for(int i=0; i<nicks.length;i++)
        {
            this.nicksRank[i] = new Text(nicks[i]);
            this.amtOfWinsText[i] = new Text(Integer.toString(amtOfwins[i]) );
            this.rankNbText[i] = new Text(Integer.toString(i+1) );
            this.rowLineSeperator[i] = new Line();
            this.nicksRank[i].setFill(Color.WHITE);
            this.amtOfWinsText[i].setFill(Color.WHITE);
            this.rankNbText[i].setFill(Color.WHITE);
            this.rowLineSeperator[i].setStroke(Color.WHITE);
            this.rowLineSeperator[i].setStrokeWidth(1);
            this.root.getChildren().add(  rowLineSeperator[i]);
            this.root.getChildren().add(   this.rankNbText[i]);
            this.root.getChildren().add( this.nicksRank[i]);
            this.root.getChildren().add( this.amtOfWinsText[i]);
        }

        this.setupMask();





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

        this.linesMainSeparetor = new Line[4];
        for(int i=0;i<linesMainSeparetor.length;i++){

            linesMainSeparetor[i] = new Line();
            linesMainSeparetor[i] .setStroke(Color.WHITE);
            linesMainSeparetor[i] .setStrokeWidth(5);
            linesMainSeparetor[i].setStrokeLineCap(StrokeLineCap.SQUARE);
            this.root.getChildren().add( linesMainSeparetor[i] );
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

        for (Line line: this. rowLineSeperator
             ) {
            line.setOnScroll(
                    new EventHandler<ScrollEvent>() {
                        @Override
                        public void handle(ScrollEvent event) {
                            onScrollMainRank(event);
                        }
                    }
            );;
        }

        for (Text text: this.rankNbText
             ) {
            text.setOnScroll(
                    new EventHandler<ScrollEvent>() {
                        @Override
                        public void handle(ScrollEvent event) {
                            onScrollMainRank(event);
                        }
                    }
            );;
        }
        for (Text text: this.amtOfWinsText
        ) {
            text.setOnScroll(
                    new EventHandler<ScrollEvent>() {
                        @Override
                        public void handle(ScrollEvent event) {
                            onScrollMainRank(event);
                        }
                    }
            );;
        }
        for (Text text: this.nicksRank
        ) {
            text.setOnScroll(
                    new EventHandler<ScrollEvent>() {
                        @Override
                        public void handle(ScrollEvent event) {
                            onScrollMainRank(event);
                        }
                    }
            );;
        }





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
        Shape screen= new Rectangle(this.mainScene.getWidth()+100,this.mainScene.getHeight()+100);
        Rectangle headerMask = new Rectangle(this.rankViewBase.getWidth(),this.linesMainSeparetor[2].getEndY()- this.rankViewBase.getY());
        headerMask.setX(this.rankViewBase.getX());
        headerMask.setY(this.rankViewBase.getY());
        Shape toCut= Shape.subtract(this.rankViewBase,headerMask);
        this.mask = Shape.subtract(screen,toCut);
        mask.setFill(this.gradient);
        this.root.getChildren().set(index,this.mask);
    }

    private void updateRecordsTextSize() {

        Font font = this.columnsHeadersText[0].getFont();

        double rankColumnCenter= (this.columnsHeadersText[0].getLayoutBounds().getMinX()+this.columnsHeadersText[0].getLayoutBounds().getMaxX())/2;
        double nickColumnCenter= (this.columnsHeadersText[1].getLayoutBounds().getMinX()+this.columnsHeadersText[1].getLayoutBounds().getMaxX())/2;
        double winsColumnCenter= (this.columnsHeadersText[2].getLayoutBounds().getMinX()+this.columnsHeadersText[2].getLayoutBounds().getMaxX())/2;

        double fontHeight=font.getSize()*1.2;

        double scorllLock=((fontHeight+1.1)*this.rowLineSeperator.length)-(this.rankViewBase.getLayoutBounds().getMaxY()-this.linesMainSeparetor[2].getEndY());
        if(scorllLock<0)
            scorllLock=0;
       // System.out.println(this.scrollAmount);
        //System.out.println(scorllLock+ "\n");
        if(abs(this.scrollAmount)>scorllLock)
        {
            this.scrollAmount=-scorllLock;
        }


        for(int i=0;i<this.amtOfWinsText.length;i++)
        {
            /////////////// font setup
            this.rankNbText[i].setFont(font);
            this.amtOfWinsText[i].setFont(font);
            this.nicksRank[i].setFont(font);
            double rowHEight=this.linesMainSeparetor[2].getEndY()+(1.1*fontHeight)+fontHeight*i+this.scrollAmount;
            ////////////// rank number updae
            this.rankNbText[i].setX(rankColumnCenter-this.rankNbText[i].getLayoutBounds().getWidth()/2.1);
            this.rankNbText[i].setY(rowHEight);
            ////////////////// nick udpdate

            double preferdWidth=this.linesMainSeparetor[1].getStartX()-this.linesMainSeparetor[0].getStartX();
            if(this.nicksRank[i].getLayoutBounds().getWidth()>preferdWidth)
            {
                Font newFont= new Font("Arila", this.nicksRank[i].getFont().getSize()*((preferdWidth/this.nicksRank[i].getLayoutBounds().getWidth())*0.7));
                  this.nicksRank[i].setFont(newFont);
            }
            this.nicksRank[i].setX(nickColumnCenter-this.nicksRank[i].getLayoutBounds().getWidth()/2.1);
            this.nicksRank[i].setY(rowHEight);
            ////////////////////// number of wins update
            this.amtOfWinsText[i].setX(winsColumnCenter-this.amtOfWinsText[i].getLayoutBounds().getWidth()/2);
            this.amtOfWinsText[i].setY(rowHEight);
            ///////////////////////// seproatting lines
            rowHEight=rowHEight+fontHeight/4;
            this.rowLineSeperator[i].setStartX(0);
            this.rowLineSeperator[i].setEndX(this.mainScene.getWidth());
            this.rowLineSeperator[i].setStartY(rowHEight);
            this.rowLineSeperator[i].setEndY(rowHEight);

        }







    }

    private void updateHeaderTextSize() {
        double scaleGuide=this.mainScene.getWidth()>this.mainScene.getHeight()?this.mainScene.getHeight():this.mainScene.getWidth();
        double fontSize= scaleGuide/20;
        Font font = new Font("Arial", fontSize);
        for (Text tx: this.columnsHeadersText
             ) {
            tx.setFont(font);
        }
    ///////////////// first column
        double headerHright= this.rankViewBase.getY()+fontSize*1.2;
        this.columnsHeadersText[0].setY(headerHright);
        this.columnsHeadersText[0].setX(this.rankViewBase.getX()+this.columnsHeadersText[0].getLayoutBounds().getWidth());

        this.linesMainSeparetor[0].setStartY(this.rankViewBase.getY()+this.linesMainSeparetor[0].getStrokeWidth()/2);
        this.linesMainSeparetor[0].setEndY(this.rankViewBase.getY()+this.rankViewBase.getHeight()-this.linesMainSeparetor[0].getStrokeWidth()/2);
        double lineVerticalX= this.columnsHeadersText[0].getLayoutBounds().getMaxX()+this.columnsHeadersText[0].getLayoutBounds().getWidth();
        this.linesMainSeparetor[0].setStartX(lineVerticalX);
        this.linesMainSeparetor[0].setEndX(lineVerticalX);
        //////////////////////////////////// third column

        this.columnsHeadersText[2].setY(headerHright);
        this.columnsHeadersText[2].setX(this.rankViewBase.getWidth()+this.rankViewBase.getX()-this.columnsHeadersText[0].getLayoutBounds().getWidth()*2);

        this.linesMainSeparetor[1].setStartY(this.rankViewBase.getY()+this.linesMainSeparetor[1].getStrokeWidth()/2);
        this.linesMainSeparetor[1].setEndY(this.rankViewBase.getY()+this.rankViewBase.getHeight()-this.linesMainSeparetor[1].getStrokeWidth()/2);
        lineVerticalX= this.columnsHeadersText[2].getLayoutBounds().getMinX()-this.columnsHeadersText[2].getLayoutBounds().getWidth()/2;
        this.linesMainSeparetor[1].setStartX(lineVerticalX);
        this.linesMainSeparetor[1].setEndX(lineVerticalX);
        //////////////////////////////////// second column
        this.columnsHeadersText[1].setY(headerHright);
        this.columnsHeadersText[1].setX((this.linesMainSeparetor[0].getStartX()+this.linesMainSeparetor[1].getStartX())/2.0-this.columnsHeadersText[1].getLayoutBounds().getWidth()/2.5);
        ////////////////// header separetor
        this.linesMainSeparetor[2].setStartX(this.rankViewBase.getX()+this.linesMainSeparetor[2].getStrokeWidth()/2);
        this.linesMainSeparetor[2].setEndX(this.rankViewBase.getX()+this.rankViewBase.getWidth()-this.linesMainSeparetor[2].getStrokeWidth()/2);
        double lineHeight=this.columnsHeadersText[0].getLayoutBounds().getMaxY()*1.1;
        this.linesMainSeparetor[2].setStartY(lineHeight);
        this.linesMainSeparetor[2].setEndY(lineHeight);



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
        if(scrollAmount>0)
            scrollAmount=0;
       //System.out.println("scrollonign: " + this.scrollAmount);
        this.updateRecordsTextSize();
    }

    int[] getScoreArray()
    {
       return new int[]{10,5,8,4,1,2,8,1000,10,5,8,4,1,2,8,1000,10,5,8,4,1,2,8,1000};
      //  return new int[]{10,5};

    }

    String[] getNicksArray()
    {
       return new String[]{"sdfdsfdsf","asdasdwwww","~~~~~~~~~~~~~~~~~~~~~~~~~~~~","4","dfsssss","dfgdfg","fgdfg","ggggg","sdfdsfdsf","asdasdwwww","~~~~~~~~~~~~~~~~~~~~~~~~~~~~","4","dfsssss","dfgdfg","fgdfg","ggggg","sdfdsfdsf","asdasdwwww","~~~~~~~~~~~~~~~~~~~~~~~~~~~~","4","dfsssss","dfgdfg","fgdfg","ggggg"};
       // return new String[]{"sdfdsfdsf","asdasdwwww"};

    }

}
