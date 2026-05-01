package com.read.scriptures.bean;

import com.read.scriptures.view.indexablerv.IndexableEntity;

import java.util.List;


public class CollectAudioBean {

    /**
     * code : 1
     * msg :
     * time : 1608901809
     * data : {"total":10,"rows":[{"id":115,"type":1,"create_time":1605694977,"datainfo":{"id":23935,"cate_id":418,"chapter":"12月14日 胜利将喜乐带进天国","author":"张睿君","audio_cover":"","audio_name":""}},{"id":117,"type":1,"create_time":1605701655,"datainfo":{"id":23938,"cate_id":418,"chapter":"12月17日 战场上的胜利","author":"张睿君","audio_cover":"","audio_name":""}},{"id":118,"type":1,"create_time":1605709455,"datainfo":{"id":23936,"cate_id":418,"chapter":"12月15日 没什么是难以克服的","author":"张睿君","audio_cover":"","audio_name":""}},{"id":119,"type":1,"create_time":1605758855,"datainfo":{"id":23937,"cate_id":418,"chapter":"12月16日 从火中抽出的一根柴","author":"张睿君","audio_cover":"","audio_name":""}},{"id":120,"type":1,"create_time":1605785652,"datainfo":{"id":23945,"cate_id":418,"chapter":"12月24日 在祂的宝座上与祂同坐","author":"张睿君","audio_cover":"","audio_name":""}},{"id":129,"type":1,"create_time":1605964867,"datainfo":{"id":7,"cate_id":7,"chapter":"第7章","author":"新译本(女)","audio_cover":"","audio_name":""}},{"id":130,"type":1,"create_time":1606010953,"datainfo":{"id":10,"cate_id":7,"chapter":"第10章","author":"新译本(女)","audio_cover":"","audio_name":""}},{"id":134,"type":1,"create_time":1606210107,"datainfo":{"id":11592,"cate_id":38,"chapter":"第2章","author":"NKJV","audio_cover":"","audio_name":""}},{"id":146,"type":1,"create_time":1608901350,"datainfo":{"id":3568,"cate_id":7,"chapter":"第1章","author":"现代版(男)","audio_cover":"","audio_name":""}},{"id":147,"type":1,"create_time":1608901400,"datainfo":{"id":3569,"cate_id":7,"chapter":"第2章","author":"现代版(男)","audio_cover":"","audio_name":""}}],"pagess":"1"}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * total : 10
         * rows : [{"id":115,"type":1,"create_time":1605694977,"datainfo":{"id":23935,"cate_id":418,"chapter":"12月14日 胜利将喜乐带进天国","author":"张睿君","audio_cover":"","audio_name":""}},{"id":117,"type":1,"create_time":1605701655,"datainfo":{"id":23938,"cate_id":418,"chapter":"12月17日 战场上的胜利","author":"张睿君","audio_cover":"","audio_name":""}},{"id":118,"type":1,"create_time":1605709455,"datainfo":{"id":23936,"cate_id":418,"chapter":"12月15日 没什么是难以克服的","author":"张睿君","audio_cover":"","audio_name":""}},{"id":119,"type":1,"create_time":1605758855,"datainfo":{"id":23937,"cate_id":418,"chapter":"12月16日 从火中抽出的一根柴","author":"张睿君","audio_cover":"","audio_name":""}},{"id":120,"type":1,"create_time":1605785652,"datainfo":{"id":23945,"cate_id":418,"chapter":"12月24日 在祂的宝座上与祂同坐","author":"张睿君","audio_cover":"","audio_name":""}},{"id":129,"type":1,"create_time":1605964867,"datainfo":{"id":7,"cate_id":7,"chapter":"第7章","author":"新译本(女)","audio_cover":"","audio_name":""}},{"id":130,"type":1,"create_time":1606010953,"datainfo":{"id":10,"cate_id":7,"chapter":"第10章","author":"新译本(女)","audio_cover":"","audio_name":""}},{"id":134,"type":1,"create_time":1606210107,"datainfo":{"id":11592,"cate_id":38,"chapter":"第2章","author":"NKJV","audio_cover":"","audio_name":""}},{"id":146,"type":1,"create_time":1608901350,"datainfo":{"id":3568,"cate_id":7,"chapter":"第1章","author":"现代版(男)","audio_cover":"","audio_name":""}},{"id":147,"type":1,"create_time":1608901400,"datainfo":{"id":3569,"cate_id":7,"chapter":"第2章","author":"现代版(男)","audio_cover":"","audio_name":""}}]
         * pagess : 1
         */

        public int total;
        public String pagess;
        public List<RowsBean> rows;

        public static class RowsBean {
            /**
             * id : 115
             * type : 1
             * create_time : 1605694977
             * datainfo : {"id":23935,"cate_id":418,"chapter":"12月14日 胜利将喜乐带进天国","author":"张睿君","audio_cover":"","audio_name":""}
             */

            public int id;
            public int type;
            public long create_time;
            public DatainfoBean datainfo;

            public static class DatainfoBean {
                /**
                 * id : 23935
                 * cate_id : 418
                 * chapter : 12月14日 胜利将喜乐带进天国
                 * author : 张睿君
                 * audio_cover :
                 * audio_name :
                 */

                public String id;
                public String cate_id;
                public String chapter;
                public String author;
                public String audio_cover;
                public String audio_name;
                public String sortAdd;
                public String cate_name;
                public String cate_image;
                public String video_cover;
            }
        }
    }
}
