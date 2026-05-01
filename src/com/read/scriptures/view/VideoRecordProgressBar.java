package com.read.scriptures.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.read.scriptures.R;

public class VideoRecordProgressBar extends View {

    private int mWidth, mHeight;

    public VideoRecordProgressBar(Context context) {
        this(context, null);
    }

    public VideoRecordProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRecordProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(float progress) {
        this.Progress = progress;
        invalidate();
    }


    private float Progress;


    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    private float maxProgress;

    private Paint progressPaint;

    private void init() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
//        ProgressPaint.setColor(Color.parseColor("#8C5FFF"));
        progressPaint.setColor(ContextCompat.getColor(getContext(), R.color.main_color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        @SuppressLint("DrawAllocation")
        RectF leftRectF = new RectF(0, 0, Progress / maxProgress * mWidth, mHeight);
        canvas.drawRect(leftRectF, progressPaint);


//        @SuppressLint("DrawAllocation")
//        RectF rightRectf = new RectF(Progress / maxProgress * mWidth, 0, Progress / maxProgress * mWidth, mHeight);
//        canvas.drawRect(rightRectf, deletePaint);
//        canvas.drawLine(mWidth / maxProgress, 0, mWidth / maxProgress, mHeight, limitPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }
}
