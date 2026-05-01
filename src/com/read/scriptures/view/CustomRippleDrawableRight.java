package com.read.scriptures.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by guoshiwen on 2020/8/8.
 */
public class CustomRippleDrawableRight extends Drawable {

    private static final int STATE_IDLE = 0;
    private static final int STATE_ENTER = 1;
    private static final int STATE_EXIT = 2;

    private final Paint mPaint;

    private PointF mPressedPointF;
    private PointF mCurrentPointF;

    private int progress = 0;
    private int maxProgress = 100;

    private int mState = STATE_IDLE;

    private boolean pendingExit;

    private long animationTime = 150;
    private float mMaxRadius;
    private ValueAnimator mRunningAnimator;
    private final int mRealAlpha;
    private Handler handler = new Handler();

    public CustomRippleDrawableRight() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mRealAlpha = 119;

        mPressedPointF = new PointF();
        mCurrentPointF = new PointF();

    }

    private Path mBezierPath = new Path();

    Point mStartPoint = new Point();
    Point mControlPoint = new Point();
    Point mEndPoint = new Point();

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mState == STATE_ENTER) {
            if (mPaint.getAlpha() != mRealAlpha) {
                mPaint.setAlpha(mRealAlpha);
            }
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            mPaint.setColor(Color.parseColor("#00000000"));
            Rect bounds = getBounds();
            //贝塞尔
            mBezierPath.moveTo(bounds.right, bounds.top);
            mBezierPath.lineTo(mStartPoint.x, mStartPoint.y);
            mBezierPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);
            mBezierPath.lineTo(bounds.right, bounds.bottom);
            mBezierPath.close();
            //绘制贝塞尔
            canvas.drawPath(mBezierPath, mPaint);
            mPaint.setColor(Color.parseColor("#77f7f7f7"));
            float value = mMaxRadius * progress / maxProgress;
            canvas.drawCircle(mPressedPointF.x, mPressedPointF.y, value, mPaint);
        } else if (mState == STATE_EXIT) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            mPaint.setColor(Color.parseColor("#00000000"));
            Rect bounds = getBounds();
            //贝塞尔
            mBezierPath.moveTo(bounds.right, bounds.top);
            mBezierPath.lineTo(mStartPoint.x, mStartPoint.y);
            mBezierPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);
            mBezierPath.lineTo(bounds.right, bounds.bottom);
            mBezierPath.close();
            //绘制贝塞尔
            canvas.drawPath(mBezierPath, mPaint);

            mPaint.setColor(Color.parseColor("#77f7f7f7"));
            int value = mRealAlpha * progress / maxProgress;
            mPaint.setAlpha(value);
            canvas.drawRect(getBounds(), mPaint);

        }
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        boolean enable = false;
        boolean pressed = false;

        for (int state : stateSet) {
            switch (state) {
                case android.R.attr.state_pressed:
                    pressed = true;
                    break;
                case android.R.attr.state_enabled:
                    enable = true;
                    break;
            }
        }

        if (!enable) return false;

        if (pressed) {
            enter();
            return true;
        } else if (mState == STATE_ENTER) {
            exit();
            return true;
        } else {
            return false;
        }
    }

    private void exit() {
        if (progress != maxProgress && mState == STATE_ENTER) {
            pendingExit = true;
        } else {
            mState = STATE_EXIT;
            startExitAnimation();
        }
    }

    private void startExitAnimation() {
        if (mRunningAnimator != null && mRunningAnimator.isRunning()) {
            mRunningAnimator.cancel();
        }

        mRunningAnimator = ValueAnimator.ofInt(progress, 0);
        mRunningAnimator.setInterpolator(new LinearInterpolator());
        mRunningAnimator.setDuration(500);
        mRunningAnimator.addUpdateListener(animation -> {
            progress = (int) animation.getAnimatedValue();
            invalidateSelf();
        });
        mRunningAnimator.start();
    }

    public void enter() {
        handler.removeCallbacksAndMessages(null);
        mState = STATE_ENTER;
        progress = 0;
        mPressedPointF.set(mCurrentPointF);
        Rect bounds = getBounds();//0,0,450,720
        mMaxRadius = hypotenuse(bounds.width(), bounds.height());

        mStartPoint.set(bounds.left + 100, 0);
        mControlPoint.set(bounds.left, bounds.bottom / 2);
        mEndPoint.set(bounds.left + 100, bounds.bottom);
        startEnterAnimation();
        handler.postDelayed(runnable, 1000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            exit();
        }
    };

    public static int hypotenuse(double a, double b) {
        return (int) Math.sqrt(a * a + b * b);

    }

    private void startEnterAnimation() {
        mRunningAnimator = ValueAnimator.ofInt(progress, maxProgress);
        mRunningAnimator.setInterpolator(new LinearInterpolator());
        mRunningAnimator.setDuration(animationTime);
        mRunningAnimator.addUpdateListener(animation -> {
            progress = (int) animation.getAnimatedValue();
            invalidateSelf();
        });
        mRunningAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (pendingExit) {
                    pendingExit = false;
                    mState = STATE_EXIT;
                    startExitAnimation();
                }
            }
        });
        mRunningAnimator.start();
    }

    @Override
    public void setHotspot(float x, float y) {
        mCurrentPointF.set(x, y);


    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
