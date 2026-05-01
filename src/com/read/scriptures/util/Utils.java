package com.read.scriptures.util;

import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.read.scriptures.R;

public class Utils {
/**
     * @param target      防止多次点击的View
     * @param defaultTime 超时时间
     * @return
     */
    public static boolean isInvalidClick(@NonNull View target, @IntRange(from = 0) long defaultTime) {
        long curTimeStamp = System.currentTimeMillis();
        long lastClickTimeStamp = 0;
        Object o = target.getTag(R.id.invalid_click);
        if (o == null) {
            target.setTag(R.id.invalid_click, curTimeStamp);
            return false;
        }
        lastClickTimeStamp = (Long) o;
        boolean isInvalid = curTimeStamp - lastClickTimeStamp < defaultTime;
        if (!isInvalid) {
            target.setTag(R.id.invalid_click, curTimeStamp);
        }
        return isInvalid;
    }
}
