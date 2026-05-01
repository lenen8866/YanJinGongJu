package com.read.scriptures.bean;

import java.util.List;

public class InviteVideoBean {

    /**
     * code : 1
     * msg :
     * time : 1619858039
     * data : [{"id":10,"type":1,"name":"最新视频","media":0,"remark":"输入了4个名称，这个是视频","data_id":"39,40,41,42","column":[{"id":41,"status":1,"cate_name":"宇宙之战","author":"","cate_desc":"","cate_image":"http://tv2.qimiaotv.com//UploadFiles/61191c88-c1c0-49d9-8fa2-b2b566be8d94.png","cate_content":"制作质量堪比好莱坞大片，藉着精彩的视频画面，将严肃的圣经真理呈现在众人面前\u2026\u2026","pid":34,"update_time":1614337311,"weigh":20,"group_one":"证道","group_two":"奇妙真相"},{"id":42,"status":1,"cate_name":"最奇妙的圣经预言","author":"","cate_desc":"","cate_image":"http://tv2.qimiaotv.com//UploadFiles/18d7ad5d-3348-457f-b353-d6ff8584d387.png","cate_content":"人类可以预知未来吗？地球的明天会怎样？罪恶、战争以及各样的自然灾害与日剧增，这是否预示着即将到来的巨变？探索最奇妙的圣经预言，去发现其中能够扭转人生的奥秘\u2026\u2026","pid":34,"update_time":1614337302,"weigh":30,"group_one":"证道","group_two":"奇妙真相"},{"id":40,"status":1,"cate_name":"奇妙集锦","author":"","cate_desc":"","cate_image":"http://tv2.qimiaotv.com//UploadFiles/7a6f8baf-d85e-4760-aa5a-1360894edd7e.png","cate_content":"在一个快节奏的时代，许多人忙于工作和生活，无瑕聆听上帝的信息。为此我们制作了这个30分钟的短讲系列，虽然时长被压缩，但真理无半点儿减少\u2026\u2026上帝赐福您！","pid":34,"update_time":1614337329,"weigh":10,"group_one":"证道","group_two":"奇妙真相"},{"id":39,"status":1,"cate_name":"祷告的特权","author":"","cate_desc":"","cate_image":"http://tv2.qimiaotv.com//UploadFiles/08e0ad57-bccc-41d4-a654-3f63209fccbc.jpg","cate_content":"若像天使般赞美，却不祷告，歌声无法救你；若慷慨地奉献，却不祷告，捐献不能救你；若汗流浃背，却不祷告，辛苦的付出也不能救你，想得救，首先要随时随地的、认真祷告\u2026..","pid":34,"update_time":1614337337,"weigh":0,"group_one":"证道","group_two":"奇妙真相"}]},{"id":11,"type":1,"name":"2个章节","media":1,"remark":"我输入了2个章节，这个是视频","data_id":"201,200","column":[{"id":200,"cate_id":39,"chapter":"第01集-不住的祷告  ","author":"道格牧师","video_cover":"","video_name":"","video_lyric":"","duration":3510,"start":"00:00:22","end":"00:58:24","group_one":"奇妙真相","group_two":"祷告的特权"},{"id":201,"cate_id":39,"chapter":"第02集 祷告十密钥","author":"道格牧师","video_cover":"","video_name":"","video_lyric":"","duration":3510,"start":"00:00:22","end":"00:58:21","group_one":"奇妙真相","group_two":"祷告的特权"}]}]
     */

    public int code;
    public String msg;
    public String time;
    public List<DataDTO> data;

    public static class DataDTO {
        @Override
        public String toString() {
            return "DataDTO{" +
                    "id=" + id +
                    ", type=" + type +
                    ", name='" + name + '\'' +
                    ", media=" + media +
                    ", remark='" + remark + '\'' +
                    ", data_id='" + data_id + '\'' +
                    ", column=" + column +
                    '}';
        }

        /**
         * id : 10
         * type : 1
         * name : 最新视频
         * media : 0
         * remark : 输入了4个名称，这个是视频
         * data_id : 39,40,41,42
         * column : [{"id":41,"status":1,"cate_name":"宇宙之战","author":"","cate_desc":"","cate_image":"http://tv2.qimiaotv.com//UploadFiles/61191c88-c1c0-49d9-8fa2-b2b566be8d94.png","cate_content":"制作质量堪比好莱坞大片，藉着精彩的视频画面，将严肃的圣经真理呈现在众人面前\u2026\u2026","pid":34,"update_time":1614337311,"weigh":20,"group_one":"证道","group_two":"奇妙真相"},{"id":42,"status":1,"cate_name":"最奇妙的圣经预言","author":"","cate_desc":"","cate_image":"http://tv2.qimiaotv.com//UploadFiles/18d7ad5d-3348-457f-b353-d6ff8584d387.png","cate_content":"人类可以预知未来吗？地球的明天会怎样？罪恶、战争以及各样的自然灾害与日剧增，这是否预示着即将到来的巨变？探索最奇妙的圣经预言，去发现其中能够扭转人生的奥秘\u2026\u2026","pid":34,"update_time":1614337302,"weigh":30,"group_one":"证道","group_two":"奇妙真相"},{"id":40,"status":1,"cate_name":"奇妙集锦","author":"","cate_desc":"","cate_image":"http://tv2.qimiaotv.com//UploadFiles/7a6f8baf-d85e-4760-aa5a-1360894edd7e.png","cate_content":"在一个快节奏的时代，许多人忙于工作和生活，无瑕聆听上帝的信息。为此我们制作了这个30分钟的短讲系列，虽然时长被压缩，但真理无半点儿减少\u2026\u2026上帝赐福您！","pid":34,"update_time":1614337329,"weigh":10,"group_one":"证道","group_two":"奇妙真相"},{"id":39,"status":1,"cate_name":"祷告的特权","author":"","cate_desc":"","cate_image":"http://tv2.qimiaotv.com//UploadFiles/08e0ad57-bccc-41d4-a654-3f63209fccbc.jpg","cate_content":"若像天使般赞美，却不祷告，歌声无法救你；若慷慨地奉献，却不祷告，捐献不能救你；若汗流浃背，却不祷告，辛苦的付出也不能救你，想得救，首先要随时随地的、认真祷告\u2026..","pid":34,"update_time":1614337337,"weigh":0,"group_one":"证道","group_two":"奇妙真相"}]
         */

        public int id;
        public int type;
        public String name;
        public int media;
        public String remark;
        public String data_id;
        public List<ColumnDTO> column;

        public static class ColumnDTO {
            @Override
            public String toString() {
                return "ColumnDTO{" +
                        "id='" + id + '\'' +
                        ", status=" + status +
                        ", cate_name='" + cate_name + '\'' +
                        ", author='" + author + '\'' +
                        ", chapter='" + chapter + '\'' +
                        ", cate_id='" + cate_id + '\'' +
                        ", cate_desc='" + cate_desc + '\'' +
                        ", cate_image='" + cate_image + '\'' +
                        ", video_cover='" + video_cover + '\'' +
                        ", cate_content='" + cate_content + '\'' +
                        ", start='" + start + '\'' +
                        ", end='" + end + '\'' +
                        ", pid=" + pid +
                        ", update_time=" + update_time +
                        ", weigh=" + weigh +
                        ", group_one='" + group_one + '\'' +
                        ", duration='" + duration + '\'' +
                        ", group_two='" + group_two + '\'' +
                        '}';
            }

            /**
             * id : 41
             * status : 1
             * cate_name : 宇宙之战
             * author :
             * cate_desc :
             * cate_image : http://tv2.qimiaotv.com//UploadFiles/61191c88-c1c0-49d9-8fa2-b2b566be8d94.png
             * cate_content : 制作质量堪比好莱坞大片，藉着精彩的视频画面，将严肃的圣经真理呈现在众人面前……
             * pid : 34
             * update_time : 1614337311
             * weigh : 20
             * group_one : 证道
             * group_two : 奇妙真相
             */

            public String id;
            public int status;
            public String cate_name;
            public String author;
            public String chapter;
            public String cate_id;
            public String cate_desc;
            public String cate_image;
            public String video_cover;
            public String cate_content;
            public String start;
            public String end;
            public String pid;
            public String image;
            public String audio_cover;
            public int update_time;
            public int weigh;
            public String group_one;
            public String duration;
            public String group_two;
            public String cacheImg;
            public V1cate v1cate;

            public class V1cate {
                public long id;
                public String cate_name;
                public long pid;
                public String cate_image;
            }
        }
    }
}
