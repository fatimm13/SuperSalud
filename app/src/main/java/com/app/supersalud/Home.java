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

    //private static final String TAG = "GoogleActivity";
    private FirebaseFirestore db;

    protected int progr_water, progr_steps;

    private final static int OBJETIVO_PASOS = 2000;
    private final static int OBJETIVO_VASOS = 8;

    private DocumentReference usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Sacas del intent los datos del usuario y los guardas en variables locales
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String email = bundle.getString("email");
        String nombre = bundle.getString("nombre");

        //Pones el nombre del usuario arriba de la página
        TextView tx = findViewById(R.id.textView2);
        tx.setText(nombre);

        //Guardas una conexión a la base de datos en una variable de la clase
        db = FirebaseFirestore.getInstance();

        //Guardas la referencia en la bd al usuario registrado en otra variable de clase
        usuario = db.collection("usuarios").document(email);

        //Si no existe el usuario lo crea
        creaUsuario(nombre);

        //Se cargan los valores del usuario en variables de la clase y se cambian los datos mostrados
        cargaDatos();

        //EN ESTE PUNTO POR ALGUNA RAZON progr_water LO TRATA COMO 0, TENERLO MUY EN CUENTA
    }

    private void creaUsuario(String nombre) {

        usuario.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Muestra los datos del usuario si este existe, no hace más, es para debugging
                        //Toast.makeText(getApplicationContext(), "DatosB: " + document.getData(), Toast.LENGTH_SHORT).show();
                    } else {
                        // No existe, por lo que se crea
                        Map<String, Object> datos = new HashMap<>();

                        //Se inicializan el nombre como el nombre introducido y los otros valores por ahora están hardcodeados.
                        datos.put("nombre", nombre);
                        datos.put("objetivo_vasos", OBJETIVO_VASOS);
                        datos.put("objetivo_pasos", OBJETIVO_PASOS);

                        //Se introducen estos datos en la base de datos
                        usuario.set(datos, SetOptions.merge());
                    }
                } else {

                    //Gestión de errores para debugging
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void cargaDatos() {

        //LocalDate hoy = LocalDate.now();

        //Creamos la fecha de hoy
        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = new Date();
        String hoy = objSDF.format(fecha);

        usuario.collection("historial").document(hoy).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Si el documento existe cogemos los vasos de agua que tenga este día
                        Map<String, Object> datos = document.getData();
                        progr_water = Integer.parseInt(datos.get("vasos").toString());
                        progr_steps = Integer.parseInt(datos.get("pasos").toString());

                        updateDataShown();

                    } else {
                        // No existe, por lo que se crean los datos a introducir poniendo ambos a 0
                        Map<String, Object> datos = new HashMap<>();
                        progr_water = 0;
                        datos.put("vasos", 0);
                        datos.put("pasos", 0);

                        //Guardamos estos datos en la base de datos
                        usuario.collection("historial").document(hoy).set(datos, SetOptions.merge());
                    }
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

        //Buscamos el texto del porcentaje, del numero de vasos y la barra de progresos.
        TextView txProg = findViewById(R.id.text_progress_water);
        TextView txVasos = findViewById(R.id.num_vasos);
        ProgressBar progressBar = findViewById(R.id.progress_bar_water);

        //Calculamos el porcentaje de agua bebido frente al objetivo
        int porc = (progr_water*100)/OBJETIVO_VASOS;

        //Ponemos el valor que tengan los datos
        txProg.setText((porc>100 ? 100 : porc) +"%");
        txVasos.setText(progr_water+"");
        progressBar.setProgress(porc>100 ? 100 : porc);
    }

    private void updateDatabase(){

        //Cogemos de nuevo la fecha de hoy
        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = new Date();
        String hoy = objSDF.format(fecha);

        //Actualizamos el valor de vasos en la base de datos
        Map<String, Object> datos = new HashMap<>();
        datos.put("vasos", progr_water);
        usuario.collection("historial").document(hoy).set(datos, SetOptions.merge());
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

    public void goObjectives (View view){
        Bundle b = new Bundle();
        b.putString("email", usuario.getId());
        Intent intent = new Intent(this, Objetivos.class);
        intent.putExtras(b);
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