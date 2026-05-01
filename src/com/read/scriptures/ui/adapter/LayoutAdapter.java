package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.read.scriptures.R;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> implements ListAdapter {
    private static final int COUNT = 100;

    private final Context mContext;
    private final TwoWayView mRecyclerView;
    private List<Integer> mItems;
    private int mCurrentItemId = 0;

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final ImageView title;

        public SimpleViewHolder(View view) {
            super(view);
            title = (ImageView) view.findViewById(R.id.title);
        }
    }

    public LayoutAdapter(Context context, TwoWayView recyclerView) {
        mContext = context;
        mItems = new ArrayList<Integer>(COUNT);
        mRecyclerView = recyclerView;
    }

    public void addItem(int position) {
        final int id = mCurrentItemId++;
        mItems.add(position, id);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void setList(List<Integer> list) {
        this.mItems = list;
        notifyDataSetChanged();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_text_color, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.title.setBackgroundColor(mContext.getResources().getColor(mItems.get(position)));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
