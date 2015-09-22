package com.fatjoni.droid.gjobaonline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fatjoni.droid.gjobaonline.adapter.RecyclerItemClickListener;
import com.fatjoni.droid.gjobaonline.adapter.RecyclerViewVehicleAdapter;
import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.model.Vehicle;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by me on 9/18/2015.
 */
public class MyVehiclesFragment extends Fragment {

    @Bind(R.id.recycler_view_my_vehicles)
    RecyclerView recyclerView;
    @Bind(R.id.tv_if_no_vehicles)
    TextView textViewIfNoVehicles;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_vehicles, container, false);
        ButterKnife.bind(this, rootView);

        GjobaDbHelper dbHelper = new GjobaDbHelper(getContext());
        ArrayList<Vehicle> vehicles = dbHelper.getAllVehicles();

        if (vehicles != null && vehicles.size() > 0) {
            textViewIfNoVehicles.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(llm);
            RecyclerViewVehicleAdapter adapter = new RecyclerViewVehicleAdapter(vehicles);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Log.d("Klick item ", "" + position);
                            Intent intent = new Intent(getContext(), DetailsActivity.class);
                            intent.putExtra(DetailsActivity.MESSAGE_START_DETAILS_ACTIVITY, position + 1);
                            startActivity(intent);
                        }
                    })
            );
        } else {
            recyclerView.setVisibility(View.GONE);
        }

        return rootView;
    }
}

