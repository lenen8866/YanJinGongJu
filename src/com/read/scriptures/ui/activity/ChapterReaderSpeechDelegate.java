package com.read.scriptures.ui.activity;

import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.read.scriptures.R;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.control.BaiduSpeechManager;
import com.read.scriptures.listener.MainHandlerConstant;
import com.read.scriptures.manager.XunFeiSpeechManager;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.widget.SpeechPopupWindow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 语音朗读模块（从 ChapterReaderActivity 独立）
 *
 * 通过 WeakReference 持有 Activity，访问其包级字段，不会内存泄漏。
 * 主文件只需调用：
 *   speechDelegate.initSpeechTts();
 *   speechDelegate.initBaiduSpeech();
 *   speechDelegate.startSpeech();
 *   speechDelegate.speakerNext();
 */
class ChapterReaderSpeechDelegate implements MainHandlerConstant {

    // 弱引用 Activity，防止内存泄漏
    private final WeakReference<ChapterReaderActivity> mRef;

    ChapterReaderSpeechDelegate(ChapterReaderActivity activity) {
        mRef = new WeakReference<>(activity);
    }

    private ChapterReaderActivity act() {
        return mRef.get();
    }

    // ===================== 初始化 =====================

    /**
     * 初始化讯飞 TTS，同时创建百度 TTS 备用。
     * 调用时机：用户点击"朗读"按钮，Speech_Model == SPEECH_MODEL_XF。
     */
    void initSpeechTts() {
        ChapterReaderActivity act = act();
        if (act == null) return;

        // 格式初始化（利用 return_default 长按事件复位排版参数）
        if (act.mSettingDelegate != null && act.mSettingDelegate.return_default != null) {
            act.mSettingDelegate.return_default.performLongClick();
        }

        act.mSpeechPosition = 0;
        act.mSpeechIndex = 0;
        act.mSpeechTxtNumIndex = 0;

        ListView listView = act.mChapterReadSlidingAdapter.getCurrentListView();
        act.mSpeechIndex = listView.getFirstVisiblePosition();

        if (act.mXunFeiSpeechManager == null) {
            act.showProgressDialog("加载中。。。。");
            act.baiduSpeechManager = new BaiduSpeechManager(act, act.mainHandler);
            act.mXunFeiSpeechManager = new XunFeiSpeechManager(act);
            act.mXunFeiSpeechManager.setTtsListener(mTtsListener);
            if (act.floatView != null) {
                act.floatView.setmXunFeiSpeechManager(act.mXunFeiSpeechManager);
                act.floatView.setBaiduSpeechManager(act.baiduSpeechManager);
            }
            act.mXunFeiSpeechManager.init(mTtsInitListener);
            // 提前初始化弹窗，避免通知栏控制时空指针
            act.mSpeechPopupWindow = new SpeechPopupWindow(act, act.mXunFeiSpeechManager, act.baiduSpeechManager, act);
            act.mSpeechPopupWindow.setOnClickListener(act);
        } else {
            startSpeech();
        }
    }

    /**
     * 初始化百度 TTS。
     * 调用时机：用户点击"朗读"按钮，Speech_Model == SPEECH_MODEL_BAIDU。
     */
    void initBaiduSpeech() {
        ChapterReaderActivity act = act();
        if (act == null) return;

        act.mSpeechPosition = 0;
        act.mSpeechIndex = 0;
        act.mSpeechTxtNumIndex = 0;

        ListView listView = act.mChapterReadSlidingAdapter.getCurrentListView();
        act.mSpeechIndex = listView.getFirstVisiblePosition();

        View firstView = listView.getChildAt(0);
        if (firstView != null) {
            int top = firstView.getTop();
            if (top <= 0) listView.smoothScrollBy(top, 100);
        }

        if (act.baiduSpeechManager == null) {
            act.showProgressDialog("加载中。。。。");
            act.baiduSpeechManager = new BaiduSpeechManager(act, act.mainHandler);
            act.mXunFeiSpeechManager = new XunFeiSpeechManager(act);
            act.mXunFeiSpeechManager.setTtsListener(mTtsListener);
            if (act.floatView != null) {
                act.floatView.setmXunFeiSpeechManager(act.mXunFeiSpeechManager);
                act.floatView.setBaiduSpeechManager(act.baiduSpeechManager);
            }
            act.mXunFeiSpeechManager.init(mTtsInitListener);
            act.mSpeechPopupWindow = new SpeechPopupWindow(act, act.mXunFeiSpeechManager, act.baiduSpeechManager, act);
            act.mSpeechPopupWindow.setOnClickListener(act);
        } else {
            startSpeech();
        }
    }

    /** 讯飞 TTS 初始化回调 */
    private final InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(final int code) {
            ChapterReaderActivity act = act();
            if (act == null) return;
            if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                act.dismissProgressDialog();
            }
            if (code == ErrorCode.SUCCESS) {
                if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                    startSpeech();
                }
            } else {
                act.showToastPkg("语音初始化失败，错误码：" + code);
            }
        }
    };

    // ===================== 朗读核心 =====================

    /**
     * 开始朗读当前章节，从当前可见行位置开始。
     */
    void startSpeech() {
        ChapterReaderActivity act = act();
        if (act == null) return;
        try {
            act.mSpeechTextMap.clear();
            act.mSpeechTxtNums = 0;

            final List<String> contentNodes = act.mChapterReadSlidingAdapter
                    .getCurrentChapterReadAdapter().getList();
            if (contentNodes == null || contentNodes.isEmpty()) {
                act.showToastPkg("暂无章节内容");
                return;
            }

            // 将每行内容按句号拆分，构建朗读文本 Map（恢复原始拆分逻辑）
            for (int i = 0; i < contentNodes.size(); i++) {
                List<String> splits = new ArrayList<>();
                String[] ssss = com.read.scriptures.util.MTextUtil.takeOutSymbol(contentNodes.get(i)).split("\u3002");
                splits.addAll(Arrays.asList(ssss));
                String[] news = new String[splits.size()];
                int position = 0;
                for (String s : splits) {
                    String head = com.read.scriptures.util.CharUtils.match("[\u4e00-\u9fa5]+\\d+:\\d+", s);
                    if (!TextUtils.isEmpty(head) && position == 0) {
                        char c = head.charAt(0);
                        if (s.indexOf(c) == 0) s = s.replace(head, "");
                    }
                    news[position] = s;
                    position++;
                }
                act.mSpeechTxtNums += news.length;
                act.mSpeechTextMap.put(i, Arrays.asList(news));
            }

            if (act.mSpeechTextMap.isEmpty()) {
                act.showToastPkg("暂无章节内容");
                return;
            }

            String remarkTxt = act.getSpeechContent(act.mSpeechIndex, act.mSpeechPosition);
            if (TextUtils.isEmpty(remarkTxt)) {
                act.showToastPkg("无法获取朗读内容");
                return;
            }

            boolean isSpeakTitle = SharedUtil.getBoolean(PreferenceConfig.Preference_Speak_Title, false);
            if (!isSpeakTitle && (remarkTxt.contains("<b") || remarkTxt.contains("<h"))) {
                skipTitle();
                return;
            }

            remarkTxt = remarkTxt.replace("<b>", "").replace("</b>", "");
            act.refreshChapterRemark(true, remarkTxt);
            remarkTxt = StringUtil.getRealSpeekText(remarkTxt);
            SystemConfig.readContent = remarkTxt;
            remarkTxt = remarkTxt.replaceAll("(?<=\\[)(.*?)(?=])", "");

            speak(remarkTxt);
            act.mSlidingLayout.setIsPagingEnabled(true);

        } catch (Exception e) {
            android.util.Log.e("SpeechDelegate", "startSpeech error: " + e.getMessage());
        }
    }

    /**
     * 翻章后开始朗读下一章内容。
     */
    void startNextSpeech(Chapter chapter) {
        ChapterReaderActivity act = act();
        if (act == null) return;

        act.mSpeechTextMap.clear();
        act.mSpeechTxtNums = 0;

        final List<String> contentNodes = SearchTextUtil.queryChaptreContent(
                act, chapter, act.mChapterReadSlidingAdapter.getTextModel());
        if (contentNodes == null || contentNodes.isEmpty()) {
            act.showToastPkg("暂无章节内容");
            return;
        }
        for (int i = 0; i < contentNodes.size(); i++) {
            List<String> splits = new ArrayList<>();
            String[] ssss = com.read.scriptures.util.MTextUtil.takeOutSymbol(contentNodes.get(i)).split("\u3002");
            splits.addAll(Arrays.asList(ssss));
            String[] news = new String[splits.size()];
            int position = 0;
            for (String s : splits) {
                String head = com.read.scriptures.util.CharUtils.match("[\u4e00-\u9fa5]+\\d+:\\d+", s);
                if (!TextUtils.isEmpty(head) && position == 0) {
                    char c = head.charAt(0);
                    if (s.indexOf(c) == 0) s = s.replace(head, "");
                }
                news[position] = s;
                position++;
            }
            act.mSpeechTxtNums += news.length;
            act.mSpeechTextMap.put(i, Arrays.asList(news));
        }

        if (act.mSpeechTextMap.isEmpty()) {
            act.showToastPkg("暂无章节内容");
            return;
        }

        String remarkTxt = act.getSpeechContent(act.mSpeechIndex, act.mSpeechPosition);
        if (TextUtils.isEmpty(remarkTxt)) return;

        boolean isSpeakTitle = SharedUtil.getBoolean(PreferenceConfig.Preference_Speak_Title, false);
        if (!isSpeakTitle && (remarkTxt.contains("<b") || remarkTxt.contains("<h"))) {
            skipTitle();
            return;
        }

        remarkTxt = remarkTxt.replace("<b>", "").replace("</b>", "");
        act.refreshChapterRemark(true, remarkTxt);
        SystemConfig.readContent = remarkTxt;
        speak(remarkTxt);
        act.mSlidingLayout.setIsPagingEnabled(true);
        act.saveHistoryInfo();
    }

    /** 向 TTS 引擎发送文本并朗读 */
    private void speak(String text) {
        ChapterReaderActivity act = act();
        if (act == null) return;
        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF && act.mXunFeiSpeechManager != null) {
            act.mXunFeiSpeechManager.startSpeaking(text, mTtsListener);
        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU && act.baiduSpeechManager != null) {
            act.baiduSpeechManager.speak(text.replaceAll("行", "行(xing2)"));
        }
    }

    /** 跳过标题（不朗读），直接推进到下一句 */
    private void skipTitle() {
        ChapterReaderActivity act = act();
        if (act == null) return;
        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
            mTtsListener.onCompleted(null);
        } else {
            act.mainHandler.sendMessage(
                    act.mainHandler.obtainMessage(UI_FINISH_TEXT_SELECTION, 0, 0));
        }
    }

    // ===================== 推进到下一句 =====================

    /**
     * 当前句朗读完毕后，推进到下一句或下一章。
     * 由 TTS 的 onCompleted 回调触发。
     */
    void speakerNext() {
        ChapterReaderActivity act = act();
        if (act == null) return;

        if (act.mSpeechTextMap.get(act.mSpeechIndex) == null) {
            act.showToastPkg("播放完成");
            return;
        }
        act.mSpeechTxtNumIndex++;

        // 判断是否读完本章
        boolean chapterEnd = act.mSpeechTextMap.size() - 1 == act.mSpeechIndex
                && act.mSpeechPosition + 1 == act.mSpeechTextMap.get(act.mSpeechIndex).size();
        if (chapterEnd) {
            act.refreshChapterRemark(true, null);
            if (act.isBack) {
                act.UpdateReadBackground();
                act.SlideNextByBackground = true;
            } else {
                act.mSlidingLayout.slideNext();
                if (act.mChapters.size() - 1 <= act.mChapterReadSlidingAdapter.getPageIndex()) {
                    act.showToastPkg("播放完成");
                    act.stopSpeechAndNotification();
                    if (act.floatView != null) act.floatView.hide();
                }
            }
            return;
        }

        // 自动滚动跟随
        autoScroll(act);

        // 推进朗读位置
        if ((act.mSpeechTextMap.get(act.mSpeechIndex).size() - 1) <= act.mSpeechPosition) {
            act.mSpeechIndex++;
            act.mSpeechPosition = 0;
        } else {
            act.mSpeechPosition++;
        }

        String remarkTxt = act.getSpeechContent(act.mSpeechIndex, act.mSpeechPosition);
        if (TextUtils.isEmpty(remarkTxt)) {
            act.showToastPkg("参数读取错误！");
            return;
        }

        // 标题跳过逻辑
        boolean isSpeakTitle = SharedUtil.getBoolean(PreferenceConfig.Preference_Speak_Title, false);
        if (!isSpeakTitle && (remarkTxt.contains("<b") || remarkTxt.contains("<h")
                || remarkTxt.contains("</b>") || remarkTxt.contains("</h>"))) {
            if ((remarkTxt.startsWith("<b>") && remarkTxt.endsWith("</b>"))
                    || (remarkTxt.startsWith("<h") && remarkTxt.endsWith("</h>"))) {
                skipTitle(); return;
            } else if ((remarkTxt.startsWith("<b") && !remarkTxt.contains("</b>"))
                    || (remarkTxt.startsWith("<h") && !remarkTxt.contains("</h>"))) {
                skipTitle(); return;
            } else if ((remarkTxt.contains("<b") && remarkTxt.endsWith("</b>"))
                    || (remarkTxt.contains("<h") && remarkTxt.endsWith("</h>"))) {
                skipTitle(); return;
            } else if (remarkTxt.contains("</b>")) {
                remarkTxt = remarkTxt.substring(remarkTxt.indexOf("</b>") + "</b>".length());
            } else if (remarkTxt.contains("</h>")) {
                remarkTxt = remarkTxt.substring(remarkTxt.indexOf("</h>") + "</h>".length());
            }
        }

        remarkTxt = remarkTxt.replaceAll("<b>", "").replaceAll("</b>", "");
        String result = SearchTextUtil.replaceTag("<.+?>", remarkTxt);
        result = SearchTextUtil.replaceTag("\\(.+?\\)", result);
        result = SearchTextUtil.replaceTag("\\{.+\\}", result);
        result = SearchTextUtil.replaceTag("（.+）", result);
        result = StringUtil.getRealSpeekText(result);

        if (TextUtils.isEmpty(result)) {
            skipTitle();
            return;
        }

        act.refreshChapterRemark(true, remarkTxt);
        result = result.replaceAll("(?<=\\[)(.*?)(?=])", "");
        SystemConfig.readContent = result;
        speak(result);
    }

    /** 自动滚动：朗读推进时让列表跟随滚动 */
    private void autoScroll(ChapterReaderActivity act) {
        ListView lv = act.mChapterReadSlidingAdapter.getCurrentListView();
        boolean isSJ = act.isLuoJiShengJing(act.mChapters) && act.HUAI_ZHU_CHAPTER_HAS_ZW != 1;
        int targetIdx = isSJ ? act.mSpeechIndex + 1 : act.mSpeechIndex;
        if (targetIdx != lv.getLastVisiblePosition()) return;

        int lastIndex = lv.getChildCount() - 1;
        View lastView = lv.getChildAt(lastIndex);
        if (lastView == null) return;
        TextView tvTitle = lastView.findViewById(R.id.tv_title);
        if (tvTitle == null || tvTitle.getLayout() == null) return;

        Layout layout = tvTitle.getLayout();
        int hangNum = layout.getLineCount();
        float hangh = tvTitle.getMeasuredHeight() / (float) hangNum;
        float hash = act.mSlidingLayout.getHeight() - lastView.getTop();

        if (hash < hangh / 2) {
            lv.smoothScrollBy(act.mSlidingLayout.getHeight(), 500);
        } else if (Math.abs(hash - hangh) < hangh / 2) {
            act.mSlidingLayout.postDelayed(
                    () -> lv.smoothScrollBy(act.mSlidingLayout.getHeight(), 500), 2000);
        } else {
            float mHangHeight = lastView.getMeasuredHeight() / (float) hangNum;
            float hangDuoShaoMeiDu = hash / mHangHeight;
            int hangNumLetter = tvTitle.getText().toString().length() / hangNum;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= act.mSpeechPosition; i++) {
                List<String> list = act.mSpeechTextMap.get(act.mSpeechIndex);
                if (list != null && i < list.size() && !TextUtils.isEmpty(list.get(i))) {
                    sb.append(list.get(i));
                }
                if (sb.length() > hangNumLetter * hangDuoShaoMeiDu) {
                    lv.smoothScrollBy(act.mSlidingLayout.getHeight(), 500);
                    break;
                }
            }
        }
    }

    // ===================== TTS 回调 =====================

    /** 讯飞 TTS 事件监听器（同时被 floatView 使用） */
    final SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            ChapterReaderActivity act = act();
            if (act == null) return;
            act.mChapterReadSlidingAdapter.getCurrentListView()
                    .smoothScrollToPosition(act.mSpeechIndex);
        }
        @Override public void onSpeakPaused() {}
        @Override public void onSpeakResumed() {}

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            ChapterReaderActivity act = act();
            if (act != null) act.mPercentForBuffering = percent;
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            ChapterReaderActivity act = act();
            if (act != null) act.mPercentForPlaying = percent;
            if (percent > 99) this.onCompleted(null);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                speakerNext();
            } else {
                ChapterReaderActivity act = act();
                if (act != null) act.showToastPkg(error.getPlainDescription(true));
            }
        }

        @Override public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
}
