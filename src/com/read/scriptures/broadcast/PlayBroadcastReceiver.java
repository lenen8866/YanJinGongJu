package com.read.scriptures.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.event.PlayEvent;

import org.greenrobot.eventbus.EventBus;

public class PlayBroadcastReceiver extends BroadcastReceiver {
    private int engineType = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("play")){
            engineType = intent.getIntExtra("engine",1);
            EventBus.getDefault().post(new PlayEvent(SystemConstants.SPEECH_TYPE,false));
        }else if (intent.getAction().equals("end")){
            EventBus.getDefault().post(new PlayEvent(SystemConstants.SPEECH_TYPE,true));
        }

    }
}