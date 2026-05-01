package com.read.scriptures.bean;

public class VolumeBean {
    private String volumeId;
    private String path;
    private String categoryId;
    private String parentId;

    public VolumeBean(String volumeId, String path, String categoryId, String parentId) {
        this.volumeId = volumeId;
        this.path = path;
        this.categoryId = categoryId;
        this.parentId = parentId;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
