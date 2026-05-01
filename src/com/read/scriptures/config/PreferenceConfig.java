package com.read.scriptures.config;

import android.content.Context;
import android.graphics.Color;

import com.read.scriptures.util.PreferencesUtils;


/**
 * @author lim
 * @ClassName: PreferenceConfig
 * @Description: 应用运行配置参数
 * @mail lgmshare@gmail.com
 * @date 2014年6月9日 下午5:13:33
 */
public class PreferenceConfig {

    public static final String Preference_User_Username = "username";
    public static final String Preference_Speech = "xiaoyan";
    public static final String Preference_Read_Model = "ReadModel";
    public static final String Preference_Text_Size = "TextSize";
    public static final String Preference_Text_Margin = "TextMargin";
    public static final String Preference_Text_Around = "TextAround";
    public static final String Preference_LINE_MARGIN = "LineMargin";
    public static final String Preference_Text_Color = "TextColor";
    public static final String Preference_Text_Model = "TextModel";
    public static final String Preference_Backgourd_Color = "BackgourdColor";
    public static final String Preference_Screen_Orientation = "ScreenOrientation";
    public static final String Preference_Copy_Volumae = "Copy_Volumae";
    public static final String Preference_Copy_Chapter = "Copy_Chapter";
    public static final String Preference_Reading_Progress = "reading_progress";
    /**
     * 首页列表展示方式
     */
    public static final String Preference_home_list_type = "home_list_type";

    /**
     * 首页列表排序方式,true:默认，false:拼音
     */
    public static final String Preference_home_sort_type = "home_sort_type";

    /**
     * 是否展示历史搜索,true:默认，false:拼音
     */
    public static final String Preference_history_search_visible = "history_search_visible";

    /**
     * 是否展示缩进段落,true:默认，false:拼音
     */
    public static final String Preference_short_paragraphs_visible = "short_paragraphs_visible";

    /**
     * 阅读滑动条位置,0:左边，1:右边
     */
    public static final String Preference_read_sroll_setting = "read_sroll_setting";

    /**
     * 楷体显示,true 显示，false 隐藏
     */
    public static final String Preference_font_face_setting = "font_face_setting";

    /**
     * 楷体显示的颜色
     */
    public static final String Preference_text_color_setting = "text_color_setting";

    /**
     * 章节列表展示方式
     */
    public static String Preference_chapter_list_type = "chapter_list_type";

    /**
     * 搜索关键字
     */
    public static final String Preference_Keyword = "keyword";
    /**
     * 是否激活（在线激活）
     */
    public static final String Preference_Activation = "activation";
    /**
     * 是否是离线激活
     * 注意：原来与 Preference_Activation 使用相同的 key "activation"，已修复
     */
    public static final String Preference_Offline_Activation = "offline_activation";
    /**
     * 服务器是否激活
     */
    public static final String Preference_Server_Activation = "server_activation";
    /**
     * 剩余时间
     */
    public static final String Preference_Activation_Time = "activation_time";

    /**
     * 第一次安装时间
     */
    public static final String Preference_INSTALL_Time = "install_time";
    /**
     * 免费开始时间
     */
    public static final String Preference_Activation_Time_Free_Begin = "activation_time_free_begin";

    /**
     * 免费结束时间
     */
    public static final String Preference_Activation_Time_Free_End = "activation_time_free_end";
    /**
     * 免费结束时间
     */
    public static final String Preference_Activation_Time_Free_Days = "activation_time_free_days";
    /**
     * 免费是否开启
     */
    public static final String Preference_Activation_Time_Free_Open = "activation_time_free_open";
    /**
     * 支付金额
     */
    public static final String Preference_Pay_Money = "pay_money";
    /**
     * 是否初次启动
     */
    public static final String Preference_First_Start = "start";

    /**
     * 是否初次启动
     */
    public static final String Preference_Version_Code = "version_code";
    /**
     * 广告更新时间
     */
    public static final String Preference_GG_Time = "gg_time";

    public static final String Preference_Speak_Title = "SP_Speak_Title";

    /**
     * 获取用户上一次登录账户
     */
    public static String getUsername(Context context) {
        return PreferencesUtils.getString(context, Preference_User_Username, "");
    }

    /**
     * 保存用户登录账户
     */
    public static void saveUsername(Context context, String username) {
        PreferencesUtils.putString(context, Preference_User_Username, username);
    }

    public static int getReadModel(Context context) {
        return PreferencesUtils.getInt(context, Preference_Read_Model, SystemConfig.READ_MODEL_NORMAL);
    }

    /**
     * 保存语音
     */
    public static void saveSpeech(Context context, String username) {
        PreferencesUtils.putString(context, Preference_Speech, username);
    }

    public static String getSpeech(Context context) {
        return PreferencesUtils.getString(context, Preference_Speech, "xiaoyu");
    }

    public static void saveReadModel(Context context, int model) {
        PreferencesUtils.putInt(context, Preference_Read_Model, model);
    }

    public static int getTextSize(Context context) {
        return PreferencesUtils.getInt(context, Preference_Text_Size, 21);
    }

    public static void saveTextSize(Context context, int textSize) {
        PreferencesUtils.putInt(context, Preference_Text_Size, textSize);
    }

    public static int getTextMargin(Context context) {
        return PreferencesUtils.getInt(context, Preference_Text_Margin, 7);
    }

    public static void saveTextMargin(Context context, int textSize) {
        PreferencesUtils.putInt(context, Preference_Text_Margin, textSize);
    }

    public static int getTextColor(Context context) {
        return PreferencesUtils.getInt(context, Preference_Text_Color, Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT));
    }

    public static void saveTextColor(Context context, int textSize) {
        PreferencesUtils.putInt(context, Preference_Text_Color, textSize);
    }

    public static int getTextAround(Context context) {
        return PreferencesUtils.getInt(context, Preference_Text_Around, 22);
    }

    public static void saveTextAround(Context context, int textSize) {
        PreferencesUtils.putInt(context, Preference_Text_Around, textSize);
    }

    public static int getLineMargin(Context context) {
        return PreferencesUtils.getInt(context, Preference_LINE_MARGIN, 7);
    }

    public static void saveLineMargin(Context context, int textSize) {
        PreferencesUtils.putInt(context, Preference_LINE_MARGIN, textSize);
    }

    public static int getTextModel(Context context) {
        return PreferencesUtils.getInt(context, Preference_Text_Model, SystemConfig
                .TEXT_MODEL_NORMAL);
    }

    public static void saveTextModel(Context context, int textModel) {
        PreferencesUtils.putInt(context, Preference_Text_Model, textModel);
    }

    public static int getBackgroudColor(Context context) {
        return PreferencesUtils.getInt(context, Preference_Backgourd_Color,
                Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_DEFAULT));
    }

    public static void saveBackgroudColor(Context context, int color) {
        PreferencesUtils.putInt(context, Preference_Backgourd_Color, color);
    }

    public static boolean getScreenOrientation(Context context) {
        return PreferencesUtils.getBoolean(context, Preference_Screen_Orientation,
                true);
    }

    public static void saveScreenOrientation(Context context, boolean orientation) {
        PreferencesUtils.putBoolean(context, Preference_Screen_Orientation, orientation);
    }

    public static boolean getCopyVolumae(Context context) {
        return PreferencesUtils.getBoolean(context, Preference_Copy_Volumae, true);
    }

    public static void saveCopyVolumae(Context context, boolean copy) {
        PreferencesUtils.putBoolean(context, Preference_Copy_Volumae, copy);
    }

    public static boolean getCopyChapter(Context context) {
        return PreferencesUtils.getBoolean(context, Preference_Copy_Chapter, true);
    }

    public static void saveCopyChapter(Context context, boolean copy) {
        PreferencesUtils.putBoolean(context, Preference_Copy_Chapter, copy);
    }

    /**
     * 保存阅读进度
     *
     * @param context
     * @return
     */
    public static void saveReadingProgress(Context context, int volumeId, int progress) {
        PreferencesUtils.putInt(context, Preference_Reading_Progress + "_" + volumeId, progress);
    }

    /**
     * 获取阅读进度
     *
     * @param context
     * @return
     */
    public static int getReadingProgress(Context context, int volumeId) {
        int size = PreferencesUtils.getInt(context, Preference_Reading_Progress + "_" + volumeId, -1);
        return size;
    }

    /**
     * 移除阅读进度
     *
     * @param context
     * @return
     */
    public static void removeReadingProgress(Context context, int volumeId) {
        PreferencesUtils.remove(context, Preference_Reading_Progress + "_" + volumeId);
    }

    /**
     * 获取免费激活接口是否开启
     *
     * @param context
     * @return
     */
    public static boolean getFreeActiveTimeOpen(Context context) {
        return PreferencesUtils.getBoolean(context, Preference_Activation_Time_Free_Open);
    }

    /**
     * 保存是否开启 免费激活
     *
     * @param context
     * @param activation
     */
    public static boolean saveFreeActiveTimeOpen(Context context, boolean activation) {
        return PreferencesUtils.putBoolean(context, Preference_Activation_Time_Free_Open, activation);
    }

    /**
     * 获取免费激活结束时间
     *
     * @param context
     * @return
     */
    public static long getFreeActiveTimeEnd(Context context) {
        return PreferencesUtils.getLong(context, Preference_Activation_Time_Free_End, 0);
    }

    /**
     * 保存免费激活结束时间
     *
     * @param context
     * @param activation
     */
    public static boolean saveFreeActiveTimeEnd(Context context, long activation) {
        return PreferencesUtils.putLong(context, Preference_Activation_Time_Free_End, activation);
    }


    /**
     * 获取已支付的金额
     *
     * @param context
     * @return
     */
    public static float getPayMoney(Context context) {

        try {
            return PreferencesUtils.getFloat(context, Preference_Pay_Money, 0);
        } catch (Exception e) {
            return PreferencesUtils.getInt(context, Preference_Pay_Money, 0);
        }
    }

    /**
     * 保存支付金额
     *
     * @param context
     * @param money   本次支付
     */
    public static void savePayMoney(Context context, float money) {
        PreferencesUtils.putFloat(context, Preference_Pay_Money, getPayMoney(context) +
                money);
    }

    /**
     * 是否是第一次启动
     *
     * @param context
     * @return
     */
    public static boolean getFirst(Context context) {
        // 这里应该设为false
        return PreferencesUtils.getBoolean(context, Preference_First_Start, false);
    }

    public static void saveFirst(Context context, boolean temp) {
        PreferencesUtils.putBoolean(context, Preference_First_Start, temp);
    }

    /**
     * 获取上次记录的版本号
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {
        return PreferencesUtils.getString(context, Preference_Version_Code, "-1");
    }

    public static void saveVersionCode(Context context, String temp) {
        PreferencesUtils.putString(context, Preference_Version_Code, temp);
    }

    /**
     * 是否清理图片缓存
     *
     * @param context
     * @return
     */
    public static long getGGTime(Context context) {
        return PreferencesUtils.getLong(context, Preference_GG_Time, 0);
    }

    public static void saveGGTime(Context context, long activation) {
        PreferencesUtils.putLong(context, Preference_GG_Time, activation);
    }
}