package com.bloodstone.weather;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.data.WeatherDbHelper;

import java.util.HashSet;

/**
 * Created by minsamy on 1/9/2016.
 */
public class TestDb extends AndroidTestCase {

    private static final String LOG_TAG=TestDb.class.getSimpleName();


    private void deleteDatabase(){
        this.mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        deleteDatabase();
    }


    public void testCreateDb(){
        final HashSet<String>tableNameHashSet=new HashSet<>();
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);

        SQLiteDatabase db=new WeatherDbHelper(mContext).getWritableDatabase();

        assertEquals(true,db.isOpen());

        //select table name
        Cursor c=db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null);

        assertTrue("ERROR: DB not created correctly",c.moveToFirst());

        //verify that table names exist
        do{
            tableNameHashSet.remove(c.getString(0));
        }while (c.moveToNext());

        assertTrue("Error: tables not created", tableNameHashSet.isEmpty());


        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Location Table not created correctly",c.moveToNext());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnIndex=c.getColumnIndex("name");
        do{
            String columnName=c.getString(columnIndex);
            locationColumnHashSet.remove(columnName);
        }while (c.moveToNext());

        assertTrue("Error: location table does not have columns as expected",locationColumnHashSet.isEmpty());

        db.close();
    }
}
