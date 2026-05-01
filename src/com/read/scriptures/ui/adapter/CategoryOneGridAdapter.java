package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.Category;

import java.util.List;

/**
 * Created by Administrator.
 * Datetime: 2015/7/2.
 * Email: lgmshare@mgail.com
 */
public class CategoryOneGridAdapter extends EIBaseAdapter<Category> {

    private Context mContext;
    private int mIndex;
    private Integer[] labelCounts;

    public CategoryOneGridAdapter(Context context) {
        super(context);
        mContext = context;
    }

    public CategoryOneGridAdapter(Context context, List<Category> list) {
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

    public void setLabelCount(Integer[] labelCounts) {
        this.labelCounts = labelCounts;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.adapter_home_category_item, null);
        }

        FrameLayout fl_count = convertView.findViewById(R.id.fl_count);
        TextView tv_count = convertView.findViewById(R.id.tv_count);

        if (labelCounts != null && labelCounts.length > 0) {
            if (labelCounts.length > position && labelCounts[position] > 0) {
                fl_count.setVisibility(View.VISIBLE);
                if (labelCounts[position] > 999) {
                    tv_count.setText("999");
                } else {
                    tv_count.setText(labelCounts[position] + "");
                }

            } else {
                fl_count.setVisibility(View.INVISIBLE);
            }
        } else {
            fl_count.setVisibility(View.INVISIBLE);
        }

        Category item = getItem(position);
        TextView title = convertView.findViewById(R.id.tv_title);
        String name = item.getCateName();
        int where = name.indexOf("-");
        name = name.substring(where + 1, name.length());
        title.setText(name);
        if (position == mIndex) {
            title.setTextColor(mContext.getResources().getColor(R.color.orange));
            setTextBold(true, title);
            setHighLightTag(true, fl_count);
        } else {
            title.setTextColor(mContext.getResources().getColor(R.color.gray_4c4c4c));
            setTextBold(false, title);
            setHighLightTag(false, fl_count);
        }
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick((AdapterView<?>) parent, v, position);
            }
        });
        return convertView;
    }


    private void setTextBold(boolean flag, TextView title) {
        TextPaint paint = title.getPaint();
        paint.setFakeBoldText(flag);
    }

    private void setHighLightTag(boolean flag, FrameLayout fl_count) {
        if (fl_count != null && fl_count.getVisibility() == View.VISIBLE) {
            if (flag) {
                fl_count.setBackground(mContext.getResources().getDrawable(R.drawable.ic_corner_mark_checked));
            } else {
                fl_count.setBackground(mContext.getResources().getDrawable(R.drawable.ic_corner_mark));
            }
        }
    }

    public void updateView(AdapterView<?> listView, int itemIndex, int lastItemIndex) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (itemIndex <= lastVisiblePosition && itemIndex >= firstVisiblePosition) {
            View curView = listView.getChildAt(itemIndex - firstVisiblePosition);
            TextView curTitle = curView.findViewById(R.id.tv_title);
            FrameLayout fl_count = curView.findViewById(R.id.fl_count);
            if (curTitle != null) {
                curTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
                setTextBold(true, curTitle);
                setHighLightTag(true, fl_count);
            }
        }
        if (lastItemIndex <= lastVisiblePosition && lastItemIndex >= firstVisiblePosition) {
            View lastView = listView.getChildAt(lastItemIndex - firstVisiblePosition);
            TextView lastTitle = lastView.findViewById(R.id.tv_title);
            FrameLayout fl_count = lastView.findViewById(R.id.fl_count);
            if (lastTitle != null) {
                lastTitle.setTextColor(mContext.getResources().getColor(R.color.gray_4c4c4c));
                setTextBold(false, lastTitle);
                setHighLightTag(false, fl_count);
            }
        }
    }

    private OnItemOnClickListener listener;

    public void setOnItemClickListener(OnItemOnClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemOnClickListener {
        void onItemClick(AdapterView<?> parent, View v, int position);
    }

}
