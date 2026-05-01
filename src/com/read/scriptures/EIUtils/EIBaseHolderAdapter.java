//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class EIBaseHolderAdapter<T> extends EIBaseAdapter<T> {
    protected Context mContext;
    protected final int mItemLayoutId;

    public EIBaseHolderAdapter(Context var1, int var2) {
        super(var1);
        this.mContext = var1;
        this.mItemLayoutId = var2;
    }

    public EIBaseHolderAdapter(Context var1, List<T> var2, int var3) {
        super(var1, var2);
        this.mContext = var1;
        this.mItemLayoutId = var3;
    }

    private ViewHolder getViewHolder(int var1, View var2, ViewGroup var3) {
        return ViewHolder.get(this.mContext, var2, var3, this.mItemLayoutId, var1);
    }

    public abstract void convert(ViewHolder var1, T var2);

    public View getView(int var1, View var2, ViewGroup var3) {
        ViewHolder var4 = this.getViewHolder(var1, var2, var3);
        this.convert(var4, this.getItem(var1));
        return var4.getConvertView();
    }
}
