package com.read.scriptures.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.ui.activity.base.BaseActivity;

/**
 * Activity管理类
 */
public class ActManager {


    public static void finishAllActivityAndRestart(Activity context) {
        if (context==null){
            return;
        }
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(HuDongApplication.getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
    }

}
