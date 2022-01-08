package com.app.supersalud;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HistorialSingleton {

    private static HistorialSingleton instance;

    public DocumentReference historial;
    public String fecha;

    private HistorialSingleton() {
        DocumentReference usuario = UsuarioSingleton.getInstance().usuario;
        fecha = getDiaActual();
        historial = usuario.collection("historial").document(fecha);
    }

    public static HistorialSingleton getInstance() {
        if (instance == null) {
            instance = new HistorialSingleton();
        } else if (!instance.fecha.equals(getDiaActual())) {
            instance = new HistorialSingleton();
        }
        return instance;
    }

    private static String getDiaActual() {
        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = new Date();
        return objSDF.format(fecha);
    }

}
