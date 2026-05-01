//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ActivityUtil {
    public ActivityUtil() {
    }

    public static void back(Activity var0) {
        back(var0, -1, -1);
    }

    public static void back(Activity var0, int var1, int var2) {
        var0.finish();
        if (var1 != -1 && var2 != -1) {
            var0.overridePendingTransition(var1, var2);
        }

    }

    public static void backWithResult(Activity var0, int var1, Bundle var2) {
        backWithResult(var0, var1, var2, -1, -1);
    }

    public static void backWithResult(Activity var0, int var1, Bundle var2, int var3, int var4) {
        Intent var5 = new Intent();
        if (var2 != null) {
            var5.putExtras(var2);
        }

        var0.setResult(var1, var5);
        var0.finish();
        if (var3 != -1 && var4 != -1) {
            var0.overridePendingTransition(var3, var4);
        }

    }

    public static void next(Activity var0, Class<?> var1) {
        next(var0, var1, (Bundle) null, -1, -1, -1, -1, false);
    }

    public static void next(Activity var0, Class<?> var1, int var2, int var3) {
        next(var0, var1, (Bundle) null, -1, -1, var2, var3, false);
    }

    public static void next(Activity var0, Class<?> var1, int var2, int var3, int var4) {
        next(var0, var1, (Bundle) null, -1, var2, var3, var4, false);
    }

    public static void next(Activity var0, Class<?> var1, int var2, int var3, int var4, boolean var5) {
        next(var0, var1, (Bundle) null, -1, var2, var3, var4, var5);
    }

    public static void next(Activity var0, Class<?> var1, int var2, int var3, boolean var4) {
        next(var0, var1, (Bundle) null, -1, -1, var2, var3, var4);
    }

    public static void next(Activity var0, Class<?> var1, Bundle var2, int var3) {
        next(var0, var1, var2, var3, -1, -1, -1, false);
    }

    public static void next(Activity var0, Class<?> var1, Bundle var2, int var3, int var4, int var5) {
        next(var0, var1, var2, var3, -1, var4, var5, false);
    }

    public static void next(Activity var0, Class<?> var1, Bundle var2, int var3, int var4, int var5, int var6) {
        next(var0, var1, var2, var3, -1, var5, var6, false);
    }

    public static void next(Activity var0, Class<?> var1, Bundle var2, int var3, int var4, int var5, int var6, boolean var7) {
        Intent var8 = new Intent(var0, var1);
        if (var2 != null) {
            var8.putExtras(var2);
        }

        if (var4 != -1) {
            var8.setFlags(var4);
        }

        if (var3 < 0) {
            var0.startActivity(var8);
        } else {
            var0.startActivityForResult(var8, var3);
        }

        if (var5 != -1 && var6 != -1) {
            var0.overridePendingTransition(var5, var6);
        }

        if (var7) {
            var0.finish();
        }

    }

    public static void next(Activity var0, Class<?> var1, Bundle var2, int var3, int var4, int var5, boolean var6) {
        next(var0, var1, var2, var3, -1, var4, var5, var6);
    }

    public static void next(Activity var0, Class<?> var1, Bundle var2, int var3, boolean var4) {
        next(var0, var1, var2, var3, -1, -1, -1, var4);
    }

    public static void next(Activity var0, Class<?> var1, boolean var2) {
        next(var0, var1, (Bundle) null, -1, -1, -1, -1, var2);
    }

    public static void nextActivityWithClearTop(Activity var0, Class<?> var1) {
        nextActivityWithClearTop(var0, var1, (Bundle) null, -1, -1);
    }

    public static void nextActivityWithClearTop(Activity var0, Class<?> var1, int var2, int var3) {
        nextActivityWithClearTop(var0, var1, (Bundle) null, var2, var3);
    }

    public static void nextActivityWithClearTop(Activity var0, Class<?> var1, Bundle var2) {
        nextActivityWithClearTop(var0, var1, var2, -1, -1);
    }

    public static void nextActivityWithClearTop(Activity var0, Class<?> var1, Bundle var2, int var3, int var4) {
        Intent var5 = new Intent(var0, var1);
        var5.setFlags(67108864);
        if (var2 != null) {
            var5.putExtras(var2);
        }

        var0.startActivity(var5);
        if (var3 != -1 && var4 != -1) {
            var0.overridePendingTransition(var3, var4);
        }

        var0.finish();
    }
}
