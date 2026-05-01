package com.read.scriptures.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.ActManager;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.widget.CustomAlertDialog;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WeixinLoginActivity extends BaseActivity {
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.rl_login)
    RelativeLayout rlLogin;
    @BindView(R.id.cb_login_protocol)
    CheckBox cbLoginProtocol;
    @BindView(R.id.tv_priacy_policy)
    TextView tvPriacyPolicy;
    @BindView(R.id.tv_service_protocol)
    TextView tvServiceProtocol;

    private IWXAPI api;
    private ProgressDialog mProgressDialog;
    private CustomAlertDialog mCommonAlertDialog;

    public static void launchAct(Activity activity) {
        Intent intent = new Intent(activity, WeixinLoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin_login);

        StatusBarUtils.initMainColorStatusBar(this);
        ButterKnife.bind(this);
        regToWx();
        showHintInfo();
    }


    @Override
    protected void onResume() {
        super.onResume();
        String weixinCode = SharedUtil.getString(getApplicationContext(), AccountManager.SP_WEIXIN_LOGIN_CODE);
        if (!StringUtil.isEmpty(weixinCode)) {
            //已授权,删除信息
            SharedUtil.remove(AccountManager.SP_WEIXIN_LOGIN_CODE);
////            //获取用户信息
            login(weixinCode);
        }
    }

    private void login(String weixinCode) {
        createProgressDialog();
        //登录后台
        loginService(weixinCode);
    }

    private void loginService(String code) {
        AccountManager.getInstance().loginService(code, new AccountManager.IAccountManagerListener() {
            @Override
            public void requestResult(final boolean isSuccess, final String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressLoadingDialog();
                        if (isSuccess) {
                            //登录成功
                            Intent intent = new Intent(WeixinLoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String tip = "";
                            if (TextUtils.isEmpty(errorMsg)) {
                                tip = "登录失败";
                            } else {
                                tip = "登录失败，" + errorMsg;
                            }
                            showToast(tip);
                        }
                    }
                });
            }
        });
    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, SystemConfig.WX_KEY, false);
        api.registerApp(SystemConfig.WX_KEY);
    }

    private void toLogin() {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(this, "您的设备未安装微信客户端", Toast.LENGTH_SHORT).show();
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            api.sendReq(req);
        }
    }

    private void createProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("登录中，请稍后");
        mProgressDialog.show();
    }

    private void dismissProgressLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void showHintInfo() {
        cbLoginProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvLogin.setBackgroundResource(R.drawable.btn_wxpay_selector);
                } else {
                    tvLogin.setBackgroundColor(getResources().getColor(R.color.gray_999999));
                }
            }
        });

        tvTitle.setText(R.string.login);
    }

    @OnClick({R.id.iv_left, R.id.tv_login, R.id.tv_priacy_policy, R.id.tv_service_protocol, R.id.tv_agreen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                showExitAlertDialog();
//                finish();
                break;
            case R.id.tv_login:
                if (!cbLoginProtocol.isChecked()) {
                    showToast("需要同意协议才能运行！");
                    return;
                }
                toLogin();
                break;
            case R.id.tv_priacy_policy:
                WebViewActivity.launchAct(this, getResources().getString(R.string.account_login_protocol_red_two), ZConfig.H5_PRIVACY_POLICY);
                break;
            case R.id.tv_service_protocol:
                WebViewActivity.launchAct(this, getResources().getString(R.string.account_login_protocol_red_one), ZConfig.H5_SERVICE_PROTOCOL);
                break;
            case R.id.tv_agreen:
                cbLoginProtocol.setChecked(!cbLoginProtocol.isChecked());
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            showExitAlertDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示退出提示对话框
     */
    private void showExitAlertDialog() {
        if (null == mCommonAlertDialog && !this.isDestroyed()) {
            mCommonAlertDialog = new CustomAlertDialog(this, "-1");
            mCommonAlertDialog.setTitle(getString(R.string.txt_quit_app));
            mCommonAlertDialog.setMessage(getString(R.string.txt_are_you_sure_to_quit));
            mCommonAlertDialog.setPositiveButton(R.string.ensure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCommonAlertDialog.dismiss();
                    exit();
                }
            });
            mCommonAlertDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCommonAlertDialog.dismiss();
                }
            });
        }
        if (!this.isDestroyed() && !mCommonAlertDialog.isShowing()) {
            mCommonAlertDialog.show();

        }
    }

}
