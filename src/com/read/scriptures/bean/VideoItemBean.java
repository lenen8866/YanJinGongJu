package com.read.scriptures.bean;

public class VideoItemBean {

    /**
     * code : 1
     * msg :
     * time : 1615122228
     * data : {"id":5,"cate_id":35,"chapter":"第1章 背道反教","author":"线路一","video_cover":"http://download.sdacn.cn/XinHuoXiangChuang/1.png","video_url":"IxIHtEKQf3v7LkvCO9QXPHoTr2mvSwjnn2gpSJ1KqD/RttzMmiwwPyWvAN1IPmrnjPA4rqY4OVxzFFtgQC+RJeJL3XOs/jSHEOGlgFCzg0qoX7X4+1MCgV/P+o7CxuBZ3lD1cS0IGu9eC4Fs1CaPJRS237NO1RjOSJYqNTB7F7w=","video_name":"*视频介绍*","video_lyric":"","create_time":1597046540,"update_time":1597241582,"remark":"","duration":2435,"weigh":"1","start":null,"end":null,"collect":0}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * id : 5
         * cate_id : 35
         * chapter : 第1章 背道反教
         * author : 线路一
         * video_cover : http://download.sdacn.cn/XinHuoXiangChuang/1.png
         * video_url : IxIHtEKQf3v7LkvCO9QXPHoTr2mvSwjnn2gpSJ1KqD/RttzMmiwwPyWvAN1IPmrnjPA4rqY4OVxzFFtgQC+RJeJL3XOs/jSHEOGlgFCzg0qoX7X4+1MCgV/P+o7CxuBZ3lD1cS0IGu9eC4Fs1CaPJRS237NO1RjOSJYqNTB7F7w=
         * video_name : *视频介绍*
         * video_lyric :
         * create_time : 1597046540
         * update_time : 1597241582
         * remark :
         * duration : 2435
         * weigh : 1
         * start : null
         * end : null
         * collect : 0
         */

        public String id;
        public int cate_id;
        public String chapter;
        public String author;
        public String video_cover;
        public String video_url;
        public String video_name;
        public String video_lyric;
        public int create_time;
        public int update_time;
        public String remark;
        public int duration;
        public String weigh;
        public String start;
        public String end;
        public String cate1_name;
        public String cate2_name;
        public int collect;
    }
}
