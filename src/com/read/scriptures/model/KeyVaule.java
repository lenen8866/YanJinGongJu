package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator.
 * Datetime: 2015/7/1.
 * Email: lgmshare@mgail.com
 */
public class KeyVaule implements Parcelable {
    private int id;
    private String key;
    private String value;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.key);
        dest.writeString(this.value);
    }

    public KeyVaule() {
    }

    private KeyVaule(Parcel in) {
        this.id = in.readInt();
        this.key = in.readString();
        this.value = in.readString();
    }

    public static final Creator<KeyVaule> CREATOR = new Creator<KeyVaule>() {
        public KeyVaule createFromParcel(Parcel source) {
            return new KeyVaule(source);
        }

        public KeyVaule[] newArray(int size) {
            return new KeyVaule[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
