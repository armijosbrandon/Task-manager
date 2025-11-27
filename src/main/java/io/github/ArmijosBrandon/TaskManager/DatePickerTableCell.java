package io.github.ArmijosBrandon.TaskManager;

import java.time.LocalDate;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

//clase para crear un datepicker para cuando quiera editar en mi tableview de mi view
//creo una clase datepicker para celdas que se vinculara con el objeto de la columna en q este por el uso de <s> que es un parametro generico que se remplazara
//extiende de una celda de la tabla de tipo localdate con el objeto antes declarado
public class DatePickerTableCell<S> extends TableCell<S, LocalDate> { 
	
    private DatePicker datePicker;

    public DatePickerTableCell() {
        super(); //inicializo la celda como una celda normal
        datePicker = new DatePicker();
        datePicker.setEditable(false); // para no escribir texto

        // Cuando el usuario selecciona una fecha guarda el valor en la celda
        datePicker.setOnAction(e -> {
            commitEdit(datePicker.getValue());//indica que la celda tardo de editarse y guarda ese valor
        });

        // guardar el ultimo valor de fecha si es que el usuario da click afuera de la celda
        //add listener agrega un evento cual algo cambia, en este caso el focus. parametro1 (obs) es el objeto que se esta utilizando, oldvalue el valor anterior(true) y el paramtro 3 el nuevo valor(false)
        datePicker.focusedProperty().addListener((obs, oldValue, newValue) -> { 
            if (!newValue) { // si se perdio el foco(focus es false) entonces
                commitEdit(datePicker.getValue());
            }
        });
    }

    @Override
    public void startEdit() {
        //solo permite editar si la celda no esta vacia
    	if (!isEmpty()) {
            super.startEdit(); //cambia el estado de la celda a modo editar
            datePicker.setValue(getItem());
            setGraphic(datePicker);//remplaza el contenido de la celda por el date picker
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);//Muestra SOLO el DatePicker, no texto.
            datePicker.requestFocus();//que el cursor vaya directo a la celda
        }
    }
    
    //cuando se cancele la edicion
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem() != null ? getItem().toString() : null);
        setGraphic(null);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override 
    //sirve para cuando se haga scroll y se tenga que cargar celdas
    protected void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                datePicker.setValue(date);
                setGraphic(datePicker);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(date != null ? date.toString() : "");
                setGraphic(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }
}

