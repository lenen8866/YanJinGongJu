package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.zxl.common.db.annotation.Table;
import com.zxl.common.db.annotation.Transient;

/**
 * Created by Administrator. Datetime: 2015/7/1. Email: lgmshare@mgail.com
 */
@Table(name = "chapter")
public class SearchTemp implements Parcelable {

    private String id;
    private String name;
    private int volumeId;
    private String volumeName;
    @Transient
    private int chapterCount;
    private int indexId;
    private String content;
    private int categoryId;
    private int parentId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(volumeId);
        dest.writeString(this.volumeName);
        dest.writeInt(this.chapterCount);
        dest.writeInt(this.indexId);
        dest.writeInt(this.categoryId);
        dest.writeInt(this.parentId);
    }

    public SearchTemp() {
    }

    private SearchTemp(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.volumeId = in.readInt();
        this.volumeName = in.readString();
        this.chapterCount = in.readInt();
        this.indexId = in.readInt();
        this.categoryId = in.readInt();
        this.parentId = in.readInt();
    }

    public static final Creator<SearchTemp> CREATOR = new Creator<SearchTemp>() {
        public SearchTemp createFromParcel(Parcel source) {
            return new SearchTemp(source);
        }

        public SearchTemp[] newArray(int size) {
            return new SearchTemp[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
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
}
