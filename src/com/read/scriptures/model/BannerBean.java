package com.read.scriptures.model;

/**
 * Created by Administrator on 2018/8/29.
 */

public class BannerBean {
    /**
     * id : 69
     * type : {"val":2,"text":"本机"}
     * pic_name : 书库轮播图1
     * link : {"audiogroup":1}
     * group : 1
     * picture : null
     * date : 2020-08-31 11:16:28
     * end_date : {"val":"2020-10-30 11:16:28","text":40}
     * pic_url : https://app.sdacn.cn/my/shuku3.png
     * weigh : 3
     * groupformat : {"id":1,"name":"首页轮播"}
     */

    private int id;
    private TypeBean type;
    private String pic_name;
    private String link;
    private int group;
    private Object picture;
    private String date;
    private EndDateBean end_date;
    private String pic_url;
    private int weigh;
    private GroupformatBean groupformat;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeBean getType() {
        return type;
    }

    public void setType(TypeBean type) {
        this.type = type;
    }

    public String getPic_name() {
        return pic_name;
    }

    public void setPic_name(String pic_name) {
        this.pic_name = pic_name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public Object getPicture() {
        return picture;
    }

    public void setPicture(Object picture) {
        this.picture = picture;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public EndDateBean getEnd_date() {
        return end_date;
    }

    public void setEnd_date(EndDateBean end_date) {
        this.end_date = end_date;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }

    public GroupformatBean getGroupformat() {
        return groupformat;
    }

    public void setGroupformat(GroupformatBean groupformat) {
        this.groupformat = groupformat;
    }

    public static class TypeBean {
        /**
         * val : 2
         * text : 本机
         */

        private int val;
        private String text;

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class EndDateBean {
        /**
         * val : 2020-10-30 11:16:28
         * text : 40
         */

        private String val;
        private int text;

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public int getText() {
            return text;
        }

        public void setText(int text) {
            this.text = text;
        }
    }

    public static class GroupformatBean {
        /**
         * id : 1
         * name : 首页轮播
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


//    /**
//     * id : 9
//     * pic_name : di1pin
//     * sort : 1
//     * link : http://101.200.169.236/index.php?s=/Admin/Login/checkname
//     * picture : http://101.200.169.236/Public/Uploads/Banner/5b0d26992b0f5.PNG
//     * group : one
//     * date : 1527860580
//     * end_date : 1530452580
//     */
//
//    private String id;
//    private String pic_name;
//    private String sort;
//    private String link;
//    private String picture;
//    private String group;
//    private int date;
//    private int end_date;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getPic_name() {
//        return pic_name;
//    }
//
//    public void setPic_name(String pic_name) {
//        this.pic_name = pic_name;
//    }
//
//    public String getSort() {
//        return sort;
//    }
//
//    public void setSort(String sort) {
//        this.sort = sort;
//    }
//
//    public String getLink() {
//        return link;
//    }
//
//    public void setLink(String link) {
//        this.link = link;
//    }
//
//    public String getPicture() {
//        return picture;
//    }
//
//    public void setPicture(String picture) {
//        this.picture = picture;
//    }
//
//    public String getGroup() {
//        return group;
//    }
//
//    public void setGroup(String group) {
//        this.group = group;
//    }
//
//    public int getDate() {
//        return date;
//    }
//
//    public void setDate(int date) {
//        this.date = date;
//    }
//
//    public int getEnd_date() {
//        return end_date;
//    }
//
//    public void setEnd_date(int end_date) {
//        this.end_date = end_date;
//    }
}
