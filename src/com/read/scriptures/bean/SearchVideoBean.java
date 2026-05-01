package com.read.scriptures.bean;

import java.util.List;

public class SearchVideoBean {

    /**
     * total : 1
     * rows : [{"id":471,"cate3_id":44,"author":"道格牧师","chapter":"第33集 前瞻思维：恶仆人与好榜样","video_cover":"","video_url":"http://tv1.qimiaotv.com/264/Eg-33-%E5%89%8D%E7%9E%BB%E6%80%9D%E7%BB%B4%EF%BC%9A%E6%81%B6%E4%BB%86%E4%BA%BA%EF%BC%8C%E5%A5%BD%E6%A6%9C%E6%A0%B7.mp4","video_name":"","video_lyric":"","duration":3513,"collect":0,"cate3_name":"永远的福音","content":"在这个系列视频中，您将听到30个与上帝的拯救密切相关的真理，我们相信，这是您的需要\u2026\u2026","desc":"","image":"http://tv2.qimiaotv.com//UploadFiles/4fc6fc78-3e1e-49fa-98ce-24a0d1a80dd0.png","cate2_id":34,"cate2_name":"奇妙真相","cate1_id":30,"cate1_name":"证道"}]
     */

    public int total;
    public List<RowsDTO> rows;

    public static class RowsDTO {
        /**
         * id : 471
         * cate3_id : 44
         * author : 道格牧师
         * chapter : 第33集 前瞻思维：恶仆人与好榜样
         * video_cover :
         * video_url : http://tv1.qimiaotv.com/264/Eg-33-%E5%89%8D%E7%9E%BB%E6%80%9D%E7%BB%B4%EF%BC%9A%E6%81%B6%E4%BB%86%E4%BA%BA%EF%BC%8C%E5%A5%BD%E6%A6%9C%E6%A0%B7.mp4
         * video_name :
         * video_lyric :
         * duration : 3513
         * collect : 0
         * cate3_name : 永远的福音
         * content : 在这个系列视频中，您将听到30个与上帝的拯救密切相关的真理，我们相信，这是您的需要……
         * desc :
         * image : http://tv2.qimiaotv.com//UploadFiles/4fc6fc78-3e1e-49fa-98ce-24a0d1a80dd0.png
         * cate2_id : 34
         * cate2_name : 奇妙真相
         * cate1_id : 30
         * cate1_name : 证道
         */

        public String id;
        public String cate3_id;
        public String author;
        public String chapter;
        public String video_cover;
        public String video_url;
        public String video_name;
        public String video_lyric;
        public String duration;
        public int collect;
        public String cate3_name;
        public String content;
        public String desc;
        public String image;
        public String cate2_id;
        public String cate2_name;
        public String cate1_id;
        public String cate1_name;
    }
}
