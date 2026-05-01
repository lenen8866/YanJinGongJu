package com.read.scriptures.bean;

public class ActiveBean {

    /**
     * status : 2
     * info : 老用户，返回信息
     * maturity : 1536249600
     * register_dt : 1539352126
     */

    private int status;
    private String info;
    private String ip;
    private int maturity;
    private int register_dt;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getMaturity() {
        return maturity;
    }

    public void setMaturity(int maturity) {
        this.maturity = maturity;
    }

    public int getRegister_dt() {
        return register_dt;
    }

    public void setRegister_dt(int register_dt) {
        this.register_dt = register_dt;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
