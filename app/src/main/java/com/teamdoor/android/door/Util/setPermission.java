package com.teamdoor.android.door.Util;

import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

/**
 * Created by juyeol on 2017. 6. 27
 * TedPermmsion을 이용한 권한 획득
 * 사용법 setPermission.getmInstance(context , 권한 후 리스너(PermissionListener , 권한 String, 권한 String...);
 * ex) setPermission.getmInstance(getContext() ,GoogleMapPermission ,Manifest.permission.ACCESS_FINE_LOCATION);
 * 리스너는 각자 구현해줘야함. (맨마지막은 가변인자를 사용해서 1개이상의 퍼미션을 한번에 받을수 있게 변경)
 */

public class setPermission {

    Context mContext;

    public setPermission(Context context, PermissionListener permissionListener, String... permission) {
        mContext = context;
        String message;
        // 퍼미션에 따른 에러메세지 설정
        message = "필수 권한 거부 시 코어를 사용할 수 없습니다\n\n설정 방법 [설정] > [권한]";

        /*      switch (permission) {
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    message = "위치서비스권한을 거부하시면 사용자기반 위치서비스를 사용수없습니다\n\n설정방법 [설정] > [권한]";
                    break;
                default:
                    message = "권한을 거부하시면 특정 서비스를 사용수없습니다\n\n설정방법 [설정] > [권한]";
                    break;
            }*/

        new TedPermission(mContext)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(message)
                .setPermissions(permission)
                .check();

    }
}
