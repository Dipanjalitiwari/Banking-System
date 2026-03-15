package com.bankingsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            primaryStage = stage;
            showScene("/com/bankingsystem/view/login.fxml", "Banking Management System");
        } catch (Exception e) {
            System.out.println("Exception Occurred");
            e.printStackTrace();
        }
    }

    public static void showScene(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, 980, 640);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}