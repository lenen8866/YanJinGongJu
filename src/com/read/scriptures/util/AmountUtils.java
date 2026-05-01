package com.read.scriptures.util;

/**
 * Time: 2020/8/31
 * Author: a123
 * Description: 金额处理
 */
public class AmountUtils {
    /**
     * 金额转成Str，如果是整数就返回不带小数点的字符串
     * @param amount
     * @return
     */
    public static String getAmountToStr(double amount){
        if (amount * 100 % 100 == 0){
            return String.valueOf((int)amount);
        }else {
            return amount + "";
        }
    }
}
