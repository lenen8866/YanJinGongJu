package com.read.scriptures.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class RadarView extends RelativeLayout {

    private int rippleColor;
    private float rippleStrokeWidth;
    private float rippleRadius;
    private int rippleDurationTime;
    private int rippleAmount;
    private int rippleDelay;
    private float rippleScale;
    private int rippleType;
    private Paint paint;
    private boolean animationRunning = false;
    private AnimatorSet animatorSet;
    private ArrayList<Animator> animatorList;
    private LayoutParams rippleParams;
    private ArrayList<RippleView> rippleViewList = new ArrayList<RippleView>();

    public RadarView(Context context) {
        super(context);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode())
            return;
        if (null == attrs) {
            throw new IllegalArgumentException("Attributes should be provided to this view,");
        }
        rippleColor = Color.parseColor("#77eeeeee");
        rippleStrokeWidth = 5;
        rippleRadius = 150;
        rippleDurationTime = 300;
        rippleAmount = 2;
        rippleScale = 5;
        rippleType = 0;

        rippleDelay = rippleDurationTime / rippleAmount / 2;

        paint = new Paint();
        paint.setAntiAlias(true);
        if (rippleType == 0) {
            rippleStrokeWidth = 0;
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        paint.setColor(rippleColor);

        rippleParams = new LayoutParams((int) (2 * (rippleRadius + rippleStrokeWidth)), (int) (2 * (rippleRadius + rippleStrokeWidth)));
        rippleParams.addRule(CENTER_IN_PARENT, TRUE);

        animatorSet = new AnimatorSet();
        animatorSet.setDuration(rippleDurationTime);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorList = new ArrayList<>();

        for (int i = 0; i < rippleAmount; i++) {
            RippleView rippleView = new RippleView(getContext());
            addView(rippleView, rippleParams);
            rippleViewList.add(rippleView);
            final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
//            scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
//            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleXAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(scaleXAnimator);
            final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
//            scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
//            scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleYAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(scaleYAnimator);
            final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f);
//            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
//            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(alphaAnimator);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationRunning = false;
                handler.postDelayed(runnable,1000);
            }
        });
        animatorSet.playTogether(animatorList);
    }

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            exitAnim();
        }
    };

    private void exitAnim() {
        final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "Alpha", 1.0f, 0f);
        alphaAnimator.setDuration(500);
        alphaAnimator.start();
    }

    private Path mBezierPath = new Path();

    private class RippleView extends View {

        public RippleView(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius = (Math.min(getWidth(), getHeight())) / 2;
            canvas.drawCircle(radius, radius, radius - rippleStrokeWidth, paint);
        }
    }

    public void startRippleAnimation() {
        handler.removeCallbacks(runnable);
        setVisibility(VISIBLE);
        setBackgroundColor(Color.parseColor("#77ffffff"));
        setAlpha(1.0f);
        if (!isRippleAnimationRunning()) {
            for (RippleView rippleView : rippleViewList) {
                rippleView.setVisibility(VISIBLE);
            }
            animatorSet.start();
            animationRunning = true;
        }
    }

    public void stopRippleAnimation() {
        if (isRippleAnimationRunning()) {
            animatorSet.end();
            animationRunning = false;
        }
    }

    public boolean isRippleAnimationRunning() {
        return animationRunning;
    }
}