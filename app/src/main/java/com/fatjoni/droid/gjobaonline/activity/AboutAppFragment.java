package com.fatjoni.droid.gjobaonline.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatjoni.droid.gjobaonline.databinding.FragmentAboutAppBinding;

/**
 * Created by me on 9/18/2015.
 */
public class AboutAppFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAboutAppBinding binding = FragmentAboutAppBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
