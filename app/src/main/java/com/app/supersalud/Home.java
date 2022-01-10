package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private boolean running = false;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;

    private int objetivo_pasos, objetivo_vasos, progr_water;
    private int progr_steps = -1;

    private DocumentReference usuario, historial;

    private TextView txProgWater, txVasos, txProgSteps, txPasos;
    private ProgressBar progressBarWater, progressBarSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Buscamos los recursos del layout.
        txProgWater = findViewById(R.id.text_progress_water);
        txVasos = findViewById(R.id.num_vasos);
        progressBarWater = findViewById(R.id.progress_bar_water);
        progressBarWater.setProgress(0);

        txProgSteps = findViewById(R.id.text_progress_steps);
        txPasos = findViewById(R.id.num_pasos);
        progressBarSteps = findViewById(R.id.progress_bar_steps);
        progressBarSteps.setProgress(0);

        // Crea un canal de notificaciones (necesario a partir de Android Oreo)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("SuperSalud", "SuperSalud", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        //Guardamos la referencia en la bd al usuario registrado
        usuario = UsuarioSingleton.getInstance().usuario;
        String nombre = UsuarioSingleton.getInstance().nombre;

        //Pones el nombre del usuario arriba de la página
        TextView tx = findViewById(R.id.tx_vecesMedi);
        tx.setText(nombre);

        //Cargamos los datos del usuario, que si no existen se crean
        creaCargaUsuario(nombre);
    }

    /** Funcion que carga los datos de un usuario, que si no existe se crea nuevo **/
    private void creaCargaUsuario(String nombre) {
        usuario.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Si el documento existe, obtenemos los objetivos del usuario
                        Map<String, Object> datos = document.getData();
                        objetivo_vasos = Integer.parseInt(datos.get("objetivo_vasos").toString());
                        objetivo_pasos = Integer.parseInt(datos.get("objetivo_pasos").toString());

                    } else {
                        // No existe el usuario, por lo que se crea con su nombre y objetivos predefinidos
                        objetivo_vasos = 8;
                        objetivo_pasos = 2000;

                        Map<String, Object> datos = new HashMap<>();
                        datos.put("nombre", nombre);
                        datos.put("objetivo_vasos", objetivo_vasos);
                        datos.put("objetivo_pasos", objetivo_pasos);

                        usuario.set(datos);     // Se introducen estos datos en la base de datos
                    }

                    //Guardamos la referencia en la bd del historial del dia actual del usuario registrado
                    historial = HistorialSingleton.getInstance().historial;

                    //Se cargan los datos del historial del dia actual, que si no existen se inicializan a 0
                    creaCargaDatos();

                } else {
                    // TODO: Falta el string
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /** Funcion que carga el historial del dia actual de un usuario, que si no existe se crea de 0 **/
    private void creaCargaDatos() {
        historial.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Si el documento existe, cogemos los valores de ese dia
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

                    // Se muestran los datos del agua y de los pasos del historial
                    updateDataShownWater();
                    updateDataShownSteps();
                } else {
                    //TODO el string
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /** Funcion que incrementa en 1 la cantidad de vasos **/
    public void incrementProgr(View v){
        progr_water+=1;
        updateWaterDB();
        updateDataShownWater();
    }

    /** Funcion que decrementa en 1 la cantidad de vasos **/
    public void redProgr(View v){
        if(progr_water > 0) {
            progr_water-=1;
        }
        updateWaterDB();
        updateDataShownWater();
    }

    /** Actualiza los datos que se muestran respecto a la cantidad de agua **/
    private void updateDataShownWater(){
        //Calculamos el porcentaje de agua bebido frente al objetivo
        int porc = 0;
        if (progr_water > 0 && objetivo_vasos > 0) {
            porc = (progr_water*100)/objetivo_vasos;
        }
        porc = Math.min(porc, 100);

        //Ponemos el valor que tengan los datos
        txProgWater.setText(porc +"%");
        txVasos.setText(progr_water+"");
        progressBarWater.setProgress(porc, true);

        //Notificamos si se ha alcanzado el objetivo
        if (progr_water == objetivo_vasos){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this, "SuperSalud");
            builder.setContentTitle(getResources().getString(R.string.Objetivo_conseguido));
            builder.setContentText(getResources().getString(R.string.Texto_objetivo_agua));
            builder.setSmallIcon(R.drawable.logo);
            builder.setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Home.this);
            managerCompat.notify(1, builder.build());
        }
    }

    /** Actualizamos el valor de vasos en la base de datos **/
    private void updateWaterDB(){
        historial.update("vasos", progr_water);
    }

    /** Actualiza los datos que se muestran respecto a la cantidad de pasos dados **/
    private void updateDataShownSteps(){
        //Calculamos el porcentaje de pasos dados frente al objetivo
        int porc = 0;
        if (progr_steps > 0 && objetivo_pasos > 0) {
            porc = (progr_steps*100)/objetivo_pasos;
        }
        porc = Math.min(porc, 100);

        //Ponemos el valor que tengan los datos
        txProgSteps.setText(porc +"%");
        txPasos.setText(progr_steps+" " + getResources().getString(R.string.steps));
        progressBarSteps.setProgress(porc, true);

        //Notificamos cuando se llega al objetivo, dando un margen por posibles fallos al contabilizar los pasos
        if (progr_steps >= objetivo_pasos && progr_steps < objetivo_pasos+3){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this, "SuperSalud");
            builder.setContentTitle(getResources().getString(R.string.Objetivo_conseguido));
            builder.setContentText(getResources().getString(R.string.Texto_objetivo_pasos));
            builder.setSmallIcon(R.drawable.logo);
            builder.setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Home.this);
            managerCompat.notify(1, builder.build());
        }
    }

    /** Actualizamos el valor de pasos en la base de datos **/
    private void updateStepsDB(){
        //Actualizamos el valor de pasos en la base de datos
        if (historial != null){
            historial.update("pasos", progr_steps);
        }
    }

    //////// METODOS PARA CONFIGURAR EL CUENTAPASOS //////

    @Override
    protected void onResume() {
        super.onResume();
        totalSteps = 0f;
        previousTotalSteps=0f;
        running = true;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            // El dispositivo no dispone de cuentapasos
            //TODO el String
            Toast.makeText(getApplicationContext(), "No sensor detected on this device", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running && event != null && progr_steps != -1) {
            if (totalSteps == 0f) {
                //Es la primera vez en el dispositivo, se restan los pasos contabilizados anteriormente
                totalSteps = event.values[0];
                previousTotalSteps = totalSteps-progr_steps;
            } else {
                totalSteps = event.values[0];
            }
            progr_steps = (int) totalSteps - (int) previousTotalSteps; ;
            updateDataShownSteps();
            updateStepsDB();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //////// FIN DE METODOS PARA CONFIGURAR EL CUENTAPASOS //////


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

    private void cerrarSesion() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    //////// FIN METODOS PARA CONFIGURAR EL MENU //////////

    /** Va a la actividad de Objetivos **/
    public void goObjectives (View view){
        Intent intent = new Intent(this, Objetivos.class);
        startActivity(intent);
    }

    /** Va a la actividad de Pastillero **/
    public void goPills (View view){
        Intent intent = new Intent(this, Pastillero.class);
        startActivity(intent);
    }

    /** Va a la actividad de Historiales **/
    public void goHistoriales (View view){
        Intent intent = new Intent(this, Historiales.class);
        startActivity(intent);
    }
}
