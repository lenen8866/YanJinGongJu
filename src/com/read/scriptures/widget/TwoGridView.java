package com.read.scriptures.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class TwoGridView extends GridView {

    public TwoGridView(Context context) {
        super(context);
    }

    public TwoGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO 自动生成的构造函数存根
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO 自动生成的方法存根
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
