package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.read.scriptures.util.CharUtils;
import com.zxl.common.db.annotation.Table;
import com.zxl.common.db.annotation.Transient;

/**
 * Created by Administrator. Datetime: 2015/7/1. Email: lgmshare@mgail.com
 */
@Table(name = "volume")
public class Volume implements Parcelable {

    private int id;
    private String volName;
    private int chpCount;
    private int categoryId;
    private String updateTime;
    private String path;
    private int parentId;
    private String intro;
    private String introVideoAdd;

    @Transient
    private String pinyin;//拼音
    @Transient
    private String firstLetter;//拼音首字母

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVolName() {
        return volName;
    }

    public void setVolName(String volName) {
        this.volName = volName;
    }

    public int getChpCount() {
        return chpCount;
    }

    public void setChpCount(int chpCount) {
        this.chpCount = chpCount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getIntroVideoAdd() {
        return introVideoAdd;
    }

    public void setIntroVideoAdd(String introVideoAdd) {
        this.introVideoAdd = introVideoAdd;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    /**
     * 第一个字
     * @return
     */
    public String getHeader() {
        String header = "";
        if (getVolName().indexOf("{") != -1  && getVolName().indexOf("}") != -1){
            header = CharUtils.match("\\{(.*?)\\}", getVolName());
            if (header.trim().equals("{}")){
                if (getVolName().indexOf("[") != -1  && getVolName().indexOf("]") != -1){
                    header = CharUtils.match("\\[(.*?)\\]", getVolName());
                    if (header.trim().equals("[]")){
                        header = getVolName().substring(getVolName().indexOf("]")+1,getVolName().indexOf("]")+2);
                    }
                }else {
                    header = getVolName().substring(getVolName().indexOf("}")+1,getVolName().indexOf("}")+2);
                }
            }
        }else if (getVolName().indexOf("[") != -1  && getVolName().indexOf("]") != -1){
            header = CharUtils.match("\\[(.*?)\\]", getVolName());
            if (header.trim().equals("[]")){
                header = getVolName().substring(getVolName().indexOf("]")+1,getVolName().indexOf("]")+2);
            }
        }else{
            header = getVolName().substring(0,1);
        }

        header = header.replaceAll("\\{", "")
                .replaceAll("\\}", "")
                .replaceAll("\\[", "")
                .replaceAll("\\]", "");
        return header;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.volName);
        dest.writeInt(this.chpCount);
        dest.writeInt(this.categoryId);
        dest.writeString(this.updateTime);
        dest.writeString(this.path);
        dest.writeInt(this.parentId);
        dest.writeString(this.intro);
        dest.writeString(this.introVideoAdd);
        dest.writeString(this.pinyin);
        dest.writeString(this.firstLetter);
    }

    public Volume() {
    }

    protected Volume(Parcel in) {
        this.id = in.readInt();
        this.volName = in.readString();
        this.chpCount = in.readInt();
        this.categoryId = in.readInt();
        this.updateTime = in.readString();
        this.path = in.readString();
        this.parentId = in.readInt();
        this.intro = in.readString();
        this.introVideoAdd = in.readString();
        this.pinyin = in.readString();
        this.firstLetter = in.readString();
    }

    public static final Creator<Volume> CREATOR = new Creator<Volume>() {
        @Override
        public Volume createFromParcel(Parcel source) {
            return new Volume(source);
        }

        @Override
        public Volume[] newArray(int size) {
            return new Volume[size];
        }
    };




    public void pinyin(String name) {
        if (!TextUtils.isEmpty(name)) {
            String firstChar = name.charAt(0)+"";
            if(firstChar.matches("^[0-9]+") || firstChar.matches("#")){
                /**以数字和#开头的name都归于#*/
                pinyin = "zzzzzzzzzzzzzzzzzzz"+ firstChar;
                firstLetter = "#";
            }else {
                try {
                    this.pinyin = PinyinHelper.convertToPinyinString(name.trim(), "", PinyinFormat.WITHOUT_TONE);
                } catch (PinyinException e) {
                    e.printStackTrace();
                }
                if(!TextUtils.isEmpty(pinyin)){
                    char firstLetterChar = this.pinyin.toUpperCase().charAt(0);
                    firstLetter = firstLetterChar + "";
                    if (firstLetterChar < 'A' || firstLetterChar > 'Z') {
                        pinyin = "zzzzzzzzzzzzzzzzzzzz";
                        firstLetter = "#";
                    }
                }
            }

        } else {
            pinyin = "zzzzzzzzzzzzzzzzzzzz";
            firstLetter = "#";
        }
    }

    @Override
    public String toString() {
        return "Volume{" +
                "id=" + id +
                ", volName='" + volName + '\'' +
                ", chpCount=" + chpCount +
                ", categoryId=" + categoryId +
                ", updateTime='" + updateTime + '\'' +
                ", path='" + path + '\'' +
                ", parentId=" + parentId +
                ", intro='" + intro + '\'' +
                ", introVideoAdd='" + introVideoAdd + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", firstLetter='" + firstLetter + '\'' +
                '}';
    }
}
