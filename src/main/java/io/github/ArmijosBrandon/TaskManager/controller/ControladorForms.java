package io.github.ArmijosBrandon.TaskManager.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import io.github.ArmijosBrandon.TaskManager.DialogosPantalla;
import io.github.ArmijosBrandon.TaskManager.TablaTareasView;
import io.github.ArmijosBrandon.TaskManager.Data.TareasRepository;
import io.github.ArmijosBrandon.TaskManager.model.Tarea;
import io.github.ArmijosBrandon.TaskManager.view.FormularioTareasView;
import javafx.scene.control.Button;

public class ControladorForms {
	private FormularioTareasView form;
	private TablaTareasView tabla_tareas;
	private Tarea tarea_activa;
	
	private TareasRepository repoTareas;
	
	private int num_tarea;
	private String nombre_tarea;
	private LocalDate fecha_inicio;
	private LocalDate fecha_final;
	private String prioridad;
	private String estado;
	private String categoria;
	private String observacion;
	
	private Button btnComfirmarCambios;
	private Button btnGuardarTarea;
	

	
	public ControladorForms(FormularioTareasView form, TablaTareasView tabla_tareas, TareasRepository repoTareas) {
		this.form=form;
		this.tabla_tareas=tabla_tareas;
		this.repoTareas=repoTareas;
		btnGuardarTarea= form.getBtnGuardarTarea();
    	btnComfirmarCambios= form.getBtnConfirmarCambios();
		inicializarEventos();
	}
	private void inicializarEventos() {
		btnGuardarTarea.setOnAction(e->guardarNuevaTarea());
		btnComfirmarCambios.setOnAction(e->actualizarTarea());
		form.getBtnCancelar().setOnAction(e->{
			form.setVisible(false);
            form.setManaged(false);
		});
		
	}
	
	public void mostrarFormNuevaTarea() {
		resetearForm();
    	btnGuardarTarea.setVisible(true);
    	btnGuardarTarea.setManaged(true);
    	btnComfirmarCambios.setVisible(false);
    	btnComfirmarCambios.setManaged(false);
        form.setVisible(true);
        form.setManaged(true);//que no ocupe espacio cuando esté oculto
	}
	
	public void mostrarFormEditarTarea() {
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
		
	}
	
	private  void actualizarTarea() {
		obtenerElementosForm();
    	num_tarea=tarea_activa.getNum();
    	try {
    		repoTareas.actualizarCampos(num_tarea,nombre_tarea, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion);
			form.setVisible(false);
            form.setManaged(false);
            tabla_tareas.refrescar();
            
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
		
	}
	
	private void guardarNuevaTarea() {
		obtenerElementosForm();
        try {
            repoTareas.nuevaTarea(nombre_tarea, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion);//dentro de ese metodo si la categoria es nueva se añade
            
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
		
	}
	
	
	private void resetearForm() {
		form.setNombreTarea("");
		form.setFechaInicio(LocalDate.now());
		form.setFechaFinal(LocalDate.now().plusDays(5));
		form.setEstado("Pendiente");
		form.setPrioridad("Media");
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
		tarea_activa = tabla_tareas.getTareaSeleccionada(); //obtener la tarea activa
		form.setNombreTarea(tarea_activa.getTareaNombre());
		form.setFechaInicio(tarea_activa.getFechaInicio());
		form.setFechaFinal(tarea_activa.getFechaFinal());
		form.setCategoria(tarea_activa.getCategoria());
		form.setPrioridad(tarea_activa.getPrioridad());
		form.setEstado(tarea_activa.getEstado());
		form.setObservacion(tarea_activa.getObservacion());
		
	}

	
}

