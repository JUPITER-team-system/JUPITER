package com.management.jupiter.fx.controllers;

import com.management.jupiter.controllers.TlController;
import com.management.jupiter.controllers.CellController;
import com.management.jupiter.controllers.InformationController;
import com.management.jupiter.controllers.ClanController;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Cell;
import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Information;
import com.management.jupiter.models.Coder;
import com.management.jupiter.security.LoginSession;
import com.management.jupiter.fx.JupiterFXApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;

import java.util.List;
import java.util.Map;

public class TlDashboardController {
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private TabPane mainTabPane;
    
    @FXML
    private Tab cellsTab;
    
    @FXML
    private Tab informationTab;
    
    @FXML
    private Tab teamTab;
    
    @FXML
    private TableView<Cell> cellsTable;
    
    @FXML
    private TableColumn<Cell, String> cellIdColumn;
    
    @FXML
    private TableColumn<Cell, String> cellNameColumn;
    
    @FXML
    private TableColumn<Cell, String> cellTypeColumn;
    
    @FXML
    private TableColumn<Cell, String> cellStatusColumn;
    
    @FXML
    private TableView<Information> informationTable;
    
    @FXML
    private TableColumn<Information, String> infoIdColumn;
    
    @FXML
    private TableColumn<Information, String> infoTitleColumn;
    
    @FXML
    private TableColumn<Information, String> infoTypeColumn;
    
    @FXML
    private TableColumn<Information, String> infoDateColumn;
    
    @FXML
    private TableColumn<Information, String> infoMessageColumn;
    
    @FXML
    private TableColumn<Information, Void> infoActionsColumn;
    
    @FXML
    private Button createCellButton;
    
    @FXML
    private Button createInfoButton;
    
    @FXML
    private Button deleteCellButton;
    
    @FXML
    private Button deleteInfoButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private VBox teamInfoBox;
    
    @FXML
    private Label teamSizeLabel;
    
    @FXML
    private Label clanNameLabel;
    
    @FXML
    private Label totalCellsLabel;
    
    @FXML
    private Label totalInfoLabel;
    
    // Coders tab
    @FXML
    private Tab codersTab;
    
    @FXML
    private ComboBox<Clan> coderClanFilterCombo;
    
    @FXML
    private Button filterCodersButton;
    
    @FXML
    private Button showAllCodersButton;
    
    @FXML
    private TreeView<String> codersTreeView;
    
    private Tl currentUser;
    private LoginSession loginSession;
    private TlController tlController;
    private CellController cellController;
    private InformationController informationController;
    private ClanController clanController;

    /**
     * Returns the first clan of the TL (Tl stores clans in a List, not via getClan_id()).
     * getClan_id() always returns null for Tl because Tl passes null to super().
     */
    private com.management.jupiter.models.Clan getCurrentClan() {
        if (currentUser.getClans() != null && !currentUser.getClans().isEmpty()) {
            return currentUser.getClans().get(0);
        }
        return null;
    }
    
    public void setUserData(Tl user, LoginSession loginSession, TlController tlController, 
                          CellController cellController, InformationController informationController, ClanController clanController) {
        this.currentUser = user;
        this.loginSession = loginSession;
        this.tlController = tlController;
        this.cellController = cellController;
        this.informationController = informationController;
        this.clanController = clanController;
    }
    
    @FXML
    public void initialize() {
        setupTables();
        setupEventHandlers();
    }
    
    public void initializeDashboard() {
        welcomeLabel.setText("Bienvenido, " + currentUser.getUsername() + " (Team Leader)");
        loadCells();
        loadInformation();
        loadTeamInfo();
        setupCodersTab();
    }
    
    private void setupTables() {
        // Cells table setup - show sequential number as ID (autoincremental from 1)
        cellIdColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                int index = cellsTable.getItems().indexOf(cellData.getValue()) + 1;
                return String.valueOf(index);
            }));
        cellNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cellTypeColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() -> "Célula"));
        cellStatusColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                "Activa")); // Default status since Cell doesn't have isActive method
        
        // Information table setup
        infoIdColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                Information info = cellData.getValue();
                return info != null && info.getId() != null ? info.getId().substring(0, 8) + "..." : "N/A";
            }));
        infoTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        infoTypeColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                Information info = cellData.getValue();
                return info != null ? "Mensaje" : "N/A";
            }));
        infoDateColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                Information info = cellData.getValue();
                if (info != null && info.getCreatedAt() != null) {
                    return info.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                }
                return "N/A";
            }));
        
        // Message column setup - show truncated message
        infoMessageColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                Information info = cellData.getValue();
                if (info != null && info.getMessage() != null) {
                    String message = info.getMessage();
                    if (message.length() > 50) {
                        return message.substring(0, 50) + "...";
                    }
                    return message;
                }
                return "N/A";
            }));
        
        // Actions column setup - add view details button
        infoActionsColumn.setCellFactory(param -> new javafx.scene.control.TableCell<Information, Void>() {
            private final javafx.scene.control.Button viewButton = new javafx.scene.control.Button("Ver");
            
            {
                viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 3;");
                viewButton.setOnAction(event -> {
                    Information info = getTableView().getItems().get(getIndex());
                    showInformationDetails(info);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
    }
    
    private void setupEventHandlers() {
        createCellButton.setOnAction(event -> showCreateCellDialog());
        createInfoButton.setOnAction(event -> showCreateInfoDialog());
        deleteCellButton.setOnAction(event -> deleteCell());
        deleteInfoButton.setOnAction(event -> deleteInformation());
        refreshButton.setOnAction(event -> refreshData());
        logoutButton.setOnAction(event -> handleLogout());
        
        // Coders tab
        if (filterCodersButton != null) filterCodersButton.setOnAction(event -> loadCodersTree(coderClanFilterCombo.getValue()));
        if (showAllCodersButton != null) showAllCodersButton.setOnAction(event -> { if (coderClanFilterCombo != null) coderClanFilterCombo.setValue(null); loadCodersTree(null); });
    }
    
    private void loadCells() {
        try {
            // Load cells for the TL's clan
            List<Cell> cells = cellController.getCellsByClan(getCurrentClan());
            ObservableList<Cell> cellObservableList = FXCollections.observableArrayList(cells);
            cellsTable.setItems(cellObservableList);
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar las células: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void loadInformation() {
        try {
            // Since getAllInformation doesn't exist, we'll load information for the user's clan
            List<Information> information = informationController.findByUserClan(currentUser);
            ObservableList<Information> infoObservableList = FXCollections.observableArrayList(information);
            informationTable.setItems(infoObservableList);
        } catch (Exception e) {
            showAlert("Error", "No se pudo cargar la información: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void loadTeamInfo() {
        try {
            // Load team statistics
            List<Cell> cells = cellController.getCellsByClan(getCurrentClan());
            List<Information> information = informationController.findByUserClan(currentUser);
            Map<String, List<Coder>> codersByCell = cellController.getCodersGroupedByCell(getCurrentClan());
            
            int totalCells = cells.size();
            int totalInfo = information.size();
            int teamSize = codersByCell.values().stream().mapToInt(List::size).sum();
            String clanName = getCurrentClan() != null ? getCurrentClan().getName() : "Sin Clan";
            
            teamSizeLabel.setText(String.valueOf(teamSize));
            clanNameLabel.setText(clanName);
            totalCellsLabel.setText(String.valueOf(totalCells));
            totalInfoLabel.setText(String.valueOf(totalInfo));
            
        } catch (Exception e) {
            showAlert("Error", "No se pudo cargar la información del equipo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showCreateCellDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Crear Células");
        dialog.setHeaderText("Utilice IA para generar células automáticamente");
        
        ButtonType createButtonType = new ButtonType("Generar Células", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Quantity field
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
        quantitySpinner.setEditable(true);
        
        // Theme field
        TextField themeField = new TextField();
        themeField.setPromptText("Ej: Desarrollo Java, Testing QA, Diseño UX");
        
        // Clan selection field
        ComboBox<com.management.jupiter.models.Clan> clanComboBox = new ComboBox<>();
        clanComboBox.setPromptText("Seleccionar clan");
        
        // Load clans for the dropdown
        try {
            List<com.management.jupiter.models.Clan> clans = clanController.readAll();
            clanComboBox.getItems().addAll(clans);
            
            // Configure the ComboBox to show only clan names
            clanComboBox.setConverter(new javafx.util.StringConverter<com.management.jupiter.models.Clan>() {
                @Override
                public String toString(com.management.jupiter.models.Clan clan) {
                    return clan != null ? clan.getName() : "";
                }
                
                @Override
                public com.management.jupiter.models.Clan fromString(String string) {
                    // Find clan by name
                    return clanComboBox.getItems().stream()
                        .filter(clan -> clan.getName().equals(string))
                        .findFirst()
                        .orElse(null);
                }
            });
            
            // Pre-select the current user's clan if available
            if (getCurrentClan() != null) {
                clanComboBox.setValue(getCurrentClan());
            }
        } catch (Exception e) {
            showAlert("Advertencia", "No se pudieron cargar los clanes: " + e.getMessage(), Alert.AlertType.WARNING);
        }
        
        // Info label
        Label infoLabel = new Label("El sistema generará nombres de células usando IA basados en el tema proporcionado");
        infoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666; -fx-wrap-text: true;");
        infoLabel.setMaxWidth(300);
        
        grid.add(new Label("Cantidad de células:"), 0, 0);
        grid.add(quantitySpinner, 1, 0);
        grid.add(new Label("Tema:"), 0, 1);
        grid.add(themeField, 1, 1);
        grid.add(new Label("Clan:"), 0, 2);
        grid.add(clanComboBox, 1, 2);
        grid.add(infoLabel, 0, 3, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    int quantity = quantitySpinner.getValue();
                    String theme = themeField.getText().trim();
                    
                    if (theme.isEmpty()) {
                        showAlert("Error", "El tema es requerido", Alert.AlertType.ERROR);
                        return null;
                    }
                    
                    if (quantity < 1 || quantity > 10) {
                        showAlert("Error", "La cantidad debe estar entre 1 y 10", Alert.AlertType.ERROR);
                        return null;
                    }
                    
                    // Get the selected clan
                    com.management.jupiter.models.Clan selectedClan = clanComboBox.getValue();
                    if (selectedClan == null) {
                        showAlert("Error", "Debe seleccionar un clan", Alert.AlertType.ERROR);
                        return null;
                    }
                    
                    // Use the existing cellController.createCell method with AI
                    cellController.createCell(quantity, theme, selectedClan);
                    return "SUCCESS";
                } catch (Exception e) {
                    showAlert("Error", "No se pudieron crear las células: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if ("SUCCESS".equals(result)) {
                showAlert("Éxito", "Células generadas correctamente usando IA", Alert.AlertType.INFORMATION);
                loadCells();
                loadTeamInfo();
            }
        });
    }
    
    private void showCreateInfoDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Crear Nueva Información");
        dialog.setHeaderText("Ingrese los datos de la nueva información");
        
        ButtonType createButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Título");
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Contenido");
        contentArea.setPrefRowCount(4);
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("TASK", "ANNOUNCEMENT", "REPORT", "NOTE");
        typeComboBox.setPromptText("Tipo de información");
        
        grid.add(new Label("Título:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Contenido:"), 0, 1);
        grid.add(contentArea, 1, 1);
        grid.add(new Label("Tipo:"), 0, 2);
        grid.add(typeComboBox, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String title = titleField.getText().trim();
                    String content = contentArea.getText().trim();
                    
                    if (title.isEmpty()) {
                        showAlert("Error", "El título es requerido", Alert.AlertType.ERROR);
                        return null;
                    }
                    
                    if (content.isEmpty()) {
                        showAlert("Error", "El contenido es requerido", Alert.AlertType.ERROR);
                        return null;
                    }
                    
                    if (getCurrentClan() == null) {
                        showAlert("Error", "No se encontró el clan del usuario", Alert.AlertType.ERROR);
                        return null;
                    }
                    
                    informationController.createInformation(
                        title,
                        content,
                        getCurrentClan()
                    );
                    return "SUCCESS";
                } catch (Exception e) {
                    showAlert("Error", "No se pudo crear la información: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if ("SUCCESS".equals(result)) {
                showAlert("Éxito", "Información creada correctamente", Alert.AlertType.INFORMATION);
                loadInformation();
                loadTeamInfo();
            }
        });
    }
    
    private void deleteCell() {
        Cell selectedCell = cellsTable.getSelectionModel().getSelectedItem();
        if (selectedCell == null) {
            showAlert("Advertencia", "Seleccione una célula para eliminar", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Eliminación");
        confirmDialog.setHeaderText("¿Está seguro de eliminar esta célula?");
        confirmDialog.setContentText("Célula: " + selectedCell.getName());
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    cellController.deleteCell(selectedCell.getId());
                    showAlert("Éxito", "Célula eliminada correctamente", Alert.AlertType.INFORMATION);
                    loadCells();
                    loadTeamInfo();
                } catch (Exception e) {
                    showAlert("Error", "No se pudo eliminar la célula: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void deleteInformation() {
        Information selectedInfo = informationTable.getSelectionModel().getSelectedItem();
        if (selectedInfo == null) {
            showAlert("Advertencia", "Seleccione información para eliminar", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Eliminación");
        confirmDialog.setHeaderText("¿Está seguro de eliminar esta información?");
        confirmDialog.setContentText("Información: " + selectedInfo.getTitle());
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    informationController.deleteInformation(selectedInfo.getId());
                    showAlert("Éxito", "Información eliminada correctamente", Alert.AlertType.INFORMATION);
                    loadInformation();
                    loadTeamInfo();
                } catch (Exception e) {
                    showAlert("Error", "No se pudo eliminar la información: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void showInformationDetails(Information info) {
        if (info == null) {
            showAlert("Error", "No se encontró la información", Alert.AlertType.ERROR);
            return;
        }
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalles de Información");
        dialog.setHeaderText("Contenido completo del mensaje");
        
        ButtonType closeButtonType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("Título:");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextField titleField = new TextField(info.getTitle());
        titleField.setEditable(false);
        
        // Message
        Label messageLabel = new Label("Mensaje:");
        messageLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextArea messageArea = new TextArea(info.getMessage());
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(8);
        
        // Date
        Label dateLabel = new Label("Fecha de creación:");
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextField dateField = new TextField();
        if (info.getCreatedAt() != null) {
            dateField.setText(info.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }
        dateField.setEditable(false);
        
        // Clan ID
        Label clanLabel = new Label("ID del Clan:");
        clanLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextField clanField = new TextField(info.getClanId());
        clanField.setEditable(false);
        
        content.getChildren().addAll(
            titleLabel, titleField,
            messageLabel, messageArea,
            dateLabel, dateField,
            clanLabel, clanField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    /**
     * Configura el tab de Coders: carga clanes en el ComboBox y muestra todos los coders agrupados por célula.
     */
    private void setupCodersTab() {
        if (coderClanFilterCombo == null || codersTreeView == null) return;
        
        try {
            List<com.management.jupiter.models.Clan> clans = clanController.readAll();
            coderClanFilterCombo.getItems().clear();
            coderClanFilterCombo.getItems().addAll(clans);
            coderClanFilterCombo.setConverter(new javafx.util.StringConverter<com.management.jupiter.models.Clan>() {
                @Override public String toString(com.management.jupiter.models.Clan c) { return c != null ? c.getName() : ""; }
                @Override public com.management.jupiter.models.Clan fromString(String s) { return null; }
            });
            // Pre-select TL's clan
            com.management.jupiter.models.Clan currentClan = getCurrentClan();
            if (currentClan != null) {
                clans.stream().filter(c -> c.getId().equals(currentClan.getId())).findFirst()
                    .ifPresent(coderClanFilterCombo::setValue);
            }
        } catch (Exception e) {
            System.err.println("Error loading clans for coders tab: " + e.getMessage());
        }
        
        // Load coders tree with current clan
        loadCodersTree(coderClanFilterCombo.getValue());
    }
    
    /**
     * Carga el TreeView de coders agrupados por célula.
     * Si clan es null, muestra todos los coders del clan del TL actual.
     */
    private void loadCodersTree(com.management.jupiter.models.Clan clan) {
        if (codersTreeView == null) return;
        
        com.management.jupiter.models.Clan targetClan = clan != null ? clan : getCurrentClan();
        
        TreeItem<String> root = new TreeItem<>("Coders");
        root.setExpanded(true);
        
        if (targetClan == null) {
            codersTreeView.setRoot(root);
            return;
        }
        
        try {
            Map<String, List<Coder>> codersByCell = cellController.getCodersGroupedByCell(targetClan);
            
            TreeItem<String> clanNode = new TreeItem<>("🏠 Clan: " + targetClan.getName());
            clanNode.setExpanded(true);
            
            for (Map.Entry<String, List<Coder>> entry : codersByCell.entrySet()) {
                String cellName = entry.getKey();
                List<Coder> coders = entry.getValue();
                
                TreeItem<String> cellNode = new TreeItem<>("📂 Célula: " + cellName + " (" + coders.size() + " coders)");
                cellNode.setExpanded(true);
                
                for (Coder coder : coders) {
                    String coderInfo = "👤 " + coder.getUsername() + " | " + coder.getEmail();
                    TreeItem<String> coderNode = new TreeItem<>(coderInfo);
                    cellNode.getChildren().add(coderNode);
                }
                
                clanNode.getChildren().add(cellNode);
            }
            
            if (codersByCell.isEmpty()) {
                clanNode.getChildren().add(new TreeItem<>("(Sin coders en este clan)"));
            }
            
            root.getChildren().add(clanNode);
        } catch (Exception e) {
            root.getChildren().add(new TreeItem<>("Error al cargar coders: " + e.getMessage()));
        }
        
        codersTreeView.setRoot(root);
    }
    
    private void refreshData() {
        loadCells();
        loadInformation();
        loadTeamInfo();
        loadCodersTree(coderClanFilterCombo != null ? coderClanFilterCombo.getValue() : null);
        showAlert("Información", "Datos actualizados", Alert.AlertType.INFORMATION);
    }
    
    private void handleLogout() {
        try {
            JupiterFXApplication.logout();
        } catch (Exception e) {
            showAlert("Error", "No se pudo cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
