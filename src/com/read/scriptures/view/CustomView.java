package com.read.scriptures.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomView extends VideoView {

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        计算屏幕宽高
        int width = getDefaultSize(0, widthMeasureSpec);
        int heigth = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, heigth);
    }
}

