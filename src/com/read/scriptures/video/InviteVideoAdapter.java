package com.read.scriptures.video;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.InviteVideoBean;

public class InviteVideoAdapter extends BaseQuickAdapter<InviteVideoBean.DataDTO, BaseViewHolder> {
    public InviteVideoAdapter() {
        super(R.layout.item_invite_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, InviteVideoBean.DataDTO item) {
        helper.setText(R.id.tv_title, item.name);
        RecyclerView recyclerView = helper.getView(R.id.item_recycle);
        if (item.media == 0) {//分组
            int count = item.column.size();
            if (count <= 4) {
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
            }
            InviteVideoChildItemAdapter1 adapter = new InviteVideoChildItemAdapter1();
            recyclerView.setAdapter(adapter);
            adapter.setLine(count);
            adapter.setNewData(item.column);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter a, View view, int position) {
                    Intent intent = new Intent(mContext, VideoPlayActivity.class);
                    intent.putExtra("VIDEO_BOOK_ID", adapter.getItem(position).id);
                    intent.putExtra("VIDEO_BOOK_COVER", adapter.getItem(position).cate_image);
                    intent.putExtra("VIDEO_ITEM_ID", adapter.getItem(position).id);
                    mContext.startActivity(intent);
                }
            });
        } else {//视频
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            InviteVideoChildItemAdapter3 adapter = new InviteVideoChildItemAdapter3();
            recyclerView.setAdapter(adapter);
            adapter.setNewData(item.column);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter a, View view, int position) {
                    InviteVideoBean.DataDTO.ColumnDTO item1 = adapter.getItem(position);
                    Intent intent = new Intent(mContext, VideoPlayActivity.class);
                    intent.putExtra("VIDEO_BOOK_ID", adapter.getItem(position).cate_id);
                    intent.putExtra("VIDEO_BOOK_COVER", adapter.getItem(position).cacheImg);
                    intent.putExtra("VIDEO_ITEM_ID", adapter.getItem(position).id);
                    mContext.startActivity(intent);
                }
            });
        }

    }
}
