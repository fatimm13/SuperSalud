package com.app.supersalud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class AddPastilla extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pastilla);
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
}