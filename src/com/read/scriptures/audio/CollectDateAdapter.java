package com.read.scriptures.audio;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.music.player.lib.bean.BaseAudioInfo;
import com.read.scriptures.R;
import com.read.scriptures.bean.DateBean;

public class CollectDateAdapter extends BaseQuickAdapter<DateBean, BaseViewHolder> {
    public CollectDateAdapter() {
        super(R.layout.item_collect_date);
    }

    @Override
    protected void convert(BaseViewHolder helper, DateBean item) {
        helper.setText(R.id.tv_date, item.monthDay);
        RecyclerView rcv_collect_list = helper.getView(R.id.rcv_collect_list);
        rcv_collect_list.setLayoutManager(new LinearLayoutManager(mContext));
        CollectAudioAdapter collectAudioAdapter = new CollectAudioAdapter();
        collectAudioAdapter.setCurrentAudio(baseAudioInfo);
        collectAudioAdapter.setNewData(item.data);
        rcv_collect_list.setAdapter(collectAudioAdapter);
        collectAudioAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mContext instanceof CollectAudioListActivity) {
                    ((CollectAudioListActivity) mContext).onAudioItemClick(collectAudioAdapter.getItem(position));
                }
            }
        });
    }

    private BaseAudioInfo baseAudioInfo;

    public void setCurrentAudio(BaseAudioInfo currentPlayerMusic) {
        this.baseAudioInfo = currentPlayerMusic;
        notifyDataSetChanged();
    }
}
