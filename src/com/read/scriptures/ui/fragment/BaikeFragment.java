package com.read.scriptures.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.read.scriptures.EIUtils.CustomArrayAdapter;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.BaikeDatabaseHepler;
import com.read.scriptures.model.Baike;
import com.read.scriptures.model.BaikeCategory;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.activity.read.BaikeActivity;
import com.read.scriptures.ui.adapter.SearchBookListAdapter;
import com.read.scriptures.ui.adapter.baike.BaikeListAdapter;
import com.read.scriptures.ui.adapter.baike.CategoryBaikeGridAdapter;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.util.CollectionUtil;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.ThreadUtil;
import com.read.scriptures.view.QuickIndexBar;
import com.read.scriptures.widget.ClearEditText;
import com.read.scriptures.widget.LoadListView;
import com.zxl.common.db.sqlite.DbException;
import com.zxl.common.db.sqlite.Selector;
import com.zxl.common.db.sqlite.WhereBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ClickableViewAccessibility")
public class BaikeFragment extends Base1Fragment implements OnItemClickListener, OnClickListener, LoadListView.ILoadListener2 {

    /**
     * 搜索输入框
     */
    private ClearEditText searchEdit;

    /**
     * 搜索按钮
     */
    private TextView searchBtn;

    private LinearLayout linearType;
    private TextView tv_search_type;
    private ImageView ic_by_name;

    private QuickIndexBar mQuickIndexBar;
    private TextView mTvTextDialog;

    /**
     * 百科文档列表
     */
    private LoadListView listView;

    private BaikeListAdapter listAdapter;

    private CustomArrayAdapter searchHistoryAdapter;

    private LinearLayout mSearchHistoryLayout;

    private SearchBookListAdapter searchBookListAdapter;

    private ProgressBar progressBar;
    /**
     * 分类选中项
     */
    private int selectIndex = 0;

    private int selectCateGoryId = 0;

    private int selectNow = 0;

    private int start = 0;

    /**
     * 搜索关键字
     */
    private String mKeyWords;


    /**
     * 搜索类型 搜索标题 2 搜索内容1
     */
    private int mSearchType = 2;
    /**
     * 默认分类
     */
    private List<BaikeCategory> rootCategorys;

    /**
     * 百科列表
     */
    private List<Baike> baikes;


    /**
     * 搜索历史
     */
    private List<String> mSearchHistoryKeyword;

    private boolean isSearchState;

    private PopupWindow popType;

    public BaikeFragment() {
        rootCategorys = new ArrayList<BaikeCategory>();
        baikes = new ArrayList<Baike>();
//        allBaikes = new ArrayList<>();
        mSearchHistoryKeyword = new ArrayList<String>();
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);

    }

    @SuppressWarnings("unchecked")
    private void initData() {
        try {
            rootCategorys = HuDongApplication.getInstance().getDbUtils().findAll(Selector.from
                    (BaikeCategory.class).where(WhereBuilder.getInstance("parentId", "=", 0)));
        } catch (DbException e) {
            LogUtil.error("rootCategorys", e);
        }
        if (rootCategorys != null && rootCategorys.size() > 0) {
            selectCateGoryId = rootCategorys.get(0).getId();
        }
        // 初始化搜索历史关键字数据
        List<String> keyword = null;
        try {
            keyword = (List<String>) PreferencesUtils.getObject(getActivity(), PreferenceConfig.Preference_Keyword);
        } catch (IOException e) {
            LogUtil.error("Exception", e);
        } catch (ClassNotFoundException e) {
            LogUtil.error("Exception", e);
        }
        if (keyword != null) {
            mSearchHistoryKeyword.clear();
            mSearchHistoryKeyword.addAll(keyword);
        }
    }

    public void initViews() {
        initPopType();
        linearType = (LinearLayout) findViewById(R.id.linear_type);
        tv_search_type = (TextView) findViewById(R.id.tv_search_type);
        ic_by_name = (ImageView) findViewById(R.id.ic_by_name);
        linearType.setOnClickListener(this);
        GridView classifyGridView = (GridView) findViewById(R.id.baike_classify_gridview);
        searchEdit = (ClearEditText) findViewById(R.id.baike_search_edt);
        searchBtn = (TextView) findViewById(R.id.baike_search_btn);
        listView = (LoadListView) findViewById(R.id.baike_listview);
        listView.setInterface(this);
        listAdapter = new BaikeListAdapter(getActivity(), baikes);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);


        mQuickIndexBar = (QuickIndexBar) findViewById(R.id.quick_index_bar);
        mTvTextDialog = (TextView) findViewById(R.id.text_dialog);
        mQuickIndexBar.setTextView(mTvTextDialog);

        mQuickIndexBar.setOnLetterChangedListener(new QuickIndexBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {

            }

            @Override
            public void onLetterGone() {
            }
        });

        CategoryBaikeGridAdapter gridAdapter = new CategoryBaikeGridAdapter(getActivity(), rootCategorys);
        gridAdapter.setIndex(0);
        classifyGridView.setNumColumns(rootCategorys.size());
        classifyGridView.setAdapter(gridAdapter);
        classifyGridView.setOnItemClickListener(this);

        searchBtn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.search_progress);
        progressBar.setVisibility(View.GONE);
        searchBookListAdapter = new SearchBookListAdapter(getActivity());
        searchBookListAdapter.setSearchType(9);
        searchEdit.setListener(new ClearEditText.TouchAndTextChangeListener() {
            @Override
            public boolean onTouch(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mSearchHistoryLayout.setVisibility(View.VISIBLE);
//                    searchHistoryAdapter.notifyDataSetChanged();
                    return true;
                }
                return true;
            }

            @Override
            public void afterTextChanged(final Editable s) {
                ThreadUtil.doOnOtherThread(new Runnable() {
                    public void run() {
                        isSearchState = false;
                        baikes.clear();
//                        for (Baike baike : allBaikes) {
//                            if (baike.getCategoryId() == selectCateGoryId && baike.getShowName()
//                                    .contains(s
//                                            .toString())) {
//                                baikes.add(baike);
//                            }
//                        }
                        baikes.addAll(HuDongApplication.getInstance().getDbUtils().findAll("select * from baike where categoryId = " + selectCateGoryId + " limit " + start + "," + (start + 500)));
                        listAdapter.setKeyWord(s.toString());
                        searchEdit.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isSearchState) {
                                    return;
                                }
                                listAdapter.setList(baikes);
                                listView.setAdapter(listAdapter);
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
//                mSearchHistoryLayout.setVisibility(View.GONE);
            }
        });
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    mKeyWords = searchEdit.getText().toString();
                    refresh();
                    return true;
                }
                return false;
            }
        });
        initSearchHistoryLayout();
    }

    private void chanageQuickBar() {
//        if (!SharedUtil.getBoolean(Preference_home_sort_type, false) && mQuickIndexBar != null) {
//            List<String> letters = new ArrayList<>();
//            for (Baike baike : baikes) {
//                if(!letters.contains(baike.getFirstLetter())) {
//                    letters.add(baike.getFirstLetter());
//                }
//            }
//            mQuickIndexBar.setLetters(letters);
//            mQuickIndexBar.setVisibility(View.VISIBLE);
//        }
    }


    private void initSearchHistoryLayout() {
        // 搜索历史
        mSearchHistoryLayout = (LinearLayout) findViewById(R.id.layout_history);
        searchHistoryAdapter = new CustomArrayAdapter(getActivity(), R.layout
                .adapter_search_history_item,
                R.id.tv_title, mSearchHistoryKeyword);
        ListView searchHistory = (ListView) findViewById(R.id.listview_history);
        searchHistory.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchEdit.setText(mSearchHistoryKeyword.get(position));
                searchEdit.setSelection(mSearchHistoryKeyword.get(position).length());
                ((BaseActivity) getActivity()).hideInput();
            }
        });
        searchHistory.setAdapter(searchHistoryAdapter);
        findViewById(R.id.clear_search_history).setOnClickListener(this);
        mSearchHistoryLayout.setVisibility(View.GONE);
    }

    @Override
    public int onObtainLayoutResId() {
        return R.layout.fragment_baike;
    }

    @Override
    public void onResume() {
        super.onResume();
//        showProgressDialog("等待加载…");
//        allBaikes.clear();
//        ThreadUtil.doOnOtherThread(new Runnable() {
//            public void run() {
//                initData();
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        refresh();
//                    }
//                });
//                dismissProgressDialog();
//            }
//        });
    }

    private String getCateName(int pos) {
        String s = "圣经百科";
        switch (pos) {
            case 0:
                s = "圣经百科";
                break;
            case 1:
                s = "圣经名词";
                break;
            case 2:
                s = "圣经词典";
                break;
            case 3:
                s = "圣经词汇";
                break;
            case 4:
                s = "圣经浅注";
                break;
        }
        return s;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.baike_classify_gridview:
//                mSearchHistoryLayout.setVisibility(View.GONE);
//                if (position == selectIndex) {
//                    return;
//                }
                selectIndex = position;
                if (isSearchState) {//说明还是搜索状态，继续搜索
                    CategoryBaikeGridAdapter adapter = (CategoryBaikeGridAdapter) parent.getAdapter();
                    selectCateGoryId = adapter.getItem(selectIndex).getId();
                    adapter.setIndex(parent, position);
                    refresh();
                } else {
                    searchEdit.getText().clear();
                    mKeyWords = "";
                    isSearchState = false;
                    CategoryBaikeGridAdapter adapter = (CategoryBaikeGridAdapter) parent.getAdapter();
                    selectCateGoryId = adapter.getItem(selectIndex).getId();
                    adapter.setIndex(parent, position);
                    searchEdit.setHint("在" + rootCategorys.get(position).getShowCateName() + "搜索");
                    refresh();
                }
                break;
            case R.id.baike_listview:
                if (isSearchState) {
                    Bookmark bookmark = searchBookListAdapter.getItem(position);
                    Bundle bd = new Bundle();
                    Baike baike = new Baike();
                    baike.setName(bookmark.getChapterName());
                    baike.setContent(bookmark.getContent());
                    baike.setCateName(bookmark.getVolumeName());
                    baike.setCategoryId(bookmark.getVolumeId());
                    baike.setId(bookmark.getId());
                    bd.putParcelable(BundleConstants.PARAM_BAIKE, baike);
                    bd.putInt(BundleConstants.PARAM_TIPS_POSTION, bookmark.getIndex());
                    bd.putString(BundleConstants.PARAM_TIPS_KEYWORD, mKeyWords);
                    ActivityUtil.next(getActivity(), BaikeActivity.class, bd, -1);
                } else {
                    Baike baike = baikes.get(position);
                    Bundle bd = new Bundle();
                    bd.putParcelable(BundleConstants.PARAM_BAIKE, baike);
                    ActivityUtil.next(getActivity(), BaikeActivity.class, bd, -1);
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baike_search_btn:
                mKeyWords = searchEdit.getText().toString();
                refresh();
                break;
            case R.id.clear_search_history:
                mSearchHistoryKeyword.clear();
                List<String> empty = new ArrayList<String>();
                try {
                    PreferencesUtils.putObject(getActivity(), PreferenceConfig.Preference_Keyword, empty);
                } catch (IOException e) {
                    LogUtil.error("IOException", e);
                }
                searchHistoryAdapter.notifyDataSetChanged();
                break;
            case R.id.linear_type:
                popType.showAsDropDown(linearType, 0, 0);//(int)DisplayUtil.dp2px(MainActivity.this,24)
                break;
            case R.id.rb_by_content:
                mSearchType = 1;
                tv_search_type.setText("内容搜索");
                iv_by_title.setVisibility(View.INVISIBLE);
                iv_by_content.setVisibility(View.VISIBLE);
                popType.dismiss();
                break;
            case R.id.rb_by_title:
                mSearchType = 2;
                tv_search_type.setText("标题搜索");
                iv_by_title.setVisibility(View.VISIBLE);
                iv_by_content.setVisibility(View.INVISIBLE);
                popType.dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 刷新百科列表展示
     */
    public void refresh() {
        if (StringUtil.isEmpty(mKeyWords)) {
            isSearchState = false;
            if (selectCateGoryId != selectNow) {//说明不是一个栏目
                baikes.clear();
                baikes.addAll(HuDongApplication.getInstance().getDbUtils().findAll("select * from baike where categoryId = " + selectCateGoryId + " limit " + start + "," + (start + 500)));
//            allBaikes = HuDongApplication.getInstance().getDbUtils().findAll("select * from baike order by categoryId,indexId");
//            for (Baike baike : allBaikes) {
//                if (baike.getCategoryId() == selectCateGoryId) {
//                    baikes.add(baike);
//                }
//            }
                listAdapter.setList(baikes);
                listAdapter.notifyDataSetChanged();
                listView.setSelection(0);
            } else {
                baikes.addAll(HuDongApplication.getInstance().getDbUtils().findAll("select * from baike where categoryId = " + selectCateGoryId + " limit " + start + "," + (start + 500)));
//            allBaikes = HuDongApplication.getInstance().getDbUtils().findAll("select * from baike order by categoryId,indexId");
//            for (Baike baike : allBaikes) {
//                if (baike.getCategoryId() == selectCateGoryId) {
//                    baikes.add(baike);
//                }
//            }
                listAdapter.addList(baikes);
                listAdapter.notifyDataSetChanged();
            }
            selectNow = selectCateGoryId;
            chanageQuickBar();
        } else {
            selectNow = selectCateGoryId;
            isSearchState = true;
            searchByKeyWord();
        }
//        mSearchHistoryLayout.setVisibility(View.GONE);
    }

    private void searchByKeyWord() {
        // 保存搜索关键字
        if (mSearchHistoryKeyword.contains(String.valueOf(mKeyWords).trim())) {
            mSearchHistoryKeyword.remove(String.valueOf(mKeyWords).trim());
            mSearchHistoryKeyword.add(0, String.valueOf(mKeyWords).trim());
        } else {
            mSearchHistoryKeyword.add(0, String.valueOf(mKeyWords).trim());
            if (mSearchHistoryKeyword.size() > 5) {
                mSearchHistoryKeyword.remove(5);
            }
            try {
                PreferencesUtils.putObject(getActivity(), PreferenceConfig.Preference_Keyword, mSearchHistoryKeyword);
            } catch (IOException e) {
                LogUtil.error("Exception", e);
            }
        }
        searchHistoryAdapter.notifyDataSetChanged();
        ((BaseActivity) getActivity()).hideInput();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        ThreadUtil.doOnOtherThread(new Runnable() {
            public void run() {
                int wait = 10;
                while (wait > 0) {
                    if (!CollectionUtil.isEmpty(baikes)) {
                        break;
                    }
                    wait--;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        LogUtil.error("InterruptedException", e);
                    }
                }
                String where = rootCategorys.get(selectCateGoryId - 1).getCateName();
                Log.w("TTT", "mSearchType:" + mSearchType);
                if (mSearchType == 1) {
//                    final List<Bookmark> bookmarks = SearchTextUtil.searchBaikeByKeyword(baikes, mKeyWords, progressBar);
                    BaikeDatabaseHepler baikeDatabaseHepler = new BaikeDatabaseHepler(getContext());
                    final List<Bookmark> bookmarks = baikeDatabaseHepler.selectKeywordByContent(mKeyWords, progressBar, where);
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            searchBookListAdapter.setList(bookmarks);
                            listView.setAdapter(searchBookListAdapter);
                            searchBookListAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
//                    final List<Bookmark> bookmarks = SearchTextUtil.searchBaikeTitleByKeyword(baikes, mKeyWords, progressBar);
                    BaikeDatabaseHepler baikeDatabaseHepler = new BaikeDatabaseHepler(getContext());
                    final List<Bookmark> bookmarks = baikeDatabaseHepler.selectKeywordByTitle(mKeyWords, progressBar, where);
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.w("TTT", "22222222222");
                            searchBookListAdapter.setList(bookmarks);
                            listView.setAdapter(searchBookListAdapter);
                            searchBookListAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });

    }

    @Override
    public void lazyLoad() {
        ThreadUtil.doOnOtherThread(new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog("等待加载…");
                    }
                });
                initData();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initViews();
                        refresh();
                        dismissProgressDialog();
                    }
                });
            }
        });
    }

    @Override
    public void initWidget() {

    }

    ImageView iv_by_title;
    ImageView iv_by_content;

    private void initPopType() {
        @SuppressLint("InflateParams") View popView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_search_baike, null);
        popView.findViewById(R.id.rb_by_title).setOnClickListener(this);
        popView.findViewById(R.id.rb_by_content).setOnClickListener(this);
        iv_by_title = (ImageView) popView.findViewById(R.id.ic_by_title);
        iv_by_content = (ImageView) popView.findViewById(R.id.ic_by_content);
        popType = new PopupWindow(popView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popType.setTouchable(true);
        popType.setFocusable(true);
        ColorDrawable draw = new ColorDrawable(0x00000000);
        popType.setBackgroundDrawable(draw);
        popType.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    @Override
    public void onLoad() {
        //获取更多数据
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //通知listView显示更新,加载完毕
                start += 500;
                refresh();
                /**
                 * 设置默认显示为Listview最后一行
                 */
//                listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//                listView.setStackFromBottom(true);
                //通知listView加载完毕，底部布局消失
                listView.loadComplete();
            }
        }, 1000);
    }
}
