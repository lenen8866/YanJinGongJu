package com.read.scriptures.util.analytics;

import android.content.Context;

public interface IAnalytics {

	public void onResume(Context context);

	public void onPause(Context context);

	public void onPageStart(String pageName);

	public void onPageEnd(String pageName);

	public void onEvent(Context context, String strTag);
}
