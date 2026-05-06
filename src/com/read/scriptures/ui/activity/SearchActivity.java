package com.read.scriptures.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.UserInfo;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.CategoryDatabaseHelper;
import com.read.scriptures.db.ChapterDatabaseHepler;
import com.read.scriptures.db.VolumeDatabaseHepler;
import com.read.scriptures.event.LoginOutEvent;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.model.Volume;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.CategoryOneGridAdapter;
import com.read.scriptures.ui.adapter.CategoryTwoGridAdapter;
import com.read.scriptures.ui.adapter.SearchBookListAdapter;
import com.read.scriptures.ui.adapter.SearchCategoryAdapter;
import com.read.scriptures.ui.adapter.SearchHistoryAdapter;
import com.read.scriptures.ui.adapter.VolumeGridAdapter;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.GsonUtils;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.MTextUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.ThreadUtil;
import com.read.scriptures.util.UmShareUtils;
import com.read.scriptures.widget.XListView;
import com.zxl.common.db.sqlite.DbException;
import com.zxl.common.db.sqlite.DbUtils;
import com.zxl.common.db.sqlite.Selector;
import com.zxl.common.db.sqlite.WhereBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@SuppressLint("ClickableViewAccessibility")
public class SearchActivity extends BaseActivity implements OnClickListener {

    private ProgressBar mSearchProgress;
    private EditText mSearchEditText;
    private TextView mSearchTextView;
    private TextView mSearchRemain;
    private RadioGroup mRradioGroup;
    private LinearLayout mSearchResultLayout;
    private TextView mResultTextView;
    private XListView mResultListView;
    private TextView searchTotalCountText;
    private ListView searchHistory;
    private TextView switchShowContent;
    private LinearLayout ll_content;

    private VolumeDatabaseHepler mVolumeHepler;
    private ChapterDatabaseHepler mChapterHepler;

    private List<String> mSearchHistoryKeyword;
    private List<String> mContainsHistoryKeyword;
    private List<Category> mRootCategorys;
    private Map<Category, List<Category>> mRootCategoryMaps;
    private List<Bookmark> mSearchBookmarkList;
    private SearchBookListAdapter mSearchBookmarkListAdapter;
    private Category mSearchRoot;
    private Category mSearchNode;
    private Volume mVolume;
    private String mKeyword;
    private int mSearchType = 4;// 搜索类别 1：书籍 2：目录 3：标题 4：内容
    private int mSearchRange = 1;
    private int mCurrenType = 3;
    private int mListViewIndex;
    private boolean mInitShowOptionPopupWindow = true;
    private int searchWidth = 150;
    private boolean isSearch;

    private int oncheck = 0;
    private LinearLayout mContentLayout;

    private SearchHistoryAdapter searchHistoryAdapter;

    private SearchCategoryAdapter mSearchCategoryAdapter;
    private CategoryTwoGridAdapter mCategoryTwoGridAdapter;

    private TextView search;
    private GridView mCategoryTwoGridView;
    private ListView mCategorySectionListView;

    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;

    private View touchView;

    private int mIndexCategory = 0;
    private FrameLayout mFlSearchProgress;
    boolean checkVipPermission = false;//默认VIP权限
    private int offset = -SearchTextUtil.searchLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        checkVipPermission();
        EventBus.getDefault().register(this);
        StatusBarUtils.initMainColorStatusBar(this);
        initExtras();
        initData();
        initActionBar();
        initViews();
        showSearchText();
        if (!TextUtils.isEmpty(mKeyword)) {
            mContentLayout.setVisibility(View.GONE);
            clickSearch(true);
        } else {
            if (searchHistory.getVisibility() == View.VISIBLE) {
                searchHistory.setVisibility(View.GONE);
                return;
            }
            mSearchRoot = mRootCategorys.get(mIndexCategory);
            showSearchText();
            clickShowOptionPopupWindow();
        }

        mSearchEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                showInput(mSearchEditText);
            }
        }, 500);
    }

    private void checkVipPermission() {
        if (AccountManager.getInstance().isLogin()) {
            if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                checkVipPermission = false;
            } else {
                checkVipPermission = true;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private void initExtras() {
        mSearchType = getIntent().getIntExtra("type", 4);
        mSearchRange = getIntent().getIntExtra("range", 1);
        mKeyword = getIntent().getStringExtra("keyword");
        mSearchNode = getIntent().getParcelableExtra("searchNode");
    }

    @SuppressWarnings("unchecked")
    private void initData() {
        // 初始化搜索数据
        CategoryDatabaseHelper categoryHepler = new CategoryDatabaseHelper(this);
        mRootCategorys = new ArrayList<Category>();
        mRootCategorys.addAll(categoryHepler.getCategroyList(0));
        mRootCategoryMaps = new HashMap<Category, List<Category>>();
        for (int i = 0; i < mRootCategorys.size(); i++) {
            List<Category> categorys = categoryHepler.getCategroyList(mRootCategorys.get(i).getId());
            mRootCategoryMaps.put(mRootCategorys.get(i), categorys);
        }
        mSearchRoot = mRootCategorys.get(mSearchRange - 1);
        mIndexCategory = mSearchRange - 1;
        mVolumeHepler = new VolumeDatabaseHepler(this);
        mChapterHepler = new ChapterDatabaseHepler(this);

        // 初始化搜索历史关键字数据
        List<String> keyword = null;
        try {
            keyword = (List<String>) PreferencesUtils.getObject(SearchActivity.this, PreferenceConfig.Preference_Keyword);
        } catch (IOException e) {
            LogUtil.error("Exception", e);
        } catch (ClassNotFoundException e) {
            LogUtil.error("Exception", e);
        }
        mSearchHistoryKeyword = new ArrayList<String>();
        if (keyword != null) {
            mSearchHistoryKeyword.addAll(keyword);
        }
    }

    private void initActionBar() {
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ll_content.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (searchHistory.getVisibility() == View.VISIBLE) {
                    searchHistory.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
        searchHistory = (ListView) findViewById(R.id.listview_history);
        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchBookmarkList = null;
                hideInput();
                finish();
            }
        });
        search = (TextView) findViewById(R.id.btn_search);
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentLayout.setVisibility(View.GONE);
                search.setClickable(false);
                clickSearch(true);
            }
        });
        // 搜索选项
        mSearchEditText = (EditText) findViewById(R.id.et_search);
        mSearchEditText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isShowHistory = SharedUtil.getBoolean(PreferenceConfig.Preference_history_search_visible, true);
                if (!isShowHistory) {
                    return false;
                }
                searchWidth = mSearchEditText.getWidth();
                if (searchHistoryAdapter != null) {
                    if (TextUtils.isEmpty(mSearchEditText.getText().toString().trim())) {//为空则显示所有的
                        searchHistoryAdapter.setList(mSearchHistoryKeyword);
                        searchHistoryAdapter.notifyDataSetChanged();
                    } else {
                        changeSearch(mSearchEditText.getText().toString().trim());
                    }
                }
                ViewGroup.LayoutParams layoutParams = searchHistory.getLayoutParams();
                layoutParams.width = searchWidth;
                layoutParams.height = WRAP_CONTENT;
                searchHistory.setLayoutParams(layoutParams);
                searchHistory.setVisibility(View.VISIBLE);
                return false;
            }
        });
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    clickSearch(true);
                    return true;
                }
                return false;
            }
        });
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchHistoryAdapter != null) {
                    if (TextUtils.isEmpty(editable.toString())) {//为空则显示所有的
                        searchHistoryAdapter.setList(mSearchHistoryKeyword);
                        searchHistoryAdapter.notifyDataSetChanged();
                        search.setEnabled(false);
                    } else {
                        search.setEnabled(true);
                        changeSearch(editable.toString());
                    }
                }
            }
        });
        mSearchEditText.setText(mKeyword);
    }

    private void changeSearch(String keyword) {
        mContainsHistoryKeyword = new ArrayList<>();
        for (String s : mSearchHistoryKeyword) {
            if (s.contains(keyword)) {
                mContainsHistoryKeyword.add(s);
            }
        }
        if (mContainsHistoryKeyword.size() != 0) {
            searchHistoryAdapter.setList(mContainsHistoryKeyword);
            searchHistoryAdapter.notifyDataSetChanged();
        } else {
            searchHistory.setVisibility(View.GONE);
        }

    }

    private void initViews() {
        touchView = findViewById(R.id.touch_view);
        mSearchProgress = (ProgressBar) findViewById(R.id.search_progress);
        mFlSearchProgress = findViewById(R.id.fl_progress);
        mSearchProgress.setVisibility(View.GONE);
        mFlSearchProgress.setVisibility(View.GONE);
        mSearchTextView = (TextView) findViewById(R.id.tv_search);
        mSearchRemain = (TextView) findViewById(R.id.tv_remain);
        mRradioGroup = (RadioGroup) findViewById(R.id.radio_group);

        touchView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchEditText.clearFocus();
                hideInput();
                return false;
            }
        });

        RadioButton rb_4 = (RadioButton) findViewById(R.id.rb_4);
        switch (mSearchType) {
            case 4:
                rb_4.setChecked(true);
                if (oncheck == 0)
                    oncheck++;
                break;
        }
        mRradioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                oncheck++;
                if (searchHistory.getVisibility() == View.VISIBLE) {
                    searchHistory.setVisibility(View.GONE);
                    return;
                }
                if (oncheck <= 1) {
                    return;
                }
                mSearchRoot = mRootCategorys.get(mIndexCategory);
                showSearchText();
                if (checkedId == R.id.rb_1) {
                    clickShowOptionPopupWindow();
                    mSearchType = 1;
                } else if (checkedId == R.id.rb_2) {
                    clickShowOptionPopupWindow();
                    mSearchType = 2;
                } else if (checkedId == R.id.rb_3) {
                    mSearchType = 3;
                    clickShowOptionPopupWindow();
                } else if (checkedId == R.id.rb_4) {
                    mSearchType = 4;
                    clickShowOptionPopupWindow();
                }
            }
        });
        radioButton1 = (RadioButton) findViewById(R.id.rb_1);
        radioButton2 = (RadioButton) findViewById(R.id.rb_2);
        radioButton3 = (RadioButton) findViewById(R.id.rb_3);
        radioButton4 = (RadioButton) findViewById(R.id.rb_4);
        switch (mSearchType) {
            case 1:
                radioButton1.setChecked(true);
                break;
            case 2:
                radioButton2.setChecked(true);
                break;
            case 3:
                radioButton3.setChecked(true);
                break;
            case 4:
                radioButton4.setChecked(true);
                break;
        }
        radioButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShowOptionPopupWindow();
            }
        });
        radioButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShowOptionPopupWindow();
            }
        });
        radioButton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShowOptionPopupWindow();
            }
        });
        radioButton4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShowOptionPopupWindow();
            }
        });
        switchShowContent = (TextView) findViewById(R.id.switch_show_content);
        switchShowContent.setVisibility(View.GONE);
        switchShowContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearch) {
                    if (mContentLayout.getVisibility() == View.GONE) {
                        mContentLayout.setVisibility(View.VISIBLE);
                    } else {
                        mContentLayout.setVisibility(View.GONE);
                    }
                    return;
                }
                mContentLayout.setVisibility(View.GONE);

                if (isSearch && mSearchResultLayout != null && mSearchResultLayout.getVisibility() == View.GONE) {
                    mSearchResultLayout.setVisibility(View.VISIBLE);
                    searchHistory.setVisibility(View.GONE);
                } else if (isSearch && mSearchResultLayout != null) {
                    mSearchResultLayout.setVisibility(View.GONE);
                    searchHistory.setVisibility(View.VISIBLE);
                }
            }
        });
        mSearchRemain.setOnClickListener(this);

        searchHistoryAdapter = new SearchHistoryAdapter(this, mSearchHistoryKeyword);
        searchHistory.setAdapter(searchHistoryAdapter);
        searchHistoryAdapter.setOnItemClickListener(new SearchHistoryAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, String word) {
                mSearchEditText.setText(word);
                mSearchEditText.setSelection(word.length());
                mSearchEditText.requestFocus();
                searchHistory.setVisibility(View.GONE);
            }
        });
        searchHistoryAdapter.setOnIVClickListener(new SearchHistoryAdapter.OnIVClickListener() {
            @Override
            public void onIVClicker(int position, String value) {
                try {
                    Iterator<String> it = mSearchHistoryKeyword.iterator();
                    while (it.hasNext()) {
                        String s = it.next();
                        if (s.equals(value)) {
                            it.remove();
                        }
                    }
                    PreferencesUtils.putObject(SearchActivity.this, PreferenceConfig.Preference_Keyword, mSearchHistoryKeyword);
                    searchHistoryAdapter.setList(mSearchHistoryKeyword);
                    searchHistoryAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // 搜索结果
        mSearchResultLayout = (LinearLayout) findViewById(R.id.layout_result);
        mResultTextView = (TextView) findViewById(R.id.tv_result);
        mResultTextView.setVisibility(View.GONE);
        mSearchBookmarkListAdapter = new SearchBookListAdapter(this, mRootCategoryMaps);
        mResultListView = (XListView) findViewById(R.id.listview_result);
        mResultListView.setAdapter(mSearchBookmarkListAdapter);
        searchTotalCountText = findViewById(R.id.searchTotalCountText);
        mResultListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrenType != 4) {
                    return true;
                }
                mListViewIndex = position - 1;
                return false;
            }
        });
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (searchHistory.getVisibility() == View.VISIBLE) {
                    searchHistory.setVisibility(View.GONE);
                    return;
                }
                clickListItem(position - 1);
            }
        });
        mResultListView.setAutoLoadMoreEnable(false);
        mResultListView.setPullRefreshEnable(false);
        mResultListView.setPullLoadEnable(true);
        mResultListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                clickSearch(false);
            }
        });

        registerForContextMenu(mResultListView);
        mContentLayout = (LinearLayout) findViewById(R.id.content_layout);
        initContentViews();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        hideInput();
        super.onDestroy();
        mSearchBookmarkList = null;
    }

    /**
     * 屏幕焦点事件，当activity加载完成后，显示popupwindow
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 界面创建完成，activity获取焦点之后
        if (mInitShowOptionPopupWindow) {
            mInitShowOptionPopupWindow = false;
            if (mKeyword == null) {
                clickShowOptionPopupWindow();
            }
        }
    }

    private void clickShowOptionPopupWindow() {
        if (mContentLayout != null) {
            mContentLayout.setVisibility(View.VISIBLE);
            searchTotalCountText.setVisibility(View.GONE);
            mSearchResultLayout.setVisibility(View.GONE);
        }
    }

    private void clickListItem(int position) {
        if (mSearchType == 1) {
            Volume volume = new Volume();
            volume.setId(mSearchBookmarkList.get(position).getVolumeId());
            volume.setVolName(mSearchBookmarkList.get(position).getVolumeName());
            volume.setChpCount(mSearchBookmarkList.get(position).getChapterCount());
            Bundle bd = new Bundle();
            bd.putParcelable(BundleConstants.PARAM_VOLUME, volume);
            bd.putString(BundleConstants.PARAM_CATEGORY, categoryName);
            ActivityUtil.next(ATHIS, ChaptersListActivity.class, bd, -1);
        } else {
            Bookmark bookmark = mSearchBookmarkList.get(position);
            Chapter chapter = new Chapter();
            chapter.setIndexId(bookmark.getChapterIndexId());
            chapter.setName(bookmark.getChapterName());
            chapter.setVolumeName(bookmark.getVolumeName());
            chapter.setChapterCount(bookmark.getChapterCount());
            chapter.setVolumeId(bookmark.getVolumeId());
            List<Chapter> list = new ChapterDatabaseHepler(this).getChapterList(bookmark.getVolumeId());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equals(chapter.getName())) {
                    chapter.setChapterIndex(list.get(i).getChapterIndex());
                    break;
                }
            }
            Bundle bd = new Bundle();
            bd.putParcelable(BundleConstants.PARAM_CHAPTER, chapter);
            if (mSearchType == 2 || mSearchType == 4) {
                if (mSearchType == 2 && mSearchRoot != null && mSearchRoot.getCateName().equals("圣经")) {
                    //搜索圣经标题需要对位置position做处理 防止进入阅读页面定位不准
                    int index = bookmark.getIndex() / 10 * HuDongApplication.mVersions.size() + bookmark.getIndex() % HuDongApplication.mVersions.size();
                    bd.putInt(BundleConstants.PARAM_TIPS_POSTION, index);
                } else {
                    bd.putInt(BundleConstants.PARAM_TIPS_POSTION, bookmark.getIndex());
                }
                bd.putString(BundleConstants.PARAM_TIPS_KEYWORD, mKeyword);
                if (!TextUtils.isEmpty(bookmark.getContent())) {
                    bd.putString(BundleConstants.PARAM_TIPS_CONTENT, bookmark.getReplaceContent());
                }
                String showName = chapter.getName() != null ? chapter.getName().replaceAll("^\\d{1,}-", "") :chapter.getName();
                LinkedHashMap<String, String> annMap = new ChapterDatabaseHepler(SearchActivity.this)
                        .getChapterAnnotationList(chapter.getVolumeId(), showName);
                bd.putString(BundleConstants.ANN_MAP, GsonUtils.objectToStr(annMap));
            }
            if (mSearchType == 3) {
                bd.putString(BundleConstants.PARAM_CHAPTER_NAME, bookmark.getChapterName());
                bd.putString(BundleConstants.PARAM_CHAPTER_NAME_KEY_WORD, mKeyword);
            }
            bd.putString(BundleConstants.PARAM_CHAPTER_CONTENT, bookmark.getContent());
            bd.putInt(BundleConstants.PARAM_SEARCH_TYPE, mSearchType);
            bd.putString(BundleConstants.PARAM_CATEGORY, categoryName);
            bd.putString(BundleConstants.PARAM_CATEGORY_TYPE, getType(bookmark));
            // mSearchBookmarkListAdapter.getList().clear();
            Log.w("TTT", "ActivityUtil.next:" + bookmark.getChapterName());
            Log.w("TTT", "ActivityUtil.next bookmark:" + GsonUtils.objectToStr(bookmark));
            ActivityUtil.next(ATHIS, ChapterReaderActivity.class, bd, -1);
        }
    }

    public String getType(Bookmark item) {
        if (mRootCategoryMaps != null && mRootCategoryMaps.size() > 0) {
            for (Map.Entry<Category, List<Category>> entry : mRootCategoryMaps.entrySet()) {
                for (Category category : entry.getValue()) {
                    if (item.getCategroyId().equals(category.getId() + "")) {
                        return entry.getKey().getCateName() + "-" + category.getCateName();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 开始搜索
     */
    private void clickSearch(boolean loadFromFirst) {
        if (!checkVipPermission) { //普通会员不可搜索
            CommonUtil.showActivateDialog(ATHIS, UserInfo.VIP_NORMAL);
            return;
        }
        hideInput();
        if (searchHistory != null) {
            searchHistory.setVisibility(View.GONE);
        }
        mKeyword = mSearchEditText.getText().toString().trim();
        if (TextUtils.isEmpty(mKeyword)) {
            search.setClickable(true);
            showToastMsg("请输入搜索关键字");
            return;
        }
        // 保存搜索关键字
        if (mSearchHistoryKeyword.contains(mKeyword.replaceAll("\\s+", " "))) {
            mSearchHistoryKeyword.remove(mKeyword.replaceAll("\\s+", " "));
            String history = mKeyword.replaceAll("\\s+", " ");
            mSearchHistoryKeyword.add(0, history);
        } else {
            String history = mKeyword.replaceAll("\\s+", " ");
            mSearchHistoryKeyword.add(0, history);
            if (mSearchHistoryKeyword.size() > 5) {
                mSearchHistoryKeyword.remove(5);
            }
            try {
                PreferencesUtils.putObject(SearchActivity.this, PreferenceConfig.Preference_Keyword, mSearchHistoryKeyword);
            } catch (IOException e) {
                LogUtil.error("Exception", e);
            }
        }
        searchHistoryAdapter.notifyDataSetChanged();

        if (loadFromFirst) {
            offset = -SearchTextUtil.searchLimit;
            mSearchBookmarkListAdapter.getList().clear();
            mSearchBookmarkListAdapter.notifyDataSetChanged();
            mResultTextView.setText("");
            mSearchBookmarkList = null;
            mSearchProgress.setVisibility(View.VISIBLE);
            mFlSearchProgress.setVisibility(View.VISIBLE);
            mSearchProgress.setProgress(0);
            mSearchProgress.setMax(100);
            mSearchProgressCount = 0;
            startSearchProgress();
        } else {
            mSearchProgress.setVisibility(View.GONE);
            mFlSearchProgress.setVisibility(View.GONE);
        }
        SearchTextUtil.searchByKeywordLoadFinish = false;
        mSearchResultLayout.setVisibility(View.VISIBLE);
        mSearchBookmarkListAdapter.setSearchType(mSearchType);
        ThreadUtil.doOnOtherThread(() -> {
            final List<Bookmark> bookmarkList;
            offset += SearchTextUtil.searchLimit;

            if (mSearchType == 1) {
                bookmarkList = searchVolumeByKeyword(mKeyword, mSearchRoot, mSearchNode);
                searchTotalCountText.post(new Runnable() {
                    @Override
                    public void run() {
                        searchTotalCountText.setText("本次搜索结果" + bookmarkList.size() + "个");
                        searchTotalCountText.setVisibility(View.VISIBLE);
                    }
                });
            } else if (mSearchType == 2) {
                // 标题搜索
                bookmarkList = searchTitleByKeyword(mKeyword, mSearchRoot, mSearchNode, mVolume);
                searchTotalCountText.post(new Runnable() {
                    @Override
                    public void run() {
                        searchTotalCountText.setText("本次搜索结果" + bookmarkList.size() + "个");
                        searchTotalCountText.setVisibility(View.VISIBLE);
                    }
                });
            } else if (mSearchType == 3) {
                bookmarkList = searchChapterByKeyword(mKeyword, mSearchRoot.getId());
                searchTotalCountText.post(new Runnable() {
                    @Override
                    public void run() {
                        searchTotalCountText.setText("本次搜索结果" + bookmarkList.size() + "个");
                        searchTotalCountText.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                long now = System.currentTimeMillis();
                searchTotalCountText.setVisibility(View.GONE);
                bookmarkList = searchContentByKeyword1(mKeyword, mSearchRoot, mSearchNode, mVolume, false, now);
            }
            final List<Bookmark> bookmarkSearchList = new ArrayList<>();
            if (mSearchBookmarkList != null) {
                bookmarkSearchList.addAll(mSearchBookmarkList);
            }
            if (bookmarkList != null && bookmarkList.size() > 0) {
                bookmarkSearchList.addAll(bookmarkList);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showSearchResult(bookmarkSearchList);
                    search.setClickable(true);
                    isSearch = true;
                }
            });
        });
    }

    String categoryName;
    int mSearchProgressCount = 0;

    private void startSearchProgress() {
        mSearchProgress.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchProgress.setProgress(mSearchProgress.getProgress() + 1);
                LogUtil.debug("startSearchProgress:" + mSearchProgress.getProgress());
                mSearchProgressCount++;
                if (mSearchProgressCount < 10) {
                    startSearchProgress();
                }
            }
        }, 500);

    }

    /**
     * 显示默认搜索条件
     */
    private void showSearchText() {
        StringBuilder sb = new StringBuilder();
        sb.append("已选中:");
        sb.append(mSearchRoot.getCateName().replaceAll("^\\d{1,}-", "") + "-");
        sb.append(mSearchNode == null ? "全部" : mSearchNode.getCateName().replaceAll("^\\d{1,}-", ""));
        if (mVolume != null) {
            if (!TextUtils.isEmpty(mVolume.getVolName()) && MTextUtil.isContainChinese(mVolume.getVolName())) {
                if (mVolume.getVolName().contains("E")) {
                    String s = MTextUtil.changeEletter(mVolume.getVolName()).trim();
                    mVolume.setVolName(s);
                }
            }
            sb.append("-《" + mVolume.getVolName().replaceAll("\\{(.*?)\\}", "").replaceAll("\\((.*?)\\)", "")
                    .replaceAll("\\[(.*?)\\]", "") + "》");
        }
        categoryName = sb.toString().replace("已选中:", "");
        mSearchTextView.setText(sb.toString());
    }

    /**
     * 显示搜索结果
     *
     * @param bookmarkList
     */
    private void showSearchResult(List<Bookmark> bookmarkList) {
        mSearchProgress.setVisibility(View.GONE);
        mFlSearchProgress.setVisibility(View.GONE);
        mSearchBookmarkList = bookmarkList;
        mSearchBookmarkListAdapter.setList(mSearchBookmarkList);
        mSearchBookmarkListAdapter.notifyDataSetChanged();
        mResultListView.onLoadMoreComplete();
        mResultListView.setPullLoadEnable(!SearchTextUtil.searchByKeywordLoadFinish);
        int length = mSearchBookmarkList.size();
        mCurrenType = mSearchType;
        if (mSearchType == 2 || mSearchType == 4) {
            StringBuilder sb = new StringBuilder();
            if (mVolume != null) {
                sb.append("在《<font color='#ff0000'>");
                sb.append(mVolume.getVolName().replaceAll("^\\d{1,}-", ""));
                sb.append("</font>》");
            } else if (mSearchNode != null) {
                sb.append("在<font color='#ff0000'>");
                sb.append(mSearchNode.getCateName().replaceAll("^\\d{1,}-", ""));
                sb.append("</font>");
            } else if (mSearchRoot != null) {
                sb.append("在<font color='#ff0000'>");
                sb.append(mSearchRoot.getCateName().replaceAll("^\\d{1,}-", ""));
                sb.append("</font>");
            }
            if (length == 0) {
                mResultTextView.setText(Html.fromHtml(sb.toString() + "中没有搜到结果"));
            } else {
                mResultTextView.setText(Html.fromHtml(sb.toString() + "中共搜索到<font color='#ff0000'>" + length + "</font>个结果"));
            }
        } else {
            if (length == 0) {
                mResultTextView.setText("没有搜到结果");
            } else {
                mResultTextView.setText(Html.fromHtml("共搜索到<font color='#ff0000'>" + length + "</font>个结果"));
            }
        }
    }

    /**
     * 搜索书籍
     *
     * @param keyWord
     * @return
     */
    private List<Bookmark> searchVolumeByKeyword(String keyWord, Category mSearchRoot, Category mSearchNode) {
        List<Volume> volumeList = mVolumeHepler.getVolumesByName(keyWord);
        CategoryDatabaseHelper categoryDatabaseHelper = new CategoryDatabaseHelper(this);
        List<Category> parentId = categoryDatabaseHelper.getCategroyList(mSearchRoot.getId());
        Iterator<Volume> iterator = volumeList.iterator();
        while (iterator.hasNext()) {
            Volume volume = iterator.next();
            boolean isExist = true;
            if (mSearchNode == null) {
                for (Category category : parentId) {
                    if (volume.getCategoryId() == category.getId()) {
                        isExist = false;
                    }
                }
            } else {
                if (volume.getCategoryId() == mSearchNode.getId()) {
                    isExist = false;
                }
            }
            if (isExist) {
                iterator.remove();
            }
        }
        List<Bookmark> bookmarkResultPoints = SearchTextUtil.searchVolumeByKeyword(volumeList, keyWord,
                mSearchProgress);
        SearchTextUtil.searchByKeywordLoadFinish = true;
        return bookmarkResultPoints;
    }

    /**
     * 搜索标题
     *
     * @param
     * @return
     */
    private List<Bookmark> searchTitleByKeyword(String keyword, Category rootCategory, Category nodeCategory,
                                                Volume volume) {
        List<Bookmark> bookmarkResultPoints = searchContentByKeyword(keyword, rootCategory, nodeCategory, volume, true);
        SearchTextUtil.searchByKeywordLoadFinish = true;
        Log.w("TTT", "searchTitleByKeyword bookmarkResultPoints size:" + bookmarkResultPoints.size());
        return bookmarkResultPoints;
    }

    /**
     * 搜索章节
     *
     * @param keyWord
     * @return
     */
    private List<Bookmark> searchChapterByKeyword(String keyWord, int id) {
        List<Chapter> volumeList = mChapterHepler.getChaptersLikeNameJoinVolume(keyWord, id);
        mChapterHepler.setVolumeName(volumeList);
        Iterator<Chapter> iterator = volumeList.iterator();
        while (iterator.hasNext()) {
            Chapter chapter = iterator.next();
            if (chapter.getName().contains("jieshao") || chapter.getName().contains("注释")) {
                iterator.remove();
            }
        }
        List<Bookmark> bookmarkResultPoints = SearchTextUtil.searchChapterByKeyword(volumeList, keyWord, mSearchProgress);
        SearchTextUtil.searchByKeywordLoadFinish = true;
        return bookmarkResultPoints;
    }

    /**
     * 搜索内容
     *
     * @param keyword
     * @param rootCategory
     * @param nodeCategory
     * @param volume
     * @return
     */
    private List<Bookmark> searchContentByKeyword(String keyword, Category rootCategory, Category nodeCategory,
                                                  Volume volume, boolean searchTitle) {
        final long start = System.currentTimeMillis();
        List<Volume> volumeList = null;
        List<Bookmark> bookmarkSearchList = new ArrayList<Bookmark>();
        DbUtils dbUtils = HuDongApplication.getInstance().getDbUtils();
        if (volume != null) {
            try {
                volumeList = dbUtils.findAll(
                        Selector.from(Volume.class).where(WhereBuilder.getInstance("id", "=", volume.getId())));
            } catch (DbException e) {
                LogUtil.error("DbException", e);
            }
        } else if (nodeCategory != null) {
            try {
                volumeList = dbUtils.findAll(Selector.from(Volume.class)
                        .where(WhereBuilder.getInstance("categoryId", "=", nodeCategory.getId())));
            } catch (DbException e) {
                LogUtil.error("DbException", e);
            }

        } else {
            try {
                volumeList = dbUtils.findAll(
                        "Select v.* from volume as v join category as ca on v.categoryId = ca.id where ca.parentId = "
                                + rootCategory.getId(),
                        Volume.class);
            } catch (DbException e) {
                LogUtil.error("DbException", e);
            }
        }
        Log.w("TTT", "searchTitleByKeyword volumeList size:" + volumeList.size());
        LogUtil.test("volume搜索耗时：" + (System.currentTimeMillis() - start));
        for (Volume volumetemp : volumeList) {
            Bookmark bookmark = new Bookmark();
            bookmark.setVolumeId(volumetemp.getId());
            bookmark.setVolumeName(volumetemp.getVolName().replaceAll("^\\d{1,}-", ""));
            bookmark.setChapterIndexId(0);
            bookmark.setChapterName("");
            bookmark.setChapterFileName("");
            bookmark.setCategroyId(volumetemp.getCategoryId() + "");
            bookmarkSearchList.add(bookmark);

        }
        LogUtil.test("其他搜索耗时：" + (System.currentTimeMillis() - start));
        Map<String, Object> searchMap = new HashMap<String, Object>();
        searchMap.put("volumeList", volumeList);
        searchMap.put("volume", volume);
        searchMap.put("category", nodeCategory);
        searchMap.put("rootId", rootCategory.getId());
        searchMap.put("searchTitle", searchTitle);

        List<Bookmark> bookmarkResultList = SearchTextUtil.searchContentByKeyword(keyword, mSearchProgress, searchMap);

        Log.w("TTT", "sql searchTitleByKeyword bookmarkResultList111 size:" + bookmarkResultList.size());
        Iterator<Bookmark> bookmarkIterator = bookmarkResultList.iterator();
        while (bookmarkIterator.hasNext()) {
            Bookmark bookmark = bookmarkIterator.next();
            if (bookmark.getChapterName().contains("jieshao")
                    || bookmark.getChapterName().contains("注释")) {
                bookmarkIterator.remove();
            }
        }
        Log.w("TTT", "sql searchTitleByKeyword bookmarkResultList222 size:" + bookmarkResultList.size());
        return bookmarkResultList;
    }

    /**
     * 搜索内容
     *
     * @param keyword
     * @param rootCategory
     * @param nodeCategory
     * @param volume
     * @return
     */
    private List<Bookmark> searchContentByKeyword1(String keyword, Category rootCategory, Category nodeCategory, Volume volume, boolean searchTitle, long now) {

        List<Volume> volumeList = null;
        DbUtils dbUtils = HuDongApplication.getInstance().getDbUtils();
        if (volume != null) {
            try {
                volumeList = dbUtils.findAll(Selector.from(Volume.class).where(WhereBuilder.getInstance("id", "=", volume.getId())));
            } catch (DbException e) {
            }
        } else if (nodeCategory != null) {
            try {
                volumeList = dbUtils.findAll(Selector.from(Volume.class).where(WhereBuilder.getInstance("categoryId", "=", nodeCategory.getId())));
            } catch (DbException e) {
            }

        } else {
            try {
                volumeList = dbUtils.findAll("Select v.* from volume as v join category as ca on v.categoryId = ca.id where ca.parentId = " + rootCategory.getId(), Volume.class);
            } catch (DbException e) {
            }
        }

        List<Bookmark> bookmarks = SearchTextUtil.searchContent(volumeList, nodeCategory, volume, keyword, rootCategory.getId(), offset, 1);
        return bookmarks;
    }

    /**
     * 搜索内容
     *
     * @param keyword
     * @param rootCategory
     * @param nodeCategory
     * @param volume
     * @return
     */
    private List<Bookmark> searchContentByKeyword(String keyword, Category rootCategory, Category nodeCategory, Volume volume, boolean searchTitle, long now) {
        now = System.currentTimeMillis();
        final long start = System.currentTimeMillis();
        List<Volume> volumeList = null;
        List<Bookmark> bookmarkSearchList = new ArrayList<Bookmark>();
        DbUtils dbUtils = HuDongApplication.getInstance().getDbUtils();
        if (volume != null) {
            try {
                volumeList = dbUtils.findAll(Selector.from(Volume.class).where(WhereBuilder.getInstance("id", "=", volume.getId())));
            } catch (DbException e) {
            }
        } else if (nodeCategory != null) {
            try {
                volumeList = dbUtils.findAll(Selector.from(Volume.class).where(WhereBuilder.getInstance("categoryId", "=", nodeCategory.getId())));
            } catch (DbException e) {
            }

        } else {
            try {
                volumeList = dbUtils.findAll("Select v.* from volume as v join category as ca on v.categoryId = ca.id where ca.parentId = " + rootCategory.getId(), Volume.class);
            } catch (DbException e) {
            }
        }
        now = System.currentTimeMillis();
        LogUtil.test("volume搜索耗时：" + (System.currentTimeMillis() - start));
        for (Volume volumetemp : volumeList) {
            Bookmark bookmark = new Bookmark();
            bookmark.setVolumeId(volumetemp.getId());
            bookmark.setVolumeName(volumetemp.getVolName().replaceAll("^\\d{1,}-", ""));
            bookmark.setChapterIndexId(0);
            bookmark.setChapterName("");
            bookmark.setChapterFileName("");
            bookmark.setCategroyId(volumetemp.getCategoryId() + "");
            bookmarkSearchList.add(bookmark);
        }
        now = System.currentTimeMillis();
        Map<String, Object> searchMap = new HashMap<String, Object>();
        searchMap.put("volumeList", volumeList);
        searchMap.put("volume", volume);
        searchMap.put("category", nodeCategory);
        searchMap.put("rootId", rootCategory.getId());
        searchMap.put("searchTitle", searchTitle);
        List<Bookmark> bookmarkResultList = SearchTextUtil.searchContentByKeyword(keyword, mSearchProgress, searchMap, now, offset);
        if (!searchTitle) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long count = SearchTextUtil.searchCountByKeyword(keyword, searchMap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (count > 1000) {
                                searchTotalCountText.setText("本次搜索有999⁺章包含关键词");
                            } else {
                                if (bookmarkResultList.size() == 0) {
                                    searchTotalCountText.setText("对不起，没有任何结果，建议更换关键词");
                                } else {
                                    searchTotalCountText.setText("本次搜索有" + bookmarkResultList.size() + "章包含关键字");
                                }
                            }
                            searchTotalCountText.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }).start();
        }


        Iterator<Bookmark> bookmarkIterator = bookmarkResultList.iterator();
        while (bookmarkIterator.hasNext()) {
            Bookmark bookmark = bookmarkIterator.next();
            if (bookmark.getChapterName().contains("jieshao") || bookmark.getChapterName().contains("注释") || (bookmark.getContent().contains("<h3>") && bookmark.getContent().contains("</h3>"))) {
                bookmarkIterator.remove();
            }
        }
        return bookmarkResultList;
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        contextMenu.add(1, 1, 0, "加入书签");
        contextMenu.add(1, 2, 0, "以短信发送");
        contextMenu.add(1, 3, 0, "复制");
        contextMenu.add(1, 4, 0, "分享");
        contextMenu.add(1, 5, 0, "取消");

        showBg(0.6f);
    }

    private void showBg(float alpha) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = alpha;
        getWindow().setAttributes(attributes);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        showBg(1f);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        showBg(1f);
        // 获取当前被选择的菜单项的信息
        Bookmark bookmark = mSearchBookmarkList.get(mListViewIndex);
        if (mVolume != null) {
            bookmark.setCategroyId(mVolume.getCategoryId() + "");
        }
        switch (menuItem.getItemId()) {
            case 1:
                // 加入书签
                Bundle bd = new Bundle();
                ArrayList<Bookmark> list = new ArrayList<Bookmark>();
                list.add(bookmark);
                bd.putParcelableArrayList(BundleConstants.PARAM_BOOK_MARK_LIST, list);
                ActivityUtil.next(ATHIS, BookmarkEditActivity.class, bd, -1);
                break;
            case 3: {
                // 复制
                StringBuffer copy = new StringBuffer();
                int index;
                if (bookmark.getVolumeName() == null || TextUtils.isEmpty(bookmark.getVolumeName().trim())) {

                } else {
                    index = bookmark.getVolumeName().indexOf("(");
                    if (index != -1) {
                        copy.append("《" + bookmark.getVolumeName().substring(0, index).replaceAll("E", "") + "》");
                    } else {
                        copy.append("《" + bookmark.getVolumeName().replaceAll("E", "") + "》");
                    }
                }
                copy.append(bookmark.getChapterName());
                String content = bookmark.getReplaceContent();
                copy.append("\n  " + content);
                String value = copy.toString();
                value = value.replaceAll("〖(.*?)〗", "");
                value = value.replaceAll("(?<=\\[)(.*?)(?=])", "");
                value = value.replaceAll("(?<=\\{)[^}]*(?=\\})", "");
                value = value.replaceAll("\\[\\]", "");
                value = value.replaceAll("\\{\\}", "");
                CommonUtil.copy(ATHIS, value);
            }
            break;
            case 4: {
                // 分享
                StringBuffer shareSb = new StringBuffer();
                int index = bookmark.getVolumeName().indexOf("(");
                if (index != -1) {
                    shareSb.append("《" + bookmark.getVolumeName().substring(0, index).replaceAll("E", "") + "》");
                } else {
                    shareSb.append("《" + bookmark.getVolumeName().replaceAll("E", "") + "》");
                }
                shareSb.append(bookmark.getChapterName());
                shareSb.append("\n  " + bookmark.getReplaceContent());
                UmShareUtils.shareText(this, shareSb.toString());
            }
            break;
            case 5:
                // 取消
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_remain:
                if (searchHistory.getVisibility() == View.VISIBLE) {
                    searchHistory.setVisibility(View.GONE);
                    return;
                }
                mSearchRoot = mRootCategorys.get(mIndexCategory);
                mSearchNode = null;
                mCategoryTwoGridAdapter.setIndex(0);

                mSearchCategoryAdapter.setNodeCategorys(mRootCategoryMaps.get(mRootCategorys.get(mIndexCategory)));
                mSearchCategoryAdapter.notifyDataSetChanged();
                mCategorySectionListView.setSelection(0);
                showSearchText();
                clickShowOptionPopupWindow();
                break;
            default:
                break;
        }
    }

    private void selectChange() {
        showSearchText();
    }

    private void initContentViews() {
        CategoryOneGridAdapter adapter = new CategoryOneGridAdapter(this, mRootCategorys);
        adapter.setIndex(mIndexCategory);
        GridView gridView = (GridView) mContentLayout.findViewById(R.id.gridview_1);
        gridView.setNumColumns(mRootCategorys.size());
        adapter.setOnItemClickListener(new CategoryOneGridAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position) {
                if (searchHistory.getVisibility() == View.VISIBLE) {
                    searchHistory.setVisibility(View.GONE);
                    return;
                }
                if (mIndexCategory == position) {
                    return;
                }
                mIndexCategory = position;
                mSearchRoot = mRootCategorys.get(mIndexCategory);
                mSearchNode = null;
                mVolume = null;
                selectChange();
                CategoryOneGridAdapter adapter = (CategoryOneGridAdapter) parent.getAdapter();
                adapter.setIndex(parent, position);
                mCategoryTwoGridAdapter.setList(mRootCategoryMaps.get(mRootCategorys.get(mIndexCategory)));
                mCategoryTwoGridAdapter.setIndex(mCategoryTwoGridView, 0);
                mCategoryTwoGridAdapter.notifyDataSetChanged();

                mSearchCategoryAdapter.notifyDataSetChanged();
                mSearchCategoryAdapter.setNodeCategorys(mRootCategoryMaps.get(mRootCategorys.get(mIndexCategory)));
                mCategorySectionListView.setSelection(0);
            }
        });
        gridView.setAdapter(adapter);

        mCategoryTwoGridAdapter = new CategoryTwoGridAdapter(this);
        mCategoryTwoGridAdapter.setList(mRootCategoryMaps.get(mRootCategorys.get(mIndexCategory)));
        int twoIndex = 0;
        if (mSearchNode != null) {
            for (int i = 0; i < mRootCategoryMaps.get(mRootCategorys.get(mIndexCategory)).size(); i++) {
                if (mRootCategoryMaps.get(mRootCategorys.get(mIndexCategory)).get(i).getId() == mSearchNode.getId()) {
                    twoIndex = i;
                    twoIndex = twoIndex + 1;
                    break;
                }
            }
        }
        mCategoryTwoGridAdapter.setIndex(twoIndex);
        mCategoryTwoGridView = (GridView) mContentLayout.findViewById(R.id.gridview_2);
        mCategoryTwoGridView.setNumColumns(7);
        mCategoryTwoGridView.setAdapter(mCategoryTwoGridAdapter);
        mCategoryTwoGridAdapter.setOnItemClickListener(new CategoryTwoGridAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position) {
                if (searchHistory.getVisibility() == View.VISIBLE) {
                    searchHistory.setVisibility(View.GONE);
                    return;
                }
                mCategoryTwoGridAdapter.setIndex(mCategoryTwoGridView, position);
                if (position == 0) {
                    mSearchNode = null;
                    mVolume = null;
                    mSearchCategoryAdapter.setNodeCategorys(mRootCategoryMaps.get(mRootCategorys.get(mIndexCategory)));
                    mSearchCategoryAdapter.notifyDataSetChanged();
                    mCategorySectionListView.setSelection(0);
                } else {
                    ArrayList<Category> categories = new ArrayList<Category>();
                    categories.add(mCategoryTwoGridAdapter.getItem(position));
                    mSearchNode = mCategoryTwoGridAdapter.getItem(position);
                    mVolume = null;
                    mSearchCategoryAdapter.setNodeCategorys(categories);
                    mSearchCategoryAdapter.notifyDataSetChanged();
                    mCategorySectionListView.setSelection(0);
                }
                selectChange();
            }
        });
        mSearchCategoryAdapter = new SearchCategoryAdapter(this);
        int finalTwoIndex = twoIndex;
        showProgressDialog("正在加载...");
        //这个数据加载耗时 放后台更新
        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                if (finalTwoIndex != 0) {
                    //有选中
                    ArrayList<Category> categories = new ArrayList<Category>();
                    categories.add(mCategoryTwoGridAdapter.getItem(finalTwoIndex));
                    mSearchCategoryAdapter.setNodeCategorys(categories);
                } else {
                    mSearchCategoryAdapter.setNodeCategorys(mRootCategoryMaps.get(mRootCategorys.get(mIndexCategory)));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                        mSearchCategoryAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        mSearchCategoryAdapter.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (searchHistory.getVisibility() == View.VISIBLE) {
                    searchHistory.setVisibility(View.GONE);
                    return;
                }
                VolumeGridAdapter adapter = (VolumeGridAdapter) parent.getAdapter();
                int index = (Integer) parent.getTag();
                mSearchNode = mSearchCategoryAdapter.getItem(index);
                mVolume = adapter.getItem(position);
                selectChange();
                mContentLayout.setVisibility(View.GONE);
            }
        });
        mCategorySectionListView = (ListView) mContentLayout.findViewById(R.id.listview);
        mCategorySectionListView.setAdapter(mSearchCategoryAdapter);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(LoginOutEvent loginOutEvent) {
        this.finish();
    }


}
