package com.read.scriptures.ui.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.read.scriptures.R;

/**
 * Created with Android Studio.
 * User : Lim
 * Email: lgmshare@gmail.com
 * Datetime : 2015/4/28 16:00
 * To change this template use File | Settings | File Templates.
 */
public class WebViewFragment extends BaseFragment {

    private WebView mWebView;
    private boolean mLoadingSuccess = true;

    private String mLoadUrl = "http://www.baidu.com";
    private boolean isOpen = false;

    public static WebViewFragment newInstance(String url, boolean isOpen) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putBoolean("open", isOpen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int onObtainLayoutResId() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void initWidget() {
        mLoadUrl = getArguments().getString("url");
        isOpen = getArguments().getBoolean("open");

        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isOpen){
                    isOpen = false;
                    view.loadUrl(url);
                    mWebView.setWebViewClient(null);
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mLoadingSuccess = false;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (mLoadingSuccess) {

                }
            }

        });
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) { // 表示按返回键																					// 时的操作
                        mWebView.goBack(); // 后退
                        return true; // 已处理
                    }
                }
                return false;
            }
        });

        /*缩放页面方式1.
        mWebView.setInitialScale(55);
        */
        /*缩放页面方式2. 会出现放大缩小的按钮
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        */
        /*缩小页面方式3.
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        */
        /*缩放页面方式4.*/
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        /*缩放页面方式5. 会将页面元素在一列中显示出来
        webSettings.LayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); */
        mWebView.loadUrl(mLoadUrl);
    }

    @Override
    protected void lazyLoad() {

    }
}
