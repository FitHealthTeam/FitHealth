package com.fithealthteam.fithealth.huawei.ui.community;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fithealthteam.fithealth.huawei.R;

public class CommunityFragment extends Fragment {

    private CommunityViewModel mViewModel;
    private WebView webViewCommunity;

    public static CommunityFragment newInstance() {
        return new CommunityFragment();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.community_fragment, container, false);

        webViewCommunity = v.findViewById(R.id.communityWebView);
        WebSettings webViewCommunitySettings = webViewCommunity.getSettings();
        webViewCommunitySettings.setJavaScriptEnabled(true);
        webViewCommunitySettings.setDomStorageEnabled(true);
        webViewCommunitySettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webViewCommunity.loadUrl("https://fithealth.dra.agchosting.link/index.html");

        webViewCommunity.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });

        //listen for user to press back button
        webViewCommunity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webViewCommunity.canGoBack()) {
                    //send handler to invoke go back
                    handler.sendEmptyMessage(1);
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        // TODO: Use the ViewModel
    }

    private void webViewGoBack(){
        webViewCommunity.goBack();
    }

}