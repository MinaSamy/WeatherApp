package com.bloodstone.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.util.Utility;

import java.util.Vector;

/**
 * Created by minasamy on 3/18/2016.
 */
public class ForecastAdapter extends CursorAdapter {

    private final Context mContext;
    public ForecastAdapter(Context context, Cursor c) {
        super(context, c, false);
        mContext=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_item_forecast,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView txt=(TextView)view.findViewById(R.id.list_item_forecast_textview);
        txt.setText(Utility.convertCursorRowToUXFormat(context, cursor));
    }



}
