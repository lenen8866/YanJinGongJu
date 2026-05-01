package com.read.scriptures.manager.alispeech.optimize;

import android.util.Log;

import com.alibaba.idst.nui.NativeNui;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Time: 2020/9/24
 * Author: a123
 * Description:
 */
public class AliOperationUtils {

    private static String TAG = "AliSpeechDemo-";
    private final Thread operationThread;
    private LinkedBlockingQueue<OperationQueue> operationQueue = new LinkedBlockingQueue();
    private OperationQueue tempData;
    AliState aliState = AliState.finish;

    public enum AliState{
        start,
        finish
    }

    public AliOperationUtils(){
        Log.i(TAG,"AliOperationUtils init!");
        operationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (aliState == AliState.finish) {
                        aliState = AliState.start;
                        try {
                            tempData = operationQueue.take();
                            Log.i(TAG,tempData.toString());
                            //操作
                            switch (tempData.getType()){
                                case OperationQueue.TYPE_SPEAK:
                                    Log.i(TAG,"执行播放："+tempData.getMsg());
                                    int result1 = NativeNui.GetInstance().startTts("1", "", tempData.getMsg());
                                    Log.i(TAG,"执行播放结果："+result1);
                                    aliState = AliState.finish;
                                    break;
                                case OperationQueue.TYPE_STOP:
                                    Log.i(TAG,"执行停止-start");
                                    //先暂停
                                    int result = NativeNui.GetInstance().pauseTts();
                                    //再停止
                                    int result2 = NativeNui.GetInstance().cancelTts("");
                                    Log.i(TAG,"执行停止结果："+ result2);
                                    aliState = AliState.finish;
                                    break;
                                case OperationQueue.TYPE_PAUSE:
                                    Log.i(TAG,"执行暂停");
                                    int result3 = NativeNui.GetInstance().pauseTts();
                                    Log.i(TAG,"执行暂停结果："+ result3);
                                    aliState = AliState.finish;
                                    break;
                                case OperationQueue.TYPE_RESUME:
                                    Log.i(TAG,"执行恢复");
                                    int result4 = NativeNui.GetInstance().resumeTts();
                                    Log.i(TAG,"执行恢复结果："+ result4);
                                    aliState = AliState.finish;
                                    break;
                                case OperationQueue.TYPE_DESTORY:
                                    Log.i(TAG,"执行销毁");
                                    int result5 = NativeNui.GetInstance().tts_release();
                                    Log.i(TAG,"执行销毁结果："+ result5);
                                    aliState = AliState.finish;
                                    break;

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            aliState = AliState.finish;
                        }

                    }else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        operationThread.start();
    }

    /**
     * 开始任务
     * @param data
     */
    public void startTask(OperationQueue data){

        if (data.getType() == OperationQueue.TYPE_STOP || data.type == OperationQueue.TYPE_PAUSE){
            //移除队列中所有任务
            operationQueue.clear();
            //立即执行
            aliState = AliState.finish;
        }
        Log.i(TAG,"加入队列-start");
        boolean isSuccess = operationQueue.offer(data);
        Log.i(TAG,"加入队列-end："+isSuccess);
        //非阻塞
    }

}
