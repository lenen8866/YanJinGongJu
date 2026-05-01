package com.read.scriptures.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;

/**
 * Created by Administrator.
 * Datetime: 2015/7/2.
 * Email: lgmshare@mgail.com
 */
public class SearchMediaCacheAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public SearchMediaCacheAdapter() {
        super(R.layout.adapter_search_history_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_title, item);
        helper.addOnClickListener(R.id.iv_clear);
    }
}
