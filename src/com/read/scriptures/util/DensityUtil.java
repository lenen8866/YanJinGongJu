package com.read.scriptures.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.read.scriptures.app.HuDongApplication;

public class DensityUtil {


    public static int getScreenWidth(Context context) {
        //获取屏幕信息
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels; // 屏幕高（像素，如：1280px）
    }

    public static int getScreenHeight(Context context) {
        //获取屏幕信息
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels; // 屏幕高（像素，如：1280px）
    }

    public static int getFullHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        // 可能有虚拟按键的情况
        display.getRealSize(outPoint);
        return outPoint.y;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int dip2px(Context var0, float var1) {
        return (int) (0.5F + var1 * var0.getResources().getDisplayMetrics().density);
    }

    public static int dip2px(float var1) {
        return (int) (0.5F + var1 * HuDongApplication.getContext().getResources().getDisplayMetrics().density);
    }
}  