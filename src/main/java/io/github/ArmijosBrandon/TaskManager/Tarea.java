package io.github.ArmijosBrandon.TaskManager;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Tarea {

    private final SimpleIntegerProperty num; //final hace que solo peuda ser declarado una ves y no se pueda iterar otra ves, lo que puede quitar sus bindings o listeners
    private final SimpleStringProperty tareaNombre;
    private final ObjectProperty<LocalDate> fechaInicio;//para objetos dinamicos que no son base de javafx como int o string, dicto que necesito una propiedad que use localdate
    private final ObjectProperty<LocalDate>  fechaFinal;
    private final SimpleStringProperty categoria;
    private final SimpleStringProperty prioridad;
    private final SimpleStringProperty estado;
    private final SimpleStringProperty observacion;

    

    public Tarea(int num, String tareaNombre, LocalDate fechaInicio, LocalDate fechaFinal, String categoria, String prioridad,String estado ,String observacion) {
        this.num = new SimpleIntegerProperty(num); //permite Vincularse (binding) con elementos de la interfaz (por ejemplo un TableView, TextField, etc.).
        this.tareaNombre = new SimpleStringProperty(tareaNombre); 
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);//creo un objeto de local date que escuche bindings y use las propiedades de ObjectProperty<LocalDate> fechaInicio 
        this.fechaFinal = new SimpleObjectProperty<>(fechaFinal);
        this.categoria = new SimpleStringProperty(categoria); 
        this.prioridad = new SimpleStringProperty(prioridad); 
        this.estado = new SimpleStringProperty(estado); 
        this.observacion = new SimpleStringProperty(observacion); 
    }
    
    public int getNum() { return num.get(); }
    public String getTareaNombre() { return tareaNombre.get(); }
    public LocalDate getFechaInicio() { return fechaInicio.get(); }
    public LocalDate getFechaFinal() { return fechaFinal.get(); }
    public String getCategoria() { return categoria.get(); }
    public String getPrioridad() { return prioridad.get(); }
    public String getEstado() { return estado.get(); }
    public String getObservacion() { return observacion.get(); }
    
    public void setTareaNombre(String value) { tareaNombre.set(value); }
    public void setFechaInicio(LocalDate value) { fechaInicio.set(value); }
    public void setFechaFinal(LocalDate value) { fechaFinal.set(value); }
    public void setCategoria(String value) { categoria.set(value); }
    public void setPrioridad(String value) { prioridad.set(value); }
    public void setEstado(String value) { estado.set(value); }
    public void setObservacion(String value) { observacion.set(value); }


}
