package com.read.scriptures.util;

/**
 * @Author Allen.Lv
 * @Description //TODO
 * @Date 16:43 2019/2/27
 * @Desc: Coding Happy!
 **/

public class VideoDuration {

    public VideoDuration(Integer second, Integer minute, Integer hour) {
        this.second = second;
        this.minute = minute;
        this.hour = hour;
    }

    /**
     * 时长秒
     */

    private Integer second;

    /**
     * 时长分
     */
    private Integer minute;

    /**
     * 时长时
     */
    private Integer hour;


    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        if (hour > 0) {
            stringBuffer.append(hour).append(":");
        }
       return stringBuffer.append(minute).append(":").append(second).toString();
    }
}