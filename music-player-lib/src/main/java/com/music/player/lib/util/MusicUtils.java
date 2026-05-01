package com.music.player.lib.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.bean.MusicAlarmSetting;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.manager.MusicPlayerManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2018/3/922
 */

public class MusicUtils {

    private static volatile MusicUtils mInstance;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public static MusicUtils getInstance() {
        if (null == mInstance) {
            synchronized (MusicUtils.class) {
                if (null == mInstance) {
                    mInstance = new MusicUtils();
                }
            }
        }
        return mInstance;
    }

    private MusicUtils() {
    }

    /**
     * 不透明度
     * 100% — FF
     * 95% — F2
     * 90% — E6
     * 85% — D9
     * 80% — CC
     * 75% — BF
     * 70% — B3
     * 65% — A6
     * 60% — 99
     * 55% — 8C
     * 50% — 80
     * 45% — 73
     * 40% — 66
     * 35% — 59
     * 30% — 4D
     * 25% — 40
     * 20% — 33
     * 15% — 26
     * 10% — 1A
     * 5% — 0D
     * 0% — 00
     *
     * @param context
     */
    @SuppressLint("CommitPrefEdits")
    public synchronized void initSharedPreferencesConfig(Context context) {
        if (null == mSharedPreferences) {
            mSharedPreferences = context.getSharedPreferences(context.getPackageName() + MusicConstants.SP_KEY_NAME, Context.MODE_MULTI_PROCESS);
            mEditor = mSharedPreferences.edit();
        }
    }

    public boolean putString(String key, String value) {
        if (null != mEditor) {
            mEditor.putString(key, value);
            mEditor.commit();
            return true;
        }
        return false;
    }

    public boolean putInt(String key, int value) {
        if (null != mEditor) {
            mEditor.putInt(key, value);
            mEditor.commit();
            return true;
        }
        return false;
    }


    public boolean putBoolean(String key, boolean value) {
        if (null != mEditor) {
            mEditor.putBoolean(key, value);
            mEditor.commit();
            return true;
        }
        return false;
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        if (null != mSharedPreferences) {
            return mSharedPreferences.getString(key, defaultValue);
        }
        return "";
    }


    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        if (null != mSharedPreferences) {
            return mSharedPreferences.getInt(key, defaultValue);
        }
        return 0;
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        if (null != mSharedPreferences) {
            return mSharedPreferences.getLong(key, defaultValue);
        }
        return 0;
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public float getFloat(String key, float defaultValue) {
        if (null != mSharedPreferences) {
            return mSharedPreferences.getFloat(key, defaultValue);
        }
        return 0;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (null != mSharedPreferences) {
            return mSharedPreferences.getBoolean(key, defaultValue);
        }
        return false;
    }


    /**
     * 生成 min 到 max之间的随机数,包含 min max
     *
     * @param min 最小数
     * @param max 最大数
     * @return 从min-max的随机数，包含两者
     */
    public int getRandomNum(int min, int max) {
        return min + (int) (Math.random() * max);
    }


    /**
     * 时长格式化
     *
     * @param timeMs 单位毫秒
     * @return 格式化后的String类型时间
     */
    public String stringForAudioTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) (totalSeconds / 3600);
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 将dp转换成px
     *
     * @param dp dp单位数值
     * @return px数值
     */
    public float dpToPx(Context context, float dp) {
        return dp * context.getApplicationContext().getResources().getDisplayMetrics().density;
    }

    public int dpToPxInt(Context context, float dp) {
        return (int) (dpToPx(context, dp) + 0.5f);
    }


    //设备屏幕宽度
    public int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    //设备屏幕高度
    public int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 创建闹钟列表
     *
     * @return 闹钟数据集
     */
    public List<MusicAlarmSetting> createAlarmSettings() {
        List<MusicAlarmSetting> alarmSettings = new ArrayList<>();
        MusicAlarmSetting alarmSetting1 = new MusicAlarmSetting("10", MusicConstants.MUSIC_ALARM_MODEL_10);
        alarmSettings.add(alarmSetting1);
        MusicAlarmSetting alarmSetting2 = new MusicAlarmSetting("15", MusicConstants.MUSIC_ALARM_MODEL_15);
        alarmSettings.add(alarmSetting2);
        MusicAlarmSetting alarmSetting3 = new MusicAlarmSetting("30", MusicConstants.MUSIC_ALARM_MODEL_30);
        alarmSettings.add(alarmSetting3);
        MusicAlarmSetting alarmSetting4 = new MusicAlarmSetting("60", MusicConstants.MUSIC_ALARM_MODEL_60);
        alarmSettings.add(alarmSetting4);
        return alarmSettings;
    }

    /**
     * 返回正在播放的位置
     *
     * @param audioInfos 数据集
     * @param musicID    音频ID
     * @return 角标
     */
    public int getCurrentPlayIndex(List<?> audioInfos, long musicID) {
        if (null == audioInfos) {
            audioInfos = MusicPlayerManager.getInstance().getCurrentPlayList();
        }
        if (null != audioInfos && audioInfos.size() > 0) {
            List<BaseAudioInfo> audioInfoList = (List<BaseAudioInfo>) audioInfos;
            for (int i = 0; i < audioInfoList.size(); i++) {
                if (musicID == audioInfoList.get(i).id) {
                    return i;
                }
            }
        }
        return 0;
    }


    /**
     * 获取应用的包名
     *
     * @param context 上下文
     * @return 应用包名
     */
    public String getPackageName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 获取音频文件的封面地址
     *
     * @param audioInfo
     * @return 封面绝对路径
     */
    public String getMusicFrontPath(BaseAudioInfo audioInfo) {
        if (null == audioInfo) {
            return null;
        }
        //未购买，直接返回封面
        if (TextUtils.isEmpty(audioInfo.audio_url)) {
            return TextUtils.isEmpty(audioInfo.image) ? audioInfo.avatar : audioInfo.image;
        }
        if (audioInfo.audio_url.startsWith("http:") || audioInfo.audio_url.startsWith("https:")) {
            return TextUtils.isEmpty(audioInfo.image) ? audioInfo.avatar : audioInfo.image;
        } else {
            //本地音频文件
            return audioInfo.audio_url;
        }
    }



    /**
     * 创建根缓存目录
     *
     * @return 绝对路径
     */
    public String createRootPath(Context context) {
        String cacheRootPath = "";
        //SD卡已挂载，使用SD卡缓存目录，这个缓存补录数据不会随着应用的卸载而清除
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            if (null != context.getExternalCacheDir()) {
                cacheRootPath = context.getExternalCacheDir().getPath();//SD卡内部临时缓存目录
            }
            //内部缓存目录，会随着应用的卸载而清除
        } else {
            // /data/data/<application package>/cache
            if (null != context.getCacheDir()) {
                cacheRootPath = context.getCacheDir().getPath();//应用内部临时缓存目录
            } else {
                File cacheDirectory = getCacheDirectory(context, null);
                if (null != cacheDirectory) {
                    cacheRootPath = cacheDirectory.getAbsolutePath();
                }
            }
        }
        return cacheRootPath;
    }

    /**
     * 获取临时数据缓存目录
     *
     * @param context
     * @return 绝对路径
     */
    public String getCacheDir(Context context) {
        String cacheRootPath = null;
        if (null != context.getCacheDir()) {
            cacheRootPath = context.getCacheDir().getPath();
        } else if (null != context.getFilesDir()) {
            cacheRootPath = context.getFilesDir().getPath();
        } else if (isSdCardAvailable()) {
            if (null != context.getExternalCacheDir()) {
                cacheRootPath = context.getExternalCacheDir().getPath();//SD卡内部临时缓存目录
            }
        } else {
            File cacheDirectory = getCacheDirectory(context, null);
            if (null != cacheDirectory) {
                cacheRootPath = cacheDirectory.getAbsolutePath();
            }
        }
        return cacheRootPath;
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    /**
     * 递归创建文件夹
     *
     * @param file
     * @return 绝对路径，创建失败返回""
     */
    public String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {

                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 递归创建文件夹
     *
     * @param dirPath
     * @return 绝对路径，创建失败返回""
     */
    public String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {

                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());

                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 获取应用专属缓存目录
     * android 4.4及以上系统不需要申请SD卡读写权限
     * 因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间
     *
     * @param context 上下文
     * @param type    文件夹类型 可以为空，为空则返回API得到的一级目录
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    public File getCacheDirectory(Context context, String type) {
        File appCacheDir = getExternalCacheDirectory(context, type);
        if (appCacheDir == null) {
            appCacheDir = getInternalCacheDirectory(context, type);
        }

        if (appCacheDir == null) {
            Log.e("getCacheDirectory", "getCacheDirectory fail ,the reason is mobile phone unknown exception !");
        } else {
            if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                Log.e("getCacheDirectory", "getCacheDirectory fail ,the reason is make directory fail !");
            }
        }
        return appCacheDir;
    }

    /**
     * 获取SD卡缓存目录
     *
     * @param context 上下文
     * @param type    文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     *                否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     *                {@link Environment#DIRECTORY_MUSIC},
     *                {@link Environment#DIRECTORY_PODCASTS},
     *                {@link Environment#DIRECTORY_RINGTONES},
     *                {@link Environment#DIRECTORY_ALARMS},
     *                {@link Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link Environment#DIRECTORY_PICTURES}, or
     *                {@link Environment#DIRECTORY_MOVIES}.or 自定义文件夹名称
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    public File getExternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(type)) {
                appCacheDir = context.getExternalCacheDir();
            } else {
                appCacheDir = context.getExternalFilesDir(type);
            }

            if (appCacheDir == null) {// 有些手机需要通过自定义目录
                appCacheDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache/" + type);
            }

            if (appCacheDir == null) {
                Log.e("getExternalDirectory", "getExternalDirectory fail ,the reason is sdCard unknown exception !");
            } else {
                if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                    Log.e("getExternalDirectory", "getExternalDirectory fail ,the reason is make directory fail !");
                }
            }
        } else {
            Log.e("getExternalDirectory", "getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !");
        }
        return appCacheDir;
    }

    /**
     * 获取内存缓存目录
     *
     * @param type 子目录，可以为空，为空直接返回一级目录
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    public File getInternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (TextUtils.isEmpty(type)) {
            appCacheDir = context.getCacheDir();// /data/data/app_package_name/cache
        } else {
            appCacheDir = new File(context.getFilesDir(), type);// /data/data/app_package_name/files/type
        }

        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            Log.e("getInternalDirectory", "getInternalDirectory fail ,the reason is make directory fail !");
        }
        return appCacheDir;
    }


    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        // DocumentProvider
        if (isKitKat && isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static final String PATH_DOCUMENT = "document";

    private boolean isDocumentUri(Context context, Uri uri) {
        final List<String> paths = uri.getPathSegments();
        if (paths.size() < 2) {
            return false;
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            return false;
        }

        return true;
    }

    private String getDocumentId(Uri documentUri) {
        final List<String> paths = documentUri.getPathSegments();
        if (paths.size() < 2) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        return paths.get(1);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     *                      [url=home.php?mod=space&uid=7300]@return[/url] The value of
     *                      the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * Get AppCompatActivity from context
     *
     * @param context context
     * @return AppCompatActivity if it's not null
     */
    public AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 反射获取Context
     *
     * @return APP的全局上下文
     */
    public Context getApplicationContext() {
        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");

            Method method = ActivityThread.getMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(ActivityThread);//获取currentActivityThread 对象

            Method method2 = currentActivityThread.getClass().getMethod("getApplication");
            return (Context) method2.invoke(currentActivityThread);//获取 Context对象

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}