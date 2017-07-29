package com.example.kwoncheolhyeok.core.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.kwoncheolhyeok.core.R;

/**
 * Created by KwonCheolHyeok on 2016-11-25.
 */

public class MapsActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_activity);
    }
}
