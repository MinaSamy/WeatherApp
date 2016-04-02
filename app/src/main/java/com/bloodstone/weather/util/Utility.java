package com.bloodstone.weather.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.bloodstone.weather.R;
import com.bloodstone.weather.data.WeatherContract;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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


    static public String formatTemperature(Context context, double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        //return String.format("%.0f", temp);
        return context.getString(R.string.degrees,temp);
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_measurement_unit),
                context.getString(R.string.celsius))
                .equals(context.getString(R.string.celsius));
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

    static public long normalizeDate(long startDate){
        //Calendar calendar=new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    static public String getReadableDateString(Context context,long time){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time);

        //System calendar
        Calendar systemCalendar=Calendar.getInstance();
        systemCalendar.setTimeInMillis(System.currentTimeMillis());

        if(isForecastDateToday(calendar,systemCalendar)){
            return context.getString(R.string.today);
        }else if(isForecastDateTomorrow(calendar,systemCalendar)){
            return context.getString(R.string.tomorrow);
        }else{
            return getDateDayNameString(calendar);
        }
    }

    public static boolean isForecastDateToday(Calendar forecastCalendar,Calendar calendar){
        if(forecastCalendar.get(Calendar.DAY_OF_WEEK)==calendar.get(Calendar.DAY_OF_WEEK)){
            return true;
        }
        return false;
    }

    public static boolean isForecastDateTomorrow(Calendar forecastCalendar,Calendar calendar){
        if(forecastCalendar.get(Calendar.DAY_OF_WEEK)==calendar.get(Calendar.DAY_OF_WEEK)+1){
            return true;
        }
        return false;
    }

    @NonNull
    private static String getDateDayNameString(Calendar calendar) {
        SimpleDateFormat format=new SimpleDateFormat("EEEE, MMM d");
        return format.format(calendar.getTime());
    }

    @NonNull
    public static String getDateMonthString(Calendar calendar) {
        SimpleDateFormat format=new SimpleDateFormat("MMM, d");
        return format.format(calendar.getTime());
    }

    public static String getFormattedWind(Context context, float windSpeed, float degrees) {
        int windFormat;
        if (Utility.isMetric(context)) {
            windFormat = R.string.format_wind_kmh;
        } else {
            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed;
        }

        // From wind direction in degrees, determine compass direction as a string (e.g NW)
        // You know what's fun, writing really long if/else statements with tons of possible
        // conditions.  Seriously, try it!
        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 || degrees < 22.5) {
            direction = "NW";
        }
        return String.format(context.getString(windFormat), windSpeed, direction);
    }


}
