package com.read.scriptures.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * 屏幕显示工具类
 *
 * @author jWX276003
 * @version [版本号, 2015-3-16]
 * @since [产品/模块版本]
 */
public abstract class DisplayUtil {

    private static final float HALF = 160f;

    private static final float PIONT_FIVE = 0.5f;


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px(像素)
     */
    public static float dp2px(final Context context, final float dpValue) {
        final float px = dpValue * (densityDPI(context) / HALF);
        return px;
    }

    public static int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();
        return height;
    }

    public static int getViewWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int width = view.getMeasuredWidth();
        return width;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context 上下文
     * @param dpValue px值
     * @return dp值
     */
    public static int px2dp(final Context context, final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dpValue / scale) + PIONT_FIVE);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变 <功能详细描述>
     *
     * @param context context
     * @param spValue sp数值
     * @return 返回相数值
     * @see [类、类#方法、类#成员]
     */
    public static int sp2px(final Context context, final float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) ((spValue * fontScale) + PIONT_FIVE);
    }

    /**
     * 获取屏幕图片大小
     *
     * @param screenWidth 屏幕宽度
     * @param bitmap      图片
     * @return 图片大小
     */
    public static int getScreenPicHeight(final int screenWidth, final Bitmap bitmap) {
        final int picWidth = bitmap.getWidth();
        final int picHeight = bitmap.getHeight();
        int picScreenHeight = 0;
        picScreenHeight = (screenWidth * picHeight) / picWidth;
        return picScreenHeight;
    }

    /**
     * 获取手机屏幕高度
     *
     * @param context 上下文
     * @return 高度
     */
    public static int getMobileHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        return height;
    }

    /**
     * 获取手机屏幕宽度
     *
     * @param context 上下文
     * @return 宽度
     */
    public static int getMobileWidth(final Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();

    }

    /**
     * 获取屏幕DPI
     *
     * @param context 上下文
     * @return DPI
     */
    public static int densityDPI(final Context context) {
        if (context == null){
            return 0;
        }
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context 上下文
     * @param pxValue px值
     * @return sp值
     */
    public static int pxTosp(final Context context, final float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) ((pxValue / scale) + PIONT_FIVE);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context 上下文
     * @param spValue sp值
     * @return px值
     */
    public static int spTopx(final Context context, final float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) ((spValue * scale) + PIONT_FIVE);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px(像素)
     */
    public static float convertDpToPixel(final Context context, final float dpValue) {

        final float px = dpValue * (densityDPI(context) / 160f);
        return px;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context 上下文
     * @param pxValue px值
     * @return dp值
     */
    public static float convertPixelsToDp(final Context context, final float pxValue) {
        final float dp = pxValue / (densityDPI(context) / 160f);
        return dp;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }



    /**
     * 获取屏幕宽高
     *
     * @return
     */
    public static int[] getScreenWH(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;//宽度
        int height = dm.heightPixels;//高度
        return new int[]{width, height};
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(final Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    /**
     * 获取控件的位置
     */
    public int getViewMeasuredHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }


    public static String getDisplayString(final Context context) {
        String str = "";
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        str += screenWidth + "x" + screenHeight;
        float density = dm.density;
        str += "-" + density * 160;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        str += String.format("-%.0f*%.0f", xdpi, ydpi);
        return str;
    }



    private static final int PORTRAIT = 0;
    private static final int LANDSCAPE = 1;
    @NonNull
    private volatile static Point[] mRealSizes = new Point[2];


    public static int getScreenRealHeight(@Nullable Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getScreenHeight(context);
        }

        int orientation = context != null
                ? context.getResources().getConfiguration().orientation
                : context.getResources().getConfiguration().orientation;
        orientation = orientation == Configuration.ORIENTATION_PORTRAIT ? PORTRAIT : LANDSCAPE;

        if (mRealSizes[orientation] == null) {
            WindowManager windowManager = context != null
                    ? (WindowManager) context.getSystemService(Context.WINDOW_SERVICE)
                    : (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) {
                return getScreenHeight(context);
            }
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            mRealSizes[orientation] = point;
        }
        return mRealSizes[orientation].y;
    }

    public static int getScreenHeight(@Nullable Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
        return 0;
    }

    private volatile static boolean mHasCheckAllScreen;
    private volatile static boolean mIsAllScreenDevice;

    public static boolean isAllScreenDevice(Context context) {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice;
        }
        mHasCheckAllScreen = true;
        mIsAllScreenDevice = false;
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                mIsAllScreenDevice = true;
            }
        }
        return mIsAllScreenDevice;
    }

    public static int getFullActivityHeight(@Nullable Context context) {
        if (!isAllScreenDevice(context)) {
            return getScreenHeight(context);
        }
        return getScreenRealHeight(context);
    }
}
