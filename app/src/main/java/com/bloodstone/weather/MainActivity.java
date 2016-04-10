package com.bloodstone.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bloodstone.weather.fragment.DetailFragment;
import com.bloodstone.weather.fragment.ForecastFragment;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        ForecastFragment.Callback {

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
        }


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        mForecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.forecast_fragment);
        mForecastFragment.setCallbackListener(this);
        mForecastFragment.useTodayListItemLayout(!mTwoPane);
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
}
