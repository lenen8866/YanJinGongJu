package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.read.scriptures.EIUtils.DateUtil;
import com.read.scriptures.R;
import com.read.scriptures.db.SpiritualityDatabaseHepler;
import com.read.scriptures.model.Spirituality;
import com.read.scriptures.util.TimeUtils;
import com.read.scriptures.widget.sliding.SlidingAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LGM.
 * Datetime: 2015/7/5.
 * Email: lgmshare@mgail.com
 */
public class SpiritualitySlidingAdapter extends SlidingAdapter<List<Spirituality>> implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private Context mCcontext;
    private int mPageIndex = 0;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;
    private String type = "";
    private String mCurrentDate;

    public SpiritualitySlidingAdapter(Context context) {
        this.mCcontext = context;
    }

    public void setDataType(String type){
        this.type = type;
    }

    public void setCurrentDate(String currentDate) {
        this.mCurrentDate = currentDate;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.mPageIndex = pageIndex;
    }

    @Override
    public View getView(View contentView, List<Spirituality> lists) {
        GridView listView;
        SpiritualityListAdapter chapterReadAdapter;
//        if (contentView == null) {
            contentView = LayoutInflater.from(mCcontext).inflate(R.layout.adapter_spirituality_sliding_item, null);
            chapterReadAdapter = new SpiritualityListAdapter(mCcontext);
            listView = (GridView) contentView.findViewById(R.id.sliding_gridview);
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
            listView.setAdapter(chapterReadAdapter);
//        } else {
//            listView = (GridView) contentView.findViewById(R.id.sliding_gridview);
//            chapterReadAdapter = (SpiritualityListAdapter) listView.getAdapter();
//        }
        chapterReadAdapter.setList(lists);
        chapterReadAdapter.notifyDataSetChanged();

        return contentView;
    }

    @Override
    public boolean hasNext() {
        return true;
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
        return true;
    }

    @Override
    public List<Spirituality> getPrevious() {
        return getContentList(mPageIndex - 1,type);
    }

    @Override
    public List<Spirituality> getNext() {
        return getContentList(mPageIndex + 1,type);
    }

    @Override
    public List<Spirituality> getCurrent() {
        List<Spirituality> list = new ArrayList<>();
        list = getContentList(mPageIndex,type);
        if (list.size() == 0){
            listener.updateUi(true);
        }else {
            listener.updateUi(false);
        }
        return list;
    }

    private List<Spirituality> getContentList(int index,String type) {
        List<Spirituality> content = null;
//        String date = DateUtil.getNextDay(mCurrentDate, "yyyy年MM月dd日", index);

        if (type.contains("每日")){
            String date = TimeUtils.getOurSelData(index);
            content = new SpiritualityDatabaseHepler(mCcontext).getSpiritualityListByDaytime(date,type);
        }else if (type.contains("每周")){
            String date = TimeUtils.getWeekSelData(index);
            content = new SpiritualityDatabaseHepler(mCcontext).getSpiritualityListByWeektime(date,type);
        }else if (type.contains("每年")){
            String date = TimeUtils.getWeekSelData(index);
            content = new SpiritualityDatabaseHepler(mCcontext).getSpiritualityListByYear(date,type);
        }

        if (content == null) {
            content = new ArrayList<Spirituality>();
        }
        return content;
    }

    public String getCurrentShowDate(){
        return DateUtil.getNextDay(mCurrentDate, "yyyy-MM-dd", mPageIndex);
    }

    public SpiritualityListAdapter getCurrentSpiritualityListAdapter() {
        View view = getCurrentView();
        GridView listView = (GridView) view.findViewById(R.id.sliding_gridview);
        SpiritualityListAdapter adapter = (SpiritualityListAdapter) listView.getAdapter();
        return adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(parent, view, position, id);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnItemLongClickListener != null){
            mOnItemLongClickListener.onItemLongClick(parent, view, position, id);
        }
        return false;
    }

    private updateUi listener;

    public void updateUi(updateUi listener)
    {
        this.listener = listener;
    }

    public interface updateUi
    {
        void updateUi(boolean show);
    }
}