package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pastillero);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listaView = findViewById(R.id.lista_medicina);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Crea/obtiene la referencia a la coleccion de medicacion del usuario
        medicacion = (CollectionReference)SingletonMap.getInstance().get(Pastillero.MEDICACION);
        if (medicacion == null) {
            medicacion = UsuarioSingleton.getInstance().usuario.collection("medicacion");
            SingletonMap.getInstance().put(Pastillero.MEDICACION, medicacion);
        }

        // Carga los medicamentos
        cargaPastillas();
    }

    private void cargaPastillas() {

        medicacion.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Recorremos la coleccion de medicamentos, si finalizaron en el pasado se eliminan
                    Pastilla pill;
                    ArrayList<Pastilla> listaPastillas = new ArrayList<>();
                    Date hoy = new Date();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        pill = document.toObject(Pastilla.class);
                        pill.setId(document.getId());
                        Date fechafin = pill.getFecha_fin();
                        if (fechafin != null && fechafin.before(hoy)) {
                            borrarMedicacion(document.getId());
                        } else {
                            listaPastillas.add(pill);
                        }
                    }
                    // Muestra los datos de los medicamentos
                    PastillaListAdapter adaptador = new PastillaListAdapter(Pastillero.this,R.layout.adapter_view_layout, (ArrayList<Pastilla>) listaPastillas);
                    listaView.setAdapter(adaptador);

                } else {
                    //TODO string
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /** Funcion para borrar una medicacion a traves de su id **/
    public void borrarMedicacion(String id) {
        medicacion.document(id).delete();
    }

    /** Funcion para ir a la actividad de AddPastilla **/
    public void goNewPill (View view){
        Intent intent = new Intent(this, AddPastilla.class);
        startActivity(intent);
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