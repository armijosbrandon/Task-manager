package io.github.ArmijosBrandon.TaskManager;


import java.sql.SQLException;

import io.github.ArmijosBrandon.TaskManager.controller.Controller;
import io.github.ArmijosBrandon.TaskManager.data.DataBaseManager;
import io.github.ArmijosBrandon.TaskManager.view.MainView;
import javafx.application.Application;


import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage) {   
        
    	try {
			DataBaseManager.getInstance().connect(); //abrimos una conexion a la base de datos global
		} catch (SQLException e) {
			DialogosPantalla.showError(
	                "No se pudo conectar con la base de datos.\n\n" +
	                "Detalles: " + e.getMessage() + "\n\n" +
	                "Posibles soluciones:\n" +
	                "• Verifica que tienes permisos para crear archivos en esta carpeta.\n" +
	                "• Asegúrate de que el archivo 'TaskManager.db' no esté corrupto.\n" +
	                "• Cierra y vuelve a abrir la aplicación.");
		}
    	
    	Controller controller = null; 
        try {
            MainView view = new MainView(stage);
            controller = new Controller(view); 
            view.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

        Controller finalController = controller; // para usarlo en lambda(e->) por que no permite si no es un objeto final tons creo una copia fija
        stage.setOnCloseRequest(e -> finalController.close()); //cerrar la conexion de sqlite cuando se cierre el programa
    }

    public static void main(String[] args) {
        launch();
    }
}

