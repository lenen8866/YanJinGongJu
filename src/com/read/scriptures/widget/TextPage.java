package com.read.scriptures.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by LGM.
 * Datetime: 2015/7/4.
 * Email: lgmshare@mgail.com
 */
public class TextPage extends EditText {

    private int off; //瀛楃涓茬殑鍋忕Щ鍊�

    public TextPage(Context context) {
        super(context);
        initialize();
    }

    public TextPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TextPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public TextPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs,  defStyleRes);
        initialize();
    }

    private void initialize() {
        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    public boolean getDefaultEditable() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Layout layout = getLayout();
        int line = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                off = layout.getOffsetForHorizontal(line, (int) event.getX());
                Selection.setSelection(getEditableText(), off);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                int curOff = layout.getOffsetForHorizontal(line, (int) event.getX());
                Selection.setSelection(getEditableText(), off, curOff);
                break;
        }
        return true;
    }
}