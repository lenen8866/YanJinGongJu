package com.read.scriptures.bean;

import java.util.List;

public class SearchAudioBean  {

    /**
     * total : 1
     * rows : [{"id":7,"status":1,"cate_name":"创世记 ","author":"新译本-女生","cate_desc":"本卷一共有50章","cate_image":"http://download.sdacn.cn/yjgj/01img/01.jpg","cate_content":"","pid":5,"update_time":1605332322,"weigh":1,"chapterCount":50,"isGroup":0,"collect":0}]
     * pagess : 1
     */

    public int total;
    public int pagess;
    public List<RowsBean> rows;

    public static class RowsBean {
        /**
         * id : 7
         * status : 1
         * cate_name : 创世记
         * author : 新译本-女生
         * cate_desc : 本卷一共有50章
         * cate_image : http://download.sdacn.cn/yjgj/01img/01.jpg
         * cate_content :
         * pid : 5
         * update_time : 1605332322
         * weigh : 1
         * chapterCount : 50
         * isGroup : 0
         * collect : 0
         */

        public int id;
        public int status;
        public String cate_name;
        public String author;
        public String cate_desc;
        public String cate_image;
        public String cate_content;
        public int pid;
        public int update_time;
        public int weigh;
        public int chapterCount;
        public int isGroup;
        public int collect;
    }
}
