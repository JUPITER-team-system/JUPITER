package com.management.jupiter.controllers;

import com.management.jupiter.exceptions.UserBlockedException;
import com.management.jupiter.models.User;
import com.management.jupiter.security.SessionManager;
import com.management.jupiter.util.ViewLoader;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Button        loginButton;

    private final UserController userController = new UserController();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        clearError();

        if (email.isEmpty() || password.isEmpty()) {
            showError("⚠  Please enter your email and password.");
            return;
        }

        // Deshabilitar botón mientras se autentica (la BD tarda ~1-2 s)
        loginButton.setDisable(true);
        showError("Connecting…");

        // Ejecutar en hilo secundario para no bloquear el UI thread
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                return userController.login(email, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            User user = loginTask.getValue();
            SessionManager.getInstance().login(user);
            clearError();
            loginButton.setDisable(false);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            switch (user.getRole()) {
                case ADMIN -> ViewLoader.navigate(stage, "/views/admin.fxml",
                        "Jupiter – Administration", 1150, 700);
                case TL    -> ViewLoader.navigate(stage, "/views/tl.fxml",
                        "Jupiter – Team Leader",   1050, 680);
                case CODER -> ViewLoader.navigate(stage, "/views/coder.fxml",
                        "Jupiter – Coder",          900,  600);
            }
        });

        loginTask.setOnFailed(e -> {
            loginButton.setDisable(false);
            Throwable ex = loginTask.getException();

            if (ex instanceof UserBlockedException ube) {
                long remaining = (ube.getBlockedUntil() - System.currentTimeMillis()) / 1000;
                showError("🔒  Account locked. Try again in " + remaining + " s.");
            } else {
                int left = userController.getLeftAttempts(email);
                String msg = ex != null ? ex.getMessage() : "Unknown error";
                // Mostrar error real (conexión, usuario no encontrado, etc.)
                showError("✗  " + msg + "  (attempts left: " + left + ")");
                System.err.println("[Login] Failed: " + msg);
            }
        });

        Thread t = new Thread(loginTask);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}
