package com.bloodstone.weather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.util.Utility;

/**
 * Created by minasamy on 3/18/2016.
 */
public class ForecastAdapter extends CursorAdapter {
    private final boolean mIsMetric;

    public ForecastAdapter(Context context, Cursor c) {
        super(context, c, false);
        mIsMetric = Utility.isMetric(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //weather id, used in showing the weather icon
        int weatherId=cursor.getInt(WeatherContract.COL_WEATHER_ID);

        //date
        long date = cursor.getLong(WeatherContract.COL_WEATHER_DATE);
        String dateString=Utility.getReadableDateString(date);
        TextView dateView=(TextView)view.findViewById(R.id.list_item_date_textview);
        dateView.setText(dateString);

        String description = cursor.getString(WeatherContract.COL_WEATHER_DESC);
        TextView descriptionView=(TextView)view.findViewById(R.id.list_item_forecast_textview);
        descriptionView.setText(description);

        //high & low temperatures
        double low = cursor.getDouble(WeatherContract.COL_WEATHER_MIN_TEMP);
        double high = cursor.getDouble(WeatherContract.COL_WEATHER_MAX_TEMP);
        String lowString = Utility.formatTemperature(low, mIsMetric);
        String highString = Utility.formatTemperature(high, mIsMetric);

        TextView lowView=(TextView)view.findViewById(R.id.list_item_low_textview);
        lowView.setText(lowString);

        TextView highText=(TextView)view.findViewById(R.id.list_item_high_textview);
        highText.setText(highString);
    }


}
