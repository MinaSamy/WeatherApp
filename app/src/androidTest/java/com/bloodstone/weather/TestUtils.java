package com.bloodstone.weather;

import android.content.ContentValues;

import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.data.WeatherContract.WeatherEntry;
import java.util.Calendar;

/**
 * Created by minsamy on 1/16/2016.
 */
public class TestUtils {

    static public String testLocationSetting = "99705";
    static public String testCityName = "North Pole";
    static public double testLatitude = 64.7488;
    static public double testLongitude = -147.353;


    static public long testWeatherDate= Calendar.getInstance().getTimeInMillis();
    static public String testWeatherShortDesc="desc";
    static public int testWeatherId=1;
    static public int testWeatherMinTemp=10;
    static public int testWeatherMaxTemp=20;
    static public double testWeatherHumidity=40.5;
    static public double testWeatherPressure=20.4;
    static public double testWeatherWindSpeed=20.0;
    static public double testWeatherDegrees=11;

    static public ContentValues getLocationContentValues(){
        ContentValues cv=new ContentValues();
        cv.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,testLocationSetting);
        cv.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,testCityName);
        cv.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,testLatitude);
        cv.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);

        return cv;
    }

    static public ContentValues getWeatherContentValues(long locationId){
        ContentValues cv=new ContentValues();
        cv.put(WeatherEntry.COLUMN_LOC_KEY,locationId);
        cv.put(WeatherEntry.COLUMN_DATE,testWeatherDate);
        cv.put(WeatherEntry.COLUMN_SHORT_DESC,testWeatherShortDesc);
        cv.put(WeatherEntry.COLUMN_WEATHER_ID,testWeatherId);
        cv.put(WeatherEntry.COLUMN_MIN_TEMP,testWeatherMinTemp);
        cv.put(WeatherEntry.COLUMN_MAX_TEMP,testWeatherMaxTemp);
        cv.put(WeatherEntry.COLUMN_HUMIDITY,testWeatherHumidity);
        cv.put(WeatherEntry.COLUMN_PRESSURE,testWeatherPressure);
        cv.put(WeatherEntry.COLUMN_WIND_SPEED,testWeatherWindSpeed);
        cv.put(WeatherEntry.COLUMN_DEGREES,testWeatherDegrees);
        return  cv;
    }
}
