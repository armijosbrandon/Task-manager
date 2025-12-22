package io.github.armijosbrandon.taskmanager.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.github.armijosbrandon.taskmanager.model.Tarea;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;


public class TablaTareasView extends VBox {
	private TableView<Tarea> tablaTareas;//tabla que se vincula con una observable list que escucha cambios y se actualiza de manera automatica de esa lista
	
	public TablaTareasView() {
		 this.tablaTareas = new TableView<>();
		 inicializarTabla();
	     this.getChildren().add(tablaTareas);
	}

	private void inicializarTabla() {
		tablaTareas.setPlaceholder(new Label("Ingresa tus tareas con el boton \"Nueva tarea\"")); //texto por defecto para cuando no hay tareas
		tablaTareas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); //ajusta todas las columnas para que llenen exactamente el ancho disponible

		//------CREACION DE COLUMNAS--------
		TableColumn<Tarea, Integer> colNum = new TableColumn<>("Num");//esta fila leera un dato entero de un objeto tarea
		TableColumn<Tarea, String> colTareaNombre = new TableColumn<>("Tarea");
		TableColumn<Tarea, LocalDate> colFechaInicio = new TableColumn<>("Fecha Inicio");
		TableColumn<Tarea, LocalDate> colFechaFinal = new TableColumn<>("Fecha Final");
		TableColumn<Tarea, String> colCategoria = new TableColumn<>("Categoría");
		TableColumn<Tarea, String> colPrioridad = new TableColumn<>("Prioridad");
		TableColumn<Tarea, String> colEstado = new TableColumn<>("Estado");
		TableColumn<Tarea, String> colObservacion = new TableColumn<>("Observación");
		
		//------DETERMINAR CONTENIDO--------
		//inidicar a cada columna que getter usar para llenar su contenido
		//setcellFactory determina como quiero  que se vea cada celda dentro de una columna
		colNum.setCellValueFactory(new PropertyValueFactory<>("num")); //busca getNum() de la clase tarea
		colTareaNombre.setCellValueFactory(new PropertyValueFactory<>("tareaNombre"));
		colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
		colFechaFinal.setCellValueFactory(new PropertyValueFactory<>("fechaFinal"));
		colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
		colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
		colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
		colObservacion.setCellValueFactory(new PropertyValueFactory<>("observacion")); 
		
		//-----CONFIGURACION DE CELDAS ADICIONAL------
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //FORMATO QUE DEBE USAR LOS LOCAL DATE
		colFechaInicio.setCellFactory(column->crearCeldaFecha(formatter));//NO EXISTE CELDA POR DEFECTO PARA DATOS LOCALDATE, POR ESO TENGO Q CREAR UNA NUEVA
		colFechaFinal.setCellFactory(column->crearCeldaFecha(formatter));
		//configuracion para textos largos en observacioens
		colObservacion.setCellFactory(col -> new TableCell<Tarea, String>() { //explicado en el metodo de crearCeldaFecha, pero vinculamos un tipo de celda a la columna
		    private final Text text = new Text(); //nodo de texto que cuenta con wrap("Salto de linea")
		    {
		        text.wrappingWidthProperty().bind(col.widthProperty().subtract(10)); //vinculamos el ancho del texto con el ancho con el ancho de la columna menos 10 px
		    }
		    @Override
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);

		        if (empty || item == null || item.isEmpty()) {
		            setGraphic(null);//eliminamos el contenido o nodo que haiga en la celda
		        } else {
		            text.setText(item);
		            setGraphic(text);//se usa este en ves de setText() para multiples lineas con wrap y cambio de tamaño
		            setPrefHeight(Region.USE_COMPUTED_SIZE); // Hace que la celda ajuste su altura automáticamente según el texto con wrap.
		        }
		    }
		});
		
		//-------AÑADIR COLUMAS A LA TABLA------
		tablaTareas.getColumns().addAll(
			    colNum,
			    colTareaNombre,
			    colFechaInicio,
			    colFechaFinal,
			    colCategoria,
			    colPrioridad,
			    colEstado,
			    colObservacion
		);
		VBox.setVgrow(tablaTareas, Priority.ALWAYS);
	}
	
	//OBTENER TAREA SELECCIONADA POR EL USUARIO
    public Tarea getTareaSeleccionada() {
        return tablaTareas.getSelectionModel().getSelectedItem();
    }
    
    //Vincular una observable list a la tabla para que obtenga su informacion y escuche cambios
    public void setContenidoPrincipal(ObservableList<Tarea> tareas) {
    	tablaTareas.setItems(tareas);
    }
    
    //eliminar temporalmente  las tareas actuales y remplazarlas por otras
    public void remplazarContenido(ObservableList<Tarea> tareas) {
    	//OJO, getItems().setAll() solo remplaza el contenido actual de la tabla por una nueva lista o datos, mas no cambia la lista original vinculada con setitems a la tabla, por lo que las operaciones que se hagan, afectan a la lista original
    	tablaTareas.getItems().setAll(tareas);
    }
    
    public ObservableList<Tarea> getContenido() {
    	return tablaTareas.getItems();
    }
    
    //texto que se añade cuando no hay ningua tarea que mostrar
    public void setPlaceHolder(String placeHolder) {
    	tablaTareas.setPlaceholder(new Label(placeHolder));
    }
    
    public void limpiarTabla() {
    	tablaTareas.getItems().clear();
    }
    
    public void refrescar() {
    	tablaTareas.refresh();
    }
    
    //----METODO PARA CREAR UNA CELDA PERSONALIZADA DE FECHA
	private TableCell<Tarea, LocalDate> crearCeldaFecha(DateTimeFormatter formatter) { //crea la celda de fecha para las columnas de fecha inicial y final
	    return new TableCell<>() {
	        @Override
	      //método que JavaFX llama automáticamente cada vez que una celda del TableView necesita dibujarse, actualizarse, mostrar un dato, borrarse, define como se dibuja un dato en este caso la fecha en la celda
	        protected void updateItem(LocalDate date, boolean empty){ //estos parametros los calcula java directamente segun su posicion en la tabla
	            super.updateItem(date, empty);//contruye la celda de manera normal pero ahora con mis valores para no perder las otras funciones
	            setText(empty || date == null ? null : formatter.format(date)); //formate la fecha a lo que quiero y lo pone en la celda
	        }
	    };
	}
    
    




}
