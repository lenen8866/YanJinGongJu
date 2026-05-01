//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

public class TextStyleUtil {
    private SpannableString ss;

    public TextStyleUtil() {
    }

    public TextStyleUtil(String var1) {
        this.ss = new SpannableString(var1);
    }

    public static void setFakeBold(TextView var0, boolean var1) {
        var0.getPaint().setFakeBoldText(var1);
    }

    public SpannableString getSpannableString() {
        return this.ss;
    }

    @TargetApi(5)
    public TextStyleUtil setAbsoluteSizeSpan(int var1, int var2, int var3, boolean var4) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new AbsoluteSizeSpan(var1, var4), var2, var3, 33);
            return this;
        }
    }

    public TextStyleUtil setBackgroundColorSpan(int var1, int var2, int var3) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new BackgroundColorSpan(var1), var2, var3, 33);
            return this;
        }
    }

    public TextStyleUtil setBackgroundColorSpan(String var1, int var2, int var3) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new BackgroundColorSpan(Color.parseColor(var1)), var2, var3, 33);
            return this;
        }
    }

    public TextStyleUtil setForegroundColorSpan(int var1, int var2, int var3) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new ForegroundColorSpan(var1), var2, var3, 33);
            return this;
        }
    }

    public TextStyleUtil setForegroundColorSpan(String var1, int var2, int var3) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new ForegroundColorSpan(Color.parseColor(var1)), var2, var3, 33);
            return this;
        }
    }

    public TextStyleUtil setRelativeSizeSpan(float var1, int var2, int var3) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new RelativeSizeSpan(var1), var2, var3, 33);
            return this;
        }
    }

    public SpannableString setScaleSpan(int var1, int var2, float var3) {
        SpannableString var4 = new SpannableString(this.ss);
        var4.setSpan(new ScaleXSpan(var3), var1, var2, 33);
        return var4;
    }

    public TextStyleUtil setStrikethroughSpan(int var1, int var2) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new StrikethroughSpan(), var1, var2, 33);
            return this;
        }
    }

    public void setString(String var1) {
        this.ss = new SpannableString(var1);
    }

    public TextStyleUtil setStyleSpan(int var1, int var2, int var3) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new StyleSpan(var1), var2, var3, 33);
            return this;
        }
    }

    public TextStyleUtil setSubscriptSpan(int var1, int var2) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new SubscriptSpan(), var1, var2, 33);
            return this;
        }
    }

    public TextStyleUtil setSuperscriptSpan(int var1, int var2) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new SuperscriptSpan(), var1, var2, 33);
            return this;
        }
    }

    public TextStyleUtil setTypeFaceSpan(String var1, int var2, int var3) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new TypefaceSpan(var1), var2, var3, 33);
            return this;
        }
    }

    public TextStyleUtil setUnderlineSpan(int var1, int var2) {
        if (this.ss == null) {
            return this;
        } else {
            this.ss.setSpan(new UnderlineSpan(), var1, var2, 33);
            return this;
        }
    }
}
