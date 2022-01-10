package com.app.supersalud;

import java.util.Date;
import java.util.List;

/** Clase para obtener una Medicacion (Pastilla) de la BD **/
public class Pastilla {

    private String nombre;
    private int veces_dia;
    private Date fecha_inicio, fecha_fin;
    private List<String> repeticiones;
    private String id;

    public Pastilla() {}

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getNombre() {
        return nombre;
    }

    public int getVeces_dia() {
        return veces_dia;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public Date getFecha_fin() {
        return fecha_fin;
    }

    public List<String> getRepeticiones() {
        return repeticiones;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setVeces_dia(int veces_dia) {
        this.veces_dia = veces_dia;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public void setRepeticiones(List<String> repeticiones) {
        this.repeticiones = repeticiones;
    }
}
