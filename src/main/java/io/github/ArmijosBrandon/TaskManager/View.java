package io.github.ArmijosBrandon.TaskManager;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;
import org.controlsfx.control.textfield.TextFields;


public class View {
	private Stage stage;
	private Button btnNuevaTarea;
	private TableView<Tarea> tabla_tareas;
	private TextField txtNombre_tarea;
	private DatePicker fecha_inicio;
	private DatePicker fecha_final;
	private TextField txtCategoria;
	private TextArea txtObservacion;
	private Button btnGuardarTarea;
	private Button btnCancelarNTarea;
	private ComboBox<String> cbEstado;
	private ComboBox<String> cbPrioridad;
	private VBox formNuevaTarea;
	public View(Stage stage) {
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
		
		Button btnEditTask = new Button("Editar Tarea");
		btnEditTask.setGraphic(new FontIcon("far-edit"));
		btnEditTask.setContentDisplay(ContentDisplay.RIGHT);
		
		Button btnDeleteTask = new Button("Eliminar Tarea");
		btnDeleteTask.setGraphic(new FontIcon("fas-trash"));
		btnDeleteTask.setContentDisplay(ContentDisplay.RIGHT);
		
		Button btnCompleteTask = new Button("Eliminar Tarea");
		btnCompleteTask.setGraphic(new FontIcon("fas-check-circle"));
		btnCompleteTask.setContentDisplay(ContentDisplay.RIGHT);
		
		TextField txtSearch = new TextField();
		txtSearch.setPromptText("Buscar.....");
		HBox searchBox = new HBox(txtSearch, new FontIcon("fas-search"));
		
		Button btnFilterTask = new Button();
		btnFilterTask.setGraphic(new FontIcon("fas-filter"));
		
		HBox contBotones= new HBox(10,btnNuevaTarea,btnEditTask,btnDeleteTask,btnCompleteTask,searchBox,btnFilterTask);
		//CONTENEDOR TABLA DE TAREAS
		tabla_tareas= new TableView<>();
		//columnas
		TableColumn<Tarea, Integer> colNum = new TableColumn<>("Num");//esta fila leera un dato entero de un objeto tarea
		TableColumn<Tarea, String> colTareaNombre = new TableColumn<>("Tarea");
		TableColumn<Tarea, LocalDate> colFechaInicio = new TableColumn<>("Fecha Inicio");
		TableColumn<Tarea, LocalDate> colFechaFinal = new TableColumn<>("Fecha Final");
		TableColumn<Tarea, String> colCategoria = new TableColumn<>("Categoría");
		TableColumn<Tarea, String> colPrioridad = new TableColumn<>("Prioridad");
		TableColumn<Tarea, String> colEstado = new TableColumn<>("Estado");
		TableColumn<Tarea, String> colObservacion = new TableColumn<>("Observación");
		
		//contenido, inidicar a cada columna que getter usar para llenar su contenido
		colNum.setCellValueFactory(new PropertyValueFactory<>("num")); //busca getNum() de la clase tarea
		colTareaNombre.setCellValueFactory(new PropertyValueFactory<>("tarea_nombre"));
		colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
		colFechaFinal.setCellValueFactory(new PropertyValueFactory<>("fecha_final"));
		colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
		colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
		colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
		colObservacion.setCellValueFactory(new PropertyValueFactory<>("observacion"));
		//añado a la tabla las columnas
		tabla_tareas.getColumns().addAll(
			    colNum,
			    colTareaNombre,
			    colFechaInicio,
			    colFechaFinal,
			    colCategoria,
			    colPrioridad,
			    colEstado,
			    colObservacion
		);
		
		
		VBox pantallaPrincipal= new VBox(10,contTitulo,contBotones,tabla_tareas);
		
		//formulario de nueva tarea
		formNuevaTarea= FormNuevaTarea();
		formNuevaTarea.setVisible(false);
		formNuevaTarea.setManaged(false);
		formNuevaTarea.setStyle("-fx-background-color: #05f0ad");
		
		
		StackPane general= new StackPane(pantallaPrincipal,formNuevaTarea); //stackpane para ponder uno encima de otro
		Scene scene = new Scene(general);
        scene.getStylesheets().add(getClass().getResource("/css/aplication.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
		stage.setTitle("Gestor de Tareas");
	}
	
	public VBox FormNuevaTarea() {
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
		btnCancelarNTarea=new Button("Cancelar");
		HBox botonesFormNuevaTarea= new HBox(10,btnGuardarTarea,btnCancelarNTarea);
		
		return new VBox(5, Titulo, lblNombre, txtNombre_tarea, lblFecha_inicio, fecha_inicio, lblFecha_final, fecha_final, lblCategoria, txtCategoria, cbPrioridad, cbEstado, lblObservacion, txtObservacion, botonesFormNuevaTarea);
	}
	
	public TableView<Tarea> getTablaTareas() {
		return tabla_tareas;

	}
	public Button getBtnNuevaTarea() {
		return btnNuevaTarea;
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
	
	public VBox getformNuevaTarea() {
		return formNuevaTarea;
		
	}

	
	public Button getBtnCancelarNTarea() {
		return btnCancelarNTarea;
	}
	
	public void show() {
		stage.show();
		
	}

	public void setAlerta(String error) {
		Alert alerta = new Alert(AlertType.ERROR);
		alerta.setTitle("!Error");
		alerta.setHeaderText("Algo salio mal...."); 
		alerta.setContentText(error);
		alerta.showAndWait();
	}

}
