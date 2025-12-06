package io.github.ArmijosBrandon.TaskManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//operaciones basadas en busqueda de FST5
public class SearchRepository {
public ObservableList<Tarea> buscarTareas(String txtBusqueda) throws SQLException {
		
		ObservableList<Tarea> tareas_buscadas= FXCollections.observableArrayList();
		String busqueda =
				"SELECT t.* "+
				"FROM Tareas t "+
				"JOIN Tareas_fts tf ON t.num = tf.rowid "+
				"WHERE Tareas_fts MATCH ?;"
				+ "";
		PreparedStatement pstmt=conn.prepareStatement(busqueda);
		pstmt.setString(1, txtBusqueda+"*"); //"*" activa la busqueda por prefijo o similares
		
		ResultSet rs= pstmt.executeQuery();
		
		while(rs.next()) {
			String strFecha_inicio= rs.getString("fecha_inicio");
			String strFecha_final= rs.getString("fecha_final");
			LocalDate fecha_inicio= (strFecha_inicio !=null && !strFecha_inicio.isEmpty())? LocalDate.parse(strFecha_inicio):null;
			LocalDate fecha_final=(strFecha_final!=null && !strFecha_final.isEmpty())? LocalDate.parse(strFecha_final):null;
			
			tareas_buscadas.add(new Tarea(
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
		rs.close();
		pstmt.close();
		
		return tareas_buscadas;
	}
}
