package com.read.scriptures.bean;

public class IntroBean {
    private int id;
    private String intro;

    public IntroBean() {

    }

    public IntroBean(int id, String intro) {
        this.id = id;
        this.intro = intro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
