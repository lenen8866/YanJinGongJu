package com.read.scriptures.model;

import java.io.Serializable;
import java.util.List;

public class NewBannerBean implements Serializable {

    public int code;
    public String msg;
    public String time;
    public List<DataBean> data;

    public static class DataBean implements Serializable{

        public int id;
        public TypeBean type;
        public String pic_name;
        public int is_image;
        public String cover;
        public int play;
        public String link;
        public int group;
        public Object picture;
        public String date;
        public EndDateBean end_date;
        public String pic_url;
        public int weigh;
        public GroupformatBean groupformat;

        public static class TypeBean {

            public int val;
            public String text;
        }

        public static class EndDateBean {

            public String val;
            public int text;
        }

        public static class GroupformatBean {

            public int id;
            public String name;
        }
    }
}
