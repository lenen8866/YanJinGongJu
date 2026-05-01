package com.read.scriptures.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.read.scriptures.widget.sliding.SlidingLayout;

/**
 * 防止横向滑动事件和ScrollView滚动事件的冲突
 */
public class TouchInterceptSlidingView extends SlidingLayout {
    private Runnable scrollListener;

    private int scrollY = 0;
    private GestureDetector mGestureDetector;
    private boolean isPagingEnabled;

    public TouchInterceptSlidingView(Context context) {
        super(context);
        init();
    }

    public TouchInterceptSlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void init() {
        this.isPagingEnabled = false;
        mGestureDetector = new GestureDetector(getContext(), new YScrollDetector());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setIsPagingEnabled(boolean isPagingEnabled) {
        this.isPagingEnabled = isPagingEnabled;

    }
    public void setIsPagingEnabled(boolean isPagingEnabled, ListView list) {
        this.isPagingEnabled = isPagingEnabled;

        list.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                list.getParent().requestDisallowInterceptTouchEvent(true);//我的事情爹处理

                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (this.isPagingEnabled) {
            return super.onInterceptTouchEvent(ev);
        }
        return mGestureDetector.onTouchEvent(ev);
    }

    private class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // invalidate();
            if (scrollListener != null) {
                scrollListener.run();
            }
            scrollY += distanceY;
            if (Math.abs(distanceY) <= Math.abs(distanceX)) {
                return true;
            }
            return false;
        }
    }

    public int getScollY() {
        return scrollY;
    }

    public Runnable getScrollListener() {
        return scrollListener;
    }

    public void setScrollListener(Runnable scrollListener) {
        this.scrollListener = scrollListener;
    }
}
