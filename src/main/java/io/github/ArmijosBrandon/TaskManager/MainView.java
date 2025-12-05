package io.github.ArmijosBrandon.TaskManager;


import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.kordamp.ikonli.javafx.FontIcon;




public class MainView {
	private Stage stage;
	//----SECCION PRINCIPA
	private Button btnNuevaTarea;
	private Button btnEditarTarea;
	private Button btnBorrarTarea;
	private Button btnCompletarTarea;
	private Button btnMarcarProgresoTarea;
	private Button btnFiltrarTarea;
	private Button btnBusqueda;
	private TextField txtBusqueda;
	//------------TABLA-------///
	private TablaTareasView tablaTareasView;
	
	//elementos del form
	private TextField txtNombre_tarea;
	private DatePicker fecha_inicio;
	private DatePicker fecha_final;
	private TextField txtCategoria;
	private ComboBox<String> cbEstado;
	private ComboBox<String> cbPrioridad;
	private TextArea txtObservacion;
	//btns de formulario
	private Button btnConfirmarCambios;
	private Button btnGuardarTarea;
	private Button btnCancelar;
	
	//btns para gestionar  datos de prueba
	private Button btnCargarTareasPrueba;
	private Button btnResetearTareas;;

	private VBox form;
	
	public MainView(Stage stage) {
		this.stage=stage;
		initializeUI();
	}

	private void initializeUI() {
//PANTALLA PRINCIPAL
		//contenedor de titulo
		HBox contTitulo = new HBox(new Label("Gestor de Tareas"));
		
		//contenedor de botones
		btnNuevaTarea = new Button("Nueva Tarea");
		btnNuevaTarea.setGraphic(new FontIcon("fas-plus"));
		//posicion del icono
		btnNuevaTarea.setContentDisplay(ContentDisplay.RIGHT);
		
		btnEditarTarea = new Button("Editar Tarea");
		btnEditarTarea.setGraphic(new FontIcon("far-edit"));
		btnEditarTarea.setContentDisplay(ContentDisplay.RIGHT);
		
		btnBorrarTarea = new Button("Eliminar Tarea");
		btnBorrarTarea.setGraphic(new FontIcon("fas-trash"));
		btnBorrarTarea.setContentDisplay(ContentDisplay.RIGHT);
		
		btnMarcarProgresoTarea= new Button("Marcar como En Progreso");
		btnMarcarProgresoTarea.setGraphic(new FontIcon("fas-circle-notch"));
		btnMarcarProgresoTarea.setContentDisplay(ContentDisplay.RIGHT); 
		
		btnCompletarTarea = new Button("Completar Tarea");
		btnCompletarTarea.setGraphic(new FontIcon("fas-check-circle"));
		btnCompletarTarea.setContentDisplay(ContentDisplay.RIGHT);
		
		txtBusqueda = new TextField();
		txtBusqueda.setPromptText("Buscar.....");
		btnBusqueda= new Button();
		btnBusqueda.setGraphic( new FontIcon("fas-search"));
		HBox busquedaBox = new HBox(txtBusqueda,btnBusqueda);
		
		btnFiltrarTarea = new Button("Filtrar por ");
		btnFiltrarTarea.setGraphic(new FontIcon("fas-filter"));
		
		HBox contBotones= new HBox(10,btnNuevaTarea, btnEditarTarea,btnBorrarTarea,btnMarcarProgresoTarea,btnCompletarTarea,busquedaBox,btnFiltrarTarea);
		
		//----TABLA DE TAREAS
		tablaTareasView= new TablaTareasView();
		
		
		//-------DATOS DE PRUEBA---------------------------
		btnCargarTareasPrueba= new Button("Cargar Tareas de Prueba");
		btnCargarTareasPrueba.setGraphic(new FontIcon("fas-file-download"));
		btnCargarTareasPrueba.setContentDisplay(ContentDisplay.RIGHT);
		btnResetearTareas= new Button("Resetear Lista de Tareas");
		btnResetearTareas.setGraphic(new FontIcon("fas-trash-restore"));
		btnResetearTareas.setContentDisplay(ContentDisplay.RIGHT);
		HBox conBotonesPrueba= new HBox(btnCargarTareasPrueba,btnResetearTareas);
		
		
		VBox pantallaPrincipal= new VBox(10,contTitulo,contBotones,getTablaTareasView(),conBotonesPrueba);
		
		//formulario de nueva tarea
		form= form();
		form.setVisible(false);
		form.setManaged(false);
		form.setStyle("-fx-background-color: #05f0ad");
		
		
		StackPane general= new StackPane(pantallaPrincipal,form); //stackpane para ponder uno encima de otro
		Scene scene = new Scene(general);
        scene.getStylesheets().add(getClass().getResource("/css/aplication.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
		stage.setTitle("Gestor de Tareas");
	}
	
	public VBox form() {
		Label Titulo= new Label("Agregar Tarea");
		Label lblNombre= new Label("Nombre: "); 
		txtNombre_tarea= new TextField();
		txtNombre_tarea.setPromptText("Ingresa el nombre de la tarea aquí.");
		
		Label lblFecha_inicio= new Label("Fecha de inicio: ");
		fecha_inicio= new DatePicker(LocalDate.now());
		
		Label lblFecha_final= new Label("Fecha limite: ");
		fecha_final= new DatePicker(LocalDate.now().plusDays(5));
		
		Label lblCategoria= new Label("Categoria: "); 
		txtCategoria= new TextField();
		txtCategoria.setPromptText("Ingresa la categoria aquí.");
		
		cbPrioridad= new ComboBox<>();
		cbPrioridad.getItems().addAll("Alta", "Media", "Baja");
		cbPrioridad.setValue("Media");//valor por defecto

		cbEstado= new ComboBox<>();
		cbEstado.getItems().addAll("Pendiente", "En progreso", "Completada");
		cbEstado.setValue("Pendiente"); // valor por defecto
		
		Label lblObservacion= new Label("Observaciones: "); 
		txtObservacion= new TextArea();
		txtObservacion.setPromptText("Observaciones adicionales.");
		
		btnGuardarTarea = new Button("Agregar Tarea");
		btnConfirmarCambios= new Button("Confirmar Cambios");
		btnCancelar=new Button("Cancelar");
		HBox botonesform= new HBox(10,btnGuardarTarea,btnConfirmarCambios,btnCancelar);
		
		return new VBox(5, Titulo, lblNombre, txtNombre_tarea, lblFecha_inicio, fecha_inicio, lblFecha_final, fecha_final, lblCategoria, txtCategoria, cbPrioridad, cbEstado, lblObservacion, txtObservacion, botonesform);
	}
	
	public Button getBtnNuevaTarea() {
		return btnNuevaTarea;
	}
	public Button getBtnEditarTarea() {
		return btnEditarTarea;
	}
	
	public Button getBtnBorrarTarea() {
		return btnBorrarTarea;
	}
	
	public Button getBtnMarcarProgresoTarea() {
		return btnMarcarProgresoTarea;
	}

	public Button getBtnCompletarTarea() {
		return btnCompletarTarea;
	}
	
	public Button getBtnFiltrarTarea() {
		return btnFiltrarTarea;
	}

	public Button getBtnBusqueda() {
		return btnBusqueda;
	}

	public TextField getTxtBusqueda() {
		return txtBusqueda;
	}

	public TextField getTxtNombre_tarea() {
		return txtNombre_tarea;
	}

	public void setTxtNombre_tarea(String txtNombre_tarea) {
		this.txtNombre_tarea.setText(txtNombre_tarea);;
	}
	public DatePicker getFecha_inicio() {
		return fecha_inicio;
	}
	public void setFecha_inicio(LocalDate fecha_inicio) {
		this.fecha_inicio.setValue(fecha_inicio);
	}
	public DatePicker getFecha_final() {
		return fecha_final;
	}
	public void setFecha_final(LocalDate fecha_final) {
		this.fecha_final.setValue(fecha_final);
	}

	public TextField getTxtCategoria() {
		return txtCategoria;
	}
	public void setTxtCategoria(String txtCategoria ) {
		this.txtCategoria.setText(txtCategoria);;
	}

	public ComboBox<String> getCbEstado() {
		return cbEstado;
	}
	public void setCbEstado(int estado ) {
		this.cbEstado.getSelectionModel().select(estado);
	}
	

	public ComboBox<String> getCbPrioridad() {
		return cbPrioridad;
	}
	
	public void setCbPrioridad(int estado ) {
		this.cbPrioridad.getSelectionModel().select(estado);
	}
	
	public TextArea getTxtObservacion() {
		return txtObservacion;
	}
	public void setTxtObservacion(String txtObservacion ) {
		this.txtObservacion.setText(txtObservacion);;
	}


	public Button getBtnGuardarTarea() {
		return btnGuardarTarea;
	}
	public Button getBtnConfirmarCambios() {
		return btnConfirmarCambios;
	}
	
	public Button getBtnCancelar() {
		return btnCancelar;
	}
	
	public Button getBtnCargarTareasPrueba() {
		return btnCargarTareasPrueba;
	}

	public Button getBtnResetearTareas() {
		return btnResetearTareas;
	}

	public VBox getForm() {
		return form;
		
	}

	public void setAlerta(String error) {
		Alert alerta = new Alert(AlertType.ERROR);
		alerta.setTitle("!Error");
		alerta.setHeaderText("Algo salio mal...."); 
		alerta.setContentText(error);
		alerta.showAndWait();
	}
	
	public Boolean getConfirmacion(String header, String content) {
		Alert alerta = new Alert(AlertType.CONFIRMATION);
		alerta.setTitle("Confirmacion");
		alerta.setHeaderText(header); 
		alerta.setContentText(content);
		
		Optional<ButtonType> result = alerta.showAndWait(); // me devolvera el boton que haiga presionado el usuario, Optional es un contenedor que puede tener un valor o estar vacío. En este caso, normalmente tendrá un botón, pero JavaFX lo usa para evitar errores si la ventana se cierra de forma inesperada.
		if (result.isPresent() && result.get() == ButtonType.OK) { //is present sera verdadero si hay algo en el optional
		    // El usuario confirmó
			return true;
		} else {
			// El usuario cancelo o cerro el dialogo forsozamente
			return false;
		}
	}
	
	public TablaTareasView getTablaTareasView() {//metodo para obtener la tabla creada
		return tablaTareasView;
	}

	public void show() {
		stage.show();
		
	}

	



}
