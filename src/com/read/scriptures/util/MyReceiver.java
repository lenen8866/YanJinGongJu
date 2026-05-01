package com.read.scriptures.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.read.scriptures.bean.NoticeBean;
import com.read.scriptures.ui.activity.NoticeHistoryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "Idol";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
            switch (intent.getAction()) {
                case JPushInterface.ACTION_REGISTRATION_ID:
                    String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                    Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                    //send the Registration Id to your server...

                    break;
                case JPushInterface.ACTION_MESSAGE_RECEIVED:
                    Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                    break;
                case JPushInterface.ACTION_NOTIFICATION_RECEIVED:
                    Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                    int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                    Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
                    String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                    String content = bundle.getString(JPushInterface.EXTRA_ALERT);
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                        NoticeBean noticeBean = new NoticeBean();
                        noticeBean.title = title;
                        noticeBean.content = content;
                        noticeBean.time = String.valueOf(System.currentTimeMillis());
                        noticeBean.save();
                        return;
                    }
                    break;
                case JPushInterface.ACTION_NOTIFICATION_OPENED:

                    break;
                case JPushInterface.ACTION_RICHPUSH_CALLBACK:
                    Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                    //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
                    Intent intent1 = new Intent(context, NoticeHistoryActivity.class);
                    context.startActivity(intent1);
                    break;
                case JPushInterface.ACTION_CONNECTION_CHANGE:
                    boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                    Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
                    break;
                default:
                    Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
                    break;
            }
        } catch (
                Exception e) {

        }

    }

    // 打印所有的 intent extra 数据

    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            switch (key) {
                case JPushInterface.EXTRA_NOTIFICATION_ID:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
                    break;
                case JPushInterface.EXTRA_CONNECTION_CHANGE:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
                    break;
                case JPushInterface.EXTRA_EXTRA:
                    if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                        Log.i(TAG, "This message has no Extra data");
                        continue;
                    }

                    try {
                        JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                        Iterator<String> it = json.keys();

                        while (it.hasNext()) {
                            String myKey = it.next();
                            sb.append("\nkey:" + key + ", value: [" +
                                    myKey + " - " + json.optString(myKey) + "]");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Get message extra JSON error!");
                    }

                    break;
                default:
                    sb.append("\nkey:" + key + ", value:" + bundle.get(key));
                    break;
            }
        }
        return sb.toString();
    }
}
