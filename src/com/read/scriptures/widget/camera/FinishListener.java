package com.read.scriptures.widget.camera;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

/**
 * 简单的监听器用于退出在少数情况下的应用程序
 * @author  s00223601
 * @version  [版本号, 2015-10-8]
 * @since  [产品/模块版本]
 */
public final class FinishListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable
{

    private final Activity activityToFinish;

    /**
     * 构造器
     * @param activityToFinish activityToFinish
     * @see [类、类#方法、类#成员]
     */
    public FinishListener(final Activity activityToFinish)
    {
        this.activityToFinish = activityToFinish;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCancel(final DialogInterface dialogInterface)
    {
        run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(final DialogInterface dialogInterface, final int i)
    {
        run();
    }

    /**
     * 发送消息给Activity
     */
    @Override
    public void run()
    {
        final Handler mHandler = ((MipcaActivityCapture)activityToFinish).getmHandler();
        final Message message = mHandler.obtainMessage();
        message.what = ((MipcaActivityCapture)activityToFinish).getScanTimeout();
        message.sendToTarget();

    }

}
