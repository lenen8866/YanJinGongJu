package com.read.scriptures.util;

import android.util.SparseArray;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 集合判空工具类
 *
 * @author jWX276003
 * @version [版本号, 2015年7月1日]
 * @since [产品/模块版本]
 */
public abstract class CollectionUtil {

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @return <li>true 为空
     * <li>false 不为空
     */
    public static boolean isEmpty(final Collection<?> collection) {

        return (null == collection) || collection.isEmpty();
    }

    /**
     * 判断MAP是否为空
     *
     * @param map MAP
     * @return <li>true 为空
     * <li>false 不为空
     */
    public static boolean isEmpty(final Map<?, ?> map) {

        return (null == map) || map.isEmpty();
    }

    /**
     * 判断SparseArray是否为空
     *
     * @param sa 集合
     * @return <li>true 为空
     * <li>false 不为空
     */
    public static boolean isEmpty(final SparseArray<?> sa) {

        return (null == sa) || (sa.size() == 0);
    }

    /**
     * 判断数组是否为空
     *
     * @param <T>   类型
     * @param array 数组
     * @return <li>true 为空
     * <li>false 不为空
     */
    public static <T> boolean isEmpty(final T[] array) {

        return (null == array) || (array.length == 0);
    }


    /**
     * @param a String[]
     * @return String[]
     */
    public static String[] getSortOfChinese(String[] a) {
        // Collator 类是用来执行区分语言环境这里使用CHINA
        Comparator<Object> cmp = Collator.getInstance(java.util.Locale.CHINA);
        // JDK自带对数组进行排序。
        Arrays.sort(a, cmp);
        return a;
    }

    public static List<String> getSortOfChinese(List<String> list) {
        // Collator 类是用来执行区分语言环境这里使用CHINA
        Comparator<Object> cmp = Collator.getInstance(java.util.Locale.CHINA);
        // JDK自带对数组进行排序。
        Collections.sort(list, cmp);
        return list;
    }
}
