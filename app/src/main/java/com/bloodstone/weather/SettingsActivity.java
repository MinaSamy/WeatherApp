package com.bloodstone.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.bloodstone.weather.sync.WeatherSyncAdapter;

/**
 * Created by minsamy on 12/29/2015.
 */
public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        WeatherSyncAdapter.syncImmediately(this);
    }

    @Override
    protected void onStop() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }
}
