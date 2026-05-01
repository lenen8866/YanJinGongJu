package com.read.scriptures.video;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.VideoListBean;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.SecToTime;

public class VideoPlayListAdapter extends BaseQuickAdapter<VideoListBean.RowsBean, BaseViewHolder> {
    public VideoPlayListAdapter() {
        super(R.layout.item_video_play_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoListBean.RowsBean item) {
        TextView tv_title = helper.getView(R.id.tv_title);
        TextView tv_time = helper.getView(R.id.tv_time);
        tv_title.setText(item.chapter);
        String timeString = SecToTime.getTimeString(item.duration);
        tv_time.setText(timeString);
        if (TextUtils.equals(timeString, "00:00")) {
            tv_time.setVisibility(View.INVISIBLE);
        } else {
            tv_time.setVisibility(View.VISIBLE);
        }
        ImageView iv_cover = helper.getView(R.id.iv_cover);
        FrameLayout fl_cover = helper.getView(R.id.fl_cover);
        if (isLight) {
            tv_title.setTextColor(Color.BLACK);
            tv_time.setTextColor(Color.parseColor("#999999"));
            if (TextUtils.equals(currentId, item.id)) {
                fl_cover.setBackgroundColor(0);
                helper.setBackgroundColor(R.id.cl_item, Color.parseColor("#f2f2f2"));
                tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv_title.setTextSize(15);
            } else {
                fl_cover.setBackgroundColor(Color.parseColor("#77ffffff"));
                helper.setBackgroundColor(R.id.cl_item, 0);
                tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tv_title.setTextSize(13);
            }
        } else {
            if (TextUtils.equals(currentId, item.id)) {
                fl_cover.setBackgroundColor(0);
                tv_title.setTextColor(Color.parseColor("#FCFCFC"));
                tv_time.setTextColor(Color.parseColor("#FCFCFC"));
                helper.setBackgroundColor(R.id.cl_item, Color.parseColor("#171717"));
                tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv_title.setTextSize(15);
            } else {
                fl_cover.setBackgroundColor(Color.parseColor("#77000000"));
                tv_title.setTextColor(Color.parseColor("#A7A7A7"));
                tv_time.setTextColor(Color.parseColor("#A7A7A7"));
                helper.setBackgroundColor(R.id.cl_item, 0);
                tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tv_title.setTextSize(13);
            }
        }

        PicassoUtils.loadImage(iv_cover, TextUtils.isEmpty(item.video_cover) ? bookCover : item.video_cover, R.drawable.video_default_bg, DensityUtil.dip2px(99), DensityUtil.dip2px(60));
        helper.addOnClickListener(R.id.iv_cover);
    }

    public void setBookCover(String cover) {
        this.bookCover = cover;
    }

    private String bookCover;
    private String currentId;

    public void setCurrentId(String id) {
        this.currentId = id;
        notifyDataSetChanged();
    }

    private boolean isLight;

    public void switchPageLight(boolean isLight) {
        this.isLight = isLight;
        notifyDataSetChanged();
    }
}
