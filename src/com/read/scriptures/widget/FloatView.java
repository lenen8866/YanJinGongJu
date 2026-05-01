package com.read.scriptures.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.control.BaiduSpeechManager;
import com.read.scriptures.manager.XunFeiSpeechManager;
import com.read.scriptures.ui.activity.ChapterReaderActivity;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.TextUtil;
import com.read.scriptures.widget.sliding.SlidingLayout;

import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 可拖动的ImageView <一句话功能简述> <功能详细描述>
 *
 * @author Administrator
 * @version [版本号, 2015-6-8]
 * @since [产品/模块版本]
 */
public class FloatView extends androidx.appcompat.widget.AppCompatImageView {//implements View.OnTouchListener
    /**
     * 长按生效时间
     */
    private static final long LONG_CLICK_ABLE_TIME = 2000;

    //触屏生效时间
    private static final long ABLE_TIME = 200;
    private static final String TAG = "FloatView";

    long end = 0;

    private Disposable mAutoScrollDownDisposable;
    private Disposable mAutoScrollTopDisposable;
    private Context mContext;
    private RelativeLayout mParentView;


    public void setParent(RelativeLayout p) {
        this.mParentView = p;
        showLog("mParentView = " + mParentView.getHeight());
    }

    // /**
    // * 触屏生效时间
    // */
    // TODO:模拟长按http://blog.csdn.net/yangdeli888/article/details/8125348
    public interface Reader {
        public int getReadCurrent();

        public void setReadCurrent(int current);

        public void refreshChapterRemark(boolean speechModel, String remarkText);

        public int getOldIndex();

        XunFeiSpeechManager speechManager();

        void setOldIndex(int oldIndex);

        HashMap<Integer, List<String>> getmSpeechTextMap();

        int getmSpeechPosition();

        void setmSpeechPosition(int mSpeechPosition);

        int getOldPosition();

        void setOldPosition(int oldPosition);
    }

    public interface ListViewListener {
        float smoothNext(ChapterReaderActivity.MoveDistanceListener ls);

        float smoothLast(ChapterReaderActivity.MoveDistanceListener ls);
    }

    private long tempTime;

    //底部菜单Y值
    private RelativeLayout bottomStatus;

    public RelativeLayout getBottomStatus() {
        return bottomStatus;
    }

    public void setBottomStatus(RelativeLayout bottomStatusY) {
        this.bottomStatus = bottomStatusY;
    }

    private Reader reader;

    public Reader getReader() {
        return reader;
    }

    public void setReader(final Reader reader) {
        this.reader = reader;
    }

    private float x;

    private float y;

    //正在读的条目Y坐标
    private float currentReadY;

    //根据上个读取的位置计算出来的新Y坐标
    private float newY;

    private float mStartX;

    private float mStartY;

    private int statusBarHeight;

    private FloatViewOnClickListener mClickListener;

    private XunFeiSpeechManager mXunFeiSpeechManager;

    private BaiduSpeechManager baiduSpeechManager;
    private SlidingLayout slidingLayout;

    private boolean isTouchPause = false;

    public SlidingLayout getSlidingLayout() {
        return slidingLayout;
    }

    public void setSlidingLayout(final SlidingLayout slidingLayout) {
        this.slidingLayout = slidingLayout;
    }

    private Paint paint;

    private int screenWidth = 0;
    private int screenHeight = 0;
    private int slideLayoutHeight = 0;
    private int listViewHeight = 0;
    private int slidTop = 0;


    private boolean showLine;

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(final boolean showLine) {
        this.showLine = showLine;
    }

    public float getLineShow() {
        return y;
    }

    public XunFeiSpeechManager getmXunFeiSpeechManager() {
        return mXunFeiSpeechManager;
    }

    public void setmXunFeiSpeechManager(final XunFeiSpeechManager mXunFeiSpeechManager) {
        this.mXunFeiSpeechManager = mXunFeiSpeechManager;
    }

    public void setBaiduSpeechManager(final BaiduSpeechManager baiduSpeechManager) {
        this.baiduSpeechManager = baiduSpeechManager;
    }


    public FloatView(Context context) {
        this(context, null);
        screenWidth = getScreenWidth();
        screenHeight = getScreenHeight();
        statusBarHeight = getStatusBarHeight();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#77233666"));
        paint.setStrokeWidth(24);
        paint.setTextSize(15);
        mContext = context;
    }

    public FloatView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    int bottomY = 0;
    float drawLineY = 0;

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (showLine) {
            if (slidTop == 0) {
                int[] rect = new int[2];
                slidingLayout.getLocationInWindow(rect);
                this.slidTop = rect[1];
            }

            if (bottomY == 0) {
                bottomY = screenHeight;
            }
            if (drawLineY <= slidTop) {
                drawLineY = slidTop;
            } else if (drawLineY >= bottomY) {
                drawLineY = bottomY;
            }
            canvas.drawLine(0, drawLineY, screenWidth, drawLineY, paint);
        }
    }

    /**
     * 获取状态栏高度 <功能详细描述>
     *
     * @return result 状态栏高度
     * @see [类、类#方法、类#成员]
     */
    private int getStatusBarHeight() {
        int result = 0;
        final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    int readCurrent;
    int speechPosition;
    String select;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        long nowTime = System.currentTimeMillis();
        end = nowTime;
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        //  去掉 statusBarHeight是系统状态栏的高度
//        y = event.getRawY() - statusBarHeight;
        y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lock = false;
                // 获取相对View的坐标，即以此View左上角为原点
                tempTime = nowTime;
                mStartX = x;
                mStartY = y;
                CACHEY = y;
                showLine = true;

                readCurrent = reader.getReadCurrent();
                speechPosition = reader.getmSpeechPosition();
                //按下时获取当前正在读的文字Y值
                ListView listView = slidingLayout.getAdapter().getCurrentView().findViewById(R.id.sliding_listview);
                ListAdapter adapter = listView.getAdapter();
                if (adapter.getCount() <= readCurrent) {
                    break;
                }
                String currentStr = (String) adapter.getItem(readCurrent);//当前的内容

                int count = listView.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = listView.getChildAt(i);
                    int[] location = new int[2];
                    view.getLocationInWindow(location);
                    int top = location[1];
                    int height = view.getMeasuredHeight();

                    int selectIndex = listView.getFirstVisiblePosition() + i;
//                    String selectStr = checkTouchLineReadPosition(reader, selectIndex, view,speechPosition);

                    if (i != readCurrent - listView.getFirstVisiblePosition()) {
                        continue;
                    }

                    String selectStr = reader.getmSpeechTextMap().get(selectIndex).get(speechPosition);//remarkText

                    if (!TextUtils.isEmpty(selectStr) && currentStr.contains(selectStr)) {
                        //自然段太长了 按照字数取Y的高度 比取一半准确点

                        String totalstr = currentStr.substring(0, currentStr.indexOf(selectStr) + selectStr.length());//0 ->index的位置[]
                        String laststr = currentStr.substring(0, currentStr.indexOf(selectStr));//0 ->index的位置 [）
                        float roate = totalstr.length() * 1.0f / currentStr.length();
                        float roatelast = laststr.length() * 1.0f / currentStr.length();
                        if (i == 0) {
                            int viewH = height;//view高度差
                            currentReadY = view.getTop() + (viewH * roate + viewH * roatelast) / 2 + screenHeight - slidingLayout.getMeasuredHeight();
                        } else if (i == count - 1) {
                            int viewH = height;
                            currentReadY = view.getTop() + (viewH * roate + viewH * roatelast) / 2 + screenHeight - slidingLayout.getMeasuredHeight();
                        } else {
                            //中间的
                            int viewH = height;
                            currentReadY = view.getTop() + (viewH * roate + viewH * roatelast) / 2 + screenHeight - slidingLayout.getMeasuredHeight();
                        }
                        break;
                    }
                }

                drawLineY = (int) currentReadY;
                reader.setOldIndex(readCurrent);
                reader.setOldPosition(speechPosition);
                isTouchPause = false;
//                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if ((nowTime - tempTime) <= ABLE_TIME) {
                    return true;
                } else if (!((Math.abs(x - mStartX) < 5) && (Math.abs(y - mStartY) < 5))) {
                    if (!isTouchPause) {
                        isTouchPause = true;
                        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                            mXunFeiSpeechManager.pauseSpeaking();
                        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                            baiduSpeechManager.pause();
                        }
                    }
                    updateViewPosition();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lock = false;
                cancleAutoScrollTopDisposable();
                cancleAutoScrollDownDisposable();
                if ((Math.abs(x - mStartX) < 5) && (Math.abs(y - mStartY) < 5)) {
                    if ((nowTime - tempTime) > LONG_CLICK_ABLE_TIME) {
                    } else if ((nowTime - tempTime) <= ABLE_TIME) {
                        if (mClickListener != null) {
                            mClickListener.onClick(x, y);
                        }
                        showLine = false;
                        invalidate();
                        return true;
                    }
                    showLine = false;
                    invalidate();

                    reader.setOldIndex(readCurrent);
                    reader.setOldPosition(speechPosition);
                    if (isTouchPause) {
                        isTouchPause = false;
                        if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                            mXunFeiSpeechManager.resumeSpeaking();
                        } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                            baiduSpeechManager.resume();
                        }
                    }
                    return true;
                }
                showLine = false;
                invalidate();
                select = slidingLayout.checkTouchLine(newY, reader);
                if ((select != null)) {//changeReadLine && (select != null)
                    select = SearchTextUtil.replaceTag("<.+?>", select);
                    select = SearchTextUtil.replaceTag("\\(.+\\)", select);
                    select = SearchTextUtil.replaceTag("（.+）", select);
                    SystemConfig.readContent = select;
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.stopSpeaking();
                        mXunFeiSpeechManager.startSpeaking(select, mXunFeiSpeechManager.getTtsListener());
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                        baiduSpeechManager.stop();
                        baiduSpeechManager.speak(select);
                    }
                } else {
                    if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_XF) {
                        mXunFeiSpeechManager.resumeSpeaking();
                    } else if (SystemConfig.Speech_Model == SystemConfig.SPEECH_MODEL_BAIDU) {
                        baiduSpeechManager.resume();
                    }
                }
                break;
        }
        return true;
    }

    private ListViewListener mListViewListener;

    public void setListViewListenner(ListViewListener l) {
        this.mListViewListener = l;
    }


    public void setNewYPosition(float y) {
//        drawLineY = y;
//        invalidate();
        showLog("setNewYPosition  lock = " + lock);
        lock = false;
    }

    boolean lock = false;

    /**
     * +
     * <一句话功能简述> <功能详细描述>
     *
     * @see [类、类#方法、类#成员]
     */
    private synchronized void updateViewPosition() {
        //按下后移动的距离
        float v = y - mStartY;//距离没问题
        newY = currentReadY + v;//screenHeight - listViewHeight 因为newY是屏幕的坐标
        final ListView listView = slidingLayout.getAdapter().getCurrentView().findViewById(R.id.sliding_listview);
        if (DOWN == getScrollState(y)) {//y在变化
            if (listView.getLastVisiblePosition() == listView.getCount() - 1) {
                if (newY >= screenHeight) {
                    float smY = listView.getChildAt(listView.getChildCount() - 1).getMeasuredHeight() - (slidingLayout.getMeasuredHeight() - listView.getChildAt(listView.getChildCount() - 1).getTop());
                    listView.smoothScrollBy((int) smY, 0);
                    return;
                }
            }
            if (newY >= screenHeight) {
                //滚动lisview
                float sy = mListViewListener.smoothNext(dis -> {

                });
                currentReadY = currentReadY - sy;
                lock = true;
            } else {
                drawLineY = newY;
            }
            slidingLayout.checkTouchLine(newY, reader);
        } else if (UP == getScrollState(y)) { //向上滑动
            //如果向下滑动 newY  不可能大于 slidingLayout.getMeasuredHeight -100
            if (listView.getFirstVisiblePosition() == 0) {
                if (newY <= slidTop) {
                    newY = slidTop;
                    return;
                }
            }
            if (newY <= slidTop) {
                //滚动lisview
                float sy = mListViewListener.smoothLast(dis -> {
                });
                currentReadY = currentReadY - sy;
                lock = true;
            } else {
                drawLineY = newY;
            }
            slidingLayout.checkTouchLine(newY, reader);
        }

        CACHEY = y;
        invalidate();


        //滚动到最底部，则翻页
//        int lastVisiblePosition = listView.getLastVisiblePosition() + 1;
//        int size = reader.getmSpeechTextMap().size();
//        if (lastVisiblePosition == size) {
//
//            int[] location = new int[2];
//            bottomStatus.getLocationInWindow(location);
//            int top = location[1];
//
//            int height = bottomStatus.getMeasuredHeight();
//
//            if (newY >= (top + height / 2)) {
//
//                mStartY = y;
//                currentReadY = statusBarHeight;
//                newY = statusBarHeight;
//
//                slidingLayout.slideNext();
//            }
//        }
    }

    private final static int DEFAULT = 0;
    private final static int UP = 1;
    private final static int DOWN = 2;
    private float CACHEY = 0;
    private float SENSOR_DIS = 5;

    private int getScrollState(float y) {
        if (y > CACHEY && Math.abs(y - CACHEY) > SENSOR_DIS) {
            return DOWN;
        } else if (y < CACHEY && Math.abs(y - CACHEY) > SENSOR_DIS) {
            return UP;
        }
        return DEFAULT;
    }


    private void cancleAutoScrollDownDisposable() {
        if (mAutoScrollDownDisposable != null && !mAutoScrollDownDisposable.isDisposed()) {
            mAutoScrollDownDisposable.dispose();
            mAutoScrollDownDisposable = null;
        }
    }

    private void cancleAutoScrollTopDisposable() {
        if (mAutoScrollTopDisposable != null && !mAutoScrollTopDisposable.isDisposed()) {
            mAutoScrollTopDisposable.dispose();
            mAutoScrollTopDisposable = null;
        }
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return 转换结果px
     */
    public static int dip2px(final Context context, final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        final float param = 0.5f;
        return (int) ((dpValue * scale) + param);
    }

    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 得到屏幕高度
     *
     * @return
     */
    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    /**
     * 触屏位置的position和字符串获得
     *
     * @see [类、类#方法、类#成员]
     */
    private String checkTouchLineReadPosition(Reader reader, int selectIndex, View view, int speechPosition) {
        final TextView textView = (TextView) ((LinearLayout) ((LinearLayout) view).getChildAt(1)).getChildAt(0);
        final Layout layout = textView.getLayout();
        String select = null;
        if (layout == null) {
            return "";
        }
        int count = layout.getLineCount();
        for (int j = 0; j < layout.getLineCount(); j++) {
            final Rect bound = new Rect();
            layout.getLineBounds(j, bound);
            final int[] location = new int[2];
            textView.getLocationInWindow(location);
            final int top = location[1];
            final int start = layout.getLineStart(j);
            final int end = layout.getLineEnd(j);

            String str = textView.getText().toString();
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
                        } else {
                            result = one;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (string.contains(result) || TextUtil.punctuationSetEmpty(string).contains(TextUtil.punctuationSetEmpty(result))) {
                    select = string;
                }
            }
//            }
        }
        return select;
    }

    public void setmClickListener(FloatViewOnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface FloatViewOnClickListener {
        void onClick(float x, float y);
    }

    private void showLog(String str) {
        LogUtil.error("[lylog ss]  --->  " + str);
    }
}