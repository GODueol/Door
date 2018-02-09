package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.CorePage.CustomDialog;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;

/**
 * Created by kimbyeongin on 2018-01-06.
 */

public class ReportDialog extends CustomDialog {

    String oUuid;
    ReportDialog(@NonNull Context context, String oUuid) {
        super(context);
        this.oUuid = oUuid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_user_report);

        TextView cancel = findViewById(R.id.cancel);
        TextView report = findViewById(R.id.report);
        final NumberPicker picker = findViewById(R.id.typePicker);
        final RadioButton isOnlyBlockBtn = findViewById(R.id.isOnlyBlock);

        final String[] reportTypeList = new String[] { "과한 노출 사진", "타인의 사진 도용", "성매매 등 부적절한 글", "미성년자 회원", "스팸 및 광고" };
        picker.setMinValue(0);
        picker.setMaxValue(reportTypeList.length - 1);
        picker.setDisplayedValues(reportTypeList);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 신고 : 아래 5개의 정보를 DB에 넣는다
                String picStr = reportTypeList[picker.getValue()];
                boolean isOnlyBlock = isOnlyBlockBtn.isChecked();
                long date = System.currentTimeMillis();
                String mUuid = DataContainer.getInstance().getUid();
                // oUuid

            }
        });

    }
}
