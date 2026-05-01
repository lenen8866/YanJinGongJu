package com.read.scriptures.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by Administrator on 2018/7/30.
 */

public class AppIconHelper {
    public static Bitmap getAppIcon(Drawable drawable) {

        if (Build.VERSION.SDK_INT >= 26) {
            return AppIconHelperV26.getAppIcon(drawable);
        }
        try {
            return ((BitmapDrawable) drawable).getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
