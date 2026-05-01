package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.widget.FlexiListView;
import com.read.scriptures.widget.sliding.SlidingAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LGM.
 * Datetime: 2015/7/5.
 * Email: lgmshare@mgail.com
 */
public class ChapterReadSlidingAdapter extends SlidingAdapter<List<String>> implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private Context mCcontext;
    private List<Chapter> mChapters;
    private int mInitPageIndex = 0;
    private int mPageIndex = 0;
    private int mTipsPostion = 0;
    private String mTipsKeyword;
    private String mTipsContent;
    private int mSearchType;
    private String mChapterName;
    private String mChapterContent;
    private String mChapterNameKeyWord;
    public boolean mTipsValidate = true;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;
    //阅读模式
    private int mReadModel = 0;
    //文字大小
    private int mTextSize = 24;
    //简繁模式
    private int mTextModel = 0;
    //阅读背景色
    private int mBackgroudColor = 0;
    //阅读字段行间距
    private int mTextMagin = 0;

    private int mTextAround = 10;

    private int mTopAndBottomMargin = 0;

    private boolean isSJmode;

    private int mLineMargin = 0;

    private int HUAI_ZHU_CHAPTER = 0; //怀著标致
    private int HUAI_ZHU_CHAPTER_HAS_ZW = 0; //怀著标致中文  1
    //文字颜色
    private int mTextColor = Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT);

    public ChapterReadSlidingAdapter(Context context, List<Chapter> chapterList, int pageIndex, boolean isSJmode, int flag, int flag1) {
        if (pageIndex >= chapterList.size()) {
            pageIndex = chapterList.size() - 1;
        }
        this.mCcontext = context;
        this.mChapters = chapterList;
        this.mPageIndex = chapterList.size() <= pageIndex ? 0 : pageIndex;
        this.mInitPageIndex = pageIndex;
        this.isSJmode = isSJmode;
        this.HUAI_ZHU_CHAPTER = flag;
        this.HUAI_ZHU_CHAPTER_HAS_ZW = flag1;
    }

    public void setChapters(List<Chapter> mChapters) {
        this.mChapters = mChapters;
    }

    public void setPageIndex(int pageIndex) {
        this.mPageIndex = pageIndex;
        this.mInitPageIndex = pageIndex;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setTipsPostion(int tipsPostion) {
        this.mTipsPostion = tipsPostion;
    }

    public void setTipsKeyword(String tipsKeyword) {
        this.mTipsKeyword = tipsKeyword;
    }

    public void setTipContent(String tipsContent) {
        this.mTipsContent = tipsContent;
    }

    public void setSearchType(int mSearchType) {
        this.mSearchType = mSearchType;
    }

    public void setChapterName(String mChapterName) {
        this.mChapterName = mChapterName;
    }

    public void setChapterContent(String chapterContent) {
        this.mChapterContent = chapterContent;
    }

    public void setChapterNameKeyWord(String mChapterNameKeyWord) {
        this.mChapterNameKeyWord = mChapterNameKeyWord;
    }

    public void setReadModel(int readModel) {
        this.mReadModel = readModel;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public void setTextMargin(int textMargin) {
        this.mTextMagin = textMargin;
    }
//    public void setTopAndBottomMargin(int mTopAndBottomMargin) {
//        this.mTopAndBottomMargin = mTopAndBottomMargin;
//    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public void setTextAroundMargin(int textMargin) {
        this.mTextAround = textMargin;
    }

    public void setLineMargin(int mLineMargin) {
        this.mLineMargin = mLineMargin;
    }

    public void setTextModel(int textModel) {
        this.mTextModel = textModel;
    }

    public int getTextModel() {
        return mTextModel;
    }

    public void setBackgroudColor(int backgroudColor) {
        this.mBackgroudColor = backgroudColor;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public View getView(View contentView, List<String> lists) {
        FlexiListView listView;
        LinearLayout linearLayout;
        ChapterReadAdapter chapterReadAdapter;//===========================================================================
        if (contentView == null) {
            contentView = LayoutInflater.from(mCcontext).inflate(R.layout.adapter_chapter_read_sliding_item, null);
            chapterReadAdapter = new ChapterReadAdapter(mCcontext);
            chapterReadAdapter.setFlag(HUAI_ZHU_CHAPTER_HAS_ZW);
            listView = (FlexiListView) contentView.findViewById(R.id.sliding_listview);
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
            listView.setAdapter(chapterReadAdapter);
        } else {
            listView = (FlexiListView) contentView.findViewById(R.id.sliding_listview);
            chapterReadAdapter = (ChapterReadAdapter) listView.getAdapter();
        }

        int srollSetting = SharedUtil.getInt(PreferenceConfig.Preference_read_sroll_setting, 0);
        if (srollSetting == 0) {
            listView.setVerticalScrollbarPosition(1);
        } else {
            listView.setVerticalScrollbarPosition(2);
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onMoveEventlister != null) {
                    onMoveEventlister.onMove();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                long curtime = System.currentTimeMillis();
                if (curtime - saveTime < 20) {
                    return;
                }
                saveTime = curtime;

                if (onMoveEventlister != null) {
                    onMoveEventlister.onMove();
                }
            }
        });


//        LinearLayout.LayoutParams lp1 =new LinearLayout.LayoutParams( listView.getLayoutParams());
//        lp1.setMargins(0,mTopAndBottomMargin,0,mTopAndBottomMargin);
//        listView.setLayoutParams(lp1);
//        listView.setPadding(0,mTopAndBottomMargin,0,mTopAndBottomMargin);
//        linearLayout.setBackgroundColor(mBackgroudColor);

        chapterReadAdapter.setList(lists);
        chapterReadAdapter.setShengJing(isSJmode);
        chapterReadAdapter.setReadModel(mReadModel);
        chapterReadAdapter.setTextSize(mTextSize);
        chapterReadAdapter.setTextMargin(mTextMagin);
        chapterReadAdapter.setLineMargin(mLineMargin);
//        chapterReadAdapter.setTopAndBottomMargin(mTopAndBottomMargin);
        chapterReadAdapter.setTextAroundMargin(mTextAround);
        chapterReadAdapter.setTextColor(mTextColor);
        //关键字提示
        if (mPageIndex == mInitPageIndex && mTipsValidate) {
//            mTipsValidate = false;
            chapterReadAdapter.setTipsPostion(mTipsPostion);
            if (mTextModel == SystemConfig.TEXT_MODEL_NORMAL) {
                chapterReadAdapter.setTipsKeyword(mTipsKeyword);
                chapterReadAdapter.setChapterContent(mChapterContent);
            } else {
                chapterReadAdapter.setTipsKeyword(SearchTextUtil.jian2fan(mTipsKeyword));
                chapterReadAdapter.setChapterContent(SearchTextUtil.jian2fan(mChapterContent));
            }
            chapterReadAdapter.setSearchType(mSearchType);
            chapterReadAdapter.setChapterName(mChapterName);
            chapterReadAdapter.setChapterNameKeyWord(mChapterNameKeyWord);
            chapterReadAdapter.setTipsContent(mTipsContent);
            if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
//                if (HuDongApplication.mVersions_HZ.size() == 2) {
//                    listView.setSelection(mTipsPostion);
//                } else {
//                    listView.setSelection(mTipsPostion / 2);
//                }
                listView.setSelection(mTipsPostion);
            } else {
                ;
                listView.setSelection(mTipsPostion);
            }
        } else {
            chapterReadAdapter.setTipsPostion(-1);
            chapterReadAdapter.setTipsKeyword(null);
            listView.setSelection(0);
        }
        chapterReadAdapter.setTextModel(getTextModel());
        chapterReadAdapter.notifyDataSetChanged();

        //阅读模式
        if (mReadModel == SystemConfig.READ_MODEL_NIGHT) {
            listView.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_NIGHT));
        } else {
            listView.setBackgroundColor(mBackgroudColor);
        }
        return contentView;
    }

    private long saveTime;

    public interface OnMoveEventlister {
        public void onMove();
    }

    public OnMoveEventlister getOnMoveEventlister() {
        return onMoveEventlister;
    }

    public ChapterReadSlidingAdapter setOnMoveEventlister(OnMoveEventlister onMoveEventlister) {
        this.onMoveEventlister = onMoveEventlister;
        return this;
    }

    private OnMoveEventlister onMoveEventlister;


    @Override
    public boolean hasNext() {
        return mPageIndex < mChapters.size() - 1;
    }

    @Override
    protected void computeNext() {
        ++mPageIndex;
    }

    @Override
    protected void computePrevious() {
        --mPageIndex;
    }

    @Override
    public boolean hasPrevious() {
        return mPageIndex > 0;
    }

    @Override
    public List<String> getPrevious() {
        return getContentList(mPageIndex - 1);
    }

    @Override
    public List<String> getNext() {
        return getContentList(mPageIndex + 1);
    }

    @Override
    public List<String> getCurrent() {
        return getContentList(mPageIndex);
    }

    // 修复：缓存章节内容，避免每次 notifyDataSetChanged 都重新解析
    // 原来每次刷新都调用 queryChaptreContent 全量重新解析每一行，包含 jian2fan 繁体转换（IO 操作）
    // 现改为：版本列表变化时才重新计算，相同版本状态下直接返回缓存
    private final HashMap<Integer, List<String>> mContentCache = new HashMap<>();
    private String mLastVersionsKey = "";

    private String buildVersionsKey() {
        // 用版本列表拼接成一个 key，判断版本是否发生变化
        List<String> v = HuDongApplication.mVersions;
        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) v = HuDongApplication.mVersions_HZ;
        if (v == null) return "";
        java.util.ArrayList<String> sorted = new ArrayList<>(v);
        Collections.sort(sorted);
        return sorted.toString();
    }

    private List<String> getContentList(int index) {
        // 版本发生变化时清除缓存
        String currentKey = buildVersionsKey();
        if (!currentKey.equals(mLastVersionsKey)) {
            mContentCache.clear();
            mLastVersionsKey = currentKey;
        }
        // 命中缓存直接返回
        if (mContentCache.containsKey(index)) {
            return mContentCache.get(index);
        }
        // 未命中才真正计算
        List<String> content = null;
        if (mChapters != null) {
            if (index >= mChapters.size()) {
                index = mChapters.size() - 1;
            }
            if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
                content = SearchTextUtil.queryChaptreContent(mCcontext, mChapters.get(index), mTextModel, HUAI_ZHU_CHAPTER_HAS_ZW);
            } else {
                content = SearchTextUtil.queryChaptreContent(mCcontext, mChapters.get(index), mTextModel);
            }
        }
        if (content == null) {
            content = new ArrayList<String>();
        }
        mContentCache.put(index, content);
        return content;
    }

    public ListView getCurrentListView() {
        View view = getCurrentView();
        ListView listView = (ListView) view.findViewById(R.id.sliding_listview);
        return listView;
    }

    public ChapterReadAdapter getCurrentChapterReadAdapter() {
        View view = getCurrentView();
        ListView listView = (ListView) view.findViewById(R.id.sliding_listview);
        ChapterReadAdapter adapter = (ChapterReadAdapter) listView.getAdapter();
        return adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mOnItemClickListener.onItemClick(parent, view, position, id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mOnItemLongClickListener.onItemLongClick(parent, view, position, id);
        return false;
    }

    public void setShengJing(boolean isSJmode) {
    }


}