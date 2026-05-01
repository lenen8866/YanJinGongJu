package com.read.scriptures.bean;

import java.io.Serializable;
import java.util.List;

public class VideoBookBean {

    /**
     * total : 1
     * rows : [{"id":35,"status":1,"cate_name":"薪火相传","author":"","cate_desc":"","cate_image":"","cate_content":"","pid":32,"update_time":1614692753,"weigh":0,"isGroup":0,"collect":0}]
     * pagess : 1
     */

    public int total;
    public int pagess;
    public List<RowsBean> rows;

    public static class RowsBean implements Serializable {
        /**
         * id : 35
         * status : 1
         * cate_name : 薪火相传
         * author :
         * cate_desc :
         * cate_image :
         * cate_content :
         * pid : 32
         * update_time : 1614692753
         * weigh : 0
         * isGroup : 0
         * collect : 0
         */

        public String id;
        public int status;
        public String cate_name;
        public String author;
        public String cate_desc;
        public String cate_image;
        public String cate_content;
        public String pid;
        public int update_time;
        public int weigh;
        public int isGroup;
        public int collect;
        public int video_count;
        public String cate1_id;
        public String cate2_id;
        public String cate3_id;
    }
}
