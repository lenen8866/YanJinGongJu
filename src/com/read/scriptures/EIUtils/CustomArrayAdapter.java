//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    public CustomArrayAdapter(Context var1, int var2, int var3, List<String> var4) {
        super(var1, var2, var3, var4);
    }

    public CustomArrayAdapter(Context var1, int var2, int var3, String[] var4) {
        super(var1, var2, var3, var4);
    }

    public CustomArrayAdapter(Context var1, int var2, List<String> var3) {
        super(var1, var2, var3);
    }

    public CustomArrayAdapter(Context var1, int var2, String[] var3) {
        super(var1, var2, var3);
    }

    public View getView(int var1, View var2, ViewGroup var3) {
        return super.getView(var1, var2, var3);
    }
}
