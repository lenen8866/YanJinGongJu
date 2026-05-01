package com.read.scriptures.bean;

public class CommitAnswerBean {


    /**
     * code : 1
     * msg :
     * time : 1608401576
     * data : {"fraction ":4,"correct":2,"error":1,"levelMsg":"","unlockLevel":0,"totalFraction":"180","prompt":999936,"prize":0}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * fraction  : 4
         * correct : 2
         * error : 1
         * levelMsg :
         * unlockLevel : 0
         * totalFraction : 180
         * prompt : 999936
         * prize : 0
         */

        public int fraction;
        public int correct;
        public int error;
        public String levelMsg;
        public int unlockLevel;
        public String totalFraction;
        public int prompt;
        public int prize;
    }
}
