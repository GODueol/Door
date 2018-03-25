package com.example.kwoncheolhyeok.core.CorePage.CoreCloudPayDialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.kwoncheolhyeok.core.R;

/**
 * Created by Administrator on 2018/3/14.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    protected View parentView;
    protected LayoutInflater inflater;
    protected Context mContext;
    private FragmentActivity activity;

    private Button closebtn;

    protected abstract void attachView();

    protected abstract int getLayoutResId();

    protected abstract void initView(View layout);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), null, false);
        final Window window = getDialog().getWindow();

        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setWindowAnimations(R.style.AnimBottom);
        window.setAttributes(wlp);

        initView(view);
        return view;
    }


}
