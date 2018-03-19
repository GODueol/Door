package com.example.kwoncheolhyeok.core.SettingActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Kwon on 2018-01-04.
 */

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar = null;

    RelativeLayout set_password = null;
    RelativeLayout new_password_layout = null;

    EditText currentPw;
    EditText pw;
    EditText pwConfirm;
    TextView forgotPw;
    TextView changePw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        set_password = findViewById(R.id.layout1);
        new_password_layout = findViewById(R.id.layout2);
        currentPw = findViewById(R.id.cur_pw);
        pw = findViewById(R.id.new_pw);
        pwConfirm = findViewById(R.id.re_pw);
        forgotPw = findViewById(R.id.forgot_pw);
        changePw = findViewById(R.id.change_pw);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);
        forgotPw.setOnClickListener(this);
        changePw.setOnClickListener(this);
        new_password_layout.setVisibility(View.GONE);
        set_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new_password_layout.getVisibility() == View.VISIBLE) {
                    new_password_layout.setVisibility(View.GONE);
                } else {
                    new_password_layout.setVisibility(View.VISIBLE);
                }
            }
        });


    }


    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.change_pw:
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userenail = DataContainer.getInstance().getUser().getEmail();
                UiUtil.getInstance().startProgressDialog(this);

                // 비밀번호 변경 검증
                try {
                    validate();
                } catch (Exception e) {
                    UiUtil.getInstance().stopProgressDialog();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthCredential credential = EmailAuthProvider
                        .getCredential(userenail, String.valueOf(currentPw.getText()));

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //패스워드 인증 성공
                                String newPassword = String.valueOf(pw.getText());

                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "패스워드가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "패스워드 변경에 실패하였습니다. ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                UiUtil.getInstance().stopProgressDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        focusEditText(currentPw);
                        Toast.makeText(getApplicationContext(), "현재 비밀번호를 다시 확인해주세요. ", Toast.LENGTH_SHORT).show();
                        UiUtil.getInstance().stopProgressDialog();
                    }
                });
                break;
            case R.id.forgot_pw:
                final AutoCompleteTextView email = new AutoCompleteTextView(this);
                email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(email)
                        .setTitle("이메일을 쓰세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String emailAddress = email.getText().toString();

                                if (!emailAddress.equals("")) {
                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    auth.sendPasswordResetEmail(emailAddress)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "이메일 전송 완료", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "이메일 주소가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                } else {
                                    Toast.makeText(getApplicationContext(), "이메일을 쓰세요", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).show();
                break;
        }
    }

    public void validate() throws Exception {
        String currentPassword = currentPw.getText().toString();
        String password = pw.getText().toString();
        String reEnterPassword = pwConfirm.getText().toString();

        if (currentPassword.length() < 6 || currentPassword.isEmpty()) {
            focusEditText(currentPw);
            throw new Exception("비밀번호를 올바르게 입력해 주세요.");
        } else if (password.isEmpty() || password.length() < 6 || password.length() > 12) {
            focusEditText(pwConfirm);
            focusEditText(pw);
            throw new Exception("비밀번호는 6자리 이상 12자리 이하로 설정해주세요.");
        } else if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || reEnterPassword.length() > 12 || !(reEnterPassword.equals(password))) {
            focusEditText(pwConfirm);
            focusEditText(pw);

            throw new Exception("패스워드가 일치하지 않습니다.");
        }
    }

    public void focusEditText(EditText editText) {
        editText.setText("");
        editText.requestFocus();
    }
}