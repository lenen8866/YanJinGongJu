package com.read.scriptures.widget.sliding;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.core.os.ParcelableCompat;
import androidx.core.os.ParcelableCompatCreatorCallbacks;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.TextUtil;
import com.read.scriptures.widget.FloatView;
import com.read.scriptures.widget.sliding.slider.Slider;

import java.util.List;

/**
 * Created by xuzb on 10/23/14.
 */
public class SlidingLayout extends ViewGroup {

    // 用于记录点击事件
    private int mDownMotionX, mDownMotionY;

    private long mDownMotionTime;

    private OnTapListener mOnTapListener;

    private Slider mSlider;

    SlidingAdapter mAdapter;

    private Parcelable mRestoredAdapterState = null;

    private ClassLoader mRestoredClassLoader = null;

    public SlidingLayout(final Context context) {
        super(context);
    }

    public SlidingLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public SlidingLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

//    public OnMoveEventlister moveEventlister;
//
//    public OnMoveEventlister getMoveEventlister() {
//        return moveEventlister;
//    }
//
//    public SlidingLayout setMoveEventlister(OnMoveEventlister moveEventlister) {
//        this.moveEventlister = moveEventlister;
//        return this;
//    }
//
//    public interface OnMoveEventlister {
//        public void onMove();
//    }


    public void setSlider(final Slider slider) {
        mSlider = slider;
        slider.init(this);
        resetFromAdapter();
    }

    public SlidingAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(final SlidingAdapter adapter) {
        mAdapter = adapter;

        mAdapter.setSlidingLayout(this);
        if (mRestoredAdapterState != null) {
            mAdapter.restoreState(mRestoredAdapterState, mRestoredClassLoader);
            mRestoredAdapterState = null;
            mRestoredClassLoader = null;
        }

        resetFromAdapter();

        postInvalidate();

//        mAdapter.setOnMoveEventlister(moveEventlister);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownMotionX = (int) event.getX();
                mDownMotionY = (int) event.getY();
                mDownMotionTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                if (onTabEventlister!=null) {
                    onTabEventlister.onTabMove();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (onTabEventlister!=null) {
                    onTabEventlister.onTabUp();
                }
                computeTapMotion(event);
                break;
        }
        return mSlider.onTouchEvent(event) || super.onTouchEvent(event);
    }

    public void setOnTapListener(final OnTapListener l) {
        this.mOnTapListener = l;
    }

    private void computeTapMotion(final MotionEvent event) {
        if (mOnTapListener == null) {
            return;
        }

        final int xDiff = Math.abs((int) event.getX() - mDownMotionX);
        final int yDiff = Math.abs((int) event.getY() - mDownMotionY);
        final long timeDiff = System.currentTimeMillis() - mDownMotionTime;

        if ((xDiff < 5) && (yDiff < 5) && (timeDiff < 200)) {
            mOnTapListener.onSingleTap(event);
        }
    }

    public SlidingLayout setTabEventlister(OnTabEventlister onTabEventlister) {
        this.onTabEventlister = onTabEventlister;
        return this;
    }

    OnTabEventlister onTabEventlister;
    public interface OnTabEventlister {
        void onTabMove();
        void onTabUp();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mSlider != null) {
            mSlider.computeScroll();
        }
    }

    public void slideNext() {
        mSlider.slideNext();
    }

    public void slidePrevious() {
        mSlider.slidePrevious();
    }

    public interface OnTapListener {
        public void onSingleTap(MotionEvent event);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final int height = child.getMeasuredHeight();
            final int width = child.getMeasuredWidth();
            child.layout(0, 0, width, height);
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public static class SavedState extends BaseSavedState {
        Parcelable adapterState;

        ClassLoader loader;

        public SavedState(final Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString() {
            return "BaseSlidingLayout.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + "}";
        }

        public static final Creator<SavedState> CREATOR = ParcelableCompat
                .newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
                    @Override
                    public SavedState createFromParcel(final Parcel in, final ClassLoader loader) {
                        return new SavedState(in, loader);
                    }

                    @Override
                    public SavedState[] newArray(final int size) {
                        return new SavedState[size];
                    }
                });

        SavedState(final Parcel in, ClassLoader loader) {
            super(in);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState ss = new SavedState(superState);
        if (mAdapter != null) {
            ss.adapterState = mAdapter.saveState();
        }
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (mAdapter != null) {
            mAdapter.restoreState(ss.adapterState, ss.loader);
            resetFromAdapter();
        } else {
            mRestoredAdapterState = ss.adapterState;
            mRestoredClassLoader = ss.loader;
        }
    }

    public void resetFromAdapter() {
        removeAllViews();
        if ((mSlider != null) && (mAdapter != null)) {
            mSlider.resetFromAdapter(mAdapter);
        }
    }

    /**
     * 根据触屏位置获取修改当前朗读
     *
     * @param lineShow 触屏位置
     * @param reader
     * @return
     * @see [类、类#方法、类#成员]
     */
    public String checkTouchLine(float lineShow, FloatView.Reader reader) {
        ListView listView = mAdapter.getCurrentView().findViewById(R.id.sliding_listview);
        listView.getAdapter();
        final int count = listView.getChildCount();
        String select = null;
        for (int i = 0; i < count; i++) {
            final View view = listView.getChildAt(i);
            final int[] location = new int[2];
            view.getLocationInWindow(location);
            final int top = location[1];
            final int height = view.getMeasuredHeight();
            if ((top < lineShow) && ((top + height) > lineShow)) {
                final int selectIndex = listView.getFirstVisiblePosition() + i;
                if (reader.getReadCurrent() != selectIndex) {
                    reader.setReadCurrent(selectIndex);
                    select = checkTouchLineReadPosition(lineShow, reader, selectIndex, view);
                    reader.refreshChapterRemark(true, select);
                    view.setBackgroundColor(Color.parseColor("#22c7edcc"));
                } else {
                    select = checkTouchLineReadPosition(lineShow, reader, selectIndex, view);
                    reader.refreshChapterRemark(true, select);
                    view.setBackgroundColor(Color.parseColor("#22c7edcc"));
                }
            }
        }
        return select;
    }

    /**
     * 触屏位置的position和字符串获得
     *
     * @see [类、类#方法、类#成员]
     */
    private String checkTouchLineReadPosition(final float lineShow, final FloatView.Reader reader, final int selectIndex,
                                              final View view) {
//        final TextView textView = (TextView) ((LinearLayout) view).getChildAt(0);
        final TextView textView = (TextView) ((LinearLayout) ((LinearLayout) view).getChildAt(1)).getChildAt(0);

        final Layout layout = textView.getLayout();
        String select = null;
        if (layout==null){
            return "";
        }
        for (int j = 0; j < layout.getLineCount(); j++) {
            final Rect bound = new Rect();
            layout.getLineBounds(j, bound);
            final int[] location = new int[2];
            textView.getLocationInWindow(location);
            final int top = location[1];
            final int yAxisTop = bound.top + top;// 字符顶部y坐标
            final int yAxisBottom = bound.bottom + top;// 字符底部y坐标

            if ((yAxisTop <= lineShow) && (yAxisBottom >= lineShow)) {
                final int start = layout.getLineStart(j);
                final int end = layout.getLineEnd(j);
                String result = textView.getText().toString().substring(start, end).trim();
                result = TextUtil.replaceBlank(result);
                String head = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", result);
                if (!TextUtils.isEmpty(head)) {//如果有head
                    char c = head.charAt(0);
                    if (result.indexOf(c) == 0) {//说明是第一个
                        result = result.replace(head, "");
                    }
                }
                final List<String> lists = reader.getmSpeechTextMap().get(selectIndex);
                // LogUtil.test("result：" + result + "\nlists:" + lists);
                if (lists == null) {
                    return select;
                }
                for (final String string : lists) {
                    if (result.contains("。")) {
                        try {
                            String one = result.split("。")[0];
                            if (result.split("。").length > 1) {
                                String two = result.split("。")[1];
                                result = one.length() > two.length() ? one : two;
                            }else{
                                result = one;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    boolean isSpeakTitle = SharedUtil.getBoolean(PreferenceConfig.Preference_Speak_Title,false);
                    if (isSpeakTitle){
                        result = result.replace("缩进","");
                    }

                    String[] tags = {"a", "b", "font", "h", "s"};
                    String lineStr = StringUtil.replaceTags(string, tags);
                    if (lineStr.contains(result.replace("缩进",""))
                            || TextUtil.punctuationSetEmpty(string).contains(TextUtil.punctuationSetEmpty(result))) {
                        select = string;
                        if (!isSpeakTitle) {
                            if ((select.contains("<b") && !select.contains("</b")) || (select.contains("<h") && !select.contains("</h"))){
                                select = "";
                            }else if (select.contains("</b>")) {
                                select = select.substring(select.indexOf("</b>") + "</b>".length());
                            } else if (select.contains("</h>")) {
                                select = select.substring(select.indexOf("</h>") + "</h>".length());
                            }
                        }
                        select = StringUtil.replaceTags(select, tags);
                        final int position = lists.indexOf(string);
                        // LogUtil.test("select:" + select);
                        reader.setmSpeechPosition(position);
                    }
                }
            }
        }
        return select;
    }

    private OnSlideChangeListener mSlideChangeListener;

    public void setOnSlideChangeListener(final OnSlideChangeListener l) {
        mSlideChangeListener = l;
    }

    public interface OnSlideChangeListener {
        public void onSlideScrollStateChanged(int touchResult);

        public void onSlideSelected(Object obj);
    }

    public void slideScrollStateChanged(final int moveDirection) {
        if (mSlideChangeListener != null) {
            mSlideChangeListener.onSlideScrollStateChanged(moveDirection);
        }
    }

    public void slideSelected(final Object obj) {
        if (mSlideChangeListener != null) {
            mSlideChangeListener.onSlideSelected(obj);
        }
    }
}
