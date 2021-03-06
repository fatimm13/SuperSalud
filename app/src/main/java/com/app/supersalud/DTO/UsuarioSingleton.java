package com.app.supersalud.DTO;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/** Clase para obtener la referencia a la BD del usuario con el metodo Singleton **/
public class UsuarioSingleton {

    private static UsuarioSingleton instance;

    public DocumentReference usuario;
    public String email, nombre;

    private UsuarioSingleton(String e, String n){
        FirebaseFirestore db = DatabaseSingleton.getInstance().database;
        usuario = db.collection("usuarios").document(e);
        email = e;
        nombre = n;
    }

    private UsuarioSingleton(){
        email = "pruebaandempotrados@gmail.com";
        nombre = "Cuenta Prueba";
        FirebaseFirestore db = DatabaseSingleton.getInstance().database;
        usuario = db.collection("usuarios").document(email);
    }

    public static UsuarioSingleton getInstance(String email, String nombre) {
        if (instance == null) {
            instance = new UsuarioSingleton(email, nombre);
        }
        return instance;
    }

    public static UsuarioSingleton getInstance() {
        if (instance == null) {
            instance = new UsuarioSingleton();
        }
        return instance;
    }

    public static void cerrarSesion() {
        instance = null;
    }

}
