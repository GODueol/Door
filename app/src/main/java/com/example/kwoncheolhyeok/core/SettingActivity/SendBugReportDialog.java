package com.example.kwoncheolhyeok.core.SettingActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kwon on 2018-03-16.
 */

public class SendBugReportDialog extends CustomDialog {


    private static int MiN_CONTENT = 10;
    @Bind(R.id.email)
    TextView email;
    @Bind(R.id.recive_email)
    EditText recive_email;
    @Bind(R.id.edit_report)
    EditText content;
    @Bind(R.id.send_report)
    TextView sendReport;
    @Bind(R.id.content_error)
    TextView content_error;
    @Bind(R.id.email_error)
    TextView email_error;

    private boolean avaliableEamil = false, avaliableContent = false;

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
        ButterKnife.bind(this);

        email.setText(DataContainer.getInstance().getUser().getEmail());

        setVerification();
        sendReport.setTextColor(getContext().getResources().getColor(R.color.gray));
        sendReport.setEnabled(false);
        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validate(email.getText().toString());
                    validate(recive_email.getText().toString());
                    validate(content.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                String brand = Build.BRAND + "/" + Build.MANUFACTURER + "/" + Build.MODEL;
                String uuid = DataContainer.getInstance().getUid();
                String version = Build.VERSION.RELEASE;
                Suggestion suggestion = new Suggestion(email.getText().toString(),
                        recive_email.getText().toString(), content.getText().toString(), brand, uuid, version);
                FirebaseDatabase.getInstance().getReference("suggestion").push().setValue(suggestion).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "의견을 보내주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "의견 보내기에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    void setVerification() {
        recive_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches()) {
                    avaliableEamil = false;
                    email_error.setVisibility(View.VISIBLE);
                    email_error.setText("이메일 형식을 지켜주세요");
                } else {
                    avaliableEamil = true;
                    email_error.setVisibility(View.GONE);
                }

                if (avaliableEamil && avaliableContent) {
                    sendReport.setTextColor(getContext().getResources().getColor(R.color.skyblue));
                    sendReport.setEnabled(true);
                }else{
                    sendReport.setTextColor(getContext().getResources().getColor(R.color.gray));
                    sendReport.setEnabled(false);
                }
            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < MiN_CONTENT) {
                    avaliableContent = false;
                    content_error.setVisibility(View.VISIBLE);
                    content_error.setText(charSequence.length() + "/" + MiN_CONTENT + "자 이상 입력해 주세요");
                } else {
                    avaliableContent = true;
                    content_error.setVisibility(View.GONE);
                }

                if (avaliableEamil && avaliableContent) {
                    sendReport.setTextColor(getContext().getResources().getColor(R.color.skyblue));
                    sendReport.setEnabled(true);
                }else {
                    sendReport.setTextColor(getContext().getResources().getColor(R.color.gray));
                    sendReport.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void validate(String text) throws Exception {
        if (text == null) {
            throw new Exception("빈칸을 채워주세요");
        }
    }


}