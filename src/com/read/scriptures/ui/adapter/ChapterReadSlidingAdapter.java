package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
    private int mReadModel = 0;
    private int mTextSize = 24;
    private int mTextModel = 0;
    private int mBackgroudColor = 0;
    private int mTextMagin = 0;
    private int mTextAround = 10;
    private boolean isSJmode;
    private int mLineMargin = 0;
    private int HUAI_ZHU_CHAPTER = 0;
    private int HUAI_ZHU_CHAPTER_HAS_ZW = 0;
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
        ChapterReadAdapter chapterReadAdapter;
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
        listView.setVerticalScrollbarPosition(srollSetting == 0 ? 1 : 2);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onMoveEventlister != null) onMoveEventlister.onMove();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                long curtime = System.currentTimeMillis();
                if (curtime - saveTime < 20) return;
                saveTime = curtime;
                if (onMoveEventlister != null) onMoveEventlister.onMove();
            }
        });

        chapterReadAdapter.setList(lists);
        chapterReadAdapter.setShengJing(isSJmode);
        chapterReadAdapter.setReadModel(mReadModel);
        chapterReadAdapter.setTextSize(mTextSize);
        chapterReadAdapter.setTextMargin(mTextMagin);
        chapterReadAdapter.setLineMargin(mLineMargin);
        chapterReadAdapter.setTextAroundMargin(mTextAround);
        chapterReadAdapter.setTextColor(mTextColor);

        // 当前页是目标章节页时，传递搜索高亮信息
        if (mPageIndex == mInitPageIndex && mTipsValidate) {
            String keyword = mTextModel == SystemConfig.TEXT_MODEL_NORMAL
                    ? mTipsKeyword
                    : SearchTextUtil.jian2fan(mTipsKeyword);
            String chapterContent = mTextModel == SystemConfig.TEXT_MODEL_NORMAL
                    ? mTipsContent          // 用 tipsContent（命中行内容）做定位依据
                    : SearchTextUtil.jian2fan(mTipsContent);

            chapterReadAdapter.setTipsKeyword(keyword);
            // 用命中行内容在渲染列表里找到精确行号，传给 adapter
            int targetPosition = findTargetPosition(lists, chapterContent, keyword);
            chapterReadAdapter.setTipsPostion(targetPosition);
            chapterReadAdapter.setSearchType(mSearchType);
            chapterReadAdapter.setChapterName(mChapterName);
            chapterReadAdapter.setChapterNameKeyWord(mChapterNameKeyWord);
            chapterReadAdapter.setTipsContent(mTipsContent);

            // 滚动到目标行
            final int scrollTo = targetPosition >= 0 ? targetPosition : 0;
            final FlexiListView finalListView = listView;
            finalListView.post(new Runnable() {
                @Override
                public void run() {
                    finalListView.setSelection(scrollTo);
                }
            });
        } else {
            chapterReadAdapter.setTipsPostion(-1);
            chapterReadAdapter.setTipsKeyword(null);
            listView.setSelection(0);
        }

        chapterReadAdapter.setTextModel(getTextModel());
        chapterReadAdapter.notifyDataSetChanged();

        if (mReadModel == SystemConfig.READ_MODEL_NIGHT) {
            listView.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_NIGHT));
        } else {
            listView.setBackgroundColor(mBackgroudColor);
        }
        return contentView;
    }

    /**
     * 在渲染后的行列表里，用命中行内容找到精确的 position。
     * 先用 tipsContent 完整匹配，找不到再用 keyword 找第一个命中行。
     */
    private int findTargetPosition(List<String> lists, String tipsContent, String keyword) {
        if (lists == null || lists.isEmpty()) return 0;

        // 1. 用命中行内容精确定位
        if (!TextUtils.isEmpty(tipsContent)) {
            String normalTips = normalize(tipsContent);
            for (int i = 0; i < lists.size(); i++) {
                String normalLine = normalize(lists.get(i));
                if (normalLine.contains(normalTips) || normalTips.contains(normalLine)) {
                    return i;
                }
            }
        }

        // 2. 退化：找第一个包含关键词的行
        if (!TextUtils.isEmpty(keyword)) {
            String[] keys = keyword.split(" ");
            for (int i = 0; i < lists.size(); i++) {
                String line = lists.get(i);
                boolean allMatch = true;
                for (String k : keys) {
                    if (!TextUtils.isEmpty(k) && !line.contains(k)) {
                        allMatch = false;
                        break;
                    }
                }
                if (allMatch) return i;
            }
        }

        return 0;
    }

    private String normalize(String s) {
        if (TextUtils.isEmpty(s)) return "";
        return s.replaceAll("<.+?>", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("\u3000", "")
                .replace(" ", "")
                .trim();
    }

    private long saveTime;

    public interface OnMoveEventlister {
        void onMove();
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

    private final HashMap<Integer, List<String>> mContentCache = new HashMap<>();
    private String mLastVersionsKey = "";

    private String buildVersionsKey() {
        List<String> v = HuDongApplication.mVersions;
        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) v = HuDongApplication.mVersions_HZ;
        if (v == null) return "";
        java.util.ArrayList<String> sorted = new ArrayList<>(v);
        Collections.sort(sorted);
        return sorted.toString();
    }

    private List<String> getContentList(int index) {
        String currentKey = buildVersionsKey();
        if (!currentKey.equals(mLastVersionsKey)) {
            mContentCache.clear();
            mLastVersionsKey = currentKey;
        }
        if (mContentCache.containsKey(index)) {
            return mContentCache.get(index);
        }
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
            content = new ArrayList<>();
        }
        mContentCache.put(index, content);
        return content;
    }

    public ListView getCurrentListView() {
        View view = getCurrentView();
        return (ListView) view.findViewById(R.id.sliding_listview);
    }

    public ChapterReadAdapter getCurrentChapterReadAdapter() {
        View view = getCurrentView();
        ListView listView = (ListView) view.findViewById(R.id.sliding_listview);
        return (ChapterReadAdapter) listView.getAdapter();
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
