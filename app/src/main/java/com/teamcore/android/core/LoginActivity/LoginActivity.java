package com.teamcore.android.core.LoginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.teamcore.android.core.Activity.MainActivity;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.UiUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

//    AnimationDrawable animationDrawable;
//    FrameLayout frameLayout;

    // auth
    private FirebaseAuth mAuth;

    // database
    DatabaseReference userRef = DataContainer.getInstance().getUsersRef();

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    TextView _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;
    @Bind(R.id.cb_save_id)
    CheckBox cb_save_id;
    @Bind(R.id.link_find_password)
    ImageView link_find_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        //statusbar 투명하게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        ImageView r = (ImageView)findViewById(R.id.milkyway);
        Glide.with(this)
                .load(UiUtil.resourceToUri(this, R.drawable.milkyway))
                .into(r);

        ImageView cor2 = (ImageView) findViewById(R.id.CORE_LOGO);
        Glide.with(this)
                .load(UiUtil.resourceToUri(this, R.drawable.login_core_ani))
                .into(cor2);



        final SharedPreferences pref = getSharedPreferences(DataContainer.getInstance().PREFERENCE, MODE_PRIVATE);
        String emailPref = pref.getString("email", "");
        if (!emailPref.equals("")) {
            cb_save_id.setChecked(true);
            _emailText.setText(emailPref);
        } else {
            cb_save_id.setChecked(false);
        }
        cb_save_id.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("email", "");
                editor.apply();
            }
        });

        _loginButton.setOnClickListener(v -> {
            String email = _emailText.getText().toString();

            if (cb_save_id.isChecked()) {
                // ID 저장 : Shared Preference
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("email", _emailText.getText().toString());
                editor.apply();

            }

            login();
        });

        _signupLink.setOnClickListener(v -> {
            // Start the Signup activity
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);

            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

        mAuth = FirebaseAuth.getInstance();

        link_find_password.setOnClickListener(view -> {
            final AutoCompleteTextView email = new AutoCompleteTextView(LoginActivity.this);
            email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);


            TextView title = new TextView(LoginActivity.this);
            title.setText("[비밀번호 찾기]\n\n가입하신 메일을 알려주세요.\n비밀번호 변경 페이지를 메일로 보내드립니다.");
            title.setGravity(Gravity.CENTER);
            title.setPadding(0,90,0,40);
            title.setTextSize(15);

            builder.setView(email)
                    .setCustomTitle(title)
                    .setPositiveButton("확인", (dialog, which) -> {
                        String emailAddress = email.getText().toString();

                        if(!emailAddress.equals("")){
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            auth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            Toast.makeText(LoginActivity.this, "메일 전송 완료", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "메일 주소가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                                        }

                                    });
                        } else {
                            Toast.makeText(LoginActivity.this, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        }

                    }).show();
        });
    }


    public void login() {
        Log.d(TAG, "Login Start");


        try {
            validate();
        } catch (Exception e) {
            onLoginFailed(e);
            return;
        }

        _loginButton.setEnabled(false);

        UiUtil.getInstance().startProgressDialog(this);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // firebase login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        getUserInfo(user);

                    } else {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        onLoginFailed(task.getException());
                    }

                });

    }

    private void getUserInfo(final FirebaseUser user) {
        if (user != null) {

            UiUtil.getInstance().startProgressDialog(this);

            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

            // user 정보 읽어오기
            userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User mUser = dataSnapshot.getValue(User.class);
                    DataContainer.getInstance().setUser(mUser);
                    onLoginSuccess();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(getApplication().getClass().getName(), databaseError.getMessage());

                }
            });

        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent i = new Intent(LoginActivity.this, MainActivity.class);

        // 프로그레스 바 종료
        UiUtil.getInstance().stopProgressDialog();
        finish();
        startActivity(i);


    }

    public void onLoginFailed(Exception e) {
        _loginButton.setEnabled(true);
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        UiUtil.getInstance().stopProgressDialog();
    }

    public void validate() throws Exception {

        final int PASSWORD_MIN = 6;
        final int PASSWORD_MAX = 12;
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty())
            throw new Exception("이메일을 입력해주세요.");
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            throw new Exception("이메일이 틀렸습니다.");
        if (password.isEmpty())
            throw new Exception("비밀번호를 입력해주세요.");
        if ( password.length() < PASSWORD_MIN || password.length() > PASSWORD_MAX)
            throw new Exception("비밀번호는 " + PASSWORD_MIN + "자에서 " + PASSWORD_MAX + "자 사이로 입력해주세요");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
