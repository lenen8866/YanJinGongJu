package com.read.scriptures.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Administrator on 2018/3/29.
 */

public class SharedPreferencesUtils {

    public static void saveBannerId(Context context, int id) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(String.valueOf(id), id);
        editor.apply();
    }

    public static void setIsTodayPlay(Context context, int id) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(String.valueOf(id), TimeUtils.getDateTag());
        editor.apply();
    }

    public static boolean getIsTodayPlay(Context context, int id) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        int time = sp.getInt(String.valueOf(id), 0);
        return time >= TimeUtils.getDateTag();
    }

    public static void setIsTodayShow(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("show_adv", TimeUtils.getDateTag());
        editor.apply();
    }

    public static boolean getIsTodayShow(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        int time = sp.getInt("show_adv", 0);
        return time >= TimeUtils.getDateTag();
    }

    public static int getBannerId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getInt("update_date", -1);
    }

    public static boolean containsKey(Context context, int id) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.contains(String.valueOf(id));
    }

    public static void clearSp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearEveryTimeSp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("every_time", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveAddTime(Context context, String addTime) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("add_time", addTime);
        editor.apply();
    }

    public static String getAddTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("add_time", "");
    }


    public static void saveEndTime(Context context, String endTime) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("end_time", endTime);
        editor.apply();
    }

    public static String getEndTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("end_time", "");
    }

    public static void saveGuideUrl(Context context, String guideUrl) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("guide_url", guideUrl);
        editor.apply();
    }

    public static String getGuideUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("guide_url", "");
    }

    public static void saveShowType(Context context, String showTime) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("show_time", showTime);
        editor.apply();
    }

    public static String getShowType(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("show_time", "");
    }

    public static void saveShowDate(Context context, String showTime) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("show_date", showTime);
        editor.apply();
    }

    public static String getShowDate(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("show_date", "");
    }

    public static void saveShowUpdateVersionDate(Context context, String showTime) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("show_version_update_date", showTime);
        editor.apply();
    }

    public static String getShowUpdateVersionDate(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("show_version_update_date", "");
    }

    public static void savePicCount(Context context, int count) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("pic_count", count);
        editor.apply();
    }


    public static int getPicCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getInt("pic_count", 0);
    }


    public static void saveWelcomeInfo(Context context, String welcomeJson) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("welcomeJson", welcomeJson);
        editor.apply();
    }

    public static String getWelcomeInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("welcomeJson", "");
    }

    public static void saveShowNow(Context context, int showNow) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("show_now", showNow);
        editor.apply();
    }

    public static int getShowNow(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getInt("show_now", -1);
    }

    public static String getBooklibCode(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("book_lib_code", "2019012458007");
    }

    public static void saveBooklibCode(Context context, String bookcode) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("book_lib_code", bookcode);
        editor.commit();
    }

    public static String getLingXiuCode(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("ling_xiu_code", "");
    }

    public static void saveLingXiuCode(Context context, String bookcode) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ling_xiu_code", bookcode);
        editor.apply();
    }

    public static String getBaiKeCode(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("bai_ke_code", "");
    }

    public static void saveBaiKeCode(Context context, String bookcode) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("bai_ke_code", bookcode);
        editor.apply();
    }

    public static String getBooklibName(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("book_lib_name", "");
    }

    public static void saveBooklibName(Context context, String bookName) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("book_lib_name", bookName);
        editor.apply();
    }

    public static String getLingXiuName(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("ling_xiu_name", "");
    }

    public static void saveLingXiuName(Context context, String bookName) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ling_xiu_name", bookName);
        editor.apply();
    }

    public static String getBaiKeName(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("bai_ke_name", "");
    }

    public static void saveBaiKeName(Context context, String bookName) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("bai_ke_name", bookName);
        editor.apply();
    }


    public static String getBannerUrls(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("banner_urls", "");
    }

    public static void saveBannerUrls(Context context, String BannerUrls) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("banner_urls", BannerUrls);
        editor.apply();
    }

    public static void saveBannerLink(Context context, String BannerLinks) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("banner_links", BannerLinks);
        editor.apply();
    }

    public static String getBannerLink(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("banner_links", "");
    }

    public static void saveBannerType(Context context, String BannerLinks) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("banner_types", BannerLinks);
        editor.apply();
    }

    public static String getBannerType(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("banner_types", "");
    }


    public static String getBannerAddDate(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("banner_add_date", "");
    }

    public static void saveBannerAddDate(Context context, String BannerUrls) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("banner_add_date", BannerUrls);
        editor.apply();
    }

    public static String getBannerEndDate(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("banner_end_date", "");
    }

    public static void saveBannerEndDate(Context context, String BannerUrls) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("banner_end_date", BannerUrls);
        editor.apply();
    }

    public static String getUpdateDate(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("update_date", "2018-04-20 17:35:00");
    }

    public static void saveUpdateDate(Context context, String date) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("update_date", date);
        editor.apply();
    }

    /**
     * 是否显示启动页广告
     */
    public static boolean isShowWelAd(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getBoolean("show_wel_ad", true);
    }

    public static void saveShowWelAd(Context context, boolean force) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("show_wel_ad", force);
        editor.apply();
    }

    public static int getTextNumber(Context context) {
        SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return sp.getInt("setting_readeing_text", 0);
    }

    public static void saveTextNumber(Context context, int position) {
        SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("setting_readeing_text", position);
        editor.apply();
    }

    public static int getBackNumber(Context context) {
        SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return sp.getInt("setting_readeing_back", 0);
    }

    public static void saveBackNumber(Context context, int position) {
        SharedPreferences sp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("setting_readeing_back", position);
        editor.apply();
    }


    /**
     * 是否需要强制更新版本名称
     */
    public static String getUpdateVersionName(Context context) {
        SharedPreferences sp = context.getSharedPreferences("update", Context.MODE_PRIVATE);
        return sp.getString("force_update", "4.0");
    }

    public static void saveUpdateVersionName(Context context, String force) {
        SharedPreferences sp = context.getSharedPreferences("update", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("force_update", force);
        editor.apply();
    }

    public static void saveUpdateVersionContent(Context context, String force) {
        SharedPreferences sp = context.getSharedPreferences("update", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("force_update_content", force);
        editor.apply();
    }

    public static String getUpdateVersionContent(Context context) {
        SharedPreferences sp = context.getSharedPreferences("update", Context.MODE_PRIVATE);
        return sp.getString("force_update_content", "对不起，请链接网络更新APP");
    }

    /**
     * 是否需要强制更新
     */
    public static boolean getUpdateForce(Context context) {
        SharedPreferences sp = context.getSharedPreferences("update", Context.MODE_PRIVATE);
        return sp.getBoolean("force_update_boolean", false);
    }

    public static void saveUpdateForce(Context context, boolean force) {
        SharedPreferences sp = context.getSharedPreferences("update", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("force_update_boolean", force);
        editor.apply();
    }

    //1个月
    public static void saveMonthMoeney(Context context, String money) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("month_money", money);
        editor.apply();
    }

    public static String getMonthMoeney(Context context) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        return sp.getString("month_money", "15");
    }

    //一季度
    public static void saveQuarterMoeney(Context context, String money) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("quarter_money", money);
        editor.apply();
    }

    public static String getQuarterMoeney(Context context) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        return sp.getString("quarter_money", "30");
    }

    //半年
    public static void saveHalfYearMoeney(Context context, String money) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("half_year_money", money);
        editor.apply();
    }

    public static String getHalfYearMoeney(Context context) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        return sp.getString("half_year_money", "50");
    }

    //一年
    public static void saveYearMoeney(Context context, String money) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("year_money", money);
        editor.apply();
    }

    public static String getYearMoeney(Context context) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        return sp.getString("year_money", "80");
    }

    //永久
    public static void saveForeverMoeney(Context context, String money) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("forever_money", money);
        editor.apply();
    }

    public static String getForeverMoeney(Context context) {
        SharedPreferences sp = context.getSharedPreferences("money_pay", Context.MODE_PRIVATE);
        return sp.getString("forever_money", "99");
    }

    public static String getShareReadTimeByDay(Context context) {
        SharedPreferences sp = context.getSharedPreferences("every_time", Context.MODE_PRIVATE);
        return sp.getString("share_day_time", "0");
    }

    public static void saveShareReadTimeByDay(Context context, String time) {
        SharedPreferences sp = context.getSharedPreferences("every_time", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("share_day_time", time);
        editor.apply();
    }

    public static void saveMarkSearchKeyword(Context context, String keyword) {
        SharedPreferences sp = context.getSharedPreferences("user_book_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mark_search_keyword", keyword);
        editor.apply();
    }

    public static String getMarkSearchKeyword(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user_book_info", Context.MODE_PRIVATE);
        String keyword = sp.getString("mark_search_keyword", "");
        return keyword;
    }

    public static void saveIsFristOpenApp(Context context, boolean isFirstOpenApp) {
        SharedPreferences sp = context.getSharedPreferences("app_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("is_first_open_app", isFirstOpenApp);
        editor.apply();
    }

    public static boolean isFristOpenApp(Activity athis) {
        SharedPreferences sp = athis.getSharedPreferences("app_info", Context.MODE_PRIVATE);
        boolean isFirstOpenApp = sp.getBoolean("is_first_open_app", true);
        return isFirstOpenApp;
    }

    public static void saveCopyCheckBox(Context context, boolean isCopyCheckBox) {
        SharedPreferences sp = context.getSharedPreferences("copy_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("is_copy_checkbox", isCopyCheckBox);
        editor.apply();
    }

    public static boolean isCopyCheckBox(Activity athis) {
        SharedPreferences sp = athis.getSharedPreferences("copy_info", Context.MODE_PRIVATE);
        boolean isCopyCheckBox = sp.getBoolean("is_copy_checkbox", false);
        return isCopyCheckBox;
    }

    public static void saveVideoListJson(Context context, String cateId, String videoListJson) {
        if(TextUtils.isEmpty(cateId) || TextUtils.isEmpty(videoListJson)){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences("videoList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("videoList_"+cateId, videoListJson);
        editor.apply();
    }


    public static String getVideoListJson(Context context, String cateId) {
        if(TextUtils.isEmpty(cateId) ){
            return null;
        }
        SharedPreferences sp = context.getSharedPreferences("videoList", Context.MODE_PRIVATE);
        String videoListJson = sp.getString("videoList_"+cateId, null);
        return videoListJson;
    }

}
