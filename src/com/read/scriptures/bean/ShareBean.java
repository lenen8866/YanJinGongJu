package com.read.scriptures.bean;

public class ShareBean {

    /**
     * open : true
     * logo : http://101.200.169.236/my/imglogo.png
     * title : app软件分享
     * body : 亲爱的弟兄姐妹，当您使用我们的软件的同时，还有更多的人不知道本软件的好处，我们很希望您将本软件告知你身边的朋友，你可以通过以下方式进行分享。。
     * week : 1
     * Button1 : 1
     * Button_WeChat_friend : 1
     * Button_WeChat_friends : 1
     * close : 1
     * Switch : 1
     * friends_content : 这个研经工具软件非常好用，为此我特别介绍我的朋友过来下载！
     * url : https://android.myapp.com/myapp/detail.htm?apkName=com.read.scriptures&ADTAG=mobile
     */

    private String open;
    private String img_logo;
    private String title;
    private String body;
    private String week;
    private String Button_web;
    private String Button_WeChat_friend;
    private String Button_WeChat_friends;
    private String close;
    private String friends_content;
    private String url;
    private String wx_mini_app_id;
    private String wx_mini_app_path;

    public String getOpen() {
        if ("1".equals(open)){
            open = "true";
        }else{
            open = "false";
        }
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getImg_logo() {
        return img_logo;
    }

    public void setImg_logo(String img_logo) {
        this.img_logo = img_logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getButton_web() {
        return Button_web;
    }

    public void setButton_web(String button_web) {
        Button_web = button_web;
    }

    public String getButton_WeChat_friend() {
        return Button_WeChat_friend;
    }

    public void setButton_WeChat_friend(String Button_WeChat_friend) {
        this.Button_WeChat_friend = Button_WeChat_friend;
    }

    public String getButton_WeChat_friends() {
        return Button_WeChat_friends;
    }

    public void setButton_WeChat_friends(String Button_WeChat_friends) {
        this.Button_WeChat_friends = Button_WeChat_friends;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getFriends_content() {
        return friends_content;
    }

    public void setFriends_content(String friends_content) {
        this.friends_content = friends_content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWx_mini_app_id() {
        return wx_mini_app_id;
    }

    public void setWx_mini_app_id(String wx_mini_app_id) {
        this.wx_mini_app_id = wx_mini_app_id;
    }

    public String getWx_mini_app_path() {
        return wx_mini_app_path;
    }

    public void setWx_mini_app_path(String wx_mini_app_path) {
        this.wx_mini_app_path = wx_mini_app_path;
    }
}
