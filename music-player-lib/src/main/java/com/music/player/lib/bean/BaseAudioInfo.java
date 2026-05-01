package com.music.player.lib.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * hty_Yuye@Outlook.com
 * 2019/3/5
 * Music java Bean
 * 带 * 标识的成员为必须赋值类型成员
 */

public class BaseAudioInfo implements Serializable {
    public long id;
    public String cate_id;
    public String chapter;
    public boolean isSelected;

    public String userid;
    public String avatar;
    public String sort;

    @Override
    public String toString() {
        return "BaseAudioInfo{" +
                "id=" + id +
//                ", cate_id='" + cate_id + '\'' +
                ", chapter='" + chapter + '\'' +
//                ", avatar='" + avatar + '\'' +
//                ", collect=" + collect +
                ", author='" + author + '\'' +
                ", image='" + image + '\'' +
                ", audio_cover='" + audio_cover + '\'' +
//                ", audio_url='" + audio_url + '\'' +
                ", audio_name='" + audio_name + '\'' +
                ", audio_lyric='" + audio_lyric + '\'' +
                ", duration='" + duration + '\'' +
//                ", cate1='" + cate1 + '\'' +
//                ", cate2='" + cate2 + '\'' +
//                ", cate3='" + cate3 + '\'' +
                ", cate1_name='" + cate1_name + '\'' +
                ", cate2_name='" + cate2_name + '\'' +
                ", cate3_name='" + cate3_name + '\'' +
//                ", content='" + content + '\'' +
                ", cate1_id=" + cate1_id +
                ", cate2_id=" + cate2_id +
                ", cate3_id=" + cate3_id +
                ", isCached=" + isCached +
                ", playDuration=" + playDuration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseAudioInfo)) return false;
        if (!super.equals(o)) return false;
        BaseAudioInfo itemBean = (BaseAudioInfo) o;
        return Objects.equals(id, itemBean.id) &&
                Objects.equals(cate_id, itemBean.cate_id) &&
                Objects.equals(author, itemBean.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, cate_id, author);
    }

    public String audioHashKey;
    public int collect;

    public String author;
    public String image;
    public String audio_cover;
    public String audio_url;
    public String audio_name;
    public String audio_lyric;
    public String duration;
    public String playDuration;
    public String cate1;
    public String cate2;
    public String cate3;
    public String cate1_name;
    public String cate2_name;
    public String cate3_name;
    public String content;
    public String cate1_id;
    public int cate2_id;
    public int cate3_id;
    public boolean isCached;
}