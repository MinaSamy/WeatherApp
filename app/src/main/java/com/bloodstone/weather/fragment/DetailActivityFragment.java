package com.bloodstone.weather.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bloodstone.weather.R;
import com.bloodstone.weather.SettingsActivity;
import com.bloodstone.weather.data.WeatherContract;
import com.bloodstone.weather.util.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private ShareActionProvider mShareActionProvider;
    private String mDetails;
    private TextView mForecastText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_detail, container, false);
        mForecastText =(TextView)root.findViewById(R.id.forecast_text);
        if(getActivity().getIntent()!=null){
            mDetails= getActivity().getIntent().getDataString();
            if(null!=mDetails){
                mForecastText.setText(mDetails);
            }

        }

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item=menu.findItem(R.id.action_share);
        mShareActionProvider= (ShareActionProvider)MenuItemCompat.getActionProvider(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_settings){
            Intent settingsIntent=new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void setShareIntent(String forecast){
        if(mShareActionProvider!=null){
            Intent shareIntent=new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,forecast);
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            }else{
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null!=mDetails){
            Uri queryUri=Uri.parse(mDetails);
            return new CursorLoader(getActivity(),queryUri,WeatherContract.FORECAST_DETAILS_COLUMNS,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null &&data.moveToFirst()){
            String date=Utility.getReadableDateString(data.getLong(1));
            String desc =data.getString(2);
            double high =data.getDouble(3);
            double low=data.getDouble(4);
            String highLow=Utility.formatHighLows(getActivity(),low,high);
            String forecast=String.format("%s - %s - %s",date,desc,highLow);
            if(null!=forecast){
                mForecastText.setText(forecast);
                setShareIntent(forecast);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
