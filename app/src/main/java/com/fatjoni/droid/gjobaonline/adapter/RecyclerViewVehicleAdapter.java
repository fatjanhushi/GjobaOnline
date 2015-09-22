package com.fatjoni.droid.gjobaonline.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fatjoni.droid.gjobaonline.R;
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

    public RecyclerViewVehicleAdapter(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
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

        VehicleVeiwHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
