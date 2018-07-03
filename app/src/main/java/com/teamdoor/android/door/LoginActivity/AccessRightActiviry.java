package com.teamdoor.android.door.LoginActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;

import com.teamdoor.android.door.R;

/**
 * Created by Kwon on 2018-04-02.
 */

public class AccessRightActiviry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_right_main);

        CheckBox check_access = (CheckBox) findViewById(R.id.check_access);
        check_access.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                finish();
            }
        });
    }


}