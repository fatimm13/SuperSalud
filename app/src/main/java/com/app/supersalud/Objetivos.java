package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Objetivos extends AppCompatActivity {

    private EditText txObjVasos, txObjPasos;

    private FirebaseFirestore db;
    private DocumentReference usuario;

    private int objetivo_vasos, objetivo_pasos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetivos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = DatabaseSingleton.getInstance().database;
        usuario = UsuarioSingleton.getInstance().usuario;

        txObjVasos = findViewById(R.id.editTextNumber);
        txObjPasos = findViewById(R.id.editTextNumber2);

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
                        Toast.makeText(getApplicationContext(), "Error, el usuario no existe", Toast.LENGTH_SHORT).show();
                    }

                    txObjVasos.setText(objetivo_vasos + "");
                    txObjPasos.setText(objetivo_pasos + "");


                } else {
                    //Gestión de errores para debugging
                    Toast.makeText(getApplicationContext(), "Fallo con " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}