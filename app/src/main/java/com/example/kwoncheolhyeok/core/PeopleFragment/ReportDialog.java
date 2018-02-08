package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.CorePage.CustomDialog;
import com.example.kwoncheolhyeok.core.R;

/**
 * Created by kimbyeongin on 2018-01-06.
 */

public class ReportDialog extends CustomDialog {

    ReportDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_user_report);

        TextView cancel = findViewById(R.id.cancel);
        TextView report = findViewById(R.id.report);
        NumberPicker picker = findViewById(R.id.typePicker);
        picker.setMinValue(0);
        picker.setMaxValue(2);
        picker.setDisplayedValues( new String[] { "Belgium", "France", "United Kingdom" } );

                cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 신고


            }
        });

    }
}
