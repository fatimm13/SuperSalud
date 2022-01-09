package com.app.supersalud;

import java.util.Date;
import java.util.List;

public class Pastilla {

    private String nombre;
    private int veces_dia;
    private Date fecha_inicio, fecha_fin;
    private List<String> repeticiones;

    public Pastilla(String nombre, int veces_dia, Date fecha_inicio, Date fecha_fin, List<String> repeticiones) {
        this.nombre = nombre;
        this.veces_dia = veces_dia;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.repeticiones = repeticiones;
    }

    public Pastilla(String nombre, int veces_dia, Date fecha_fin, List<String> repeticiones) {
        this.nombre = nombre;
        this.veces_dia = veces_dia;
        this.fecha_fin = fecha_fin;
        this.repeticiones = repeticiones;
    }

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
}
