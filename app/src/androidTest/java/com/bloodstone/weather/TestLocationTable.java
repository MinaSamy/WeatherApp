package com.bloodstone.weather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import static com.bloodstone.weather.data.WeatherContract.LocationEntry;

import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.data.WeatherDbHelper;

/**
 * Created by minsamy on 1/16/2016.
 */
public class TestLocationTable extends AndroidTestCase {



    @Override
    protected void setUp() throws Exception {
        this.mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }



    public void testInsert(){
        WeatherDbHelper helper=new WeatherDbHelper(mContext);
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues cv=TestUtils.getLocationContentValues();
        long id=db.insert(LocationEntry.TABLE_NAME, null, cv);
        assertTrue("Error in inserting a new row", id != -1);

        //query the database
        Cursor c=db.query(LocationEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Cursor has no results",c.getCount()==1);
        assertTrue("Empty result", c.moveToNext());

        String locationSetting=c.getString(c.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING));
        assertEquals(TestUtils.testLocationSetting,locationSetting);

        String city=c.getString(c.getColumnIndex(LocationEntry.COLUMN_CITY_NAME));
        assertEquals(TestUtils.testCityName,city);

        double lat=c.getDouble(c.getColumnIndex(LocationEntry.COLUMN_COORD_LAT));
        assertEquals(TestUtils.testLatitude,lat);

        double lon=c.getDouble(c.getColumnIndex(LocationEntry.COLUMN_COORD_LONG));
        assertEquals(TestUtils.testLongitude,lon);

        c.close();
        db.close();
    }
}
