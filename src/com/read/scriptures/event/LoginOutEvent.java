package com.read.scriptures.event;

/**
 * Time: 2020/9/28
 * Author: a123
 * Description:
 */
public class LoginOutEvent {
    boolean isTokenInvalid;
    String errMsg;

    public LoginOutEvent(boolean isTokenInvalid,String errMsg) {
        this.isTokenInvalid = isTokenInvalid;
        this.errMsg = errMsg;
    }

    public boolean isTokenInvalid() {
        return isTokenInvalid;
    }

    public void setTokenInvalid(boolean tokenInvalid) {
        isTokenInvalid = tokenInvalid;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
