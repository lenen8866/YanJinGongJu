//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
    private View mConvertView;
    private int mPosition;
    private final SparseArray<View> mViews;

    private ViewHolder(Context var1, ViewGroup var2, int var3, int var4) {
        this.mPosition = var4;
        this.mViews = new SparseArray();
        this.mConvertView = LayoutInflater.from(var1).inflate(var3, var2, false);
        this.mConvertView.setTag(this);
    }

    public static ViewHolder get(Context var0, View var1, ViewGroup var2, int var3, int var4) {
        if (var1 == null) {
            return new ViewHolder(var0, var2, var3, var4);
        } else {
            ViewHolder var5 = (ViewHolder)var1.getTag();
            var5.mPosition = var4;
            return var5;
        }
    }

    public View getConvertView() {
        return this.mConvertView;
    }

    public int getPosition() {
        return this.mPosition;
    }

    public <T extends View> T getView(int var1) {
        View var2 = (View)this.mViews.get(var1);
        if (var2 == null) {
            var2 = this.mConvertView.findViewById(var1);
            this.mViews.put(var1, var2);
        }

        return (T) var2;
    }

    public ViewHolder setImageBitmap(int var1, Bitmap var2) {
        ((ImageView)this.getView(var1)).setImageBitmap(var2);
        return this;
    }

    public ViewHolder setImageResource(int var1, int var2) {
        ((ImageView)this.getView(var1)).setImageResource(var2);
        return this;
    }

    public ViewHolder setText(int var1, int var2) {
        ((TextView)this.getView(var1)).setText(var2);
        return this;
    }

    public ViewHolder setText(int var1, String var2) {
        ((TextView)this.getView(var1)).setText(var2);
        return this;
    }

    public ViewHolder setViewBackground(int var1, int var2) {
        this.getView(var1).setBackgroundResource(var2);
        return this;
    }
}
