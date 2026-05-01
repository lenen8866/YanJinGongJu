package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
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
public class CategorySpinnerAdapter extends EIBaseAdapter<Category> implements SpinnerAdapter {

    public CategorySpinnerAdapter(Context context) {
        super(context);
    }

    @Override
    public void setList(List<Category> list) {
        mList.clear();
        mList.addAll(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.adapter_home_category_item, null);
        }
        TextView city = (TextView) convertView.findViewById(R.id.tv_title);
        Category item = getItem(position);
        city.setText(item.getCateName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // 修改Spinner展开后的字体颜色
        if (convertView == null) {
            //我们也可以加载自己的Layout布局
            convertView = mLayoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(getItem(position).getCateName());
        tv.setTextSize(15);
        return convertView;
    }
}
