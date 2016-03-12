/**
 * Copyright
 *
 */

package com.kyanlife.code.evolis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ESPFRequestTool extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        String toolTitle = "Evolis SDK request tool";

        Parent root = FXMLLoader.load(getClass().getResource("ESPFRequestTool.fxml"));
        primaryStage.setTitle(toolTitle);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
