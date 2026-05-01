package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.util.TypedValue;
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
 * Created by Administrator. Datetime: 2015/7/2. Email: lgmshare@mgail.com
 */
public class CategoryTwoGridAdapter extends EIBaseAdapter<Category> {

    private Context mContext;
    private int mIndex;
    private int mIndexCategory  = -1;

    public int getIndex() {
        return mIndex;
    }

    public CategoryTwoGridAdapter(Context context) {
        super(context);
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

    @Override
    public void setList(List<Category> list) {
        mList.clear();
        mList.add(new Category());
        if(list!=null) {
            mList.addAll(list);
        }
    }

    public void setIndexCategory(int mIndexCategory){
        this.mIndexCategory = mIndexCategory;
    }

    public int getIndexCategory() {
        return mIndexCategory;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.adapter_home_category_item, null);
        }
        TextView title = convertView.findViewById(R.id.tv_title);
        FrameLayout fl_count = convertView.findViewById(R.id.fl_count);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Category item = getItem(position);
        if (position == 0) {
            title.setText("全部");
        } else {
            String name = item.getCateName();
            int where = name.indexOf("-");
            name = name.substring(where + 1, name.length());
            title.setText(name);
        }
        if (position == mIndex) {
            title.setTextColor(mContext.getResources().getColor(R.color.orange));
            setTextBold(true, title);
        } else {
            title.setTextColor(mContext.getResources().getColor(R.color.gray_4c4c4c));
            setTextBold(false, title);
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

    public void updateView(AdapterView<?> listView, int itemIndex, int lastItemIndex) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (itemIndex <= lastVisiblePosition && itemIndex >= firstVisiblePosition) {
            View curView = listView.getChildAt(itemIndex - firstVisiblePosition);
            TextView curTitle = curView.findViewById(R.id.tv_title);
            if (curTitle != null) {
                curTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
                setTextBold(true, curTitle);
            }
        }
        if (lastItemIndex <= lastVisiblePosition && lastItemIndex >= firstVisiblePosition) {
            View lastView = listView.getChildAt(lastItemIndex - firstVisiblePosition);
            TextView lastTitle = lastView.findViewById(R.id.tv_title);
            if (lastTitle != null) {
                lastTitle.setTextColor(mContext.getResources().getColor(R.color.gray_4c4c4c));
                setTextBold(false, lastTitle);
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