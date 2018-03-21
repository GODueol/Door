package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.CorePage.CustomDialog;
import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.UiUtil;

/**
 * Created by kimbyeongin on 2018-01-06.
 */

public class ReportDialog extends CustomDialog {

    private String oUuid;

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
        final CheckBox isOnlyBlockBtn = findViewById(R.id.isOnlyBlock);

        final String[] reportTypeList = new String[]{"성기 노출 사진", "타인의 사진 도용", "성매매 등 부적절한 글", "미성년자 회원", "스팸 및 광고"};
        picker.setMinValue(0);
        picker.setMaxValue(reportTypeList.length - 1);
        picker.setDisplayedValues(reportTypeList);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setWrapSelectorWheel(true);


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
                try {
                    long date = UiUtil.getInstance().getCurrentTime(getContext());
                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity(getOwnerActivity());
                }
                String mUuid = DataContainer.getInstance().getUid();
                // oUuid

            }
        });

    }
}
