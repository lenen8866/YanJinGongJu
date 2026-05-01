package com.read.scriptures.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.StatusBarUtils;


public class WebViewActivity extends BaseActivity {
    private TextView tvTitle;
    private WebView mWebView;
    private String mTitle;
    private String mUrlString;
    private ImageView ivLeft;

    public static void launchAct(Activity activity, String title, String url) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        StatusBarUtils.initMainColorStatusBar(this);
        initExtra();
        initViews();
        initWebView();
    }

    private void initExtra() {
        mTitle = getIntent().getExtras().getString("title");
        mUrlString = getIntent().getExtras().getString("url");
    }


    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        ivLeft = findViewById(R.id.iv_left);
        tvTitle.setText(mTitle);
        mWebView = findViewById(R.id.wb_content);
        registerWebViewJsHandlers();
    }

    private void initWebView() {
        if (mWebView != null && ((mUrlString != null && mUrlString.length() > 0))) {
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
                mWebView.setWebViewClient(new MyWebViewClient());
                MyWebChromeClient myWebChromeClient = new MyWebChromeClient();
                mWebView.setWebChromeClient(myWebChromeClient);

                loadUrlStr(mUrlString);

            } catch (Throwable e) {
                e.printStackTrace();
            }


        }
    }

    private void loadUrlStr(String urlStr) {
        if (mWebView != null) {
            mWebView.loadUrl(urlStr);
        } else {
            finish();
        }
    }

    private void registerWebViewJsHandlers() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput();
                goBack();
            }
        });
    }

    public void goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
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
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
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
            if (TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }

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
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) { //处理返回的图片，并进行上传
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }


    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
