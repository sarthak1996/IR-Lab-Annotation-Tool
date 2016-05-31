package com.example.sarthak.ir_annotation_tool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by sarthak on 25/5/16.
 */
public class CustomWebView extends WebViewClient {
    private Context context;
    private String url;
    public CustomWebView(Context context,String url) {
        this.context = context;
        this.url=url;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(this.url));
        context.startActivity(i);
        return true;
    }
}