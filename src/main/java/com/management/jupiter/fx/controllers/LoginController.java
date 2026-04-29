package com.management.jupiter.fx.controllers;

import com.management.jupiter.controllers.*;
import com.management.jupiter.exceptions.UserBlockedException;
import com.management.jupiter.models.User;
import com.management.jupiter.fx.JupiterFXApplication;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class LoginController {
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private Label attemptsLabel;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private VBox loginBox;
    
    private UserController userController;
    private AdminController adminController;
    private TlController tlController;
    private CoderController coderController;
    private ClanController clanController;
    private CellController cellController;
    private InformationController informationController;
    
    private int remainingAttempts = 3;
    
    public void setControllers(UserController userController, AdminController adminController, 
                            TlController tlController, CoderController coderController,
                            ClanController clanController, CellController cellController,
                            InformationController informationController) {
        this.userController = userController;
        this.adminController = adminController;
        this.tlController = tlController;
        this.coderController = coderController;
        this.clanController = clanController;
        this.cellController = cellController;
        this.informationController = informationController;
    }
    
    @FXML
    public void initialize() {
        attemptsLabel.setText("Intentos restantes: " + remainingAttempts);
        errorLabel.setText("");
        errorLabel.setTextFill(Color.RED);
        
        // Bind enter key to login
        loginButton.setDefaultButton(true);
        
        // Add event handlers
        loginButton.setOnAction(event -> handleLogin());
        clearButton.setOnAction(event -> clearFields());
        
        // Add real-time validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            errorLabel.setText("");
        });
        
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            errorLabel.setText("");
        });
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            showError("Por favor complete todos los campos");
            return;
        }
        
        User user;
        
        // ── Paso 1: autenticación ──────────────────────────────────────────
        try {
            user = userController.login(email, password);
        } catch (com.management.jupiter.exceptions.UserBlockedException e) {
            showError("Usuario bloqueado. Espere " + e.remaningTime() + " segundos");
            disableLogin();
            return;
        } catch (Exception e) {
            remainingAttempts = userController.getLeftAttempts(email);
            attemptsLabel.setText("Intentos restantes: " + remainingAttempts);
            showError("Credenciales incorrectas");
            if (remainingAttempts <= 0) disableLogin();
            return;
        }
        
        // ── Paso 2: navegación al dashboard (errores separados) ────────────
        try {
            switch (user.getRole()) {
                case ADMIN  -> JupiterFXApplication.showAdminDashboard(user);
                case TL     -> JupiterFXApplication.showTlDashboard(user);
                case CODER  -> JupiterFXApplication.showCoderDashboard(user);
            }
        } catch (Exception e) {
            // El login fue correcto; el error es al cargar el dashboard
            showError("Error al cargar el panel: " + e.getMessage());
            System.err.println("[ERROR] Dashboard load failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void clearFields() {
        emailField.clear();
        passwordField.clear();
        errorLabel.setText("");
        emailField.requestFocus();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setTextFill(Color.RED);
    }
    
    private void disableLogin() {
        loginButton.setDisable(true);
        emailField.setDisable(true);
        passwordField.setDisable(true);
        clearButton.setDisable(true);
        showError("Usuario bloqueado. Contacte al administrador.");
    }
}
