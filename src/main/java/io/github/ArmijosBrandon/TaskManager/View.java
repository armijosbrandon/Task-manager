package io.github.ArmijosBrandon.TaskManager;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
public class View {
	private Stage stage;
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
		
		VBox principal= new VBox(contTitulo,contBotones);
		Scene scene = new Scene(principal);
        scene.getStylesheets().add(getClass().getResource("/css/aplication.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
		stage.setTitle("Gestor de Tareas");
	}

	public void show() {
		stage.show();
		
	}

}
