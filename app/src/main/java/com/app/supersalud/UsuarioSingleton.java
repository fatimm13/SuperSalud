package com.app.supersalud;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        FirebaseFirestore db = DatabaseSingleton.getInstance().database;
        usuario = db.collection("usuarios").document();
        email = "anonimo@anonimo.com";
        nombre = "Anonimo";
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

}
