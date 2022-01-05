package com.app.supersalud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int progr_water = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        updateProgressBar();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String email = bundle.getString("email");

        TextView tx = (TextView) findViewById(R.id.textView2);
        tx.setText(email);


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