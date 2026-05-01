package com.read.scriptures.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.bean.RechargeBean;

import java.util.ArrayList;

public class RechargeListAdapter extends RecyclerView.Adapter<RechargeListAdapter.RechargeListViewHolder> {

    String[] priceList = {"10", "20", "50", "70", "77", "97"};

    public ArrayList<RechargeBean> getRechargeBeans() {
        return rechargeBeans;
    }

    ArrayList<RechargeBean> rechargeBeans = new ArrayList<>();

    public RechargeListAdapter() {
        for (int i = 0; i < priceList.length; i++) {
            RechargeBean rechargeBean = new RechargeBean();
            rechargeBean.price = priceList[i];
            if (i == 0) {
                rechargeBean.isSelected = true;
            }
            rechargeBeans.add(rechargeBean);
        }
    }

    @NonNull
    @Override
    public RechargeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RechargeListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recharge, parent, false));
    }

    int lastIndex = -1;

    @Override
    public void onBindViewHolder(@NonNull RechargeListViewHolder holder, int position) {
        holder.tv_price.setText("¥ " + rechargeBeans.get(position).price);
        holder.tv_price.setBackgroundResource(rechargeBeans.get(position).isSelected ? R.drawable.dialog_recharge_item_bg : 0);
        if (clickListener != null) {
            holder.tv_price.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(rechargeBeans, position);
                    if (lastIndex != -1) {
                        rechargeBeans.get(lastIndex).isSelected = false;
                    }
                    rechargeBeans.get(position).isSelected = true;
                    lastIndex = position;
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return rechargeBeans.size();
    }

    OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(ArrayList<RechargeBean> rechargeBean, int position);
    }

    class RechargeListViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_price;

        public RechargeListViewHolder(View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }
}
