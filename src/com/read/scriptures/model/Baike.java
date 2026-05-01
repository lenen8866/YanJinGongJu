package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.zxl.common.db.annotation.Table;
import com.zxl.common.db.annotation.Transient;

@Table(name = "baike")
public class Baike implements Parcelable {
    private int id;
    private String name;
    private int indexId;
    private String content;
    private int categoryId;
    @Transient
    private String pinyin;//拼音
    @Transient
    private String firstLetter;//拼音首字母

    public String getShowCateName() {
        return cateName;
    }

    public String getCateName() {
        return cateName.replaceAll("^\\d{1,}-", "");
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    private String cateName;


    public String getFirstLetter() {
        if(TextUtils.isEmpty(firstLetter)) {
            pinyin(name);
        }
        return firstLetter;
    }


    public static final Creator<Baike> CREATOR = new Creator<Baike>() {
        public Baike createFromParcel(Parcel source) {
            return new Baike(source);
        }

        public Baike[] newArray(int size) {
            return new Baike[size];
        }
    };

    public Baike() {

    }

    public Baike(int id, String name, int indexId, String content, int categoryId, String cateName) {
        this.id = id;
        this.name = name;
        this.indexId = indexId;
        this.content = content;
        this.categoryId = categoryId;
        this.cateName = cateName;
    }

    private Baike(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.indexId = in.readInt();
        this.content = in.readString();
        this.categoryId = in.readInt();
        this.cateName = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShowName() {
        return name.replaceAll("^\\d{1,}-", "").replaceAll("\\(.*\\)", "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.indexId);
        dest.writeString(this.content);
        dest.writeInt(this.categoryId);
        dest.writeString(this.cateName);
    }

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
}