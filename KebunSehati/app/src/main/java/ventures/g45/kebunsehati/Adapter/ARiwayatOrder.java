package ventures.g45.kebunsehati.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ventures.g45.kebunsehati.DetailOrder;
import ventures.g45.kebunsehati.R;
import ventures.g45.kebunsehati.RiwayatOrder;
import ventures.g45.kebunsehati.model.MRiwayatOrder;

public class ARiwayatOrder extends RecyclerView.Adapter<ARiwayatOrder.ViewHolder>{
        private ArrayList<MRiwayatOrder> riwayatOrder;
        private Context context;
        RiwayatOrder listener;
        DecimalFormat decimalFormat;

    public ARiwayatOrder (ArrayList<MRiwayatOrder> riwayat, Context ctx){

        riwayatOrder = riwayat;
        context = ctx;
        listener = (RiwayatOrder) ctx;
        decimalFormat = new DecimalFormat("#,###.##");
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView idOrder, waktuOrder, totalOrder, status;
        CardView cardView;
        ImageView detail;

        ViewHolder(View v) {
            super(v);
            idOrder = (TextView) v.findViewById(R.id.idOrder);
            waktuOrder = (TextView) v.findViewById(R.id.waktuOrder);
            totalOrder = (TextView) v.findViewById(R.id.totalOrder);
            status = (TextView) v.findViewById(R.id.status);
            cardView = (CardView) v.findViewById(R.id.cvRiwayatOrder);

        }

    }

    @NonNull
    @Override
    public ARiwayatOrder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);

        ARiwayatOrder.ViewHolder vh = new ARiwayatOrder.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ARiwayatOrder.ViewHolder holder, final int position) {
        final String idOrder = riwayatOrder.get(position).getIdOrder();
        final String waktuOrder = riwayatOrder.get(position).getWaktuOrder();
        final Integer totalOrder = riwayatOrder.get(position).getTotalOrder();
        final String status = riwayatOrder.get(position).getStatus();

        //System.out.println(position + riwayatOrder.size());
        /*holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RiwayatOrder.class);
                intent.putExtra("id_order", riwayatOrder.get(position).getIdOrder());

                //view.getContext().startActivity(intent);
                Toast.makeText(holder.cardView.getContext(), "ID : " + riwayatOrder.get(position).getIdOrder(), Toast.LENGTH_LONG).show();
            }
        });*/
        holder.idOrder.setText(idOrder);
        holder.waktuOrder.setText(waktuOrder);
        holder.totalOrder.setText("Total : IDR " + decimalFormat.format(totalOrder));
        holder.status.setText(status);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailOrder.class);
                intent.putExtra("id_order", riwayatOrder.get(position).getIdOrder());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return riwayatOrder.size();
    }
}
