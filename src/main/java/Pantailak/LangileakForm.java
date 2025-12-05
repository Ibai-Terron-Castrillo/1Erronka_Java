package Pantailak;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Klaseak.Erabiltzailea;
import Klaseak.Langilea;
import Klaseak.Lanpostua;
import services.ErabiltzaileaService;
import services.LangileaService;

public class LangileakForm {

    @FXML private TextField txtIzena, txtAbizena1, txtAbizena2, txtTelefonoa;
    @FXML private ComboBox<Lanpostua> comboLanpostu;
    @FXML private CheckBox checkErabiltzaile;
    @FXML private VBox boxErabiltzaile;
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;

    private static Langilea editing;
    private static Runnable refreshCallback;

    public static void show(Langilea langile, Runnable onRefresh) {
        try {
            editing = langile;
            refreshCallback = onRefresh;

            FXMLLoader loader = new FXMLLoader(LangileakForm.class.getResource("/view/LangileakForm.fxml"));
            VBox root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(langile == null ? "Langile berria" : "Aldatu langilea");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void initialize() {
        comboLanpostu.getItems().setAll(LangileaService.getLanpostuak());

        checkErabiltzaile.selectedProperty().addListener((obs, old, val) -> {
            boxErabiltzaile.setVisible(val);
            boxErabiltzaile.setManaged(val);
        });

        if (editing != null) {
            txtIzena.setText(editing.getIzena());
            txtAbizena1.setText(editing.getAbizena1());
            txtAbizena2.setText(editing.getAbizena2());
            txtTelefonoa.setText(editing.getTelefonoa());
            comboLanpostu.setValue(editing.getLanpostua());

            // erabiltzailea kargatu
            Erabiltzailea e = ErabiltzaileaService.getByLangile(editing.getId());
            if (e != null) {
                checkErabiltzaile.setSelected(true);
                txtUser.setText(e.getErabiltzailea());
                txtPass.setText(e.getPasahitza());
            }
        }
    }

    @FXML
    private void onSave() {
        Langilea l = (editing == null ? new Langilea() : editing);

        l.setIzena(txtIzena.getText());
        l.setAbizena1(txtAbizena1.getText());
        l.setAbizena2(txtAbizena2.getText());
        l.setTelefonoa(txtTelefonoa.getText());
        l.setLanpostua(comboLanpostu.getValue());

        if (editing == null) {
            l = LangileaService.create(l);
        } else {
            LangileaService.update(l);
        }

        if (checkErabiltzaile.isSelected()) {

            Erabiltzailea er = new Erabiltzailea();
            er.setErabiltzailea(txtUser.getText());
            er.setPasahitza(txtPass.getText());
            er.setLangilea(l);

            ErabiltzaileaService.saveOrUpdate(er);

        } else {
            ErabiltzaileaService.deleteByLangile(l.getId());
        }

        refreshCallback.run();
        close();
    }

    @FXML
    private void onCancel() { close(); }

    private void close() {
        Stage stage = (Stage) txtIzena.getScene().getWindow();
        stage.close();
    }
}
