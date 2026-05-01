package com.read.scriptures.audio;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicPlayerEventListener;
import com.music.player.lib.listener.MusicPlayerInfoListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.read.scriptures.R;
import com.read.scriptures.bean.DownloadAudioEvent;
import com.read.scriptures.bean.NewAudioBean;
import com.read.scriptures.bean.NewAudioChapterData;
import com.read.scriptures.bean.NewBookData;
import com.read.scriptures.bean.SearchChapterBean;
import com.read.scriptures.bean.SearchVideoBean;
import com.read.scriptures.bean.VideoBookBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.SearchMediaCacheAdapter;
import com.read.scriptures.util.LogUtil;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.video.SearchVideoItemAdapter;
import com.read.scriptures.video.VideoListAdapter;
import com.read.scriptures.view.AudioPlayingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAudioActivity extends BaseActivity {

    private MusicPlayerManager musicPlayerManager;
    private RadioGroup radio_group;

    private ArrayList<NewAudioBean.RowsBean> cateIds;

    private String currentCateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_search_audio);
        StatusBarUtils.initMainColorStatusBar(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initData();
    }


    private EditText mSearchEditText;
    private TextView btn_search;
    private TextView tv_count;
    private RecyclerView rcv_list;
    private RecyclerView rcv_cache_search;
    private AudioPlayingView fl_view;

    private RecyclerView rcv_cate;

    private String type = "2";
    private String lastSearchText = "";

    private SearchMediaCacheAdapter searchMediaCacheAdapter;
    private ArrayList<String> cacheSearchList = new ArrayList<>();

    private SearchAudioCateAdapter searchAudioCateAdapter;

    private void initView() {
        radio_group = findViewById(R.id.radio_group);
        mSearchEditText = findViewById(R.id.et_search);
        btn_search = findViewById(R.id.btn_search);
        rcv_list = findViewById(R.id.rcv_list);
        rcv_cache_search = findViewById(R.id.rcv_cache_search);
        fl_view = findViewById(R.id.fl_view);
        tv_count = findViewById(R.id.tv_count);
        rcv_cate = findViewById(R.id.rcv_cate);

        rcv_cate.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        searchAudioCateAdapter = new SearchAudioCateAdapter();
        rcv_cate.setAdapter(searchAudioCateAdapter);

        searchAudioCateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                lastSearchText = "";
                searchAudioCateAdapter.setCurrentId(currentCateId = searchAudioCateAdapter.getItem(position).id);
                formatSearchResult(currentCateId);
            }
        });

        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        rcv_cache_search.setLayoutManager(new LinearLayoutManager(this));
        searchMediaCacheAdapter = new SearchMediaCacheAdapter();
        rcv_cache_search.setAdapter(searchMediaCacheAdapter);


        searchMediaCacheAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_clear:
                        searchMediaCacheAdapter.remove(position);
                        break;
                }
            }
        });
        searchMediaCacheAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String str = searchMediaCacheAdapter.getItem(position);
                rcv_cache_search.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(str)) {
                    mSearchEditText.setText(str);
                    mSearchEditText.setSelection(mSearchEditText.length());
                }
            }
        });
        mSearchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (TextUtils.isEmpty(mSearchEditText.getText().toString().trim())) {//为空则显示所有的
                    searchMediaCacheAdapter.setNewData(cacheSearchList);
                } else {
                    changeSearch(mSearchEditText.getText().toString().trim());
                }
                ViewGroup.LayoutParams layoutParams = rcv_cache_search.getLayoutParams();
                layoutParams.width = mSearchEditText.getWidth();
                if (searchMediaCacheAdapter.getItemCount() > 0) {
                    rcv_cache_search.setVisibility(View.VISIBLE);
                } else {
                    rcv_cache_search.setVisibility(View.GONE);
                }
                return false;
            }
        });

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_1://搜索书籍
                        type = "1";
                        lastSearchText = "";
                        break;
                    case R.id.rb_2: //搜索章节
                        type = "2";
                        lastSearchText = "";
                        break;
                    case R.id.rb_3: //搜索作者
                        type = "3";
                        lastSearchText = "";
                        break;
                }
            }
        });
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            rcv_cache_search.setVisibility(View.GONE);
                            String trim = mSearchEditText.getText().toString().trim();
                            if (TextUtils.isEmpty(trim)) {
                                return false;
                            }
                            clickSearch(trim);
                            return true;
                        default:
                            return true;
                    }
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
                if (TextUtils.isEmpty(editable.toString())) {//为空则显示所有的
                    btn_search.setEnabled(false);
                } else {
                    btn_search.setEnabled(true);
                }
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchEditText.getText().toString().trim();
                if (!TextUtils.equals(searchText, lastSearchText)) {
                    clickSearch(searchText);
                }
                lastSearchText = searchText;
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchAudioBookAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                rcv_cache_search.setVisibility(View.GONE);
                Intent intent = new Intent(SearchAudioActivity.this, NewAudioChapterActivity.class);
                intent.putExtra(NewAudioChapterActivity.BOOK_DATA, searchAudioBookAdapter.getItem(position));
                intent.putExtra(NewAudioChapterActivity.BOOK_AUTHOR, searchAudioBookAdapter.getItem(position).author);
                startActivity(intent);
            }
        });
        rcv_list.setAdapter(searchAudioChapterAdapter);
        searchAudioChapterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                rcv_cache_search.setVisibility(View.GONE);
                BaseAudioInfo item = searchAudioChapterAdapter.getItem(position);
                searchAudioChapterAdapter.setCurrentAudio(item);
                getBookAllAudio(item);
            }
        });

        searchAuthorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                rcv_cache_search.setVisibility(View.GONE);
                BaseAudioInfo item = searchAuthorAdapter.getItem(position);
                searchAuthorAdapter.setCurrentAudio(item);
                getBookAllAudio(item);
            }
        });

        mSearchEditText.post(new Runnable() {
            @Override
            public void run() {
                showInput(mSearchEditText);
            }
        });

        findViewById(R.id.cl_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcv_cache_search.setVisibility(View.GONE);
            }
        });
    }

    private void formatSearchResult(String currentCateId) {
        RecyclerView.Adapter adapter = rcv_list.getAdapter();
        if (adapter instanceof NewSearchAudioBookAdapter) {
            if (newBookData == null || newBookData.rows == null || newBookData.rows.isEmpty()) {
                return;
            }
            if (TextUtils.equals("0", currentCateId)) {//点击全部
                tv_count.setText("搜索结果:" + newBookData.rows.size());
                ((NewSearchAudioBookAdapter) adapter).setNewData(newBookData.rows);
                return;
            }
            List<NewBookData.RowsBean> newResult = new ArrayList<>();
            for (NewBookData.RowsBean item : newBookData.rows) {
                if (TextUtils.equals(currentCateId, item.cate1_id)) {
                    newResult.add(item);
                }
            }

            tv_count.setText("搜索结果:" + newResult.size());
            ((NewSearchAudioBookAdapter) adapter).setNewData(newResult);
        } else if (adapter instanceof NewSearchAudioChapterAdapter) {
            if (newChapterData == null || newChapterData.rows == null || newChapterData.rows.isEmpty()) {
                return;
            }
            if (TextUtils.equals("0", currentCateId)) {//点击全部
                tv_count.setText("搜索结果:" + newChapterData.rows.size());
                ((NewSearchAudioChapterAdapter) adapter).setNewData(newChapterData.rows);
                return;
            }
            List<BaseAudioInfo> newResult = new ArrayList<>();
            for (BaseAudioInfo item : newChapterData.rows) {
                if (TextUtils.equals(currentCateId, item.cate1_id)) {
                    newResult.add(item);
                }
            }
            tv_count.setText("搜索结果:" + newResult.size());
            ((NewSearchAudioChapterAdapter) adapter).setNewData(newResult);
        } else if (adapter instanceof NewSearchAuthorChapterAdapter) {
            if (newAuthorData == null || newAuthorData.rows == null || newAuthorData.rows.isEmpty()) {
                return;
            }
            if (TextUtils.equals("0", currentCateId)) {//点击全部
                tv_count.setText("搜索结果:" + newAuthorData.rows.size());
                ((NewSearchAuthorChapterAdapter) adapter).setNewData(newAuthorData.rows);
                return;
            }
            List<BaseAudioInfo> newResult = new ArrayList<>();
            for (BaseAudioInfo item : newAuthorData.rows) {
                if (TextUtils.equals(currentCateId, item.cate1_id)) {
                    newResult.add(item);
                }
            }
            tv_count.setText("搜索结果:" + newResult.size());
            ((NewSearchAuthorChapterAdapter) adapter).setNewData(newResult);
        }
    }

    private void changeSearch(String trim) {
        if (cacheSearchList == null) {
            return;
        }
        ArrayList<String> temp = new ArrayList<>();
        for (String item : cacheSearchList) {
            if (item.contains(trim)) {
                temp.add(item);
            }
        }
        searchMediaCacheAdapter.setNewData(temp);
    }

    private void getBookAllAudio(BaseAudioInfo item) {
        showProgressDialog("加载中...");
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("book", String.valueOf(item.cate3_id));
        map.put("author", item.author);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/multimedia3/audioList", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                dismissProgressDialog();
                NewAudioChapterData newAudioChapterData = new Gson().fromJson(t, NewAudioChapterData.class);
                if (newAudioChapterData == null || newAudioChapterData.rows == null || newAudioChapterData.rows.isEmpty()) {
                    showToast("章节无内容");
                    return;
                }
                int playIndex = 0;
                for (int i = 0; i < newAudioChapterData.rows.size(); i++) {
                    BaseAudioInfo baseAudioInfo = newAudioChapterData.rows.get(i);
                    if (baseAudioInfo.id == item.id) {
                        playIndex = i;
                        break;
                    }
                }
                playAudio(newAudioChapterData.rows, playIndex);
            }

            @Override
            public void onError(String t) {
                dismissProgressDialog();
            }
        });
    }

    private void playAudio(List<BaseAudioInfo> data, int position) {
        musicPlayerManager.startPlayMusic(data, Math.max(position, 0));
        musicPlayerManager.setCurrentAuthor(data.get(Math.max(position, 0)).author);
    }

    private NewSearchAudioBookAdapter searchAudioBookAdapter = new NewSearchAudioBookAdapter();
    private NewSearchAudioChapterAdapter searchAudioChapterAdapter = new NewSearchAudioChapterAdapter();
    private NewSearchAuthorChapterAdapter searchAuthorAdapter = new NewSearchAuthorChapterAdapter();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(String tag) {
        switch (tag) {
            case "audio_chapter_no_cache":
                if (fl_view != null) {
                    fl_view.stopAnimation();
//                    fl_view.hide();
                }
                break;
            case "on_audio_stop":
                if (fl_view != null) {
                    fl_view.hide();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(DownloadAudioEvent downloadAudioEvent) {

    }

    private void initData() {
        musicPlayerManager = MusicPlayerManager.getInstance().init(getApplicationContext());
        cateIds = (ArrayList<NewAudioBean.RowsBean>) getIntent().getSerializableExtra("audio_cate_data");
        if (cateIds != null) {
            int cateIndex = getIntent().getIntExtra("audio_cate_index", 0);
            if (cateIndex < 0 || cateIndex >= cateIds.size()) {
                currentCateId = "0";
            } else {
                currentCateId = cateIds.get(cateIndex).id;
            }
            searchAudioCateAdapter.setCurrentId(currentCateId);
            searchAudioCateAdapter.setNewData(cateIds);
        }
        try {
            cacheSearchList = (ArrayList<String>) PreferencesUtils.getObject(this, "search_audio_cache_str");
            searchMediaCacheAdapter.setNewData(cacheSearchList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        fl_view.onActivityDestroy();
    }

    private NewBookData newBookData;

    private void searchBook(String search) {
        showProgressDialog("搜索中...");
        searchAudioBookAdapter.setSearchText(search);
        hideInput();
        Map<String, String> map = new HashMap<>();
        map.put("search", search);
        map.put("level", "2");
        map.put("cate", currentCateId);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/multimedia3/cate", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                newBookData = new Gson().fromJson(t, NewBookData.class);
                tv_count.setVisibility(View.VISIBLE);
                rcv_list.setAdapter(searchAudioBookAdapter);
                if (newBookData == null || newBookData.rows == null) {
                    tv_count.setText("搜索结果:0");
                    searchAudioBookAdapter.setSearchText("");
                    searchAudioBookAdapter.setNewData(null);
                    return;
                }
                tv_count.setText("搜索结果:" + newBookData.rows.size());
                searchAudioBookAdapter.setNewData(newBookData.rows);
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
            }
        });
    }

    private SearchChapterBean newAuthorData;

    private void searchAuthor(String search) {
        showProgressDialog("搜索中...");
        searchAuthorAdapter.setSearchText(search);
        hideInput();
        Map<String, String> map = new HashMap<>();
        map.put("author", search);
        map.put("cate_id", currentCateId);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/multimedia3/searchAudioList", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                newAuthorData = new Gson().fromJson(t, SearchChapterBean.class);
                tv_count.setVisibility(View.VISIBLE);
                rcv_list.setAdapter(searchAuthorAdapter);
                if (newAuthorData == null || newAuthorData.rows == null) {
                    tv_count.setText("搜索结果:0");
                    searchAuthorAdapter.setSearchText("");
                    searchAuthorAdapter.setNewData(null);
                    return;
                }
                tv_count.setText("搜索结果:" + newAuthorData.rows.size());
                searchAuthorAdapter.setNewData(newAuthorData.rows);
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
            }
        });
    }

    private SearchChapterBean newChapterData;

    private void searchChapter(String search) {
        showProgressDialog("加载中...");
        searchAudioChapterAdapter.setSearchText(search);
        hideInput();
        Map<String, String> map = new HashMap<>();
        map.put("chapter", search);
        map.put("cate_id", currentCateId);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/multimedia3/searchAudioList", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                newChapterData = new Gson().fromJson(t, SearchChapterBean.class);
                tv_count.setVisibility(View.VISIBLE);
                rcv_list.setAdapter(searchAudioChapterAdapter);
                if (newChapterData == null || newChapterData.rows == null) {
                    tv_count.setText("搜索结果:0");
                    searchAudioChapterAdapter.setSearchText("");
                    searchAudioChapterAdapter.setNewData(null);
                    return;
                }
                tv_count.setText("搜索结果:" + newChapterData.rows.size());
                searchAudioChapterAdapter.setNewData(newChapterData.rows);
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
            }
        });
    }

    private void clickSearch(String search) {
        rcv_cache_search.setVisibility(View.GONE);
        if (cacheSearchList == null) {
            cacheSearchList = new ArrayList<>();
        }
        if (!cacheSearchList.contains(search)) {
            cacheSearchList.add(search);
        }
        try {
            PreferencesUtils.putObject(this, "search_audio_cache_str", cacheSearchList);
        } catch (IOException e) {
            LogUtil.error("Exception", e);
        }
        switch (type) {
            case "1":
                searchBook(search);
                break;
            case "2":
                searchChapter(search);
                break;
            case "3":
                searchAuthor(search);
                break;
        }
    }

}
