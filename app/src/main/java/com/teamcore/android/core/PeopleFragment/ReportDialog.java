package com.teamcore.android.core.PeopleFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.teamcore.android.core.CorePage.CustomDialog;
import com.teamcore.android.core.Entity.Report;
import com.teamcore.android.core.Exception.ChildSizeMaxException;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kimbyeongin on 2018-01-06.
 */

public class ReportDialog extends CustomDialog {

    private String oUuid;
    private String postKey;
    private String cUuid;

    ReportDialog(@NonNull Context context, String oUuid) {
        super(context);
        this.oUuid = oUuid;
    }

    public ReportDialog(@NonNull Context context, String oUuid, String cUuid, String postKey) {
        super(context);
        this.oUuid = oUuid;
        this.postKey = postKey;
        this.cUuid = cUuid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_user_report);


        TextView cancel = findViewById(R.id.cancel);
        TextView report = findViewById(R.id.report);
        final EditText txt_report = findViewById(R.id.txt_report);
        final NumberPicker picker = findViewById(R.id.typePicker);

        final String[] reportTypeList = new String[]{"성기 노출 사진", "타인의 사진 도용", "성매매 등 부적절한 글", "미성년자 회원", "스팸 및 광고", "기타 사유"};
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

                try {
                    long date = UiUtil.getInstance().getCurrentTime(getContext());

                    String mUuid = DataContainer.getInstance().getUid();

                    Task task;
                    Report report = new Report(txt_report.getText().toString(), date);
                    if(postKey == null){
                        // user 신고
                        task = FirebaseDatabase.getInstance().getReference().child("reports/users")
                                .child(oUuid)   // 신고 타겟 유저
                                .child(picStr).child(mUuid).setValue(report);
                    } else {
                        // post 신고
                        task = FirebaseDatabase.getInstance().getReference().child("reports/posts")
                                .child(oUuid)   // 신고 타겟 유저
                                .child(cUuid)   // 코어 유저+
                                .child(postKey) // 신고 포스트 키+
                                .child(picStr).child(mUuid).setValue(report);
                    }

                    task.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "신고 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), "신고 실패 : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity(getOwnerActivity());
                }
            }
        });

    }
}
