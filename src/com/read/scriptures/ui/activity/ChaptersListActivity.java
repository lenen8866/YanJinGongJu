package com.read.scriptures.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.bean.CollectBean;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.ChapterDatabaseHepler;
import com.read.scriptures.db.DatabaseManager;
import com.read.scriptures.db.VolumeDatabaseHepler;
import com.read.scriptures.event.LoginOutEvent;
import com.read.scriptures.event.RefreshChapterListEvent;
import com.read.scriptures.manager.HomeDataManager;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.model.Volume;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.ChapterListViewAdapter;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.DisplayUtil;
import com.read.scriptures.util.GsonUtils;
import com.read.scriptures.util.MTextUtil;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.view.AudioPlayingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedHashMap;
import java.util.List;

public class ChaptersListActivity extends BaseActivity {

    private GridView mListView;
    private ChapterListViewAdapter mAdapter;
    private Volume mVolume;
    private String categoryName;
    private TextView title;
    private TextView year;
    private TextView chapter;
    private ImageView ivLeft;
    private ImageView ze_icon_first;
    private ImageView ze_icon_last;
    private CheckBox cbShowType;
    private CheckBox cbCollect;//收藏

    private AudioPlayingView fl_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);
        StatusBarUtils.initMainColorStatusBar(this);
        initExtras();
        initViews();
        initData();
    }

    private void initExtras() {
        mVolume = getIntent().getParcelableExtra(BundleConstants.PARAM_VOLUME);
        categoryName = getIntent().getStringExtra(BundleConstants.PARAM_CATEGORY_NAME);
    }

    private void initViews() {
        fl_view = findViewById(R.id.fl_view);
        title = findViewById(R.id.tv_title);
        year = findViewById(R.id.tv_year);
        chapter = findViewById(R.id.tv_chapter);
        cbShowType = findViewById(R.id.cb_show_type);
        cbCollect = findViewById(R.id.cb_collect);
        mListView = findViewById(R.id.listview);
        ivLeft = findViewById(R.id.iv_left);
        ze_icon_first = findViewById(R.id.ze_icon_first);
        ze_icon_last = findViewById(R.id.ze_icon_last);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void initData() {
        try {
            String content = mVolume.getVolName().replaceAll("\\((.*?)\\)", "").replaceAll("\\[(.*?)\\]", "")
                    .replaceAll("\\{(.*?)\\}", "");
            title.setText(content);
            if (title.getText() != null && !TextUtils.isEmpty(title.getText().toString())) {
                String strTitle = title.getText().toString().trim();
                if (MTextUtil.isContainChinese(strTitle) && strTitle.contains("E")) {
                    strTitle = MTextUtil.changeEletter(strTitle);

                    title.setText(strTitle);
                    if (strTitle.length() > 15) {
                        ze_icon_last.setVisibility(View.VISIBLE);
                    } else {
                        ze_icon_first.setVisibility(View.VISIBLE);
                    }
                }
            }
            String author = CharUtils.match("\\((.*?)\\)", mVolume.getVolName());
            if (!TextUtils.isEmpty(author)) {
                author = author.replaceAll("\\(", "").replaceAll("\\)", "");
                year.setText(author);
            } else {
                year.setText("");
            }
        } catch (Exception e) {
            title.setText(mVolume.getVolName());
        }
        List<Chapter> list = HomeDataManager.getInstance().getChapterListByVolumeId(mVolume.getId());
        if (list != null && list.size() > 0) {
            Chapter chapter1 = list.get(list.size() - 1);
            String name = chapter1.getName();
            if (name.contains("章")) {
                String count = name.substring(name.indexOf("第") + 1, name.indexOf("章"));
                chapter.setText("共" + count + "章");
            } else {
                chapter.setText("共" + list.size() + "章");
            }
        } else {
            chapter.setText("共0章");
        }

        //获取展示方式
        boolean isList = SharedUtil.getBoolean(PreferenceConfig.Preference_chapter_list_type, SharedUtil.getBoolean(PreferenceConfig.Preference_home_list_type, true));
        cbShowType.setChecked(isList);
        chanageListShowType(isList);

        mAdapter = new ChapterListViewAdapter(this, list, isList, mListView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position >= mAdapter.getList().size()) {
                    return;
                }
                clickListViewItem(position);
            }
        });

        cbShowType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedUtil.putBoolean(PreferenceConfig.Preference_chapter_list_type, isChecked);
                chanageListShowType(isChecked);
                mAdapter.setShowList(isChecked);
                int progress = PreferenceConfig.getReadingProgress(getApplicationContext(), mVolume.getId());
                if (progress >= 0 && progress <= mAdapter.getCount() - 1) {
                    mListView.setSelection(progress);
                }
            }
        });

        boolean isExitCollect = DatabaseManager.getHistoryHelper().isExistCollect(mVolume.getVolName());
        cbCollect.setChecked(isExitCollect);
        cbCollect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //收藏
                    CollectBean collectBean = new CollectBean();
                    collectBean.setCategoryId(mVolume.getCategoryId());
                    collectBean.setParentId(mVolume.getParentId());
                    collectBean.setVolumeId(mVolume.getId());
                    collectBean.setVolumeName(mVolume.getVolName());
                    collectBean.setChapter(mVolume.getIntro());
                    if (!DatabaseManager.getHistoryHelper().isExistCollect(mVolume.getVolName())) {//不存在才添加，否则更新
                        DatabaseManager.getHistoryHelper().addCollect(collectBean);
                        //收藏成功
                        showToast(getResources().getString(R.string.collect_success));
                    } else {
                        DatabaseManager.getHistoryHelper().updateCollect(collectBean);
                    }


                } else {
                    //取消收藏
                    if (DatabaseManager.getHistoryHelper().isExistCollect(mVolume.getVolName())) {//存在删除
                        DatabaseManager.getHistoryHelper().deleteCollect(String.valueOf(mVolume.getId()));
                        showToast(getResources().getString(R.string.collect_cancel_success));
                    }
                }
            }
        });
    }

    private void chanageListShowType(boolean isList) {
        if (isList) {
            mListView.setNumColumns(1);
            mListView.setHorizontalSpacing(0);
            mListView.setVerticalSpacing((int) DisplayUtil.dp2px(this, 1));
        } else {
            mListView.setNumColumns(3);
            mListView.setHorizontalSpacing((int) DisplayUtil.dp2px(this, 1));
            mListView.setVerticalSpacing((int) DisplayUtil.dp2px(this, 1));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        int progress = PreferenceConfig.getReadingProgress(getApplicationContext(), mVolume.getId());
        mAdapter.setProgress(progress);
        if (progress >= 0 && progress <= mAdapter.getCount() - 1) {
            mListView.setSelection(progress);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    // 列表点击后的响应
    private Chapter checkedChapter;

    public void clickListViewItem(int index) {
        checkedChapter = mAdapter.getItem(index);
        Thread thread = new Thread() {
            @Override
            public void run() {
                LinkedHashMap<String, String> annMap = new ChapterDatabaseHepler(ChaptersListActivity.this)
                        .getChapterAnnotationList(mVolume.getId(), checkedChapter.getShowName());
                Message message = new Message();
                message.what = 0;
                message.obj = annMap;
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    //跳转到阅读页面
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                LinkedHashMap<String, String> annMap = (LinkedHashMap<String, String>) msg.obj;
                checkedChapter.setVolumeName(mVolume.getVolName().replaceAll("^\\d{1,}-", ""));
                Bundle bundle = new Bundle();
                bundle.putParcelable(BundleConstants.PARAM_CHAPTER, checkedChapter);
                bundle.putString(BundleConstants.ANN_MAP, GsonUtils.objectToStr(annMap));
                bundle.putString(BundleConstants.PARAM_CATEGORY, categoryName);
                Intent intent=new Intent();
                intent.setClass(ChaptersListActivity.this,ChapterReaderActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(RefreshChapterListEvent refreshChapterListEvent) {
        int volumeId = refreshChapterListEvent.getVolumeId();
        List<Volume> volumes = new VolumeDatabaseHepler(this).getVolumeById(volumeId);
        if (volumes != null && !volumes.isEmpty()) {
            mVolume = volumes.get(0);
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        fl_view.onActivityDestroy();
    }

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
    public void UpdateUI(LoginOutEvent loginOutEvent) {
        this.finish();
    }


}