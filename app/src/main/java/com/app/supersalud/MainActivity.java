package com.app.supersalud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private int progr_water = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateProgressBar();

    }

    public void incrementProgr (View v){
        if(progr_water<= 90) {
            progr_water+=10;
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