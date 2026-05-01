package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;

import java.util.List;

/**
 * Created by Administrator.
 * Datetime: 2015/7/2.
 * Email: lgmshare@mgail.com
 */
public class SearchHistoryAdapter extends EIBaseAdapter<String> {

    private Context mContext;

    public SearchHistoryAdapter(Context context, List<String> list) {
        super(context, list);
        mContext = context;
    }
    @Override
    public void setList(List<String> list) {
        mList.clear();
        mList.addAll(list);
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.adapter_search_history_item, null);
        }
        final String title = getItem(position);
        TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        ImageView iv_clear =  (ImageView)convertView.findViewById(R.id.iv_clear);
        LinearLayout linear_root =  (LinearLayout) convertView.findViewById(R.id.linear_root);

        tv_title.setText(title);
        linear_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null)
                    listener.onItemClick((AdapterView<?>) parent,view,position,title);
            }
        });

        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ivClickListener!=null)
                    ivClickListener.onIVClicker(position,title);
            }
        });
        return convertView;
    }

    private OnItemOnClickListener listener;

    public void setOnItemClickListener(OnItemOnClickListener listener)
    {
        this.listener = listener;
    }

    public interface OnItemOnClickListener
    {
        void onItemClick(AdapterView<?> parent, View v, int position, String word);
    }

    private OnIVClickListener ivClickListener;

    public void setOnIVClickListener(OnIVClickListener longClickListener)
    {
        this.ivClickListener = longClickListener;
    }

    public interface OnIVClickListener
    {
        void onIVClicker(int position, String value);
    }
}
