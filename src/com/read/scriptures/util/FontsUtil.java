package com.read.scriptures.util;

import android.content.Context;
import android.graphics.Typeface;

public class FontsUtil {

    public static FontsUtil fontsUtil;

    private Context mContext;
    private static Typeface numTypeface;

    public FontsUtil(Context context) {
        this.mContext = context;
        // 字体资源放在内存中，避免反复读取浪费资源
        numTypeface = Typeface.createFromAsset(mContext.getAssets(), "simkai.ttf");

    }

    public static FontsUtil getInstance(Context context) {
        if (fontsUtil == null) {
            fontsUtil = new FontsUtil(context.getApplicationContext());
        }
        return fontsUtil;
    }


    public MyTypefaceSpan getMyNumTypefaceSpan() {
        return new MyTypefaceSpan(numTypeface);
    }

}