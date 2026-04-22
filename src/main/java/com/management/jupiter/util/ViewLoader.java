package com.management.jupiter.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Utilidad de navegación entre vistas FXML.
 * Carga el archivo FXML indicado y lo muestra en el Stage proporcionado.
 */
public class ViewLoader {

    private ViewLoader() {}

    /**
     * Navega a una nueva vista reemplazando el contenido del Stage actual.
     *
     * @param stage   Stage donde se mostrará la nueva vista.
     * @param fxml    Ruta del FXML desde el classpath (ej. "/views/login.fxml").
     * @param title   Título de la ventana.
     * @param width   Ancho preferido de la ventana.
     * @param height  Alto preferido de la ventana.
     */
    public static void navigate(Stage stage, String fxml, String title, double width, double height) {
        try {
            URL resource = ViewLoader.class.getResource(fxml);
            if (resource == null) {
                throw new IOException("FXML not found: " + fxml);
            }
            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root, width, height);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Error loading view: " + fxml, e);
        }
    }
}
