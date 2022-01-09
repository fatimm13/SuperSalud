package com.app.supersalud;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PastillasSingleton {
    private static PastillasSingleton instance;

    public CollectionReference medicacion;
    public String fecha;

    private PastillasSingleton() {
        DocumentReference usuario = UsuarioSingleton.getInstance().usuario;
        fecha = getDiaActual();
        medicacion = usuario.collection("pastillas");
    }

    public static PastillasSingleton getInstance() {
        if (instance == null) {
            instance = new PastillasSingleton();
        } else if (!instance.fecha.equals(getDiaActual())) {
            instance = new PastillasSingleton();
        }
        return instance;
    }

    private static String getDiaActual() {
        //Hará falta para gestionar el tema de los recordatorios creo
        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = new Date();
        return objSDF.format(fecha);
    }

    public static void cerrarSesion() {
        instance = null;
    }

}
