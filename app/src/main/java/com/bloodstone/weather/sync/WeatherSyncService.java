package com.bloodstone.weather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by minsamy on 4/22/2016.
 */
public class WeatherSyncService extends Service {

    private static final Object syncAdapterLock=new Object();
    private static WeatherSyncAdapter weatherSyncAdapter=null;

    @Override
    public void onCreate() {
        synchronized (syncAdapterLock){
            if(weatherSyncAdapter==null){
                weatherSyncAdapter=new WeatherSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return weatherSyncAdapter.getSyncAdapterBinder();
    }
}
