package com.read.scriptures.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.bean.AuthorTitleBean;
import com.read.scriptures.view.indexablerv.IndexableAdapter;

import org.jetbrains.annotations.NotNull;


/**
 * Created by YoKey on 16/10/7.
 */
public class TitleAuthorAdapter extends IndexableAdapter<AuthorTitleBean> {
    private LayoutInflater mInflater;

    public TitleAuthorAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_book_cate, parent, false);
        return new IndexVH(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_title_author, parent, false);
        return new ContentVH(view);
    }

    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
        IndexVH vh = (IndexVH) holder;
        vh.tv_title.setText(indexTitle);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, AuthorTitleBean entity) {
        ContentVH vh = (ContentVH) holder;
        vh.tv_title.setText(entity.author);
        vh.view_line.setVisibility(TextUtils.equals(entity.author, author) ? View.VISIBLE : View.GONE);
    }

    private String author;

    public void setCurrentAuthor(@NotNull String author) {
        this.author = author;
    }

    private class IndexVH extends RecyclerView.ViewHolder {
        TextView tv_title;

        public IndexVH(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

    private class ContentVH extends RecyclerView.ViewHolder {
        TextView tv_title;
        View view_line;

        public ContentVH(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            view_line = itemView.findViewById(R.id.view_line);
        }
    }
}
