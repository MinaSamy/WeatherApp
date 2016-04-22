package com.bloodstone.weather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by minsamy on 4/22/2016.
 */
public class WeatherAuthenticatorService extends Service {

    private WeatherAuthenticator mAuthenticator;

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onCreate() {
        mAuthenticator=new WeatherAuthenticator(this);
    }
}
