package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private FirebaseFirestore db;

    private int progr_water = 0;

    private final static int OBJETIVO_PASOS = 2000;
    private final static int OBJETIVO_VASOS = 8;

    private DocumentReference usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        updateProgressBar();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String email = bundle.getString("email");
        String nombre = bundle.getString("nombre");

        TextView tx = (TextView) findViewById(R.id.textView2);
        tx.setText(nombre);

        db = FirebaseFirestore.getInstance();

        usuario = db.collection("usuarios").document(email);

        creaUsuario(nombre);

        cargaDatos();


    }

    private void creaUsuario(String nombre) {

        usuario.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(getApplicationContext(), "Datos: " + document.getData(), Toast.LENGTH_SHORT).show();
                    } else {
                        // No existe, por lo que se crea
                        Map<String, Object> datos = new HashMap<>();
                        datos.put("nombre", nombre);
                        datos.put("objetivo_vasos", OBJETIVO_VASOS);
                        datos.put("objetivo_pasos", OBJETIVO_PASOS);
                        usuario.set(datos, SetOptions.merge());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void cargaDatos() {

        //LocalDate hoy = LocalDate.now();

        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-mm-dd");
        Date fecha = new Date();
        String hoy = objSDF.format(fecha);

        usuario.collection("historial").document(hoy).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int vasos = 0;
                    int pasos = 0;
                    if (document.exists()) {

                        Map<String, Object> datos = document.getData();
                        vasos = (Integer) datos.get("vasos");
                        pasos = (Integer) datos.get("datos");

                        Toast.makeText(getApplicationContext(), "Datos: " + document.getData(), Toast.LENGTH_SHORT).show();
                    } else {
                        // No existe, por lo que se crea
                        Map<String, Object> datos = new HashMap<>();
                        datos.put("vasos", vasos);
                        datos.put("pasos", pasos);
                        usuario.collection("historial").document(hoy).set(datos, SetOptions.merge());
                    }
                    /**
                    TextView numVasos = (TextView) findViewById(R.id.num_vasos);
                    numVasos.setText(vasos);
                    TextView numPasos = (TextView) findViewById(R.id.num_pasos);
                    numPasos.setText(pasos);
                     **/

                } else {
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void incrementProgr (View v){
        if(progr_water<= 90) {
            progr_water+=10;
        }
        updateProgressBar();
    }
    public void redProgr (View v){
        if(progr_water> 0) {
            progr_water-=10;
        }
        updateProgressBar();
    }

    private void updateProgressBar(){
        TextView txProg = (TextView) findViewById(R.id.text_progress_water);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_water);
        txProg.setText(progr_water+"%");
        progressBar.setProgress(progr_water);
    }

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
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

/**

 // Para guardar datos o actualizar si ya existia (Tendremos un documento por usuario, email es el id):
 db.collection("users").document(email).set(HashMapOf(
    "dato1" to dato1TextView.text.toString(),
    "dato2" to dato2TextView.text.toString(),
 ));

 // Para hacer un get (it es un documento dentro de la bd):
 db.collection("users").document(email).get().addOnSuccessListener {
    dato1TextView.setText(it.get("dato1") as String)
 }

 // Para borrar
 db.collection("users").document(email).delete();


 **/