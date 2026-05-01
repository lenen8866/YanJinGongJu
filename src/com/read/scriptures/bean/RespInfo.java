package com.read.scriptures.bean;

/**
 * Time: 2020/8/31
 * Author: a123
 * Description: 网络请求基础类
 */
public class RespInfo<T> {
    private int code;
    private String msg;
    private long time;
    private T data;

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
