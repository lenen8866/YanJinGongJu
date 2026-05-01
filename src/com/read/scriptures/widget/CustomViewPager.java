package com.read.scriptures.widget;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    public interface ScollAbleArea {
        public int getScollY();
    }

    private boolean noScoll;

    private float ableScollY;

    private ScollAbleArea area;

    public ScollAbleArea getArea() {
        return area;
    }

    public void setArea(ScollAbleArea area) {
        this.area = area;
    }

    public float getAbleScollY() {
        return ableScollY;
    }

    public void setAbleScollY(float ableScollY) {
        this.ableScollY = ableScollY;
    }

    public boolean isNoScoll() {
        return noScoll;
    }

    public void setNoScoll(boolean noScoll) {
        this.noScoll = noScoll;
    }

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (area != null) {
            ableScollY = area.getScollY();
        }
        if (noScoll) {
            return false;
        } else {
            if (ableScollY > 0 && event.getY() > ableScollY) {
                return false;
            } else if (ableScollY < 0 && event.getY() < 0 - ableScollY) {
                return false;
            }
            return super.onTouchEvent(event);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (area != null) {
            ableScollY = area.getScollY();
        }
        if (noScoll) {
            return false;
        } else {
            if (ableScollY > 0 && event.getY() > ableScollY) {
                return false;
            } else if (ableScollY < 0 && event.getY() < 0 - ableScollY) {
                return false;
            }
            return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

}
