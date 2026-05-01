package com.read.scriptures.widget.wheelview;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * @author deliang.xie
 * @date 2013-7-3
 * @time 上午9:07:15
 */
public class DateUtil {
    final private static long[] lunarInfo = new long[]{0x04bd8, 0x04ae0,
            0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0,
            0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540,
            0x0d6a0, 0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5,
            0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3,
            0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0,
            0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0,
            0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8,
            0x0e950, 0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570,
            0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5,
            0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0,
            0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50,
            0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0,
            0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7,
            0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50,
            0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954,
            0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260,
            0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0,
            0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0,
            0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20,
            0x0ada0};

    final private static int[] year20 = new int[]{1, 4, 1, 2, 1, 2, 1, 1, 2,
            1, 2, 1};

    final private static int[] year19 = new int[]{0, 3, 0, 1, 0, 1, 0, 0, 1,
            0, 1, 0};

    final private static int[] year2000 = new int[]{0, 3, 1, 2, 1, 2, 1, 1,
            2, 1, 2, 1};

    public final static String[] nStr1 = new String[]{"", "正", "二", "三", "四",
            "五", "六", "七", "八", "九", "十", "十一", "十二"};

    private final static String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊",
            "己", "庚", "辛", "壬", "癸"};

    private final static String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰",
            "巳", "午", "未", "申", "酉", "戌", "亥"};

    private final static String[] Animals = new String[]{"鼠", "牛", "虎", "兔",
            "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};

    private final static String[] solarTerm = new String[]{"小寒", "大寒", "立春",
            "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑",
            "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};

    private final static String[] sFtv = new String[]{"0101*元旦", "0214 情人节",
            "0308 妇女节", "0312 植树节", "0315 消费者权益日", "0401 愚人节", "0501 劳动节",
            "0504 青年节", "0512 护士节", "0601 儿童节", "0701 建党节", "0801 建军节",
            "0808 父亲节", "0909 毛泽东逝世纪念", "0910 教师节", "0928 孔子诞辰", "1001*国庆节",
            "1006 老人节", "1024 联合国日", "1112 孙中山诞辰", "1220 澳门回归", "1225 圣诞节",
            "1226 毛泽东诞辰"};

    private final static String[] lFtv = new String[]{"0101*农历春节",
            "0115 元宵节", "0505 端午节", "0707 七夕情人节", "0815 中秋节", "0909 重阳节",
            "1208 腊八节", "1224 小年", "0100*除夕"};

    /**
     * 传回农历 y年的总天数
     *
     * @param y
     * @return
     */
    final public static int lYearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0)
                sum += 1;
        }
        return (sum + leapDays(y));
    }

    /**
     * 传回农历 y年闰月的天数
     *
     * @param y
     * @return
     */
    final public static int leapDays(int y) {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else
            return 0;
    }

    /**
     * 传回农历 y年闰哪个月 1-12 , 没闰传回 0
     *
     * @param y
     * @return
     */
    final public static int leapMonth(int y) {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }

    /**
     * 传回农历 y年m月的总天数
     *
     * @param y
     * @param m
     * @return
     */
    final public static int monthDays(int y, int m) {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
            return 29;
        else
            return 30;
    }

    /**
     * 传回农历 y年的生肖
     *
     * @param y
     * @return
     */
    final public static String AnimalsYear(int y) {
        return Animals[(y - 4) % 12];
    }

    /**
     * 得到天干地支
     *
     * @return
     */
    public static String[] getYera() {
        String str[] = new String[107];

        int index1 = 1942;
        // int num = year - 1900 + 36;
        for (int i = 0; i < str.length; i++) {
            str[i] = cyclicalm(index1 - 1900 + 36) + "年(" + index1 + ")";
            index1++;
        }
        return str;
    }

    /**
     * 传入 月日的offset 传回干支,0=甲子
     *
     * @param num
     * @return
     */
    final public static String cyclicalm(int num) {
        return (Gan[num % 10] + Zhi[num % 12]);
    }

    /**
     * 传入 offset 传回干支, 0=甲子
     *
     * @param y
     * @return
     */
    final public static String cyclical(int y) {
        int num = y - 1900 + 36;
        return (cyclicalm(num));
    }

    /**
     * 传出农历.year0 .month1 .day2 .yearCyl3 .monCyl4 .dayCyl5 .isLeap6
     *
     * @param y
     * @param m
     * @return
     */
    final public long[] Lunar(int y, int m) {
        long[] nongDate = new long[7];
        int i = 0, temp = 0, leap = 0;
        Date baseDate = new GregorianCalendar(1900 + 1900, 1, 31).getTime();
        Date objDate = new GregorianCalendar(y + 1900, m, 1).getTime();
        long offset = (objDate.getTime() - baseDate.getTime()) / 86400000L;
        if (y < 2000)
            offset += year19[m - 1];
        if (y > 2000)
            offset += year20[m - 1];
        if (y == 2000)
            offset += year2000[m - 1];
        nongDate[5] = offset + 40;
        nongDate[4] = 14;

        for (i = 1900; i < 2050 && offset > 0; i++) {
            temp = lYearDays(i);
            offset -= temp;
            nongDate[4] += 12;
        }
        if (offset < 0) {
            offset += temp;
            i--;
            nongDate[4] -= 12;
        }
        nongDate[0] = i;
        nongDate[3] = i - 1864;
        leap = leapMonth(i); // 闰哪个月
        nongDate[6] = 0;

        for (i = 1; i < 13 && offset > 0; i++) {
            // 闰月
            if (leap > 0 && i == (leap + 1) && nongDate[6] == 0) {
                --i;
                nongDate[6] = 1;
                temp = leapDays((int) nongDate[0]);
            } else {
                temp = monthDays((int) nongDate[0], i);
            }

            // 解除闰月
            if (nongDate[6] == 1 && i == (leap + 1))
                nongDate[6] = 0;
            offset -= temp;
            if (nongDate[6] == 0)
                nongDate[4]++;
        }

        if (offset == 0 && leap > 0 && i == leap + 1) {
            if (nongDate[6] == 1) {
                nongDate[6] = 0;
            } else {
                nongDate[6] = 1;
                --i;
                --nongDate[4];
            }
        }
        if (offset < 0) {
            offset += temp;
            --i;
            --nongDate[4];
        }
        nongDate[1] = i;
        nongDate[2] = offset + 1;
        return nongDate;
    }

    /**
     * 传出y年m月d日对应的农历.year0 .month1 .day2 .yearCyl3 .monCyl4 .dayCyl5 .isLeap6
     *
     * @param y
     * @param m
     * @param d
     * @return
     */
    final public static long[] calElement(int y, int m, int d) {
        long[] nongDate = new long[7];
        int i = 0, temp = 0, leap = 0;
        Date baseDate = new GregorianCalendar(0 + 1900, 0, 31).getTime();
        Date objDate = new GregorianCalendar(y, m - 1, d).getTime();
        long offset = (objDate.getTime() - baseDate.getTime()) / 86400000L;
        nongDate[5] = offset + 40;
        nongDate[4] = 14;

        for (i = 1900; i < 2050 && offset > 0; i++) {
            temp = lYearDays(i);
            offset -= temp;
            nongDate[4] += 12;
        }
        if (offset < 0) {
            offset += temp;
            i--;
            nongDate[4] -= 12;
        }
        nongDate[0] = i;
        nongDate[3] = i - 1864;
        leap = leapMonth(i); // 闰哪个月
        nongDate[6] = 0;

        for (i = 1; i < 13 && offset > 0; i++) {
            // 闰月
            if (leap > 0 && i == (leap + 1) && nongDate[6] == 0) {
                --i;
                nongDate[6] = 1;
                temp = leapDays((int) nongDate[0]);
            } else {
                temp = monthDays((int) nongDate[0], i);
            }

            // 解除闰月
            if (nongDate[6] == 1 && i == (leap + 1))
                nongDate[6] = 0;
            offset -= temp;
            if (nongDate[6] == 0)
                nongDate[4]++;
        }

        if (offset == 0 && leap > 0 && i == leap + 1) {
            if (nongDate[6] == 1) {
                nongDate[6] = 0;
            } else {
                nongDate[6] = 1;
                --i;
                --nongDate[4];
            }
        }
        if (offset < 0) {
            offset += temp;
            --i;
            --nongDate[4];
        }
        nongDate[1] = i;
        nongDate[2] = offset + 1;
        return nongDate;
    }

    public final static String getChinaDate(int day) {
        String a = "";
        if (day == 10)
            return "初十";
        if (day == 20)
            return "二十";
        if (day == 30)
            return "三十";
        int two = (int) ((day) / 10);
        if (two == 0)
            a = "初";
        if (two == 1)
            a = "十";
        if (two == 2)
            a = "廿";
        if (two == 3)
            a = "三";
        int one = (int) (day % 10);
        switch (one) {
            case 1:
                a += "一";
                break;
            case 2:
                a += "二";
                break;
            case 3:
                a += "三";
                break;
            case 4:
                a += "四";
                break;
            case 5:
                a += "五";
                break;
            case 6:
                a += "六";
                break;
            case 7:
                a += "七";
                break;
            case 8:
                a += "八";
                break;
            case 9:
                a += "九";
                break;
        }
        return a;
    }

    public static String getLunarDate(int year, int month, int day) {
        // Calendar today = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        // int year = today.get(Calendar.YEAR);
        // int month = today.get(Calendar.MONTH) + 1;
        // int date = today.get(Calendar.DATE);
        // int year = 2014;
        // int month = 8;
        // int day = 3;
        long[] l = calElement(year, month, day);
        StringBuffer sToday = new StringBuffer();
        try {
            // sToday.append(sdf.format(today.getTime()));
            // sToday.append(" 农历");
            // sToday.append(cyclical(year));
            // sToday.append('(');
            // sToday.append(AnimalsYear(year));
            // sToday.append(")年");

            sToday.append(nStr1[(int) l[1]]);
            sToday.append(",");
            sToday.append(getChinaDate((int) (l[2])));
            sToday.append(",");
            sToday.append(l[1]);
            sToday.append(",");
            sToday.append(l[2]);
            sToday.append(",");
            sToday.append(l[6]);
            return sToday.toString();
        } finally {
            sToday = null;
        }
    }

    public static long[] getLongStr(int year, int month, int day) {
        long[] l = calElement(year, month, day);
        for (int i = 0; i < l.length; i++) {
            System.out.println(l[i]);
        }

        return l;
    }

    public static List<String> runMonth(int m) {
        // List<String> list = new ArrayList<String>();
        List<String> list = null;
        switch (m) {

            case 0:
                String chineseNumber0[] = {"正", "二", "三", "四", "五", "六", "七", "八",
                        "九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber0);
                break;
            case 1:
                String chineseNumber1[] = {"正", "闰一", "二", "三", "四", "五", "六", "七",
                        "八", "九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber1);
                break;
            case 2:
                String chineseNumber2[] = {"正", "二", "闰二", "三", "四", "五", "六", "七",
                        "八", "九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber2);
                break;
            case 3:
                String chineseNumber3[] = {"正", "二", "三", "闰三", "四", "五", "六", "七",
                        "八", "九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber3);
                break;
            case 4:
                String chineseNumber4[] = {"正", "二", "三", "四", "闰四", "五", "六", "七",
                        "八", "九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber4);
                break;
            case 5:
                String chineseNumber5[] = {"正", "二", "三", "四", "五", "闰五", "六", "七",
                        "八", "九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber5);

                break;
            case 6:
                String chineseNumber6[] = {"正", "二", "三", "四", "五", "六", "闰六", "七",
                        "八", "九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber6);
                break;
            case 7:
                String chineseNumber7[] = {"正", "二", "三", "四", "五", "六", "七", "闰七",
                        "八", "九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber7);
                break;
            case 8:
                String chineseNumber8[] = {"正", "二", "三", "四", "五", "六", "七", "八",
                        "闰八", "九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber8);
                break;
            case 9:
                String chineseNumber9[] = {"正", "二", "三", "四", "五", "六", "七", "八",
                        "九", "闰九", "十", "冬", "腊"};
                list = Arrays.asList(chineseNumber9);
                break;
            case 10:
                String chineseNumber10[] = {"正", "二", "三", "四", "五", "六", "七",
                        "八", "九", "十", "闰十", "冬", "腊"};
                list = Arrays.asList(chineseNumber10);
                break;
            case 11:
                String chineseNumber11[] = {"正", "二", "三", "四", "五", "六", "七",
                        "八", "九", "十", "冬", "闰冬", "腊"};
                list = Arrays.asList(chineseNumber11);
                break;
            case 12:
                String chineseNumber12[] = {"正", "二", "三", "四", "五", "六", "七",
                        "八", "九", "十", "冬", "腊", "闰腊"};
                list = Arrays.asList(chineseNumber12);
                break;
        }
        return list;

    }

    public static int getNum(String n) {
        int i = 0;
        if (n.equals("正")) {
            i = 1;
        } else if (n.equals("二")) {
            i = 2;
        } else if (n.equals("三")) {
            i = 3;
        } else if (n.equals("四")) {
            i = 4;
        } else if (n.equals("五")) {
            i = 5;
        } else if (n.equals("六")) {
            i = 6;
        } else if (n.equals("七")) {
            i = 7;
        } else if (n.equals("八")) {
            i = 8;
        } else if (n.equals("九")) {
            i = 9;
        } else if (n.equals("十")) {
            i = 10;
        } else if (n.equals("冬")) {
            i = 11;
        } else if (n.equals("腊")) {
            i = 12;
        } else if (n.equals("闰")) {
            i = 0;
        }
        return i;
    }

    public static Date strToDateShort(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param sdate
     * @return int 1=星期日 7=星期六，其他类推
     */
    public static int getWeekInt(String sdate) {
        // 再转换为时间
        Date date = strToDateShort(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 根据一个日期，返回是星期几
     *
     * @param sdate
     * @return
     */
    public static String getWeekStr(String sdate) {
        String str = "";
        int week = getWeekInt(sdate);
        if (1 == week) {
            str = "星期日";
        } else if (2 == week) {
            str = "星期一";
        } else if (3 == week) {
            str = "星期二";
        } else if (4 == week) {
            str = "星期三";
        } else if (5 == week) {
            str = "星期四";
        } else if (6 == week) {
            str = "星期五";
        } else if (7 == week) {
            str = "星期六";
        }
        return str;
    }

}