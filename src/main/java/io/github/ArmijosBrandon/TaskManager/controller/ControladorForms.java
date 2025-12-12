package io.github.ArmijosBrandon.TaskManager.controller;
//----------ARCHIVOS VINCULADOS
import io.github.ArmijosBrandon.TaskManager.DialogosPantalla;
import io.github.ArmijosBrandon.TaskManager.TablaTareasView;
import io.github.ArmijosBrandon.TaskManager.Data.TareasRepository;
import io.github.ArmijosBrandon.TaskManager.model.Tarea;
import io.github.ArmijosBrandon.TaskManager.view.FormularioTareasView;

//----------Librerias
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.scene.control.Button;

public class ControladorForms {
	//VIEWS INVOLUCRADAS
	private final FormularioTareasView form;
	private final TablaTareasView tablaTareas;
	
	//REPOSITORIOS
	private final TareasRepository repoTareas;
	
	//UTILES
	private Tarea tareaActiva;	//GUARDARA LA TAREA SELECCIONADA ACTUALMENTE EN LA TABLA
	//-----CAMPOS DEL FORM
	private int num_tarea;
	private String nombre_tarea;
	private LocalDate fecha_inicio;
	private LocalDate fecha_final;
	private String prioridad;
	private String estado;
	private String categoria;
	private String observacion;
	//------BOTONES INVOLUCRADOS EN EL FORM
	private final Button btnComfirmarCambios;
	private final Button btnGuardarTarea;

	public ControladorForms(FormularioTareasView form, TablaTareasView tabla_tareas, TareasRepository repoTareas) {
		this.form=form;
		this.tablaTareas=tabla_tareas;
		this.repoTareas=repoTareas;
		btnGuardarTarea= form.getBtnGuardarTarea();
    	btnComfirmarCambios= form.getBtnConfirmarCambios();
		inicializarEventos();
	}
	private void inicializarEventos() {
		btnGuardarTarea.setOnAction(e->guardarNuevaTarea());
		btnComfirmarCambios.setOnAction(e->actualizarTarea());
		form.getBtnCancelar().setOnAction(e->{//poco codigo para un metodo separado
			mostrarFormulario(false);//ocultamos el form
		});
		
	}
	
	//----------MANEJO DE FORMS
	public void mostrarFormNuevaTarea() {
		resetearForm();//reseteamos los campos y q se vea visible solo lo relacionado a guardar
    	btnGuardarTarea.setVisible(true);
    	btnGuardarTarea.setManaged(true);
    	btnComfirmarCambios.setVisible(false);
    	btnComfirmarCambios.setManaged(false);
    	mostrarFormulario(true);
	}
	
	public void mostrarFormEditarTarea() {
		tareaActiva = tablaTareas.getTareaSeleccionada(); //obtener la tarea activa
		if(tareaActiva!=null) {//comprobamos que si se haiga seleccionado algo
    		llenarFormCampoActivo();//rellenamos con los elementos de la tarea q se escogio
    		btnComfirmarCambios.setVisible(true);
        	btnComfirmarCambios.setManaged(true);
        	btnGuardarTarea.setVisible(false);
        	btnGuardarTarea.setManaged(false);
        	mostrarFormulario(true);
    	}else{
    		DialogosPantalla.showError("Selecciona una fila para poder editar.");
    	}
		
	}
	
	//------OPERACIONES USADAS POR EL CONTROLLER DE TAREAS INCRUSTADAS EN EL FORMULARIO
	private  void actualizarTarea() {
		obtenerElementosForm();//OBTENEMOS ELEMENTOS ACTUALES DEL FOR
    	num_tarea=tareaActiva.getNum();
    	
    	//VALIDACION DE CAMPOS
    	if (nombre_tarea == null || nombre_tarea.isBlank()) {
    	    DialogosPantalla.showError("El nombre de la tarea es obligatorio.");
    	    return;
    	}
    	if (fecha_inicio.isAfter(fecha_final)) {
    	    DialogosPantalla.showError("La fecha de inicio no puede ser mayor que la fecha final.");
    	    return;
    	}
    	
    	try {
    		repoTareas.actualizarCampos(num_tarea,nombre_tarea, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion);
    		mostrarFormulario(false);//ocultar despues de atualizar
            tablaTareas.refrescar();//REFRESCAMOS LA TABLA
            
    	} catch (SQLException e1) {
			DialogosPantalla.showErrorCRUD("No se pudo actualizar la tarea.", e1);
		}
		
	}
	
	private void guardarNuevaTarea() {
		obtenerElementosForm();
		if (nombre_tarea == null || nombre_tarea.isBlank()) {
    	    DialogosPantalla.showError("El nombre de la tarea es obligatorio.");
    	    return;
    	}
    	if (fecha_inicio.isAfter(fecha_final)) {
    	    DialogosPantalla.showError("La fecha de inicio no puede ser mayor que la fecha final.");
    	    return;
    	}
        try {
            repoTareas.nuevaTarea(nombre_tarea, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion);//dentro de ese metodo si la categoria es nueva se añade
            
            mostrarFormulario(false);//ocultar despues de guardar
        } catch (SQLException e1) {
            DialogosPantalla.showErrorCRUD("No se pudo guardar la nueva tarea en la base de datos.",e1);
        }
		
	}
	
	
	//----------METODOS UTILIARIOS
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
		
		form.setNombreTarea(tareaActiva.getTareaNombre());
		form.setFechaInicio(tareaActiva.getFechaInicio());
		form.setFechaFinal(tareaActiva.getFechaFinal());
		form.setCategoria(tareaActiva.getCategoria());
		form.setPrioridad(tareaActiva.getPrioridad());
		form.setEstado(tareaActiva.getEstado());
		form.setObservacion(tareaActiva.getObservacion());
		
	}
	
	private void mostrarFormulario(Boolean mostrar) {
		form.setVisible(mostrar);
    	form.setManaged(mostrar);
	}

	
}

