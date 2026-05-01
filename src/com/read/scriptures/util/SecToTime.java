package com.read.scriptures.util;


import android.text.TextUtils;

/**
 * @Author Allen.Lv
 * @Description //TODO
 * @Date 19:43 2019/2/28
 * @Desc: Coding Happy!
 **/
public class SecToTime {

    public static VideoDuration secToTime(String times) {
        int time = parseInt(times);
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return new VideoDuration(0, 0, 0);
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return new VideoDuration(59, 59, 99);
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return new VideoDuration(Integer.parseInt(unitFormat(second)), Integer.parseInt(unitFormat(minute)), Integer.parseInt(unitFormat(hour)));
    }

    private static int parseInt(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        return Integer.parseInt(time);
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getTimeString(String timeStr) {
        int time = TextUtils.isEmpty(timeStr) ? 0 : Integer.parseInt(timeStr);
        int miao = time % 60;
        int fen = time / 60;
        int hour = 0;
        if (fen >= 60) {
            hour = fen / 60;
            fen = fen % 60;
        }
        String timeString = "";
        String miaoString = "";
        String fenString = "";
        String hourString = "";
        if (miao < 10) {
            miaoString = "0" + miao;
        } else {
            miaoString = miao + "";
        }
        if (fen < 10) {
            fenString = "0" + fen;
        } else {
            fenString = fen + "";
        }
        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = hour + "";
        }
        if (hour != 0) {
            timeString = hourString + ":" + fenString + ":" + miaoString;
        } else {
            timeString = fenString + ":" + miaoString;
        }
        return timeString;
    }
}