package com.teamcore.android.core.CorePage.CoreCloudPayDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.UiUtil;

import static com.teamcore.android.core.Util.DataContainer.SecToDay;

public abstract class BaseFragment extends Fragment {

    protected View parentView;
    protected FragmentActivity activity;
    protected LayoutInflater inflater;
    protected Context mContext;
    ViewPager viewPager;

    BaseFragment(ViewPager viewPager){
        this.viewPager = viewPager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {

        parentView = inflater.inflate(getLayoutResId(), container, false);
        mContext = activity;
        this.inflater = inflater;

        parentView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return parentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Button next = view.findViewById(R.id.next);
//        if(next != null) next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
//            }
//        });

    }

    @LayoutRes
    public abstract int getLayoutResId();

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }


    public boolean isPostPossible(long oldestPostDate) {

        if(oldestPostDate == Long.MAX_VALUE) return true;

        // diff
        long diff = 0;
        try {
            diff = UiUtil.getInstance().getCurrentTime(getContext()) - oldestPostDate;
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity(getActivity());
        }

        // 해당 포스트 삭제 후에 가능
        return diff >= SecToDay;
    }
}
