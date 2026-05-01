package com.read.scriptures.bean;


import com.read.scriptures.view.indexablerv.IndexableEntity;

import java.io.Serializable;
import java.util.ArrayList;

public class NewAudioBean {

    /**
     * total : 5
     * rows : [{"id":1,"name":"圣经"},{"id":2,"name":"怀著"},{"id":3,"name":"教育"},{"id":4,"name":"专题"},{"id":76,"name":"诗歌"}]
     */

    public int total;
    public ArrayList<RowsBean> rows;

    public static class RowsBean implements IndexableEntity, Serializable {
        /**
         * id : 1
         * name : 圣经
         */

        public String id;
        public String name;
        private String pinyin;

        @Override
        public String getFieldIndexBy() {
            return name;
        }

        @Override
        public void setFieldIndexBy(String indexField) {
            name = indexField;
        }

        @Override
        public void setFieldPinyinIndexBy(String pinyin) {
            this.pinyin = pinyin;
        }
    }
}
