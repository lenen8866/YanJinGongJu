package com.read.scriptures.bean;

import java.util.List;
import java.util.Objects;

public class NewSearchAudioBean1 {

    /**
     * code : 1
     * msg :
     * time : 1607681946
     * data : [{"id":7,"pid":5,"status":1,"cate_name":"创世记 ","author":"","cate_desc":"本卷一共有50章","cate_image":"http://download.sdacn.cn/yjgj/01img/01.jpg","cate_content":"","update_time":1606916715,"weigh":1,"sortAdd":"圣经-旧约","num":50,"version":12,"item":[{"author":"新译本版女生版","cate_id":7,"num":50},{"author":"和合本圣经朗读","cate_id":7,"num":50},{"author":"KJV","cate_id":7,"num":50},{"author":"汉语协会","cate_id":7,"num":50},{"author":"台湾(闽南语)","cate_id":7,"num":50},{"author":"Japan","cate_id":7,"num":50},{"author":"NRSV","cate_id":7,"num":50},{"author":"雷州话","cate_id":7,"num":50},{"author":"德语","cate_id":7,"num":50},{"author":"Spanish","cate_id":7,"num":50},{"author":"法语","cate_id":7,"num":50},{"author":"韩语","cate_id":7,"num":50}]},{"id":135,"pid":79,"status":1,"cate_name":"cd06全能创造主","author":"","cate_desc":"","cate_image":"","cate_content":"","update_time":1594173623,"weigh":6,"sortAdd":"诗歌-CD主内音乐","num":0,"version":0,"item":[]},{"id":392,"pid":307,"status":1,"cate_name":"06-全能的创造主","author":"","cate_desc":"","cate_image":"https://book.sdacn.cn/uploads/20200814/a0f4b4d63a85d64f83e922387f40ae48.jpg","cate_content":"","update_time":1606491895,"weigh":60,"sortAdd":"诗歌-赞美之泉","num":17,"version":1,"item":[{"author":"赞美之泉事工","cate_id":392,"num":17}]},{"id":409,"pid":307,"status":1,"cate_name":"12-深夜的牵引（个人创作1）","author":"","cate_desc":"","cate_image":"https://book.sdacn.cn/uploads/20200814/897129e37c4d513854290a671010bb55.jpg","cate_content":"","update_time":1606494197,"weigh":120,"sortAdd":"诗歌-赞美之泉","num":13,"version":1,"item":[{"author":"赞美之泉事工","cate_id":409,"num":13}]}]
     */

    public int code;
    public String msg;
    public String time;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * id : 7
         * pid : 5
         * status : 1
         * cate_name : 创世记
         * author :
         * cate_desc : 本卷一共有50章
         * cate_image : http://download.sdacn.cn/yjgj/01img/01.jpg
         * cate_content :
         * update_time : 1606916715
         * weigh : 1
         * sortAdd : 圣经-旧约
         * num : 50
         * version : 12
         * item : [{"author":"新译本版女生版","cate_id":7,"num":50},{"author":"和合本圣经朗读","cate_id":7,"num":50},{"author":"KJV","cate_id":7,"num":50},{"author":"汉语协会","cate_id":7,"num":50},{"author":"台湾(闽南语)","cate_id":7,"num":50},{"author":"Japan","cate_id":7,"num":50},{"author":"NRSV","cate_id":7,"num":50},{"author":"雷州话","cate_id":7,"num":50},{"author":"德语","cate_id":7,"num":50},{"author":"Spanish","cate_id":7,"num":50},{"author":"法语","cate_id":7,"num":50},{"author":"韩语","cate_id":7,"num":50}]
         */

        public String id;
        public String cate_id;
        public int pid;
        public int status;
        public String cate_name;
        public String author;
        public String selectedAuthor;
        public String cate_desc;
        public String cate_image;
        public String cate_content;
        public String duration;
        public int update_time;
        public int weigh;
        public String sortAdd;
        public int num;
        public int version;
        public List<ItemBean> item;
        public String chapter;
        public boolean isPlay;

        public static class ItemBean {
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof ItemBean)) return false;
                ItemBean itemBean = (ItemBean) o;
                return cate_id == itemBean.cate_id &&
                        Objects.equals(author, itemBean.author);
            }

            @Override
            public int hashCode() {
                return Objects.hash(author, cate_id);
            }

            /**
             * author : 新译本版女生版
             * cate_id : 7
             * num : 50
             */

            public String author;
            public int cate_id;
            public int num;
        }
    }
}
