package io.github.ArmijosBrandon.TaskManager;

import java.sql.Connection;
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

//OPERACIONES CRUD CON TABLA TAREAS
public class TareasRepository {
	private Connection conn = null;
	private ObservableList<Tarea> lista_tareas= FXCollections.observableArrayList(); //lista q avisa automaticamente al elemento que se vincule sus cambios.
	
	public TareasRepository(Connection conn) {
		this.conn=conn;
	}
	
	public ObservableList<Tarea> obtenerTareas() throws SQLException { 
		
		lista_tareas.clear();//limpiar lista antes de volver a mandar, evitamos duplicados
		
		//CODIGO SQL
	    String sql = "SELECT num, tarea_nombre, fecha_inicio, fecha_final, categoria, prioridad, estado, observacion FROM Tareas";
	    
	    //EXECUCION CODIGO SQL
	    try (Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        while (rs.next()) {
	        	lista_tareas.add(mapearTarea(rs));
	        }
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
			pstmt.setString(2, fecha_inicio != null ? fecha_inicio.toString() : null);// si no es null ponemos la fecha dada, si no null
			pstmt.setString(3, fecha_final != null ? fecha_final.toString() : null);
			pstmt.setString(4, categoria);
			pstmt.setString(5, prioridad);
			pstmt.setString(6, estado);
			pstmt.setString(7, observacion);
			pstmt.executeUpdate(); //ejecuta todo el codigo
			
			//consultar ultimo num agregado
			try(ResultSet rs= pstmt.getGeneratedKeys()){// pstmt.getGeneratedKeys devuelve las claves generadas en la insercion
					while(rs.next()) {
						num_tarea=rs.getInt(1);//obtiene el primer registro que es el ultimo num ingresado
					}
				}
		}
		lista_tareas.add(new Tarea(num_tarea,tarea_nombre,fecha_inicio,fecha_final, categoria, prioridad, estado, observacion));
	}
	
	//PARA ACTUALIZAR TAREAS
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
		            t.setTareaNombre(nombre_tarea);
		            t.setFechaInicio(fecha_inicio);
		            t.setFechaFinal(fecha_final);
		            t.setCategoria(categoria);
		            t.setPrioridad(prioridad);
		            t.setEstado(estado);
		            t.setObservacion(observacion);
		            break; //salir en cuando encontremos nuestra tarea
		        }
		    }
		}
	}
	
	//BORRAR TAREAS
	public  void borrarTarea(Tarea tarea_activa) throws SQLException {
		String sqlEliminar="Delete from Tareas where num= ?";
		try(PreparedStatement pstmt = conn.prepareStatement(sqlEliminar)){
			pstmt.setInt(1, tarea_activa.getNum());
			pstmt.executeUpdate();
			lista_tareas.remove(tarea_activa);
		}
		
	}
	
	//MARCAR TAREA SELECCIONADA EN PROGRESO
	public void marcarProgresoTarea(Tarea tarea_activa) throws SQLException {
		String sqlMarcarProgreso="update Tareas set estado= 'En progreso' where num=?";
		try(PreparedStatement pstmt = conn.prepareStatement(sqlMarcarProgreso)){
			pstmt.setInt(1, tarea_activa.getNum());
			pstmt.executeUpdate();
			tarea_activa.setEstado("En progreso");
		}
		
	}
	
	//MARCAR TAREA SELECCIONADA COMO COMPLETA
	public void marcarCompletaTarea(Tarea tarea_activa) throws SQLException {
		String sqlMarcarCompleta="update Tareas set estado= 'Completada' where num=?";
		try(PreparedStatement pstmt = conn.prepareStatement(sqlMarcarCompleta)){
			pstmt.setInt(1, tarea_activa.getNum());
			pstmt.executeUpdate();
			tarea_activa.setEstado("Completada");
		}
		
	}
	
	//METODO PARA FILTRAR TABLAS
	//Set<>: coleccion que no permite elementos repetidos
	public ObservableList<Tarea> filtrarTabla(Set<String> categoriasSeleccionadas, Set<String> prioridadesSeleccionadas,Set<String> estadosSeleccionados) throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT * FROM Tareas WHERE "); //para poder modificar mi consulta
	    List<String> condiciones = new ArrayList<>();//las condiciones que va a tener mi consulta
	    ObservableList<Tarea> tareas_obtenidas= FXCollections.observableArrayList();

	    // Categorías
	    if (!categoriasSeleccionadas.isEmpty()) {
	        String placeholders = String.join(", ",//une todo con comas, resultado obtenido "?,?.....,?"
	                categoriasSeleccionadas.stream().map(c -> "?").toList());
	        		//stream(), comvierte una lista en un stream: me permite aplicar filtros, cambiar valores, etc a cada elemento de la lista que al final lo devuelvo como un resultado, como un for pero mas corto
	        		//map transforma todas las categorias del stream en "? y por ultimo .toList() me retorna el stream nuevo en lista
	        
	        condiciones.add("categoria IN (" + placeholders + ")"); //se obtiene algo como categoria in(?,?.....,?)
	    }

	    // Prioridades
	    if (!prioridadesSeleccionadas.isEmpty()) {
	        String placeholders = String.join(", ",
	                prioridadesSeleccionadas.stream().map(p -> "?").toList());
	        condiciones.add("prioridad IN (" + placeholders + ")");
	    }

	    // Estados
	    if (!estadosSeleccionados.isEmpty()) {
	        String placeholders = String.join(", ",
	                estadosSeleccionados.stream().map(e -> "?").toList());
	        condiciones.add("estado IN (" + placeholders + ")");
	    }
	    

	    sql.append(String.join(" AND ", condiciones));//separa cada condicion con un and para que filtre y cumple segun todas las condiciones, cada IN usa un or interno
	    
	    PreparedStatement pstmt= conn.prepareStatement(sql.toString());
	    int index=1;
	    //categorias
	    for(String categoria:categoriasSeleccionadas) {
	    	pstmt.setString(index, categoria);
	    	index++;
	    }

	    for(String prioridad:prioridadesSeleccionadas) {
	    	pstmt.setString(index, prioridad);
	    	index++;
	    }

	    for(String estado:estadosSeleccionados) {
	    	pstmt.setString(index, estado);
	    	index++;
	    }

	    ResultSet rs= pstmt.executeQuery(); //primer tengo que tener creado el pstmt  con los datos ya inyectados
	    while (rs.next()) {
        	tareas_obtenidas.add(mapearTarea(rs));
        }

	    rs.close();
	    pstmt.close();

	    return tareas_obtenidas; // nueva lista independiente
	}
	
	//vaciamos las tablas
	public void vaciarTablas() throws SQLException { 
	    String borrarTareas = "DELETE FROM Tareas;"; //SQLite no soporta truncate, por eso uso delate para borrar todos los datos
	    String reiniciarIDs= "DELETE FROM sqlite_sequence;";

	    try (Statement stmt = conn.createStatement()) {
	        stmt.execute(borrarTareas);
	        stmt.execute(reiniciarIDs);
	    }
	}
	
	//convertir un texto a fecha o nul si esta vacio
	private LocalDate parseFecha(ResultSet rs, String col) throws SQLException {
	    String s = rs.getString(col);
	    return (s != null && !s.isEmpty()) ? LocalDate.parse(s) : null;
	}
	
	private Tarea mapearTarea(ResultSet rs) throws SQLException {
	    return new Tarea(
	        rs.getInt("num"),
	        rs.getString("tarea_nombre"),
	        parseFecha(rs, "fecha_inicio"),//select no nos devuelde date por que lo guardamos como text, aqui converitmos a date esa columna
	        parseFecha(rs,"fecha_final"),
	        rs.getString("categoria"),
	        rs.getString("prioridad"),
	        rs.getString("estado"),
	        rs.getString("observacion")
	    );
	}


}
