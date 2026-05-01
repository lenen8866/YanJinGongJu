package com.read.scriptures.util.push;


import android.content.Context;

import java.util.ArrayList;

/**
 * 消息推送工具类
 *
 * @author LGM
 */
public class PushUtil {

    /**
     * 第三方平推送平台初始化
     * @param context
     */
    public static void init(Context context) {
        IXinTuiPush.getInstance().init(context);
    }

    /**
     * 设置推送别名
     * @param context
     * @param alais
     */
    public static void setAlais(Context context, String alais) {

    }

    /**
     * 设置推送标签
     * @param context
     * @param tags
     */
    public static void addTag(Context context, ArrayList<String> tags) {
        IXinTuiPush.getInstance().addTags(context, tags);
    }

    /**
     * 删除推送标签
     * @param context
     * @param tags
     */
    public static void deleteTag(Context context, ArrayList<String> tags) {
        IXinTuiPush.getInstance().deleteTags(context, tags);
    }
}
