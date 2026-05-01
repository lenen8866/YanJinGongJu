package com.read.scriptures.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.read.scriptures.R;
import com.read.scriptures.bean.VideoListBean;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.video.VideoPlayActivity;

import java.text.DecimalFormat;

public class VideoCacheView extends FrameLayout implements View.OnClickListener {
    public VideoCacheView(@NonNull Context context) {
        super(context);
    }

    public VideoCacheView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initData(context);
    }


    public VideoCacheView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ImageView iv_cover;
    private TextView tv_chapter;
    private TextView tv_book;
    private TextView tv_duration;
    private TextView tv_play_time;

    private VideoListBean.RowsBean videoItem;

    private void initView(Context context) {
        inflate(context, R.layout.layout_video_playing, this);
        setOnClickListener(this);
        iv_cover = findViewById(R.id.iv_cover);
        tv_chapter = findViewById(R.id.tv_chapter);
        tv_book = findViewById(R.id.tv_book);
        tv_duration = findViewById(R.id.tv_duration);
        tv_play_time = findViewById(R.id.tv_play_time);
    }

    public void initData(Context context) {
        try {
            VideoListBean videoCacheBean = (VideoListBean) PreferencesUtils.getObject(context, "last_play_video_cache");
            if (videoCacheBean == null) {
                setVisibility(GONE);
                return;
            }
            if (videoCacheBean.rows == null && videoCacheBean.rows.isEmpty()) {
                setVisibility(GONE);
                return;
            }
            String cateId = videoCacheBean.rows.get(0).cate_id;
            int videoInCateIndex = PreferencesUtils.getInt(context, "VIDEO_CACHE_PLAY_INDEX_" + cateId, 0);
            if (videoInCateIndex < 0 || videoInCateIndex >= videoCacheBean.rows.size()) {
                setVisibility(GONE);
                return;
            }
            setVisibility(VISIBLE);
            //当前播放的视频
            videoItem = videoCacheBean.rows.get(videoInCateIndex);
            PicassoUtils.loadImage(iv_cover, TextUtils.isEmpty(videoItem.video_cover) ? videoItem.bookCover : videoItem.video_cover, R.drawable.video_default_bg, DensityUtil.dip2px(85), DensityUtil.dip2px(52));
            tv_chapter.setText(videoItem.chapter);
            tv_book.setText(videoItem.cate3_name);
            tv_duration.setText(videoItem.cate1_name + "-" + videoItem.cate2_name);
            long time = parseLong(videoItem.playDuration);
            long totalTime = parseLong(videoItem.duration);
            float percent = ((float) time / totalTime / 1000) * 100;
            if (percent > 100) {
                percent = 100.0f;
            } else if (percent < 0) {
                percent = 0.0f;
            }
            DecimalFormat df = new DecimalFormat("#0.0");//这样为保持2位
            String format = df.format(percent);
            tv_play_time.setText("点击继续观看\n(" + format + "%)");
        } catch (Exception e) {
            setVisibility(GONE);
        }
    }

    private long parseLong(String str) {
        if (str == null || TextUtils.isEmpty(str.trim())) {
            return 0L;
        }
        return Long.parseLong(str);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), VideoPlayActivity.class);
        intent.putExtra("VIDEO_BOOK_ID", videoItem.cate_id);
        intent.putExtra("VIDEO_BOOK_COVER", TextUtils.isEmpty(videoItem.video_cover) ? videoItem.bookCover : videoItem.video_cover);
        intent.putExtra("VIDEO_ITEM_ID", videoItem.id);
        getContext().startActivity(intent);
    }

}
