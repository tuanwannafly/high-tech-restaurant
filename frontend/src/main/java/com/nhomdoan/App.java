package com.nhomdoan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("Smart Restaurant");
        stage.setResizable(false);
        showLogin();
        stage.show();
    }

    public static void showLogin() throws Exception {
        Parent root = FXMLLoader.load(App.class.getResource("/com/nhomdoan/login.fxml"));
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(App.class.getResource("/com/nhomdoan/auth.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Smart Restaurant");
        primaryStage.setWidth(900);
        primaryStage.setHeight(640);
        primaryStage.centerOnScreen();
    }

    public static void showRegister() throws Exception {
        Parent root = FXMLLoader.load(App.class.getResource("/com/nhomdoan/register.fxml"));
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(App.class.getResource("/com/nhomdoan/auth.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Smart Restaurant");
        primaryStage.setWidth(900);
        primaryStage.setHeight(640);
        primaryStage.centerOnScreen();
    }

    public static void showMain(UserSession session) throws Exception {
        Parent root = FXMLLoader.load(App.class.getResource("/com/nhomdoan/dashboard.fxml"));
        Scene scene = new Scene(root, 1100, 680);
        scene.getStylesheets().add(App.class.getResource("/com/nhomdoan/auth.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Smart Restaurant — " + session.getName());
        primaryStage.setWidth(1100);
        primaryStage.setHeight(720);
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch();
    }
}
