package com.read.scriptures.util;

import android.content.Intent;
import android.text.TextUtils;

import com.read.scriptures.EIUtils.DateUtil;
import com.read.scriptures.model.Spirituality;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String getDateSp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
    public static String getMonth() {//yyyy-MM
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Date start = calendar.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("M", Locale.getDefault());
        return sf.format(start);
    }
    public static String formatTime(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");// HH:mm:ss
        //获取当前时间
        return simpleDateFormat.format(time);
    }

    public static String parseTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        //获取当前时间
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    public static int getDateTag() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return Integer.parseInt(simpleDateFormat.format(date));
    }

    public static long getTimeLong(String date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getUpdateDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");// HH:mm:ss
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    public static String getAudioUpdateDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    public static long getNow() {
        return new Date(System.currentTimeMillis()).getTime();
    }

    public static long diffTime(long past, long now) {
        return now - past;
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) {
        String res = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            res = String.valueOf(ts);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long getNowStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 取得今天，昨天，前天，明天，后天...的日期
     *
     * @param sel --- 0->当天    -1->昨天     -2->前天      1->明天      2->后天  ......
     * @return ------- 返回指定日期
     */
    public static String getOurSelData(int sel) {
        String str = "";
        //格式化日期格式
        SimpleDateFormat df = new SimpleDateFormat("M" + "月" + "d" + "日");
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, sel);
        str = df.format(calendar.getTime());

        return str;
    }

    /**
     * 取得今天，昨天，前天，明天，后天...的日期
     *
     * @param sel --- 0->当天    -1->昨天     -2->前天      1->明天      2->后天  ......
     * @return ------- 返回指定日期
     */
    public static String getWeekSelData(int sel) {
        String str = "";
        //格式化日期格式
        SimpleDateFormat df = new SimpleDateFormat("y" + "年" + "M" + "月" + "d" + "日");
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, sel);
        str = df.format(calendar.getTime());

        return str;
    }

    public static String timeStamp2DateC(long time) {
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);

    }

    public static String timeStamp2Date(long time) {
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分SS秒");
        return format.format(date);

    }

    public static String timeStamp2DateNoSecond(long time) {
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        return format.format(date);

    }

    public static String timeStamp2DateNoSecond1(long time) {
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时");
        return format.format(date);

    }

    public static String timeStamp2DateNoHour(long time) {
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);

    }

    public static String timeStamp2DateHour(long time) {
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时");
        return format.format(date);

    }

    /*
     *计算time2减去time1的差值 差值只设置 几天 几个小时 或 几分钟
     * 根据差值返回多长之间前或多长时间后
     * */
    public static String getDistanceTime(long time1, long time2) {
        time1 = time1 * 1000;
        time2 = time2 * 1000;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff;
        String flag;
        if (time1 < time2) {
            diff = time2 - time1;
            flag = "前";
        } else {
            diff = time1 - time2;
            flag = "后";
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) return day + "天";
        if (hour != 0) return hour + "小时";
        if (min != 0) return min + "分钟";
        return "刚刚";
    }

    /*
     *计算time2减去time1的差值 差值只设置 几天 几个小时 或 几分钟
     * 根据差值返回多长之间前或多长时间后
     * */
    public static String getDistanceTimeActive(long time1, long time2) {
        time1 = time1 * 1000;
        time2 = time2 * 1000;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff;
        String flag;
        if (time1 < time2) {
            diff = time2 - time1;
            flag = "前";
        } else {//未激活
            return "未激活";
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0 && hour != 0) {
            return day + "天" + hour + "小时";
        }
        if (day != 0) return day + "天";
        if (hour != 0) return hour + "小时";
        if (min != 0) return min + "分钟";
        return "刚刚";
    }

    /*
     *计算time2减去time1的差值 差值只设置 几天 几个小时 或 几分钟
     * 根据差值返回多长之间前或多长时间后
     * */
    public static long getDistanceDays(long time1, long time2) {
        time1 = time1 * 1000;
        time2 = time2 * 1000;
        long day = 0;
        long diff;
        String flag;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {//未激活
            return -1;
        }
        day = diff / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    public static ArrayList<Spirituality> sortData(ArrayList<Spirituality> mList) {
        Collections.sort(mList, new Comparator<Spirituality>() {
            /**
             *
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(Spirituality lhs, Spirituality rhs) {
                Date date1 = stringToDate(lhs.getDaytime());
                Date date2 = stringToDate(rhs.getDaytime());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1 == null || date2 == null) {
                    return 0;
                }
                if (date1.before(date2)) {
                    return -1;
                }
                return 1;
            }
        });
        return mList;
    }

    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    public static int getDateWeekSeq(String date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
//        c.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
//        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周从周一开始
//        c.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        c.setTime(DateUtil.strToDate(date, "yy年MM月dd日"));
        int week = c.get(Calendar.WEEK_OF_YEAR);
        return week;
    }

    public static int getYearSeq(String date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        c.setTime(DateUtil.strToDate(date, "MM月dd日"));
        return c.get(Calendar.WEEK_OF_YEAR);
    }
    public static String getYearStr() {//yyyy-MM
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Date start = calendar.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy", Locale.getDefault());
        return sf.format(start);
    }

    public static long getOverYearTime(int y) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, y);
        long time = 0;
        try {
            String d = df.format(c.getTime());
            time = df.parse(df.format(c.getTime())).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static int getWeekDay() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return 7;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            default:
                return 7;
        }
    }

    public static long getTime(String val) {
        //
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = null;
        try {
            date = simpleDateFormat.parse(val);
            return date.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public static long parseTime(String val) {
        try {
            if (TextUtils.isEmpty(val)) {
                return -1;
            }
            String[] my = val.split(":");
            int hour = 0;
            int min = 0;
            int sec = 0;
            if (my.length == 3) {//01：01：01  时分秒
                hour = Integer.parseInt(my[0]);
                min = Integer.parseInt(my[1]);
                sec = Integer.parseInt(my[2]);
            } else if (my.length == 2) {
                min = Integer.parseInt(my[0]);
                sec = Integer.parseInt(my[1]);
            } else if (my.length == 1) {
                sec = Integer.parseInt(my[0]);
            } else {

            }
            return hour * 3600 + min * 60 + sec;
        } catch (Exception e) {

        }
        return 0;
    }


    /**
     * 返回带 时 分 的时间字符串
     *
     * @param time
     * @return
     */
    public static String getTimeKey(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sf.format(d);
    }

    public static String getMonthDay(String key) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String monthDay = "";
        try {
            Date date = simpleDateFormat.parse(key);
            monthDay = getMonthDay(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthDay;
    }

    public static long getTargetTime(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date start = calendar.getTime();
        return start.getTime();
    }

    public static String getMonthDay(Date date) {//yyyy-MM
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Date start = calendar.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("MM月dd日", Locale.getDefault());
        return sf.format(start);
    }
}
