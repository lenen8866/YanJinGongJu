package com.read.scriptures.audio;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.kymjs.rxvolley.client.HttpCallback;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicOnItemClickListener;
import com.music.player.lib.listener.MusicPlayerEventListener;
import com.music.player.lib.listener.MusicPlayerInfoListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.MusicClickControler;
import com.music.player.lib.util.MusicUtils;
import com.music.player.lib.util.NetUtil;
import com.music.player.lib.util.SaltUtils;
import com.music.player.lib.view.dialog.MusicAlarmSettingDialog;
import com.music.player.lib.view.dialog.MusicPlayerListDialog;
import com.read.scriptures.R;
import com.read.scriptures.bean.AudioCollectBean;
import com.read.scriptures.bean.CheckCommitPermissionBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoBlurTransformation;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SecToTime;
import com.read.scriptures.util.SrtParser;
import com.read.scriptures.util.TextUtil;
import com.read.scriptures.util.UmShareUtils;
import com.read.scriptures.view.BubbleSeekBar;
import com.read.scriptures.view.MyViewPager;
import com.read.scriptures.view.lrc.Lrc;
import com.read.scriptures.view.lrc.LrcHelper;
import com.read.scriptures.view.lrc.LrcView;
import com.read.scriptures.widget.LoadingProgressDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioPlayActivity extends BaseActivity {

    public static final String PLAY_DURATION = "PLAY_DURATION";
    public static final String TOTAL_DURATION = "TOTAL_DURATION";
    public static final String CURRENT_PLAY_AUDIO = "current_play_audio";
    public static final String TOTAL_DURATION_DATA = "TOTAL_DURATION_DATA";//服务器返回的时间

    private BubbleSeekBar customSeekBar;
    private MyViewPager viewPager;
    private TextView tv_title;
    private TextView tv_book;

    private TextView tv_chapter_detail;
    private TextView tv_chapter_detail1;
    private ImageView iv_edit_lrc;
    private TextView tv_top_lrc;
    private TextView tv_bottom_lrc;
    private TextView tv_3_lrc;
    private TextView tv_4_lrc;
    private ImageView iv_like;
    private TextView tv_speed;
    private ImageView iv_share;
    private ImageView iv_rnd;
    private ImageView iv_pre;
    private ImageView iv_next;
    private ImageView iv_play;
    private ImageView iv_menu;
    private TextView tv_time_down;
    private TextView tv_current_time;
    private TextView tv_total_time;
    private TextView tv_author;
    private MusicPlayerManager musicPlayerManager;
    private ImageView ivCover;
    private ImageView iv_ac_bg;
    private TextView tv_refresh;
    private TextView tv_cover_loading;
    private LrcView mlv_lrc;
    private ConstraintLayout ll_top;
    private long seekTime;
    private MusicClickControler musicClickControler;
    private String bookContent;
    private boolean isEnable = true;

    private MusicPlayerEventListener playerEventListener = new MusicPlayerEventListener() {
        @Override
        public void onMusicPlayerState(int playerState, String message) {
            switch (playerState) {
                case MusicConstants.MUSIC_PLAYER_COMPLETE:
                    iv_play.setImageResource(R.drawable.icon_play);
                    customSeekBar.getConfigBuilder().max(0).build();
                    customSeekBar.setProgress(0);
                    tv_current_time.setText("00:00");
                    isEnable = true;
                    break;
                case MusicConstants.MUSIC_PLAYER_STOP://停止
                    iv_play.setImageResource(R.drawable.icon_play);
                    customSeekBar.getConfigBuilder().max(0).build();
                    tv_current_time.setText("00:00");
                    customSeekBar.setProgress(0);
                    isEnable = true;
                    mlv_lrc.updateTime(0);
                    break;
                case MusicConstants.MUSIC_PLAYER_PREPARE://准备
                    iv_play.setImageResource(R.drawable.icon_play);
                    isEnable = false;
                    break;
                case MusicConstants.MUSIC_PLAYER_BUFFER://缓冲
                    iv_play.setImageResource(R.drawable.icon_play);
                    isEnable = false;
                    break;
                case MusicConstants.MUSIC_PLAYER_PLAYING://播放中
                    iv_play.setImageResource(R.drawable.icon_pause);
                    isEnable = true;
                    break;
                case MusicConstants.MUSIC_PLAYER_PAUSE://暂停
                    iv_play.setImageResource(R.drawable.icon_play);
                    isEnable = true;
                    break;
                case MusicConstants.MUSIC_PLAYER_ERROR://错误
                    iv_play.setImageResource(R.drawable.icon_play);
                    tv_current_time.setText("00:00");
                    customSeekBar.getConfigBuilder().max(0).build();
                    customSeekBar.setProgress(0);
                    isEnable = true;
//                    playNext();
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
            if (totalDurtion == -1 || currentDurtion == -1) {
                return;
            }
            tv_current_time.post(new Runnable() {
                @Override
                public void run() {
                    tv_current_time.setText(SecToTime.getTimeString(String.valueOf(currentDurtion / 1000)));
                    tv_total_time.setText(SecToTime.getTimeString(String.valueOf(totalDurtion / 1000)));
                    customSeekBar.getConfigBuilder().max((int) (totalDurtion / 1000)).build();
                    customSeekBar.setProgress((int) (currentDurtion / 1000));

                    mlv_lrc.updateTime(currentDurtion);
                    List<Lrc> lrcData = mlv_lrc.getLrcData();
                    if (lrcData == null || lrcData.isEmpty()) {
                        iv_edit_lrc.setVisibility(View.INVISIBLE);
                        tv_top_lrc.setTextSize(17);
                        tv_top_lrc.setTextColor(TextUtils.isEmpty(bookContent) ? Color.WHITE : Color.WHITE);
                        tv_top_lrc.setText(TextUtils.isEmpty(bookContent) ? "暂无字幕" : bookContent);
                        return;
                    }
                    int currentLine = mlv_lrc.getCurrentLine();
                    if (currentLine > -1 && currentLine < lrcData.size()) {
                        tv_top_lrc.setTextSize(19);
                        tv_top_lrc.setTextColor(Color.parseColor("#FFFF00"));
                        tv_top_lrc.setText(lrcData.get(currentLine).getText());
                    } else {
                        tv_top_lrc.setText("");
                    }

                    int currentLine1 = mlv_lrc.getCurrentLine() + 1;
                    if (currentLine1 > -1 && currentLine1 < lrcData.size()) {
                        tv_bottom_lrc.setText(lrcData.get(currentLine1).getText());
                    } else {
                        tv_bottom_lrc.setText("");
                    }

                    int currentLine2 = mlv_lrc.getCurrentLine() + 2;
                    if (currentLine2 > -1 && currentLine2 < lrcData.size()) {
                        tv_3_lrc.setText(lrcData.get(currentLine2).getText());
                    } else {
                        tv_3_lrc.setText("");
                    }
                    int currentLine3 = mlv_lrc.getCurrentLine() + 3;
                    if (currentLine3 > -1 && currentLine3 < lrcData.size()) {
                        tv_4_lrc.setText(lrcData.get(currentLine3).getText());
                    } else {
                        tv_4_lrc.setText("");
                    }

                    //定时闹钟状态
                    if (null != tv_time_down) {
                        if (alarmResidueDurtion <= 0) {
                            tv_time_down.setText("");
                            return;
                        }
                        if (alarmResidueDurtion <= 60 * 60) {
                            String audioTime = MusicUtils.getInstance().stringForAudioTime(alarmResidueDurtion * 1000);
                            tv_time_down.setText(audioTime);
                        }
                    }
                }
            });
        }

        @Override
        public void onPlayerConfig(int playModel, int alarmModel, boolean isToast) {
        }
    };

    private MusicPlayerInfoListener musicPlayerInfoListener = new MusicPlayerInfoListener() {
        @Override
        public void onPlayMusiconInfo(BaseAudioInfo musicInfo, int position) {
            refreshUI(musicInfo);
        }
    };
    private ShowImageDialog showImageDialog;
    private BaseAudioInfo singeBean;

    LoadingProgressDialog parpareDialog = null;
    private boolean cancelByUser = false;

    private Handler perpareHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == 1111 && musicPlayerManager!=null){
                int playerState = musicPlayerManager.getPlayerState();
                Log.w("TTT","playerState:"+playerState);
                if(playerState== MusicConstants.MUSIC_PLAYER_PREPARE || playerState ==  MusicConstants.MUSIC_PLAYER_BUFFER){
                    //准备中或缓冲中
                    if(parpareDialog == null && !cancelByUser){
                        parpareDialog = new LoadingProgressDialog(AudioPlayActivity.this, "音频加载中,请稍后...");
                        parpareDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    try {
                                        parpareDialog.dismiss();
                                        cancelByUser = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return false;
                            }
                        });
                    }
                    if(!parpareDialog.isShowing() && !cancelByUser) {
                        parpareDialog.show();
                    }
                }else{
                    if(parpareDialog != null && parpareDialog.isShowing()){
                        parpareDialog.dismiss();
                    }
                }
                perpareHandler.sendEmptyMessageDelayed(1111,1000);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(perpareHandler!=null){
            perpareHandler.removeMessages(1111);
            perpareHandler.sendEmptyMessageDelayed(1111,1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(perpareHandler!=null){
            perpareHandler.removeMessages(1111);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_in, R.anim.no_anim);
        setContentView(R.layout.ac_audio_play);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        musicPlayerManager = MusicPlayerManager.getInstance().init(this);

        initView();
        ImmersionBar.with(this).titleBar(ll_top).init();
        initPlayerData();
        initGetData();
        musicClickControler = new MusicClickControler();
        musicClickControler.init(1, 500);
        initPermissionData();
    }

    private void initPermissionData() {
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/lyric/status", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                CheckCommitPermissionBean permissionBean = new Gson().fromJson(t, CheckCommitPermissionBean.class);
                if (permissionBean.data) {
                    iv_edit_lrc.setVisibility(View.VISIBLE);
                } else {
                    iv_edit_lrc.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initPlayerData() {
        //获取设置的播放模式
        int playerModel = PreferencesUtils.getInt(this, "play_mode", MusicConstants.MUSIC_MODEL_ORDER);
        switch (playerModel) {
            case MusicConstants.MUSIC_MODEL_SINGLE://单曲循环
                iv_rnd.setImageResource(R.drawable.icon_single);
                break;
            case MusicConstants.MUSIC_MODEL_ORDER://顺序播放
                iv_rnd.setImageResource(R.drawable.icon_line);
                break;
            case MusicConstants.MUSIC_MODEL_RANDOM://随机播放
                iv_rnd.setImageResource(R.drawable.icon_rnd);
                break;
        }
        musicPlayerManager.setPlayerModel(playerModel);
        if (musicPlayerManager.isPlaying()) {
            iv_play.setImageResource(R.drawable.icon_pause);
        } else {
            iv_play.setImageResource(R.drawable.icon_play);
        }
        tv_speed.setText(musicPlayerManager.getSpeed() == 1.0f ? "正常" : musicPlayerManager.getSpeed() + "");
        Log.w("TTT","musicPlayerManager.getPlayerState():"+musicPlayerManager.getPlayerState());
    }


    private void initGetData() {
        //获取初始化选中的音频
        singeBean = musicPlayerManager.getCurrentPlayerMusic();
        int currentDuration = 0;
        int maxDuration = 0;
        if (singeBean == null) {//没有播放
            singeBean = (BaseAudioInfo) getIntent().getSerializableExtra(CURRENT_PLAY_AUDIO);
            if (singeBean != null) {
                currentDuration = parseInt(singeBean.playDuration);
                maxDuration = parseInt(singeBean.duration) * 1000;
            }
        }
//        else {
//            currentDuration = getIntent().getIntExtra(PLAY_DURATION, 0);
//            maxDuration = getIntent().getIntExtra(TOTAL_DURATION, 0);
//        }
        if (currentDuration / 1000 >= maxDuration / 1000) {
            currentDuration = 0;
        }
        if (maxDuration == 0 && singeBean != null) {
            maxDuration = parseInt(singeBean.duration) * 1000;
        }
        tv_current_time.setText(SecToTime.getTimeString(String.valueOf(currentDuration / 1000)));
        tv_total_time.setText(SecToTime.getTimeString(String.valueOf(maxDuration / 1000)));
        customSeekBar.getConfigBuilder().max(maxDuration / 1000).build();
        customSeekBar.setProgress(currentDuration / 1000);
        refreshUI(singeBean);

        musicPlayerManager.addOnPlayerEventListener(playerEventListener);
        musicPlayerManager.addPlayInfoListener(musicPlayerInfoListener);

//        onPreparingHandler.removeMessages(1);
//        onPreparingHandler.sendEmptyMessageDelayed(1,1000);

    }


    private long parseLong(String str) {
        if (str == null || TextUtils.isEmpty(str.trim())) {
            return 0L;
        }
        return Long.parseLong(str);
    }

    private int parseInt(String str) {
        if (str == null || TextUtils.isEmpty(str.trim())) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_book = findViewById(R.id.tv_book);
        tv_speed = findViewById(R.id.tv_speed);
        iv_like = findViewById(R.id.iv_like);
        iv_share = findViewById(R.id.iv_share);
        iv_rnd = findViewById(R.id.iv_rnd);
        iv_pre = findViewById(R.id.iv_pre);
        iv_next = findViewById(R.id.iv_next);
        iv_play = findViewById(R.id.iv_play);
        iv_menu = findViewById(R.id.iv_menu);
        tv_time_down = findViewById(R.id.tv_time_down);
        viewPager = findViewById(R.id.iv_disc_rotate);
        iv_ac_bg = findViewById(R.id.iv_ac_bg);
        ll_top = findViewById(R.id.ll_top);
        tv_current_time = findViewById(R.id.tv_current_time);
        tv_total_time = findViewById(R.id.tv_total_time);
        tv_author = findViewById(R.id.tv_author);

        customSeekBar = findViewById(R.id.seekbar);

        customSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                super.getProgressOnActionUp(bubbleSeekBar, progress, progressFloat);
                int time = progress * 1000;
                tv_current_time.setText(SecToTime.getTimeString(progress + ""));
                showLrc(time);
                musicPlayerManager.seekTo(time);
            }
        });

        ArrayList<View> views = new ArrayList<>();
        View view1 = getLayoutInflater().inflate(R.layout.vp_img_item, null);
        ivCover = view1.findViewById(R.id.iv_bg);
        tv_refresh = view1.findViewById(R.id.tv_refresh);
        tv_cover_loading = view1.findViewById(R.id.tv_cover_loading);
        tv_chapter_detail = view1.findViewById(R.id.tv_chapter_detail);
        tv_top_lrc = view1.findViewById(R.id.tv_top_lrc);
        tv_bottom_lrc = view1.findViewById(R.id.tv_bottom_lrc);
        tv_3_lrc = view1.findViewById(R.id.tv_3_lrc);
        tv_4_lrc = view1.findViewById(R.id.tv_4_lrc);
        view1.findViewById(R.id.ll_page_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1, false);
            }
        });
        View view2 = getLayoutInflater().inflate(R.layout.view_audio_lrc, null);
        mlv_lrc = view2.findViewById(R.id.mlv_lrc);
        tv_chapter_detail1 = view2.findViewById(R.id.tv_chapter_detail1);
        iv_edit_lrc = view2.findViewById(R.id.iv_edit_lrc);
        mlv_lrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0, false);
            }
        });
        iv_edit_lrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AudioPlayActivity.this, EditLrcActivity.class);
                BaseAudioInfo playerMusic = musicPlayerManager.getCurrentPlayerMusic();
                if (playerMusic == null) {
                    intent.putExtra("edit_audio_data", singeBean);
                    singeBean.playDuration = customSeekBar.getProgress() * 1000 + "";
                } else {
                    playerMusic.playDuration = customSeekBar.getProgress() * 1000 + "";
                    intent.putExtra("edit_audio_data", playerMusic);
                }
                startActivity(intent);
            }
        });
        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_cover_loading.setVisibility(View.VISIBLE);
                BaseAudioInfo currentPlayerMusic = musicPlayerManager.getCurrentPlayerMusic();
                loadImage1(currentPlayerMusic.image, currentPlayerMusic.audio_cover);
            }
        });
//        mlv_lrc.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//
//                        break;
//                    case MotionEvent.ACTION_UP:
//
//                        break;
//                }
//                return false;
//            }
//        });


        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.icon_play_lrc);
        drawable.setTint(ContextCompat.getColor(this, R.color.main_color));
        mlv_lrc.setIconWidth(DensityUtil.dip2px(this, 25));
        mlv_lrc.setIconHeight(DensityUtil.dip2px(this, 25));
        mlv_lrc.setPlayDrawable(drawable);
        mlv_lrc.setEmptyContent("加载中...");
        views.add(view1);
        views.add(view2);
        ViewAdapter adapter = new ViewAdapter(views);
        viewPager.setAdapter(adapter);


        mlv_lrc.setOnPlayIndicatorLineListener(new LrcView.OnPlayIndicatorLineListener() {
            @Override
            public void onPlay(long time, String content) {
                seekTime = time;
                musicPlayerManager.seekTo(seekTime);
            }
        });
        findViewById(R.id.iv_back).setOnClickListener(this);
        iv_like.setOnClickListener(this);
        ivCover.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_rnd.setOnClickListener(this);
        tv_speed.setOnClickListener(this);
        iv_pre.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
        tv_time_down.setOnClickListener(this);
        tv_title.setOnClickListener(this);
        tv_book.setOnClickListener(this);

        initPlayModePop();
    }

    private void showLrc(long currentDurtion) {
        mlv_lrc.post(new Runnable() {
            @Override
            public void run() {
                mlv_lrc.updateTime(currentDurtion);
                List<Lrc> lrcData = mlv_lrc.getLrcData();
                int currentLine = mlv_lrc.getCurrentLine();
                if (lrcData != null && !lrcData.isEmpty()) {
                    if (currentLine > -1 && currentLine < lrcData.size()) {
                        tv_top_lrc.setTextSize(19);
                        tv_top_lrc.setTextColor(Color.parseColor("#FFFF00"));
                        tv_top_lrc.setText(lrcData.get(currentLine).getText());
                    }
                } else {
                    iv_edit_lrc.setVisibility(View.INVISIBLE);
                    tv_top_lrc.setTextSize(17);
                    tv_top_lrc.setTextColor(TextUtils.isEmpty(bookContent) ? Color.WHITE : Color.WHITE);
                    tv_top_lrc.setText(TextUtils.isEmpty(bookContent) ? "暂无字幕" : bookContent);
                    return;
                }

                if (tv_bottom_lrc != null) {
                    int currentLine1 = mlv_lrc.getCurrentLine() + 1;
                    if (!lrcData.isEmpty()) {
                        if (currentLine1 > -1 && currentLine1 < lrcData.size()) {
                            tv_bottom_lrc.setText(lrcData.get(currentLine1).getText());
                        }
                    }
                }

                if (tv_3_lrc != null) {
                    int currentLine2 = mlv_lrc.getCurrentLine() + 2;
                    if (!lrcData.isEmpty()) {
                        if (currentLine2 > -1 && currentLine2 < lrcData.size()) {
                            tv_3_lrc.setText(lrcData.get(currentLine2).getText());
                        }
                    }
                }

                if (tv_4_lrc != null) {
                    int currentLine3 = mlv_lrc.getCurrentLine() + 3;
                    if (!lrcData.isEmpty()) {
                        if (currentLine3 > -1 && currentLine3 < lrcData.size()) {
                            tv_4_lrc.setText(lrcData.get(currentLine3).getText());
                        }
                    }
                }
            }
        });
    }

    private void refreshUI(BaseAudioInfo rowsBean) {
        if (rowsBean == null) {
            return;
        }
        if (isFinishing() || isDestroyed()) {
            return;
        }
        tv_book.setText(rowsBean.cate1_name + "-" + rowsBean.cate2_name);
        tv_title.setText(rowsBean.cate3_name);
        if (tv_chapter_detail != null) {
            tv_chapter_detail.setText(rowsBean.chapter);
        }
        if (tv_chapter_detail1 != null) {
            tv_chapter_detail1.setText(rowsBean.chapter);
        }
        bookContent = rowsBean.content;
        iv_like.setImageResource(rowsBean.collect == 1 ? R.drawable.icon_collected : R.drawable.icon_collect);
        //封面
        loadImage1(rowsBean.image, rowsBean.audio_cover);
        ///歌词
        loadLrc(String.valueOf(rowsBean.id), rowsBean.audio_lyric);
        tv_author.setText(rowsBean.author);

        loadAudioBg(rowsBean.image, rowsBean.audio_cover);
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
                        iv_ac_bg.setBackground(new BitmapDrawable(getResources(), bitmap));

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
        iv_ac_bg.setImageBitmap(bitmap);
//        iv_ac_bg.animate().alpha(0.4f).setDuration(1000).withEndAction(new Runnable() {
//            @Override
//            public void run() {
//                iv_ac_bg.animate().alpha(1.0f).setDuration(1000).withStartAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        iv_ac_bg.setImageBitmap(bitmap);
//                    }
//                }).start();
//            }
//        }).start();
    }

    private void loadBgBlur(String img, Target target) {
        Picasso.get()
                .load(img)
                .config(Bitmap.Config.ARGB_4444)
                .transform(new PicassoBlurTransformation(this))
                .resize(200, 200)
                .into(target);
    }

    private void loadLrc(String audioId, String audio_lyric) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        audio_lyric = SaltUtils.getUrl(audio_lyric);
        if (!TextUtils.isEmpty(audio_lyric)) {
            if (audio_lyric.endsWith("lrc") || audio_lyric.endsWith("LRC")) {
                downloadLrc(audioId, audio_lyric);
            } else if (audio_lyric.endsWith("srt") || audio_lyric.endsWith("SRT")) {
                downloadSrt(audioId, audio_lyric);
            } else {
                iv_edit_lrc.setVisibility(View.INVISIBLE);
                mlv_lrc.setEmptyContent(emptyWords);
                showNoWords();
            }
        } else {
            iv_edit_lrc.setVisibility(View.INVISIBLE);
            mlv_lrc.setEmptyContent(emptyWords);
            showNoWords();
        }
    }

    private void showNoWords() {
        tv_top_lrc.setTextSize(17);
        tv_top_lrc.setTextColor(TextUtils.isEmpty(bookContent) ? Color.WHITE : Color.WHITE);
        tv_top_lrc.setText(TextUtils.isEmpty(bookContent) ? "暂无字幕" : bookContent);
    }

    public String emptyWords = "暂无字幕";

    private void downloadLrc(String audioId, String audio_lyric) {
        NetUtil.downloadLrc(this, audio_lyric, audioId, "lrc", new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                File file = new File(t);
                if (!file.exists()) {
                    iv_edit_lrc.setVisibility(View.INVISIBLE);
                    mlv_lrc.setEmptyContent(emptyWords);
                    showNoWords();
                } else {
                    ArrayList<Lrc> lrcs = LrcHelper.parseLrcFromFile(file);
                    if (lrcs == null || lrcs.isEmpty()) {
                        showNoWords();
                        iv_edit_lrc.setVisibility(View.INVISIBLE);
                        mlv_lrc.setEmptyContent(emptyWords);
                        return;
                    }
                    iv_edit_lrc.setVisibility(View.VISIBLE);
                    mlv_lrc.setLrcData(lrcs);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                showNoWords();
                iv_edit_lrc.setVisibility(View.INVISIBLE);
                mlv_lrc.setEmptyContent(emptyWords);
            }
        });
    }


    private void downloadSrt(String audioId, String audio_lyric) {
        NetUtil.downloadLrc(this, audio_lyric, audioId, "srt", new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                File file = new File(t);
                if (!file.exists()) {
                    showNoWords();
                    iv_edit_lrc.setVisibility(View.INVISIBLE);
                    mlv_lrc.setEmptyContent(emptyWords);
                } else {
                    ArrayList<Lrc> lrcs = SrtParser.parseSrt(file);
                    if (lrcs.isEmpty()) {
                        showNoWords();
                        iv_edit_lrc.setVisibility(View.INVISIBLE);
                        mlv_lrc.setEmptyContent(emptyWords);
                        return;
                    }
                    iv_edit_lrc.setVisibility(View.VISIBLE);
                    mlv_lrc.setLrcData(lrcs);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                showNoWords();
                iv_edit_lrc.setVisibility(View.INVISIBLE);
                mlv_lrc.setEmptyContent(emptyWords);
            }
        });

    }

    private PopupWindow playModePop;
    TextView tv_line_mode;
    TextView tv_rnd_mode;
    TextView tv_single_mode;

    private void initPlayModePop() {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_play_mode, null);
        tv_line_mode = popView.findViewById(R.id.tv_line_mode);
        tv_rnd_mode = popView.findViewById(R.id.tv_rnd_mode);
        tv_single_mode = popView.findViewById(R.id.tv_single_mode);
        tv_line_mode.setOnClickListener(this);
        tv_rnd_mode.setOnClickListener(this);
        tv_single_mode.setOnClickListener(this);

        playModePop = new PopupWindow(popView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        playModePop.setTouchable(true);
        playModePop.setFocusable(true);
        ColorDrawable draw = new ColorDrawable(0x00000000);
        playModePop.setBackgroundDrawable(draw);
        playModePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    Float[] speedArray = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 2.25f, 2.5f, 3.0f};

    private void initPlaySpeedPop() {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_play_speed, null);
        RecyclerView rcv_speed = popView.findViewById(R.id.rcv_speed);
        rcv_speed.setLayoutManager(new GridLayoutManager(this, 3));
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
                PreferencesUtils.putFloat(AudioPlayActivity.this, "play_speed", speed);
            }
        });

        playSpeedPop.setTouchable(true);
        playSpeedPop.setFocusable(true);
        ColorDrawable draw = new ColorDrawable(0x00000000);
        playSpeedPop.setBackgroundDrawable(draw);
        playSpeedPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                musicPlayerManager.setSpeed(speedValue);
            }
        });
        playSpeedPop.showAtLocation(iv_rnd, Gravity.BOTTOM, 0, 0);
    }


    private void animateView(View view) {
        view.animate().scaleX(0.8f).scaleY(0.8f).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate().scaleX(1.0f).scaleY(1.0f).start();
            }
        }).setDuration(100).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_title://返回
            case R.id.tv_book://返回
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.iv_like://点赞
                animateView(view);
                if (!NetUtil.isNetWorkAvailable(AudioPlayActivity.this)) {
                    showToast("请连接网络~");
                    return;
                }
                if (musicClickControler.canTrigger()) {
                    parseAudio();
                }
                break;
            case R.id.iv_share://分享
                animateView(view);
                shareAudio();
                break;
            case R.id.iv_rnd://播放模式
                animateView(view);
                if (musicClickControler.canTrigger()) {
                    setPlayModel();
                }
                break;
            case R.id.tv_speed://播放速度
                animateView(view);
                if (musicClickControler.canTrigger()) {
                    setPlaySpeed();
                }
                break;
            case R.id.iv_pre://上一首
                animateView(view);
                if (musicClickControler.canTrigger()) {
                    playLast();
                }
                break;
            case R.id.iv_play://播放
//                if (!isEnable) {
//                    showToast("音频加载中,请稍后...");
//                }
                cancelByUser = false;
                animateView(view);
                if (musicClickControler.canTrigger()) {
                    musicPlayerManager.playOrPause();
                }
                break;
            case R.id.iv_next://下一首
                animateView(view);
                if (musicClickControler.canTrigger()) {
                    playNext();
                }
                break;
            case R.id.iv_menu://播放列表
                animateView(view);
                if (musicClickControler.canTrigger()) {
                    showPlayList();
                }
                break;
            case R.id.tv_time_down://倒计时
                animateView(view);
                if (musicClickControler.canTrigger()) {
                    setTimeDown();
                }
                break;
            case R.id.tv_line_mode://顺序播放
                animateView(view);
                musicPlayerManager.setPlayerModel(MusicConstants.MUSIC_MODEL_ORDER);
                iv_rnd.setImageResource(R.drawable.icon_line);
                playModePop.dismiss();
                PreferencesUtils.putInt(this, "play_mode", MusicConstants.MUSIC_MODEL_ORDER);
                break;
            case R.id.tv_rnd_mode://随机播放
                animateView(view);
                musicPlayerManager.setPlayerModel(MusicConstants.MUSIC_MODEL_RANDOM);
                iv_rnd.setImageResource(R.drawable.icon_rnd);
                playModePop.dismiss();
                PreferencesUtils.putInt(this, "play_mode", MusicConstants.MUSIC_MODEL_RANDOM);
                break;
            case R.id.tv_single_mode://单曲循环
                animateView(view);
                musicPlayerManager.setPlayerModel(MusicConstants.MUSIC_MODEL_SINGLE);
                iv_rnd.setImageResource(R.drawable.icon_single);
                playModePop.dismiss();
                PreferencesUtils.putInt(this, "play_mode", MusicConstants.MUSIC_MODEL_SINGLE);
                break;
            case R.id.iv_bg:
                if (showImageDialog == null) {
                    showImageDialog = new ShowImageDialog();
                }
                if (!showImageDialog.isAdded() && !showImageDialog.isVisible() && getSupportFragmentManager().findFragmentByTag("showImageDialog") == null) {
                    showImageDialog.setData(img);
                    showImageDialog.show(getSupportFragmentManager(), "showImageDialog");

                }
                break;
        }
    }

    /**
     * 设置倒计时
     */
    private void setTimeDown() {
        MusicAlarmSettingDialog.getInstance(this).
                setOnAlarmModelListener(new MusicAlarmSettingDialog.OnAlarmModelListener() {
                    @Override
                    public void onAlarmModel(int alarmModel) {
                        final int musicAlarmModel = MusicPlayerManager.getInstance().setPlayerAlarmModel(alarmModel);
                        setPlayerConfig(musicAlarmModel);
                    }
                }).show();
    }

    /**
     * 显示播放列表
     */
    private void showPlayList() {
        MusicPlayerListDialog
                .getInstance(this)
                .setMusicOnItemClickListener(new MusicOnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, long musicID) {
                        cancelByUser = false;
                        musicPlayerManager.startPlayMusic(position);
                    }

                    @Override
                    public void onItemDeleteClick(int position) {
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
//                                    getNextChapter();
                                } else {
                                    cancelByUser = false;
                                    musicPlayerManager.startPlayMusic(nextIndex);
                                }
                            }
                            musicPlayerManager.getCurrentPlayList().remove(position);
                        }
                    }
                }).show();
    }


    /**
     * 播放下一首
     */
    private void playNext() {
        int nextPlayIndex = musicPlayerManager.playNextIndex();
        if (nextPlayIndex == -1) {
            showToast("已经是最后一章了");
        } else {
            cancelByUser = false;
            musicPlayerManager.startPlayMusic(nextPlayIndex);
        }
    }

    /**
     * 播放上一首
     */
    private void playLast() {
        int lastPlayIndex = musicPlayerManager.playLastIndex();
        if (lastPlayIndex < 0) {
            lastPlayIndex = 0;
        }
        cancelByUser = false;
        musicPlayerManager.startPlayMusic(lastPlayIndex);
    }


    /**
     * 快进 点一次 进15s
     */
    private void kuaijin() {
        long durtion = customSeekBar.getProgress();
        long time = durtion + 15;
        BaseAudioInfo currentPlayerMusic = musicPlayerManager.getCurrentPlayerMusic();
        if (currentPlayerMusic != null) {
            long audioDurtion = parseLong(currentPlayerMusic.duration);
            musicPlayerManager.seekTo(Math.min(time, audioDurtion) * 1000);
            if (!musicPlayerManager.isPlaying()) {
                musicPlayerManager.play();
            }
        }
    }

    /**
     * 快退 点一次 退15s
     */
    private void kuaitui() {
        long durtion = customSeekBar.getProgress();
        long time = durtion - 15;
        musicPlayerManager.seekTo(time > 0 ? time * 1000 : 0);
        if (!musicPlayerManager.isPlaying()) {
            musicPlayerManager.play();
        }
    }

    /**
     * 设置播放速度
     */
    private void setPlaySpeed() {
        initPlaySpeedPop();
    }


    /**
     * 设置播放模式
     */
    private void setPlayModel() {
        int playerModel = musicPlayerManager.getPlayerModel();
        switch (playerModel) {
            case MusicConstants.MUSIC_MODEL_SINGLE://单曲循环
                tv_single_mode.setBackgroundColor(Color.parseColor("#f6f6f6"));
                tv_rnd_mode.setBackgroundColor(Color.WHITE);
                tv_line_mode.setBackgroundColor(Color.WHITE);
                break;
            case MusicConstants.MUSIC_MODEL_ORDER://顺序播放
                tv_line_mode.setBackgroundColor(Color.parseColor("#f6f6f6"));
                tv_rnd_mode.setBackgroundColor(Color.WHITE);
                tv_single_mode.setBackgroundColor(Color.WHITE);
                break;
            case MusicConstants.MUSIC_MODEL_RANDOM://随机播放
                tv_rnd_mode.setBackgroundColor(Color.parseColor("#f6f6f6"));
                tv_line_mode.setBackgroundColor(Color.WHITE);
                tv_single_mode.setBackgroundColor(Color.WHITE);
                break;
        }
        playModePop.showAtLocation(iv_rnd, Gravity.BOTTOM | Gravity.LEFT, 0, 0);
    }


    /**
     * 分享
     */
    private void shareAudio() {
        BaseAudioInfo currentAudio = musicPlayerManager.getCurrentPlayerMusic();
        if (currentAudio == null) {
            showToast("暂时无法分享");
            return;
        }
        UmShareUtils.shareMusic(this, SaltUtils.getUrl(currentAudio.audio_url), currentAudio.chapter, currentAudio.cate3_name + "-" + currentAudio.cate2_name + "-" + currentAudio.cate1_name, currentAudio.image, SaltUtils.getUrl(currentAudio.audio_url));
    }

    /**
     * 点赞
     */
    private void parseAudio() {
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("value", String.valueOf(musicPlayerManager.getCurrentPlayerMusic().id));
        map.put("type", "1");
        NetUtil.get(ZConfig.GET_ADD_COLLECT, map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                AudioCollectBean bean = new Gson().fromJson(t, AudioCollectBean.class);
                iv_like.setImageResource(bean.data.status == 1 ? R.drawable.icon_collected : R.drawable.icon_collect);
                BaseAudioInfo currentAudio = (BaseAudioInfo) musicPlayerManager.getCurrentPlayerMusic();
                currentAudio.collect = bean.data.status;
                showToastMsg(bean.msg);
            }
        });
    }

    /**
     * 播放器配置
     *
     * @param alarmModel
     */
    private synchronized void setPlayerConfig(int alarmModel) {
        if (null != tv_time_down) {
            if (alarmModel == MusicConstants.MUSIC_ALARM_MODEL_0) {
                tv_time_down.setText("");
            } else {
                String durtion = "00:00";
                if (alarmModel == MusicConstants.MUSIC_ALARM_MODEL_10) {
                    durtion = "10:00";
                } else if (alarmModel == MusicConstants.MUSIC_ALARM_MODEL_15) {
                    durtion = "15:00";
                } else if (alarmModel == MusicConstants.MUSIC_ALARM_MODEL_30) {
                    durtion = "30:00";
                } else if (alarmModel == MusicConstants.MUSIC_ALARM_MODEL_60) {
                    durtion = "60:00";
                } else if (alarmModel == MusicConstants.MUSIC_ALARM_MODEL_CURRENT) {
                    durtion = "00:00";
                }
                tv_time_down.setText(durtion);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        if (playerEventListener != null) {
            musicPlayerManager.removePlayerListener(playerEventListener);
        }
        if (musicPlayerInfoListener != null) {
            musicPlayerManager.removePlayInfoListener(musicPlayerInfoListener);
        }
        if(perpareHandler!=null){
            perpareHandler.removeMessages(1111);
            perpareHandler = null;
        }
    }

    Handler loadCoverImageHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            BaseAudioInfo currentPlayerMusic = musicPlayerManager.getCurrentPlayerMusic();
            loadCoverImage(currentPlayerMusic.image,currentPlayerMusic.image, currentPlayerMusic.audio_cover);
        }
    };

    private void loadCoverImage(String imageUrl,String bookImg, String audio_cover){
        Glide.with(this) .load(imageUrl) .placeholder(R.drawable.icon_play_deault_bg).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                if(imageUrl.equals(audio_cover) && !audio_cover.equals(bookImg)){
                    loadCoverImageHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadCoverImageHandler.sendMessage(Message.obtain());
                        }
                    });
//                    loadCoverImage(bookImg,bookImg,audio_cover);
                }else{
                    tv_cover_loading.setVisibility(View.GONE);
                    tv_refresh.setVisibility(View.VISIBLE);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                tv_cover_loading.setVisibility(View.GONE);
                return false;
            }
        }).into(ivCover);
    }
    private String img;

    private void loadImage1(String bookImg, String audio_cover) {
        img = TextUtils.isEmpty(audio_cover) ? bookImg : audio_cover;
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (TextUtils.isEmpty(bookImg) && TextUtils.isEmpty(audio_cover)) {
            tv_cover_loading.setVisibility(View.GONE);
            PicassoUtils.loadImage(ivCover, R.drawable.icon_play_deault_bg, DensityUtil.dip2px(200), DensityUtil.dip2px(230));
            return;
        }
        String imageUrl = audio_cover;
        if(TextUtils.isEmpty(imageUrl)){
            imageUrl = bookImg;
        }
        loadCoverImage(imageUrl,bookImg,audio_cover);
       /* if (TextUtils.isEmpty(audio_cover)) {//音频封面为空，直接加载书籍封面
            Picasso.get()
                    .load(bookImg)
                    .resize(DensityUtil.dip2px(200), DensityUtil.dip2px(230))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            tv_cover_loading.setVisibility(View.GONE);
                            ivCover.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {//音频封面加载失败
                            tv_cover_loading.setVisibility(View.GONE);
                            PicassoUtils.loadImage(ivCover, R.drawable.icon_play_deault_bg, DensityUtil.dip2px(200), DensityUtil.dip2px(230));
                            if (TextUtils.isEmpty(bookImg) && TextUtils.isEmpty(audio_cover)) {
                                return;
                            }
                            tv_refresh.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        } else {//否则先加音频封面
            Picasso.get()
                    .load(audio_cover)
                    .resize(DensityUtil.dip2px(200), DensityUtil.dip2px(230))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            tv_cover_loading.setVisibility(View.GONE);
                            ivCover.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {//音频封面加载失败
                            loadImage2(bookImg);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        }*/
    }

    private void loadImage2(String bookImg) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        Picasso.get()
                .load(bookImg)
                .resize(DensityUtil.dip2px(200), DensityUtil.dip2px(230))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        tv_cover_loading.setVisibility(View.GONE);
                        ivCover.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {//音频封面加载失败
                        tv_cover_loading.setVisibility(View.GONE);
                        PicassoUtils.loadImage(ivCover, R.drawable.icon_play_deault_bg, DensityUtil.dip2px(200), DensityUtil.dip2px(230));
                        if (TextUtils.isEmpty(bookImg)) {
                            return;
                        }
                        tv_refresh.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_anim, R.anim.exit_in);
    }
}
