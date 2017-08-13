package com.fatjoni.droid.gjobaonline.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fatjoni.droid.gjobaonline.network.NetworkUtils;
import com.fatjoni.droid.gjobaonline.service.MyService;

/**
 * Created by me on 9/23/2015.
 */
public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MyService.class));
    }
}
