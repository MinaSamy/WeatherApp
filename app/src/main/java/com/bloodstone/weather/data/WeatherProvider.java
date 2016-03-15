package com.bloodstone.weather.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import static com.bloodstone.weather.data.WeatherContract.WeatherEntry;
import static com.bloodstone.weather.data.WeatherContract.LocationEntry;

/**
 * Created by minsamy on 3/4/2016.
 */
public class WeatherProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher=buildUriMatcher();
    private WeatherDbHelper mDbHelper;

    //Uri types
    private static final int TYPE_WEATHER=100;
    private static final int TYPE_WEATHER_WITH_LOCATION=101;
    private static final int TYPE_WEATHER_WITH_LOCATION_AND_DATE=102;
    private static final int TYPE_LOCATION=300;

    private static final SQLiteQueryBuilder sQueryBuilder;

    //selection statements

    //Select by location setting
    private static final String LOCATION_SETTING_SELECTION=LocationEntry.COLUMN_LOCATION_SETTING+"=?";

    //select by location setting and start date
    private static final String LOCATION_SETTING_AND_START_DATE=LocationEntry.COLUMN_LOCATION_SETTING+"=? AND "
            +WeatherEntry.COLUMN_DATE+">=?";

    //select by location setting and date
    private static final String LOCATION_SETTING_AND_DATE=LocationEntry.COLUMN_LOCATION_SETTING+"=? AND "
            +WeatherEntry.COLUMN_DATE+"=?";


    //initialize static members
    static {
        sQueryBuilder=new SQLiteQueryBuilder();
        //query weather by location
        sQueryBuilder.setTables(WeatherEntry.TABLE_NAME+" INNER JOIN "+
        LocationEntry.TABLE_NAME+
        "ON "+ WeatherEntry.COLUMN_LOC_KEY+" = "+
        LocationEntry._ID);


    }

    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_WEATHER,TYPE_WEATHER);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_WEATHER+"/*",TYPE_WEATHER_WITH_LOCATION);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_WEATHER+"/*/#",TYPE_WEATHER_WITH_LOCATION_AND_DATE);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_LOCATION,TYPE_LOCATION);
        return null;
    }

    @Override
    public boolean onCreate() {
        mDbHelper=new WeatherDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match=sUriMatcher.match(uri);
        switch (match){
            case TYPE_WEATHER:
                return WeatherEntry.CONTENT_TYPE;
            case TYPE_LOCATION:
                return LocationEntry.CONTENT_TYPE;
            case TYPE_WEATHER_WITH_LOCATION:
                return LocationEntry.CONTENT_ITEM_TYPE;
            case TYPE_WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
