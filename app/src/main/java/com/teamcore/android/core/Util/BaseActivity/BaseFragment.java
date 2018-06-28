package com.teamcore.android.core.Util.BaseActivity;


import com.google.android.gms.tasks.Task;

public class BaseFragment extends android.support.v4.app.Fragment {

    // 구독 결제 확인
    public Task<Boolean> checkCorePlus(BaseActivity baseActivity){
        return baseActivity.checkCorePlus();
    }

}
