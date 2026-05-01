package com.read.scriptures.video;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.read.scriptures.R;
import com.read.scriptures.bean.VideoCateBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.FragmentViewPagerAdapterNew;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.view.VideoCacheView;
import com.read.scriptures.widget.CustomViewPager;
import com.read.scriptures.widget.PagerSlidingTabStrip;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class VideoMainActivity extends BaseActivity implements CustomViewPager.ScollAbleArea {
    private String assetsVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_video);
        StatusBarUtils.initMainColorStatusBar(this);
        initView();
        initData();
        //获取缓存资源版本
        assetsVersion = PreferencesUtils.getString(this, "assets_is_update", "1.0.0");
        tv_version.setText("v" + assetsVersion);
    }

    private PagerSlidingTabStrip mIndicator;
    private CustomViewPager mPager;
    private FragmentViewPagerAdapterNew mAdapter;
    private TextView tv_no_data;
    private TextView tv_version;
    private VideoCacheView vcv_cache;

    private void initView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mPager = findViewById(R.id.pager);
        tv_no_data = findViewById(R.id.tv_no_data);
        tv_version = findViewById(R.id.tv_version);
        vcv_cache = findViewById(R.id.vcv_cache);
        mPager.setArea(this);
        // 设置viewpager内部页面之间的间距
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.common_margin_8));
        // 设置viewpager内部页面间距的drawable
        mPager.setPageMarginDrawable(R.color.gray_f5f5f5);
        mPager.setCurrentItem(0);
        mPager.setNoScoll(true);
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setTextColor(getResources().getColor(R.color.white_alph70));
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
                Intent intent = new Intent(VideoMainActivity.this, SearchVideoActivity.class);
                intent.putExtra("video_cate_data", cateIds);
                intent.putExtra("video_cate_index", currentItem);
                startActivity(intent);
            }
        });

        findViewById(R.id.iv_right1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideoMainActivity.this, CollectVideoListActivity.class));
            }
        });

        findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private ArrayList<VideoCateBean.RowsBean> cateIds = new ArrayList<>();

    private void initData() {
        ArrayList<String> fragmentTitle = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        showProgressDialog("加载中...");
        NetUtil.getCache(ZConfig.SERVICE_URL + "/api/v1/multimedia/videogrouping", new NetUtil.CallBack() {
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
                VideoCateBean videoCateBean = new Gson().fromJson(t, VideoCateBean.class);
                fragmentTitle.add("推荐");
                fragments.add(new InviteVideoFragment());
                VideoCateBean.RowsBean inviteBean = new VideoCateBean.RowsBean();
                inviteBean.cate_name = "全部";
                inviteBean.id = "0";
                cateIds.add(inviteBean);
                for (VideoCateBean.RowsBean rowsBean : videoCateBean.rows) {
                    fragmentTitle.add(rowsBean.cate_name);
                    fragments.add(VideoFragment.getInstance(rowsBean.id));
                    cateIds.add(rowsBean);
                }
                mAdapter = new FragmentViewPagerAdapterNew(getSupportFragmentManager(), fragments, fragmentTitle);
                mPager.setOffscreenPageLimit(fragments.size());
                mPager.setAdapter(mAdapter);
                mIndicator.setViewPager(mPager);
                //获取用户点到哪一个分类
                int video_cate_index = PreferencesUtils.getInt(VideoMainActivity.this, "VIDEO_CATE_INDEX", 0);
                if (video_cate_index > 0 && video_cate_index < fragments.size()) {
                    mPager.setCurrentItem(video_cate_index);
                }

            }

            @Override
            public void onError(String t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                if (!NetUtil.isNetWorkAvailable(VideoMainActivity.this)) {
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记录用户点到哪一个分类
        PreferencesUtils.putInt(this, "VIDEO_CATE_INDEX", mPager.getCurrentItem());
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(String tag) {
        switch (tag) {
            case "video_cache_refresh":
                vcv_cache.initData(this);
                break;
        }
    }

    @Override
    public int getScollY() {
        return 0;
    }
}
