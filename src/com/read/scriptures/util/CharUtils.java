package com.read.scriptures.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharUtils {
    public static String removeHead(String name) {
        String result = name.replaceFirst("\\d+-", "");
        return result == null ? name : result;
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

    public static String getShowName(String name) {
        return name.replaceAll("^\\d{1,}-", "").replaceAll("\\(.*\\)", "");
    }
}
