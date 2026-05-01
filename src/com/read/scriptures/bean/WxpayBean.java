package com.read.scriptures.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class WxpayBean {

    /**
     * code : 1
     * msg :
     * data : {"appid":"wx3c1726b5a53b8155","partnerid":"1513734801","prepayid":"wx28102355918042c76566be8e0938921949","noncestr":"RRTE4345233632408","timestamp":"1538101435","package":"Sign=WXPay","sign":"0B6660F8CFE7B0DA31D4ED1FD0D7DACF"}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * appid : wx3c1726b5a53b8155
         * partnerid : 1513734801
         * prepayid : wx28102355918042c76566be8e0938921949
         * noncestr : RRTE4345233632408
         * timestamp : 1538101435
         * package : Sign=WXPay
         * sign : 0B6660F8CFE7B0DA31D4ED1FD0D7DACF
         */

        private String appid;
        private String partnerid;
        private String prepayid;
        private String noncestr;
        private String timestamp;
        @JSONField(name = "package")
        private String packageX;
        private String sign;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
}
