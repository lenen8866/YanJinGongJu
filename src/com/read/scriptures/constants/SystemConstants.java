package com.read.scriptures.constants;

import com.read.scriptures.app.HuDongApplication;

/**
 * Created by Administrator on 2015/6/12.
 */
public class SystemConstants {

    public static final int XLIST_REFRESH_FINISH = 0;
    public static final int XLIST_LOADH_FINISH = 1;
    public static final int XLIST_REQUEST_FINISH = 2;

    public static final int MONTH = 30;
    public static final int QUARTER = 90;
    public static final int HALF_YEAR = 180;
    public static final int YEAR = 360;
    public static final int FOREVER = -1;

    public static final int Notification_ID_BASE = 120;

    public static String WX_DAYS = "30";
    public static String WX_MONEY = "0";
    public static String WX_UUID = "";
    public static String WX_LEVEL_TYPE = "";
    public static int SPEECH_TYPE = 1;

    /** 字体名称 */
    public static String FONT_NAME = "-1";
    public static boolean isActive = false;
    public static boolean isUpdateBook = false;

    // FIX: 原来使用 Environment.getExternalStorageDirectory()（SD 卡根目录）
    // Android 10+ 已禁止直接访问 SD 卡根目录，会导致崩溃或 SecurityException。
    // 现改为应用专属外部目录（getExternalFilesDir），无需存储权限，兼容 Android 5-14。
    // 外部存储不可用时自动回退到内部存储。
    private static String getAppExternalDir() {
        java.io.File extDir = HuDongApplication.getInstance().getExternalFilesDir(null);
        if (extDir != null) {
            return extDir.getAbsolutePath();
        }
        return HuDongApplication.getInstance().getFilesDir().getAbsolutePath();
    }

    public static final String APP_PATH = getAppExternalDir() + "/YanJing/";
    public static final String APP_BANNER_PATH = getAppExternalDir() + "/Banner/";
    public static final String SHUKU = getAppExternalDir() + "/ShuKu/";
    public static final String APK = HuDongApplication.getInstance().getFilesDir().getAbsolutePath() + "/HD_APK/hudong.apk";
    public static final String DOWNLOAD_FILE_PATH = HuDongApplication.getInstance().getFilesDir().getAbsolutePath() + "/";

    // 原 SDCARD_PATH 已废弃，保留空字段避免其他处引用时编译报错
    @Deprecated
    public static final String SDCARD_PATH = "";
}
