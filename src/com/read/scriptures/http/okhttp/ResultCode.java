package com.read.scriptures.http.okhttp;

/**
 * Time: 2020/8/31
 * Author: a123
 * Description:
 */
public enum ResultCode {

    KNET_SUCCESS(1,"成功"),
    KNET_ERROR(0,"服务器错误"),
    KNET_NETWORK_ERROR(-1,"网络错误"),
    KNET_GSON_EROR(-2,"解析错误")
    ;
    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
