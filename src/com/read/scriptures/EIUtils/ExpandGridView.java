package com.read.scriptures.EIUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.GridView;

/**
 * 展开式 GridView，禁用内部滚动，展开全部内容用于嵌套在 ScrollView/ListView 中。
 * 内置 measure 缓存，宽度不变时跳过重复全量测量，减少滚动卡顿。
 */
public class ExpandGridView extends GridView {

    private boolean haveScrollbar = false;
    private int mLastMeasureWidthSpec = -1;
    private int mCachedMeasuredHeight = -1;

    public ExpandGridView(Context context) {
        super(context);
    }

    public ExpandGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (haveScrollbar) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        // 宽度没变且有缓存时直接返回，跳过昂贵的全量 measure
        if (widthMeasureSpec == mLastMeasureWidthSpec && mCachedMeasuredHeight > 0) {
            setMeasuredDimension(getMeasuredWidth(), mCachedMeasuredHeight);
            return;
        }
        int expandSpec = MeasureSpec.makeMeasureSpec(536870911, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
        mLastMeasureWidthSpec = widthMeasureSpec;
        mCachedMeasuredHeight = getMeasuredHeight();
    }

    /** 数据变化时调用，清除缓存让下次重新测量 */
    public void invalidateMeasureCache() {
        mLastMeasureWidthSpec = -1;
        mCachedMeasuredHeight = -1;
    }

    public void setHaveScrollbar(boolean haveScrollbar) {
        this.haveScrollbar = haveScrollbar;
    }
}
