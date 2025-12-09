package io.github.ArmijosBrandon.TaskManager.controller;

import java.sql.SQLException;
import java.time.LocalDate;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import io.github.ArmijosBrandon.TaskManager.DialogosPantalla;
import io.github.ArmijosBrandon.TaskManager.TablaTareasView;
import io.github.ArmijosBrandon.TaskManager.Data.CategoriasRepository;
import io.github.ArmijosBrandon.TaskManager.Data.TareasRepository;
import io.github.ArmijosBrandon.TaskManager.model.Tarea;
import io.github.ArmijosBrandon.TaskManager.view.FormularioTareasView;
import javafx.collections.ObservableList;

public class ControladorTareas {
	private TablaTareasView tabla_tareas;
	private FormularioTareasView form;
	private ControladorForms controladorForms;
	private TareasRepository repoTareas;
	private CategoriasRepository repoCategorias;
	private Tarea tarea_activa;
	private ObservableList<String> categorias = null;
	private AutoCompletionBinding<String> autoCategoria;
	
	public ControladorTareas(TablaTareasView tabla_tareas, FormularioTareasView form, TareasRepository repoTareas, CategoriasRepository repoCategorias) {
		this.tabla_tareas=tabla_tareas;
		this.form=form;
		this.repoTareas=repoTareas;
		this.repoCategorias=repoCategorias;
		
		controladorForms= new ControladorForms(form, tabla_tareas, repoTareas);
	}
	
	public void nuevaTarea() {
		controladorForms.mostrarFormNuevaTarea();
		cargarCategorias();
	}
	
	public void editarTarea() {
		controladorForms.mostrarFormEditarTarea();
		cargarCategorias();
	}
	
	public void eliminarTarea() {
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
    	
    	
	}
	
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
	
	public ObservableList<String> getCategorias(){
		return categorias;
		
	}

	public void cargarTareasPrueba() {
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
	}
	
	public void resetearTablaTareas() {
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
	}
	
	private void borrarTareas() throws SQLException {
		tabla_tareas.limpiarTabla();
		repoTareas.vaciarTablas();
		tabla_tareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
	}
}
