package Pantailak;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.EskaeraService;
import services.OsagaiaService;
import services.PDFSortzailea;
import Klaseak.Eskaera;
import Klaseak.EskaeraOsagaia;
import Klaseak.Osagaia;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EskaerakController {

    @FXML private TableView<Eskaera> eskaerakTable;
    @FXML private TableColumn<Eskaera, Integer> zenbakiaColumn;
    @FXML private TableColumn<Eskaera, String> dataColumn;
    @FXML private TableColumn<Eskaera, Double> totalaEskaeraColumn;
    @FXML private TableColumn<Eskaera, String> egoeraColumn;
    @FXML private TableColumn<Eskaera, String> pdfColumn;
    @FXML private TableColumn<Eskaera, Void> akzioakColumn;

    @FXML private TableView<EskaeraOsagaia> eskaeraOsagaiakTable;
    @FXML private TableColumn<EskaeraOsagaia, String> osagaiaColumn;
    @FXML private TableColumn<EskaeraOsagaia, Integer> kopuruaColumn;
    @FXML private TableColumn<EskaeraOsagaia, Double> prezioaColumn;
    @FXML private TableColumn<EskaeraOsagaia, Double> totalaColumn;
    @FXML private TableColumn<EskaeraOsagaia, Void> ezabatuColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCombo;
    @FXML private ComboBox<Osagaia> osagaiaCombo;
    @FXML private TextField eskaeraZenbakiaField;
    @FXML private TextField kopuruaField;
    @FXML private TextField prezioaField;

    @FXML private Button refreshButton, gehituButton, garbituButton, sortuButton;
    @FXML private Button ikusiButton, bukatuButton, ezabatuEskaeraButton;
    @FXML private Button atzeraBotoia;  // FXML-en dago

    @FXML private Label guztiraLabel;
    @FXML private Label kontaketaLabel;

    private final ObservableList<Eskaera> eskaeraList = FXCollections.observableArrayList();
    private final ObservableList<EskaeraOsagaia> eskaeraOsagaiakList = FXCollections.observableArrayList();
    private final ObservableList<Osagaia> osagaiaList = FXCollections.observableArrayList();
    private final DecimalFormat decimalFormat;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public EskaerakController() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,##0.00", symbols);
    }

    @FXML
    public void initialize() {
        setupTables();
        setupFilters();
        loadData();
        setupEventHandlers();
    }

    private void setupTables() {
        zenbakiaColumn.setCellValueFactory(new PropertyValueFactory<>("eskaeraZenbakia"));

        dataColumn.setCellValueFactory(cellData -> {
            Eskaera eskaera = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    dateFormat.format(eskaera.getData())
            );
        });

        totalaEskaeraColumn.setCellValueFactory(new PropertyValueFactory<>("totala"));
        totalaEskaeraColumn.setCellFactory(column -> new TableCell<Eskaera, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(decimalFormat.format(item) + " €");
                }
            }
        });

        egoeraColumn.setCellValueFactory(cellData -> {
            Eskaera eskaera = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    eskaera.isEgoera() ? "Bukatua" : "Pendiente"
            );
        });

        egoeraColumn.setCellFactory(column -> new TableCell<Eskaera, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Bukatua")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                }
            }
        });

        pdfColumn.setCellValueFactory(new PropertyValueFactory<>("eskaeraPdf"));

        akzioakColumn.setCellFactory(param -> new TableCell<Eskaera, Void>() {
            private final Button ikusiBtn = new Button("Ikusi");
            private final Button bukatuBtn = new Button("Bukatu");
            private final Button pdfBtn = new Button("PDF Ikusi");
            private final Button pdfSortuBtn = new Button("PDF Sortu");

            {
                ikusiBtn.setStyle("-fx-font-size: 11px;");
                bukatuBtn.setStyle("-fx-font-size: 11px; -fx-background-color: #27ae60; -fx-text-fill: white;");
                pdfBtn.setStyle("-fx-font-size: 11px; -fx-background-color: #3498db; -fx-text-fill: white;");
                pdfSortuBtn.setStyle("-fx-font-size: 11px; -fx-background-color: #9b59b6; -fx-text-fill: white;");

                ikusiBtn.setOnAction(event -> {
                    Eskaera eskaera = getTableView().getItems().get(getIndex());
                    showEskaeraDetails(eskaera);
                });

                bukatuBtn.setOnAction(event -> {
                    Eskaera eskaera = getTableView().getItems().get(getIndex());
                    handleBukatuEskaera(eskaera);
                });

                pdfBtn.setOnAction(event -> {
                    Eskaera eskaera = getTableView().getItems().get(getIndex());
                    handleIkusiPdf(eskaera);
                });

                pdfSortuBtn.setOnAction(event -> {
                    Eskaera eskaera = getTableView().getItems().get(getIndex());
                    handleSortuPdfEskaerarako(eskaera);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Eskaera eskaera = getTableView().getItems().get(getIndex());

                    HBox buttons = new HBox(5, ikusiBtn);

                    if (!eskaera.isEgoera()) {
                        buttons.getChildren().add(bukatuBtn);
                    }

                    // PDF sortzeko botoia beti agertu
                    buttons.getChildren().add(pdfSortuBtn);

                    setGraphic(buttons);
                }
            }
        });

        eskaerakTable.setItems(eskaeraList);

        osagaiaColumn.setCellValueFactory(new PropertyValueFactory<>("osagaiaIzena"));
        kopuruaColumn.setCellValueFactory(new PropertyValueFactory<>("kopurua"));

        prezioaColumn.setCellValueFactory(new PropertyValueFactory<>("prezioa"));
        prezioaColumn.setCellFactory(column -> new TableCell<EskaeraOsagaia, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(decimalFormat.format(item) + " €");
                }
            }
        });

        totalaColumn.setCellValueFactory(new PropertyValueFactory<>("totala"));
        totalaColumn.setCellFactory(column -> new TableCell<EskaeraOsagaia, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(decimalFormat.format(item) + " €");
                }
            }
        });

        ezabatuColumn.setCellFactory(param -> new TableCell<EskaeraOsagaia, Void>() {
            private final Button ezabatuBtn = new Button("Ezabatu");

            {
                ezabatuBtn.setStyle("-fx-font-size: 11px; -fx-background-color: #e74c3c; -fx-text-fill: white;");
                ezabatuBtn.setOnAction(event -> {
                    EskaeraOsagaia eskaeraOsagaia = getTableView().getItems().get(getIndex());
                    eskaeraOsagaiakList.remove(eskaeraOsagaia);
                    updateGuztira();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(ezabatuBtn);
                }
            }
        });

        eskaeraOsagaiakTable.setItems(eskaeraOsagaiakList);
    }

    private void setupFilters() {
        filterCombo.getItems().addAll("Guztiak", "Pendienteak", "Bukatuak");
        filterCombo.setValue("Guztiak");
        filterCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));
    }

    private void applyFilter(String filter) {
        if (filter.equals("Guztiak")) {
            loadEskaerak();
        } else if (filter.equals("Pendienteak")) {
            loadPendienteak();
        } else if (filter.equals("Bukatuak")) {
            loadBukatuak();
        }
    }

    private void loadData() {
        loadNextEskaeraZenbakia();
        loadOsagaiak();
        loadEskaerak();
    }

    private void loadNextEskaeraZenbakia() {
        new Thread(() -> {
            int nextZenbakia = EskaeraService.getNextEskaeraZenbakia();
            Platform.runLater(() -> {
                eskaeraZenbakiaField.setText(String.valueOf(nextZenbakia));
            });
        }).start();
    }

    private void loadOsagaiak() {
        new Thread(() -> {
            List<Osagaia> osagaiak = OsagaiaService.getOsagaiak();
            Platform.runLater(() -> {
                osagaiaList.clear();
                osagaiaList.addAll(osagaiak);
                osagaiaCombo.setItems(osagaiaList);
            });
        }).start();
    }

    private void loadEskaerak() {
        new Thread(() -> {
            List<Eskaera> eskaerak = EskaeraService.getEskaerak();
            Platform.runLater(() -> {
                eskaeraList.clear();
                eskaeraList.addAll(eskaerak);
                kontaketaLabel.setText(eskaeraList.size() + " eskaera");
            });
        }).start();
    }

    private void loadPendienteak() {
        new Thread(() -> {
            List<Eskaera> eskaerak = EskaeraService.getPendienteak();
            Platform.runLater(() -> {
                eskaeraList.clear();
                eskaeraList.addAll(eskaerak);
                kontaketaLabel.setText(eskaeraList.size() + " eskaera");
            });
        }).start();
    }

    private void loadBukatuak() {
        new Thread(() -> {
            List<Eskaera> eskaerak = EskaeraService.getBukatuak();
            Platform.runLater(() -> {
                eskaeraList.clear();
                eskaeraList.addAll(eskaerak);
                kontaketaLabel.setText(eskaeraList.size() + " eskaera");
            });
        }).start();
    }

    private void setupEventHandlers() {
        refreshButton.setOnAction(event -> loadData());

        osagaiaCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                prezioaField.setText(decimalFormat.format(newVal.getAzkenPrezioa()));
            }
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterBySearch(newText);
        });

        ikusiButton.setOnAction(event -> handleIkusiEskaera());
        bukatuButton.setOnAction(event -> handleBukatuSelectedEskaera());
        ezabatuEskaeraButton.setOnAction(event -> handleEzabatuEskaera());
        gehituButton.setOnAction(event -> handleGehituOsagaia());
        garbituButton.setOnAction(event -> handleGarbitu());
        sortuButton.setOnAction(event -> handleSortuEskaera());
    }

    private void filterBySearch(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            eskaerakTable.setItems(eskaeraList);
        } else {
            ObservableList<Eskaera> filtered = FXCollections.observableArrayList();
            String lowerCaseFilter = searchText.toLowerCase();

            for (Eskaera eskaera : eskaeraList) {
                if (String.valueOf(eskaera.getEskaeraZenbakia()).contains(lowerCaseFilter) ||
                        dateFormat.format(eskaera.getData()).toLowerCase().contains(lowerCaseFilter)) {
                    filtered.add(eskaera);
                }
            }

            eskaerakTable.setItems(filtered);
        }
    }

    @FXML
    private void handleGehituOsagaia() {
        try {
            Osagaia selectedOsagaia = osagaiaCombo.getSelectionModel().getSelectedItem();
            if (selectedOsagaia == null) {
                alertaErakutsi("Abisua", "Hautatu ezazu osagaia", Alert.AlertType.WARNING);
                return;
            }

            String kopuruaText = kopuruaField.getText();
            String prezioaText = prezioaField.getText().replace(',', '.');

            if (kopuruaText.isEmpty() || prezioaText.isEmpty()) {
                alertaErakutsi("Abisua", "Bete ezazu kopurua eta prezioa", Alert.AlertType.WARNING);
                return;
            }

            int kopurua = Integer.parseInt(kopuruaText);
            double prezioa = Double.parseDouble(prezioaText);

            if (kopurua <= 0 || prezioa <= 0) {
                alertaErakutsi("Abisua", "Kopurua eta prezioa positiboak izan behar dira", Alert.AlertType.WARNING);
                return;
            }

            EskaeraOsagaia eskaeraOsagaia = new EskaeraOsagaia();
            eskaeraOsagaia.setOsagaiakId(selectedOsagaia.getId());
            eskaeraOsagaia.setOsagaiaIzena(selectedOsagaia.getIzena());
            eskaeraOsagaia.setKopurua(kopurua);
            eskaeraOsagaia.setPrezioa(prezioa);
            eskaeraOsagaia.setTotala(kopurua * prezioa);

            eskaeraOsagaiakList.add(eskaeraOsagaia);
            updateGuztira();

            kopuruaField.clear();

        } catch (NumberFormatException e) {
            alertaErakutsi("Errorea", "Zenbaki baliagarriak sartu behar dira", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGarbitu() {
        eskaeraOsagaiakList.clear();
        updateGuztira();
    }

    @FXML
    private void handleSortuEskaera() {
        if (eskaeraOsagaiakList.isEmpty()) {
            alertaErakutsi("Abisua", "Gehitu ezazu gutxienez osagai bat", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Berrespena");
        confirm.setHeaderText("Eskaera sortu");
        confirm.setContentText("Ziur zaude eskaera hau sortu nahi duzula?\nPDF bat automatikoki sortuko da.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    double guztira = guztiraKalkulatu();
                    int eskaeraZenbakia = Integer.parseInt(eskaeraZenbakiaField.getText());

                    // Eskaera sortu API-n (totala = 0)
                    boolean eskaeraCreated = EskaeraService.createEskaera(eskaeraZenbakia);

                    if (eskaeraCreated) {
                        List<Eskaera> eskaerak = EskaeraService.getEskaerak();
                        int createdEskaeraId = -1;
                        Eskaera createdEskaera = null;

                        for (Eskaera e : eskaerak) {
                            if (e.getEskaeraZenbakia() == eskaeraZenbakia) {
                                createdEskaeraId = e.getId();
                                createdEskaera = e;
                                break;
                            }
                        }

                        if (createdEskaeraId > 0 && createdEskaera != null) {
                            boolean allAdded = true;

                            // Osagaiak gehitu
                            for (EskaeraOsagaia eskaeraOsagaia : eskaeraOsagaiakList) {
                                boolean osagaiaAdded = EskaeraService.addOsagaiaToEskaera(
                                        createdEskaeraId,
                                        eskaeraOsagaia.getOsagaiakId(),
                                        eskaeraOsagaia.getKopurua(),
                                        eskaeraOsagaia.getPrezioa()
                                );

                                if (!osagaiaAdded) {
                                    allAdded = false;
                                }
                            }

                            // PDF-A SORTU
                            if (allAdded) {
                                // Osagai berriak kargatu PDF-arentzat
                                List<EskaeraOsagaia> osagaiak = EskaeraService.getEskaeraOsagaiak(createdEskaeraId);

                                // Eskaera eguneratu (totala kalkulatuta)
                                createdEskaera = EskaeraService.getEskaeraById(createdEskaeraId);

                                if (createdEskaera != null && osagaiak != null) {
                                    File pdfFitxategia = PDFSortzailea.sortuEskaeraPdfZerbitzarian(createdEskaera, osagaiak);

                                    boolean finalAllAdded = allAdded;
                                    File finalPdfFitxategia = pdfFitxategia;

                                    Platform.runLater(() -> {
                                        if (finalAllAdded) {
                                            String mezua = "Eskaera ondo sortu da!";

                                            if (finalPdfFitxategia != null && finalPdfFitxategia.exists()) {
                                                mezua += "\nPDF fitxategia: " + finalPdfFitxategia.getName();

                                                // PDF-a irekitzeko aukera eman
                                                Alert pdfAlert = new Alert(Alert.AlertType.CONFIRMATION);
                                                pdfAlert.setTitle("PDF sortuta");
                                                pdfAlert.setHeaderText("Eskaeraren PDF-a ondo sortu da");
                                                pdfAlert.setContentText("PDF fitxategia ireki nahi duzu?");

                                                if (pdfAlert.showAndWait().get() == ButtonType.OK) {
                                                    PDFSortzailea.irekiPdf(finalPdfFitxategia);
                                                }
                                            }

                                            alertaErakutsi("Arrakasta", mezua, Alert.AlertType.INFORMATION);
                                            handleGarbitu();
                                            loadNextEskaeraZenbakia();
                                            loadEskaerak();
                                        } else {
                                            alertaErakutsi("Abisua", "Eskaera sortu da baina osagai batzuk ezin izan dira gehitu",
                                                    Alert.AlertType.WARNING);
                                        }
                                    });
                                }
                            } else {
                                Platform.runLater(() -> {
                                    alertaErakutsi("Abisua", "Eskaera sortu da baina osagai batzuk ezin izan dira gehitu",
                                            Alert.AlertType.WARNING);
                                });
                            }
                        } else {
                            Platform.runLater(() -> {
                                alertaErakutsi("Errorea", "Ezin izan da eskaeraren ID-a lortu", Alert.AlertType.ERROR);
                            });
                        }
                    } else {
                        Platform.runLater(() -> {
                            alertaErakutsi("Errorea", "Ezin izan da eskaera sortu", Alert.AlertType.ERROR);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        alertaErakutsi("Errorea", "Errorea gertatu da: " + e.getMessage(), Alert.AlertType.ERROR);
                    });
                }
            }).start();
        }
    }

    /**
     * Eskaera baten PDF berria sortu
     */
    private void handleSortuPdfEskaerarako(Eskaera eskaera) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("PDF sortu");
        confirm.setHeaderText("Eskaeraren PDF-a sortu");
        confirm.setContentText("Ziur zaude eskaera #" + eskaera.getEskaeraZenbakia() + "-ren PDF bat sortu nahi duzula?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    // Osagaiak kargatu
                    List<EskaeraOsagaia> osagaiak = EskaeraService.getEskaeraOsagaiak(eskaera.getId());

                    // PDF-a sortu
                    File pdfFitxategia = PDFSortzailea.sortuEskaeraPdfZerbitzarian(eskaera, osagaiak);

                    Platform.runLater(() -> {
                        if (pdfFitxategia != null && pdfFitxategia.exists()) {
                            String mezua = "PDF fitxategia ondo sortu da!\n";
                            mezua += "Izena: " + pdfFitxategia.getName() + "\n";
                            mezua += "Kokapena: " + pdfFitxategia.getParent();

                            Alert pdfAlert = new Alert(Alert.AlertType.CONFIRMATION);
                            pdfAlert.setTitle("PDF sortuta");
                            pdfAlert.setHeaderText("PDF-a ondo sortu da");
                            pdfAlert.setContentText(mezua + "\n\nPDF fitxategia ireki nahi duzu?");

                            if (pdfAlert.showAndWait().get() == ButtonType.OK) {
                                PDFSortzailea.irekiPdf(pdfFitxategia);
                            }
                        } else {
                            alertaErakutsi("Errorea", "Ezin izan da PDF fitxategia sortu", Alert.AlertType.ERROR);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        alertaErakutsi("Errorea", "Errorea PDF sortzean: " + e.getMessage(), Alert.AlertType.ERROR);
                    });
                }
            }).start();
        }
    }

    @FXML
    private void handleIkusiEskaera() {
        Eskaera selected = eskaerakTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertaErakutsi("Abisua", "Hautatu ezazu ikusi nahi duzun eskaera", Alert.AlertType.WARNING);
            return;
        }

        showEskaeraDetails(selected);
    }

    private void showEskaeraDetails(Eskaera eskaera) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Eskaera Xehetasunak");
        dialog.setHeaderText("Eskaera #" + eskaera.getEskaeraZenbakia() + " - " +
                (eskaera.isEgoera() ? "Bukatua" : "Pendiente"));

        StringBuilder content = new StringBuilder();
        content.append("Eskaera Zenbakia: ").append(eskaera.getEskaeraZenbakia()).append("\n");
        content.append("Data: ").append(dateFormat.format(eskaera.getData())).append("\n");
        content.append("Totala: ").append(decimalFormat.format(eskaera.getTotala())).append(" €\n");
        content.append("Egoera: ").append(eskaera.isEgoera() ? "Bukatua" : "Pendiente").append("\n\n");

        new Thread(() -> {
            List<EskaeraOsagaia> osagaiak = EskaeraService.getEskaeraOsagaiak(eskaera.getId());

            if (!osagaiak.isEmpty()) {
                content.append("=== O S A G A I A K ===\n");
                for (EskaeraOsagaia eo : osagaiak) {
                    content.append(String.format("- %s: %d x %s = %s\n",
                            eo.getOsagaiaIzena(),
                            eo.getKopurua(),
                            decimalFormat.format(eo.getPrezioa()),
                            decimalFormat.format(eo.getTotala())
                    ));
                }
            } else {
                content.append("Ez dago osagairik eskaera honetan\n");
            }

            Platform.runLater(() -> {
                TextArea textArea = new TextArea(content.toString());
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setPrefSize(500, 300);

                dialog.getDialogPane().setContent(textArea);
                dialog.showAndWait();
            });
        }).start();
    }

    @FXML
    private void handleBukatuSelectedEskaera() {
        Eskaera selected = eskaerakTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertaErakutsi("Abisua", "Hautatu ezazu bukatu nahi duzun eskaera", Alert.AlertType.WARNING);
            return;
        }

        handleBukatuEskaera(selected);
    }

    private void handleBukatuEskaera(Eskaera eskaera) {
        if (eskaera.isEgoera()) {
            alertaErakutsi("Abisua", "Eskaera hau dagoeneko bukatuta dago", Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Berrespena");
        confirm.setHeaderText("Eskaera bukatu");
        confirm.setContentText("Ziur zaude eskaera #" + eskaera.getEskaeraZenbakia() + " bukatu nahi duzula?\n" +
                "Honek stock-a eguneratuko du.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            new Thread(() -> {
                boolean success = EskaeraService.markAsCompleted(eskaera.getId());

                if (success) {
                    // Bukatutakoan, PDF berria sortu
                    List<EskaeraOsagaia> osagaiak = EskaeraService.getEskaeraOsagaiak(eskaera.getId());
                    Eskaera eguneratutakoEskaera = EskaeraService.getEskaeraById(eskaera.getId());

                    if (eguneratutakoEskaera != null) {
                        PDFSortzailea.sortuEskaeraPdfZerbitzarian(eguneratutakoEskaera, osagaiak);
                    }
                }

                Platform.runLater(() -> {
                    if (success) {
                        alertaErakutsi("Arrakasta", "Eskaera ondo bukatu da!\nPDF berria sortu da.", Alert.AlertType.INFORMATION);
                        loadEskaerak();
                    } else {
                        alertaErakutsi("Errorea", "Ezin izan da eskaera bukatu", Alert.AlertType.ERROR);
                    }
                });
            }).start();
        }
    }

    @FXML
    private void handleEzabatuEskaera() {
        Eskaera selected = eskaerakTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertaErakutsi("Abisua", "Hautatu ezazu ezabatu nahi duzun eskaera", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Berrespena");
        confirm.setHeaderText("Eskaera ezabatu");
        confirm.setContentText("Ziur zaude eskaera #" + selected.getEskaeraZenbakia() + " ezabatu nahi duzula?\n" +
                "Ekintza hau ezin da desegin.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            new Thread(() -> {
                boolean success = EskaeraService.deleteEskaera(selected.getId());
                Platform.runLater(() -> {
                    if (success) {
                        alertaErakutsi("Arrakasta", "Eskaera ondo ezabatuta!", Alert.AlertType.INFORMATION);
                        loadEskaerak();
                    } else {
                        alertaErakutsi("Errorea", "Ezin izan da eskaera ezabatu", Alert.AlertType.ERROR);
                    }
                });
            }).start();
        }
    }

    private void handleIkusiPdf(Eskaera eskaera) {
        if (eskaera.getEskaeraPdf() == null || eskaera.getEskaeraPdf().isEmpty()) {
            alertaErakutsi("Abisua", "Ez dago PDF fitxategirik eskaera honetarako", Alert.AlertType.INFORMATION);
            return;
        }

        alertaErakutsi("PDF", "PDF fitxategia: " + eskaera.getEskaeraPdf(), Alert.AlertType.INFORMATION);
    }

    private double guztiraKalkulatu() {
        return eskaeraOsagaiakList.stream()
                .mapToDouble(EskaeraOsagaia::getTotala)
                .sum();
    }

    private void updateGuztira() {
        double guztira = guztiraKalkulatu();
        guztiraLabel.setText(decimalFormat.format(guztira) + " €");
    }

    private void alertaErakutsi(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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