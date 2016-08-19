package com.bignerdranch.android.networkingarchitecture.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.bignerdranch.android.networkingarchitecture.VenueApplication;
import com.bignerdranch.android.networkingarchitecture.helper.FoursquareOauthUriHelper;
import com.bignerdranch.android.networkingarchitecture.model.TokenStore;
import com.bignerdranch.android.networkingarchitecture.web.DataManager;

import javax.inject.Inject;

public class AuthenticationActivity extends AppCompatActivity {
    private WebView mWebView;
    @Inject
     DataManager mDataManager;

    public static Intent newIntent(Context context) {
        return new Intent(context, AuthenticationActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mWebView = new WebView(this);
        setContentView(mWebView);
        VenueApplication.component(this).inject(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(mWebViewClient);


        mWebView.loadUrl(mDataManager.getAuthenticationUrl());
    }
    private WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url){
            if(url.contains(DataManager.OAUTH_REDIRECT_URI)){
                FoursquareOauthUriHelper uriHelper =
                    new FoursquareOauthUriHelper(url);
                if (uriHelper.isAuthorized()){
                    String accessToken = uriHelper.getAccessToken();
                    TokenStore tokenStore =
                        TokenStore.get(AuthenticationActivity.this);
                    tokenStore.setAccessToken(accessToken);
                }
                finish();
            }

            return  false;
        }
    };
}
