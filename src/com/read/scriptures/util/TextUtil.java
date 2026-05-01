package com.read.scriptures.util;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.TextUtils;

import com.read.scriptures.app.HuDongApplication;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextUtil {

    /**
     * 小括号文字内容变淡颜色 #efefef
     *
     * @param text
     * @return
     */
    public static String parentheseSetHtmlColor(String text) {
        text = text.replace("（", "(").replace("）", ")");
        Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String find = matcher.group();
            text = text.replace("(" + find + ")",
                    "<font color=\"#7b2f2f\" style=\"background-color:#7b2f2f\">" + "(" + find + ")" + "</font>");
        }
        text = text.replace("(", "（").replace(")", "）");
        StringBuffer stringBuffer = new StringBuffer(text);
        // for (int i = 0; i < 30; i++) {
        // stringBuffer.append("&nbsp;");
        // }
        return stringBuffer.toString();
    }

    /**
     * 小括号文字内容变淡颜色 #efefef
     *
     * @param text
     * @return
     */
    public static String parseSetHtmlColor(String text) {
        List<String> allTags = HuDongApplication.baseVersions;
        allTags.addAll(HuDongApplication.HZ_baseVersions);
        String rText = text;
        String color = "";
        for (String tagName : allTags) {
            switch (tagName) {
                case "中文":
                    color = "<font color=\"#000000\" >";
                    break;
                case "英文":
                    color = "<font color=\"#3f51b5\" >";
                    break;
                case "和合本":
                    color = "<font color=\"#000000\" >";
                    break;
                case "吕振中":
                    color = "<font color=\"#3f51b5\" >";
                    break;
                case "思高本":
                    color = "<font color=\"#666666\" >";
                    break;
                case "现代本":
                    color = "<font color=\"#009688\" >";
                    break;
                case "新译本":
                    color = "<font color=\"#9c27b0\" >";
                    break;
                case "当代版":
                    color = "<font color=\"#ef9a9a\" >";
                    break;
                case "KJV":
                    color = "<font color=\"#e53935\" >";
                    break;
                case "NIV":
                    color = "<font color=\"#4caf50\" >";
                    break;
                case "BBE":
                    color = "<font color=\"#673ab7\" >";
                    break;
                case "ASV":
                    color = "<font color=\"#5677FC\" >";
                    break;
            }

            if (!TextUtils.isEmpty(color) && text.contains("〖" + tagName + "〗") && text.contains("〖/" + tagName + "〗")) {
                rText = text.replace("〖" + tagName + "〗", color);
                rText = rText.replace("〖/" + tagName + "〗", "</font>");
            }
        }

        StringBuffer stringBuffer = new StringBuffer(rText);
        return stringBuffer.toString();
    }

    /**
     * 小括号文字内容变淡颜色 #efefef
     *
     * @param text
     * @param mTextColor
     * @return
     */
    @SuppressLint("Range")
    public static int getVersionColor(String text, int mTextColor) {
        if (TextUtils.isEmpty(text)) {
            return Color.BLACK;
        }
        int color = mTextColor;
        if (text.contains("〖中文〗") && text.contains("〖/中文〗")) {
            color = mTextColor;
        } else if (text.contains("〖英文〗") && text.contains("〖/英文〗")) {
            color = Color.parseColor("#3f51b5");
        } else if (text.contains("〖和合本〗") && text.contains("〖/和合本〗")) {
            color = mTextColor;
        } else if (text.contains("〖吕振中") && text.contains("〖/吕振中〗")) {
            color = Color.parseColor("#3f51b5");
        } else if (text.contains("〖思高本〗") && text.contains("〖/思高本〗")) {
            color = Color.parseColor("#666666");
        } else if (text.contains("〖现代本〗") && text.contains("〖/现代本〗")) {
            color = Color.parseColor("#009688");
        } else if (text.contains("〖新译本〗") && text.contains("〖/新译本〗")) {
            color = Color.parseColor("#9c27b0");
        } else if (text.contains("〖当代版〗") && text.contains("〖/当代版〗")) {
            color = Color.parseColor("#ef9a9a");
        } else if (text.contains("〖KJV〗") && text.contains("〖/KJV〗")) {
            color = Color.parseColor("#e53935");
        } else if (text.contains("〖NIV〗") && text.contains("〖/NIV〗")) {
            color = Color.parseColor("#4caf50");
        } else if (text.contains("〖BBE〗") && text.contains("〖/BBE〗")) {
            color = Color.parseColor("#673ab7");
        } else if (text.contains("〖ASV〗") && text.contains("〖/ASV〗")) {
            color = Color.parseColor("#5677FC");
        } else {
            color = mTextColor;
        }
        return color;
    }

    public static String parentheseSetEmpty(String text) {
        text = text.replace("（", "(").replace("）", ")");
        Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String find = matcher.group();
            text = text.replace("(" + find + ")", "");
        }
        text = text.replace("(", "（").replace(")", "）");
        return text;
    }

    public static String punctuationSetEmpty(String text) {
        text = replaceBlank(text);
        text = text.replace(",", "").replace(".", "");
        text = text.replace(";", "").replace(":", "");
        text = text.replace("'", "").replace("\"", "");
        text = text.replace("?", "").replace("!", "");

        text = text.replace("，", "").replace("。", "");
        text = text.replace("：", "").replace("；", "");
        text = text.replace("‘", "").replace("“", "");
        text = text.replace("？", "").replace("！", "");
        return text;
    }

    /**
     * 去除指标符
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
