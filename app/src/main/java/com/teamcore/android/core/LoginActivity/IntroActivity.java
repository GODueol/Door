package com.teamcore.android.core.LoginActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.teamcore.android.core.Activity.MainActivity;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.SharedPreferencesUtil;
import com.teamcore.android.core.Util.UiUtil;
import com.teamcore.android.core.Util.setPermission;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kwon on 2017-09-27.
 */

public class IntroActivity extends BaseActivity {
    private static final int ACCESS_RIGHT_REQUEST = 0;
    /**
     * Called when the activity is first created.
     */
    private SharedPreferencesUtil SPUtil;

    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    boolean isHaveAllPermission() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void setPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (isHaveAllPermission()) {
                    checkCorePlus().done(isPlus -> getUserInfo(FirebaseAuth.getInstance().getCurrentUser())).fail(str -> Toast.makeText(IntroActivity.this, "구글 플레이 스토어 계정을 연결하고 다시 시도해주세요", Toast.LENGTH_SHORT).show());
                    finish();
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                //new setPermission(getApplicationContext(), this, permissions); // 권한요청 및 권한에따른 구글맵 셋팅});
                Toast.makeText(getApplication(), "권한이 없으면 앱을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                finish();   // 권한 거부시 앱 종료
            }
        };

        new setPermission(this, permissionListener, permissions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        // 시간 자동 설정 체크
        if(!UiUtil.getInstance().isAutoTimeSet(this)){
            Toast.makeText(this, "시간을 수동으로 설정한 경우 코어를 사용하실 수 없습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 권한 체크
        if (isHaveAllPermission()) {
            setPermission();
        } else {
            // 권한 다이얼로그 띄움
            startActivityForResult(new Intent(IntroActivity.this, AccessRightActiviry.class), ACCESS_RIGHT_REQUEST);
        }

        SPUtil = new SharedPreferencesUtil(this);
        //  광고 아이디 설정 (최초 1회)
        MobileAds.initialize(this,this.getString(R.string.adsID));
        SPUtil.initAds();
    }

    private void getUserInfo(final FirebaseUser user) {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        // wifi 또는 모바일 네트워크 어느 하나라도 연결이 되어있다면,
        if (!wifi.isConnected() && !mobile.isConnected()) {
            Log.i("Internet Connection", "인터넷 연결 안된 상태");
            Toast.makeText(getApplicationContext(), "인터넷 연결이 안되어 있습니다", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (user != null) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (user.getEmail() == null) {
                Log.d(getApplication().getClass().getName(), "계정없음:" + user.getUid());
                logout();
                return;
            }
            mAuth.fetchProvidersForEmail(user.getEmail()).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }

                List<String> provider = task.getResult().getProviders();
                if (provider == null || provider.isEmpty()) { // 계정이 없는 경우
                    Log.d(getApplication().getClass().getName(), "계정없음:" + user.getUid());
                    logout();
                    return;
                }

                // User is signed in
                Log.d(getApplication().getClass().getName(), "onAuthStateChanged:signed_in:" + user.getUid());

                // user 정보 읽어오기
                String uuid = user.getUid();
                DataContainer.getInstance().getUsersRef().child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User mUser = dataSnapshot.getValue(User.class);
                        DataContainer.getInstance().setUser(mUser);
                        onLoginSuccess();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Getting UserInfo Cancelled", Toast.LENGTH_SHORT).show();
                        Log.d(getApplication().getClass().getName(), databaseError.getMessage());
                    }

                });
            });
        } else {
            // User is signed out
            Log.d(getApplication().getClass().getName(), "onAuthStateChanged:signed_out");
            goToLoginActivity();
        }
    }

    private void logout() {
        SPUtil.initAds();
        FirebaseAuth.getInstance().signOut();
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
            finish();
        }, 1800);
    }

    public void onLoginSuccess() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        // 계정있으면 로그아웃
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            logout();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ACCESS_RIGHT_REQUEST) {
            setPermission();
        }
    }
}

