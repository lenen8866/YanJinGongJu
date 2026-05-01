package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.read.scriptures.R;

import java.util.List;

/**
 * Created by Administrator.
 * Datetime: 2015/7/6.
 * Email: lgmshare@mgail.com
 */
public class ChapterReadMenuGvAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Integer> mPictures;
    private List<String> mTitles;

    public ChapterReadMenuGvAdapter(Context context, List<Integer> pictures, List<String> titles){
        this.mContext = context;
        this.mPictures = pictures;
        this.mTitles = titles;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mPictures.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int picture = mPictures.get(position);
        String title = mTitles.get(position);
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_chapter_read_menu_item, null);
            viewHolder.imageView = convertView.findViewById(R.id.grid_img);
            viewHolder.textView = convertView.findViewById(R.id.grid_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.imageView.setBackgroundResource(picture);
        viewHolder.textView.setText(title);
        return convertView;
    }

    private final class ViewHolder{
        public ImageView imageView;
        public TextView textView;
    }
}