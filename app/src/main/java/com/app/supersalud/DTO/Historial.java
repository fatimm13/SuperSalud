package com.app.supersalud.DTO;

/** Clase para establecer y obtener el Historial **/
public class Historial {

    private int vasos, pasos;
    private String fecha;

    public Historial() {}

    public int getVasos() {
        return vasos;
    }

    public void setVasos(int vasos) {
        this.vasos = vasos;
    }

    public int getPasos() {
        return pasos;
    }

    public void setPasos(int pasos) {
        this.pasos = pasos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
