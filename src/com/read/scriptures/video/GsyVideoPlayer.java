package com.read.scriptures.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.read.scriptures.R;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.NetWorkSpeedUtils;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SingleDoubleClickListener;
import com.read.scriptures.view.RadarView;
import com.read.scriptures.view.RadiusCardView;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.Random;

import moe.codeest.enviews.ENDownloadView;
import moe.codeest.enviews.ENPlayView;


/**
 * Created by shuyu on 2016/12/7.
 * 注意
 * 这个播放器的demo配置切换到全屏播放器
 * 这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
 */

public class GsyVideoPlayer extends StandardGSYVideoPlayer {

    private ImageView iv_more;
    private ImageView iv_pre;
    private ImageView iv_next;

    private TextView tv_tui;
    private TextView tv_tui1;
    private TextView tv_loading;
    private RadarView rv_tui1;
    private RadiusCardView rsv1;

    private TextView tv_jin;
    private TextView tv_jin1;
    private RadarView rv_jin1;
    private RadiusCardView rsv2;

    private TextView tv_skip_start;
    private TextView tv_skip_end;
    private TextView tv_speed;

    private TextView title1;
    private View view_bg;

    //记住切换数据源类型
    private int mType = 0;
    private int mTransformSize = 0;
    //数据源
    private int mSourcePosition = 0;
    private GsyVideoCallBack callBack;

    private int videoCount = 0;
    private int currentPlayIndex = 0;

    public void setCallBack(GsyVideoCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public GsyVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public GsyVideoPlayer(Context context) {
        super(context);
    }

    public GsyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
    }

    public boolean isAutoPlayNext() {
        return PreferencesUtils.getBoolean(mContext, "auto_play_next", true);
    }

    public boolean isAutoSkipStart() {
        return PreferencesUtils.getBoolean(mContext, "auto_skip_start", true);
    }


    private RelativeLayout mPreviewLayout;
    private ImageView mPreView;
    private int mPreProgress = -2;
    //是否因为用户点击
    private boolean mIsFromUser;
    protected boolean byStartedClick;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressTime1 = 0;
            progressTime2 = 0;
        }
    };
    private long progressTime1 = 0;
    private long progressTime2 = 0;


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        iv_more = findViewById(R.id.iv_more);
        iv_pre = findViewById(R.id.iv_pre);
        iv_next = findViewById(R.id.iv_next);
        tv_tui = findViewById(R.id.tv_tui);
        tv_tui1 = findViewById(R.id.tv_tui1);
        rv_tui1 = findViewById(R.id.rv_tui1);
        tv_jin = findViewById(R.id.tv_jin);
        rv_jin1 = findViewById(R.id.rv_jin1);
        tv_jin1 = findViewById(R.id.tv_jin1);
        rsv1 = findViewById(R.id.rsv1);
        rsv2 = findViewById(R.id.rsv2);
        view_bg = findViewById(R.id.view_bg);
        tv_loading = findViewById(R.id.tv_loading);
        tv_skip_start = findViewById(R.id.tv_skip_start);
        tv_skip_end = findViewById(R.id.tv_skip_end);
        tv_speed = findViewById(R.id.tv_speed);
        title1 = findViewById(R.id.title1);
        title1.setText("双击可快进、快退");

        ViewGroup.LayoutParams layoutParams1 = rsv1.getLayoutParams();
        layoutParams1.width = mIfCurrentIsFullscreen ? DensityUtil.getScreenHeight(mContext) / 3 : DensityUtil.getScreenWidth(mContext) * 2 / 5;
        rsv1.setLayoutParams(layoutParams1);

        ViewGroup.LayoutParams layoutParams3 = tv_tui.getLayoutParams();
        layoutParams3.width = mIfCurrentIsFullscreen ? DensityUtil.getScreenHeight(mContext) / 3 : DensityUtil.getScreenWidth(mContext) * 2 / 5;
        tv_tui.setLayoutParams(layoutParams3);

        ViewGroup.LayoutParams layoutParams4 = tv_jin.getLayoutParams();
        layoutParams4.width = mIfCurrentIsFullscreen ? DensityUtil.getScreenHeight(mContext) / 3 : DensityUtil.getScreenWidth(mContext) * 2 / 5;
        tv_jin.setLayoutParams(layoutParams4);

        ViewGroup.LayoutParams layoutParams2 = rsv2.getLayoutParams();
        layoutParams2.width = mIfCurrentIsFullscreen ? DensityUtil.getScreenHeight(mContext) / 3 : DensityUtil.getScreenWidth(mContext) * 2 / 5;
        rsv2.setLayoutParams(layoutParams2);

        mPreviewLayout = findViewById(R.id.preview_layout);
        mPreView = findViewById(R.id.preview_image);

        tv_tui.setOnTouchListener(new SingleDoubleClickListener(new SingleDoubleClickListener.MyClickCallBack() {
            @Override
            public void oneClick(float x, float y) {
            }

            @Override
            public void doubleClick(float x, float y) {
                byStartedClick = false;
                rv_tui1.startRippleAnimation();
                handler.removeCallbacks(runnable);
                int currentPositionWhenPlaying = getCurrentPlayer().getCurrentPositionWhenPlaying();
                if (currentPositionWhenPlaying == 0) {
                    return;
                }
                int time = currentPositionWhenPlaying - 10 * 1000;
                seekTo(Math.max(time, 0));
                progressTime1 += 10;
                tv_tui1.setText(progressTime1 + "秒");
                handler.postDelayed(runnable, 1500);
            }

            @Override
            public void click(float x, float y) {
                byStartedClick = false;
                rv_tui1.startRippleAnimation();
                handler.removeCallbacks(runnable);
                int currentPositionWhenPlaying = getCurrentPlayer().getCurrentPositionWhenPlaying();
                if (currentPositionWhenPlaying == 0) {
                    return;
                }
                int time = currentPositionWhenPlaying - 10 * 1000;
                seekTo(Math.max(time, 0));
                progressTime1 += 10;
                tv_tui1.setText(progressTime1 + "秒");
                handler.postDelayed(runnable, 1500);
            }
        }));

        tv_jin.setOnTouchListener(new SingleDoubleClickListener(new SingleDoubleClickListener.MyClickCallBack() {
            @Override
            public void oneClick(float x, float y) {
            }

            @Override
            public void doubleClick(float x, float y) {
                byStartedClick = false;
                rv_jin1.startRippleAnimation();
                handler.removeCallbacks(runnable);
                int currentPositionWhenPlaying = getCurrentPlayer().getCurrentPositionWhenPlaying();
                if (currentPositionWhenPlaying == 0) {
                    return;
                }
                int time = currentPositionWhenPlaying + 10 * 1000;
                seekTo(time);
                progressTime2 += 10;
                tv_jin1.setText(progressTime2 + "秒");
                handler.postDelayed(runnable, 1500);
            }

            @Override
            public void click(float x, float y) {
                byStartedClick = false;
                rv_jin1.startRippleAnimation();
                handler.removeCallbacks(runnable);
                int currentPositionWhenPlaying = getCurrentPlayer().getCurrentPositionWhenPlaying();
                if (currentPositionWhenPlaying == 0) {
                    return;
                }
                int time = currentPositionWhenPlaying + 10 * 1000;
                seekTo(time);
                progressTime2 += 10;
                tv_jin1.setText(progressTime2 + "秒");
                handler.postDelayed(runnable, 1500);
            }
        }));

        iv_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showControlDialog();
            }
        });
        iv_pre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.playLastVideo();
                }
            }
        });
        findViewById(R.id.layout_bottom).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        iv_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.playNextVideo();
                }
            }
        });

        if (videoControlFt == null) {
            videoControlFt = new VideoControlFt();
        }
        videoControlFt.setCallBack(new GsyVideoCallBack() {
            @Override
            public void shareVideo() {
                if (callBack != null) {
                    callBack.shareVideo();
                }
            }

            @Override
            public void showSetSpeed() {
                if (callBack != null) {
                    callBack.showSetSpeed();
                }
            }

            @Override
            public void playLastVideo() {
                if (callBack != null) {
                    callBack.playLastVideo();
                }
            }

            @Override
            public void playNextVideo() {
                if (callBack != null) {
                    callBack.playNextVideo();
                }
            }

            @Override
            public void pageLight(boolean isLight) {
                if (callBack != null) {
                    callBack.pageLight(isLight);
                }
            }

            @Override
            public void setVideoSpeed(float playSpeed) {

            }
        });
        netWorkSpeedUtils = new NetWorkSpeedUtils(getContext());
    }

    private VideoControlFt videoControlFt;

    private NetWorkSpeedUtils netWorkSpeedUtils;

    Runnable runnableSpeed = new Runnable() {
        @Override
        public void run() {
            if (tv_loading.getVisibility() == VISIBLE) {
                String netSpeedText = netWorkSpeedUtils.getNetSpeed();
                if ("0 KB/s".equals(netSpeedText)) {
                    netSpeedText = new Random().nextInt(100) + " KB/s";
                }
                tv_loading.setText(netSpeedText);
                tv_loading.postDelayed(this, 1000);
            } else {
                tv_loading.removeCallbacks(this);
            }
        }
    };

    public void showControlDialog() {
        videoControlFt.setFullScreen(mIfCurrentIsFullscreen);
        if (videoControlFt.isVisible()) {
            videoControlFt.dismissAllowingStateLoss();
        } else {
            videoControlFt.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "liveShareDialogFt");
        }
    }

    private SpannableString matchText(String str, int start, int end) {
        SpannableString sStr = new SpannableString(str);
        sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#5677FC")), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return sStr;
    }

    private SpannableString matchText(String str, int start) {
        SpannableString sStr = new SpannableString(str);
        sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#5677FC")), start, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return sStr;
    }

    /**
     * 处理显示逻辑
     */
    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
        resolveRotateUI();
        if (mIfCurrentIsFullscreen) {
//            iv_more.setVisibility(INVISIBLE);
            getTitleTextView().setVisibility(VISIBLE);

        } else {
//            iv_more.setVisibility(VISIBLE);
            getTitleTextView().setVisibility(GONE);
        }
        setShowSpeed(speed);
    }


    @Override
    public int getLayoutId() {
        return R.layout.layout_gsy_player;
    }


    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        rsv2.isFullScreen(false, true);
        GsyVideoPlayer sampleVideo = (GsyVideoPlayer) super.startWindowFullscreen(context, actionBar, statusBar);
        sampleVideo.mSourcePosition = mSourcePosition;
        sampleVideo.mType = mType;
        sampleVideo.mTransformSize = mTransformSize;
        sampleVideo.currentPlayIndex = currentPlayIndex;
        sampleVideo.videoCount = videoCount;
        sampleVideo.callBack = callBack;
        sampleVideo.speed = speed;
        return sampleVideo;
    }

    /**
     * 退出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            GsyVideoPlayer sampleVideo = (GsyVideoPlayer) gsyVideoPlayer;
            mSourcePosition = sampleVideo.mSourcePosition;
            mType = sampleVideo.mType;
            mTransformSize = sampleVideo.mTransformSize;
            speed = sampleVideo.speed;
            sampleVideo.currentPlayIndex = currentPlayIndex;
            sampleVideo.videoCount = videoCount;
            sampleVideo.callBack = callBack;
            sampleVideo.speed = speed;
        }
    }

    /**
     * 旋转逻辑
     */
    private void resolveRotateUI() {
        if (!mHadPlay) {
            return;
        }
        mTextureView.setRotation(mRotate);
        mTextureView.requestLayout();

    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        byStartedClick = false;
        iv_pre.setVisibility(INVISIBLE);
        iv_next.setVisibility(INVISIBLE);
    }

    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        tv_loading.setVisibility(VISIBLE);
        iv_pre.setVisibility(INVISIBLE);
        iv_next.setVisibility(INVISIBLE);
        tv_loading.removeCallbacks(runnableSpeed);
        tv_loading.postDelayed(runnableSpeed, 0);
    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        tv_loading.setVisibility(INVISIBLE);
        iv_pre.setVisibility(INVISIBLE);
        iv_next.setVisibility(INVISIBLE);
//        if (!byStartedClick) {
        setViewShowState(mStartButton, INVISIBLE);
//        }
        tv_loading.removeCallbacks(runnableSpeed);
    }

    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        showControlState();
    }

    @Override
    protected void hideAllWidget() {
        if (mCurrentState == 2 || mCurrentState == 1 || mCurrentState == 3) {
            super.hideAllWidget();
            iv_pre.setVisibility(INVISIBLE);
            iv_next.setVisibility(INVISIBLE);
            changeUi(false);
        } else {
//            super.hideAllWidget();
            setViewShowState(mBottomContainer, VISIBLE);
            setViewShowState(mTopContainer, VISIBLE);
            setViewShowState(mBottomProgressBar, VISIBLE);
            setViewShowState(mStartButton, VISIBLE);
            showControlState();
            changeUi(true);
        }
    }

    private void changeUi(boolean bottomVisible) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_speed.getLayoutParams();
        layoutParams.bottomMargin = DensityUtil.dip2px(mContext, bottomVisible ? 45 : 10);
        tv_speed.setLayoutParams(layoutParams);
    }

    @Override
    protected void onClickUiToggle(MotionEvent e) {
//        super.onClickUiToggle(e);
        if (mCurrentState == CURRENT_STATE_PLAYING) {
            getGSYVideoManager().pause();
            mCurrentState = CURRENT_STATE_PAUSE;
            setViewShowState(mTopContainer, VISIBLE);
            setViewShowState(mBottomContainer, VISIBLE);
            setViewShowState(mStartButton, VISIBLE);
            setViewShowState(mLoadingProgressBar, INVISIBLE);
            setViewShowState(mThumbImageViewLayout, INVISIBLE);
            setViewShowState(mBottomProgressBar, INVISIBLE);
            setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

            changeUi(true);
            if (mLoadingProgressBar instanceof ENDownloadView) {
                ((ENDownloadView) mLoadingProgressBar).reset();
            }
            updateStartImage();
            updatePauseCover();
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mBottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPauseClear();
            } else {
                changeUiToPauseShow();
            }
        }

        byStartedClick = true;
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }
        if (mCurrentState == CURRENT_STATE_PREPAREING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
//                    changeUiToPrepareingClear();

                    setViewShowState(mTopContainer, INVISIBLE);
                    setViewShowState(mBottomContainer, INVISIBLE);
//                    setViewShowState(mStartButton, INVISIBLE);
//                    setViewShowState(mLoadingProgressBar, INVISIBLE);
                    setViewShowState(mThumbImageViewLayout, INVISIBLE);
                    setViewShowState(mBottomProgressBar, INVISIBLE);
                    setViewShowState(mLockScreen, GONE);

                    changeUi(false);
                    if (mLoadingProgressBar instanceof ENDownloadView) {
                        ((ENDownloadView) mLoadingProgressBar).reset();
                    }
                    iv_pre.setVisibility(INVISIBLE);
                    iv_next.setVisibility(INVISIBLE);
                } else {
                    changeUiToPreparingShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingClear();
                } else {
                    changeUiToPlayingShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
//                    changeUiToPauseClear();
                } else {
//                    changeUiToPauseShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToCompleteClear();
                } else {
                    changeUiToCompleteShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingBufferingClear();
                } else {
                    changeUiToPlayingBufferingShow();
                }
            }
        }

        if (mTopContainer.getVisibility() != View.VISIBLE) {
            iv_pre.setVisibility(INVISIBLE);
            iv_next.setVisibility(INVISIBLE);
        } else {
//            mStartButton.setVisibility(VISIBLE);
//            if (!mIfCurrentIsFullscreen) {
            showControlState();
//            }
        }
    }


    private void showControlState() {
        if (currentPlayIndex != 0) {
            iv_pre.setVisibility(VISIBLE);
        } else {
            iv_pre.setVisibility(INVISIBLE);
        }
        if (currentPlayIndex < videoCount - 1) {
            iv_next.setVisibility(VISIBLE);
        } else {
            iv_next.setVisibility(INVISIBLE);
        }
    }

    private float speed = 1.0f;

    public void setShowSpeed(float s) {
        speed = s;
        if (s == 1.0 || s == 0.0) {
            tv_speed.setVisibility(GONE);
        } else {
            tv_speed.setVisibility(VISIBLE);
            tv_speed.setText("倍速：" + s);
        }
        setSpeedPlaying(speed, false);
    }

    public void setPlayVideoCount(int size) {
        videoCount = size;
    }

    public void setCurrentPlayIndex(int index) {
        currentPlayIndex = index;
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;
        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;
        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }

    @Override
    protected void touchDoubleUp(MotionEvent event) {
//        super.touchDoubleUp();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        if (fromUser) {
            if (isInPlayingState()) {
                iv_pre.setVisibility(INVISIBLE);
                iv_next.setVisibility(INVISIBLE);
                getStartButton().setVisibility(INVISIBLE);
            } else {
                showControlState();
            }
            int width = seekBar.getWidth();
            int time = progress * getDuration() / 100;
            int offset = (width) / 100 * progress;
//            showPreView(seekBar, mOriginUrl, time);
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPreviewLayout.getLayoutParams();
//            layoutParams.leftMargin = offset;
            //设置帧预览图的显示位置
//            mPreviewLayout.setLayoutParams(layoutParams);
            if (mHadPlay) {
                mPreProgress = progress;
            }
        }
    }

    private void showPreView(SeekBar seekBar, String url, long time) {
        mPreView.setVisibility(GONE);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        byStartedClick = true;
        super.onStartTrackingTouch(seekBar);
        mIsFromUser = true;
//        mPreviewLayout.setVisibility(VISIBLE);
        mPreProgress = -2;
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mPreProgress >= 0) {
            seekBar.setProgress(mPreProgress);
        }
        super.onStopTrackingTouch(seekBar);
        mIsFromUser = false;
        mPreviewLayout.setVisibility(GONE);
    }

    @Override
    protected void setTextAndProgress(int secProgress) {
//        if (mIsFromUser) {
//            return;
//        }
        super.setTextAndProgress(secProgress);
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();

        if (!byStartedClick) {
            setViewShowState(mStartButton, INVISIBLE);
        }
    }

    protected void updateStartImage() {
        if (mStartButton instanceof ENPlayView) {
            ENPlayView enPlayView = (ENPlayView) mStartButton;
            enPlayView.setDuration(500);
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                view_bg.setVisibility(GONE);
                enPlayView.play();
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                view_bg.setVisibility(VISIBLE);
                enPlayView.pause();
            } else {
                view_bg.setVisibility(VISIBLE);
                enPlayView.pause();
            }
        } else if (mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(R.drawable.video_click_pause_selector);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                imageView.setImageResource(R.drawable.video_click_error_selector);
            } else {
                imageView.setImageResource(R.drawable.video_click_play_selector);
            }
        }
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
        tv_loading.setVisibility(INVISIBLE);
        tv_loading.removeCallbacks(runnableSpeed);
        changeUiToNormal();
    }

    public void showSkipStart() {
        if (tv_skip_start != null) {
            tv_skip_start.setVisibility(VISIBLE);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_skip_start.setVisibility(GONE);
                }
            }, 2000);
        }
    }

    public void showSkipEnd() {
        if (tv_skip_end != null) {
            tv_skip_end.setVisibility(VISIBLE);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_skip_end.setVisibility(GONE);
                }
            }, 2000);
        }
    }
}
