package com.fatjoni.droid.gjobaonline.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fatjoni.droid.gjobaonline.R;
import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.databinding.ActivityInfoBinding;
import com.fatjoni.droid.gjobaonline.model.Vehicle;
import com.fatjoni.droid.gjobaonline.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class InfoActivity extends AppCompatActivity {

    private ActivityInfoBinding binding;
    private Vehicle vehicle;
    ArrayList<String> responseValues;
    private NativeExpressAdView adView;
    private InterstitialAd mInterstitialAd;
    private GjobaDbHelper dbHelper;
    private int vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_info);
        binding.toolbar.setTitle("Gjoba Online");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        vehicleId = Integer.parseInt(getIntent().getStringExtra("vehicle_id"));
        dbHelper = new GjobaDbHelper(getApplicationContext());
        vehicle = dbHelper.getVehicle(vehicleId);

        try {

            run();


        } catch (Exception e) {
            e.printStackTrace();
        }


        loadAds();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_delete) {

            dbHelper.deleteFromDB(vehicleId);
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void run() throws Exception {
        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("plate", vehicle.getPlate())
                .addFormDataPart("vin", vehicle.getVin())
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

                        // Run view-related code back on the main thread
                        InfoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils myUtils = new Utils();
                                responseValues = myUtils.parseHtml(responseData);
                                //Log.d("Gjoba eshte:", responseValues.get(1));

                                String pergjigja = "Nr.Gjobave: " + responseValues.get(0) + "\n" +
                                        "Vlera Total: " + responseValues.get(1) + "\n" +
                                        "Shkeljet: " + responseValues.get(2) + "\n" +
                                        "Pershkrimet: " + responseValues.get(3);

                                binding.resultText.setText(pergjigja);

                                int isNewVehicle = myUtils.saveToDatabase(getApplicationContext(), vehicle.getVin(), vehicle.getPlate(), responseValues.get(0), responseValues.get(1));

                                if (isNewVehicle != 1) {
                                    Snackbar.make(binding.getRoot(), "Gjobat u perditesuan!", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(Utils.DEVICE_ID_I9100)
                .addTestDevice(Utils.DEVICE_ID_LGD850)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void loadAds(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                NavUtils.navigateUpFromSameTask(InfoActivity.this);
            }
        });

        int max = 4, min = 1, randNumber = min + (int) (Math.random() * ((max - min) + 1));

        if (randNumber == 1) {
            requestNewInterstitial();
        }

        adView = (NativeExpressAdView) findViewById(R.id.nativeAdView);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AdRequest request = new AdRequest.Builder()
                        .addTestDevice(Utils.DEVICE_ID_I9100)
                        .addTestDevice(Utils.DEVICE_ID_LGD850)
                        .build();
                adView.loadAd(request);
            }
        }, 300);
    }

}
