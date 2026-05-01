package com.read.scriptures.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.SpliTab;

import java.util.List;

/**
 * Created by Administrator. Datetime: 2015/7/2. Email: lgmshare@mgail.com
 */
public class CategorySpiriGridAdapter extends EIBaseAdapter<SpliTab> {

    private Context mContext;
    private int mIndex;

    public CategorySpiriGridAdapter(Context context) {
        super(context);
    }

    public CategorySpiriGridAdapter(Context context, List<SpliTab> list) {
        super(context, list);
        mContext = context;
    }

    public void setIndex(int index) {
        if (index != mIndex) {
            this.mIndex = index;
            notifyDataSetChanged();
        }
    }

    public void setIndex(AdapterView<?> listView, int index) {
        if (index != mIndex) {
            updateView(listView, index, mIndex);
            this.mIndex = index;
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.adapter_home_category_item, null);
        }
        SpliTab item = getItem(position);
        TextView title = (TextView) convertView.findViewById(R.id.tv_title);
        title.setText(item.getCateName());
        if (position == mIndex) {
            title.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else {
            title.setTextColor(mContext.getResources().getColor(R.color.gray_4c4c4c));
        }
        return convertView;
    }

    public void updateView(AdapterView<?> listView, int itemIndex, int lastItemIndex) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (itemIndex <= lastVisiblePosition && itemIndex >= firstVisiblePosition) {
            View curView = listView.getChildAt(itemIndex - firstVisiblePosition);
            TextView curTitle = (TextView) curView.findViewById(R.id.tv_title);
            if (curTitle != null) {
                curTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
            }
        }
        if (lastItemIndex <= lastVisiblePosition && lastItemIndex >= firstVisiblePosition) {
            View lastView = listView.getChildAt(lastItemIndex - firstVisiblePosition);
            TextView lastTitle = (TextView) lastView.findViewById(R.id.tv_title);
            if (lastTitle != null) {
                lastTitle.setTextColor(mContext.getResources().getColor(R.color.gray_4c4c4c));
            }
        }
    }
}
