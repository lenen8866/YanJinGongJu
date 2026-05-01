package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;

import java.util.List;

public class SearchHistoryListAdapter extends EIBaseAdapter<String> {

    private Holder holder;

    public SearchHistoryListAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            holder = new Holder();
            view = getLayoutInflater().inflate(R.layout.adapter_search_history_item, null);
            holder.name = (TextView) view.findViewById(R.id.tv_title);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.name.setText(getItem(position));
        return view;
    }

    private class Holder {
        TextView name;
    }
}
