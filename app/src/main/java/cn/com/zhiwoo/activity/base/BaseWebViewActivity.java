package cn.com.zhiwoo.activity.base;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import cn.com.zhiwoo.R;


public abstract class BaseWebViewActivity extends BaseActivity {
    public WebView webView;
    public ProgressBar progressBar;

    @Override
    public void initView() {
        super.initView();
        flContent.addView(View.inflate(this, R.layout.base_webview_activity, null));
        webView = (WebView) findViewById(R.id.article_detail_webview);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }
    @Override
    public void initData() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = view.getTitle();
                titleView.setText(title);
                progressBar.setVisibility(View.GONE);
                webViewLoadSuccess();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.setVisibility(View.GONE);
            }
        });
        //搞清楚这几个设置是什么意思,避免入坑
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {

    }

    public abstract void webViewLoadSuccess();
}
