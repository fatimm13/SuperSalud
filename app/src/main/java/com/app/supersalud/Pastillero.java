package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pastillero extends AppCompatActivity {

    public final static String MEDICACION = "Medicacion_ref";

    private CollectionReference medicacion;

    private ListView listaView;
    private Context context;
    private List<Pastilla> listaPastillas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pastillero);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listaView = findViewById(R.id.lista_medicina);
        context = this;
        listaPastillas = new ArrayList<>();

        medicacion = (CollectionReference)SingletonMap.getInstance().get(Pastillero.MEDICACION);
        if (medicacion == null) {
            medicacion = UsuarioSingleton.getInstance().usuario.collection("medicacion");
            SingletonMap.getInstance().put(Pastillero.MEDICACION, medicacion);
        }

        creaCargaPastillas();

    }

    private void creaCargaPastillas() {

        medicacion.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> med;
                    Pastilla pill;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        med = document.getData();
                        Timestamp fecha_fin = (Timestamp) med.get("fecha_fin");
                        Date fecha;
                        if(fecha_fin!= null){
                            fecha = fecha_fin.toDate();
                        }else{
                            fecha = null;
                        }
                        //Guardamos los datos de cada medicamento de la lista en una lista local de la clase pastilla para mostrar más facilmente estos datos a continuación
                        pill=new Pastilla((String) med.get("nombre"), Integer.parseInt(((Long) med.get("veces_dia")).toString()), fecha, (List<String>) med.get("repeticiones"));
                        listaPastillas.add(pill);

                    }
                    PastillaListAdapter adaptador = new PastillaListAdapter(context,R.layout.adapter_view_layout, (ArrayList<Pastilla>) listaPastillas);
                    listaView.setAdapter(adaptador);


                } else {
                    //Gestión de errores para debugging
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void goNewPill (View view){
        Intent intent = new Intent(this, AddPastilla.class);
        startActivity(intent);
    }
}