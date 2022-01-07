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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    protected int progr_water, progr_steps;

    private int objetivo_pasos;
    private int objetivo_vasos;

    private DocumentReference usuario, historial;

    private TextView txProg, txVasos;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Buscamos el texto del porcentaje, del numero de vasos y la barra de progresos.
        txProg = findViewById(R.id.text_progress_water);
        txVasos = findViewById(R.id.num_vasos);
        progressBar = findViewById(R.id.progress_bar_water);

    }


    // Lo he puesto aqui porque CREO que asi no peta cuando cierras sesion, pero maybe es cosa de mi portatil
    @Override
    protected void onStart() {
        super.onStart();
        //Guardas la referencia en la bd al usuario registrado en otra variable de clase
        usuario = UsuarioSingleton.getInstance().usuario;
        String nombre = UsuarioSingleton.getInstance().nombre;

        //Pones el nombre del usuario arriba de la página
        TextView tx = findViewById(R.id.textView2);
        tx.setText(nombre);
        //usuario = db.collection("usuarios").document(email);

        //Si no existe el usuario lo crea
        creaCargaUsuario(nombre);

        //EN ESTE PUNTO POR ALGUNA RAZON progr_water LO TRATA COMO 0, TENERLO MUY EN CUENTA
    }

    private void creaCargaUsuario(String nombre) {

        usuario.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Si el documento existe cogemos los objetivos del usuario
                        Map<String, Object> datos = document.getData();
                        objetivo_vasos = Integer.parseInt(datos.get("objetivo_vasos").toString());
                        objetivo_pasos = Integer.parseInt(datos.get("objetivo_pasos").toString());

                    } else {
                        // No existe el usuario, por lo que se crea
                        objetivo_vasos = 8;
                        objetivo_pasos = 2000;

                        Map<String, Object> datos = new HashMap<>();

                        //Se inicializan el nombre como el nombre introducido y los otros valores por ahora están hardcodeados.
                        datos.put("nombre", nombre);
                        datos.put("objetivo_vasos", objetivo_vasos);
                        datos.put("objetivo_pasos", objetivo_pasos);

                        //Se introducen estos datos en la base de datos
                        usuario.set(datos);
                    }

                    //Creamos la fecha de hoy
                    SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");
                    Date fecha = new Date();
                    String hoy = objSDF.format(fecha);

                    historial = usuario.collection("historial").document(hoy);

                    //Se cargan los valores del usuario en variables de la clase y se cambian los datos mostrados
                    creaCargaDatos();

                } else {
                    //Gestión de errores para debugging
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void creaCargaDatos() {

        historial.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Si el documento existe cogemos los vasos de agua que tenga este día
                        Map<String, Object> datos = document.getData();
                        progr_water = Integer.parseInt(datos.get("vasos").toString());
                        progr_steps = Integer.parseInt(datos.get("pasos").toString());

                    } else {
                        // No existe, por lo que se crean los datos a introducir poniendo ambos a 0
                        Map<String, Object> datos = new HashMap<>();
                        progr_water = 0;
                        progr_steps = 0;
                        datos.put("vasos", progr_water);
                        datos.put("pasos", progr_steps);

                        //Guardamos estos datos en la base de datos
                        historial.set(datos);
                    }
                    updateDataShown();
                } else {
                    //Gestion de errores
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void incrementProgr (View v){
        progr_water+=1;
        updateDatabase();
        updateDataShown();

    }
    public void redProgr (View v){
        if(progr_water> 0) {
            progr_water-=1;
        }
        updateDatabase();
        updateDataShown();
    }

    private void updateDataShown(){
        //Calculamos el porcentaje de agua bebido frente al objetivo
        int porc = 0;
        if (progr_water > 0 && objetivo_vasos > 0) {
            porc = (progr_water*100)/objetivo_vasos;
        }
        porc = Math.min(porc, 100);

        //Ponemos el valor que tengan los datos
        txProg.setText((porc) +"%");
        txVasos.setText(progr_water+"");
        progressBar.setProgress(porc);
    }

    private void updateDatabase(){
        //Actualizamos el valor de vasos en la base de datos
        historial.update("vasos", progr_water);
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
                usuario = null;
                historial = null;
                cerrarSesion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //////// FIN METODOS PARA CONFIGURAR EL MENU //////////

    private void cerrarSesion() {
        UsuarioSingleton.cerrarSesion();
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    public void goObjectives (View view){
        Intent intent = new Intent(this, Objetivos.class);
        startActivity(intent);
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