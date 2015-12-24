package com.bloodstone.weather.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by minsamy on 12/24/2015.
 */
public class WeatherDataParser {

    private static final String TOKEN_LIST="list";
    private static final String TOKEN_TEMP="temp";
    private static final String TOKEN_MAX="max";
    private static final String TOKEN_MIN="min";
    private static final String TOKEN_MAIN="main";
    private static final String TOKEN_WEATHER="weather";
    private static final String TOKEN_DT="dt";

    private static final SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");

    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex) throws JSONException {
        JSONObject obj=new JSONObject(weatherJsonStr);
        JSONArray list= obj.getJSONArray(TOKEN_LIST);
        if(list.length()>dayIndex){
            JSONObject dayObject= list.getJSONObject(dayIndex);
            JSONObject tempObject=dayObject.getJSONObject(TOKEN_TEMP);
            double max=tempObject.getDouble(TOKEN_MAX);
            return max;
        }
        return Double.NaN;
    }

    private static String formatHighLows(double min,double max){
        min=Math.round(min);
        max=Math.round(max);
        return max+" / "+min;
    }

    public static String[]getWeatherDataFromJson(String forecastJsonStr,int numDays) throws JSONException {
        JSONObject obj=new JSONObject(forecastJsonStr);
        JSONArray list=obj.getJSONArray(TOKEN_LIST);

        String []results=new String[list.length()];

        Calendar calendar=Calendar.getInstance();
        for(int i=0;i<list.length();i++){
            JSONObject dayObject=list.getJSONObject(i);

            //date and time
            long timeInMillis=dayObject.getLong(TOKEN_DT)*1000l;
            calendar.setTimeInMillis(timeInMillis);
            String day=shortenedDateFormat.format(calendar.getTime());

            //temperature object
            JSONObject tempObject=dayObject.getJSONObject(TOKEN_TEMP);
            double min=tempObject.getDouble(TOKEN_MIN);
            double max=tempObject.getDouble(TOKEN_MAX);

            //weather object
            JSONObject weatherObject=dayObject.getJSONArray(TOKEN_WEATHER).getJSONObject(0);
            String highLow=formatHighLows(min, max);
            String description=weatherObject.getString(TOKEN_MAIN);
            results[i]=day+" - "+description+" - "+highLow;
        }
        return results;
    }
}
