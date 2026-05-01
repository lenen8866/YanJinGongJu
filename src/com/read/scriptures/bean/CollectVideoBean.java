package com.read.scriptures.bean;

import com.read.scriptures.view.indexablerv.IndexableEntity;

import java.util.List;

public class CollectVideoBean {

    /**
     * code : 1
     * msg :
     * time : 1615687200
     * data : {"total":5,"rows":[{"id":619,"type":3,"create_time":1615686489,"datainfo":{"id":5,"cate_id":35,"chapter":"第1章 背道反教","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/1.png","video_name":"*视频介绍*"}},{"id":620,"type":3,"create_time":1615686590,"datainfo":{"id":6,"cate_id":35,"chapter":"第2章 压迫下的宗教改革","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/2.png","video_name":"视频介绍*:"}},{"id":621,"type":3,"create_time":1615687192,"datainfo":{"id":200,"cate_id":39,"chapter":"第01集-不住的祷告  ","author":"道格牧师","video_cover":"","video_name":""}},{"id":622,"type":3,"create_time":1615687194,"datainfo":{"id":201,"cate_id":39,"chapter":"第02集 祷告十密钥","author":"道格牧师","video_cover":"","video_name":""}},{"id":623,"type":3,"create_time":1615687197,"datainfo":{"id":161,"cate_id":42,"chapter":"第01集 最后的王国","author":"道格牧师","video_cover":"","video_name":""}}],"pagess":"1"}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * total : 5
         * rows : [{"id":619,"type":3,"create_time":1615686489,"datainfo":{"id":5,"cate_id":35,"chapter":"第1章 背道反教","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/1.png","video_name":"*视频介绍*"}},{"id":620,"type":3,"create_time":1615686590,"datainfo":{"id":6,"cate_id":35,"chapter":"第2章 压迫下的宗教改革","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/2.png","video_name":"视频介绍*:"}},{"id":621,"type":3,"create_time":1615687192,"datainfo":{"id":200,"cate_id":39,"chapter":"第01集-不住的祷告  ","author":"道格牧师","video_cover":"","video_name":""}},{"id":622,"type":3,"create_time":1615687194,"datainfo":{"id":201,"cate_id":39,"chapter":"第02集 祷告十密钥","author":"道格牧师","video_cover":"","video_name":""}},{"id":623,"type":3,"create_time":1615687197,"datainfo":{"id":161,"cate_id":42,"chapter":"第01集 最后的王国","author":"道格牧师","video_cover":"","video_name":""}}]
         * pagess : 1
         */

        public int total;
        public String pagess;
        public List<RowsBean> rows;

        public static class RowsBean implements IndexableEntity {
            /**
             * id : 619
             * type : 3
             * create_time : 1615686489
             * datainfo : {"id":5,"cate_id":35,"chapter":"第1章 背道反教","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/1.png","video_name":"*视频介绍*"}
             */
            @Override
            public String getFieldIndexBy() {
                return chapter;
            }

            @Override
            public void setFieldIndexBy(String indexField) {
                this.chapter = indexField;
            }

            @Override
            public void setFieldPinyinIndexBy(String pinyin) {
                this.pinyin = pinyin;
            }

            private String pinyin;
            public String chapter;
            public String id;
            public int type;
            public String create_time;
            public DatainfoBean datainfo;

            public static class DatainfoBean {
                /**
                 * id : 5
                 * cate_id : 35
                 * chapter : 第1章 背道反教
                 * author : 线路一
                 * video_cover : http://download.sdacn.cn/XinHuoXiangChuang/1.png
                 * video_name : *视频介绍*
                 */

                public String id;
                public String cate_id;
                public String chapter;
                public String author;
                public String video_cover;
                public String video_name;
            }
        }
    }
}
