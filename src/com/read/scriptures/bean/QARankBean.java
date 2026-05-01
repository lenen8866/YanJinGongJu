package com.read.scriptures.bean;

import java.util.List;

public class QARankBean {

    /**
     * code : 1
     * msg :
     * time : 1609294153
     * data : {"user":{"rank":"1","msg":"当前排名第1名","score":"200"},"rank":[{"id":4,"username":"1908644918","sequence":"oBz-wxIgeraAGdjcDSSqfayosryI","avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/EXRkfunOwDF6UmnqF1a7iblXszUXEV4BicsNLsQhPWe3m7QlIyR40DDxSFdMFnibUwFbWI1Nmic2z8WOfBiaEHibY5Rg/132","nickname":"侧身","num":215,"score":"200"},{"id":1,"username":"1908643960","sequence":"aaaaa1111111111","avatar":"","nickname":"","num":14,"score":"38"},{"id":156,"username":"1908644226","sequence":"oBz-wxAl4kDzArdbE-LwlmeY7tpA","avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/yicwAG7FcGUXH0fvquW6ZacF7fHrnwe1LrTEqBl5LDwErnvA5QDKumbZhhG2F4lkibnHS8pXbjHTB96c3icpqXGDQ/132","nickname":"服侍之美（王敏莲）","num":10,"score":"26"},{"id":149,"username":"1908645900","sequence":"oBz-wxHczMJ9feuTUrMugOzgsP4E","avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJCu8kWGqJEdgZ5J3Hbq1434XtL73Hf4p5eaOYXic6ibQQrp7VcqQdTViasxPKuC5DTJE1FY4MWH4YRg/132","nickname":"ml&  行者","num":22,"score":"12"}]}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * user : {"rank":"1","msg":"当前排名第1名","score":"200"}
         * rank : [{"id":4,"username":"1908644918","sequence":"oBz-wxIgeraAGdjcDSSqfayosryI","avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/EXRkfunOwDF6UmnqF1a7iblXszUXEV4BicsNLsQhPWe3m7QlIyR40DDxSFdMFnibUwFbWI1Nmic2z8WOfBiaEHibY5Rg/132","nickname":"侧身","num":215,"score":"200"},{"id":1,"username":"1908643960","sequence":"aaaaa1111111111","avatar":"","nickname":"","num":14,"score":"38"},{"id":156,"username":"1908644226","sequence":"oBz-wxAl4kDzArdbE-LwlmeY7tpA","avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/yicwAG7FcGUXH0fvquW6ZacF7fHrnwe1LrTEqBl5LDwErnvA5QDKumbZhhG2F4lkibnHS8pXbjHTB96c3icpqXGDQ/132","nickname":"服侍之美（王敏莲）","num":10,"score":"26"},{"id":149,"username":"1908645900","sequence":"oBz-wxHczMJ9feuTUrMugOzgsP4E","avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJCu8kWGqJEdgZ5J3Hbq1434XtL73Hf4p5eaOYXic6ibQQrp7VcqQdTViasxPKuC5DTJE1FY4MWH4YRg/132","nickname":"ml&  行者","num":22,"score":"12"}]
         */

        public UserBean user;
        public List<RankBean> rank;

        public static class UserBean {
            /**
             * rank : 1
             * msg : 当前排名第1名
             * score : 200
             */

            public String rank;
            public String msg;
            public String avatar;
            public String score;
            public String nickname;
            public long submitTime;
        }

        public static class RankBean {
            /**
             * id : 4
             * username : 1908644918
             * sequence : oBz-wxIgeraAGdjcDSSqfayosryI
             * avatar : https://thirdwx.qlogo.cn/mmopen/vi_32/EXRkfunOwDF6UmnqF1a7iblXszUXEV4BicsNLsQhPWe3m7QlIyR40DDxSFdMFnibUwFbWI1Nmic2z8WOfBiaEHibY5Rg/132
             * nickname : 侧身
             * num : 215
             * score : 200
             */

            public int id;
            public String username;
            public String sequence;
            public String avatar;
            public String nickname;
            public int num;
            public String score;
            public long submitTime;
        }
    }
}
