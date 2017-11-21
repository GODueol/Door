package com.example.kwoncheolhyeok.core.BoardActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.kwoncheolhyeok.core.Activity.MainActivity;
import com.example.kwoncheolhyeok.core.R;

public class TabFragment2 extends android.support.v4.app.Fragment {

    private WebView webView;
    private Bundle webViewBundle;

    @Nullable


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        webView = (WebView) view.findViewById(R.id.webView1);
        webView.setWebViewClient(new WebViewClient());

        if (webViewBundle == null) {
            webView.loadUrl("http://www.google.com");
        } else {
            webView.restoreState(webViewBundle);
        }

        //Fragment에서 webView 뒤로가기 버튼 구현
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //This is the filter
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        ((MainActivity)getActivity()).onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });

        return view;

    }

    @Override
    public void onPause() {
        super.onPause();

        webViewBundle = new Bundle();
        webView.saveState(webViewBundle);
    }



}



//        articleList = (ListView) view.findViewById(R.id.articleList);
//
//        List<String> list = new ArrayList<String>();
//        for(int i=0; i<50; i++){
//            list.add(i+"");
//        }
//
//        ArticleListAdapter articleListAdapter = new ArticleListAdapter(list, getActivity());
//        articleList.setAdapter(articleListAdapter);