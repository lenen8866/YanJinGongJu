package com.read.scriptures.ui.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.broadcast.PlayBroadcastReceiver;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.event.PlayEvent;
import com.read.scriptures.util.MTextUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

/**
 * 通知栏管理模块（从 ChapterReaderActivity 抽取）
 *
 * 职责：
 *   - 创建并显示朗读通知栏（兼容 Android 8.0 以下和以上）
 *   - 处理通知栏的播放/暂停/停止广播事件（PlaySpeech）
 *   - 翻章时更新通知栏显示的书名和章节名
 *   - 取消通知栏
 */
class ReadingNotificationHelper {

    private final WeakReference<ChapterReaderActivity> mRef;

    private Notification notification;
    private NotificationCompat.Builder notificationCompat;
    private NotificationManagerCompat mNotificationManager;
    private RemoteViews contentView;
    private IntentFilter mIntentFilter;
    private PlayBroadcastReceiver playBroadcastReceiver;

    ReadingNotificationHelper(ChapterReaderActivity activity) {
        mRef = new WeakReference<>(activity);
    }

    private ChapterReaderActivity act() {
        return mRef.get();
    }

    // ===================== 初始化通知栏 =====================

    void initNotificationBar() {
        ChapterReaderActivity act = act();
        if (act == null) return;

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("play");
        mIntentFilter.addAction("end");
        playBroadcastReceiver = new PlayBroadcastReceiver();
        act.registerReceiver(playBroadcastReceiver, mIntentFilter);

        contentView = new RemoteViews(act.getPackageName(), R.layout.notification_control);

        String title = act.title;
        if (MTextUtil.isContainChinese(title) && title.contains("E")) {
            title = MTextUtil.changeEletter(title).trim();
        }
        contentView.setTextViewText(R.id.tv_book_name, title);
        if (act.mChapter != null) {
            contentView.setTextViewText(R.id.tv_chapter_name, act.mChapter.getShowName());
        }

        Intent intentClick = new Intent(act, ChapterReaderActivity.class);
        intentClick.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntentPlay = PendingIntent.getActivity(act, 0,
                intentClick, PendingIntent.FLAG_CANCEL_CURRENT);
        contentView.setOnClickPendingIntent(R.id.linear_notify, pIntentPlay);

        Intent intentPlay = new Intent("play");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(act, 1, intentPlay, 0);
        contentView.setOnClickPendingIntent(R.id.tv_stop, pendingIntent);

        Intent intentEnd = new Intent("end");
        PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(act, 1, intentEnd, 0);
        contentView.setOnClickPendingIntent(R.id.tv_end, pendingIntentEnd);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notification = new Notification();
            notification.icon = R.mipmap.ic_launcher;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.contentView = contentView;
            notification.flags = notification.FLAG_NO_CLEAR;
            HuDongApplication.getInstance().notManager.notify(
                    SystemConstants.Notification_ID_BASE, notification);
        } else {
            String channelId = "book_play";
            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel(
                    channelId, "book_reader", NotificationManager.IMPORTANCE_MAX);
            HuDongApplication.getInstance().notManager.createNotificationChannel(channel);

            notificationCompat = new NotificationCompat.Builder(act, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setCustomContentView(contentView)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notificationCompat.setPriority(NotificationManager.IMPORTANCE_HIGH);
            } else {
                notificationCompat.setPriority(NotificationCompat.PRIORITY_HIGH);
            }

            mNotificationManager = NotificationManagerCompat.from(act);
            mNotificationManager.notify(SystemConstants.Notification_ID_BASE,
                    notificationCompat.build());
        }
    }

    // ===================== 通知栏控制 =====================

    void cancelNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            HuDongApplication.getInstance().notManager.cancel(SystemConstants.Notification_ID_BASE);
        } else {
            if (mNotificationManager != null) {
                mNotificationManager.cancel(SystemConstants.Notification_ID_BASE);
            }
        }
    }

    void updateChapterName(String chapterName) {
        if (contentView == null) return;
        ChapterReaderActivity act = act();
        if (act == null) return;
        contentView.setTextViewText(R.id.tv_book_name, act.title);
        contentView.setTextViewText(R.id.tv_chapter_name, chapterName);
        notifyUpdate();
    }

    void notifyUpdate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            HuDongApplication.getInstance().notManager.notify(
                    SystemConstants.Notification_ID_BASE, notification);
        } else {
            if (mNotificationManager != null && notificationCompat != null) {
                mNotificationManager.notify(SystemConstants.Notification_ID_BASE,
                        notificationCompat.build());
            }
        }
    }

    void unregisterReceiver() {
        ChapterReaderActivity act = act();
        if (act == null || playBroadcastReceiver == null) return;
        try {
            act.unregisterReceiver(playBroadcastReceiver);
        } catch (Exception e) {
            android.util.Log.e("NotificationHelper", "unregisterReceiver: " + e.getMessage());
        }
    }

    // ===================== 广播事件处理 =====================

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void PlaySpeech(PlayEvent playEvent) {
        ChapterReaderActivity act = act();
        if (act == null) return;

        if (playEvent.isFinish()) {
            act.mSpeechModel = false;
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF
                    && act.mXunFeiSpeechManager != null) {
                act.mXunFeiSpeechManager.stopSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                    && act.baiduSpeechManager != null) {
                act.baiduSpeechManager.stop();
            }
            cancelNotification();
            if (act.floatView != null) act.floatView.hide();
            act.refreshChapterRemark(true, "");
            act.showToastPkg("已退出朗读模式");
            return;
        }

        if (playEvent.getType() == 1) {
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF
                    && act.mXunFeiSpeechManager != null) {
                act.mXunFeiSpeechManager.pauseSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                    && act.baiduSpeechManager != null) {
                act.baiduSpeechManager.pause();
            }
            SystemConstants.SPEECH_TYPE = 0;
            if (act.mSpeechPopupWindow != null) act.mSpeechPopupWindow.setButton(0);
            if (contentView != null)
                contentView.setImageViewResource(R.id.tv_stop, R.drawable.ic_play);
        } else {
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF
                    && act.mXunFeiSpeechManager != null) {
                act.mXunFeiSpeechManager.resumeSpeaking();
            } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                    && act.baiduSpeechManager != null) {
                act.baiduSpeechManager.resume();
            }
            SystemConstants.SPEECH_TYPE = 1;
            if (act.mSpeechPopupWindow != null) act.mSpeechPopupWindow.setButton(1);
            if (contentView != null)
                contentView.setImageViewResource(R.id.tv_stop, R.drawable.ic_stop);
        }
        notifyUpdate();
    }
}
