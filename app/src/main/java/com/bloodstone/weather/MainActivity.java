package com.bloodstone.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bloodstone.weather.fragment.DetailFragment;
import com.bloodstone.weather.fragment.ForecastFragment;
import com.bloodstone.weather.sync.WeatherSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        ForecastFragment.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private ForecastFragment mForecastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                //add the detail fragment once
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0.0f);
        }


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        mForecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.forecast_fragment);
        mForecastFragment.setCallbackListener(this);
        mForecastFragment.useTodayListItemLayout(!mTwoPane);

        //set the sync
        WeatherSyncAdapter.initializeSyncAdapter(this);

        //setup the google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_location))) {
            mForecastFragment.onLocationChanged();
        } else if (key.equals(getString(R.string.pref_measurement_unit))) {
            mForecastFragment.onMeasurementSettingChanged();
        }
    }

    @Override
    public void onItemSelected(Uri uri) {
        if (mTwoPane) {
            DetailFragment detailFragment = DetailFragment.newInstance(uri);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, detailFragment).commit();
        } else {
            Intent detailIntent = new Intent(this, DetailsActivity.class);
            detailIntent.setData(uri);
            startActivity(detailIntent);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //connect the google api client
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        //unregister the preferences listener
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        //disconnect the google api client
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //connect to the location services
        startLocationUpdates();
    }


    private void startLocationUpdates(){
        mLocationRequest=new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient
                ,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
