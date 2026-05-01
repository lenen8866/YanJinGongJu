package com.read.scriptures.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.google.android.material.tabs.TabLayout;
import com.music.player.lib.util.NetUtil;
import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.alipay.PayResult;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.BookBean;
import com.read.scriptures.bean.LevelActiveInfo;
import com.read.scriptures.bean.RechargeBean;
import com.read.scriptures.bean.RecommendPayType;
import com.read.scriptures.bean.RespInfo;
import com.read.scriptures.bean.ShareBean;
import com.read.scriptures.bean.UserInfo;
import com.read.scriptures.bean.WxpayBean;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.event.LoginOutEvent;
import com.read.scriptures.event.ReStartEvent;
import com.read.scriptures.event.RefreshDataEvent;
import com.read.scriptures.event.RefreshUserInfoEvent;
import com.read.scriptures.http.okhttp.HttpCallback;
import com.read.scriptures.http.okhttp.OkHttpUtils;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.manager.LevelInfoManager;
import com.read.scriptures.model.BookResult;
import com.read.scriptures.model.DownLoadItem;
import com.read.scriptures.model.ShareModel;
import com.read.scriptures.net.NetObserver;
import com.read.scriptures.share.wxapi.WXPayUtils;
import com.read.scriptures.ui.activity.ActiveActivity;
import com.read.scriptures.ui.activity.NoticeHistoryActivity;
import com.read.scriptures.ui.activity.PayResultActivity;
import com.read.scriptures.ui.activity.PayVipActivity;
import com.read.scriptures.ui.activity.WebViewActivity;
import com.read.scriptures.ui.activity.WeixinLoginActivity;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.LevelActiveAdapter;
import com.read.scriptures.ui.adapter.LevelActivePayTypeAdapter;
import com.read.scriptures.ui.adapter.RechargeListAdapter;
import com.read.scriptures.util.ActManager;
import com.read.scriptures.util.AmountUtils;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.DialogUtils;
import com.read.scriptures.util.DownloadQueue;
import com.read.scriptures.util.DownloadUtils;
import com.read.scriptures.util.GsonUtils;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.NetConnectUtil;
import com.read.scriptures.util.NetSocietyShare;
import com.read.scriptures.util.NotOnlineException;
import com.read.scriptures.util.PayUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SharedPreferencesUtils;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.SystemUtils;
import com.read.scriptures.util.ThreadUtil;
import com.read.scriptures.util.TimeUtils;
import com.read.scriptures.util.UmShareUtils;
import com.read.scriptures.widget.ClearEditText;
import com.read.scriptures.widget.ColorPickDialogFt;
import com.read.scriptures.widget.CustomAlertDialog;
import com.read.scriptures.widget.ListViewForScrollView;
import com.read.scriptures.widget.WelcomeProgressDialog;
import com.read.scriptures.widget.colorpicker.ColorPickerDialog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.CLIPBOARD_SERVICE;

public class FragmentActive extends Fragment implements View.OnClickListener {
    @BindView(R.id.ll_active_time)
    RelativeLayout ll_active_time;
    @BindView(R.id.ll_pay_info)
    LinearLayout ll_pay_info;
    @BindView(R.id.tv_uuid)
    EditText tv_uuid;
    @BindView(R.id.tv_uuid_txt)
    TextView tv_uuid_txt;
    @BindView(R.id.tv_version_name)
    TextView tv_version_name;
    @BindView(R.id.tv_install_time)
    TextView tv_install_time;
    @BindView(R.id.btn_copy)
    TextView tv_copy;
    @BindView(R.id.tv_donate)
    TextView tv_donate;
    @BindView(R.id.tv_active_friend)
    TextView tv_active_friend;
    @BindView(R.id.tv_update_time)
    TextView tv_update_time;
    @BindView(R.id.rl_download)
    RelativeLayout rl_download;
    @BindView(R.id.tv_share)
    TextView tv_share;
    @BindView(R.id.share_line)
    View share_line;
    @BindView(R.id.tv_update_hint)
    TextView tv_update_hint;
    @BindView(R.id.tv_level_time)
    TextView tvLevelTime;
    @BindView(R.id.cb_expand_level_time)
    CheckBox cbExpandLevelTime;
    @BindView(R.id.et_friend_id)
    EditText et_friend_id;
    @BindView(R.id.iv_clear)
    ImageView iv_clear;
    @BindView(R.id.iv_clear_edit)
    ImageView iv_clear_edit;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.loading_view)
    ProgressBar pbLoading;
    @BindView(R.id.tv_tag)
    TextView tv_tag;
    @BindView(R.id.linear_pb_loading)
    LinearLayout loading;
    Unbinder unbinder;
    @BindView(R.id.gv_level_active)
    GridView gvLevelActive;
    @BindView(R.id.tv_confirm_pay)
    TextView tvConfirmPay;
    @BindView(R.id.lv_pay_type)
    ListView lvPayType;
    @BindView(R.id.tv_download)
    TextView tvDownload;
    @BindView(R.id.rl_home_show_type)
    RelativeLayout rlHomeShowType;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.rbtn_list)
    RadioButton rbtnList;
    @BindView(R.id.rbtn_grid)
    RadioButton rbtnGrid;
    @BindView(R.id.rg_home_type)
    RadioGroup rgHomeType;
    @BindView(R.id.rbtn_category)
    RadioButton rbtnCategory;
    @BindView(R.id.rbtn_pinyin)
    RadioButton rbtnPinyin;
    @BindView(R.id.rg_home_sort_type)
    RadioGroup rgHomeSortType;
    @BindView(R.id.rg_history_search_visible)
    RadioGroup rgHistorySearchVisible;
    @BindView(R.id.rbtn_history_search_show)
    RadioButton rbtnHistorySearchShow;
    @BindView(R.id.rbtn_history_search_hide)
    RadioButton rbtnHistorySearchHide;


    @BindView(R.id.rg_short_paragraphs_visible)
    RadioGroup rgShortParagraphsVisible;
    @BindView(R.id.rbtn_short_paragraphs_show)
    RadioButton rbtnShortParagraphsShow;
    @BindView(R.id.rbtn_short_paragraphs_hide)
    RadioButton rbtnShortParagraphsHide;

    @BindView(R.id.rg_sroll_setting)
    RadioGroup rgSrollSetting;
    @BindView(R.id.rbtn_sroll_setting_left)
    RadioButton rbtnSrollSettingLeft;
    @BindView(R.id.rbtn_sroll_setting_right)
    RadioButton rbtnSrollSettingRight;

    @BindView(R.id.tv_priacy_policy)
    TextView tvPriacyPolicy;
    @BindView(R.id.tv_service_protocol)
    TextView tvServiceProtocol;
    @BindView(R.id.rl_share_app)
    RelativeLayout rl_share_app;

    @BindView(R.id.rg_font_face)
    RadioGroup rg_font_face;
    @BindView(R.id.rb_font_open)
    RadioButton rb_font_open;
    @BindView(R.id.rb_font_close)
    RadioButton rb_font_close;
    @BindView(R.id.view_text_size)
    View view_text_size;
    @BindView(R.id.btn_pay)
    View btn_pay;
    @BindView(R.id.tv_clearCahce)
    View tv_clearCahce;

    @BindView(R.id.view_line)
    View view_line;

    @BindView(R.id.tv_pay_vip)
    TextView tv_pay_vip;

    @BindView(R.id.tv_notice_history)
    TextView tv_notice_history;

    @BindView(R.id.rg_service_setting)
    RadioGroup rg_service_setting;

    public static final String PAY_TYPE_ALIPAY = "alipay";
    public static final String PAY_TYPE_WEICHAT = "wxpay";

    private String mUUid;
    private DownLoadItem mDownLoadItem = new DownLoadItem();
    private AlertDialog donateDialog;
    private AlertDialog shareDialog;
    private String shareUrl = "";
    private String shareContent = "";
    private String wxMiniId = "";
    private String wxMiniPath = "";
    private boolean active_own = true;
    ActiveActivity mActivity;
    private LevelActiveAdapter mLevelActiveMoneyAdapter;
    private LevelActivePayTypeAdapter mLevelActivePayTypeAdapter;
    private boolean mBookIsDowning;
    private CustomAlertDialog appUpdateDialog;
    private CustomAlertDialog mDownloadBookDialog;
    private int textColor;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (ActiveActivity) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (AccountManager.getInstance().getUserInfo() == null) {
            WeixinLoginActivity.launchAct(getActivity());
            getActivity().finish();
            return;
        }
        EventBus.getDefault().register(this);
        mUUid = AccountManager.getInstance().getUserInfo().getUsername();
        setUuid(mUUid);
        tv_version_name.setText("当前版本：" + SystemUtils.getVersionName(getContext()));
        tv_install_time.setText("首次安装时间：" + TimeUtils.timeStamp2DateC(AccountManager.getInstance().getUserInfo().getCreate_time()));//PreferenceConfig.getFirstInstallTime(getContext())

        mLevelActivePayTypeAdapter = new LevelActivePayTypeAdapter(getContext());
        lvPayType.setAdapter(mLevelActivePayTypeAdapter);

        initListener();
        initViews();
        queryInfo();
        initData();

    }

    private void initListener() {
        cbExpandLevelTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //展开
                    String expireTimeStr = AccountManager.getInstance().getUserInfo().getLevelExpireTimeStr();
                    tvLevelTime.setText(Html.fromHtml(expireTimeStr));
                } else {
                    //收起
                    String expireTimeStr = AccountManager.getInstance().getUserInfo().getLevelExpireTimeStr();
                    if (expireTimeStr.contains("<br/>")) {
                        expireTimeStr = expireTimeStr.substring(0, expireTimeStr.indexOf("<br/>"));
                    }
                    tvLevelTime.setText(Html.fromHtml(expireTimeStr));
                }
            }
        });
        tvLevelTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbExpandLevelTime.setChecked(!cbExpandLevelTime.isChecked());
            }
        });
        tv_version_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_version_name.getText().toString().contains("更新")) {
                    if (SystemUtils.isOnline(mActivity)) {
                        update(false);
                    } else {
                        XToast.showToast(mActivity, "请连接网络后尝试");
                    }
                }
            }
        });
        tvPriacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.launchAct(getActivity(), getResources().getString(R.string.account_login_protocol_red_two), ZConfig.H5_PRIVACY_POLICY);
            }
        });

        tvServiceProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.launchAct(getActivity(), getResources().getString(R.string.account_login_protocol_red_one), ZConfig.H5_SERVICE_PROTOCOL);
            }
        });

        rl_share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtil.doOnOtherThread(new Runnable() {
                    @Override
                    public void run() {
                        getShareData();
                    }
                });

            }
        });
        tv_clearCahce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showNormalDialog(getActivity(), "温馨提示", "确定要清除缓存并重新启动APP么？", "取消", "确定", new DialogUtils.onDialogClickListener() {
                    @Override
                    public void onCancel(Dialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onOk(Dialog dialog) {
                        dialog.dismiss();
                        ActManager.finishAllActivityAndRestart(getActivity());
                        FragmentActivity activity = getActivity();
                        if (activity instanceof BaseActivity) {
                            ((BaseActivity) activity).exit();
                        }
                    }
                });
            }
        });

        textColor = SharedUtil.getInt(PreferenceConfig.Preference_text_color_setting, Color.BLACK);
        view_text_size.setBackgroundColor(textColor == -1 ? Color.BLACK : textColor);
        view_text_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickDialogFt colorPickDialogFt = new ColorPickDialogFt();
                colorPickDialogFt.setDefaultColor(textColor == -1 ? Color.BLACK : textColor);
                colorPickDialogFt.show(getChildFragmentManager(), "ColorPickDialogFt");
                colorPickDialogFt.setCallBack(new ColorPickDialogFt.ColorSetCallBack() {
                    @Override
                    public void onColorSet(int color) {
                        SharedUtil.putInt(PreferenceConfig.Preference_text_color_setting, color);
                        v.setBackgroundColor(color);
                    }
                });
            }
        });
    }

    private void getShareData() {
        String json = NetConnectUtil.getWelContent(getActivity(), ZConfig.GETSHARE);
        ShareModel shareModel = JSONObject.parseObject(json, ShareModel.class);
        if (shareModel != null && shareModel.code == 1 && shareModel.data != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UmShareUtils.shareUrl(getActivity(), shareModel.data.title, shareModel.data.content, shareModel.data.image, shareModel.data.link);
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    XToast.showToast(mActivity, "暂无数据");
                }
            });
        }
    }


    private void initViews() {
        //书籍最后更新版本
        tv_update_time.setText(SharedPreferencesUtils.getBooklibCode(getContext()));
        if (AccountManager.getInstance().isLogin()) {
            String expireTimeStr = AccountManager.getInstance().getUserInfo().getLevelExpireTimeStr();
            if (expireTimeStr.contains("<br/>")) {
                //有多行
                cbExpandLevelTime.setVisibility(View.VISIBLE);
                if (!cbExpandLevelTime.isChecked()) {
                    //默认只显示一行
                    expireTimeStr = expireTimeStr.substring(0, expireTimeStr.indexOf("<br/>"));
                }
            } else {
                //只有一行
                cbExpandLevelTime.setVisibility(View.GONE);
            }
            //默认只显示一行数据
            tvLevelTime.setText(Html.fromHtml(expireTimeStr));
            tv_pay_vip.setVisibility(expireTimeStr.contains("截止") || expireTimeStr.contains("续费") ? View.VISIBLE : View.GONE);
        }

        tv_pay_vip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isNetWorkAvailable(v.getContext())) {
                    showMessage(v.getContext(), "请链接网络...");
                    return;
                }
                if (ll_pay_info.getVisibility() == View.VISIBLE) {
                    ll_pay_info.setVisibility(View.GONE);
                    view_line.setVisibility(View.GONE);
                } else {
                    ll_pay_info.setVisibility(View.VISIBLE);
                    view_line.setVisibility(View.VISIBLE);
                }
            }
        });
        if (vip_recharge_type != -1) {
            view_line.setVisibility(View.VISIBLE);
            ll_pay_info.setVisibility(View.VISIBLE);
        } else {
            ll_pay_info.setVisibility(View.GONE);
            view_line.setVisibility(View.GONE);
        }
        boolean isServiceOpen = PreferencesUtils.getBoolean(getActivity(), "service_is_open", true);
        rg_service_setting.check(isServiceOpen ? R.id.rb_service_setting_left : R.id.rb_service_setting_right);
        rg_service_setting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_service_setting_left://开启;
                        PreferencesUtils.putBoolean(group.getContext(), "service_is_open", true);
                        EventBus.getDefault().post("service_is_open");
                        break;
                    case R.id.rb_service_setting_right://隐藏
                        PreferencesUtils.putBoolean(group.getContext(), "service_is_open", false);
                        EventBus.getDefault().post("service_is_hide");
                        break;
                }
            }
        });
    }

    private void queryInfo() {
        initHomeShowType();

        initHomeSortType();

        initHistorySearchType();

        initShorType();
        //获取书籍更新信息
        getUpdate();

        initShare();

        update(true);
    }

    //首页列表展示方式
    boolean nowType;
    boolean originType;

    private void initHomeShowType() {
        rgHomeType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_list) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_home_list_type, true);
                    nowType = true;
                } else if (checkedId == R.id.rbtn_grid) {
                    if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                        CommonUtil.showActivateDialog(getActivity(), UserInfo.VIP_NORMAL);
                        rbtnList.setChecked(true);
                        return;
                    }
                    SharedUtil.putBoolean(PreferenceConfig.Preference_home_list_type, false);
                    nowType = false;
                }
                if (nowType != originType) {
                    //通知首页修改
                    EventBus.getDefault().post(new RefreshDataEvent());
                    originType = nowType;
                }
            }
        });
        originType = SharedUtil.getBoolean(PreferenceConfig.Preference_home_list_type, true);
        if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
            originType = true;
            SharedUtil.putBoolean(PreferenceConfig.Preference_home_list_type, true);
        }
        nowType = originType;
        if (originType) {
            rbtnList.setChecked(true);
        } else {
            rbtnGrid.setChecked(true);
        }
    }


    boolean nowSortType;
    boolean originSortType;

    private void initHomeSortType() {

        rgHomeSortType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_category) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_home_sort_type, true);
                    nowSortType = true;
                } else if (checkedId == R.id.rbtn_pinyin) {
                    if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                        CommonUtil.showActivateDialog(getActivity(), UserInfo.VIP_NORMAL);
                        rbtnCategory.setChecked(true);
                        return;
                    }
                    SharedUtil.putBoolean(PreferenceConfig.Preference_home_sort_type, false);
                    nowSortType = false;
                }
                if (nowSortType != originSortType) {
                    //通知首页修改
                    EventBus.getDefault().post(new RefreshDataEvent());
                    originSortType = nowSortType;
                }
            }
        });
        originSortType = SharedUtil.getBoolean(PreferenceConfig.Preference_home_sort_type, true);
        if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
//            originSortType = true;
            SharedUtil.putBoolean(PreferenceConfig.Preference_home_sort_type, true);
        }
        nowSortType = originSortType;
        if (originSortType) {
            rbtnCategory.setChecked(true);
        } else {
            rbtnPinyin.setChecked(true);
        }
    }


    boolean nowHistorySearchType;
    boolean originHistorySearchType;

    private void initHistorySearchType() {
        rgHistorySearchVisible.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_history_search_show) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_history_search_visible, true);
                    nowHistorySearchType = true;
                } else if (checkedId == R.id.rbtn_history_search_hide) {
                    if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                        CommonUtil.showActivateDialog(getActivity(), UserInfo.VIP_NORMAL);
                        rbtnHistorySearchShow.setChecked(true);
                        return;
                    }
                    SharedUtil.putBoolean(PreferenceConfig.Preference_history_search_visible, false);
                    nowHistorySearchType = false;
                }
                if (nowHistorySearchType != originHistorySearchType) {
                    //通知首页修改
                    EventBus.getDefault().post(new RefreshDataEvent());
                    originHistorySearchType = nowHistorySearchType;
                }
            }
        });

        originHistorySearchType = SharedUtil.getBoolean(PreferenceConfig.Preference_history_search_visible, true);
        if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
            originHistorySearchType = true;
            SharedUtil.putBoolean(PreferenceConfig.Preference_history_search_visible, true);
        }
        nowHistorySearchType = originHistorySearchType;
        if (originHistorySearchType) {
            rbtnHistorySearchShow.setChecked(true);
        } else {
            rbtnHistorySearchHide.setChecked(true);
        }
    }


    boolean nowShortParagraphsType;
    boolean originShortParagraphsType;

    private void initShorType() {
        if (rbtnShortParagraphsShow == null) {
            return;
        }
        originShortParagraphsType = SharedUtil.getBoolean(PreferenceConfig.Preference_short_paragraphs_visible, true);
        if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
            originShortParagraphsType = true;
            SharedUtil.putBoolean(PreferenceConfig.Preference_short_paragraphs_visible, true);
        }
        nowShortParagraphsType = originShortParagraphsType;
        if (originShortParagraphsType) {
            rbtnShortParagraphsShow.setChecked(true);
        } else {
            rbtnShortParagraphsHide.setChecked(true);
        }
        rgShortParagraphsVisible.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_short_paragraphs_show) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_short_paragraphs_visible, true);
                    nowShortParagraphsType = true;
                } else if (checkedId == R.id.rbtn_short_paragraphs_hide) {
                    if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                        CommonUtil.showActivateDialog(getActivity(), UserInfo.VIP_NORMAL);
                        rbtnShortParagraphsShow.setChecked(true);
                        return;
                    }
                    SharedUtil.putBoolean(PreferenceConfig.Preference_short_paragraphs_visible, false);
                    nowShortParagraphsType = false;
                }
                if (nowShortParagraphsType != originShortParagraphsType) {
                    originShortParagraphsType = nowShortParagraphsType;
                }
            }
        });

        int srollSetting = SharedUtil.getInt(PreferenceConfig.Preference_read_sroll_setting, 0);
        if (srollSetting == 0) {
            rbtnSrollSettingLeft.setChecked(true);
        } else {
            rbtnSrollSettingRight.setChecked(true);
        }

        rgSrollSetting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_sroll_setting_left) {
                    SharedUtil.putInt(PreferenceConfig.Preference_read_sroll_setting, 0);
                } else if (checkedId == R.id.rbtn_sroll_setting_right) {
                    SharedUtil.putInt(PreferenceConfig.Preference_read_sroll_setting, 1);
                }
            }
        });

        boolean isFontFace = SharedUtil.getBoolean(PreferenceConfig.Preference_font_face_setting, true);
        rb_font_open.setChecked(isFontFace);
        rb_font_close.setChecked(!isFontFace);
        rg_font_face.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_font_open) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_font_face_setting, true);
                } else if (checkedId == R.id.rb_font_close) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_font_face_setting, false);
                }
            }
        });
    }


    private void getUpdate() {
        if (SystemUtils.isOnline(mActivity)) {
            final Request request = new Request.Builder()
                    .url(ZConfig.BOOKLIBUPDATE)//请求的url
                    .get()
                    .build();
            HuDongApplication.getHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (mActivity == null) {
                        return;
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            XToast.showToast(mActivity, "获取更新信息失败，连接服务器失败");
                            if (tv_update_hint == null) {
                                return;
                            }
                            tv_update_hint.setText("获取更新信息失败");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (mActivity == null) {
                        return;
                    }
                    if (response.isSuccessful()) {
                        try {
                            BookResult result = JSONObject.parseObject(response.body().string(), BookResult.class);
                            Log.e("验证", "onResponse: " + (result.getPagess() == 1));
                            if ((result.getPagess() == 1)) {//成功
                                boolean update = false;
                                List<BookBean> list = result.getRows();
                                newBookCode = "";
                                for (BookBean bookUpdateBean : list) {
                                    if (bookUpdateBean.getTypename().getName().contains("书库")) {
                                        if (Long.valueOf(SharedPreferencesUtils.getBooklibCode(mActivity)) < Long.valueOf(bookUpdateBean.getBookcode())) {
                                            update = true;
                                            newBookCode = bookUpdateBean.getBookcode();
                                        }
                                    }
                                }
                                if (update) {//说明需要更新
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (tv_update_hint == null) {
                                                return;
                                            }
                                            tv_update_hint.setText("可更新:" + newBookCode);
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });

                                } else {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (tv_update_hint == null) {
                                                return;
                                            }
                                            tv_update_hint.setText("已是最新内容");
                                            mProgressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            } else {//失败
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (tv_update_hint == null) {
                                            return;
                                        }
                                        showMessage(getActivity(), "获取更新信息失败，连接服务器失败");
                                        XToast.showToast(mActivity, "获取更新信息失败，连接服务器失败");
                                        tv_update_hint.setText("获取更新信息失败");
                                    }
                                });

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tv_update_hint == null) {
                                        return;
                                    }
                                    showMessage(getActivity(), "获取更新信息失败，连接服务器失败");
                                    tv_update_hint.setText("获取更新信息失败");
                                }
                            });
                        }
                    }
                }
            });
        } else {
            if (tv_update_hint == null) {
                return;
            }
            tv_update_hint.setText("联网失败，请检查网络");
        }
    }

    private void initMoneyGet() {
        queryLevelActiveListInfos();
    }

    boolean update = false;
    String newBookCode = "";

    private void getUpdateMsg() {
        if (SystemUtils.isOnline(mActivity)) {
            final Request request = new Request.Builder()
                    .url(ZConfig.BOOKLIBUPDATE)//请求的url
                    .get()
                    .build();
            HuDongApplication.getHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mBookIsDowning = false;
                    if (mActivity == null) {
                        return;
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMessage(mActivity, "获取更新信息失败，连接服务器失败");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (mActivity == null) {
                        mBookIsDowning = false;
                        return;
                    }
                    if (response.isSuccessful()) {
                        try {
                            Log.e("验证", "onResponse: " + response.body().string());
                            BookResult result = JSONObject.parseObject(response.body().string(), BookResult.class);
                            Log.e("验证", "onResponse: " + result.toString());
                            if ((result.getPagess() == 1)) {//成功

                                final List<BookBean> list = result.getRows();//JSONObject.parseArray(result.get("message").toString(), BookBean.class);
//                                    List<BookUpdateBean> list = JSONObject.parseArray(response.body().string(), BookUpdateBean.class);
                                newBookCode = "";
                                for (BookBean bookUpdateBean : list) {
                                    if (bookUpdateBean.getTypename().getName().contains("书库")) {
                                        if (Long.valueOf(SharedPreferencesUtils.getBooklibCode(mActivity)) < Long.valueOf(bookUpdateBean.getBookcode())) {
                                            update = true;
                                            newBookCode = bookUpdateBean.getBookcode();
                                            break;
                                        }
                                    }
                                }
                                if (update) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showUpdate(list);
                                        }
                                    });

                                } else {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBookIsDowning = false;
                                            tv_update_hint.setText("已是最新内容");
                                            mProgressBar.setVisibility(View.GONE);
                                            showMessage(mActivity, "暂无更新");
                                        }
                                    });
                                }
                            } else {//失败
                                mActivity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        mBookIsDowning = false;
                                        if (update) {
                                            tv_update_hint.setText("可更新:" + newBookCode);
                                        } else {
                                            tv_update_hint.setText("已是最新内容");
                                        }
                                        showMessage(mActivity, "获取更新信息失败，连接服务器失败");
                                    }
                                });

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mActivity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (update) {
                                        tv_update_hint.setText("可更新:" + newBookCode);
                                    } else {
                                        tv_update_hint.setText("已是最新内容");
                                    }
                                    showMessage(mActivity, "获取更新信息失败，未知原因错误");
                                }
                            });

                        }
                    }
                }
            });
        } else {
            showMessage(mActivity, "请连接网络");
            tv_update_hint.setText("联网失败，请检查网络");
        }
    }

    private void showUpdate(final List<BookBean> list) {
        if (mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mDownloadBookDialog) {
                    String lastCode = SharedPreferencesUtils.getBooklibCode(getActivity());
                    String serviceCode = list.get(0).getBookcode();
                    mDownloadBookDialog = new CustomAlertDialog(getActivity(), "-1");
                    mDownloadBookDialog.setTitle(getString(R.string.txt_update_book));
                    mDownloadBookDialog.setMessage("当前版本" + lastCode + "\n可更新到" + serviceCode + "\n是否更新？");//R.string.txt_are_you_sure_to_update
                    mDownloadBookDialog.setPositiveButton(R.string.update, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDownloadBookDialog.dismiss();
                            if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                                CommonUtil.showActivateDialog(getActivity(), UserInfo.VIP_NORMAL);
                                mBookIsDowning = false;
                                return;
                            }
                            updateDownload(list);
                        }
                    });
                    mDownloadBookDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDownloadBookDialog.dismiss();
                            mBookIsDowning = false;
                        }
                    });
                }
                mDownloadBookDialog.show();
            }
        });
    }

    private void updateDownload(List<BookBean> list) {
        if (mActivity == null) {
            return;
        }
        final WelcomeProgressDialog dialog = new WelcomeProgressDialog(getActivity(), "下载中");
        dialog.show();
        Window mWindow = dialog.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        dialog.setMaxProgress(100);
        //  downLoadItem.setBookName(jsonItem.getString("name"));
        mDownLoadItem.setBookName(list.get(0).getTypename().getName());
        mDownLoadItem.setSize("0");
        mDownLoadItem.setTitle(list.get(0).getTypename().getName());
        mDownLoadItem.setCategory("name");
        mDownLoadItem.setType(list.get(0).getTypename().getName());
        mDownLoadItem.setCount(1);
        mDownLoadItem.setTime(String.valueOf(list.get(0).getCreate_time()));
        mDownLoadItem.setBookcode(list.get(0).getBookcode());
        mDownLoadItem.setSizeValue(0);
        mDownLoadItem.setProgressBar(dialog.getProgressBar());
        mDownLoadItem.setIntroduction("暂无简介");
        mDownLoadItem.setUrl(list.get(0).getFile());

        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                final DownloadQueue downloadQueue = new DownloadQueue(mDownLoadItem);
                downloadQueue.setType(0);
                try {
                    boolean isSuccess = downloadQueue.downloadFiles(getActivity(), getActivity(), dialog.getTvTitle());
                    mBookIsDowning = false;
                    if (getActivity() == null) {
                        return;
                    }
                    if (!isSuccess) {
                        mDownLoadItem.setState(1);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                        return;
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });

                    SharedPreferencesUtils.saveBooklibCode(HuDongApplication.getInstance(), mDownLoadItem.getBookcode());
                    SharedPreferencesUtils.saveBooklibName(HuDongApplication.getInstance(), mDownLoadItem.getBookName());
                    mDownLoadItem.setState(1);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SystemConstants.isUpdateBook = true;
                            //重启
                            EventBus.getDefault().post(new ReStartEvent());

                            final Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();

                        }
                    });

                } catch (NotOnlineException e) {
                    if (getActivity() == null) {
                        return;
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBookIsDowning = false;
                            dialog.dismiss();
                            showMessage(getActivity(), "与服务器连接不正确");
                        }
                    });
                }
            }
        });

    }

    private void initData() {
        showLevelActiveListInfos();
        showCheckPayType();
        initMoneyGet();
        queryRecommendPayType();
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
        MobclickAgent.onPageStart(getClass().getName());

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mActivity = null;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    @OnClick({R.id.btn_copy, R.id.tv_donate, R.id.tv_notice_history, R.id.tv_active_friend, R.id.rl_download, R.id.tv_share, R.id.iv_clear, R.id.tv_confirm_pay, R.id.iv_clear_edit, R.id.btn_pay})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tv_notice_history:
                startActivity(new Intent(getActivity(), NoticeHistoryActivity.class));
                break;
            case R.id.btn_pay:
                startActivity(new Intent(getActivity(), PayVipActivity.class));
                break;
            case R.id.btn_copy:
                if (active_own) {
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                    if (cm != null) {
                        ClipData cd = ClipData.newPlainText("share_content", mUUid);
                        cm.setPrimaryClip(cd);
                        showMessage(getContext(), "复制成功");
                    }
                } else {
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                    if (cm != null && cm.hasPrimaryClip()) {
                        ClipData data = cm.getPrimaryClip();
                        if (data != null) {
                            ClipData.Item item = data.getItemAt(0);
                            String content = item.getText().toString();
                            tv_uuid.setText(content);
                            tv_uuid.setSelection(content.length());
                        }
                    }
                }
                break;
            case R.id.tv_donate:
//                showDonateDialog(getContext());
                showRechargeDialog();
                break;
            case R.id.tv_active_friend://帮好友开通
                active_own = !active_own;
                if (active_own) {
                    tv_active_friend.setText("帮好友开通");
                    tv_uuid.setEnabled(false);
                    tv_uuid.setText(mUUid);
                    tv_uuid_txt.setText("本机ID账号：");
                    iv_clear_edit.setVisibility(View.GONE);
                    tv_copy.setText("复制");
                } else {
                    tv_active_friend.setText("本机开通");
                    tv_uuid.setEnabled(true);
                    tv_uuid.setHint("请输入对方的ID账号");
                    tv_uuid_txt.setText("好友ID账号：");
                    tv_uuid.setText("");
                    iv_clear_edit.setVisibility(View.VISIBLE);
                    tv_copy.setText("粘贴");
                }
                break;
            case R.id.rl_download:
                if (mBookIsDowning) {
                    return;
                }
                mBookIsDowning = true;
                getUpdateMsg();
                break;
            case R.id.tv_share:
                if (shareDialog != null) {
                    shareDialog.show();
                    ViewGroup.LayoutParams linearParams = (ViewGroup.LayoutParams) mCardView.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
                    linearParams.height = mActivity.getWindowManager().getDefaultDisplay().getHeight() / 3;
                    mCardView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                }
                break;
            case R.id.iv_clear:
                et_friend_id.setText("");
                break;
            case R.id.iv_clear_edit:
                tv_uuid.setText("");
                break;
            case R.id.tv_confirm_pay:
                //确认支付
                confirmPay();
                break;
//            case R.id.rl_home_show_type:
//                //首页展示方式修改
//                showHomeShowTypeDialog(getActivity());
//                break;
            default:
                break;
        }
    }

    int lastIndex = 0;

    private void showRechargeDialog() {
        DialogUtils.showCenterDialog(getActivity(), R.layout.dialog_recharge, DensityUtil.dip2px(getActivity(), 320), -2, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                EditText tvEditText = view.findViewById(R.id.et_donate_number);
                TextView tv_confirm_pay = view.findViewById(R.id.tv_confirm_pay);
                CheckBox rb_wx_pay = view.findViewById(R.id.rb_wx_pay);
                CheckBox rb_ali_pay = view.findViewById(R.id.rb_ali_pay);
                RecyclerView rcv_list = view.findViewById(R.id.rcv_list);
                rcv_list.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
                RechargeListAdapter rechargeListAdapter = new RechargeListAdapter();
                rcv_list.setAdapter(rechargeListAdapter);
                rechargeListAdapter.setOnItemClickListener(new RechargeListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ArrayList<RechargeBean> rechargeBeans, int position) {
                        if (lastIndex != -1) {
                            rechargeBeans.get(lastIndex).isSelected = false;
                        }
                        rechargeBeans.get(position).isSelected = true;
                        lastIndex = position;
                        rechargeListAdapter.notifyDataSetChanged();
                        tvEditText.setText(rechargeBeans.get(position).price);
                        tvEditText.setSelection(tvEditText.length());
                    }
                });
                tvEditText.setText(rechargeListAdapter.getRechargeBeans().get(0).price);
                tvEditText.setSelection(tvEditText.length());
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
                view.findViewById(R.id.tv_confirm_pay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String payType = PAY_TYPE_WEICHAT;
                        if (rb_wx_pay.isChecked()) {
                            payType = PAY_TYPE_WEICHAT;
                        } else if (rb_ali_pay.isChecked()) {
                            payType = PAY_TYPE_ALIPAY;
                        } else {
                            showMessage(mActivity, "请选择支付方式!");
                            return;
                        }
                        if (TextUtils.isEmpty(tvEditText.getText().toString().trim())) {
                            showMessage(getContext(), "金额不能为空");
                        } else if (CommonUtil.formatMoney(tvEditText.getText().toString().trim(), 2).equals("￥0.00")) {
                            showMessage(getContext(), "亲，真的捐0元吗？请您慷慨解囊...");
                        } else if (Float.valueOf(tvEditText.getText().toString().trim()) > 999999.99) {
                            showMessage(getContext(), "对不起，单次超过捐款限额！");
                        } else {
                            if (SystemConstants.isActive) {
                                return;
                            }
                            SystemConstants.isActive = true;//正在激活
                            donation(payType, "0", tvEditText.getText().toString().trim());
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * 无偿捐赠弹框
     *
     * @param context
     */
    private void showDonateDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_donate, null);
        final ClearEditText tvEditText = view.findViewById(R.id.et_donate_number);
        TextView tvClose = view.findViewById(R.id.tv_close);
        ListViewForScrollView lvPayType = view.findViewById(R.id.lv_pay_type);
        lvPayType.setAdapter(mLevelActivePayTypeAdapter);
        tvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tvEditText.getText().toString().trim().indexOf(".") != -1) {
                    if (tvEditText.getText().toString().trim().substring(tvEditText.getText().toString().trim().indexOf(".") + 1).length() > 1) {
                        //多余2位小数
                        tvEditText.setText(tvEditText.getText().toString().trim().substring(0, tvEditText.getText().toString().trim().indexOf(".") + 2));
                        tvEditText.setSelection(tvEditText.getText().toString().length());
                    }
                }
            }
        });
        view.findViewById(R.id.tv_confirm_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(tvEditText.getText().toString().trim())) {
                    showMessage(getContext(), "金额不能为空");
                } else if (CommonUtil.formatMoney(tvEditText.getText().toString().trim(), 2).equals("￥0.00")) {
                    showMessage(getContext(), "亲，真的捐0元吗？请您慷慨解囊...");
                } else if (Float.valueOf(tvEditText.getText().toString().trim()) > 999999.99) {
                    showMessage(getContext(), "对不起，单次超过捐款限额！");
                } else {
                    String payType = PAY_TYPE_ALIPAY;
                    if (mLevelActivePayTypeAdapter.getCurrentCheckedType().equals(PAY_TYPE_WEICHAT)) {
                        payType = PAY_TYPE_WEICHAT;
                    }
                    if (SystemConstants.isActive)
                        return;
                    SystemConstants.isActive = true;//正在激活
                    donation(payType, "0", tvEditText.getText().toString().trim());

                }
            }
        });


        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donateDialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        donateDialog = builder.create();
        donateDialog.setCanceledOnTouchOutside(false);
        donateDialog.show();
    }


    private void initShare() {
        if (SystemUtils.isOnline(getContext())) {
            ThreadUtil.doOnOtherThread(new Runnable() {
                public void run() {
                    String json = NetConnectUtil.getContent(getContext(), ZConfig.SHARE_SETTING_URL, 1);
                    if (StringUtil.isEmpty(json)) {
                        if (mActivity == null) {
                            return;
                        }
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_share.setVisibility(View.GONE);
                                share_line.setVisibility(View.GONE);
                            }
                        });
                        return;
                    }
                    try {
                        final ShareBean shareBean = JSONObject.parseObject(json, ShareBean.class);
                        if ("true".equals(shareBean.getOpen())) {//打开分享
                            if (mActivity == null) {
                                return;
                            }
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tv_share == null) {
                                        return;
                                    }
                                    tv_share.setVisibility(View.VISIBLE);
                                    share_line.setVisibility(View.VISIBLE);
                                    initShareDialog(shareBean);
                                }
                            });
                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tv_share == null) {
                                        return;
                                    }
                                    tv_share.setVisibility(View.GONE);
                                    share_line.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (Exception e) {
                        if (mActivity == null) {
                            return;
                        }
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tv_share == null) {
                                    return;
                                }
                                tv_share.setVisibility(View.GONE);
                                share_line.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        } else {
            if (tv_share == null) {
                return;
            }
            tv_share.setVisibility(View.GONE);
            share_line.setVisibility(View.GONE);
        }
    }

    ImageView logo;
    FrameLayout close;
    TextView title;
    TextView content;
    LinearLayout button1;
    LinearLayout button2;
    LinearLayout button3;
    CardView mCardView;

    private void initShareDialog(ShareBean shareBean) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_share, null);
        logo = (ImageView) view.findViewById(R.id.logo);
        title = (TextView) view.findViewById(R.id.title);
        content = (TextView) view.findViewById(R.id.content);
        button1 = (LinearLayout) view.findViewById(R.id.button1);
        button2 = (LinearLayout) view.findViewById(R.id.button2);
        button3 = (LinearLayout) view.findViewById(R.id.button3);
        close = (FrameLayout) view.findViewById(R.id.close);
        mCardView = (CardView) view.findViewById(R.id.cardview);

        if (StringUtil.isEmpty(shareBean.getTitle()) && StringUtil.isEmpty(shareBean.getBody())) {
            //标题和内容都为空
            title.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
        } else if (StringUtil.isEmpty(shareBean.getTitle())) {
            title.setVisibility(View.GONE);
            content.setText("\u3000\u3000" + shareBean.getBody());
            content.setMovementMethod(ScrollingMovementMethod.getInstance());
        } else if (StringUtil.isEmpty(shareBean.getBody())) {
            title.setText(shareBean.getTitle());
            content.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
            title.setText(shareBean.getTitle());
            content.setText("\u3000\u3000" + shareBean.getBody());
            content.setMovementMethod(ScrollingMovementMethod.getInstance());
        }

        if (StringUtil.isEmpty(shareBean.getImg_logo())) {
            logo.setVisibility(View.GONE);
        } else {
            if (getActivity() == null) {
                return;
            }
            PicassoUtils.loadImage(logo, shareBean.getImg_logo(), R.drawable.ic_launcher);
        }

        if ("1".equals(shareBean.getButton_WeChat_friend())) {
            button1.setVisibility(View.VISIBLE);
        } else {
            button1.setVisibility(View.GONE);
            view.findViewById(R.id.dex_line1).setVisibility(View.GONE);
        }
        if ("1".equals(shareBean.getButton_WeChat_friends())) {
            button2.setVisibility(View.VISIBLE);
        } else {
            button2.setVisibility(View.GONE);
            view.findViewById(R.id.dex_line1).setVisibility(View.GONE);
        }
        if ("1".equals(shareBean.getButton_web())) {
            button3.setVisibility(View.VISIBLE);
        } else {
            button3.setVisibility(View.GONE);
            view.findViewById(R.id.dex_line2).setVisibility(View.GONE);
        }
        shareUrl = shareBean.getUrl();
        shareContent = shareBean.getFriends_content();
        wxMiniId = shareBean.getWx_mini_app_id();
        wxMiniPath = shareBean.getWx_mini_app_path();

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
//        button4.setOnClickListener(this);
        close.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        shareDialog = builder.create();
        shareDialog.setCanceledOnTouchOutside(false);
        shareDialog.setCancelable(false);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
            case R.id.button2:
                String strPackageName = "";
                String strActivityName = "";
                switch (view.getId()) {
                    case R.id.button1:
                        strPackageName = "com.tencent.mm";
                        strActivityName = "com.tencent.mm.ui.tools.ShareImgUI";
                        break;
                    case R.id.button2:
                        strPackageName = "com.tencent.mm";
                        strActivityName = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
                        break;
                }
                if (strPackageName != null) {
                    String strUrl = shareUrl;
                    String strContent = shareContent;
                    if (!NetSocietyShare.share(getContext(), strPackageName, strActivityName, strUrl,
                            strContent, "研经工具")) {
                        showMessage(getContext(), "未安装此应用");
                    }
                }
                break;
            case R.id.button3:
                if (StringUtil.isEmpty(wxMiniId)) {
                    showMessage(getContext(), "暂无可打开信息");
                    return;
                }
                strPackageName = "com.tencent.mm";
                if (getActivity() == null) {
                    return;
                }
                if (!NetSocietyShare.goWechatMini(getActivity(), strPackageName, wxMiniId, wxMiniPath)) {
                    showMessage(getContext(), "未安装此应用");
                }
                break;
            case R.id.close:
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                break;
        }
    }

    //===================================调整后==========================

    private void showLevelActiveListInfos() {
        if (ll_pay_info == null) {
            return;
        }
        List<String> tabList = LevelInfoManager.getInstance().getTabList();
        if (tabList == null || tabList.size() == 0 || !AccountManager.getInstance().isLogin()) {
//            //没获取到会员价格信息 或 用户未登录 或 开启了免费使用
//            ll_pay_info.setVisibility(View.GONE);
//            view_line.setVisibility(View.GONE);
            return;
        } else {
//            ll_pay_info.setVisibility(View.VISIBLE);
//            view_line.setVisibility(View.VISIBLE);
        }
        final List<LevelActiveInfo> levelActiveInfos = LevelInfoManager.getInstance().getLevelPriceLise(tabList.get(0));
        tabLayout.removeAllTabs();
        for (String tab : tabList) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        if (vip_recharge_type != -1) {
            tabLayout.getTabAt(vip_recharge_type).select();
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mLevelActiveMoneyAdapter.setCurrentCheckedIndex(0);
                mLevelActiveMoneyAdapter.addAllList(LevelInfoManager.getInstance().getLevelPriceLise(tab.getText().toString()));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mLevelActiveMoneyAdapter = new LevelActiveAdapter(getContext(), levelActiveInfos);
        mLevelActiveMoneyAdapter.setOnItemClickListener(new LevelActiveAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position, LevelActiveInfo levelActiveInfo) {
                tvConfirmPay.setText("确认支付 " + AmountUtils.getAmountToStr(levelActiveInfo.getPayPrice()) + "元");
            }
        });
        gvLevelActive.setAdapter(mLevelActiveMoneyAdapter);
        mLevelActiveMoneyAdapter.setCurrentCheckedIndex(0);
        tvConfirmPay.setText("确认支付 " + AmountUtils.getAmountToStr(mLevelActiveMoneyAdapter.getItem(0).getPayPrice()) + "元");
    }

    /**
     * 展示支付方式
     */
    private void showCheckPayType() {
        RecommendPayType recommendPayType = LevelInfoManager.getInstance().getRecommendPayType();
        if (recommendPayType == null) {
            return;
        }
        mLevelActivePayTypeAdapter.setCurrentCheckedType(recommendPayType.getPay_name());
    }

    /**
     * 请求会员激活信息
     */
    private void queryLevelActiveListInfos() {
        LevelInfoManager.getInstance().queryLevelActiveListInfos(new HttpCallback<RespInfo<LinkedHashMap<String, List<LevelActiveInfo>>>>() {
            @Override
            public void onSuccess(final RespInfo<LinkedHashMap<String, List<LevelActiveInfo>>> result) {
                if (getActivity() == null) {
                    return;
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //更新用户信息
                        showLevelActiveListInfos();
                    }
                });
            }

            @Override
            public void onError(int code, String errorMsg) {

            }

            @Override
            public void onFinish() {

            }
        });

    }


    /**
     * 请求支付推荐方式
     */
    private void queryRecommendPayType() {
        LevelInfoManager.getInstance().queryRecommendPayType(new HttpCallback<RespInfo<RecommendPayType>>() {
            @Override
            public void onSuccess(final RespInfo<RecommendPayType> result) {
                if (getActivity() == null) {
                    return;
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showCheckPayType();
                    }
                });
            }

            @Override
            public void onError(int code, String errorMsg) {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 确认支付
     */
    private void confirmPay() {
        if (mLevelActiveMoneyAdapter == null || mLevelActiveMoneyAdapter.isEmpty()) {
            showMessage(getContext(), "数据有误");
            return;
        }

        String payType;
        LevelActiveInfo currentCheckedActiveInfo = mLevelActiveMoneyAdapter.getItem(mLevelActiveMoneyAdapter.getCurrentCheckedIndex());

        if (mLevelActivePayTypeAdapter.getCurrentCheckedType().equals(PAY_TYPE_ALIPAY)) {
            payType = PAY_TYPE_ALIPAY;
        } else {
            payType = PAY_TYPE_WEICHAT;
        }

        //支付宝支付
        if (SystemConstants.isActive)
            return;
        SystemConstants.isActive = true;//正在激活
        newOrder(payType, currentCheckedActiveInfo.getDescStr(), mLevelActiveMoneyAdapter.getItem(mLevelActiveMoneyAdapter.getCurrentCheckedIndex()).getPayPrice() + "", "v" + currentCheckedActiveInfo.getVip_level().getVal(), currentCheckedActiveInfo.getId() + "");

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(RefreshUserInfoEvent refreshDataEvent) {
        if (AccountManager.getInstance().getUserInfo() != null) {
            initViews();
        } else {
            if (getActivity() == null) {
                return;
            }
            getActivity().finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(LoginOutEvent loginOutEvent) {
        if (getActivity() == null) {
            return;
        }
        WeixinLoginActivity.launchAct(getActivity());
        getActivity().finish();
    }

    //版本更新
    public void update(final boolean isInit) {
        if (getActivity() == null) {
            return;
        }
        if (SystemUtils.isOnline(getActivity())) {
            ThreadUtil.doOnOtherThread(new Runnable() {
                public void run() {
                    final String oldVersion = SystemUtils.getVersionName(getActivity());
                    String json = NetConnectUtil.getContent(getActivity(), ZConfig.UPDATE, 3);
                    if (StringUtil.isEmpty(json)) {
                        return;
                    }
                    final JSONObject jsonObject = JSON.parseObject(json);
                    if (!jsonObject.containsKey("data")) {
                        return;
                    }

                    final JSONObject dataJson = JSON.parseObject(JSON.toJSONString(jsonObject.get("data")));
                    if (dataJson == null) {
                        return;
                    }

                    if (Float.valueOf(oldVersion) > Float.valueOf(dataJson.getString("version_code")))
                        return;
                    if (!oldVersion.equals(dataJson.getString("version_code"))) {//需要更新
                        if (tv_version_name == null) {
                            return;
                        }
                        if (dataJson.getString("is_update").equals("1")) {
                            if (getActivity() == null) {
                                return;
                            }
                            SharedPreferencesUtils.saveUpdateVersionName(getActivity(), dataJson.getString("version_code"));//保存版本号
                            SharedPreferencesUtils.saveUpdateForce(getActivity(), true);//需要强制更新
                            SharedPreferencesUtils.saveUpdateVersionContent(getActivity(), dataJson.getString("remark"));
                            //新版本与本机版本不时，提示更新
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isInit) {
                                        //展示
                                        tv_version_name.setText("当前版本：" + SystemUtils.getVersionName(getContext()) + "  点击更新");
                                        return;
                                    }
                                    if (null == appUpdateDialog && !getActivity().isDestroyed()) {
                                        appUpdateDialog = new CustomAlertDialog(getActivity(), dataJson.getString("web_url"));
                                        appUpdateDialog.setCancelable(false);
                                        appUpdateDialog.setTitle("程序升级");
                                        appUpdateDialog.setMessage("当前：" + oldVersion + "----> " + dataJson.getString("version_code") + "\n\n" + dataJson.getString("remark"));
                                        appUpdateDialog.setPositiveButton("现在更新", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                appUpdateDialog.dismiss();
                                                ThreadUtil.doOnOtherThread(new Runnable() {
                                                    public void run() {
                                                        try {
                                                            DownloadUtils.downloadApk(getActivity(), dataJson.getString("dow_url"));
                                                        } catch (NotOnlineException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    if (!appUpdateDialog.isShowing()) {
                                        appUpdateDialog.show();
                                    }

                                }
                            });
                        } else if (dataJson.getString("is_update").equals("0")) {
                            if (getActivity() == null) {
                                return;
                            }
                            //判断今天是不是首次弹
                            PreferencesUtils.getString(getActivity(), "is_update").equals("0");
                            SharedPreferencesUtils.saveUpdateForce(getActivity(), false);//需要强制更新

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isInit) {
                                        //展示
                                        tv_version_name.setText("当前版本：" + SystemUtils.getVersionName(getContext()) + "  点击更新");
                                        return;
                                    }
                                    if (null == appUpdateDialog && !getActivity().isDestroyed()) {
                                        appUpdateDialog = new CustomAlertDialog(getActivity(), dataJson.getString("web_url"));
                                        appUpdateDialog.setCancelable(false);
                                        appUpdateDialog.setTitle("程序升级");
                                        appUpdateDialog.setMessage("当前：" + oldVersion + "----> " + dataJson.getString("version_code") + "\n\n" + dataJson.getString("remark"));
                                        String confrim = "现在更新";
                                        appUpdateDialog.setPositiveButton(confrim, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                appUpdateDialog.dismiss();
                                                ThreadUtil.doOnOtherThread(new Runnable() {
                                                    public void run() {
                                                        try {
                                                            DownloadUtils.downloadApk(getActivity(), dataJson.getString("dow_url"));
                                                        } catch (NotOnlineException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        appUpdateDialog.setNegativeButton("取消", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                appUpdateDialog.dismiss();
                                            }
                                        });
                                    }
                                    if (!appUpdateDialog.isShowing()) {
                                        appUpdateDialog.show();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else {
            if (getActivity() == null) {
                return;
            }
            final String oldVersion = SystemUtils.getVersionName(getActivity());
            if (!SharedPreferencesUtils.getUpdateVersionName(getActivity()).equals(oldVersion) && SharedPreferencesUtils.getUpdateForce(getActivity())) {//版本号不同
                if (null == appUpdateDialog && !getActivity().isDestroyed()) {
                    appUpdateDialog = new CustomAlertDialog(getActivity(), "-1");
                    appUpdateDialog.setCancelable(false);
                    appUpdateDialog.setTitle("软件更新提示");
                    appUpdateDialog.setMessage("对不起，请链接网络更新APP\n本次更新：\n" + SharedPreferencesUtils.getUpdateVersionContent(getActivity()));
                    appUpdateDialog.setPositiveButton("退出", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appUpdateDialog.dismiss();
                            HuDongApplication.getInstance().exitApp();
                        }
                    });
                }
            }
        }
    }


    //========================================================调整下单================================
    String apliy_fee;
    String days;
    String levelType;

    private void newOrder(final String payType, String days, String money, String levelType, String shopId) {
        if (AccountManager.getInstance().getUserInfo() == null) {
            return;
        }
        this.apliy_fee = money;
        this.days = days;
        this.levelType = levelType;
        if (payType.equals(PAY_TYPE_WEICHAT)) {
            //赋值
            SystemConstants.WX_DAYS = days;
            SystemConstants.WX_MONEY = money;
            SystemConstants.WX_UUID = mUUid;
            SystemConstants.WX_LEVEL_TYPE = levelType;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("token", AccountManager.getInstance().getUserInfo().getToken());
        params.put("shopid", shopId);
        params.put("paytype", payType);
        params.put("version", String.valueOf(SystemUtils.getVersionName(HuDongApplication.getInstance())));
        OkHttpUtils.getInstance().post(ZConfig.PAY_NEW_ORDER, params, new HttpCallback<RespInfo<HashMap<String, String>>>() {
            @Override
            public void onSuccess(final RespInfo<HashMap<String, String>> data) {
                if (mActivity == null) {
                    return;
                }
                mActivity.runOnUiThread(new Runnable() {
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
                if (getActivity() == null) {
                    return;
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SystemConstants.isActive = false;
                        showMessage(getActivity().getApplicationContext(), errorMsg);
                    }
                });
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 无偿捐赠
     */
    private void donation(final String payType, String days, String money) {
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
        params.put("token", AccountManager.getInstance().getUserInfo().getToken());
        OkHttpUtils.getInstance().post(ZConfig.DONATION, params, new HttpCallback<RespInfo<HashMap<String, String>>>() {
            @Override
            public void onSuccess(final RespInfo<HashMap<String, String>> data) {
                if (mActivity == null) {
                    return;
                }
                mActivity.runOnUiThread(new Runnable() {
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
                if (mActivity == null) {
                    return;
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SystemConstants.isActive = false;
                        showMessage(getActivity().getApplicationContext(), errorMsg);
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
        if (mActivity == null) {
            return;
        }
        SystemConstants.isActive = false;
        if (TextUtils.isEmpty(PayUtil.APPID) || TextUtils.isEmpty(PayUtil.RSA_PRIVATE)) {
            new AlertDialog.Builder(mActivity).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
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
                        PreferenceConfig.savePayMoney(getActivity(), Float.valueOf(apliy_fee));
                        showMessage(getActivity(), "支付成功");
                        //重新获取激活时间
//                        ThreadUtil.doOnOtherThread(new Runnable() {
//                            @Override
//                            public void run() {
                        AccountManager.getInstance().refreshUserInfo(new AccountManager.IAccountManagerListener() {
                            @Override
                            public void requestResult(boolean isSuccess, String errMsg) {
                                if (mActivity == null) {
                                    return;
                                }
                                mActivity.runOnUiThread(new Runnable() {
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
                    if (getActivity() == null) {
                        return;
                    }
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

    private void setUuid(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (str.length() > 3) {
            stringBuilder.append(str.substring(0, 3)).append(" ");
        }
        if (str.length() > 6) {
            stringBuilder.append(str.substring(3, 6)).append(" ");
        }
        if (str.length() >= 10) {
            stringBuilder.append(str.substring(6)).append(" ");
        }
//        String uuid = str.replaceAll(".{3}(?!$)", "$0 ");
        tv_uuid.setText(stringBuilder.toString());
    }

    public void netStatusChange(NetObserver.NetAction action) {
        if (action.isAvailable()) {
            queryInfo();
        }
        if (SystemUtils.isOnline(getContext())) {
            tv_donate.setVisibility(View.VISIBLE);
        } else {
            tv_donate.setVisibility(View.GONE);
        }
    }

    protected void showMessage(Context activity, String str) {
        if (activity != null && !TextUtils.isEmpty(str)) {
            XToast.showToast(activity, str);
        }
    }

    private int vip_recharge_type = -1;

    public void setVipRechargeType(int vip_recharge_type) {
        this.vip_recharge_type = vip_recharge_type;
    }
}
