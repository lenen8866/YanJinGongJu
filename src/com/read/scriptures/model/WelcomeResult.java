package com.read.scriptures.model;

import java.util.List;

public class WelcomeResult {

    /**
     * code : 1
     * msg :
     * time : 1600484515
     * data : []
     */

    private int code;
    private String msg;
    private String time;
    private List<ShouYeBean> data;

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

    public List<ShouYeBean> getData() {
        return data;
    }

    public void setData(List<ShouYeBean> data) {
        this.data = data;
    }
}
