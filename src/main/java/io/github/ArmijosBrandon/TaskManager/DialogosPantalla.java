package io.github.ArmijosBrandon.TaskManager;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;

//Clase utilitaria para mostrar diálogos y alertas en la aplicación
public final class DialogosPantalla {//para evitar instanciar y otros, que quede como clase utilaria

    // Evita instanciación
    private DialogosPantalla() {}

    // ERROR
    public static void showError(String error) {//static por que son metodos que se van a usar sin necesidad de instanciar un objeto
		Alert alerta = new Alert(AlertType.ERROR);
		alerta.setTitle("!Error");
		alerta.setHeaderText("Algo salio mal...."); 
		alerta.setContentText(error);
		alerta.showAndWait();
	}
	
    //CONFIRMACION
	public static Boolean getConfirmacion(String header, String content) {
		Alert alerta = new Alert(AlertType.CONFIRMATION);
		alerta.setTitle("Confirmacion");
		alerta.setHeaderText(header); 
		alerta.setContentText(content);
		
		Optional<ButtonType> result = alerta.showAndWait(); // me devolvera el boton que haiga presionado el usuario, Optional es un contenedor que puede tener un valor o estar vacío. En este caso, normalmente tendrá un botón, pero JavaFX lo usa para evitar errores si la ventana se cierra de forma inesperada.
		if (result.isPresent() && result.get() == ButtonType.OK) { //is present sera verdadero si hay algo en el optional
		    // El usuario confirmó
			return true;
		} else {
			// El usuario cancelo o cerro el dialogo forsozamente
			return false;
		}
	}
}

