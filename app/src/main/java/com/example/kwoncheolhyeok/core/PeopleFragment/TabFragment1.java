package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.kwoncheolhyeok.core.R;

public class TabFragment1 extends android.support.v4.app.Fragment {

    GridView gridView = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(getContext()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent p = new Intent(getActivity().getApplicationContext(), FullImageActivity.class);
                p.putExtra("id", position);

                startActivity(p);

            }
        });

        return view;
    }
}






