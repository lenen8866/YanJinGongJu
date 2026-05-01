package com.read.scriptures.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.read.scriptures.R;
import com.read.scriptures.adapter.AnswerNumAdapter;
import com.read.scriptures.adapter.RechargeTipAdapter;
import com.read.scriptures.alipay.PayResult;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.AnswerPromptBean;
import com.read.scriptures.bean.AnswerPromptSignBean;
import com.read.scriptures.bean.LevelQuestionBean;
import com.read.scriptures.bean.QARankBean;
import com.read.scriptures.bean.QuestionBean;
import com.read.scriptures.bean.RechargeTipBean;
import com.read.scriptures.bean.RechargeTipData;
import com.read.scriptures.bean.RechargeTypeBean;
import com.read.scriptures.bean.StartAnswerAgainBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.share.wxapi.WXPayUtils;
import com.read.scriptures.ui.activity.StartAnswerActivity;
import com.read.scriptures.util.CircleTransform;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.DialogUtils;
import com.read.scriptures.util.LogUtil;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.PayUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelQuestionFragment extends BaseFragment implements View.OnClickListener {
    public static String CATE_ID = "CATE_ID";
    public static String ANSWER_NUM = "ANSWER_NUM";
    public static String ANSWER_TIME = "ANSWER_TIME";
    private String cateId;

    private ArrayList<String> answerNumList;

    public static LevelQuestionFragment getInstance(String cateId, ArrayList<String> arrayList, int answerTime) {
        LevelQuestionFragment starHomeFt = new LevelQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CATE_ID, cateId);
        bundle.putInt(ANSWER_TIME, answerTime);
        bundle.putStringArrayList(ANSWER_NUM, arrayList);
        starHomeFt.setArguments(bundle);
        return starHomeFt;
    }

    private LevelQuestionBean.DataBean topData;
    private LevelQuestionBean.DataBean bottomData;

    @Override
    public int onObtainLayoutResId() {
        return R.layout.ft_level_question;
    }

    @Override
    public void lazyLoad() {

    }

    private RelativeLayout rl_1;
    private RelativeLayout rl_2;
    private RelativeLayout rl_3;
    private RelativeLayout rl_4;
    private RelativeLayout rl_5;
    private RelativeLayout rl_6;
    private RelativeLayout rl_7;
    private RelativeLayout rl_8;

    private CardView cv_top_1;
    private CardView cv_bottom_2;
    private TextView tv_top_name;
    private TextView tv_bottom_name;

    private TextView tv_1_name;
    private TextView tv_2_name;
    private TextView tv_3_name;
    private TextView tv_4_name;
    private TextView tv_5_name;
    private TextView tv_6_name;
    private TextView tv_7_name;
    private TextView tv_8_name;

    private TextView tv_1_count;
    private TextView tv_2_count;
    private TextView tv_3_count;
    private TextView tv_4_count;
    private TextView tv_5_count;
    private TextView tv_6_count;
    private TextView tv_7_count;
    private TextView tv_8_count;

    LinearLayout ll_start_answer;
    TextView tv_answer_count;
    Button bt_sort;
    TextView tv_prompt_num;
    TextView tv_recharge_title;
    RecyclerView rcv_answer_num;
    private String limit;
    private CardView cv_10;

    private AnswerNumAdapter answerNumAdapter;

    @Override
    public void initWidget() {
        rl_1 = findViewById1(R.id.rl_1);
        rl_2 = findViewById1(R.id.rl_2);
        rl_3 = findViewById1(R.id.rl_3);
        rl_4 = findViewById1(R.id.rl_4);
        rl_5 = findViewById1(R.id.rl_5);
        rl_6 = findViewById1(R.id.rl_6);
        rl_7 = findViewById1(R.id.rl_7);
        rl_8 = findViewById1(R.id.rl_8);

        cv_top_1 = findViewById1(R.id.cv_top_1);
        cv_bottom_2 = findViewById1(R.id.cv_bottom_2);
        tv_top_name = findViewById1(R.id.tv_top_name);
        tv_bottom_name = findViewById1(R.id.tv_bottom_name);

        tv_1_name = findViewById1(R.id.tv_1_name);
        tv_2_name = findViewById1(R.id.tv_2_name);
        tv_3_name = findViewById1(R.id.tv_3_name);
        tv_4_name = findViewById1(R.id.tv_4_name);
        tv_5_name = findViewById1(R.id.tv_5_name);
        tv_6_name = findViewById1(R.id.tv_6_name);
        tv_7_name = findViewById1(R.id.tv_7_name);
        tv_8_name = findViewById1(R.id.tv_8_name);

        tv_1_count = findViewById1(R.id.tv_1_count);
        tv_2_count = findViewById1(R.id.tv_2_count);
        tv_3_count = findViewById1(R.id.tv_3_count);
        tv_4_count = findViewById1(R.id.tv_4_count);
        tv_5_count = findViewById1(R.id.tv_5_count);
        tv_6_count = findViewById1(R.id.tv_6_count);
        tv_7_count = findViewById1(R.id.tv_7_count);
        tv_8_count = findViewById1(R.id.tv_8_count);

        ll_start_answer = findViewById1(R.id.ll_start_answer);
        tv_answer_count = findViewById1(R.id.tv_answer_count);
        bt_sort = findViewById1(R.id.bt_rank);
        rcv_answer_num = findViewById1(R.id.rcv_answer_num);
        tv_prompt_num = findViewById1(R.id.tv_prompt_num);
        cv_10 = findViewById1(R.id.cv_10);
        tv_recharge_title = findViewById1(R.id.tv_recharge_title);

        tv_answer_count.setText("今天还剩余" + frequency + "次");

        rcv_answer_num.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        answerNumAdapter = new AnswerNumAdapter();
        rcv_answer_num.setAdapter(answerNumAdapter);

        answerNumAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                limit = answerNumAdapter.getItem(position);
                answerNumAdapter.setCurrentNum(limit);
            }
        });

        rl_1.setOnClickListener(this);
        rl_2.setOnClickListener(this);
        rl_3.setOnClickListener(this);
        rl_4.setOnClickListener(this);
        rl_5.setOnClickListener(this);
        rl_6.setOnClickListener(this);
        rl_7.setOnClickListener(this);
        rl_8.setOnClickListener(this);
        cv_top_1.setOnClickListener(this);
        cv_bottom_2.setOnClickListener(this);
        ll_start_answer.setOnClickListener(this);
        bt_sort.setOnClickListener(this);
        tv_prompt_num.setOnClickListener(this);
        cv_10.setOnClickListener(this);
        initData();

//        if (NetUtil.isNetWorkAvailable(getActivity())) {
//            iv_recharge_qa.setVisibility(View.VISIBLE);
//        } else {
//            iv_recharge_qa.setVisibility(View.GONE);
//        }
    }

    private void initData() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            cateId = arguments.getString(CATE_ID);
            answerNumList = arguments.getStringArrayList(ANSWER_NUM);
            answerTime = arguments.getInt(ANSWER_TIME);
            getDetail(cateId);
            String level = "初级排行榜";
            switch (cateId) {
                case "1":
                    level = "初级排行榜";
                    break;
                case "2":
                    level = "中级排行榜";
                    break;
                case "3":
                    level = "高级排行榜";
                    break;
            }
            bt_sort.setText(level);
            if (answerNumList != null) {
                answerNumAdapter.setNewData(answerNumList);
                limit = answerNumList.get(1);
                answerNumAdapter.setNewData(answerNumList);
                answerNumAdapter.setCurrentNum(limit);
            }
        }
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

    @Override
    public void onClick(View v) {
        if (!NetUtil.isNetWorkAvailable(getActivity())) {
            showToast("请链接网络");
            return;
        }
        if (topData == null || topData.childs == null || topData.childs.size() != 4 && bottomData == null || bottomData.childs == null || bottomData.childs.size() != 4) {
            showToast("网络加载中，请稍后...");
            Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof QAFragment) {
                ((QAFragment) parentFragment).initData();
                ((QAFragment) parentFragment).initQAConfig();
            }
            getDetail(cateId);
            return;
        }
        switch (v.getId()) {
            case R.id.rl_1:
                click1();
                break;
            case R.id.rl_2:
                click2();
                break;
            case R.id.rl_3:
                click3();
                break;
            case R.id.rl_4:
                click4();
                break;
            case R.id.rl_5:
                click5();
                break;
            case R.id.rl_6:
                click6();
                break;
            case R.id.rl_7:
                click7();
                break;
            case R.id.rl_8:
                click8();
                break;
            case R.id.cv_top_1:
                clickTop();
                break;
            case R.id.cv_bottom_2:
                clickBottom();
                break;
            case R.id.ll_start_answer:
                clickStartAnswer();
                break;
            case R.id.bt_rank:
                getRankData();
                break;
            case R.id.tv_prompt_num:
            case R.id.cv_10:
                getRechargeData();
                break;
        }
    }

    private void getRechargeData() {
        showProgressDialog("");
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/answer/shopLists", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                dismissProgressDialog();
                RechargeTipBean rechargeTipBean = new Gson().fromJson(t, RechargeTipBean.class);
                if (rechargeTipBean == null || rechargeTipBean.data == null || rechargeTipBean.data.isEmpty()) {
                    showToast("充值数据异常");
                    return;
                }
                for (RechargeTipBean.DataBean itemBean : rechargeTipBean.data) {
                    if (!TextUtils.equals(itemBean.former, itemBean.price)) {
                        itemBean.selected = true;
                        break;
                    }
                }
                showRechargeDialog(rechargeTipBean.data);
            }
        });
    }

    private void showRechargeDialog(List<RechargeTipBean.DataBean> data) {
        DialogUtils.showBottomDialog(getActivity(), R.layout.dialog_recharge_tip_layout, -1, -2, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                RecyclerView rcv_recharge = view.findViewById(R.id.rcv_recharge);
                rcv_recharge.setLayoutManager(new GridLayoutManager(view.getContext(), 4));
                RechargeTipAdapter rechargeTipAdapter = new RechargeTipAdapter();
                rcv_recharge.setAdapter(rechargeTipAdapter);
                rechargeTipAdapter.setNewData(data);
                rechargeTipAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        for (RechargeTipBean.DataBean itemBean : rechargeTipAdapter.getData()) {
                            itemBean.selected = false;
                        }
                        RechargeTipBean.DataBean item = rechargeTipAdapter.getItem(position);
                        item.selected = true;
                        adapter.notifyDataSetChanged();
                    }
                });
                CheckBox rb_wx_pay = view.findViewById(R.id.rb_wx_pay);
                CheckBox rb_ali_pay = view.findViewById(R.id.rb_ali_pay);
                TextView tv_confirm_pay = view.findViewById(R.id.tv_confirm_pay);

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
                        if (rb_wx_pay.isChecked()) {
                            payMethod = "wxpay";
                        } else if (rb_ali_pay.isChecked()) {
                            payMethod = "alipay";
                        } else {
                            showToast("请选择支付方式!");
                            return;
                        }
                        RechargeTipBean.DataBean dataBean = null;
                        for (RechargeTipBean.DataBean itemBean : rechargeTipAdapter.getData()) {
                            if (itemBean.selected) {
                                dataBean = itemBean;
                                break;
                            }
                        }
                        if (dataBean == null) {
                            showToast("请选择充值数量!");
                            return;
                        }
                        getPaymentData(dataBean, payMethod);
                    }
                });
            }
        });
    }

    private int rechargeTipNum;

    private void getPaymentData(RechargeTipBean.DataBean dataBean, String payMethod) {
        HuDongApplication.getInstance().rechargeType = 1;
        rechargeTipNum = dataBean.num;
        showProgressDialog("");
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("shopid", dataBean.id);
        map.put("remark", "");
        map.put("paytype", payMethod);
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/user/answerBuy", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                dismissProgressDialog();
                RechargeTipData rechargeTipBean = new Gson().fromJson(t, RechargeTipData.class);
                if (rechargeTipBean == null || rechargeTipBean.data == null) {
                    showToast("充值数据异常!");
                    return;
                }
                switch (payMethod) {
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
                .toWXPayNotSign(getActivity());
    }

    private void startAliPay(RechargeTipData rechargeTipBean) {
        if (getActivity() == null) {
            return;
        }
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(getActivity());
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


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PayUtil.SDK_PAY_FLAG: {
                    if (getActivity() == null) {
                        return;
                    }
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
                        reStart("answer_tip_recharge_success");
                    } else {   // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        reStart("answer_tip_recharge_failure");
                    }
                    break;
                }
                default:
                    reStart("answer_tip_recharge_failure");
                    break;
            }
        }
    };


    private void getRankData() {
        showProgressDialog("");
        HashMap<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        NetUtil.getNoCache(ZConfig.SERVICE_URL + "/api/v1/answer/rank", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                Log.w("TTT","onSuccess:"+t);
                dismissProgressDialog();
                try {
                    QARankBean rankBean = new Gson().fromJson(t, QARankBean.class);
                    showRankDialog(rankBean);
                }catch (Exception e){
                    e.printStackTrace();
                    showToast("数据异常，请稍候再试!");
                }
            }
        });
    }

    private void showRankDialog(QARankBean rankBean) {
        DialogUtils.showBottomDialog(getActivity(), R.layout.dialog_rank_layout, -1, (int) (DensityUtil.getScreenHeight(getActivity()) * 0.45), new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                RecyclerView rcv_rank = view.findViewById(R.id.rcv_rank);
                TextView tv_empty = view.findViewById(R.id.tv_empty);
                View cl_root = view.findViewById(R.id.cl_root);
                if (rankBean == null || rankBean.data == null || rankBean.data.rank == null || rankBean.data.rank.isEmpty()) {
                    tv_empty.setVisibility(View.VISIBLE);
                    rcv_rank.setVisibility(View.GONE);
                    cl_root.setVisibility(View.GONE);
                    return;
                }
                if (rankBean.data.user != null) {
                    TextView tv_index = view.findViewById(R.id.tv_index);
                    ImageView iv_cover = view.findViewById(R.id.iv_cover);
                    TextView tv_title = view.findViewById(R.id.tv_title);
                    TextView tv_sub_title = view.findViewById(R.id.tv_chapter_num);
                    TextView tv_end = view.findViewById(R.id.tv_end);


                    tv_index.setText(rankBean.data.user.rank);
                    tv_end.setText(TimeUtils.getUpdateDate(rankBean.data.user.submitTime * 1000));
                    tv_sub_title.setText(rankBean.data.user.score + "分");
                    tv_title.setText(TextUtils.isEmpty(rankBean.data.user.nickname) ? "暂无" : rankBean.data.user.nickname);
                    PicassoUtils.loadImage(iv_cover,rankBean.data.user.avatar,R.drawable.icon_play_deault_bg,new CircleTransform(), DensityUtil.dip2px(40), DensityUtil.dip2px(40));
                }


                Collections.sort(rankBean.data.rank, new Comparator<QARankBean.DataBean.RankBean>() {
                    @Override
                    public int compare(QARankBean.DataBean.RankBean o1, QARankBean.DataBean.RankBean o2) {
                        if (parseInt(o1.score) > parseInt(o2.score)) {
                            return -1;
                        } else if (parseInt(o1.score) == parseInt(o2.score)) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });
                rcv_rank.setLayoutManager(new LinearLayoutManager(view.getContext()));
                RankAdapter rankAdapter = new RankAdapter();
//                rankAdapter.setCurrentUser(AccountManager.getInstance().getUserInfo().getUsername());
                rcv_rank.setAdapter(rankAdapter);
                rankAdapter.setNewData(rankBean.data.rank);
            }
        });
//        DialogUtils.showBottomDialog(getActivity(), R.layout.dialog_rank_layout, new DialogUtils.InitViewsListener() {
//            @Override
//            public void setAction(Dialog dialog, View view) {
//
//            }
//        });
    }

    private int parseInt(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    private boolean isCheckedTop = true;
    private boolean isCheckedBottom = true;

    private boolean checked1 = true;
    private boolean checked2 = true;
    private boolean checked3 = true;
    private boolean checked4 = true;

    private boolean checked5 = true;
    private boolean checked6 = true;
    private boolean checked7 = true;
    private boolean checked8 = true;

    private void showTopTip(LevelQuestionBean.DataBean dataBean, int index, boolean select) {
        LevelQuestionBean.DataBean.ChildsBean bean = dataBean.childs.get(index);
        showToast(select ? "已选中 " + bean.name : "已取消 " + bean.name);
    }

    private void click1() {
        if (checked1) {//按钮1已选中
            if (checked2 | checked3 | checked4) {
                checked1 = false;
                setBackgroundColor(false, rl_1);
                showTopTip(topData, 0, false);
            } else {
                if (isCheckedBottom) {
                    checked1 = false;
                    isCheckedTop = true;
                    setBackgroundColor(false, rl_1, cv_top_1);
                    showTopTip(topData, 0, false);
                } else {
                    showToast("至少选中一个分类");
                }
            }
        } else {//没有选中
            checked1 = true;
            isCheckedTop = true;
            setBackgroundColor(true, rl_1, cv_top_1);
            showTopTip(topData, 0, true);
        }
    }

    private void click2() {
        if (checked2) {//按钮1已选中
            if (checked1 | checked3 | checked4) {
                checked2 = false;
                setBackgroundColor(false, rl_2);
                showTopTip(topData, 1, false);
            } else {
                if (isCheckedBottom) {
                    checked2 = false;
                    isCheckedTop = false;
                    setBackgroundColor(false, rl_2, cv_top_1);
                    showTopTip(topData, 1, false);
                } else {
                    showToast("至少选中一个分类");
                }
            }
        } else {//没有选中
            checked2 = true;
            isCheckedTop = true;
            setBackgroundColor(true, rl_2, cv_top_1);
            showTopTip(topData, 1, true);
        }
    }

    private void click3() {
        if (checked3) {//按钮1已选中
            if (checked1 | checked2 | checked4) {
                checked3 = false;
                setBackgroundColor(false, rl_3);
                showTopTip(topData, 2, false);
            } else {
                if (isCheckedBottom) {
                    checked3 = false;
                    isCheckedTop = false;
                    setBackgroundColor(false, rl_3, cv_top_1);
                    showTopTip(topData, 2, false);
                } else {
                    showToast("至少选中一个分类");
                }
            }
        } else {//没有选中
            checked3 = true;
            isCheckedTop = true;
            setBackgroundColor(true, rl_3, cv_top_1);
            showTopTip(topData, 2, true);
        }
    }

    private void click4() {
        if (checked4) {//按钮1已选中
            if (checked1 | checked2 | checked3) {
                checked4 = false;
                setBackgroundColor(false, rl_4);
                showTopTip(topData, 3, false);
            } else {
                if (isCheckedBottom) {
                    checked4 = false;
                    isCheckedTop = false;
                    setBackgroundColor(false, rl_4, cv_top_1);
                    showTopTip(topData, 3, false);
                } else {
                    showToast("至少选中一个分类");
                }
            }
        } else {//没有选中
            checked4 = true;
            isCheckedTop = true;
            setBackgroundColor(true, rl_4, cv_top_1);

            showTopTip(topData, 3, true);
        }
    }

    private void click5() {
        if (checked5) {//按钮1已选中
            if (checked6 | checked7 | checked8) {
                checked5 = false;
                setBackgroundColor(false, rl_5);
                showTopTip(bottomData, 0, false);
            } else {
                if (isCheckedTop) {
                    checked5 = false;
                    isCheckedBottom = false;
                    setBackgroundColor(false, rl_5, cv_bottom_2);
                    showTopTip(bottomData, 0, false);
                } else {
                    showToast("至少选中一个分类");
                }
            }
        } else {//没有选中
            checked5 = true;
            isCheckedBottom = true;
            setBackgroundColor(true, rl_5, cv_bottom_2);
            showTopTip(bottomData, 0, true);
        }
    }

    private void click6() {
        if (checked6) {//按钮1已选中
            if (checked5 | checked7 | checked8) {
                checked6 = false;
                setBackgroundColor(false, rl_6);
                showTopTip(bottomData, 1, false);
            } else {
                if (isCheckedTop) {
                    checked6 = false;
                    isCheckedBottom = false;
                    setBackgroundColor(false, rl_6, cv_bottom_2);
                    showTopTip(bottomData, 1, false);
                } else {
                    showToast("至少选中一个分类");
                }
            }
        } else {//没有选中
            checked6 = true;
            isCheckedBottom = true;
            setBackgroundColor(true, rl_6, cv_bottom_2);
            showTopTip(bottomData, 1, true);
        }
    }

    private void click7() {
        if (checked7) {//按钮1已选中
            if (checked5 | checked6 | checked8) {
                checked7 = false;
                setBackgroundColor(false, rl_7);
                showTopTip(bottomData, 2, false);
            } else {
                if (isCheckedTop) {
                    checked7 = false;
                    isCheckedBottom = false;
                    setBackgroundColor(false, rl_7, cv_bottom_2);
                    showTopTip(bottomData, 2, false);
                } else {
                    showToast("至少选中一个分类");
                }
            }
        } else {//没有选中
            checked7 = true;
            isCheckedBottom = true;
            setBackgroundColor(true, rl_7, cv_bottom_2);
            showTopTip(bottomData, 2, true);
        }
    }

    private void click8() {
        if (checked8) {//按钮1已选中
            if (checked5 | checked6 | checked7) {
                checked8 = false;
                setBackgroundColor(false, rl_8);
                showTopTip(bottomData, 3, false);
            } else {
                if (isCheckedTop) {
                    checked8 = false;
                    isCheckedBottom = false;
                    setBackgroundColor(false, rl_8, cv_bottom_2);
                    showTopTip(bottomData, 3, false);
                } else {
                    showToast("至少选中一个分类");
                }
            }
        } else {//没有选中
            checked8 = true;
            isCheckedBottom = true;
            setBackgroundColor(true, rl_8, cv_bottom_2);
            showTopTip(bottomData, 3, true);
        }
    }

    private void clickTop() {
        if (checked1 && checked2 && checked3 && checked4) {
            if (isCheckedBottom) {
                isCheckedTop = false;
                checked1 = false;
                checked2 = false;
                checked3 = false;
                checked4 = false;
                setBackgroundColor(false, rl_1, rl_2, rl_3, rl_4, cv_top_1);
                showToast("已取消 " + topData.name);
            } else {
                showToast("至少选中一个分类");
            }
        } else {
            isCheckedTop = true;
            checked1 = true;
            checked2 = true;
            checked3 = true;
            checked4 = true;
            setBackgroundColor(true, rl_1, rl_2, rl_3, rl_4, cv_top_1);
            showToast("已选中 " + topData.name);
        }
    }

    private void clickBottom() {
        if (checked5 && checked6 && checked7 && checked8) {
            if (isCheckedTop) {
                isCheckedBottom = false;
                checked5 = false;
                checked6 = false;
                checked7 = false;
                checked8 = false;
                setBackgroundColor(false, rl_5, rl_6, rl_7, rl_8, cv_bottom_2);
                showToast("已取消 " + bottomData.name);
            } else {
                showToast("至少选中一个分类");
            }
        } else {
            isCheckedBottom = true;
            checked5 = true;
            checked6 = true;
            checked7 = true;
            checked8 = true;
            setBackgroundColor(true, rl_5, rl_6, rl_7, rl_8, cv_bottom_2);
            showToast("已选中 " + bottomData.name);
        }
    }

    private void clickStartAnswer() {
        StringBuilder sb = new StringBuilder();
        if (isCheckedTop) {//上面有选中
            sb.append(topData.id).append(",");
            if (checked1) {
                sb.append(topData.childs.get(0).id);
                sb.append(",");
            }
            if (checked2) {
                sb.append(topData.childs.get(1).id);
                sb.append(",");
            }
            if (checked3) {
                sb.append(topData.childs.get(2).id);
                sb.append(",");
            }
            if (checked4) {
                sb.append(topData.childs.get(3).id);
                sb.append(",");
            }
        }
        if (isCheckedBottom) {
            sb.append(bottomData.id).append(",");
            if (checked5) {
                sb.append(bottomData.childs.get(0).id);
                sb.append(",");
            }
            if (checked6) {
                sb.append(bottomData.childs.get(1).id);
                sb.append(",");
            }
            if (checked7) {
                sb.append(bottomData.childs.get(2).id);
                sb.append(",");
            }
            if (checked8) {
                sb.append(bottomData.childs.get(3).id);
                sb.append(",");
            }
        }

        String ids = sb.substring(0, sb.length() - 1);
        startAnswer(ids, limit);
    }


    private void setBackgroundColor(boolean isChecked, View... view) {
        int color;
        if (isChecked) {
            color = Color.parseColor("#5677FC");
        } else {
            color = Color.parseColor("#D5DDFE");
        }
        for (View v : view) {
            if (v instanceof CardView) {
                ((CardView) v).setCardBackgroundColor(color);
            } else {
                v.setBackgroundColor(color);
            }
        }
    }


    private void getDetail(String id) {
        Map<String, String> map = new HashMap<>();
        map.put("level", id);
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/answer/group", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                LevelQuestionBean levelQuestionBean = new Gson().fromJson(t, LevelQuestionBean.class);
                if (levelQuestionBean == null || levelQuestionBean.data == null || levelQuestionBean.data.isEmpty()) {
                    return;
                }
                parseData(levelQuestionBean);
            }
        });
    }

    private void parseData(LevelQuestionBean levelQuestionBean) {
        int size = levelQuestionBean.data.size();
        if (size >= 1) {
            topData = levelQuestionBean.data.get(0);
            showTopData(topData);
        }
        if (size >= 2) {
            bottomData = levelQuestionBean.data.get(1);
            showBottomData(bottomData);
        }
    }

    private void showTopData(LevelQuestionBean.DataBean dataBean) {
        tv_top_name.setText(dataBean.name);
        int size = dataBean.childs.size();
        if (size >= 1) {
            LevelQuestionBean.DataBean.ChildsBean childBean = dataBean.childs.get(0);
            tv_1_name.setText(childBean.name);
            tv_1_count.setText(childBean.count);
        }
        if (size >= 2) {
            LevelQuestionBean.DataBean.ChildsBean childBean = dataBean.childs.get(1);
            tv_2_name.setText(childBean.name);
            tv_2_count.setText(childBean.count);
        }
        if (size >= 3) {
            LevelQuestionBean.DataBean.ChildsBean childBean = dataBean.childs.get(2);
            tv_3_name.setText(childBean.name);
            tv_3_count.setText(childBean.count);
        }
        if (size >= 4) {
            LevelQuestionBean.DataBean.ChildsBean childBean = dataBean.childs.get(3);
            tv_4_name.setText(childBean.name);
            tv_4_count.setText(childBean.count);
        }
    }


    private void showBottomData(LevelQuestionBean.DataBean dataBean) {
        tv_bottom_name.setText(dataBean.name);
        int size = dataBean.childs.size();
        if (size >= 1) {
            LevelQuestionBean.DataBean.ChildsBean childBean = dataBean.childs.get(0);
            tv_5_name.setText(childBean.name);
            tv_5_count.setText(childBean.count);
        }
        if (size >= 2) {
            LevelQuestionBean.DataBean.ChildsBean childBean = dataBean.childs.get(1);
            tv_6_name.setText(childBean.name);
            tv_6_count.setText(childBean.count);
        }
        if (size >= 3) {
            LevelQuestionBean.DataBean.ChildsBean childBean = dataBean.childs.get(2);
            tv_7_name.setText(childBean.name);
            tv_7_count.setText(childBean.count);
        }
        if (size >= 4) {
            LevelQuestionBean.DataBean.ChildsBean childBean = dataBean.childs.get(3);
            tv_8_name.setText(childBean.name);
            tv_8_count.setText(childBean.count);
        }
    }

    /**
     * id集合 逗号隔开
     * limit 条数
     * level 等级 1 2 3 初 中 高
     *
     * @param ids
     * @param limit
     */
    private void startAnswer(String ids, String limit) {
        showProgressDialog("");
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("group", ids);
        map.put("limit", limit);
        map.put("level", cateId);
        NetUtil.getNoCache(ZConfig.SERVICE_URL + "/api/v1/answer", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                dismissProgressDialog();
                QuestionBean questionBean = new Gson().fromJson(t, QuestionBean.class);
                if (questionBean.data == null || questionBean.data.rows == null || questionBean.data.rows.isEmpty()) {
                    showToast(TextUtils.isEmpty(questionBean.msg) ? "数据异常" : questionBean.msg);
                    return;
                }
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof QAFragment) {
                    int count = frequency - 1;
                    ((QAFragment) parentFragment).setAnswerCount(count);
                }
                Intent intent = new Intent(getActivity(), StartAnswerActivity.class);
                intent.putExtra(StartAnswerActivity.QUESTION_DATA, questionBean.data);
                intent.putExtra(StartAnswerActivity.QUESTION_LEVEL, cateId);
                intent.putExtra(StartAnswerActivity.REMAIN_ANSWER_NUM, frequency > 0);
//                intent.putExtra(StartAnswerActivity.REMAIN_ANSWER_NUM, false);
                intent.putExtra(ANSWER_TIME, answerTime);
                startActivity(intent);
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                showToast(t);
            }
        });
    }

    private int frequency;

    public void setAnswerCount(int frequency) {
        this.frequency = frequency;
        if (tv_answer_count != null) {
            tv_answer_count.setText("今天还剩余" + frequency + "次");
        }
    }

    public void setAnswerNumConfig(List<String> frequency) {
        if (frequency == null || frequency.isEmpty()) {
            return;
        }
        if (TextUtils.isEmpty(limit)) {
            if (answerNumAdapter != null) {
                limit = frequency.get(1);
                answerNumAdapter.setNewData(frequency);
                answerNumAdapter.setCurrentNum(limit);
            }
        }
    }

    private int answerTime;

    public void setAnswerTime(int answerTime) {
        this.answerTime = answerTime;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(StartAnswerAgainBean againBean) {
        if (againBean.level.equals(cateId)) {
            clickStartAnswer();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(String str) {
        HuDongApplication.getInstance().rechargeType = 0;
        if (str.equals("answer_tip_recharge_success")) {
            String num_org_str = tv_prompt_num.getText().toString();
            int num_org = parseInt(num_org_str);
            int allTipNum = num_org + rechargeTipNum;
            showToast("恭喜您，成功充值了" + rechargeTipNum + "个问答币");
            tv_prompt_num.setText(String.valueOf(allTipNum));
        } else if (str.equals("answer_tip_recharge_failure")) {
            showToast("充值失败，请重试");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(AnswerPromptBean answerPromptBean) {
        if (answerPromptBean != null) {
            if (tv_prompt_num != null) {
                tv_prompt_num.setText(String.valueOf(answerPromptBean.answerPromptNum < 0 ? 0 : answerPromptBean.answerPromptNum));
                if (answerPromptBean.answerPromptNum < 10) {
                    cv_10.setCardBackgroundColor(Color.RED);
                    tv_recharge_title.setText("充值");
                } else {
                    cv_10.setCardBackgroundColor(Color.parseColor("#5677FC"));
                    tv_recharge_title.setText("剩余");
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(AnswerPromptSignBean answerPromptBean) {
        if (answerPromptBean != null) {
            if (tv_prompt_num != null) {
                String num_org_str = tv_prompt_num.getText().toString();
                int num_org = parseInt(num_org_str);
                int allTipNum = num_org + answerPromptBean.answerPromptNum;
                tv_prompt_num.setText(String.valueOf(allTipNum));

                if (allTipNum < 10) {
                    cv_10.setCardBackgroundColor(Color.RED);
                    tv_recharge_title.setText("充值");
                } else {
                    cv_10.setCardBackgroundColor(Color.parseColor("#5677FC"));
                    tv_recharge_title.setText("剩余");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private int promptNum;

    public void setPromptNum(int prompt) {
        this.promptNum = prompt;
        if (tv_prompt_num != null) {
            if (prompt <= 0) {
                tv_prompt_num.setText("0");
            } else {
                tv_prompt_num.setText("" + promptNum);
            }

            if (prompt < 10) {
                cv_10.setCardBackgroundColor(Color.RED);
                tv_recharge_title.setText("充值");
            } else {
                cv_10.setCardBackgroundColor(Color.parseColor("#5677FC"));
                tv_recharge_title.setText("剩余");
            }
        }
    }
}
