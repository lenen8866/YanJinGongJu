package com.read.scriptures.util;

import android.text.TextUtils;

import com.read.scriptures.config.PreferenceConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author zWX243327
 */
public final class StringUtil {
    /**
     * 空字符串
     **/
    public static final String EMPTY = "";

    /**
     * 构造方法私有化
     */
    private StringUtil() {

    }

    public static String replaceAll(String str, String tag, String value) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str.replaceAll(tag, value);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param index 保留位数
     * @return String
     */
    public static String subStringUtil(final String str, final int index) {

        if (index < (str.length() - 1)) {
            return str.substring(0, index) + "...";
        }
        return str;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return 判断结果
     */
    public static boolean isEmpty(final String str) {
        // str.isEmpty() 需要API9
        return (str == null) || (0 == str.length());

    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 字符串
     * @return 判断结果
     */
    public static boolean isNotEmpty(final String str) {

        return !isEmpty(str);

    }

    public static String match(String regex, String input) {
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return matcher.group().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算字符串长度
     *
     * @param str 字符串
     * @return 长度
     */
    public static int length(final String str) {

        if (str == null) {
            return 0;
        } else {
            return str.length();
        }
    }

    /**
     * 字符串截取
     *
     * @param str        字符串
     * @param startIndex 开始位置（包括）
     * @param endIndex   结束位置（不包括）
     * @return 字符串
     */
    public static String substring(final String str, final int startIndex, final int endIndex) {

        if (!StringUtil.isEmpty(str)) {
            if (endIndex > startIndex) {
                if (str.length() >= endIndex) {
                    return str.substring(startIndex, endIndex);
                } else if ((str.length() < endIndex) && (str.length() > startIndex)) {
                    return str.substring(startIndex, str.length());
                }
            }
        }

        return EMPTY;
    }

    /**
     * 字符串拼接
     *
     * @param strs 字符
     * @return 字符串
     */
    public static String concat(final String... strs) {

        final StringBuilder builder = new StringBuilder();
        for (String item : strs) {
            item = StringUtil.isEmpty(item) ? EMPTY : item;
            builder.append(item);
        }
        return builder.toString();
    }

    /**
     * 判断两个可变字符串是否相等
     *
     * @param a 字符串
     * @param b 字符串
     * @return 结果
     */
    public static boolean assertEqual(final String a, final String b) {

        boolean ret = false;
        if (isEmpty(a) && isEmpty(b)) {
            ret = true;
        } else if (!isEmpty(a) && !isEmpty(b) && a.equals(b)) {
            ret = true;
        }
        return ret;
    }

    /**
     * 转为字符串
     *
     * @param object 对象
     * @return 结果
     */
    public static String toString(final Object object) {

        if (null == object) {
            return EMPTY;
        } else {
            return object.toString();
        }
    }

    /**
     * 判断两个可变字符串是否相等不区分大小写
     *
     * @param a 字符串
     * @param b 字符串
     * @return 结果
     */
    public static boolean assertEqualIgnoreCase(final String a, final String b) {

        boolean ret = false;
        if (isEmpty(a) && isEmpty(b)) {
            ret = true;
        } else if (!isEmpty(a) && !isEmpty(b) && a.equalsIgnoreCase(b)) {
            ret = true;
        }
        return ret;
    }

    /**
     * 将byte数组转换为16进制字符串 如：byte[]{0x2B, 0x44, 0xEF,0xD9} -->"2B44EFD9"
     *
     * @param b byte[]
     * @return String
     */
    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase(Locale.ENGLISH);
        }
        return ret;
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
     * 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static String showKey(byte[] key) {
        String temp = new String(key);
        StringBuilder stringBuilder = new StringBuilder();
        int length = temp.length();
        int size = length / 80 + 1;
        int beginIndex = 0;
        for (int i = 0; i < size; i++) {
            int endIndex = beginIndex + 80;
            if (endIndex > length) {
                endIndex = length;
            }
            stringBuilder.append(temp.substring(beginIndex, endIndex)
                    // + "\n"
            );
            beginIndex = endIndex;
        }
        return stringBuilder.toString();
    }

    public static String sizeStringValue(long size) {
        String value = "";
        if (size < 1024) {
            value = size + "B";
        } else if (size < 1024 * 1024) {
            size = size / 1024;
            value = size + "K";
        } else if (size < 1024 * 1024 * 1024) {
            double show = NumberUtil.keepEffectiveNumbers(size / 1024d / 1024d, 2);
            value = show + "M";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            double show = NumberUtil.keepEffectiveNumbers(size / 1024d / 1024d / 1024d, 2);
            value = show + "G";
        } else {
            double show = NumberUtil.keepEffectiveNumbers(size / 1024d / 1024d / 1024d / 1024d, 2);
            value = show + "T";
        }
        return value;
    }

    public static String tagsReplace(String content, String[] tags) {
        List<Map<String, String>> tagMaps = new ArrayList<>();
        for (String tag : tags) {
            String regex = "<" + tag + "[^>]*>([^<]*)</" + tag + ">";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            Map<String, String> tagMap = new HashMap<>();
            int aTagIndex = 0;
            String tagKey = System.currentTimeMillis() + "zxl" + tag;
            while (matcher.find()) {
                String aTag = matcher.group();
                tagMap.put(tagKey + aTagIndex++, aTag);
            }
            Set<String> keyStrings = tagMap.keySet();
            for (String key : keyStrings) {
                content = content.replace(tagMap.get(key), key);
            }
            tagMaps.add(tagMap);
        }

        boolean isShowSuoxian = SharedUtil.getBoolean(PreferenceConfig.Preference_short_paragraphs_visible, true);
        if (isShowSuoxian) {
            content = content.replace("<", "<font color=\"#7b2f2f\" " +
                    "style=\"background-color:#7b2f2f\">&lt;");
            content = content.replace(">", "&gt;</font>");
        } else if (content.indexOf("<") != -1 && content.indexOf(">") != -1) {
            content = content.substring(0, content.lastIndexOf("<"));
        } else if (content.indexOf("{") != -1 && content.indexOf("}") != -1) {
            content = content.substring(0, content.lastIndexOf("{"));
        }

        for (Map<String, String> tagMap : tagMaps) {
            Set<String> keyStrings = tagMap.keySet();
            for (String key : keyStrings) {
                content = content.replace(key, tagMap.get(key));
            }
        }
        return content;
    }

    public static String replaceTags(String content, String[] tags) {
        for (String tag : tags) {
            String regex = "<" + tag + "[^>]*>";
            String regex1 = "</" + tag + ">";
            content = content.replaceAll(regex, "");
            content = content.replaceAll(regex1, "");
        }
        return content;
    }

    public static String getRealSpeekText(String remarkTxt) {
        String moreHead = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+-\\d+", remarkTxt);
        String head = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", remarkTxt);
        if (!TextUtils.isEmpty(moreHead)) {//如果有head
            remarkTxt = remarkTxt.replace(moreHead, "&%L");
            moreHead = moreHead.replace(":", "章:").replace("-", ",") + "节";
            remarkTxt = remarkTxt.replace("&%L", moreHead);
        } else if (!TextUtils.isEmpty(head)) {//如果有head
            remarkTxt = remarkTxt.replace(head, "&%L");
            head = head.replace(":", "章:") + "节";
            remarkTxt = remarkTxt.replace("&%L", head);
        }
        return remarkTxt;
    }

    //截取数字
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "0";
    }
}
