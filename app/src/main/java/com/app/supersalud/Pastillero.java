package com.app.supersalud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Pastillero extends AppCompatActivity {

    private CollectionReference pastillas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pastillas = PastillasSingleton.getInstance().pastillas;
        setContentView(R.layout.activity_pastillero);

    }

    private void creaCargaPastillas() {

    }

    public void goNewPill (View view){
        Intent intent = new Intent(this, AddPastilla.class);
        startActivity(intent);
    }
}