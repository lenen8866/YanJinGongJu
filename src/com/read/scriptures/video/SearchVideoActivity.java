package com.read.scriptures.video;

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
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.read.scriptures.R;
import com.read.scriptures.bean.SearchVideoBean;
import com.read.scriptures.bean.VideoBookBean;
import com.read.scriptures.bean.VideoCateBean;
import com.read.scriptures.bean.VideoListBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.SearchMediaCacheAdapter;
import com.read.scriptures.util.LogUtil;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.widget.ClearEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchVideoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_search_video);
        StatusBarUtils.initMainColorStatusBar(this);
        initView();
        initData();
    }

    private ArrayList<VideoCateBean.RowsBean> cateIds;
    private String currentCateId;

    private void initData() {
        try {
            cacheSearchList = (ArrayList<String>) PreferencesUtils.getObject(this, "search_video_cache_str");
            searchMediaCacheAdapter.setNewData(cacheSearchList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cateIds = (ArrayList<VideoCateBean.RowsBean>) getIntent().getSerializableExtra("video_cate_data");
        if (cateIds != null) {
            int cateIndex = getIntent().getIntExtra("video_cate_index", 0);
            if (cateIndex < 0 || cateIndex >= cateIds.size()) {
                currentCateId = "0";
            } else {
                currentCateId = cateIds.get(cateIndex).id;
            }
            searchVideoCateAdapter.setCurrentId(currentCateId);
            searchVideoCateAdapter.setNewData(cateIds);
        }
    }

    private ClearEditText et_search;
    private TextView btn_search;
    private TextView tv_count;
    private RadioGroup radio_group;
    private RecyclerView rcv_list;

    private int searchType = 1;//搜索书籍  1 搜索视频
    private SearchVideoItemAdapter searchVideoItemAdapter;

    private VideoListAdapter videoListAdapter;

    private RecyclerView rcv_cache_search;
    private RecyclerView rcv_cate;

    private SearchMediaCacheAdapter searchMediaCacheAdapter;
    private ArrayList<String> cacheSearchList = new ArrayList<>();

    private SearchVideoCateAdapter searchVideoCateAdapter;

    private void initView() {
        et_search = findViewById(R.id.et_search);
        btn_search = findViewById(R.id.btn_search);
        radio_group = findViewById(R.id.radio_group);
        tv_count = findViewById(R.id.tv_count);
        rcv_list = findViewById(R.id.rcv_list);
        rcv_cache_search = findViewById(R.id.rcv_cache_search);
        rcv_cate = findViewById(R.id.rcv_cate);

        rcv_cate.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        searchVideoCateAdapter = new SearchVideoCateAdapter();
        rcv_cate.setAdapter(searchVideoCateAdapter);

        searchVideoCateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                searchVideoCateAdapter.setCurrentId(currentCateId = searchVideoCateAdapter.getItem(position).id);
                formatSearchResult(currentCateId);
            }
        });


        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        searchVideoItemAdapter = new SearchVideoItemAdapter();

        videoListAdapter = new VideoListAdapter();

        rcv_cache_search.setLayoutManager(new LinearLayoutManager(this));
        searchMediaCacheAdapter = new SearchMediaCacheAdapter();
        rcv_cache_search.setAdapter(searchMediaCacheAdapter);

        radio_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcv_cache_search.setVisibility(View.GONE);
            }
        });
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
                    et_search.setText(str);
                    et_search.setSelection(et_search.length());
                }
            }
        });
        et_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (TextUtils.isEmpty(et_search.getText().toString().trim())) {//为空则显示所有的
                    searchMediaCacheAdapter.setNewData(cacheSearchList);
                } else {
                    changeSearch(et_search.getText().toString().trim());
                }
                ViewGroup.LayoutParams layoutParams = rcv_cache_search.getLayoutParams();
                layoutParams.width = et_search.getWidth();
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
                    case R.id.rb_1:
                        searchType = 0;
                        break;
                    case R.id.rb_2:
                        searchType = 1;
                        break;
                }
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcv_cache_search.setVisibility(View.GONE);
                String trim = et_search.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    return;
                }
                if (searchType == 1) {
                    searchVideo(trim);
                } else {
                    searchVideoBook(trim);
                }
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            rcv_cache_search.setVisibility(View.GONE);
                            String trim = et_search.getText().toString().trim();
                            if (TextUtils.isEmpty(trim)) {
                                return false;
                            }
                            if (searchType == 1) {
                                searchVideo(trim);
                            } else {
                                searchVideoBook(trim);
                            }
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
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

        searchVideoItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                rcv_cache_search.setVisibility(View.GONE);
                if (position < 0) {
                    return;
                }
                SearchVideoBean.RowsDTO item = searchVideoItemAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                Intent intent = new Intent(SearchVideoActivity.this, VideoPlayActivity.class);
                intent.putExtra("VIDEO_BOOK_ID", item.cate3_id);
                intent.putExtra("VIDEO_ITEM_ID", item.id);
                intent.putExtra("VIDEO_BOOK_COVER", !TextUtils.isEmpty(item.video_cover) ? item.video_cover : item.image);
                intent.putExtra(VideoPlayActivity.VIDEO_CATE, "");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        videoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                rcv_cache_search.setVisibility(View.GONE);
                if (position < 0) {
                    return;
                }
                VideoBookBean.RowsBean item = videoListAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                Intent intent = new Intent(SearchVideoActivity.this, VideoPlayActivity.class);
                intent.putExtra("VIDEO_BOOK_ID", item.id);
                intent.putExtra("VIDEO_BOOK_COVER", item.cate_image);
                intent.putExtra(VideoPlayActivity.VIDEO_CATE, "");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        et_search.post(new Runnable() {
            @Override
            public void run() {
                showInput(et_search);
            }
        });
    }

    private void formatSearchResult(String currentCateId) {
        RecyclerView.Adapter adapter = rcv_list.getAdapter();
        if (adapter instanceof SearchVideoItemAdapter) {
            if (videoListBean == null || videoListBean.rows == null || videoListBean.rows.isEmpty()) {
                return;
            }
            if (TextUtils.equals("0", currentCateId)) {//点击全部
                tv_count.setText("搜索结果:" + videoListBean.rows.size());
                ((SearchVideoItemAdapter) adapter).setNewData(videoListBean.rows);
                return;
            }
            List<SearchVideoBean.RowsDTO> newResult = new ArrayList<>();
            for (SearchVideoBean.RowsDTO item : videoListBean.rows) {
                if (TextUtils.equals(currentCateId, item.cate1_id)) {
                    newResult.add(item);
                }
            }

            tv_count.setText("搜索结果:" + newResult.size());
            ((SearchVideoItemAdapter) adapter).setNewData(newResult);
        } else if (adapter instanceof VideoListAdapter) {
            if (videoCateBean == null || videoCateBean.rows == null || videoCateBean.rows.isEmpty()) {
                return;
            }
            if (TextUtils.equals("0", currentCateId)) {//点击全部
                tv_count.setText("搜索结果:" + videoCateBean.rows.size());
                ((VideoListAdapter) adapter).setNewData(videoCateBean.rows);
                return;
            }
            List<VideoBookBean.RowsBean> newResult = new ArrayList<>();
            for (VideoBookBean.RowsBean item : videoCateBean.rows) {
                if (TextUtils.equals(currentCateId, item.cate1_id)) {
                    newResult.add(item);
                }
            }
            tv_count.setText("搜索结果:" + newResult.size());
            ((VideoListAdapter) adapter).setNewData(newResult);
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

    private VideoBookBean videoCateBean;

    private void searchVideoBook(String searchText) {
        saveData(searchText);
        hideInput();
        showProgressDialog("搜索中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("search", searchText);
        map.put("type", "all");
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/multimedia/videogrouping", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                videoCateBean = new Gson().fromJson(t, VideoBookBean.class);
                tv_count.setVisibility(View.VISIBLE);
                rcv_list.setAdapter(videoListAdapter);
                if (videoCateBean.rows == null || videoCateBean.rows.isEmpty()) {
                    tv_count.setText("搜索结果:0");
                    videoListAdapter.setSearchText("");
                    videoListAdapter.setNewData(null);
                    return;
                }
                tv_count.setText("搜索结果:" + videoCateBean.rows.size());
                videoListAdapter.setSearchText(searchText);
                videoListAdapter.setNewData(videoCateBean.rows);
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

    private SearchVideoBean videoListBean;

    private void searchVideo(String searchText) {
        saveData(searchText);
        hideInput();
        showProgressDialog("搜索中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("chapter", searchText);
        map.put("cate_id", TextUtils.equals(currentCateId, "0") ? "all" : currentCateId);
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/multimedia3/searchVideoList", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                videoListBean = new Gson().fromJson(t, SearchVideoBean.class);
                tv_count.setVisibility(View.VISIBLE);
                rcv_list.setAdapter(searchVideoItemAdapter);
                if (videoListBean.rows == null || videoListBean.rows.isEmpty()) {
                    tv_count.setText("搜索结果:0");
                    searchVideoItemAdapter.setSearchText("");
                    searchVideoItemAdapter.setNewData(null);
                    return;
                }
                tv_count.setText("搜索结果:" + videoListBean.rows.size());
                searchVideoItemAdapter.setSearchText(searchText);
                searchVideoItemAdapter.setNewData(videoListBean.rows);
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

    private void saveData(String search) {
        rcv_cache_search.setVisibility(View.GONE);
        if (cacheSearchList == null) {
            cacheSearchList = new ArrayList<>();
        }
        if (!cacheSearchList.contains(search)) {
            cacheSearchList.add(search);
        }
        try {
            PreferencesUtils.putObject(this, "search_video_cache_str", cacheSearchList);
        } catch (IOException e) {
            LogUtil.error("Exception", e);
        }
    }
}
