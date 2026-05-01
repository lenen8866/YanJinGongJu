package com.read.scriptures.ui.activity.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.music.player.lib.util.XToast;
import com.read.scriptures.net.NetMonitor;
import com.read.scriptures.net.NetObserver;
import com.read.scriptures.util.VibrateHelp;
import com.read.scriptures.util.analytics.AnalyticsUtil;
import com.read.scriptures.widget.LoadingProgressDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lim
 * @ClassName: BaseActivity
 * @Description: 基础activity
 * @mail lgmshare@gmail.com
 * @date 2014-6-3 上午10:14:20
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    /* 加载等待框 */
    protected LoadingProgressDialog mLoadingDialog;
    protected InputMethodManager mInputMethodManager;
    protected Activity ATHIS;


    private IntentFilter filter;
    private BroadcastReceiver receiver;
    protected LocalBroadcastManager broadcastManager;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        filter = new ExitIntentFilter();
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        broadcastManager.registerReceiver(receiver, filter);
        //网络状态监听
        NetMonitor.getInstance().addObserver(this.mNetObserver);

        ATHIS = this;
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initActionBar();
    }

    public void showToastMsg(String var1) {
        showToast(var1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsUtil.onResume(this);
        MobclickAgent.onPageStart(getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AnalyticsUtil.onPause(this);
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(getClass().getName());
    }

    @Override
    protected void onDestroy() {
        //销毁网络监听
        NetMonitor.getInstance().delObserver(mNetObserver);
        broadcastManager.unregisterReceiver(receiver);
        mNetObserver = null;
        super.onDestroy();
    }

    public void exit() {
        MobclickAgent.onKillProcess(this);
        //退出并注销IM
        broadcastManager.sendBroadcast(new ExitIntent());
//        System.exit(0);
    }

    /**
     * 显示加载框
     *
     * @param msg
     */
    public void showProgressDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingProgressDialog(this, msg);
            mLoadingDialog.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        try {
                            dismissProgressDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            });
        }
        if (!mLoadingDialog.isShowing() && !isFinishing() && !isDestroyed()) {
            mLoadingDialog.setText(msg);
            mLoadingDialog.show();
        }
    }


    /**
     * 隐藏加载等待框
     */
    public void dismissProgressDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 监控网络状况
     */
    private NetObserver mNetObserver = new NetObserver() {
        @Override
        public void notify(NetAction action) {
            netStatusChange(action);
        }
    };

    /**
     * 网络状态改变
     */
    public void netStatusChange(NetObserver.NetAction action) {

    }

    @Override
    public void onClick(View view) {
        VibrateHelp.vSimple(view.getContext(), 300);
    }

    protected void showToast(String str) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (!TextUtils.isEmpty(str)) {
            XToast.showToast(this, str);
        }
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏键盘
     */
    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 临时存放变量
     */
    private Map<String, Object> activityValue = new HashMap<>();


    public Map<String, Object> getActivityValue() {
        return activityValue;
    }


    protected boolean isHideActionbar() {
        return false;
    }

    /* 页面头部 */
    protected ActionBar mActionBar;

    private void initActionBar() {
        mActionBar = getActionBar();
        if (mActionBar != null) {
            // 隐藏Home图标和Title文字
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayUseLogoEnabled(false);
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setHomeButtonEnabled(false);
            // 隐藏Label标签
            mActionBar.setDisplayShowTitleEnabled(false);
            // 对ActionBar启用自定义View
            mActionBar.setDisplayShowCustomEnabled(true);
            if (isHideActionbar()) {
                mActionBar.hide();
            }
        }
    }

}
