package io.github.ArmijosBrandon.TaskManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends Application {
	
    @Override
    public void start(Stage stage) {
        // Crear un botón
        Button boton = new Button("¡Haz clic aquí!");
        boton.setOnAction(e -> boton.setText("😎 ¡Funcionó!"));

        // Crear layout (contenedor)
        StackPane root = new StackPane(boton);

        // Crear escena
        Scene scene = new Scene(root, 300, 200);

        // Opcional: aplicar CSS
        // scene.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());

        // Configurar y mostrar ventana
        stage.setTitle("Prueba JavaFX");
        stage.setScene(scene);
        stage.show();
        
    }

    public static void main(String[] args) {
        // Lanzar la app JavaFX
        launch();
    }
}
