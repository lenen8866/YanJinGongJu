package com.read.scriptures.util;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import org.xml.sax.XMLReader;

public class SizeLabelHandler implements Html.TagHandler {
    private int s_size;
    private int m_size;
    private int startIndex = 0;
    private int stopIndex = 0;
    private Context context;

    public SizeLabelHandler(Context context, int s_size,int m_size) {
        this.context = context;
        this.s_size = s_size;
        this.m_size = m_size;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.toLowerCase().equals("s_size")) {
            if (opening) {
                startIndex = output.length();
            } else {
                stopIndex = output.length();
                output.setSpan(new AbsoluteSizeSpan(sp2px(s_size)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (tag.toLowerCase().equals("m_size")) {
            if (opening) {
                startIndex = output.length();
            } else {
                stopIndex = output.length();
                output.setSpan(new AbsoluteSizeSpan(sp2px(m_size)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public int sp2px(float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);

    }
}