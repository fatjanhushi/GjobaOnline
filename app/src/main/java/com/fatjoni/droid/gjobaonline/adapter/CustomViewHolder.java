package com.fatjoni.droid.gjobaonline.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by me on 05-Feb-17.
 */

public class CustomViewHolder extends RecyclerView.ViewHolder {
    private final ViewDataBinding viewDataBinding;

    CustomViewHolder(View v) {
        super(v);
        viewDataBinding = DataBindingUtil.bind(v);
        v.setTag(viewDataBinding);
    }

    ViewDataBinding getBinding(){
        return viewDataBinding;
    }

}
