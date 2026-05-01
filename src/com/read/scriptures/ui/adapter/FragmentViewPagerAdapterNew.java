package com.read.scriptures.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import java.util.List;

public class FragmentViewPagerAdapterNew extends FragmentPagerAdapter implements OnPageChangeListener {
    private int currentPageIndex = 0;
    private List<Fragment> fragments;
    private OnExtraPageChangeListener onExtraPageChangeListener;
    private List<String> titles;

    public FragmentViewPagerAdapterNew(FragmentManager fm, List<Fragment> al, List<String> titleList) {
        super(fm);
        this.fragments = al;
        titles = titleList;
    }


    public int getCount() {
        return this.fragments.size();
    }


    public OnExtraPageChangeListener getOnExtraPageChangeListener() {
        return this.onExtraPageChangeListener;
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);//页卡标题
    }


    public void onPageScrollStateChanged(int var1) {
        if (this.onExtraPageChangeListener != null) {
            this.onExtraPageChangeListener.onExtraPageScrollStateChanged(var1);
        }

    }

    public void onPageScrolled(int var1, float var2, int var3) {
        if (this.onExtraPageChangeListener != null) {
            this.onExtraPageChangeListener.onExtraPageScrolled(var1, var2, var3);
        }

    }

    public void onPageSelected(int var1) {
        ((Fragment)this.fragments.get(this.currentPageIndex)).onPause();
        if (((Fragment)this.fragments.get(var1)).isAdded()) {
            ((Fragment)this.fragments.get(var1)).onResume();
        }

        this.currentPageIndex = var1;
        if (this.onExtraPageChangeListener != null) {
            this.onExtraPageChangeListener.onExtraPageSelected(var1);
        }

    }

    public void setOnExtraPageChangeListener(OnExtraPageChangeListener var1) {
        this.onExtraPageChangeListener = var1;
    }

    public static class OnExtraPageChangeListener {
        public OnExtraPageChangeListener() {
        }

        public void onExtraPageScrollStateChanged(int var1) {
        }

        public void onExtraPageScrolled(int var1, float var2, int var3) {
        }

        public void onExtraPageSelected(int var1) {
        }
    }
}
