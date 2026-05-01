package com.read.scriptures.video;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.music.player.lib.bean.BaseAudioInfo;
import com.read.scriptures.R;
import com.read.scriptures.audio.CollectAudioAdapter;
import com.read.scriptures.audio.CollectAudioListActivity;
import com.read.scriptures.bean.DateBean;

public class CollectDateVideoAdapter extends BaseQuickAdapter<DateBean, BaseViewHolder> {
    public CollectDateVideoAdapter() {
        super(R.layout.item_collect_date);
    }

    @Override
    protected void convert(BaseViewHolder helper, DateBean item) {
        helper.setText(R.id.tv_date, item.monthDay);
        RecyclerView rcv_collect_list = helper.getView(R.id.rcv_collect_list);
        rcv_collect_list.setLayoutManager(new LinearLayoutManager(mContext));
        CollectVideoAdapter collectAudioAdapter = new CollectVideoAdapter();
        collectAudioAdapter.setNewData(item.data);
        rcv_collect_list.setAdapter(collectAudioAdapter);
        collectAudioAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mContext instanceof CollectVideoListActivity) {
                    ((CollectVideoListActivity) mContext).onVideoItemClick(collectAudioAdapter.getItem(position));
                }
            }
        });
    }

}
