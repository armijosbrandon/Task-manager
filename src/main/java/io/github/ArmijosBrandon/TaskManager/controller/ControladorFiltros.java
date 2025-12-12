package io.github.ArmijosBrandon.TaskManager.controller;

//---archivos vinculados
import io.github.ArmijosBrandon.TaskManager.DialogosPantalla;
import io.github.ArmijosBrandon.TaskManager.TablaTareasView;
import io.github.ArmijosBrandon.TaskManager.Data.CategoriasRepository;
import io.github.ArmijosBrandon.TaskManager.Data.TareasRepository;
import io.github.ArmijosBrandon.TaskManager.model.Tarea;
import io.github.ArmijosBrandon.TaskManager.view.FormFiltrarView;

//--LIBRERIAS
import java.sql.SQLException;
import java.util.HashSet;//lista que no permite elementos repetidos
import java.util.List; //lista de elementos
import java.util.Set; // funciona con hashset
import javafx.collections.ObservableList;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class ControladorFiltros {
	//----VIEWS VINCULADAS
	private final FormFiltrarView formFiltrarView;
	private final TablaTareasView tablaTareas;
	
	//----REPOSITORIOS
	private final TareasRepository repoTareas;
	private final CategoriasRepository repoCategorias;
	
	//----ELEMENTOS
	private Popup popupCategoria;
	private Popup popupPrioridades;
	private Popup popupEstados;
	
	private ComboBox<String> comboCategorias;
	private ComboBox<String> comboPrioridades;
	private ComboBox<String> comboEstados;
	
	private Set<String> categoriasSeleccionadas = new HashSet<>();//coleccion que no permite elementos repetidos
	private Set<String> prioridadesSeleccionadas = new HashSet<>();
	private Set<String> estadosSeleccionados = new HashSet<>();
	
	private HBox columnasCategorias;//USADO PARA ALMACENAR CATEGORIAS TIPO CHECKBOX DE FORMA DINAMICA
	
	
	public ControladorFiltros(FormFiltrarView formFiltrarView, TablaTareasView tablaTareas, TareasRepository repoTareas, CategoriasRepository repoCategorias) {
		this.formFiltrarView=formFiltrarView;
		this.tablaTareas=tablaTareas;
		this.repoTareas=repoTareas;
		this.repoCategorias=repoCategorias;
		inicializarComponentes();
		inicializarEventos();
	}
	
	private void inicializarComponentes() {
	    popupCategoria = formFiltrarView.getPopupCategorias();
	    popupPrioridades = formFiltrarView.getPopupPrioridades();
	    popupEstados = formFiltrarView.getPopupEstados();
	    comboCategorias = formFiltrarView.getCategoriaCombo();
	    comboPrioridades = formFiltrarView.getPrioridadCombo();
	    comboEstados = formFiltrarView.getEstadoCombo();
	    columnasCategorias = formFiltrarView.getCategoriaCheckContainer();
	}

	
	private void inicializarEventos() {
		comboCategorias.setOnMouseClicked(e ->cargarCategoriasCombo());
        
        comboPrioridades.setOnMouseClicked(e->{
        	mostrarPopupDebajo(popupPrioridades, comboPrioridades);
        });
        
        comboEstados.setOnMouseClicked(e->{
        	mostrarPopupDebajo(popupEstados, comboEstados);
        });
        
        formFiltrarView.getBtnFiltrar().setOnAction(e->filtrarTareas());
		
	}
	
	//----METODO USADO POR EL CONTROLLER PRINCIPAL
	public void mostrarPopUpFiltrar(Button btnFiltrar) {
		formFiltrarView.setAutoHide(true);//hacer que se cierre al clickear fuera de el
    	if (!formFiltrarView.isShowing()) {//solo mostrar si no se esta mostrando, para evitar que espame el boton y salgan muchas
    		 formFiltrarView.show(btnFiltrar,//objeto de referencia para posicion del popup
    				 btnFiltrar.localToScreen(0, btnFiltrar.getHeight()).getX(),//0 px desde el boton en x
    				 btnFiltrar.localToScreen(0, btnFiltrar.getHeight()).getY());
    	    }
		
	}
	
	//--METODOS DE BOTONES Y COMBOS
	private void cargarCategoriasCombo() {
		ObservableList<String> categorias = null;//lista que escucha cambios automaticamente
		try {
			categorias = repoCategorias.obtenerCategorias();//cargamos nuestras categorias actuales
		} catch (SQLException e1) {
			DialogosPantalla.showErrorDB("No se pudieron cargar las categorías.", e1);
		}
		
    	// --- GUARDAR SELECCIÓNES ANTERIORES SI ES QUE HAY ANTES DE LIMPIAR, ESTO SE USA MAS CUANDO SE RE ABRE POR SEGUNDA VES EL COMBO POR QUE ESTE PIERDE INFORMACION ---
    	guardarSeleccionCategorias(columnasCategorias);

    	// --- TOGGLE DEL POPUP ---
    	popupCategoria.getContent().clear();//Limpiamos contenido previo si hay

    	if (popupCategoria.isShowing()) {
    		popupCategoria.hide();
    		return;
    	}

    	
    	//----CARGAR CATEGORIAS SELECCIONADAS Y CONTROL EN COLUMNAS DE LAS MISMAS
    	int totalCategorias =  categorias.size();
    	int categoriasPorColumna = 5;

    	// número de columnas necesarias
    	//int math.ceil() redondea hacia arriba un resultado en decimal de una division y lo convierte a int
    	int columnas = (int) Math.ceil((double) totalCategorias / categoriasPorColumna); //(double) totalCategorias convierte a double totalCategorias para obtener un resultado en decimal
    	columnasCategorias.getChildren().clear();

    	for (int col = 0; col < columnas; col++) {

    		int desde = col * categoriasPorColumna;
    		int hasta = Math.min(desde + categoriasPorColumna, totalCategorias);//me devuelde el minimo entre esos dos datos, asi solo hacemos hasta el tamaño de la lista por si  desde + categoriasPorColumna sobrepasa su tamaño

    		List<String> categoriasEnColumna = categorias.subList(desde, hasta);//sublista de categorias que va a ver por culumna

    		VBox columna = new VBox(5);//espaciado

    		for (String categoria : categoriasEnColumna) {
    			CheckBox check = new CheckBox(categoria);
    			check.setSelected(categoriasSeleccionadas.contains(categoria)); // Si categorias Seleccionadas contiene esa categoria, marca al checkbox, si no , lo deja desactivado
    			columna.getChildren().add(check);//añadimos a una columna
    		}

    		columnasCategorias.getChildren().add(columna);//añadimos a las columnas generales la recien creada
    	}

    	// --- MOSTRAR POPUP ---
    	popupCategoria.getContent().add(columnasCategorias);
    	mostrarPopupDebajo(popupCategoria, comboCategorias);
	}
	
	private void filtrarTareas(){
		
		//---OBTENCION DE LISTAS DE FILTROS SELECCIONADOS
		guardarFiltrosSeleccionados(formFiltrarView.getPrioridadCheckContainer(),prioridadesSeleccionadas);
    	guardarFiltrosSeleccionados( formFiltrarView.getEstadoCheckContainer(), estadosSeleccionados);
    	guardarSeleccionCategorias(formFiltrarView.getCategoriaCheckContainer());//LOGICA DIFERENTE POR LO QUE NO USO EL MISMO METODO
    	
    	try {
    		//si no hay nada recargo mi tabla principal
    		if(prioridadesSeleccionadas.isEmpty() && estadosSeleccionados.isEmpty() && categoriasSeleccionadas.isEmpty()) {
    			tablaTareas.limpiarTabla(); //limpiamos para que no se acumule la lista de las tareas filtradas con las de la tabla original
    			tablaTareas.setContenidoPrincipal(repoTareas.obtenerTareas()); //vlvemos a recargar la lista principal
    			if(tablaTareas.getContenido().isEmpty()) {//para que en operaciones como filtrar  si es que regresesa a la tabla normal y no hay tareas que cargue el siguiente place holder
    				tablaTareas.setPlaceHolder("Ingresa tus tareas con el boton \"Nueva tarea\"");
    			}
    		}else {//cargo las tareas con los filtros del usuario
    			ObservableList<Tarea>tareasFiltradas= repoTareas.filtrarTabla(categoriasSeleccionadas, prioridadesSeleccionadas, estadosSeleccionados);
    			if(!tareasFiltradas.isEmpty()) {
    				tablaTareas.remplazarContenido(tareasFiltradas);//remplazo el contenido actual
    			}else {//por si no hay tareas que coincidan con los filtros
    				tablaTareas.limpiarTabla();
    				tablaTareas.setPlaceHolder("No hay ninguna tarea que coincida con esos criterios.");
    			}
    		}
			
		} catch (SQLException e1) {
			DialogosPantalla.showErrorDB("No se pudieron obtener las tareas filtradas desde la base de datos.", e1);
		}
	}
	
	
	//------METODOS UTILES
	private void guardarFiltrosSeleccionados(VBox contenedor, Set<String> filtrosSeleccionados) {
		filtrosSeleccionados.clear();
		
		for(Node node:contenedor.getChildren()) {
			if(node instanceof CheckBox cb && cb.isSelected()) {
				filtrosSeleccionados.add(cb.getText());
				
			}
		}
	}
	private void guardarSeleccionCategorias(HBox columnasCategorias) {
	    categoriasSeleccionadas.clear();//Antes de guardar las nuevas selecciones, borra las anteriores
	    for (Node colNode : columnasCategorias.getChildren()) {//recorro cada columna
	        if (colNode instanceof VBox col) { //verifica que sea un vbox
	            for (Node node : col.getChildren()) {//reccore cada checkbox
	                if (node instanceof CheckBox cb && cb.isSelected()) {//si es un checkbox y esta seleccionado
	                    categoriasSeleccionadas.add(cb.getText());
	                }
	            }
	        }
	    }
	}
	
	private void mostrarPopupDebajo(Popup popup, ComboBox<String> dueño) {
	    // Si ya está abierto → cerrarlo (toggle)
	    if (popup.isShowing()) {
	        popup.hide();
	        return;
	    }

	    popup.setAutoHide(true);
	    popup.show(dueño, dueño.localToScreen(0,dueño.getHeight()).getX(),
	    		dueño.localToScreen(0,dueño.getHeight()).getY()
    		);
	}
	
	
	
}
