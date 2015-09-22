package com.fatjoni.droid.gjobaonline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private Toolbar toolbar;
    private Fragment fragment;
    private static int lastClicked = R.id.nav_check_gjobe;
    private SharedPreferences sharedpreferences;
    public static final String LAST_CLICK_NAV_MENU_ITEM = "LAST_CLICKED_ITEM";
    public static final String DEVICE_ID = "605C9D109BD6BC5D68A431986B59BBD3";
    public static final String DEVICE_ID_GENYMOTION_NEZUS_6 = "26A429A4EEC6FB3CF42FB2F8F0D97363";
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAdView = (AdView) findViewById(R.id.adView);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(MainActivity.DEVICE_ID)
                        .addTestDevice(MainActivity.DEVICE_ID_GENYMOTION_NEZUS_6)
                        .build();
                mAdView.loadAd(adRequest);
            }
        }, 500);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onResume() {
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedpreferences != null) {
            lastClicked = sharedpreferences.getInt(LAST_CLICK_NAV_MENU_ITEM, lastClicked);
            openNeededFragment(lastClicked);
        }
        navigationView.setCheckedItem(lastClicked);
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveToPreference(lastClicked);
        super.onPause();
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
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int itemID = item.getItemId();
        if (lastClicked != itemID && (itemID == R.id.nav_check_gjobe || itemID == R.id.nav_my_vehicles || itemID == R.id.nav_about_app)) {
            openNeededFragment(itemID);

            lastClicked = itemID;
            saveToPreference(lastClicked);

        } else if (itemID == R.id.nav_share) {
            Intent mShareIntent = new Intent();
            mShareIntent.setAction(Intent.ACTION_SEND);
            mShareIntent.setType("text/plain");
            String intentStringToShare = "Kontrollo gjobat me kete APP: \n" +
                    " https://play.google.com/store/apps/details?id=" + getPackageName();
            mShareIntent.putExtra(Intent.EXTRA_TEXT, intentStringToShare);
            startActivity(mShareIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openNeededFragment(int itemID) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (itemID) {
            case R.id.nav_check_gjobe:
                fragment = new CheckGjobeFragment();
                transaction.replace(R.id.contentPanel, fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                toolbar.setTitle(R.string.item_drawer_kontrollo);
                transaction.commit();
                break;
            case R.id.nav_my_vehicles:
                fragment = new MyVehiclesFragment();
                transaction.replace(R.id.contentPanel, fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                toolbar.setTitle(R.string.item_drawer_automjetet);
                transaction.commit();
                break;
            case R.id.nav_about_app:
                fragment = new AboutAppFragment();
                transaction.replace(R.id.contentPanel, fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                toolbar.setTitle(R.string.item_drawer_about);
                transaction.commit();
                break;
        }
    }

    private void saveToPreference(int lastClicked) {
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(LAST_CLICK_NAV_MENU_ITEM, lastClicked);
        editor.apply();
    }

}
