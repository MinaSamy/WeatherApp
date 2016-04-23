package com.bloodstone.weather.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bloodstone.weather.FetchWeatherUtils;

/**
 * Created by minsamy on 4/20/2016.
 */
public class WeatherService extends IntentService {

    static public final String EXTRA_LOCATION="location";
    private final String TAG=WeatherService.class.getSimpleName();
    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.hasExtra(EXTRA_LOCATION)){
            Log.d(TAG,"Service Started");
            String location=intent.getStringExtra(EXTRA_LOCATION);
            FetchWeatherUtils.getWeatherData(this,location);

        }
    }

    static public Intent makeServiceIntent(Context context,String location){
        Intent intent=new Intent(context,WeatherService.class);
        intent.putExtra(EXTRA_LOCATION,location);
        return intent;
    }

    static public class AlarmReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String locationSetting=intent.getStringExtra(EXTRA_LOCATION);
            Intent weatherServiceIntent=makeServiceIntent(context,locationSetting);
            context.startService(weatherServiceIntent);
        }
    }
}
