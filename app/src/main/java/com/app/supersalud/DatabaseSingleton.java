package com.app.supersalud;

import com.google.firebase.firestore.FirebaseFirestore;

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
