package com.read.scriptures.listener;

/**
 * Created by fujiayi on 2017/9/13.
 */

public interface MainHandlerConstant {
    static final int PRINT = 0;
    static final int UI_CHANGE_INPUT_TEXT_SELECTION = 1;
    static final int UI_CHANGE_SYNTHES_TEXT_SELECTION = 2;
    static final int UI_FINISH_TEXT_SELECTION = 3;
    static final int UI_START_TEXT_SELECTION = 4;
    static final int UI_ERROR_TEXT_SPEECH = 5;

    static final int INIT_BAIDU_SUCCESS = 99;
    static final int INIT_BAIDU_ERROR = 100;
    static final int INIT_ALI_SUCCESS = 98;

    // 语音朗读相关常量
    static final int SLIDE_NEXT_CHAPTER = 101;  // 自动翻到下一章
    static final int SPEECH_FINISH = 102;       // 朗读完成
}
