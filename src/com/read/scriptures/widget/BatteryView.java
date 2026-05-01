package com.read.scriptures.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by LGM.
 * Datetime: 2015/7/5.
 * Email: lgmshare@mgail.com
 */
public class BatteryView extends View {

    private int mBorderColor = 0xffc0c0c0;
    private int mFillColor = 0xff2b2b2b;
    private int mSelectColor = 0xffffffff;
    private int mSelectAlertColor = 0xffffffff;

    private float mWidth;
    private float mHeight;
    private float mBorderWidth = 2;
    private float mCorner;
    private float mHeaderWidth;
    private int mPower = 100;

    public BatteryView(Context context) {
        super(context);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setColor(int color) {
        this.mSelectColor = color;
    }

    public void setFillColor(int color) {
        this.mFillColor = color;
    }

    public void setPower(int power) {
        mPower = power;
        if (mPower < 0) {
            mPower = 0;
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        mWidth = getWidth();
        mHeight = getHeight();
        mHeaderWidth = mWidth / 7;
        mCorner = mHeight / 5;
        drawBackground(canvas);
        if (mPower > 0) {
            drawSelectView(canvas);
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setColor(mFillColor);
        paint.setStyle(Paint.Style.FILL);

        Path path = new Path();
        path.moveTo(0, mCorner);
        path.quadTo(0, 0, mCorner, 0);
        path.lineTo(mWidth - mHeaderWidth - mCorner, 0);
        path.quadTo(mWidth - mHeaderWidth, 0, mWidth - mHeaderWidth, mCorner);
        path.lineTo(mWidth - mHeaderWidth, mHeight / 4);
        path.lineTo(mWidth - mCorner / 2, mHeight / 4);
        path.quadTo(mWidth, mHeight / 4, mWidth, mHeight / 4 + mCorner / 2);
        path.lineTo(mWidth, mHeight / 4 * 2 - mCorner / 2);
        path.quadTo(mWidth, mHeight / 4 * 3, mWidth - mCorner / 2, mHeight / 4 * 3);
        path.lineTo(mWidth - mHeaderWidth, mHeight / 4 * 3);
        path.lineTo(mWidth - mHeaderWidth, mHeight - mCorner);
        path.quadTo(mWidth - mHeaderWidth, mHeight, mWidth - mHeaderWidth - mCorner, mHeight);
        path.lineTo(mCorner, mHeight);
        path.quadTo(0, mHeight, 0, mHeight - mCorner);
        path.lineTo(0, mCorner);
        path.close();
        canvas.drawPath(path, paint);

        paint.setColor(mBorderColor);
        paint.setStrokeWidth(mBorderWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
    }

    /**
     * 绘制电量选中效果
     *
     * @param canvas
     */
    private void drawSelectView(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setColor(mPower <= 30 ? mSelectAlertColor : mSelectColor);
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.moveTo(mBorderWidth, mBorderWidth + mCorner / 2);
        path.quadTo(mBorderWidth, mBorderWidth, mBorderWidth + mCorner / 2, mBorderWidth);
        float xTo = (mPower / 100.0f) * (mWidth - 2 * mBorderWidth);
        if (mPower >= 100) { //满电
            path.lineTo(mWidth - mHeaderWidth - mBorderWidth - mCorner / 2, mBorderWidth);
            path.quadTo(mWidth - mHeaderWidth - mBorderWidth, mBorderWidth, mWidth - mHeaderWidth - mBorderWidth, mBorderWidth + mCorner / 2);
            path.lineTo(mWidth - mHeaderWidth - mBorderWidth, mHeight / 4 + mBorderWidth);
            path.lineTo(mWidth - mBorderWidth, mHeight / 4 + mBorderWidth);
            path.lineTo(mWidth - mBorderWidth, mHeight / 4 * 3 - mBorderWidth);
            path.lineTo(mWidth - mHeaderWidth - mBorderWidth, mHeight / 4 * 3 - mBorderWidth);
            path.lineTo(mWidth - mHeaderWidth - mBorderWidth, mHeight - mBorderWidth - mCorner / 2);
            path.quadTo(mWidth - mHeaderWidth - mBorderWidth, mHeight - mBorderWidth, mWidth - mHeaderWidth - mBorderWidth - mCorner / 2, mHeight - mBorderWidth);
            path.lineTo(mBorderWidth + mCorner / 2, mHeight - mBorderWidth);
            path.quadTo(mBorderWidth, mHeight - mBorderWidth, mBorderWidth, mHeight - mBorderWidth - mCorner / 2);
            path.lineTo(mBorderWidth, mBorderWidth + mCorner / 2);
            path.close();
        } else if (xTo < mCorner / 2) { //电量最低
            path.lineTo(mBorderWidth + mCorner / 2, mHeight - mBorderWidth);
            path.quadTo(mBorderWidth, mHeight - mBorderWidth, mBorderWidth, mHeight - mBorderWidth - mCorner / 2);
            path.lineTo(mBorderWidth, mBorderWidth + mCorner / 2);
        } else if ((mBorderWidth + mCorner / 2 + xTo) > (mWidth - mHeaderWidth)) { //电量超出了右侧的正极图形处
            path.lineTo(mWidth - mHeaderWidth - mBorderWidth - mCorner / 2, mBorderWidth);
            path.quadTo(mWidth - mHeaderWidth - mBorderWidth, mBorderWidth, mWidth - mHeaderWidth - mBorderWidth, mBorderWidth + mCorner / 2);
            path.lineTo(mWidth - mHeaderWidth - mBorderWidth, mHeight / 4 + mBorderWidth);
            path.lineTo(mBorderWidth + xTo, mHeight / 4 + mBorderWidth);
            path.lineTo(mBorderWidth + xTo, mHeight / 4 * 3 - mBorderWidth);
            path.lineTo(mWidth - mHeaderWidth - mBorderWidth, mHeight / 4 * 3 - mBorderWidth);
            path.lineTo(mWidth - mHeaderWidth - mBorderWidth, mHeight - mBorderWidth - mCorner / 2);
            path.quadTo(mWidth - mHeaderWidth - mBorderWidth, mHeight - mBorderWidth, mWidth - mHeaderWidth - mBorderWidth - mCorner / 2, mHeight - mBorderWidth);
            path.lineTo(mBorderWidth + mCorner / 2, mHeight - mBorderWidth);
            path.quadTo(mBorderWidth, mHeight - mBorderWidth, mBorderWidth, mHeight - mBorderWidth - mCorner / 2);
            path.lineTo(mBorderWidth, mBorderWidth + mCorner / 2);
        } else { //电量只需要填充矩形区域
            path.lineTo(mBorderWidth + xTo, mBorderWidth);
            path.lineTo(mBorderWidth + xTo, mHeight - mBorderWidth);
            path.lineTo(mBorderWidth + mCorner / 2, mHeight - mBorderWidth);
            path.quadTo(mBorderWidth, mHeight - mBorderWidth, mBorderWidth, mHeight - mBorderWidth - mCorner / 2);
            path.lineTo(mBorderWidth, mBorderWidth + mCorner / 2);
        }
        canvas.drawPath(path, paint);
    }
}