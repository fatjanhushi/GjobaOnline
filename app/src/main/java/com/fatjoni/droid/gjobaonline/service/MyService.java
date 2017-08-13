package com.fatjoni.droid.gjobaonline.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fatjoni.droid.gjobaonline.R;
import com.fatjoni.droid.gjobaonline.activity.StartActivity;
import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.model.Vehicle;
import com.fatjoni.droid.gjobaonline.network.NetworkUtils;
import com.fatjoni.droid.gjobaonline.utils.Utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by me on 9/23/2015.
 */
public class MyService extends Service {
    ArrayList<String> responseValues;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies
        //Log.v("MyService", "Service Started");
        Log.d("Service started:", "servisi u startua");

        if (NetworkUtils.isNetworkAvailable(this)) {
            //Log.v("InternetCheck", "Kemi internet");

            GjobaDbHelper dbHelper = new GjobaDbHelper(getApplicationContext());
            List<Vehicle> vehicles = dbHelper.getAllVehicles();

            Log.d("Kemi keto makina:", String.valueOf(vehicles.size()));
            if (vehicles.size()>0){
                for(Vehicle vehicle : vehicles){
                    try {
                        run(vehicle.getPlate(), vehicle.getVin());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Double vleraGjobashDouble = dbHelper.getTotalGjobeValue();
                String vleraGjobash = String.valueOf(vleraGjobashDouble);
                if (vleraGjobashDouble > 0.0){
                    showGjobeNotification("Ju keni gjoba te papaguara","Vlera totale = " + vleraGjobash);
                }

            }

            // I don't want this service to stay in memory, so I stop it
            // immediately after doing what I wanted it to do.
            stopSelf();

            return START_NOT_STICKY;
        }

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (36000000), //cdo 10 ore
                PendingIntent.getService(this, 0, new Intent(this, MyService.class), 0)
        );
    }

    private void showGjobeNotification(String title, String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_car)
                .setAutoCancel(true);

        Intent myIntent = new Intent(this, StartActivity.class);
        PendingIntent intent2 = PendingIntent.getActivity(this, 1,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent2);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());

    }


    public void run(final String plate, final String vin) throws Exception {
        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("plate", plate)
                .addFormDataPart("vin", vin)
                .build();

        Request request = new Request.Builder()
                .url(Utils.REQUEST_URL)
                .post(requestBody)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        // Read data on the worker thread
                        final String responseData = response.body().string();

                        Log.d("Response from service:", responseData);

                        Utils myUtils = new Utils();
                        responseValues = myUtils.parseHtml(responseData);

                        if (responseValues != null){
                            myUtils.saveToDatabase(getApplicationContext(), vin, plate, responseValues.get(0), responseValues.get(1));
                        }

                    }
                });
    }
}
