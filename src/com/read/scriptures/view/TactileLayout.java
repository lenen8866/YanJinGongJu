package com.read.scriptures.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.read.scriptures.util.StatusBarUtils;

public class TactileLayout extends FrameLayout {
    /**
     * 用于显示'圈'的视图
     */
    private ShinyView viewShiny;
    /**
     * 滑动触摸的判断边界
     */
    private int touchSlop;
    private int downX, downY;

    public TactileLayout(Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        viewShiny = new ShinyView(context);
        addView(viewShiny);
    }

    public interface DispatchTouchEventListener {
        void onDispatchTouchEvent(MotionEvent ev);
    }

    private DispatchTouchEventListener dispatchTouchEventListener;

    public void setDispatchTouchEventListener(DispatchTouchEventListener dispatchTouchEventListener) {
        this.dispatchTouchEventListener = dispatchTouchEventListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (dispatchTouchEventListener != null) {
            dispatchTouchEventListener.onDispatchTouchEvent(ev);
        }
        // 这里可以接收到所有的触摸事件
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - downX) > touchSlop || Math.abs(ev.getY() - downY) > touchSlop) {
                    // 在滑动满足一定条件下，把事件交给viewShiny处理
                    viewShiny.refreshShiny(ev);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // 在抬起的时候，把事件交给viewShiny处理
                viewShiny.refreshShiny(ev);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 为了能够接收到所有的触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
        }
        return super.onTouchEvent(event);
    }

    public static class ShinyView extends View {
        /**
         * 将圈作为对象来处理
         */
        class Shiny {
            /**
             * radius表示正常态的半径， radius2表示坑爹圆环的内部透明圆半径
             */
            private int radius, radius2;

            private float fraction;

            public Shiny() {
            }

            public Shiny(int radius, int radius2) {
                this.radius = radius;
                this.radius2 = radius2;
            }

            public int getRadius() {
                return radius;
            }

            public void setRadius(int radius) {
                this.radius = radius;
            }

            public int getRadius2() {
                return radius2;
            }

            public void setRadius2(int radius2) {
                this.radius2 = radius2;
            }

            public void setFraction(float fraction) {
                this.fraction = fraction;
            }
        }
        // 郭林的博客中， 属性动画中那篇有讲

        /**
         * 对象估值器，用于时刻改变Shiny的值来影响每个时刻对应的界面效果
         */
        public class ShinyEvaluator implements TypeEvaluator<Shiny> {

            private Shiny shiny = new Shiny(0, 0);

            @Override
            public Shiny evaluate(float fraction, Shiny startValue, Shiny endValue) {
                int x = startValue.getRadius() + (int) (fraction * (endValue.getRadius() - startValue.getRadius()));
                shiny.setRadius(x);
                if (fraction >= 0.5f) {
                    int y = startValue.getRadius2() + (int) ((fraction - 0.5) * 2f * (endValue.getRadius2() - startValue.getRadius2()));
                    shiny.setRadius2(y);
                }
                return shiny;
            }

        }

        /**
         * 圈的状态 无
         */
        private static final int ACTION_NONE = 0;
        /**
         * 圈的状态 移动中
         */
        private static final int ACTION_MOVE = 1;
        /**
         * 圈的状态 移动后抬起
         */
        private static final int ACTION_UP_AFTER_MOVE = 2;
        /**
         * 圈的状态 点击抬起
         */
        private static final int ACTION_UP = 3;
        private int action = ACTION_NONE;

        private final Paint paint = new Paint();
        /**
         * 绘制坑爹圆所需神器
         */
        private final PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
        /**
         * ShinyLayout 传递来的触摸事件
         */
        private MotionEvent event;
        /**
         * 圈的宽
         */
        private int shinyWidth;
        private int statusBarHeight;
        private ValueAnimator anim1, anim2;

        private final Shiny shiny = new Shiny();



        public ShinyView(Context context) {
            super(context);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            paint.setAntiAlias(true);
            statusBarHeight = StatusBarUtils.getStatusBarHeight((Activity) context);


            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);

            shinyWidth = StatusBarUtils.isScreenVertical((Activity) context) ? (int) (outMetrics.widthPixels * 0.1f)
                    : (int) (outMetrics.heightPixels * 0.1f);
            // 点击抬起后的动画
            anim1 = ValueAnimator.ofObject(new ShinyEvaluator(), new Shiny(0, 0), new Shiny(shinyWidth, (int) (shinyWidth * 0.92f)));
            anim1.setDuration(400);
            anim1.setInterpolator(new DecelerateInterpolator(2));
            anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Shiny mShiny = (Shiny) animation.getAnimatedValue();
                    float f = animation.getAnimatedFraction();
                    shiny.setFraction(f);
                    shiny.setRadius(mShiny.radius);
                    shiny.setRadius2(mShiny.radius2);
                    //
                    invalidate();
                }
            });
            anim1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    action = ACTION_NONE;
                    invalidate();
                }
            });
            // 移动抬起后的动画
            anim2 = ValueAnimator.ofInt((int) (shinyWidth * 0.7f), shinyWidth);
            anim2.setInterpolator(new DecelerateInterpolator(2));
            anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int radius = (Integer) animation.getAnimatedValue();
                    float f = animation.getAnimatedFraction();
                    shiny.setRadius(radius);
                    shiny.setFraction(f);
                    invalidate();
                }
            });
        }

        private void refreshShiny(MotionEvent ev) {
            boolean showShinyTrace = true;
            if (!showShinyTrace) {
                return;
            }

            event = ev;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    // 标记事件为  移动
                    action = ACTION_MOVE;
                    shiny.radius = (int) (shinyWidth * 0.7f);
                    // 移动时，时刻重绘更新视图
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    // 根据移动标记，判断执行对应动画
                    if (action == ACTION_MOVE) {
                        action = ACTION_UP_AFTER_MOVE;
                        anim2.start();
                    } else {
                        action = ACTION_UP;
                        anim1.start();
                    }
                    break;
            }
        }

        /**
         * 各种逻辑判断，处理完各种值之后，都将在这里画出来
         */
        @Override
        protected void onDraw(Canvas canvas) {
            if (event != null) {
                final int count = event.getPointerCount();
                switch (action) {
                    case ACTION_UP:
                        /** 坑爹圆绘制逻辑 */
                        for (int i = 0; i < count; i++) {
                            paint.setARGB(255 - (int) (50 * shiny.fraction), 255, 255, 255);
                            canvas.drawCircle(event.getX(i), event.getY(i) - statusBarHeight, shiny.radius, paint);
                            paint.setXfermode(porterDuffXfermode);
                            paint.setColor(Color.TRANSPARENT);
                            canvas.drawCircle(event.getX(i), event.getY(i) - statusBarHeight, shiny.radius2, paint);
                            paint.setXfermode(null);
                        }
                        break;
                    case ACTION_MOVE:
                        /** 移动绘制逻辑 */
                        for (int i = 0; i < count; i++) {
                            paint.setColor(Color.WHITE);
                            canvas.drawCircle(event.getX(i), event.getY(i) - statusBarHeight, shiny.radius * 0.7f, paint);
                        }
                        break;
                    case ACTION_UP_AFTER_MOVE:
                        /** 移动抬起的绘制逻辑 */
                        for (int i = 0; i < count; i++) {
                            paint.setARGB((int) (255 * (1 - shiny.fraction)), 255, 255, 255);
                            canvas.drawCircle(event.getX(i), event.getY(i) - statusBarHeight, shiny.radius, paint);
                        }
                        break;
                }
            }
        }
    }
}
