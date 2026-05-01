package com.read.scriptures.model;

public class ShareModel {


    /**
     * code : 1
     * msg :
     * time : 1603968029
     * data : {"type":1,"title":"研经工具","subtitle":"这个属于教会软件，能帮助大家学习到更多的知识","status":1,"link":"https://www.sdacn.cn","content":"我特别推荐大家使用这个软件","image":"https://book.sdacn.cn/logo.jpg","tip":1}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * type : 1
         * title : 研经工具
         * subtitle : 这个属于教会软件，能帮助大家学习到更多的知识
         * status : 1
         * link : https://www.sdacn.cn
         * content : 我特别推荐大家使用这个软件
         * image : https://book.sdacn.cn/logo.jpg
         * tip : 1
         */

        public int type;
        public String title;
        public String subtitle;
        public int status;
        public String link;
        public String content;
        public String image;
        public int tip;
    }
}
