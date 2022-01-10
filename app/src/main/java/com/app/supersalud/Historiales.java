package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Historiales extends AppCompatActivity {

    public final static String HISTORIALES = "Historial_ref";
    private CollectionReference historiales;
    private ListView listaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historiales);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listaView = findViewById(R.id.lista_historial);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Crea/obtiene la referencia a la coleccion de historiales del usuario
        historiales = (CollectionReference)SingletonMap.getInstance().get(HISTORIALES);
        if (historiales == null) {
            historiales = UsuarioSingleton.getInstance().usuario.collection("historial");
            SingletonMap.getInstance().put(HISTORIALES, historiales);
        }

        // Carga los historiales y los muestra
        cargaHistoriales();
    }

    /** Funcion que carga los historiales del usuario **/
    private void cargaHistoriales() {
        historiales.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Añadimos cada documento de la coleccion en un ArrayList
                    ArrayList<Historial> listaHistoriales = new ArrayList<>();
                    Historial hist;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        hist = document.toObject(Historial.class);
                        hist.setFecha(document.getId());
                        listaHistoriales.add(hist);
                    }
                    Collections.reverse(listaHistoriales);  // Para mostrarlo por orden cronologico
                    // Muestra los datos del historial
                    HistorialListAdapter adaptador = new HistorialListAdapter(Historiales.this,R.layout.adapter_view_layout_history, listaHistoriales);
                    listaView.setAdapter(adaptador);
                } else {
                    //TODO string
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    ////// METODOS PARA CONFIGURAR EL MENU /////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.cerrar_sesion:
                historiales = null;
                cerrarSesion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cerrarSesion() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

}