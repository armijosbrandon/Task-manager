package io.github.ArmijosBrandon.TaskManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import org.controlsfx.control.textfield.TextFields;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class Controller {
	private Model model;
	private View view;
	private TableView<Tarea> tabla_tareas;
	private ObservableList<String> categorias = null;
	private VBox formNuevaTarea;
	public Controller(Model model, View view) {
		this.model=model;
		this.view=view;
		
		inicializarConexion();//conecta a la base de datos
        inicializarTablas();//crea tablas si no existen
        cargarCategorias(); //carga las categorias comunes del usuario
        TextFields.bindAutoCompletion(view.getTxtCategoria(), categorias); //inicializar autocompletado de categorias obtenido en el metodo anterior
        inicializarFormularios();
        inicializarEventosBotones(); //cargar los eventos de los botones
        inicializarTareasTabla();
        
	}
	
	//------------ CREACIÓN DE TABLAS ------------------------------------------------
    private void inicializarTablas() {
        try {
            model.crearTablaTareas();
            model.crearTablaCategorias();
        } catch (SQLException e) {
            view.setAlerta(
                "No se pudo crear/verificar la tabla de tareas.\n\n" +
                "Detalles: " + e.getMessage() + "\n\n" +
                "Esto suele ocurrir solo la primera vez si hay un problema con la base de datos.\n" +
                "Solución: Cierra la aplicación. Si el error persiste, borra el archivo 'TaskManager.db'."
            );
        }
    }

    //------------ CARGA DE CATEGORÍAS ----------------------------------------------
    private void cargarCategorias() {
        try {
            categorias = model.obtenerCategorias();
        } catch (SQLException e) {
            view.setAlerta(
                "No se pudieron cargar las categorías desde la base de datos.\n\n" +
                "Posibles causas:\n" +
                "   • El archivo 'TaskManager.db' está siendo usado por otro programa.\n" +
                "   • La base de datos está corrupta o inaccesible.\n\n" +
                "Qué puedes hacer:\n" +
                "   • Cierra y vuelve a abrir la aplicación.\n" +
                "   • Asegúrate de que ningún otro programa esté usando la base de datos.\n" +
                "   • Si el problema persiste, considera eliminar el archivo 'TaskManager.db' para crear uno nuevo.\n\n" +
                "Detalles técnicos:\n" + e.getMessage()
            );
        }
    }
    
    public void inicializarFormularios() {
    	 formNuevaTarea = view.getformNuevaTarea();
    }


    //------------ EVENTOS DE BOTONES -------------------------------------------------
    private void inicializarEventosBotones() {
        
        //evento botón "Nueva Tarea"
        view.getBtnNuevaTarea().setOnAction(e -> {
        	resetearComponentes();
            formNuevaTarea.setVisible(true);
            formNuevaTarea.setManaged(true);//que no ocupe espacio cuando esté oculto
        });
        
        view.getBtnCancelarNTarea().setOnAction(e->{
        	formNuevaTarea.setVisible(false);
            formNuevaTarea.setManaged(false);
        });

        //evento botón "Guardar Tarea"
        view.getBtnGuardarTarea().setOnAction(e -> {
            String nombre_tarea = view.getTxtNombre_tarea().getText();
            LocalDate fecha_inicio = view.getFecha_inicio().getValue();
            LocalDate fecha_final = view.getFecha_final().getValue();
            String prioridad = view.getCbPrioridad().getValue();
            String estado = view.getCbEstado().getValue();
            String categoria = view.getTxtCategoria().getText();
            String observacion = view.getTxtObservacion().getText();

            try {
                model.nuevaTarea(nombre_tarea, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion);
            } catch (SQLException e1) {
                view.setAlerta(
                    "No se pudo guardar la nueva tarea en la base de datos.\n\n" +
                    "Posibles causas:\n" +
                    "   • La base de datos está siendo usada por otro programa.\n" +
                    "   • El archivo 'TaskManager.db' está corrupto.\n" +
                    "   • Algún dato ingresado contiene caracteres no válidos.\n" +
                    "   • La tabla 'Tareas' o 'Categorias' no existe o está dañada.\n\n" +
                    "Qué puedes hacer:\n" +
                    "   • Cierra y vuelve a abrir la aplicación.\n" +
                    "   • Revisa que las fechas ingresadas sean válidas.\n" +
                    "   • Asegúrate de que ningún otro programa esté usando la base de datos.\n" +
                    "   • Si el problema continúa, elimina el archivo 'TaskManager.db' para crear uno nuevo.\n\n" +
                    "Detalles técnicos:\n" + e1.getMessage()
                );
                return; // Evita continuar si falló
            }

            //si la categoria es nueva se guardara en las categorias
            if (!categorias.contains(categoria)) {
                categorias.add(categoria);
            }

            formNuevaTarea.setVisible(false);
            formNuevaTarea.setManaged(false);
        });
    }
    
	private void resetearComponentes() {
		view.setTxtNombre_tarea("");
		view.setFecha_inicio(LocalDate.now());
		view.setFecha_final(LocalDate.now().plusDays(5));
		view.setTxtCategoria("");
		view.setCbPrioridad(1);
		view.setCbEstado(1);
		view.setTxtObservacion("");
		
	}

	private void inicializarConexion() {
        try {
            model.setConnection(model.connect());  
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
    }
	public void inicializarTareasTabla() {

		//obtener tabla con tareas actuales en la bd
		tabla_tareas= view.getTablaTareas();
		try {
			tabla_tareas.setItems(model.obtenerTareas());//cargar celdas, va a hacer un for interno por cada tarea al cual le va a hacer los gettes establecidos en cada columna
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
	
	//funcion para cerrar comunicacion cuando se cierra la app
	public void close() {
		Connection conn= model.getConnection();
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