package io.github.ArmijosBrandon.TaskManager;


import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;

import java.time.LocalDate;

import org.kordamp.ikonli.javafx.FontIcon;
public class View {
	private Stage stage;
	private TableView<Tarea> tabla_tareas;
	public View(Stage stage) {
		this.stage=stage;
		initializeUI();
	}

	private void initializeUI() {
//PANTALLA PRINCIPAL
		//contenedor de titulo
		HBox contTitulo = new HBox(new Label("Gestor de Tareas"));
		
		//contenedor de botones
		Button btnNewTask = new Button("Nueva Tarea");
		btnNewTask.setGraphic(new FontIcon("fas-plus"));
		//posicion del icono
		btnNewTask.setContentDisplay(ContentDisplay.RIGHT);
		
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
		
		HBox contBotones= new HBox(10,btnNewTask,btnEditTask,btnDeleteTask,btnCompleteTask,searchBox,btnFilterTask);
		//CONTENEDOR TABLA DE TAREAS
		tabla_tareas= new TableView<>();
		//columnas
		TableColumn<Tarea, Integer> colNum = new TableColumn<>("Num");
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
		
		
		VBox principal= new VBox(10,contTitulo,contBotones,tabla_tareas);
		Scene scene = new Scene(principal);
        scene.getStylesheets().add(getClass().getResource("/css/aplication.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
		stage.setTitle("Gestor de Tareas");
	}
	public TableView<Tarea> getTablaTareas() {
		return tabla_tareas;

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
