//package com.read.scriptures.widget;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alipay.sdk.app.PayTask;
//import com.lgmshare.eiframe.utils.ToastUtil;
//import com.read.scriptures.R;
//import com.read.scriptures.alipay.PayResult;
//import com.read.scriptures.app.HuDongApplication;
//import com.read.scriptures.config.PreferenceConfig;
//import com.read.scriptures.config.ZConfig;
//import com.read.scriptures.util.LogUtil;
//import com.read.scriptures.util.NetConnectUtil;
//import com.read.scriptures.util.NumberUtil;
//import com.read.scriptures.util.OrderInfoUtil2_0;
//import com.read.scriptures.util.PayUtil;
//import com.read.scriptures.util.PhoneInfo;
//import com.read.scriptures.util.StringUtil;
//import com.read.scriptures.util.ThreadUtil;
//import com.read.scriptures.util.rsa.DES;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class DonateDialog extends Dialog implements View.OnClickListener, View.OnTouchListener {
//
//    private final Activity activity;
//
//    private TextView tvShow;
//
//    private TextView donateRandom;
//
//    private TextView donateFree;
//
//    private TextView donatePlus;
//
//    private TextView donateSub;
//    private EditText editText;
//
//    public DonateDialog(final Activity activity) {
//        super(activity, R.style.custom_dialog);
//        this.activity = activity;
//    }
//
//    @Override
//    protected void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        init();
//    }
//
//    public void init() {
//        this.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
//        final LayoutInflater inflater = LayoutInflater.from(activity);
//        final View view = inflater.inflate(R.layout.donate_dialog, null);
//        setContentView(view);
//        tvShow = (TextView) findViewById(R.id.show);
//        tvShow.setText("        " +
//                "感谢您安装并使用我们的软件，正因为你们这样的使用，我们在继续制作的动力，一直增加，你们的付出，我相信上帝看的见，你们的钱归为圣的！\n" +
//                "        我们现在需要你们的加入，如果可以，你们可以做一些力所能及的事情，我们需要百夫长、五十夫长、十夫长，管理这软件！愿上帝与我们同在！");
//        donateRandom = (TextView) findViewById(R.id.donate_random);
//        donateFree = (TextView) findViewById(R.id.donate_free);
//        donatePlus = (TextView) findViewById(R.id.donate_plus);
//        donateSub = (TextView) findViewById(R.id.donate_subtract);
//        editText = (EditText) findViewById(R.id.donate_money);
//
//
//        donateFree.setText("无偿捐献");
//        donateRandom.setText("随缘捐献");
//
//        donateFree.setOnClickListener(this);
//        donateRandom.setOnClickListener(this);
//        donatePlus.setOnClickListener(this);
//        donateSub.setOnClickListener(this);
//
////        donatePlus.setOnTouchListener(this);
////        donateSub.setOnTouchListener(this);
//
//        final Window dialogWindow = getWindow();
//        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        final DisplayMetrics d = activity.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
//        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.6
//        lp.height = (int) (d.heightPixels * 0.6); // 高度设置为屏幕的0.6
//        dialogWindow.setAttributes(lp);
//        this.setCanceledOnTouchOutside(true);
//    }
//
//    @Override
//    public void onClick(final View v) {
//        final int id = v.getId();
//        float money = 1;
//        switch (id) {
//            case R.id.donate_free:
//                try {
//                    money = NumberUtil.keepEffectiveNumbers(Float.valueOf(editText.getText()
//                            .toString()), 2);
//                    if (money <= 0) {
//                        money = 1;
//                        editText.setText(String.valueOf(money));
//                    }
//                } catch (Exception e) {
//                }
//                pay(money);
//                break;
//            case R.id.donate_random:
//                money =
//                        NumberUtil.keepEffectiveNumbers(Float.valueOf(String.valueOf(Math.random
//                                () * 100)), 2);
//                editText.setText(String.valueOf(money));
//                pay(money);
//                break;
//            case R.id.donate_plus:
//                try {
//                    money = NumberUtil.keepEffectiveNumbers(Float.valueOf(editText.getText()
//                            .toString()), 2);
//                } catch (Exception e) {
//                }
//                money += 1;
//                if (money <= 0) {
//                    money = 1;
//                }
//                editText.setText(String.valueOf(money));
//                break;
//            case R.id.donate_subtract:
//                try {
//                    money = NumberUtil.keepEffectiveNumbers(Float.valueOf(editText.getText()
//                            .toString()), 2);
//                } catch (Exception e) {
//                }
//                money -= 1;
//                if (money < 0) {
//                    money = 0;
//                }
//                editText.setText(String.valueOf(money));
//                break;
//        }
//    }
//
//    private void pay(final float money) {
//        /**
//         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
//         * TODO:真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
//         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
//         *
//         * orderInfo的获取必须来自服务端；
//         */
//        Map<String, String> params = OrderInfoUtil2_0.buildDonateOrderParamMap(PayUtil.APPID,
//                String.valueOf(money));
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//        String sign = OrderInfoUtil2_0.getSign(params, PayUtil.RSA_PRIVATE);
//        final String orderInfo = orderParam + "&" + sign;
//
//        ThreadUtil.doOnOtherThread(new Runnable() {
//            public void run() {
//                PayTask alipay = new PayTask(activity);
//                Map<String, String> result = alipay.payV2(orderInfo, true);
//                PayResult payResult = new PayResult(result);
//                LogUtil.info("msp", result.toString());
//                final String resultStatus = payResult.getResultStatus();
//                if (TextUtils.equals(resultStatus, "9000")) {
//                    sendMsgToServer(money);
//                }
//                tvShow.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (TextUtils.equals(resultStatus, "9000")) {
//                            DonateDialog.this.dismiss();
//                            ToastCenterUtil.showMessage(activity, "感谢您的捐献！");
//                        } else {
//                            ToastCenterUtil.showMessage(activity, "捐献失败");
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    private void sendMsgToServer(float money) {
//        final Map<String, Object> userMap = new HashMap<String, Object>();
//        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//        PhoneInfo phoneInfo = new PhoneInfo(activity);
//        userMap.put("uuid", HuDongApplication.getInstance().getAppUniqueID());
//        float moneyTotal = PreferenceConfig.getPayMoney(activity) + money;
//        userMap.put("money", moneyTotal);
//        PreferenceConfig.savePayMoney(activity, money);
//        userMap.put("phone", phoneInfo.getNativePhoneNumber());
//        long activation = PreferenceConfig.getActivationTime(activity) - System.currentTimeMillis();
//        userMap.put("time", activation);
//        String json = new JSONObject(userMap).toJSONString();
//        json = StringUtil.Bytes2HexString(DES.encrypt(json.getBytes(), DES.NET_PASSWORD));
//        String returnJson = NetConnectUtil.getContent(activity,
//                ZConfig.URL + "/Sys/android/saveUser.do?json=" + json, 5);
//        try {
//            JSONObject jsonObject = JSONObject.parseObject(returnJson);
//            if (jsonObject.getBooleanValue("success")) {
//                LogUtil.info("感谢捐献" + money + "!");
//            } else {
//                LogUtil.info("平台服务器没有给出正确响应。");
//            }
//        } catch (Exception e) {
//            LogUtil.error("netError", e);
//        }
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        final int id = v.getId();
//        LogUtil.info("action:" + event.getAction());
//        float money = 1;
//        switch (id) {
//            case R.id.donate_plus:
//                try {
//                    money = NumberUtil.keepEffectiveNumbers(Float.valueOf(editText.getText()
//                            .toString()), 2);
//                } catch (Exception e) {
//                }
//                money += 10;
//                if (money <= 0) {
//                    money = 10;
//                }
//                editText.setText(String.valueOf(money));
//                break;
//            case R.id.donate_subtract:
//                try {
//                    money = NumberUtil.keepEffectiveNumbers(Float.valueOf(editText.getText()
//                            .toString()), 2);
//                } catch (Exception e) {
//                }
//                money -= 10;
//                if (money < 0) {
//                    money = 0;
//                }
//                editText.setText(String.valueOf(money));
//                break;
//        }
//        return true;
//    }
//}