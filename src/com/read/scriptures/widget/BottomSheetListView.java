package com.read.scriptures.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

public class BottomSheetListView extends ListView {
    float lastY = 0;
    int  moveDirection = -1;

    public BottomSheetListView (Context context, AttributeSet p_attrs) {
        super (context, p_attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (ev.getY() - lastY > 50){
                    //向下滑动
                    moveDirection = 1;
                    if (canScrollVertically(this)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else if (lastY - ev.getY()  > 50){
                    //向上滑动
                    moveDirection = 0;
                    if (canScrollVertically(this)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else{
                    return super.onTouchEvent(ev);
                }
                break;
        }
//        if (canScrollVertically(this)) {
            getParent().requestDisallowInterceptTouchEvent(true);
//        }
        return super.onTouchEvent(ev);
    }

    public boolean canScrollVertically (AbsListView view) {
        boolean canScroll = false;

        if (view !=null && view.getChildCount ()> 0) {
            boolean isOnTop = view.getFirstVisiblePosition() != 0 || view.getChildAt(0).getTop() != 0;
            boolean isAllItemsVisible = isOnTop && view.getLastVisiblePosition() == view.getChildCount();

            if ((isOnTop && moveDirection == 1) || (isAllItemsVisible && moveDirection == 0) ) {
                canScroll = true;
            }
        }

        return  canScroll;
    }
}
