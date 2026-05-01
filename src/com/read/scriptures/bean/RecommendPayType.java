package com.read.scriptures.bean;

/**
 * Time: 2020/8/31
 * Author: a123
 * Description: 系统推荐的支付方式
 */
public class RecommendPayType {

    /**
     * enabled : 1
     * pay_name : alipay
     */

    private int enabled;
    private String pay_name;

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getPay_name() {
        return pay_name;
    }

    public void setPay_name(String pay_name) {
        this.pay_name = pay_name;
    }
}
