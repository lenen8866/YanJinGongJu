package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.Volume;

import java.util.List;

/**
 * Created by Administrator.
 * Datetime: 2015/7/2.
 * Email: lgmshare@mgail.com
 */
public class VolumeListAdapter extends EIBaseAdapter<Volume> {

    public VolumeListAdapter(Context context) {
        super(context);
    }
    public VolumeListAdapter(Context context, List<Volume> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.adapter_volume_item, null);
        }
        TextView city = (TextView) convertView.findViewById(R.id.tv_title);
        Volume item = getItem(position);
        city.setText(item.getVolName().replaceAll("^\\d{1,}-",""));
        return convertView;
    }
}
