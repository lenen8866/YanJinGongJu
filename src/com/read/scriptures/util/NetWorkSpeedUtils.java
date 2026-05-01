package com.read.scriptures.util;

import android.content.Context;
import android.net.TrafficStats;

/**
 * Created by Ricky on 2016/10/13.
 */
public class NetWorkSpeedUtils {
    private final Context context;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public NetWorkSpeedUtils(Context context) {
        this.context = context;
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();
    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024 / 1024);
    }

    public String getNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 10 % (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        return  speed / 2 + "." + speed2 + " MB/S";
    }
}