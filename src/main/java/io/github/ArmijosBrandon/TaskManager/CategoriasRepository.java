package io.github.ArmijosBrandon.TaskManager;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//operaciones crud en la tabla categorias
public class CategoriasRepository {
	public ObservableList<String> obtenerCategorias() throws SQLException {
        ObservableList<String> categorias = FXCollections.observableArrayList();
        ResultSet rs = conn.createStatement().executeQuery("SELECT nombre FROM Categorias");
        while(rs.next()) {
            categorias.add(rs.getString("nombre"));
        }
        rs.close();
        System.out.println("categorias cargadas exitosamente");
        return categorias;
	}
}
