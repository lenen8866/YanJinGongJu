package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.zxl.common.db.annotation.Table;

/**
 * Created by Administrator.
 * Datetime: 2015/7/1.
 * Email: lgmshare@mgail.com
 */
@Table
public class Category implements Parcelable {

    private int id;
    private String cateName;
    private int volCount;
    private int parentId;
    private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public int getVolCount() {
        return volCount;
    }

    public void setVolCount(int volCount) {
        this.volCount = volCount;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Category() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.cateName);
        dest.writeInt(this.volCount);
        dest.writeInt(this.parentId);
        dest.writeString(this.path);
    }

    protected Category(Parcel in) {
        this.id = in.readInt();
        this.cateName = in.readString();
        this.volCount = in.readInt();
        this.parentId = in.readInt();
        this.path = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
