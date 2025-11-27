package Pantailak;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Saioa Hasi");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(600);
        stage.setHeight(400);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
