package com.bloodstone.weather.fragment;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.bloodstone.weather.R;
import com.bloodstone.weather.SettingsActivity;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {


    private ShareActionProvider mShareActionProvider;
    private String mDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_detail, container, false);
        TextView forecastText=(TextView)root.findViewById(R.id.forecast_text);
        if(getActivity().getIntent().getExtras().containsKey(Intent.EXTRA_TEXT)){
            mDetails= getActivity().getIntent().getExtras().getString(Intent.EXTRA_TEXT);
            forecastText.setText(mDetails);
        }
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item=menu.findItem(R.id.action_share);
        mShareActionProvider= (ShareActionProvider)MenuItemCompat.getActionProvider(item);
        setShareIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_settings){
            Intent settingsIntent=new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void setShareIntent(){
        if(mShareActionProvider!=null){
            Intent shareIntent=new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,mDetails);
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            }else{
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}