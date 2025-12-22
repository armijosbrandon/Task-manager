package io.github.armijosbrandon.taskmanager.controller;
// --- LIBRERIAS
import java.sql.SQLException;
import java.time.LocalDate;
import org.controlsfx.control.textfield.AutoCompletionBinding; //autocompletado de textfield, use dependencias externas
import org.controlsfx.control.textfield.TextFields; //componente que se autocompleta,

import io.github.armijosbrandon.taskmanager.DialogosPantalla;
import io.github.armijosbrandon.taskmanager.data.CategoriasRepository;
import io.github.armijosbrandon.taskmanager.data.SearchRepository;
import io.github.armijosbrandon.taskmanager.data.TareasRepository;
import io.github.armijosbrandon.taskmanager.model.Tarea;
import io.github.armijosbrandon.taskmanager.view.FormularioTareasView;
import io.github.armijosbrandon.taskmanager.view.TablaTareasView;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

public class ControladorTareas {
	
	//--VIEWS
	private final TablaTareasView tablaTareas;
	private final FormularioTareasView form;
	
	//--CONTROLADOR VINCULADO DE FORMS PARA GUARDAD Y EDITAR
	private final ControladorForms controladorForms;
	
	//--UTILES
	private Tarea tareaActiva;
	private ObservableList<String> categorias = null;//Lista de categorias que se vincularan a el texfield
	private AutoCompletionBinding<String> autoCategoria; //para control del autocompletado
	
	//--REPOSITORIOS
	private final TareasRepository repoTareas;
	private final CategoriasRepository repoCategorias;
	private final SearchRepository repoSearch;
	
	public ControladorTareas(TablaTareasView tablaTareas, FormularioTareasView form, TareasRepository repoTareas, CategoriasRepository repoCategorias,SearchRepository repoSearch, Pane overlay) {
		this.tablaTareas=tablaTareas;
		this.form=form;
		this.repoTareas=repoTareas;
		this.repoCategorias=repoCategorias;
		this.repoSearch = repoSearch;
		cargarCategorias();//cargamos las categorias de la base de datos al texfield vinculado
		controladorForms= new ControladorForms(form, tablaTareas, repoTareas,overlay);//instancio el controlador y ejecuto sus metodos iniciales
		
	}
	
	private void cargarCategorias() {
        try {
            categorias = repoCategorias.obtenerCategorias();//obtener de base de datos
            
            //para cambiar las sugerencias del autocompletado primero tengo que eliminar las anteriores si existen
            if (autoCategoria != null) {// si no es null
                autoCategoria.dispose();//se destruye 
            }
            autoCategoria =TextFields.bindAutoCompletion(form.getCategoriaTextField(), categorias); //inicializar autocompletado de las categorias
        } catch (SQLException e) {
        	DialogosPantalla.showErrorDB("No se pudieron cargar las categorías.", e);
        }
        
    }
	
	
	//-------OPEREACIONES CRUD-----
	//-------------CRUD CON FORMULARIO
	public void nuevaTarea() {
		controladorForms.mostrarFormNuevaTarea();
		cargarCategorias();//recargamos las categorias al textfield
	}
	
	public void editarTarea() {
		controladorForms.mostrarFormEditarTarea();
		cargarCategorias();//recargamos las categorias al textfield
	}
	
	//-----OPERACIONES CRUD SIN FORMULARIO Y DE MARCAR TAREAS
	public void eliminarTarea() {
		tareaActiva=validarSeleccion();//valido si hay seleccionado algo
    	if (tareaActiva==null) return;//cancelo este metodo
    		if(DialogosPantalla.getConfirmacion("¿Estas seguro de eliminar esta tarea?","Se eliminara esta tarea permamentemente")) {
        		try {
    				repoTareas.borrarTarea(tareaActiva);
    				tablaTareas.refrescar();//aves no se carga, por eso refresco manualmente
    				cargarCategorias();//volvemos a vincular las categorias a el textfield
    				
    				//QUE PASA SI NO HAY TAREAS?
    				if(tablaTareas.getContenido().isEmpty()) {
    					tablaTareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
    				}
    			} catch (SQLException e) {
    				 DialogosPantalla.showErrorCRUD("No se pudo eliminar la tarea.", e);
    			}
        	}
	}
	
	public void buscarTareas(String criterioBusqueda) {
		
    	try {
    		if(criterioBusqueda.trim().isEmpty()) {
    			tablaTareas.limpiarTabla();
    			try {
    				tablaTareas.setContenidoPrincipal(repoTareas.obtenerTareas());
    			} catch (SQLException e) {
    				DialogosPantalla.showErrorDB("No se pudo obtener las tareas de la base de datos", e);
    			}
    			if(tablaTareas.getContenido().isEmpty()) {//si la tabla  no tiene tareas que cargue el siguiente place holder
    				tablaTareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
    			}

    		}else {
    			ObservableList<Tarea> tareas_buscadas = repoSearch.buscarTareas(criterioBusqueda);
    			if (!tareas_buscadas.isEmpty()) {
    			    tablaTareas.remplazarContenido(tareas_buscadas);
    			} else {
    			    tablaTareas.setPlaceHolder("No hay resultados.");
    			    tablaTareas.limpiarTabla();
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
	
	public void marcarTareaEnProgreso() {
		tareaActiva=validarSeleccion();
    	if (tareaActiva==null) return;//cancelo este metodo
        	try {
				repoTareas.marcarProgresoTarea(tareaActiva);
			} catch (SQLException e) {
				 DialogosPantalla.showErrorCRUD("No se pudo actualizar la tarea.", e);
			}
        	tablaTareas.refrescar();
	}
	
	public void marcarTareaCompletada() {
		tareaActiva=validarSeleccion();
    	if (tareaActiva==null) return;//cancelo este metodo
        	try {
				repoTareas.marcarCompletaTarea(tareaActiva);
			} catch (SQLException e) {
				DialogosPantalla.showErrorCRUD("No se pudo actualizar la tarea.", e);
			}
        	tablaTareas.refrescar();
    	
	}
	
	//----OPERACIONES PARA TAREAS DE PRUEBA Y RESETEAR LA TABLA ENTERA
	public void cargarTareasPrueba() {
		if (DialogosPantalla.getConfirmacion("¿Estas seguro de cargar las tareas de prueba?", "Tus tareas personales se borraran de forma permamente"))
    		try {
    			vaciarTablas();
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
    			cargarCategorias();//recargamos las categorias con el autocompletado
    		} catch (SQLException e) {
    			DialogosPantalla.showErrorDB("No se pudo generar las tareas de prueba en la base de datos.",e);				
    		}
	}
	
	public void resetearTablaTareas() {
		if(DialogosPantalla.getConfirmacion("¿Estas seguro de resetear la tabla de tareas?", "Se perderan los cambios y tareas de forma permamente")) {
    		try {
				vaciarTablas();
				cargarCategorias();//reseteamos categorias
			} catch (SQLException e) {
				 DialogosPantalla.showErrorDB("No se pudo resetear la tabla.", e);
			}
    	}
	}
	
	//-----METODOS UTILIARIOS
	
	private void vaciarTablas() throws SQLException {
		tablaTareas.limpiarTabla();
		repoTareas.vaciarTablas();
		tablaTareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
	}
	
	private Tarea validarSeleccion() {
        Tarea t = tablaTareas.getTareaSeleccionada();
        if (t == null) {
            DialogosPantalla.showError("No hay ninguna fila seleccionada");
        }
        return t;
    }

}
