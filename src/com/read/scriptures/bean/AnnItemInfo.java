package com.read.scriptures.bean;

public class AnnItemInfo {

    private boolean isCheck;
    private String title;
    private String content;

    public AnnItemInfo(boolean isCheck, String title, String content) {
        this.isCheck = isCheck;
        this.title = title;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
