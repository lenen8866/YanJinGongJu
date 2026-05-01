package com.read.scriptures.view.csstextview;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.util.DisplayUtil;


/**
 * Created by sflin
 * <p>
 * 设置文本内容其中文字的颜色 text文字 color颜色 startIndex开始位置 OnClickSpan点击事件
 * <p>
 * setTextArrColor(String text, int color)
 * <p>
 * setTextArrColor(String text, int color, OnClickSpan onClickSpan)
 * <p>
 * setTextArrColor(String text, int color, int startIndex)
 * <p>
 * setTextArrColor(String text, int color, int startIndex, OnClickSpan onClickSpan)
 * 设置文本内容其中文字的字体大小 text文字 size大小 startIndex开始位置 OnClickSpan点击事件
 * <p>
 * setTextArrSize(String text, int size)
 * <p>
 * setTextArrSize(String text, int size, OnClickSpan onClickSpan)
 * <p>
 * setTextArrSize(String text, int size, int startIndex)
 * <p>
 * setTextArrSize(String text, int size, int startIndex, OnClickSpan onClickSpan)
 * 设置文本内容其中文字的字体样式 text文字 style样式 startIndex开始位置 OnClickSpan点击事件
 * (样式:Typeface.NORMAL、Typeface.BOLD、Typeface.ITALIC、Typeface.BOLD_ITALIC）
 * <p>
 * setTextArrStyle(String text, int style)
 * <p>
 * setTextArrStyle(String text, int style, OnClickSpan onClickSpan)
 * <p>
 * setTextArrStyle(String text, int style, int startIndex)
 * <p>
 * setTextArrStyle(String text, int style, int startIndex, OnClickSpan onClickSpan)
 * 设置文本内容其中文字的点击事件 text文字 startIndex开始位置 OnClickSpan点击事件
 * <p>
 * setTextClick(String text, OnClickSpan onClickSpan)
 * <p>
 * setTextClick(String text, int startIndex, OnClickSpan onClickSpan)
 * 设置文本内容其中文字的字体颜色，字体大小 text文字 color颜色 size大小 startIndex开始位置 OnClickSpan点击事件
 * <p>
 * setTextArrColorSize(String text, int color, int size)
 * <p>
 * setTextArrColorSize(String text, int color, int size, OnClickSpan onClickSpan)
 * <p>
 * setTextArrColorSize(String text, int color, int size, int startIndex)
 * <p>
 * setTextArrColorSize(String text, int color, int size, int startIndex, OnClickSpan onClickSpan)
 * 设置文本内容其中文字的字体颜色，字体样式 text文字 color颜色 style样式 startIndex开始位置 OnClickSpan点击事件
 * <p>
 * setTextArrColorStyle(String text, int color, int style)
 * <p>
 * setTextArrColorStyle(String text, int color, int style, OnClickSpan onClickSpan)
 * <p>
 * setTextArrColorStyle(String text, int color, int style, int startIndex)
 * <p>
 * setTextArrColorStyle(String text, int color, int style, int startIndex, OnClickSpan onClickSpan)
 * 设置文本内容其中文字的字体大小，字体样式 text文字 size大小 style样式 startIndex开始位置 OnClickSpan点击事件
 * <p>
 * setTextArrSizeStyle(String text, int size, int style)
 * <p>
 * setTextArrSizeStyle(String text, int size, int style, OnClickSpan onClickSpan)
 * <p>
 * setTextArrSizeStyle(String text, int size, int style, int startIndex)
 * <p>
 * setTextArrSizeStyle(String text, int size, int style, int startIndex, OnClickSpan onClickSpan)
 * 设置文本内容其中文字的字体颜色，字体大小，字体样式 text文字 color颜色 size大小 style样式 startIndex开始位置 OnClickSpan点击事件
 * <p>
 * setTextArrColorSizeStyle(String text, int color, int size, int style)
 * <p>
 * setTextArrColorSizeStyle(String text, int color, int size, int style, OnClickSpan onClickSpan)
 * <p>
 * setTextArrColorSizeStyle(String text, int color, int size, int style, int startIndex)
 * <p>
 * setTextArrColorSizeStyle(String text, int color, int size, int style, int startIndex, OnClickSpan onClickSpan)
 */

/**
 * Created by sflin
 */

public class CSSTextView extends AppCompatTextView {

    public boolean isClickSpan = false;

    private String[] colorArr, sizeArr, styleArr;

    public CSSTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CSSTextView(Context context) {
        this(context, null);
    }

    public CSSTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        if (isClickSpan) {
            return true;
        }
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isClickSpan = false;
        return super.onTouchEvent(event);
    }

    /**
     * 设置文本内容其中文字的颜色
     * @param text   内容
     * @param color  颜色
     */
    public CSSTextView setTextArrColor(String text, int color) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的颜色
     * @param text   内容
     * @param color  颜色
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrColor(final String text, final int color, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onClickSpan.onClick(text);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置文本的颜色
                    ds.setColor(color);
                    //超链接形式的下划线，false 表示不
                    ds.setUnderlineText(false);
                }
            }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
            setMovementMethod(CustomLinkMovementMethod.getInstance());
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的颜色
     * @param text   内容
     * @param color  颜色
     * @param startIndex  开始位置
     */
    public CSSTextView setTextArrColor(String text, int color, int startIndex) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        return this;
    }

    /**
     * 设置文本内容其中文字的颜色
     * @param text   内容
     * @param color  颜色
     * @param startIndex  开始位置
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrColor(final String text, final int color, int startIndex, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onClickSpan.onClick(text);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文本的颜色
                ds.setColor(color);
                //超链接形式的下划线，false 表示不
                ds.setUnderlineText(false);
            }
        }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        setMovementMethod(CustomLinkMovementMethod.getInstance());

        return this;
    }

    /**
     * 设置文本内容其中文字的字体大小
     * @param text  内容
     * @param size  字体大小
     */
    public CSSTextView setTextArrSize(String text, int size) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体大小
     * @param text  内容
     * @param size  字体大小
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrSize(final String text, int size, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onClickSpan.onClick(text);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置文本的颜色
                    ds.setColor(getTextColors().getDefaultColor());
                    //超链接形式的下划线，false 表示不
                    ds.setUnderlineText(false);
                }
            }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
            setMovementMethod(CustomLinkMovementMethod.getInstance());
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体大小
     * @param text  内容
     * @param size  字体大小
     * @param startIndex  开始位置
     */
    public CSSTextView setTextArrSize(String text, int size, int startIndex) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        return this;
    }

    /**
     * 设置文本内容其中文字的字体大小
     * @param text  内容
     * @param size  字体大小
     * @param startIndex  开始位置
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrSize(final String text, int size, int startIndex, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onClickSpan.onClick(text);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文本的颜色
                ds.setColor(getTextColors().getDefaultColor());
                //超链接形式的下划线，false 表示不
                ds.setUnderlineText(false);
            }
        }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        setMovementMethod(CustomLinkMovementMethod.getInstance());
        return this;
    }

    /**
     * 设置文本内容其中文字的字体样式
     * @param text   内容
     * @param style  字体样式
     */
    public CSSTextView setTextArrStyle(String text, int style) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体样式
     * @param text   内容
     * @param style  字体样式
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrStyle(final String text, int style, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onClickSpan.onClick(text);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置文本的颜色
                    ds.setColor(getTextColors().getDefaultColor());
                    //超链接形式的下划线，false 表示不
                    ds.setUnderlineText(false);
                }
            }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
            setMovementMethod(CustomLinkMovementMethod.getInstance());
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体样式
     * @param text   内容
     * @param style  字体样式
     * @param startIndex  开始位置
     */
    public CSSTextView setTextArrStyle(String text, int style, int startIndex) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        return this;
    }

    /**
     * 设置文本内容其中文字的字体样式
     * @param text   内容
     * @param style  字体样式
     * @param startIndex  开始位置
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrStyle(final String text, int style, int startIndex, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onClickSpan.onClick(text);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文本的颜色
                ds.setColor(getTextColors().getDefaultColor());
                //超链接形式的下划线，false 表示不
                ds.setUnderlineText(false);
            }
        }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        setMovementMethod(CustomLinkMovementMethod.getInstance());
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体大小
     * @param text   内容
     * @param color  字体颜色
     * @param size   字体大小
     */
    public CSSTextView setTextArrColorSize(String text, int color, int size) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体大小
     * @param text   内容
     * @param color  字体颜色
     * @param size   字体大小
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrColorSize(final String text, final int color, int size, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onClickSpan.onClick(text);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置文本的颜色
                    ds.setColor(color);
                    //超链接形式的下划线，false 表示不
                    ds.setUnderlineText(false);
                }
            }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
            setMovementMethod(CustomLinkMovementMethod.getInstance());
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体大小
     * @param text   内容
     * @param color  字体颜色
     * @param size   字体大小
     * @param startIndex  开始位置
     */
    public CSSTextView setTextArrColorSize(String text, int color, int size, int startIndex) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体大小
     * @param text   内容
     * @param color  字体颜色
     * @param size   字体大小
     * @param startIndex  开始位置
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrColorSize(final String text, final int color, int size, int startIndex, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onClickSpan.onClick(text);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文本的颜色
                ds.setColor(color);
                //超链接形式的下划线，false 表示不
                ds.setUnderlineText(false);
            }
        }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        setMovementMethod(CustomLinkMovementMethod.getInstance());
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体样式
     * @param text   内容
     * @param color  字体颜色
     * @param style  字体样式
     */
    public CSSTextView setTextArrColorStyle(String text, int color, int style) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体样式
     * @param text   内容
     * @param color  字体颜色
     * @param style  字体样式
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrColorStyle(final String text, final int color, int style, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onClickSpan.onClick(text);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置文本的颜色
                    ds.setColor(color);
                    //超链接形式的下划线，false 表示不
                    ds.setUnderlineText(false);
                }
            }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
            setMovementMethod(CustomLinkMovementMethod.getInstance());
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体样式
     * @param text   内容
     * @param color  字体颜色
     * @param style  字体样式
     * @param startIndex  开始位置
     */
    public CSSTextView setTextArrColorStyle(String text, int color, int style, int startIndex) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体样式
     * @param text   内容
     * @param color  字体颜色
     * @param style  字体样式
     * @param startIndex  开始位置
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrColorStyle(final String text, final int color, int style, int startIndex, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onClickSpan.onClick(text);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文本的颜色
                ds.setColor(color);
                //超链接形式的下划线，false 表示不
                ds.setUnderlineText(false);
            }
        }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        setMovementMethod(CustomLinkMovementMethod.getInstance());
        return this;
    }

    /**
     * 设置文本内容其中文字的字体大小，字体样式
     * @param text   内容
     * @param size   字体大小
     * @param style  字体样式
     */
    public CSSTextView setTextArrSizeStyle(String text, int size, int style) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体大小，字体样式
     * @param text   内容
     * @param size   字体大小
     * @param style  字体样式
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrSizeStyle(final String text, int size, int style, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onClickSpan.onClick(text);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置文本的颜色
                    ds.setColor(getTextColors().getDefaultColor());
                    //超链接形式的下划线，false 表示不
                    ds.setUnderlineText(false);
                }
            }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
            setMovementMethod(CustomLinkMovementMethod.getInstance());
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体大小，字体样式
     * @param text   内容
     * @param size   字体大小
     * @param style  字体样式
     * @param startIndex  开始位置
     */
    public CSSTextView setTextArrSizeStyle(String text, int size, int style, int startIndex) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        return this;
    }

    /**
     * 设置文本内容其中文字的字体大小，字体样式
     * @param text   内容
     * @param size   字体大小
     * @param style  字体样式
     * @param startIndex  开始位置
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrSizeStyle(final String text, int size, int style, int startIndex, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onClickSpan.onClick(text);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文本的颜色
                ds.setColor(getTextColors().getDefaultColor());
                //超链接形式的下划线，false 表示不
                ds.setUnderlineText(false);
            }
        }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        setMovementMethod(CustomLinkMovementMethod.getInstance());
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体大小，字体样式
     * @param text   内容
     * @param color  字体颜色
     * @param size   字体大小
     * @param style  字体样式
     */
    public CSSTextView setTextArrColorSizeStyle(String text, int color, int size, int style) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体大小，字体样式
     * @param text   内容
     * @param color  字体颜色
     * @param size   字体大小
     * @param style  字体样式
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrColorSizeStyle(final String text, final int color, int size, int style, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onClickSpan.onClick(text);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置文本的颜色
                    ds.setColor(color);
                    //超链接形式的下划线，false 表示不
                    ds.setUnderlineText(false);
                }
            }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
            setMovementMethod(CustomLinkMovementMethod.getInstance());
        }
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体大小，字体样式
     * @param text   内容
     * @param color  字体颜色
     * @param size   字体大小
     * @param style  字体样式
     * @param startIndex  开始位置
     */
    public CSSTextView setTextArrColorSizeStyle(String text, int color, int size, int style, int startIndex) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        return this;
    }

    /**
     * 设置文本内容其中文字的字体颜色，字体大小，字体样式
     * @param text   内容
     * @param color  字体颜色
     * @param size   字体大小
     * @param style  字体样式
     * @param startIndex  开始位置
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextArrColorSizeStyle(final String text, final int color, int size, int style, int startIndex, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new AbsoluteSizeSpan((int)DisplayUtil.dp2px(HuDongApplication.getInstance(),size)), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new StyleSpan(style), startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onClickSpan.onClick(text);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文本的颜色
                ds.setColor(color);
                //超链接形式的下划线，false 表示不
                ds.setUnderlineText(false);
            }
        }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        setMovementMethod(CustomLinkMovementMethod.getInstance());
        return this;
    }

    /**
     * 设置文本内容其中文字的点击事件
     * @param text   内容
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextClick(final String text, final boolean isUnderline, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        int startIndex = (getText() + "").indexOf(text);
        if (startIndex > -1) {
            styledText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onClickSpan.onClick(text);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置文本的颜色
//                    ds.setColor(getTextColors().getDefaultColor());
                    //超链接形式的下划线，false 表示不
                    ds.setUnderlineText(isUnderline);
                }
            }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(styledText, TextView.BufferType.SPANNABLE);
            setMovementMethod(CustomLinkMovementMethod.getInstance());
        }

        return this;
    }

    /**
     * 设置文本内容其中文字的点击事件
     * @param text   内容
     * @param startIndex  开始位置
     * @param onClickSpan  点击事件
     */
    public CSSTextView setTextClick(final String text, int startIndex, final OnClickSpan onClickSpan) {
        if (getText().length() == 0) {
            throw new NullPointerException("Please Set The textView Content!");
        }
        SpannableString styledText = new SpannableString(getText());
        styledText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onClickSpan.onClick(text);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文本的颜色
                ds.setColor(getTextColors().getDefaultColor());
                //超链接形式的下划线，false 表示不
                ds.setUnderlineText(false);
            }
        }, startIndex, startIndex + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(styledText, TextView.BufferType.SPANNABLE);
        setMovementMethod(CustomLinkMovementMethod.getInstance());

        return this;
    }

    public interface OnClickSpan {
        void onClick(String text);
    }
}