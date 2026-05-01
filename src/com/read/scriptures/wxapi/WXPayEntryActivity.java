package com.read.scriptures.wxapi;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.TimeUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WXPayEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    @BindView(R.id.tv_active_time)
    TextView active;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.ll_success)
    LinearLayout ll_success;
    @BindView(R.id.ll_fail)
    LinearLayout ll_fail;

    @BindView(R.id.rl_main)
    RelativeLayout rl_main;
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        StatusBarUtils.initMainColorStatusBar(this);
        ButterKnife.bind(this);
        api = WXAPIFactory.createWXAPI(this, SystemConfig.WX_KEY);
        api.handleIntent(getIntent(), this);
        title.setText("微信支付结果");
        if (HuDongApplication.getInstance().rechargeType == 1) {
            rl_main.setBackgroundColor(Color.TRANSPARENT);
            rl_main.setVisibility(View.GONE);
        } else {
            rl_main.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    ll_success.setVisibility(View.VISIBLE);
                    ll_fail.setVisibility(View.GONE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            title.setText("支付成功");
                        }
                    });
                    switch (HuDongApplication.getInstance().rechargeType) {
                        case 0:
                        case 2:
                            getActiveTime();
                            break;
                        case 1:
                            EventBus.getDefault().post("answer_tip_recharge_success");
                            finish();
                            break;
                    }
                    break;
                default:
                    ll_success.setVisibility(View.GONE);
                    ll_fail.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            title.setText("支付失败");
                        }
                    });

                    switch (HuDongApplication.getInstance().rechargeType) {
                        case 0:
                        case 2:
                            getActiveTime();
                            break;
                        case 1:
                            EventBus.getDefault().post("answer_tip_recharge_failure");
                            finish();
                            break;
                    }
                    break;
            }
        }
    }

    private void getActiveTime() {
        //刷新用户信息
        AccountManager.getInstance().refreshUserInfo(new AccountManager.IAccountManagerListener() {
            @Override
            public void requestResult(boolean isSuccess, String errMsg) {
                if (isSuccess) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String notTime = TimeUtils.getDate();
                            String mUUid = SystemConstants.WX_UUID;
                            String apliy_fee = SystemConstants.WX_MONEY;
                            String days = SystemConstants.WX_DAYS;
                            if ("0".equals(days)) {
                                active.setText("账号：" + mUUid + "感谢您的捐赠" + "\n支付时间为" + notTime);
                            } else {
                                String levelType = SystemConstants.WX_LEVEL_TYPE;
                                long activeTime = AccountManager.getInstance().getUserInfo().getMaturity(levelType);
                                String str = "账号：" + mUUid + " 已经充值 " + AccountManager.getInstance().getLevelName(levelType) + " " + days + "！\n到期时间：" + TimeUtils.timeStamp2Date(activeTime) + "\n支付时间为" + notTime;
                                if (HuDongApplication.getInstance().rechargeType == 2) {
                                    str = "账号：" + mUUid + " 已经充值 " + AccountManager.getInstance().getLevelName(levelType) + " " + days + "\n支付时间为" + notTime;
                                }
                                active.setText(str);
                            }
                        }
                    });
                }
            }
        });
    }

    @OnClick({R.id.iv_left, R.id.tv_sure, R.id.tv_back_pay})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
            case R.id.tv_sure:
            case R.id.tv_back_pay:
                finish();
                break;
        }
    }
}