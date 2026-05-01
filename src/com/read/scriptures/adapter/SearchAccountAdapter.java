package com.read.scriptures.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.SearchAccountBean;
import com.read.scriptures.util.CircleTransform;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;

public class SearchAccountAdapter extends BaseQuickAdapter<SearchAccountBean.DataBean, BaseViewHolder> {
    public SearchAccountAdapter() {
        super(R.layout.item_search_account);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchAccountBean.DataBean item) {
        helper.setText(R.id.tv_title, TextUtils.isEmpty(item.nickname) ? "无" : item.nickname)
                .setText(R.id.tv_chapter_num, item.username);

        ImageView iv_cover = helper.getView(R.id.iv_cover);
        PicassoUtils.loadImage(iv_cover, item.avatar, R.drawable.icon_play_deault_bg, new CircleTransform(), DensityUtil.dip2px(30), DensityUtil.dip2px(30));
    }
}
