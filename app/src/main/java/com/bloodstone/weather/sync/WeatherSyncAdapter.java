package com.bloodstone.weather.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.bloodstone.weather.FetchWeatherUtils;
import com.bloodstone.weather.R;
import com.bloodstone.weather.util.Utility;

/**
 * Created by minsamy on 4/22/2016.
 */
public class WeatherSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG=getClass().getSimpleName();

    public WeatherSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG,"Performing sync");
        String location= Utility.getPreferredLocation(getContext());
        FetchWeatherUtils.getWeatherData(getContext(),location);
    }

    public static void syncImmediately(Context context){
        Bundle bundle=new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);

        ContentResolver.requestSync(getSyncAccount(context),context.getString(R.string.content_authority),bundle);
    }

    public static Account getSyncAccount(Context context){
        AccountManager am=(AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount=new Account(context.getString(R.string.app_name),context.getString(R.string.account_type));
        if(null==am.getPassword(newAccount)){
            //password doesn't exist

            //add the account info, no password or any user data
            if(!am.addAccountExplicitly(newAccount,"",null)){
                return null;
            }
        }
        return newAccount;
    }
}
