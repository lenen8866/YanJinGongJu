package com.read.scriptures.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.read.scriptures.R;
import com.read.scriptures.adapter.PayAccountAdapter;
import com.read.scriptures.adapter.RechargePayVipAdapter;
import com.read.scriptures.adapter.SearchAccountAdapter;
import com.read.scriptures.alipay.PayResult;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.app.PayVipBean;
import com.read.scriptures.bean.RechargeTipData;
import com.read.scriptures.bean.RechargeTypeBean;
import com.read.scriptures.bean.SearchAccountBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.share.wxapi.WXPayUtils;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.DialogUtils;
import com.read.scriptures.util.LogUtil;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.PayUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.widget.ClearEditText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class PayVipActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_pay_vip);
        StatusBarUtils.initMainColorStatusBar(this);
        initView();
        getRechargeType();
    }

    String payMethod = "wxpay";//默认微信

    private void getRechargeType() {
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/paying/pay", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                RechargeTypeBean rechargeTypeBean = new Gson().fromJson(t, RechargeTypeBean.class);
                if (rechargeTypeBean != null && rechargeTypeBean.data != null) {
                    payMethod = rechargeTypeBean.data.pay_name;
                }
            }
        });
    }

    private ClearEditText et_search;
    private TextView btn_search;

    private RecyclerView rcv_search_result;
    private SearchAccountAdapter accountAdapter;

    private RecyclerView rcv_list;
    private PayAccountAdapter payAccountAdapter;

    private View ll_main;

    private void initView() {
        et_search = findViewById(R.id.et_search);
        btn_search = findViewById(R.id.btn_search);
        rcv_search_result = findViewById(R.id.rcv_search_result);
        rcv_list = findViewById(R.id.rcv_list);
        ll_main = findViewById(R.id.ll_main);

        rcv_search_result.setLayoutManager(new LinearLayoutManager(this));
        accountAdapter = new SearchAccountAdapter();
        rcv_search_result.setAdapter(accountAdapter);

        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        payAccountAdapter = new PayAccountAdapter(R.layout.item_search_account1);
        rcv_list.setAdapter(payAccountAdapter);


        accountAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SearchAccountBean.DataBean item = accountAdapter.getItem(position);
//                if (!payAccountAdapter.getData().contains(item)) {
                    btn_search.setEnabled(true);
                    payAccountAdapter.getData().clear();
                    payAccountAdapter.addData(item);
                    rcv_search_result.setVisibility(View.GONE);
//                } else {
//                    showToast("该账号已添加");
//                }
            }
        });

        payAccountAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (rcv_search_result.getVisibility() == View.VISIBLE) {
                    rcv_search_result.setVisibility(View.GONE);
                    return;
                }
                SearchAccountBean.DataBean item = payAccountAdapter.getItem(position);
                getRechargeData(item);
            }
        });

        payAccountAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_delete:
                        payAccountAdapter.remove(position);
                        btn_search.setEnabled(false);
                        break;
                }
            }
        });

        rcv_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (rcv_search_result.getVisibility() == View.VISIBLE) {
                    rcv_search_result.setVisibility(View.GONE);
                }
                return false;
            }
        });
        ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rcv_search_result.getVisibility() == View.VISIBLE) {
                    rcv_search_result.setVisibility(View.GONE);
                }
            }
        });
        RxTextView.afterTextChangeEvents(et_search)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TextViewAfterTextChangeEvent>() {
                    @Override
                    public void accept(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) throws Exception {
                        String str = textViewAfterTextChangeEvent.editable().toString().trim();
                        if (str.length() >= 10) {
                            getSearchResult(str);
                        }
                    }
                });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String trim = et_search.getText().toString().trim();
                    if (TextUtils.isEmpty(trim)) {
                        return false;
                    }
                    getSearchResult(trim);
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.iv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rcv_search_result.getVisibility() == View.VISIBLE) {
                    rcv_search_result.setVisibility(View.GONE);
                    return;
                }
                SearchAccountBean.DataBean item = payAccountAdapter.getItem(0);
                getRechargeData(item);
            }
        });
    }

    private void getRechargeData(SearchAccountBean.DataBean item) {
        showProgressDialog("");
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/paying/payment", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                dismissProgressDialog();
                PayVipBean payVipBean = new Gson().fromJson(t, PayVipBean.class);
                if (payVipBean == null || payVipBean.data == null || payVipBean.data.普通会员 == null || payVipBean.data.普通会员.isEmpty()) {
                    showToast("充值数据异常");
                    return;
                }
                Collections.sort(payVipBean.data.普通会员, new Comparator<PayVipBean.DataBean.普通会员Bean>() {
                    @Override
                    public int compare(PayVipBean.DataBean.普通会员Bean o1, PayVipBean.DataBean.普通会员Bean o2) {
                        return Integer.parseInt(o2.day) < Integer.parseInt(o1.day) ? 1 : -1;
                    }
                });
                payVipBean.data.普通会员.get(0).selected = true;
                showRechargeDialog(payVipBean.data.普通会员, item);
            }
        });
    }

    private void showRechargeDialog(List<PayVipBean.DataBean.普通会员Bean> data, SearchAccountBean.DataBean item) {
        DialogUtils.showBottomDialog(this, R.layout.dialog_recharge_vip_layout, -1, -2, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                RecyclerView rcv_recharge = view.findViewById(R.id.rcv_recharge);
                rcv_recharge.setLayoutManager(new GridLayoutManager(view.getContext(), 4));
                RechargePayVipAdapter rechargeTipAdapter = new RechargePayVipAdapter();
                rcv_recharge.setAdapter(rechargeTipAdapter);
                rechargeTipAdapter.setNewData(data);
                rechargeTipAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        for (PayVipBean.DataBean.普通会员Bean itemBean : rechargeTipAdapter.getData()) {
                            itemBean.selected = false;
                        }
                        PayVipBean.DataBean.普通会员Bean item = rechargeTipAdapter.getItem(position);
                        item.selected = true;
                        adapter.notifyDataSetChanged();
                    }
                });
                CheckBox rb_wx_pay = view.findViewById(R.id.rb_wx_pay);
                CheckBox rb_ali_pay = view.findViewById(R.id.rb_ali_pay);
                TextView tv_confirm_pay = view.findViewById(R.id.tv_confirm_pay);
                TextView tv_title = view.findViewById(R.id.tv_title);
                tv_title.setText(item.nickname + "(" + item.username + ")");
                if (TextUtils.equals(payMethod, "wxpay")) {
                    rb_wx_pay.setChecked(true);
                    rb_ali_pay.setChecked(false);
                } else {
                    rb_ali_pay.setChecked(true);
                    rb_wx_pay.setChecked(false);
                }

                rb_wx_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        rb_wx_pay.setChecked(isChecked);
                        if (isChecked) {
                            rb_ali_pay.setChecked(false);
                        }
                    }
                });
                rb_ali_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        rb_ali_pay.setChecked(isChecked);
                        if (isChecked) {
                            rb_wx_pay.setChecked(false);
                        }
                    }
                });

                tv_confirm_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String payMethod;
                        if (rb_wx_pay.isChecked()) {
                            payMethod = "wxpay";
                        } else if (rb_ali_pay.isChecked()) {
                            payMethod = "alipay";
                        } else {
                            showToast("请选择支付方式!");
                            return;
                        }
                        PayVipBean.DataBean.普通会员Bean dataBean = null;
                        for (PayVipBean.DataBean.普通会员Bean itemBean : rechargeTipAdapter.getData()) {
                            if (itemBean.selected) {
                                dataBean = itemBean;
                                break;
                            }
                        }
                        if (dataBean == null) {
                            showToast("请选择充值数量!");
                            return;
                        }
                        days = dataBean.day;
                        apliy_fee = dataBean.discount;
                        SystemConstants.WX_UUID = mUUid = item.nickname + "(" + item.username + ")";
                        getPayInfo(dataBean, item, payMethod);

//                        HuDongApplication.getInstance().rechargeType = 2;
//                        Intent intent = new Intent(PayVipActivity.this, PayResultActivity.class);
//                        intent.putExtra("result", true);
//                        intent.putExtra("days", days);
//                        intent.putExtra("uuid", mUUid);
//                        intent.putExtra("money", apliy_fee);
//                        intent.putExtra("levelType", levelType);
//                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void getSearchResult(String str) {
        if (TextUtils.isEmpty(str)) {
            rcv_search_result.setVisibility(View.GONE);
            accountAdapter.setNewData(null);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("username", str);
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/user/searchUser", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                SearchAccountBean searchAccountBean = new Gson().fromJson(t, SearchAccountBean.class);
                if (searchAccountBean == null || searchAccountBean.data == null || searchAccountBean.data.isEmpty()) {
                    rcv_search_result.setVisibility(View.GONE);
                    accountAdapter.setNewData(null);
                } else {
                    rcv_search_result.setVisibility(View.VISIBLE);
                    accountAdapter.setNewData(searchAccountBean.data);
                }
            }
        });
    }

    private void getPayInfo(PayVipBean.DataBean.普通会员Bean payItem, SearchAccountBean.DataBean item, String payType) {
        HuDongApplication.getInstance().rechargeType = 2;
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("shopid", payItem.id);
        map.put("paytype", payType);
        map.put("give", item.username);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/user/buy", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                RechargeTipData rechargeTipBean = new Gson().fromJson(t, RechargeTipData.class);
                if (rechargeTipBean == null || rechargeTipBean.data == null) {
                    showToast("充值数据异常");
                    return;
                }
                switch (payType) {
                    case "wxpay":
                        startWxPay(rechargeTipBean);
                        break;
                    case "alipay":
                        startAliPay(rechargeTipBean);
                        break;
                }
            }
        });
    }

    private void startWxPay(RechargeTipData data) {
        WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
        builder.setAppId(data.data.appid)
                .setPartnerId(data.data.partnerid)
                .setPrepayId(data.data.prepayid)
                .setPackageValue("Sign=WXPay")
                .setNonceStr(data.data.noncestr)
                .setSign(data.data.sign)
                .setTimeStamp(data.data.timestamp)
                .build()
                .toWXPayNotSign(PayVipActivity.this);
    }

    private void startAliPay(RechargeTipData rechargeTipBean) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(PayVipActivity.this);
                Map<String, String> result = alipay.payV2(rechargeTipBean.data.json, true);
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

    private String apliy_fee;
    private String days;
    private String levelType = "";
    private String mUUid;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

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
                        Intent intent = new Intent(PayVipActivity.this, PayResultActivity.class);
                        intent.putExtra("result", true);
                        intent.putExtra("days", days);
                        intent.putExtra("uuid", mUUid);
                        intent.putExtra("money", apliy_fee);
                        intent.putExtra("levelType", levelType);
                        startActivity(intent);
                    } else {   // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Intent intent = new Intent(PayVipActivity.this, PayResultActivity.class);
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
                    Intent intent = new Intent(PayVipActivity.this, PayResultActivity.class);
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(String str) {
        HuDongApplication.getInstance().rechargeType = 0;
        if (str.equals("pay_vip_recharge_success")) {
            showRechargeSuccessDialog();
        } else if (str.equals("pay_vip_recharge_failure")) {
            showToast("充值失败，请重试");
        }
    }

    private void showRechargeSuccessDialog() {
        showToast("开通成功");
    }
}
