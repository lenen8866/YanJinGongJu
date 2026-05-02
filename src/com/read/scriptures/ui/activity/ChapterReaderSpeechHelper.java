package com.read.scriptures.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ListView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.control.BaiduSpeechManager;
import com.read.scriptures.listener.MainHandlerConstant;
import com.read.scriptures.manager.XunFeiSpeechManager;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * 语音朗读模块
 *
 * 职责：
 *   - 讯飞 TTS 初始化、朗读、暂停、继续、停止
 *   - 百度 TTS 初始化与切换
 *   - 朗读进度追踪（按行、按段自动翻页）
 *   - speakerNext 自动翻章
 *
 * 使用方式（在 ChapterReaderActivity 中）：
 *   mSpeechHelper = new ChapterReaderSpeechHelper(this);
 *   mSpeechHelper.initSpeechTts();
 *   mSpeechHelper.startSpeech();
 *
 * 注意：此类持有 Activity 的 WeakReference，不会导致内存泄漏。
 */
public class ChapterReaderSpeechHelper implements MainHandlerConstant {

    // 弱引用持有 Activity，避免内存泄漏
    private final WeakReference<ChapterReaderActivity> mActivityRef;

    // 语音引擎
    private XunFeiSpeechManager mXunFeiSpeechManager;
    private BaiduSpeechManager  mBaiduSpeechManager;

    // 朗读进度状态
    private final HashMap<Integer, List<String>> mSpeechTextMap = new HashMap<>();
    private int mPercentForBuffering  = 0;
    private int mPercentForPlaying    = 0;
    private int mSpeechPosition       = 0;
    private int mSpeechIndex          = 0;
    private int mSpeechTxtNums        = 0;
    private int mSpeechTxtNumIndex    = 0;
    private boolean longClick         = false;

    public ChapterReaderSpeechHelper(ChapterReaderActivity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    private ChapterReaderActivity getActivity() {
        return mActivityRef.get();
    }

    // ===================== 对外接口 =====================

    public XunFeiSpeechManager getXunFeiSpeechManager() { return mXunFeiSpeechManager; }
    public BaiduSpeechManager  getBaiduSpeechManager()  { return mBaiduSpeechManager; }

    public int  getSpeechPosition()                     { return mSpeechPosition; }
    public void setSpeechPosition(int pos)              { mSpeechPosition = pos; }
    public int  getSpeechIndex()                        { return mSpeechIndex; }
    public void setSpeechIndex(int idx)                 { mSpeechIndex = idx; }
    public boolean isLongClick()                        { return longClick; }
    public void setLongClick(boolean v)                 { longClick = v; }

    // ===================== 初始化 =====================

    /**
     * 初始化讯飞 TTS，完成后自动开始朗读。
     * 应在用户点击"朗读"后调用。
     */
    public void initSpeechTts() {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;

        // 重置朗读位置到当前可见行
        mSpeechPosition = 0;
        mSpeechIndex    = 0;
        mSpeechTxtNumIndex = 0;
        final ListView listView = act.getChapterReadSlidingAdapter().getCurrentListView();
        final int firstView = listView.getFirstVisiblePosition();
        mSpeechIndex = firstView;

        if (mXunFeiSpeechManager == null) {
            act.showProgressDialog("加载中...");
        }

        mXunFeiSpeechManager = new XunFeiSpeechManager(act);
        mXunFeiSpeechManager.init(new InitListener() {
            @Override
            public void onInit(final int code) {
                ChapterReaderActivity a = getActivity();
                if (a == null) return;
                a.dismissProgressDialog();
                if (code != ErrorCode.SUCCESS) {
                    LogUtil.error("讯飞TTS初始化失败，错误码：" + code);
                    return;
                }
                startSpeech();
            }
        });
    }

    /**
     * 初始化百度 TTS（模式A，已隐藏，保留供后续恢复使用）
     */
    public void initBaiduSpeech() {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;
        try {
            mBaiduSpeechManager = new BaiduSpeechManager(act, act.mainHandler);
        } catch (Exception e) {
            LogUtil.error("百度TTS初始化失败：" + e.getMessage());
        }
    }

    // ===================== 朗读控制 =====================

    /**
     * 开始朗读当前章节。
     * 从当前可见行位置开始，逐行发送给 TTS 引擎。
     */
    public void startSpeech() {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;

        String remarkTxt = getSpeechContent(mSpeechIndex, mSpeechPosition);
        if (TextUtils.isEmpty(remarkTxt)) {
            speakerNext();
            return;
        }
        remarkTxt = remarkTxt.replace("<b>", "").replace("</b>", "");
        SystemConfig.readContent = StringUtil.getRealSpeekText(remarkTxt);
        act.refreshChapterRemark(true, remarkTxt);

        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF
                && mXunFeiSpeechManager != null) {
            String content = SystemConfig.readContent.contains("行(xing2)")
                    ? SystemConfig.readContent.replaceAll("行\\(xing2\\)", "行")
                    : SystemConfig.readContent;
            mXunFeiSpeechManager.startSpeaking(content, mTtsListener);
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU
                && mBaiduSpeechManager != null) {
            mBaiduSpeechManager.speak(SystemConfig.readContent);
        }
    }

    /**
     * 停止当前朗读。
     */
    public void stopSpeech() {
        if (mXunFeiSpeechManager != null) mXunFeiSpeechManager.stopSpeaking();
        if (mBaiduSpeechManager  != null) mBaiduSpeechManager.stop();
    }

    /**
     * 销毁语音引擎资源，应在 onDestroy 中调用。
     */
    public void destroy() {
        if (mXunFeiSpeechManager != null) {
            mXunFeiSpeechManager.destroy();
            mXunFeiSpeechManager = null;
        }
        if (mBaiduSpeechManager != null) {
            mBaiduSpeechManager.stop();
            mBaiduSpeechManager = null;
        }
    }

    // ===================== 朗读进度 =====================

    /**
     * 朗读完一行后，推进到下一行或下一章。
     */
    public void speakerNext() {
        ChapterReaderActivity act = getActivity();
        if (act == null) return;

        mSpeechPosition++;
        String nextTxt = getSpeechContent(mSpeechIndex, mSpeechPosition);

        if (!TextUtils.isEmpty(nextTxt)) {
            // 还有内容，继续朗读下一行
            startSpeech();
        } else {
            // 当前页内容读完，尝试翻到下一章
            mSpeechPosition = 0;
            int nextIndex = mSpeechIndex + 1;
            if (act.getChapters() != null && nextIndex < act.getChapters().size()) {
                mSpeechIndex = nextIndex;
                // 通知 Activity 翻页
                act.mainHandler.sendEmptyMessage(MainHandlerConstant.SLIDE_NEXT_CHAPTER);
                startSpeech();
            } else {
                // 全部读完
                act.mainHandler.sendEmptyMessage(MainHandlerConstant.SPEECH_FINISH);
            }
        }
    }

    /**
     * 获取指定位置的朗读文本。
     */
    public String getSpeechContent(int index, int position) {
        ChapterReaderActivity act = getActivity();
        if (act == null || act.getChapters() == null) return null;
        if (index >= act.getChapters().size()) return null;

        List<String> list = mSpeechTextMap.get(index);
        if (list == null) {
            list = SearchTextUtil.queryChaptreContent(act, act.getChapters().get(index), act.getChapterReadSlidingAdapter().getTextModel());
            if (list != null) mSpeechTextMap.put(index, list);
        }
        if (list == null || position >= list.size()) return null;
        return list.get(position);
    }

    // ===================== 讯飞回调 =====================

    /**
     * 讯飞 TTS 合成监听器。
     * 朗读完成后自动推进到下一行（speakerNext）。
     */
    private final SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override public void onSpeakBegin() {}
        @Override public void onSpeakPaused() {}
        @Override public void onSpeakResumed() {}

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            mPercentForBuffering = percent;
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            mPercentForPlaying = percent;
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                speakerNext();
            } else {
                LogUtil.error("讯飞朗读错误：" + error.getErrorDescription());
            }
        }

        @Override public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
}
