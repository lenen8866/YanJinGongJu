package com.read.scriptures.bean;

import com.read.scriptures.view.indexablerv.IndexableEntity;

public class AuthorTitleBean implements IndexableEntity {
    public String author;
    private String pinyin;

    @Override
    public String getFieldIndexBy() {
        return author;
    }

    @Override
    public void setFieldIndexBy(String indexField) {
        author=indexField;
    }

    @Override
    public void setFieldPinyinIndexBy(String pinyin) {
        this.pinyin = pinyin;
    }
}
