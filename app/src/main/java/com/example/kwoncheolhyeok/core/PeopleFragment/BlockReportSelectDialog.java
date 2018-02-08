package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.CorePage.CustomDialog;
import com.example.kwoncheolhyeok.core.R;

/**
 * Created by kimbyeongin on 2018-01-06.
 */

public class BlockReportSelectDialog extends CustomDialog {

    interface BlockReportSelectDialogListener{
        void block();
        void report();
    }
    private BlockReportSelectDialogListener blockReportSelectDialogListener;

    BlockReportSelectDialog(@NonNull Context context, BlockReportSelectDialogListener blockReportSelectDialogListener) {
        super(context);
        this.blockReportSelectDialogListener = blockReportSelectDialogListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_select_block_report);

//        sizeSet();

        TextView blockBtn = findViewById(R.id.block);
        TextView reportBtn = findViewById(R.id.report);

        blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockReportSelectDialogListener.block();
                dismiss();
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockReportSelectDialogListener.report();
                dismiss();
            }
        });

    }

}
