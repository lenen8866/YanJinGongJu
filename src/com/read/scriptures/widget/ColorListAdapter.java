package com.read.scriptures.widget;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;

public class ColorListAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

    public ColorListAdapter() {
        super(R.layout.item_color_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, Integer color) {
       View iv_color= helper.getView(R.id.iv_color);
        if (color == -1) {
            iv_color.setBackgroundResource(R.drawable.icon_add);
        } else {
            iv_color.setBackgroundColor(color);
        }
    }


}
