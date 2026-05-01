package com.read.scriptures.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.read.scriptures.R;
import com.read.scriptures.adapter.SignListAdapter;
import com.read.scriptures.bean.AnswerPromptSignBean;
import com.read.scriptures.bean.SignBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.DialogUtils;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

//问答
public class QuestionActivity extends BaseActivity {

    private ImageView iv_search;
    private ImageView iv_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_wenda);
        StatusBarUtils.initMainColorStatusBar(this);
        findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_search = findViewById(R.id.iv_search);
        boolean isVoice = PreferencesUtils.getBoolean(QuestionActivity.this, "question_voice", true);
        iv_search.setImageResource(isVoice ? R.drawable.icon_voice1 : R.drawable.icon_voice);

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVoice = PreferencesUtils.getBoolean(QuestionActivity.this, "question_voice", true);
                if (isVoice) {
                    showToast("声音已关闭");
                    iv_search.setImageResource(R.drawable.icon_voice);
                    PreferencesUtils.putBoolean(QuestionActivity.this, "question_voice", false);
                } else {
                    showToast("声音已开启");
                    iv_search.setImageResource(R.drawable.icon_voice1);
                    PreferencesUtils.putBoolean(QuestionActivity.this, "question_voice", true);
                }
            }
        });

        iv_sign = findViewById(R.id.iv_sign);
        iv_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign();
            }
        });
        findViewById(R.id.iv_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.launchAct(QuestionActivity.this, "问答规则", "https://book.sdacn.cn/wenda/index.html");
            }
        });
        getSignInfo(false);
    }

    private void sign() {
        getSignInfo(true);
    }

    private boolean isSigned;

    private void getSignInfo(boolean showDialog) {
        if (showDialog) {
            showProgressDialog("");
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("type", String.valueOf(1));
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/answer/mark", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (showDialog) {
                    dismissProgressDialog();
                }
                SignBean signBean = new Gson().fromJson(t, SignBean.class);
                if (signBean == null || signBean.data == null) {
                    return;
                }
                isSigned = signBean.data.status == 1;
                iv_sign.setImageResource(isSigned ? R.drawable.icon_signed : R.drawable.icon_sign);
                if (showDialog && signBean.data.week != null && !signBean.data.week.isEmpty()) {
                    showSignDialog(signBean.data);
                }
                if (showDialog && isSigned) {
                    showToast("您已签到！");
                }
            }
        });
    }

    private Dialog dialog;
    private SignListAdapter signListAdapter;

    private void showSignDialog(SignBean.DataBean data) {
        if (dialog != null && dialog.isShowing() && signListAdapter != null) {
            signListAdapter.setNewData(data.week);
            return;
        }
        dialog = DialogUtils.showCenterDialog(QuestionActivity.this, R.layout.dialog_sign_layout, DensityUtil.dip2px(QuestionActivity.this, 320), -2, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                RecyclerView rcv_sign_list = view.findViewById(R.id.rcv_sign_list);
                TextView tv_tips = view.findViewById(R.id.tv_title);

                GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 4);

                rcv_sign_list.setLayoutManager(gridLayoutManager);
                signListAdapter = new SignListAdapter();
                rcv_sign_list.setAdapter(signListAdapter);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return position == 6 ? 2 : 1;
                    }
                });
                signListAdapter.setNewData(data.week);
                signListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                        SignBean.DataBean.WeekBean item = signListAdapter.getItem(position);
                        if (item.status != 1 && item.date.equals(TimeUtils.getDateSp())) {//今天
                            Map<String, String> map = new HashMap<>();
                            map.put("token", AccountManager.getInstance().getUserInfo().getToken());
                            map.put("type", String.valueOf(0));
                            NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/answer/mark", map, new NetUtil.CallBack() {
                                @Override
                                public void onSuccess(String t) {
                                    SignBean signBean = new Gson().fromJson(t, SignBean.class);
                                    if (signBean == null || signBean.data == null) {
                                        return;
                                    }
                                    isSigned = signBean.data.status == 1;
                                    iv_sign.setImageResource(isSigned ? R.drawable.icon_signed : R.drawable.icon_sign);
                                    item.status = 1;
                                    item.prompt = signBean.data.prompt;
                                    signListAdapter.notifyItemChanged(position);
                                    tv_tips.setVisibility(View.VISIBLE);
                                    tv_tips.setText("恭喜签到成功，得到" + item.num + "个币!");
                                    AnswerPromptSignBean answerPromptBean = new AnswerPromptSignBean();
                                    answerPromptBean.answerPromptNum = Integer.parseInt(signBean.data.prompt);
                                    EventBus.getDefault().post(answerPromptBean);
                                    getSignInfo(false);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isVoice = PreferencesUtils.getBoolean(QuestionActivity.this, "question_voice", true);
        iv_search.setImageResource(isVoice ? R.drawable.icon_voice1 : R.drawable.icon_voice);

    }
}
