package com.read.scriptures.widget;

/** RTU参数 */
public class SelectDialogShowItem {

    /** 详细说明 */
    private String detail;
    /** 显示 */
    private String name;
    /** 是否显示左侧图片 */
    private boolean showLeft;
    /** 左侧图片R文件地址 */
    private int image = -1;
    /** 备用字段 */
    private String remark;
    /** 是否是文件夹 */
    private boolean isDirectory;
    /** 是否是要检索文件 */
    private boolean isSearch = false;

    public boolean isSearch() {
        return isSearch;
    }

    public void setSearch(boolean isSearch) {
        this.isSearch = isSearch;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public SelectDialogShowItem(String name) {
        this.name = name;
    }

    public SelectDialogShowItem(String detail, String name, int image, boolean isDirectory) {
        this.detail = detail;
        this.name = name;
        this.showLeft = true;
        this.image = image;
        this.isDirectory = isDirectory;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowLeft() {
        return showLeft;
    }

    public void setShowLeft(boolean showLeft) {
        this.showLeft = showLeft;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
