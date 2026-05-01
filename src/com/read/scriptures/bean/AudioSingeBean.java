package com.read.scriptures.bean;

import com.music.player.lib.bean.BaseAudioInfo;

public class AudioSingeBean {

    /**
     * code : 1
     * msg :
     * time : 1605521878
     * data : {"id":14366,"cate_id":7,"chapter":"第6章","author":"新译本版女生版","audio_cover":"","audio_url":"http://dw.sdacn.cn/01-圣经/01-圣经新译本版女生版/01-旧约/01-创世纪/第06章.mp3","audio_name":"","audio_lyric":"","create_time":1605334492,"update_time":1605334492,"remark":"","weigh":6,"duration":"215","collect":0,"group":{"id":7,"cate_name":"创世记 ","author":"新译本-女生","cate_desc":"本卷一共有50章","cate_image":"http://download.sdacn.cn/yjgj/01img/01.jpg","cate_content":"","pid":5}}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean extends BaseAudioInfo {
        /**
         * id : 14366
         * cate_id : 7
         * chapter : 第6章
         * author : 新译本版女生版
         * audio_cover :
         * audio_url : http://dw.sdacn.cn/01-圣经/01-圣经新译本版女生版/01-旧约/01-创世纪/第06章.mp3
         * audio_name :
         * audio_lyric :
         * create_time : 1605334492
         * update_time : 1605334492
         * remark :
         * weigh : 6
         * duration : 215
         * collect : 0
         * group : {"id":7,"cate_name":"创世记 ","author":"新译本-女生","cate_desc":"本卷一共有50章","cate_image":"http://download.sdacn.cn/yjgj/01img/01.jpg","cate_content":"","pid":5}
         */

        public String id;
        public int cate_id;
        public String chapter;
        public String author;
        public String audio_cover;
        public String audio_url;
        public String audio_name;
        public String audio_lyric;
        public int create_time;
        public int update_time;
        public String remark;
        public int weigh;
        public long duration;
        public int collect;
        public GroupBean group;

        public static class GroupBean {
            /**
             * id : 7
             * cate_name : 创世记
             * author : 新译本-女生
             * cate_desc : 本卷一共有50章
             * cate_image : http://download.sdacn.cn/yjgj/01img/01.jpg
             * cate_content :
             * pid : 5
             */

            public int id;
            public String cate_name;
            public String author;
            public String cate_desc;
            public String cate_image;
            public String cate_content;
            public int pid;
        }
    }
}
