package com.read.scriptures.bean;

public class CategoryBean {
    private String id;
    private String path;
    private String parentId;

    public CategoryBean(String id, String path, String parentId) {
        this.id = id;
        this.path = path;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
