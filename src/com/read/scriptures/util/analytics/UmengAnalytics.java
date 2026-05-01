package com.read.scriptures.util.analytics;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * 友盟事件统计方法封装
 *
 * @ClassName: UmengAnalytics.java
 * @date 2014-11-20 下午2:53:32
 */
class UmengAnalytics implements IAnalytics {

    private static UmengAnalytics umengAnalytics;

    public static UmengAnalytics getInstance() {
        if (null == umengAnalytics) {
            umengAnalytics = new UmengAnalytics();
        }
        return umengAnalytics;
    }

    public void onEvent(Context context, String tag) {
        MobclickAgent.onEvent(context, tag);
    }

    @Override
    public void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    @Override
    public void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    @Override
    public void onPageStart(String pageName) {
        MobclickAgent.onPageStart(pageName);
    }

    @Override
    public void onPageEnd(String pageName) {
        MobclickAgent.onPageEnd(pageName);
    }

}
