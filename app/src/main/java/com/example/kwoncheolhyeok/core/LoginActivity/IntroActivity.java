package com.example.kwoncheolhyeok.core.LoginActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Activity.MainActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.setPermission;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;

import java.util.ArrayList;


/**
 * Created by Kwon on 2017-09-27.
 */

public class IntroActivity extends Activity {
    /** Called when the activity is first created. */

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        new setPermission(getApplicationContext(), GPSPermission, Manifest.permission.ACCESS_FINE_LOCATION); // 권한요청 및 권한에따른 구글맵 셋팅});

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

    }

    private void getUserInfo(FirebaseUser user) {
        if (user != null) {

//            CoreProgress.getInstance().startProgressDialog(this);

            // User is signed in
            Log.d(getApplication().getClass().getName(), "onAuthStateChanged:signed_in:" + user.getUid());

            // user 정보 읽어오기
            DatabaseReference userRef = database.getReference("users");
            String uuid = user.getUid();
            userRef.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    DataContainer.getInstance().setUser(user);
                    onLoginSuccess();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Getting UserInfo Cancelled",Toast.LENGTH_SHORT).show();
                    Log.d(getApplication().getClass().getName(),databaseError.getMessage());

                }

            });


        } else {
            // User is signed out
            Log.d(getApplication().getClass().getName(), "onAuthStateChanged:signed_out");

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
                    finish();
                }
            }, 1800);

        }
    }

    public void onLoginSuccess() {
        Intent i = new Intent(this, MainActivity.class);
        startActivityForResult(i, 0);
    }

    /******************구글맵 메소드(+권한)**********************/
    PermissionListener GPSPermission = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            getUserInfo(currentUser);
            Toast.makeText(getApplication(), "위치 권한가져옴",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            new setPermission(getApplicationContext(), GPSPermission, Manifest.permission.ACCESS_FINE_LOCATION); // 권한요청 및 권한에따른 구글맵 셋팅});
            Toast.makeText(getApplication(), "권한이 없으면 앱을 실행할 수 없습니다.",Toast.LENGTH_SHORT).show();
        }
    };

    /******************구글맵 메소드(+권한) 끝**********************/

}

