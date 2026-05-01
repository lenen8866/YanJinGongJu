package com.read.scriptures.ui.adapter;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator.
 * Datetime: 2015/7/2.
 * Email: lgmshare@mgail.com
 */
public class ViewPagerAdapter extends PagerAdapter{
    private List<View> list;
    /**
     * title 对应于每个大Fragment的小Fragment
     */
    private String[] titles;
    public ViewPagerAdapter(List<View> list, String[] titles) {
        this.list = list;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        if (list != null && list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list.get(position));
        return list.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position % titles.length];
    }
}
