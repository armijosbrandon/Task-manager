package io.github.ArmijosBrandon.TaskManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class Model {
	private ObservableList<Tarea> lista_tareas = FXCollections.observableArrayList(); //lista de javafx que notifica a los elementos vinculados si se quita o modifica un elemento, en este caso actualizara automaticamente la tabla

	
	

	
}
