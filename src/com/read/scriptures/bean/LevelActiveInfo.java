package com.read.scriptures.bean;

/**
 * Time: 2020/8/31
 * Author: a123
 * Description: 会员激活信息
 */
public class LevelActiveInfo {

    /**
     * id : 10
     * status : 1
     * payments : 180.00
     * discount : 100.00
     * day : 180
     * vip_level : {"val":1,"text":"普通会员"}
     */

    private int id;
    private int status;
    private double payments;
    private double discount;
    private int day;
    private VipLevelBean vip_level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getPayments() {
        return payments;
    }

    public void setPayments(float payments) {
        this.payments = payments;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public VipLevelBean getVip_level() {
        return vip_level;
    }

    public void setVip_level(VipLevelBean vip_level) {
        this.vip_level = vip_level;
    }

    public String getDescStr(){
        String desc = "";
        if (getDay() % 30 == 0) {
            //能被30 整除，按月显示
            desc = String.format("%d个月", getDay() / 30);
        }else{
            //按天显示
            desc = String.format("%d天", getDay());
        }
        return desc;
    }


    public double getPayPrice(){
        if (getDiscount() < getPayments()){
            return getDiscount();
        }
        return getPayments();
    }
    public static class VipLevelBean {
        /**
         * val : 1
         * text : 普通会员
         */

        private int val;
        private String text;

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
