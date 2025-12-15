package io.github.ArmijosBrandon.TaskManager.view;


import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import org.kordamp.ikonli.javafx.FontIcon;

import io.github.ArmijosBrandon.TaskManager.TablaTareasView;




public class MainView {
	private final Stage stage;
	//--------SECCION PRINCIPAL
    // botones principales
    private final Button btnNuevaTarea;
    private final Button btnEditarTarea;
    private final Button btnBorrarTarea;
    private final Button btnCompletarTarea;
    private final Button btnMarcarProgresoTarea;
    private final Button btnFiltrarTarea;
    // busqueda
    private final Button btnBusqueda;
    private final TextField txtBusqueda;
    // tabla de tareas
    private final TablaTareasView tablaTareasView;
    // datos prueba
    private final Button btnCargarTareasPrueba;
    private final Button btnResetearTareas;

    //--------FORMULARIO DE EDICION Y CREACION DE TAREAS
    private final FormularioTareasView form;
	
	public MainView(Stage stage) {
		this.stage=stage;	
		//botones principales
		btnNuevaTarea = boton("Nueva Tarea", "fas-plus");
		btnEditarTarea = boton("Editar Tarea", "far-edit");
		btnBorrarTarea = boton("Eliminar Tarea", "fas-trash");
		btnMarcarProgresoTarea = boton("Marcar como En Progreso", "fas-circle-notch");
		btnCompletarTarea = boton("Completar Tarea", "fas-check-circle");
		btnFiltrarTarea = boton("Filtrar", "fas-filter");
		
		//busqueda
		txtBusqueda = new TextField();
		txtBusqueda.setPromptText("Buscar.....");
		btnBusqueda = new Button();
		btnBusqueda.setGraphic(new FontIcon("fas-search"));

		// ----TABLA DE TAREAS
		tablaTareasView = new TablaTareasView();

		//datos de prueba
		btnCargarTareasPrueba = boton("Cargar Tareas de Prueba", "fas-file-download");
		btnResetearTareas = boton("Resetear Lista", "fas-trash-restore");

		// formulario de nueva tarea
		form = new FormularioTareasView();
		form.setVisible(false);
		form.setManaged(false);
		form.setStyle("-fx-background-color: #05f0ad");
		
		initializeUI();
	}

	private void initializeUI() {
		//PANTALLA PRINCIPAL
		//contenedor de titulo
		HBox contTitulo = new HBox(new Label("Gestor de Tareas"));
		contTitulo.getStyleClass().add("cont-titulo");
		
		//caja de busqueda
		HBox busquedaBox = new HBox(txtBusqueda,btnBusqueda);
		busquedaBox.getStyleClass().add("search-box");
		//ACCIONES PRINCIPALES
		HBox contAcciones= new HBox(10,btnNuevaTarea, btnEditarTarea,btnBorrarTarea,btnMarcarProgresoTarea,btnCompletarTarea,busquedaBox,btnFiltrarTarea);
		
		//seccion para pruebas
		HBox botonesPrueba = new HBox(10, btnCargarTareasPrueba, btnResetearTareas);
		
		VBox pantallaPrincipal= new VBox(10,contTitulo,contAcciones,getTablaTareasView(),botonesPrueba);
		
		StackPane root= new StackPane(pantallaPrincipal,form); //stackpane para ponder uno encima de otro
		Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/aplication.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
		stage.setTitle("Gestor de Tareas");
	}
	
	private Button boton(String texto, String icon) { //metodo Helper para crear botones con misma logica y diseño
	    Button b = new Button(texto);
	    b.setGraphic(new FontIcon(icon));
	    b.setContentDisplay(ContentDisplay.RIGHT);
	    return b;
	}

	  // ----GETTERS EXTERNOS PARA EL CONTROLLER -----

    public Button getBtnNuevaTarea() { return btnNuevaTarea; }
    public Button getBtnEditarTarea() { return btnEditarTarea; }
    public Button getBtnBorrarTarea() { return btnBorrarTarea; }
    public Button getBtnCompletarTarea() { return btnCompletarTarea; }
    public Button getBtnMarcarProgresoTarea() { return btnMarcarProgresoTarea; }

    public Button getBtnBusqueda() { return btnBusqueda; }
    public TextField getTxtBusqueda() { return txtBusqueda; }

    public Button getBtnFiltrarTarea() { return btnFiltrarTarea; }
    public Button getBtnCargarTareasPrueba() { return btnCargarTareasPrueba; }
    public Button getBtnResetearTareas() { return btnResetearTareas; }

    public TablaTareasView getTablaTareasView() { return tablaTareasView; }
    public FormularioTareasView getFormularioTareasView() { return form; }

	public void show() {
		stage.show();
		
	}
}
