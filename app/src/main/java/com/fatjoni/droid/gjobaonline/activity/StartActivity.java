package com.fatjoni.droid.gjobaonline.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fatjoni.droid.gjobaonline.R;
import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.databinding.ActivityStartBinding;
import com.fatjoni.droid.gjobaonline.service.MyService;
import com.fatjoni.droid.gjobaonline.utils.Utils;
import com.google.android.gms.ads.AdRequest;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, MyService.class));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_start);
        binding.toolbar.setTitle("Gjoba Online");
        setSupportActionBar(binding.toolbar);


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.toolbar.setTitle("Shto Automjet");
                binding.fab.setVisibility(View.GONE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_start, new CheckGjobeFragment())
                        .commit();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        GjobaDbHelper dbHelper = new GjobaDbHelper(getApplicationContext());
        if (dbHelper.areGjobaOnDB()) {
            binding.toolbar.setTitle("Automjetet e mia");
            binding.fab.setVisibility(View.VISIBLE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_start, new MyVehiclesFragment())
                    .commit();
        } else {
            binding.toolbar.setTitle("Kontrollo per Gjobe");
            binding.fab.setVisibility(View.GONE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_start, new CheckGjobeFragment())
                    .commit();
        }

        loadAd();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Pro-Albania-279436405773649/"));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_check_gjobe) {
            binding.toolbar.setTitle("Kontrollo per Gjobe");
            binding.fab.setVisibility(View.GONE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_start, new CheckGjobeFragment())
                    .commit();

        } else if (id == R.id.nav_my_vehicles) {
            binding.toolbar.setTitle("Automjetet e mia");
            binding.fab.setVisibility(View.VISIBLE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_start, new MyVehiclesFragment())
                    .commit();

        } else if (id == R.id.nav_about_app) {
            binding.toolbar.setTitle("Rreth app");
            binding.fab.setVisibility(View.GONE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_start, new AboutAppFragment())
                    .commit();

        } else if (id == R.id.nav_share) {
            Intent mShareIntent = new Intent();
            mShareIntent.setAction(Intent.ACTION_SEND);
            mShareIntent.setType("text/plain");
            String intentStringToShare = "Kontrollo gjobat me kete APP: \n" +
                    " https://play.google.com/store/apps/details?id=" + getPackageName();
            mShareIntent.putExtra(Intent.EXTRA_TEXT, intentStringToShare);
            startActivity(mShareIntent);
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadAd() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(Utils.DEVICE_ID_I9100)
                        .addTestDevice(Utils.DEVICE_ID_LGD850)
                        .build();
                binding.adView.loadAd(adRequest);
            }
        }, 500);
    }
}
