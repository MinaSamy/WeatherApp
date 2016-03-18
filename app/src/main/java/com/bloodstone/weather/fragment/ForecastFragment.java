package com.bloodstone.weather.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bloodstone.weather.DetailActivity;
import com.bloodstone.weather.FetchWeatherTask;
import com.bloodstone.weather.ForecastAdapter;
import com.bloodstone.weather.R;
import com.bloodstone.weather.SettingsActivity;
import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.util.Utility;

import java.util.ArrayList;
import java.util.Arrays;


public class ForecastFragment extends Fragment {


    private ForecastAdapter mForecastAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cursor = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        mForecastAdapter = new ForecastAdapter(getActivity(),cursor);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView list = (ListView) rootView.findViewById(R.id.listview_forecast);
        list.setAdapter(mForecastAdapter);

        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, mForecastAdapter.getItem(position));
                startActivity(detailIntent);
            }
        });*/


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            updateWeather();
            return true;
        }else if(item.getItemId()==R.id.action_settings){
            Intent settingsIntent=new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }else if(item.getItemId()==R.id.action_preferred_location){
            String postalCode= Utility.getPreferredLocation(getActivity());
            Intent locationIntent=Utility.makeLocationIntent(getActivity(),postalCode);
            if(locationIntent!=null){
                startActivity(locationIntent);
            }else{
             Snackbar.make(this.getView(),R.string.prompt_install_map_apps, Snackbar.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location=preferences.getString(getString(R.string.pref_location)
                ,getString(R.string.pref_location_default_value));
        FetchWeatherTask task = new FetchWeatherTask(getActivity());
        task.execute(location);
    }


}
