package io.github.armijosbrandon.taskmanager.view;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import org.kordamp.ikonli.javafx.FontIcon;




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
    private final Pane overlay;//overlay que bloquea interacion de la pantalla principal cuando se abre el form
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
		
		//overlay que bloquea interacion de la pantalla principal cuando se abre el form
		overlay= new Pane();
		overlay.setVisible(false);
		overlay.setManaged(false);
		
		initializeUI();
	}

	private void initializeUI() {
		//PANTALLA PRINCIPAL
		//contenedor de titulo
		HBox contTitulo = new HBox(new Label("Gestor de Tareas"));
		//caja de busqueda
		HBox busquedaBox = new HBox(txtBusqueda,btnBusqueda); //para juntar en una sola caja la lupa y el textfield
				
		Region spacing = new Region(); //espacio en blanco que crece todo el espacio disponible
		HBox.setHgrow(spacing, Priority.ALWAYS);
		//ACCIONES PRINCIPALES
		HBox contAcciones= new HBox(10,btnNuevaTarea, btnEditarTarea,btnBorrarTarea,btnMarcarProgresoTarea,btnCompletarTarea,spacing,busquedaBox,btnFiltrarTarea);
		
		//seccion para pruebas
		HBox botonesPrueba = new HBox(10, btnCargarTareasPrueba, btnResetearTareas);
		
		VBox pantallaPrincipal= new VBox(10,contTitulo,contAcciones,tablaTareasView,botonesPrueba);//elementos visibles pricnipales
		StackPane root= new StackPane(pantallaPrincipal,overlay,form); //stackpane para poner uno encima de otro
		Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setMaximized(true);
		stage.setTitle("Gestor de Tareas");
		
		
		
		//---estilos
		scene.getStylesheets().add(getClass().getResource("/css/aplication.css").toExternalForm());
		pantallaPrincipal.setPadding(new Insets(20));
		
		//titulo
		contTitulo.setAlignment(Pos.CENTER);
		contTitulo.getStyleClass().add("cont-titulo");
		
		//acciones
		contAcciones.setAlignment(Pos.CENTER);
		//busqueda 
		busquedaBox.getStyleClass().add("search-box");
		busquedaBox.setAlignment(Pos.CENTER);
		txtBusqueda.setPrefWidth(350);//el ancho que debe tener el txt de busqueda si hay espacio en el contenedor padre
		
		
		
		//table view
		VBox.setVgrow(tablaTareasView, Priority.ALWAYS);
		
		//form
		overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
		StackPane.setAlignment(form, Pos.CENTER);
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
    public Pane getOverlay() {return overlay;	}
    
	public void show() {
		stage.show();
		
	}
}
