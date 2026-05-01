package com.read.scriptures.widget;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class QScrollingMovementMethod extends ScrollingMovementMethod {

    public static void textViewScrollToEnd(TextView textView) {
        int padding = textView.getTotalPaddingTop()
                + textView.getTotalPaddingBottom();
        int line = textView.getLayout().getLineCount() - 1;
        textView.scrollTo(textView.getScrollX(),
                textView.getLayout().getLineTop(line + 1)
                        - (textView.getHeight() - padding));
    }

    @Override
    protected boolean left(TextView widget, Spannable buffer) {
        super.left(widget, buffer);
        return true;
    }

    @Override
    protected boolean right(TextView widget, Spannable buffer) {
        super.right(widget, buffer);
        return true;
    }

    @Override
    protected boolean up(TextView widget, Spannable buffer) {
        super.up(widget, buffer);
        return true;
    }

    @Override
    protected boolean down(TextView widget, Spannable buffer) {
        super.down(widget, buffer);
        return true;
    }

    @Override
    protected boolean pageUp(TextView widget, Spannable buffer) {
        super.pageUp(widget, buffer);
        return true;
    }

    @Override
    protected boolean pageDown(TextView widget, Spannable buffer) {
        super.pageDown(widget, buffer);
        return true;
    }

    @Override
    protected boolean top(TextView widget, Spannable buffer) {
        super.top(widget, buffer);
        return true;
    }

    @Override
    protected boolean bottom(TextView widget, Spannable buffer) {
        super.bottom(widget, buffer);
        return true;
    }

    @Override
    protected boolean lineStart(TextView widget, Spannable buffer) {
        super.lineStart(widget, buffer);
        return true;
    }

    @Override
    protected boolean lineEnd(TextView widget, Spannable buffer) {
        super.lineEnd(widget, buffer);
        return true;
    }

    @Override
    protected boolean home(TextView widget, Spannable buffer) {
        return top(widget, buffer);
    }

    @Override
    protected boolean end(TextView widget, Spannable buffer) {
        return bottom(widget, buffer);
    }

    @Override
    public void onTakeFocus(TextView widget, Spannable text, int dir) {
        Layout layout = widget.getLayout();

        if (layout != null && (dir & View.FOCUS_FORWARD) != 0) {
            widget.scrollTo(widget.getScrollX(), layout.getLineTop(0));
        }
        if (layout != null && (dir & View.FOCUS_BACKWARD) != 0) {
            int padding = widget.getTotalPaddingTop()
                    + widget.getTotalPaddingBottom();
            int line = layout.getLineCount() - 1;

            widget.scrollTo(widget.getScrollX(), layout.getLineTop(line + 1)
                    - (widget.getHeight() - padding));
        }
    }
}
