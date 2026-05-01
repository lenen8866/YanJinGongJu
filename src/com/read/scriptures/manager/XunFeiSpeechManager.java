package com.read.scriptures.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.util.PreferencesUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator. Datetime: 2015/7/14. Email: lgmshare@mgail.com
 */
public class XunFeiSpeechManager {
    private Context mContext;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 语音合成对象
    private SpeechSynthesizer mTts;

    private Map<String, String> mPlayerNameKeyParam;
    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue;
    private int mVolume;
    private int mSpeed;
    // 默认发音人
    private String mVoicer = "xiaoyan";
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;

    private static final String APPID = "=5ab200de";

    // 5ab200de 魏建新的id
    // 5814a6f2醉雪乱的讯飞id

    // 55a7bc4e以前的开发人员的ID 需要使用旧的so包和jar包

    private String mSpeechContext = "";
    private SynthesizerListener ttsListener;

    private long playTime;
    private Handler mHandler = new Handler();
    private SwitchThread mSwitchThread;
    private PlayTimeChangeListener playTimeChangeListener;
    private boolean isStopThread = true;

    public XunFeiSpeechManager(Context context) {
        mContext = context;
        SpeechUtility.createUtility(context, SpeechConstant.APPID + APPID);
        initPlayerNameKeyParam();
    }

    public boolean isStopThread() {
        return isStopThread;
    }

    private void initPlayerNameKeyParam() {
        String[][] playerNameKeyParam = new String[2][];
        playerNameKeyParam[0] = mContext.getResources().getStringArray(R.array.player_name_list);
        playerNameKeyParam[1] = mContext.getResources().getStringArray(R.array.player_name_list_param);
        mPlayerNameKeyParam = new HashMap();
        for (int i = 0; i < playerNameKeyParam[0].length; i++) {
            mPlayerNameKeyParam.put(playerNameKeyParam[0][i], playerNameKeyParam[1][i]);
        }

        // 云端发音人名称列表
        mCloudVoicersEntries = mContext.getResources().getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = mContext.getResources().getStringArray(R.array.voicer_cloud_values);
    }

    /**
     * 初始化参数设置
     *
     * @return
     */
    private void setSpeechParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, PreferenceConfig.getSpeech(mContext));
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }
        // 设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, PreferencesUtils.getString(mContext, "speed_preference", "50"));
        // 设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, PreferencesUtils.getString(mContext, "pitch_preference", "50"));
        // 设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, PreferencesUtils.getString(mContext, "volume_preference", "100"));
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, PreferencesUtils.getString(mContext, "stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置合成音频保存路径，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.PARAMS,
                "tts_audio_path=" + Environment.getExternalStorageDirectory() + "/hudong.pcm");
    }

    public void init(InitListener initListener) {
        if (mTts == null) {
            mTts = SpeechSynthesizer.createSynthesizer(mContext, initListener);
        }
        setSpeechParam();
    }

    /**
     * 开始播放
     *
     * @param content 播放内容
     */
    public void startSpeaking(String content) {
        if (mTts == null) {
            throw new RuntimeException("语音还未初始化");
        }
        // String path = Environment.getExternalStorageDirectory() + "/tts.pcm";
        // int code = mTts.synthesizeToUri(content, path, mTtsListener);
        mTts.startSpeaking(content, mTtsListener);
    }

    /**
     * 开始播放
     *
     * @param content     播放内容
     * @param ttsListener 合成回调监听
     */
    public void startSpeaking(String content, SynthesizerListener ttsListener) {
        if (mTts == null) {
            throw new RuntimeException("语音还未初始化");
        }
        mSpeechContext = content;
        this.ttsListener = ttsListener;
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            setSpeechVoicer(PreferenceConfig.getSpeech(mContext));
        }

        mTts.startSpeaking(content, ttsListener);
    }

    public SynthesizerListener getTtsListener() {
        return ttsListener;
    }

    public void setTtsListener(SynthesizerListener ttsListener) {
        this.ttsListener = ttsListener;
    }

    /**
     * 继续播放
     */
    public void resumeSpeaking() {
        if (mTts != null) {
            mTts.resumeSpeaking();
        }
    }

    /**
     * 暂停播放
     */

    public void pauseSpeaking() {
        if (mTts != null) {
            mTts.pauseSpeaking();
        }
    }

    /**
     * 停止播放
     */
    public void stopSpeaking() {
        if (mTts != null) {
            mTts.stopSpeaking();
        }
    }

    public void resetSpeaking() {
        stopSpeaking();
        if (!mSpeechContext.trim().equals(SystemConfig.readContent.trim())) {
            mSpeechContext = SystemConfig.readContent;
        }
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            setSpeechVoicer(PreferenceConfig.getSpeech(mContext));
        }
        mTts.startSpeaking(mSpeechContext, ttsListener);

    }

    /**
     * 销毁
     */
    public void destroy() {
        if (mTts != null) {
            mTts.destroy();
            mTts = null;
        }
    }

    /**
     * 设置语速
     *
     * @param speed
     */
    public void setSpeechSpeed(int speed) {
        if (mTts != null) {
            mTts.setParameter(SpeechConstant.SPEED, String.valueOf(speed));
        }
        PreferencesUtils.putString(mContext, "speed_preference", String.valueOf(speed));
    }

    public int getSpeechSpeed() {
        String speed = PreferencesUtils.getString(mContext, "speed_preference", "50");
        return Integer.parseInt(speed);
    }

    /**
     * 设置音量
     *
     * @param volume
     */
    public void setSpeechVolume(int volume) {
        if (mTts != null) {
            mTts.setParameter(SpeechConstant.VOLUME, String.valueOf(volume));
            PreferencesUtils.putString(mContext, "volume_preference", String.valueOf(volume));
        }
    }

    /**
     * 设置发音人
     *
     * @param voicer
     */
    public void setSpeechVoicer(String voicer) {
        if (mTts != null) {
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        }
        PreferenceConfig.saveSpeech(mContext, voicer);
//        PreferenceUtil.getInstance(mContext).putString("voicer_preference", voicer);
    }

    public void setEngineType(String engineType) {
        this.mEngineType = engineType;
    }

    public String getEngineType() {
        return mEngineType;
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code == ErrorCode.SUCCESS) {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
                setSpeechParam();
            } else {
                XToast.showToast(mContext, "语音初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            XToast.showToast(mContext, "开始播放");
        }

        @Override
        public void onSpeakPaused() {
            XToast.showToast(mContext, "暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            XToast.showToast(mContext, "继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度
            mPercentForBuffering = percent;
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                XToast.showToast(mContext, "播放完成");
            } else if (error != null) {
                XToast.showToast(mContext,  error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

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
        mSwitchThread = new SwitchThread();
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
        public void onChange(long playTime);

        public void onStop();
    }

    public void setPlayTimeChangeListener(PlayTimeChangeListener playTimeChangeListener) {
        this.playTimeChangeListener = playTimeChangeListener;
    }
}
