package com.fatjoni.droid.gjobaonline.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatjoni.droid.gjobaonline.BR;
import com.fatjoni.droid.gjobaonline.R;
import com.fatjoni.droid.gjobaonline.model.Gjobe;
import com.fatjoni.droid.gjobaonline.model.Vehicle;

import java.util.List;

/**
 * Created by me on 05-Feb-17.
 */

public class VehicleGjobeAdapter extends RecyclerView.Adapter<CustomViewHolder>{
    private final List<Gjobe> gjobeList;
    private final List<Vehicle> vehicleList;
    private final View.OnClickListener onClickListener;

    public VehicleGjobeAdapter(List<Gjobe> gjobeList, List<Vehicle> vehicleList, View.OnClickListener onClickListener) {
        this.gjobeList = gjobeList;
        this.vehicleList = vehicleList;
        this.onClickListener = onClickListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_vehicle, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.gjobe, gjobeList.get(position));
        holder.getBinding().setVariable(BR.vehicle, vehicleList.get(position));
        holder.getBinding().setVariable(BR.click_listener, onClickListener);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return (null != vehicleList ? vehicleList.size() : 0);
    }

}
