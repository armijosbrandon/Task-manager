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
	private HBox contCategorias;
	private Popup popupPrioridades;
	private Popup popupEstados;
	
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
		VBox contPrioridades=new VBox(5,p1,p2,p3);
		popupPrioridades.getContent().add(contPrioridades);
		
		
		estadoCbox=new ComboBox<>();
		estadoCbox.setPromptText("Seleccionar");
		Label labelEstado= new Label("Estado:");
		VBox estado = new VBox(labelEstado,estadoCbox);
		
		popupEstados=new Popup();
		CheckBox e1= new CheckBox("Pendiente");
		CheckBox e2= new CheckBox("En progreso");
		CheckBox e3= new CheckBox("Completada");
		VBox contEstados=new VBox(5,e1,e2,e3);
		popupEstados.getContent().add(contEstados);
		
		Button btnFiltrar= new Button("Filtrar");
		btnFiltrar.setGraphic(new FontIcon("fas-filter"));
		HBox contenedorPickers= new HBox(categoria,prioridad,estado);
		VBox contenedorPopup= new VBox(10,contenedorPickers,btnFiltrar);
		this.getContent().add(contenedorPopup);
	}
	
	//-------------METODOS CONSTRUCTORES DE LOS POPUPS DE CADA COMBOBOX--------------//
	public Popup getPopupCategoria() {
		return popupCategorias;	
	}
	
	public HBox getContCategorias() {
		return contCategorias;
	}
	
	public Popup getPopupPrioridades() {
		return popupPrioridades;	
	}
	
	public Popup getPopupEstados() {
		return popupEstados;	
	}
	

	public ComboBox<String> getCategoriaCbox() { return categoriaCbox; }
	public ComboBox<String> getPrioridadCbox() { return prioridadCbox; }
	public ComboBox<String> getEstadoCbox() { return estadoCbox; }






}
