package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created with Android Studio. User : Lim Email: lgmshare@gmail.com Datetime :
 * 2015/3/24 15:36 To change this template use File | Settings | File Templates.
 */
public class Advertisement implements Parcelable, Serializable {
    public final int TYPE_WEB = 1;
    public final int TYPE_WX_SMALL_APP = 3;
    /**
     *
     */
    private static final long serialVersionUID = -6837247854476675815L;
    private String image;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;


    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    private String cover;
    private int type;
    private int is_image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public int getPlay() {
        return play;
    }

    public void setPlay(int play) {
        this.play = play;
    }

    private int play;

    public int getIs_image() {
        return is_image;
    }

    public void setIs_image(int is_image) {
        this.is_image = is_image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {

        return image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeString(this.url);
        dest.writeString(this.name);
        dest.writeString(this.cover);
        dest.writeInt(this.type);
        dest.writeInt(this.is_image);
        dest.writeInt(this.play);
        dest.writeInt(this.id);
    }

    public Advertisement() {
    }

    protected Advertisement(Parcel in) {
        this.image = in.readString();
        this.url = in.readString();
        this.name = in.readString();
        this.cover = in.readString();
        this.type = in.readInt();
        this.is_image = in.readInt();
        this.play = in.readInt();
        this.id = in.readInt();
    }

    public static final Creator<Advertisement> CREATOR = new Creator<Advertisement>() {
        public Advertisement createFromParcel(Parcel source) {
            return new Advertisement(source);
        }

        public Advertisement[] newArray(int size) {
            return new Advertisement[size];
        }
    };

    public String toString() {
        return "image:" + image + ",url:" + url;
    }
}
