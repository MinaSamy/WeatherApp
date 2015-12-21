package com.bloodstone.weather.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bloodstone.weather.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        ArrayList<String> items = new ArrayList<>(Arrays.asList(data));

        ArrayAdapter<String>adapter=new ArrayAdapter<>(getActivity(),R.layout.list_item_forecast,R.id.text_forecast,items);
        View rootView=inflater.inflate(R.layout.fragment_main,container,false);
        ListView list=(ListView)rootView.findViewById(R.id.listview_forecast);
        list.setAdapter(adapter);
        return rootView;
    }

}
