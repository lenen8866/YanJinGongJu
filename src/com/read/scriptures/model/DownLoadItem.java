package com.read.scriptures.model;

import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class DownLoadItem {

    private String bookName;
    private String category;
    private int count;
    private String bookcode;
    private File file;
    private String id;
    private String introduction;
    private boolean isNew;
    private int oldCount;
    private int progress;

    private ProgressBar progressBar;

    private TextView tvProgress;

    private int secondaryProgress;
    private boolean showIn;
    private String size;

    private long sizeValue;

    private int state;

    private String time;

    private String title;

    private String type;

    private String url;


    public String getBookName() {
        return bookName;
    }

    public String getCategory() {
        return category;
    }

    public int getCount() {
        return count;
    }

    public File getFile() {
        return file;
    }

    public String getId() {
        return id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public int getOldCount() {
        return oldCount;
    }

    public int getProgress() {
        return progress;
    }


    public int getSecondaryProgress() {
        return secondaryProgress;
    }

    public String getSize() {
        return size;
    }

    public long getSizeValue() {
        return sizeValue;
    }

    public int getState() {
        return state;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean isShowIn() {
        return showIn;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setOldCount(int oldCount) {
        this.oldCount = oldCount;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public TextView getTvProgress() {
        return tvProgress;
    }

    public void setTvProgress(TextView tvProgress) {
        this.tvProgress = tvProgress;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setSecondaryProgress(int secondaryProgress) {
        this.secondaryProgress = secondaryProgress;
    }

    public void setShowIn(boolean showIn) {
        this.showIn = showIn;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setSizeValue(long sizeValue) {
        this.sizeValue = sizeValue;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBookcode() {
        return bookcode;
    }

    public void setBookcode(String bookcode) {
        this.bookcode = bookcode;
    }
}
