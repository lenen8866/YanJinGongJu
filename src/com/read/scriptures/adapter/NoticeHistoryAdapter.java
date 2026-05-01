package com.read.scriptures.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.NoticeBean;
import com.read.scriptures.util.TimeUtils;

public class NoticeHistoryAdapter extends BaseQuickAdapter<NoticeBean, BaseViewHolder> {
    public NoticeHistoryAdapter() {
        super(R.layout.item_notice_history);
    }

    @Override
    protected void convert(BaseViewHolder helper, NoticeBean item) {
        helper.setText(R.id.tv_title, item.title)
                .setText(R.id.tv_content, item.content)
                .setText(R.id.tv_time, TimeUtils.timeStamp2DateC(Long.parseLong(item.time)/1000))
        ;
    }
}
