package com.read.scriptures.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.read.scriptures.R;
import com.read.scriptures.alipay.PayResult;
import com.read.scriptures.bean.RechargeTypeBean;
import com.read.scriptures.bean.RespInfo;
import com.read.scriptures.bean.WxpayBean;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.http.okhttp.HttpCallback;
import com.read.scriptures.http.okhttp.OkHttpUtils;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.share.wxapi.WXPayUtils;
import com.read.scriptures.ui.adapter.RechargeListAdapter1;
import com.read.scriptures.ui.fragment.BaseFragment;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.FontsUtil;
import com.read.scriptures.util.GsonUtils;
import com.read.scriptures.util.LogUtil;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.PayUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RechargeFragment extends BaseFragment {

    private RechargeListAdapter1 rechargeListAdapter;

    @Override
    protected void lazyLoad() {

    }

    public static final String PAY_TYPE_ALIPAY = "alipay";
    public static final String PAY_TYPE_WEICHAT = "wxpay";

    private RelativeLayout rl_remark;
    private LinearLayout ll_recharge_1;
    private LinearLayout ll_recharge_2;
    private LinearLayout ll_recharge_3;
    private CheckBox cb_recharge_1;
    private CheckBox cb_recharge_2;
    private CheckBox cb_recharge_3;
    private CheckBox cb_remark;
    private CheckBox rb_wx_pay;
    private CheckBox rb_ali_pay;
    private EditText et_money;
    private EditText et_remark;
    private View ll_main;
    private TextView tv_confirm_pay;

    @Override
    protected void initWidget() {

        rl_remark = findViewById1(R.id.rl_remark);
        ll_recharge_1 = findViewById1(R.id.ll_recharge_1);
        ll_recharge_2 = findViewById1(R.id.ll_recharge_2);
        ll_recharge_3 = findViewById1(R.id.ll_recharge_3);
        cb_recharge_1 = findViewById1(R.id.cb_recharge_1);
        cb_recharge_2 = findViewById1(R.id.cb_recharge_2);
        cb_recharge_3 = findViewById1(R.id.cb_recharge_3);
        cb_remark = findViewById1(R.id.cb_remark);
        et_money = findViewById1(R.id.et_money);
        et_remark = findViewById1(R.id.et_remark);
        rb_wx_pay = findViewById1(R.id.rb_wx_pay);
        rb_ali_pay = findViewById1(R.id.rb_ali_pay);
        ll_main = findViewById1(R.id.ll_main);
        tv_confirm_pay = findViewById1(R.id.tv_confirm_pay);

        RecyclerView rcv_list = findViewById1(R.id.rcv_list);
        rcv_list.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rechargeListAdapter = new RechargeListAdapter1();
        rechargeListAdapter.setPrice("27");
        rcv_list.setAdapter(rechargeListAdapter);
        mUUid = AccountManager.getInstance().getUserInfo().getUsername();
        cb_recharge_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !cb_recharge_2.isChecked() && !cb_recharge_3.isChecked()) {
                    cb_recharge_1.setChecked(true);
                } else {
                    cb_recharge_1.setChecked(isChecked);
                    if (isChecked) {
                        cb_recharge_2.setChecked(false);
                        cb_recharge_3.setChecked(false);
                    }
                }
            }
        });

        cb_recharge_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !cb_recharge_1.isChecked() && !cb_recharge_3.isChecked()) {
                    cb_recharge_2.setChecked(true);
                } else {
                    cb_recharge_2.setChecked(isChecked);
                    if (isChecked) {
                        cb_recharge_1.setChecked(false);
                        cb_recharge_3.setChecked(false);
                    }
                }
            }
        });
        cb_recharge_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !cb_recharge_1.isChecked() && !cb_recharge_2.isChecked()) {
                    cb_recharge_3.setChecked(true);
                } else {
                    cb_recharge_3.setChecked(isChecked);
                    if (isChecked) {
                        cb_recharge_1.setChecked(false);
                        cb_recharge_2.setChecked(false);
                    }
                }
            }
        });
//#5677FC
        rb_wx_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rb_wx_pay.setChecked(isChecked);
                if (isChecked) {
                    rb_ali_pay.setChecked(false);
                    rechargeListAdapter.setRechargeTypeWithBg(R.drawable.dialog_recharge_item_bg1);//微信
                    tv_confirm_pay.setBackgroundResource(R.drawable.btn_wxpay_selector);
                }
            }
        });
        rb_ali_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rb_ali_pay.setChecked(isChecked);
                if (isChecked) {
                    rb_wx_pay.setChecked(false);
                    rechargeListAdapter.setRechargeTypeWithBg(R.drawable.dialog_recharge_item_bg3);//阿里
                    tv_confirm_pay.setBackgroundResource(R.drawable.btn_alipay_selector1);
                }
            }
        });

        tv_confirm_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payType = PAY_TYPE_WEICHAT;
                if (rb_wx_pay.isChecked()) {
                    payType = PAY_TYPE_WEICHAT;
                } else if (rb_ali_pay.isChecked()) {
                    payType = PAY_TYPE_ALIPAY;
                } else {
                    showToast("请选择支付方式!");
                    return;
                }
                if (TextUtils.isEmpty(et_money.getText().toString().trim())) {
                    showToast("金额不能为空");
                } else if (CommonUtil.formatMoney(et_money.getText().toString().trim(), 2).equals("￥0.00")) {
                    showToast("亲，真的捐0元吗？请您慷慨解囊...");
                } else if (Float.valueOf(et_money.getText().toString().trim()) > 999999.99) {
                    showToast("对不起，单次超过捐款限额！");
                } else {
                    if (SystemConstants.isActive) {
                        return;
                    }
                    SystemConstants.isActive = true;//正在激活
                    String remark = "";
                    if (cb_remark.isChecked()) {
                        remark = et_remark.getText().toString().trim();
                    }
                    String payDetail = "";
                    if (cb_recharge_1.isChecked()) {
                        payDetail = "donation2";
                    } else if (cb_recharge_2.isChecked()) {
                        payDetail = "donation";
                    } else if (cb_recharge_3.isChecked()) {
                        payDetail = "donation3";
                    } else {
                        SystemConstants.isActive = false;
                        showToast("请选择捐赠模式!");
                        return;
                    }
                    donation(payType, "0", et_money.getText().toString().trim(), payDetail, remark);
                }
            }
        });
        cb_remark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rl_remark.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        rechargeListAdapter.setOnItemClickListener(new RechargeListAdapter1.OnItemClickListener() {
            @Override
            public void onItemClick(String price) {
                rechargeListAdapter.setPrice(price);
                et_money.setText(price);
                et_money.setSelection(et_money.length());
            }
        });

        findViewById1(R.id.iv_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.launchAct(getActivity(), "关于捐款", "https://book.sdacn.cn/wenda/jiankuan.html");
            }
        });

        findViewById1(R.id.rl_remark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_remark.requestFocus();
                et_remark.setSelection(et_remark.length());
                showKeyboard(et_remark);

            }
        });
        et_money.setText("27");
        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rechargeListAdapter.setPrice(s.toString());
            }
        });
        match(findViewById1(R.id.tv_title));
        addOnGlobalLayoutListener(ll_main, et_remark);
        getRechargeType();
    }

    /**
     * 显示键盘
     *
     * @param view
     */
    public static void showKeyboard(final View view) {
        if (view == null) {
            return;
        }
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    private String apliy_fee;
    private String days;
    private String levelType;
    private String mUUid;

    /**
     * 无偿捐赠
     */
    private void donation(final String payType, String days, String money, String payDetail, String remark) {
        if (AccountManager.getInstance().getUserInfo() == null) {
            return;
        }
        this.apliy_fee = money;
        this.days = days;
        this.levelType = "";
        if (payType.equals(PAY_TYPE_WEICHAT)) {
            //赋值
            SystemConstants.WX_DAYS = days;
            SystemConstants.WX_MONEY = money;
            SystemConstants.WX_UUID = mUUid;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("money", money);
        params.put("paytype", payType);
        params.put("donation", payDetail);
        params.put("remarks", remark);
        params.put("token", AccountManager.getInstance().getUserInfo().getToken());
        OkHttpUtils.getInstance().post(ZConfig.DONATION, params, new HttpCallback<RespInfo<HashMap<String, String>>>() {
            @Override
            public void onSuccess(final RespInfo<HashMap<String, String>> data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (payType) {
                            case "alipay":
                                String aliJson = data.getData().get("json");
                                payV2ByNet(aliJson);
                                break;
                            case "wxpay":
                                String orderInfoJson = GsonUtils.objectToStr(data);
                                final WxpayBean wxpayBean = JSON.parseObject(orderInfoJson, WxpayBean.class);
                                WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
                                builder.setAppId(wxpayBean.getData().getAppid())
                                        .setPartnerId(wxpayBean.getData().getPartnerid())
                                        .setPrepayId(wxpayBean.getData().getPrepayid())
                                        .setPackageValue("Sign=WXPay")
                                        .setNonceStr(wxpayBean.getData().getNoncestr())
                                        .setSign(wxpayBean.getData().getSign())
                                        .setTimeStamp(wxpayBean.getData().getTimestamp())
                                        .build()
                                        .toWXPayNotSign(getActivity());

                                SystemConstants.isActive = false;
                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(int code, final String errorMsg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SystemConstants.isActive = false;
                        showToast(errorMsg);
                    }
                });
            }

            @Override
            public void onFinish() {

            }
        });
    }


    /**
     * 支付宝支付业务
     */
    public void payV2ByNet(final String orderInfo) {
        SystemConstants.isActive = false;
        if (TextUtils.isEmpty(PayUtil.APPID) || TextUtils.isEmpty(PayUtil.RSA_PRIVATE)) {
            new AlertDialog.Builder(getActivity()).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //

                        }
                    }).show();
            return;
        }
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(getActivity());
                Map<String, String> result = alipay.payV2(orderInfo, true);
                LogUtil.info("msp", result.toString());

                Message msg = new Message();
                msg.what = PayUtil.SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PayUtil.SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    String momo = payResult.getMemo();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {//支付成功
                        PreferenceConfig.savePayMoney(getActivity(), Float.valueOf(apliy_fee));
                        showToast("支付成功");
                        //重新获取激活时间
//                        ThreadUtil.doOnOtherThread(new Runnable() {
//                            @Override
//                            public void run() {
                        AccountManager.getInstance().refreshUserInfo(new AccountManager.IAccountManagerListener() {
                            @Override
                            public void requestResult(boolean isSuccess, String errMsg) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getActivity(), PayResultActivity.class);
                                        intent.putExtra("result", true);
                                        intent.putExtra("days", days);
                                        intent.putExtra("uuid", mUUid);
                                        intent.putExtra("money", apliy_fee);
                                        intent.putExtra("levelType", levelType);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
//                            }
//                        });
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showToastMsg("支付失败，" + momo);
                        Intent intent = new Intent(getActivity(), PayResultActivity.class);
                        intent.putExtra("result", false);
                        intent.putExtra("days", days);
                        intent.putExtra("uuid", mUUid);
                        intent.putExtra("money", apliy_fee);
                        intent.putExtra("levelType", levelType);
                        startActivity(intent);
                    }
                    break;
                }
                default:
                    Intent intent = new Intent(getActivity(), PayResultActivity.class);
                    intent.putExtra("result", false);
                    intent.putExtra("days", days);
                    intent.putExtra("uuid", mUUid);
                    intent.putExtra("money", apliy_fee);
                    intent.putExtra("levelType", levelType);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected int onObtainLayoutResId() {
        return R.layout.ft_recharge;
    }

    public void match(TextView textView) {
        SpannableString sStr = new SpannableString(textView.getText());
        String rx = "(" + "去吧！明天再来，我必给你。" + ")";
        Pattern p = Pattern.compile(rx);
        Matcher matcher = p.matcher(sStr);
        while (matcher.find()) {
            int i = 0;
            sStr.setSpan(new ForegroundColorSpan(Color.BLACK), matcher.start() + i, matcher.end() + i, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            sStr.setSpan(FontsUtil.getInstance(textView.getContext()).getMyNumTypefaceSpan(), matcher.start() + i, matcher.end() + i, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        textView.setText(sStr);
    }


    String payMethod = "wxpay";//默认微信

    private void getRechargeType() {
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/paying/pay", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                RechargeTypeBean rechargeTypeBean = new Gson().fromJson(t, RechargeTypeBean.class);
                if (rechargeTypeBean != null && rechargeTypeBean.data != null) {
                    payMethod = rechargeTypeBean.data.pay_name;
                    if (TextUtils.equals(payMethod, "wxpay")) {
                        rb_wx_pay.setChecked(true);
                        rb_ali_pay.setChecked(false);
                    } else {
                        rb_ali_pay.setChecked(true);
                        rb_wx_pay.setChecked(false);
                    }
                }
            }
        });
    }


    public static void addOnGlobalLayoutListener(final View main, final View scroll) {
        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //1、获取main在窗体的可视区域
                main.getWindowVisibleDisplayFrame(rect);
                //2、获取main在窗体的不可视区域高度，在键盘没有弹起时，main.getRootView().getHeight()调节度应该和rect.bottom高度一样
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                int screenHeight = main.getRootView().getHeight();//屏幕高度
                //3、不可见区域大于屏幕本身高度的1/4：说明键盘弹起了
                if (mainInvisibleHeight > screenHeight / 4) {
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    // 4､获取Scroll的窗体坐标，算出main需要滚动的高度
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    //5､让界面整体上移键盘的高度
                    main.scrollBy(0, srollHeight);
                } else {
                    //3、不可见区域小于屏幕高度1/4时,说明键盘隐藏了，把界面下移，移回到原有高度
                    main.scrollTo(0, 0);
                }
            }
        };
        main.getViewTreeObserver().addOnGlobalLayoutListener(listener);

    }
}
