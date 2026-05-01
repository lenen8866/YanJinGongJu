package com.read.scriptures.bean;

import com.google.gson.annotations.SerializedName;

public class RechargeTipData {

    /**
     * code : 1
     * msg :
     * time : 1609928293
     * data : {"appid":"wxd8e57656faf5d7e0","noncestr":"aCoFTaHsmww0I5ic22tjy6oZLChgv13J","package":"Sign=WXPay","partnerid":"1600909086","prepayid":"wx06181814125132bad3611f4a6470c20000","timestamp":1609928294,"sign":"A6FD4DA297B6DCB484435821F2026581"}
     */

    public int code;
    public String msg;
    public String time;
    public DataBean data;

    public static class DataBean {
        /**
         * appid : wxd8e57656faf5d7e0
         * noncestr : aCoFTaHsmww0I5ic22tjy6oZLChgv13J
         * package : Sign=WXPay
         * partnerid : 1600909086
         * prepayid : wx06181814125132bad3611f4a6470c20000
         * timestamp : 1609928294
         * sign : A6FD4DA297B6DCB484435821F2026581
         */

        public String appid;
        public String noncestr;
        @SerializedName("package")
        public String packageX;
        public String partnerid;
        public String prepayid;
        public String timestamp;
        public String sign;
        public String json;
    }
}
