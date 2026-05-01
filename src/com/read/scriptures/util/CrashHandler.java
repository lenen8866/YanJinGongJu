package com.read.scriptures.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author user
 */
public final class CrashHandler implements UncaughtExceptionHandler {
    /**
     * TAG
     */
    public static final String TAG = "CrashHandler";

    // CrashHandler实例
    private static final CrashHandler INSTANCE = new CrashHandler();

    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;

    // 程序的Context对象
    private Context mContext;

    // 用来存储设备信息和异常信息
    private final Map<String, String> info = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /***
     * 获取CrashHandler实例 ,单例模式
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @return 单例模式
     * @see [类、类#方法、类#成员]
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context context
     */
    public void init(final Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        try {
            if (!handleException(ex) && (mDefaultHandler != null)) {
                // 如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(thread, ex);
            } else {
//                final Intent i = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.putExtra("isKilled", true);
//                mContext.startActivity(i);
//            AppManager.getAppManager().appExit(mContext);
                //退出程序
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "error : ", e);
                }
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        } else {
            LogUtil.debug(TAG, "APPLICATION EXCEPTION ", ex);
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }.start();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx ctx
     */
    private void collectDeviceInfo(final Context ctx) {
        try {
            final PackageManager pm = ctx.getPackageManager();
            final PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                final String versionName = pi.versionName == null ? "null" : pi.versionName;
                final String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (final NameNotFoundException e) {
            LogUtil.error(TAG, "an error occured when collect package info NameNotFoundException");
        }
        final Field[] fields = Build.class.getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            try {
                info.put(field.getName(), field.get(null).toString());
                LogUtil.debug(TAG, field.getName() + " : " + field.get(null));
            } catch (final IllegalArgumentException e) {
                LogUtil.error(TAG, "an error occured when collect crash info IllegalArgumentException");
            } catch (final IllegalAccessException e) {
                LogUtil.error(TAG, "an error occured when collect crash info IllegalAccessException");
            }

        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(final Throwable ex) {

        final StringBuffer buffer = new StringBuffer();
        for (final Map.Entry<String, String> entry : info.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            buffer.append(key + "=" + value + "\n");
        }

        final Writer writer = new StringWriter();
        final PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        final String result = writer.toString();
        buffer.append(result);
        FileOutputStream stream = null;
        try {
            final long timestamp = System.currentTimeMillis();
            final String time = formatter.format(new Date());
            final String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // final String path = "/sdcard/crash/";
                final String path = Environment.getExternalStorageDirectory().getPath() + "/crash/";
                final File dir = new File(path);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        LogUtil.error(TAG, "an error occured while mkdirs...");
                    }
                }
                stream = new FileOutputStream(path + fileName);
                stream.write(buffer.toString().getBytes("UTF-8"));
            }
            return fileName;
        } catch (final FileNotFoundException e) {
            LogUtil.error(TAG, "an error occured while writing file......", e);
        } catch (final UnsupportedEncodingException e) {
            LogUtil.error(TAG, "an error occured while writing file......", e);
        } catch (final IOException e) {
            LogUtil.error(TAG, "an error occured while writing file......", e);
        } finally {
            if (null != stream) {
                try {
                    stream.close();
                } catch (final IOException e) {
                    LogUtil.error(TAG, "an error occured while fos close.......", e);
                }
            }
        }
        return null;
    }
}