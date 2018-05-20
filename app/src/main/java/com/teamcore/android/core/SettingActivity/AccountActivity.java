package com.teamcore.android.core.SettingActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.UiUtil;

/**
 * Created by Kwon on 2018-01-04.
 */

public class AccountActivity extends BaseActivity implements View.OnClickListener {

    Toolbar toolbar = null;

    RelativeLayout set_password = null;
    RelativeLayout new_password_layout = null;

    EditText currentPw;
    EditText pw;
    EditText pwConfirm;
    TextView forgotPw;
    TextView changePw;
    TextView email;
    RelativeLayout deleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account_activity);

        email = (TextView) findViewById(R.id.email);
        email.setText(DataContainer.getInstance().getUser().getEmail());
        email.setTypeface(null, Typeface.BOLD);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        set_password = (RelativeLayout) findViewById(R.id.layout1);
        new_password_layout = (RelativeLayout) findViewById(R.id.layout2);
        currentPw = (EditText) findViewById(R.id.cur_pw);
        pw = (EditText) findViewById(R.id.new_pw);
        pwConfirm = (EditText) findViewById(R.id.re_pw);
        forgotPw = (TextView) findViewById(R.id.forgot_pw);
        changePw = (TextView) findViewById(R.id.change_pw);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        forgotPw.setOnClickListener(this);
        changePw.setOnClickListener(this);
        new_password_layout.setVisibility(View.GONE);
        set_password.setOnClickListener(view -> {
            if (new_password_layout.getVisibility() == View.VISIBLE) {
                new_password_layout.setVisibility(View.GONE);
            } else {
                new_password_layout.setVisibility(View.VISIBLE);
            }
        });

        deleteAccount = (RelativeLayout) findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener((View v) ->
            checkUserPrevent(() -> checkPostPrevent(this::deleteAccount))
        );
    }

    private void deleteAccount() {
        UiUtil.getInstance().showDialog(this, "계정 삭제", "아직은 채팅 이미지파일 삭제 기능이 개발되지 않았습니다!!! 정말 계정을 삭제 하시겠습니까?", (dialog, whichButton) -> deleteMyAccount(DataContainer.getInstance().getUid()), null);
    }

    private void checkPostPrevent(Runnable runnable) {
        UiUtil.getInstance().checkPostPrevent(this, (isRelease, releaseDate) -> {
            if (isRelease) {
                runnable.run();
            } else {
                Toast.makeText(AccountActivity.this,
                        "포스트 제재 당하셨기 때문에 " +
                                releaseDate + " 까지 계정삭제 불가능합니다"
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserPrevent(Runnable runnable) {
        UiUtil.getInstance().checkUserPrevent(this, (isRelease, releaseDate) -> {
            if (isRelease) {
                runnable.run();
            } else {
                Toast.makeText(AccountActivity.this,
                        "프로필 제재 당하셨기 때문에 " +
                                releaseDate + " 까지 계정삭제 불가능합니다"
                        , Toast.LENGTH_SHORT).show();
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
                        .addOnCompleteListener(task -> {
                            //패스워드 인증 성공
                            String newPassword = String.valueOf(pw.getText());

                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            currentPw.setText(null);
                                            pw.setText(null);
                                            pwConfirm.setText(null);
                                            new_password_layout.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "비밀번호 변경에 실패하였습니다. ", Toast.LENGTH_SHORT).show());
                            UiUtil.getInstance().stopProgressDialog();
                        }).addOnFailureListener(e -> {
                            focusEditText(currentPw);
                            Toast.makeText(getApplicationContext(), "현재 비밀번호를 다시 확인해주세요. ", Toast.LENGTH_SHORT).show();
                            UiUtil.getInstance().stopProgressDialog();
                        });
                break;

            case R.id.forgot_pw:
                final AutoCompleteTextView email = new AutoCompleteTextView(this);
                email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                TextView title = new TextView(this);
                title.setText("[비밀번호 찾기]\n\n가입하신 메일을 알려주세요.\n비밀번호 변경 페이지를 메일로 보내드립니다.");
                title.setGravity(Gravity.CENTER);
                title.setPadding(0,90,0,40);
                title.setTextSize(15);

                builder.setView(email)
                        .setCustomTitle(title)
                        .setPositiveButton("확인", (dialog, which) -> {
                            String emailAddress = email.getText().toString();

                            if (!emailAddress.equals("")) {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.sendPasswordResetEmail(emailAddress)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "메일 전송 완료", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "메일 주소가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                                            }

                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요", Toast.LENGTH_SHORT).show();
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

            throw new Exception("비밀번호가 일치하지 않습니다.");
        }
    }

    public void focusEditText(EditText editText) {
        editText.setText("");
        editText.requestFocus();
    }
}