package com.read.scriptures.ui.fragment;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.read.scriptures.R;


public class AboutUsFragment extends BaseFragment {

    private WebView mWebView;

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initWidget() {
        mWebView = findViewById1(R.id.webview);
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
//        settings.setTextZoom(250);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        // 此方法可以处理mWebView 在加载时和加载完成时一些操作
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https://") || url.startsWith("http://")) {
                    view.loadUrl(url);
                    return false;
                }
                return true;
            }

        });
        mWebView.loadUrl("https://book.sdacn.cn/wenda/jiankuan-guanyuwm.html");
    }

    @Override
    protected int onObtainLayoutResId() {
        return R.layout.ft_center;
    }
}
