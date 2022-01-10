package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.supersalud.DTO.UsuarioSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class Objetivos extends AppCompatActivity {

    private EditText txObjVasos, txObjPasos;
    private DocumentReference usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetivos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtenemos los objetos del layout
        txObjVasos = findViewById(R.id.editTextNumber);
        txObjPasos = findViewById(R.id.editTextNumber2);

        // Obtenemos el usuario y sus objetivos
        usuario = UsuarioSingleton.getInstance().usuario;
        usuario.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int objetivo_vasos, objetivo_pasos;
                    if (document.exists()) {
                        //Si el documento existe cogemos los objetivos del usuario
                        Map<String, Object> datos = document.getData();
                        objetivo_vasos = Integer.parseInt(datos.get("objetivo_vasos").toString());
                        objetivo_pasos = Integer.parseInt(datos.get("objetivo_pasos").toString());
                        txObjVasos.setText(objetivo_vasos + "");
                        txObjPasos.setText(objetivo_pasos + "");
                    } else {
                        // No existe el usuario, error
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Error_de_BD), Toast.LENGTH_SHORT).show();
                        cerrarSesion();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Error_de_BD), Toast.LENGTH_SHORT).show();
                    cerrarSesion();
                }
            }
        });

        // Establecemos la funcion del boton de actualizar
        Button updateButton = findViewById(R.id.bActualiza);
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    int objetivo_vasos = (int) Float.parseFloat(txObjVasos.getText().toString());
                    int objetivo_pasos = (int) Float.parseFloat(txObjPasos.getText().toString());
                    // Comprobamos que se han introducido datos validos
                    if(objetivo_pasos <=0 || objetivo_vasos <= 0){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.Objetivos_positivos), Toast.LENGTH_SHORT).show();
                    }else{
                        // Se actualizan los objetivos
                        usuario.update("objetivo_vasos", objetivo_vasos);
                        usuario.update("objetivo_pasos", objetivo_pasos);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Objetivos_actualizados), Toast.LENGTH_SHORT).show();
                    }
                } catch(Exception e) {
                    // Error al introducir los datos numericos
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.Introduzca_bien_los_datos), Toast.LENGTH_SHORT).show();
                }
            }
        });
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