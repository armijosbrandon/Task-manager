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
		
		// --- TRIGGER: Insertar categoría automáticamente al crear nueva tarea
		String sqlTriggerInsertCategoria =
		    "CREATE TRIGGER IF NOT EXISTS tgr_insert_categoria " + 
		    "AFTER INSERT ON Tareas " +
		    "WHEN NEW.categoria IS NOT NULL AND NEW.categoria <> '' " +
		    "BEGIN " +
		        "INSERT OR IGNORE INTO Categorias(nombre) VALUES (NEW.categoria); " + //ignora si no existe
		    "END;";

		// --- TRIGGER: Actualizar categorías cuando se edita la tarea
		String sqlTriggerUpdateCategoria =
		    "CREATE TRIGGER IF NOT EXISTS tgr_update_categoria " +
		    "AFTER UPDATE OF categoria ON Tareas " + // cuando cambia la columna categoria en tareas
		    "BEGIN " +
		        // Agregar la categoría nueva si no existía
		        "INSERT OR IGNORE INTO Categorias(nombre) VALUES (NEW.categoria); " +

		        // Si nadie usa ya la categoría anterior entonces eliminarla
		        "DELETE FROM Categorias " +
		        "WHERE nombre = OLD.categoria " +
		        "AND NOT EXISTS (SELECT 1 FROM Tareas WHERE categoria = OLD.categoria); " + //borra donde este la antigua cateogira y si y solo si  ya no existe 1 registro con esa categoria
		    "END;";

		// --- TRIGGER: Eliminar categoría automáticamente si ya no tiene tareas asociadas
		String sqlTriggerDeleteCategoria =
		    "CREATE TRIGGER IF NOT EXISTS tgr_delete_categoria " +
		    "AFTER DELETE ON Tareas " +
		    "BEGIN " +
		        "DELETE FROM Categorias " +
		        "WHERE nombre = OLD.categoria " +
		        "AND NOT EXISTS (SELECT 1 FROM Tareas WHERE categoria = OLD.categoria); " +
		    "END;";

   
		try (Statement stmt = conn.createStatement()) {
	       stmt.execute(sql);
	       stmt.execute(sqlTriggerInsertCategoria);
	       stmt.execute(sqlTriggerUpdateCategoria);
	       stmt.execute(sqlTriggerDeleteCategoria);
		}
	 
	}
	
	public void CrearTablaBusqueda() throws SQLException {

		//tabla virutal: tabla que usa modulos especiales para funciones especificas,  en este caso usa el módulo FTS5 para buscar texto
		//fts5 es un motor de busqueda de texto para encontrar palabras dentro de textos largos similar a un indice fulltext en sql que se aplicara en las dos columnas dadas
		// tokenize='porter' le indica al motor que use el algoritmo Porter Stemmer, que sirve para reducir las palabras a su raíz.
		// crea índices especiales para búsquedas por prefijo de 2,3 y 4 letras
		String sqlCreacionTablaBusqueda =
				"CREATE VIRTUAL TABLE IF NOT EXISTS Tareas_fts USING fts5(tarea_nombre, observacion, prefix='2 3 4' ,tokenize='porter');"; 

		//-------TRIGERS PARA QUE COINCIDAN LOS DATOS DE LA TABLA VIRTUAL DE MI TABLA NORMAL TAREAS
		String sqlTriggerNuevaTarea =
				"CREATE TRIGGER IF NOT EXISTS tgr_nueva_tarea AFTER INSERT ON Tareas " + // trigger que se aplica sobre la tabla tareas despues de insertar
						"BEGIN " + 
						"INSERT INTO Tareas_fts(rowid, tarea_nombre, observacion) " + // rowid es un valor que tiene por defecto cada tabla, por eso no lo declaro
						"VALUES (new.num, new.tarea_nombre, new.observacion);" + //new.num es el num de la nueva fila que se acaba de registrar
						"END;";

		String sqlTriggerEditarTarea =
				"CREATE TRIGGER IF NOT EXISTS tgr_editar_tarea AFTER UPDATE ON Tareas " +
						"BEGIN " +
						"UPDATE Tareas_fts SET tarea_nombre = new.tarea_nombre, observacion = new.observacion " + 
						"WHERE rowid = old.num;" + //donde el id por defecto de la tabla coincida con la fila eliminada
						"END;";

		String sqlTriggerEliminarTarea =
				"CREATE TRIGGER IF NOT EXISTS tgr_eliminar_tarea AFTER DELETE ON Tareas " +
						"BEGIN " +
						"DELETE FROM Tareas_fts WHERE rowid = old.num;" +
						"END;";
		
		try (Statement stmt = conn.createStatement()) {
		    stmt.execute(sqlCreacionTablaBusqueda);
		    stmt.execute(sqlTriggerNuevaTarea);
		    stmt.execute(sqlTriggerEditarTarea);
		    stmt.execute(sqlTriggerEliminarTarea);
		}

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
	public void borrarTarea(Tarea tarea_activa) throws SQLException {
		String sqlEliminar="Delete from Tareas where num= ?";
		try(PreparedStatement pstmt = conn.prepareStatement(sqlEliminar)){
			pstmt.setInt(1, tarea_activa.getNum());
			pstmt.executeUpdate();
			lista_tareas.remove(tarea_activa);
		}
		
	}
	public void MarcarProgresoTarea(Tarea tarea_activa) throws SQLException {
		String sqlMarcarProgreso="update Tareas set estado= 'En progreso' where num=?";
		try(PreparedStatement pstmt = conn.prepareStatement(sqlMarcarProgreso)){
			pstmt.setInt(1, tarea_activa.getNum());
			pstmt.executeUpdate();
			tarea_activa.setEstado("En progreso");
		}
		
	}
	
	public void MarcarCompletaTarea(Tarea tarea_activa) throws SQLException {
		String sqlMarcarCompleta="update Tareas set estado= 'Completada' where num=?";
		try(PreparedStatement pstmt = conn.prepareStatement(sqlMarcarCompleta)){
			pstmt.setInt(1, tarea_activa.getNum());
			pstmt.executeUpdate();
			tarea_activa.setEstado("Completada");
		}
		
	}
	

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
        	String strFecha_inicio= rs.getString("fecha_inicio");
        	String strFecha_final= rs.getString("fecha_final");
        	LocalDate fecha_inicio = (strFecha_inicio !=null && !strFecha_inicio.isEmpty())?LocalDate.parse(strFecha_inicio):null; //localdate.parse no permite valores null
        	LocalDate fecha_final = (strFecha_final!=null&& !strFecha_final.isEmpty())?LocalDate.parse(strFecha_final):null;
        	tareas_obtenidas.add(new Tarea(
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

	    return tareas_obtenidas; // nueva lista independiente
	}
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
	public void borrarTareas() throws SQLException { 
	    String borrarTareas = "DELETE FROM Tareas;"; //SQLite no soporta truncate, por eso uso delate para borrar todos los datos
	    String reiniciarIDs= "DELETE FROM sqlite_sequence;";

	    try (Statement stmt = conn.createStatement()) {
	        stmt.execute(borrarTareas);
	        stmt.execute(reiniciarIDs);
	    }
	}

	
}
