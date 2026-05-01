package com.read.scriptures.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicOnItemClickListener;
import com.music.player.lib.listener.MusicPlayerEventListener;
import com.music.player.lib.listener.MusicPlayerInfoListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.MusicClickControler;
import com.music.player.lib.util.XToast;
import com.music.player.lib.view.dialog.MusicPlayerListDialog;
import com.read.scriptures.R;
import com.read.scriptures.audio.AudioPlayActivity;
import com.read.scriptures.util.CircleTransform;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;

import java.util.List;
import java.util.Random;

public class AudioPlayingView extends FrameLayout implements View.OnClickListener {
    public AudioPlayingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        setOnClickListener(this);
    }

    private CirclePercentView cpv_progress;
    private ImageView iv_cover;
    private ProgressBar pb_loading;
    private AutoMarqueeTextView amt_chapter;
    private AutoMarqueeTextView amt_book;
    private AutoMarqueeTextView amt_cate;
    private ImageView iv_playing_pause;
    private ImageView iv_playing_next;
    private ImageView iv_playing_list;
    private VideoRecordProgressBar progressBar;

    private ObjectAnimator objectAnimator;
    private MusicClickControler musicClickControler;

    private MusicPlayerManager musicPlayerManager;

    private void initView() {
        inflate(getContext(), R.layout.layout_audio_playing, this);
        iv_cover = findViewById(R.id.iv_cover);
        cpv_progress = findViewById(R.id.cpv_progress);
        amt_chapter = findViewById(R.id.amt_chapter);
        amt_book = findViewById(R.id.amt_book);
        iv_playing_pause = findViewById(R.id.iv_playing_pause);
        iv_playing_next = findViewById(R.id.iv_playing_next);
        iv_playing_list = findViewById(R.id.iv_playing_list);
        amt_cate = findViewById(R.id.amt_cate);
        progressBar = findViewById(R.id.hp_progress);
        pb_loading = findViewById(R.id.pb_loading);
        iv_playing_pause.setOnClickListener(this);
        iv_playing_next.setOnClickListener(this);
        iv_playing_list.setOnClickListener(this);

        amt_chapter.setOnClickListener(this);
        amt_book.setOnClickListener(this);
        amt_cate.setOnClickListener(this);
        findViewById(R.id.cl_main).setOnClickListener(this);
        musicClickControler = new MusicClickControler();
        musicClickControler.init(1, 600);
        initAnim();
        initStatus();
        initListener();

    }

    private void initStatus() {
        musicPlayerManager = MusicPlayerManager.getInstance().init(getContext());
        if (musicPlayerManager.isPlaying()) {
            show();
            BaseAudioInfo currentPlayerMusic = musicPlayerManager.getCurrentPlayerMusic();
            setData(currentPlayerMusic);
            startAnimation();
        } else {
            setVisibility(GONE);
        }
    }

    private void initListener() {
        musicPlayerManager.addOnPlayerEventListener(musicPlayerEventListener);
        musicPlayerManager.addPlayInfoListener(playerInfoListener);
    }

    private void initAnim() {
        objectAnimator = ObjectAnimator.ofFloat(iv_cover, "rotation", 0f, 360f);
        objectAnimator.setDuration(15000);
        //动画：时间线性渐变
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    public void startAnimation() {
        if (!objectAnimator.isStarted()) {
            objectAnimator.start();
        } else if (objectAnimator.isPaused()) {
            objectAnimator.resume();
        }
        iv_playing_pause.setImageResource(R.drawable.icon_playing_pause);
    }

    public void stopAnimation() {
        if (objectAnimator != null) {
            objectAnimator.pause();
        }
        iv_playing_pause.setImageResource(R.drawable.icon_playing_play);
    }

    private BaseAudioInfo audioInfo;

    public void setData(BaseAudioInfo musicInfo) {
        audioInfo = musicInfo;
        Context context = getContext();
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                return;
            }
        }
        PicassoUtils.loadImage(iv_cover, TextUtils.isEmpty(musicInfo.audio_cover) ? musicInfo.image : musicInfo.audio_cover, R.drawable.icon_play_default, new CircleTransform(), DensityUtil.dip2px(40), DensityUtil.dip2px(40));
        amt_chapter.setText(musicInfo.chapter);
        amt_cate.setText(musicInfo.cate3_name + (TextUtils.isEmpty(musicInfo.author) ? "" : "-" + musicInfo.author));
        amt_book.setText(musicInfo.cate1_name + "-" + musicInfo.cate2_name);
    }

    private long currentDuration;
    private long maxDuration;

    public void setProgress(float progress, long total, long current) {
        if (cpv_progress != null) {
            cpv_progress.setPercentage(progress);
        }
        maxDuration = total;
        currentDuration = current;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_playing_pause:
                if (musicClickControler.canTrigger()) {
                    musicPlayerManager.playOrPause();
                }
                break;
            case R.id.iv_playing_next:
                if (musicClickControler.canTrigger()) {
                    playNext();
                }
                break;
            case R.id.iv_playing_list:
                if (musicClickControler.canTrigger()) {
                    showPlayList();
                }
                break;
            case R.id.cl_main:
            case R.id.amt_chapter:
            case R.id.amt_cate:
            case R.id.amt_book:
                if (musicClickControler.canTrigger()) {
                    Intent intent = new Intent(getContext(), AudioPlayActivity.class);
                    intent.putExtra(AudioPlayActivity.PLAY_DURATION, (int) currentDuration);
                    intent.putExtra(AudioPlayActivity.TOTAL_DURATION, (int) maxDuration);
                    intent.putExtra(AudioPlayActivity.CURRENT_PLAY_AUDIO, audioInfo);
                    intent.putExtra(AudioPlayActivity.TOTAL_DURATION_DATA, audioInfo);
                    getContext().startActivity(intent);
                }
                break;
        }
    }

    /**
     * 播放下一首
     */
    private void playNext() {
        showLoading();
        int nextPlayIndex = musicPlayerManager.playNextIndex();
        if (nextPlayIndex == -1) {
            hideLoading();
            XToast.showToast(getContext(), "已经是最后一章了");
        } else {
            musicPlayerManager.startPlayMusic(nextPlayIndex);
        }
    }

    /**
     * 显示播放列表
     */
    private void showPlayList() {
        MusicPlayerListDialog
                .getInstance(getContext())
                .setMusicOnItemClickListener(new MusicOnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, long musicID) {
                        musicPlayerManager.startPlayMusic(position);
                    }

                    @Override
                    public void onItemDeleteClick(int position) {
                        if (position == -1) {
                            return;
                        }
                        //删除随机列表中的
                        List<BaseAudioInfo> randomPlayList = (List<BaseAudioInfo>) musicPlayerManager.getRandomPlayList();
                        if (musicPlayerManager.getPlayerModel() == MusicConstants.MUSIC_MODEL_RANDOM && randomPlayList != null && !randomPlayList.isEmpty()) {
                            List<BaseAudioInfo> currentPlayList = (List<BaseAudioInfo>) musicPlayerManager.getCurrentPlayList();
                            randomPlayList.remove(currentPlayList.get(position));
                        }
                        if (musicPlayerManager.isPlaying()) {
                            BaseAudioInfo currentPlayerMusic = musicPlayerManager.getCurrentPlayerMusic();
                            int index = musicPlayerManager.getCurrentPlayList().indexOf(currentPlayerMusic);
                            if (index == position) {
                                int nextIndex = musicPlayerManager.playNextIndex();
                                if (nextIndex == -1) {//最后一首
                                    if (musicPlayerManager.isPlaying()) {
                                        musicPlayerManager.onStop();
                                    }
                                } else {
                                    musicPlayerManager.startPlayMusic(nextIndex);
                                }
                            }
                            musicPlayerManager.getCurrentPlayList().remove(position);
                        }
                    }
                }).show();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what==1){
                MessageBean messageBean = (MessageBean) msg.obj;
                setProgress(messageBean.progress, messageBean.totalDurtion, messageBean.current);
            }
        }
    };

    class MessageBean {
        public float progress;
        public long totalDurtion;
        public long current;

        public MessageBean(float progress, long totalDurtion, long current) {
            this.progress = progress;
            this.totalDurtion = totalDurtion;
            this.current = current;
        }
    }

    MusicPlayerEventListener musicPlayerEventListener = new MusicPlayerEventListener() {

        @Override
        public void onMusicPlayerState(int playerState, String message) {
            switch (playerState) {
                case MusicConstants.MUSIC_PLAYER_COMPLETE:
                    setProgress(0, 0, 0);
                    hideLoading();
                    stopAnimation();
                    break;
                case MusicConstants.MUSIC_PLAYER_PREPARE://准备
                    setProgress(0, 0, 0);
                    showLoading();
                    stopAnimation();
                    break;
                case MusicConstants.MUSIC_PLAYER_ERROR://错误
                    setProgress(0, 0, 0);
                    hideLoading();
                    stopAnimation();
                    break;
                case MusicConstants.MUSIC_PLAYER_STOP://停止
                    setProgress(0, 0, 0);
                    hideLoading();
                    stopAnimation();
                    break;
                case MusicConstants.MUSIC_PLAYER_BUFFER://缓冲
                    hideLoading();
                    break;
                case MusicConstants.MUSIC_PLAYER_PAUSE://暂停
                    hideLoading();
                    stopAnimation();
                    break;
                case MusicConstants.MUSIC_PLAYER_PLAYING://播放中
                    hideLoading();
                    startAnimation();
                    break;
            }
        }

        @Override
        public void onPrepared(long totalDurtion) {
            setProgress(0, 0, 0);
            if (audioInfo == null) {
                return;
            }
            String playDuration = audioInfo.playDuration;
            if (!TextUtils.isEmpty(playDuration) && audioInfo.isCached) {//只有在
                audioInfo.isCached = false;
                long time = parseLong(playDuration);
                if (musicPlayerManager.isPlaying()) {
                    musicPlayerManager.seekTo(time);
                }
            }
        }

        @Override
        public void onBufferingUpdate(int percent) {
        }

        @Override
        public void onInfo(int event, int extra) {

        }

        @Override
        public void onPlayMusiconInfo(BaseAudioInfo musicInfo, int position) {

        }

        @Override
        public void onMusicPathInvalid(BaseAudioInfo musicInfo, int position) {

        }

        @Override
        public void onTaskRuntime(long totalDurtion, long current, long alarmResidueDurtion, int bufferProgress) {
            if (totalDurtion == -1 || current == -1) {
                return;
            }
            if (current / 1000 >= totalDurtion / 1000) {//会有1s内的误差 所以换算秒
                current = 0;
            }
            float progress = ((float) current / totalDurtion) * 100;

            Message message = Message.obtain();
            MessageBean messageBean = new MessageBean(progress,totalDurtion,current);
            message.obj =messageBean;
            message.what=1;
            myHandler.sendMessage(message);
        }

        @Override
        public void onPlayerConfig(int playModel, int alarmModel, boolean isToast) {

        }
    };

    private long parseLong(String str) {
        if (str == null || TextUtils.isEmpty(str.trim())) {
            return 0L;
        }
        return Long.parseLong(str);
    }

    public void showLoading() {
        if (pb_loading != null) {
            pb_loading.setVisibility(VISIBLE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(VISIBLE);
            progressBar.setMaxProgress(100);
            progressBar.setProgress(new Random().nextInt(20) + 5);

        }
    }

    public void hideLoading() {
        if (pb_loading != null) {
            pb_loading.setVisibility(INVISIBLE);
        }
        if (progressBar != null) {
            progressBar.setMaxProgress(100);
            progressBar.setProgress(100);
            progressBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(INVISIBLE);
                }
            }, 500);

        }
    }

    MusicPlayerInfoListener playerInfoListener = new MusicPlayerInfoListener() {
        @Override
        public void onPlayMusiconInfo(BaseAudioInfo musicInfo, int position) {
            show();
            setData(musicInfo);
        }
    };

    public void onActivityDestroy() {
        musicPlayerManager.removePlayerListener(musicPlayerEventListener);
        musicPlayerManager.removePlayInfoListener(playerInfoListener);
        playerInfoListener = null;
        musicPlayerEventListener = null;
    }
}
