package com.read.scriptures.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.read.scriptures.R;
import com.read.scriptures.util.StringUtil;

import java.util.regex.Pattern;

/**
 * Created by luoliuqing on 17/6/30.
 * 宽高比控件，高度随宽度改变
 */
public class WHProportionRelativeLayout extends RelativeLayout {
    String mProportion = "1:1";
    private float mWhScanFloat;

    public WHProportionRelativeLayout(Context context) {
        super(context);
    }

    public WHProportionRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context, attrs);
    }

    public WHProportionRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        //获取比例值
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.WHProportionRelativeLayout);
        mProportion = a.getString(R.styleable.WHProportionRelativeLayout_proportion);
    }

    @SuppressWarnings("unused")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        if (!StringUtil.isEmpty(mProportion) && mWhScanFloat == 0 && Pattern.compile("([0-9]{1,}):([0-9]{1,})").matcher(mProportion).find()) {
            int width_pro = Integer.parseInt(mProportion.substring(0, mProportion.indexOf(":")));  //得到宽的比例
            int height_pro = Integer.parseInt(mProportion.substring(mProportion.indexOf(":") + 1));       //得到高的比例
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
            /**
             * 按照比例改变高度值
             */
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (childWidthSize * height_pro * 1.0) / width_pro, MeasureSpec.EXACTLY);

        }else if (mWhScanFloat > 0){
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
            /**
             * 按照比例改变高度值
             */
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (childWidthSize / mWhScanFloat), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setmProportion(String mProportion) {
        this.mProportion = mProportion;
        requestLayout();
    }

    public void setmProportion(float proportion) {
//        mProportion = convert(proportion);
        mWhScanFloat = proportion;
        requestLayout();
    }

    /**
     * 求两个数的最大公约数
     * @param a
     * @param b
     * @return
     */
    private int getGongYueShu(int a, int b) {
        int t = 0;
        if (a < b) {
            t = a;
            a = b;
            b = t;
        }
        int c = a % b;
        if (c == 0) {
            return b;
        } else {
            return getGongYueShu(b, c);
        }
    }

    /**
     * 小数转成比例关系
     * @param num
     * @return
     */
    public String convert(float num) {
        String floatNumStr = String.valueOf(num);
        String[] array = new String[2];
        array = floatNumStr.split("\\.");
        int a = Integer.parseInt(array[0]);//获取整数部分
        int b = Integer.parseInt(array[1]);//获取小数部分
        int length = array[1].length();
        int FenZi = (int) (a * Math.pow(10, length) + b);
        int FenMu = (int) Math.pow(10, length);
        int MaxYueShu = getGongYueShu(FenZi, FenMu);
        return FenZi / MaxYueShu + ":" + FenMu / MaxYueShu;
    }
}
