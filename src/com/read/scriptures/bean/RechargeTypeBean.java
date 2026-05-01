package com.read.scriptures.bean;

public class RechargeTypeBean {

    /**
     * code : 1
     * msg :
     * time : 1609926160
     * data : {"enabled":1,"pay_name":"wxpay"}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * enabled : 1
         * pay_name : wxpay
         */

        public int enabled;
        public String pay_name;
    }
}
