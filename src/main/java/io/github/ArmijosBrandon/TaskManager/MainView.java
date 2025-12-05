package io.github.ArmijosBrandon.TaskManager;


import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.kordamp.ikonli.javafx.FontIcon;




public class MainView {
	private Stage stage;
	//----SECCION PRINCIPA
	private Button btnNuevaTarea;
	private Button btnEditarTarea;
	private Button btnBorrarTarea;
	private Button btnCompletarTarea;
	private Button btnMarcarProgresoTarea;
	private Button btnFiltrarTarea;
	private Button btnBusqueda;
	private TextField txtBusqueda;
	//------------TABLA-------///
	private TablaTareasView tablaTareasView;
	
	
	//btns para gestionar  datos de prueba
	private Button btnCargarTareasPrueba;
	private Button btnResetearTareas;;

	private FormularioTareasView form;
	
	public MainView(Stage stage) {
		this.stage=stage;
		initializeUI();
	}

	private void initializeUI() {
//PANTALLA PRINCIPAL
		//contenedor de titulo
		HBox contTitulo = new HBox(new Label("Gestor de Tareas"));
		
		//contenedor de botones
		btnNuevaTarea = new Button("Nueva Tarea");
		btnNuevaTarea.setGraphic(new FontIcon("fas-plus"));
		//posicion del icono
		btnNuevaTarea.setContentDisplay(ContentDisplay.RIGHT);
		
		btnEditarTarea = new Button("Editar Tarea");
		btnEditarTarea.setGraphic(new FontIcon("far-edit"));
		btnEditarTarea.setContentDisplay(ContentDisplay.RIGHT);
		
		btnBorrarTarea = new Button("Eliminar Tarea");
		btnBorrarTarea.setGraphic(new FontIcon("fas-trash"));
		btnBorrarTarea.setContentDisplay(ContentDisplay.RIGHT);
		
		btnMarcarProgresoTarea= new Button("Marcar como En Progreso");
		btnMarcarProgresoTarea.setGraphic(new FontIcon("fas-circle-notch"));
		btnMarcarProgresoTarea.setContentDisplay(ContentDisplay.RIGHT); 
		
		btnCompletarTarea = new Button("Completar Tarea");
		btnCompletarTarea.setGraphic(new FontIcon("fas-check-circle"));
		btnCompletarTarea.setContentDisplay(ContentDisplay.RIGHT);
		
		txtBusqueda = new TextField();
		txtBusqueda.setPromptText("Buscar.....");
		btnBusqueda= new Button();
		btnBusqueda.setGraphic( new FontIcon("fas-search"));
		HBox busquedaBox = new HBox(txtBusqueda,btnBusqueda);
		
		btnFiltrarTarea = new Button("Filtrar por ");
		btnFiltrarTarea.setGraphic(new FontIcon("fas-filter"));
		
		HBox contBotones= new HBox(10,btnNuevaTarea, btnEditarTarea,btnBorrarTarea,btnMarcarProgresoTarea,btnCompletarTarea,busquedaBox,btnFiltrarTarea);
		
		//----TABLA DE TAREAS
		tablaTareasView= new TablaTareasView();
		
		
		//-------DATOS DE PRUEBA---------------------------
		btnCargarTareasPrueba= new Button("Cargar Tareas de Prueba");
		btnCargarTareasPrueba.setGraphic(new FontIcon("fas-file-download"));
		btnCargarTareasPrueba.setContentDisplay(ContentDisplay.RIGHT);
		btnResetearTareas= new Button("Resetear Lista de Tareas");
		btnResetearTareas.setGraphic(new FontIcon("fas-trash-restore"));
		btnResetearTareas.setContentDisplay(ContentDisplay.RIGHT);
		HBox conBotonesPrueba= new HBox(btnCargarTareasPrueba,btnResetearTareas);
		
		
		VBox pantallaPrincipal= new VBox(10,contTitulo,contBotones,getTablaTareasView(),conBotonesPrueba);
		
		//formulario de nueva tarea
		form=  new FormularioTareasView();
		form.setVisible(false);
		form.setManaged(false);
		form.setStyle("-fx-background-color: #05f0ad");
		
		
		StackPane general= new StackPane(pantallaPrincipal,form); //stackpane para ponder uno encima de otro
		Scene scene = new Scene(general);
        scene.getStylesheets().add(getClass().getResource("/css/aplication.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
		stage.setTitle("Gestor de Tareas");
	}
	
	
	
	public Button getBtnNuevaTarea() {
		return btnNuevaTarea;
	}
	public Button getBtnEditarTarea() {
		return btnEditarTarea;
	}
	
	public Button getBtnBorrarTarea() {
		return btnBorrarTarea;
	}
	
	public Button getBtnMarcarProgresoTarea() {
		return btnMarcarProgresoTarea;
	}

	public Button getBtnCompletarTarea() {
		return btnCompletarTarea;
	}
	
	public Button getBtnFiltrarTarea() {
		return btnFiltrarTarea;
	}

	public Button getBtnBusqueda() {
		return btnBusqueda;
	}

	public TextField getTxtBusqueda() {
		return txtBusqueda;
	}
	
	public Button getBtnCargarTareasPrueba() {
		return btnCargarTareasPrueba;
	}

	public Button getBtnResetearTareas() {
		return btnResetearTareas;
	}

	public VBox getForm() {
		return form;
		
	}

	public void setAlerta(String error) {
		Alert alerta = new Alert(AlertType.ERROR);
		alerta.setTitle("!Error");
		alerta.setHeaderText("Algo salio mal...."); 
		alerta.setContentText(error);
		alerta.showAndWait();
	}
	
	public Boolean getConfirmacion(String header, String content) {
		Alert alerta = new Alert(AlertType.CONFIRMATION);
		alerta.setTitle("Confirmacion");
		alerta.setHeaderText(header); 
		alerta.setContentText(content);
		
		Optional<ButtonType> result = alerta.showAndWait(); // me devolvera el boton que haiga presionado el usuario, Optional es un contenedor que puede tener un valor o estar vacío. En este caso, normalmente tendrá un botón, pero JavaFX lo usa para evitar errores si la ventana se cierra de forma inesperada.
		if (result.isPresent() && result.get() == ButtonType.OK) { //is present sera verdadero si hay algo en el optional
		    // El usuario confirmó
			return true;
		} else {
			// El usuario cancelo o cerro el dialogo forsozamente
			return false;
		}
	}
	
	public TablaTareasView getTablaTareasView() {//metodo para obtener la tabla creada
		return tablaTareasView;
	}
	
	public FormularioTareasView getFormularioTareasView() {
		return form;
	}

	public void show() {
		stage.show();
		
	}

	



}
