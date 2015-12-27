package com.bloodstone.weather.fragment;

import android.content.Intent;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bloodstone.weather.R;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_detail, container, false);
        TextView forecastText=(TextView)root.findViewById(R.id.forecast_text);
        if(getActivity().getIntent().getExtras().containsKey(Intent.EXTRA_TEXT)){
            forecastText.setText(getActivity().getIntent().getExtras().getString(Intent.EXTRA_TEXT));
        }
        return root;
    }
}
