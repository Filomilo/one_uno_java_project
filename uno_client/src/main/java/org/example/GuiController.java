package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

import java.util.ServiceConfigurationError;
import java.util.concurrent.TimeUnit;

public class
GuiController extends Application {

    GameView gameView;
    MainVew mainVew;

    Scene mainScene;

    public static void main(String[] args) {
        launch(args);
    }
    Stage primaryStage;
    @Override
    public void start(Stage primaryStage) {

        try{
            this.primaryStage=primaryStage;
            this.mainVew= new MainVew(this);

            this.gameView= new GameView(this);
            this.gameView.iniit(primaryStage);

            mainVew.iniit(primaryStage);// mainScene = new Scene(mainVew.root, 1250, 720,true, SceneAntialiasing.BALANCED);
            primaryStage.setScene(mainVew.mainScene);

            primaryStage.show();
            mainVew.updateOnSize();




        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    void changeSceneToMain()
    {

    }

    void changeSceneToGame()
    {
        this.primaryStage.setScene(gameView.mainScene);
        this.gameView.updateOnSize();
    }
}
