package com.read.scriptures.adapter;

import android.content.Context;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.bean.AnnItemInfo;

import java.util.List;


/**
 * Created by Administrator on 2016/6/2.
 * 头部tab GridView adapter
 */
public class AnnTabAdapter extends BaseAdapter {
    private Context context;
    private List<AnnItemInfo> lists;
    private int selectIndex = 0;

    public AnnTabAdapter(Context context, List<AnnItemInfo> lists) {
        this.context = context;
        this.lists = lists;
    }

    public void updateList(List<AnnItemInfo> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public AnnItemInfo getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_ann_tab, null);
            holder.tvAnnTitle = convertView.findViewById(R.id.tv_ann_title);
            holder.viewLine = convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AnnItemInfo info = lists.get(position);


        if(selectIndex == position){
            holder.tvAnnTitle.setText(Html.fromHtml(String.format("<B>%s</B>",info.getTitle())));
            holder.tvAnnTitle.setTextColor(context.getResources().getColor(R.color.black));
            holder.tvAnnTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            holder.viewLine.setVisibility(getCount() > 1 ? View.VISIBLE :View.GONE);
//            holder.tvAnnTitle.setBackgroundColor(context.getResources().getColor(R.color.bgColor));
        }else{
            holder.tvAnnTitle.setText(Html.fromHtml(String.format("<Span>%s</Span>",info.getTitle())));
            holder.tvAnnTitle.setTextColor(context.getResources().getColor(R.color.grey));
            holder.tvAnnTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            holder.viewLine.setVisibility(getCount() > 1 ? View.INVISIBLE : View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvAnnTitle;
        View viewLine;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selectIndex;
    }
}
