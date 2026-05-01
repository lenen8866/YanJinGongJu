package com.read.scriptures.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CollectBean implements Parcelable {

    private String chapter;
    private int volumeId;
    private String volumeName;
    private int chapterCount;
    private int indexId;
    private String content;
    private int categoryId;
    private int parentId;
    private String parentPath;
    private String time;
    private int topIndex;//置顶标记，默认不置顶0

    public CollectBean(String chapter, int volumeId, String volumeName, int chapterCount, int indexId, String content, int categoryId, int parentId, String parentPath, String time, int topIndex) {
        this.chapter = chapter;
        this.volumeId = volumeId;
        this.volumeName = volumeName;
        this.chapterCount = chapterCount;
        this.indexId = indexId;
        this.content = content;
        this.categoryId = categoryId;
        this.parentId = parentId;
        this.parentPath = parentPath;
        this.time = time;
        this.topIndex = topIndex;
    }

    public CollectBean() {
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public int getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(int volumeId) {
        this.volumeId = volumeId;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTopIndex() {
        return topIndex;
    }

    public void setTopIndex(int topIndex) {
        this.topIndex = topIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.chapter);
        dest.writeInt(this.volumeId);
        dest.writeString(this.volumeName);
        dest.writeInt(this.chapterCount);
        dest.writeInt(this.indexId);
        dest.writeString(this.content);
        dest.writeInt(this.categoryId);
        dest.writeInt(this.parentId);
        dest.writeString(this.parentPath);
        dest.writeString(this.time);
        dest.writeInt(this.topIndex);
    }

    protected CollectBean(Parcel in) {
        this.chapter = in.readString();
        this.volumeId = in.readInt();
        this.volumeName = in.readString();
        this.chapterCount = in.readInt();
        this.indexId = in.readInt();
        this.content = in.readString();
        this.categoryId = in.readInt();
        this.parentId = in.readInt();
        this.parentPath = in.readString();
        this.time = in.readString();
        this.topIndex = in.readInt();
    }

    public static final Creator<CollectBean> CREATOR = new Creator<CollectBean>() {
        @Override
        public CollectBean createFromParcel(Parcel source) {
            return new CollectBean(source);
        }

        @Override
        public CollectBean[] newArray(int size) {
            return new CollectBean[size];
        }
    };
}
