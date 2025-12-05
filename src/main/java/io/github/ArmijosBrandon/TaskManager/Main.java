package io.github.ArmijosBrandon.TaskManager;

import io.github.ArmijosBrandon.TaskManager.MainView;
import io.github.ArmijosBrandon.TaskManager.Model;
import io.github.ArmijosBrandon.TaskManager.Controller;
import javafx.application.Application;
import javafx.scene.Scene;

import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage) {   
        Controller controller = null; 

        try {
            Model model = new Model();
            MainView view = new MainView(stage);
            controller = new Controller(model, view); 
            view.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

        Controller finalController = controller; // para usarlo en lambda(e->) por que no permite si no es un objeto final tons creo una copia fija
        stage.setOnCloseRequest(e -> finalController.close()); //cerrar la conexion de sqlite cuando se cierre el programa
    }

    public static void main(String[] args) {
        launch();
    }
}

