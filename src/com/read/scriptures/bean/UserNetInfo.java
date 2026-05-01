package com.read.scriptures.bean;

import java.util.List;

public class UserNetInfo {
    /**
     * username : 1908643952
     * token : CB38ZoFFgOKNm6inR8HghuYJeffVOu4ITIkAnq1gi6/FFzgf3rdX4k0i7iAD47rL7unI3g0XX/zS9t2TKgN3j4cqGvuA
     * access_token : 37_1yickFN7HJGOz8rZByfJBx797FTRUeP57UqZcx3H-5UqcTtpEUbvDSEbTMRhjUG57o1BzUP5HvODagzwKCQgx2esW80uEYiZmssuBcog_JI
     * refresh_token : 37_Or1OhjVYIMoWDZFOFlPAgeAHgo4S0VcEFahTBgr84-wXe6FjzDoyG4x8LCXDBWBv-su_2-k6xzyN5SWNwDsNNsCNXEZQSTb6FWHwS3b3-xk
     * openid : oBz-wxDNz-qPU6WycDF_6ZMuMouo
     * level : [{"val":"v3","text":"超级会员","expire":"1601384546","time":"2020-09-29 21:02:26","format":"3天0小时0分0秒"}]
     */

    private String username;
    private String token;
    private String access_token;
    private String refresh_token;
    private String openid;
    private String sequence;
    private long create_time;
    private List<LevelBean> level;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String version;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public List<LevelBean> getLevel() {
        return level;
    }

    public void setLevel(List<LevelBean> level) {
        this.level = level;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public static class LevelBean {
        /**
         * val : v3
         * text : 超级会员
         * expire : 1601384546
         * time : 2020-09-29 21:02:26
         * format : 3天0小时0分0秒
         */

        private String val;
        private String text;
        private long expire;
        private String time;
        private String format;
        private String version;

        @Override
        public String toString() {
            return "version:" + version;
        }

        public String getVal() {
            return val == null ? "" : val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public String getText() {
            return text == null ? "" : text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public long getExpire() {
            return expire;
        }

        public void setExpire(long expire) {
            this.expire = expire;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }
}
