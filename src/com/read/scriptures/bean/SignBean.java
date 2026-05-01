package com.read.scriptures.bean;

import java.util.List;

public class SignBean {

    /**
     * code : 1
     * msg : 今天已打卡！
     * time : 1609753053
     * data : {"mark":2,"status":1,"week":[{"date":"2021-01-04","time":1609689600,"week":"周一","msg":"签到","status":1},{"date":"2021-01-05","time":1609776000,"week":"周二","msg":"未签到","status":0},{"date":"2021-01-06","time":1609862400,"week":"周三","msg":"未签到","status":0},{"date":"2021-01-07","time":1609948800,"week":"周四","msg":"未签到","status":0},{"date":"2021-01-08","time":1610035200,"week":"周五","msg":"未签到","status":0},{"date":"2021-01-09","time":1610121600,"week":"周六","msg":"未签到","status":0},{"date":"2021-01-10","time":1610208000,"week":"周日","msg":"未签到","status":0}]}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * mark : 2
         * status : 1
         * week : [{"date":"2021-01-04","time":1609689600,"week":"周一","msg":"签到","status":1},{"date":"2021-01-05","time":1609776000,"week":"周二","msg":"未签到","status":0},{"date":"2021-01-06","time":1609862400,"week":"周三","msg":"未签到","status":0},{"date":"2021-01-07","time":1609948800,"week":"周四","msg":"未签到","status":0},{"date":"2021-01-08","time":1610035200,"week":"周五","msg":"未签到","status":0},{"date":"2021-01-09","time":1610121600,"week":"周六","msg":"未签到","status":0},{"date":"2021-01-10","time":1610208000,"week":"周日","msg":"未签到","status":0}]
         */

        public int mark;
        public int status;
        public String prompt;
        public List<WeekBean> week;

        public static class WeekBean {
            /**
             * date : 2021-01-04
             * time : 1609689600
             * week : 周一
             * msg : 签到
             * status : 1
             */

            public String date;
            public int time;
            public String week;
            public String msg;
            public String prompt;
            public String num;
            public int status;
        }
    }
}
