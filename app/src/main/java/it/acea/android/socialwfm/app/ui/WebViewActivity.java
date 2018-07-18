package it.acea.android.socialwfm.app.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.fingerlinks.mobile.android.navigator.utils.Constant;
import org.fingerlinks.mobile.android.utils.activity.BaseActivity;

import it.acea.android.socialwfm.R;

/**
 * Created by fabio on 02/09/14.
 */
public class WebViewActivity extends BaseActivity {

    WebView webview = null;
    View web_view_loader;

    @Override
    public int getLayoutResource() {
        return R.layout.activity_web_view;
    }

    @Override
    protected int getToolbarProjectId() {
        return R.id.toolbar_above;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webview = (WebView) findViewById(R.id.webview);
        web_view_loader = findViewById(R.id.web_view_loader);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        String url = getIntent().getBundleExtra(Constant.BUNDLE).getString("url_to_load");
        webview.loadUrl(url);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                web_view_loader.setVisibility(View.GONE);
                setSubtitle(view.getTitle());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                web_view_loader.setVisibility(View.VISIBLE);
                setSubtitle("");
            }
        });

        setNavigationIcon(R.drawable.ic_ab_back_white);
        setIcon(R.drawable.acea_logo_toolbar);
        setSubtitle("");
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void setNavigationOnClickListener() {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
            return;
        }
        finish();
    }
};