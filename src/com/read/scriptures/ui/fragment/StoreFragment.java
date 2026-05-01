package com.read.scriptures.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.music.player.lib.util.NetUtil;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.util.SystemUtils;

public class StoreFragment extends Base1Fragment {
    @Override
    public int onObtainLayoutResId() {
        return R.layout.ft_store;
    }

    @Override
    public void lazyLoad() {

    }

    private WebView mWebView;
    private LinearLayout ll_retry;
    private TextView tv_title;
    private SwipeRefreshLayout srl_refresh;

    @Override
    public void initWidget() {
        mWebView = findViewById1(R.id.wb_content);
        ll_retry = findViewById1(R.id.ll_retry);
        tv_title = findViewById1(R.id.tv_title);
        srl_refresh = findViewById1(R.id.srl_refresh);
        srl_refresh.setEnabled(false);
        initWebView();
        ll_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isNetWorkAvailable(mContext)) {
                    srl_refresh.setRefreshing(true);
                    srl_refresh.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_title.setText("对不起，本界面需要链接网络...");
                            srl_refresh.setRefreshing(false);
                        }
                    },1000);
                } else {
                    ll_retry.setVisibility(View.GONE);
                    mWebView.reload();
                    srl_refresh.setRefreshing(true);
                }
            }
        });
    }

    private void initWebView() {
        try {
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setSaveFormData(false);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSettings.setDefaultTextEncodingName("utf-8");
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setSupportZoom(false);
            webSettings.setDomStorageEnabled(true);//开启DOM
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setBlockNetworkImage(false);//解决图片不显示
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.clearSslPreferences();
            mWebView.setWebViewClient(new StoreFragment.MyWebViewClient());
            StoreFragment.MyWebChromeClient myWebChromeClient = new StoreFragment.MyWebChromeClient();
            mWebView.setWebChromeClient(myWebChromeClient);
            String url = "https://book.sdacn.cn/h5?token="+ AccountManager.getInstance().getUserInfo().getToken() + "&version=" + SystemUtils.getVersionName(HuDongApplication.getInstance());
            mWebView.loadUrl(url);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    class MyWebViewClient extends WebViewClient {
        private MyWebViewClient() {
            super();
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (super.shouldOverrideUrlLoading(webView, url)) {
                return true;
            }
            WebView.HitTestResult hit = webView.getHitTestResult();

            //这里执行自定义的操作

            if (hit != null) {
                int hitType = hit.getType();
                if (hitType == WebView.HitTestResult.SRC_ANCHOR_TYPE) {//点击超链接
                    webView.loadUrl(url);
                    return true;//返回true浏览器不再执行默认的操作
                } else if (hitType == 0) {//重定向时hitType为0
                    return false;//不捕获302重定向
                } else {
                    return false;
                }
            }

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            srl_refresh.setRefreshing(true);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            /*当加载错误时，加载一个自己定义的网址*/
            ll_retry.setVisibility(View.VISIBLE);
            if (!NetUtil.isNetWorkAvailable(mContext)) {
                tv_title.setText("对不起，本界面需要链接网络...");
            } else {
                tv_title.setText("对不起，加载错误...");
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            srl_refresh.setRefreshing(false);
        }
    }

    @JavascriptInterface
    public void getItem() {

    }


    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;


    final class MyWebChromeClient extends WebChromeClient {
        private MyWebChromeClient() {
            super();
        }

        @Override
        public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
            jsResult.confirm();
            return super.onJsAlert(webView, s, s1, jsResult);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }
    }

    private void openImageChooserActivity() {
        //调用自己的图库
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
