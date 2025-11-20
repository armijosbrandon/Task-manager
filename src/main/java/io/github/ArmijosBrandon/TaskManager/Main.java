package io.github.ArmijosBrandon.TaskManager;

import io.github.ArmijosBrandon.TaskManager.View;
import io.github.ArmijosBrandon.TaskManager.Model;
import io.github.ArmijosBrandon.TaskManager.Controller;
import javafx.application.Application;
import javafx.scene.Scene;

import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage) {   
        try{
        	Model model= new Model();
        	View view = new View(stage);
        	new Controller(model,view);
        	view.show();
        }catch(Exception e) {
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
        // Lanzar la app JavaFX
        launch();
    }
}
