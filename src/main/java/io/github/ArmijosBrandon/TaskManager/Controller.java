package io.github.ArmijosBrandon.TaskManager;

import java.awt.TextField;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class Controller {
	private Model model;
	private MainView mainView;
	//-----TABLA DE TAREAS 
	private TablaTareasView tabla_tareas;
	
	
	private ObservableList<String> categorias = null;
	private AutoCompletionBinding<String> autoCategoria; //guarda la instacia(lista_sugerencias) actal de autocompletado de un TextFields.bindAutoCompletion()
	private VBox form;
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


	
	public Controller(Model model, MainView mainView) {
		this.model=model;
		this.mainView=mainView;
		
		inicializarConexion();//conecta a la base de datos
        inicializarTablas();//crea tablas si no existen
        cargarCategorias(); //carga las categorias comunes del usuario
        inicializarTareasTabla();
        inicializarFormularios();
        inicializarEventosBotones(); //cargar los eventos de los botones
       
        
        
	}
	
	//------------ CREACIÓN DE TABLAS ------------------------------------------------
    private void inicializarTablas() {
        try {
            model.crearTablaTareas();
            model.crearTablaCategorias();
            model.CrearTablaBusqueda();
        } catch (SQLException e) {
            mainView.setAlerta(
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
            
            //para cambiar las sugerencias del autcompletado primero tengo que eliminar las anteriores
            if (autoCategoria != null) {// si no es null
                autoCategoria.dispose();//se destruye 
            }
            autoCategoria =TextFields.bindAutoCompletion(mainView.getTxtCategoria(), categorias); //inicializar autocompletado de categorias obtenido en el metodo anterior
        } catch (SQLException e) {
            mainView.setAlerta(
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
    	 form = mainView.getForm();
    	 
    	 //Form de filtrado y sus elementos
    	 formFiltrarView = new FormFiltrarView();
         popupCategoria= formFiltrarView.getPopupCategoria();
         comboCategorias= formFiltrarView.getCategoriaCbox();
         comboPrioridades= formFiltrarView.getPrioridadCbox();
         comboEstados=formFiltrarView.getEstadoCbox();
         columnasCategorias = formFiltrarView.getContCategorias();
  
    }


    //------------ EVENTOS DE BOTONES -------------------------------------------------
    private void inicializarEventosBotones() {
        
    	Button btnGuardarTarea= mainView.getBtnGuardarTarea();
    	Button btnComfirmarCambios= mainView.getBtnConfirmarCambios();
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
        		mainView.setAlerta("Selecciona una fila para poder editar.");
        	}	
        });
        
        
        mainView.getBtnBorrarTarea().setOnAction(e->{
        	tarea_activa=tabla_tareas.getTareaSeleccionada();
        	if(tarea_activa!=null) {
        		if(mainView.getConfirmacion("¿Estas seguro de eliminar esta tarea?","Se eliminara esta tarea permamentemente")) {
            		try {
        				model.borrarTarea(tarea_activa);
        				tabla_tareas.refrescar();
        				cargarCategorias();//volvemos a vincular las categorias a el textfield
        				if(tabla_tareas.getContenido().isEmpty()) {
        					tabla_tareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
        				}
        			} catch (SQLException e1) {
        				mainView.setAlerta(
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
        		mainView.setAlerta("No hay ninguna fila seleccionada");
        	}
        	
        	
        });
        
        //Boton de marcar tarea como "en progreso"
        mainView.getBtnMarcarProgresoTarea().setOnAction(e->{
        	tarea_activa=tabla_tareas.getTareaSeleccionada();
        	if(tarea_activa!=null) {
	        	tarea_activa=tabla_tareas.getTareaSeleccionada();
	        	try {
					model.MarcarProgresoTarea(tarea_activa);
				} catch (SQLException e1) {
					mainView.setAlerta(
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
        		mainView.setAlerta("No hay ninguna fila seleccionada");
        	}
        });
        
      //Boton de marcar tarea como "Completada"
        mainView.getBtnCompletarTarea().setOnAction(e->{
        	tarea_activa=tabla_tareas.getTareaSeleccionada();
        	if(tarea_activa!=null) {
	        	tarea_activa=tabla_tareas.getTareaSeleccionada();
	        	try {
					model.MarcarCompletaTarea(tarea_activa);
				} catch (SQLException e1) {
					mainView.setAlerta(
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
        		mainView.setAlerta("No hay ninguna fila seleccionada");
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
        	guardarFiltrosSeleccionados(formFiltrarView.getContPrioridades(),prioridadesSeleccionadas);
        	guardarFiltrosSeleccionados( formFiltrarView.getContEstados(), estadosSeleccionados);
        	guardarSeleccionCategorias(formFiltrarView.getContCategorias());
        	try {
        		if(prioridadesSeleccionadas.isEmpty() && estadosSeleccionados.isEmpty() && categoriasSeleccionadas.isEmpty()) {
        			tabla_tareas.limpiarTabla(); //limpiamos para que no se acumule la lista de las tareas obtenidas con las de la tabla original
        			inicializarTareasTabla();
        		}else {
        			ObservableList<Tarea>tareas_filtradas= model.filtrarTabla(categoriasSeleccionadas, prioridadesSeleccionadas, estadosSeleccionados);
        			if(!tareas_filtradas.isEmpty()) {
        				
        				tabla_tareas.remplazarContenido(tareas_filtradas);
        			}else {
        				tabla_tareas.limpiarTabla();
        				tabla_tareas.setPlaceHolder("No hay ninguna tarea que coincida con esos criterios.");
        			}
        			
        			
        		}
				
			} catch (SQLException e1) {
				mainView.setAlerta(
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
        	if (mainView.getConfirmacion("¿Estas seguro de cargar las tareas de prueba?", "Tus tareas personales se borraran de forma permamente"))
        		try {
        			borrarTareas();
        			model.nuevaTarea("Estudiar matemáticas", LocalDate.of(2025,1,10), LocalDate.of(2025,1,12), "Estudios", "Alta", "Pendiente", "Repasar ecuaciones y álgebra");
        			model.nuevaTarea("Comprar víveres", LocalDate.of(2025,1,8), LocalDate.of(2025,1,8), "Hogar", "Media", "Pendiente", "Comprar arroz, leche y verduras");
        			model.nuevaTarea("Llamar al médico", LocalDate.of(2025,1,5), LocalDate.of(2025,1,5), "Salud", "Alta", "Pendiente", "Solicitar cita de control");
        			model.nuevaTarea("Preparar presentación", LocalDate.of(2025,1,10), LocalDate.of(2025,1,15), "Trabajo", "Alta", "En progreso", "Avanzar diapositivas");
        			model.nuevaTarea("Hacer ejercicio", LocalDate.of(2025,1,2), LocalDate.of(2025,1,2), "Personal", "Baja", "Completada", "30 minutos de cardio");
        			model.nuevaTarea("Leer libro de Java", LocalDate.of(2025,1,3), LocalDate.of(2025,1,20), "Estudios", "Media", "En progreso", "Capítulo sobre colecciones");
        			model.nuevaTarea("Organizar escritorio", LocalDate.of(2025,1,6), LocalDate.of(2025,1,6), "Hogar", "Baja", "Pendiente", "Ordenar cables y papeles");
        			model.nuevaTarea("Enviar currículum", LocalDate.of(2025,1,14), LocalDate.of(2025,1,14), "Trabajo", "Alta", "Pendiente", "Enviar a 3 empresas");
        			model.nuevaTarea("Limpiar la cocina", LocalDate.of(2025,1,2), LocalDate.of(2025,1,2), "Hogar", "Media", "Pendiente", "Fregar platos y limpiar estufa");
        			model.nuevaTarea("Vaciar papeleras", LocalDate.of(2025,1,3), LocalDate.of(2025,1,3), "Hogar", "Baja", "Pendiente", "Todas las habitaciones");
        			model.nuevaTarea("Actualizar portafolio", LocalDate.of(2025,1,18), LocalDate.of(2025,1,18), "Trabajo", "Alta", "En progreso", "Agregar proyecto JavaFX");
        			model.nuevaTarea("Planificar viaje", LocalDate.of(2025,1,25), LocalDate.of(2025,1,30), "Personal", "Media", "Pendiente", "Buscar hoteles y vuelos");
        			model.nuevaTarea("Revisar correo", LocalDate.of(2025,1,5), LocalDate.of(2025,1,5), "Trabajo", "Baja", "Completada", "Limpiar bandeja de entrada");
        			model.nuevaTarea("Practicar guitarra", LocalDate.of(2025,1,4), LocalDate.of(2025,1,4), "Personal", "Baja", "Pendiente", "Aprender nuevo acorde");
        			model.nuevaTarea("Hacer copia de seguridad", LocalDate.of(2025,1,12), LocalDate.of(2025,1,12), "Tecnología", "Alta", "Pendiente", "Respaldar documentos");
        			model.nuevaTarea("Pagar servicios", LocalDate.of(2025,1,9), LocalDate.of(2025,1,9), "Hogar", "Alta", "Pendiente", "Luz y agua");
        			model.nuevaTarea("Regar plantas", LocalDate.of(2025,1,3), LocalDate.of(2025,1,3), "Hogar", "Media", "Completada", "Regar todas las macetas");
        			model.nuevaTarea("Revisar proyecto Java", LocalDate.of(2025,1,6), LocalDate.of(2025,1,7), "Estudios", "Alta", "En progreso", "Corregir errores");
        			model.nuevaTarea("Ver tutorial de SQL", LocalDate.of(2025,1,11), LocalDate.of(2025,1,11), "Estudios", "Media", "Pendiente", "FTS5 y triggers");
        			model.nuevaTarea("Ordenar archivos del PC", LocalDate.of(2025,1,8), LocalDate.of(2025,1,8), "Tecnología", "Baja", "Pendiente", "Eliminar duplicados");
        			model.nuevaTarea("Sacar al perro", LocalDate.of(2025,1,2), LocalDate.of(2025,1,2), "Hogar", "Media", "Completada", "Paseo de 20 min");
        			model.nuevaTarea("Estudiar inglés", LocalDate.of(2025,1,13), LocalDate.of(2025,1,20), "Estudios", "Alta", "En progreso", "Repasar vocabulario");
        			model.nuevaTarea("Revisar finanzas", LocalDate.of(2025,1,16), LocalDate.of(2025,1,16), "Personal", "Alta", "Pendiente", "Organizar gastos del mes");
        			model.nuevaTarea("Limpiar coche", LocalDate.of(2025,1,19), LocalDate.of(2025,1,19), "Hogar", "Baja", "Pendiente", "Aspirar asientos");
        			model.nuevaTarea("Escribir ideas de proyecto", LocalDate.of(2025,1,12), LocalDate.of(2025,1,12), "Trabajo", "Media", "Pendiente", "Anotar nuevas funciones");
        			cargarCategorias();
        		} catch (SQLException e1) {
        			mainView.setAlerta(
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
        	if(mainView.getConfirmacion("¿Estas seguro de resetear la tabla de tareas?", "Se perderan los cambios y tareas de forma permamente")) {
        		try {
					borrarTareas();
					cargarCategorias();
				} catch (SQLException e1) {
					mainView.setAlerta(
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
        mainView.getBtnGuardarTarea().setOnAction(e -> {
        	obtenerElementosForm();
            try {
                model.nuevaTarea(nombre_tarea, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion);//dentro de ese metodo si la categoria es nueva se añade
                cargarCategorias();//volvemos a vincular las categorias a el textfield
                
                form.setVisible(false);
                form.setManaged(false);
            } catch (SQLException e1) {
                mainView.setAlerta(
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
        mainView.getBtnConfirmarCambios().setOnAction(e->{
        	obtenerElementosForm();
        	num_tarea=tarea_activa.getNum();
        	try {
				model.actualizarCampos(num_tarea,nombre_tarea, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion);
				form.setVisible(false);
	            form.setManaged(false);
	            tabla_tareas.refrescar();
	            cargarCategorias();//volvemos a vincular las categorias a el textfield
        	} catch (SQLException e1) {
				mainView.setAlerta(
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
        mainView.getBtnCancelar().setOnAction(e->{
        	form.setVisible(false);
            form.setManaged(false);
        });
    }

	private void borrarTareas() throws SQLException {
		tabla_tareas.limpiarTabla();
		model.borrarTareas();
		tabla_tareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
	}

	private void buscarTareas() {
		String txtBusqueda= mainView.getTxtBusqueda().getText();
    	try {
    		if(txtBusqueda.trim().isEmpty()) {
    			tabla_tareas.limpiarTabla();
    			inicializarTareasTabla();

    		}else {
    			ObservableList<Tarea> tareas_buscadas = model.buscarTareas(txtBusqueda);
    			if (!tareas_buscadas.isEmpty()) {
    			    tabla_tareas.remplazarContenido(tareas_buscadas);
    			} else {
    			    tabla_tareas.setPlaceHolder("No hay resultados.");
    			    tabla_tareas.limpiarTabla();
    			}

    		}
			
		} catch (SQLException e1) {
			mainView.setAlerta(
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
        try {
            model.setConnection(model.connect());  
        } catch (SQLException e) {
            mainView.setAlerta(
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
		tabla_tareas= mainView.getTablaTareasView();
		
		try {
			//vincula una lista para la tabla donde va a cargar celdas, va a hacer un for interno por cada tarea al cual le va a hacer los gettes establecidos en cada columna
			tabla_tareas.setContenidoPrincipal(model.obtenerTareas());//ademas establece la lista a la que se la va a aplciar los cambios realizados en la tabla
			if(tabla_tareas.getContenido().isEmpty()) {//para que en operaciones como en la de buscar si es que regresesa a la tabla normal y no hay tareas que cargue el siguiente place holder
				tabla_tareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
			}
		} catch (SQLException e) {
			 mainView.setAlerta(
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
	
	private void resetearForm() {
		mainView.setTxtNombre_tarea("");
		mainView.setFecha_inicio(LocalDate.now());
		mainView.setFecha_final(LocalDate.now().plusDays(5));
		mainView.setTxtCategoria("");
		mainView.setTxtObservacion("");
		
	}
	
	private void obtenerElementosForm() {
		nombre_tarea = mainView.getTxtNombre_tarea().getText();
        fecha_inicio = mainView.getFecha_inicio().getValue();
        fecha_final = mainView.getFecha_final().getValue();
        prioridad = mainView.getCbPrioridad().getValue();
        estado = mainView.getCbEstado().getValue();
        categoria = mainView.getTxtCategoria().getText();
        observacion = mainView.getTxtObservacion().getText();
	}
	
	private void llenarFormCampoActivo() {
		tarea_activa= tabla_tareas.getTareaSeleccionada(); //obtener la tarea activa
		String prioridad= tarea_activa.getPrioridad();
		String estado=tarea_activa.getEstado();
		int item_prioridad=0;
		int item_estado=0;
		
		if(prioridad.equals("Alta")) {
			item_prioridad=0;
		}else if(prioridad.equals("Media")) {
			item_prioridad=1;
		}else {
			item_prioridad=2;
		}
		
		if(estado.equals("Pendiente")) {
			item_estado=0;
		}else if(estado.equals("En progreso")) {
			item_estado=1;
		}else {
			item_estado=2;
		}
		
		mainView.setTxtNombre_tarea(tarea_activa.getTareaNombre());
		mainView.setFecha_inicio(tarea_activa.getFechaInicio());
		mainView.setFecha_final(tarea_activa.getFechaFinal());
		mainView.setTxtCategoria(tarea_activa.getCategoria());
		mainView.setCbPrioridad(item_prioridad);
		mainView.setCbEstado(item_estado);
		mainView.setTxtObservacion(tarea_activa.getObservacion());
		
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
		Connection conn= model.getConnection();
	    if (conn != null) {
	        try {
	            conn.close();
	        } catch (SQLException e) {
	        	mainView.setAlerta(
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