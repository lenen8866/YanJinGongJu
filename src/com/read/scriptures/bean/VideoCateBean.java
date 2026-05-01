package com.read.scriptures.bean;

import com.read.scriptures.view.indexablerv.IndexableEntity;

import java.io.Serializable;
import java.util.List;

public class VideoCateBean {

    public int total;
    public int pagess;
    public List<RowsBean> rows;

    public static class RowsBean implements IndexableEntity, Serializable {
        /**
         * id : 27
         * status : 1
         * cate_name : 圣经
         * author :
         * cate_desc :
         * cate_image :
         * cate_content :
         * pid : 0
         * update_time : 1614692297
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
        public int pid;
        public int update_time;
        public int weigh;
        public int isGroup;
        public int collect;
        public String fname;
        private String pinyin;

        @Override
        public String getFieldIndexBy() {
            return cate_name;
        }

        @Override
        public void setFieldIndexBy(String indexField) {
            cate_name = indexField;
        }

        @Override
        public void setFieldPinyinIndexBy(String pinyin) {
            this.pinyin = pinyin;
        }
    }
}
