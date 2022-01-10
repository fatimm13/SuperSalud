package com.app.supersalud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


public class HistorialListAdapter extends ArrayAdapter<Historial> {

    private Context mContext;
    int mResource;


    public HistorialListAdapter(@NonNull Context context, int resource, @NonNull List<Historial> objects) {
        super(context, resource, objects);
        mContext= context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String fecha = getItem(position).getFecha();
        int agua = getItem(position).getVasos();
        int pasos = getItem(position).getPasos();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView txFecha = convertView.findViewById(R.id.tx_fechaHist);
        TextView txVasos = convertView.findViewById(R.id.tx_aguaHist);
        TextView txPasos = convertView.findViewById(R.id.tx_pasosHist);

        txFecha.setText(fecha);
        txVasos.setText(mContext.getResources().getString(R.string.water) + ": " + agua);
        txPasos.setText(mContext.getResources().getString(R.string.pasos) + ": " + pasos);

        return convertView;
    }
}
