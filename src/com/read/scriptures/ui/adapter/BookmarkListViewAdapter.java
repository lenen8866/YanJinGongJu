package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.Bookmark;

import java.util.List;

public class BookmarkListViewAdapter extends EIBaseAdapter<Bookmark> {

    private Context mContext;
    private Holder mHolder;

    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public BookmarkListViewAdapter(Context context, List<Bookmark> list) {
        super(context, list);
        mContext = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            mHolder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_bookmark_item, null);
            mHolder.name = (TextView) view.findViewById(R.id.tv_name);
            mHolder.content = (TextView) view.findViewById(R.id.tv_content);
            mHolder.description = (TextView) view.findViewById(R.id.tv_description);
            mHolder.btndelete = (TextView) view.findViewById(R.id.btn_delete);
            mHolder.btndelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(v);
                    }
                }
            });
            view.setTag(mHolder);
        } else {
            mHolder = (Holder) view.getTag();
        }
        mHolder.btndelete.setTag(position);
        Bookmark bookmark = mList.get(position);
        mHolder.name.setText("《" + bookmark.getVolumeName() + "》" + bookmark.getChapterName());
        mHolder.content.setText("\t\t" + bookmark.getContent());
        mHolder.description.setText(bookmark.getDescription());
        return view;
    }

    private class Holder {
        TextView name;
        TextView content;
        TextView description;
        TextView btndelete;
    }
}
