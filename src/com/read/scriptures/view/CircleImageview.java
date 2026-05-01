package com.read.scriptures.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.read.scriptures.util.AppIconHelper;

/**
 * Created by Administrator on 2018/7/19.
 */

public class CircleImageview extends ImageView {

    //画笔
    private Paint mPaint;
    //圆形图片的半径
    private int mRadius;
    //图片的宿放比例
    private float mScale;

    public CircleImageview(Context context) {
        super(context);
    }

    public CircleImageview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //由于是圆形，宽高应保持一致
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mRadius = size / 2;
        setMeasuredDimension(size, size);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        mPaint = new Paint();

        Drawable drawable = getDrawable();

        if (null != drawable) {
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap bitmap = AppIconHelper.getAppIcon(drawable);
            //初始化BitmapShader，传入bitmap对象
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            //计算缩放比例
            mScale = (mRadius * 2.0f) / Math.min(bitmap.getHeight(), bitmap.getWidth());

            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            bitmapShader.setLocalMatrix(matrix);
            mPaint.setShader(bitmapShader);
            //画圆形，指定好坐标，半径，画笔
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        } else {
            super.onDraw(canvas);
        }
    }

}
