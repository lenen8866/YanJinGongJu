//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.GridView;

public class ExpandGridView extends GridView {
    private boolean haveScrollbar = false;

    public ExpandGridView(Context var1) {
        super(var1);
    }

    public ExpandGridView(Context var1, AttributeSet var2) {
        super(var1, var2);
    }

    public ExpandGridView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
    }

    protected void onMeasure(int var1, int var2) {
        if (!this.haveScrollbar) {
            super.onMeasure(var1, MeasureSpec.makeMeasureSpec(536870911, -2147483648));
        } else {
            super.onMeasure(var1, var2);
        }
    }

    public void setHaveScrollbar(boolean var1) {
        this.haveScrollbar = var1;
    }
}
