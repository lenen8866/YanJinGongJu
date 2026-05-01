package com.read.scriptures.bean;

public class AlipayBean {

    /**
     * code : 1
     * msg : 下单成功
     * data : alipay_sdk=alipay-sdk-php-20161101&app_id=2018062760451218&biz_content=%7B%22body%22%3A%22%E8%B4%AD%E4%B9%B0%E9%98%85%E8%AF%BB%E6%97%B6%E9%97%B430%E5%A4%A9%22%2C%22subject%22%3A%22%E8%B4%AD%E4%B9%B0%E9%98%85%E8%AF%BB%E6%97%B6%E9%97%B4%22%2C%22out_trade_no%22%3A%2213%22%2C%22timeout_express%22%3A%2290mm%22%2C%22total_amount%22%3A%220.01%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%22passback_params%22%3A%2230%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F101.200.169.236%2Findex.php%2Fhome%2Forder%2Falipay_callback&sign_type=RSA2&timestamp=2018-09-21+23%3A59%3A40&version=1.0&sign=YleN2ewaN%2FsLe0hU0y2A6TGeDLkvj311p1amqBXdYS3gR%2BNRGcZaBRJp5yUBwbQesMtu%2FIItKa5bJipI9L2SM%2BcvaC9VfqmbRDuu%2FdJIawo5YujmWN8Po305slXt2UIG%2F8wXZvaykdjDudHC%2B%2Fintqp8b9RsoKoea28yT1qAkoGxz35NDzMe%2Fj%2FIkfjWPbRBR3RXugMI1DqjZZQIfq5uq1zFbaPTwmX1OdwF8TUIGgHGrzfjoEDImgDtON%2Ff%2FyG4E881Btekpag0bOrCo%2FOvz08bfJ4hDB2lo6azLUYMcyCLkpFnXd2rGHnoHHUHOe1LsVyEqzE%2BLCfaa0iMHvt6yQ%3D%3D
     */

    private int code;
    private String msg;
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
