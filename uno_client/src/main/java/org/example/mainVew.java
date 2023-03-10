package org.example;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import static com.sun.java.accessibility.util.AWTEventMonitor.addComponentListener;

public class mainVew extends Application {

    Scene mainScene;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            Group root = new Group();

            mainScene = new Scene(root, 1250, 720);








           this.setBackground();
            primaryStage.setScene(mainScene);
            primaryStage.setFullScreen(true);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    void setBackground()
    {
        Stop[] stops = new Stop [] {new Stop(0, Color.GREEN), new Stop(1, Color.BLACK)} ;
        RadialGradient gradient = new RadialGradient(0,0,mainScene.getWidth()/2,mainScene.getHeight()/2,1000, false, CycleMethod.NO_CYCLE,stops);

        mainScene.setFill(gradient);
    }

}
