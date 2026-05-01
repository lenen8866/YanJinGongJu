package com.read.scriptures.audio;


import android.graphics.Color;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.music.player.lib.bean.BaseAudioInfo;
import com.read.scriptures.R;
import com.read.scriptures.bean.DateBean;

public class AllDayAdapter extends BaseQuickAdapter<DateBean, BaseViewHolder> {

    public AllDayAdapter() {
        super(R.layout.item_layout_day);
    }

    @Override
    protected void convert(BaseViewHolder helper, DateBean item) {
        helper.setText(R.id.tv_day, item.day)
                .setBackgroundRes(R.id.tv_day, item.isOnClick ? R.drawable.red_click_bg : item.isToday ? R.drawable.red_tag_bg : 0)
                .setVisible(R.id.view_event, item.data != null && !item.isOnClick);

        TextView tv_day = helper.getView(R.id.tv_day);
        if (item.isToday || item.isOnClick) {
            tv_day.setTextColor(Color.parseColor("#ffffff"));
        } else if (item.type != 1) {
            tv_day.setTextColor(Color.parseColor("#999999"));
        } else {
            tv_day.setTextColor(Color.parseColor("#333333"));
        }
    }

}
