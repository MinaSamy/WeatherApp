package com.bloodstone.weather.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        "ON "+ WeatherEntry.TABLE_NAME+"."+ WeatherEntry.COLUMN_LOC_KEY+" = "+
        LocationEntry.TABLE_NAME+"."+ LocationEntry._ID);


    }

    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_WEATHER,TYPE_WEATHER);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_WEATHER+"/*",TYPE_WEATHER_WITH_LOCATION);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_WEATHER+"/*/#",TYPE_WEATHER_WITH_LOCATION_AND_DATE);
        matcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_LOCATION,TYPE_LOCATION);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper=new WeatherDbHelper(getContext());
        return true;
    }

    private Cursor getWeatherByLocationSetting(Uri uri,String[]projection,String sortOrder){
        String locationSetting=WeatherEntry.getLocationSettingFromUri(uri);
        long startDate=WeatherEntry.getDateFromUri(uri);

        String selection=null;
        String[]selectionArgs=null;
        if(startDate==0){
            selection=LOCATION_SETTING_SELECTION;
            selectionArgs=new String[]{locationSetting};
        }else{
            selection=LOCATION_SETTING_AND_START_DATE;
            selectionArgs=new String[]{locationSetting,Long.toString(startDate)};
        }

        return sQueryBuilder.query(mDbHelper.getReadableDatabase(),projection,selection,selectionArgs
        ,null,null,sortOrder);
    }

    private Cursor getWeatherByLocationAndDate(Uri uri,String[]projection,String sortOrder){
        String locationSetting=WeatherEntry.getLocationSettingFromUri(uri);
        long startDate=WeatherEntry.getStartDateFromUri(uri);
        String selection=null;
        String[] selectionArgs=null;
        if(startDate==0){
            selection=LOCATION_SETTING_SELECTION;
        }else{
            selection=LOCATION_SETTING_AND_START_DATE;
            selectionArgs=new String[]{locationSetting,Long.toString(startDate)};
        }

        return  sQueryBuilder.query(mDbHelper.getReadableDatabase(),projection,selection,selectionArgs,
                null,null,null);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor=null;
        switch (sUriMatcher.match(uri)){
            case TYPE_WEATHER_WITH_LOCATION_AND_DATE:
                return getWeatherByLocationAndDate(uri,projection,sortOrder);
            case TYPE_WEATHER_WITH_LOCATION:
                return getWeatherByLocationSetting(uri,projection,sortOrder);
            case TYPE_LOCATION:
                return mDbHelper.getReadableDatabase().query(LocationEntry.TABLE_NAME,projection
                ,selection,selectionArgs,null,null,sortOrder);
        }
        if(cursor!=null){
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
        }
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match=sUriMatcher.match(uri);
        switch (match){
            case TYPE_WEATHER:
                return WeatherEntry.CONTENT_TYPE;
            case TYPE_LOCATION:
                return LocationEntry.CONTENT_TYPE;
            case TYPE_WEATHER_WITH_LOCATION:
                return LocationEntry.CONTENT_TYPE;
            case TYPE_WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }

    }


    private void normalizeValues(ContentValues cv){
        if(cv.containsKey(WeatherEntry.COLUMN_DATE)){
            long date=cv.getAsLong(WeatherEntry.COLUMN_DATE);
            date=WeatherEntry.normalizeDate(date);
            cv.put(WeatherEntry.COLUMN_DATE,date);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db=mDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        Uri retUri=null;

        switch (match){
            case TYPE_WEATHER:
                normalizeValues(values);
                long id=db.insert(WeatherEntry.TABLE_NAME,null,values);
                if(id>0){
                    retUri=WeatherEntry.buildWeatherUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into "+uri);
                }
                break;
            case TYPE_LOCATION:
                long locationId=db.insert(LocationEntry.TABLE_NAME,null,values);
                if(locationId>0){
                    retUri=LocationEntry.buildLocationUri(locationId);
                }else{
                    throw new android.database.SQLException("Failed to insert row into "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match=sUriMatcher.match(uri);
        int rowsDeleted;
        if(null==selection){
            selection="1";
        }
        switch (match){
            case TYPE_WEATHER:
                rowsDeleted= mDbHelper.getWritableDatabase().delete(WeatherEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case TYPE_LOCATION:
                rowsDeleted=mDbHelper.getWritableDatabase().delete(LocationEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Cannot delete, unknown Uri "+uri);
        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match=sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case TYPE_WEATHER:
                rowsUpdated=mDbHelper.getWritableDatabase().update(WeatherEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case TYPE_LOCATION:
                rowsUpdated=mDbHelper.getWritableDatabase().update(LocationEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Cannot update, unknown uri "+uri);
        }
        if(rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int retCount=0;
        final int match=sUriMatcher.match(uri);
        final SQLiteDatabase db=mDbHelper.getWritableDatabase();
        switch (match){
            case TYPE_WEATHER:
                db.beginTransaction();
                try {
                    for(ContentValues value:values){
                        normalizeValues(value);
                        long id=db.insert(WeatherEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            retCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                break;
            case TYPE_LOCATION:
                db.beginTransaction();
                try{
                    for(ContentValues cv:values){
                        normalizeValues(cv);
                        long rowId=db.insert(LocationEntry.TABLE_NAME,null,cv);
                        if(rowId!=-1){
                            retCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Cannot bulk insert, unknown Uri "+uri);
        }
        if(retCount>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return retCount;
    }
}
