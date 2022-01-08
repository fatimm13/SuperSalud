package com.app.supersalud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class AddPastilla extends AppCompatActivity {

    private EditText txNombre, txDias, txVeces;

    private CollectionReference pastillas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pastilla);

        txNombre = findViewById(R.id.in_nombreMed);
        txDias = findViewById(R.id.in_dayAmount);
        txVeces = findViewById(R.id.in_quantPillsDaily);

        txNombre.setText("");
        txDias.setText("1");
        txVeces.setText("1");

        pastillas = PastillasSingleton.getInstance().pastillas;

        ocultarSemana();
    }

    public void clickSwitch(View view){
        Switch sw = findViewById(R.id.switch1);
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
        String fecha = PastillasSingleton.getInstance().fecha;
        int dias = (int) Float.parseFloat(txDias.getText().toString());;
        int veces = (int) Float.parseFloat(txVeces.getText().toString());;

        Map<String, Object> datos = new HashMap<>();

        datos.put("nombre", nombre);
        datos.put("num_dias", dias);
        datos.put("veces_dia", veces);
        datos.put("fecha_inicio", fecha);

        //Se introducen estos datos en la base de datos
        pastillas.document().set(datos);
        Intent intent = new Intent(this, Pastillero.class);
        this.startActivity(intent);
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(),"Introduzca bien los datos", Toast.LENGTH_SHORT).show();
        }
    }
}