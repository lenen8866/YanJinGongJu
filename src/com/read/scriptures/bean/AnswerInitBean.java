package com.read.scriptures.bean;

public class AnswerInitBean {

    /**
     * code : 1
     * msg :
     * time : 1607866285
     * data : {"id":4,"frequency":5,"level":1,"prompt":10}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * id : 4
         * frequency : 5
         * level : 1
         * prompt : 10
         */

        public int id;
        public int frequency;
        public int level;
        public int prompt;
    }
}
