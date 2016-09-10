package com.fatjoni.droid.gjobaonline.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fatjoni.droid.gjobaonline.MainActivity;
import com.fatjoni.droid.gjobaonline.R;
import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.model.Gjobe;
import com.fatjoni.droid.gjobaonline.model.Vehicle;
import com.fatjoni.droid.gjobaonline.network.NetworkUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by me on 9/23/2015.
 */
public class MyService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies
        //Log.v("MyService", "Service Started");

        if (NetworkUtils.isNetworkAvailable(this)) {
            //Log.v("InternetCheck", "Kemi internet");

            NetworkTask task = new NetworkTask();
            task.execute(NetworkUtils.REQUEST_URL);

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

        Intent myIntent = new Intent(this, MainActivity.class);
        PendingIntent intent2 = PendingIntent.getActivity(this, 1,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent2);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());

    }

    private class NetworkTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String stringNrGjobave, stringVleraTotal, returnString = null;
            int nrGjobave = 0;
            Double vleraTotal = 0.0;

            GjobaDbHelper dbHelper = new GjobaDbHelper(getApplicationContext());
            ArrayList<Vehicle> vehicleList = dbHelper.getAllVehicles();

            if (vehicleList.size() > 0) {
                for (int i = 1; i <= vehicleList.size(); i++) {
                    Vehicle vehicle = dbHelper.getVehicle(i);

                    HashMap<String, String> parametrat = new HashMap<>();
                    parametrat.put("plate", vehicle.getPlate());
                    parametrat.put("vin", vehicle.getVin());

                    returnString = NetworkUtils.performPostCall(params[0], parametrat);

                    if (returnString != null) {
                        Document doc = Jsoup.parse(returnString);
                        Elements data = doc.select("td[style]");
                        if (data.size() > 0) {
                            Log.d("data object returned", data.toString());
                            Element elementNrGjobave = data.get(0);
                            stringNrGjobave = elementNrGjobave.text();
                            int thisVehicleNrGjobave = Integer.parseInt(stringNrGjobave);
                            nrGjobave = nrGjobave + thisVehicleNrGjobave;
                            Log.v("Gjoba makina" + i, "" + thisVehicleNrGjobave);

                            Element elementVleraTotal = data.get(1);
                            stringVleraTotal = elementVleraTotal.text().replace("LEK", "").replace(",","");
                            Double thisVehicleVleraTotal = Double.parseDouble(stringVleraTotal);
                            vleraTotal = vleraTotal + thisVehicleVleraTotal;
                            Log.v("Vlera makina" + i, "" + thisVehicleVleraTotal);

                            Gjobe gjobe = new Gjobe(i, thisVehicleNrGjobave, thisVehicleVleraTotal);
                            dbHelper.updateGjobe(gjobe);

                        } else {
                            Log.d("Empty Response!", "Empty string from network request.");
                        }

                    }

                }

                if (nrGjobave > 0)
                    showGjobeNotification(nrGjobave + " Gjoba te papaguara", "Ju keni " + vleraTotal +
                            "LEK gjoba te papaguara");
            }

            return returnString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }


    }
}
