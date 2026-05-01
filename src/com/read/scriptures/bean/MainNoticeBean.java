package com.read.scriptures.bean;

import java.util.List;

public class MainNoticeBean {

    public int code;
    public String msg;
    public String time;
    public List<DataDTO> data;

    public static class DataDTO {

        public int id;
        public String title;
        public String code;
        public String content;
        public TypeDTO type;
        public String date;

        public static class TypeDTO {

            public int val;
            public String text;
        }
    }
}
