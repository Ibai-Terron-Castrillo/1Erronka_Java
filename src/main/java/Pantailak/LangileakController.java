package Pantailak;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Klaseak.Langilea;
import services.LangileaService;

public class LangileakController {

    @FXML private TableView<Langilea> tableLangileak;
    @FXML private TableColumn<Langilea, Integer> colId;
    @FXML private TableColumn<Langilea, String> colIzena;
    @FXML private TableColumn<Langilea, String> colAbizena1;
    @FXML private TableColumn<Langilea, String> colAbizena2;
    @FXML private TableColumn<Langilea, String> colTelefonoa;
    @FXML private TableColumn<Langilea, String> colLanpostua;

    @FXML private Button btnAdd, btnEdit, btnDelete, atzeraBotoia;

    @FXML
    public void initialize() {
        System.out.println("initialize() deituta");
        System.out.println("tableLangileak null da? " + (tableLangileak == null));

        if (colId != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colIzena.setCellValueFactory(new PropertyValueFactory<>("izena"));
            colAbizena1.setCellValueFactory(new PropertyValueFactory<>("abizena1"));
            colAbizena2.setCellValueFactory(new PropertyValueFactory<>("abizena2"));
            colTelefonoa.setCellValueFactory(new PropertyValueFactory<>("telefonoa"));
            colLanpostua.setCellValueFactory(new PropertyValueFactory<>("lanpostuaName"));
        } else {
            System.err.println("ERROREA: Zutabe bat edo gehiago null da");
        }

        refreshTable();

        if (btnAdd != null && btnEdit != null && btnDelete != null) {
            btnAdd.setOnAction(e -> openForm(null));
            btnEdit.setOnAction(e -> openForm(tableLangileak.getSelectionModel().getSelectedItem()));
            btnDelete.setOnAction(e -> deleteSelected());
        }
    }

    private void refreshTable() {
        if (tableLangileak == null) {
            System.err.println("ERROREA: tableLangileak null da refreshTable()-en");
            return;
        }

        try {
            java.util.List<Klaseak.Langilea> langileak = LangileaService.getAll();

            if (langileak == null) {
                System.err.println("WARNING: LangileaService.getAll() null bueltatu du");
                tableLangileak.getItems().clear();
            } else {
                tableLangileak.getItems().setAll(langileak);
                System.out.println("Taula birkargatu da " + langileak.size() + " erragistrorekin");
            }

        } catch (Exception e) {
            e.printStackTrace();
            tableLangileak.getItems().clear();
        }
    }

    private void deleteSelected() {
        if (tableLangileak == null) return;

        Langilea selected = tableLangileak.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("KONTUZ!");
        alert.setHeaderText("Ziur zaude erregistro hau ezabatu nahi duzula?");
        alert.setContentText("Betirako ezabatuko da");

        ButtonType bai = new ButtonType("Bai", ButtonBar.ButtonData.OK_DONE);
        ButtonType ez = new ButtonType("Ez", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(bai, ez);

        var result = alert.showAndWait();

        if (result.isPresent() && result.get() == bai) {
            LangileaService.deleteLangile(selected.getId());
            refreshTable();
        }
    }

    private void openForm(Langilea langile) {
        LangileakForm.show(langile, this::refreshTable);
    }

    @FXML
    public void atzeraBueltatu(ActionEvent actionEvent) {
        try {
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Menu Nagusia");
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