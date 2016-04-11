package com.gatbhet.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gatbhet.R;
import com.gatbhet.config.WebServiceAsyncTask;
import com.gatbhet.services.WebServiceHelper;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new WebServiceAsyncTask(this).execute();

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient() { public boolean shouldOverrideUrlLoading(WebView view, String url) { view.loadUrl(url); return true; }});
        myWebView.loadUrl("http://dev.mulikainfotech.com/gathbhet.com/");

    }
}
