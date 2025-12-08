package io.github.ArmijosBrandon.TaskManager.Data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//operaciones crud en la tabla categorias
public class CategoriasRepository {
	private final Connection conn;
	public CategoriasRepository(Connection conn) {
		this.conn=conn;
	}
	
	public ObservableList<String> obtenerCategorias() throws SQLException {
        ObservableList<String> categorias = FXCollections.observableArrayList();
        String sqlObtenerCategorias="SELECT nombre FROM Categorias ORDER BY nombre ASC";
        
        try (Statement stmt= conn.createStatement();
               ResultSet rs = stmt.executeQuery(sqlObtenerCategorias)) {
               while (rs.next()) {
                   categorias.add(rs.getString("nombre"));
               }

           }
        
        return categorias;
	}
}
