package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.model.Spirituality;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.widget.sliding.SlidingAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LGM.
 * Datetime: 2015/7/5.
 * Email: lgmshare@mgail.com
 */
public class LinXiuReadSlidingAdapter extends SlidingAdapter<List<String>>
    implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{

    private final Context mCcontext;

    private int mInitPageIndex = 0;

    private int mPageIndex = 0;

    private int mTipsPostion = 0;

    private String mTipsKeyword;
    private String mTipsContent;

    public boolean mTipsValidate = true;

    private Spirituality mSpirituality;
    private List<Spirituality> mSpiritualityList;

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

    private int mLineMargin = 0;
    //文字颜色
    private int mTextColor = Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT);

    public LinXiuReadSlidingAdapter(final Context context, List<Spirituality> mSpiritualityList,int pageIndex)
    {
        if (pageIndex >= mSpiritualityList.size()) {
            pageIndex = mSpiritualityList.size() - 1;
        }
        this.mCcontext = context;
        this.mSpiritualityList = mSpiritualityList;
        this.mPageIndex = pageIndex;
        this.mInitPageIndex = pageIndex;
    }

    public void setSpirituality(final Spirituality mSpirituality){
        this.mSpirituality = mSpirituality;
    }

    public void setPageIndex(final int pageIndex)
    {
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
    public void setTipsContent(String tipsContent) {
        this.mTipsContent = tipsContent;
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

    public void setBackgroudColor(int backgroudColor) {
        this.mBackgroudColor = backgroudColor;
    }

    public void setOnItemClickListener(final AdapterView.OnItemClickListener onItemClickListener)
    {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(final AdapterView.OnItemLongClickListener onItemLongClickListener)
    {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public View getView(View contentView, final List<String> lists)
    {
        ListView listView;
        ChapterReadAdapter chapterReadAdapter;
        if (contentView == null)
        {
            contentView = LayoutInflater.from(mCcontext).inflate(R.layout.adapter_chapter_read_sliding_item, null);
            chapterReadAdapter = new ChapterReadAdapter(mCcontext);
            listView = (ListView)contentView.findViewById(R.id.sliding_listview);
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
            listView.setAdapter(chapterReadAdapter);
        }
        else
        {
            listView = (ListView)contentView.findViewById(R.id.sliding_listview);
            chapterReadAdapter = (ChapterReadAdapter)listView.getAdapter();
        }
        chapterReadAdapter.setList(lists);
        chapterReadAdapter.setReadModel(mReadModel);
        chapterReadAdapter.setTextSize(mTextSize);
        chapterReadAdapter.setTextMargin(mTextMagin);
        chapterReadAdapter.setLineMargin(mLineMargin);
//        chapterReadAdapter.setTopAndBottomMargin(mTopAndBottomMargin);
        chapterReadAdapter.setTextAroundMargin(mTextAround);
        chapterReadAdapter.setTextColor(mTextColor);
        //关键字提示
        if ((mPageIndex == mInitPageIndex) && mTipsValidate)
        {
            mTipsValidate = false;
            chapterReadAdapter.setTipsPostion(mTipsPostion);
            chapterReadAdapter.setTipsKeyword(mTipsKeyword);
            chapterReadAdapter.setTipsContent(mTipsContent);
            listView.setSelection(mTipsPostion);
        }
        else
        {
            chapterReadAdapter.setTipsPostion(-1);
            chapterReadAdapter.setTipsKeyword(null);
            listView.setSelection(0);
        }
        chapterReadAdapter.notifyDataSetChanged();

        //阅读模式
        if (mReadModel == SystemConfig.READ_MODEL_NIGHT)
        {
            listView.setBackgroundColor(Color.parseColor(SystemConfig.DEFAULT_READ_BACKGROUND_NIGHT));
        }
        else
        {
            listView.setBackgroundColor(mBackgroudColor);
        }
        return contentView;
    }

    @Override
    public boolean hasNext()
    {
        return mPageIndex < mSpiritualityList.size() - 1;
    }

    @Override
    protected void computeNext()
    {
        ++mPageIndex;
    }

    @Override
    protected void computePrevious()
    {
        --mPageIndex;
    }

    @Override
    public boolean hasPrevious()
    {
        return mPageIndex > 0;
    }

    @Override
    public List<String> getPrevious()
    {
        return getContentList(mPageIndex - 1);
    }

    @Override
    public List<String> getNext()
    {
        return getContentList(mPageIndex + 1);
    }

    @Override
    public List<String> getCurrent()
    {
        return getContentList(mPageIndex);
    }

    private List<String> getContentList( int index)
    {
        List<String> content = null;
        if (mSpiritualityList != null && !mSpiritualityList.isEmpty())
        {
            if(index >= mSpiritualityList.size()){
                index = mSpiritualityList.size() -1;
            }
            content = SearchTextUtil.querySpiritualityContent(mCcontext,
                mSpiritualityList.get(index),
                HuDongApplication.getInstance().getTextModel());
        }
        if (content == null)
        {
            content = new ArrayList<String>();
        }
        return content;
    }

    public ListView getCurrentListView()
    {
        final View view = getCurrentView();
        final ListView listView = (ListView)view.findViewById(R.id.sliding_listview);
        return listView;
    }

    public ChapterReadAdapter getCurrentChapterReadAdapter()
    {
        final View view = getCurrentView();
        final ListView listView = (ListView)view.findViewById(R.id.sliding_listview);
        final ChapterReadAdapter adapter = (ChapterReadAdapter)listView.getAdapter();
        return adapter;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
    {
        mOnItemClickListener.onItemClick(parent, view, position, id);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id)
    {
        mOnItemLongClickListener.onItemLongClick(parent, view, position, id);
        return false;
    }
}