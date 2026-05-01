package com.read.scriptures.ui.fragment;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.QARankBean;
import com.read.scriptures.util.CircleTransform;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.TimeUtils;

public class RankAdapter extends BaseQuickAdapter<QARankBean.DataBean.RankBean, BaseViewHolder> {
    public RankAdapter() {
        super(R.layout.item_rank);
    }

    @Override
    protected void convert(BaseViewHolder helper, QARankBean.DataBean.RankBean item) {
        helper.setText(R.id.tv_title, TextUtils.isEmpty(item.nickname) ? "暂无" : item.nickname)
                .setText(R.id.tv_chapter_num, TextUtils.isEmpty(item.score) ? "0分" : item.score + "分")
                .setText(R.id.tv_index, helper.getAdapterPosition() + 1 + "")
                .setText(R.id.tv_end, TimeUtils.getUpdateDate(item.submitTime * 1000))
        ;
        ImageView iv_cover = helper.getView(R.id.iv_cover);
        PicassoUtils.loadImage(iv_cover, item.avatar, R.drawable.icon_play_deault_bg, new CircleTransform(), DensityUtil.dip2px(40), DensityUtil.dip2px(40));
    }

}
