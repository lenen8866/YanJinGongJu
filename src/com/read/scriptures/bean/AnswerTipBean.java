package com.read.scriptures.bean;

public class AnswerTipBean {


    /**
     * code : 1
     * msg :
     * time : 1608378514
     * data : {"tips":"王上神赐给所罗门极大的智慧聪明和广大的心，如同海沙不可测量。","prompt":999941}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * tips : 王上神赐给所罗门极大的智慧聪明和广大的心，如同海沙不可测量。
         * prompt : 999941
         */

        public String tips;
        public int prompt;
    }
}
