package com.read.scriptures.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.music.player.lib.manager.MusicPlayerManager;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.EIUtils.DateUtil;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.AnnItemInfo;
import com.read.scriptures.bean.HistoryBean;
import com.read.scriptures.bean.UserInfo;
import com.read.scriptures.broadcast.PlayBroadcastReceiver;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.control.BaiduSpeechManager;
import com.read.scriptures.db.CategoryDatabaseHelper;
import com.read.scriptures.db.ChapterDatabaseHepler;
import com.read.scriptures.db.DatabaseManager;
import com.read.scriptures.db.VolumeDatabaseHepler;
import com.read.scriptures.event.LoginOutEvent;
import com.read.scriptures.event.PlayEvent;
import com.read.scriptures.event.RefreshChapterListEvent;
import com.read.scriptures.listener.MainHandlerConstant;
import com.read.scriptures.manager.XunFeiSpeechManager;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.model.Volume;
import com.read.scriptures.net.NetObserver;
import com.read.scriptures.service.NotifacationService;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.ChapterReadAdapter;
import com.read.scriptures.ui.adapter.ChapterReadMenuGvAdapter;
import com.read.scriptures.ui.adapter.ChapterReadSlidingAdapter;
import com.read.scriptures.ui.fragment.BaseFullBottomSheetFragment;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.CollectionUtil;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.GsonUtils;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.MTextUtil;
import com.read.scriptures.util.NumberUtil;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.SystemUtils;
import com.read.scriptures.util.UmShareUtils;
import com.read.scriptures.view.AudioPlayingView;
import com.read.scriptures.widget.BatteryView;
import com.read.scriptures.widget.FloatView;
import com.read.scriptures.widget.FloatView.Reader;
import com.read.scriptures.widget.QSelectDialog;
import com.read.scriptures.widget.ReadOptionsPopupWindow;
import com.read.scriptures.widget.SelectDialogShowItem;
import com.read.scriptures.widget.SeleteTextSizePopupWindow;
import com.read.scriptures.widget.SpeechPopupWindow;
import com.read.scriptures.widget.TouchInterceptSlidingView;
import com.read.scriptures.widget.VersionSettingDialog;
import com.read.scriptures.widget.colorpicker.ColorPickerDialog;
import com.read.scriptures.widget.sliding.SlidingAdapter;
import com.read.scriptures.widget.sliding.SlidingLayout;
import com.read.scriptures.widget.sliding.slider.PageSlider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChapterReaderActivity extends BaseActivity implements OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SeekBar
                .OnSeekBarChangeListener, Reader, MainHandlerConstant, FloatView.ListViewListener {

    // 包级访问：停止朗读并取消通知栏，供 ChapterReaderSpeechDelegate 调用
    void stopSpeechAndNotification() {
        if (mSpeechModel) {
            mSpeechModel = false;
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF && mXunFeiSpeechManager != null) {
                mXunFeiSpeechManager.stopSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU && baiduSpeechManager != null) {
                baiduSpeechManager.stop();
            }
            if (notificationHelper != null) notificationHelper.cancelNotification();
        }
    }

    // 包级访问：供 ChapterReaderSettingDelegate 调用 protected showToast
    void showToastPkg(String msg) {
        showToast(msg);
    }

    // 包级访问：供 ChapterReaderSettingDelegate 使用（同包内可见）
    RelativeLayout mParentView;
    RelativeLayout mRlReaderView;
    // 顶部数据信息栏
    RelativeLayout mTitleLayout;
    TextView mTitleVolumeTextView;
    TextView mTitletvCategoryName;
    TextView mTitletvCategoryType;
    TextView mTitleChapterTextView;
    ImageView iv_back;
    PopupWindow popSetting;
    PopupWindow popMarginSetting;
    PopupWindow popColorSetting;
    View dex;
    // 底部状态栏
    TextView mStatusWeekTextView;
    TextView mStatusBatteryTextView;
    TextView mStatusTimeTextView;
    RelativeLayout mStatusLayout;

    TextView tvItemTxt;

    BatteryView mStatusBatteryView;
    // 顶部操作栏
    private RelativeLayout mOptionLayout;
    private TextView mOptionSelectNumTextView;

    // 章节内容翻页控件
    TouchInterceptSlidingView mSlidingLayout;//===========================================================
    ChapterReadSlidingAdapter mChapterReadSlidingAdapter;//===============================================

    /**
     * 获取章节阅读滑动适配器
     * @return ChapterReadSlidingAdapter
     */
    public ChapterReadSlidingAdapter getChapterReadSlidingAdapter() {
        return mChapterReadSlidingAdapter;
    }

    Chapter mChapter;
    private String enter = "";
    private int mTipsPostion;
    private String mTipsKeyword;
    private String mTipsContent;
    List<Chapter> mChapters;

    /**
     * 获取章节列表
     * @return List<Chapter>
     */
    public List<Chapter> getChapters() {
        return mChapters;
    }

    private String categoryName;
    private int mSearchType;
    private String mChapterName;
    private String mChapterContent;
    private String mChapterType;
    private String mChapterNameKeyWord;
    private String mVolumeName;
    private int mVolumeId;
    private Dialog mSettingOptionDialog;

    private ChapterReadMenuGvAdapter mChapterReadMenuGvAdapter;//=============================
    private ReadOptionsPopupWindow mReadOptionsPopupWindow;
    private SeleteTextSizePopupWindow mSeleteTextSizePopupWindow;
    SpeechPopupWindow mSpeechPopupWindow;

    // 图片资源
    private List<Integer> mPictures = new ArrayList<>();

    // 文字资源
    private ArrayList<String> mTitles = new ArrayList<>();
    private boolean mSelectModel = false;// 编辑模式
    boolean mSpeechModel = false;// 朗读模式
    FloatView floatView;
    private int oldIndex;
    private int oldPosition;
    private int currentCount = 0;
    private int textSize = 0;


    private Notification notification;
    ReadingNotificationHelper notificationHelper; // 通知栏管理模块
    //8.0及以上版本使用
    private NotificationCompat.Builder notificationCompat;
    private NotificationManagerCompat mNotificationManager;
    private RemoteViews contentView;
    private IntentFilter mIntentFilter = null;
    private PlayBroadcastReceiver playBroadcastReceiver = null;

    String title = "";

    boolean SlideNextByBackground = false;
    boolean isBack = false;
    //百度语音参数
    protected Handler mainHandler;
    BaiduSpeechManager baiduSpeechManager;//===============================================

    //注释
    private List<AnnItemInfo> tabList;

//    CategoryDatabaseHelper categoryDatabaseHelper;

    private final int HUAI_ZHU = 2;//怀著标致

    private int HUAI_ZHU_CHAPTER = 0;//怀著标致  2

    int HUAI_ZHU_CHAPTER_HAS_ZW = 0;//怀著还有中文标致 1

    private AudioPlayingView fl_view;

    private List<Category> mRootCategorys;
    private Map<Category, List<Category>> mRootCategoryMaps;

    // ================================================================
    // REGION: 生命周期（onCreate / onResume / onPause / onDestroy 等）
    // 文件位置：第 259 行 — 第 690 行
    // ================================================================
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕唤醒
        setContentView(R.layout.activity_chapter_reader);

        mChapter = getIntent().getParcelableExtra(BundleConstants.PARAM_CHAPTER);
        enter = getIntent().getStringExtra(BundleConstants.PARAM_ENTER);
        mTipsPostion = getIntent().getIntExtra(BundleConstants.PARAM_TIPS_POSTION, 0);
        mTipsKeyword = getIntent().getStringExtra(BundleConstants.PARAM_TIPS_KEYWORD);
        mTipsContent = getIntent().getStringExtra(BundleConstants.PARAM_TIPS_CONTENT);
        //重置注释数据
        SharedUtil.putString(this, BundleConstants.ANN_LIST, "");
        tvItemTxt = findViewById(R.id.tv_item_txt);
        showProgressDialog("正在加载...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mChapters = new ChapterDatabaseHepler(ChapterReaderActivity.this).getChapterList(mChapter.getVolumeId());
                // 初始化搜索数据
                CategoryDatabaseHelper categoryHepler = new CategoryDatabaseHelper(ChapterReaderActivity.this);
                mRootCategorys = new ArrayList<Category>();
                mRootCategorys.addAll(categoryHepler.getCategroyList(0));
                mRootCategoryMaps = new HashMap<Category, List<Category>>();
                for (int i = 0; i < mRootCategorys.size(); i++) {
                    List<Category> categorys = categoryHepler.getCategroyList(mRootCategorys.get(i).getId());
                    mRootCategoryMaps.put(mRootCategorys.get(i), categorys);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initIntentExtras();
                        initViews();
                        title = mChapter.getShowVolumeName().replaceAll("\\((.*?)\\)", "").replaceAll("\\[(.*?)\\]", "")
                                .replaceAll("\\{(.*?)\\}", "");
                        if (MTextUtil.isContainChinese(title) && title.contains("E")) {
                            title = MTextUtil.changeEletter(title);
                        }
                        if (mTitletvCategoryType != null && mChapters != null && mChapters.size() > 0) {
                            mTitletvCategoryType.setText(getType(mChapters.get(0).getCategoryId() + ""));
                        }
                        mTitleVolumeTextView.setText(title);//"《" + mChapter.getShowVolumeName().replaceAll("\\(.+\\)", "") + "》"
                        setChapterNameText(mChapter.getShowName());
                        boolean isSJmode = isLuoJiShengJing(mChapters);
                        if (isSJmode) {
                            tvItemTxt.setText("");
                            tvItemTxt.setVisibility(View.VISIBLE);
                        } else {
                            tvItemTxt.setVisibility(View.GONE);
                        }
                        dismissProgressDialog();
                    }
                });
            }
        }).start();
        initActionBar();
        mSettingDelegate = new ChapterReaderSettingDelegate(this, mChapterReadSlidingAdapter, handler);
        speechDelegate = new ChapterReaderSpeechDelegate(this);
        notificationHelper = new ReadingNotificationHelper(this);
        mSettingDelegate.initSettingPop();
        mSettingDelegate.initMarginPop();
        mSettingDelegate.initColorPop();
        initDatetimeUpdateThread();
        startService(new Intent(ChapterReaderActivity.this, NotifacationService.class));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        enter = getIntent().getStringExtra(BundleConstants.PARAM_ENTER);
    }

    //获取传递的参数
    private void initIntentExtras() {
        //处理注释数据
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonStr = extras.getString(BundleConstants.ANN_MAP);
            categoryName = extras.getString(BundleConstants.PARAM_CATEGORY);
            mChapterName = extras.getString(BundleConstants.PARAM_CHAPTER_NAME);
            mChapterContent = extras.getString(BundleConstants.PARAM_CHAPTER_CONTENT);
            mChapterType = extras.getString(BundleConstants.PARAM_CATEGORY_TYPE);
            mChapterNameKeyWord = extras.getString(BundleConstants.PARAM_CHAPTER_NAME_KEY_WORD);
            mSearchType = extras.getInt(BundleConstants.PARAM_SEARCH_TYPE);

            mTitletvCategoryType.setText(mChapterType);
            LinkedHashMap<String, String> annMap = new Gson().fromJson(jsonStr, new TypeToken<LinkedHashMap<String, String>>() {
            }.getType());
            if (annMap != null && annMap.size() > 0) {
                tabList = new ArrayList<>();
                for (String key : annMap.keySet()) {
                    tabList.add(new AnnItemInfo(false, key, annMap.get(key)));
                }
                tabList.get(0).setCheck(true);
                SharedUtil.putString(this, BundleConstants.ANN_LIST, GsonUtils.objectToStr(tabList));
            }
        }

        if (mChapter == null) {
            showToastMsg("参数错误！");
            finish();
            return;
        }
        saveHistoryInfo();

        mVolumeName = mChapter.getShowVolumeName();
        mVolumeId = mChapter.getVolumeId();

        String[] titles = getResources().getStringArray(R.array.chapter_read_8option);
        Integer[] pictures = new Integer[]{R.drawable.ic_menu_option_6, R.drawable.ic_menu_option_2, R.drawable.ic_menu_option_3, R.drawable.ic_menu_option_4, R.drawable.ic_menu_option_5, R.drawable.ic_menu_option_8, R.drawable.ic_menu_option_22};//, R.drawable.ic_menu_option_1
        mTitles.clear();
        mPictures.clear();
        mTitles.addAll(Arrays.asList(titles));
        mPictures.addAll(Arrays.asList(pictures));
        if (mChapter != null) {
            int categoryLevelOneId = new VolumeDatabaseHepler(getApplicationContext()).getCategoryLeve1IdByVolumeID(mChapter.getVolumeId());
            if (categoryLevelOneId != 1) {
                //不是圣经则不显示注释按钮
                mTitles.remove(0);
                mPictures.remove(0);
            }
        }
        if (!isLuoJiShengJing(mChapters)) {
            mTitles.remove("版本对照");
            mPictures.remove(new Integer(R.drawable.ic_menu_option_22));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle state = new Bundle();
        state.putParcelable(BundleConstants.PARAM_CHAPTER, mChapter);
        state.putInt(BundleConstants.PARAM_TIPS_POSTION, mTipsPostion);
        state.putString(BundleConstants.PARAM_TIPS_KEYWORD, mTipsKeyword);
        state.putString(BundleConstants.PARAM_TIPS_CONTENT, mTipsContent);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mChapter = savedInstanceState.getParcelable(BundleConstants.PARAM_CHAPTER);
        mTipsPostion = savedInstanceState.getInt(BundleConstants.PARAM_TIPS_POSTION, 0);
        mTipsKeyword = savedInstanceState.getString(BundleConstants.PARAM_TIPS_KEYWORD);
        mTipsContent = savedInstanceState.getString(BundleConstants.PARAM_TIPS_CONTENT);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("this", "当前屏幕为横屏");
        } else {
            Log.d("this", "当前屏幕为竖屏");
        }
        // 重新设置平移模式，否则宽度不能设配屏幕宽度变化。
        mSlidingLayout.setSlider(new PageSlider());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (floatView != null) {
            floatView.hide();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        isBack = true;
    }


    public String getType(String categoryId) {
        if (mRootCategoryMaps != null && mRootCategoryMaps.size() > 0) {
            for (Map.Entry<Category, List<Category>> entry : mRootCategoryMaps.entrySet()) {
                for (Category category : entry.getValue()) {
                    if (categoryId.equals(category.getId() + "")) {
                        return entry.getKey().getCateName() + "-" + category.getCateName();
                    }
                }
            }
        }
        return null;
    }

    private void initActionBar() {
        mParentView = findViewById(R.id.layout_chapter_read);
        mRlReaderView = findViewById(R.id.rl_reader_view);
        // 顶部bar
        mTitleLayout = findViewById(R.id.layout_title);

        iv_back = findViewById(R.id.btn_back);
        iv_back.setOnClickListener(this);
        dex = findViewById(R.id.dex_line);
        mTitleVolumeTextView = findViewById(R.id.tv_volume);
        mTitletvCategoryName = findViewById(R.id.tv_category_name);
        mTitletvCategoryType = findViewById(R.id.tv_category_type);
        mTitleVolumeTextView.setOnClickListener(this);
        mTitletvCategoryName.setVisibility(View.VISIBLE);
        mTitletvCategoryName.setText(categoryName);
        mTitleChapterTextView = findViewById(R.id.tv_chapter);

        // 底部bar
        mStatusLayout = findViewById(R.id.layout_status);
        mStatusWeekTextView = findViewById(R.id.tv_week);
        mStatusTimeTextView = findViewById(R.id.tv_book_name);
        mStatusBatteryTextView = findViewById(R.id.tv_strong);
        mStatusBatteryView = findViewById(R.id.battery_view);
        mStatusWeekTextView.setText(DateUtil.getWeekStr(DateUtil.getStringDateShort()));
        mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm"));
        showTabbarBackgroundColor();
    }

    boolean isLuoJiShengJing(List<Chapter> mChapters) {
        if (mChapters != null && !mChapters.isEmpty()) {
            Chapter chapter = mChapters.get(0);
            String zjContent = chapter.getContent();
            if (chapter != null && HUAI_ZHU == chapter.getParentId()) {
                HUAI_ZHU_CHAPTER = 2;
                if (zjContent != null && (zjContent.contains("〖中文〗") || zjContent.contains("〖英文〗"))) {
                    HUAI_ZHU_CHAPTER_HAS_ZW = 1;
                    return true;
                }
            }
            if (!TextUtils.isEmpty(zjContent)) {
                String[] dlContent = zjContent.split("\n");
                if (dlContent != null && dlContent.length > 0) {
                    for (String content : dlContent) {
                        if (!TextUtils.isEmpty(content) && content.length() > 12) {
                            String preTxt = content.substring(0, 12);
                            String shengJingTxt = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", preTxt);
                            if (!TextUtils.isEmpty(shengJingTxt) && content.replace(" ", "").startsWith(shengJingTxt))
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void initViews() {
        textSize = HuDongApplication.getInstance().getTextSize();
        // 编辑
        mOptionLayout = findViewById(R.id.layout_option);
        mOptionLayout.findViewById(R.id.btn_cancle).setOnClickListener(this);
        mOptionLayout.findViewById(R.id.btn_complete).setOnClickListener(this);
        mOptionSelectNumTextView = findViewById(R.id.tv_select_num);
        fl_view = findViewById(R.id.fl_view);

        boolean isSJmode = isLuoJiShengJing(mChapters);
        if (isSJmode) {
            tvItemTxt.setText("");
            tvItemTxt.setVisibility(View.VISIBLE);
        } else {
            tvItemTxt.setVisibility(View.GONE);
        }

        mChapterReadSlidingAdapter = new ChapterReadSlidingAdapter(this, mChapters, mChapter.getChapterIndex(), isSJmode, HUAI_ZHU_CHAPTER, HUAI_ZHU_CHAPTER_HAS_ZW);
        mChapterReadSlidingAdapter.setShengJing(isSJmode);
        mChapterReadSlidingAdapter.setTipsPostion(mTipsPostion - 1);
        mChapterReadSlidingAdapter.setTipsKeyword(mTipsKeyword);
        mChapterReadSlidingAdapter.setSearchType(mSearchType);
        mChapterReadSlidingAdapter.setChapterName(mChapterName);
        mChapterReadSlidingAdapter.setChapterContent(mChapterContent);
        mChapterReadSlidingAdapter.setChapterNameKeyWord(mChapterNameKeyWord);
        mChapterReadSlidingAdapter.setTipContent(mTipsContent);
        mChapterReadSlidingAdapter.setTextSize(HuDongApplication.getInstance().getTextSize());
        mChapterReadSlidingAdapter.setTextMargin(HuDongApplication.getInstance().getTextMagin());
        mChapterReadSlidingAdapter.setLineMargin(HuDongApplication.getInstance().getmLineMargin());
        mChapterReadSlidingAdapter.setTextAroundMargin(HuDongApplication.getInstance().getTextAround());
        mChapterReadSlidingAdapter.setTextColor(HuDongApplication.getInstance().getTextColor());
        mChapterReadSlidingAdapter.setTextModel(HuDongApplication.getInstance().getTextModel());
        mChapterReadSlidingAdapter.setReadModel(HuDongApplication.getInstance().getReadModel());
        mChapterReadSlidingAdapter.setBackgroudColor(HuDongApplication.getInstance().getBackgroudColor());
        mChapterReadSlidingAdapter.setOnItemClickListener(this);
        mChapterReadSlidingAdapter.setOnItemLongClickListener(this);
        mSlidingLayout = findViewById(R.id.sliding_layout);
        // 默认为左右平移模式
        mSlidingLayout.setSlider(new PageSlider());

        mSlidingLayout.setAdapter(mChapterReadSlidingAdapter);

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

        mSlidingLayout.setTabEventlister(new SlidingLayout.OnTabEventlister() {
            @Override
            public void onTabMove() {
                tvItemTxt.setVisibility(View.GONE);
            }

            @Override
            public void onTabUp() {
                ListView listView = null;
                if (mSlidingLayout != null && mSlidingLayout.getAdapter() != null) {
                    SlidingAdapter adapter = mSlidingLayout.getAdapter();
                    if (adapter.hasPrevious()) {
                        View prevView = mSlidingLayout.getAdapter().getPreviousView();
                        listView = prevView.findViewById(R.id.sliding_listview);
                        if (listView != null) {
                            listView.setSelection(0);
                        }
                    } else {
//                        ToastUtil.showMessage(ChapterReaderActivity.this,"已经是第一页了");
                    }
                    if (adapter.hasNext()) {
                        View nextView = mSlidingLayout.getAdapter().getNextView();
                        listView = nextView.findViewById(R.id.sliding_listview);
                        if (listView != null) {
                            listView.setSelection(0);
                        }
                    } else {
//                        ToastUtil.showMessage(ChapterReaderActivity.this,"已经是最后一页了");
                    }
                }
            }
        });

        mChapterReadSlidingAdapter.setOnMoveEventlister(new ChapterReadSlidingAdapter.OnMoveEventlister() {
            @Override
            public void onMove() {
                listScrollLister();
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
                if (!SlideNextByBackground) {//如果已经在后台把数据切换到下一章了，不需要在设置
                    if (mChapter != null)
                        PreferenceConfig.saveReadingProgress(getApplicationContext(), mChapter.getVolumeId(), mChapterReadSlidingAdapter.getPageIndex());
                    mChapter = mChapters.get(mChapterReadSlidingAdapter.getPageIndex());
                    if (mChapter != null) {
                        saveHistoryInfo();
                    }
                    setChapterNameText(mChapter.getShowName());
                    //TODO 通知栏更新阅读状态
                    if (contentView != null) {
                        //设置标题名
                        if (MTextUtil.isContainChinese(title) && title.contains("E")) {
                            title = MTextUtil.changeEletter(title);
                        }
                        contentView.setTextViewText(R.id.tv_book_name, title);
                        Log.w("TTT", "R.id.tv_chapter_name:" + mChapter.getShowName());
                        contentView.setTextViewText(R.id.tv_chapter_name, mChapter.getShowName());
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                            HuDongApplication.getInstance().notManager.notify(SystemConstants.Notification_ID_BASE, notification);//开启通知
                        } else {
                            mNotificationManager.notify(SystemConstants.Notification_ID_BASE, notificationCompat.build());
                        }
                    }
                    if (mSpeechModel) {
                        stopSpeechAndNotification();
                        mSpeechModel = true; // 翻章继续朗读
                        mSpeechIndex = 0;
                        mSpeechPosition = 0;
                        startSpeech();
                    }
                    final ListView listView = mChapterReadSlidingAdapter.getCurrentListView();
                    currentCount = listView.getCount();
                    listView.setOnScrollListener(new OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(final AbsListView view, final int scrollState) {
                            listScrollLister();
                        }

                        @Override
                        public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
                            listScrollLister();
                            if (CommonUtil.isListViewReachBottomEdge(listView)) {
                                mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm") + "" + " " + "已阅100%");
                            } else {
                                mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm") + "" + getReadProgress());
                            }
                        }
                    });

                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            //重置注释数据
                            SharedUtil.putString(getApplication(), BundleConstants.ANN_LIST, "");
                            //更新注释信息
                            Map<String, String> annMap = new ChapterDatabaseHepler(ChapterReaderActivity.this).getChapterAnnotationList(mChapter.getVolumeId(), mChapter.getShowName());
                            if (annMap != null && annMap.size() > 0) {
                                tabList = new ArrayList<>();
                                for (String key : annMap.keySet()) {
                                    tabList.add(new AnnItemInfo(false, key, annMap.get(key)));
                                }
//                                Collections.reverse(tabList);
                                tabList.get(0).setCheck(true);
                                SharedUtil.putString(getApplication(), BundleConstants.ANN_LIST, GsonUtils.objectToStr(tabList));
                            }
                        }
                    };
                    thread.start();
                } else {
                    mChapterReadSlidingAdapter.setPageIndex(mChapterReadSlidingAdapter.getPageIndex() - 1);
                }
                SlideNextByBackground = false;
            }
        });
        if (mChapter != null) {
            PreferenceConfig.saveReadingProgress(getApplicationContext(), mChapter.getVolumeId(), mChapterReadSlidingAdapter.getPageIndex());
        }
        initFloatView();
    }

    // ================================================================
    // REGION: 滚动监听 / 翻页控制
    // 文件位置：第 695 行 — 第 815 行
    // ================================================================
    private void listScrollLister() {
        boolean isSJmode = isLuoJiShengJing(mChapters);
        try {
            if (!isSJmode) {
                return;
            }
            if (mSlidingLayout != null &&
                    mSlidingLayout.getAdapter() != null &&
                    mSlidingLayout.getAdapter().getCurrentView() != null
            ) {
                ListView listView = mSlidingLayout.getAdapter().getCurrentView().findViewById(R.id.sliding_listview);
                if (listView == null) {
                    return;
                }
                ChapterReadAdapter adapter = (ChapterReadAdapter) listView.getAdapter();
                int firstPostion = listView.getFirstVisiblePosition();

                String indexTxt = getIndexTxt(adapter, firstPostion);

                // 有第二栏，第二栏是新的
                if (listView.getCount() > firstPostion + 1) {
                    String content2 = adapter.getItem(firstPostion + 1);
                    if (!TextUtils.isEmpty(content2) && content2.length() > 12) {
                        String preTxt2 = content2.substring(0, 12);
                        String headTxt2 = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", preTxt2);
                        if (!TextUtils.isEmpty(headTxt2)) {
                            View view = listView.getChildAt(0);
                            float y = view.getY();
                            float iheight = view.getHeight();
                            int height = tvItemTxt.getHeight();

                            if (y < 0 && iheight + y < height) {
                                tvItemTxt.setY(iheight + y - height);
                            } else {
                                tvItemTxt.setY(0);
                            }
                        } else {
                            tvItemTxt.setY(0);
                            tvItemTxt.setVisibility(View.VISIBLE);
                        }
                    }

//                    if (HUAI_ZHU_CHAPTER == 2) {
//                        if (!TextUtils.isEmpty(content2) && content2.contains("【中文】") || content2.contains("【英文】")) {
//                            showLog("怀著 move");
//                            View view = listView.getChildAt(0);
//                            float y = view.getY();
//                            float iheight = view.getHeight();
//                            int height = tvItemTxt.getHeight();
//
//                            if (y < 0 && iheight + y < height) {
//                                tvItemTxt.setY(iheight + y - height);
//                            } else {
//                                tvItemTxt.setY(0);
//                            }
//                        } else {
//                            showLog("怀著 tvItemTxt 不可见");
//                            tvItemTxt.setVisibility(View.GONE);
//                        }
//
//                    }

                }
                // 是带标题栏
                View view = listView.getChildAt(0);


                tvItemTxt.setText(indexTxt);

                int[] location = new int[2];
                view.getLocationInWindow(location); //获取在当前窗口内的绝对坐标

                if (location[0] != 0) {
//                            tvItemTxt.setX(location[0]);
                    tvItemTxt.setVisibility(View.GONE);
                } else {
//                            tvItemTxt.setX(0);
                    tvItemTxt.setVisibility(View.VISIBLE);
                    if (HUAI_ZHU_CHAPTER == 2) {
                        tvItemTxt.setVisibility(View.GONE);
                    }
                }

            }
        } catch (Exception e) {
            Log.e("调试", "出错了 message = " + e.getMessage());
        }
    }

    private String getIndexTxt(ChapterReadAdapter adapter, int firstPostion) {
        String indexTxt = "";


        if (adapter != null && firstPostion >= 0 && adapter.getCount() > firstPostion) {
            for (int i = firstPostion; i >= 0; i--) {
                String content = adapter.getItem(i);
                if (!TextUtils.isEmpty(content) && content.length() > 12) {
                    String preTxt = content.substring(0, 12);

                    String headTxt = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", preTxt);


                    if (!TextUtils.isEmpty(headTxt)) {
                        int startIndex = headTxt.lastIndexOf(":") + 1;
                        indexTxt = headTxt.substring(startIndex);
                    } else {
                        indexTxt = "";
                    }

                    if (!TextUtils.isEmpty(indexTxt)) {
                        return indexTxt;
                    }
                }
            }
        }

        return indexTxt;
    }

    //将阅读控件与触摸view绑定
    public void initFloatView() {
        if (floatView != null) {
            return;
        }
        floatView = new FloatView(getApplicationContext());
        floatView.hide();
        floatView.setBottomStatus(mStatusLayout);
        floatView.setReader(this);
        floatView.setParent(mParentView);
        floatView.setSlidingLayout(mSlidingLayout);
        floatView.setmClickListener(new FloatView.FloatViewOnClickListener() {
            @Override
            public void onClick(float x, float y) {
                if (x < iv_back.getRight() + 100 && y < mTitleLayout.getBottom()) {
                    iv_back.performClick();
                    //恢复

                } else {
                    showSpeechPopupWindow();
                    floatView.hide();
                }
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        floatView.setLayoutParams(params);
        floatView.setListViewListenner(this);
        mParentView.addView(floatView);
//        mSlidingLayout.addView(floatView);
    }

    /**
     * 获取当前阅读进度
     *
     * @return
     */
    private String getReadProgress() {
        if (mChapterReadSlidingAdapter == null) {
            return "";
        }
        ListView listView = mChapterReadSlidingAdapter.getCurrentListView();
        int firstView = listView.getFirstVisiblePosition();
        double result = 0;
        if (currentCount != 0) {
            result = NumberUtil.keepEffectiveNumbers(((firstView * 1d) / currentCount) * 100, 2);
        }

        return " 已阅" + result + "%";
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

    private final Handler mDatetimeUpdateHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            mStatusWeekTextView.setText(DateUtil.getWeekStr(DateUtil.getStringDateShort()));
            mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm") + "" + getReadProgress());
        }
    };

    /**
     * 弹出PopupWindow
     */
    public void initMenuDialog() {

        // 加载Dialog的布局文件
        mChapterReadMenuGvAdapter = new ChapterReadMenuGvAdapter(this, mPictures, mTitles);
        final View view = LayoutInflater.from(ATHIS).inflate(R.layout.popup_chapter_read_menu,
                null);
        final GridView gridView = view.findViewById(R.id.popup_gv_setting);
//        if (mTitles.size() % 3 == 0) {
        if (mTitles.size() > 6) {
            gridView.setNumColumns(4);
        } else {
            gridView.setNumColumns(3);
        }
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(mChapterReadMenuGvAdapter);

        mSettingOptionDialog = new Dialog(ATHIS, R.style.ActionSheetDialogStyle);
        mSettingOptionDialog.setContentView(view);
        mSettingOptionDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface dialog) {
                longClick = false;
            }
        });
        final WindowManager.LayoutParams lp = mSettingOptionDialog.getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT; // 宽度设置为屏幕的
        lp.height = DensityUtil.dip2px(ATHIS, 190);
        lp.alpha = 0.9f;
        mSettingOptionDialog.getWindow().setAttributes(lp);
        mSettingOptionDialog.getWindow().setGravity(Gravity.LEFT | Gravity.BOTTOM); // 设置靠右对齐
        mSettingOptionDialog.setCanceledOnTouchOutside(true);
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
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        onMenuOpened(0, null);
        return false;
    }


    @Override
    // ================================================================
    // REGION: 点击事件分发（onItemClick / onClick 等）
    // 文件位置：第 957 行 — 第 1180 行
    // ================================================================
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        switch (parent.getId()) {
            case R.id.sliding_listview:
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

    /**
     * 章节内容Listview Item点击
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    private void onItemClickOfSlidingListView(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (longClick) {
            return;
        }
        // 朗读模式
        if (mSpeechModel) {
            // 修复：弹窗打开前先读取当前内容赋値给 readContent
            // 避免弹窗关闭时 startSpeaking 发现 readContent 为空而不播放
            String remarkTxt = getSpeechContent(mSpeechIndex, mSpeechPosition);
            if (!TextUtils.isEmpty(remarkTxt)) {
                remarkTxt = remarkTxt.replace("<b>", "").replace("</b>", "");
                SystemConfig.readContent = StringUtil.getRealSpeekText(remarkTxt);
            }
            showSpeechPopupWindow();
            return;
        }
        boolean isTopSelect = false;
        if (mSlidingLayout != null) {
            ListView listView = mSlidingLayout.getAdapter().getCurrentView().findViewById(R.id.sliding_listview);
            if (listView != null) {
                if (listView.getFirstVisiblePosition() == position) {
                    isTopSelect = true;
                }
            }
        }

        final ChapterReadAdapter adapter = (ChapterReadAdapter) parent.getAdapter();
        if (adapter.isSelectModel()) {
            // 已是编辑模式
            adapter.setChecked(position, !adapter.isCheckedKey(position));
            adapter.updateView(parent, position);
            mOptionSelectNumTextView.setText("已选中" + adapter.getCheckedCount() + "段");

            if (isTopSelect) {
                tvItemTxt.setBackgroundColor(adapter.getBackgroudColor());
            } else {
                tvItemTxt.setBackgroundColor(normalColor);
            }
        } else {
            // 开启编辑模式
            mOptionLayout.setVisibility(View.GONE);
            StatusBarUtils.initColorStatusBar(this, HuDongApplication.getInstance().getBackgroudColor());
            mSlidingLayout.setIsPagingEnabled(true);
            adapter.setSelectModel(true);
            adapter.setChecked(position, true);
            adapter.updateView(parent, position);
            showReadOptionsPopupWindow(true);

            if (isTopSelect) {
                tvItemTxt.setBackgroundColor(adapter.getBackgroudColor());
            } else {
                tvItemTxt.setBackgroundColor(normalColor);
            }
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
    int position, final long id) {
        switch (position) {
            case 0:
                mSelectModel = true;
                mOptionLayout.setVisibility(View.VISIBLE);
                StatusBarUtils.initColorStatusBar(this, getResources().getColor(R.color.black));
                break;
            case 1:
                mSelectModel = false;
                goBookmarkEditActivity();
                break;
            case 2:
                // 分享
                goShare();
                break;
            case 3:
                // 复制书名
                break;
            case 4:
                // 复制章节
                break;
            case 5:
                mSelectModel = false;
                goCopy();
                break;
            case 6:
                goJumpLinks();
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
    int position, final long id) {
        mSettingOptionDialog.dismiss();
        // mOptionLayout.setVisibility(View.GONE);
        showReadOptionsPopupWindow(false);

        if (mTitles.get(position).equals("显示注释")) {//显示注释
            if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                CommonUtil.showActivateDialog(this, UserInfo.VIP_NORMAL);
                return;
            }
            BaseFullBottomSheetFragment sheetFragment = new BaseFullBottomSheetFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("rootHeight", mParentView.getHeight());
            sheetFragment.setArguments(bundle);
            sheetFragment.show(getSupportFragmentManager(), "dialog");
        } else if (mTitles.get(position).equals("内容朗读")) {// 朗读
            if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                CommonUtil.showActivateDialogWithCancelAction(ATHIS, UserInfo.VIP_NORMAL, new Runnable() {
                    public void run() {
                    }
                });
                return;
            }
            changeSpeech();
            MusicPlayerManager.getInstance().pause();
            fl_view.hide();
        } else if (mTitles.get(position).equals("字体设置")) {//字体
            if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                CommonUtil.showActivateDialogWithCancelAction(ATHIS, UserInfo.VIP_NORMAL, new Runnable() {
                    public void run() {
                    }
                });
                return;
            }
            if (mSettingDelegate != null) mSettingDelegate.updateTextSizeTv(textSize);
            popSetting.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
//            changeTextSizePopWindow();
        } else if (mTitles.get(position).equals("搜索本书")) {//搜索
            searchVolumeContent();
        } else if (mTitles.get(position).equals("书签目录")) {//书签
            goBookmarkListActivity(false);
        } else if (mTitles.get(position).equals("横竖阅读")) {//横竖屏
            changeSreenOrientation();
        } else if (mTitles.get(position).equals("夜间模式") || mTitles.get(position).equals("普通模式")) {//夜间
            changeReadModel();
        } else if (mTitles.get(position).equals("版本对照")) {//版本对照
            versionSetting();
        }
    }

    // 修复：复用 VersionSettingDialog，避免每次都 new 一个新对象不释放
    private VersionSettingDialog mVersionSettingDialog;

    private void versionSetting() {
        if (mVersionSettingDialog == null || !mVersionSettingDialog.isShowing()) {
            mVersionSettingDialog = new VersionSettingDialog(this, HUAI_ZHU_CHAPTER)
                    .setListener(new VersionSettingDialog.OnChoiceClickListener() {
                        @Override
                        public void versionSelect(List<String> versions) {
                            if (versions == null || versions.isEmpty()) return;
                            if (HUAI_ZHU_CHAPTER != 2) {
                                HuDongApplication.mVersions = versions;
                            } else {
                                HuDongApplication.mVersions_HZ = versions;
                            }
                            // 修复：每次回调只调 notifyDataSetChanged()
                            // 原来每次先重复 set 大量 adapter 属性再刷新，这些 set 操作本身就很耗时
                            // 实际上 versions 已经通过全局变量共享，adapter 内部读取的就是最新值
                            // 直接 notifyDataSetChanged() 就够
                            try {
                                mChapterReadSlidingAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        mVersionSettingDialog.show();
    }

    private void setChapterModel(final boolean model) {
        mSelectModel = model;
        mOptionSelectNumTextView.setText("已选中1段");
        mOptionLayout.setVisibility(model ? View.VISIBLE : View.GONE);
        StatusBarUtils.initColorStatusBar(this, model ? getResources().getColor(R.color.black) : HuDongApplication.getInstance().getBackgroudColor());

        mSlidingLayout.setIsPagingEnabled(model);
        final ChapterReadAdapter adapter = mChapterReadSlidingAdapter
                .getCurrentChapterReadAdapter();
        adapter.setSelectModel(model);
        adapter.notifyDataSetChanged();
    }

    // ================================================================
    // REGION: 功能跳转（分享、复制、书签、搜索等）
    // 文件位置：第 1180 行 — 第 1745 行
    // ================================================================
    private void goBookmarkEditActivity() {
        final ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
        final List<String> lists = mChapterReadSlidingAdapter.getCurrentChapterReadAdapter()
                .getChecked();
        if (CollectionUtil.isEmpty(lists)) {
            showToastMsg("请至少选中一段文字");
            return;
        }
        final int length = lists.size();
        for (int i = 0; i < length; i++) {
            final Chapter chapter = mChapters.get(mChapterReadSlidingAdapter.getPageIndex());
            final Bookmark bookmark = new Bookmark();
            bookmark.setChapterIndexId(chapter.getIndexId());
            bookmark.setChapterName(chapter.getShowName());
            bookmark.setVolumeId(chapter.getVolumeId());
            bookmark.setVolumeName(mVolumeName);
            bookmark.setChapterCount(chapter.getChapterCount());
            bookmark.setContent(lists.get(i));
            bookmark.setCategroyId(chapter.getCategoryId() + "");
            bookmarks.add(bookmark);
        }
        final Bundle bd = new Bundle();
        bd.putParcelableArrayList(BundleConstants.PARAM_BOOK_MARK_LIST, bookmarks);
        ActivityUtil.next(ATHIS, BookmarkEditActivity.class, bd, -1);
    }

    /**
     * 分享
     */
    private void goShare() {
        final List<String> lists = mChapterReadSlidingAdapter.getCurrentChapterReadAdapter()
                .getChecked();
        if (CollectionUtil.isEmpty(lists)) {
            showToastMsg("请至少选中一段文字");
            return;
        }
        mSelectModel = false;
        StringBuilder stringBuilder = new StringBuilder();
        String bookName = mTitleVolumeTextView.getText().toString();
        stringBuilder.append("《").append(bookName).append("》").append("\n").append(mTitleChapterTextView.getText().toString()).append("\n");
        for (String str : lists) {
            stringBuilder.append(str);
        }
        UmShareUtils.shareText(this, stringBuilder.toString());
    }


    /**
     * 内容复制
     */
    private void goCopy() {
        final List<String> lists = mChapterReadSlidingAdapter.getCurrentChapterReadAdapter()
                .getChecked();
        if (CollectionUtil.isEmpty(lists)) {
            showToastMsg("请至少选中一段文字");
            return;
        }
        final StringBuffer sb3 = new StringBuffer();
        if (mReadOptionsPopupWindow.isSelectedVolume()) {
            String volumeName = mVolumeName.replaceAll("E", "").replaceAll("\\[.*?\\]", "").replaceAll("\\{.*?\\}", "");
            Log.w("TTT", "copyStr1111 mVolumeName:" + mVolumeName);
            Log.w("TTT", "copyStr1111 volumeName:" + volumeName);
            sb3.append("《" + volumeName + "》");
        }
        if (mReadOptionsPopupWindow.isSelectedChapter()) {
            if (sb3.length() > 0) {
                sb3.append("\n");
            }
            sb3.append(mChapter.getShowName());
        }
        if (sb3.length() > 0) {
            sb3.append("\n\t\t");
        } else {
            sb3.append("\t\t");
        }
        for (int i = 0; i < lists.size(); i++) {
            String content = lists.get(i).replace("<b>", "").replace("</b>", "");
            sb3.append(content + "\n\t\t");
        }
        Log.w("TTT", "copyStr1111:" + sb3.toString());
        //过滤〖中文〗，{}，［］，E
        String copyStr = sb3.toString()
//                .replaceFirst("E","")
                .replaceAll("〖.*?〗", "")
//                .replace("\\[.*?\\]", "")
//                .replace("\\{.*?\\}", "")
                ;
        Log.w("TTT", "copyStr222:" + sb3.toString());
        CommonUtil.copy(ATHIS, copyStr);
        showToast("复制成功");
    }

    /**
     * 跳转链接
     */
    private void goJumpLinks() {
        final List<String> lists = mChapterReadSlidingAdapter.getCurrentChapterReadAdapter()
                .getChecked();
        if (CollectionUtil.isEmpty(lists)) {
            showToastMsg("请至少选中一段文字");
            return;
        }
        final StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < lists.size(); i++) {
            stringBuffer.append(lists.get(i) + "\n\t\t");
        }

        String selectContent = stringBuffer.toString();
        Document document = Jsoup.parse(selectContent);
        Elements aTags = document.getElementsByTag("a");
        if (aTags.size() == 1) {
            String url = aTags.get(0).attr("href");
            // String text = aTags.get(0).text();
            SystemUtils.jumpToUrl(ATHIS, url);
        } else {
            ArrayList<SelectDialogShowItem> selectDialogShowItems = new
                    ArrayList<SelectDialogShowItem>();
            int startIndex = 0;
            for (Element element : aTags) {
                String parentString = element.parent().toString();
                int index = parentString.indexOf(element.toString(), startIndex);
                String text = element.text();
                int limitSize = (18 - text.length()) / 2;
                String prefixHtml = Jsoup.parse(parentString.substring(0, index)).text();
                String prefixString = prefixHtml.substring(Math.max(0, prefixHtml.length() -
                        limitSize), prefixHtml.length());
                String suffixHtml = Jsoup.parse(parentString.substring(index + element.toString()
                        .length(), Math.min(parentString.length(), index + element.toString()
                        .length() + limitSize))).text();
                String suffixString = suffixHtml.substring(0, Math.min(suffixHtml.length(),
                        limitSize));
                startIndex = index + element.toString().length();
                String url = element.attr("href");
                String showString = "<small>" + prefixString + "</small><big><b><font " +
                        "color='#3385ff'>" + text + "</font></big></b><small>" + suffixString +
                        "</small>";
                SelectDialogShowItem selectDialogShowItem = new SelectDialogShowItem(showString);
                selectDialogShowItem.setDetail(url);
                selectDialogShowItems.add(selectDialogShowItem);
            }
            final QSelectDialog selectDialog = new QSelectDialog(ATHIS, "请选择跳转的链接",
                    selectDialogShowItems,
                    new QSelectDialog.SelectActionSon() {
                        @Override
                        public void callBack(SelectDialogShowItem select) {
                            SystemUtils.jumpToUrl(ATHIS, select.getDetail());
                        }
                    });
            selectDialog.setNoAddItem(true);
            selectDialog.setFontSize(12);
            selectDialog.setCustomHeight(Math.max(4, selectDialogShowItems.size()));
            selectDialog.show();
        }
    }

    private void changeReadModel() {
        if (HuDongApplication.getInstance().getReadModel() == SystemConfig.READ_MODEL_NORMAL) {
            HuDongApplication.getInstance().setReadModel(SystemConfig.READ_MODEL_NIGHT);
        } else {
            HuDongApplication.getInstance().setReadModel(SystemConfig.READ_MODEL_NORMAL);
        }
        showTabbarBackgroundColor();
        mChapterReadSlidingAdapter.setReadModel(HuDongApplication.getInstance().getReadModel());
        mChapterReadSlidingAdapter.notifyDataSetChanged();
    }

    //朗读 ==========================================================================================================================================================================================
    private void changeSpeech() {
        if (HuDongApplication.getInstance().isAppNormalLevelActivate()) {
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                if (!mSpeechModel) {
                    mainHandler = new Handler() {
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
                if (!mSpeechModel) {
                    mainHandler = new Handler() {
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
        } else {
            CommonUtil.showActivateDialog(ATHIS, UserInfo.VIP_NORMAL);
        }
    }

    private void changeTextSizePopWindow() {
        mSeleteTextSizePopupWindow = new SeleteTextSizePopupWindow(ChapterReaderActivity.this,
                HuDongApplication.getInstance().getTextSize(), ChapterReaderActivity.this);
        mSeleteTextSizePopupWindow.showAtLocation(getWindow().getDecorView(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void searchVolumeContent() {
        final Bundle bundle = new Bundle();
        final Volume volume = new Volume();
        if (mChapter != null)
            volume.setId(mChapter.getVolumeId());
        volume.setVolName(mVolumeName);
        volume.setChpCount(mChapter.getChapterCount());
        bundle.putParcelable(BundleConstants.PARAM_VOLUME, volume);
        ActivityUtil.next(ChapterReaderActivity.this, SearchVolumeActivity.class, bundle, 888);
    }

    private void goBookmarkListActivity(boolean isBack) {
        Bundle bundle = new Bundle();
        bundle.putInt("index", 2);
        bundle.putBoolean("isBack", isBack);
        if (!TextUtils.isEmpty(mTipsKeyword)) {
            bundle.putString("keyWords", mTipsKeyword);
        }
        ActivityUtil.next(ATHIS, UserBookInfoActivity.class, bundle, -1);
    }

    /**
     * 更改背景色
     */
    private void changeBackgroundColor() {
        final int color = PreferenceConfig.getBackgroudColor(ATHIS);
        final ColorPickerDialog dialog = new ColorPickerDialog(ATHIS, color);
        dialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(final int color) {
                HuDongApplication.getInstance().setBackgroudColor(color);
                mChapterReadSlidingAdapter.setBackgroudColor(color);
                mChapterReadSlidingAdapter.notifyDataSetChanged();
                showTabbarBackgroundColor();
            }
        });
        dialog.show();
    }

    private void changeTextModel() {
        Log.w("TTT", "changeTextModel1 1111111111111");
        if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
            CommonUtil.showActivateDialog(ATHIS, UserInfo.VIP_NORMAL);
            return;
        }
        if (HuDongApplication.getInstance().getTextModel() == SystemConfig.TEXT_MODEL_FANTI) {
            HuDongApplication.getInstance().setTextModel(SystemConfig.TEXT_MODEL_NORMAL);
            if (mSettingDelegate != null) mSettingDelegate.updateTraditionalTv(false);
            mChapterReadSlidingAdapter.setTipsKeyword(mTipsKeyword);
            mChapterReadSlidingAdapter.setChapterContent(mChapterContent);
            Log.w("TTT", "changeTextModel1 222222");
        } else {
            HuDongApplication.getInstance().setTextModel(SystemConfig.TEXT_MODEL_FANTI);
            if (mSettingDelegate != null) mSettingDelegate.updateTraditionalTv(true);
            mChapterReadSlidingAdapter.setTipsKeyword(SearchTextUtil.jian2fan(mTipsKeyword));
            mChapterReadSlidingAdapter.setChapterContent(SearchTextUtil.jian2fan(mChapterContent));
            Log.w("TTT", "changeTextModel1 3333333333");
        }
        mChapterReadSlidingAdapter.setTextModel(HuDongApplication.getInstance().getTextModel());
        mChapterReadSlidingAdapter.notifyDataSetChanged();
    }

    private void changeSreenOrientation() {
        final int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void showReadOptionsPopupWindow(final boolean show) {
        List<String> list = mChapterReadSlidingAdapter.getCurrentChapterReadAdapter().getChecked();
        if (list == null) {
            list = new ArrayList<>();
        }
        final StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            stringBuffer.append(list.get(i) + "\n\t\t");
        }
        if (mReadOptionsPopupWindow == null) {
            mReadOptionsPopupWindow = new ReadOptionsPopupWindow(ChapterReaderActivity.this,
                    stringBuffer.toString());
            mReadOptionsPopupWindow.setOnItemClickListener(ChapterReaderActivity.this);
            mReadOptionsPopupWindow.setOnClickListener(this);
            mReadOptionsPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (!mSelectModel) {
                        tvItemTxt.setBackgroundColor(normalColor);
                        StatusBarUtils.initColorStatusBar(ChapterReaderActivity.this, HuDongApplication.getInstance().getBackgroudColor());

                        setChapterModel(false);
                    }
                    mSelectModel = false;
                }
            });
        }
        mReadOptionsPopupWindow.setSelectContent(stringBuffer.toString());
        if (show) {
            mReadOptionsPopupWindow.showMenu(mParentView);
            StatusBarUtils.initColorStatusBar(this, getResources().getColor(R.color.black));
        } else {
            mReadOptionsPopupWindow.hideMenu();
            if (mSelectModel) {
                StatusBarUtils.initColorStatusBar(this, getResources().getColor(R.color.black));
            } else {
                StatusBarUtils.initColorStatusBar(this, HuDongApplication.getInstance().getBackgroudColor());
            }
        }
    }

    /**
     * 长按菜单
     */
    private void showSettingDialog() {
        if (mReadOptionsPopupWindow != null && mReadOptionsPopupWindow.isShowing()) {
            return;
        }
        longClick = true;
//        if (HuDongApplication.getInstance().getReadModel() == SystemConfig.READ_MODEL_NORMAL) {
//            mTitles.set(mTitles.size() - 1, "夜间模式");
//        } else {
//            mTitles.set(mTitles.size() - 1, "普通模式");
//        }
//        if (HuDongApplication.getInstance().getTextModel() == SystemConfig.TEXT_MODEL_FANTI) {
//            mPictures[6] = R.drawable.ic_menu_option_7_2;
//        } else {
//            mPictures[6] = R.drawable.ic_menu_option_7;
//        }
        if (mSettingOptionDialog == null) {
            initMenuDialog();
        }
        mChapterReadMenuGvAdapter.notifyDataSetChanged();
        mSettingOptionDialog.show();
//        if (floatView != null)
//            Log.e("ASDJKHCXJJAD", "onItemClickOfSettingGridView: "+floatView.getVisbility() );
    }

    int normalColor;

    private void showTabbarBackgroundColor() {
        if (HuDongApplication.getInstance().getReadModel() == SystemConfig.READ_MODEL_NORMAL) {
            mTitleLayout.setBackgroundColor(HuDongApplication.getInstance().getBackgroudColor());
            mStatusLayout.setBackgroundColor(HuDongApplication.getInstance().getBackgroudColor());
            dex.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mTitleVolumeTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mTitletvCategoryType.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mTitleChapterTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mStatusWeekTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mStatusTimeTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mStatusBatteryTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT));
            mStatusBatteryView.setColor(0xFF2B2B2B);
            mStatusBatteryView.setFillColor(0XFFFFFFFF);

            normalColor = HuDongApplication.getInstance().getBackgroudColor();
        } else {
            dex.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_NIGHT));
            mTitleLayout.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_NIGHT));
            mStatusLayout.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_NIGHT));
            mTitleVolumeTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_NIGHT));
            mTitletvCategoryType.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_NIGHT));
            mTitleChapterTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_NIGHT));
            mStatusWeekTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_NIGHT));
            mStatusTimeTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_NIGHT));
            mStatusBatteryTextView.setTextColor(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_NIGHT));
            mStatusBatteryView.setColor(0xFFFFFFFF);
            mStatusBatteryView.setFillColor(0XFF2B2B2B);

            normalColor = Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_NIGHT);
        }
        mStatusBatteryView.invalidate();

        tvItemTxt.setBackgroundColor(normalColor);
    }

    /**
     * 朗读菜单
     */
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
        switch (seekBar.getId()) {
            case R.id.line_seek_bar:
                HuDongApplication.getInstance().setmLineMargin(progress);
                if (mSettingDelegate != null) mSettingDelegate.updateLineTv(progress);
                mChapterReadSlidingAdapter.setLineMargin(progress);
                mChapterReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.section_seek_bar:
                HuDongApplication.getInstance().setTextMagin(progress);
                if (mSettingDelegate != null) mSettingDelegate.updateSectionTv(progress);
                mChapterReadSlidingAdapter.setTextMargin(progress);
                mChapterReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.left_right_seek_bar:
                HuDongApplication.getInstance().setTextAround(progress);
                if (mSettingDelegate != null) mSettingDelegate.updateLeftRightTv(progress);
                mChapterReadSlidingAdapter.setTextAroundMargin(progress);
                mChapterReadSlidingAdapter.notifyDataSetChanged();
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
        if ((floatView != null) && mSpeechModel) {
            floatView.show();
        }
        // 注册电量广播
        registerReceiver(mBatteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        isBack = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销电量广播
        unregisterReceiver(mBatteryChangedReceiver);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fl_view.onActivityDestroy();
        mDatetimeUpdateHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        if (mXunFeiSpeechManager != null) {
            mXunFeiSpeechManager.stopSpeaking();
            mXunFeiSpeechManager.destroy();
        }
        if (baiduSpeechManager != null) {
            baiduSpeechManager.stop();
            baiduSpeechManager.destory();
        }
        if (notificationHelper != null) notificationHelper.cancelNotification();

        if (floatView != null) {
            floatView.hide();
            floatView = null;
        }
        if (playBroadcastReceiver != null) {
            unregisterReceiver(playBroadcastReceiver);
        }
        stopService(new Intent(ChapterReaderActivity.this, NotifacationService.class));

    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mChapter = data.getParcelableExtra(BundleConstants.PARAM_CHAPTER);
            mTipsPostion = data.getIntExtra(BundleConstants.PARAM_TIPS_POSTION, 0);
            mTipsKeyword = data.getStringExtra(BundleConstants.PARAM_TIPS_KEYWORD);
            mTipsContent = data.getStringExtra(BundleConstants.PARAM_TIPS_CONTENT);

            if (mChapter != null) {
                mChapters = new ChapterDatabaseHepler(this).getChapterList(mChapter.getVolumeId());
            }
            Iterator<Chapter> iterator = mChapters.iterator();
            while (iterator.hasNext()) {
                Chapter c = iterator.next();
                if (c.getName() != null) {
                    if (c.getName().contains("jieshao")) {
                        iterator.remove();
                    }
                }
            }
            mChapterReadSlidingAdapter.setChapters(mChapters);
            mChapterReadSlidingAdapter.setPageIndex(mChapter.getIndexId() - 1);
            mChapterReadSlidingAdapter.setTipsPostion(mTipsPostion - 1);
            mChapterReadSlidingAdapter.setTipsKeyword(mTipsKeyword);
            mChapterReadSlidingAdapter.setSearchType(mSearchType);
            mChapterReadSlidingAdapter.setChapterName(mChapterName);
            mChapterReadSlidingAdapter.setChapterContent(mChapterContent);
            mChapterReadSlidingAdapter.setChapterNameKeyWord(mChapterNameKeyWord);
            mChapterReadSlidingAdapter.setTipContent(mTipsContent);
            mChapterReadSlidingAdapter.mTipsValidate = true;
            mChapterReadSlidingAdapter.notifyDataSetChanged();
        }
    }

    ////////////////////////////////////////////////////////
    XunFeiSpeechManager mXunFeiSpeechManager;

    // ================================================================
    // REGION: 语音朗读（讯飞 TTS / 百度 TTS / 朗读进度）
    // 文件位置：第 1745 行 — 第 2260 行
    // 关联文件：ChapterReaderSpeechHelper.java（已抽取核心逻辑）
    // ================================================================
    final HashMap<Integer, List<String>> mSpeechTextMap = new HashMap<Integer, List<String>>();

    // 缓冲进度
    int mPercentForBuffering = 0;

    // 播放进度
    int mPercentForPlaying = 0;

    int mSpeechPosition = 0;

    int mSpeechIndex = 0;

    int mSpeechTxtNums;

    int mSpeechTxtNumIndex;

    private boolean longClick;

    private void initSpeechTts() {
        speechDelegate.initSpeechTts();
    }

    // mTtsInitListener 已移至 ChapterReaderSpeechDelegate

    //开始朗读 =======================================================================================================================================
    private void startSpeech() {
        speechDelegate.startSpeech();
    }

    String getSpeechContent(final int index, final int position) {
        // 委托给 ChapterReaderSpeechDelegate 处理
        try {
            if (mSpeechTextMap == null || mSpeechTextMap.isEmpty()) return "";
            List<String> textList = mSpeechTextMap.get(index);
            if (textList == null || position < 0 || position >= textList.size()) return "";
            String txt = textList.get(position);
            return txt != null ? txt : "";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void refreshChapterRemark(final boolean speechModel, final String remarkText) {
        ChapterReadAdapter adapter = mChapterReadSlidingAdapter.getCurrentChapterReadAdapter();
        adapter.setSpeechModel(speechModel);
        adapter.setRemarkText(remarkText);
        adapter.setSeechIndex(mSpeechIndex);
        adapter.notifyDataSetChanged();
    }

    /**
     * 合成回调监听。
     */
    // mTtsListener 已移至 ChapterReaderSpeechDelegate，通过 speechDelegate.mTtsListener 访问

    private void speakerNext() {
        speechDelegate.speakerNext();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ATHIS);  //先得到构造器
            builder.setTitle("退出朗读提示");
            builder.setCancelable(false);
            builder.setMessage("是否退出朗读？"); //设置内容

            builder.setNegativeButton("否", new DialogInterface.OnClickListener() { //设置确定按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() { //设置确定按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopSpeechAndNotification();
                    if (floatView != null) floatView.hide();
                    refreshChapterRemark(false, "");
                    showToast("恭喜！成功退出");
                }
            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();


        } else {
            if (!TextUtils.isEmpty(enter)) {
                if ("mark".equals(enter)) {
                    //书签跳转，跳回书签
                    enter = "";
                    goBookmarkListActivity(true);
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    //通知章节刷新
                    EventBus.getDefault().post(new RefreshChapterListEvent(mChapter.getVolumeId()));
                    finish();
                    return;
                }
                Volume volume = new Volume();
                if (mChapter != null)
                    volume.setChpCount(mChapter.getChapterCount());
                volume.setVolName(mVolumeName);
                volume.setId(mVolumeId);
                Bundle bd = new Bundle();
                bd.putParcelable(BundleConstants.PARAM_VOLUME, volume);
                ActivityUtil.next(ATHIS, ChaptersListActivity.class, bd, -1);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            } else {
                //通知章节刷新
                EventBus.getDefault().post(new RefreshChapterListEvent(mVolumeId));
            }
            super.onBackPressed();
        }

    }

    @Override
    public void finish() {
        try {
            stopSpeechAndNotification();
            if (floatView != null) floatView.hide();
        } catch (Exception e) {
            LogUtil.error("floatView.removeView", e);
        }


        if (!TextUtils.isEmpty(enter)) {
            if ("mark".equals(enter)) {
                //书签跳转，跳回书签
                enter = "";
                goBookmarkListActivity(true);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                //通知章节刷新
                EventBus.getDefault().post(new RefreshChapterListEvent(mChapter.getVolumeId()));
                finish();
                return;
            }
        }

        saveHistoryInfo();
        super.finish();
    }

    /**
     * 保存历史信息
     */
    void saveHistoryInfo() {
        if (mChapter == null) {
            return;
        }
        HistoryBean historyBean = new HistoryBean();
        if (!TextUtils.isEmpty(mChapter.getName()))
            historyBean.setChapter(mChapter.getName());
        historyBean.setCategoryId(mChapter.getCategoryId());
        historyBean.setChapterCount(mChapter.getChapterCount());
        historyBean.setContent(mChapter.getContent());
        historyBean.setIndexId(mChapter.getIndexId());
        historyBean.setParentId(mChapter.getParentId());
        historyBean.setVolumeId(mChapter.getVolumeId());
        historyBean.setVolumeName(mVolumeName);
        if (StringUtil.isEmpty(mVolumeName)) {
            return;
        }
        if (!DatabaseManager.getHistoryHelper().isExist(mChapter.getVolumeId())) {//不存在才添加，否则更新
            DatabaseManager.getHistoryHelper().addHistory(historyBean);
        } else {
            DatabaseManager.getHistoryHelper().updateHistory(historyBean);
        }
    }

    // ================================================================
    // REGION: onClick 主入口（所有按鈕点击分发）
    // 文件位置：第 2400 行 — 第 2550 行
    // ================================================================
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_back:
            case R.id.tv_volume:
                onBackPressed();
                break;
            case R.id.btn_cancle:
                // 取消编辑模式
                mSelectModel = false;
                mOptionLayout.setVisibility(View.GONE);
                StatusBarUtils.initColorStatusBar(this, HuDongApplication.getInstance().getBackgroudColor());
                setChapterModel(false);
                showReadOptionsPopupWindow(false);
                break;
            case R.id.btn_selectall:
                // 选中所有
                final ChapterReadAdapter adapter = mChapterReadSlidingAdapter
                        .getCurrentChapterReadAdapter();
                adapter.checkAll();
                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_selectnone:
                // 反选
                final ChapterReadAdapter adapter2 = mChapterReadSlidingAdapter
                        .getCurrentChapterReadAdapter();
                adapter2.uncheck();
                adapter2.notifyDataSetChanged();
                break;
            case R.id.btn_complete:
                // 选中完成
                mSelectModel = false;
                mOptionLayout.setVisibility(View.GONE);
                StatusBarUtils.initColorStatusBar(this, HuDongApplication.getInstance().getBackgroudColor());
                showReadOptionsPopupWindow(true);
                break;
            case R.id.btn_cancel_model:
                mSelectModel = false;
                showReadOptionsPopupWindow(false);
                break;
            case R.id.btn_exit:
                // 停止朗读
                stopSpeechAndNotification();
                refreshChapterRemark(false, null);
                mSlidingLayout.setIsPagingEnabled(false);
                if (mSpeechPopupWindow != null) mSpeechPopupWindow.dismiss(false);
                if (floatView != null) floatView.hide();
                break;
            case R.id.btn_previous_chapter:
                mSlidingLayout.slidePrevious();
                break;
            case R.id.btn_next_chapter:
                mSlidingLayout.slideNext();
                break;
            //阅读设置
            case R.id.tv_traditional:
                changeTextModel();
                break;
            case R.id.iv_2_hor:
                if (mSettingDelegate != null) mSettingDelegate.setBackground(R.id.iv_2_hor);
                HuDongApplication.getInstance().setTextMagin(40);
                HuDongApplication.getInstance().setTextAround(20);
                mChapterReadSlidingAdapter.setTextMargin(40);
                mChapterReadSlidingAdapter.setTextAroundMargin(20);
                mChapterReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_3_hor:
                if (mSettingDelegate != null) mSettingDelegate.setBackground(R.id.iv_3_hor);
                HuDongApplication.getInstance().setTextMagin(30);
                HuDongApplication.getInstance().setTextAround(20);
                mChapterReadSlidingAdapter.setTextMargin(30);
                mChapterReadSlidingAdapter.setTextAroundMargin(20);
                mChapterReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_4_hor:
                if (mSettingDelegate != null) mSettingDelegate.setBackground(R.id.iv_4_hor);
                HuDongApplication.getInstance().setTextMagin(20);
                HuDongApplication.getInstance().setTextAround(20);
                mChapterReadSlidingAdapter.setTextMargin(20);
                mChapterReadSlidingAdapter.setTextAroundMargin(20);
                mChapterReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_full_4_hor:
                if (mSettingDelegate != null) mSettingDelegate.setBackground(R.id.iv_full_4_hor);
                HuDongApplication.getInstance().setTextMagin(20);
                HuDongApplication.getInstance().setTextAround(0);
                mChapterReadSlidingAdapter.setTextMargin(20);
                mChapterReadSlidingAdapter.setTextAroundMargin(0);
                mChapterReadSlidingAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_3_ver:
                if (mSettingDelegate != null) mSettingDelegate.setBackground(R.id.iv_3_ver);
                break;
            case R.id.iv_more_margin:
                if (mSettingDelegate != null) mSettingDelegate.setBackground(R.id.iv_more_margin);
                if (mSettingDelegate != null) mSettingDelegate.showMarginPop(mParentView);
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
            case R.id.return_default:
                HuDongApplication.getInstance().setmLineMargin(7);
                HuDongApplication.getInstance().setTextMagin(7);
                HuDongApplication.getInstance().setTextAround(22);
                if (mSettingDelegate != null) mSettingDelegate.resetMarginProgress(7, 7, 22);
                mChapterReadSlidingAdapter.notifyDataSetChanged();
                break;
        }
    }

    // 阅读设置模块已迁移到 ChapterReaderSettingDelegate.java
    // 包含：字体大小、背景颜色、文字颜色、边距设置（共 783 行 → 已精简）
    ChapterReaderSettingDelegate mSettingDelegate;
    ChapterReaderSpeechDelegate speechDelegate; // 语音朗读模块
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
        if (textSize >= 50) return;
        textSize++;
        if (mSettingDelegate != null) mSettingDelegate.updateTextSizeTv(textSize);
        HuDongApplication.getInstance().setTextSize(textSize);
        mChapterReadSlidingAdapter.setTextSize(textSize);
        mChapterReadSlidingAdapter.notifyDataSetChanged();
    }

    private void reduceValue() {
        if (textSize <= 10) return;
        textSize--;
        if (mSettingDelegate != null) mSettingDelegate.updateTextSizeTv(textSize);
        HuDongApplication.getInstance().setTextSize(textSize);
        mChapterReadSlidingAdapter.setTextSize(textSize);
        mChapterReadSlidingAdapter.notifyDataSetChanged();
    }

    // ================================================================
    // REGION: 通知栏 — 播放事件处理
    // 文件位置：第 3345 行 — 第 3490 行
    // ================================================================
    public void initNotificationBar() {
        //过滤器
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("play");
        mIntentFilter.addAction("end");
        //创建广播接收者的对象
        playBroadcastReceiver = new PlayBroadcastReceiver();
        //注册广播接收者的对象
        registerReceiver(playBroadcastReceiver, mIntentFilter);

        contentView = new RemoteViews(getPackageName(), R.layout.notification_control);
        //设置标题名
        if (MTextUtil.isContainChinese(title) && title.contains("E")) {
            title = MTextUtil.changeEletter(title).trim();
        }
        contentView.setTextViewText(R.id.tv_book_name, title);
        contentView.setTextViewText(R.id.tv_chapter_name, mChapter.getShowName());

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
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//8.0以下 && 7.0及以上 设置优先级
                notificationCompat.setPriority(NotificationManager.IMPORTANCE_HIGH);
            } else {
                notificationCompat.setPriority(NotificationCompat.PRIORITY_HIGH);
            }

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
            if (notificationHelper != null) notificationHelper.cancelNotification();
            if (floatView != null) floatView.hide();
            refreshChapterRemark(true, "");
            showToast("已退出朗读模式");
            return;
        }
        if (playEvent.getType() == 1) {
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
//                mXunFeiSpeechManager.setEngineType("0");
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
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
//                mXunFeiSpeechManager.setEngineType("1");
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
        } else {
            mNotificationManager.notify(SystemConstants.Notification_ID_BASE, notificationCompat.build());
        }
    }

    // ================================================================
    // REGION: 滚动背景更新 / 章节名显示 / 習第翻章
    // 文件位置：第 3490 行 — 第 3680 行
    // ================================================================
    void UpdateReadBackground() {
        mSlidingLayout.slideNext();
        mChapterReadSlidingAdapter.setPageIndex(mChapterReadSlidingAdapter.getPageIndex() + 1);
        if (mChapter != null) {
            PreferenceConfig.saveReadingProgress(getApplicationContext(), mChapter.getVolumeId(), mChapterReadSlidingAdapter.getPageIndex());
            saveHistoryInfo();
        }
        if (mChapters.size() <= mChapterReadSlidingAdapter.getPageIndex()) {
            //最后一章
            showToast("阅读完毕");
            //退出朗读
            stopSpeechAndNotification();
            if (floatView != null) floatView.hide();
            return;
        }
        mChapter = mChapters.get(mChapterReadSlidingAdapter.getPageIndex());
        if (mChapter == null) {
            return;
        }
        setChapterNameText(mChapter.getShowName());
        //TODO 通知栏更新阅读状态
        if (contentView != null) {
            //设置标题名
            contentView.setTextViewText(R.id.tv_book_name, title);
            contentView.setTextViewText(R.id.tv_chapter_name, mChapter.getShowName());
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                HuDongApplication.getInstance().notManager.notify(SystemConstants.Notification_ID_BASE, notification);//开启通知
            } else {
                mNotificationManager.notify(SystemConstants.Notification_ID_BASE, notificationCompat.build());
            }
        }
        if (mSpeechModel) {
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                mXunFeiSpeechManager.stopSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                baiduSpeechManager.stop();
            }
            mSpeechIndex = 0;
            mSpeechPosition = 0;
//            startSpeech();
            startNextSpeech(mChapter);
        }
        final ListView listView = mChapterReadSlidingAdapter.getCurrentListView();
        currentCount = listView.getCount();
        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, final int scrollState) {
                listScrollLister();
            }

            @Override
            public void onScroll(final AbsListView view, final int firstVisibleItem,
                                 final int visibleItemCount, final int totalItemCount) {
                listScrollLister();
                //listView已滚动到最底部
                if (CommonUtil.isListViewReachBottomEdge(listView)) {
                    mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm") + "" + " " + "已阅100%");
                } else {
                    mStatusTimeTextView.setText(DateUtil.getStringDate("HH:mm") + "" + getReadProgress());
                }
            }
        });
    }

    private void setChapterNameText(String showName) {
        if (!TextUtils.isEmpty(mChapterName) && !TextUtils.isEmpty(mChapterNameKeyWord) && mChapterName.equals(showName)) {
            SpannableStringBuilder span = new SpannableStringBuilder(mChapter.getShowName());
            String[] keyWords = mChapterNameKeyWord.split(" ");
            for (String keyWord : keyWords) {
                Pattern P1 = Pattern.compile(keyWord);
                Matcher matcherKeyWord = P1.matcher(span);
                while (matcherKeyWord.find()) {
                    span.setSpan(new ForegroundColorSpan(Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_KEY_WORD)), matcherKeyWord.start(), matcherKeyWord.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            mTitleChapterTextView.setText(span);
        } else {
            mTitleChapterTextView.setText(showName);
        }
    }


    private void startNextSpeech(Chapter chapter) {
        speechDelegate.startNextSpeech(chapter);
    }


    private void initBaiduSpeech() {
        speechDelegate.initBaiduSpeech();
    }


    // ================================================================
    // REGION: Handler 处理 / 网络状态 / EventBus 事件 / 其他
    // 文件位置：第 3680 行 — 文件末尾
    // ================================================================
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
                    showToast("首次初始化需要连接网络");
                    mXunFeiSpeechManager = null;
                    baiduSpeechManager.release();
                    baiduSpeechManager = null;
                    if (floatView != null) floatView.hide();
                    if (notificationHelper != null) notificationHelper.cancelNotification();
                }
                msg.what = PRINT;
                break;
            case UI_CHANGE_SYNTHES_TEXT_SELECTION://msg.arg1表示当前字体个数
                break;
            case UI_ERROR_TEXT_SPEECH://msg.arg1表示当前字体个数
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
                    Log.e("BAIDUSPEECHENGERTEST", "UI_ERROR_TEXT_SPEECH:" + "重载成功");
                }
                break;
            case UI_FINISH_TEXT_SELECTION://每句播放结束回调
                speakerNext();
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

    public interface MoveDistanceListener {
        void getMoveDistance(float dis);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(LoginOutEvent loginOutEvent) {
        //登录失效
        this.finish();
    }

    @Override
    public float smoothNext(MoveDistanceListener ls) {
        float slH = mSlidingLayout.getMeasuredHeight();
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
        diffY = slH;
        return diffY;
    }

    @Override
    public float smoothLast(MoveDistanceListener ls) {
        float slH = mSlidingLayout.getMeasuredHeight();

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
        diffY = -slH;
        floatView.setNewYPosition(diffY);
        return diffY;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(String tag) {
        switch (tag) {
            case "audio_chapter_no_cache":
                if (fl_view != null) {
                    fl_view.stopAnimation();
                }
                break;
            case "on_audio_stop":
                if (fl_view != null) {
                    fl_view.hide();
                }
                break;
        }
    }

}