package io.github.ArmijosBrandon.TaskManager.controller;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.ArmijosBrandon.TaskManager.DialogosPantalla;
import io.github.ArmijosBrandon.TaskManager.TablaTareasView;
import io.github.ArmijosBrandon.TaskManager.Data.TareasRepository;
import io.github.ArmijosBrandon.TaskManager.model.Tarea;
import io.github.ArmijosBrandon.TaskManager.view.FormFiltrarView;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class ControladorFiltros {
	private FormFiltrarView formFiltrarView;
	private TablaTareasView tabla_tareas;
	private TareasRepository repoTareas;
	private Popup popupCategoria;
	private Popup popupPrioridades;
	private Popup popupEstados;
	private HBox columnasCategorias;
	private ComboBox<String> comboCategorias;
	private ComboBox<String> comboPrioridades;
	private ComboBox<String> comboEstados;
	private Set<String> categoriasSeleccionadas = new HashSet<>();//coleccion que no permite elementos repetidos
	private Set<String> prioridadesSeleccionadas = new HashSet<>();
	private Set<String> estadosSeleccionados = new HashSet<>();
	public ControladorFiltros(FormFiltrarView formFiltrarView, TablaTareasView tabla_tareas, TareasRepository repoTareas) {
		this.formFiltrarView=formFiltrarView;
		this.tabla_tareas=tabla_tareas;
		this.repoTareas=repoTareas;
		inicializarPopus();
		inicializarEventos();
	}
	private void inicializarEventos() {
		comboCategorias.setOnMouseClicked(e -> {
        	
        	ObservableList<String> categorias= ControladorTareas.getCategorias();
        	// --- GUARDAR SELECCIÓNES ANTERIORES SI ES QUE HAY ANTES DE LIMPIAR ---
        	guardarSeleccionCategorias(columnasCategorias);

        	// --- TOGGLE DEL POPUP ---
        	popupCategoria.getContent().clear();

        	if (popupCategoria.isShowing()) {
        		popupCategoria.hide();
        		return;
        	}

        	
        	int totalCategorias =  categorias.size();
        	int categoriasPorColumna = 5;

        	// número de columnas necesarias
        	//int math.ceil() redondea hacia arriba un resultado en decimal de una division y lo convierte a int
        	int columnas = (int) Math.ceil((double) totalCategorias / categoriasPorColumna); //(double) totalCategorias convierte a double totalCategorias para obtener un resultado en decimal
        	columnasCategorias.getChildren().clear();

        	for (int col = 0; col < columnas; col++) {

        		int desde = col * categoriasPorColumna;
        		int hasta = Math.min(desde + categoriasPorColumna, totalCategorias);

        		List<String> categoriasEnColumna = categorias.subList(desde, hasta);

        		VBox columna = new VBox(5);

        		for (String categoria : categoriasEnColumna) {
        			CheckBox check = new CheckBox(categoria);
        			check.setSelected(categoriasSeleccionadas.contains(categoria)); // Si categorias Seleccionadas contiene esa categoria, marca al checkbox, si no , lo deja desactivado
        			columna.getChildren().add(check);
        		}

        		columnasCategorias.getChildren().add(columna);
        	}

        	// --- MOSTRAR POPUP ---
        	popupCategoria.getContent().add(columnasCategorias);
        	popupCategoria.setAutoHide(true);
        	popupCategoria.show(
        			comboCategorias,
        			comboCategorias.localToScreen(0, comboCategorias.getHeight()).getX(),
        			comboCategorias.localToScreen(0, comboCategorias.getHeight()).getY()
        			);
        });
        
        comboPrioridades.setOnMouseClicked(e->{
        	popupPrioridades= formFiltrarView.getPopupPrioridades();
        	mostrarPopupDebajo(popupPrioridades, comboPrioridades);
        });
        
        comboEstados.setOnMouseClicked(e->{
        	popupEstados= formFiltrarView.getPopupEstados();
        	mostrarPopupDebajo(popupEstados, comboEstados);
        });
        
        formFiltrarView.getBtnFiltrar().setOnAction(e->{
        	guardarFiltrosSeleccionados(formFiltrarView.getPrioridadCheckContainer(),prioridadesSeleccionadas);
        	guardarFiltrosSeleccionados( formFiltrarView.getEstadoCheckContainer(), estadosSeleccionados);
        	guardarSeleccionCategorias(formFiltrarView.getCategoriaCheckContainer());
        	try {
        		if(prioridadesSeleccionadas.isEmpty() && estadosSeleccionados.isEmpty() && categoriasSeleccionadas.isEmpty()) {
        			tabla_tareas.limpiarTabla(); //limpiamos para que no se acumule la lista de las tareas obtenidas con las de la tabla original
        			tabla_tareas.setContenidoPrincipal(repoTareas.obtenerTareas());
        		}else {
        			ObservableList<Tarea>tareas_filtradas= repoTareas.filtrarTabla(categoriasSeleccionadas, prioridadesSeleccionadas, estadosSeleccionados);
        			if(!tareas_filtradas.isEmpty()) {
        				
        				tabla_tareas.remplazarContenido(tareas_filtradas);
        			}else {
        				tabla_tareas.limpiarTabla();
        				tabla_tareas.setPlaceHolder("No hay ninguna tarea que coincida con esos criterios.");
        			}
        			
        			
        		}
				
			} catch (SQLException e1) {
				DialogosPantalla.showError(
					    "No se pudieron obtener las tareas filtradas desde la base de datos.\n\n" +
					    "Posibles causas:\n" +
					    "   • La conexión con la base de datos falló o está cerrada.\n" +
					    "   • Alguno de los filtros seleccionados no coincide con los valores almacenados.\n" +
					    "   • El archivo 'TaskManager.db' está dañado o bloqueado por otro programa.\n" +
					    "   • Existen valores nulos o inesperados en las columnas 'categoria', 'prioridad' o 'estado'.\n\n" +
					    "Qué puedes hacer:\n" +
					    "   • Verifica que la base de datos no esté siendo usada por otra aplicación.\n" +
					    "   • Asegúrate de seleccionar filtros válidos.\n" +
					    "   • Reinicia la aplicación y vuelve a intentar.\n" +
					    "   • Si el problema persiste, elimina 'TaskManager.db' para regenerarlo.\n\n" +
					    "Detalles técnicos:\n" + e1.getMessage()
					);
			}
        	
        });
		
	}
	
	private void inicializarPopus() {
		popupCategoria= formFiltrarView.getPopupCategorias();
        comboCategorias= formFiltrarView.getCategoriaCombo();
        comboPrioridades= formFiltrarView.getPrioridadCombo();
        comboEstados=formFiltrarView.getEstadoCombo();
        columnasCategorias = formFiltrarView.getCategoriaCheckContainer();
		
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
	
	public void mostrarPopupDebajo(Popup popup, ComboBox<String> dueño) {
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
	public void mostrarPopUpFiltrar(Button btnFiltrar) {
		formFiltrarView.setAutoHide(true);//hacer que se cierre al clickear fuera de el
    	if (!formFiltrarView.isShowing()) {//solo mostrar si no se esta mostrando, para evitar que espame el boton y salgan muchas
    		 formFiltrarView.show(btnFiltrar,//objeto de referencia para posicion del popup
    				 btnFiltrar.localToScreen(0, btnFiltrar.getHeight()).getX(),//0 px desde el boton en x
    				 btnFiltrar.localToScreen(0, btnFiltrar.getHeight()).getY());
    	    }
		
	}
	
	private void guardarFiltrosSeleccionados(VBox contenedor, Set<String> filtros_seleccionados) {
		filtros_seleccionados.clear();
		
		for(Node node:contenedor.getChildren()) {
			if(node instanceof CheckBox cb && cb.isSelected()) {
				filtros_seleccionados.add(cb.getText());
				
			}
		}
	}
}
