package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.zxl.common.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by LGM. Datetime: 2015/7/11. Email: lgmshare@mgail.com
 */
@Table(name = "spirituality")
public class Spirituality implements Parcelable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2699500033826604808L;
    private int id;
    private String daytime;
    private String book;
    private String name;
    private String content;
    private String path;
    private String patrent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDaytime() {
        return daytime;
    }

    public void setDaytime(String daytime) {
        this.daytime = daytime;
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

    public String getShowBook() {
        return book != null ? book.replaceAll("^\\d{1,}-", "") : book;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String toString() {
        return daytime + "" + ":" + name + ":" + book;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPatrent() {
        return patrent;
    }

    public void setPatrent(String patrent) {
        this.patrent = patrent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.daytime);
        dest.writeString(this.book);
        dest.writeString(this.name);
        dest.writeString(this.content);
        dest.writeString(this.path);
    }

    public Spirituality() {
    }

    public Spirituality(int id, String daytime, String book, String name, String content, String patrent) {
        this.id = id;
        this.daytime = daytime;
        this.book = book;
        this.name = name;
        this.content = content;
        this.patrent = patrent;
    }

    protected Spirituality(Parcel in) {
        this.id = in.readInt();
        this.daytime = in.readString();
        this.book = in.readString();
        this.name = in.readString();
        this.content = in.readString();
        this.path = in.readString();
    }

    public static final Creator<Spirituality> CREATOR = new Creator<Spirituality>() {
        @Override
        public Spirituality createFromParcel(Parcel source) {
            return new Spirituality(source);
        }

        @Override
        public Spirituality[] newArray(int size) {
            return new Spirituality[size];
        }
    };
}
