package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class Historiales extends AppCompatActivity {

    public final static String HISTORIALES = "Historial_ref";

    private CollectionReference historiales;

    private ArrayList<Historial> listaHistoriales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historiales);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        historiales = (CollectionReference)SingletonMap.getInstance().get(HISTORIALES);
        if (historiales == null) {
            historiales = UsuarioSingleton.getInstance().usuario.collection("historial");
            SingletonMap.getInstance().put(HISTORIALES, historiales);
        }

        cargaHistoriales();
    }

    private void cargaHistoriales() {

        historiales.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Historial hist;
                    listaHistoriales = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        hist = document.toObject(Historial.class);
                        hist.setFecha(document.getId());
                        listaHistoriales.add(hist);
                        //Guardamos los datos de cada medicamento de la lista en una lista local de la clase pastilla para mostrar más facilmente estos datos a continuación
                        //pill=new Pastilla((String) med.get("nombre"), Integer.parseInt(((Long) med.get("veces_dia")).toString()), fecha, (List<String>) med.get("repeticiones"));

                    }
                    //PastillaListAdapter adaptador = new PastillaListAdapter(Pastillero.this,R.layout.adapter_view_layout, (ArrayList<Pastilla>) listaPastillas);
                    //listaView.setAdapter(adaptador);
                    //Toast.makeText(getApplicationContext(), listaHistoriales.get(0).getFecha() + " // " + listaHistoriales.get(0).getPasos() + " // " + listaHistoriales.get(0).getVasos() , Toast.LENGTH_SHORT).show();

                } else {
                    //Gestión de errores para debugging
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}