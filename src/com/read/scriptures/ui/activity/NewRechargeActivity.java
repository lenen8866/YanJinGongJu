package com.read.scriptures.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.read.scriptures.R;
import com.read.scriptures.bean.DonationRecordBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.FragmentViewPagerAdapterNew;
import com.read.scriptures.ui.fragment.AboutUsFragment;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.TimeUtils;
import com.read.scriptures.widget.CustomViewPager;
import com.read.scriptures.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRechargeActivity extends BaseActivity implements CustomViewPager.ScollAbleArea {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_new_recharge);
        initView();
    }

    private PagerSlidingTabStrip mIndicator;
    private CustomViewPager mPager;
    private FragmentViewPagerAdapterNew mAdapter;
    private TextView tv_recharge_list;

    private void initView() {
        StatusBarUtils.initMainColorStatusBar(this);
        mPager = findViewById(R.id.pager);
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

        tv_recharge_list = findViewById(R.id.tv_recharge_list);
        tv_recharge_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewRechargeActivity.this, DonationRecordActivity.class));
            }
        });


        findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        String time = TimeUtils.formatTime(new Date());
        getData(time);
    }

    private void initData() {
        ArrayList<String> fragmentTitle = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        fragmentTitle.add("详情");
        fragmentTitle.add("关于我们");
        fragments.add(new RechargeFragment());
        fragments.add(new AboutUsFragment());
        mAdapter = new FragmentViewPagerAdapterNew(getSupportFragmentManager(), fragments, fragmentTitle);
        mPager.setOffscreenPageLimit(fragments.size());
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);
    }

    @Override
    public void finish() {
        super.finish();
        hideInput();
    }

    @Override
    public int getScollY() {
        return 0;
    }

    private void getData(String time) {
        Map<String, String> map = new HashMap<>();
        map.put("month", time);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/paying/donation", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                DonationRecordBean donationRecordBean = new Gson().fromJson(t, DonationRecordBean.class);
                if (donationRecordBean != null && donationRecordBean.rows != null && !donationRecordBean.rows.isEmpty()) {
                    tv_recharge_list.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
