package io.github.ArmijosBrandon.TaskManager;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * Clase utilitaria para mostrar diálogos y alertas en la aplicación.
 * Centraliza todos los mensajes para que la vista/controlador se mantengan limpios.
 */
public final class DialogUtils {

    // Evita instanciación
    private DialogUtils() {}

    // ===========================
    //       ALERTAS
    // ===========================

    /**
     * Muestra una alerta de información.
     */
    public static void showInfo(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title != null ? title : "Información");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de advertencia.
     */
    public static void showWarning(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title != null ? title : "Advertencia");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de error.
     */
    public static void showError(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title != null ? title : "Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===========================
    //     CONFIRMACIONES
    // ===========================

    /**
     * Muestra un diálogo de confirmación "Aceptar / Cancelar".
     *
     * @return true si el usuario presionó ACEPTAR, false en caso contrario.
     */
    public static boolean confirm(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title != null ? title : "Confirmación");
        alert.setHeaderText(header);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // ===========================
    //       INPUT DIALOG
    // ===========================

    /**
     * Muestra un cuadro de entrada de texto.
     *
     * @return El texto ingresado o null si se canceló.
     */
    public static String showTextInput(String title, String header, String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title != null ? title : "Ingresar texto");
        dialog.setHeaderText(header);
        dialog.setContentText(prompt);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

}

