package com.read.scriptures.ui.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayResultActivity extends Activity {

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
    boolean isOwn = true;
    boolean success;
    private String apliy_fee = "0";
    private String days = "-1";
    private String mUUid;
    private String mLevelType = "";
    private String fontName = "";

    /**
     * 是否从购买字体页面过来
     */
    private boolean font = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        StatusBarUtils.initMainColorStatusBar(this);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        title.setText("支付宝支付结果");
        success = getIntent().getBooleanExtra("result", true);
        font = getIntent().getBooleanExtra("font", false);
        days = getIntent().getStringExtra("days");
        apliy_fee = getIntent().getStringExtra("money");
        mUUid = getIntent().getStringExtra("uuid");
        fontName = getIntent().getStringExtra("fontName");
        mLevelType = getIntent().getStringExtra("levelType");
//        if (mUUid.equals(HuDongApplication.getInstance().getAppUniqueID())) {//说明是自己
//            isOwn = true;
//        } else {
//            isOwn = false;
//        }
        isOwn = true;
        rl_main.setVisibility(View.VISIBLE);
        if (success) {
            ll_success.setVisibility(View.VISIBLE);
            ll_fail.setVisibility(View.GONE);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    title.setText("支付成功");
                }
            });
            getActiveTime();
        } else {
            ll_success.setVisibility(View.GONE);
            ll_fail.setVisibility(View.VISIBLE);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    title.setText("支付失败");
                }
            });
            getActiveTime();
        }
    }

    private void getActiveTime() {
        AccountManager.getInstance().refreshUserInfo(new AccountManager.IAccountManagerListener() {
            @Override
            public void requestResult(boolean isSuccess, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String notTime = TimeUtils.getDate();
                        if (font) {
                            active.setText("账号：" + mUUid + "您已经成功购买了" + fontName + "字体\n订单时间为" + notTime);
                            return;
                        }
                        if ("0".equals(days)) {
                            active.setText("账号：" + mUUid + " 感谢您的捐赠" + "\n支付时间为" + notTime);
                        } else {
                            long activeTime = AccountManager.getInstance().getUserInfo().getMaturity(mLevelType);
                            String str = "账号：" + mUUid + " 已经充值 " + AccountManager.getInstance().getLevelName(mLevelType) + " " + days + "！\n到期时间：" + TimeUtils.timeStamp2Date(activeTime) + "\n支付时间为" + notTime;
                            if (HuDongApplication.getInstance().rechargeType == 2) {
                                str = "账号：" + mUUid + " 已经充值 " + AccountManager.getInstance().getLevelName(mLevelType) + " " + days + "\n微信支付时间为" + notTime;
                            }
                            active.setText(str);
                        }
                    }
                });
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