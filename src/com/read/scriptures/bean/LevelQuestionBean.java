package com.read.scriptures.bean;

import java.util.List;

public class LevelQuestionBean {

    /**
     * code : 1
     * msg :
     * time : 1606998821
     * data : [{"id":1,"pid":0,"name":"旧约","rule":"11","picurl":"https://book.sdacn.cn/uploads/20201111/66f8af2bcae358a4fecc866daab7d59a.jpg","remark":"一级标题","childs":[{"id":4,"pid":1,"name":"律法书","rule":"","picurl":"","remark":"","count":542},{"id":5,"pid":1,"name":"诗歌，智慧书","rule":"","picurl":"","remark":"","count":403},{"id":2,"pid":1,"name":"历史书","rule":"1","picurl":"https://book.sdacn.cn/uploads/20201106/7d51668fa963bbbd2786b0fbfd103169.png","remark":"","count":481},{"id":6,"pid":1,"name":"先知书","rule":"","picurl":"","remark":"","count":530}]},{"id":3,"pid":0,"name":"新约","rule":"","picurl":"","remark":"一级标题","childs":[{"id":7,"pid":3,"name":"四福音","rule":"","picurl":"","remark":"","count":323},{"id":8,"pid":3,"name":"书信","rule":"","picurl":"","remark":"","count":223},{"id":9,"pid":3,"name":"教会历史","rule":"","picurl":"","remark":"","count":48},{"id":10,"pid":3,"name":"约翰的启示","rule":"","picurl":"","remark":"","count":43}]}]
     */

    public int code;
    public String msg;
    public String time;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * id : 1
         * pid : 0
         * name : 旧约
         * rule : 11
         * picurl : https://book.sdacn.cn/uploads/20201111/66f8af2bcae358a4fecc866daab7d59a.jpg
         * remark : 一级标题
         * childs : [{"id":4,"pid":1,"name":"律法书","rule":"","picurl":"","remark":"","count":542},{"id":5,"pid":1,"name":"诗歌，智慧书","rule":"","picurl":"","remark":"","count":403},{"id":2,"pid":1,"name":"历史书","rule":"1","picurl":"https://book.sdacn.cn/uploads/20201106/7d51668fa963bbbd2786b0fbfd103169.png","remark":"","count":481},{"id":6,"pid":1,"name":"先知书","rule":"","picurl":"","remark":"","count":530}]
         */

        public int id;
        public int pid;
        public String name;
        public String rule;
        public String picurl;
        public String remark;
        public List<ChildsBean> childs;

        public static class ChildsBean {
            /**
             * id : 4
             * pid : 1
             * name : 律法书
             * rule :
             * picurl :
             * remark :
             * count : 542
             */

            public int id;
            public int pid;
            public String name;
            public String rule;
            public String picurl;
            public String remark;
            public String count;
        }
    }
}
