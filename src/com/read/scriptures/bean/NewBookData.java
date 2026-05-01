package com.read.scriptures.bean;

import java.io.Serializable;
import java.util.List;

public class NewBookData {

    /**
     * total : 30
     * offset : 0
     * rows : [{"id":7,"name":"创世记 ","image":"http://download.sdacn.cn/yjgj/01img/01.jpg","author":"摩西"},{"id":8,"name":"出埃及记","image":"http://download.sdacn.cn/yjgj/01img/02.jpg","author":"摩西"},{"id":9,"name":"利未记","image":"http://download.sdacn.cn/yjgj/01img/03.jpg","author":"摩西"},{"id":10,"name":"民数记","image":"http://download.sdacn.cn/yjgj/01img/04.jpg","author":"摩西"},{"id":11,"name":"申命记","image":"http://download.sdacn.cn/yjgj/01img/05.jpg","author":"摩西"},{"id":12,"name":"约书亚记","image":"http://download.sdacn.cn/yjgj/01img/06.jpg","author":""},{"id":13,"name":"士师记","image":"http://download.sdacn.cn/yjgj/01img/07.jpg","author":""},{"id":14,"name":"路得记","image":"http://download.sdacn.cn/yjgj/01img/08.jpg","author":""},{"id":15,"name":"撒母耳记上","image":"http://download.sdacn.cn/yjgj/01img/09.jpg","author":""},{"id":16,"name":"撒母耳记下","image":"http://download.sdacn.cn/yjgj/01img/10.jpg","author":""},{"id":17,"name":"列王纪上","image":"http://download.sdacn.cn/yjgj/01img/11.jpg","author":""},{"id":18,"name":"列王纪下","image":"http://download.sdacn.cn/yjgj/01img/12.jpg","author":""},{"id":19,"name":"历代志上","image":"http://download.sdacn.cn/yjgj/01img/13.jpg","author":""},{"id":20,"name":"历代志下","image":"http://download.sdacn.cn/yjgj/01img/14.jpg","author":""},{"id":21,"name":"以斯拉记","image":"http://download.sdacn.cn/yjgj/01img/15.jpg","author":""},{"id":22,"name":"尼希米记","image":"http://download.sdacn.cn/yjgj/01img/16.jpg","author":""},{"id":23,"name":"以斯帖记","image":"http://download.sdacn.cn/yjgj/01img/17.jpg","author":""},{"id":24,"name":"约伯记","image":"http://download.sdacn.cn/yjgj/01img/18.jpg","author":""},{"id":25,"name":"诗篇","image":"http://download.sdacn.cn/yjgj/01img/19.jpg","author":""},{"id":26,"name":"箴言","image":"http://download.sdacn.cn/yjgj/01img/20.jpg","author":""},{"id":27,"name":"传道书","image":"http://download.sdacn.cn/yjgj/01img/21.jpg","author":""},{"id":28,"name":"雅歌","image":"http://download.sdacn.cn/yjgj/01img/22.jpg","author":""},{"id":29,"name":"以赛亚书","image":"http://download.sdacn.cn/yjgj/01img/23.jpg","author":""},{"id":30,"name":"耶利米书","image":"http://download.sdacn.cn/yjgj/01img/24.jpg","author":""},{"id":31,"name":"耶利米哀歌","image":"http://download.sdacn.cn/yjgj/01img/25.jpg","author":""},{"id":32,"name":"以西结书","image":"http://download.sdacn.cn/yjgj/01img/26.jpg","author":""},{"id":33,"name":"但以理书","image":"http://download.sdacn.cn/yjgj/01img/27.jpg","author":""},{"id":34,"name":"何西阿书","image":"http://download.sdacn.cn/yjgj/01img/28.jpg","author":""},{"id":35,"name":"约珥书","image":"http://download.sdacn.cn/yjgj/01img/29.jpg","author":""},{"id":36,"name":"阿摩司书","image":"http://download.sdacn.cn/yjgj/01img/30.jpg","author":""}]
     */

    public int total;
    public int offset;
    public List<RowsBean> rows;

    public static class RowsBean implements Serializable {
        /**
         * id : 7
         * name : 创世记
         * image : http://download.sdacn.cn/yjgj/01img/01.jpg
         * author : 摩西
         */

        public String id;
        public String cate1_id;
        public String name;
        public String image;
        public String author;
        public String content;
        public String desc;//本卷一共有50章
        public String cate1_name;//圣经
        public String cate2_name;//旧约
        public boolean isOpen;
        public int color = -1;
    }
}
