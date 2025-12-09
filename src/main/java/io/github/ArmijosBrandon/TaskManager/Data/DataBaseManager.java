package io.github.ArmijosBrandon.TaskManager.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//clase utilaria para manejar la base de datos
public class DataBaseManager {
	private Connection conn;
	private static DataBaseManager instance; 

    //PATRON SINGLETON: permite una sola instancia en toda la aplicacion, asi usamos una sola conexion
	private DataBaseManager() {}//prohibimos instancias
    //static para que sea un metodo de clase, no de objeto
    public static synchronized DataBaseManager getInstance() {
        if (instance == null) {//si no existe una instancia la crea
            instance = new DataBaseManager();
        }
        return instance;
    }
    
    //CONEXION CON LA BASE DE DATOS
	public void connect() throws SQLException{
		String url="jdbc:sqlite:TaskManager.db";//nombre de base de datos
		conn= DriverManager.getConnection(url);
		try (Statement st = conn.createStatement()) {
			st.execute("PRAGMA foreign_keys = ON;");//para respetar claves foranes o triggers
		}
		
	}

	public Connection getConnection() {
		return conn;
	}
	
	public void iniciarBaseDatos() throws SQLException {
		connect();//inicializamos conexion
		crearTablaTareas();//importante oner esta antes de los trigerrs que la usan, si no error
		crearTablaCategorias();
		crearTablaBusqueda();
		
	}
	
	private void crearTablaTareas() throws SQLException {
		//CODIGO SQL
		System.out.println(">>> creando tabla Tareas");
		String sql ="CREATE TABLE IF NOT EXISTS Tareas (" +//solo crea la tabla si no existe
					"num INTEGER PRIMARY KEY AUTOINCREMENT," +
					"tarea_nombre TEXT," +
					"fecha_inicio TEXT," +
					"fecha_final TEXT," +
			        "categoria TEXT," +
			        "prioridad TEXT," +
			        "estado TEXT," +
			        "observacion TEXT" +
                ");";
		//EJECUCION DE CODIGO SQL
	   try (Statement stmt = conn.createStatement()) {
	       stmt.execute(sql);
	   }
	}
	
	private void crearTablaCategorias() throws SQLException {
		//-----CODIGO SQL
		System.out.println(">>> creando tabla categoriass");
		String sql ="CREATE TABLE IF NOT EXISTS Categorias (nombre text primary key);";
		
		//---------------TRIGGERS DE CATEOGRIA CON TAREAS
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
		
		//EJECUCION DE CODIGO SQL
		try (Statement stmt = conn.createStatement()) {
	       stmt.execute(sql);
	       stmt.execute(sqlTriggerInsertCategoria);
	       stmt.execute(sqlTriggerUpdateCategoria);
	       stmt.execute(sqlTriggerDeleteCategoria);
		}
	 
	}
	
	private void crearTablaBusqueda() throws SQLException {
		
		System.out.println(">>> creando tabla busqueda");
		//tabla virutal: tabla que usa modulos especiales para funciones especificas,  en este caso usa el módulo FTS5 para buscar texto
		//fts5 es un motor de busqueda de texto para encontrar palabras dentro de textos largos similar a un indice fulltext en sql que se aplicara en las dos columnas dadas
		// tokenize='porter' le indica al motor que use el algoritmo Porter Stemmer, que sirve para reducir las palabras a su raíz, ESTUDIAR->ESTUDI.
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
		
		//EJECUCION DE CODIGO SQL
		try (Statement stmt = conn.createStatement()) {
		    stmt.execute(sqlCreacionTablaBusqueda);
		    stmt.execute(sqlTriggerNuevaTarea);
		    stmt.execute(sqlTriggerEditarTarea);
		    stmt.execute(sqlTriggerEliminarTarea);
		}

	}
	
	//METODO PARA CERRAR CONEXION
	public void close() throws SQLException {
		
	    if (conn != null) {
	    	conn.close();   
	    }
	}
	
	// método debug - ponlo en DataBaseManager
	public void debugMostrarEstructuraBD() {
	    try {
	        System.out.println("=== RUTA BD: " + new java.io.File("TaskManager.db").getAbsolutePath() + " ===");
	        try (Statement st = conn.createStatement()) {
	            var rs = st.executeQuery("PRAGMA database_list;");
	            System.out.println("=== PRAGMA database_list ===");
	            while (rs.next()) {
	                System.out.printf("seq=%s name=%s file=%s%n", rs.getString("seq"), rs.getString("name"), rs.getString("file"));
	            }
	            rs.close();

	            var rs2 = st.executeQuery("SELECT type, name, sql FROM sqlite_master ORDER BY type, name;");
	            System.out.println("=== sqlite_master (tables/indexes/triggers) ===");
	            while (rs2.next()) {
	                System.out.printf("%s: %s -> %s%n", rs2.getString("type"), rs2.getString("name"), rs2.getString("sql"));
	            }
	            rs2.close();
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

}
