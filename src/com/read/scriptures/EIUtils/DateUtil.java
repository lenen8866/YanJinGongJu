//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.text.TextUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

public class DateUtil {
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYYMMDDHH_MM_SS = "yyyy/MM/dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";


    public static String dateToStr(Date var0, String var1) {
        String var2 = "";
        if (var0 != null) {
            var2 = (new SimpleDateFormat(var1, Locale.getDefault())).format(var0);
        }

        return var2;
    }


    public static String format(String var0, String var1, String var2) {
        if (var0 != null) {
            try {
                if (!TextUtils.isEmpty(var0)) {
                    SimpleDateFormat var4 = new SimpleDateFormat(var1, Locale.US);
                    String var5 = (new SimpleDateFormat(var2, Locale.US)).format(var4.parse(var0));
                    return var5;
                }
            } catch (Exception var6) {
                return null;
            }
        }

        return "";
    }


    public static String friendlyTime(String var0) {
        SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date var2 = strToDateLong(var0);
        String var3;
        if (var2 == null) {
            var3 = "Unknown";
        } else {
            var3 = "";
            Calendar var4 = Calendar.getInstance();
            if (var1.format(var4.getTime()).equals(var1.format(var2))) {
                int var9 = (int)((var4.getTimeInMillis() - var2.getTime()) / 3600000L);
                if (var9 == 0) {
                    return Math.max((var4.getTimeInMillis() - var2.getTime()) / 60000L, 1L) + "分钟前";
                }

                return var9 + "小时前";
            }

            long var5 = var2.getTime() / 86400000L;
            int var7 = (int)(var4.getTimeInMillis() / 86400000L - var5);
            if (var7 == 0) {
                int var8 = (int)((var4.getTimeInMillis() - var2.getTime()) / 3600000L);
                if (var8 == 0) {
                    return Math.max((var4.getTimeInMillis() - var2.getTime()) / 60000L, 1L) + "分钟前";
                }

                return var8 + "小时前";
            }

            if (var7 == 1) {
                return "昨天";
            }

            if (var7 == 2) {
                return "前天";
            }

            if (var7 > 2 && var7 <= 10) {
                return var7 + "天前";
            }

            if (var7 > 10) {
                return var1.format(var2);
            }
        }

        return var3;
    }



    public static String getNextDay(String var0, String var1, int var2) {
        try {
            Date var4 = strToDateShort(var0);
            var4.setTime(1000L * (var4.getTime() / 1000L + (long) (60 * 60 * var2 * 24)));
            String var5 = dateToStr(var4, var1);
            return var5;
        } catch (Exception var6) {
            return "";
        }
    }
    public static String getStringDate(String var0) {
        Date var1 = new Date();
        return (new SimpleDateFormat(var0, Locale.getDefault())).format(var1);
    }
    public static String getStringDateShort() {
        Date var0 = new Date();
        return (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())).format(var0);
    }

    public static int getWeekInt(String var0) {
        Date var1 = strToDateShort(var0);
        Calendar var2 = Calendar.getInstance();
        var2.setTime(var1);
        return var2.get(7);
    }


    public static String getWeekStr(String var0) {
        String var1 = "";
        int var2 = getWeekInt(var0);
        if (1 == var2) {
            var1 = "星期日";
        } else {
            if (2 == var2) {
                return "星期一";
            }

            if (3 == var2) {
                return "星期二";
            }

            if (4 == var2) {
                return "星期三";
            }

            if (5 == var2) {
                return "星期四";
            }

            if (6 == var2) {
                return "星期五";
            }

            if (7 == var2) {
                return "星期六";
            }
        }

        return var1;
    }

    public static String getWeekStrFormat(String var0) {
        Date var1 = strToDateShort(var0);
        Calendar var2 = Calendar.getInstance();
        var2.setTime(var1);
        return (new SimpleDateFormat("EEEE", Locale.getDefault())).format(var2.getTime());
    }


    public static Date strToDate(String var0, String var1) {
        return (new SimpleDateFormat(var1, Locale.getDefault())).parse(var0, new ParsePosition(0));
    }

    public static Date strToDateLong(String var0) {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())).parse(var0, new ParsePosition(0));
    }

    public static Date strToDateShort(String var0) {
        return (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())).parse(var0, new ParsePosition(0));
    }


}
