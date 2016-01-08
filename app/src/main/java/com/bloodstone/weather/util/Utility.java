package com.bloodstone.weather.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.bloodstone.weather.R;

/**
 * Created by minsamy on 1/7/2016.
 */
public class Utility {



    private static boolean isImperial(Context context){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        String prefValue=preferences.getString(context.getString(R.string.pref_measurement_unit),
                context.getString(R.string.celsius));
        if(prefValue==context.getString(R.string.celsius)){
            return false;
        }
        return true;
    }

    public static String formatHighLows(Context context, double low,double high){
        if(isImperial(context)){
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        }
        low=Math.round(low);
        high=Math.round(high);
        return high+" / "+low;
    }

    public static String getPreferredLocation(Context context){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        String location=preferences.getString(context.getString(R.string.pref_location),context.getString(R.string.pref_location_default_value));
        return location;
    }

    public static Intent makeLocationIntent(Context context, String postalCode){
        Intent locationIntent=new Intent(Intent.ACTION_VIEW);
        String geocode="geo:0,0?q="+postalCode;
        locationIntent.setData(Uri.parse(geocode));
        if(locationIntent.resolveActivity(context.getPackageManager())!=null){
            return locationIntent;
        }
        return null;
    }

}
