package io.github.ArmijosBrandon.TaskManager;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Popup;
public class FormFiltrarView extends Popup{
	
	private ComboBox<String> categoriaCbox;
	private ComboBox<String> prioridadCbox;
	private ComboBox<String> estadoCbox;
	
	private Popup popupCategorias;
	private Popup popupPrioridades;
	private Popup popupEstados;
	
	private HBox contCategorias;
	private VBox contPrioridades;
	private VBox contEstados;
	
	private Button btnFiltrar;
	
	public FormFiltrarView() {
		categoriaCbox=new ComboBox<>();
		categoriaCbox.setPromptText("Seleccionar");
		Label labelCategoria= new Label("Categoria:");
		VBox categoria = new VBox(labelCategoria,categoriaCbox);
				
		popupCategorias=new Popup();
		contCategorias=new HBox();
		contCategorias.setStyle("-fx-background-color:#ffffff");
		popupCategorias.getContent().add(contCategorias);
		
		
		prioridadCbox=new ComboBox<>();
		prioridadCbox.setPromptText("Seleccionar");
		Label labelPrioridad= new Label("Prioridad:");
		VBox prioridad = new VBox(labelPrioridad,prioridadCbox);
		
		popupPrioridades=new Popup();
		CheckBox p1= new CheckBox("Alta");
		CheckBox p2= new CheckBox("Media");
		CheckBox p3= new CheckBox("Baja");
		contPrioridades=new VBox(5,p1,p2,p3);
		popupPrioridades.getContent().add(contPrioridades);
		
		
		estadoCbox=new ComboBox<>();
		estadoCbox.setPromptText("Seleccionar");
		Label labelEstado= new Label("Estado:");
		VBox estado = new VBox(labelEstado,estadoCbox);
		
		popupEstados=new Popup();
		CheckBox e1= new CheckBox("Pendiente");
		CheckBox e2= new CheckBox("En progreso");
		CheckBox e3= new CheckBox("Completada");
		contEstados=new VBox(5,e1,e2,e3);
		popupEstados.getContent().add(getContEstados());
		
		btnFiltrar= new Button("Filtrar");
		btnFiltrar.setGraphic(new FontIcon("fas-filter"));
		HBox contenedorPickers= new HBox(categoria,prioridad,estado);
		VBox contenedorPopup= new VBox(10,contenedorPickers,getBtnFiltrar());
		this.getContent().add(contenedorPopup);
	}
	
	public ComboBox<String> getCategoriaCbox() { return categoriaCbox; }
	public ComboBox<String> getPrioridadCbox() { return prioridadCbox; }
	public ComboBox<String> getEstadoCbox() { return estadoCbox; }

	
	//-------------METODOS CONSTRUCTORES DE LOS POPUPS DE CADA COMBOBOX--------------//
	public Popup getPopupCategoria() {
		return popupCategorias;	
	}
	
	public Popup getPopupPrioridades() {
		return popupPrioridades;	
	}
	
	public Popup getPopupEstados() {
		return popupEstados;	
	}
	
	//-------------METODOS PARA OBTENER LOS CHECKBOX DE CADA COMBOBOX--------------//
	
	public HBox getContCategorias() {
		return contCategorias;
	}
	
	public VBox getContPrioridades() {
		return contPrioridades;
	}
	
	public VBox getContEstados() {
		return contEstados;
	}
	
	
	


	public Button getBtnFiltrar() {
		return btnFiltrar;
	}






}
