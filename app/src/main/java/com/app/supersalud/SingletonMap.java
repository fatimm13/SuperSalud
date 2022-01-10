package com.app.supersalud;

import java.util.HashMap;

/** Clase que almacena algunas variables compartidas **/
public class SingletonMap extends HashMap<String, Object> {

    // Clase privada estatica para que el objeto se cree solo bajo demanda
    private static class SingletonHolder {
        private static final SingletonMap ourInstance = new SingletonMap();
    }

    public static SingletonMap getInstance() {
        return SingletonHolder.ourInstance;
    }

    private SingletonMap() {}

}