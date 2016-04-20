package com.bloodstone.weather.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bloodstone.weather.FetchWeatherTask;
import com.bloodstone.weather.ForecastAdapter;
import com.bloodstone.weather.R;
import com.bloodstone.weather.SettingsActivity;
import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.service.WeatherService;
import com.bloodstone.weather.util.Utility;


public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int LOADER_ID=100;
    private ForecastAdapter mForecastAdapter;
    private Callback mCallbackListener;
    private int mListSelectedItemIndex=ListView.INVALID_POSITION;
    private final String KEY_SELECTED_INDEX="selected_index";
    private ListView mList;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mForecastAdapter = new ForecastAdapter(getActivity(),null);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mList = (ListView) rootView.findViewById(R.id.listview_forecast);
        mList.setAdapter(mForecastAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mListSelectedItemIndex=position;
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String locationSetting = Utility.getPreferredLocation(getActivity());

                Uri itemUri=WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting,
                        cursor.getLong(WeatherContract.COL_WEATHER_DATE));
                if(mCallbackListener!=null){
                    mCallbackListener.onItemSelected(itemUri);
                }
            }
        });


        if(savedInstanceState!=null &&savedInstanceState.containsKey(KEY_SELECTED_INDEX)){
            mListSelectedItemIndex=savedInstanceState.getInt(KEY_SELECTED_INDEX);
        }
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
            getLoaderManager().restartLoader(LOADER_ID,null,this);
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



    private void updateWeather() {
        String locationSetting= Utility.getPreferredLocation(getActivity());
        Intent weatherServiceIntent= WeatherService.makeServiceIntent(getActivity(),locationSetting);
        getActivity().startService(weatherServiceIntent);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mListSelectedItemIndex!=ListView.INVALID_POSITION){
            outState.putInt(KEY_SELECTED_INDEX,mListSelectedItemIndex);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder= WeatherContract.WeatherEntry.COLUMN_DATE+" ASC";
        String locationSetting=Utility.getPreferredLocation(getActivity());
        Uri queryUri= WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting,System.currentTimeMillis());
        return new CursorLoader(getActivity(), queryUri, WeatherContract.FORECAST_COLUMNS,null,null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if(mListSelectedItemIndex!=ListView.INVALID_POSITION ){
           mList.smoothScrollToPosition(mListSelectedItemIndex);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }


    public void setCallbackListener(Callback listener){
        mCallbackListener=listener;
    }

    static public interface Callback{
        void onItemSelected(Uri uri);
    }

    public void onLocationChanged(){
        updateWeather();
        getLoaderManager().restartLoader(LOADER_ID,null,this);
    }

    public void onMeasurementSettingChanged(){
        mForecastAdapter.notifyDataSetInvalidated();
    }

    public void useTodayListItemLayout(boolean userTodayItemLayout){
        mForecastAdapter.setUseTodayLayout(userTodayItemLayout);

    }
}
