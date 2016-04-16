package com.bloodstone.weather;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.util.Utility;

/**
 * Created by minasamy on 3/18/2016.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE = 1;



    private boolean mUseTodayLayout=true;

    public void setUseTodayLayout(boolean useTodayLayout) {
        this.mUseTodayLayout = useTodayLayout;
    }

    public ForecastAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0&&mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        ViewHolder viewHolder = null;
        View view = null;
        if (viewType == VIEW_TYPE_TODAY) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast_today, parent, false);

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
        }
        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        //weather id, used in showing the weather icon
        int weatherId = cursor.getInt(WeatherContract.COL_WEATHER_CONDITION_ID);
        int weatherIcon=-1;
        int viewType=getItemViewType(cursor.getPosition());
        if(viewType==VIEW_TYPE_FUTURE){
            weatherIcon=Utility.getIconResourceForWeatherCondition(weatherId);
        }else{
            weatherIcon=Utility.getArtResourceForWeatherCondition(weatherId);
        }
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        //date
        long date = cursor.getLong(WeatherContract.COL_WEATHER_DATE);
        String dateString = Utility.getReadableDateString(context, date);
        viewHolder.dateView.setText(dateString);

        String description = cursor.getString(WeatherContract.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(description);

        //high & low temperatures
        double low = cursor.getDouble(WeatherContract.COL_WEATHER_MIN_TEMP);
        double high = cursor.getDouble(WeatherContract.COL_WEATHER_MAX_TEMP);
        boolean isMetric = Utility.isMetric(context);
        String lowString = Utility.formatTemperature(context, low, isMetric);
        String highString = Utility.formatTemperature(context, high, isMetric);

        viewHolder.lowTempView.setText(lowString);

        viewHolder.highTempView.setText(highString);
        //viewHolder.iconView.setImageDrawable(ContextCompat.getDrawable(context,weatherIcon));
        final int finalWeatherIcon = weatherIcon;
        viewHolder.iconView.post(new Runnable() {
            @Override
            public void run() {
                viewHolder.iconView.setImageResource(finalWeatherIcon);
                viewHolder.iconView.setContentDescription(Utility.getWeatherDescription(context,finalWeatherIcon));
            }
        });
    }

    static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

}
