package com.read.scriptures.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.read.scriptures.app.HuDongApplication;

public class NotifacationService extends Service {
    private MyBinder binder = new MyBinder();
    @Override
    public void onCreate() {
        super.onCreate();
        binder=new MyBinder();
    }

    public class MyBinder extends Binder {
        public NotifacationService getService() {
            return NotifacationService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        HuDongApplication.getInstance().notManager.cancelAll();
        super.onTaskRemoved(rootIntent);
    }
}
