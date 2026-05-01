package com.read.scriptures.util;

import java.math.BigDecimal;

public class NumberUtil {
    /**
     * 保留小数点后位数
     * 
     * @param value
     * @param effective
     * @return
     */
    public static double keepEffectiveNumbers(double value, int effective) {
        // 四舍五入
        BigDecimal b = new BigDecimal(String.valueOf(value));
        return b.setScale(effective, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 保留小数点后位数
     *
     * @param value
     * @param effective
     * @return
     */
    public static float keepEffectiveNumbers(float value, int effective) {
        // 四舍五入
        BigDecimal b = new BigDecimal(String.valueOf(value));
        return b.setScale(effective, BigDecimal.ROUND_HALF_UP).floatValue();
    }

}
