package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.Spirituality;

/**
 * Created by LGM.
 * Datetime: 2015/7/11.
 * Email: lgmshare@mgail.com
 */
public class SpiritualityListRecntAdapter extends EIBaseAdapter<Spirituality> {

    private Context mContext;

    public SpiritualityListRecntAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_spir_gv_item_recnt, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTitle.setText(getItem(position).getShowBook().replaceAll("\\[(.*?)\\]",""));
        viewHolder.tvName.setText(getItem(position).getShowName());
        return convertView;
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvName;
    }
}