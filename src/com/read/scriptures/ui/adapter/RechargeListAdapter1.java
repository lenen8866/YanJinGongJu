package com.read.scriptures.ui.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.read.scriptures.R;

public class RechargeListAdapter1 extends RecyclerView.Adapter<RechargeListAdapter1.RechargeListViewHolder> {

    String[] priceList = {"27", "77", "127", "157", "227", "377", "500", "777", "1000"};

    @NonNull
    @Override
    public RechargeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RechargeListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recharge1, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RechargeListViewHolder holder, int position) {
        holder.tv_price.setText("¥ " + priceList[position]);
        holder.tv_price.setBackgroundResource(TextUtils.equals(priceList[position], price) ? resId : R.drawable.dialog_recharge_item_bg2);
        holder.tv_price.setTextColor(TextUtils.equals(priceList[position], price) ? Color.WHITE : Color.BLACK);
        if (clickListener != null) {
            holder.tv_price.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(priceList[position]);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return priceList.length;
    }

    OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    private String price;

    public void setPrice(String price) {
        this.price = price;
        notifyDataSetChanged();
    }

    private int resId = R.drawable.dialog_recharge_item_bg1;

    public void setRechargeTypeWithBg(int resId) {
        this.resId = resId;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        void onItemClick(String price);
    }

    class RechargeListViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_price;

        public RechargeListViewHolder(View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }
}
