package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.EIUtils.ViewHolder;

import java.util.List;

/**
 * @author lim
 * @Description: 绑定到ViewHolder的基础类
 * @mail lgmshare@gmail.com
 * @date 2014年7月7日  上午11:04:44
 */
public abstract class BaseHolderAdapter<T> extends EIBaseAdapter<T> {

    protected Context mContext;

    protected final int mItemLayoutId;

    public BaseHolderAdapter(Context context, int itemLayoutId) {
        super(context);
        mContext = context;
        mItemLayoutId = itemLayoutId;
    }

    public BaseHolderAdapter(Context context, List<T> list, int itemLayoutId) {
        super(context, list);
        mContext = context;
        mItemLayoutId = itemLayoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
        convert(viewHolder, position, getItem(position));
        return viewHolder.getConvertView();

    }

    public abstract void convert(ViewHolder helper, int position, T item);

    private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }

}
