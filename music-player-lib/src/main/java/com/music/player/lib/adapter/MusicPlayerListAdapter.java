package com.music.player.lib.adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.player.lib.R;
import com.music.player.lib.adapter.base.BaseAdapter;
import com.music.player.lib.bean.BaseAudioInfo;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/3/8
 * Player List
 */

public class MusicPlayerListAdapter extends BaseAdapter<BaseAudioInfo, MusicPlayerListAdapter.MusicHolderView> {

    public MusicPlayerListAdapter(Context context, List<BaseAudioInfo> data) {
        super(context, data);
    }

    @Override
    public MusicHolderView inCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new MusicHolderView(mInflater.inflate(R.layout.music_item_player_list, null));
    }

    @Override
    public void inBindViewHolder(MusicHolderView viewHolder, final int position) {
        BaseAudioInfo itemData = getItemData(position);
        if (null != itemData) {
            viewHolder.textTitle.setText(itemData.chapter);
            viewHolder.textSubTitle.setText(itemData.author);
            if (itemData.isSelected) {
                viewHolder.cl_main.setBackgroundColor(Color.parseColor("#fafafa"));
                viewHolder.textTitle.setTextColor(Color.parseColor("#5677FC"));
                viewHolder.textSubTitle.setTextColor(Color.parseColor("#5677FC"));
                viewHolder.textTitle.setTextSize(16);
                viewHolder.textSubTitle.setTextSize(13);

            } else {
                viewHolder.cl_main.setBackgroundColor(Color.parseColor("#ffffff"));
                viewHolder.textTitle.setTextColor(Color.parseColor("#000000"));
                viewHolder.textSubTitle.setTextColor(Color.parseColor("#999999"));
                viewHolder.textTitle.setTextSize(14);
                viewHolder.textSubTitle.setTextSize(11);
            }
            viewHolder.itemView.setTag(itemData);
        }

    }

    @Override
    protected void inBindViewHolder(MusicHolderView viewHolder, int position, List<Object> payloads) {
        super.inBindViewHolder(viewHolder, position, payloads);
        BaseAudioInfo itemData = getItemData(position);
        if (null != itemData) {
            viewHolder.textTitle.setText(itemData.chapter);
            if (itemData.isSelected) {
                viewHolder.cl_main.setBackgroundColor(Color.parseColor("#fafafa"));
                viewHolder.textTitle.setTextColor(Color.parseColor("#5677FC"));
                viewHolder.textSubTitle.setTextColor(Color.parseColor("#5677FC"));
                viewHolder.textTitle.setTextSize(17);
                viewHolder.textSubTitle.setTextSize(13);
            } else {
                viewHolder.textTitle.setTextColor(Color.parseColor("#000000"));
                viewHolder.textSubTitle.setTextColor(Color.parseColor("#000000"));
                viewHolder.cl_main.setBackgroundColor(Color.parseColor("#ffffff"));
                viewHolder.textTitle.setTextSize(15);
                viewHolder.textSubTitle.setTextSize(11);
            }
            viewHolder.itemView.setTag(itemData);
        }
    }

    public class MusicHolderView extends RecyclerView.ViewHolder {
        private TextView textTitle;
        private TextView textSubTitle;
        private View cl_main;
        private ImageView iv_delete;

        public MusicHolderView(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.view_item_title);
            textSubTitle = itemView.findViewById(R.id.view_item_subtitle);
            cl_main = itemView.findViewById(R.id.cl_main);
            iv_delete = itemView.findViewById(R.id.iv_delete);

            if (mOnItemClickListener != null) {
                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, getAdapterPosition(), 0);

                    }
                });
            }
        }
    }
}