package com.read.scriptures.audio;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.InviteVideoBean;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.SecToTime;

public class InviteAudioChildItemAdapter3 extends BaseQuickAdapter<InviteVideoBean.DataDTO.ColumnDTO, BaseViewHolder> {
    public InviteAudioChildItemAdapter3() {
        super(R.layout.item_invite_audio_child3);
    }

    @Override
    protected void convert(BaseViewHolder helper, InviteVideoBean.DataDTO.ColumnDTO item) {
        helper.setText(R.id.tv_title, TextUtils.isEmpty(item.cate_name) ? item.chapter : item.cate_name)
                .setText(R.id.tv_time, SecToTime.getTimeString(item.duration))
                .setText(R.id.tv_sub_title, item.group_one + "-" + item.group_two)
                .setText(R.id.tv_sub_title1, item.author);

        ImageView iv_cover = helper.getView(R.id.iv_cover);
        if (!TextUtils.isEmpty(item.audio_cover)) {
            PicassoUtils.loadImage(iv_cover, item.audio_cover, R.drawable.video_default_bg, DensityUtil.dip2px(45), DensityUtil.dip2px(70));
        } else if (!TextUtils.isEmpty(item.image)) {
            PicassoUtils.loadImage(iv_cover, item.image, R.drawable.video_default_bg, DensityUtil.dip2px(45), DensityUtil.dip2px(70));
        } else if (item.v1cate != null) {
            PicassoUtils.loadImage(iv_cover, item.v1cate.cate_image, R.drawable.video_default_bg, DensityUtil.dip2px(45), DensityUtil.dip2px(70));
        } else {
            PicassoUtils.loadImage(iv_cover, "", R.drawable.video_default_bg, DensityUtil.dip2px(45), DensityUtil.dip2px(70));
        }
        if (audioId == Long.parseLong(item.id)) {
            helper.setTextColor(R.id.tv_title, ContextCompat.getColor(mContext, R.color.main_color));
            helper.setTextColor(R.id.tv_time, ContextCompat.getColor(mContext, R.color.main_color));
            helper.setTextColor(R.id.tv_sub_title, ContextCompat.getColor(mContext, R.color.main_color));
            helper.setTextColor(R.id.tv_sub_title1, ContextCompat.getColor(mContext, R.color.main_color));
            ((TextView) helper.getView(R.id.tv_title)).setTextSize(15);
        } else {
            helper.setTextColor(R.id.tv_title, Color.parseColor("#000000"));
            helper.setTextColor(R.id.tv_time, Color.parseColor("#999999"));
            helper.setTextColor(R.id.tv_sub_title, Color.parseColor("#999999"));
            helper.setTextColor(R.id.tv_sub_title1, Color.parseColor("#999999"));
            ((TextView) helper.getView(R.id.tv_title)).setTextSize(13);
        }
    }

    private long audioId;

    public void setCurrentAudio(long id) {
        this.audioId = id;
        notifyDataSetChanged();
    }
}
