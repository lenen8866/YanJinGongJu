package com.read.scriptures.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.read.scriptures.bean.NoticeBean;
import com.read.scriptures.ui.activity.NoticeHistoryActivity;

import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class JPushReceiver extends JPushMessageReceiver {
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
//        super.onNotifyMessageOpened(context, notificationMessage);
        Intent intent1 = new Intent(context, NoticeHistoryActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        context.startActivity(intent1);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        String title = notificationMessage.notificationTitle;
        String content = notificationMessage.notificationContent;
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            NoticeBean noticeBean = new NoticeBean();
            noticeBean.title = title;
            noticeBean.content = content;
            noticeBean.time = String.valueOf(System.currentTimeMillis());
            noticeBean.save();
            return;
        }
    }

    @Override
    public void onNotifyMessageUnShow(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageUnShow(context, notificationMessage);
    }
}
