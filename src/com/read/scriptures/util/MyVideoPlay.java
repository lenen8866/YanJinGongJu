package com.read.scriptures.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;
import com.squareup.picasso.Picasso;

public class MyVideoPlay extends NormalGSYVideoPlayer {
    public MyVideoPlay(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public MyVideoPlay(Context context) {
        super(context);
    }

    public MyVideoPlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mBackButton.setImageResource(0);
        mLockScreen.setImageResource(0);
    }

    public boolean isCompletion;

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        isCompletion = true;
        showCover();
    }

    @Override
    public void startPlayLogic() {
        super.startPlayLogic();
        isCompletion = false;
    }

    public void showCover() {
        if (container != null) {
            container.removeAllViews();   //增加封面
            container.addView(imageView);
        }
        if (ivPlay != null) {
            ivPlay.setVisibility(View.VISIBLE);
        }
        if (imageView != null) {
            PicassoUtils.loadImage(imageView, cover, R.drawable.icon_play_deault_bg);
        }
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
        XToast.showToast(getContext(), "此视频链接已失效！");
    }

    private String cover;
    ImageView imageView;
    FrameLayout container;
    ImageView ivPlay;

    public void setCover(FrameLayout container, ImageView imageView, ImageView ivPlay, String cover) {
        this.cover = cover;
        this.imageView = imageView;
        this.container = container;
        this.ivPlay = ivPlay;
        Picasso.get().load(cover).noFade().into(imageView);
    }

    /**
     * 不需要触摸
     */
    @Override
    protected void touchSurfaceMove(float deltaX, float deltaY, float y) {
        return;
    }

    /**
     * 不需要触摸
     */
    @Override
    protected void touchSurfaceUp() {
        return;

    }

}
