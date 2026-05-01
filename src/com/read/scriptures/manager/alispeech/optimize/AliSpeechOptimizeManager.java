package com.read.scriptures.manager.alispeech.optimize;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.idst.nui.CommonUtils;
import com.alibaba.idst.nui.Constants;
import com.alibaba.idst.nui.INativeTtsCallback;
import com.alibaba.idst.nui.NativeNui;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.manager.alispeech.util.AudioPlayer;
import com.read.scriptures.manager.alispeech.util.AudioPlayerCallback;
import com.read.scriptures.manager.alispeech.util.Auth;
import com.read.scriptures.net.NetworkUtils;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.ThreadUtil;
import com.read.scriptures.widget.SpeechPopupWindow;

import java.util.LinkedHashMap;

import static com.read.scriptures.listener.MainHandlerConstant.INIT_ALI_SUCCESS;
import static com.read.scriptures.listener.MainHandlerConstant.UI_FINISH_TEXT_SELECTION;
import static com.read.scriptures.listener.MainHandlerConstant.UI_START_TEXT_SELECTION;

/**
 * 重新优化阿里使用LinkedBlockingQueue队列方式执行操作，但还是会阻塞线程
 */
public class AliSpeechOptimizeManager {
    private static final String TAG = "AliSpeechDemo";
    private int TTS_SUCCESS = 0;
    private int TTS_TEXT_ERROR = 140002;//文本非法，如文本为空。
    private Handler mainHandler;
    private Handler mHandler = new Handler();
    private com.read.scriptures.manager.alispeech.optimize.AliSpeechOptimizeManager.SwitchThread mSwitchThread;
    private com.read.scriptures.manager.alispeech.optimize.AliSpeechOptimizeManager.PlayTimeChangeListener playTimeChangeListener;
    private long playTime;
    private boolean isStopThread = true;
    //是否初始化完成
    boolean initialized = false;


    public LinkedHashMap<String, String> mSpeechTypeMap = new LinkedHashMap<>();
    private double mCurrentRate = 1;//速率 0.5～2 默认1
    private String mCurrentSpeechTypeName = "xiaoyan";
    private final Context mContext;
    private int mSpeechSpeed = 50;
    private String mSpeechContext = "";//阅读文本
    private boolean mIsPause;

    private int mAliOperationId = 1;
    private AliOperationUtils mAliOperationUtils;



    private AudioPlayer mAudioTrack;
    private SpeechPopupWindow speechPopupWindow;

    public void setSpeechPopupWindow(SpeechPopupWindow speechPopupWindow) {
        this.speechPopupWindow = speechPopupWindow;
    }

    public AliSpeechOptimizeManager(Context context, final Handler mainHandler) {
        mContext = context;
        this.mainHandler = mainHandler;
        mAliOperationUtils = new AliOperationUtils();

        mSpeechTypeMap.clear();
        mSpeechTypeMap.put("xiaoyan", "Xiaoyun");//女声
        mSpeechTypeMap.put("xiaoyu", "Xiaogang");//男生
        mSpeechTypeMap.put("xiaomei", "Shanshan");//粤语
        mSpeechTypeMap.put("xiaolin", "Qingqing");//台湾
        mSpeechTypeMap.put("xiaorong", "Xiaoyue");//四川
        mSpeechTypeMap.put("xiaoqian", "Cuijie");//东北
        mSpeechTypeMap.put("xiaoqiang", "Xiaoze");//湖南
        mSpeechTypeMap.put("nannan", "Aitong");//女童

        mSpeechTypeMap.put("soft", "Ruoxi");//温柔女声
        mSpeechTypeMap.put("affine", "Aixia");//亲和女声
        mSpeechTypeMap.put("sweet", "Aimei");//甜美女声
        mSpeechTypeMap.put("lolita", "Xiaobei");//萝莉女声
        mSpeechTypeMap.put("natural", "Aiyu");//自然女声
        mSpeechTypeMap.put("serious", "Aiya");//严肃女声
        mSpeechTypeMap.put("zhejiang", "Aina");//浙普
        mSpeechTypeMap.put("manTwo", "Sicheng");//男声二
        mSpeechTypeMap.put("manThree", "Aicheng");//男声三
        mSpeechTypeMap.put("manFour", "Aida");//男声四


        mAudioTrack = new AudioPlayer(new AudioPlayerCallback() {
            @Override
            public void playStart() {
                Log.i(TAG, "start play:" + mSpeechContext);
            }

            @Override
            public void playOver() {
                Log.i(TAG, "play over:" + mSpeechContext);
                if (!mIsPause) {
                    if (speechPopupWindow != null && speechPopupWindow.isShowing()){
                        mIsPause = true;
                        //暂停中
                        return;
                    }
                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainHandler.sendMessage(mainHandler.obtainMessage(UI_FINISH_TEXT_SELECTION, 0, 0));

                        }
                    }, 200);
                }
            }
        });

        int initResult = initialize();
        if (Constants.NuiResultCode.SUCCESS == initResult) {
            initialized = true;
            mainHandler.sendMessage(mainHandler.obtainMessage(INIT_ALI_SUCCESS, 0, 0));
        } else {
            Log.e(TAG, "init failed");
        }


    }

    /**
     * 初始化
     *
     * @return
     */
    public int initialize() {
        if (!NetworkUtils.isNetAvailable(mContext)){
            //网络不可用
            initialized = false;
            return -1;
        }
        if (initialized){
            //已经初始化，不需要再初始化
            return 0;
        }
        String path = CommonUtils.getModelPath(mContext);
        if (CommonUtils.copyAssetsData(mContext)) {
            Log.i(TAG, "copy assets data done");
        } else {
            Log.i(TAG, "copy assets failed");
            return -1;
        }
        final int ret = NativeNui.GetInstance().tts_initialize(new INativeTtsCallback() {
            @Override
            public void onTtsEventCallback(INativeTtsCallback.TtsEvent event, String task_id, final int ret_code) {
//                Log.i(TAG, "tts event:" + event + " task id " + task_id + " ret " + ret_code);
                if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_START) {
                    if (mIsPause){
                        return;
                    }
                    if (speechPopupWindow != null && speechPopupWindow.isShowing()){
                        mIsPause = true;
                        //暂停中
                        return;
                    }
                    mAudioTrack.play();
                    mainHandler.sendMessage(mainHandler.obtainMessage(UI_START_TEXT_SELECTION, 0, 0));
                    Log.i(TAG, "语音合成开始，准备播放:taskId:"+task_id);
                    mAudioTrack.isFinishSend(false);
                } else if (event == INativeTtsCallback.TtsEvent.TTS_EVENT_END) {
                    Log.i(TAG, "语音合成播放结束:taskId:"+task_id);
                    if (mIsPause){
                        return;
                    }
                    if (speechPopupWindow != null && speechPopupWindow.isShowing()){
                        mIsPause = true;
                        //暂停中
                        return;
                    }
                    mAudioTrack.isFinishSend(true);
                    mAudioTrack.finish();
                } else if (event == TtsEvent.TTS_EVENT_PAUSE) {
                    mAudioTrack.pause();
                    Log.i(TAG, "语音合成暂停:taskId:"+task_id);
                } else if (event == TtsEvent.TTS_EVENT_RESUME) {
                    mAudioTrack.play();
                    Log.i(TAG, "语音合成恢复播放:taskId:"+task_id);
                }  else if (event == TtsEvent.TTS_EVENT_CANCEL) {
                    mAudioTrack.isFinishSend(false);
                    mAudioTrack.stop();
                    Log.i(TAG, "语音合成取消了:taskId:"+task_id);

                } else if (event == TtsEvent.TTS_EVENT_ERROR) {
                    Log.i(TAG, "错误："+ event + " ret " + ret_code );
                    if (144300 == ret_code){
                        //下一句
                        mainHandler.sendMessage(mainHandler.obtainMessage(UI_FINISH_TEXT_SELECTION, 0, 0));
                    }
                }
            }

            /**
             * 合成数据回调。
             * @param text 保留参数，仅本地合成使用。
             * @param work_idx 合成文本数据的下标，仅本地合成时使用。
             * @param data 合成的音频数据，写入播放器。
             */
            @Override
            public void onTtsDataCallback(byte[] text, int work_idx, byte[] data) {
                if (text.length > 0) {
                    Log.i(TAG, "word_idx:" + work_idx + ";text:" + text);
                }
                if (data.length > 0 && !mIsPause) {
                    mAudioTrack.setAudioData(data);
//                    Log.i(TAG, "write:" + data.length);
                } else {
                    //
                    Log.i(TAG, "write-empty:0");
                }
            }

            @Override
            public void onTtsVolCallback(int vol) {
                Log.i(TAG, "tts vol " + vol);
            }
        }, genTicket(path), Constants.LogLevel.LOG_LEVEL_VERBOSE, true);

        if (Constants.NuiResultCode.SUCCESS != ret) {
            Log.i(TAG, "create failed---" + ret);
        }
        //音频采样率，默认值：16000
        NativeNui.GetInstance().setparamTts("sample_rate", "16000");
        return ret;
    }

    private String genTicket(String workpath) {
        String str = "";
        try {
            JSONObject object = Auth.getAliYunTicket();
            object.put("workspace", workpath);
            str = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "UserContext:" + str);
        return str;
    }

    public void speak(final String body) {
        mIsPause = false;
        speakContent(body);
    }

    //阅读
    private void speakContent(final String body) {
        if (!initialized) {
            //为初始话
            initialize();
            return;
        }
//        if (!mInitIsNet){
//            //在没有网的情况下初始化的，重新初始化
//            destory();
//            initialize();
//        }
        if (mIsPause){
            return;
        }
        mSpeechContext = body;
        Log.i(TAG, "调用播放:" + mSpeechContext);
        mSpeechContext = body;
        if (TextUtils.isEmpty(mSpeechContext.trim()) || "” ".trim().equals(body.trim())) {
            mSpeechContext = "";
            mainHandler.sendMessage(mainHandler.obtainMessage(UI_FINISH_TEXT_SELECTION, 0, 0));
            return;
        }
        String speaker;
        // 设置人声
        if (PreferenceConfig.getSpeech(mContext) != null && mSpeechTypeMap.containsKey(PreferenceConfig.getSpeech(mContext))) {
            speaker = mSpeechTypeMap.get(PreferenceConfig.getSpeech(mContext));
        } else {
            speaker = mCurrentSpeechTypeName;
        }
        //设置参数

        //设置发音人
        NativeNui.GetInstance().setparamTts("font_name", speaker);
        //设置语速
        NativeNui.GetInstance().setparamTts("speed_level", mCurrentRate + "");
        //设置音量
        NativeNui.GetInstance().setparamTts("volume", "2");
        //开始播放
        mAliOperationUtils.startTask(new OperationQueue(mAliOperationId += 1, OperationQueue.TYPE_SPEAK, mSpeechContext));

    }

    public void setSpeaker(String speaker) {
        this.mCurrentSpeechTypeName = speaker;

    }

    public String getSpeaker() {
        return mCurrentSpeechTypeName;
    }

    /**
     * 设置速度
     *
     * @param speechSpeed（0-100）需换算0.5 - 2
     */
    public void setSpeechSpeed(int speechSpeed) {
        this.mSpeechSpeed = speechSpeed;
        this.mCurrentRate = mSpeechSpeed / 50d;
        PreferencesUtils.putString(mContext,"speed_preference", String.valueOf(speechSpeed));
    }

    public int getSpeechSpeed() {
        String speed = PreferencesUtils.getString(mContext,"speed_preference", "50");
        mSpeechSpeed = Integer.valueOf(speed);
        this.mCurrentRate = mSpeechSpeed / 50d;
        return mSpeechSpeed;
    }

    /**
     * 重新阅读
     */
    public void resetSpeaking() {
        Log.i(TAG, "重新播放该句子");
        if (!mSpeechContext.trim().equals(SystemConfig.readContent.trim())) {
            mSpeechContext = SystemConfig.readContent;
        }
        speakContent(mSpeechContext);
    }

    /**
     * 关闭销毁
     */
    public void destory() {
        mIsPause = true;
        mAudioTrack.stop();
        initialized = false;
        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                int result = NativeNui.GetInstance().tts_release();
                Log.i(TAG, "destory:" + result);
            }
        });
    }

    /**
     * 暂停
     */
    public void stop() {
        mIsPause = true;
        mAliOperationUtils.startTask(new OperationQueue(mAliOperationId += 1, OperationQueue.TYPE_STOP, mSpeechContext));

    }

    public void stopOnOtherThread() {
        mIsPause = true;
        //停止播放
        mAliOperationUtils.startTask(new OperationQueue(mAliOperationId += 1, OperationQueue.TYPE_STOP, mSpeechContext));
    }


    /**
     * 暂停
     */
    public void pause() {
        mIsPause = true;
        mAliOperationUtils.startTask(new OperationQueue(mAliOperationId += 1, OperationQueue.TYPE_PAUSE, mSpeechContext));

    }

    /**
     * 暂停恢复
     */
    public void resume() {
        mIsPause = false;
        mAliOperationUtils.startTask(new OperationQueue(mAliOperationId += 1, OperationQueue.TYPE_RESUME, mSpeechContext));

    }


    public void startAutoFlowTimer(int time) {
        playTime = time * 60;
        if (mSwitchThread != null && !mSwitchThread.isPause) {
            return;
        }
        if (mSwitchThread != null) {
            mSwitchThread.onThreadResume();
            isStopThread = false;
            return;
        }
        mSwitchThread = new com.read.scriptures.manager.alispeech.optimize.AliSpeechOptimizeManager.SwitchThread();
        mSwitchThread.start();
        isStopThread = false;
    }

    public void stopAutoFlowTimer() {
        if (mSwitchThread != null) {
            mSwitchThread.onThreadPause();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        playTimeChangeListener.onStop();
        isStopThread = true;
    }

    public boolean isStopThread() {
        return isStopThread;
    }

    public void newSpeak(final String body) {
        stop();
        speak(body);
    }

    class SwitchThread extends Thread {

        private boolean isClose = false;
        private boolean isPause = false;
        private boolean isGoOn = true;

        /**
         * 暂停线程
         */
        public synchronized void onThreadPause() {
            isPause = true;
            isGoOn = false;
        }

        /**
         * 线程继续运行
         */
        public synchronized void onThreadResume() {
            isPause = false;
            this.notify();
        }

        /**
         * 线程等待,不提供给外部调用
         */
        private void onThreadWait() {
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 关闭线程
         */
        public synchronized void closeThread() {
            try {
                this.notify();
                this.setClose(true);
                this.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isClose() {
            return isClose;
        }

        public void setClose(boolean isClose) {
            this.isClose = isClose;
        }

        @Override
        public void run() {
            while (!isClose && !isInterrupted()) {
                if (!isPause) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    playTime--;
                    if (playTimeChangeListener != null) {
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                playTimeChangeListener.onChange(playTime);
                            }
                        });
                    }
                    if (playTime == 0) {
                        closeThread();
                        playTimeChangeListener = null;
                    }
                } else {
                    onThreadWait();
                }
            }
        }
    }

    public interface PlayTimeChangeListener {
        void onChange(long playTime);

        void onStop();
    }

    public void setPlayTimeChangeListener(AliSpeechOptimizeManager.PlayTimeChangeListener playTimeChangeListener) {
        this.playTimeChangeListener = playTimeChangeListener;
    }


    public boolean hasSpeaker(String speakerName) {
        return mSpeechTypeMap.containsKey(speakerName);
    }

}

