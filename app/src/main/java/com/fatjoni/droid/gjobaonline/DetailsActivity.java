package com.fatjoni.droid.gjobaonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.model.Gjobe;
import com.fatjoni.droid.gjobaonline.model.Vehicle;
import com.fatjoni.droid.gjobaonline.network.NetworkUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {
    public static final String MESSAGE_START_DETAILS_ACTIVITY = "START_ACTIVITY";
    @Bind(R.id.vehicle_details_container)
    ViewGroup vehicleDetailsContainer;
    @Bind(R.id.response_container)
    View responseContainer;
    @Bind(R.id.response)
    TextView mTextView;
    ProgressDialog pd;
    private String targa, vin;
    private Intent mShareIntent;
    String intentStringToShare;
    private NativeExpressAdView adView;
    private InterstitialAd mInterstitialAd;
    private GjobaDbHelper dbHelper;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        i = intent.getIntExtra(MESSAGE_START_DETAILS_ACTIVITY, 1);
        //Log.d("Numri: ", ""+i);

        dbHelper = new GjobaDbHelper(DetailsActivity.this);

        Vehicle vehicle = dbHelper.getVehicle(i);
        targa = vehicle.getPlate();
        vin = vehicle.getVin();
        getSupportActionBar().setTitle(targa);
        //Log.d("Vehicle", str);

        NetworkTask task = new NetworkTask();
        task.execute(NetworkUtils.REQUEST_URL);

        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");

        loadAds();

    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            super.onBackPressed();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(MainActivity.DEVICE_ID_I9100)
                .addTestDevice(MainActivity.DEVICE_ID_LGD850)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_details, menu);

        // Find the MenuItem that we know has the ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Get its ShareActionProvider
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Connect the dots: give the ShareActionProvider its Share Intent
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(mShareIntent);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fatjoni.com"));
            startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DetailsActivity.this);
            pd.setMessage("Ju lutem prisni...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> parametrat = new HashMap<>();
            parametrat.put("plate", targa);
            parametrat.put("vin", vin);

            return NetworkUtils.performPostCall(params[0], parametrat);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Document doc = Jsoup.parse(s);
                Elements data = doc.select("td[style]");
                if (data.size() > 0) {
                    //Log.d("data object returned", data.toString());
                    Element elementNrGjobave = data.get(0);
                    String stringNrGjobave = elementNrGjobave.text();
                    int nrGjobave = Integer.parseInt(stringNrGjobave);

                    Element elementVleraTotal = data.get(1);
                    String stringVleraTotal = elementVleraTotal.text().replace(",","");
                    Double vleraTotal = Double.parseDouble(stringVleraTotal.replace("LEK", ""));
                    //Log.d("Vlera Total",""+vleraTotal);
                    intentStringToShare = "Une kisha " + nrGjobave + " gjoba me vlere " + vleraTotal +
                            " LEK pa paguar. Kontrollo edhe ti ketu: \n" +
                            " https://st732.app.goo.gl/Q8OH";
                    mShareIntent.putExtra(Intent.EXTRA_TEXT, intentStringToShare);

                    Element elementShkeljet = data.get(2);
                    String stringShkeljet = elementShkeljet.text();

                    Element elementPershkrimet = data.get(3);
                    String stringPershkrimet = elementPershkrimet.text();

                    s = "Nr.Gjobave: " + stringNrGjobave + "\n" +
                            "Vlera Total: " + stringVleraTotal + "\n" +
                            "Shkeljet: " + stringShkeljet + "\n" +
                            "Pershkrimet: " + stringPershkrimet;

                    mTextView.setText(s);
                    responseContainer.setVisibility(View.VISIBLE);

                    //dbHelper.createGjobe(i, nrGjobave, vleraTotal);
                    Gjobe gjobe = new Gjobe(i, nrGjobave, vleraTotal);
                    dbHelper.updateGjobe(gjobe);

                } else {
                    Snackbar.make(vehicleDetailsContainer, "Nuk u gjet automjet me kete targe dhe nr. shasie.", Snackbar.LENGTH_LONG).show();
                }
            }
            pd.dismiss();
        }

    }

    private void loadAds(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
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
                        .addTestDevice(MainActivity.DEVICE_ID_I9100)
                        .addTestDevice(MainActivity.DEVICE_ID_LGD850)
                        .build();
                adView.loadAd(request);
            }
        }, 300);
    }

}
