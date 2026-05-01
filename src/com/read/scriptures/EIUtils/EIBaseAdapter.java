//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

public abstract class EIBaseAdapter<T> extends BaseAdapter {
    protected final LayoutInflater mLayoutInflater;
    protected List<T> mList;

    public EIBaseAdapter(Context var1) {
        this(var1, new ArrayList());
    }

    public EIBaseAdapter(Context var1, List<T> var2) {
        if (var2 == null) {
            new IllegalArgumentException("not allows null paramater");
        }

        this.mLayoutInflater = (LayoutInflater)LayoutInflater.from(var1);
        this.mList = new ArrayList();
        this.mList.addAll(var2);
    }

    public void addItem(T var1) {
        this.mList.add(var1);
    }

    public void addList(List<T> var1) {
        this.mList.addAll(var1);
    }

    public int getCount() {
        return this.mList == null ? 0 : this.mList.size();
    }

    public T getItem(int var1) {
        return this.mList.get(var1);
    }

    public long getItemId(int var1) {
        return (long)var1;
    }

    public LayoutInflater getLayoutInflater() {
        return this.mLayoutInflater;
    }

    public List<T> getList() {
        return this.mList;
    }

    public void setList(List<T> var1) {
        this.mList.clear();
        this.mList.addAll(var1);
    }
}
