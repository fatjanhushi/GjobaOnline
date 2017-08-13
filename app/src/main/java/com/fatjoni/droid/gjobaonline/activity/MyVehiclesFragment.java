package com.fatjoni.droid.gjobaonline.activity;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatjoni.droid.gjobaonline.adapter.VehicleGjobeAdapter;
import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.databinding.FragmentMyVehiclesBinding;
import com.fatjoni.droid.gjobaonline.model.Gjobe;
import com.fatjoni.droid.gjobaonline.model.Vehicle;

import java.util.List;


/**
 * Created by me on 9/18/2015.
 */
public class MyVehiclesFragment extends Fragment {
    FragmentMyVehiclesBinding binding;

    private final View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("Klikimi", "Klikimi eshte ne rregull");
            final Vehicle vehicle = (Vehicle) v.getTag();
            Intent intent = new Intent(getContext(), InfoActivity.class);
            intent.putExtra("vehicle_id", String.valueOf(vehicle.getId()));
            startActivity(intent);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMyVehiclesBinding binding = FragmentMyVehiclesBinding.inflate(inflater, container, false);

        GjobaDbHelper dbHelper = new GjobaDbHelper(getContext());

        List<Gjobe> gjobeList = dbHelper.getAllGjobat();
        List<Vehicle> vehicleList = dbHelper.getAllVehicles();

        if (gjobeList.size()>1){
            binding.tvValueTotal.setVisibility(View.VISIBLE);
            String vleraTotalGjoba = String.valueOf(Math.round(dbHelper.getTotalGjobeValue()));
            binding.tvValueTotal.setText("Vlera totale e gjobave = " + vleraTotalGjoba + " Leke");
        }else if (gjobeList.size()==1){

            binding.tvIfNoVehicles.setVisibility(View.GONE);
        }else {
            binding.tvIfNoVehicles.setVisibility(View.VISIBLE);
            return binding.getRoot();
        }

        binding.recyclerViewMyVehicles.setHasFixedSize(true);
        binding.recyclerViewMyVehicles.setAdapter(new VehicleGjobeAdapter(gjobeList, vehicleList, listener));
        binding.recyclerViewMyVehicles.getAdapter().notifyDataSetChanged();


        return binding.getRoot();
    }


    @BindingAdapter("specialTag")
    public static void setSpecialTag(View view, Object value) {
        view.setTag(value);
    }

}

