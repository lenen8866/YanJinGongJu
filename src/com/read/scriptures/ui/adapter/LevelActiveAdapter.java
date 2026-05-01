package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.bean.LevelActiveInfo;
import com.read.scriptures.util.AmountUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 激活信息列表显示
 */
public class LevelActiveAdapter extends BaseAdapter {

    private List<LevelActiveInfo> dataList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private int currentCheckedIndex = 0;

    public LevelActiveAdapter(Context context, List<LevelActiveInfo> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public LevelActiveInfo getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_level_active_item, null);
            holder = new MyViewHolder();
            holder.rlRootView = (RelativeLayout) convertView.findViewById(R.id.rl_root_view);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tvOriginPrice = (TextView) convertView.findViewById(R.id.tv_origin_price);
            holder.tvDay= (TextView) convertView.findViewById(R.id.tv_day);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        LevelActiveInfo levelActiveInfo = dataList.get(position);
        if (levelActiveInfo.getDiscount() == 0 || levelActiveInfo.getDiscount() > levelActiveInfo.getPayments()) {
            holder.tvOriginPrice.setVisibility(View.GONE);
        } else {
            holder.tvOriginPrice.setVisibility(View.VISIBLE);
            holder.tvOriginPrice.setText("¥"+ AmountUtils.getAmountToStr(getItem(position).getPayments()));
            holder.tvPrice.setText(AmountUtils.getAmountToStr(getItem(position).getDiscount()));
        }

        holder.tvDay.setText(getItem(position).getDescStr());
        holder.tvOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰

        holder.rlRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentCheckedIndex(position);
                if (listener != null)
                    listener.onItemClick(view, position, dataList.get(position));
            }
        });
        holder.rlRootView.setBackgroundResource(currentCheckedIndex == position ? R.drawable.shape_level_price_checked_bg : R.drawable.shape_level_price_unchecked_bg);
        return convertView;
    }


    class MyViewHolder {
        private RelativeLayout rlRootView;
        private TextView tvPrice;
        private TextView tvOriginPrice;
        private TextView tvDay;

    }

    private OnItemOnClickListener listener;


    public void setOnItemClickListener(OnItemOnClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemOnClickListener {
        void onItemClick(View v, int position, LevelActiveInfo levelActiveInfo);
    }

    public OnItemOnClickListener getListener() {
        return listener;
    }

    public int getCurrentCheckedIndex() {
        return currentCheckedIndex;
    }

    public void setCurrentCheckedIndex(int currentCheckedIndex) {
        this.currentCheckedIndex = currentCheckedIndex;
        notifyDataSetChanged();
    }
    public void addAllList(List<LevelActiveInfo> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }
}
