package com.bloodstone.weather.fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.bloodstone.weather.R;

/**
 * Created by minsamy on 12/29/2015.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        EditTextPreference locationPreference=(EditTextPreference)findPreference(getString(R.string.pref_location));
        locationPreference.setOnPreferenceChangeListener(this);
        bindPreferenceValueToSummary(locationPreference,locationPreference.getText());

        ListPreference measurementPreference=(ListPreference)findPreference(getString(R.string.pref_measurement_unit));
        bindPreferenceValueToSummary(measurementPreference,measurementPreference.getValue());
        measurementPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        bindPreferenceValueToSummary(preference,newValue);
        return true;
    }

    private void bindPreferenceValueToSummary(Preference preference,Object newValue){
        String value=newValue.toString();

        if(preference instanceof ListPreference){
            ListPreference pref=(ListPreference)preference;
            int prefIndex=pref.findIndexOfValue(value);
            if(prefIndex>-1){
                pref.setSummary(pref.getEntries()[prefIndex]);
            }
        }else if(preference instanceof EditTextPreference){
            preference.setSummary(value);
        }
    }
}
