package io.github.ArmijosBrandon.TaskManager;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class View {
	private Stage stage;
	public View(Stage stage) {
		this.stage=stage;
		initializeUI();
	}

	private void initializeUI() {
		
		VBox principal= new VBox();
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
