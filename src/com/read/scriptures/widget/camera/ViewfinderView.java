package com.read.scriptures.widget.camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.read.scriptures.R;
import com.read.scriptures.EIUtils.EIApplication;

import java.util.Collection;
import java.util.HashSet;

/**
 * 扫描框
 *
 * @author zWX243327
 * @version V100R001C13, 2015-9-23
 * @since V100R001C13
 */
public final class ViewfinderView extends View {

    /**
     * 刷新界面的时间
     */
    private static final long ANIMATION_DELAY = 1L;

    private static final int OPAQUE = 0xFF;

    /**
     * 四个绿色边角对应的宽度
     */
    private static final int CORNER_WIDTH = 5;

    /**
     * 中间那条线每次刷新移动的距离
     */
    //  private static final int SPEEN_DISTANCE = 5;

    /**
     * 字体大小
     */
    private static final int TEXT_SIZE = 16;

    /**
     * 字体距离扫描框下面的距离
     */
    private static final int TEXT_PADDING_TOP = 30;

    /**
     * 中间线的宽度
     */
    private static final int LINEWIDTH = 5;

    /**
     * 手机的屏幕密度
     */
    private final float density;

    /**
     * 四个绿色边角对应的长度
     */
    private final int screenRate;

    /**
     * 画笔对象的引用
     */
    private final Paint paint;

    /**
     * 中间滑动线的最顶端位置
     */
    private int slideTop;

    private Bitmap resultBitmap;

    private final int maskColor;

    private final int resultColor;

    private final Collection<ResultPoint> possibleResultPoints;

    private boolean isFirst;

    /**
     * 构造器
     *
     * @param context 上下文
     * @param attrs   参数
     * @see [类、类#方法、类#成员]
     */
    public ViewfinderView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        density = context.getResources().getDisplayMetrics().density;
        //将像素转换成dp
        screenRate = (int) (15 * density);

        paint = new Paint();
        final Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);

        possibleResultPoints = new HashSet<ResultPoint>(5);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDraw(final Canvas canvas) {
        //中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
        final Rect frame = CameraManager.getInstance(EIApplication.getInstance()).getFramingRect();
        if (frame == null) {
            return;
        }

        //初始化中间线滑动的最上边和最下边
        if (!isFirst) {
            isFirst = true;
            slideTop = frame.top;
        }

        //获取屏幕的宽和高
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        paint.setColor(resultBitmap != null ? resultColor : maskColor);

        //画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            //如果结果图片不为空，所有解码成功了，那么就把图片绘制到中间那块区域
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            //如果不成功，就依旧绘制4条边框加一条中间线
            //画扫描框边上的角，总共8个部分
            paint.setColor(Color.GREEN);
            canvas.drawRect(frame.left, frame.top, frame.left + screenRate, frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top + screenRate, paint);
            canvas.drawRect(frame.right - screenRate, frame.top, frame.right, frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top + screenRate, paint);
            canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left + screenRate, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.bottom - screenRate, frame.left + CORNER_WIDTH, frame.bottom, paint);
            canvas.drawRect(frame.right - screenRate, frame.bottom - CORNER_WIDTH, frame.right, frame.bottom, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - screenRate, frame.right, frame.bottom, paint);

            //绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
            /* slideTop += SPEEN_DISTANCE;
            if (slideTop >= frame.bottom-CORNER_WIDTH)
            {
                slideTop = frame.top+CORNER_WIDTH;
            }*/
            slideTop = (frame.bottom + frame.top) / 2;
            final Rect lineRect = new Rect();
            lineRect.left = frame.left;
            lineRect.right = frame.right;
            lineRect.top = slideTop - (LINEWIDTH / 2);
            lineRect.bottom = slideTop + (LINEWIDTH / 2);
            canvas.drawBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.scancode_line)).getBitmap(),
                    null,
                    lineRect,
                    paint);

            //画扫描框下面的字
            paint.setColor(Color.WHITE);
            paint.setTextSize(TEXT_SIZE * density);
            paint.setAlpha(0x40);
            paint.setTypeface(Typeface.create("System", Typeface.BOLD));
            final String text = getResources().getString(R.string.scan_text);
            final float textWidth = paint.measureText(text);

            canvas.drawText(text, (width - textWidth) / 2, frame.bottom + (TEXT_PADDING_TOP * density), paint);

            //只刷新扫描框的内容，其他地方不刷新
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);

        }
    }

    /**
     * 这个方法用来清空图片，就是扫描好了，现在要重现扫描，那么就要将先前的图片撤掉，然后就会绘制那个框框和中间线
     * 在activity中调用
     *
     * @see [类、类#方法、类#成员]
     */
    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * 把点点加入possibleResultPoints
     *
     * @param point 索引
     * @see [类、类#方法、类#成员]
     */
    public void addPossibleResultPoint(final ResultPoint point) {
        possibleResultPoints.add(point);
    }

}