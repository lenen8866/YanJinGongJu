package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.bean.LevelActiveInfo;
import com.read.scriptures.ui.fragment.FragmentActive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 支付方式选择
 */
public class LevelActivePayTypeAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private int currentCheckedIndex = 0;
    List<HashMap<String,Object>> dataList = new ArrayList<>();

    public LevelActivePayTypeAdapter(Context context) {
        initData();
        layoutInflater = LayoutInflater.from(context);
    }

    private void initData() {
        this.dataList.clear();
        HashMap<String,Object> data1 = new HashMap<>();
        data1.put("name","支付宝");
        data1.put("icon",R.drawable.ic_alipay);
        data1.put("type", FragmentActive.PAY_TYPE_ALIPAY);
        HashMap<String,Object> data2 = new HashMap<>();
        data2.put("name","微信");
        data2.put("icon",R.drawable.ic_weichat_pay);
        data2.put("type", FragmentActive.PAY_TYPE_WEICHAT);
        this.dataList.add(data1);
        this.dataList.add(data2);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public HashMap<String,Object> getItem(int position) {
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
            convertView = layoutInflater.inflate(R.layout.fragment_active_pay_type, null);
            holder = new MyViewHolder();
            holder.rlRootView = (RelativeLayout) convertView.findViewById(R.id.rl_root_view);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ic_type);
            holder.rbtnChecked= (RadioButton) convertView.findViewById(R.id.rbtn_checked);
            holder.viewLine = (View)convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        holder.tvName.setText(getItem(position).get("name").toString());
        holder.ivIcon.setImageResource(Integer.valueOf(getItem(position).get("icon").toString()));

        holder.rlRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCheckedIndex = position;
                notifyDataSetChanged();
            }
        });
        holder.rbtnChecked.setChecked(currentCheckedIndex == position);
        holder.viewLine.setVisibility(position == getCount() -1 ? View.GONE : View.VISIBLE);
        return convertView;
    }


    class MyViewHolder {
        private RelativeLayout rlRootView;
        private TextView tvName;
        private ImageView ivIcon;
        private RadioButton rbtnChecked;
        private View viewLine;

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


    public String getCurrentCheckedType() {
        return dataList.get(currentCheckedIndex).get("type").toString();
    }

    public void setCurrentCheckedType(String type) {
        for (int i = 0; i < getCount(); i++) {
            if (dataList.get(i).get("type").toString().equals(type)){
                currentCheckedIndex = i;
                break;
            }
        }
        notifyDataSetChanged();
    }
}
