package com.read.scriptures.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

    /**
     * 返回yyyy-MM-dd日期格式 的字符串
     *
     * @param
     * @return
     */

    public static String currentDateString() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        return df.format(date);
    }

    public static Date currentDate() {
        return new Date();
    }

    public static String currentDateTimeString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        return df.format(new Date());
    }

    /**
     * 返回yyyy-MM-dd日期格式 的字符串
     *
     * @param c
     * @return
     */

    public static String toDateString(Calendar c) {
        Date date = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        return df.format(date);
    }

    public static String timeToDateString(long time) {
        Date date = new Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        return df.format(date);
    }

    public static String timeToDateString(Calendar c) {
        Date date = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        return df.format(date);
    }

    public static String dateTimeString(int year, int month, int day) {
        return dateTimeString(year, month, day, 0, 0, 0);
    }

    public static String dateTimeEightString(int year, int month, int day) {
        return dateTimeString(year, month, day, 8, 0, 0);
    }

    public static String dateTimeString(int year, int month, int day, int hour, int minute, int second) {
        String year_string = String.valueOf(year);
        String month_string = String.valueOf(month);
        if (month <= 9) {
            month_string = "0" + month;
        }
        String day_string = String.valueOf(day);
        if (day <= 9) {
            day_string = "0" + day;
        }
        String hour_string = String.valueOf(hour);
        if (hour <= 9) {
            hour_string = "0" + hour;
        }
        String minute_string = String.valueOf(minute);
        if (minute <= 9) {
            minute_string = "0" + minute;
        }
        String second_string = String.valueOf(second);
        if (second <= 9) {
            second_string = "0" + second;
        }
        return dateTimeString(year_string, month_string, day_string, hour_string, minute_string, second_string);
    }

    public static String dateTimeString(String year, String month, String day, String hour, String minute,
                                        String second) {
        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }

    /**
     * 可命名文件的时间样式
     *
     * @return
     */

    public static String currentDateStringCanNamedFile() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");// 设置日期格式
        return df.format(new Date());
    }

    public static Date stringToDate(String value) {
        Date date = null;
        try {
            date = stringToDateYYMMDDHHMMSS(value);
        } catch (ParseException e) {
            try {
                date = stringToDateYYMMDDHH(value);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return date;
    }

    public static Date stringToDateYYMMDDHH(String value) throws ParseException {
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.parse(value);
        return date;
    }

    public static Date stringToDateYYMMDDHHMMSS(String value) throws ParseException {
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = df.parse(value);
        return date;
    }

    public static boolean after(String date1, String date2) throws ParseException {
        long time1 = stringToDateYYMMDDHH(date1).getTime();
        long time2 = stringToDateYYMMDDHH(date2).getTime();
        return time1 > time2;
    }

    /**
     * 由value到现在是否已经超过了两天
     *
     * @param value 日期格式字符串
     * @return
     */

    public static boolean pastTwoDays(String value) {
        Date date = stringToDate(value);
        long old = date.getTime();
        long time = System.currentTimeMillis();
        long past = time - old;
        // 一天的毫秒数
        long day = 24 * 60 * 60 * 1000;
        // if (past < 0) {
        // return true;
        // } else
        if (past > 2 * day) {
            return true;
        }
        return false;
    }

    /**
     * 将毫秒数格式化为"##:##"的时间
     *
     * @param milliseconds 毫秒数
     * @return ##:##
     */
    public static String formatTime(int milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = milliseconds / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * .Description://根据字符日期返回星期几
     * .Author:麦克劳林
     * .@Date: 2018/12/29
     */
    public static String getWeek(String dateTime) {
        String week = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateTime);
            SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
            week = dateFm.format(date);
            week = week.replaceAll("星期", "周");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return week;
    }

    /**
     * 获取过去7天内的日期数组
     *
     * @param intervals intervals天内
     * @return 日期数组
     */
    public static ArrayList<String> getDays(int intervals) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        for (int i = intervals - 1; i >= 0; i--) {
            pastDaysList.add(getPastDate(i));
        }
        return pastDaysList;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }
}
