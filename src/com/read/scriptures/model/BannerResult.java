package com.read.scriptures.model;

import java.util.List;

public class BannerResult {

    /**
     * code : 1
     * msg :
     * time : 1600497637
     * data : []
     */

    private int code;
    private String msg;
    private String time;
    private List<BannerBean> data;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<BannerBean> getData() {
        return data;
    }

    public void setData(List<BannerBean> data) {
        this.data = data;
    }
}
