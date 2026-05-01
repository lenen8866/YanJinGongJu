package com.read.scriptures.bean;

import java.util.List;

public class QAConfigBean  {

    /**
     * code : 1
     * msg :
     * time : 1609246452
     * data : [{"name":"duration","value":["18","25","30"],"extend":"","tip":"答题时长/秒"},{"name":"unlock","value":["0","180","400"],"extend":"","tip":"等级解锁分数"},{"name":"frequency","value":5,"extend":"","tip":"答题次数/天"},{"name":"numAnswer","value":["15","30","50","100","150","200"],"extend":"","tip":"选择答题数量"},{"name":"fraction","value":["2","4","6"],"extend":"","tip":"分数设置/每题"}]
     */

    public int code;
    public String msg;
    public String time;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * name : duration
         * value : ["18","25","30"]
         * extend :
         * tip : 答题时长/秒
         */

        public String name;
        public String extend;
        public String tip;
        public Object value;
    }
}
