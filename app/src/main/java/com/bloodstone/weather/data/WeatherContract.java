package com.bloodstone.weather.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.bloodstone.weather.util.Utility;


/**
 * Created by minsamy on 1/9/2016.
 */
public class WeatherContract {


    public static final String CONTENT_AUTHORITY = "com.bloodstone.weather";
    public static final Uri CONTENT_BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    static public final String[] FORECAST_COLUMNS = new String[]
            {
                    WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
                    WeatherEntry.COLUMN_DATE,
                    WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherEntry.COLUMN_MIN_TEMP,
                    LocationEntry.COLUMN_LOCATION_SETTING,
                    WeatherEntry.COLUMN_WEATHER_ID,
                    LocationEntry.COLUMN_COORD_LAT,
                    LocationEntry.COLUMN_COORD_LONG
            };

    static public final String[] FORECAST_DETAILS_COLUMNS=new String[]
            {
                    WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
                    WeatherEntry.COLUMN_DATE,
                    WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherEntry.COLUMN_MIN_TEMP
            };

    static public final int COL_WEATHER_ID = 0;
    static public final int COL_WEATHER_DATE = 1;
    static public final int COL_WEATHER_DESC = 2;
    static public final int COL_WEATHER_MAX_TEMP = 3;
    static public final int COL_WEATHER_MIN_TEMP = 4;
    static public final int COL_LOCATION_SETTING = 5;
    static public final int COL_WEATHER_CONDITION_ID = 6;
    static public final int COL_COORD_LAT = 7;
    static public final int COL_COORD_LONG = 8;

    public static final class LocationEntry implements BaseColumns {

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final Uri CONTENT_URI = CONTENT_BASE_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        public static final String COLUMN_CITY_NAME = "city_name";

        public static final String COLUMN_COORD_LAT = "coord_lat";

        public static final String COLUMN_COORD_LONG = "coord_long";

        static public Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class WeatherEntry implements BaseColumns {

        //content provider info
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final Uri CONTENT_URI = CONTENT_BASE_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_LOC_KEY = "location_id";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_WEATHER_ID = "weather_id";

        public static final String COLUMN_SHORT_DESC = "short_description";

        public static final String COLUMN_MIN_TEMP = "min";

        public static final String COLUMN_MAX_TEMP = "max";

        public static final String COLUMN_HUMIDITY = "humidity";

        public static final String COLUMN_PRESSURE = "pressure";

        public static final String COLUMN_WIND_SPEED = "wind";

        public static final String COLUMN_DEGREES = "degrees";


        static public Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        static public Uri buildWeatherLocationUri(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        static public Uri buildWeatherLocationWithStartDate(String locationSetting, long startDate) {
            long normalizedDate = Utility.normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        static public Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(Utility.normalizeDate(date))).build();
        }

        static public String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        static public long getDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }

        public static long getStartDateFromUri(Uri uri) {
            //String dateString = uri.getQueryParameter(COLUMN_DATE);
            String dateString=uri.getPathSegments().get(2);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }


    }
}
