package io.github.ArmijosBrandon.TaskManager;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Tarea {

    private final SimpleIntegerProperty num; //final hace que solo peuda ser declarado una ves y no se pueda iterar otra ves, lo que puede quitar sus bindings o listeners
    private final SimpleStringProperty tarea_nombre;
    private final ObjectProperty<LocalDate> fecha_inicio;//para objetos dinamicos que no son base de javafx como int o string, dicto que necesito una propiedad que use localdate
    private final ObjectProperty<LocalDate>  fecha_final;
    private final SimpleStringProperty categoria;
    private final SimpleStringProperty prioridad;
    private final SimpleStringProperty estado;
    private final SimpleStringProperty observacion;

    

    public Tarea(int num, String tarea_nombre, LocalDate fecha_inicio, LocalDate fecha_final, String categoria, String prioridad,String estado ,String observacion) {
        this.num = new SimpleIntegerProperty(num); //permite Vincularse (binding) con elementos de la interfaz (por ejemplo un TableView, TextField, etc.).
        this.tarea_nombre = new SimpleStringProperty(tarea_nombre); 
        this.fecha_inicio = new SimpleObjectProperty<>(fecha_inicio);//creo un objeto de local date que escuche bindings y use las propiedades de ObjectProperty<LocalDate> fecha_inicio 
        this.fecha_final = new SimpleObjectProperty<>(fecha_final);
        this.categoria = new SimpleStringProperty(categoria); 
        this.prioridad = new SimpleStringProperty(prioridad); 
        this.estado = new SimpleStringProperty(estado); 
        this.observacion = new SimpleStringProperty(observacion); 
    }
    
    public int getNum() { return num.get(); }
    public String getTarea_nombre() { return tarea_nombre.get(); }
    public LocalDate getFecha_inicio() { return fecha_inicio.get(); }
    public LocalDate getFecha_final() { return fecha_final.get(); }
    public String getCategoria() { return categoria.get(); }
    public String getPrioridad() { return prioridad.get(); }
    public String getEstado() { return estado.get(); }
    public String getObservacion() { return observacion.get(); }
    
    public void setTarea_nombre(String value) { tarea_nombre.set(value); }
    public void setFecha_inicio(LocalDate value) { fecha_inicio.set(value); }
    public void setFecha_final(LocalDate value) { fecha_final.set(value); }
    public void setCategoria(String value) { categoria.set(value); }
    public void setPrioridad(String value) { prioridad.set(value); }
    public void setEstado(String value) { estado.set(value); }
    public void setObservacion(String value) { observacion.set(value); }


}
