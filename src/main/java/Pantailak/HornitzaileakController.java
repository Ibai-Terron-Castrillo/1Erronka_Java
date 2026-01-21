package Pantailak;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Klaseak.Hornitzailea;
import Klaseak.Osagaia;
import services.HornitzaileaService;
import services.OsagaiaService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HornitzaileakController {

    // Hornitzaileak table
    @FXML private TableView<Hornitzailea> tableHornitzaileak;
    @FXML private TableColumn<Hornitzailea, Integer> colHornitzaileId;
    @FXML private TableColumn<Hornitzailea, String> colHornitzaileIzena;
    @FXML private TableColumn<Hornitzailea, String> colHornitzaileCif;
    @FXML private TableColumn<Hornitzailea, String> colHornitzaileSektorea;
    @FXML private TableColumn<Hornitzailea, String> colHornitzaileHelbidea;
    @FXML private TableColumn<Hornitzailea, String> colHornitzaileTelefonoa;
    @FXML private TableColumn<Hornitzailea, String> colHornitzaileEmail;

    // Hornitzailea formularioa
    @FXML private TextField txtIzena, txtCif, txtSektorea, txtTelefonoa, txtEmail, txtHelbidea;
    @FXML private Button btnSaveHornitzailea, btnCancelHornitzailea;

    // Hornitzailearen osagaiak table
    @FXML private TableView<Osagaia> tableHornitzailearenOsagaiak;
    @FXML private TableColumn<Osagaia, String> colOsagaiIzena;
    @FXML private TableColumn<Osagaia, Double> colOsagaiPrezioa;
    @FXML private TableColumn<Osagaia, Integer> colOsagaiStock;
    @FXML private TableColumn<Osagaia, Boolean> colOsagaiEskatu;

    // Osagaiak gehitzeko combo
    @FXML private ComboBox<Osagaia> comboOsagaiak;
    @FXML private Button btnGehituOsagaia, btnKenduOsagaia;

    // Botoiak eta kontrolak
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterOrdenatu;
    @FXML private Button btnAddHornitzailea, btnDeleteHornitzailea, atzeraBotoia, refreshButton;

    // Estatistikak
    @FXML private Label hornitzaileKopuruaLabel, osagaiKopuruaLabel;
    @FXML private Label totalHornitzaileakLabel, hornitzaileBerriakLabel;
    @FXML private Label osagaiGehienLabel, osagaiGutxienLabel;
    @FXML private Label dataLabel, orduaLabel;

    private ObservableList<Hornitzailea> hornitzaileakLista;
    private ObservableList<Osagaia> hornitzailearenOsagaiakLista;
    private ObservableList<Osagaia> osagaiakDisponibleLista;
    private FilteredList<Hornitzailea> filteredHornitzaileak;

    private Hornitzailea hornitzaileaEditatzen;

    private HornitzaileaService hornitzaileaService;
    private OsagaiaService osagaiaService;
    private static final Logger LOGGER = Logger.getLogger(HornitzaileakController.class.getName());

    @FXML
    public void initialize() {
        try {
            LOGGER.info("HornitzaileakController inicializando...");

            // Zerbitzuak hasieratu
            hornitzaileaService = new HornitzaileaService();
            osagaiaService = new OsagaiaService();

            // Listak hasieratu
            hornitzaileakLista = FXCollections.observableArrayList();
            hornitzailearenOsagaiakLista = FXCollections.observableArrayList();
            osagaiakDisponibleLista = FXCollections.observableArrayList();

            // Hornitzaileak table konfiguratu
            colHornitzaileId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colHornitzaileIzena.setCellValueFactory(new PropertyValueFactory<>("izena"));
            colHornitzaileCif.setCellValueFactory(new PropertyValueFactory<>("cif"));
            colHornitzaileSektorea.setCellValueFactory(new PropertyValueFactory<>("sektorea"));
            colHornitzaileHelbidea.setCellValueFactory(new PropertyValueFactory<>("helbidea"));
            colHornitzaileTelefonoa.setCellValueFactory(new PropertyValueFactory<>("telefonoa"));
            colHornitzaileEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

            // Hornitzailearen osagaiak table konfiguratu
            colOsagaiIzena.setCellValueFactory(new PropertyValueFactory<>("izena"));
            colOsagaiPrezioa.setCellValueFactory(new PropertyValueFactory<>("azkenPrezioa"));
            colOsagaiStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            colOsagaiEskatu.setCellValueFactory(new PropertyValueFactory<>("eskatu"));

            // Combo konfiguratu
            comboOsagaiak.setItems(osagaiakDisponibleLista);

            // Tablak hasieratu
            tableHornitzaileak.setItems(hornitzaileakLista);
            tableHornitzailearenOsagaiak.setItems(hornitzailearenOsagaiakLista);

            // Formularioak konfiguratu
            formularioakKonfiguratu();

            // Filtroak konfiguratu
            filtroakKonfiguratu();

            // Datuak kargatu
            datuakKargatu();

            // Bilaketa konfiguratu
            bilaketaKonfiguratu();

            // Botoiak konfiguratu
            botoiakKonfiguratu();

            // Taula aukerak
            taulaAukerak();

            // Formularioak garbitu
            formularioHornitzaileaGarbitu();

            // Data eta ordua eguneratu
            eguneratuDataOrdua();

            LOGGER.info("HornitzaileakController inicializado correctamente");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errorea initialize-n: " + e.getMessage(), e);
            mostrarError("Errorea pantaila kargatzean: " + e.getMessage());
        }
    }

    private void formularioakKonfiguratu() {
        // Hornitzailea formulario botoiak
        btnSaveHornitzailea.setOnAction(e -> {
            try {
                hornitzaileaGorde();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Errorea hornitzailea gordetzean: " + ex.getMessage(), ex);
                mostrarError("Errorea hornitzailea gordetzean: " + ex.getMessage());
            }
        });

        btnCancelHornitzailea.setOnAction(e -> formularioHornitzaileaGarbitu());
    }

    private void filtroakKonfiguratu() {
        // Solo mantener filtro de ordenación
        filterOrdenatu.getItems().addAll("ID", "Izena", "Sektorea", "CIF");
        filterOrdenatu.setValue("ID");

        // Solo el listener de ordenación
        filterOrdenatu.setOnAction(e -> ordenaAplikatu());
    }

    private void bilaketaKonfiguratu() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filtroakAplikatu();
            });
        }
    }

    private void botoiakKonfiguratu() {
        // Hornitzaile botoiak
        btnAddHornitzailea.setOnAction(e -> {
            formularioHornitzaileaGarbitu();
            hornitzaileaEditatzen = null;
        });

        btnDeleteHornitzailea.setOnAction(e -> {
            try {
                deleteHornitzailea();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Errorea hornitzailea ezabatzean: " + ex.getMessage(), ex);
                mostrarError("Errorea hornitzailea ezabatzean: " + ex.getMessage());
            }
        });

        // Osagai botoiak
        btnGehituOsagaia.setOnAction(e -> {
            try {
                gehituOsagaiaHornitzaileari();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Errorea osagaia gehitzean: " + ex.getMessage(), ex);
                mostrarError("Errorea osagaia gehitzean: " + ex.getMessage());
            }
        });

        btnKenduOsagaia.setOnAction(e -> {
            try {
                kenduOsagaiaHornitzaileatik();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Errorea osagaia kentzean: " + ex.getMessage(), ex);
                mostrarError("Errorea osagaia kentzean: " + ex.getMessage());
            }
        });

        // Refresh botoia
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> {
                try {
                    datuakKargatu();
                    eguneratuDataOrdua();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Errorea datuak freskatzean: " + ex.getMessage(), ex);
                    mostrarError("Errorea datuak freskatzean: " + ex.getMessage());
                }
            });
        }
    }

    private void taulaAukerak() {
        tableHornitzaileak.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    try {
                        if (newSelection != null && newSelection.getId() > 0) {
                            kargatuFormularioaHornitzailea(newSelection);
                            kargatuHornitzailearenOsagaiak(newSelection.getId());
                            kargatuOsagaiakCombo();

                            btnGehituOsagaia.setDisable(false);
                            btnKenduOsagaia.setDisable(false);
                        } else {
                            if (newSelection != null && newSelection.getId() == 0) {
                                tableHornitzaileak.getSelectionModel().clearSelection();
                            }
                            btnGehituOsagaia.setDisable(true);
                            btnKenduOsagaia.setDisable(true);
                            hornitzailearenOsagaiakLista.clear();
                            osagaiakDisponibleLista.clear();
                            comboOsagaiak.getSelectionModel().clearSelection();
                            osagaiKopuruaLabel.setText("0 osagai");
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Errorea hornitzailea aukeratzean: " + e.getMessage(), e);
                    }
                });

        // Hornitzailearen osagaia aukeratzean
        tableHornitzailearenOsagaiak.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    btnKenduOsagaia.setDisable(newSelection == null);
                });
    }

    private void kargatuFormularioaHornitzailea(Hornitzailea hornitzailea) {
        if (hornitzailea == null) return;

        try {
            hornitzaileaEditatzen = hornitzailea;

            txtIzena.setText(hornitzailea.getIzena() != null ? hornitzailea.getIzena() : "");
            txtCif.setText(hornitzailea.getCif() != null ? hornitzailea.getCif() : "");
            txtSektorea.setText(hornitzailea.getSektorea() != null ? hornitzailea.getSektorea() : "");
            txtTelefonoa.setText(hornitzailea.getTelefonoa() != null ? hornitzailea.getTelefonoa() : "");
            txtEmail.setText(hornitzailea.getEmail() != null ? hornitzailea.getEmail() : "");
            txtHelbidea.setText(hornitzailea.getHelbidea() != null ? hornitzailea.getHelbidea() : "");

            LOGGER.log(Level.INFO, "Formularioa kargatuta: {0} (ID: {1})",
                    new Object[]{hornitzailea.getIzena(), hornitzailea.getId()});

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errorea formularioa kargatzean: " + e.getMessage(), e);
        }
    }

    private void kargatuHornitzailearenOsagaiak(int hornitzaileaId) {
        if (hornitzaileaId <= 0) {
            hornitzailearenOsagaiakLista.clear();
            osagaiKopuruaLabel.setText("0 osagai");
            return;
        }

        try {
            LOGGER.log(Level.INFO, "Kargatzen hornitzailearen osagaiak ID: {0}", hornitzaileaId);

            List<Osagaia> osagaiak = hornitzaileaService.getOsagaiakByHornitzailea(hornitzaileaId);

            hornitzailearenOsagaiakLista.setAll(osagaiak);
            osagaiKopuruaLabel.setText(osagaiak.size() + " osagai");

            LOGGER.log(Level.INFO, "Osagaiak kargatuta: {0} osagai", osagaiak.size());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errorea osagaiak kargatzean: " + e.getMessage(), e);
            hornitzailearenOsagaiakLista.clear();
            osagaiKopuruaLabel.setText("0 osagai");
            mostrarError("Errorea osagaiak kargatzean: " + e.getMessage());
        }
    }

    private void kargatuOsagaiakCombo() {
        Hornitzailea selected = tableHornitzaileak.getSelectionModel().getSelectedItem();

        if (selected == null || selected.getId() <= 0) {
            osagaiakDisponibleLista.clear();
            comboOsagaiak.getSelectionModel().clearSelection();
            return;
        }

        try {
            LOGGER.log(Level.INFO, "Kargatzen osagaiak combo-ra ID: {0}", selected.getId());

            // Osagai guztiak kargatu
            List<Osagaia> osagaiak = osagaiaService.getOsagaiak();

            // Hornitzaileak dituen osagaiak lortu
            List<Osagaia> hornitzailearenOsagaiak = hornitzaileaService.getOsagaiakByHornitzailea(selected.getId());

            // Hornitzaileak dituen osagaiak kendu aukeretatik
            osagaiak.removeAll(hornitzailearenOsagaiak);

            osagaiakDisponibleLista.setAll(osagaiak);

            if (!osagaiak.isEmpty()) {
                comboOsagaiak.setValue(osagaiak.get(0));
            } else {
                comboOsagaiak.getSelectionModel().clearSelection();
            }

            LOGGER.log(Level.INFO, "Combo kargatuta: {0} osagai disponible", osagaiak.size());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errorea combo kargatzean: " + e.getMessage(), e);
            osagaiakDisponibleLista.clear();
            comboOsagaiak.getSelectionModel().clearSelection();
        }
    }

    private void formularioHornitzaileaGarbitu() {
        try {
            hornitzaileaEditatzen = null;

            txtIzena.clear();
            txtCif.clear();
            txtSektorea.clear();
            txtTelefonoa.clear();
            txtEmail.clear();
            txtHelbidea.clear();

            tableHornitzaileak.getSelectionModel().clearSelection();

            // Hornitzailearen osagaiak garbitu
            hornitzailearenOsagaiakLista.clear();

            // Combo garbitu
            osagaiakDisponibleLista.clear();
            comboOsagaiak.getSelectionModel().clearSelection();

            // Botoiak desgaitu
            btnGehituOsagaia.setDisable(true);
            btnKenduOsagaia.setDisable(true);

            // Etiketa garbitu
            osagaiKopuruaLabel.setText("0 osagai");

            LOGGER.info("Formularioa garbitu da");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errorea formularioa garbitzean: " + e.getMessage(), e);
        }
    }

    private void hornitzaileaGorde() {
        try {
            // Balidazioak
            if (txtIzena.getText().isBlank()) {
                alertaErakutsi("Izena jarri behar da.");
                return;
            }

            if (txtCif.getText().isBlank()) {
                alertaErakutsi("CIF jarri behar da.");
                return;
            }

            if (txtTelefonoa.getText().isBlank()) {
                alertaErakutsi("Telefonoa jarri behar da.");
                return;
            }

            if (txtEmail.getText().isBlank()) {
                alertaErakutsi("Emaila jarri behar da.");
                return;
            }

            if (!txtEmail.getText().contains("@")) {
                alertaErakutsi("Email baliogabea.");
                return;
            }

            Hornitzailea hornitzailea = (hornitzaileaEditatzen == null ? new Hornitzailea() : hornitzaileaEditatzen);

            hornitzailea.setIzena(txtIzena.getText());
            hornitzailea.setCif(txtCif.getText());
            hornitzailea.setSektorea(txtSektorea.getText());
            hornitzailea.setTelefonoa(txtTelefonoa.getText());
            hornitzailea.setEmail(txtEmail.getText());
            hornitzailea.setHelbidea(txtHelbidea.getText());

            boolean success;
            if (hornitzaileaEditatzen == null) {
                LOGGER.info("Hornitzailea berria sortzen...");
                success = hornitzaileaService.createHornitzailea(hornitzailea);
            } else {
                LOGGER.log(Level.INFO, "Hornitzailea eguneratzen ID: {0}", hornitzailea.getId());
                success = hornitzaileaService.updateHornitzailea(hornitzailea);
            }

            if (success) {
                datuakKargatu();
                formularioHornitzaileaGarbitu();
                eguneratuDataOrdua();
                arrakastaErakutsi("Hornitzailea ondo gorde da.");
            } else {
                alertaErakutsi("Errorea hornitzailea gordetzerakoan.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errorea hornitzailea gordetzean: " + e.getMessage(), e);
            alertaErakutsi("Errorea hornitzailea gordetzerakoan: " + e.getMessage());
        }
    }

    private void filtroakAplikatu() {
        if (filteredHornitzaileak == null) return;

        String searchText = searchField.getText().toLowerCase();

        filteredHornitzaileak.setPredicate(hornitzailea -> {
            if (hornitzailea == null) return false;

            boolean matchesSearch = searchText.isEmpty() ||
                    (hornitzailea.getIzena() != null && hornitzailea.getIzena().toLowerCase().contains(searchText)) ||
                    (hornitzailea.getCif() != null && hornitzailea.getCif().toLowerCase().contains(searchText)) ||
                    (hornitzailea.getSektorea() != null && hornitzailea.getSektorea().toLowerCase().contains(searchText)) ||
                    (hornitzailea.getEmail() != null && hornitzailea.getEmail().toLowerCase().contains(searchText)) ||
                    (hornitzailea.getHelbidea() != null && hornitzailea.getHelbidea().toLowerCase().contains(searchText));

            return matchesSearch; // Solo filtro por búsqueda
        });

        hornitzaileKopuruaLabel.setText(filteredHornitzaileak.size() + " hornitzaile");
    }

    private void ordenaAplikatu() {
        if (filteredHornitzaileak == null) return;

        String orden = filterOrdenatu.getValue();

        Comparator<Hornitzailea> comparator = switch (orden) {
            case "Izena" -> Comparator.comparing(h -> h.getIzena() != null ? h.getIzena() : "");
            case "Sektorea" -> Comparator.comparing(h -> h.getSektorea() != null ? h.getSektorea() : "");
            case "CIF" -> Comparator.comparing(h -> h.getCif() != null ? h.getCif() : "");
            default -> Comparator.comparing(Hornitzailea::getId);
        };

        SortedList<Hornitzailea> sortedData = new SortedList<>(filteredHornitzaileak);
        sortedData.setComparator(comparator);
        tableHornitzaileak.setItems(sortedData);
    }

    private void datuakKargatu() {
        try {
            LOGGER.info("Datuak kargatzen...");

            List<Hornitzailea> hornitzaileak = hornitzaileaService.getHornitzaileak();

            if (hornitzaileak != null) {
                List<Hornitzailea> hornitzaileakValidoak = hornitzaileak.stream()
                        .filter(h -> h != null && h.getId() > 0)
                        .collect(Collectors.toList());

                hornitzaileakLista.setAll(hornitzaileakValidoak);
                filteredHornitzaileak = new FilteredList<>(hornitzaileakLista);
                ordenaAplikatu();

                LOGGER.log(Level.INFO, "Hornitzaileak kargatuta: {0} erregistro valido", hornitzaileakValidoak.size());
                LOGGER.log(Level.INFO, "Descartados: {0} registros nulos o ID=0",
                        hornitzaileak.size() - hornitzaileakValidoak.size());
            } else {
                hornitzaileakLista.clear();
                filteredHornitzaileak = new FilteredList<>(hornitzaileakLista);
                tableHornitzaileak.setItems(filteredHornitzaileak);
                LOGGER.warning("Hornitzaileak null bueltatu du");
            }

            estatistikakEguneratu();

            LOGGER.info("Datuak kargatu dira");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errorea datuak kargatzean: " + e.getMessage(), e);
            hornitzaileakLista.clear();
            filteredHornitzaileak = new FilteredList<>(hornitzaileakLista);
            tableHornitzaileak.setItems(filteredHornitzaileak);
            mostrarError("Errorea datuak kargatzean: " + e.getMessage());
        }
    }

    private void estatistikakEguneratu() {
        if (hornitzaileakLista == null || hornitzaileakLista.isEmpty()) {
            totalHornitzaileakLabel.setText("0");
            hornitzaileKopuruaLabel.setText("0 hornitzaile");
            hornitzaileBerriakLabel.setText("0");
            osagaiGehienLabel.setText("-");
            osagaiGutxienLabel.setText("-");
            return;
        }

        // Hornitzaile estatistikak
        int totalHornitzaileak = hornitzaileakLista.size();
        totalHornitzaileakLabel.setText(String.valueOf(totalHornitzaileak));
        hornitzaileKopuruaLabel.setText(totalHornitzaileak + " hornitzaile");

        // Azken 7 egunetako hornitzaile berriak (simulatu)
        int hornitzaileBerriak = 0; // Simulatu
        hornitzaileBerriakLabel.setText(String.valueOf(hornitzaileBerriak));

        // Osagai estatistikak (gehien eta gutxien osagai dituzten hornitzaileak)
        Hornitzailea gehienOsagaiak = null;
        Hornitzailea gutxienOsagaiak = null;
        int maxOsagaiak = -1;
        int minOsagaiak = Integer.MAX_VALUE;

        for (Hornitzailea hornitzailea : hornitzaileakLista) {
            try {
                int osagaiKopurua = hornitzaileaService.getOsagaiakByHornitzailea(hornitzailea.getId()).size();

                if (osagaiKopurua > maxOsagaiak) {
                    maxOsagaiak = osagaiKopurua;
                    gehienOsagaiak = hornitzailea;
                }

                if (osagaiKopurua < minOsagaiak) {
                    minOsagaiak = osagaiKopurua;
                    gutxienOsagaiak = hornitzailea;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Errorea osagaiak kontatzean ID: {0}", hornitzailea.getId());
            }
        }

        if (gehienOsagaiak != null && maxOsagaiak > 0) {
            osagaiGehienLabel.setText(gehienOsagaiak.getIzena() + " (" + maxOsagaiak + ")");
        } else {
            osagaiGehienLabel.setText("-");
        }

        if (gutxienOsagaiak != null) {
            osagaiGutxienLabel.setText(gutxienOsagaiak.getIzena() + " (" + minOsagaiak + ")");
        } else {
            osagaiGutxienLabel.setText("-");
        }
    }

    private void deleteHornitzailea() {
        Hornitzailea selected = tableHornitzaileak.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertaErakutsi("Aukeratu hornitzaile bat ezabatzeko.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("KONTUZ!");
        alert.setHeaderText("Ziur zaude hornitzaile hau ezabatu nahi duzula?");
        alert.setContentText(selected.getIzena() + " (" + selected.getCif() + ") betirako ezabatuko da");

        ButtonType bai = new ButtonType("Bai", ButtonBar.ButtonData.OK_DONE);
        ButtonType ez = new ButtonType("Ez", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(bai, ez);

        var result = alert.showAndWait();

        if (result.isPresent() && result.get() == bai) {
            try {
                LOGGER.log(Level.INFO, "Hornitzailea ezabatzen ID: {0}", selected.getId());
                boolean success = hornitzaileaService.deleteHornitzailea(selected.getId());
                if (success) {
                    datuakKargatu();
                    formularioHornitzaileaGarbitu();
                    arrakastaErakutsi("Hornitzailea ondo ezabatu da.");
                } else {
                    alertaErakutsi("Ezin izan da hornitzailea ezabatu. Agian osagaiekin dago erlazionatuta.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errorea hornitzailea ezabatzean: " + e.getMessage(), e);
                alertaErakutsi("Errorea hornitzailea ezabatzean: " + e.getMessage());
            }
        }
    }

    private void gehituOsagaiaHornitzaileari() {
        Hornitzailea hornitzailea = tableHornitzaileak.getSelectionModel().getSelectedItem();
        Osagaia osagaia = comboOsagaiak.getValue();

        if (hornitzailea == null || osagaia == null) {
            alertaErakutsi("Aukeratu hornitzaile bat eta osagai bat.");
            return;
        }

        try {
            LOGGER.log(Level.INFO, "Osagaia gehitzen hornitzaileari - Hornitzailea ID: {0}, Osagaia ID: {1}",
                    new Object[]{hornitzailea.getId(), osagaia.getId()});

            boolean success = hornitzaileaService.addOsagaiaToHornitzailea(
                    hornitzailea.getId(), osagaia.getId());

            if (success) {
                kargatuHornitzailearenOsagaiak(hornitzailea.getId());
                kargatuOsagaiakCombo();
                arrakastaErakutsi("Osagaia hornitzaileari gehitu zaio.");
            } else {
                alertaErakutsi("Errorea osagaia gehitzean. Agian dagoeneko dago erlazionatuta.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errorea osagaia gehitzean: " + e.getMessage(), e);
            alertaErakutsi("Errorea osagaia gehitzean: " + e.getMessage());
        }
    }

    private void kenduOsagaiaHornitzaileatik() {
        Hornitzailea hornitzailea = tableHornitzaileak.getSelectionModel().getSelectedItem();
        Osagaia osagaia = tableHornitzailearenOsagaiak.getSelectionModel().getSelectedItem();

        if (hornitzailea == null || osagaia == null) {
            alertaErakutsi("Aukeratu hornitzaile bat eta kendu nahi duzun osagaia.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Kontuz!");
        alert.setHeaderText("Ziur zaude osagai hau kendu nahi duzula?");
        alert.setContentText(osagaia.getIzena() + " hornitzaile honetatik kenduko da");

        ButtonType bai = new ButtonType("Bai", ButtonBar.ButtonData.OK_DONE);
        ButtonType ez = new ButtonType("Ez", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(bai, ez);

        var result = alert.showAndWait();

        if (result.isPresent() && result.get() == bai) {
            try {
                LOGGER.log(Level.INFO, "Osagaia kentzen hornitzailetik - Hornitzailea ID: {0}, Osagaia ID: {1}",
                        new Object[]{hornitzailea.getId(), osagaia.getId()});

                boolean success = hornitzaileaService.removeOsagaiaFromHornitzailea(
                        hornitzailea.getId(), osagaia.getId());

                if (success) {
                    kargatuHornitzailearenOsagaiak(hornitzailea.getId());
                    kargatuOsagaiakCombo();
                    arrakastaErakutsi("Osagaia hornitzaileatik kendu da.");
                } else {
                    alertaErakutsi("Errorea osagaia kentzean.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errorea osagaia kentzean: " + e.getMessage(), e);
                alertaErakutsi("Errorea osagaia kentzean: " + e.getMessage());
            }
        }
    }

    private void eguneratuDataOrdua() {
        try {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter dateFormatter =
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            java.time.format.DateTimeFormatter timeFormatter =
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

            dataLabel.setText(now.format(dateFormatter));
            orduaLabel.setText(now.format(timeFormatter));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Errorea data/ordua eguneratzean: " + e.getMessage(), e);
        }
    }

    private void alertaErakutsi(String mezua) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Abisua");
        alert.setHeaderText(null);
        alert.setContentText(mezua);
        alert.showAndWait();
    }

    private void arrakastaErakutsi(String mezua) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ondo");
        alert.setHeaderText(null);
        alert.setContentText(mezua);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errorea");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    @FXML
    public void atzeraBueltatu(ActionEvent actionEvent) {
        try {
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            StageManager.switchStage(
                    currentStage,
                    "menu-view.fxml",
                    "Menu Nagusia",
                    true
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}