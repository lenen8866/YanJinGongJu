package com.read.scriptures.app;

import java.util.List;

public class PayVipBean {

    /**
     * code : 1
     * msg :
     * time : 1610423841
     * data : {"普通会员":[{"id":10,"status":1,"payments":"120.00","discount":"47.70","day":"180","vip_level":{"val":1,"text":"普通会员"}},{"id":9,"status":1,"payments":"60.00","discount":"27.70","day":"90","vip_level":{"val":1,"text":"普通会员"}},{"id":8,"status":1,"payments":"40.00","discount":"19.70","day":"60","vip_level":{"val":1,"text":"普通会员"}},{"id":7,"status":1,"payments":"20.00","discount":"0.01","day":"30","vip_level":{"val":1,"text":"普通会员"}},{"id":11,"status":1,"payments":"240.00","discount":"77.70","day":"360","vip_level":{"val":1,"text":"普通会员"}}]}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;
    public static class DataBean {
        public List<普通会员Bean> 普通会员;

        public static class 普通会员Bean {
            /**
             * id : 10
             * status : 1
             * payments : 120.00
             * discount : 47.70
             * day : 180
             * vip_level : {"val":1,"text":"普通会员"}
             */

            public String id;
            public int status;
            public String payments;
            public String discount;
            public String day;
            public VipLevelBean vip_level;
            public boolean selected;

            public static class VipLevelBean {
                /**
                 * val : 1
                 * text : 普通会员
                 */

                public int val;
                public String text;
            }
        }
    }
}
