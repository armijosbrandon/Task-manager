package io.github.ArmijosBrandon.TaskManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class Model {
	private ObservableList<Tarea> lista_tareas = FXCollections.observableArrayList(); //lista de javafx que notifica a los elementos vinculados si se quita o modifica un elemento, en este caso actualizara automaticamente la tabla
	private Connection conn= null;
	public Connection connect() throws SQLException {
		String url="jdbc:sqlite:TaskManager.db";
		conn= DriverManager.getConnection(url);
		System.out.println("conexion establecida");
		return conn;
	}
	public void setConnection(Connection conn) {
		this.conn=conn;
	}
	public Connection getConnection() {
		return conn;
	}
	public void crearTablaTareas() throws SQLException {
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
	
	public void crearTablaCategorias() throws SQLException {
		String sql ="CREATE TABLE IF NOT EXISTS Categorias (nombre text primary key);";
   
	   try (Statement stmt = conn.createStatement()) {
	       stmt.execute(sql);
	   }
	   System.out.println("tabla categorias creada correctamente");
	}
	
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
	
	public void agregarCategoria(String categoria) throws SQLException {
	        String sql = "INSERT OR IGNORE INTO Categorias(nombre) VALUES(?)"; //si existe un duplicado omitira ese valor y no genera error
	        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
	        	pstmt.setString(1, categoria);
	        	pstmt.executeUpdate();
	        }
	        System.out.println("categoria agregada existosamente");
	}
	
	public ObservableList<Tarea> obtenerTareas() throws SQLException { 
	    String sql = "SELECT num, tarea_nombre, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion FROM Tareas";

	    try (Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        while (rs.next()) {
	        	String strFecha_inicio= rs.getString("fecha_inicio");
	        	String strFecha_final= rs.getString("fecha_final");
	        	LocalDate fecha_inicio = (strFecha_inicio !=null && !strFecha_inicio.isEmpty())?LocalDate.parse(strFecha_inicio):null; //localdate.parse no permite valores null
	        	LocalDate fecha_final = (strFecha_final!=null&& !strFecha_final.isEmpty())?LocalDate.parse(strFecha_final):null;
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
	        System.out.println("tareas cargadas exitosamente");
	    }
	    return lista_tareas;
	}

	//funcion para agregar tarea, genera el num de tarea automaticamente
	public void nuevaTarea(String tarea_nombre, LocalDate fecha_inicio, LocalDate fecha_final, String categoria, String prioridad, String estado,String observacion) throws SQLException {
		//sentencia a ejecutar en la base de datos
		String sqlInsertar= "Insert into Tareas(tarea_nombre,fecha_inicio,fecha_final, categoria, prioridad, estado, observacion) values(?,?,?,?,?,?,?)";
		int num_tarea=0;//inicializado en 0
		
		//insertar datos
		try(PreparedStatement pstmt= conn.prepareStatement(sqlInsertar,Statement.RETURN_GENERATED_KEYS)){ //crea una orden del string  y Statement.RETURN_GENERATED_KEYS  retorna o guarda las claves generadas de manera automatica por autoincrement
			pstmt.setString(1, tarea_nombre); // permite evitar inyeccion convirtiendo a string los valores en los marcadores indicados
			pstmt.setString(2, fecha_inicio != null ? fecha_inicio.toString() : null);
			pstmt.setString(3, fecha_final != null ? fecha_final.toString() : null);
			pstmt.setString(4, categoria);
			pstmt.setString(5, prioridad);
			pstmt.setString(6, estado);
			pstmt.setString(7, observacion);
			pstmt.executeUpdate(); //ejecuta orden
			
			//consultar ultimo num agregado
			try(ResultSet rs= pstmt.getGeneratedKeys()){// pstmt.getGeneratedKeys devuelve las claves generadas en la insercion
					while(rs.next()) {
						num_tarea=rs.getInt(1);//obtiene el primer registro que es el ultimo num ingresado
					}
				}
			agregarCategoria(categoria);
		}
		lista_tareas.add(new Tarea(num_tarea,tarea_nombre,fecha_inicio,fecha_final, categoria, prioridad, estado, observacion));
	}
	
	public void actualizarCampos(int num_tarea, String nombre_tarea, LocalDate fecha_inicio, LocalDate fecha_final,String categoria, String prioridad, String estado, String observacion) throws SQLException {
	String sqlEditar= "Update Tareas set tarea_nombre =?, fecha_inicio =?, fecha_final=?, categoria=?, prioridad =?, estado=?, observacion=? where num=?";
		try(PreparedStatement pstmt=conn.prepareStatement(sqlEditar)){
			pstmt.setString(1, nombre_tarea);
			pstmt.setString(2, fecha_inicio != null ? fecha_inicio.toString() : null);
			pstmt.setString(3, fecha_final != null ? fecha_final.toString() : null);
			pstmt.setString(4, categoria);
			pstmt.setString(5, prioridad);
			pstmt.setString(6, estado);
			pstmt.setString(7, observacion);
			pstmt.setInt(8, num_tarea);
			pstmt.executeUpdate();
			
			for (Tarea t : lista_tareas) {
		        if (t.getNum() == num_tarea) {
		            t.setTarea_nombre(nombre_tarea);
		            t.setFecha_inicio(fecha_inicio);
		            t.setFecha_final(fecha_final);
		            t.setCategoria(categoria);
		            t.setPrioridad(prioridad);
		            t.setEstado(estado);
		            t.setObservacion(observacion);
		            break; //salir en cuando encontremos nuestra tarea
		        }
		    }
		}
	}
	public void borrarTarea(int num_tarea) throws SQLException {
		String sqlEliminar="Delete from Tareas where num= ?";
		try(PreparedStatement pstmt = conn.prepareStatement(sqlEliminar)){
			pstmt.setInt(1, num_tarea);
			pstmt.executeUpdate();
			
			for(Tarea t:lista_tareas) {
				if(t.getNum()==num_tarea) {
					lista_tareas.remove(t);
					break;
				}
			}
			
		}
		
	}

	
}
