package com.read.scriptures.bean;

import java.util.List;

public class RechargeTipBean {

    /**
     * code : 1
     * msg :
     * time : 1609916291
     * data : [{"id":4,"type":0,"title":"提示充值","num":10,"former":"2.00","price":"1.50","detail":"","remark":""},{"id":5,"type":0,"title":"提示充值","num":50,"former":"10.00","price":"7.50","detail":"","remark":""},{"id":6,"type":0,"title":"提示充值","num":100,"former":"20.00","price":"15.00","detail":"","remark":""},{"id":7,"type":0,"title":"问答设置","num":200,"former":"40.00","price":"20.00","detail":"","remark":""}]
     */

    public int code;
    public String msg;
    public String time;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * id : 4
         * type : 0
         * title : 提示充值
         * num : 10
         * former : 2.00
         * price : 1.50
         * detail :
         * remark :
         */

        public String id;
        public int type;
        public String title;
        public int num;
        public String former;
        public String price;
        public String detail;
        public String remark;
        public boolean selected;//当前选择
    }
}
