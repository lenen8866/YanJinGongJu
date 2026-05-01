package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.R;
import com.read.scriptures.model.Chapter;

import java.util.List;

public class ChapterListViewAdapter extends EIBaseAdapter<Chapter> {

    private Context mContext;
    private Holder mHolder;
    private int mpProgress = 0;
    private boolean delete = false;
    private boolean isShowList = true;
    private GridView gv;


    public ChapterListViewAdapter(Context context, List<Chapter> list,boolean isShowList,GridView gv) {
        super(context, list);
        mContext = context;
        this.isShowList = isShowList;
        this.gv = gv;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public void setProgress(int progress) {
        if (delete){
            this.mpProgress = progress-1;
        }else {
            this.mpProgress = progress;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            mHolder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_chapter_item, null);
            mHolder.listItemLayout = view.findViewById(R.id.list_item_layout);
            mHolder.tvListName = view.findViewById(R.id.tv_list_name);
            mHolder.gridItemLayout = view.findViewById(R.id.grid_item_layout);
            mHolder.tvGridName = view.findViewById(R.id.tv_grid_name);
            mHolder.tvGridNameDetails = view.findViewById(R.id.tv_grid_details);
            view.setTag(mHolder);
        } else {
            mHolder = (Holder) view.getTag();
        }

        if (isShowList) {
            mHolder.listItemLayout.setVisibility(View.VISIBLE);
            mHolder.gridItemLayout.setVisibility(View.GONE);
            if (mpProgress == position) {
                mHolder.tvListName.setText(Html.fromHtml(mList.get(position).getShowName() + "<small>(上次阅读)</small>"));
                mHolder.tvListName.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                mHolder.tvListName.setText(mList.get(position).getShowName());
                mHolder.tvListName.setTextColor(mContext.getResources().getColor(R.color.gray_333333));
            }
        }else{
            mHolder.gridItemLayout.setVisibility(View.VISIBLE);
            mHolder.listItemLayout.setVisibility(View.GONE);
            mHolder.tvGridName.setVisibility(View.VISIBLE);
            mHolder.tvGridNameDetails.setVisibility(View.VISIBLE);
            if (position >= mList.size()){
                mHolder.tvGridName.setText(" ");
                mHolder.tvGridNameDetails.setText(" ");
                mHolder.tvGridNameDetails.setVisibility(View.GONE);
                return view;
            }
            String name = mList.get(position).getShowName();
            String indexName = "";
            String detailsName = "";
            if (name.contains("章")){
                indexName = name.substring(0,name.indexOf("章")+1);
                if (name.indexOf("章") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("章")+1);
                }
            }else if (name.contains("编")){
                indexName = name.substring(0,name.indexOf("编")+1);
                if (name.indexOf("编") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("编")+1);
                }
            }else if (name.contains("节")){
                indexName = name.substring(0,name.indexOf("节")+1);
                if (name.indexOf("节") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("节")+1);
                }
            }else if (name.contains("月")){
                indexName = name.substring(0,name.indexOf("月")+1);
                if (name.indexOf("月") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("月")+1);
                }
            }else if (name.contains("号")){
                indexName = name.substring(0,name.indexOf("号")+1);
                if (name.indexOf("号") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("号")+1);
                }
            }else if (name.contains("篇")){
                indexName = name.substring(0,name.indexOf("篇")+1);
                if (name.indexOf("篇") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("篇")+1);
                }
            }else if (name.contains("讲")){
                indexName = name.substring(0,name.indexOf("讲")+1);
                if (name.indexOf("讲") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("讲")+1);
                }
            }else if (name.contains("课")){
                indexName = name.substring(0,name.indexOf("课")+1);
                if (name.indexOf("课") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("课")+1);
                }
            }else if (name.contains("：")){
                indexName = name.substring(0,name.indexOf("：")+1);
                if (name.indexOf("：") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("：")+1);
                }
            }else if (name.contains("　")){
                indexName = name.substring(0,name.indexOf("　")+1);
                if (name.indexOf("　") < name.length()-1) {
                    detailsName = name.substring(name.indexOf("　")+1);
                }
            }else{
                indexName = name;
//                detailsName = "-|-";
            }
            detailsName = detailsName.trim();
            mHolder.tvGridNameDetails.setVisibility(detailsName.length() == 0 ? View.GONE : View.VISIBLE);
            mHolder.tvGridName.setText(indexName.trim());
            mHolder.tvGridNameDetails.setText(detailsName.trim());
//            mHolder.nameDetails.setText(detailsName.replaceAll("-\\|-"," ").trim());
            if (mpProgress == position) {
                mHolder.tvGridName.setTextColor(mContext.getResources().getColor(R.color.red));
                mHolder.tvGridNameDetails.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                mHolder.tvGridName.setTextColor(mContext.getResources().getColor(R.color.gray_333333));
                mHolder.tvGridNameDetails.setTextColor(mContext.getResources().getColor(R.color.gray_333333));
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        if (isShowList) {
            return super.getCount();
        }else {
            if (getList().size() % 3 == 0){
                return super.getCount();
            }
            return super.getCount()+(3 - getList().size() % 3);
        }
    }

    private class Holder {
        TextView tvListName;
        TextView tvGridName;
        TextView tvGridNameDetails;

        LinearLayout listItemLayout;
        LinearLayout gridItemLayout;
    }


    public void setShowList(boolean showList) {
        isShowList = showList;
        notifyDataSetChanged();
    }




}
