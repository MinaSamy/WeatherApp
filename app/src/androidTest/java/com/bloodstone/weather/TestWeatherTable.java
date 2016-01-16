package com.bloodstone.weather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.bloodstone.weather.data.WeatherDbHelper;
import com.bloodstone.weather.data.WeatherContract.LocationEntry;
import com.bloodstone.weather.data.WeatherContract.WeatherEntry;

import junit.framework.Test;

/**
 * Created by minsamy on 1/16/2016.
 */
public class TestWeatherTable extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        this.mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void testInsert(){
        //insert a location first
        WeatherDbHelper helper=new WeatherDbHelper(this.mContext);
        SQLiteDatabase db=helper.getWritableDatabase();

        ContentValues locationContentValues=TestUtils.getLocationContentValues();
        long locationId=db.insert(LocationEntry.TABLE_NAME,null,locationContentValues);

        assertTrue("Error: Location couldn't be inserted",locationId!=-1);

        ContentValues weatherContentValues= TestUtils.getWeatherContentValues(locationId);
        long weatherId=db.insert(WeatherEntry.TABLE_NAME,null,weatherContentValues);

        assertTrue("Error: Weather row couldn't be inserted",weatherId!=-1);

        Cursor c=db.query(WeatherEntry.TABLE_NAME,null,null,null,null,null,null);
        assertEquals(1,c.getCount());

        assertTrue(c.moveToNext());
        c.close();
        db.close();
    }
}
