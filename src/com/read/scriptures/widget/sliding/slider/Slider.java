package com.read.scriptures.widget.sliding.slider;

import android.view.MotionEvent;

import com.read.scriptures.widget.sliding.SlidingAdapter;
import com.read.scriptures.widget.sliding.SlidingLayout;

/**
 * Created by xuzb on 1/16/15.
 */
public interface Slider {
    public void init(SlidingLayout slidingLayout);
    public void resetFromAdapter(SlidingAdapter adapter);
    public boolean onTouchEvent(MotionEvent event);
    public void computeScroll();
    public void slideNext();
    public void slidePrevious();
}
