package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPastilla extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText txNombre, txVeces;
    private Switch sw;
    private CheckBox lunes, martes, miercoles, jueves, viernes, sabado, domingo;
    private Date fecha;
    private TextView txFecha;
    private SimpleDateFormat objSDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pastilla);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Marcamos un formato de fecha
        objSDF = new SimpleDateFormat("dd/MM/yyyy");

        // Obtenemos todos los objetos del layout que necesitamos
        txNombre = findViewById(R.id.in_nombreMed);
        txVeces = findViewById(R.id.in_quantPillsDaily);
        sw = findViewById(R.id.switch1);
        lunes = findViewById(R.id.cL);
        martes = findViewById(R.id.cM);
        miercoles = findViewById(R.id.cX);
        jueves = findViewById(R.id.cJ);
        viernes = findViewById(R.id.cV);
        sabado = findViewById(R.id.cS);
        domingo = findViewById(R.id.cD);
        txFecha = findViewById(R.id.tFecha);

        // Indicamos el onClick del boton
        findViewById(R.id.bFechaFin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Establecemos texto predeterminado
        txNombre.setText("");
        txVeces.setText("1");
        txFecha.setText(getResources().getString(R.string.Indefinido));

        ocultarSemana();
    }

    /** Muestra un calendario **/
    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Si la fecha elegida del calendario es correcta, se guarda
        LocalDate f = LocalDate.of(year, month + 1, dayOfMonth);
        Date date = Date.from(f.atStartOfDay(ZoneId.systemDefault()).toInstant());
        if(date.before(new Date())) {
            // La fecha es pasada
            // TODO string
            Toast.makeText(getApplicationContext(),"La fecha no puede ser pasada", Toast.LENGTH_SHORT).show();
        } else {
            fecha = date;
            txFecha.setText(objSDF.format(fecha));
        }
    }

    /** Funcion para cuando se clickea el switch **/
    public void clickSwitch(View view){
        if(sw.isChecked()){
            mostrarSemana();
        }else{
            ocultarSemana();
        }
    }

    /** Funcion que establece como visible los checkbox de la semana **/
    private void mostrarSemana(){
        Group group = findViewById(R.id.group);
        group.setVisibility(View.VISIBLE);
    }

    /** Funcion que establece como idos los checkbox de la semana **/
    private void ocultarSemana(){
        Group group = findViewById(R.id.group);
        group.setVisibility(View.GONE);
    }

    /** Funcion que añade el medicamento **/
    public void crearPastilla(View view){
        try{
            String nombre = txNombre.getText().toString();
            int veces = (int) Float.parseFloat(txVeces.getText().toString());

            // Comprobamos si los datos del nombre y del num de veces es correcto
            if(nombre == null || nombre.equals("") || veces <=0){
                throw new Exception();
            }

            // Guardamos los datos obtenidos
            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombre);
            datos.put("veces_dia", veces);
            datos.put("fecha_inicio", new Date());
            datos.put("fecha_fin", fecha);

            // Comprobamos si existen dias de repeticiones para añadirlos
            List<String> rep;
            rep = new ArrayList<>();
            if (sw.isChecked()) {
                if (lunes.isChecked()) { rep.add("Mon"); }
                if (martes.isChecked()) { rep.add("Tue"); }
                if (miercoles.isChecked()) { rep.add("Wed"); }
                if (jueves.isChecked()) { rep.add("Thu"); }
                if (viernes.isChecked()) { rep.add("Fri"); }
                if (sabado.isChecked()) { rep.add("Sat"); }
                if (domingo.isChecked()) { rep.add("Sun"); }
            }
            datos.put("repeticiones", rep);

            //Se introducen los datos en la base de datos
            CollectionReference medicacion = (CollectionReference)SingletonMap.getInstance().get(Pastillero.MEDICACION);
            if (medicacion == null) {
                // Ha habido un error, vuelve a la actividad anterior
                Toast.makeText(getApplicationContext(), "Error al conectar con la BD", Toast.LENGTH_SHORT).show();
                goPastillero();
            } else {
                // Se añade la medicacion con los datos establecidos
                medicacion.document().set(datos);
                goPastillero();
            }

        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Introduzca_bien_los_datos), Toast.LENGTH_SHORT).show();
        }
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

    //////// FIN METODOS PARA CONFIGURAR EL MENU //////////

    /** Funcion para volver a la actividad del Pastillero **/
    private void goPastillero() {
        Intent intent = new Intent(this, Pastillero.class);
        this.startActivity(intent);
    }

}