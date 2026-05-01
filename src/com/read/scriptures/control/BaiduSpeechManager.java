package com.read.scriptures.control;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.listener.UiMessageListener;
import com.read.scriptures.util.AutoCheck;
import com.read.scriptures.util.OfflineResource;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.SystemUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaiduSpeechManager {
    private Context mContext;
    private Handler mainHandler;
    protected MySyntherizer synthesizer;
    private String mContent;
    private String mSpeechContext="";
    private PlayTimeChangeListener playTimeChangeListener;
    private SwitchThread mSwitchThread;
    private long playTime;
    private Handler mHandler = new Handler();
    private boolean isStopThread = true;
    public BaiduSpeechManager(Context context, Handler mainHandler) {
        mContext = context;
        this.mainHandler = mainHandler;
        initialTts();
    }

    public boolean isStopThread() {
        return isStopThread;
    }

    private void initialTts() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);

        Map<String, String> params = getParams();

        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(SystemConfig.BAIDU_APP_ID, SystemConfig.BAIDU_APP_KEY, SystemConfig.BAIDU_SECRET_KEY, SystemConfig.ttsMode, params, listener);
        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
        // 上线时请删除AutoCheck的调用
        AutoCheck.getInstance(HuDongApplication.getInstance()).check(initConfig, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainDebugMessage();
                        toPrint(message); // 可以用下面一行替代，在logcat中查看代码
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }

        });
        synthesizer = new NonBlockSyntherizer(mContext, initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
    }


    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        switch ( PreferenceConfig.getSpeech(mContext)){
            case "xiaoyan":
                params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
                SystemConfig.offlineVoice = OfflineResource.VOICE_FEMALE;
                break;
            case "xiaoyu":
                params.put(SpeechSynthesizer.PARAM_SPEAKER, "3");
                SystemConfig.offlineVoice = OfflineResource.VOICE_MALE;
                break;
            case "nannan":
                params.put(SpeechSynthesizer.PARAM_SPEAKER, "4");
                SystemConfig.offlineVoice = OfflineResource.VOICE_DUYY;
                break;
        }
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        String speed = PreferencesUtils.getString(mContext,"speed_preference", "50");
        params.put(SpeechSynthesizer.PARAM_SPEED, String.valueOf(Integer.valueOf(speed)/10));
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_NETWORK);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件，从 assets 目录中复制到临时目录
        OfflineResource offlineResource = createOfflineResource(SystemConfig.offlineVoice);
        // 修复：createOfflineResource 可能因 assets 文件不存在而返回 null
        // 加 null 检查，避免 NullPointerException 导致崩溃
        if (offlineResource != null) {
            params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
            params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, offlineResource.getModelFilename());
        } else {
            Log.e("BaiduSpeechManager", "离线语音资源加载失败，offlineResource 为 null，跳过离线模型参数设置");
        }
        return params;
    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(mContext, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
            toPrint("【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }

    protected void toPrint(String str) {
        Message msg = Message.obtain();
        msg.obj = str;
        mainHandler.sendMessage(msg);
    }

    private void print(Message msg) {
        String message = (String) msg.obj;
//        if (message != null) {
//            scrollLog(message);
//        }
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    public void speak() {
        // 需要合成的文本text的长度不能超过1024个GBK字节。
        if (TextUtils.isEmpty(mContent)) {
            mContent = "百度语音，面向广大开发者永久免费开放语音合成技术。";
        }
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        int result = synthesizer.speak(mContent);
        checkResult(result, "speak");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
            Log.e("BAIDUSPEECHENGERTEST", "error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }
    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    /**
     * 暂停播放。仅调用speak后生效
     */
    public void pause() {
        int result = synthesizer.pause();
        checkResult(result, "pause");
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    public void resume() {
        int result = synthesizer.resume();
        checkResult(result, "resume");
    }

    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    public void stop() {
        int result = synthesizer.stop();
        checkResult(result, "stop");
    }

    /**
     * 批量播放
     */
    public void batchSpeak(String text) {
        if (synthesizer == null){
            throw new RuntimeException("百度语音还未初始化");
        }

        List<String> content = null;
        content = new ArrayList<String>(Arrays.asList(text.split("\n")));
        for (int i = 0; i < content.size(); i++) {
            String string = content.get(i);
            if (string.trim().equals("\n") || string.trim().equals("\n\r") || StringUtil.isEmpty
                    (string.trim())) {
                content.remove(i);
                i--;
            }
        }
        List<Pair<String, String>> texts = new ArrayList<Pair<String, String>>();
        int position = 0;
        for (String s:content){
            texts.add(new Pair<String, String>(s,"a"+position));
            position++;
        }
//        texts.add(new Pair<String, String>("开始批量播放，", "a0"));
//        texts.add(new Pair<String, String>("123456，", "a1"));
//        texts.add(new Pair<String, String>("欢迎使用百度语音，，，", "a2"));
//        texts.add(new Pair<String, String>("重(chong2)量这个是多音字示例", "a3"));
        Log.e("BAIDUSPEECHENGERTEST", "batchSpeak text: "+ text);
        Log.e("BAIDUSPEECHENGERTEST", "batchSpeak size: "+ texts.size());
        Log.e("BAIDUSPEECHENGERTEST", "batchSpeak texts(0): "+ texts.get(0));

        mSpeechContext = text;
        try {
            int result = synthesizer.batchSpeak(texts);
            checkResult(result, "batchSpeak");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    public void speak(String text) {
        // 需要合成的文本text的长度不能超过1024个GBK字节。
        if (TextUtils.isEmpty(text)) {
            text = "";
        }
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        Log.e("BAIDUSPEECHENGERTEST", "batchSpeak text: "+ text);
        mSpeechContext = text;

        // 设置人声
        if (PreferenceConfig.getSpeech(mContext) != null ) {
            String speaker = PreferenceConfig.getSpeech(mContext);
            setSpeaker(speaker);
        }

        int result = synthesizer.speak(text);
        checkResult(result, "speak");
    }
    public void destory(){
        if (synthesizer != null) {
            synthesizer.release();
        }
    }

    public interface PlayTimeChangeListener {
        public void onChange(long playTime);
        public void onStop();

    }

    public void setPlayTimeChangeListener(PlayTimeChangeListener playTimeChangeListener) {
        this.playTimeChangeListener = playTimeChangeListener;
    }

    /**
     * 设置语速
     *
     * @param speed
     */
    public void setSpeechSpeed(int speed) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SpeechSynthesizer.PARAM_SPEED, String.valueOf(speed/10));
        if (synthesizer != null) {
            synthesizer.setParams(params);
        }
       PreferencesUtils.putString(mContext,"speed_preference", String.valueOf(speed));
    }

    public int getSpeechSpeed() {
        String speed = PreferencesUtils.getString(mContext,"speed_preference", "50");
        return Integer.parseInt(speed);
    }

    /**
     * 设置发言人
     *
     * @param speaker
     */
    public void setSpeaker(String speaker) {
        if (synthesizer != null) {
            switch (speaker){
                case "xiaoyan":
                    synthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER,"0");
                    SystemConfig.offlineVoice = OfflineResource.VOICE_FEMALE;
                    break;
                case "xiaoyu":
                    synthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER,"3");
                    SystemConfig.offlineVoice = OfflineResource.VOICE_MALE;
                    break;
                case "nannan":
                    synthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER,"4");
                    SystemConfig.offlineVoice = OfflineResource.VOICE_DUYY;
                    break;
            }
            if (!SystemUtils.isOnline(mContext)){
                loadModel(SystemConfig.offlineVoice);
            }

        }
    }
    public void resetSpeaking() {
        stop();
//        batchSpeak(mSpeechContext);
        if (!mSpeechContext.trim().equals(SystemConfig.readContent.trim())){
            mSpeechContext = SystemConfig.readContent;
        }
        speak(mSpeechContext);

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

    public void release(){
        if (synthesizer != null) {
            synthesizer.release();
        }
    }

    /**
     * 切换离线发音。注意需要添加额外的判断：引擎在合成时该方法不能调用
     */
    private void loadModel(String mode) {
        SystemConfig.offlineVoice = mode;
        OfflineResource offlineResource = createOfflineResource(SystemConfig.offlineVoice);
        toPrint("切换离线语音：" + offlineResource.getModelFilename());
        int result = synthesizer.loadModel(offlineResource.getModelFilename(), offlineResource.getTextFilename());
        checkResult(result, "loadModel");
    }
}
