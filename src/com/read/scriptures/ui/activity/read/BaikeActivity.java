package com.read.scriptures.ui.activity.read;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.read.scriptures.EIUtils.DateUtil;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.UserInfo;
import com.read.scriptures.broadcast.PlayBroadcastReceiver;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.control.BaiduSpeechManager;
import com.read.scriptures.event.LoginOutEvent;
import com.read.scriptures.event.PlayEvent;
import com.read.scriptures.manager.XunFeiSpeechManager;
import com.read.scriptures.model.Baike;
import com.read.scriptures.net.NetObserver;
import com.read.scriptures.service.NotifacationService;
import com.read.scriptures.ui.activity.ChapterReaderActivity;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.BackgroundColorAdapter;
import com.read.scriptures.ui.adapter.BaikeReadSlidingAdapter;
import com.read.scriptures.ui.adapter.ChapterReadAdapter;
import com.read.scriptures.ui.adapter.ChapterReadMenuGvAdapter;
import com.read.scriptures.ui.adapter.TextColorAdapter;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.CollectionUtil;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.NumberUtil;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.UmShareUtils;
import com.read.scriptures.widget.BatteryView;
import com.read.scriptures.widget.FloatView;
import com.read.scriptures.widget.FloatView.Reader;
import com.read.scriptures.widget.ReadOptionsPopupWindow;
import com.read.scriptures.widget.SeleteTextSizePopupWindow;
import com.read.scriptures.widget.SpacesItemDecoration;
import com.read.scriptures.widget.SpeechPopupWindow;
import com.read.scriptures.widget.TouchInterceptSlidingView;
import com.read.scriptures.widget.colorpicker.ColorPickerDialog;
import com.read.scriptures.widget.sliding.SlidingLayout;
import com.read.scriptures.widget.sliding.slider.PageSlider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.read.scriptures.listener.MainHandlerConstant.INIT_ALI_SUCCESS;
import static com.read.scriptures.listener.MainHandlerConstant.INIT_BAIDU_ERROR;
import static com.read.scriptures.listener.MainHandlerConstant.INIT_BAIDU_SUCCESS;
import static com.read.scriptures.listener.MainHandlerConstant.PRINT;
import static com.read.scriptures.listener.MainHandlerConstant.UI_CHANGE_SYNTHES_TEXT_SELECTION;
import static com.read.scriptures.listener.MainHandlerConstant.UI_ERROR_TEXT_SPEECH;
import static com.read.scriptures.listener.MainHandlerConstant.UI_FINISH_TEXT_SELECTION;

/**
 *
 */
public class BaikeActivity extends BaseActivity implements OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SeekBar
                .OnSeekBarChangeListener, Reader, FloatView.ListViewListener {

    protected RelativeLayout mParentView;
    private boolean longClick = false;
    // 顶部数据信息栏
    protected RelativeLayout mTitleLayout;

    protected TextView mTitleVolumeTextView;

    protected TextView mTitleChapterTextView;

    // 底部状态栏
    protected RelativeLayout mStatusLayout;

    protected TextView mStatusWeekTextView;

    protected TextView mStatusTimeTextView;

    protected TextView mStatusBatteryTextView;

    protected BatteryView mStatusBatteryView;

    private ImageView iv_back;

    private PopupWindow popSetting;

    private PopupWindow popMarginSetting;

    private PopupWindow popColorSetting;

    private View dex;
    // 顶部操作栏
    protected RelativeLayout mOptionLayout;

    protected TextView mOptionSelectNumTextView;

    protected BaikeReadSlidingAdapter mBaikeReadSlidingAdapter;

    protected int mTipsPostion;

    protected String mTipsKeyword;

    protected Baike baike;

    protected ChapterReadMenuGvAdapter mChapterReadMenuGvAdapter;

    protected Dialog mSettingOptionDialog;

    protected ReadOptionsPopupWindow mReadOptionsPopupWindow;

    protected SeleteTextSizePopupWindow mSeleteTextSizePopupWindow;

    protected SpeechPopupWindow mSpeechPopupWindow;

    protected boolean mSelectModel = false;

    protected boolean mSpeechModel = false;// 朗读模式
    // 章节内容翻页控件

    protected TouchInterceptSlidingView mSlidingLayout;

    protected FloatView floatView;

    protected int oldIndex;

    protected int oldPosition;

    private int textSize = 0;

    private Notification notification;
    //8.0及以上版本使用
    private NotificationCompat.Builder notificationCompat;
    private NotificationManagerCompat mNotificationManager;

    private RemoteViews contentView;

    private IntentFilter mIntentFilter = null;

    private PlayBroadcastReceiver playBroadcastReceiver = null;

    BaiduSpeechManager baiduSpeechManager;

    protected Handler mainHandler;
    /**
     * 图片资源
     */
    protected List<Integer> mPictures;

    /**
     * 文字资源
     */
    protected List<String> mTitles;

    @SuppressLint("HandlerLeak")
    private final Handler mDatetimeUpdateHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm"));
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕唤醒
        setContentView(R.layout.activity_spirituality_reader);
        StatusBarUtils.initColorStatusBar(this,R.color.black);

        mPictures = Arrays.asList(new Integer[]{ R.drawable.ic_menu_option_2,
                R.drawable.ic_menu_option_3,
//            R.drawable.ic_menu_option_4, R.drawable.ic_menu_option_5,
//            R.drawable.ic_menu_option_6, R.drawable.ic_menu_option_7,
                R.drawable.ic_menu_option_8});//R.drawable.ic_menu_option_1,
        mTitles = Arrays.asList(new String[]{ "内容朗读", "字体大小",
//            "搜索本书", "书签目录",
//            "背景颜色", "简繁转换",
                "横竖阅读"});//"夜间模式",

        initIntentExtras(savedInstanceState);
        initActionBar();
        initViews();
        initSettingPop();
        initMarginPop();
        initColorPop();
        initDatetimeUpdateThread();
        initMenuDialog();
    }


    private void initIntentExtras(final Bundle savedInstanceState) {
        baike = getIntent().getParcelableExtra(BundleConstants.PARAM_BAIKE);
        mTipsPostion = getIntent().getIntExtra(BundleConstants.PARAM_TIPS_POSTION, 0);
        mTipsKeyword = getIntent().getStringExtra(BundleConstants.PARAM_TIPS_KEYWORD);
        if (baike == null) {
            showToastMsg("参数错误！");
            finish();
            return;
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        final Bundle state = new Bundle();
        state.putParcelable(BundleConstants.PARAM_BAIKE, baike);
        state.putInt(BundleConstants.PARAM_TIPS_POSTION, mTipsPostion);
        state.putString(BundleConstants.PARAM_TIPS_KEYWORD, mTipsKeyword);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        baike = savedInstanceState.getParcelable(BundleConstants.PARAM_BAIKE);
        mTipsPostion = savedInstanceState.getInt(BundleConstants.PARAM_TIPS_POSTION, 0);
        mTipsKeyword = savedInstanceState.getString(BundleConstants.PARAM_TIPS_KEYWORD);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtil.test("当前屏幕为横屏");
        } else {
            LogUtil.test("当前屏幕为竖屏");
        }
        // 重新设置平移模式，否则宽度不能设配屏幕宽度变化。
        mSlidingLayout.setSlider(new PageSlider());
    }

    private void initActionBar() {
        mParentView = findViewById(R.id.layout_chapter_read);
        // 顶部bar
        iv_back = (ImageView) findViewById(R.id.btn_back);
        iv_back.setOnClickListener(this);
        dex = (View) findViewById(R.id.dex_line);
        mTitleLayout = (RelativeLayout) findViewById(R.id.layout_title);
        findViewById(R.id.btn_back).setOnClickListener(this);
        mTitleVolumeTextView = (TextView) findViewById(R.id.tv_volume);
        mTitleChapterTextView = (TextView) findViewById(R.id.tv_chapter);

        mTitleChapterTextView.setText(Html.fromHtml(baike.getShowName()));
        mTitleVolumeTextView.setText(CharUtils.removeHead(baike.getShowCateName()));
        mTitleVolumeTextView.setOnClickListener(this);

        // 底部bar
        mStatusLayout = (RelativeLayout) findViewById(R.id.layout_status);
        mStatusWeekTextView = (TextView) findViewById(R.id.tv_week);
        mStatusTimeTextView = (TextView) findViewById(R.id.tv_book_name);
        mStatusBatteryTextView = (TextView) findViewById(R.id.tv_strong);
        mStatusBatteryView = (BatteryView) findViewById(R.id.battery_view);

        mStatusWeekTextView.setText(DateUtil.getWeekStr(DateUtil.getStringDateShort()));
        mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm"));

        showTabbarBackgroundColor();
    }

    private void initViews() {
        textSize = HuDongApplication.getInstance().getTextSize();
        // 编辑
        mOptionLayout = (RelativeLayout) findViewById(R.id.layout_option);
        mOptionLayout.findViewById(R.id.btn_cancle).setOnClickListener(this);
        mOptionLayout.findViewById(R.id.btn_complete).setOnClickListener(this);
        mOptionSelectNumTextView = (TextView) findViewById(R.id.tv_select_num);

        mBaikeReadSlidingAdapter = new BaikeReadSlidingAdapter(this, baike);
        mBaikeReadSlidingAdapter.setTipsPostion(mTipsPostion);
        mBaikeReadSlidingAdapter.setTipsKeyword(mTipsKeyword);
        mBaikeReadSlidingAdapter.setTextSize(HuDongApplication.getInstance().getTextSize());
        mBaikeReadSlidingAdapter.setTextMargin(HuDongApplication.getInstance().getTextMagin());
        mBaikeReadSlidingAdapter.setLineMargin(HuDongApplication.getInstance().getmLineMargin());
        mBaikeReadSlidingAdapter.setTextAroundMargin(HuDongApplication.getInstance().getTextAround());
        mBaikeReadSlidingAdapter.setTextColor(HuDongApplication.getInstance().getTextColor());
        mBaikeReadSlidingAdapter.setTextModel(HuDongApplication.getInstance().getTextModel());
        mBaikeReadSlidingAdapter.setReadModel(HuDongApplication.getInstance().getReadModel());
        mBaikeReadSlidingAdapter.setOnItemLongClickListener(this);
        mBaikeReadSlidingAdapter.setOnItemClickListener(this);


        mSlidingLayout = (TouchInterceptSlidingView) findViewById(R.id.sliding_layout);
        mSlidingLayout.setAdapter(mBaikeReadSlidingAdapter);
        // 默认为左右平移模式
        mSlidingLayout.setSlider(new PageSlider());

        mSlidingLayout.setOnTapListener(new SlidingLayout.OnTapListener() {

            @Override
            public void onSingleTap(final MotionEvent event) {
                final int screenWidth = getResources().getDisplayMetrics().widthPixels;
                final int x = (int) event.getX();
                if (x > (screenWidth / 2)) {
                    mSlidingLayout.slideNext();
                } else if (x <= (screenWidth / 2)) {
                    mSlidingLayout.slidePrevious();
                }
            }
        });
        mSlidingLayout.setScrollListener(new Runnable() {

            @Override
            public void run() {
            }
        });
        mSlidingLayout.setOnSlideChangeListener(new SlidingLayout.OnSlideChangeListener() {
            @Override
            public void onSlideScrollStateChanged(final int touchResult) {

            }

            @Override
            public void onSlideSelected(final Object obj) {
                final ListView listView = mBaikeReadSlidingAdapter.getCurrentListView();
                listView.setOnScrollListener(new OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(final AbsListView view, final int
                            scrollState) {
                        // LogUtil.test("scrollState：" + scrollState);
                        // mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm")
                        // + "" + getReadProgress());
                    }

                    @Override
                    public void onScroll(final AbsListView view, final int firstVisibleItem,
                                         final int visibleItemCount,
                                         final int totalItemCount) {
                        // LogUtil.test("getScollY:" +
                        // mSlidingLayout.getScollY() +
                        // ",view.getLastVisiblePosition()："
                        // + view.getLastVisiblePosition() + ",totalItemCount:"
                        // + totalItemCount);
                        if ((view.getLastVisiblePosition() + 1) >= totalItemCount) {
                            mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm") + "" + " " +
                                    "已阅100%");
                        } else {
                            mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm") + "" +
                                    getReadProgress());
                        }
                    }
                });
            }
        });
        resetListViewBackgroundColor();

        initFloatView();
    }

    public void initFloatView() {
        if (floatView != null) {
            return;
        }
        floatView = new FloatView(getApplicationContext());
        floatView.hide();
        floatView.setBottomStatus(mStatusLayout);
        floatView.setReader(this);
        floatView.setSlidingLayout(mSlidingLayout);
        floatView.setmClickListener(new FloatView.FloatViewOnClickListener() {
            @Override
            public void onClick(float x,float y) {
                if (x < iv_back.getRight() + 100 && y < mTitleLayout.getBottom()) {
                    iv_back.performClick();
                }else {
                    showSpeechPopupWindow();
                    floatView.hide();
                }
            }
        });

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        floatView.setLayoutParams(params);
        floatView.setListViewListenner(this);
        mParentView.addView(floatView);
    }

    public void initBaiduFloatView() {
        if (floatView != null) {
            return;
        }
        floatView = new FloatView(getApplicationContext());
        floatView.hide();
        floatView.setReader(this);
        floatView.setSlidingLayout(mSlidingLayout);
        floatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                showSpeechPopupWindow();
                floatView.hide();
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        floatView.setLayoutParams(params);
        mParentView.addView(floatView);
    }

    /**
     * 获取当前阅读进度
     *
     * @return
     */
    private String getReadProgress() {
        final ListView listView = mBaikeReadSlidingAdapter.getCurrentListView();
        final int firstView = listView.getFirstVisiblePosition();
        final double result = NumberUtil.keepEffectiveNumbers(((firstView * 1d) / listView.getCount()) * 100, 2);
        return " 已阅" + result + "%";
    }

    private void resetListViewBackgroundColor() {
        // 阅读模式
        if (HuDongApplication.getInstance().getReadModel() == SystemConfig.READ_MODEL_NIGHT) {
            mSlidingLayout.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_NIGHT));
        } else {
            mSlidingLayout.setBackgroundColor(HuDongApplication.getInstance().getBackgroudColor());
        }
    }

    private void initDatetimeUpdateThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    mDatetimeUpdateHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(60000);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 弹出PopupWindow
     */
    @SuppressLint({"InflateParams", "RtlHardcoded"})
    public void initMenuDialog() {
        // 加载Dialog的布局文件
        mChapterReadMenuGvAdapter = new ChapterReadMenuGvAdapter(this, mPictures, mTitles);
        final View view = LayoutInflater.from(ATHIS).inflate(R.layout.popup_chapter_baike_menu,
                null);
        final GridView gridView = (GridView) view.findViewById(R.id.popup_gv_setting);
        gridView.setOnItemClickListener(this);
        gridView.setNumColumns(3);
        gridView.setAdapter(mChapterReadMenuGvAdapter);

        mSettingOptionDialog = new Dialog(ATHIS, R.style.ActionSheetDialogStyle);
        mSettingOptionDialog.setContentView(view);
        final WindowManager.LayoutParams lp = mSettingOptionDialog.getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT; // 宽度设置为屏幕的
        lp.height = DensityUtil.dip2px(ATHIS, 130);
        lp.alpha = 0.9f;
        mSettingOptionDialog.getWindow().setAttributes(lp);
        mSettingOptionDialog.getWindow().setGravity(Gravity.LEFT | Gravity.BOTTOM); // 设置靠右对齐
        mSettingOptionDialog.setCanceledOnTouchOutside(true);
        mSettingOptionDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                longClick = false;
            }
        });
        mSettingOptionDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(final DialogInterface dialog, final int keyCode, final KeyEvent
                    event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    dialog.dismiss();
                }
                return false;
            }
        });
    }

    /**
     * 创建MENU
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.add("menu");// 必须创建一项
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 拦截MENU
     */
    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        showSettingDialog();
        return false;// 返回为true 则显示系统menu
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_back:
            case R.id.tv_volume:
                finish();
                break;
            case R.id.btn_cancle:
                mSelectModel = false;
                mOptionLayout.setVisibility(View.GONE);
                setChapterModel(false);
                showReadOptionsPopupWindow(false);
                break;
            case R.id.btn_selectall:
                // 选中所有
                ChapterReadAdapter adapter = mBaikeReadSlidingAdapter
                        .getCurrentChapterReadAdapter();
                adapter.checkAll();
                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_selectnone:
                // 反选
                ChapterReadAdapter adapter2 = mBaikeReadSlidingAdapter
                        .getCurrentChapterReadAdapter();
                adapter2.uncheck();
                adapter2.notifyDataSetChanged();
                break;
            case R.id.btn_complete:
                mSelectModel = false;
                mOptionLayout.setVisibility(View.GONE);
                showReadOptionsPopupWindow(true);
                break;
            case R.id.btn_cancel_model:
                mSelectModel = false;
                showReadOptionsPopupWindow(false);
                break;
            case R.id.btn_exit:
                // 停止朗读
                mSpeechModel = false;
                refreshChapterRemark(false, null);
                mSpeechPopupWindow.dismiss(mSpeechModel);
                floatView.hide();
                break;
            case R.id.btn_previous_chapter:
                break;
            case R.id.btn_next_chapter:
                break;
            //阅读设置
            case R.id.tv_traditional:
                changeTextModel();
                break;
//            case R.id.tv_size_small:
//                textSize--;
//                tv_size.setText(String.valueOf(textSize));
//                HuDongApplication.getInstance().setTextSize(textSize);
//                mBaikeReadSlidingAdapter.setTextSize(textSize);
//                mBaikeReadSlidingAdapter.notifyDataSetChanged();
//                break;
//            case R.id.tv_size_big:
//                textSize++;
//                tv_size.setText(String.valueOf(textSize));
//                HuDongApplication.getInstance().setTextSize(textSize);
//                mBaikeReadSlidingAdapter.setTextSize(textSize);
//                mBaikeReadSlidingAdapter.notifyDataSetChanged();
//                break;
            case R.id.iv_2_hor:
                setBackground(R.id.iv_2_hor);
                HuDongApplication.getInstance().setTextMagin(40);
                HuDongApplication.getInstance().setTextAround(20);
                mBaikeReadSlidingAdapter.setTextMargin(40);
                mBaikeReadSlidingAdapter.setTextAroundMargin(20);
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_3_hor:
                setBackground(R.id.iv_3_hor);
                HuDongApplication.getInstance().setTextMagin(30);
                HuDongApplication.getInstance().setTextAround(20);
                mBaikeReadSlidingAdapter.setTextMargin(30);
                mBaikeReadSlidingAdapter.setTextAroundMargin(20);
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_4_hor:
                setBackground(R.id.iv_4_hor);
                HuDongApplication.getInstance().setTextMagin(20);
                HuDongApplication.getInstance().setTextAround(20);
                mBaikeReadSlidingAdapter.setTextMargin(20);
                mBaikeReadSlidingAdapter.setTextAroundMargin(20);
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_full_4_hor:
                setBackground(R.id.iv_full_4_hor);
                HuDongApplication.getInstance().setTextMagin(20);
                HuDongApplication.getInstance().setTextAround(0);
                mBaikeReadSlidingAdapter.setTextMargin(20);
                mBaikeReadSlidingAdapter.setTextAroundMargin(0);
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_3_ver:
                setBackground(R.id.iv_3_ver);
                break;
            case R.id.iv_more_margin:
                setBackground(R.id.iv_more_margin);
                sb_line.setProgress(HuDongApplication.getInstance().getmLineMargin());
                sb_section.setProgress(HuDongApplication.getInstance().getTextMagin());
                sb_left_right.setProgress(HuDongApplication.getInstance().getTextAround());
                tv_left_right.setText(HuDongApplication.getInstance().getTextAround() + "");
                tv_line.setText(HuDongApplication.getInstance().getmLineMargin() + "");
                tv_section.setText(HuDongApplication.getInstance().getTextMagin() + "");
                popMarginSetting.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
                break;
            //间距设置
            case R.id.iv_back_margin:
                if (popMarginSetting != null)
                    popMarginSetting.dismiss();
                break;
            //背景设置
            case R.id.iv_back_color:
                if (popColorSetting != null)
                    popColorSetting.dismiss();
                break;
            case R.id.return_default://恢复默认设置
                HuDongApplication.getInstance().setmLineMargin(24);
                HuDongApplication.getInstance().setTextMagin(36);
                HuDongApplication.getInstance().setTextAround(56);
                sb_line.setProgress(24);
                sb_section.setProgress(36);
                sb_left_right.setProgress(56);
                tv_left_right.setText(HuDongApplication.getInstance().getTextAround() + "");
                tv_line.setText(HuDongApplication.getInstance().getmLineMargin() + "");
                tv_section.setText(HuDongApplication.getInstance().getTextMagin() + "");
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position,
                            final long id) {
        switch (parent.getId()) {
            case R.id.sliding_listview:
                onItemClickOfSlidingListView(parent, view, position, id);
                break;
            case R.id.listview:
                onItemClickOfSlidingListView(parent, view, position, id);
                break;
            case R.id.popup_gv_option:
                onItemClickOfOptionsGridView(parent, view, position, id);
                break;
            case R.id.popup_gv_setting:
                onItemClickOfSettingGridView(parent, view, position, id);
                break;
        }
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int
            position, final long id) {
        onMenuOpened(0, null);
        return false;
    }

    /**
     * 章节内容Listview Item点击
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    private void onItemClickOfSlidingListView(final AdapterView<?> parent, final View view, final
    int position,
                                              final long id) {
        if (longClick) {
            return;
        }
        // 朗读模式
        if (mSpeechModel) {
            showSpeechPopupWindow();
            return;
        }

        final ChapterReadAdapter adapter = (ChapterReadAdapter) parent.getAdapter();
        if (adapter.isSelectModel()) {
            // 已是编辑模式
            adapter.setChecked(position, !adapter.isCheckedKey(position));
            adapter.updateView(parent, position);
            mOptionSelectNumTextView.setText("已选中" + adapter.getCheckedCount() + "段");
        } else {
            // 开启编辑模式
            mOptionLayout.setVisibility(View.GONE);
            adapter.setSelectModel(true);
            adapter.setChecked(position, true);
            adapter.updateView(parent, position);
            showReadOptionsPopupWindow(true);
        }
    }

    /**
     * 6选项菜单按钮
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    private void onItemClickOfOptionsGridView(final AdapterView<?> parent, final View view, final
    int position,
                                              final long id) {
        switch (position) {
            case 0:
                mSelectModel = true;
                mOptionLayout.setVisibility(View.VISIBLE);
                break;
            case 1:
                // 分享
                // final List<String> lists2 =
                // mSpiritualityReadAdapter.getChecked();
                final List<String> lists2 = mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter
                        ().getChecked();
                if (CollectionUtil.isEmpty(lists2)) {
                    showToastMsg("请至少选中一段文字");
                    return;
                }
                final StringBuffer sb2 = new StringBuffer();

                if (mReadOptionsPopupWindow.isSelectedVolume()) {
                    sb2.append(baike.getShowCateName());
                }
                sb2.append("\t\t");
                if (mReadOptionsPopupWindow.isSelectedVolume()) {
                    sb2.append(baike.getShowName());
                }
                if (sb2.length() > 0) {
                    sb2.append("\n\t\t");
                } else {
                    sb2.append("\t\t");
                }
                for (int i = 0; i < lists2.size(); i++) {
                    sb2.append(lists2.get(i) + "\n  ");
                }
                mSelectModel = false;
                String[] tags = {"a", "b", "font", "h", "s"};
                String content = StringUtil.replaceTags(sb2.toString(), tags);
//                ShareUtil.showShare(ATHIS, share);
                UmShareUtils.shareText(this,content);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                mSelectModel = false;
                final List<String> lists3 = mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter
                        ().getChecked();
                if (CollectionUtil.isEmpty(lists3)) {
                    showToastMsg("请至少选中一段文字");
                    return;
                }
                final StringBuffer sb3 = new StringBuffer();
                if (mReadOptionsPopupWindow.isSelectedVolume()) {
                    sb3.append(baike.getShowCateName());
                }
                sb3.append("\t\t");
                if (mReadOptionsPopupWindow.isSelectedVolume()) {
                    sb3.append(baike.getShowName());
                }
                if (sb3.length() > 0) {
                    sb3.append("\n\t\t");
                } else {
                    sb3.append("\t\t");
                }
                for (int i = 0; i < lists3.size(); i++) {
                    String c = lists3.get(i).replace("<b>", "").replace("</b>", "");
                    sb3.append(c + "\n\t\t");
                }
                CommonUtil.copy(ATHIS, sb3.toString());
                break;
        }
        showReadOptionsPopupWindow(false);
    }

    /**
     * 8选项菜单按钮
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    private void onItemClickOfSettingGridView(final AdapterView<?> parent, final View view, final
    int position,
                                              final long id) {
        mSettingOptionDialog.dismiss();
        showReadOptionsPopupWindow(false);
//        if (position == 0) {
//            changeReadModel();
//        } else
            if (position == 0) {
            if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                CommonUtil.showActivateDialogWithCancelAction(ATHIS, UserInfo.VIP_NORMAL, new Runnable() {
                    public void run() {
                    }
                });
                return;
            }
            changeSpeech();
        } else if (position == 1) {
            if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                CommonUtil.showActivateDialogWithCancelAction(ATHIS,UserInfo.VIP_NORMAL, new Runnable() {
                    public void run() {
                    }
                });
                return;
            }
            tv_size.setText(String.valueOf(textSize));
            popSetting.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
//            mSeleteTextSizePopupWindow = new SeleteTextSizePopupWindow(BaikeActivity.this,
//                    HuDongApplication.getInstance().getTextSize(), BaikeActivity.this);
//            mSeleteTextSizePopupWindow.showAtLocation(getWindow().getDecorView(),
//                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
//        else if (position == 3) {
//            final Bundle bundle = new Bundle();
//            bundle.putParcelable(BundleConstants.PARAM_BAIKE, baike);
//            ActivityUtil.next(BaikeActivity.this, SearchSpiritualityActivity.class, bundle, -1);
//        }         else if (position == 4) {
//            ActivityUtil.next(ATHIS, BookmarkListActivity.class);
//        }
        else if (position == 2) {
            final int color = PreferenceConfig.getBackgroudColor(ATHIS);
            final ColorPickerDialog dialog = new ColorPickerDialog(ATHIS, color);
            dialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                @Override
                public void onColorChanged(final int color) {
                    HuDongApplication.getInstance().setBackgroudColor(color);
                    showTabbarBackgroundColor();
                    resetListViewBackgroundColor();
                }
            });
            dialog.show();
        } else if (position == 3) {
            changeTextModel();
        } else if (position == 4) {
            final int mCurrentOrientation = getResources().getConfiguration().orientation;
            if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    private void changeReadModel() {
        if (HuDongApplication.getInstance().getReadModel() == SystemConfig.READ_MODEL_NORMAL) {
            HuDongApplication.getInstance().setReadModel(SystemConfig.READ_MODEL_NIGHT);
        } else {
            HuDongApplication.getInstance().setReadModel(SystemConfig.READ_MODEL_NORMAL);
        }
        showTabbarBackgroundColor();
        mBaikeReadSlidingAdapter.setReadModel(HuDongApplication.getInstance().getReadModel());
        mBaikeReadSlidingAdapter.notifyDataSetChanged();
//        resetListViewBackgroundColor();
    }

    private void changeTextModel() {
        if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
            CommonUtil.showActivateDialog(ATHIS,UserInfo.VIP_NORMAL);
            return;
        }
        if (HuDongApplication.getInstance().getTextModel() == SystemConfig.TEXT_MODEL_FANTI) {
            HuDongApplication.getInstance().setTextModel(SystemConfig.TEXT_MODEL_NORMAL);
            tv_traditional.setText("繁");
        } else {
            HuDongApplication.getInstance().setTextModel(SystemConfig.TEXT_MODEL_FANTI);
            tv_traditional.setText("简");
        }
        final List<String> lists = SearchTextUtil.queryBaikeContent(this, baike, HuDongApplication.getInstance().getTextModel());
        mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter().setList(lists);
        mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter().notifyDataSetChanged();
    }

    /**
     * 朗读
     */
    private void changeSpeech() {
        if (HuDongApplication.getInstance().isAppNormalLevelActivate()) {
//            if (SystemsUtils.checkFloatPermission(this)) {
                if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
//                    initFloatView();
                    if (!mSpeechModel) {
                        mainHandler = new Handler() {
                            /*
                             * @param msg
                             */
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                handle(msg);
                            }

                        };
                        mSpeechModel = true;
                        if (floatView != null) {
                            floatView.show();
                        }
                        initSpeechTts();
                        initNotificationBar();
                    }
                } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
//                    initBaiduFloatView();
                    if (!mSpeechModel) {
                        mainHandler = new Handler() {
                            /*
                             * @param msg
                             */
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                handle(msg);
                            }

                        };
                        mSpeechModel = true;
                        if (floatView != null) {
                            floatView.show();
                        }
                        initBaiduSpeech();
                        initNotificationBar();
                    }
                }

//            } else {
//                ToastCenterUtil.showMessage(ATHIS, "您好，请开启悬浮窗口权限，朗读可以拖动喔…");
//                PermissionPageUtils permissionPageUtils = new PermissionPageUtils(this);
//                permissionPageUtils.jumpPermissionPage();
//            }
        } else {
            CommonUtil.showActivateDialog(ATHIS,UserInfo.VIP_NORMAL);
        }
    }

    private void setChapterModel(final boolean model) {
        mSelectModel = model;
        mOptionSelectNumTextView.setText("已选中1段");
        mOptionLayout.setVisibility(model ? View.VISIBLE : View.GONE);

        mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter().setSelectModel(model);
        mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter().notifyDataSetChanged();
        // mSpiritualityReadAdapter.setSelectModel(model);
        // mSpiritualityReadAdapter.notifyDataSetChanged();
    }

    private void showReadOptionsPopupWindow(final boolean show) {
        if (mReadOptionsPopupWindow == null) {
            List<String> list = mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter()
                    .getChecked();
            if (list == null) {
                list = new ArrayList<String>();
            }
            final StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                stringBuffer.append(list.get(i) + "\n\t\t");
            }
            mReadOptionsPopupWindow = new ReadOptionsPopupWindow(BaikeActivity.this,
                    stringBuffer.toString(), 1);
            mReadOptionsPopupWindow.setOnItemClickListener(BaikeActivity.this);
            mReadOptionsPopupWindow.setOnClickListener(this);
            mReadOptionsPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    StatusBarUtils.initColorStatusBar(BaikeActivity.this,HuDongApplication.getInstance().getBackgroudColor());
                    if (!mSelectModel) {
                        setChapterModel(false);
                    }
                    mSelectModel = false;
                }
            });
        }
        if (show) {
            mReadOptionsPopupWindow.showMenu(mParentView);
            StatusBarUtils.initColorStatusBar(this,getResources().getColor(R.color.black));
        } else {
            mReadOptionsPopupWindow.hideMenu();
            StatusBarUtils.initColorStatusBar(this,HuDongApplication.getInstance().getBackgroudColor());
        }
    }

    private void showSettingDialog() {
        if (mReadOptionsPopupWindow != null && mReadOptionsPopupWindow.isShowing()) {
            return;
        }
        longClick = true;
        if (mSettingOptionDialog == null) {
            initMenuDialog();
        } else {
//            if (HuDongApplication.getInstance().getReadModel() == SystemConfig.READ_MODEL_NORMAL) {
//                mTitles.set(0, "夜间模式");
//            } else {
//                mTitles.set(0, "普通模式");
//            }
//            if (HuDongApplication.getInstance().getTextModel() == SystemConfig.TEXT_MODEL_FANTI) {
//                mPictures[4] = R.drawable.ic_menu_option_7_2;
//            } else {
//                mPictures[4] = R.drawable.ic_menu_option_7;
//            }
            mChapterReadMenuGvAdapter.notifyDataSetChanged();
            mSettingOptionDialog.show();
        }
    }

    private void showTabbarBackgroundColor() {
        if (HuDongApplication.getInstance().getReadModel() == SystemConfig.READ_MODEL_NORMAL) {
            mTitleLayout.setBackgroundColor(HuDongApplication.getInstance().getBackgroudColor());
            mStatusLayout.setBackgroundColor(HuDongApplication.getInstance().getBackgroudColor());
            dex.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mTitleVolumeTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mTitleChapterTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mStatusWeekTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mStatusTimeTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mStatusBatteryTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mStatusBatteryView.setColor(0xFF2B2B2B);
            mStatusBatteryView.setFillColor(0XFFFFFFFF);
        } else {
            dex.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_NIGHT));
            mTitleLayout.setBackgroundColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_BACKGROUND_NIGHT));
            mStatusLayout.setBackgroundColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_BACKGROUND_NIGHT));
            mTitleVolumeTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_NIGHT));
            mTitleChapterTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_NIGHT));
            mStatusWeekTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_NIGHT));
            mStatusTimeTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_NIGHT));
            mStatusBatteryTextView.setTextColor(Color.parseColor(SystemConfig
                    .DEFAULT_READ_TEXT_COLOR_NIGHT));
            mStatusBatteryView.setColor(0xFFFFFFFF);
            mStatusBatteryView.setFillColor(0XFF2B2B2B);
        }
        mStatusBatteryView.invalidate();
    }

    private void showSpeechPopupWindow() {
        if (mSpeechPopupWindow == null) {
            mSpeechPopupWindow = new SpeechPopupWindow(this, mXunFeiSpeechManager, baiduSpeechManager, this);
            mSpeechPopupWindow.setOnClickListener(this);
        }
        mSpeechPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                if (floatView != null) {
                    floatView.show();
                }
            }
        });
        mSpeechPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean
            fromUser) {
//        final int textSize = (progress / 5) + 16;
//        HuDongApplication.getInstance().setTextSize(textSize);
//        mBaikeReadSlidingAdapter.setTextSize(textSize);
//        mBaikeReadSlidingAdapter.notifyDataSetChanged();
        switch (seekBar.getId()) {
            case R.id.line_seek_bar:
                Log.e("onProgressChangedASDASD", "行间距:" + progress);
                HuDongApplication.getInstance().setmLineMargin(progress);
                tv_line.setText(progress + "");
                mBaikeReadSlidingAdapter.setLineMargin(progress);
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.section_seek_bar:
                Log.e("onProgressChangedASDASD", "段间距:" + progress);
                HuDongApplication.getInstance().setTextMagin(progress);
                tv_section.setText(progress + "");
                mBaikeReadSlidingAdapter.setTextMargin(progress);
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                Log.e("onProgressChangedASDASD", "左右间距:" + progress);
                break;
//            case R.id.top_down_seek_bar:
//                Log.e("onProgressChangedASDASD", "上下间距:"+progress );
////                HuDongApplication.getInstance().setTextMagin(progress);
//                mChapterReadSlidingAdapter.setTopAndBottomMargin(progress);
//                mChapterReadSlidingAdapter.notifyDataSetChanged();
//                break;
            case R.id.left_right_seek_bar:
                HuDongApplication.getInstance().setTextAround(progress);
                tv_left_right.setText(progress + "");
                mBaikeReadSlidingAdapter.setTextAroundMargin(progress);
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                Log.e("onProgressChangedASDASD", "左右间距:" + progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册电量广播
        registerReceiver(mBatteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销电量广播
        unregisterReceiver(mBatteryChangedReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXunFeiSpeechManager != null) {
            mXunFeiSpeechManager.stopSpeaking();
            mXunFeiSpeechManager.destroy();
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                HuDongApplication.getInstance().notManager.cancel(SystemConstants.Notification_ID_BASE);
            }else{
                mNotificationManager.cancel(SystemConstants.Notification_ID_BASE);
            }
        }
        if (baiduSpeechManager != null) {
            baiduSpeechManager.stop();
            baiduSpeechManager.destory();
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                HuDongApplication.getInstance().notManager.cancel(SystemConstants.Notification_ID_BASE);
            }else{
                mNotificationManager.cancel(SystemConstants.Notification_ID_BASE);
            }
        }

        if (floatView != null) {
            floatView.hide();
            floatView = null;
        }
        if (playBroadcastReceiver != null)
            unregisterReceiver(playBroadcastReceiver);
        stopService(new Intent(BaikeActivity.this, NotifacationService.class));

    }

    // 接受广播
    private final BroadcastReceiver mBatteryChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                final int level = intent.getIntExtra("level", 0);
                final int scale = intent.getIntExtra("scale", 100);
                final int power = (level * 100) / scale;
                mStatusBatteryTextView.setText(power + "%");
                mStatusBatteryView.setPower(power);
            }
        }
    };

    ////////////////////////////////////////////////////////
    private XunFeiSpeechManager mXunFeiSpeechManager;

    @SuppressLint("UseSparseArrays")
    private final HashMap<Integer, List<String>> mSpeechTextMap = new HashMap<Integer,
            List<String>>();

    private int mSpeechPosition = 0;

    private int mSpeechIndex = 0;

    private void initSpeechTts() {
        mSpeechPosition = 0;
        mSpeechIndex = 0;
        // 初始化合成对象
        if (mXunFeiSpeechManager == null) {
            showProgressDialog("加载中……");
            baiduSpeechManager = new BaiduSpeechManager(this, mainHandler);
            mXunFeiSpeechManager = new XunFeiSpeechManager(this);
            mXunFeiSpeechManager.setTtsListener(mTtsListener);
            if (floatView != null) {
                floatView.setmXunFeiSpeechManager(mXunFeiSpeechManager);
                floatView.setBaiduSpeechManager(baiduSpeechManager);
            }
            mXunFeiSpeechManager.init(mTtsInitListener);
            //先初始化，避免notifa报空指针
            mSpeechPopupWindow = new SpeechPopupWindow(this, mXunFeiSpeechManager, baiduSpeechManager, this);
            mSpeechPopupWindow.setOnClickListener(this);
        } else {
            startSpeech();
        }

    }

    private final InitListener mTtsInitListener = new InitListener() {

        @Override
        public void onInit(final int code) {
            dismissProgressDialog();
            if (code == ErrorCode.SUCCESS) {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
                if (SystemConfig.Speech_Model == 1)
                    startSpeech();
            } else {
                showToast( "语音初始化失败,错误码：" + code);
            }
        }
    };

    private void startSpeech() {
        mSpeechTextMap.clear();
        // final List<String> contentNodes = mSpiritualityReadAdapter.getList();
        final List<String> contentNodes = mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter()
                .getList();
        for (int i = 0; i < contentNodes.size(); i++) {
            final String[] ssss = contentNodes.get(i).split("。");
            mSpeechTextMap.put(i, Arrays.asList(ssss));
        }

        if ((mSpeechTextMap == null) || (mSpeechTextMap.size() == 0)) {
            showToastMsg("暂无章节内容");
            return;
        }

        String remarkTxt = getSpeechContent(mSpeechIndex, mSpeechPosition);
        if (TextUtils.isEmpty(remarkTxt)) {
            showToastMsg("无法获取朗读内容");
            return;
        }
        refreshChapterRemark(true, remarkTxt);
        remarkTxt = StringUtil.getRealSpeekText(remarkTxt);
        SystemConfig.readContent = remarkTxt;
        
        // 修复：添加朗读引擎的空指针检查
        try {
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                if (mXunFeiSpeechManager == null) {
                    showToast("讯飞语音未初始化");
                    return;
                }
                mXunFeiSpeechManager.startSpeaking(remarkTxt, mTtsListener);
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                if (baiduSpeechManager == null) {
                    showToast("百度语音未初始化");
                    return;
                }
//            baiduSpeechManager.batchSpeak(remarkTxt);
                String txt = remarkTxt.replaceAll("行", "行(xing2)");
                baiduSpeechManager.speak(txt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("BaikeSpeech", "startSpeech error: " + e.getMessage());
            showToast("朗读失败：" + e.getMessage());
        }
    }

    private String getSpeechContent(final int index, final int position) {
        try {
            if (mSpeechTextMap == null || mSpeechTextMap.isEmpty()) {
                Log.e("BaikeSpeech", "mSpeechTextMap is null or empty");
                return "";
            }
            
            List<String> textList = mSpeechTextMap.get(index);
            if (textList == null || textList.isEmpty()) {
                Log.e("BaikeSpeech", "textList is null or empty for index: " + index);
                return "";
            }
            
            if (position < 0 || position >= textList.size()) {
                Log.e("BaikeSpeech", "position out of range: " + position + ", size: " + textList.size());
                return "";
            }
            
            final String remarkTxt = textList.get(position);
            return remarkTxt != null ? remarkTxt : "";
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("BaikeSpeech", "getSpeechContent error: " + e.getMessage());
            return "";
        }
    }

    /**
     * 合成回调监听。
     */
    private final SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(final int percent, final int beginPos, final int endPos,
                                     final String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(final int percent, final int beginPos, final int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(final SpeechError error) {
            if (error == null) {
                if (mSpeechTextMap.get(mSpeechIndex) == null) {
                    showToastMsg("播放完成");
                    return;
                }
                if ((mSpeechTextMap.get(mSpeechIndex).size() - 1) <= mSpeechPosition) {
                    mSpeechIndex++;
                    mSpeechPosition = 0;
                    mBaikeReadSlidingAdapter.getCurrentListView().smoothScrollToPosition
                            (mSpeechIndex);
                } else {
                    mSpeechPosition++;
                }
                final String remarkTxt = getSpeechContent(mSpeechIndex, mSpeechPosition);
                if (TextUtils.isEmpty(remarkTxt)) {
                    showToastMsg("参数读取错误！");
                    return;
                }
                final String result = SearchTextUtil.replaceTag("<.+?>", remarkTxt);
                String result2 = SearchTextUtil.replaceTag("\\(.+?\\)", result);
                result2 = SearchTextUtil.replaceTag("\\{.+\\}", result2);
                String result3 = SearchTextUtil.replaceTag("（.+）", result2);
                result3 = StringUtil.getRealSpeekText(result3);
                if (TextUtils.isEmpty(result3)) {
                    onCompleted(null);
                    return;
                }
                refreshChapterRemark(true, remarkTxt);
                SystemConfig.readContent = result3;
                mXunFeiSpeechManager.startSpeaking(result3, mTtsListener);
            } else if (error != null) {
                showToastMsg(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(final int eventType, final int arg1, final int arg2, final Bundle obj) {

        }
    };

    @Override
    public void refreshChapterRemark(final boolean speechModel, final String remarkText) {
        mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter().setSpeechModel(speechModel);
        mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter().setRemarkText(remarkText);
        mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter().setSeechIndex(mSpeechIndex);
        mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter().notifyDataSetChanged();

        ChapterReadAdapter adapter = mBaikeReadSlidingAdapter.getCurrentChapterReadAdapter();

        adapter.setSpeechModel(speechModel);
        adapter.setRemarkText(remarkText);
        adapter.setSeechIndex(mSpeechIndex);
        adapter.notifyDataSetChanged();

    }

    @Override
    public int getReadCurrent() {
        return mSpeechIndex;
    }

    @Override
    public void setReadCurrent(final int current) {
        this.mSpeechIndex = current;
    }

    @Override
    public XunFeiSpeechManager speechManager() {
        return mXunFeiSpeechManager;
    }

    @Override
    public int getOldIndex() {
        return oldIndex;
    }

    @Override
    public void setOldIndex(final int oldIndex) {
        this.oldIndex = oldIndex;
    }

    @Override
    public HashMap<Integer, List<String>> getmSpeechTextMap() {
        return mSpeechTextMap;
    }

    @Override
    public int getmSpeechPosition() {
        return mSpeechPosition;
    }

    @Override
    public void setmSpeechPosition(final int mSpeechPosition) {
        this.mSpeechPosition = mSpeechPosition;
    }

    @Override
    public int getOldPosition() {
        return oldPosition;
    }

    @Override
    public void setOldPosition(final int oldPosition) {
        this.oldPosition = oldPosition;
    }

    @Override
    public void onBackPressed() {
        if (mSpeechPopupWindow != null && mSpeechPopupWindow.isShowing()) {
            mSpeechPopupWindow.dismiss();
            return;
        } else if (mSpeechModel) {
            mSpeechModel = false;
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                mXunFeiSpeechManager.stopSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                baiduSpeechManager.stop();
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                HuDongApplication.getInstance().notManager.cancel(SystemConstants.Notification_ID_BASE);
            }else{
                mNotificationManager.cancel(SystemConstants.Notification_ID_BASE);
            }
            floatView.hide();
            refreshChapterRemark(true, "");
            showToast( "已退出朗读模式");
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        try {
            if (mSpeechModel){
                mSpeechModel = false;
                if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                    mXunFeiSpeechManager.stopSpeaking();
                } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                    baiduSpeechManager.stop();
                }
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                    HuDongApplication.getInstance().notManager.cancel(SystemConstants.Notification_ID_BASE);
                } else {
                    mNotificationManager.cancel(SystemConstants.Notification_ID_BASE);
                }
            }
            if (floatView != null) {
                floatView.hide();
            }
        } catch (Exception e) {
            LogUtil.error("floatView.removeView", e);
        }
        super.finish();

    }

    private ImageView tv_size_small;
    private ImageView iv_font;
    private ImageView tv_size_big;
    private TextView tv_size;
    private TextView tv_traditional;
    private ImageView iv_2_hor;
    private ImageView iv_3_hor;
    private ImageView iv_4_hor;
    private ImageView iv_full_4_hor;
    private ImageView iv_3_ver;
    private ImageView iv_more_margin;
    private RadioGroup rg_background;
    private RadioButton iv_text_white;
    private RadioButton iv_text_yellow;
    private RadioButton iv_text_grey;
    private RadioButton iv_text_green;
    private RadioButton iv_text_blue;
    private RadioButton iv_more_color;

    private void initSettingPop() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.pop_read_setting, null);
        tv_size_small = (ImageView) view.findViewById(R.id.tv_size_small);
        iv_font = (ImageView) view.findViewById(R.id.iv_font);
        tv_size_big = (ImageView) view.findViewById(R.id.tv_size_big);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
        tv_traditional = (TextView) view.findViewById(R.id.tv_traditional);
        iv_2_hor = (ImageView) view.findViewById(R.id.iv_2_hor);
        iv_3_hor = (ImageView) view.findViewById(R.id.iv_3_hor);
        iv_4_hor = (ImageView) view.findViewById(R.id.iv_4_hor);
        iv_full_4_hor = (ImageView) view.findViewById(R.id.iv_full_4_hor);
        iv_3_ver = (ImageView) view.findViewById(R.id.iv_3_ver);
        iv_more_margin = (ImageView) view.findViewById(R.id.iv_more_margin);
        rg_background = (RadioGroup) view.findViewById(R.id.rg_background);
        iv_text_white = (RadioButton) view.findViewById(R.id.iv_text_white);
        iv_text_yellow = (RadioButton) view.findViewById(R.id.iv_text_yellow);
        iv_text_grey = (RadioButton) view.findViewById(R.id.iv_text_grey);
        iv_text_green = (RadioButton) view.findViewById(R.id.iv_text_green);
        iv_text_blue = (RadioButton) view.findViewById(R.id.iv_text_blue);
        iv_more_color = (RadioButton) view.findViewById(R.id.iv_more_color);
        rg_background.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.iv_text_white) {
                    HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.color_read_white));
                    HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.color_text_white));
                    setBackTint();
                    mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.color_read_white));
                    mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.color_text_white));
                    mBaikeReadSlidingAdapter.notifyDataSetChanged();

                } else if (checkedId == R.id.iv_text_yellow) {
                    HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.color_read_yellow));
                    HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.color_text_yellow));
                    setBackTint();
                    mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.color_read_yellow));
                    mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.color_text_yellow));
                    mBaikeReadSlidingAdapter.notifyDataSetChanged();
                } else if (checkedId == R.id.iv_text_grey) {
                    HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.color_read_grey));
                    HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.color_text_grey));
                    setBackTint();
                    mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.color_read_grey));
                    mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.color_text_grey));
                    mBaikeReadSlidingAdapter.notifyDataSetChanged();
                } else if (checkedId == R.id.iv_text_green) {
                    HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.color_read_green));
                    HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.color_text_green));
                    setBackTint();
                    mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.color_read_green));
                    mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.color_text_green));
                    mBaikeReadSlidingAdapter.notifyDataSetChanged();
                } else if (checkedId == R.id.iv_text_blue) {
                    HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.color_read_blue));
                    HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.color_text_blue));
                    setBackTint();
                    mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.color_read_blue));
                    mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.color_text_blue));
                    mBaikeReadSlidingAdapter.notifyDataSetChanged();
                } else if (checkedId == R.id.iv_more_color) {
                    if (popColorSetting != null)
                        popColorSetting.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
                }
            }
        });

        iv_font.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 去购买字体
                showToast( "正在开发中...");
//                Intent intent = new Intent(BaikeActivity.this, BuyFontActivity.class);
//                startActivity(intent);
            }
        });

        iv_more_color.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popColorSetting != null)
                    popColorSetting.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
            }
        });
        if (HuDongApplication.getInstance().getTextModel() == SystemConfig.TEXT_MODEL_FANTI) {
            tv_traditional.setText("简");
        } else {
            tv_traditional.setText("繁");
        }
//        tv_size_small.setOnClickListener(this);
//        tv_size_big.setOnClickListener(this);
        tv_size_small.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    updateAddOrSubtract(v.getId());    //手指按下时触发不停的发送消息
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopAddOrSubtract();    //手指抬起时停止发送
                }
                return true;
            }
        });

        tv_size_big.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    updateAddOrSubtract(v.getId());    //手指按下时触发不停的发送消息
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopAddOrSubtract();    //手指抬起时停止发送
                }
                return true;
            }
        });
        tv_size.setOnClickListener(this);
        tv_traditional.setOnClickListener(this);
        iv_2_hor.setOnClickListener(this);
        iv_3_hor.setOnClickListener(this);
        iv_4_hor.setOnClickListener(this);
        iv_full_4_hor.setOnClickListener(this);
        iv_3_ver.setOnClickListener(this);
        iv_more_margin.setOnClickListener(this);

        popSetting = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popSetting.setTouchable(true);
        popSetting.setFocusable(true);
        ColorDrawable draw = new ColorDrawable(0x00000000);
        popSetting.setBackgroundDrawable(draw);
        popSetting.setAnimationStyle(R.style.pop_bottom);
        popSetting.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                StatusBarUtils.setBackgroundAlpha(BaikeActivity.this, 1.0f);
            }
        });
        if (HuDongApplication.getInstance().getReadModel() == SystemConfig.READ_MODEL_NORMAL) {
            setChecked();
        }
    }

    private void setChecked() {
        //TODO 设置图片tint
        setBackTint();

        if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_white)) {
            iv_text_white.setChecked(true);
        } else if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_yellow)) {
            iv_text_yellow.setChecked(true);
        } else if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_grey)) {
            iv_text_grey.setChecked(true);
        } else if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_green)) {
            iv_text_green.setChecked(true);
        } else if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_blue)) {
            iv_text_blue.setChecked(true);
        } else {
            iv_more_color.setChecked(true);
        }

        if (HuDongApplication.getInstance().getTextAround() == 20 && HuDongApplication.getInstance().getTextMagin() == 40) {
            setBackground(R.id.iv_2_hor);
        } else if (HuDongApplication.getInstance().getTextAround() == 20 && HuDongApplication.getInstance().getTextMagin() == 30) {
            setBackground(R.id.iv_3_hor);
        } else if (HuDongApplication.getInstance().getTextAround() == 20 && HuDongApplication.getInstance().getTextMagin() == 20) {
            setBackground(R.id.iv_4_hor);
        } else if (HuDongApplication.getInstance().getTextAround() == 0 && HuDongApplication.getInstance().getTextMagin() == 20) {
            setBackground(R.id.iv_full_4_hor);
        } else if (HuDongApplication.getInstance().getTextAround() == 20 && HuDongApplication.getInstance().getTextMagin() == 40) {
            setBackground(R.id.iv_3_ver);
        } else {
            setBackground(R.id.iv_more_margin);
        }

    }

    private void setBackground(int id) {
        switch (id) {
            case R.id.iv_2_hor:
                iv_2_hor.setBackground(getResources().getDrawable(R.drawable.btn_normal_pressed));
                iv_3_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_full_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_ver.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_more_margin.setBackground(getResources().getDrawable(R.drawable.ic_more_setting));
                break;
            case R.id.iv_3_hor:
                iv_2_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_hor.setBackground(getResources().getDrawable(R.drawable.btn_normal_pressed));
                iv_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_full_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_ver.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_more_margin.setBackground(getResources().getDrawable(R.drawable.ic_more_setting));
                break;
            case R.id.iv_4_hor:
                iv_2_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_normal_pressed));
                iv_full_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_ver.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_more_margin.setBackground(getResources().getDrawable(R.drawable.ic_more_setting));
                break;
            case R.id.iv_full_4_hor:
                iv_2_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_full_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_normal_pressed));
                iv_3_ver.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_more_margin.setBackground(getResources().getDrawable(R.drawable.ic_more_setting));
                break;
            case R.id.iv_3_ver:
                iv_2_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_full_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_ver.setBackground(getResources().getDrawable(R.drawable.btn_normal_pressed));
                iv_more_margin.setBackground(getResources().getDrawable(R.drawable.ic_more_setting));
                break;
            case R.id.iv_more_margin:
                iv_2_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_full_4_hor.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_3_ver.setBackground(getResources().getDrawable(R.drawable.btn_border_normal));
                iv_more_margin.setBackground(getResources().getDrawable(R.drawable.ic_more_setting_pressed));
                break;
        }
    }

    private void setBackTint() {
        Drawable up = ContextCompat.getDrawable(this, R.drawable.ic_back);
        Drawable drawableUp = DrawableCompat.wrap(up);
        DrawableCompat.setTint(drawableUp, HuDongApplication.getInstance().getTextColor());
        iv_back.setImageDrawable(drawableUp);

        dex.setBackgroundColor(HuDongApplication.getInstance().getTextColor());
        mTitleVolumeTextView.setTextColor(HuDongApplication.getInstance().getTextColor());
        mTitleChapterTextView.setTextColor(HuDongApplication.getInstance().getTextColor());
        mStatusTimeTextView.setTextColor(HuDongApplication.getInstance().getTextColor());
        mStatusWeekTextView.setTextColor(HuDongApplication.getInstance().getTextColor());
        mStatusBatteryTextView.setTextColor(HuDongApplication.getInstance().getTextColor());
        mStatusLayout.setBackgroundColor(HuDongApplication.getInstance().getBackgroudColor());
        mTitleLayout.setBackgroundColor(HuDongApplication.getInstance().getBackgroudColor());
        StatusBarUtils.initColorStatusBar(this,HuDongApplication.getInstance().getBackgroudColor());
        if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_white)) {
            iv_text_white.setChecked(true);
            mStatusBatteryView.setColor(0xFF2B2B2B);
            mStatusBatteryView.setFillColor(0XFFFFFFFF);
        } else if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_yellow)) {
            iv_text_yellow.setChecked(true);
            mStatusBatteryView.setColor(0xFF2B2B2B);
            mStatusBatteryView.setFillColor(0XFFFFFFFF);
        } else if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_grey)) {
            iv_text_grey.setChecked(true);
            mStatusBatteryView.setColor(0xFFFFFFFF);
            mStatusBatteryView.setFillColor(0XFF2B2B2B);
        } else if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_green)) {
            iv_text_green.setChecked(true);
            mStatusBatteryView.setColor(0xFF2B2B2B);
            mStatusBatteryView.setFillColor(0XFFFFFFFF);
        } else if (HuDongApplication.getInstance().getBackgroudColor() == getResources().getColor(R.color.color_read_blue)) {
            iv_text_blue.setChecked(true);
            mStatusBatteryView.setColor(0xFF2B2B2B);
            mStatusBatteryView.setFillColor(0XFFFFFFFF);
        } else {
            iv_more_color.setChecked(true);
        }
    }

    private ImageView iv_back_margin;
    private SeekBar sb_line;
    private SeekBar sb_section;
    private SeekBar sb_top_bottom;
    private SeekBar sb_left_right;
    private TextView tv_left_right;
    private TextView tv_line;
    private TextView tv_section;
    private TextView return_default;

    private void initMarginPop() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.pop_magin_setting, null);
        iv_back_margin = (ImageView) view.findViewById(R.id.iv_back_margin);
        sb_line = (SeekBar) view.findViewById(R.id.line_seek_bar);
        sb_section = (SeekBar) view.findViewById(R.id.section_seek_bar);
//        sb_top_bottom = (SeekBar) view.findViewById(R.id.top_down_seek_bar);
        sb_left_right = (SeekBar) view.findViewById(R.id.left_right_seek_bar);
        tv_left_right = (TextView) view.findViewById(R.id.tv_left_right_margin);
        tv_line = (TextView) view.findViewById(R.id.tv_line_margin);
        tv_section = (TextView) view.findViewById(R.id.tv_section_margin);
        return_default = (TextView) view.findViewById(R.id.return_default);
        sb_line.setProgress(HuDongApplication.getInstance().getmLineMargin());
        sb_section.setProgress(HuDongApplication.getInstance().getTextMagin());
        sb_left_right.setProgress(HuDongApplication.getInstance().getTextAround());
        tv_left_right.setText(HuDongApplication.getInstance().getTextAround() + "");
        tv_line.setText(HuDongApplication.getInstance().getmLineMargin() + "");
        tv_section.setText(HuDongApplication.getInstance().getTextMagin() + "");
        sb_line.setOnSeekBarChangeListener(this);
        sb_section.setOnSeekBarChangeListener(this);
//        sb_top_bottom.setOnSeekBarChangeListener(this);
        sb_left_right.setOnSeekBarChangeListener(this);
        iv_back_margin.setOnClickListener(this);
        return_default.setOnClickListener(this);
        popMarginSetting = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popMarginSetting.setTouchable(true);
        popMarginSetting.setFocusable(true);
        ColorDrawable draw = new ColorDrawable(0x00000000);
        popMarginSetting.setBackgroundDrawable(draw);
        popMarginSetting.setAnimationStyle(R.style.pop_bottom);
        popMarginSetting.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                HuDongApplication.getInstance().setmLineMargin(sb_line.getProgress());
                HuDongApplication.getInstance().setTextMagin(sb_section.getProgress());
                HuDongApplication.getInstance().setTextAround(sb_left_right.getProgress());
                mBaikeReadSlidingAdapter.setLineMargin(sb_line.getProgress());
                mBaikeReadSlidingAdapter.setTextMargin(sb_section.getProgress());
                mBaikeReadSlidingAdapter.setTextAroundMargin(sb_left_right.getProgress());
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                StatusBarUtils.setBackgroundAlpha(BaikeActivity.this, 1.0f);
            }
        });
    }

    private ImageView iv_back_color;
    private RecyclerView rv_text;
    private RecyclerView rv_background;

    private void initColorPop() {
        String[] toast = new String[25];
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.pop_color_setting, null);
        iv_back_color = (ImageView) view.findViewById(R.id.iv_back_color);
        rv_text = (RecyclerView) view.findViewById(R.id.rv_text_color);
        rv_background = (RecyclerView) view.findViewById(R.id.rv_background_color);
//        LayoutAdapter layoutAdapter = new LayoutAdapter(this,rv_text);
//        rv_text.setAdapter(layoutAdapter);
//        rv_text.setLayoutMode(GridLayoutManager);
//        layoutAdapter.setList(Arrays.asList(toast));
        SpacesItemDecoration decoration = new SpacesItemDecoration(8);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        rv_text.setLayoutManager(staggeredGridLayoutManager);
        rv_background.setLayoutManager(staggeredGridLayoutManager1);
        rv_text.addItemDecoration(decoration);
        rv_background.addItemDecoration(decoration);

        TextColorAdapter textColorAdapter = new TextColorAdapter(this, Arrays.asList(toast));
        BackgroundColorAdapter backgroundColorAdapter = new BackgroundColorAdapter(this, Arrays.asList(toast));
        rv_text.setAdapter(textColorAdapter);
        rv_background.setAdapter(backgroundColorAdapter);

        textColorAdapter.setOnItemClickListener(new TextColorAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                setTextColor(position);
            }
        });
        backgroundColorAdapter.setOnItemClickListener(new BackgroundColorAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                setBackgroundColor(position);
            }
        });
        iv_back_color.setOnClickListener(this);
        popColorSetting = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popColorSetting.setTouchable(true);
        popColorSetting.setFocusable(true);
        ColorDrawable draw = new ColorDrawable(0x00000000);
        popColorSetting.setBackgroundDrawable(draw);
        popColorSetting.setAnimationStyle(R.style.pop_bottom);
        popColorSetting.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                StatusBarUtils.setBackgroundAlpha(BaikeActivity.this, 1.0f);
            }
        });
    }

    private void setTextColor(int position) {
        switch (position + 1) {
            case 1:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_1));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_1));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 2:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_2));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_2));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 3:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_3));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_4));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 4:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_4));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_4));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 5:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_5));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_5));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 6:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_6));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_6));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 7:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_7));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_7));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 8:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_8));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_8));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 9:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_9));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_9));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 10:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_10));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_10));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 11:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_11));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_11));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 12:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_12));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_12));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 13:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_13));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_13));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 14:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_14));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_14));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 15:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_15));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_15));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 16:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_16));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_16));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 17:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_17));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_17));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 18:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_18));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_18));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 19:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_19));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_19));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 20:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_20));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_20));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 21:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_21));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_21));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 22:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_22));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_22));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 23:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_23));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_23));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 24:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_24));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_24));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 25:
                HuDongApplication.getInstance().setTextColor(getResources().getColor(R.color.text_25));
                setBackTint();
                mBaikeReadSlidingAdapter.setTextColor(getResources().getColor(R.color.text_25));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void setBackgroundColor(int position) {
        switch (position + 1) {
            case 1:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_1));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_1));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 2:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_2));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_2));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 3:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_3));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_3));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 4:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_4));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_4));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 5:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_5));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_5));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 6:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_6));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_6));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 7:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_7));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_7));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 8:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_8));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_8));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 9:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_9));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_9));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 10:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_10));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_10));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 11:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_11));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_11));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 12:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_12));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_12));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 13:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_13));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_13));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 14:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_14));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_14));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 15:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_15));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_15));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 16:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_16));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_16));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 17:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_17));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_17));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 18:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_18));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_18));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 19:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_19));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_19));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 20:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_20));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_20));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 21:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_21));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_21));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 22:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_22));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_22));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 23:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_23));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_23));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 24:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_24));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_24));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
            case 25:
                HuDongApplication.getInstance().setBackgroudColor(getResources().getColor(R.color.bac_25));
                setBackTint();
                mBaikeReadSlidingAdapter.setBackgroudColor(getResources().getColor(R.color.bac_25));
                mBaikeReadSlidingAdapter.notifyDataSetChanged();
                break;
        }
    }

    private ScheduledExecutorService scheduledExecutor;

    private void updateAddOrSubtract(int viewId) {
        final int vid = viewId;
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = vid;
                handler.sendMessage(msg);
            }
        }, 0, 200, TimeUnit.MILLISECONDS);    //每间隔100ms发送Message
    }

    private void stopAddOrSubtract() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int viewId = msg.what;
            switch (viewId) {
                case R.id.tv_size_small:
                    reduceValue();   //减小操作
                    break;
                case R.id.tv_size_big:
                    addValue();   //增大操作
                    break;
            }
        }
    };

    private void addValue() {
        if (textSize >= 50)
            return;
        textSize++;
        tv_size.setText(String.valueOf(textSize));
        HuDongApplication.getInstance().setTextSize(textSize);
        mBaikeReadSlidingAdapter.setTextSize(textSize);
        mBaikeReadSlidingAdapter.notifyDataSetChanged();
    }

    private void reduceValue() {
        if (textSize <= 10)
            return;
        textSize--;
        tv_size.setText(String.valueOf(textSize));
        HuDongApplication.getInstance().setTextSize(textSize);
        mBaikeReadSlidingAdapter.setTextSize(textSize);
        mBaikeReadSlidingAdapter.notifyDataSetChanged();
    }

    public void initNotificationBar() {
        //过滤器
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("play");
        mIntentFilter.addAction("end");
        //创建广播接收者的对象
        playBroadcastReceiver = new PlayBroadcastReceiver();
        //注册广播接收者的对象
        registerReceiver(playBroadcastReceiver, mIntentFilter);

        contentView = new RemoteViews(getPackageName(),
                R.layout.notification_control);
        //设置标题名
        contentView.setTextViewText(R.id.tv_book_name, CharUtils.removeHead(baike.getShowCateName()));
        contentView.setTextViewText(R.id.tv_chapter_name, Html.fromHtml(baike.getShowName()));
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            notification = new Notification();
            //初始化通知
            notification.icon = R.mipmap.ic_launcher;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.contentView = contentView;

            Intent intentClick = new Intent(getApplicationContext(), ChapterReaderActivity.class);//新建意图，并设置action标记为"play"，用于接收广播时过滤意图信息
            intentClick.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntentPlay = PendingIntent.getActivity(this, 0,
                    intentClick, PendingIntent.FLAG_CANCEL_CURRENT);
            contentView.setOnClickPendingIntent(R.id.linear_notify, pIntentPlay);//为play控件注册事件

            Intent intentPlay = new Intent("play");//新建意图，并设置action标记为"play"，用于接收广播时过滤意图信息
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1,
                    intentPlay, 0);
            contentView.setOnClickPendingIntent(R.id.tv_stop, pendingIntent);//为play控件注册事件

            Intent intentEnd = new Intent("end");
            PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(this, 1,
                    intentEnd, 0);
            contentView.setOnClickPendingIntent(R.id.tv_end, pendingIntentEnd);
            notification.flags = notification.FLAG_NO_CLEAR;//设置通知点击或滑动时不被清除
            HuDongApplication.getInstance().notManager.notify(SystemConstants.Notification_ID_BASE, notification);//开启通知
        } else {
            Intent intentClick = new Intent(getApplicationContext(), ChapterReaderActivity.class);//新建意图，并设置action标记为"play"，用于接收广播时过滤意图信息
            intentClick.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntentPlay = PendingIntent.getActivity(this, 0,
                    intentClick, PendingIntent.FLAG_CANCEL_CURRENT);
            contentView.setOnClickPendingIntent(R.id.linear_notify, pIntentPlay);//为play控件注册事件

            Intent intentPlay = new Intent("play");//新建意图，并设置action标记为"play"，用于接收广播时过滤意图信息
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1,
                    intentPlay, 0);
            contentView.setOnClickPendingIntent(R.id.tv_stop, pendingIntent);//为play控件注册事件

            Intent intentEnd = new Intent("end");
            PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(this, 1,
                    intentEnd, 0);
            contentView.setOnClickPendingIntent(R.id.tv_end, pendingIntentEnd);
            String channelId = "book_play";

            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId, "book_reader", NotificationManager.IMPORTANCE_MAX);
            HuDongApplication.getInstance().notManager.createNotificationChannel(channel);
            notificationCompat = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(contentView)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(false);
            mNotificationManager = NotificationManagerCompat.from(this);
            mNotificationManager.notify(SystemConstants.Notification_ID_BASE, notificationCompat.build());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void PlaySpeech(PlayEvent playEvent) {
        if (playEvent.isFinish()) {
            mSpeechModel = false;
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                mXunFeiSpeechManager.stopSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                baiduSpeechManager.stop();
            }
            HuDongApplication.getInstance().notManager.cancel(SystemConstants.Notification_ID_BASE);
            if (floatView != null) {
                floatView.hide();
            }
            refreshChapterRemark(true, "");
            showToast( "已退出朗读模式");
            return;
        }
        if (playEvent.getType() == 1) {
            mXunFeiSpeechManager.setEngineType("0");
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                mXunFeiSpeechManager.pauseSpeaking();
                SystemConstants.SPEECH_TYPE = 0;
                mSpeechPopupWindow.setButton(SystemConstants.SPEECH_TYPE);
                contentView.setImageViewResource(R.id.tv_stop, R.drawable.ic_play);
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                baiduSpeechManager.pause();
                SystemConstants.SPEECH_TYPE = 0;
                mSpeechPopupWindow.setButton(SystemConstants.SPEECH_TYPE);
                contentView.setImageViewResource(R.id.tv_stop, R.drawable.ic_play);
            }

        } else {
            mXunFeiSpeechManager.setEngineType("1");
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                mXunFeiSpeechManager.resumeSpeaking();
                SystemConstants.SPEECH_TYPE = 1;
                mSpeechPopupWindow.setButton(SystemConstants.SPEECH_TYPE);
                contentView.setImageViewResource(R.id.tv_stop, R.drawable.ic_stop);
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                baiduSpeechManager.resume();
                SystemConstants.SPEECH_TYPE = 1;
                mSpeechPopupWindow.setButton(SystemConstants.SPEECH_TYPE);
                contentView.setImageViewResource(R.id.tv_stop, R.drawable.ic_stop);
            }

        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            HuDongApplication.getInstance().notManager.notify(SystemConstants.Notification_ID_BASE, notification);//开启通知
        }else{
            mNotificationManager.notify(SystemConstants.Notification_ID_BASE,notificationCompat.build());
        }
    }

    private void initBaiduSpeech() {
        mSpeechPosition = 0;
        mSpeechIndex = 0;
        final ListView listView = mBaikeReadSlidingAdapter.getCurrentListView();
        final int firstView = listView.getFirstVisiblePosition();
        mSpeechIndex = firstView;
        // 初始化合成对象
        if (baiduSpeechManager == null) {
            showProgressDialog("加载中。。。。");
            baiduSpeechManager = new BaiduSpeechManager(this, mainHandler);
            mXunFeiSpeechManager = new XunFeiSpeechManager(this);
            mXunFeiSpeechManager.setTtsListener(mTtsListener);
            if (floatView != null) {
                floatView.setmXunFeiSpeechManager(mXunFeiSpeechManager);
                floatView.setBaiduSpeechManager(baiduSpeechManager);
            }
            mXunFeiSpeechManager.init(mTtsInitListener);
            //先初始化，避免notifa报空指针
            mSpeechPopupWindow = new SpeechPopupWindow(this, mXunFeiSpeechManager, baiduSpeechManager, this);
            mSpeechPopupWindow.setOnClickListener(this);
        } else {
            startSpeech();
        }

    }

    protected void handle(Message msg) {
        switch (msg.what) {
            case INIT_BAIDU_SUCCESS:
                if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                    dismissProgressDialog();
                    startSpeech();
                }
                msg.what = PRINT;
                break;
            case INIT_BAIDU_ERROR:
                if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                    dismissProgressDialog();
                    mSpeechModel = false;
                    showToast( "首次初始化需要连接网络");
                    mXunFeiSpeechManager = null;
                    baiduSpeechManager.release();
                    baiduSpeechManager = null;
                    if (floatView != null) {
                        floatView.hide();
                    }
                    //取消通知栏
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                        HuDongApplication.getInstance().notManager.cancel(SystemConstants.Notification_ID_BASE);
                    } else {
                        if (mNotificationManager != null) {
                            mNotificationManager.cancel(SystemConstants.Notification_ID_BASE);
                        }
                    }
                }
                msg.what = PRINT;
                break;
            case UI_CHANGE_SYNTHES_TEXT_SELECTION://msg.arg1表示当前字体个数
                Log.e("OkHttp", "handle: " + msg.arg1);
                break;
            case UI_ERROR_TEXT_SPEECH://msg.arg1表示当前字体个数
                Log.e("BAIDUSPEECHENGERTEST", "UI_ERROR_TEXT_SPEECH:" + "错误发生：" + msg.arg1);
                if (msg.arg1 == -15) {
                    if (SystemConfig.Speech_Model == 1)
                        return;
                    final String remarkTxt = getSpeechContent(mSpeechIndex, mSpeechPosition);
                    if (TextUtils.isEmpty(remarkTxt)) {
                        showToastMsg("参数读取错误！");
                        return;
                    }
                    final String result = SearchTextUtil.replaceTag("<.+?>", remarkTxt);
                    String result2 = SearchTextUtil.replaceTag("\\(.+?\\)", result);
                    result2 = SearchTextUtil.replaceTag("\\{.+\\}", result2);
                    String result3 = SearchTextUtil.replaceTag("（.+）", result2);
                    result3 = StringUtil.getRealSpeekText(result3);
                    if (TextUtils.isEmpty(result3.trim())) {
                        mainHandler.sendMessage(mainHandler.obtainMessage(UI_FINISH_TEXT_SELECTION, 0, 0));
                        return;
                    }
                    refreshChapterRemark(true, remarkTxt);
                    SystemConfig.readContent = result3;
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                        String txt = result3.replaceAll("行", "行(xing2)");
                        baiduSpeechManager.speak(txt);
                    }

                }
                break;
            case UI_FINISH_TEXT_SELECTION://每句播放结束回调
                if (mSpeechTextMap.get(mSpeechIndex) == null) {
                    showToastMsg("播放完成");
                    return;
                }
                if (SystemConfig.Speech_Model == 1)
                    return;
                if ((mSpeechTextMap.get(mSpeechIndex).size() - 1) <= mSpeechPosition) {
                    mSpeechIndex++;
                    mSpeechPosition = 0;
                    mBaikeReadSlidingAdapter.getCurrentListView().smoothScrollToPosition
                            (mSpeechIndex);
                } else {
                    mSpeechPosition++;
                }
                if (mSpeechTextMap.get(mSpeechIndex) == null) {
                    showToastMsg("播放完成");
                    return;
                }
                final String remarkTxt = getSpeechContent(mSpeechIndex, mSpeechPosition);
                if (TextUtils.isEmpty(remarkTxt)) {
                    showToastMsg("参数读取错误！");
                    return;
                }
                final String result = SearchTextUtil.replaceTag("<.+?>", remarkTxt);
                String result2 = SearchTextUtil.replaceTag("\\(.+?\\)", result);
                result2 = SearchTextUtil.replaceTag("\\{.+\\}", result2);
                String result3 = SearchTextUtil.replaceTag("（.+）", result2);
                result3 = StringUtil.getRealSpeekText(result3);
                if (TextUtils.isEmpty(result3.trim())) {
                    mainHandler.sendMessage(mainHandler.obtainMessage(UI_FINISH_TEXT_SELECTION, 0, 0));
                    return;
                }
                refreshChapterRemark(true, remarkTxt);
//                baiduSpeechManager.batchSpeak(result3);
                SystemConfig.readContent = result3;
                if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                    String txt = result3.replaceAll("行", "行(xing2)");
                    baiduSpeechManager.speak(txt);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void netStatusChange(NetObserver.NetAction action) {
        super.netStatusChange(action);
        if (!action.isAvailable() && mSpeechModel) {
            //网络不可用
            if (mSpeechPopupWindow != null) {
                mSpeechPopupWindow.netUnAvailable();
            }
        }
        if (action.isAvailable()) {
            if (mSpeechPopupWindow != null) {
                mSpeechPopupWindow.netAvailable();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(LoginOutEvent loginOutEvent) {
        //登录失效
        this.finish();
    }

    @Override
    public float smoothNext(ChapterReaderActivity.MoveDistanceListener ls) {
        float slH =  mSlidingLayout.getMeasuredHeight();
        ListView listView = mSlidingLayout.getAdapter().getCurrentView().findViewById(R.id.sliding_listview);
        float diffY = 0;

        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        listView.scrollListBy((int) slH);
        diffY =slH;
        return diffY;
    }

    @Override
    public float smoothLast(ChapterReaderActivity.MoveDistanceListener ls) {
        float slH =  mSlidingLayout.getMeasuredHeight();

        ListView listView = mSlidingLayout.getAdapter().getCurrentView().findViewById(R.id.sliding_listview);
        float diffY = 0;

        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


            }
        });
//        listView.smoothScrollBy(-mSlidingLayout.getMeasuredHeight(),0);
        listView.scrollListBy(-(int) slH);
        diffY =-slH;
        floatView.setNewYPosition(diffY);
        return diffY;
    }
}