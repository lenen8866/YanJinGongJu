package com.read.scriptures.video;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.NetUtil;
import com.music.player.lib.util.SaltUtils;
import com.read.scriptures.R;
import com.read.scriptures.audio.ShowImageDialog;
import com.read.scriptures.bean.AudioCollectBean;
import com.read.scriptures.bean.VideoListBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.DialogUtils;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SharedPreferencesUtils;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.TimeUtils;
import com.read.scriptures.util.UmShareUtils;
import com.read.scriptures.view.CustomMovementMethod;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoPlayActivity extends BaseActivity {

    public static final String VIDEO_CATE = "VIDEO_CATE";
    private String videoItemId;
    private VideoListBean videoListBean;
    private GSYVideoOptionBuilder gsyVideoOption;
    private String videoBookId;
    private String bookCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_video_play);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        StatusBarUtils.setColor(this, Color.BLACK);
        MusicPlayerManager instance = MusicPlayerManager.getInstance();
        if (instance != null) {
            if (instance.getCurrentPlayerMusic() != null) {
                instance.pause();
                EventBus.getDefault().post("audio_chapter_no_cache");
            }
        }
        initView();
        initData();
    }

    private VideoPlayListAdapter videoPlayListAdapter;

    private TextView tv_video_count;
    private TextView tv_video_content;
    private TextView tv_da_gang;
    private TextView tv_video_cate;
    private TextView tv_more;
    private TextView tv_current_video_title;
    private RecyclerView rcv_video_list;
    private GsyVideoPlayer detailPlayer;
    private ImageView iv_collect;
    private LinearLayout ll_content;
    private ImageView iv_show;
    protected OrientationUtils orientationUtils;
    private ImageView imageView;
    private LinearLayout ll_main;
    private ShowImageDialog showImageDialog;
    private boolean isLight;


    private void initView() {
        tv_video_count = findViewById(R.id.tv_video_count);
        rcv_video_list = findViewById(R.id.rcv_video_list);
        detailPlayer = findViewById(R.id.detail_player);
        tv_video_content = findViewById(R.id.tv_video_content);
        tv_video_cate = findViewById(R.id.tv_video_cate);
        tv_current_video_title = findViewById(R.id.tv_current_video_title);
        iv_collect = findViewById(R.id.iv_collect);
        ll_content = findViewById(R.id.ll_content);
        iv_show = findViewById(R.id.iv_show);
        ll_main = findViewById(R.id.ll_main);
        tv_da_gang = findViewById(R.id.tv_da_gang);
        tv_more = findViewById(R.id.tv_more);

        rcv_video_list.setLayoutManager(new LinearLayoutManager(VideoPlayActivity.this));
        videoPlayListAdapter = new VideoPlayListAdapter();
        rcv_video_list.setAdapter(videoPlayListAdapter);
        rcv_video_list.setNestedScrollingEnabled(false);

        videoPlayListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (currentPlayIndex == position) {
                    return;
                }
                VideoListBean.RowsBean item = videoPlayListAdapter.getItem(position);
                currentPlayIndex = position;
                playVideo(item);
            }
        });
        videoPlayListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_cover:
                        if (showImageDialog == null) {
                            showImageDialog = new ShowImageDialog();
                        }
                        if (!showImageDialog.isAdded() && !showImageDialog.isVisible() && getSupportFragmentManager().findFragmentByTag("showImageDialog") == null) {
                            showImageDialog.setData(TextUtils.isEmpty(videoPlayListAdapter.getItem(position).video_cover) ? bookCover : videoPlayListAdapter.getItem(position).video_cover);
                            showImageDialog.show(getSupportFragmentManager(), "showImageDialog");
                        }
                        break;
                }
            }
        });
        GSYVideoType.enableMediaCodec();
        GSYVideoType.enableMediaCodecTexture();
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        //增加封面
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //增加title
//        detailPlayer.getTitleTextView().setVisibility(View.GONE);
//        detailPlayer.getBackButton().setVisibility(View.GONE);
        detailPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        gsyVideoOption = new GSYVideoOptionBuilder();
        //获取视频播放速度
        playSpeed = PreferencesUtils.getFloat(VideoPlayActivity.this, "play_video_speed", 1.0f);
        ((GsyVideoPlayer) detailPlayer.getCurrentPlayer()).setShowSpeed(playSpeed);
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(false)
                .setIsTouchWigetFull(false)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setCacheWithPlay(true)
                .setDismissControlTime(1000)
                .setSpeed(playSpeed)
                .setShowDragProgressTextOnSeekBar(true)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //获取当前视频播放进度
                        long cachePlayTime = PreferencesUtils.getLong(VideoPlayActivity.this, "video_cache_time_" + videoItem.id, 0);
                        if (cachePlayTime != 0) {
                            detailPlayer.seekTo(cachePlayTime);
                        } else if (detailPlayer.isAutoSkipStart() && !TextUtils.isEmpty(videoItem.start)) {
                            long time = TimeUtils.parseTime(videoItem.start);
                            if (time > 0) {
                                detailPlayer.seekTo(time * 1000);
                                ((GsyVideoPlayer) detailPlayer.getCurrentPlayer()).showSkipStart();
                            }
                        }
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(detailPlayer.isRotateWithSystem());
                        //记录最后播放的视频
                        PreferencesUtils.putString(VideoPlayActivity.this, "VIDEO_PLAY_END", videoBookId);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }

                    @Override
                    public void onPlayError(String url, Object... objects) {
                        if (!NetUtil.isNetWorkAvailable(VideoPlayActivity.this)) {
                            showToast("网络连接失败");
                            return;
                        }
                        showToast(objects[0].toString() + " 链接失效");
//                        playNextChapter();
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                        if (detailPlayer.isAutoPlayNext()) {//播放下一章
                            playNextChapter();
                        }
                    }

                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            //配合下方的onConfigurationChanged
                            orientationUtils.setEnable(!lock);
                        }
                    }
                }).build(detailPlayer);
        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(VideoPlayActivity.this, true, true);
            }
        });
        detailPlayer.setCallBack(new GsyVideoCallBack() {
            @Override
            public void shareVideo() {
                if (videoItem != null) {
                    UmShareUtils.shareVideo(VideoPlayActivity.this,
                            videoItem.chapter,
                            videoItem.video_name,
                            SaltUtils.getUrl(videoItem.video_url),
                            videoItem.video_cover);
                }
            }

            @Override
            public void showSetSpeed() {
                showPlaySpeedPop();
            }

            @Override
            public void playLastVideo() {
                playLastChapter();
//                detailPlayer.playLast();
            }

            @Override
            public void playNextVideo() {
                playNextChapter();
            }

            @Override
            public void pageLight(boolean isLight) {
                switchPageLight(ll_main, isLight);
            }

            @Override
            public void setVideoSpeed(float playSpeed) {

            }
        });
        detailPlayer.getCurrentPlayer().setGSYVideoProgressListener(new GSYVideoProgressListener() {
            @Override
            public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
                videoItem.playDuration = String.valueOf(currentPosition);
                videoItem.duration = String.valueOf(duration / 1000);
                if (!detailPlayer.isAutoSkipStart()) {//不开启 跳过
                    return;
                }
                if (videoItem != null && !TextUtils.isEmpty(videoItem.end)) {
                    long time = TimeUtils.parseTime(videoItem.end);
                    if (time != 0) {
                        if (currentPosition >= time * 1000) {
                            //跳过片尾时 清空当前视频的保存进度
                            PreferencesUtils.putLong(VideoPlayActivity.this, "video_cache_time_" + videoItem.id, 0);
                            ((GsyVideoPlayer) detailPlayer.getCurrentPlayer()).onVideoPause();
                            ((GsyVideoPlayer) detailPlayer.getCurrentPlayer()).onAutoCompletion();
                            ((GsyVideoPlayer) detailPlayer.getCurrentPlayer()).showSkipEnd();
                        }
                    }
                }
            }
        });
        iv_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoItem != null && videoItem != null) {
                    parseVideo(videoItem);
                }
            }
        });


//        GSYVideoManager
//                .instance()
//                .setPlayerInitSuccessListener(new IPlayerInitSuccessListener() {
//                    ///播放器初始化成果回调，可用于播放前的自定义设置
//                    @Override
//                    public void onPlayerInitSuccess(IMediaPlayer player, GSYModel model) {
//                        Log.d("tang", "onPlayerInitSuccess");
//                        if (player instanceof IjkExo2MediaPlayer) {
//                            Log.d("tang", "instanceof");
//                            ((IjkExo2MediaPlayer) player).setTrackSelector(new DefaultTrackSelector());
//                            ((IjkExo2MediaPlayer) player).setLoadControl(new DefaultLoadControl());
//                        }
//                    }
//                });

        iv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllContent();
            }
        });
        tv_da_gang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllContent();
            }
        });
        tv_video_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick) {
                    isClick = false;
                    return;
                }
                showAllContent();
            }
        });
        //记录点亮关灯状态 默认关灯
        isLight = PreferencesUtils.getBoolean(this, "page_is_light", false);
        switchPageLight(ll_main, isLight);

        if (videoSpeedFt == null) {
            videoSpeedFt = new VideoSpeedFt();
        }
        videoSpeedFt.setCallBack(new GsyVideoCallBack() {
            @Override
            public void shareVideo() {

            }

            @Override
            public void showSetSpeed() {

            }

            @Override
            public void playLastVideo() {

            }

            @Override
            public void playNextVideo() {

            }

            @Override
            public void pageLight(boolean isLight) {

            }

            @Override
            public void setVideoSpeed(float playSpeed) {
                PreferencesUtils.putFloat(VideoPlayActivity.this, "play_video_speed", playSpeed);
                ((GsyVideoPlayer) detailPlayer.getCurrentPlayer()).setShowSpeed(playSpeed);
//                detailPlayer.setSpeedPlaying(playSpeed, false);
                detailPlayer.showControlDialog();
            }
        });
    }

    boolean isClick = false;

    private void switchPageLight(ViewGroup viewGroup, boolean isLight) {
        int childCount = viewGroup.getChildCount();
        viewGroup.setBackgroundColor(isLight ? Color.parseColor("#FBFBFB") : Color.parseColor("#212121"));
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof RecyclerView) {
                videoPlayListAdapter.switchPageLight(isLight);
            } else if (childAt instanceof ViewGroup) {
                switchPageLight((ViewGroup) childAt, isLight);
            } else if (childAt instanceof TextView) {
                TextView textView = (TextView) childAt;
                switch (textView.getId()) {
                    case R.id.tv_da_gang:
                        break;
                    default:
                        textView.setTextColor(isLight ? Color.parseColor("#333333") : Color.parseColor("#FCFCFC"));
                }
            }
        }
    }

    private void showAllContent() {
        if (videoItem == null) {
            return;
        }
        DialogUtils.showBottomDialog(this, R.layout.dialog_layout_video_content, -1, DensityUtil.dip2px(this, 450), new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                TextView tv_video_content = view.findViewById(R.id.tv_video_content);
                if (TextUtils.isEmpty(videoItem.remark)) {
                    tv_video_content.setText("暂无内容");
                } else {
                    tv_video_content.setText(matchTime(videoItem.remark.trim()));
                    tv_video_content.setMovementMethod(CustomMovementMethod.getInstance());
                }
            }
        });
    }

    private void showContent() {
        int lineCount = tv_video_content.getMaxLines();
        if (lineCount == 4) {
            iv_show.setImageResource(R.drawable.icon_show_down);
            tv_video_content.setMaxLines(Integer.MAX_VALUE);
        } else {
            iv_show.setImageResource(R.drawable.icon_show_up);
            tv_video_content.setMaxLines(4);
        }
    }

    /**
     * 点赞
     */
    private void parseVideo(VideoListBean.RowsBean item) {
        iv_collect.setImageResource(item.collect == 1 ? R.drawable.icon_collect : R.drawable.icon_collected);
        if (item.collect == 1) {
            item.collect = 0;
        } else {
            item.collect = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        map.put("value", item.id);
        map.put("type", "3");
        NetUtil.get(ZConfig.GET_ADD_COLLECT, map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                AudioCollectBean bean = new Gson().fromJson(t, AudioCollectBean.class);
                item.collect = bean.data.status;
                showToastMsg(bean.msg);
//                iv_collect.setImageResource(item.collect == 1 ? R.drawable.icon_collected : R.drawable.icon_collect);
            }
        });
    }

    private void playLastChapter() {
        int playIndex = currentPlayIndex - 1;
        if (playIndex > -1) {
            VideoListBean.RowsBean item = videoPlayListAdapter.getItem(playIndex);
            playVideo(item);
            currentPlayIndex = playIndex;
        } else {
        }
    }

    private int currentPlayIndex = 0;
    private int cachePlayIndex = 0;

    private void playNextChapter() {
        int itemCount = videoPlayListAdapter.getItemCount();
        int playIndex = currentPlayIndex + 1;
        if (playIndex >= 0 && playIndex < itemCount) {
            VideoListBean.RowsBean item = videoPlayListAdapter.getItem(playIndex);
            playVideo(item);
            currentPlayIndex = playIndex;
        } else {
        }
    }

    private float playSpeed;

    private void initData() {
        //保证seekTo精度
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List list = new ArrayList<>();
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);

        videoBookId = getIntent().getStringExtra("VIDEO_BOOK_ID");
        String videoCate = getIntent().getStringExtra(VIDEO_CATE);
        videoItemId = getIntent().getStringExtra("VIDEO_ITEM_ID");
        bookCover = getIntent().getStringExtra("VIDEO_BOOK_COVER");
        videoPlayListAdapter.setBookCover(bookCover);
        tv_video_cate.setText(videoCate);
        getVideoList(videoBookId);
    }
    AlertDialog alertDialog;
    private void getVideoList(String cateId) {
        String videoListJson = SharedPreferencesUtils.getVideoListJson(this, cateId);
        if(!TextUtils.isEmpty(videoListJson)){
            setVideoList(videoListJson, cateId);
            return;
        }
        showProgressDialog("加载中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("type", cateId);
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/multimedia/videolists", map, new NetUtil.CallBack() {

            @Override
            public void onSuccess(String videoListJson) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                SharedPreferencesUtils.saveVideoListJson(VideoPlayActivity.this,cateId,videoListJson);
                setVideoList(videoListJson, cateId);
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dismissProgressDialog();
                alertDialog = DialogUtils.showSureDialog(VideoPlayActivity.this, "温馨提示", "视频列表获取失败，请稍候再试", "确定", new DialogUtils.onDialogClickListener() {
                    @Override
                    public void onCancel(Dialog dialog) {

                    }

                    @Override
                    public void onOk(Dialog dialog) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }
        });
    }

    private void setVideoList(String videoListJson, String cateId) {
        videoListBean = new Gson().fromJson(videoListJson, VideoListBean.class);
        videoPlayListAdapter.setNewData(videoListBean.rows);
        if (videoListBean.rows != null && !videoListBean.rows.isEmpty()) {
            tv_video_count.setText("共" + videoListBean.rows.size() + "集");
            if (parseInt(videoItemId) > 0) {
                VideoListBean.RowsBean rowsBean = new VideoListBean.RowsBean();
                rowsBean.id = videoItemId;

                int i = videoListBean.rows.indexOf(rowsBean);
                if (i >= 0) {
                    currentPlayIndex = i;
                }
            } else {
                //记录当前书籍 用户播放第几集
                cachePlayIndex = PreferencesUtils.getInt(VideoPlayActivity.this, "VIDEO_CACHE_PLAY_INDEX_" + cateId, 0);
                currentPlayIndex = cachePlayIndex;
            }
            rcv_video_list.scrollToPosition(currentPlayIndex);
            playVideo(videoListBean.rows.get(currentPlayIndex));
            detailPlayer.setPlayVideoCount(videoListBean.rows.size());
        }
    }

    private VideoListBean.RowsBean videoItem;

    private void playVideo(VideoListBean.RowsBean rowsBean) {
        if (videoItem != null) {
            long time = detailPlayer.getCurrentPlayer().getCurrentPositionWhenPlaying();
            //记录上一个视频播放时长
            PreferencesUtils.putLong(VideoPlayActivity.this, "video_cache_time_" + videoItem.id, time);
        }

        detailPlayer.getCurrentPlayer().release();
        videoItem = rowsBean;
        videoItem.bookCover = bookCover;
        videoPlayListAdapter.setCurrentId(videoItem.id);
        PicassoUtils.loadImage(imageView, TextUtils.isEmpty(videoItem.video_cover) ? bookCover : videoItem.video_cover, R.drawable.video_default_bg, DensityUtil.getScreenWidth(this), DensityUtil.dip2px(211));
        String playUrl = SaltUtils.getUrl(videoItem.video_url);
        tv_current_video_title.setText(videoItem.chapter);
        if (TextUtils.isEmpty(videoItem.remark)) {
            tv_video_content.setText("");
            tv_da_gang.setText("暂无大纲");
            iv_show.setVisibility(View.INVISIBLE);
            tv_video_content.setVisibility(View.GONE);
            tv_more.setVisibility(View.GONE);
        } else {
            tv_da_gang.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tv_da_gang.setText("大纲");
            iv_show.setVisibility(View.VISIBLE);
            tv_video_content.setVisibility(View.VISIBLE);
            tv_video_content.setText(matchTime(videoItem.remark.trim()));
            tv_video_content.setMovementMethod(CustomMovementMethod.getInstance());
            if (tv_video_content.getLineCount() > 4) {
                tv_more.setVisibility(View.VISIBLE);
            } else {
                tv_more.setVisibility(View.GONE);
            }
        }
        playUrl = playUrl.replace(" ", "%20");//处理有空格的url地址

        tv_video_cate.setText(rowsBean.cate2_name + "-" + rowsBean.cate3_name);
        iv_collect.setImageResource(videoItem.collect == 1 ? R.drawable.icon_collected : R.drawable.icon_collect);
        playSpeed = PreferencesUtils.getFloat(VideoPlayActivity.this, "play_video_speed", 1.0f);
        detailPlayer.setSpeed(playSpeed, false);
        Log.w("TTT","playUrl:"+playUrl);
        detailPlayer.getCurrentPlayer().setUp(playUrl, true, videoItem.chapter);
        ((GsyVideoPlayer) detailPlayer.getCurrentPlayer()).startPlayLogic();
        ((GsyVideoPlayer) detailPlayer.getCurrentPlayer()).setCurrentPlayIndex(currentPlayIndex);
        //记录当前书籍 播放到第几个视频
        PreferencesUtils.putInt(VideoPlayActivity.this, "VIDEO_CACHE_PLAY_INDEX_" + videoBookId, currentPlayIndex);
    }

    private int parseInt(String videoItemId) {
        if (TextUtils.isEmpty(videoItemId)) {
            return 0;
        }
        return Integer.parseInt(videoItemId);
    }


    private SpannableString matchTime(String str) {
        //([0-1]?[0-9]|2[0-3])-([0-5][0-9])-([0-5][0-9])
        SpannableString sStr = new SpannableString(str);
        String rx = "([0-9]{1,2}:[0-9]{1,2})?(:[0-5]{0,1}[0-9]{1})?";
        Pattern p = Pattern.compile(rx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            //设置部分文字点击事件
            String text = str.substring(m.start(), m.end());
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (TextUtils.isEmpty(text)) {
                        isClick = false;
                    } else {
                        isClick = true;
                        long time = TimeUtils.parseTime(text);
                        if (time != 0) {
                            detailPlayer.seekTo(time * 1000);
                        }
                    }
                }
            };
            sStr.setSpan(clickableSpan, m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#5677FC")), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return sStr;
    }

    private VideoSpeedFt videoSpeedFt;

    private void showPlaySpeedPop() {
        videoSpeedFt.setFullScreen(detailPlayer.isIfCurrentIsFullscreen());
        if (videoSpeedFt.isVisible()) {
            videoSpeedFt.dismissAllowingStateLoss();
        } else {
            videoSpeedFt.show(getSupportFragmentManager(), "videoSpeedFt");
        }
    }

    @Override
    protected void onPause() {
        detailPlayer.getCurrentPlayer().onVideoPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
//        detailPlayer.getCurrentPlayer().onVideoResume(false);
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoItem != null) {
            long time = detailPlayer.getCurrentPlayer().getCurrentPositionWhenPlaying();
            //记录当前视频播放时长
            PreferencesUtils.putLong(VideoPlayActivity.this, "video_cache_time_" + videoItem.id, time);
        }
        detailPlayer.release();
        GSYBaseVideoPlayer currentPlayer = detailPlayer.getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayer.release();
        }
        detailPlayer = null;
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        try {
            if (videoListBean != null) {//防止因没有缓存数据，而导致刷新错误的数据
                boolean last_play_video_cache = PreferencesUtils.putObject(this, "last_play_video_cache", videoListBean);
                Log.d("tang", "存储状态:" + last_play_video_cache);
            }
        } catch (Exception e) {

        }
        EventBus.getDefault().post("video_cache_refresh");
    }


    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (detailPlayer.isInPlayingState()) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }
}
