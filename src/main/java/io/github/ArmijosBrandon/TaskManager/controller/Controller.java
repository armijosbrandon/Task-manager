package io.github.ArmijosBrandon.TaskManager.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import io.github.ArmijosBrandon.TaskManager.DialogosPantalla;
import io.github.ArmijosBrandon.TaskManager.TablaTareasView;
import io.github.ArmijosBrandon.TaskManager.Data.CategoriasRepository;
import io.github.ArmijosBrandon.TaskManager.Data.DataBaseManager;
import io.github.ArmijosBrandon.TaskManager.Data.SearchRepository;
import io.github.ArmijosBrandon.TaskManager.Data.TareasRepository;
import io.github.ArmijosBrandon.TaskManager.model.Tarea;
import io.github.ArmijosBrandon.TaskManager.view.FormFiltrarView;
import io.github.ArmijosBrandon.TaskManager.view.FormularioTareasView;
import io.github.ArmijosBrandon.TaskManager.view.MainView;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;


public class Controller {
	private MainView mainView;
	//-----CONEXION CON BASE DE DATOS
	private Connection conn;
	private TareasRepository repoTareas;
	private CategoriasRepository repoCategorias;
	private SearchRepository repoSearch;
	
	//-----TABLA DE TAREAS 
	private TablaTareasView tabla_tareas;
	
	//------FORMULARIO DE TARAS
	private FormularioTareasView form;
	
	
	
	
	private FormFiltrarView formFiltrarView;
	
	

	private ControladorTareas controladorTareas;
	ControladorFiltros controladorFiltros;

	
	
	public Controller( MainView mainView) {
		this.mainView=mainView; //vista principal del programa
		
		//base de datos
		inicializarConexion();//conecta a la base de datos
		inicializarTablas();//crea tablas si no existen
		inicializarRepos();
        
        inicializarFormularios();
        inicializarTareasTabla(); //cargar tareas de la base a la tabla
        
        inicializarControladores();
        inicializarEventosBotones(); //cargar los eventos de los botones
       
        
        
        
	}
	
	private void inicializarControladores() {
		controladorTareas= new ControladorTareas(tabla_tareas, form, repoTareas, repoCategorias);
		controladorFiltros= new ControladorFiltros(formFiltrarView, tabla_tareas, repoTareas);
		
		
	}

	//------------ CREACIÓN DE TABLAS ------------------------------------------------
    private void inicializarTablas() {
        try {
         DataBaseManager.getInstance().iniciarBaseDatos();
        } catch (SQLException e) {
        	DialogosPantalla.showError(
                    "No se pudieron cargar las tareas desde la base de datos.\n\n" +
                    "Detalles: " + e.getMessage() + "\n\n" +
                    "Esto puede deberse a:\n" +
                    "• La base de datos está dañada.\n" +
                    "• La tabla 'Tareas' no existe o fue eliminada.\n" +
                    "• Un error inesperado interno de SQLite.\n\n" +
                    "Solución recomendada:\n" +
                    "1. Cierra la aplicación.\n" +
                    "2. Borra el archivo 'TaskManager.db'.\n" +
                    "3. Inicia nuevamente el programa para que se regenere automáticamente."
                );
        }
    }

    
    public void inicializarFormularios() {
    	 form = mainView.getFormularioTareasView();
    	 
    	 //Form de filtrado y sus elementos
    	 formFiltrarView = new FormFiltrarView();
         
  
    }


    //------------ EVENTOS DE BOTONES -------------------------------------------------
    private void inicializarEventosBotones() {
        
        //evento botón "Nueva Tarea"
        mainView.getBtnNuevaTarea().setOnAction(e -> controladorTareas.nuevaTarea());   
        
        //evento de editar tarea seleccionada
        mainView.getBtnEditarTarea().setOnAction(e->controladorTareas.editarTarea());
        
        
        mainView.getBtnBorrarTarea().setOnAction(e->controladorTareas.eliminarTarea());
        
        //Boton de marcar tarea como "en progreso"
        mainView.getBtnMarcarProgresoTarea().setOnAction(e->controladorTareas.marcarTareaEnProgreso());
        
      //Boton de marcar tarea como "Completada"
        mainView.getBtnCompletarTarea().setOnAction(e->controladorTareas.marcarTareaCompletada());
        
        mainView.getBtnCargarTareasPrueba().setOnAction(e -> controladorTareas.cargarTareasPrueba());
        
        mainView.getBtnResetearTareas().setOnAction(e->controladorTareas.resetearTablaTareas());
        
        //boton de filtrar
        mainView.getBtnFiltrarTarea().setOnAction(e->{
        	Button btnFiltrar=mainView.getBtnFiltrarTarea();
        	controladorFiltros.mostrarPopUpFiltrar(btnFiltrar);
        	});
 
        
   //----------------------BOTONES DE FILTRADO-------------------
        
        

//----------------------BOTON DE BUSQUEDA-------------------
        mainView.getTxtBusqueda().setOnAction(e->{
        	buscarTareas();
        });
        mainView.getBtnBusqueda().setOnAction(e->{
        	buscarTareas();
        });
        



        

    }



	private void buscarTareas() {
		String Busqueda= mainView.getTxtBusqueda().getText();
    	try {
    		if(Busqueda.trim().isEmpty()) {
    			tabla_tareas.limpiarTabla();
    			inicializarTareasTabla();

    		}else {
    			ObservableList<Tarea> tareas_buscadas = repoSearch.buscarTareas(Busqueda);
    			if (!tareas_buscadas.isEmpty()) {
    			    tabla_tareas.remplazarContenido(tareas_buscadas);
    			} else {
    			    tabla_tareas.setPlaceHolder("No hay resultados.");
    			    tabla_tareas.limpiarTabla();
    			}

    		}
			
		} catch (SQLException e1) {
			DialogosPantalla.showError(
				    "No se pudieron obtener las tareas buscadas desde la base de datos.\n\n" +
				    "Posibles causas:\n" +
				    "   • La conexión con la base de datos falló o está cerrada.\n" +
				    "   • El archivo 'TaskManager.db' está dañado o bloqueado por otro programa.\n" +
				    "   • Existen valores nulos o inesperados en el campo de busqueda" +
				    "Qué puedes hacer:\n" +
				    "   • Verifica que la base de datos no esté siendo usada por otra aplicación.\n" +
				    "   • Asegúrate de seleccionar criterios de busqueda válidos.\n" +
				    "   • Reinicia la aplicación y vuelve a intentar.\n" +
				    "   • Si el problema persiste, elimina 'TaskManager.db' para regenerarlo.\n\n" +
				    "Detalles técnicos:\n" + e1.getMessage()
				);
		}
		
	}

	private void inicializarConexion() {
        conn= DataBaseManager.getInstance().getConnection();
    }
	
	private void inicializarRepos() {
		repoTareas = new TareasRepository(conn);
		repoCategorias=new CategoriasRepository(conn);
		repoSearch= new SearchRepository(conn);
	}
	
	public void inicializarTareasTabla() {
		//obtener tabla con tareas actuales en la bd
		tabla_tareas= mainView.getTablaTareasView();
		//vincula una lista para la tabla donde va a cargar celdas, va a hacer un for interno por cada tarea al cual le va a hacer los gettes establecidos en cada columna
		try {
			tabla_tareas.setContenidoPrincipal(repoTareas.obtenerTareas());
		} catch (SQLException e) {
			
		}//ademas establece la lista a la que se la va a aplciar los cambios realizados en la tabla
		if(tabla_tareas.getContenido().isEmpty()) {//para que en operaciones como en la de buscar si es que regresesa a la tabla normal y no hay tareas que cargue el siguiente place holder
			tabla_tareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
		}
	}
	
		//funcion para cerrar comunicacion cuando se cierra la app
	public void close() {
		try {
			DataBaseManager.getInstance().close();
		} catch (SQLException e) {

		}
	}


}