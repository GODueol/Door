package com.teamdoor.android.door.SettingActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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
import com.teamdoor.android.door.LoginActivity.LoginActivity;
import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.BaseActivity.BaseActivity;
import com.teamdoor.android.door.Util.UiUtil;

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
        email.setText(getUser().getEmail());
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

        final EditText password = new EditText(AccountActivity.this);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setSelection(password.getText().length());
        password.setLines(2);

        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        TextView title = new TextView(AccountActivity.this);
        title.setText("[계정 삭제]\n\n계정을 삭제하시려면 비밀번호를 입력해주세요.");
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,90,0,40);
        title.setTextSize(15);

        builder.setView(password)
                .setCustomTitle(title)
                .setPositiveButton("확인", (dialog, which) -> {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // Get auth credentials from the user for re-authentication. The example below shows
                    // email and password credentials but there are multiple possible providers,
                    // such as GoogleAuthProvider or FacebookAuthProvider.
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(getUser().getEmail(), password.getText().toString());

                    startProgressDialog();
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(voidTask -> {

                                        if (voidTask.isSuccessful()) {
                                            deleteMyIdentifier();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent homeIntent = new Intent(this, LoginActivity.class);
                                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(homeIntent);
                                        } else {
                                            Toast.makeText(this, voidTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d("KBJ", voidTask.getException().getMessage());
                                        }
                                        stopProgressDialog();

                                    });
                                } else {
                                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    stopProgressDialog();
                                }
                            });
                }).show();
    }

    private void checkPostPrevent(Runnable runnable) {
        UiUtil.getInstance().checkPostPrevent(this, (isRelease, releaseDate) -> {
            if (isRelease) {
                runnable.run();
            } else {
                Toast.makeText(AccountActivity.this,
                        "현재 제재 대상자이므로 " +
                                releaseDate + " 까지 계정삭제가 불가능합니다."
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserPrevent(Runnable runnable) {
        UiUtil.getInstance().checkUserPrevent(this, (isRelease, releaseDate) -> {
            if (isRelease) {
                runnable.run();
            } else {
                Toast.makeText(AccountActivity.this,
                        "현재 제재 대상자이므로 " +
                                releaseDate + " 까지 계정삭제가 불가능합니다."
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
                String userenail = getUser().getEmail();
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
                                            Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "비밀번호 변경에 실패하였습니다", Toast.LENGTH_SHORT).show());
                            UiUtil.getInstance().stopProgressDialog();
                        }).addOnFailureListener(e -> {
                            focusEditText(currentPw);
                            Toast.makeText(getApplicationContext(), "현재 비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
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
            throw new Exception("비밀번호는 6자리 이상 12자리 이하로 설정해주세요.");
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