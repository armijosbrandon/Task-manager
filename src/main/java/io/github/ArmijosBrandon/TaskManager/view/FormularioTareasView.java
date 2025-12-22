package io.github.ArmijosBrandon.TaskManager.view;

import java.time.LocalDate;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class FormularioTareasView extends VBox{
	private final TextField txtNombreTarea;
	private final DatePicker fechaInicio;
	private final DatePicker fechaFinal;
	private final TextField txtCategoria;
	private final ComboBox<String> cbEstado;
	private final ComboBox<String> cbPrioridad;
	private final TextArea txtObservacion;
	private final Button btnConfirmarCambios;
	private final Button btnGuardarTarea;
	private final Button btnCancelar;
	
	public FormularioTareasView() {
		Label Titulo= new Label("Agregar Tarea");
		Label lblNombre= new Label("Nombre: "); 
		this.txtNombreTarea= new TextField();
		txtNombreTarea.setPromptText("Ingresa el nombre de la tarea aquí.");
		
		Label lblFechaInicio= new Label("Fecha de inicio: ");
		this.fechaInicio= new DatePicker(LocalDate.now());
		
		Label lblFechaFinal= new Label("Fecha limite: ");
		this.fechaFinal= new DatePicker(LocalDate.now().plusDays(5));//le añado 5 dias
		
		Label lblCategoria= new Label("Categoria: "); 
		this.txtCategoria= new TextField();
		txtCategoria.setPromptText("Ingresa la categoria aquí.");
		
		this.cbPrioridad= new ComboBox<>();
		cbPrioridad.getItems().addAll("Alta", "Media", "Baja");
		cbPrioridad.setValue("Media");//valor por defecto

		this.cbEstado= new ComboBox<>();
		cbEstado.getItems().addAll("Pendiente", "En progreso", "Completada");
		cbEstado.setValue("Pendiente"); // valor por defecto
		
		Label lblObservacion= new Label("Observaciones: "); 
		this.txtObservacion= new TextArea();
		txtObservacion.setPromptText("Observaciones adicionales.");
		
		this.btnGuardarTarea = new Button("Agregar Tarea");
		this.btnConfirmarCambios= new Button("Confirmar Cambios");
		this.btnCancelar=new Button("Cancelar");
		HBox botonesform= new HBox(10,btnGuardarTarea,btnConfirmarCambios,btnCancelar);
		
		this.getChildren().addAll(Titulo, lblNombre, txtNombreTarea, lblFechaInicio, fechaInicio, lblFechaFinal, fechaFinal, lblCategoria, txtCategoria, cbPrioridad, cbEstado, lblObservacion, txtObservacion, botonesform);
		
		
		//------ESTILOS
		this.setPadding(new Insets(20));
		this.setSpacing(10);
		
		setMaxWidth(800);                  // límite visual
	    setMinWidth(320);                  // móviles / ventanas pequeñas, debajo de esto se rompe visualmente
	    setMaxHeight(Region.USE_PREF_SIZE);// usa el tamaño que ocupan los componentes
	    
		this.getStylesheets().add(getClass().getResource("/css/form.css").toExternalForm());
		this.getStyleClass().add("root-form");
	}
	
	//------GETTERS Y SETTERS
	public String getNombreTarea() { return txtNombreTarea.getText(); }
	public void setNombreTarea(String n) { txtNombreTarea.setText(n); }

	public LocalDate getFechaInicio() { return fechaInicio.getValue(); }
	public void setFechaInicio(LocalDate f) { fechaInicio.setValue(f); }

	public LocalDate getFechaFinal() { return fechaFinal.getValue(); }
	public void setFechaFinal(LocalDate f) { fechaFinal.setValue(f); }

	public String getCategoria() { return txtCategoria.getText(); }
	public void setCategoria(String c) { txtCategoria.setText(c); }

	public String getEstado() { return cbEstado.getValue(); }
	public void setEstado(String e) { cbEstado.getSelectionModel().select(e); }

	public String getPrioridad() { return cbPrioridad.getValue(); }
	public void setPrioridad(String p) { cbPrioridad.getSelectionModel().select(p); }

	public String getObservacion() { return txtObservacion.getText(); }
	public void setObservacion(String o) { txtObservacion.setText(o); }

	public Button getBtnGuardarTarea() { return btnGuardarTarea; }
	public Button getBtnConfirmarCambios() { return btnConfirmarCambios; }
	public Button getBtnCancelar() { return btnCancelar; }
	
	public TextField getCategoriaTextField() {//usado para binding del textfield con categorias usadas por el usuario
		return txtCategoria;
	}
}

