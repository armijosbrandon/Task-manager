package io.github.ArmijosBrandon.TaskManager.view;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Popup;
public class FormFiltrarView extends Popup{
	
	// Combobox principales
    private final ComboBox<String> categoriaCombo;
    private final ComboBox<String> prioridadCombo;
    private final ComboBox<String> estadoCombo;

    private final Popup popupCategorias;
    private final Popup popupPrioridades;
    private final Popup popupEstados;

    // Contenedores dinámicos
    private final HBox categoriaCheckContainer;
    private final VBox prioridadCheckContainer;
    private final VBox estadoCheckContainer;

    // Botón aplicar filtro
    private final Button btnFiltrar;
	
	public FormFiltrarView() {
		
		// --------- COMBOBOX PRINCIPALES ---------
        categoriaCombo = crearComboBox("Seleccionar");
        prioridadCombo = crearComboBox("Seleccionar");
        estadoCombo = crearComboBox("Seleccionar");
        
        VBox categoriaBox = new VBox(new Label("Categoría:"), categoriaCombo);
        VBox prioridadBox = new VBox(new Label("Prioridad:"), prioridadCombo);
        VBox estadoBox = new VBox(new Label("Estado:"), estadoCombo);
		
		//------POPUPS-------
        //a cateogiras no voy a darle un grupo de checks por determinado, si no que sera dinamico segun las categorias usadas por el usuario, esto se maneja en el controlador
        popupCategorias= new Popup();
		categoriaCheckContainer= new HBox();
		popupCategorias.getContent().add(categoriaCheckContainer);
		
		popupPrioridades=new Popup();
		prioridadCheckContainer = crearCheckGroup("Alta", "Media", "Baja");
		popupPrioridades.getContent().add(prioridadCheckContainer);
		
		popupEstados=new Popup();
        estadoCheckContainer = crearCheckGroup("Pendiente", "En progreso", "Completada");
		popupEstados.getContent().add(estadoCheckContainer);
		
        //----BOTONES DE FILTRAR-------
		btnFiltrar= new Button("Filtrar");
		btnFiltrar.setGraphic(new FontIcon("fas-filter"));
		
		//---Layout Principal
		HBox contenedorCombos= new HBox(15, categoriaBox, prioridadBox, estadoBox);
		VBox root= new VBox(10,contenedorCombos,getBtnFiltrar());
		this.getContent().add(root);
	}
	
	
	//----------------HELPERS--------
	private ComboBox<String> crearComboBox(String prompt) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        return cb;
    }
	
    // Crea una lista vertical de checkboxes
    private VBox crearCheckGroup(String... valores) { //String ... es un Varargs = argumentos variables., donde me puede llegar uno o muchos elemenetos del mismo tipo, similar a un string
        VBox v = new VBox(8);
        for (String val : valores) {
            v.getChildren().add(new CheckBox(val));
        }
        return v;
    }
	
	
    //--------GETTERS--------
    public ComboBox<String> getCategoriaCombo() { return categoriaCombo; }
    public ComboBox<String> getPrioridadCombo() { return prioridadCombo; }
    public ComboBox<String> getEstadoCombo() { return estadoCombo; }

    public Popup getPopupCategorias() { return popupCategorias; }
    public Popup getPopupPrioridades() { return popupPrioridades; }
    public Popup getPopupEstados() { return popupEstados; }

    //retorno los contenedores para modificar su comportamiento al darles click, para que me muestre los popus y conseguir los checks dinamicos que se generaran en categorias
    public HBox getCategoriaCheckContainer() { return categoriaCheckContainer; }
    public VBox getPrioridadCheckContainer() { return prioridadCheckContainer; }
    public VBox getEstadoCheckContainer() { return estadoCheckContainer; }

    public Button getBtnFiltrar() { return btnFiltrar; }






}
