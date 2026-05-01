package com.read.scriptures.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class NewSearchAudioBean implements Serializable{

    /**
     * code : 1
     * msg :
     * time : 1608812474
     * data : [{"id":28854,"cate_id":414,"chapter":"01月01日 在新年高举上帝的儿子主耶稣","author":"朗读:李佳女","num":1,"sortAdd":"怀著-灵修","cate_name":"高举主耶稣","item":[{"id":28854,"cate_id":414,"author":"朗读:李佳女","num":365,"v1cate":{"id":414,"cate_name":"高举主耶稣","pid":239}}]},{"id":1,"cate_id":7,"chapter":"第1章","author":"新译本(女)","num":66,"sortAdd":"圣经-旧约","cate_name":"创世记 ","item":[{"id":1,"cate_id":7,"author":"新译本(女)","num":50,"v1cate":{"id":7,"cate_name":"创世记 ","pid":5}},{"id":51,"cate_id":8,"author":"新译本(女)","num":40,"v1cate":{"id":8,"cate_name":"出埃及记","pid":5}},{"id":91,"cate_id":9,"author":"新译本(女)","num":27,"v1cate":{"id":9,"cate_name":"利未记","pid":5}},{"id":118,"cate_id":10,"author":"新译本(女)","num":36,"v1cate":{"id":10,"cate_name":"民数记","pid":5}},{"id":154,"cate_id":11,"author":"新译本(女)","num":34,"v1cate":{"id":11,"cate_name":"申命记","pid":5}},{"id":188,"cate_id":12,"author":"新译本(女)","num":24,"v1cate":{"id":12,"cate_name":"约书亚记","pid":5}},{"id":212,"cate_id":13,"author":"新译本(女)","num":21,"v1cate":{"id":13,"cate_name":"士师记","pid":5}},{"id":233,"cate_id":14,"author":"新译本(女)","num":4,"v1cate":{"id":14,"cate_name":"路得记","pid":5}},{"id":237,"cate_id":15,"author":"新译本(女)","num":31,"v1cate":{"id":15,"cate_name":"撒母耳记上","pid":5}},{"id":268,"cate_id":16,"author":"新译本(女)","num":24,"v1cate":{"id":16,"cate_name":"撒母耳记下","pid":5}},{"id":292,"cate_id":17,"author":"新译本(女)","num":22,"v1cate":{"id":17,"cate_name":"列王纪上","pid":5}},{"id":314,"cate_id":18,"author":"新译本(女)","num":25,"v1cate":{"id":18,"cate_name":"列王纪下","pid":5}},{"id":339,"cate_id":19,"author":"新译本(女)","num":29,"v1cate":{"id":19,"cate_name":"历代志上","pid":5}},{"id":368,"cate_id":20,"author":"新译本(女)","num":36,"v1cate":{"id":20,"cate_name":"历代志下","pid":5}},{"id":404,"cate_id":21,"author":"新译本(女)","num":10,"v1cate":{"id":21,"cate_name":"以斯拉记","pid":5}},{"id":414,"cate_id":22,"author":"新译本(女)","num":13,"v1cate":{"id":22,"cate_name":"尼希米记","pid":5}},{"id":427,"cate_id":23,"author":"新译本(女)","num":10,"v1cate":{"id":23,"cate_name":"以斯帖记","pid":5}},{"id":437,"cate_id":24,"author":"新译本(女)","num":42,"v1cate":{"id":24,"cate_name":"约伯记","pid":5}},{"id":479,"cate_id":25,"author":"新译本(女)","num":150,"v1cate":{"id":25,"cate_name":"诗篇","pid":5}},{"id":629,"cate_id":26,"author":"新译本(女)","num":31,"v1cate":{"id":26,"cate_name":"箴言","pid":5}},{"id":660,"cate_id":27,"author":"新译本(女)","num":12,"v1cate":{"id":27,"cate_name":"传道书","pid":5}},{"id":672,"cate_id":28,"author":"新译本(女)","num":8,"v1cate":{"id":28,"cate_name":"雅歌","pid":5}},{"id":680,"cate_id":29,"author":"新译本(女)","num":66,"v1cate":{"id":29,"cate_name":"以赛亚书","pid":5}},{"id":746,"cate_id":30,"author":"新译本(女)","num":52,"v1cate":{"id":30,"cate_name":"耶利米书","pid":5}},{"id":798,"cate_id":31,"author":"新译本(女)","num":5,"v1cate":{"id":31,"cate_name":"耶利米哀歌","pid":5}},{"id":803,"cate_id":32,"author":"新译本(女)","num":48,"v1cate":{"id":32,"cate_name":"以西结书","pid":5}},{"id":851,"cate_id":33,"author":"新译本(女)","num":12,"v1cate":{"id":33,"cate_name":"但以理书","pid":5}},{"id":863,"cate_id":34,"author":"新译本(女)","num":14,"v1cate":{"id":34,"cate_name":"何西阿书","pid":5}},{"id":877,"cate_id":35,"author":"新译本(女)","num":3,"v1cate":{"id":35,"cate_name":"约珥书","pid":5}},{"id":880,"cate_id":36,"author":"新译本(女)","num":9,"v1cate":{"id":36,"cate_name":"阿摩司书","pid":5}},{"id":889,"cate_id":37,"author":"新译本(女)","num":1,"v1cate":{"id":37,"cate_name":"俄巴底亚书","pid":5}},{"id":890,"cate_id":38,"author":"新译本(女)","num":4,"v1cate":{"id":38,"cate_name":"约拿书","pid":5}},{"id":894,"cate_id":39,"author":"新译本(女)","num":7,"v1cate":{"id":39,"cate_name":"弥迦书","pid":5}},{"id":901,"cate_id":40,"author":"新译本(女)","num":3,"v1cate":{"id":40,"cate_name":"那鸿书","pid":5}},{"id":904,"cate_id":41,"author":"新译本(女)","num":3,"v1cate":{"id":41,"cate_name":"哈巴谷书","pid":5}},{"id":907,"cate_id":42,"author":"新译本(女)","num":3,"v1cate":{"id":42,"cate_name":"西番雅书","pid":5}},{"id":910,"cate_id":43,"author":"新译本(女)","num":2,"v1cate":{"id":43,"cate_name":"哈该书","pid":5}},{"id":912,"cate_id":44,"author":"新译本(女)","num":14,"v1cate":{"id":44,"cate_name":"撒迦利亚书","pid":5}},{"id":926,"cate_id":45,"author":"新译本(女)","num":4,"v1cate":{"id":45,"cate_name":"玛拉基书","pid":5}},{"id":930,"cate_id":46,"author":"新译本(女)","num":28,"v1cate":{"id":46,"cate_name":"马太福音","pid":6}},{"id":958,"cate_id":47,"author":"新译本(女)","num":16,"v1cate":{"id":47,"cate_name":"马可福音","pid":6}},{"id":974,"cate_id":48,"author":"新译本(女)","num":24,"v1cate":{"id":48,"cate_name":"路加福音","pid":6}},{"id":998,"cate_id":49,"author":"新译本(女)","num":21,"v1cate":{"id":49,"cate_name":"约翰福音","pid":6}},{"id":1019,"cate_id":50,"author":"新译本(女)","num":28,"v1cate":{"id":50,"cate_name":"使徒行传","pid":6}},{"id":1047,"cate_id":51,"author":"新译本(女)","num":16,"v1cate":{"id":51,"cate_name":"罗马书","pid":6}},{"id":1063,"cate_id":52,"author":"新译本(女)","num":16,"v1cate":{"id":52,"cate_name":"哥林多前书","pid":6}},{"id":1079,"cate_id":53,"author":"新译本(女)","num":13,"v1cate":{"id":53,"cate_name":"哥林多后书","pid":6}},{"id":1092,"cate_id":54,"author":"新译本(女)","num":6,"v1cate":{"id":54,"cate_name":"加拉太书","pid":6}},{"id":1098,"cate_id":55,"author":"新译本(女)","num":6,"v1cate":{"id":55,"cate_name":"以弗所书","pid":6}},{"id":1104,"cate_id":56,"author":"新译本(女)","num":4,"v1cate":{"id":56,"cate_name":"腓立比书","pid":6}},{"id":1108,"cate_id":57,"author":"新译本(女)","num":4,"v1cate":{"id":57,"cate_name":"歌罗西书","pid":6}},{"id":1112,"cate_id":58,"author":"新译本(女)","num":5,"v1cate":{"id":58,"cate_name":"帖撒罗尼迦前书","pid":6}},{"id":1117,"cate_id":59,"author":"新译本(女)","num":3,"v1cate":{"id":59,"cate_name":"帖撒罗尼迦后书","pid":6}},{"id":1120,"cate_id":60,"author":"新译本(女)","num":6,"v1cate":{"id":60,"cate_name":"提摩太前书","pid":6}},{"id":1126,"cate_id":61,"author":"新译本(女)","num":4,"v1cate":{"id":61,"cate_name":"提摩太后书","pid":6}},{"id":1130,"cate_id":62,"author":"新译本(女)","num":3,"v1cate":{"id":62,"cate_name":"提多书","pid":6}},{"id":1133,"cate_id":63,"author":"新译本(女)","num":1,"v1cate":{"id":63,"cate_name":"腓利门书","pid":6}},{"id":1134,"cate_id":64,"author":"新译本(女)","num":13,"v1cate":{"id":64,"cate_name":"希伯来书","pid":6}},{"id":1147,"cate_id":65,"author":"新译本(女)","num":5,"v1cate":{"id":65,"cate_name":"雅各书","pid":6}},{"id":1152,"cate_id":66,"author":"新译本(女)","num":5,"v1cate":{"id":66,"cate_name":"彼得前书","pid":6}},{"id":1157,"cate_id":67,"author":"新译本(女)","num":3,"v1cate":{"id":67,"cate_name":"彼得后书","pid":6}},{"id":1160,"cate_id":68,"author":"新译本(女)","num":5,"v1cate":{"id":68,"cate_name":"约翰一书","pid":6}},{"id":1165,"cate_id":69,"author":"新译本(女)","num":1,"v1cate":{"id":69,"cate_name":"约翰二书","pid":6}},{"id":1166,"cate_id":70,"author":"新译本(女)","num":1,"v1cate":{"id":70,"cate_name":"约翰三书","pid":6}},{"id":1167,"cate_id":71,"author":"新译本(女)","num":1,"v1cate":{"id":71,"cate_name":"犹大书","pid":6}},{"id":1168,"cate_id":72,"author":"新译本(女)","num":22,"v1cate":{"id":72,"cate_name":"启示录","pid":6}}]},{"id":35503,"cate_id":77,"chapter":"000.跟随救主","author":"女低","num":1,"sortAdd":"诗歌-赞美诗","cate_name":"赞美诗388","item":[{"id":35503,"cate_id":77,"author":"女低","num":387,"v1cate":{"id":77,"cate_name":"赞美诗388","pid":84}}]},{"id":35890,"cate_id":77,"chapter":"000.跟随救主","author":"女高","num":1,"sortAdd":"诗歌-赞美诗","cate_name":"赞美诗388","item":[{"id":35890,"cate_id":77,"author":"女高","num":388,"v1cate":{"id":77,"cate_name":"赞美诗388","pid":84}}]}]
     */

    public int code;
    public String msg;
    public String time;
    public List<DataBean> data;

    public static class DataBean implements Serializable {
        /**
         * id : 28854
         * cate_id : 414
         * chapter : 01月01日 在新年高举上帝的儿子主耶稣
         * author : 朗读:李佳女
         * num : 1
         * sortAdd : 怀著-灵修
         * cate_name : 高举主耶稣
         * item : [{"id":28854,"cate_id":414,"author":"朗读:李佳女","num":365,"v1cate":{"id":414,"cate_name":"高举主耶稣","pid":239}}]
         */
        public String selectedAuthor;
        public String id;
        public String cate_id;
        public String chapter;
        public String author;
        public int num;
        public String sortAdd;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DataBean)) return false;
            DataBean dataBean = (DataBean) o;
            return Objects.equals(cate_id, dataBean.cate_id) &&
                    Objects.equals(chapter, dataBean.chapter) &&
                    Objects.equals(cate_name, dataBean.cate_name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cate_id, chapter, cate_name);
        }

        public String cate_name;
        public String duration;
        public List<ItemBean> item;

        public static class ItemBean implements Serializable {

            /**
             * id : 28854
             * cate_id : 414
             * author : 朗读:李佳女
             * num : 365
             * v1cate : {"id":414,"cate_name":"高举主耶稣","pid":239}
             */
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof NewSearchAudioBean1.DataBean.ItemBean)) return false;
                NewSearchAudioBean1.DataBean.ItemBean itemBean = (NewSearchAudioBean1.DataBean.ItemBean) o;
                return  Objects.equals(cate_id, itemBean.cate_id) &&
                        Objects.equals(author, itemBean.author);
            }

            @Override
            public int hashCode() {
                return Objects.hash(author, cate_id);
            }


            public String id;
            public String cate_id;
            public String author;
            public int num;
            public V1cateBean v1cate;


            public String cate_name;
            public String sortAdd;

            public static class V1cateBean implements Serializable{
                /**
                 * id : 414
                 * cate_name : 高举主耶稣
                 * pid : 239
                 */

                public int id;
                public String cate_name;
                public int pid;
            }
        }
    }
}
