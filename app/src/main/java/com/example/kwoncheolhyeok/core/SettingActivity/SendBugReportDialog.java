package com.example.kwoncheolhyeok.core.SettingActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.CorePage.CustomDialog;
import com.example.kwoncheolhyeok.core.Entity.Suggestion;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Kwon on 2018-03-16.
 */

public class SendBugReportDialog extends CustomDialog {

    private TextView email;

    private EditText recive_email;
    private EditText content;

    private TextView sendReport;

    interface SendBugReportDialogListener {
        void show_sendBugReport_dialog();
    }

    private SendBugReportDialog.SendBugReportDialogListener SendBugReportDialogListener;

    SendBugReportDialog(@NonNull Context context, SendBugReportDialog.SendBugReportDialogListener SendBugReportDialogListener) {
        super(context);
        this.SendBugReportDialogListener = SendBugReportDialogListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send_bugreport);

        email = (TextView) findViewById(R.id.email);
        recive_email = (EditText) findViewById(R.id.recive_email);
        content = (EditText)findViewById(R.id.edit_report);
        sendReport = (TextView)findViewById(R.id.send_report);
        email.setText(DataContainer.getInstance().getUser().getEmail());
        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validate(email.getText().toString());
                    validate(recive_email.getText().toString());
                    validate(content.getText().toString());
                }catch (Exception e){
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                String brand = Build.BRAND +"/"+ Build.MANUFACTURER +"/"+ Build.MODEL;
                String uuid = DataContainer.getInstance().getUid();
                String version = Build.VERSION.RELEASE;
                Suggestion suggestion = new Suggestion(email.getText().toString(),
                        recive_email.getText().toString(),content.getText().toString(), brand,uuid,version);
                FirebaseDatabase.getInstance().getReference("suggestion").push().setValue(suggestion).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),"의견보내기에 성공하였습니다",Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"의견 보내기에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    public void validate(String text) throws Exception {
        if(text == null){
            throw new Exception("빈칸을 채워주세요");
        }
    }





    }