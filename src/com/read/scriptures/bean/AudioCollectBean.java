package com.read.scriptures.bean;

public class AudioCollectBean {

    /**
     * code : 1
     * msg : 收藏成功!
     * time : 1605690872
     * data : {"status":1}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * status : 1
         */

        public int status;
    }
}
