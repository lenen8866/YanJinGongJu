package com.read.scriptures.audio;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.SaltUtils;
import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.bean.InviteVideoBean;
import com.read.scriptures.bean.NewAudioChapterData;
import com.read.scriptures.bean.NewBookData;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.music.player.lib.util.NetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteAudioAdapter extends BaseQuickAdapter<InviteVideoBean.DataDTO, BaseViewHolder> {
    public InviteAudioAdapter() {
        super(R.layout.item_invite_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, InviteVideoBean.DataDTO item) {
        helper.setText(R.id.tv_title, item.name);
        RecyclerView recyclerView = helper.getView(R.id.item_recycle);
        if (item.media == 0) {
            int count = item.column.size();
            if (count <= 4) {
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            }
            InviteAudioChildItemAdapter1 adapter = new InviteAudioChildItemAdapter1();
            recyclerView.setAdapter(adapter);
            adapter.setLine(count);
            adapter.setNewData(item.column);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter a, View view, int position) {
                    goAudioChapter(view.getContext(), adapter.getItem(position));
                }
            });
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            InviteAudioChildItemAdapter3 adapter = new InviteAudioChildItemAdapter3();
            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
            adapter.setNewData(item.column);
            adapter.setCurrentAudio(audioId);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter a, View view, int position) {
                    getBookAllAudio(view, adapter.getItem(position).cate_id, adapter.getItem(position).id, adapter.getItem(position).author);
                    adapter.setCurrentAudio(audioId = Long.parseLong(adapter.getItem(position).id));
                }
            });
        }
    }

    private void goAudioChapter(Context context, InviteVideoBean.DataDTO.ColumnDTO itemBean) {
        NewBookData.RowsBean rowsBean = new NewBookData.RowsBean();
        rowsBean.image = TextUtils.isEmpty(itemBean.cate_image) ? itemBean.video_cover : itemBean.cate_image;
        rowsBean.name = itemBean.cate_name;
        rowsBean.cate1_name = itemBean.group_one;
        rowsBean.cate2_name = itemBean.group_two;
        rowsBean.content = itemBean.cate_content;
        rowsBean.id = itemBean.id;

        Intent intent = new Intent(mContext, NewAudioChapterActivity.class);
        intent.putExtra(NewAudioChapterActivity.BOOK_DATA, rowsBean);
        intent.putExtra(NewAudioChapterActivity.BOOK_AUTHOR, "");
        if (context instanceof NewAudioActivity) {
            intent.putExtra("audio_cate_data", ((NewAudioActivity) context).cateIds);
        }
        mContext.startActivity(intent);
    }

    int playIndex = 0;

    private void getBookAllAudio(View view, String cateId, String id, String author) {
        MusicPlayerManager musicPlayerManager = MusicPlayerManager.getInstance();
        BaseAudioInfo currentPlayerMusic = musicPlayerManager.getCurrentPlayerMusic();
        if (currentPlayerMusic != null && currentPlayerMusic.id == Long.parseLong(id)) {
            musicPlayerManager.playOrPause();
            switch (musicPlayerManager.getPlayerState()) {
                case MusicConstants.MUSIC_PLAYER_PAUSE:
                    XToast.showToast(mContext, "暂停成功");
                    break;
                case MusicConstants.MUSIC_PLAYER_PLAYING:
                    XToast.showToast(mContext, "正在播放");
                    break;
            }
            return;
        }
        if (mContext instanceof NewAudioActivity) {
            ((NewAudioActivity) mContext).startAnim(view.findViewById(R.id.iv_cover));
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("book", cateId);
        map.put("author", author);
        NetUtil.postCache(ZConfig.SERVICE_URL + "/api/v1/multimedia3/audioList", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                NewAudioChapterData newAudioChapterData = new Gson().fromJson(t, NewAudioChapterData.class);
                if (newAudioChapterData == null || newAudioChapterData.rows == null || newAudioChapterData.rows.isEmpty()) {
                    XToast.showToast(mContext, "章节无内容");
                    return;
                }
                for (int i = 0; i < newAudioChapterData.rows.size(); i++) {
                    BaseAudioInfo baseAudioInfo = newAudioChapterData.rows.get(i);
                    if (baseAudioInfo.id == Long.parseLong(id)) {
                        playIndex = i;
                        break;
                    }
                }
                playAudio(newAudioChapterData.rows, playIndex);
            }

            @Override
            public void onError(String t) {
            }
        });
    }

    private void playAudio(List<BaseAudioInfo> data, int position) {
        MusicPlayerManager.getInstance().startPlayMusic(data, Math.max(position, 0));
        MusicPlayerManager.getInstance().setCurrentAuthor(data.get(Math.max(position, 0)).author);
    }

    private long audioId;

    public void setCurrentAudio(long id) {
        this.audioId = id;
        notifyDataSetChanged();
    }
}
