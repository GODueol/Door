package com.example.kwoncheolhyeok.core.ClubActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.kwoncheolhyeok.core.R;

import java.util.ArrayList;
import java.util.List;

public class TabFragment3 extends android.support.v4.app.Fragment {

    // 싱글톤 패턴
    @SuppressLint("StaticFieldLeak")
    private static TabFragment3 mInstance;

    public static TabFragment3 getInstance() {
        if (mInstance == null) mInstance = new TabFragment3();
        return mInstance;
    }
    @SuppressLint("ValidFragment")
    private TabFragment3(){};

    private ListView club_recommend_list;
    ImageView club1, club2, club3, club4, club5, club6;

    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        // My Club 6개
        club1 = (ImageView) view.findViewById(R.id.club1);
        club1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClubActivity.class);
                startActivity(i);
            }
        });
        club2 = (ImageView) view.findViewById(R.id.club2);
        club2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClubActivity.class);
                startActivity(i);
            }
        });
        club3 = (ImageView) view.findViewById(R.id.club3);
        club3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClubActivity.class);
                startActivity(i);
            }
        });
        club4 = (ImageView) view.findViewById(R.id.club4);
        club4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClubActivity.class);
                startActivity(i);
            }
        });
        club5 = (ImageView) view.findViewById(R.id.club5);
        club5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClubActivity.class);
                startActivity(i);
            }
        });
        club6 = (ImageView) view.findViewById(R.id.club6);
        club6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClubActivity.class);
                startActivity(i);
            }
        });

        club_recommend_list = (ListView) view.findViewById(R.id.club_recommend_list);

        List<String> list = new ArrayList<String>();
        for(int i=0; i<20; i++){
            list.add(i+"");
        }
        ClubRecommendListAdapter clubRecommendListAdapter = new ClubRecommendListAdapter(list, getActivity());
        club_recommend_list.setAdapter(clubRecommendListAdapter);

        club_recommend_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent myIntent = new Intent(getActivity(), ClubActivity.class);
                getActivity().startActivity(myIntent);
            }
        });

        return view;

    }

    }