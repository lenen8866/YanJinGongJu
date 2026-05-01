package com.read.scriptures.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.alipay.PayResult;
import com.read.scriptures.bean.RespInfo;
import com.read.scriptures.bean.WxpayBean;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.http.okhttp.HttpCallback;
import com.read.scriptures.http.okhttp.OkHttpUtils;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.model.FontListModel;
import com.read.scriptures.share.wxapi.WXPayUtils;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.LevelActivePayTypeAdapter;
import com.read.scriptures.util.GsonUtils;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.PayUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.widget.ListViewForScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.read.scriptures.ui.fragment.FragmentActive.PAY_TYPE_WEICHAT;

//import com.ahmadrosid.svgloader.SvgLoader;
//import com.read.scriptures.util.svg_util.SvgSoftwareLayerSetter;

public class BuyFontActivity extends BaseActivity {

    private ImageView iv_left;
    private TextView tv_title;
    private TextView tv_normal;
    private TextView tv_pinyin;
    private TextView tv_have;
    private RecyclerView recycle_font_list;
    private FontAdapter fontAdapter;
    private List<FontListModel.RowsBean> strings;

    private String type = "常见字体";

    /**
     * 字体名称，传给购买成功页面显示
     */
    private String fontName = "";

    private String apliy_fee;

    private AlertDialog donateDialog;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_buy_font);
        initView();
        initListener();
    }

    private void initListener() {

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_normal.setBackgroundResource(R.drawable.select_font_bg);
                tv_pinyin.setBackgroundColor(Color.parseColor("#F0F0F0"));
                tv_have.setBackgroundColor(Color.parseColor("#F0F0F0"));
                type = "常见字体";
                getData();
            }
        });
        tv_pinyin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_pinyin.setBackgroundResource(R.drawable.select_font_bg);
                tv_normal.setBackgroundColor(Color.parseColor("#F0F0F0"));
                tv_have.setBackgroundColor(Color.parseColor("#F0F0F0"));
                type = "拼音字体";
                getData();
            }
        });
        tv_have.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_have.setBackgroundResource(R.drawable.select_font_bg);
                tv_pinyin.setBackgroundColor(Color.parseColor("#F0F0F0"));
                tv_normal.setBackgroundColor(Color.parseColor("#F0F0F0"));
                type = "已购字体";
                getData();
            }
        });
        fontAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                FontListModel.RowsBean rowsBean = (FontListModel.RowsBean) adapter.getData().get(position);
                if (view.getId() == R.id.item_recycle_font_tv_caozuo && !TextUtils.isEmpty(String.valueOf(rowsBean.getPay_status()))) {
                    if (Double.parseDouble(rowsBean.getPrice()) == 0.00) {
                        Toast.makeText(ATHIS, "免费下载", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (rowsBean.getPay_status() == 1) {
                        //TODO 立即使用
                        showToast("暂未开放");
                    } else if (rowsBean.getPay_status() == 2) {
                        //TODO 正在使用
                        showToast("正在使用");
                    } else if (rowsBean.getPay_status() == 0) {
                        showDonateDialog(rowsBean.getId(), rowsBean.getPrice());
                        apliy_fee = rowsBean.getPrice();
                        fontName = rowsBean.getFont_name();
                    }
                }
            }
        });

    }

    private void buyFont(int fontId, final String payType) {

        HashMap<String, String> params = new HashMap<>();
        params.put("token", AccountManager.getInstance().getUserInfo().getToken());
        params.put("fontid", String.valueOf(fontId));
        params.put("feetype", "CNY");
        params.put("paytype", payType);
        createProgressDialog();
        OkHttpUtils.getInstance().get(ZConfig.BUY_FONT, params, new HttpCallback<RespInfo<HashMap<String, String>>>() {
            @Override
            public void onSuccess(final RespInfo<HashMap<String, String>> result) {
                dismissProgressLoadingDialog();
                switch (payType) {
                    case "alipay":
                        String aliJson = result.getData().get("json");
                        payV2ByNet(aliJson);
                        break;
                    case "wxpay":
                        String orderInfoJson = GsonUtils.objectToStr(result);
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
                                .toWXPayNotSign(getBaseContext());

                        SystemConstants.isActive = false;
                        SystemConstants.FONT_NAME = fontName;
                        break;
                }
            }


            @Override
            public void onError(int code, final String errorMsg) {
                dismissProgressLoadingDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ATHIS, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFinish() {
                dismissProgressLoadingDialog();
            }
        });
    }


    /**
     * 支付宝支付业务
     */
    public void payV2ByNet(final String orderInfo) {
        SystemConstants.isActive = false;
        if (TextUtils.isEmpty(PayUtil.APPID) || TextUtils.isEmpty(PayUtil.RSA_PRIVATE)) {
            new AlertDialog.Builder(getBaseContext()).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
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
                PayTask alipay = new PayTask(BuyFontActivity.this);
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
                        PreferenceConfig.savePayMoney(getBaseContext(), Float.valueOf(apliy_fee));
                        showToast("支付成功");
                        //重新获取激活时间
//                        ThreadUtil.doOnOtherThread(new Runnable() {
//                            @Override
//                            public void run() {
                        AccountManager.getInstance().refreshUserInfo(new AccountManager.IAccountManagerListener() {
                            @Override
                            public void requestResult(boolean isSuccess, String errMsg) {
                                Intent intent = new Intent(getBaseContext(), PayResultActivity.class);
                                intent.putExtra("result", true);
                                intent.putExtra("days", "0");
                                intent.putExtra("font", true);
                                intent.putExtra("fontName", fontName);
                                intent.putExtra("uuid", AccountManager.getInstance().getUserInfo().getUsername());
                                intent.putExtra("money", apliy_fee);
//                                intent.putExtra("levelType", levelType);
                                startActivity(intent);
                            }
                        });
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showToastMsg("支付失败，" + momo);
                        Intent intent = new Intent(getBaseContext(), PayResultActivity.class);
                        intent.putExtra("result", false);
                        intent.putExtra("days", "0");
                        intent.putExtra("font", true);
                        intent.putExtra("fontName", fontName);
                        intent.putExtra("uuid", AccountManager.getInstance().getUserInfo().getUsername());
                        intent.putExtra("money", apliy_fee);
//                        intent.putExtra("levelType", levelType);
                        startActivity(intent);
                    }
                    break;
                }
                default:
                    if (getBaseContext() == null) {
                        return;
                    }
                    Intent intent = new Intent(getBaseContext(), PayResultActivity.class);
                    intent.putExtra("result", false);
                    intent.putExtra("days", "0");
                    intent.putExtra("uuid", AccountManager.getInstance().getUserInfo().getUsername());
                    intent.putExtra("money", apliy_fee);
//                    intent.putExtra("levelType", levelType);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void initView() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_normal = (TextView) findViewById(R.id.tv_normal);
        tv_pinyin = (TextView) findViewById(R.id.tv_pinyin);
        tv_have = (TextView) findViewById(R.id.tv_have);
        recycle_font_list = (RecyclerView) findViewById(R.id.recycle_font_list);
        strings = new ArrayList<>();
        recycle_font_list.setLayoutManager(new LinearLayoutManager(this));
        fontAdapter = new FontAdapter(R.layout.item_recycle_font, strings);
        fontAdapter.bindToRecyclerView(recycle_font_list);
        getData();
    }

    /**
     * 选择付款方式弹窗
     */
    private void showDonateDialog(final int fontId, final String money) {
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_donate, null);
        view.findViewById(R.id.ll_donate_number).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.tv_donate_number)).setText("购买字体");
        ListViewForScrollView lvPayType = view.findViewById(R.id.lv_pay_type);
        final LevelActivePayTypeAdapter mLevelActivePayTypeAdapter = new LevelActivePayTypeAdapter(getBaseContext());
        lvPayType.setAdapter(mLevelActivePayTypeAdapter);
        view.findViewById(R.id.tv_confirm_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0.00".equals(money)) {
                    return;
                }
                String payType = "alipay";
                if (mLevelActivePayTypeAdapter.getCurrentCheckedType().equals(PAY_TYPE_WEICHAT)) {
                    payType = "wxpay";
                }
                buyFont(fontId, payType);
                donateDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donateDialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        donateDialog = builder.create();
        donateDialog.setCanceledOnTouchOutside(false);
        donateDialog.show();
    }

    private void createProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("请稍等");
        mProgressDialog.show();
    }

    private void dismissProgressLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void getData() {

        HashMap<String, String> params = new HashMap<>();
        createProgressDialog();
        OkHttpUtils.getInstance().get(ZConfig.FONT_LIST, params, new HttpCallback<RespInfo<FontListModel>>() {
            @Override
            public void onSuccess(final RespInfo<FontListModel> result) {
                dismissProgressLoadingDialog();
                if (result != null && result.getData() != null) {
                    FontListModel fontListModel = result.getData();
                    strings.clear();
                    for (int i = 0; i < fontListModel.getRows().size(); i++) {
                        if (type.contains(fontListModel.getRows().get(i).getCategory()))
                            strings.add(fontListModel.getRows().get(i));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fontAdapter.notifyDataSetChanged();
                            recycle_font_list.scrollBy(0, 0);
                        }
                    });
                }
            }

            @Override
            public void onError(int code, String errorMsg) {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    class FontAdapter extends BaseQuickAdapter<FontListModel.RowsBean, BaseViewHolder> {
        public FontAdapter(int layoutResId, @Nullable List<FontListModel.RowsBean> data) {
            super(layoutResId, data);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void convert(BaseViewHolder helper, FontListModel.RowsBean item) {
            ImageView imageView = helper.getView(R.id.item_recycle_font_iv);
            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            PicassoUtils.loadImage(imageView, item.getPre_img(), R.drawable.icon_play_deault_bg);

            ((TextView) (helper.getView(R.id.item_recycle_font_tv_name))).setText(item.getFont_name());
            ((TextView) (helper.getView(R.id.item_recycle_font_tv_size))).setText(item.getSize());
            TextView text = helper.getView(R.id.item_recycle_font_tv_caozuo);

            if (item.getPay_status() == 1) {
                text.setText("立即使用");
                text.setTextColor(Color.parseColor("#4caf50"));
                text.setBackground(getDrawable(R.drawable.stroke_green_1));
            } else if (item.getPay_status() == 2) {
                text.setText("正在使用");
                text.setTextColor(Color.parseColor("#4c4c4c"));
                text.setBackground(getDrawable(R.drawable.stroke_gray_1));
            } else if (item.getPay_status() == 0) {
                text.setText(item.getPrice() + "元购买");
                text.setTextColor(Color.parseColor("#4caf50"));
                text.setBackground(getDrawable(R.drawable.stroke_green_1));
            }

            if (Double.parseDouble(item.getPrice()) == 0.00) {
                text.setText("免费下载");
                text.setTextColor(Color.parseColor("#f44336"));
                text.setBackground(getDrawable(R.drawable.stroke_red_1));
            }

            helper.addOnClickListener(R.id.item_recycle_font_tv_caozuo);
        }
    }

}
