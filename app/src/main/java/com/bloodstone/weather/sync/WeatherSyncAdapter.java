package com.bloodstone.weather.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
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
    // Interval at which to sync with the weather, in milliseconds.
    // 3 hours
    public static final int SYNC_INTERVAL = 60*60*3;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

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
            onAccountCreated(newAccount,context);
        }
        return newAccount;
    }

    public static void configurePeriodicSync(Context context,int syncInterval,int flexTime){
        Account account=getSyncAccount(context);
        String authority=context.getString(R.string.content_authority);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            SyncRequest request=new SyncRequest.Builder()
                    .syncPeriodic(syncInterval,flexTime)
                    .setSyncAdapter(account,context.getString(R.string.content_authority))
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        }else{
            ContentResolver.addPeriodicSync(account,authority,new Bundle(),syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount,Context context){
        configurePeriodicSync(context,SYNC_INTERVAL,SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,context.getString(R.string.content_authority),true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }
}
