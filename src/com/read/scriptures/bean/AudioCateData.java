package com.read.scriptures.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class AudioCateData implements Serializable {

    /**
     * total : 2
     * rows : [{"id":1,"status":1,"cate_name":"圣经","author":"","cate_desc":"","cate_image":"","cate_content":"","pid":0,"update_time":1605232475,"weigh":1,"isGroup":1,"collect":0,"fname":"圣经"},{"id":3,"status":1,"cate_name":"教育","author":"","cate_desc":"","cate_image":"","cate_content":"","pid":0,"update_time":1598140249,"weigh":6,"isGroup":1,"collect":0,"fname":"教育"}]
     * pagess : 1
     */

    public int total;
    public int pagess;
    public ArrayList<RowsBean> rows;

    public static class RowsBean implements Serializable {
        /**
         * id : 1
         * status : 1
         * cate_name : 圣经
         * author :
         * cate_desc :
         * cate_image :
         * cate_content :
         * pid : 0
         * update_time : 1605232475
         * weigh : 1
         * isGroup : 1
         * collect : 0
         * fname : 圣经
         */

        public String id;
        public int status;
        public String cate_name;
        public String author;
        public String cate_desc;
        public String cate_image;
        public String cate_content;
        public String chapterCount;
        public int pid;
        public int update_time;
        public int weigh;
        public int isGroup;
        public int collect;
        public String fname;
        public boolean isSelect;//当前选中
    }
}
