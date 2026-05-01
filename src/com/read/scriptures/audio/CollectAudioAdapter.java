package com.read.scriptures.audio;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.music.player.lib.bean.BaseAudioInfo;
import com.read.scriptures.R;
import com.read.scriptures.bean.CollectAudioBean;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.TimeUtils;

public class CollectAudioAdapter extends BaseQuickAdapter<CollectAudioBean.DataBean.RowsBean, BaseViewHolder> {
    public CollectAudioAdapter() {
        super(R.layout.item_collect_audio);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectAudioBean.DataBean.RowsBean item) {
        if (item.datainfo == null) {
            return;
        }
        helper.setText(R.id.tv_title, item.datainfo.chapter);
        helper.setText(R.id.tv_author, item.datainfo.author);
        helper.setText(R.id.tv_cate, item.datainfo.sortAdd);
        helper.setText(R.id.tv_book_name, item.datainfo.cate_name);
        helper.setText(R.id.tv_time, TimeUtils.parseTime(item.create_time * 1000));

        helper.setBackgroundColor(R.id.cl_root, helper.getAdapterPosition() % 2 == 0 ? Color.parseColor("#ffffff") : Color.parseColor("#fafafa"));

        if (currentAudio == null || !TextUtils.equals(String.valueOf(currentAudio.id), item.datainfo.id)) {
            helper.setTextColor(R.id.tv_title, Color.parseColor("#000000"));
            helper.setTextColor(R.id.tv_author, Color.parseColor("#B7B7B7"));
            helper.setTextColor(R.id.tv_cate, Color.parseColor("#B7B7B7"));
            helper.setTextColor(R.id.tv_book_name, Color.parseColor("#B7B7B7"));
            helper.setTextColor(R.id.tv_time, Color.parseColor("#B7B7B7"));

        } else {
            helper.setTextColor(R.id.tv_title, Color.parseColor("#ff9800"));
            helper.setTextColor(R.id.tv_author, Color.parseColor("#ff9800"));
            helper.setTextColor(R.id.tv_cate, Color.parseColor("#ff9800"));
            helper.setTextColor(R.id.tv_book_name, Color.parseColor("#ff9800"));
            helper.setTextColor(R.id.tv_time, Color.parseColor("#ff9800"));
        }
        ImageView iv_cover = helper.getView(R.id.iv_cover);
        PicassoUtils.loadImage(iv_cover, item.datainfo.cate_image, R.drawable.icon_play_deault_bg, DensityUtil.dip2px(50), DensityUtil.dip2px(70));
    }

    private BaseAudioInfo currentAudio;

    public void setCurrentAudio(BaseAudioInfo currentPlayerMusic) {
        this.currentAudio = currentPlayerMusic;
        notifyDataSetChanged();
    }
}
