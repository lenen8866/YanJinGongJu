package com.read.scriptures.bean;

public class PayOrderDetails {

    /**
     * pay_id : 1
     * username : 18548569999
     * goods : 18548569999--购买简黑体
     * goods_type : font
     * price : 1.00
     * cre_dt : 2020-01-07 12:38:48
     * pay_status : 1  0:待支付/1:支付成功/2:订单过期
     * success_dt : 2020-01-07 14:39:06
     * pay_type : wxpay
     * trade_no : J107719284332597
     */

    private int pay_id;
    private String username;
    private String goods;
    private String goods_type;
    private String price;
    private String cre_dt;
    private int pay_status;
    private String success_dt;
    private String pay_type;
    private String trade_no;
    private String json;

    public int getPay_id() {
        return pay_id;
    }

    public void setPay_id(int pay_id) {
        this.pay_id = pay_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCre_dt() {
        return cre_dt;
    }

    public void setCre_dt(String cre_dt) {
        this.cre_dt = cre_dt;
    }

    public int getPay_status() {
        return pay_status;
    }

    public void setPay_status(int pay_status) {
        this.pay_status = pay_status;
    }

    public String getSuccess_dt() {
        return success_dt;
    }

    public void setSuccess_dt(String success_dt) {
        this.success_dt = success_dt;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public boolean isSuccess(){
        return getPay_status() == 1;
    }
}
