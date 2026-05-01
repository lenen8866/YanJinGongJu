package com.read.scriptures.audio;

import android.graphics.Color;

import androidx.core.content.ContextCompat;

import android.util.Log;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.music.player.lib.bean.BaseAudioInfo;
import com.read.scriptures.R;
import com.read.scriptures.util.FileUtil;
import com.read.scriptures.util.SecToTime;

import java.io.File;

public class NewAudioChapterAdapter extends BaseQuickAdapter<BaseAudioInfo, BaseViewHolder> {

    public NewAudioChapterAdapter() {
        super(R.layout.item_new_audio_chapter);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseAudioInfo item) {

        TextView tv_chapter_name = helper.getView(R.id.tv_chapter_name);
        TextView tv_chapter_time = helper.getView(R.id.tv_chapter_time);
        tv_chapter_name.setText(item.chapter);
        tv_chapter_time.setText(SecToTime.getTimeString(item.duration));
        helper.setImageResource(R.id.iv_collect, item.collect == 1 ? R.drawable.icon_collected : R.drawable.icon_collect)
                .addOnClickListener(R.id.iv_collect)
                .addOnClickListener(R.id.iv_share)
        ;

        if (audioId == item.id) {
            tv_chapter_name.setTextColor(ContextCompat.getColor(mContext, R.color.main_color));
            tv_chapter_time.setTextColor(ContextCompat.getColor(mContext, R.color.main_color));
            tv_chapter_name.setTextSize(15);
            tv_chapter_time.setTextSize(15);
        } else {
            tv_chapter_name.setTextColor(Color.parseColor("#000000"));
            tv_chapter_time.setTextColor(Color.parseColor("#000000"));
            tv_chapter_name.setTextSize(13);
            tv_chapter_time.setTextSize(13);
        }
        File file = new File(FileUtil.getDiskCachePath(mContext), String.valueOf(item.id));
        tv_chapter_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, file.exists() ? R.drawable.icon_download_audio : 0, 0);
    }

    private long audioId;

    public void setCurrentAudio(long audioId) {
        this.audioId = audioId;
        notifyDataSetChanged();
    }

    public void setCurrentDownloadAudio() {
        notifyDataSetChanged();
    }
}
