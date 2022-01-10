package com.app.supersalud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public class PastillaListAdapter extends ArrayAdapter<Pastilla> {

    private Context mContext;
    int mResource;
    CollectionReference medicacion;
    ArrayList<Pastilla> listaPastillas;

    public PastillaListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Pastilla> listaPastillas) {
        super(context, resource, listaPastillas);
        mContext= context;
        mResource = resource;
        this.listaPastillas = listaPastillas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String nombre = getItem(position).getNombre();
        Date fecha_inicio = getItem(position).getFecha_inicio();
        Date fecha_fin = getItem(position).getFecha_fin();
        int veces = getItem(position).getVeces_dia();
        List<String> rep = getItem(position).getRepeticiones();
        String id = getItem(position).getId();
        medicacion = (CollectionReference)SingletonMap.getInstance().get(Pastillero.MEDICACION);

        //Create the person object with the information
        //Pastilla pill = new Pastilla(nombre,veces,fecha_inicio,rep);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView txNombre = convertView.findViewById(R.id.tx_nombreMedi);
        TextView txVeces = convertView.findViewById(R.id.tx_vecesMedi);
        TextView txFechaIni = convertView.findViewById(R.id.tx_fechaMediIni);
        TextView txFechaFin = convertView.findViewById(R.id.tx_fechaMediFin);
        TextView txDias = convertView.findViewById(R.id.tx_diasMedi);
        Button button = convertView.findViewById(R.id.button_deletePill);

        StringJoiner agrupar= new StringJoiner(", ");
        for (String s:rep) {
            agrupar.add(s);
        }

        SimpleDateFormat objSDF = new SimpleDateFormat("dd/MM/yyyy");

        txNombre.setText(nombre);
        txVeces.setText(mContext.getResources().getString(R.string.Dosis_diarias) + " " + veces);
        if(fecha_inicio!=null){
            txFechaIni.setText(mContext.getResources().getString(R.string.Inicio_Tratamiento) + " " + objSDF.format(fecha_inicio));
        }else{
            txFechaIni.setText(mContext.getResources().getString(R.string.Inicio_Tratamiento) + " " +mContext.getResources().getString(R.string.Indefinido));
        }
        if(fecha_fin!=null){
            txFechaFin.setText(mContext.getResources().getString(R.string.Fin_Tratamiento) + " " +objSDF.format(fecha_fin));
        }else{
            txFechaFin.setText(mContext.getResources().getString(R.string.Inicio_Tratamiento) + " " + mContext.getResources().getString(R.string.Indefinido));
        }

        if(rep!=null && !rep.isEmpty()){
            txDias.setText(agrupar.toString());
        }else{
            txDias.setText(mContext.getResources().getString(R.string.Todos_los_dias));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medicacion.document(id).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    listaPastillas.remove(listaPastillas.get(position));
                                    notifyDataSetChanged();
                                    Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT);
                                }else{
                                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT);
                                }
                            }
                        });
            }
        });

        return convertView;
    }
}
