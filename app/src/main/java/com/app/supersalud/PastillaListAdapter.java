package com.app.supersalud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public class PastillaListAdapter extends ArrayAdapter<Pastilla> {

    private Context mContext;
    int mResource;

    public PastillaListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Pastilla> objects) {
        super(context, resource, objects);
        mContext= context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String nombre = getItem(position).getNombre();
        Date fecha_inicio = getItem(position).getFecha_inicio();
        int veces = getItem(position).getVeces_dia();
        List<String> rep = getItem(position).getRepeticiones();

        //Create the person object with the information
        Pastilla pill = new Pastilla(nombre,veces,fecha_inicio,rep);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView txNombre = convertView.findViewById(R.id.tx_nombreMedi);
        TextView txVeces = convertView.findViewById(R.id.tx_vecesMedi);
        TextView txFecha = convertView.findViewById(R.id.tx_fechaMedi);
        TextView txDias = convertView.findViewById(R.id.tx_diasMedi);

        StringJoiner agrupar= new StringJoiner(",");
        for (String s:rep) {
            agrupar.add(s);
        }

        txNombre.setText(nombre);
        txVeces.setText(veces+ "");
        if(fecha_inicio!=null){
            txFecha.setText(fecha_inicio.toString());
        }else{
            txFecha.setText("Indefinido");
        }

        txDias.setText(agrupar.toString());

        return convertView;
    }
}
