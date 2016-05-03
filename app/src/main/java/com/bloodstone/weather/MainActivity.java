package com.bloodstone.weather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bloodstone.weather.fragment.DetailFragment;
import com.bloodstone.weather.fragment.ForecastFragment;
import com.bloodstone.weather.sync.WeatherSyncAdapter;
import com.bloodstone.weather.util.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        ForecastFragment.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private ForecastFragment mForecastFragment;

    private final int PERMISSION_REQUEST_LOCATION = 100;

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


    private void startLocationUpdates() {
        Log.d(TAG, "Started Location Updates");
        //check if location permissions are granted
        boolean permissionGranted = Utility.checkLocationPermission(this);
        if (permissionGranted) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient
                    , mLocationRequest, this);
        } else {
            //check if we need to show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder=new AlertDialog.Builder(this)
                        .setMessage(R.string.location_permission_request)
                        .setNegativeButton(android.R.string.cancel,null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestLocationPermission();
                            }
                        });
                builder.create().show();
            } else {
                //ask for the permission
                requestLocationPermission();
            }
        }

    }


    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_LOCATION);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed with result " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location", location.toString());
    }
}
