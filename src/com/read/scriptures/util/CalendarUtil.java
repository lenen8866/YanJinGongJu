package com.read.scriptures.util;

import android.text.TextUtils;

import com.read.scriptures.bean.CollectAudioBean;
import com.read.scriptures.bean.DateBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarUtil {

    private CalendarUtil() {
    }

    /**
     * finalTime 年月 yyyyMM 用于判断是否是今天
     * <p>
     * 获得当月显示的日期（上月 + 当月 + 下月）
     *
     * @return
     */
    public static ArrayList<DateBean> getMonthDate(long current, List<CollectAudioBean.DataBean.RowsBean> signedDates) {
        Map<String, ArrayList<CollectAudioBean.DataBean.RowsBean>> signedArrays = getSignedDates(signedDates);
        // 获取日期实例
        Calendar calendar = Calendar.getInstance();
        // 将日历设置为指定的时间
        calendar.setTimeInMillis(current);
        // 获取当前年份
        int year = calendar.get(Calendar.YEAR);
        // 这里要注意，月份是从0开始，实际月份+1
        int month = calendar.get(Calendar.MONTH) + 1;
        //计算本月1号是周几
        int week = getFirstWeekOfMonth(year, month - 1);

        String toDay = TimeUtils.getTimeKey(System.currentTimeMillis());
        int lastYear;
        int lastMonth;
        if (month == 1) {
            lastMonth = 12;
            lastYear = year - 1;
        } else {
            lastMonth = month - 1;
            lastYear = year;
        }
        int lastMonthDays = getMonthDays(lastYear, lastMonth);//上个月总天数
        int currentMonthDays = getMonthDays(year, month);//当前月总天数
        ArrayList<DateBean> datas = new ArrayList<>();
        String yearStr = year + "";
        String monthStr = month + "";
        String lastYearStr = lastYear + "";
        String lastMonthStr = lastMonth + "";
        if (lastMonthStr.length() < 2) {
            lastMonthStr = 0 + lastMonthStr;
        }
        if (monthStr.length() < 2) {
            monthStr = 0 + monthStr;
        }
        for (int i = 0; i < week; i++) {//上个月的不用显示
//            String lastMonthDaysStr = (lastMonthDays - week + 1 + i) + "";
//            if (lastMonthDaysStr.length() < 2) {
//                lastMonthDaysStr = 0 + lastMonthDaysStr;
//            }
//            String key = lastYearStr + lastMonthStr + lastMonthDaysStr;
//            ArrayList<CollectAudioBean.DataBean.RowsBean> newsItemsBean = signedArrays.get(key);
            datas.add(initDateBean(lastYearStr, lastMonthStr, String.valueOf(lastMonthDays - week + 1 + i), false, null, 0));
        }
        for (int i = 0; i < currentMonthDays; i++) {
            String day = String.valueOf(i + 1);
            if (day.length() < 2) {
                day = 0 + day;
            }
            String key = yearStr + monthStr + day;
            ArrayList<CollectAudioBean.DataBean.RowsBean> newsItemsBean = signedArrays.get(key);
            datas.add(initDateBean(yearStr, monthStr, day, TextUtils.equals(key, toDay), newsItemsBean, 1));
        }
        return datas;
    }

    private static Map<String, ArrayList<CollectAudioBean.DataBean.RowsBean>> getSignedDates(List<CollectAudioBean.DataBean.RowsBean> signedDates) {
        if (signedDates == null) {
            return new HashMap<>(0);
        }
        Map<String, ArrayList<CollectAudioBean.DataBean.RowsBean>> array = new HashMap<>();
        for (CollectAudioBean.DataBean.RowsBean item : signedDates) {//20020202
            String key = TimeUtils.getTimeKey(item.create_time * 1000);
            ArrayList<CollectAudioBean.DataBean.RowsBean> rowsBeans = array.get(key);
            if (rowsBeans == null) {
                rowsBeans = new ArrayList<>();
            }
            rowsBeans.add(item);
            array.put(key, rowsBeans);
        }
        return array;
    }

    private static DateBean initDateBean(String year, String month, String day, boolean isToday, ArrayList<CollectAudioBean.DataBean.RowsBean> data, int type) {
        DateBean dateBean = new DateBean();
        dateBean.timeStr = year + month + day;
        dateBean.isToday = isToday;
        dateBean.day = day;
        dateBean.data = data;
        dateBean.type = type;
        dateBean.monthDay = month + "月" + day + "日";
        return dateBean;
    }
    /**
     * 计算指定月份的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 计算当月1号是周几
     *
     * @param year
     * @param month
     * @return
     */
    public static int getFirstWeekOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }
}
