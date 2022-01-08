package com.app.supersalud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Pastillero extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pastillero);
    }

    public void goNewPill (View view){
        Intent intent = new Intent(this, AddPastilla.class);
        startActivity(intent);
    }
}