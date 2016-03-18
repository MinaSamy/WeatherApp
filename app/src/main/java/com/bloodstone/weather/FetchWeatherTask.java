package com.bloodstone.weather;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.util.Utility;
import com.bloodstone.weather.util.WeatherDataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.Calendar;
import java.util.Vector;
import static com.bloodstone.weather.data.WeatherContract.WeatherEntry;
/**
 * Created by minsamy on 3/18/2016.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG=getClass().getName();
    private ArrayAdapter<String> mForecastAdapter;
    private final Context mContext;

    public FetchWeatherTask(Context context, ArrayAdapter<String> forecastAdapter) {
        mContext = context;
        mForecastAdapter = forecastAdapter;
    }

    @Override
    protected String[] doInBackground(String... params) {
        String locationSetting = params[0];
        //http://api.openweathermap.org/data/2.5/forecast/daily?q=94041&mode=json&units=metric&cnt=7&appid=2de143494c0b295cca9337e1e96b00e0

        String baseUri = "http://api.openweathermap.org/data/2.5/forecast/daily?";


        Uri apiAddress = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("q", locationSetting)
                .appendQueryParameter("mode", "json")
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("cnt","14")
                .appendQueryParameter("APPID",BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                .build();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(apiAddress.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream stream = urlConnection.getInputStream();
            if (stream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            //return WeatherDataParser.getWeatherDataFromJson(mContext, buffer.toString(), 7);
            return getWeatherDataFromJson(buffer.toString(),locationSetting);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] s) {

        if (s != null) {
            mForecastAdapter.clear();
            mForecastAdapter.addAll(s);

        }
    }

    long addLocation(String locationSetting,String cityName,double lat,double lon){
        long locationId=-1;
        //check if the location exists
        Cursor locationCursor=mContext.getContentResolver().query(WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING+"=?",
                new String[]{locationSetting},
                null);
        if(locationCursor.moveToNext()){
            int locationIndex=locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId=locationCursor.getLong(locationIndex);
        }else{
            ContentValues locationValues=new ContentValues();
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,lon);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);

            Uri insertedUri = mContext.getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    locationValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);
        }
        return locationId;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr,
                                            String locationSetting)
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Location information
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";

        // Location coordinate
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String OWM_LIST = "list";

        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        // All temperatures are children of the "temp" object.
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";

        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            String cityName = cityJson.getString(OWM_CITY_NAME);

            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

            long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

            //Calendar calendar=Calendar.getInstance();



            for(int i = 0; i < weatherArray.length(); i++) {
                // These are the values that will be collected.
                long dateTime;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;

                double high;
                double low;

                String description;
                int weatherId;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                Calendar calendar=Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH,i);
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = Utility.normalizeDate(calendar.getTimeInMillis());

                pressure = dayForecast.getDouble(OWM_PRESSURE);
                humidity = dayForecast.getInt(OWM_HUMIDITY);
                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

                // Description is in a child array called "weather", which is 1 element long.
                // That element also contains a weather code.
                JSONObject weatherObject =
                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);
                weatherId = weatherObject.getInt(OWM_WEATHER_ID);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                high = temperatureObject.getDouble(OWM_MAX);
                low = temperatureObject.getDouble(OWM_MIN);

                ContentValues weatherValues = new ContentValues();

                weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationId);
                weatherValues.put(WeatherEntry.COLUMN_DATE, dateTime);
                weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);
                weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                weatherValues.put(WeatherEntry.COLUMN_DEGREES, windDirection);
                weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, description);
                weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);

                cVVector.add(weatherValues);
            }

            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[]values=new ContentValues[cVVector.size()];
                cVVector.toArray(values);
                mContext.getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI,values);
            }

            // Sort order:  Ascending, by date.
            String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";
            Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                    locationSetting, Utility.normalizeDate(System.currentTimeMillis()));



            Cursor cur = mContext.getContentResolver().query(weatherForLocationUri,
                    null, null, null, sortOrder);

            cVVector = new Vector<ContentValues>(cur.getCount());
            if ( cur.moveToFirst() ) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");

            String[] resultStrs = convertContentValuesToUXFormat(cVVector);
            return resultStrs;

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues weatherValues = cvv.elementAt(i);
            String highAndLow = Utility.formatHighLows(mContext,
                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MAX_TEMP),
                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MIN_TEMP));
            resultStrs[i] = Utility.getReadableDateString(
                    weatherValues.getAsLong(WeatherEntry.COLUMN_DATE)) +
                    " - " + weatherValues.getAsString(WeatherEntry.COLUMN_SHORT_DESC) +
                    " - " + highAndLow;
        }
        return resultStrs;
    }
}
