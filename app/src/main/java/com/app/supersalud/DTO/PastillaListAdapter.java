package com.app.supersalud.DTO;

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

import com.app.supersalud.Pastillero;
import com.app.supersalud.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/** Clase para manejar como se muestra el listado de Pastillas **/
public class PastillaListAdapter extends ArrayAdapter<Pastilla> {

    private Context mContext;
    int mResource;
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

        CollectionReference medicacion = (CollectionReference) SingletonMap.getInstance().get(Pastillero.MEDICACION);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView txNombre = convertView.findViewById(R.id.tx_nombreMedi);
        TextView txVeces = convertView.findViewById(R.id.tx_vecesMedi);
        TextView txFechaIni = convertView.findViewById(R.id.tx_fechaMediIni);
        TextView txFechaFin = convertView.findViewById(R.id.tx_fechaMediFin);
        TextView txDias = convertView.findViewById(R.id.tx_diasMedi);
        Button button = convertView.findViewById(R.id.button_deletePill);

        // Para encadenar los dias de la semana que se repite
        StringJoiner agrupar= new StringJoiner(", ");
        for (String s:rep) {
            agrupar.add(s);
        }

        // Para dar formato a la fecha de salida
        SimpleDateFormat objSDF = new SimpleDateFormat("dd/MM/yyyy");

        // Establece los datos
        txNombre.setText(nombre);
        txVeces.setText(mContext.getResources().getString(R.string.Dosis_diarias) + " " + veces);

        // Si las fechas son null, se indican como indefinidas
        if(fecha_inicio!=null){
            txFechaIni.setText(mContext.getResources().getString(R.string.Inicio_Tratamiento) + " " + objSDF.format(fecha_inicio));
        }else{
            txFechaIni.setText(mContext.getResources().getString(R.string.Inicio_Tratamiento) + " " +mContext.getResources().getString(R.string.Indefinido));
        }

        if(fecha_fin!=null){
            txFechaFin.setText(mContext.getResources().getString(R.string.Fin_Tratamiento) + " " +objSDF.format(fecha_fin));
        }else{
            txFechaFin.setText(mContext.getResources().getString(R.string.Fin_Tratamiento) + " " + mContext.getResources().getString(R.string.Indefinido));
        }

        // Si la repeticion no se ha indicado, se repite todos los dias
        if(rep!=null && !rep.isEmpty()){
            txDias.setText(agrupar.toString());
        }else{
            txDias.setText(mContext.getResources().getString(R.string.Todos_los_dias));
        }

        // Se da funcionalidad al boton para borrar dicho medicamento
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
                                    //TODO String
                                    Toast.makeText(mContext, "Borrado exitoso", Toast.LENGTH_SHORT);
                                }else{
                                    //TODO String
                                    Toast.makeText(mContext, "Fallo al borrar", Toast.LENGTH_SHORT);
                                }
                            }
                        });
            }
        });

        return convertView;
    }
}
