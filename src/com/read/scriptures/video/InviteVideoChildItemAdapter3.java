package com.read.scriptures.video;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.InviteVideoBean;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.SecToTime;

public class InviteVideoChildItemAdapter3 extends BaseQuickAdapter<InviteVideoBean.DataDTO.ColumnDTO, BaseViewHolder> {
    public InviteVideoChildItemAdapter3() {
        super(R.layout.item_invite_child3);
    }

    @Override
    protected void convert(BaseViewHolder helper, InviteVideoBean.DataDTO.ColumnDTO item) {
        helper.setText(R.id.tv_title, TextUtils.isEmpty(item.cate_name) ? item.chapter : item.cate_name)
                .setText(R.id.tv_time, SecToTime.getTimeString(item.duration))
                .setText(R.id.tv_sub_title, item.group_one + "-" + item.group_two);

        ImageView iv_cover = helper.getView(R.id.iv_cover);
        if (!TextUtils.isEmpty(item.video_cover)) {
            item.cacheImg = item.video_cover;
            PicassoUtils.loadImage(iv_cover, item.video_cover, R.drawable.video_default_bg, DensityUtil.dip2px(110), DensityUtil.dip2px(70));
        } else if (item.v1cate != null) {
            item.cacheImg = item.v1cate.cate_image;
            PicassoUtils.loadImage(iv_cover, item.v1cate.cate_image, R.drawable.video_default_bg, DensityUtil.dip2px(110), DensityUtil.dip2px(70));
        } else {
            PicassoUtils.loadImage(iv_cover, "", R.drawable.video_default_bg, DensityUtil.dip2px(110), DensityUtil.dip2px(70));
        }

    }
}
