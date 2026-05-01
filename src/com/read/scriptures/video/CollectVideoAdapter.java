package com.read.scriptures.video;

import android.graphics.Color;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.CollectAudioBean;
import com.read.scriptures.bean.CollectVideoBean;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.TimeUtils;


/**
 * Created by YoKey on 16/10/7.
 */
public class CollectVideoAdapter extends BaseQuickAdapter<CollectAudioBean.DataBean.RowsBean, BaseViewHolder> {

    public CollectVideoAdapter() {
        super(R.layout.item_collect_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectAudioBean.DataBean.RowsBean item) {
        helper.setText(R.id.tv_title,item.datainfo.chapter);
        helper.setText(R.id.tv_author,item.datainfo.author);
//        vh.tv_cate.setText(item.datainfo.sortAdd);
//        vh.tv_book_name.setText(item.datainfo.cate_name);
        helper.setText(R.id.tv_time,TimeUtils.parseTime(Long.parseLong((item.create_time + "000"))));

        helper.setBackgroundColor(R.id.cl_root,helper.getAdapterPosition() % 2 == 0 ? Color.parseColor("#fafafa") : Color.parseColor("#ffffff"));

        helper.setTextColor(R.id.tv_title,Color.parseColor("#000000"));
        helper.setTextColor(R.id.tv_author,Color.parseColor("#B7B7B7"));
        helper.setTextColor(R.id.tv_time,Color.parseColor("#B7B7B7"));
        helper.setTextColor(R.id.tv_cate,Color.parseColor("#B7B7B7"));
        helper.setTextColor(R.id.tv_book_name,Color.parseColor("#B7B7B7"));

        ImageView iv_cover = helper.getView(R.id.iv_cover);
        PicassoUtils.loadImage(iv_cover,item.datainfo.video_cover,R.drawable.icon_play_deault_bg,   DensityUtil.dip2px(100), DensityUtil.dip2px(60));
    }

}
