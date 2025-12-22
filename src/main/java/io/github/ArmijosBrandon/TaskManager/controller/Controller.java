package io.github.ArmijosBrandon.TaskManager.controller;

import io.github.ArmijosBrandon.TaskManager.DialogosPantalla;
import io.github.ArmijosBrandon.TaskManager.data.CategoriasRepository;
import io.github.ArmijosBrandon.TaskManager.data.DataBaseManager;
import io.github.ArmijosBrandon.TaskManager.data.SearchRepository;
import io.github.ArmijosBrandon.TaskManager.data.TareasRepository;
import io.github.ArmijosBrandon.TaskManager.view.FormFiltrarView;
import io.github.ArmijosBrandon.TaskManager.view.FormularioTareasView;
import io.github.ArmijosBrandon.TaskManager.view.MainView;
import io.github.ArmijosBrandon.TaskManager.view.TablaTareasView;

import java.sql.SQLException;



public class Controller {
	//-----CONEXION CON BASE DE DATOS Y REPOSITORIOS
	private TareasRepository repoTareas;
	private CategoriasRepository repoCategorias;
	private SearchRepository repoSearch;
	
	//-----VIEWS VINCULADOS
	private MainView mainView; //VISTA PRINCIPAL
	private TablaTareasView tablaTareas;//tabla de tareas
	private FormularioTareasView form; //formulario de nueva y editar tarea
	private FormFiltrarView formFiltrarView; //POPUP DE FILTRADO DE TAREAS
	
	//-----CONTROLADORES VINCULADOS
	private ControladorTareas controladorTareas;
	private ControladorFiltros controladorFiltros;
	
	public Controller( MainView mainView) {
		this.mainView=mainView; //vista principal del programa
		//base de datos
		inicializarBaseDatos();//crea tablas si no existen
		inicializarRepos();
		
        inicializarFormularios();//formlarios de tareas y filtrado
        inicializarTareasTabla(); //cargar tareas de la base a la tabla
        inicializarControladores();//controladores
        inicializarEventosBotones(); //cargar los eventos de los botones
	}
	

	//CREACIÓN DE TABLAS TAREAS, CATEGORIAS Y DE BUSQUEDA CON SUS TRIGGERS 
    private void inicializarBaseDatos() {
        try {
         DataBaseManager.getInstance().iniciarBaseDatos();
        } catch (SQLException e) {
        	DialogosPantalla.showErrorDB("No se pudieron cargar las tareas desde la base de datos.", e);
        }
    }

    //OBTENER REPOSITORIOS PARA HACER OPERACIONES CRUD EN LAS TABLAS DE LA BASE DE DATOS
	private void inicializarRepos() {
		repoTareas = new TareasRepository(DataBaseManager.getInstance().getConnection());
		repoCategorias=new CategoriasRepository(DataBaseManager.getInstance().getConnection());
		repoSearch= new SearchRepository(DataBaseManager.getInstance().getConnection());
	}
    
	//FORMULARIOS
    private void inicializarFormularios() {
    	 form = mainView.getFormularioTareasView();//form de nueva y editar tarea
    	 formFiltrarView = new FormFiltrarView(); //Form de filtrado y sus elementos
    }
	
    //CARGAR TAREAS DE BASE DE DATOS
	private void inicializarTareasTabla() {
		//obtener tabla con tareas actuales en la bd
		tablaTareas= mainView.getTablaTareasView();
		//vincula una lista para la tabla donde va a cargar celdas, va a hacer un for interno por cada tarea al cual le va a hacer los getters establecidos en cada columna
		try {
			tablaTareas.setContenidoPrincipal(repoTareas.obtenerTareas());
		} catch (SQLException e) {
			DialogosPantalla.showErrorDB("No se pudo inicializar la base de datos.", e);
		}
	}
	
	//CONTROLADORES EXTERNOS PARA EVITAR EXCESO DE CODIGO Y RESPONSABILIDADES COMPARTIDAS EN UN SOLO ARCHIVO
	private void inicializarControladores() {
		controladorTareas= new ControladorTareas(tablaTareas, form, repoTareas, repoCategorias,repoSearch,mainView.getOverlay());
		controladorFiltros= new ControladorFiltros(formFiltrarView, tablaTareas, repoTareas,repoCategorias);
	}

    //------------ EVENTOS DE BOTONES -------------------------------------------------
    private void inicializarEventosBotones() {
        
        //evento botón "Nueva Tarea"
        mainView.getBtnNuevaTarea().setOnAction(e -> controladorTareas.nuevaTarea());   
        
        //evento de editar tarea seleccionada
        mainView.getBtnEditarTarea().setOnAction(e->controladorTareas.editarTarea());
             
        mainView.getBtnBorrarTarea().setOnAction(e->controladorTareas.eliminarTarea());
        
        //Boton de marcar tarea como "en progreso"
        mainView.getBtnMarcarProgresoTarea().setOnAction(e->controladorTareas.marcarTareaEnProgreso());
        
        //Boton de marcar tarea como "Completada"
        mainView.getBtnCompletarTarea().setOnAction(e->controladorTareas.marcarTareaCompletada());
        
        //BOTON DE BUSQUEDA
        mainView.getTxtBusqueda().setOnAction(e->{ //PARA CUANDO DE ENTER EN EL TEXT
        	String ctriterioBusqueda= mainView.getTxtBusqueda().getText();
        	controladorTareas.buscarTareas(ctriterioBusqueda);
        });
        mainView.getBtnBusqueda().setOnAction(e->{ //PARA CUANDO DE EN EL BOTON DE LUPA
        	String ctriterioBusqueda= mainView.getTxtBusqueda().getText();
        	controladorTareas.buscarTareas(ctriterioBusqueda);
        });   
        
        //boton de filtrar
        mainView.getBtnFiltrarTarea().setOnAction(e->{
        	controladorFiltros.mostrarPopUpFiltrar(mainView.getBtnFiltrarTarea());// lo mando para que sepa su ubicacion
        	});
        
        
        //------BOTONES PARA TESTEO DE LA APP
        mainView.getBtnCargarTareasPrueba().setOnAction(e -> controladorTareas.cargarTareasPrueba());
        
        //RESETEA TODA LA TBLA ACTUAL ELIMIANDO LAS TAREAS
        mainView.getBtnResetearTareas().setOnAction(e->controladorTareas.resetearTablaTareas());
                
        }

		//funcion para cerrar comunicacion cuando se cierra la app
	public void close() {
		try {
			DataBaseManager.getInstance().close();
		} catch (SQLException e) {
			DialogosPantalla.showError(
				    "No se pudo cerrar correctamente la conexión con la base de datos.\n\n" +
				    "Posibles causas:\n" +
				    "• La conexión ya estaba cerrada o no estaba disponible.\n" +
				    "• El archivo de base de datos estaba siendo utilizado por otro proceso.\n" +
				    "• Ocurrió un error inesperado durante la liberación de recursos.\n\n" +
				    "La aplicación se cerrará igualmente, pero es recomendable reiniciarla para asegurar un estado estable."
				);
		}
	}


}