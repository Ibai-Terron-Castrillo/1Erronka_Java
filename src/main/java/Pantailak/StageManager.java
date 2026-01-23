package Pantailak;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class StageManager {

    private static final Image APP_ICON =
            new Image(StageManager.class.getResourceAsStream("/icons/app_icon.png"));
    private static final String APP_CSS =
            StageManager.class.getResource("/css/osis-suite.css").toExternalForm();

    private StageManager() {}

    public static void switchToLogin(Stage currentStage) throws IOException {
        switchStage(
                currentStage,
                "login-view.fxml",
                "Saioa Hasi",
                false
        );
    }

    public static void switchStage(
            Stage currentStage,
            String fxml,
            String title,
            boolean maximized
    ) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(StageManager.class.getResource(fxml));
        Parent root = loader.load();

        Stage newStage = new Stage();
        newStage.setTitle(title);
        newStage.getIcons().add(APP_ICON);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(APP_CSS);
        newStage.setScene(scene);

        if (maximized) {
            newStage.setMaximized(true);
        } else {
            newStage.centerOnScreen();
        }

        newStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        currentStage.close();
        newStage.show();
    }

    public static Stage openStage(
            String fxml,
            String title,
            boolean maximized,
            int width,
            int height
    ) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(StageManager.class.getResource(fxml));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(APP_ICON);

        Scene scene;
        if (maximized) {
            scene = new Scene(root);
            stage.setMaximized(true);
        } else {
            scene = new Scene(root, width, height);
        }

        scene.getStylesheets().add(APP_CSS);
        stage.setScene(scene);
        stage.centerOnScreen();

        return stage;
    }
}