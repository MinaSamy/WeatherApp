package com.bloodstone.weather.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodstone.weather.R;
import com.bloodstone.weather.SettingsActivity;
import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.util.Utility;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private ShareActionProvider mShareActionProvider;

    private TextView mTodayTextView;
    private TextView mDateTextView;
    private TextView mHighTextView;
    private TextView mLowTextView;
    private TextView mDescriptionTextView;
    private TextView mHumidityTextView;
    private TextView mWindSpeedTextView;
    private TextView mPressureTextView;
    private ImageView weatherImage;

    private final static String KEY_URI = "uri";
    private Uri mUri;
    private final int DETAIL_LOADER_ID=0;

    static public DetailFragment newInstance(Uri forecastUri) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_URI, forecastUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments()!=null){

            mUri = getArguments().getParcelable(KEY_URI);
        }
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        mTodayTextView = (TextView) root.findViewById(R.id.today_textview);
        mDateTextView = (TextView) root.findViewById(R.id.date_textview);
        mHighTextView = (TextView) root.findViewById(R.id.high_textview);
        mLowTextView = (TextView) root.findViewById(R.id.low_textview);
        mDescriptionTextView = (TextView) root.findViewById(R.id.desc_textview);
        mHumidityTextView = (TextView) root.findViewById(R.id.humidity_textview);
        mWindSpeedTextView = (TextView) root.findViewById(R.id.wind_speed_textview);
        mPressureTextView = (TextView) root.findViewById(R.id.pressure_textview);
        weatherImage = (ImageView) root.findViewById(R.id.weather_image);


        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }




    private void setShareIntent(String forecast) {
        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, forecast);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            } else {
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(), mUri, WeatherContract.FORECAST_DETAILS_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Calendar forecastCalendar = Calendar.getInstance();
            forecastCalendar.setTimeInMillis(data.getLong(WeatherContract.COL_WEATHER_DATE));

            Calendar systemCalendar = Calendar.getInstance();
            systemCalendar.setTimeInMillis(System.currentTimeMillis());

            //set today tomorrow
            if (Utility.isForecastDateToday(forecastCalendar, systemCalendar)) {
                mTodayTextView.setText(R.string.today);

            } else if (Utility.isForecastDateTomorrow(forecastCalendar, systemCalendar)) {
                mTodayTextView.setText(R.string.tomorrow);

            } else {
                mTodayTextView.setText(Utility.getDayFromDate(forecastCalendar.getTimeInMillis()));
            }

            //set date day
            mDateTextView.setText(Utility.getDateMonthString(forecastCalendar));

            //set high/low temperatures
            double high = data.getDouble(WeatherContract.COL_WEATHER_MAX_TEMP);
            double low = data.getDouble(WeatherContract.COL_WEATHER_MIN_TEMP);
            boolean isMetric = Utility.isMetric(getActivity());

            mHighTextView.setText(Utility.formatTemperature(getActivity(), high, isMetric));
            mLowTextView.setText(Utility.formatTemperature(getActivity(), low, isMetric));


            //description
            String description = data.getString(WeatherContract.COL_WEATHER_DESC);
            mDescriptionTextView.setText(description);

            //humidity
            int humidity = data.getInt(WeatherContract.COL_WEATHER_HUMIDITY);
            mHumidityTextView.setText(getString(R.string.format_humidity, humidity));

            //wind speed
            //degrees
            float degrees = data.getFloat(WeatherContract.COL_WEATHER_DEGREES);
            float windSpeed = data.getFloat(WeatherContract.COL_WEATHER_WIND_SPEED);
            mWindSpeedTextView.setText(Utility.getFormattedWind(getActivity(), windSpeed, degrees));

            //pressure
            float pressure = data.getFloat(WeatherContract.COL_WEATHER_PRESSURE);
            mPressureTextView.setText(getString(R.string.format_pressure, pressure));


            //icon
            int weatherId = data.getInt(WeatherContract.COL_WEATHER_CONDITION_ID);
            int drawableId = Utility.getArtResourceForWeatherCondition(weatherId);
            weatherImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), drawableId));
            //set the share intent
            String highLow = Utility.formatHighLows(getActivity(), low, high);
            String date = Utility.getReadableDateString(getActivity(), data.getLong(1));
            String forecast = String.format("%s - %s - %s", date, description, highLow);
            if (null != forecast) {
                setShareIntent(forecast);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(0);
        super.onDestroyView();
    }

    public void onLocationChanged(String newLocation) {
        if(mUri!=null){
            long date=WeatherContract.WeatherEntry.getDateFromUri(mUri);
            mUri=WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation,date);
            getLoaderManager().restartLoader(DETAIL_LOADER_ID,null,this);
        }
    }
}
