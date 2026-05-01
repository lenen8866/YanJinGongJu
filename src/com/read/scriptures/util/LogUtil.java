package com.read.scriptures.util;

import android.text.TextUtils;
import android.util.Log;

import com.read.scriptures.app.HuDongApplication;

/**
 * Log工具，类似android.util.Log。 tag自动产生，格式:
 * customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 */
public class LogUtil {
    /**
     * customTagPrefix
     */
    private static String customTagPrefix = "";

    /**
     * allowD
     */
    private static boolean allowD = true;

    /**
     * allowE
     */
    private static boolean allowE = true;

    /**
     * allowI
     */
    private static boolean allowI = true;

    /**
     * allowV
     */
    private static boolean allowV = true;

    /**
     * allowW
     */
    private static boolean allowW = true;


    /**
     * customLogger
     */
    private static CustomLogger customLogger;

    /**
     * allowWtf
     */
    private static boolean allowWtf = true;

    private LogUtil() {
    }

    private static String generateTag(final StackTraceElement caller) {
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    /**
     * CustomLogger
     *
     * @author wwx277222
     * @version [版本号, 2015年10月13日]
     * @since [产品/模块版本]
     */
    private interface CustomLogger {
        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @see [类、类#方法、类#成员]
         */
        void debug(String tag, String content);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @param tr      tr
         * @see [类、类#方法、类#成员]
         */
        void debug(String tag, String content, Throwable tr);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @see [类、类#方法、类#成员]
         */
        void error(String tag, String content);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @param tr      tr
         * @see [类、类#方法、类#成员]
         */
        void error(String tag, String content, Throwable tr);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @see [类、类#方法、类#成员]
         */
        void info(String tag, String content);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @param tr      tr
         * @see [类、类#方法、类#成员]
         */
        void info(String tag, String content, Throwable tr);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @see [类、类#方法、类#成员]
         */
        void verbose(String tag, String content);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @param tr      tr
         * @see [类、类#方法、类#成员]
         */
        void verbose(String tag, String content, Throwable tr);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @see [类、类#方法、类#成员]
         */
        void warn(String tag, String content);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @param tr      tr
         * @see [类、类#方法、类#成员]
         */
        void warn(String tag, String content, Throwable tr);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag tag
         * @param tr  tr
         * @see [类、类#方法、类#成员]
         */
        void warn(String tag, Throwable tr);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @see [类、类#方法、类#成员]
         */
        void wtf(String tag, String content);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag     tag
         * @param content content
         * @param tr      tr
         * @see [类、类#方法、类#成员]
         */
        void wtf(String tag, String content, Throwable tr);

        /**
         * <一句话功能简述> <功能详细描述>
         *
         * @param tag tag
         * @param tr  tr
         * @see [类、类#方法、类#成员]
         */
        void wtf(String tag, Throwable tr);
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void debug(final String content) {
        if (!allowD || (null == content)) {
            return;
        }
        // 获取堆栈信息
        final StackTraceElement caller = getCallerStackTraceElement();
        // 调整堆栈信息样式
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.debug(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.d(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void debug(final String tag, final String content) {
        if (!allowD || (null == tag) || (null == content)) {
            return;
        }

        if (customLogger != null) {
            customLogger.debug(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.d(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void debug(final String content, final Throwable tr) {
        if (!allowD || (null == content) || (null == tr)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.debug(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.d(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void debug(final String tag, final String content, final Throwable tr) {
        if (!allowD || (null == tag) || (null == content) || (null == tr)) {
            return;
        }
        if (customLogger != null) {
            customLogger.debug(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.d(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void error(final String content) {
        if (!allowE || (null == content)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.error(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.e(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void error(final String tag, final String content) {
        if (!allowE || (null == tag) || (null == content)) {
            return;
        }

        if (customLogger != null) {
            customLogger.error(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.e(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void error(final String content, final Throwable tr) {
        if (!allowE || (null == content) || (null == tr)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.error(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.e(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void error(final String tag, final String content, final Throwable tr) {
        if ((!allowE) || (null == tag) || (null == content) || (null == tr)) {
            return;
        }
        if (customLogger != null) {
            customLogger.error(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.e(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void info(final String content) {
        if (!allowI || (null == content)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.info(tag, content);
        } else {
            if ( HuDongApplication.getInstance().isLog()) {
                Log.i(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void info(final String tag, final String content) {
        if (!allowI || (null == tag) || (null == content)) {
            return;
        }

        if (customLogger != null) {
            customLogger.info(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.i(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void info(final String content, final Throwable tr) {
        if (!allowI || (null == content) || (null == tr)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.info(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.i(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void info(final String tag, final String content, final Throwable tr) {
        if ((!allowI) || (null == tag) || (null == content) || (null == tr)) {
            return;
        }
        if (customLogger != null) {
            customLogger.info(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.i(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void verbose(final String content) {
        if (!allowV || (null == content)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.verbose(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.v(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void verbose(final String tag, final String content) {
        if (!allowV || (null == tag) || (null == content)) {
            return;
        }

        if (customLogger != null) {
            customLogger.verbose(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.v(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void verbose(final String content, final Throwable tr) {
        if (!allowV || (null == content) || (null == tr)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.verbose(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.v(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void verbose(final String tag, final String content, final Throwable tr) {
        if ((!allowV) || (null == tag) || (null == content) || (null == tr)) {
            return;
        }
        if (customLogger != null) {
            customLogger.verbose(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.v(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void warn(final String content) {
        if (!allowW || (null == content)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.warn(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.w(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void warn(final String tag, final String content) {
        if (!allowW || (null == tag) || (null == content)) {
            return;
        }

        if (customLogger != null) {
            customLogger.warn(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.w(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void warn(final String content, final Throwable tr) {
        if (!allowW || (null == content) || (null == tr)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.warn(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.w(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void warn(final String tag, final String content, final Throwable tr) {
        if ((!allowW) || (null == tag) || (null == content) || (null == tr)) {
            return;
        }
        if (customLogger != null) {
            customLogger.warn(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.w(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tr tr
     * @see [类、类#方法、类#成员]
     */
    public static void warn(final Throwable tr) {
        if (!allowW || (null == tr)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.warn(tag, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.w(tag, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void wtf(final String content) {
        if (!allowWtf || (null == content)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.wtf(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void wtf(final String tag, final String content) {
        if (!allowWtf || (null == tag) || (null == content)) {
            return;
        }

        if (customLogger != null) {
            customLogger.wtf(tag, content);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.wtf(tag, content);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void wtf(final String content, final Throwable tr) {
        if (!allowWtf || (null == content) || (null == tr)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.wtf(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tag     tag
     * @param content content
     * @param tr      tr
     * @see [类、类#方法、类#成员]
     */
    public static void wtf(final String tag, final String content, final Throwable tr) {
        if ((!allowWtf) || (null == tag) || (null == content) || (null == tr)) {
            return;
        }
        if (customLogger != null) {
            customLogger.wtf(tag, content, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.wtf(tag, content, tr);
            }
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param tr tr
     * @see [类、类#方法、类#成员]
     */
    public static void wtf(final Throwable tr) {
        if (!allowWtf || (null == tr)) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        final String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, tr);
        } else {
            if (HuDongApplication.getInstance().isLog()) {
                Log.wtf(tag, tr);
            }
        }
    }

    /**
     * 线程池 [方法功能说明]
     *
     * @return 线程
     */
    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void log(final String content) {
        if (!HuDongApplication.getInstance().isLog()) {
            return;
        }
        final StackTraceElement caller = getCallerStackTraceElement();
        // 调整堆栈信息样式
        final String tag = generateTag(caller);
        FileUtil.saveString("[" + tag + "]" + content);
        Log.i("test", content);
        // }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param content content
     * @see [类、类#方法、类#成员]
     */
    public static void test(final String content) {
        if (!HuDongApplication.getInstance().isLog()) {
            return;
        }
        Log.i("test", content);
        // }
    }

    public static void setCustomLogger(CustomLogger customLogger) {
        LogUtil.customLogger = customLogger;
    }
}
