package com.management.jupiter.fx.controllers;

import com.management.jupiter.controllers.CoderController;
import com.management.jupiter.controllers.InformationController;
import com.management.jupiter.controllers.CellController;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Information;
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

public class CoderDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private TabPane mainTabPane;
    @FXML private Tab informationTab;
    @FXML private Tab profileTab;
    @FXML private Tab teamTab;
    @FXML private TableView<Information> informationTable;
    @FXML private TableColumn<Information, String> infoIdColumn;
    @FXML private TableColumn<Information, String> infoTitleColumn;
    @FXML private TableColumn<Information, String> infoTypeColumn;
    @FXML private TableColumn<Information, String> infoDateColumn;
    @FXML private TableColumn<Information, String> infoMessageColumn;
    @FXML private TableColumn<Information, Void> infoActionsColumn;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private VBox profileInfoBox;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label clanLabel;
    @FXML private Label cellLabel;
    @FXML private Label roleLabel;
    @FXML private Label totalInfoLabel;
    
    // Team tab
    @FXML private TreeView<String> teamTreeView;
    
    private Coder currentUser;
    private LoginSession loginSession;
    private CoderController coderController;
    private InformationController informationController;
    private CellController cellController;
    
    public void setUserData(Coder user, LoginSession loginSession, CoderController coderController,
                          InformationController informationController) {
        this.currentUser = user;
        this.loginSession = loginSession;
        this.coderController = coderController;
        this.informationController = informationController;
    }
    
    // Extended setter to include CellController for team view
    public void setUserData(Coder user, LoginSession loginSession, CoderController coderController,
                          InformationController informationController, CellController cellController) {
        this.currentUser = user;
        this.loginSession = loginSession;
        this.coderController = coderController;
        this.informationController = informationController;
        this.cellController = cellController;
    }
    
    @FXML
    public void initialize() {
        setupTables();
        setupEventHandlers();
    }
    
    public void initializeDashboard() {
        welcomeLabel.setText("Bienvenido, " + currentUser.getUsername() + " (Coder)");
        loadInformation();
        loadProfileInfo();
        loadTeamView();
    }
    
    private void setupTables() {
        infoIdColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                int index = informationTable.getItems().indexOf(cellData.getValue()) + 1;
                return String.valueOf(index);
            }));
        infoTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        infoTypeColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() -> "Mensaje"));
        infoDateColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                Information info = cellData.getValue();
                if (info != null && info.getCreatedAt() != null) {
                    return info.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                }
                return "N/A";
            }));
        infoMessageColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                Information info = cellData.getValue();
                if (info != null && info.getMessage() != null) {
                    String message = info.getMessage();
                    return message.length() > 50 ? message.substring(0, 50) + "..." : message;
                }
                return "N/A";
            }));
        
        infoActionsColumn.setCellFactory(param -> new TableCell<Information, Void>() {
            private final Button viewButton = new Button("Ver");
            {
                viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 3;");
                viewButton.setOnAction(event -> {
                    Information info = getTableView().getItems().get(getIndex());
                    showInformationDetail(info);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewButton);
            }
        });
    }
    
    private void setupEventHandlers() {
        refreshButton.setOnAction(event -> refreshData());
        logoutButton.setOnAction(event -> handleLogout());
        
        informationTable.setRowFactory(tv -> {
            TableRow<Information> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) showInformationDetail(row.getItem());
            });
            return row;
        });
    }
    
    private void loadInformation() {
        try {
            List<Information> information = informationController.findByUserClan(currentUser);
            ObservableList<Information> list = FXCollections.observableArrayList(information);
            informationTable.setItems(list);
            totalInfoLabel.setText(String.valueOf(information.size()));
        } catch (Exception e) {
            showAlert("Error", "No se pudo cargar la información: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void loadProfileInfo() {
        try {
            usernameLabel.setText(currentUser.getUsername());
            emailLabel.setText(currentUser.getEmail());
            clanLabel.setText(currentUser.getClan_id() != null ? currentUser.getClan_id().getName() : "Sin Clan");
            roleLabel.setText(currentUser.getRole().toString());
            // Cell assignment loaded separately
            cellLabel.setText("Cargando...");
            loadCellAssignment();
        } catch (Exception e) {
            showAlert("Error", "No se pudo cargar el perfil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void loadCellAssignment() {
        if (cellController == null || currentUser.getClan_id() == null) {
            cellLabel.setText("Sin Célula Asignada");
            return;
        }
        try {
            Map<String, List<com.management.jupiter.models.Coder>> grouped = 
                cellController.getCodersGroupedByCell(currentUser.getClan_id());
            String myCell = "Sin Célula Asignada";
            for (Map.Entry<String, List<com.management.jupiter.models.Coder>> entry : grouped.entrySet()) {
                boolean isMe = entry.getValue().stream()
                    .anyMatch(c -> c.getId() != null && c.getId().equals(currentUser.getId()));
                if (isMe) {
                    myCell = entry.getKey();
                    break;
                }
            }
            cellLabel.setText(myCell);
        } catch (Exception e) {
            cellLabel.setText("Sin Célula Asignada");
            System.err.println("[WARN] Could not load cell assignment: " + e.getMessage());
        }
    }
    
    /**
     * Carga la vista de equipo: muestra el clan y la célula del coder,
     * y los compañeros de equipo agrupados por célula.
     */
    private void loadTeamView() {
        if (teamTreeView == null) return;
        
        TreeItem<String> root = new TreeItem<>("Mi Equipo");
        root.setExpanded(true);
        
        com.management.jupiter.models.Clan clan = currentUser.getClan_id();
        if (clan == null) {
            root.getChildren().add(new TreeItem<>("No perteneces a ningún clan aún"));
            teamTreeView.setRoot(root);
            return;
        }
        
        TreeItem<String> clanNode = new TreeItem<>("🏠 Clan: " + clan.getName());
        clanNode.setExpanded(true);
        
        try {
            if (cellController != null) {
                Map<String, List<com.management.jupiter.models.Coder>> grouped;
                try {
                    grouped = cellController.getCodersGroupedByCell(clan);
                } catch (Exception ex) {
                    clanNode.getChildren().add(new TreeItem<>("(No hay células aún en este clan)"));
                    root.getChildren().add(clanNode);
                    teamTreeView.setRoot(root);
                    return;
                }
                
                for (Map.Entry<String, List<com.management.jupiter.models.Coder>> entry : grouped.entrySet()) {
                    String cellName = entry.getKey();
                    List<com.management.jupiter.models.Coder> coders = entry.getValue();
                    
                    boolean isMyCell = coders.stream()
                        .anyMatch(c -> c.getId() != null && c.getId().equals(currentUser.getId()));
                    
                    String cellLabel2 = isMyCell ? "📂 ⭐ Mi Célula: " + cellName : "📂 Célula: " + cellName;
                    TreeItem<String> cellNode = new TreeItem<>(cellLabel2 + " (" + coders.size() + " coders)");
                    cellNode.setExpanded(isMyCell);
                    
                    for (com.management.jupiter.models.Coder coder : coders) {
                        boolean isMe = coder.getId() != null && coder.getId().equals(currentUser.getId());
                        String info = (isMe ? "👤 ⭐ YO: " : "👤 ") + coder.getUsername() + " | " + coder.getEmail();
                        cellNode.getChildren().add(new TreeItem<>(info));
                    }
                    clanNode.getChildren().add(cellNode);
                }
                
                if (grouped.isEmpty()) {
                    clanNode.getChildren().add(new TreeItem<>("(Sin células asignadas aún)"));
                }
                
                // TL messages section
                try {
                    List<Information> messages = informationController.findByUserClan(currentUser);
                    if (!messages.isEmpty()) {
                        TreeItem<String> messagesNode = new TreeItem<>("💬 Mensajes del TL (" + messages.size() + ")");
                        messagesNode.setExpanded(true);
                        for (Information msg : messages) {
                            String preview = msg.getMessage() != null && msg.getMessage().length() > 60 
                                ? msg.getMessage().substring(0, 60) + "..." 
                                : msg.getMessage();
                            messagesNode.getChildren().add(new TreeItem<>("📩 " + msg.getTitle() + ": " + preview));
                        }
                        clanNode.getChildren().add(messagesNode);
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            clanNode.getChildren().add(new TreeItem<>("Error al cargar equipo: " + e.getMessage()));
        }
        
        root.getChildren().add(clanNode);
        teamTreeView.setRoot(root);
    }
    
    private void viewSelectedInformation() {
        Information selectedInfo = informationTable.getSelectionModel().getSelectedItem();
        if (selectedInfo == null) { showAlert("Advertencia", "Seleccione información para ver", Alert.AlertType.WARNING); return; }
        showInformationDetail(selectedInfo);
    }
    
    private void showInformationDetail(Information information) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalle de Información");
        dialog.setHeaderText(information.getTitle());
        
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Label dateLabel = new Label("Fecha: " + (information.getCreatedAt() != null ? 
            information.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "No disponible"));
        Label clanLbl = new Label("Clan ID: " + (information.getClanId() != null ? information.getClanId() : "No especificado"));
        TextArea contentArea = new TextArea(information.getMessage() != null ? information.getMessage() : "Sin contenido");
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(10);
        
        content.getChildren().addAll(dateLabel, clanLbl, new Label("Mensaje:"), contentArea);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    private void refreshData() {
        loadInformation();
        loadProfileInfo();
        loadTeamView();
        showAlert("Información", "Datos actualizados", Alert.AlertType.INFORMATION);
    }
    
    private void handleLogout() {
        try { JupiterFXApplication.logout(); }
        catch (Exception e) { showAlert("Error", "No se pudo cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR); }
    }
    
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
