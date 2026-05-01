package com.read.scriptures.util.analytics;

import android.content.Context;

import com.read.scriptures.config.SystemConfig;

/**
 * 统计工具类
 * 
 * @ClassName: AnalyticsUtil.java
 * @date 2014-11-20 下午2:18:06
 */
public class AnalyticsUtil {

	/**
	 * Activity生命周期onResume调用
	 * 
	 * @param context
	 */
	public static void onResume(Context context) {
		if (SystemConfig.DEBUG_ANALYTICS){
			UmengAnalytics.getInstance().onResume(context);
		}
	}

	/**
	 * Activity生命周期onPause调用
	 * 
	 * @param context
	 */
	public static void onPause(Context context) {
		if (SystemConfig.DEBUG_ANALYTICS){
			UmengAnalytics.getInstance().onPause(context);
		}
	}

	/**
	 * fragment生命周期onResume统计调用
	 * 
	 * @param pageName
	 */
	public static void onPageStart(String pageName) {
		if (SystemConfig.DEBUG_ANALYTICS){
			UmengAnalytics.getInstance().onPageStart(pageName);
		}
	}

	/**
	 * fragment生命周期onPause统计调用
	 * 
	 * @param pageName
	 */
	public static void onPageEnd(String pageName) {
		if (SystemConfig.DEBUG_ANALYTICS){
			UmengAnalytics.getInstance().onPageEnd(pageName);
		}
	}

	/**
	 * 点击事件调用
	 * 
	 * @param context
	 * @param tag
	 */
	public static void onEvent(Context context, String tag) {
		if (SystemConfig.DEBUG_ANALYTICS){
			UmengAnalytics.getInstance().onEvent(context, tag);
		}
	}
}
