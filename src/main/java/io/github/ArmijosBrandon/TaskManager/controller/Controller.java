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
	
	
	
	//elementos del form
	private int num_tarea;
	private String nombre_tarea;
	private LocalDate fecha_inicio;
	private LocalDate fecha_final;
	private String prioridad;
	private String estado;
	private String categoria;
	private String observacion;
	private Tarea  tarea_activa;
	
	
	private FormFiltrarView formFiltrarView;
	private Popup popupCategoria;
	private Popup popupPrioridades;
	private Popup popupEstados;
	private HBox columnasCategorias;
	private ComboBox<String> comboCategorias;
	private ComboBox<String> comboPrioridades;
	private ComboBox<String> comboEstados;
	private Set<String> categoriasSeleccionadas = new HashSet<>();//coleccion que no permite elementos repetidos
	private Set<String> prioridadesSeleccionadas = new HashSet<>();
	private Set<String> estadosSeleccionados = new HashSet<>();

	private ControladorTareas controladorTareas;

	
	
	public Controller( MainView mainView) {
		this.mainView=mainView;
		
		inicializarConexion();//conecta a la base de datos
		inicializarTablas();//crea tablas si no existen
		inicializarRepos();
        
        inicializarFormularios();
        inicializarTareasTabla();
        
        inicializarEventosBotones(); //cargar los eventos de los botones
       
        inicialiizarControladores();
        
        
	}
	
	private void inicialiizarControladores() {
		controladorTareas= new ControladorTareas(tabla_tareas, form, repoTareas, repoCategorias);
		
		
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
         popupCategoria= formFiltrarView.getPopupCategorias();
         comboCategorias= formFiltrarView.getCategoriaCombo();
         comboPrioridades= formFiltrarView.getPrioridadCombo();
         comboEstados=formFiltrarView.getEstadoCombo();
         columnasCategorias = formFiltrarView.getCategoriaCheckContainer();
  
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
        	formFiltrarView.setAutoHide(true);//hacer que se cierre al clickear fuera de el
        	if (!formFiltrarView.isShowing()) {//solo mostrar si no se esta mostrando, para evitar que espame el boton y salgan muchas
        		 formFiltrarView.show(btnFiltrar,//objeto de referencia para posicion del popup
        				 btnFiltrar.localToScreen(0, btnFiltrar.getHeight()).getX(),//0 px desde el boton en x
        				 btnFiltrar.localToScreen(0, btnFiltrar.getHeight()).getY());
        	    }
        	});
 
        
   //----------------------BOTONES DE FILTRADO-------------------
        comboCategorias.setOnMouseClicked(e -> {
        	
        	ObservableList<String> categorias= controladorTareas.getCategorias();
        	// --- GUARDAR SELECCIÓNES ANTERIORES SI ES QUE HAY ANTES DE LIMPIAR ---
        	guardarSeleccionCategorias(columnasCategorias);

        	// --- TOGGLE DEL POPUP ---
        	popupCategoria.getContent().clear();

        	if (popupCategoria.isShowing()) {
        		popupCategoria.hide();
        		return;
        	}

        	
        	int totalCategorias =  categorias.size();
        	int categoriasPorColumna = 5;

        	// número de columnas necesarias
        	//int math.ceil() redondea hacia arriba un resultado en decimal de una division y lo convierte a int
        	int columnas = (int) Math.ceil((double) totalCategorias / categoriasPorColumna); //(double) totalCategorias convierte a double totalCategorias para obtener un resultado en decimal
        	columnasCategorias.getChildren().clear();

        	for (int col = 0; col < columnas; col++) {

        		int desde = col * categoriasPorColumna;
        		int hasta = Math.min(desde + categoriasPorColumna, totalCategorias);

        		List<String> categoriasEnColumna = categorias.subList(desde, hasta);

        		VBox columna = new VBox(5);

        		for (String categoria : categoriasEnColumna) {
        			CheckBox check = new CheckBox(categoria);
        			check.setSelected(categoriasSeleccionadas.contains(categoria)); // Si categorias Seleccionadas contiene esa categoria, marca al checkbox, si no , lo deja desactivado
        			columna.getChildren().add(check);
        		}

        		columnasCategorias.getChildren().add(columna);
        	}

        	// --- MOSTRAR POPUP ---
        	popupCategoria.getContent().add(columnasCategorias);
        	popupCategoria.setAutoHide(true);
        	popupCategoria.show(
        			comboCategorias,
        			comboCategorias.localToScreen(0, comboCategorias.getHeight()).getX(),
        			comboCategorias.localToScreen(0, comboCategorias.getHeight()).getY()
        			);
        });
        
        comboPrioridades.setOnMouseClicked(e->{
        	popupPrioridades= formFiltrarView.getPopupPrioridades();
        	mostrarPopupDebajo(popupPrioridades, comboPrioridades);
        });
        
        comboEstados.setOnMouseClicked(e->{
        	popupEstados= formFiltrarView.getPopupEstados();
        	mostrarPopupDebajo(popupEstados, comboEstados);
        });
        
        formFiltrarView.getBtnFiltrar().setOnAction(e->{
        	guardarFiltrosSeleccionados(formFiltrarView.getPrioridadCheckContainer(),prioridadesSeleccionadas);
        	guardarFiltrosSeleccionados( formFiltrarView.getEstadoCheckContainer(), estadosSeleccionados);
        	guardarSeleccionCategorias(formFiltrarView.getCategoriaCheckContainer());
        	try {
        		if(prioridadesSeleccionadas.isEmpty() && estadosSeleccionados.isEmpty() && categoriasSeleccionadas.isEmpty()) {
        			tabla_tareas.limpiarTabla(); //limpiamos para que no se acumule la lista de las tareas obtenidas con las de la tabla original
        			inicializarTareasTabla();
        		}else {
        			ObservableList<Tarea>tareas_filtradas= repoTareas.filtrarTabla(categoriasSeleccionadas, prioridadesSeleccionadas, estadosSeleccionados);
        			if(!tareas_filtradas.isEmpty()) {
        				
        				tabla_tareas.remplazarContenido(tareas_filtradas);
        			}else {
        				tabla_tareas.limpiarTabla();
        				tabla_tareas.setPlaceHolder("No hay ninguna tarea que coincida con esos criterios.");
        			}
        			
        			
        		}
				
			} catch (SQLException e1) {
				DialogosPantalla.showError(
					    "No se pudieron obtener las tareas filtradas desde la base de datos.\n\n" +
					    "Posibles causas:\n" +
					    "   • La conexión con la base de datos falló o está cerrada.\n" +
					    "   • Alguno de los filtros seleccionados no coincide con los valores almacenados.\n" +
					    "   • El archivo 'TaskManager.db' está dañado o bloqueado por otro programa.\n" +
					    "   • Existen valores nulos o inesperados en las columnas 'categoria', 'prioridad' o 'estado'.\n\n" +
					    "Qué puedes hacer:\n" +
					    "   • Verifica que la base de datos no esté siendo usada por otra aplicación.\n" +
					    "   • Asegúrate de seleccionar filtros válidos.\n" +
					    "   • Reinicia la aplicación y vuelve a intentar.\n" +
					    "   • Si el problema persiste, elimina 'TaskManager.db' para regenerarlo.\n\n" +
					    "Detalles técnicos:\n" + e1.getMessage()
					);
			}
        	
        });
        

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
	
	
	public void mostrarPopupDebajo(Popup popup, ComboBox<String> dueño) {
	    // Si ya está abierto → cerrarlo (toggle)
	    if (popup.isShowing()) {
	        popup.hide();
	        return;
	    }

	    popup.setAutoHide(true);
	    popup.show(dueño, dueño.localToScreen(0,dueño.getHeight()).getX(),
	    		dueño.localToScreen(0,dueño.getHeight()).getY()
    		);
	}
	
	private void guardarSeleccionCategorias(HBox columnasCategorias) {
	    categoriasSeleccionadas.clear();//Antes de guardar las nuevas selecciones, borra las anteriores
	    for (Node colNode : columnasCategorias.getChildren()) {//recorro cada columna
	        if (colNode instanceof VBox col) { //verifica que sea un vbox
	            for (Node node : col.getChildren()) {//reccore cada checkbox
	                if (node instanceof CheckBox cb && cb.isSelected()) {//si es un checkbox y esta seleccionado
	                    categoriasSeleccionadas.add(cb.getText());
	                }
	            }
	        }
	    }
	}
	
	private void guardarFiltrosSeleccionados(VBox contenedor, Set<String> filtros_seleccionados) {
		filtros_seleccionados.clear();
		
		for(Node node:contenedor.getChildren()) {
			if(node instanceof CheckBox cb && cb.isSelected()) {
				filtros_seleccionados.add(cb.getText());
				
			}
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