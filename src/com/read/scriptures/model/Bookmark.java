package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.read.scriptures.util.DateUtils;
import com.read.scriptures.util.SearchTextUtil;
import com.zxl.common.db.annotation.Table;

/**
 * Created by Administrator. Datetime: 2015/7/3. Email: lgmshare@mgail.com
 */
@Table(name = "Bbookmark")
public class Bookmark implements Parcelable {

    private int id;
    private int volumeId;
    private String volumeName;
    private String chapterName;
    private String chapterFileName;
    private int chapterCount;
    private String content;
    private String description;
    private int type;
    private String createTime;
    private String categroyId;
    private String categroyName;

    private int index;
    private int chapterIndexId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategroyId() {
        return categroyId;
    }

    public void setCategroyId(String categroyId) {
        this.categroyId = categroyId;
    }

    public String getCategroyName() {
        return categroyName;
    }

    public void setCategroyName(String categroyName) {
        this.categroyName = categroyName;
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

    public int getChapterIndexId() {
        return chapterIndexId;
    }

    public void setChapterIndexId(int chapterId) {
        this.chapterIndexId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterFileName() {
        return chapterFileName;
    }

    public void setChapterFileName(String chapterFileName) {
        this.chapterFileName = chapterFileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String itContent) {
        this.content = itContent;
    }

    public String getReplaceContent() {
        String result = SearchTextUtil.replaceTag("<.+?>", content);
        result = result.replace("&lt;", "<");
        result = result.replace("&gt;", ">");
        return result;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateTime() {
        if (createTime == null || createTime.length() == 0){
           createTime = DateUtils.currentDateTimeString();
        }
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Bookmark() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.categroyId);
        dest.writeString(this.categroyName);
        dest.writeInt(this.volumeId);
        dest.writeString(this.volumeName);
        dest.writeInt(this.chapterIndexId);
        dest.writeString(this.chapterName);
        dest.writeString(this.chapterFileName);
        dest.writeInt(this.chapterCount);
        dest.writeString(this.content);
        dest.writeInt(this.index);
        dest.writeString(this.description);
        dest.writeInt(this.type);
        dest.writeString(this.createTime);
    }

    private Bookmark(Parcel in) {
        this.id = in.readInt();
        this.categroyId = in.readString();
        this.categroyName = in.readString();
        this.volumeId = in.readInt();
        this.volumeName = in.readString();
        this.chapterIndexId = in.readInt();
        this.chapterName = in.readString();
        this.chapterFileName = in.readString();
        this.chapterCount = in.readInt();
        this.content = in.readString();
        this.index = in.readInt();
        this.description = in.readString();
        this.type = in.readInt();
        this.createTime = in.readString();
    }

    public static final Creator<Bookmark> CREATOR = new Creator<Bookmark>() {
        public Bookmark createFromParcel(Parcel source) {
            return new Bookmark(source);
        }

        public Bookmark[] newArray(int size) {
            return new Bookmark[size];
        }
    };
}
