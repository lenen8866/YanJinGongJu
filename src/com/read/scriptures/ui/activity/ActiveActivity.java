package com.read.scriptures.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.net.NetObserver;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.fragment.FragmentActive;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.widget.CustomAlertDialog;

public class ActiveActivity extends BaseActivity {
    private FragmentActive mFragmentActive;
    private ImageView iv_left;
    private TextView tv_right;
    protected InputMethodManager mInputMethodManager;
    private CustomAlertDialog mCommonAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        StatusBarUtils.initMainColorStatusBar(this);
        initViews();
    }

    private void initViews() {
        int vip_recharge_type = getIntent().getIntExtra("vip_recharge_type", -1);
        mFragmentActive = (FragmentActive) getSupportFragmentManager().findFragmentById(R.id.active_fragment);
        mFragmentActive.setVipRechargeType(vip_recharge_type);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_right = (TextView) findViewById(R.id.tv_right);

        tv_right.setVisibility(View.VISIBLE);
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                goWechatApplet();
                if (null == mCommonAlertDialog && !ActiveActivity.this.isDestroyed()) {
                    mCommonAlertDialog = new CustomAlertDialog(ActiveActivity.this, "-1");
                    mCommonAlertDialog.setTitle(getString(R.string.txt_quit_login));
                    mCommonAlertDialog.setMessage(getString(R.string.txt_are_you_sure_to_quit));
                    mCommonAlertDialog.setPositiveButton(R.string.ensure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCommonAlertDialog.dismiss();
                            AccountManager.getInstance().loginOut(false);
                            XToast.showToast(ActiveActivity.this, "退出成功!");
                            WeixinLoginActivity.launchAct(ActiveActivity.this);
                        }
                    });
                    mCommonAlertDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCommonAlertDialog.dismiss();
                        }
                    });
                }
                if (!ActiveActivity.this.isDestroyed() && !mCommonAlertDialog.isShowing()) {
                    mCommonAlertDialog.show();

                }

            }
        });

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected boolean isHideActionbar() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void netStatusChange(NetObserver.NetAction action) {
        super.netStatusChange(action);
        if (mFragmentActive != null) {
            mFragmentActive.netStatusChange(action);
        }
    }
}
