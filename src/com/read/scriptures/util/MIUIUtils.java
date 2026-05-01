package com.read.scriptures.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;

public abstract class MIUIUtils {
    /**
     * 判断MIUI的悬浮窗权限
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isMiuiFloatWindowOpAllowed(final Context context) {
        if (!"Xiaomi".equals(Build.MANUFACTURER)) {
            return true;
        }
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            if ((context.getApplicationInfo().flags & (1 << 27)) == (1 << 27)) {
                return true;
            } else {
                return false;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(final Context context, final int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            final AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {

                Class.forName(manager.getClass().getName());
                final Method method =
                        manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                final int property =
                        (Integer) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
                LogUtil.test(AppOpsManager.MODE_ALLOWED + " invoke " + property);

                if (AppOpsManager.MODE_ALLOWED == property) {
                    return true;
                } else {
                    return false;
                }
            } catch (final Exception e) {
                LogUtil.test(e.getMessage());
            }
        } else {
            LogUtil.test("Below API 19 cannot invoke!");
        }
        return false;
    }

    /**
     * 经测试V5版本是有区别的
     *
     * @param context
     */
    public static void openMiuiPermissionActivity(final Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        if (!"Xiaomi".equals(Build.MANUFACTURER)) {
            if ("HUAWEI1".equals(Build.MANUFACTURER)) {
                LogUtil.test("HUAWEI");
                intent = gotoHuaweiPermission();
                final Activity a = (Activity) context;
                a.startActivity(intent);
            } else {
                LogUtil.test("Build.MANUFACTURER:" + Build.MANUFACTURER);
                intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                //                final ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
                //                intent.setComponent(cName);
                final Activity a = (Activity) context;
                a.startActivity(intent);
            }
        } else if ("V5".equals(getProperty())) {
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (final PackageManager.NameNotFoundException e) {
                Log.e("canking", "error");
            }
            intent.setClassName("com.miui.securitycenter", "com.miui.securitycenter.permission.AppPermissionsEditor");
            intent.putExtra("extra_package_uid", pInfo.applicationInfo.uid);
        } else {
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
        }

        if (context instanceof Activity) {
            try {
                final Activity a = (Activity) context;
                a.startActivityForResult(intent, 2);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        //        }
        else {
            Log.e("canking", "Intent is not available!");
        }
    }

    /**
     * 华为的权限管理页面
     */
    private static Intent gotoHuaweiPermission() {
        final Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final ComponentName comp =
                new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
        intent.setComponent(comp);
        return intent;
    }

    public static String getProperty() {
        String property = "null";
        LogUtil.test("phone:" + Build.MANUFACTURER);
        if (!"Xiaomi".equals(Build.MANUFACTURER)) {
            return property;
        }
        try {
            final Class<?> spClazz = Class.forName("android.os.SystemProperties");
            final Method method = spClazz.getDeclaredMethod("get", String.class, String.class);
            property = (String) method.invoke(spClazz, "ro.miui.ui.version.name", null);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return property;
    }
}
