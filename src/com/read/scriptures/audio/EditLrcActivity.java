
package com.read.scriptures.audio;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicPlayerEventListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.MusicClickControler;
import com.music.player.lib.util.NetUtil;
import com.music.player.lib.util.SaltUtils;
import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.bean.CheckCommitPermissionBean;
import com.read.scriptures.bean.CommitLrcBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.DialogUtils;
import com.read.scriptures.util.PicassoBlurTransformation;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SrtParser;
import com.read.scriptures.util.ThreadUtil;
import com.read.scriptures.view.CustomSeekBar;
import com.read.scriptures.view.lrc.EditLrcBean;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditLrcActivity extends BaseActivity {

    private BaseAudioInfo playerMusic;
    private CustomSeekBar customSeekBar;
    private LinearLayout ll_set_speed;
    private TextView tv_speed;
    private ImageView iv_speed_mode;
    private ImageView iv_play;
    private View cl_main;
    private RecyclerView rcv_lrc;
    private EditLrcAdapter editLrcAdapter;

    private MusicPlayerManager musicPlayerManager;
    private MusicClickControler musicClickControler;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_edit_lrc);
        playerMusic = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
        if (playerMusic == null) {
            playerMusic = (BaseAudioInfo) getIntent().getSerializableExtra("edit_audio_data");
        }
        if (playerMusic == null) {
            finish();
            return;
        }
        checkSubtitles(playerMusic);
        musicPlayerManager = MusicPlayerManager.getInstance();
        //编辑歌词默认 1.0倍速
        musicPlayerManager.setSpeed(1.0f);
        //默认单曲循环播放
        musicPlayerManager.setPlayerModel(MusicConstants.MUSIC_MODEL_SINGLE);
        musicClickControler = new MusicClickControler();
        musicClickControler.init(1, 500);
        initView();
    }

    private void test() {
        File newFile = new File(NetUtil.getDiskCachePath(this), "test.lrc");
        parseLrc(newFile);
    }

    private void checkSubtitles(BaseAudioInfo playerMusic) {
        String subtitlesUrl = SaltUtils.getUrl(playerMusic.audio_lyric);
        if (subtitlesUrl.toLowerCase().equals("lrc")) {
            File newFile = new File(NetUtil.getDiskCachePath(this), playerMusic.id + ".lrc");
            if (newFile.exists()) {
                parseLrc(newFile);
            } else {//下载

            }
        } else if (subtitlesUrl.toLowerCase().endsWith("srt")) {
            File newFile = new File(NetUtil.getDiskCachePath(this), playerMusic.id + ".srt");
            if (newFile.exists()) {
                parseSrt(newFile.getAbsolutePath());
            } else {//下载

            }
        }
    }

    private void parseSrt(String absolutePath) {
        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<EditLrcBean> editLrcBeans = SrtParser.parseEditSrt(absolutePath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editLrcAdapter.setNewData(editLrcBeans);
                    }
                });
            }
        });
    }

    private void parseLrc(File lrcFile) {
        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<EditLrcBean> editLrcBeans = SrtParser.parseEditLrc(lrcFile);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editLrcAdapter.setNewData(editLrcBeans);
                    }
                });
            }
        });
    }

    private void initView() {
        RelativeLayout rl_top = findViewById(R.id.rl_top);
        ImmersionBar.with(this).titleBarMarginTop(rl_top).init();
        TextView tv_book_name = findViewById(R.id.tv_book_name);
        TextView tv_chapter_name = findViewById(R.id.tv_chapter_name);
        iv_play = findViewById(R.id.iv_play);
        customSeekBar = findViewById(R.id.csb_progress);
        tv_speed = findViewById(R.id.tv_speed);
        iv_speed_mode = findViewById(R.id.iv_speed_mode);
        ll_set_speed = findViewById(R.id.ll_set_speed);
        cl_main = findViewById(R.id.cl_main);
        rcv_lrc = findViewById(R.id.rcv_lrc);
        if (musicPlayerManager.isPlaying()) {
            iv_play.setImageResource(R.drawable.icon_pause);
        } else {
            iv_play.setImageResource(R.drawable.icon_play);
        }
        editLrcAdapter = new EditLrcAdapter();
        linearLayoutManager = new LinearLayoutManager(this);
        rcv_lrc.setLayoutManager(linearLayoutManager);
        rcv_lrc.setNestedScrollingEnabled(false);
        rcv_lrc.setAdapter(editLrcAdapter);
        String playDuration = playerMusic.playDuration;
        String duration = playerMusic.duration;
        showProgress(Long.parseLong(duration) * 1000, Long.parseLong(playDuration));
        rcv_lrc.post(new Runnable() {
            @Override
            public void run() {
                int bottom = rcv_lrc.getHeight() / 2;
                rcv_lrc.setPadding(0, 0, 0, bottom);
            }
        });
        tv_book_name.setText(playerMusic.cate3_name);
        tv_chapter_name.setText(playerMusic.chapter);

        loadAudioBg(playerMusic.image, playerMusic.audio_cover);
        initSeekBar();

        iv_play.setOnClickListener(this);
        ll_set_speed.setOnClickListener(this);
        findViewById(R.id.iv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput();
                if (!editLrcAdapter.isEdit()) {
                    finish();
                } else {
                    showTipDialog();
                }
            }
        });

        findViewById(R.id.tv_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editLrcAdapter.isEdit()) {
                    showToast("并没有做任何修改哟...");
                    return;
                }
                checkPermission();
            }
        });
        musicPlayerManager.addOnPlayerEventListener(musicPlayerEventListener);
    }

    private void checkPermission() {
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/lyric/status", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                CheckCommitPermissionBean permissionBean = new Gson().fromJson(t, CheckCommitPermissionBean.class);
                hideInput();
                if (!permissionBean.data) {
                    showToast("对不起，没有权限编辑歌词...");
                    return;
                }
                showProgressDialog("提交中...");
                saveSubtitles();
            }
        });
    }

    /**
     * 处理提交数据
     */
    private void saveSubtitles() {
        List<EditLrcBean> lrcList = editLrcAdapter.getLrcList();
        if (lrcList == null || lrcList.isEmpty()) {
            showToast("数据异常");
            return;
        }
        String index = lrcList.get(0).index;
        if (TextUtils.isEmpty(index)) {//lrc
            parseCommitLrc(lrcList);
        } else {//srt
            parseCommitSrt(lrcList);
        }
    }

    /**
     * 处理srt字幕提交数据
     */
    private void parseCommitSrt(List<EditLrcBean> lrcList) {
        StringBuilder stringBuilder = new StringBuilder(lrcList.size());
        for (EditLrcBean item : lrcList) {
            stringBuilder.append(item.index).append("\n").append(item.timeStr).append("\n").append(item.title).append("\n").append("\n");
        }
        commitSrt(stringBuilder.toString());
    }

    /**
     * 开始上传
     *
     * @param lrcString
     */
    private void commitSrt(String lrcString) {
        Map<String, String> map = new HashMap<>();
        map.put("id", playerMusic.id + "");
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("desc", "edit");
        map.put("file", lrcString);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/lyric/save", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                dismissProgressDialog();
                CommitLrcBean commitLrcBean = new Gson().fromJson(t, CommitLrcBean.class);
                if (commitLrcBean.code == 1) {
                    showToast("字幕已提交后台审核...");
                    finish();
                } else {
                    showToast(commitLrcBean.msg);
                }
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                dismissProgressDialog();
                XToast.showToast(EditLrcActivity.this, t);
                finish();
            }
        });
    }

    /**
     * 处理lrc字幕提交数据
     */
    private void parseCommitLrc(List<EditLrcBean> lrcList) {
        StringBuilder stringBuilder = new StringBuilder(lrcList.size());
        for (EditLrcBean item : lrcList) {
            stringBuilder.append(item.timeStr).append(item.title).append("\n");
        }
        commitSrt(stringBuilder.toString());
    }

    private void showTipDialog() {
        DialogUtils.showNormalDialog(this, "提示", "是否放弃编辑字幕？", "取消", "确定", new DialogUtils.onDialogClickListener() {
            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onOk(Dialog dialog) {
                dialog.dismiss();
                finish();
            }
        });
    }

    private void showProgress(long totalDurtion, long currentDurtion) {
        if (totalDurtion == -1 || currentDurtion == -1) {
            return;
        }
        customSeekBar.post(new Runnable() {
            @Override
            public void run() {
                customSeekBar.setMaxProgress((int) (totalDurtion / 1000));
                customSeekBar.progress((int) (currentDurtion / 1000));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play:
                if (musicClickControler.canTrigger()) {
                    musicPlayerManager.playOrPause();
                    if (musicPlayerManager.isPlaying()) {
                        musicPlayerManager.seekTo(customSeekBar.getProgress() * 1000);
                    }
                }
                break;
            case R.id.ll_set_speed:
                hideInput();
                iv_speed_mode.setImageResource(R.drawable.icon_edit_play_speed1);
                initPlaySpeedPop();
                break;
        }
    }

    Float[] speedArray = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 2.25f, 2.5f, 3.0f};

    private void initPlaySpeedPop() {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_play_speed, null);
        RecyclerView rcv_speed = popView.findViewById(R.id.rcv_speed);
        rcv_speed.setLayoutManager(new LinearLayoutManager(this));
        float speed = musicPlayerManager.getSpeed();
        SpeedAdapter speedAdapter = new SpeedAdapter();
        speedAdapter.setCurrentSpeed(speed);
        speedAdapter.setNewData(Arrays.asList(speedArray));
        rcv_speed.setAdapter(speedAdapter);
        PopupWindow playSpeedPop = new PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        speedAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                float speed = (float) adapter.getItem(position);
                tv_speed.setText(speed == 1.0f ? "正常" : speed + "");
                musicPlayerManager.setSpeed(speed);
                playSpeedPop.dismiss();
            }
        });

        playSpeedPop.setTouchable(true);
        playSpeedPop.setFocusable(true);
        ColorDrawable draw = new ColorDrawable(0x00000000);
        playSpeedPop.setBackgroundDrawable(draw);
        playSpeedPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                iv_speed_mode.setImageResource(R.drawable.icon_edit_play_speed);
            }
        });
        playSpeedPop.showAtLocation(ll_set_speed, Gravity.BOTTOM, 0, 0);
    }


    private void initSeekBar() {
        customSeekBar.setMaxProgress(parseInt(playerMusic.duration));//最大进度s
        customSeekBar.setProgressBarHeight(1.0f);//进度条高度dp 默认1.0f
        customSeekBar.setCacheProgressBarHeight(1.5f);//缓存条高度dp 默认1.5f
        customSeekBar.setProgressBarColor(android.R.color.darker_gray);//进度条颜色colorId
        customSeekBar.setCacheProgressBarColor(android.R.color.white);//缓存条颜色colorId
        customSeekBar.setTextBgColor(android.R.color.white);//文字背景颜色colorId
        customSeekBar.setTextColor(android.R.color.black);//字体颜色colorId
        customSeekBar.setTextSize(10);//文字大小sp 默认10sp
        // 设置进度拖动监听
        // 手动拖动进度条会返回当前进度
        customSeekBar.setProgressListener(new CustomSeekBar.IProgressListener() {
            @Override
            public void progress(int progress) {
                if (progress < 0) {
                    progress = 0;
                }
                if (musicPlayerManager.isPlaying()) {
                    musicPlayerManager.seekTo(progress * 1000);
                }
                scrollToPosition(progress * 1000);
            }
        });
    }

    private int parseInt(String str) {
        if (str == null || TextUtils.isEmpty(str.trim())) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    private void loadDefaultBg() {
        //进来先加载默认背景  加载出来后加载音频封面
        Picasso.get()
                .load(R.drawable.icon_audio_default_bg)
                .config(Bitmap.Config.ARGB_4444)
                .transform(new PicassoBlurTransformation(this))
                .error(R.drawable.icon_audio_default_bg)
                .placeholder(R.drawable.icon_audio_default_bg)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        cl_main.setBackground(new BitmapDrawable(getResources(), bitmap));

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }


    private void loadAudioBg(String bookImg, String audioCover) {
        if (!TextUtils.isEmpty(audioCover)) {
            loadBgBlur(audioCover, new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    showBulrBg(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    loadAudioBg(bookImg, "");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else if (!TextUtils.isEmpty(bookImg)) {
            loadBgBlur(bookImg, new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    showBulrBg(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    loadDefaultBg();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    private void showBulrBg(Bitmap bitmap) {
        cl_main.setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    private void loadBgBlur(String img, Target target) {
        Picasso.get()
                .load(img)
                .config(Bitmap.Config.ARGB_4444)
                .transform(new PicassoBlurTransformation(this))
                .resize(200, 200)
                .into(target);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        float playSpeed = PreferencesUtils.getFloat(this, "play_speed", 1.0f);
        musicPlayerManager.setSpeed(playSpeed);
        int playerModel = PreferencesUtils.getInt(this, "play_mode", MusicConstants.MUSIC_MODEL_ORDER);
        musicPlayerManager.setPlayerModel(playerModel);
        musicPlayerManager.removePlayerListener(musicPlayerEventListener);
    }

    MusicPlayerEventListener musicPlayerEventListener = new MusicPlayerEventListener() {

        @Override
        public void onMusicPlayerState(int playerState, String message) {
            switch (playerState) {
                case MusicConstants.MUSIC_PLAYER_COMPLETE:
                    iv_play.setImageResource(R.drawable.icon_play);
                    customSeekBar.setMaxProgress(0);
                    customSeekBar.progress(0);
                    break;
                case MusicConstants.MUSIC_PLAYER_STOP://停止
                    iv_play.setImageResource(R.drawable.icon_play);
                    customSeekBar.setMaxProgress(0);
                    customSeekBar.progress(0);
                    break;
                case MusicConstants.MUSIC_PLAYER_PREPARE://准备
                    iv_play.setImageResource(R.drawable.icon_play);
                    break;
                case MusicConstants.MUSIC_PLAYER_BUFFER://缓冲
                    iv_play.setImageResource(R.drawable.icon_play);
                    break;
                case MusicConstants.MUSIC_PLAYER_PLAYING://播放中
                    iv_play.setImageResource(R.drawable.icon_pause);
                    break;
                case MusicConstants.MUSIC_PLAYER_PAUSE://暂停
                    iv_play.setImageResource(R.drawable.icon_play);
                    break;
                case MusicConstants.MUSIC_PLAYER_ERROR://错误
                    iv_play.setImageResource(R.drawable.icon_play);
                    customSeekBar.setMaxProgress(0);
                    customSeekBar.progress(0);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPrepared(long totalDurtion) {

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
        public void onTaskRuntime(long totalDurtion, long currentDurtion, long alarmResidueDurtion, int bufferProgress) {
            if (totalDurtion == -1 || currentDurtion == -1) return;
            showProgress(totalDurtion, currentDurtion);
            scrollToPosition(currentDurtion);
        }


        @Override
        public void onPlayerConfig(int playModel, int alarmModel, boolean isToast) {

        }
    };


    int lastScrollToPosition = -1;

    private void scrollToPosition(long currentTime) {
        if (editLrcAdapter.isEditing()) {//编辑中不滑动
            return;
        }
        int scrollPosition = getScrollPosition(currentTime);
        if (scrollPosition != lastScrollToPosition) {
            rcv_lrc.post(new Runnable() {
                @Override
                public void run() {
                    editLrcAdapter.setScrollToPosition(scrollPosition);
                    //第一条可见的
                    int firstCompletelyVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    //最后一条可见的
                    int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (scrollPosition < firstCompletelyVisibleItemPosition) {
                        rcv_lrc.scrollToPosition(0);
                    } else {
                        rcv_lrc.scrollToPosition(scrollPosition + ((lastCompletelyVisibleItemPosition - firstCompletelyVisibleItemPosition) / 2) - 4);
                    }
                }
            });
        }
        lastScrollToPosition = scrollPosition;

    }

    protected int getScrollPosition(long currentTime) {
        if (editLrcAdapter == null) {
            return -1;
        }
        int linePos = 0;
        List<EditLrcBean> lrcList = editLrcAdapter.getLrcList();
        if (lrcList != null && !lrcList.isEmpty()) {
            int size = lrcList.size();
            for (int i = 0; i < size; i++) {
                EditLrcBean item = lrcList.get(i);
                if (currentTime >= item.time) {//判断时间是否满足要求
                    if (i == size - 1) {//判断是否最后一行字幕
                        linePos = size - 1;
                    } else if (currentTime < lrcList.get(i + 1).time) {//判断时间小于下一行字幕的的时间
                        linePos = i;
                        break;
                    }
                }
            }
        }
        return linePos;
    }

    @Override
    public void onBackPressed() {
        if (!editLrcAdapter.isEdit()) {
            super.onBackPressed();
        } else {
            showTipDialog();
        }
    }

}
