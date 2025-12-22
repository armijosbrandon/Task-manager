package io.github.armijosbrandon.taskmanager.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import io.github.armijosbrandon.taskmanager.model.Tarea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


//operaciones basadas en busqueda de FST5
public class SearchRepository {
	private final Connection conn;
	public SearchRepository(Connection conn) {
		this.conn=conn;
	}
	public ObservableList<Tarea> buscarTareas(String txtBusqueda) throws SQLException {
		ObservableList<Tarea> tareas_buscadas= FXCollections.observableArrayList();//LISTA PARA TAREAS OBTENIDAS
		//CODIGO SQL
		String busqueda =
				"SELECT t.* "+
				"FROM Tareas t "+
				"JOIN Tareas_fts tf ON t.num = tf.rowid "+//UNE DONDE EL NUMERO DE TAREA COINCICA CON EL ID DE LA TABLA DE BUSQUEDA
				"WHERE Tareas_fts MATCH ?;"//CRITERIO DE BUSQUEDA
				+ "";
		
		//EXECUCION DE CODIGO SQL
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
