package com.read.scriptures.audio;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicPlayerEventListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.R;
import com.read.scriptures.bean.NewAudioBean;
import com.read.scriptures.bean.NewAudioChapterData;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.MainActivity;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.FragmentViewPagerAdapterNew;
import com.read.scriptures.util.AnimationUtils;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.view.AudioPlayingView;
import com.read.scriptures.widget.CustomViewPager;
import com.read.scriptures.widget.PagerSlidingTabStrip;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAudioActivity extends BaseActivity implements CustomViewPager.ScollAbleArea {

    public static final String AUDIO_CACHE_DATA = "Audio_CACHE_DATA";
    public static final String AUDIO_IN_CATE_INDEX = "AUDIO_IN_CATE_INDEX";
    public static final String AUDIO_IS_PLAYED = "AUDIO_IS_PLAYED";

    private String assetsVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_audio);
        StatusBarUtils.initMainColorStatusBar(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initData();
        assetsVersion = PreferencesUtils.getString(this, "assets_is_update", "1.0.0");
        tv_version.setText("v" + assetsVersion);
    }

    private PagerSlidingTabStrip mIndicator;
    private CustomViewPager mPager;
    private FragmentViewPagerAdapterNew mAdapter;
    private AudioPlayingView fl_view;
    private TextView tv_no_data;
    private TextView tv_version;
    private View view_target;
    private ViewGroup cl_main;

    private void initView() {
        mPager = findViewById(R.id.pager);
        fl_view = findViewById(R.id.fl_view);
        tv_no_data = findViewById(R.id.tv_no_data);
//        tv_title = findViewById(R.id.tv_title);
        mPager.setArea(this);
        // 设置viewpager内部页面之间的间距
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.common_margin_8));
        // 设置viewpager内部页面间距的drawable
        mPager.setPageMarginDrawable(R.color.gray_f5f5f5);
        mPager.setNoScoll(true);
        mIndicator = findViewById(R.id.indicator);
        tv_version = findViewById(R.id.tv_version);
        cl_main = findViewById(R.id.cl_main);
        view_target = findViewById(R.id.view_target);
        mIndicator.setTextColor(getResources().getColor(R.color.white_alph70));
        mIndicator.setShouldExpand(false);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                mPager.setCurrentItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        findViewById(R.id.iv_right2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mPager.getCurrentItem();
                Intent intent = new Intent(NewAudioActivity.this, SearchAudioActivity.class);
                intent.putExtra("audio_cate_data", cateIds);
                intent.putExtra("audio_cate_index", currentItem);
                startActivity(intent);
            }
        });

        findViewById(R.id.iv_right1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewAudioActivity.this, CollectAudioListActivity.class));
            }
        });

        findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public ArrayList<NewAudioBean.RowsBean> cateIds = new ArrayList<>();

    private void initData() {
        ArrayList<String> fragmentTitle = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        showProgressDialog("加载中...");
        NetUtil.getCache(ZConfig.SERVICE_URL + "/api/v1/multimedia3/cate", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                cateIds.clear();
                tv_no_data.setVisibility(View.GONE);
                fragmentTitle.clear();
                fragments.clear();
                fragmentTitle.add("推荐");
                fragments.add(new InviteAudioFragment());
                NewAudioBean audioCateData = new Gson().fromJson(t, NewAudioBean.class);
                NewAudioBean.RowsBean inviteBean = new NewAudioBean.RowsBean();
                inviteBean.name = "全部";
                inviteBean.id = "0";
                cateIds.add(inviteBean);
                for (NewAudioBean.RowsBean rowsBean : audioCateData.rows) {
                    fragmentTitle.add(rowsBean.name);
                    fragments.add(NewAudioFragment.getInstance(rowsBean.id));
                    cateIds.add(rowsBean);
                }
                mAdapter = new FragmentViewPagerAdapterNew(getSupportFragmentManager(), fragments, fragmentTitle);
                mPager.setAdapter(mAdapter);
                mIndicator.setViewPager(mPager);
//                mPager.setOffscreenPageLimit(fragments.size());
                int audio_cate_index = PreferencesUtils.getInt(NewAudioActivity.this, "AUDIO_CATE_INDEX", 0);
                if (audio_cate_index >= 0 && audio_cate_index < fragments.size()) {
                    mPager.setCurrentItem(audio_cate_index);
                    mPager.setOffscreenPageLimit(fragments.size());
                }
            }

            @Override
            public void onError(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                if (!NetUtil.isNetWorkAvailable(NewAudioActivity.this)) {
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            }
        });

        Intent intent = getIntent();
        ArrayList<BaseAudioInfo> cacheAudioData = null;
        if(intent.getSerializableExtra(AUDIO_CACHE_DATA) != null){
            cacheAudioData = (ArrayList<BaseAudioInfo>) intent.getSerializableExtra(AUDIO_CACHE_DATA);
        }else{
            try {
                cacheAudioData = (ArrayList<BaseAudioInfo>) PreferencesUtils.getObject(NewAudioActivity.this,NewAudioActivity.AUDIO_CACHE_DATA);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        int audioInCateIndex = intent.getIntExtra(AUDIO_IN_CATE_INDEX, 0);
        boolean audioIsPlayed = intent.getBooleanExtra(AUDIO_IS_PLAYED, false);
        if (cacheAudioData != null && !audioIsPlayed) {//有缓存的音频
            MusicPlayerManager.getInstance().updateMusicPlayerData(cacheAudioData, audioInCateIndex);
            BaseAudioInfo baseAudioInfo = cacheAudioData.get(audioInCateIndex);
            fl_view.show();
            fl_view.setData(baseAudioInfo);
            long currentTime = parseLong(baseAudioInfo.playDuration) / 1000;
            long totalTime = parseLong(baseAudioInfo.duration);
            if (totalTime == 0) {
                totalTime = 1;
            }
            fl_view.setProgress(((float) currentTime / totalTime) * 100, totalTime, currentTime);
        }

    }

    private long parseLong(String str) {
        if (str == null || TextUtils.isEmpty(str.trim())) {
            return 0L;
        }
        return Long.parseLong(str);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PreferencesUtils.putInt(this, "AUDIO_CATE_INDEX", mPager.getCurrentItem());
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

    public void startAnim(View beginView) {
        AnimationUtils.addShopCartAnimation(cl_main, beginView, view_target, 4);
    }

    @Override
    public int getScollY() {
        return 0;
    }

}
