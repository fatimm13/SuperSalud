package com.app.supersalud;

import com.google.firebase.firestore.FirebaseFirestore;

/** Clase para obtener la conexion con la BD con el metodo Singleton **/
public class DatabaseSingleton {

    public FirebaseFirestore database;
    private static DatabaseSingleton instance;

    private DatabaseSingleton(){
        database = FirebaseFirestore.getInstance();
    }

    public static DatabaseSingleton getInstance() {
        if (instance == null) {
            instance = new DatabaseSingleton();
        }
        return instance;
    }
}
