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
        newStage.setScene(new Scene(root));

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

        if (maximized) {
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } else {
            stage.setScene(new Scene(root, width, height));
        }

        stage.centerOnScreen();
        return stage;
    }
}
