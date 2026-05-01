package com.read.scriptures.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.read.scriptures.app.HuDongApplication;

public class SystemUtils {

    private static String code = "MS210019011520160001";

    public static String getCode() {
        return code;
    }

    public static final int INVALID_VERSION_CODE = -1;

    public static final String INVALID_VERSION_NAME = "0";

    public static int getVersionCode(Context context) {
        int versionCode = INVALID_VERSION_CODE;
        try {
            versionCode = HuDongApplication.getInstance().getPackageManager().getPackageInfo(context.getPackageName(), 1).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getVersionName(Context context) {
        String versionName = INVALID_VERSION_NAME;
        try {
            versionName = HuDongApplication.getInstance().getPackageManager().getPackageInfo(context.getPackageName(), 1).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        if (context == null){
            return false;
        }
        try {
            ConnectivityManager manager = (ConnectivityManager) HuDongApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info == null) {
                return false;
            }
            return info.isConnected();
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 跳转到相应URL
     * 
     * @param activity
     * @param url
     */
    public static void jumpToUrl(Context activity, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(uri);
        activity.startActivity(intent);
    }
}
