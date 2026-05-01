package com.read.scriptures.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.read.scriptures.app.HuDongApplication;

/**
 * XJ
 * SharedPreferences工具类
 */
public class SharedUtil {

	private static final String SHARED_PATH = "app_share";

	public static SharedPreferences getDefaultSharedPreferences(Context context) {
		return context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
	}


	public static void putInt(String key, int value) {
		putInt(HuDongApplication.getInstance(),key,value);
	}
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	public static int getInt(String key,int defValue) {
		return getInt(HuDongApplication.getInstance(),key,defValue);
	}

	public static int getInt(Context context, String key,int defaultValue) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getInt(key, defaultValue);
	}

	public static int getInt(Context context, String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getInt(key, 0);
	}

	public static void putString(String key, String value) {
		putString(HuDongApplication.getInstance(),key,value);
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putString(key, value);
		edit.commit();
	}

	public static String getString(String key) {
		return getString(HuDongApplication.getInstance(),key);
	}

	public static String getString(Context context, String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getString(key,null);
	}

	public static void putBoolean(String key, boolean value) {
		putBoolean(HuDongApplication.getInstance(),key,value);
	}

	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	public static boolean getBoolean(String key,boolean defValue) {
		return getBoolean(HuDongApplication.getInstance(),key,defValue);
	}

	public static boolean getBoolean(Context context, String key, boolean defValue) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(key,defValue);
	}
	public static void remove(String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(HuDongApplication.getInstance());
		Editor edit = sharedPreferences.edit();
		edit.remove(key);
		edit.commit();
	}
}
