package Pantailak;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OsagaiakApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Simple approach - load FXML directly
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(OsagaiakApplication.class.getResource("/Pantailak/osagaiak-view.fxml"));

        Scene scene = new Scene(loader.load());

        // Try to add CSS if it exists
        try {
            scene.getStylesheets().add(
                    OsagaiakApplication.class.getResource("/css/osis-suite.css").toExternalForm()
            );
        } catch (NullPointerException e) {
            System.out.println("CSS not found, continuing without it");
        }

        stage.setScene(scene);
        stage.setTitle("Osagaiak");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}