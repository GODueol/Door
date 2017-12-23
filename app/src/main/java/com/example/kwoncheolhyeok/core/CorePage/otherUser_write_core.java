package com.example.kwoncheolhyeok.core.CorePage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.kwoncheolhyeok.core.R;

/**
 * Created by Kwon on 2017-12-23.
 */

public class otherUser_write_core extends AppCompatActivity {

    ToggleButton btn_yes = null;
    ToggleButton btn_pass = null;
    ToggleButton btn_no = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_other_user_write);

        btn_yes = (ToggleButton)findViewById(R.id.btn_yes);
        btn_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    buttonView.setBackgroundColor(Color.parseColor("#28B463"));
                else buttonView.setBackgroundColor(Color.parseColor("#BFC9CA"));
            }
        });

        btn_pass = (ToggleButton)findViewById(R.id.btn_pass);
        btn_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    buttonView.setBackgroundColor(Color.parseColor("#F4D03F"));
                else buttonView.setBackgroundColor(Color.parseColor("#BFC9CA"));
            }
        });

        btn_no = (ToggleButton)findViewById(R.id.btn_no);
        btn_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    buttonView.setBackgroundColor(Color.parseColor("#E74C3C"));
                else buttonView.setBackgroundColor(Color.parseColor("#BFC9CA"));
            }
        });





    }




}