package com.fithealthteam.fithealth.huawei.ui.community;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fithealthteam.fithealth.huawei.IOnBackPressed;
import com.fithealthteam.fithealth.huawei.R;

public class CommunityFragment extends Fragment implements IOnBackPressed {

    private CommunityViewModel mViewModel;
    private WebView webViewCommunity;

    public static CommunityFragment newInstance() {
        return new CommunityFragment();
    }

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

        return v;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public boolean onBackPressed() {
        if (webViewCommunity.canGoBack()) {
            //action not popBackStack
            webViewCommunity.goBack();
            return true;
        } else {
            return false;
        }
    }

}