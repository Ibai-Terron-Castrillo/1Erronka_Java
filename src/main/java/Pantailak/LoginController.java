package Pantailak;

import Klaseak.Erabiltzailea;
import Kontrola.ErabiltzaileKudeaketa;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField erabiltzailea;

    @FXML
    private PasswordField pasahitza;

    @FXML
    private Button sartu;

    @FXML
    private Button irten;

    @FXML
    protected void saioaHasi() {
        String user = erabiltzailea.getText();
        String pass = pasahitza.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            erroreaErakutsi("Erabiltzailea eta pasahitza bete behar dira");
            return;
        }

        Erabiltzailea erab = new Erabiltzailea(user, pass);
        ErabiltzaileKudeaketa erabiltzaileKudeaketa = new ErabiltzaileKudeaketa();

        try {
            if (erabiltzaileKudeaketa.balidatu(erab)) {
                menuNagusiaIreki();
            } else {
                erroreaErakutsi("Erabiltzailea edo pasahitza okerrak");
            }
        } catch (SQLException e) {
            erroreaErakutsi("Errorea datu basearekin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void menuNagusiaIreki() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();

            Stage menuStage = new Stage();
            Scene scene = new Scene(root);
            menuStage.setScene(scene);
            menuStage.setTitle("Menu Nagusia");

            menuStage.setMaximized(true);

            menuStage.centerOnScreen();

            Stage loginStage = (Stage) erabiltzailea.getScene().getWindow();
            loginStage.close();

            menuStage.show();

            menuStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });

        } catch (IOException e) {
            erroreaErakutsi("Errorea menua irekitzean: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void erroreaErakutsi(String mezua) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errorea");
        alert.setHeaderText(null);
        alert.setContentText(mezua);
        alert.showAndWait();
    }

    @FXML
    protected void irten() {
        System.exit(0);
    }
}
