package com.management.jupiter.fx.controllers;

import com.management.jupiter.controllers.AdminController;
import com.management.jupiter.controllers.ClanController;
import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Clan;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.security.LoginSession;
import com.management.jupiter.fx.JupiterFXApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.File;
import javafx.stage.FileChooser;

public class AdminDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private TabPane mainTabPane;
    @FXML private Tab usersTab;
    @FXML private Tab clansTab;
    @FXML private Tab statisticsTab;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> clanColumn;
    @FXML private TableView<Clan> clansTable;
    @FXML private TableColumn<Clan, String> clanIdColumn;
    @FXML private TableColumn<Clan, String> clanNameColumn;
    @FXML private TableColumn<Clan, String> clanDescColumn;
    @FXML private TableColumn<Clan, Integer> clanSizeColumn;
    @FXML private Button createUserButton;
    @FXML private Button editUserButton;
    @FXML private Button filterUsersButton;
    @FXML private Button createClanButton;
    @FXML private Button editClanButton;
    @FXML private Button deleteUserButton;
    @FXML private Button deleteClanButton;
    @FXML private Button importCodersButton;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private VBox statisticsBox;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalClansLabel;
    @FXML private Label totalAdminsLabel;
    @FXML private Label totalTlsLabel;
    @FXML private Label totalCodersLabel;
    
    // Filter controls for users
    private ComboBox<String> roleFilterComboBox;
    private ComboBox<String> clanFilterComboBox;
    
    private Admin currentUser;
    private LoginSession loginSession;
    private AdminController adminController;
    private ClanController clanController;
    
    // Full user list for filtering
    private List<User> allUsers = new ArrayList<>();
    
    public void setUserData(Admin user, LoginSession loginSession, AdminController adminController, ClanController clanController) {
        this.currentUser = user;
        this.loginSession = loginSession;
        this.adminController = adminController;
        this.clanController = clanController;
    }
    
    @FXML
    public void initialize() {
        setupTables();
        setupEventHandlers();
    }
    
    public void initializeDashboard() {
        welcomeLabel.setText("Bienvenido, " + currentUser.getUsername() + " (Administrador)");
        loadUsers();
        loadClans();
        loadStatistics();
    }
    
    private void setupTables() {
        // Users table - show sequential number as ID
        userIdColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                int index = usersTable.getItems().indexOf(cellData.getValue()) + 1;
                return String.valueOf(index);
            }));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() ->
                cellData.getValue().getRole().toString()));
        clanColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                com.management.jupiter.models.User u = cellData.getValue();
                Clan clan = u.getClan_id();
                if (clan == null && u instanceof com.management.jupiter.models.Tl tlUser) {
                    if (tlUser.getClans() != null && !tlUser.getClans().isEmpty()) {
                        clan = tlUser.getClans().get(0);
                    }
                }
                return clan != null ? clan.getName() : "Sin Clan";
            }));
        
        // Clans table - show sequential number as ID
        clanIdColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                int index = clansTable.getItems().indexOf(cellData.getValue()) + 1;
                return String.valueOf(index);
            }));
        clanNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        clanDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        clanSizeColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createObjectBinding(() -> {
                Clan clan = cellData.getValue();
                int codersCount = clan != null && clan.getCoders() != null ? clan.getCoders().size() : 0;
                int tlsCount = clan != null && clan.getTls() != null ? clan.getTls().size() : 0;
                return codersCount + tlsCount;
            }));
    }
    
    private void setupEventHandlers() {
        createUserButton.setOnAction(event -> showCreateUserDialog());
        editUserButton.setOnAction(event -> showEditUserDialog());
        if (filterUsersButton != null) filterUsersButton.setOnAction(event -> showUserFilterDialog());
        createClanButton.setOnAction(event -> showCreateClanDialog());
        editClanButton.setOnAction(event -> showEditClanDialog());
        deleteUserButton.setOnAction(event -> deleteUser());
        deleteClanButton.setOnAction(event -> deleteClan());
        importCodersButton.setOnAction(event -> showImportCodersDialog());
        refreshButton.setOnAction(event -> refreshData());
        logoutButton.setOnAction(event -> handleLogout());
    }
    
    private void loadUsers() {
        try {
            allUsers = adminController.getAll();
            applyUserFilters();
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar los usuarios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void applyUserFilters() {
        String roleFilter = roleFilterComboBox != null ? roleFilterComboBox.getValue() : null;
        String clanFilter = clanFilterComboBox != null ? clanFilterComboBox.getValue() : null;
        
        List<User> filtered = allUsers.stream()
            .filter(u -> {
                if (roleFilter != null && !roleFilter.equals("TODOS")) {
                    if (!u.getRole().toString().equals(roleFilter)) return false;
                }
                if (clanFilter != null && !clanFilter.equals("TODOS")) {
                    String clanName = u.getClan_id() != null ? u.getClan_id().getName() : "Sin Clan";
                    if (!clanName.equals(clanFilter)) return false;
                }
                return true;
            })
            .collect(Collectors.toList());
        
        ObservableList<User> list = FXCollections.observableArrayList(filtered);
        usersTable.setItems(list);
        // Refresh to update sequential IDs
        usersTable.refresh();
    }
    
    private void loadClans() {
        try {
            List<Clan> clans = clanController.readAll();
            ObservableList<Clan> clanObservableList = FXCollections.observableArrayList(clans);
            clansTable.setItems(clanObservableList);
            clansTable.refresh();
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar los clanes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void loadStatistics() {
        try {
            List<User> users = adminController.getAll();
            List<Clan> clans = clanController.readAll();
            
            long totalUsers = users.size();
            long totalClans = clans.size();
            long totalAdmins = users.stream().filter(u -> u.getRole() == Role.ADMIN).count();
            long totalTls = users.stream().filter(u -> u.getRole() == Role.TL).count();
            long totalCoders = users.stream().filter(u -> u.getRole() == Role.CODER).count();
            
            totalUsersLabel.setText(String.valueOf(totalUsers));
            totalClansLabel.setText(String.valueOf(totalClans));
            totalAdminsLabel.setText(String.valueOf(totalAdmins));
            totalTlsLabel.setText(String.valueOf(totalTls));
            totalCodersLabel.setText(String.valueOf(totalCoders));
            
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar las estadísticas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Shows a dialog with filters and the users table.
     * Called when the Users tab is selected or refresh is triggered.
     * Adds filter controls above the users table area.
     */
    private void showUserFilterPanel() {
        // This is handled inline in showCreateUserDialog context;
        // The filter panel is added dynamically to the tab.
    }
    
    private void showCreateUserDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Crear Nuevo Usuario");
        dialog.setHeaderText("Ingrese los datos del nuevo usuario");
        
        ButtonType createButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nombre de usuario");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("ADMIN", "TL", "CODER");
        roleComboBox.setPromptText("Rol");
        
        ComboBox<Clan> clanComboBox = new ComboBox<>();
        clanComboBox.setPromptText("Clan (opcional)");
        
        // Especialidad solo: PROGRAMACION e INGLES
        ComboBox<String> especialidadComboBox = new ComboBox<>();
        especialidadComboBox.getItems().addAll("PROGRAMACION", "INGLES");
        especialidadComboBox.setPromptText("Especialidad");
        especialidadComboBox.setDisable(true);
        
        Label especialidadNote = new Label("Solo: PROGRAMACION o INGLES");
        especialidadNote.setStyle("-fx-font-style: italic; -fx-text-fill: #666; -fx-font-size: 11px;");
        
        // Load clans
        try {
            List<Clan> clans = clanController.readAll();
            clanComboBox.getItems().addAll(clans);
            clanComboBox.setConverter(new javafx.util.StringConverter<Clan>() {
                @Override public String toString(Clan clan) { return clan != null ? clan.getName() : ""; }
                @Override public Clan fromString(String s) {
                    return clanComboBox.getItems().stream().filter(c -> c.getName().equals(s)).findFirst().orElse(null);
                }
            });
        } catch (Exception e) {
            showAlert("Advertencia", "No se pudieron cargar los clanes: " + e.getMessage(), Alert.AlertType.WARNING);
        }
        
        roleComboBox.setOnAction(event -> {
            String selectedRole = roleComboBox.getValue();
            boolean isTl = "TL".equals(selectedRole);
            especialidadComboBox.setDisable(!isTl);
            if (!isTl) especialidadComboBox.setValue(null);
        });
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Rol:"), 0, 3);
        grid.add(roleComboBox, 1, 3);
        grid.add(new Label("Clan:"), 0, 4);
        grid.add(clanComboBox, 1, 4);
        grid.add(new Label("Especialidad:"), 0, 5);
        grid.add(especialidadComboBox, 1, 5);
        grid.add(especialidadNote, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText();
                String roleStr = roleComboBox.getValue();
                
                if (username.isEmpty()) { showAlert("Error", "El nombre de usuario es requerido", Alert.AlertType.ERROR); return null; }
                if (email.isEmpty()) { showAlert("Error", "El email es requerido", Alert.AlertType.ERROR); return null; }
                if (password.isEmpty()) { showAlert("Error", "La contraseña es requerida", Alert.AlertType.ERROR); return null; }
                if (roleStr == null) { showAlert("Error", "Debe seleccionar un rol", Alert.AlertType.ERROR); return null; }
                
                // Validate especialidad - only applies to TL
                String especialidadStr = especialidadComboBox.getValue();
                TlType tlType = null;
                if ("TL".equals(roleStr) && especialidadStr != null) {
                    try {
                        tlType = TlType.fromString(especialidadStr);
                    } catch (IllegalArgumentException ex) {
                        showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                        return null;
                    }
                }
                if ("TL".equals(roleStr) && tlType == null) {
                    showAlert("Error", "Debe seleccionar una especialidad (PROGRAMACION o INGLES) para el TL", Alert.AlertType.ERROR);
                    return null;
                }
                
                try {
                    Role role = Role.valueOf(roleStr);
                    Clan selectedClan = clanComboBox.getValue();
                    adminController.createUser(username, email, password, role, selectedClan, tlType);
                    return "SUCCESS";
                } catch (IllegalArgumentException e) {
                    showAlert("Error", "Datos inválidos: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                } catch (Exception e) {
                    showAlert("Error", "No se pudo crear el usuario: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if ("SUCCESS".equals(result)) {
                showAlert("Éxito", "Usuario creado correctamente", Alert.AlertType.INFORMATION);
                loadUsers();
                loadStatistics();
            }
        });
    }
    
    private void showCreateClanDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Crear Nuevo Clan");
        dialog.setHeaderText("Ingrese los datos del nuevo clan");
        
        ButtonType createButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del clan");
        TextArea descField = new TextArea();
        descField.setPromptText("Descripción del clan");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
        
        // Show next sequential ID
        int nextId = clansTable.getItems().size() + 1;
        Label idNote = new Label("ID asignado automáticamente: #" + nextId);
        idNote.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
        
        grid.add(idNote, 0, 0, 2, 1);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Descripción:"), 0, 2);
        grid.add(descField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String description = descField.getText().trim();
                    if (name.isEmpty()) { showAlert("Error", "El nombre del clan es requerido", Alert.AlertType.ERROR); return null; }
                    if (description.isEmpty()) description = "Sin descripción";
                    clanController.createClan(name, description);
                    return "SUCCESS";
                } catch (Exception e) {
                    showAlert("Error", "No se pudo crear el clan: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if ("SUCCESS".equals(result)) {
                showAlert("Éxito", "Clan creado correctamente", Alert.AlertType.INFORMATION);
                loadClans();
                loadStatistics();
            }
        });
    }
    
    private void showEditClanDialog() {
        Clan selectedClan = clansTable.getSelectionModel().getSelectedItem();
        if (selectedClan == null) { showAlert("Advertencia", "Seleccione un clan para editar", Alert.AlertType.WARNING); return; }
        
        Dialog<Clan> dialog = new Dialog<>();
        dialog.setTitle("Editar Clan");
        dialog.setHeaderText("Modifique los datos del clan");
        
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(selectedClan.getName());
        TextArea descField = new TextArea(selectedClan.getDescription());
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
        
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Descripción:"), 0, 1);
        grid.add(descField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String description = descField.getText().trim();
                    if (name.isEmpty()) { showAlert("Error", "El nombre del clan es requerido", Alert.AlertType.ERROR); return null; }
                    if (description.isEmpty()) description = "Sin descripción";
                    Clan updatedClan = new Clan(selectedClan.getId(), name, description);
                    updatedClan.setCoders(selectedClan.getCoders());
                    updatedClan.setTls(selectedClan.getTls());
                    clanController.updateClan(updatedClan);
                    return updatedClan;
                } catch (Exception e) {
                    showAlert("Error", "No se pudo actualizar el clan: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            showAlert("Éxito", "Clan actualizado correctamente", Alert.AlertType.INFORMATION);
            loadClans();
            loadStatistics();
        });
    }
    
    private void showEditUserDialog() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) { showAlert("Advertencia", "Seleccione un usuario para editar", Alert.AlertType.WARNING); return; }
        
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Editar Usuario");
        dialog.setHeaderText("Modifique los datos del usuario");
        
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField usernameField = new TextField(selectedUser.getUsername());
        TextField emailField = new TextField(selectedUser.getEmail());
        
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("ADMIN", "TL", "CODER");
        roleComboBox.setValue(selectedUser.getRole().toString());
        
        // Clan ComboBox with proper converter showing only name
        ComboBox<Clan> clanComboBox = new ComboBox<>();
        clanComboBox.setPromptText("Sin clan");
        clanComboBox.setConverter(new javafx.util.StringConverter<Clan>() {
            @Override public String toString(Clan clan) { return clan != null ? clan.getName() : "Sin clan"; }
            @Override public Clan fromString(String s) {
                return clanComboBox.getItems().stream().filter(c -> c.getName().equals(s)).findFirst().orElse(null);
            }
        });
        
        // Especialidad: only PROGRAMACION or INGLES
        ComboBox<String> especialidadComboBox = new ComboBox<>();
        especialidadComboBox.getItems().addAll("PROGRAMACION", "INGLES");
        especialidadComboBox.setPromptText("Especialidad");
        
        // Load clans and pre-select current clan by ID match
        try {
            List<Clan> clans = clanController.readAll();
            clanComboBox.getItems().addAll(clans);
            
            // For TL: clan is stored in getClans(), not getClan_id()
            Clan currentClan = selectedUser.getClan_id();
            if (currentClan == null && selectedUser instanceof com.management.jupiter.models.Tl tlUser) {
                if (tlUser.getClans() != null && !tlUser.getClans().isEmpty()) {
                    currentClan = tlUser.getClans().get(0);
                }
            }
            
            if (currentClan != null) {
                final String currentClanId = currentClan.getId();
                clans.stream()
                    .filter(c -> c.getId() != null && c.getId().equals(currentClanId))
                    .findFirst()
                    .ifPresent(clanComboBox::setValue);
            }
        } catch (Exception e) {
            showAlert("Advertencia", "No se pudieron cargar los clanes: " + e.getMessage(), Alert.AlertType.WARNING);
        }
        
        boolean isTl = "TL".equals(selectedUser.getRole().toString());
        especialidadComboBox.setDisable(!isTl);
        
        roleComboBox.setOnAction(event -> {
            boolean tl = "TL".equals(roleComboBox.getValue());
            especialidadComboBox.setDisable(!tl);
            if (!tl) especialidadComboBox.setValue(null);
        });
        
        Label especialidadNote = new Label("Solo: PROGRAMACION o INGLES");
        especialidadNote.setStyle("-fx-font-style: italic; -fx-text-fill: #666; -fx-font-size: 11px;");
        
        grid.add(new Label("Username:"), 0, 0);    grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);        grid.add(emailField, 1, 1);
        grid.add(new Label("Rol:"), 0, 2);          grid.add(roleComboBox, 1, 2);
        grid.add(new Label("Clan:"), 0, 3);         grid.add(clanComboBox, 1, 3);
        grid.add(new Label("Especialidad:"), 0, 4); grid.add(especialidadComboBox, 1, 4);
        grid.add(especialidadNote, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String username = usernameField.getText().trim();
                    String email = emailField.getText().trim();
                    String roleStr = roleComboBox.getValue();
                    
                    if (username.isEmpty() || email.isEmpty() || roleStr == null) {
                        showAlert("Error", "Todos los campos son requeridos", Alert.AlertType.ERROR);
                        return null;
                    }
                    
                    // Validate especialidad if provided
                    String especialidadStr = especialidadComboBox.getValue();
                    if (especialidadStr != null && !especialidadStr.isEmpty()) {
                        try { TlType.fromString(especialidadStr); }
                        catch (IllegalArgumentException ex) {
                            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                            return null;
                        }
                    }
                    
                    if (!username.equals(selectedUser.getUsername())) {
                        adminController.updateUser(selectedUser.getId(), username, "username");
                    }
                    if (!email.equals(selectedUser.getEmail())) {
                        adminController.updateUser(selectedUser.getId(), email, "email");
                    }
                    if (!roleStr.equals(selectedUser.getRole().toString())) {
                        adminController.updateUser(selectedUser.getId(), roleStr, "role");
                    }
                    
                    // Update clan using the selected clan's ID (not the whole object)
                    Clan selectedClan = clanComboBox.getValue();
                    if (selectedClan != null && selectedClan.getId() != null) {
                        adminController.updateUser(selectedUser.getId(), selectedClan.getId(), "clan_id");
                    }
                    
                    return selectedUser;
                } catch (Exception e) {
                    showAlert("Error", "No se pudo actualizar el usuario: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            showAlert("Éxito", "Usuario actualizado correctamente", Alert.AlertType.INFORMATION);
            loadUsers();
            loadStatistics();
        });
    }
    
    /**
     * Shows filter dialog for users - filters by Role and Clan
     */
    public void showUserFilterDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Filtrar Usuarios");
        dialog.setHeaderText("Seleccione filtros para los usuarios");
        
        ButtonType applyButtonType = new ButtonType("Aplicar", ButtonBar.ButtonData.OK_DONE);
        ButtonType clearButtonType = new ButtonType("Limpiar", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, clearButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));
        
        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.getItems().addAll("TODOS", "TL", "CODER", "ADMIN");
        roleFilter.setValue(roleFilterComboBox != null && roleFilterComboBox.getValue() != null ? roleFilterComboBox.getValue() : "TODOS");
        
        ComboBox<String> clanFilter = new ComboBox<>();
        clanFilter.getItems().add("TODOS");
        try {
            List<Clan> clans = clanController.readAll();
            clans.stream().map(Clan::getName).forEach(clanFilter.getItems()::add);
        } catch (Exception ignored) {}
        clanFilter.setValue(clanFilterComboBox != null && clanFilterComboBox.getValue() != null ? clanFilterComboBox.getValue() : "TODOS");
        
        grid.add(new Label("Filtrar por Rol:"), 0, 0);
        grid.add(roleFilter, 1, 0);
        grid.add(new Label("Filtrar por Clan:"), 0, 1);
        grid.add(clanFilter, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(btn -> {
            if (btn == applyButtonType) {
                if (roleFilterComboBox == null) roleFilterComboBox = new ComboBox<>();
                if (clanFilterComboBox == null) clanFilterComboBox = new ComboBox<>();
                roleFilterComboBox.setValue(roleFilter.getValue());
                clanFilterComboBox.setValue(clanFilter.getValue());
                applyUserFilters();
            } else if (btn == clearButtonType) {
                if (roleFilterComboBox != null) roleFilterComboBox.setValue("TODOS");
                if (clanFilterComboBox != null) clanFilterComboBox.setValue("TODOS");
                applyUserFilters();
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void showImportCodersDialog() {
        Clan selectedClan = clansTable.getSelectionModel().getSelectedItem();
        if (selectedClan == null) { showAlert("Advertencia", "Seleccione un clan para importar coders", Alert.AlertType.WARNING); return; }
        
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Importar Coders CSV");
        dialog.setHeaderText("Importe coders para el clan: " + selectedClan.getName());
        
        ButtonType importButtonType = new ButtonType("Importar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(importButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        Label infoLabel = new Label("El archivo CSV debe tener el formato: username,email,password");
        infoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
        grid.add(infoLabel, 0, 0, 2, 1);
        dialog.getDialogPane().setContent(grid);
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo CSV");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("All Files", "*.*"));
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == importButtonType) {
                try {
                    File selectedFile = fileChooser.showOpenDialog(null);
                    if (selectedFile != null) return selectedFile.getAbsolutePath();
                    else { showAlert("Advertencia", "No se seleccionó ningún archivo", Alert.AlertType.WARNING); return null; }
                } catch (Exception e) {
                    showAlert("Error", "Error al seleccionar archivo: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(filePath -> {
            if (filePath != null) {
                try { importCodersFromCSV(filePath, selectedClan); }
                catch (Exception e) { showAlert("Error", "No se pudo importar los coders: " + e.getMessage(), Alert.AlertType.ERROR); }
            }
        });
    }
    
    private void importCodersFromCSV(String filePath, Clan clan) {
        try {
            List<String[]> csvData = com.management.jupiter.persistance.ReadCSV.readCSV(new java.io.FileInputStream(filePath));
            if (csvData.isEmpty()) { showAlert("Advertencia", "El archivo CSV está vacío", Alert.AlertType.WARNING); return; }
            
            int successCount = 0, errorCount = 0;
            List<String> errors = new ArrayList<>();
            
            for (int i = 0; i < csvData.size(); i++) {
                String[] row = csvData.get(i);
                if (i == 0 && row.length > 0 && (row[0].toLowerCase().contains("username") || row[0].toLowerCase().contains("email"))) continue;
                try {
                    if (row.length >= 3) {
                        String username = row[0].trim(), email = row[1].trim(), password = row[2].trim();
                        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) { errors.add("Línea " + (i+1) + ": Campos vacíos"); errorCount++; continue; }
                        adminController.createUser(username, email, password, Role.CODER, clan, null);
                        successCount++;
                    } else { errors.add("Línea " + (i+1) + ": Formato incorrecto"); errorCount++; }
                } catch (Exception e) { errors.add("Línea " + (i+1) + ": " + e.getMessage()); errorCount++; }
            }
            
            StringBuilder message = new StringBuilder("Importación completada:\n✅ Creados: " + successCount + "\n❌ Errores: " + errorCount);
            if (!errors.isEmpty()) { message.append("\n\nPrimeros errores:"); errors.stream().limit(5).forEach(err -> message.append("\n• ").append(err)); }
            
            if (successCount > 0) { showAlert("Éxito", message.toString(), Alert.AlertType.INFORMATION); loadUsers(); loadStatistics(); loadClans(); }
            else showAlert("Error", message.toString(), Alert.AlertType.ERROR);
            
        } catch (java.io.FileNotFoundException e) {
            showAlert("Error", "Archivo no encontrado: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Error al procesar el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void deleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) { showAlert("Advertencia", "Seleccione un usuario para eliminar", Alert.AlertType.WARNING); return; }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Eliminación");
        confirmDialog.setHeaderText("¿Está seguro de eliminar este usuario?");
        confirmDialog.setContentText("Usuario: " + selectedUser.getUsername());
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    adminController.deleteUser(selectedUser.getId());
                    showAlert("Éxito", "Usuario eliminado correctamente", Alert.AlertType.INFORMATION);
                    loadUsers(); loadStatistics();
                } catch (Exception e) {
                    showAlert("Error", "No se pudo eliminar el usuario: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void deleteClan() {
        Clan selectedClan = clansTable.getSelectionModel().getSelectedItem();
        if (selectedClan == null) { showAlert("Advertencia", "Seleccione un clan para eliminar", Alert.AlertType.WARNING); return; }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Eliminación");
        confirmDialog.setHeaderText("¿Está seguro de eliminar este clan?");
        confirmDialog.setContentText("Clan: " + selectedClan.getName());
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    clanController.deleteClan(selectedClan.getId());
                    showAlert("Éxito", "Clan eliminado correctamente", Alert.AlertType.INFORMATION);
                    loadClans(); loadStatistics();
                } catch (Exception e) {
                    showAlert("Error", "No se pudo eliminar el clan: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void refreshData() {
        loadUsers(); loadClans(); loadStatistics();
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
