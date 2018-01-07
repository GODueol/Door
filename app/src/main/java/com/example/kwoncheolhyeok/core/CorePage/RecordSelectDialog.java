package com.example.kwoncheolhyeok.core.CorePage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;

/**
 * Created by kimbyeongin on 2018-01-06.
 */

public class RecordSelectDialog extends CustomDialog {

    public RecordSelectDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_select_record);

        sizeSet();

        TextView recordBtn = findViewById(R.id.record);
        TextView fileBtn = findViewById(R.id.getByFile);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordDialog recordDialog = new RecordDialog(getContext());
                recordDialog.show();
                cancel();
            }
        });

        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : 음성 파일 가져오기
                cancel();
            }
        });

    }
}
