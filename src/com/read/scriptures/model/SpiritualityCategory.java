package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.zxl.common.db.annotation.Table;

@Table(name = "spirituality_category")
public class SpiritualityCategory implements Parcelable {
    private int id;
    private String cateName;
    private int volCount;
    private int parentId;
    private String updateTime;

    public SpiritualityCategory() {

    }

    public SpiritualityCategory(int id, String cateName, int volCount, int parentId, String updateTime) {
        this.id = id;
        this.cateName = cateName;
        this.volCount = volCount;
        this.parentId = parentId;
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShowCateName() {
        return cateName.replaceAll("^\\d{1,}-", "");
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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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
        dest.writeString(this.updateTime);
    }

    protected SpiritualityCategory(Parcel in) {
        this.id = in.readInt();
        this.cateName = in.readString();
        this.volCount = in.readInt();
        this.parentId = in.readInt();
        this.updateTime = in.readString();
    }

    public static final Creator<SpiritualityCategory> CREATOR = new Creator<SpiritualityCategory>() {
        @Override
        public SpiritualityCategory createFromParcel(Parcel source) {
            return new SpiritualityCategory(source);
        }

        @Override
        public SpiritualityCategory[] newArray(int size) {
            return new SpiritualityCategory[size];
        }
    };
}
