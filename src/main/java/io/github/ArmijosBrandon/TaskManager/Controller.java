package io.github.ArmijosBrandon.TaskManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Controller {
	private Model model;
	private View view;
	private Connection conn=null;
	public Controller(Model model, View view) {
		this.model=model;
		this.view=view;
		
		try {
			this.conn=model.connect();
			
		} catch (SQLException e) {
			view.setAlerta(
	                "No se pudo conectar con la base de datos.\n\n" +
	                "Detalles: " + e.getMessage() + "\n\n" +
	                "Posibles soluciones:\n" +
	                "• Verifica que tienes permisos para crear archivos en esta carpeta.\n" +
	                "• Asegúrate de que el archivo 'TaskManager.db' no esté corrupto.\n" +
	                "• Cierra y vuelve a abrir la aplicación."
	        );
		}
		try {
			model.crearTablaTareas(conn);
		} catch (SQLException e) {
			view.setAlerta(
	                "No se pudo crear/verificar la tabla de tareas.\n\n" +
	                "Detalles: " + e.getMessage() + "\n\n" +
	                "Esto suele ocurrir solo la primera vez si hay un problema con la base de datos.\n" +
	                "Solución: Cierra la aplicación. Si el error persiste, borra el archivo 'TaskManager.db'."
			);
		}
		
	}
	public void cargarTareasTabla() {
		TableView<Tarea> tabla_tareas= view.getTablaTareas();
		try {
			tabla_tareas.setItems(model.obtenerTareas(conn));
		} catch (SQLException e) {
			 view.setAlerta(
		                "No se pudieron cargar las tareas desde la base de datos.\n\n" +
		                "Posibles causas:\n" +
		                "   • El archivo 'TaskManager.db' está siendo usado por otro programa.\n" +
		                "   • La base de datos está corrupta.\n" +
		                "   • Algún dato almacenado es inválido.\n\n" +
		                "Qué puedes hacer:\n" +
		                "   • Cierra y vuelve a abrir la aplicación.\n" +
		                "   • Asegúrate de que ninguna otra aplicación esté usando la base de datos.\n" +
		                "   • Si el problema continúa, elimina el archivo 'TaskManager.db'.\n\n" +
		                "Detalles: " + e.getMessage()
		     );
		}
	}
	public void close() {
	    if (conn != null) {
	        try {
	            conn.close();
	        } catch (SQLException e) {
	        	view.setAlerta(
	                    "No se pudo cerrar correctamente la conexión con la base de datos.\n\n" +
	                    "Tus datos no se han perdido. Este error no afecta el funcionamiento de la aplicación.\n\n" +
	                    "Qué puedes hacer:\n" +
	                    "   • Simplemente vuelve a abrir la aplicación.\n" +
	                    "   • Si el error aparece repetidamente, reinicia tu computadora.\n\n" +
	                    "Detalles: " + e.getMessage()
	        	);
	        }
	    }
	}


}