package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.read.scriptures.util.StringUtil;
import com.zxl.common.db.annotation.Table;
import com.zxl.common.db.annotation.Transient;

/**
 * Created by Administrator. Datetime: 2015/7/1. Email: lgmshare@mgail.com
 */
@Table(name = "chapter")
public class Chapter implements Parcelable {

    private String id;
    private String name;
    private int volumeId;
    @Transient
    private String volumeName;
    @Transient
    private int chapterCount;
    private int indexId;
    private String content;
    private int categoryId;
    private int parentId;
    private String parentPath;

    //真实index
    private int chapterIndex;

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

    public Chapter() {
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShowName() {
        return name != null ? name.replaceAll("^\\d{1,}-", "") : name;
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

    public String getShowVolumeName() {
        if (!TextUtils.isEmpty(volumeName)){
            String size = StringUtil.match("\\{.*?\\}",volumeName);
            if (!TextUtils.isEmpty(size) && size.length() <= 2){
                return volumeName != null ? volumeName.replaceAll("^\\d{1,}-", "").replace("{","").replace("}","") : volumeName;
            }else {
                return volumeName != null ? volumeName.replaceAll("^\\d{1,}-", "") : volumeName;
            }
        }else {
            return volumeName;
        }

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

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.volumeId);
        dest.writeString(this.volumeName);
        dest.writeInt(this.chapterCount);
        dest.writeInt(this.indexId);
        dest.writeString(this.content);
        dest.writeInt(this.categoryId);
        dest.writeInt(this.parentId);
        dest.writeString(this.parentPath);
        dest.writeInt(this.chapterIndex);
    }

    protected Chapter(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.volumeId = in.readInt();
        this.volumeName = in.readString();
        this.chapterCount = in.readInt();
        this.indexId = in.readInt();
        this.content = in.readString();
        this.categoryId = in.readInt();
        this.parentId = in.readInt();
        this.parentPath = in.readString();
        this.chapterIndex = in.readInt();
    }

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel source) {
            return new Chapter(source);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };
}
