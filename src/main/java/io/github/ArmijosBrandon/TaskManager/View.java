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
		
		Button newTask = new Button("Nueva Tarea");
		FontIcon icon= new FontIcon("fas-plus");
		newTask.setGraphic(icon);
		newTask.setContentDisplay(ContentDisplay.RIGHT);
		
		
		VBox principal= new VBox(contTitulo,newTask);
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
