package com.management.jupiter;

import com.management.jupiter.util.ViewLoader;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicación JUPITER con interfaz gráfica JavaFX.
 * La lógica de negocio (servicios, repositorios, modelos) permanece intacta;
 * aquí sólo se inicializa el Stage principal y se carga la vista de login.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(false);
        ViewLoader.navigate(
                primaryStage,
                "/views/login.fxml",
                "Jupiter – Management System",
                900, 600
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
