package io.github.ArmijosBrandon.TaskManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class Model {
	public Connection connect() throws SQLException {
		Connection conn=null;
		String url="jdbc:sqlite:TaskManager.db";
		conn= DriverManager.getConnection(url);
		System.out.println("conexion establecida");
		return conn;
	}
	public void crearTablaTareas(Connection conn) throws SQLException {
		String sql ="CREATE TABLE IF NOT EXISTS Tareas (" +
					"num INTEGER PRIMARY KEY AUTOINCREMENT," +
					"tarea_nombre TEXT," +
					"fecha_inicio TEXT," +
					"fecha_final TEXT," +
			        "categoria TEXT," +
			        "prioridad TEXT," +
			        "estado TEXT," +
			        "observacion TEXT" +
                ");";
   
	   try (Statement stmt = conn.createStatement()) {
	       stmt.execute(sql);
	   }
	}
	public ObservableList<Tarea> obtenerTareas(Connection conn) throws SQLException { //lista de javafx que notifica a la ui si se quita o modifica un elemento
	    ObservableList<Tarea> lista_tareas = FXCollections.observableArrayList();

	    String sql = "SELECT num, tarea_nombre, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion FROM Tareas";

	    try (Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        while (rs.next()) {
	        	LocalDate fecha_inicio = LocalDate.parse(rs.getString("fecha_inicio"));
	        	LocalDate fecha_final = LocalDate.parse(rs.getString("fecha_final"));
	        	lista_tareas.add(new Tarea(
	                    rs.getInt("num"),
	                    rs.getString("tarea_nombre"),
	                    fecha_inicio,
	                    fecha_final,
	                    rs.getString("categoria"),
	                    rs.getString("prioridad"),
	                    rs.getString("estado"),
	                    rs.getString("observacion")         
	            ));
	        }
	    }
	    return lista_tareas;
	}

	
}
