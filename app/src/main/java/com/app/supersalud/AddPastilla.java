package com.app.supersalud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPastilla extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText txNombre, txDias, txVeces;
    private Switch sw;
    private CheckBox lunes, martes, miercoles, jueves, viernes, sabado, domingo;
    private Date fecha;
    private TextView txFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pastilla);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txNombre = findViewById(R.id.in_nombreMed);
        txDias = findViewById(R.id.in_dayAmount);
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

        findViewById(R.id.bFechaInicio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });


        txNombre.setText("");
        txDias.setText("1");
        txVeces.setText("1");

        txFecha.setText("Seleccione fecha");

        ocultarSemana();
    }

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
        LocalDate f = LocalDate.of(year, month + 1, dayOfMonth);
        fecha = Date.from(f.atStartOfDay(ZoneId.systemDefault()).toInstant());
        txFecha.setText(f.toString());
    }

    public void clickSwitch(View view){
        if(sw.isChecked()){
            mostrarSemana();
        }else{
            ocultarSemana();
        }
    }

    private void mostrarSemana(){
        Group group = findViewById(R.id.group);
        group.setVisibility(View.VISIBLE);
    }

    private void ocultarSemana(){
        Group group = findViewById(R.id.group);
        group.setVisibility(View.GONE);
    }

    public void crearPastilla(View view){

        try{
            String nombre = txNombre.getText().toString();
            //String fecha = PastillasSingleton.getInstance().fecha;
            int dias = (int) Float.parseFloat(txDias.getText().toString());;
            int veces = (int) Float.parseFloat(txVeces.getText().toString());;

            Map<String, Object> datos = new HashMap<>();

            datos.put("nombre", nombre);
            datos.put("num_dias", dias);
            datos.put("veces_dia", veces);
            datos.put("fecha_inicio", new Date());
            datos.put("fecha_fin", fecha);

            List<String> rep = Arrays.asList();
            if (sw.isChecked()) {
                if (lunes.isChecked()) { rep.add("lunes"); }
                if (martes.isChecked()) { rep.add("martes"); }
                if (miercoles.isChecked()) { rep.add("miercoles"); }
                if (jueves.isChecked()) { rep.add("jueves"); }
                if (viernes.isChecked()) { rep.add("viernes"); }
                if (sabado.isChecked()) { rep.add("sabado"); }
                if (domingo.isChecked()) { rep.add("domingo"); }
            }
            datos.put("repeticiones", rep);

            //Se introducen estos datos en la base de datos
            CollectionReference medicacion = (CollectionReference)SingletonMap.getInstance().get(Pastillero.MEDICACION);
            if (medicacion == null) {
                //medicacion = UsuarioSingleton.getInstance().usuario.collection("medicacion");
                //SingletonMap.getInstance().put(Pastillero.MEDICACION, medicacion);
                goPastillero();
            }

            medicacion.document().set(datos);

            goPastillero();


        } catch(Exception e) {
            Toast.makeText(getApplicationContext(),"Introduzca bien los datos " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void goPastillero() {
        Intent intent = new Intent(this, Pastillero.class);
        this.startActivity(intent);
    }


}