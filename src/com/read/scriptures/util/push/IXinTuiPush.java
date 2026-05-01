package com.read.scriptures.util.push;

import android.content.Context;

import java.util.List;

/**
 * 爱心推推送实例
 * User : Lim
 * Email: lgmshare@gmail.com
 * Datetime : 2015/3/30 14:35 To
 * change this template use File | Settings | File Templates.
 */
class IXinTuiPush implements IPush {

    private static final int IXINTUI_KEY = 1492135369;

    private static IXinTuiPush instance;

    private String token;

    private IXinTuiPush() {
    }

    public static IXinTuiPush getInstance() {
        if (instance == null) {
            instance = new IXinTuiPush();
        }
        return instance;
    }

    @Override
    public void init(Context context) {
//        PushSdkApi.register(context, IXINTUI_KEY, "1", HuDongApplication.getInstance().getVersionName());
    }

    @Override
    public void setAlais(Context context, String alais) {

    }

    @Override
    public void addTags(Context context, List<String> tags) {
//        PushSdkApi.addTags(context, tags);
    }

    @Override
    public void deleteTags(Context context, List<String> tags) {
//        PushSdkApi.deleteTags(context, tags);
    }

    @Override
    public void pausePush(Context context) {
//        PushSdkApi.suspendPush(context);
    }

    @Override
    public void resumePush(Context context) {
//        PushSdkApi.resumePush(context);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
