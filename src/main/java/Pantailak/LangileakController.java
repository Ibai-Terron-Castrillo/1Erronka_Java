package Pantailak;

import javafx.fxml.FXML;
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

    @FXML private Button btnAdd, btnEdit, btnDelete;

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

        LangileaService.deleteLangile(selected.getId());
        refreshTable();
    }

    private void openForm(Langilea langile) {
        LangileakForm.show(langile, this::refreshTable);
    }
}