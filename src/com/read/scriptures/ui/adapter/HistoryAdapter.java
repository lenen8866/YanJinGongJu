package com.read.scriptures.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.bean.HistoryBean;
import com.read.scriptures.util.TimeUtils;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private List<HistoryBean> dataList;

    private LayoutInflater layoutInflater;

    public HistoryAdapter(Context context, List<HistoryBean> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setDataList(List<HistoryBean> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv_title.setText(dataList.get(position).getVolumeName().replaceAll("\\((.*?)\\)","").replaceAll("\\[(.*?)\\]","")
        .replaceAll("\\{(.*?)\\}",""));
        holder.tv_chapter.setText(dataList.get(position).getChapter());
        holder.tv_time.setText(TimeUtils.timeStamp2DateNoSecond(Long.valueOf(dataList.get(position).getTime())));
        holder.ll_item_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onItemClick(view,position,dataList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_item_click;
        private TextView tv_title;
        private TextView tv_chapter;
        private TextView tv_time;

        MyViewHolder(View itemView) {
            super(itemView);
            ll_item_click = (LinearLayout) itemView.findViewById(R.id.item_click);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_chapter = (TextView) itemView.findViewById(R.id.tv_chapter);
            tv_time = (TextView) itemView.findViewById(R.id.tv_book_name);
        }
    }

    private OnItemOnClickListener listener;

    public void setOnItemClickListener(OnItemOnClickListener listener)
    {
        this.listener = listener;
    }

    public interface OnItemOnClickListener
    {
        void onItemClick(View v, int position, HistoryBean historyBean);
    }
}
