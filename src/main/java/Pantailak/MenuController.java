package Pantailak;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Optional;

public class MenuController {

    @FXML
    private Button atzeraBotoia;

    @FXML
    private void saioaItxi(javafx.event.ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Saioa itxi");
        alert.setHeaderText("Ziur zaude saioa itxi nahi duzula?");
        alert.setContentText("Login pantailara itzuliko zara.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("login-view.fxml"));
                Scene loginScene = new Scene(loginRoot);
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.setScene(loginScene);
                currentStage.setTitle("Saioa Hasi");
                currentStage.centerOnScreen();
            } catch (IOException e) {
                erroreaErakutsi("Errorea saioa ixtean: " + e.getMessage());
            }
        }
    }

    private void erroreaErakutsi(String mezua) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errorea");
        alert.setHeaderText(null);
        alert.setContentText(mezua);
        alert.showAndWait();
    }

    @FXML
    private void onLangileakClick(ActionEvent ActionEvent) {
        try {
            Stage currentStage = (Stage) ((Node) ActionEvent.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("langileak-view.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Langileak");
            newStage.setMaximized(true);
            newStage.centerOnScreen();
            newStage.setScene(new Scene(root));

            newStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });

            currentStage.close();

            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onOsagaiakClick(ActionEvent actionEvent) {
        try {
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("osagaiak-view.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Osagaiak");
            newStage.setMaximized(true);
            newStage.centerOnScreen();
            newStage.setScene(new Scene(root));

            newStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });

            currentStage.close();
            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEskaerakClick(ActionEvent actionEvent) {
        try {
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("eskaerak-view.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Eskaerak");
            newStage.setMaximized(true);
            newStage.centerOnScreen();
            newStage.setScene(new Scene(root));

            newStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });

            currentStage.close();
            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}