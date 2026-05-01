package com.read.scriptures.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharUtils {

    // PERF: 原来每次调用 match() 都执行 Pattern.compile()，正则编译开销极大
    // getView 里每行调用两次，一章几十行 * 频繁 notifyDataSetChanged = 大量重复编译
    // 改为缓存已编译的 Pattern，相同正则只编译一次
    private static final ConcurrentHashMap<String, Pattern> sPatternCache = new ConcurrentHashMap<>();

    public static String removeHead(String name) {
        String result = name.replaceFirst("\\d+-", "");
        return result == null ? name : result;
    }

    public static String match(String regex, String input) {
        try {
            // PERF: 从缓存取 Pattern，没有才编译并放入缓存
            Pattern pattern = sPatternCache.get(regex);
            if (pattern == null) {
                pattern = Pattern.compile(regex);
                sPatternCache.put(regex, pattern);
            }
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return matcher.group().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getShowName(String name) {
        return name.replaceAll("^\\d{1,}-", "").replaceAll("\\(.*\\)", "");
    }
}
