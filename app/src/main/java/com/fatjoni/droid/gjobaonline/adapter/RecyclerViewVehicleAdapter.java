package com.fatjoni.droid.gjobaonline.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fatjoni.droid.gjobaonline.R;
import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.model.Gjobe;
import com.fatjoni.droid.gjobaonline.model.Vehicle;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by me on 9/19/2015.
 */
public class RecyclerViewVehicleAdapter extends
        RecyclerView.Adapter<RecyclerViewVehicleAdapter.VehicleVeiwHolder>{
    List<Vehicle> vehicles;
    Context context;

    public RecyclerViewVehicleAdapter(List<Vehicle> vehicles, Context context) {
        this.vehicles = vehicles;
        this.context = context;
    }

    @Override
    public VehicleVeiwHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_vehicle, viewGroup, false);
        return new VehicleVeiwHolder(v);
    }

    @Override
    public void onBindViewHolder(VehicleVeiwHolder vvh, int i) {
        vvh.txt_plate.setText(vehicles.get(i).getPlate());
        vvh.txt_vin.setText(vehicles.get(i).getVin());

        GjobaDbHelper dbHelper = new GjobaDbHelper(context);
        Gjobe gjobe = dbHelper.getGjobe(i+1);
        if (gjobe != null){
            if (gjobe.getVehicleId() == i+1){
                int nr = gjobe.getNrTotalPerAutomjet();
                Double vl = gjobe.getVleraTotalPerAutomjet();
                vvh.txt_gjobat.setText(nr + " Gjoba" + " = " + vl + " LEKE");
            }else {
                Log.d("Gabim", "Gjoba per automjetin" + i + 1 + " nuk eshte e sakte");
            }
        }else {
            Log.d("Gabim", "Nuk u gjet gjobe per automjetin" + i + 1);
        }



    }

    @Override
    public int getItemCount() {
        if (vehicles != null)
            return vehicles.size();
        else
            return 0;
    }

    public static class VehicleVeiwHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.card_view_item)
        CardView cv;
        @Bind(R.id.tv_plate_display)
        TextView txt_plate;
        @Bind(R.id.tv_vin_display)
        TextView txt_vin;
        @Bind(R.id.tv_gjobat)
        TextView txt_gjobat;


        VehicleVeiwHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
