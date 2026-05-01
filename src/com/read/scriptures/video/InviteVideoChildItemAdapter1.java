package com.read.scriptures.video;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.InviteVideoBean;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;

public class InviteVideoChildItemAdapter1 extends BaseQuickAdapter<InviteVideoBean.DataDTO.ColumnDTO, BaseViewHolder> {
    public InviteVideoChildItemAdapter1() {
        super(R.layout.item_invite_child1);
    }

    @Override
    protected void convert(BaseViewHolder helper, InviteVideoBean.DataDTO.ColumnDTO item) {
        helper.setText(R.id.tv_title, TextUtils.isEmpty(item.cate_name) ? item.chapter : item.cate_name)
                .setText(R.id.tv_sub_title, item.group_one + "-" + item.group_two);

        ImageView iv_cover = helper.getView(R.id.iv_cover);
        PicassoUtils.loadImage(iv_cover, TextUtils.isEmpty(item.cate_image) ? item.video_cover : item.cate_image, R.drawable.video_default_bg, DensityUtil.dip2px(130), DensityUtil.dip2px(85));
        LinearLayout ll_main = helper.getView(R.id.ll_main);
        ViewGroup.LayoutParams layoutParams = ll_main.getLayoutParams();
        if (count <= 4) {
            layoutParams.width = DensityUtil.dip2px(mContext, 130);
        } else {
            layoutParams.width = DensityUtil.getScreenWidth(mContext) / 2-DensityUtil.dip2px(mContext,15);
        }
    }

    private int count;

    public void setLine(int count) {
        this.count = count;
        notifyDataSetChanged();
    }
}
