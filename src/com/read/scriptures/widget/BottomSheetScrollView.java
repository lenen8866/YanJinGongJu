package com.read.scriptures.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;


public class BottomSheetScrollView extends WebView {
    float lastY = 0;

    public BottomSheetScrollView(Context context, AttributeSet p_attrs) {
        super(context, p_attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                float distance = currentY - lastY;
                if (Math.abs(distance) < 50) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }else{
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
