package com.read.scriptures.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class VideoListBean implements Serializable {

    /**
     * total : 8
     * rows : [{"id":5,"cate_id":35,"chapter":"第1章 背道反教","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/1.png","video_name":"*视频介绍*","video_lyric":"","create_time":1597046540,"update_time":1597241582,"remark":"","duration":2435,"weigh":"1","start":null,"end":null,"v1cate":{"id":35,"cate_name":"薪火相传"},"collect":0},{"id":6,"cate_id":35,"chapter":"第2章 压迫下的宗教改革","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/2.png","video_name":"视频介绍*:","video_lyric":"","create_time":1597046540,"update_time":1597241594,"remark":"","duration":1272,"weigh":"2","start":null,"end":null,"v1cate":{"id":35,"cate_name":"薪火相传"},"collect":0},{"id":7,"cate_id":35,"chapter":"第3章 大期望","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/3.png","video_name":"视频介绍*:","video_lyric":"","create_time":1597046540,"update_time":1597241637,"remark":"","duration":2141,"weigh":"3","start":null,"end":null,"v1cate":{"id":35,"cate_name":"薪火相传"},"collect":0},{"id":8,"cate_id":35,"chapter":"第4章 大失望之后","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/4.png","video_name":"视频介绍*:","video_lyric":"","create_time":1597046540,"update_time":1597241651,"remark":"","duration":1967,"weigh":"4","start":null,"end":null,"v1cate":{"id":35,"cate_name":"薪火相传"},"collect":0},{"id":9,"cate_id":35,"chapter":"第5章  弱者中的强者","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/5.png","video_name":"视频介绍*:","video_lyric":"","create_time":1597046540,"update_time":1597241662,"remark":"","duration":1941,"weigh":"5","start":null,"end":null,"v1cate":{"id":35,"cate_name":"薪火相传"},"collect":0},{"id":10,"cate_id":35,"chapter":"第6章 小光","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/6.png","video_name":"视频介绍*:","video_lyric":"","create_time":1597046540,"update_time":1597241675,"remark":"","duration":2048,"weigh":"6","start":null,"end":null,"v1cate":{"id":35,"cate_name":"薪火相传"},"collect":0},{"id":11,"cate_id":35,"chapter":"第7章 健康医疗事工","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/7.png","video_name":"","video_lyric":"","create_time":1597046540,"update_time":1597309529,"remark":"","duration":2401,"weigh":"7","start":null,"end":null,"v1cate":{"id":35,"cate_name":"薪火相传"},"collect":0},{"id":12,"cate_id":35,"chapter":"第8章 一个普通的妇人","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/8.png","video_name":"","video_lyric":"","create_time":1597046540,"update_time":1597309524,"remark":"","duration":2856,"weigh":"8","start":null,"end":null,"v1cate":{"id":35,"cate_name":"薪火相传"},"collect":0}]
     * pagess : 1
     */

    public int total;
    public int pagess;
    public List<RowsBean> rows;

    public static class RowsBean implements Serializable {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RowsBean)) return false;
            RowsBean rowsBean = (RowsBean) o;
            return id.equals(rowsBean.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        /**
         * id : 5
         * cate_id : 35
         * chapter : 第1章 背道反教
         * author : 线路一
         * video_cover : http://download.sdacn.cn/XinHuoXiangChuang/1.png
         * video_name : *视频介绍*
         * video_lyric :
         * create_time : 1597046540
         * update_time : 1597241582
         * remark :
         * duration : 2435
         * weigh : 1
         * start : null
         * end : null
         * v1cate : {"id":35,"cate_name":"薪火相传"}
         * collect : 0
         */


        public String id;
        public String cate_id;
        public String chapter;
        public String author;
        public String video_cover;
        public String video_name;
        public String video_lyric;
        public String video_url;
        public int create_time;
        public int update_time;
        public String remark;
        public String duration;
        public String weigh;
        public String start;
        public String end;
        public V1cateBean v1cate;
        public String cate1_name="";
        public String cate2_name="";
        public String cate3_name="";
        public int collect;
        public String playDuration;
        public String bookCover;


        public static class V1cateBean implements Serializable{
            /**
             * id : 35
             * cate_name : 薪火相传
             */

            public int id;
            public String cate_name;
            public String cate_image;
        }
    }
}
