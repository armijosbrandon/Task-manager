package io.github.ArmijosBrandon.TaskManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

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
	
	
	private ObservableList<String> categorias = null;
	private AutoCompletionBinding<String> autoCategoria; //guarda la instacia(lista_sugerencias) actal de autocompletado de un TextFields.bindAutoCompletion()
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


	
	
	public Controller( MainView mainView) {
		this.mainView=mainView;
		
		inicializarConexion();//conecta a la base de datos
		inicializarRepos();
        inicializarTablas();//crea tablas si no existen
        inicializarFormularios();
        cargarCategorias(); //carga las categorias comunes del usuario
        inicializarTareasTabla();
        
        inicializarEventosBotones(); //cargar los eventos de los botones
       
        
        
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

    //------------ CARGA DE CATEGORÍAS ----------------------------------------------
    private void cargarCategorias() {
        try {
            categorias = repoCategorias.obtenerCategorias();
            
            //para cambiar las sugerencias del autcompletado primero tengo que eliminar las anteriores
            if (autoCategoria != null) {// si no es null
                autoCategoria.dispose();//se destruye 
            }
            autoCategoria =TextFields.bindAutoCompletion(form.getCategoriaTextField(), categorias); //inicializar autocompletado de categorias obtenido en el metodo anterior
        } catch (SQLException e) {
            DialogosPantalla.showError(
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
        
    	Button btnGuardarTarea= form.getBtnGuardarTarea();
    	Button btnComfirmarCambios= form.getBtnConfirmarCambios();
        //evento botón "Nueva Tarea"
        mainView.getBtnNuevaTarea().setOnAction(e -> {
        	resetearForm();
        	btnGuardarTarea.setVisible(true);
        	btnGuardarTarea.setManaged(true);
        	btnComfirmarCambios.setVisible(false);
        	btnComfirmarCambios.setManaged(false);
            form.setVisible(true);
            form.setManaged(true);//que no ocupe espacio cuando esté oculto
        });   
        
        //evento de editar tarea seleccionada
        mainView.getBtnEditarTarea().setOnAction(e->{
        	if(tabla_tareas.getTareaSeleccionada()!=null) {
        		llenarFormCampoActivo();
        		form.setVisible(true);
            	form.setManaged(true);
            	btnComfirmarCambios.setVisible(true);
            	btnComfirmarCambios.setManaged(true);
            	btnGuardarTarea.setVisible(false);
            	btnGuardarTarea.setManaged(false);
        	}else{
        		DialogosPantalla.showError("Selecciona una fila para poder editar.");
        	}	
        });
        
        
        mainView.getBtnBorrarTarea().setOnAction(e->{
        	tarea_activa=tabla_tareas.getTareaSeleccionada();
        	if(tarea_activa!=null) {
        		if(DialogosPantalla.getConfirmacion("¿Estas seguro de eliminar esta tarea?","Se eliminara esta tarea permamentemente")) {
            		try {
        				repoTareas.borrarTarea(tarea_activa);
        				tabla_tareas.refrescar();
        				cargarCategorias();//volvemos a vincular las categorias a el textfield
        				if(tabla_tareas.getContenido().isEmpty()) {
        					tabla_tareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
        				}
        			} catch (SQLException e1) {
        				DialogosPantalla.showError(
        					    "No se pudo eliminar la tarea.\n\n" +
        					    "Posibles causas:\n" +
        					    "   • La base de datos está en uso por otro programa.\n" +
        					    "   • El archivo 'TaskManager.db' está dañado.\n" +
        					    "   • La tarea ya no existe o no pudo accederse.\n\n" +
        					    "Recomendaciones:\n" +
        					    "   • Cierra y vuelve a abrir la aplicación.\n" +
        					    "   • Asegúrate de que la base de datos no esté siendo utilizada.\n\n" +
        					    "Detalles técnicos:\n" + e1.getMessage()
        					);
        			}
            	}
        	}else {
        		DialogosPantalla.showError("No hay ninguna fila seleccionada");
        	}
        	
        	
        });
        
        //Boton de marcar tarea como "en progreso"
        mainView.getBtnMarcarProgresoTarea().setOnAction(e->{
        	tarea_activa=tabla_tareas.getTareaSeleccionada();
        	if(tarea_activa!=null) {
	        	tarea_activa=tabla_tareas.getTareaSeleccionada();
	        	try {
					repoTareas.marcarProgresoTarea(tarea_activa);
				} catch (SQLException e1) {
					DialogosPantalla.showError(
						    "No se pudo actualizar el estado de la tarea.\n\n" +
						    "Posibles causas:\n" +
						    "   • La base de datos está en uso por otro programa.\n" +
						    "   • El archivo 'TaskManager.db' está dañado.\n" +
						    "   • La tarea ya no existe o no pudo modificarse.\n\n" +
						    "Recomendaciones:\n" +
						    "   • Cierra y vuelve a abrir la aplicación.\n" +
						    "   • Verifica que la base de datos no esté siendo utilizada.\n\n" +
						    "Detalles técnicos:\n" + e1.getMessage()
						);
				}
	        	tabla_tareas.refrescar();
        	}else {
        		DialogosPantalla.showError("No hay ninguna fila seleccionada");
        	}
        });
        
      //Boton de marcar tarea como "Completada"
        mainView.getBtnCompletarTarea().setOnAction(e->{
        	tarea_activa=tabla_tareas.getTareaSeleccionada();
        	if(tarea_activa!=null) {
	        	tarea_activa=tabla_tareas.getTareaSeleccionada();
	        	try {
					repoTareas.marcarCompletaTarea(tarea_activa);
				} catch (SQLException e1) {
					DialogosPantalla.showError(
						    "No se pudo actualizar el estado de la tarea.\n\n" +
						    "Posibles causas:\n" +
						    "   • La base de datos está en uso por otro programa.\n" +
						    "   • El archivo 'TaskManager.db' está dañado.\n" +
						    "   • La tarea ya no existe o no pudo modificarse.\n\n" +
						    "Recomendaciones:\n" +
						    "   • Cierra y vuelve a abrir la aplicación.\n" +
						    "   • Verifica que la base de datos no esté siendo utilizada.\n\n" +
						    "Detalles técnicos:\n" + e1.getMessage()
						);
				}
	        	tabla_tareas.refrescar();
        	}else {
        		DialogosPantalla.showError("No hay ninguna fila seleccionada");
        	}
        });
        
        
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
        	// --- GUARDAR SELECCIÓNES ANTERIORES SI ES QUE HAY ANTES DE LIMPIAR ---
        	guardarSeleccionCategorias(columnasCategorias);

        	// --- TOGGLE DEL POPUP ---
        	popupCategoria.getContent().clear();

        	if (popupCategoria.isShowing()) {
        		popupCategoria.hide();
        		return;
        	}

        	
        	int totalCategorias = categorias.size();
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
        
// -------------------------BOTONES DE TAREAS DE PRUEBA -----------------
        mainView.getBtnCargarTareasPrueba().setOnAction(e -> {
        	if (DialogosPantalla.getConfirmacion("¿Estas seguro de cargar las tareas de prueba?", "Tus tareas personales se borraran de forma permamente"))
        		try {
        			borrarTareas();
        			repoTareas.nuevaTarea("Estudiar matemáticas", LocalDate.of(2025,1,10), LocalDate.of(2025,1,12), "Estudios", "Alta", "Pendiente", "Repasar ecuaciones y álgebra");
        			repoTareas.nuevaTarea("Comprar víveres", LocalDate.of(2025,1,8), LocalDate.of(2025,1,8), "Hogar", "Media", "Pendiente", "Comprar arroz, leche y verduras");
        			repoTareas.nuevaTarea("Llamar al médico", LocalDate.of(2025,1,5), LocalDate.of(2025,1,5), "Salud", "Alta", "Pendiente", "Solicitar cita de control");
        			repoTareas.nuevaTarea("Preparar presentación", LocalDate.of(2025,1,10), LocalDate.of(2025,1,15), "Trabajo", "Alta", "En progreso", "Avanzar diapositivas");
        			repoTareas.nuevaTarea("Hacer ejercicio", LocalDate.of(2025,1,2), LocalDate.of(2025,1,2), "Personal", "Baja", "Completada", "30 minutos de cardio");
        			repoTareas.nuevaTarea("Leer libro de Java", LocalDate.of(2025,1,3), LocalDate.of(2025,1,20), "Estudios", "Media", "En progreso", "Capítulo sobre colecciones");
        			repoTareas.nuevaTarea("Organizar escritorio", LocalDate.of(2025,1,6), LocalDate.of(2025,1,6), "Hogar", "Baja", "Pendiente", "Ordenar cables y papeles");
        			repoTareas.nuevaTarea("Enviar currículum", LocalDate.of(2025,1,14), LocalDate.of(2025,1,14), "Trabajo", "Alta", "Pendiente", "Enviar a 3 empresas");
        			repoTareas.nuevaTarea("Limpiar la cocina", LocalDate.of(2025,1,2), LocalDate.of(2025,1,2), "Hogar", "Media", "Pendiente", "Fregar platos y limpiar estufa");
        			repoTareas.nuevaTarea("Vaciar papeleras", LocalDate.of(2025,1,3), LocalDate.of(2025,1,3), "Hogar", "Baja", "Pendiente", "Todas las habitaciones");
        			repoTareas.nuevaTarea("Actualizar portafolio", LocalDate.of(2025,1,18), LocalDate.of(2025,1,18), "Trabajo", "Alta", "En progreso", "Agregar proyecto JavaFX");
        			repoTareas.nuevaTarea("Planificar viaje", LocalDate.of(2025,1,25), LocalDate.of(2025,1,30), "Personal", "Media", "Pendiente", "Buscar hoteles y vuelos");
        			repoTareas.nuevaTarea("Revisar correo", LocalDate.of(2025,1,5), LocalDate.of(2025,1,5), "Trabajo", "Baja", "Completada", "Limpiar bandeja de entrada");
        			repoTareas.nuevaTarea("Practicar guitarra", LocalDate.of(2025,1,4), LocalDate.of(2025,1,4), "Personal", "Baja", "Pendiente", "Aprender nuevo acorde");
        			repoTareas.nuevaTarea("Hacer copia de seguridad", LocalDate.of(2025,1,12), LocalDate.of(2025,1,12), "Tecnología", "Alta", "Pendiente", "Respaldar documentos");
        			repoTareas.nuevaTarea("Pagar servicios", LocalDate.of(2025,1,9), LocalDate.of(2025,1,9), "Hogar", "Alta", "Pendiente", "Luz y agua");
        			repoTareas.nuevaTarea("Regar plantas", LocalDate.of(2025,1,3), LocalDate.of(2025,1,3), "Hogar", "Media", "Completada", "Regar todas las macetas");
        			repoTareas.nuevaTarea("Revisar proyecto Java", LocalDate.of(2025,1,6), LocalDate.of(2025,1,7), "Estudios", "Alta", "En progreso", "Corregir errores");
        			repoTareas.nuevaTarea("Ver tutorial de SQL", LocalDate.of(2025,1,11), LocalDate.of(2025,1,11), "Estudios", "Media", "Pendiente", "FTS5 y triggers");
        			repoTareas.nuevaTarea("Ordenar archivos del PC", LocalDate.of(2025,1,8), LocalDate.of(2025,1,8), "Tecnología", "Baja", "Pendiente", "Eliminar duplicados");
        			repoTareas.nuevaTarea("Sacar al perro", LocalDate.of(2025,1,2), LocalDate.of(2025,1,2), "Hogar", "Media", "Completada", "Paseo de 20 min");
        			repoTareas.nuevaTarea("Estudiar inglés", LocalDate.of(2025,1,13), LocalDate.of(2025,1,20), "Estudios", "Alta", "En progreso", "Repasar vocabulario");
        			repoTareas.nuevaTarea("Revisar finanzas", LocalDate.of(2025,1,16), LocalDate.of(2025,1,16), "Personal", "Alta", "Pendiente", "Organizar gastos del mes");
        			repoTareas.nuevaTarea("Limpiar coche", LocalDate.of(2025,1,19), LocalDate.of(2025,1,19), "Hogar", "Baja", "Pendiente", "Aspirar asientos");
        			repoTareas.nuevaTarea("Escribir ideas de proyecto", LocalDate.of(2025,1,12), LocalDate.of(2025,1,12), "Trabajo", "Media", "Pendiente", "Anotar nuevas funciones");
        			cargarCategorias();
        		} catch (SQLException e1) {
        			DialogosPantalla.showError(
        					"No se pudo generar las tareas de prueba en la base de datos.\n\n" +
        							"Posibles causas:\n" +
        							"   • La base de datos está siendo usada por otro programa.\n" +
        							"   • El archivo 'TaskManager.db' está corrupto.\n" +
        							"   • La tabla 'Tareas' o 'Categorias' no existe o está dañada.\n\n" +
        							"Qué puedes hacer:\n" +
        							"   • Cierra y vuelve a abrir la aplicación.\n" +
        							"   • Asegúrate de que ningún otro programa esté usando la base de datos.\n" +
        							"   • Si el problema continúa, elimina el archivo 'TaskManager.db' para crear uno nuevo.\n\n" +
        							"Detalles técnicos:\n" + e1.getMessage()
        					);
        		}

        });
        
        mainView.getBtnResetearTareas().setOnAction(e->{
        	if(DialogosPantalla.getConfirmacion("¿Estas seguro de resetear la tabla de tareas?", "Se perderan los cambios y tareas de forma permamente")) {
        		try {
					borrarTareas();
					cargarCategorias();
				} catch (SQLException e1) {
					DialogosPantalla.showError(
	                        "No se pudo resetear la tabla de tareas en la base de datos.\n\n" +
	                        "Posibles causas:\n" +
	                        "   • La base de datos está siendo usada por otro programa.\n" +
	                        "   • El archivo 'TaskManager.db' está corrupto.\n" +
	                        "   • La tabla 'Tareas' o 'Categorias' no existe o está dañada.\n\n" +
	                        "Qué puedes hacer:\n" +
	                        "   • Cierra y vuelve a abrir la aplicación.\n" +
	                        "   • Asegúrate de que ningún otro programa esté usando la base de datos.\n" +
	                        "   • Si el problema continúa, elimina el archivo 'TaskManager.db' para crear uno nuevo.\n\n" +
	                        "Detalles técnicos:\n" + e1.getMessage()
	                    );
				}
        	}
        	
        });



        
//----------------------BOTONES DEL FORM-------------------
        //evento botón "Guardar Tarea" del form
        btnGuardarTarea.setOnAction(e -> {
        	obtenerElementosForm();
            try {
                repoTareas.nuevaTarea(nombre_tarea, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion);//dentro de ese metodo si la categoria es nueva se añade
                cargarCategorias();//volvemos a vincular las categorias a el textfield
                
                form.setVisible(false);
                form.setManaged(false);
            } catch (SQLException e1) {
                DialogosPantalla.showError(
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
            }
        });
        
        //btn comfirmar cambios del form
        btnComfirmarCambios.setOnAction(e->{
        	obtenerElementosForm();
        	num_tarea=tarea_activa.getNum();
        	try {
        		repoTareas.actualizarCampos(num_tarea,nombre_tarea, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion);
				form.setVisible(false);
	            form.setManaged(false);
	            tabla_tareas.refrescar();
	            cargarCategorias();//volvemos a vincular las categorias a el textfield
        	} catch (SQLException e1) {
				DialogosPantalla.showError(
					    "No se pudo actualizar la tarea.\n\n" +
					    "Posibles causas:\n" +
					    "   • La tarea no existe o no está seleccionada.\n" +
					    "   • La base de datos está en uso por otro programa.\n" +
					    "   • Datos inválidos (fechas o texto incorrecto).\n" +
					    "   • La tabla 'Tareas' está dañada o no existe.\n\n" +
					    "Qué puedes hacer:\n" +
					    "   • Verifica los datos ingresados.\n" +
					    "   • Asegúrate de que nadie más esté usando la base de datos.\n" +
					    "   • Reinicia la aplicación.\n\n" +
					    "Detalles técnicos:\n" + e1.getMessage()
						);
			}
        	
        });
        
        //btn cancelar del formulario
        form.getBtnCancelar().setOnAction(e->{
        	form.setVisible(false);
            form.setManaged(false);
        });
    }

	private void borrarTareas() throws SQLException {
		tabla_tareas.limpiarTabla();
		repoTareas.vaciarTablas();
		tabla_tareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
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
	
	private void resetearForm() {
		form.setNombreTarea("");
		form.setFechaInicio(LocalDate.now());
		form.setFechaFinal(LocalDate.now().plusDays(5));
		form.setCategoria("");
		form.setObservacion("");
		
	}
	
	private void obtenerElementosForm() {
		nombre_tarea = form.getNombreTarea();
        fecha_inicio = form.getFechaInicio();
        fecha_final = form.getFechaFinal();
        prioridad = form.getPrioridad();
        estado = form.getEstado();
        categoria = form.getCategoria();
        observacion = form.getObservacion();
	}
	
	private void llenarFormCampoActivo() {
		tarea_activa= tabla_tareas.getTareaSeleccionada(); //obtener la tarea activa
		form.setNombreTarea(tarea_activa.getTareaNombre());
		form.setFechaInicio(tarea_activa.getFechaInicio());
		form.setFechaFinal(tarea_activa.getFechaFinal());
		form.setCategoria(tarea_activa.getCategoria());
		form.setPrioridad(tarea_activa.getPrioridad());
		form.setEstado(tarea_activa.getEstado());
		form.setObservacion(tarea_activa.getObservacion());
		
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
		
	  
	}


}