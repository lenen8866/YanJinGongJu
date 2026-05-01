package com.read.scriptures.ui.adapter.baike;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.bean.CollectBean;
import com.read.scriptures.EIUtils.EIBaseAdapter;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.MTextUtil;
import com.read.scriptures.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class CollectAdapter extends EIBaseAdapter<CollectBean> {

    private final Context mContext;
    //是否是操作模式
    private boolean isOperationModel = false;
    //当前点钟
    private List<String> selectIndexs = new ArrayList<>();

    private CheckedChangeCallback  checkedChangeCallback;

    public interface CheckedChangeCallback{
        void checkedChange();
    }

    public CollectAdapter(Context context, List<CollectBean> list, CheckedChangeCallback  checkedChangeCallback) {
        super(context, list);
        mContext = context;
        this.checkedChangeCallback = checkedChangeCallback;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder mHolder;
        if (convertView == null) {
            mHolder = new CollectAdapter.Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_history_or_collect_list_item, null);
            mHolder.fl_check = convertView.findViewById(R.id.fl_check);
            mHolder.ze_icon_first = convertView.findViewById(R.id.ze_icon_first);
            mHolder.tv_title = convertView.findViewById(R.id.tv_name);
            mHolder.tv_chapter = convertView.findViewById(R.id.tv_content);
            mHolder.tv_time = convertView.findViewById(R.id.tv_book_name);
            mHolder.tv_head = convertView.findViewById(R.id.tv_head);
            mHolder.cb_selected = convertView.findViewById(R.id.cb_select);
            mHolder.ivTopLabel = convertView.findViewById(R.id.iv_top_label);
            mHolder.ze_icon_first = convertView.findViewById(R.id.ze_icon_first);
            mHolder.ze_icon_last = convertView.findViewById(R.id.ze_icon_last);
            convertView.setTag(mHolder);
        } else {
            mHolder = (CollectAdapter.Holder) convertView.getTag();
        }

        mHolder.fl_check.setVisibility(isOperationModel ? View.VISIBLE : View.INVISIBLE);
        if (selectIndexs.contains(String.valueOf(position))){
            mHolder.cb_selected.setChecked(true);
        }else{
            mHolder.cb_selected.setChecked(false);
        }

        mHolder.ivTopLabel.setVisibility(getList().get(position).getTopIndex() > 0 ? View.VISIBLE : View.GONE);
        mHolder.tv_head.setText(getHeader(getList().get(position).getVolumeName()));
        mHolder.tv_title.setText(getList().get(position).getVolumeName().replaceAll("\\((.*?)\\)", "").replaceAll("\\[(.*?)\\]", "")
                .replaceAll("\\{(.*?)\\}", ""));

        if(MTextUtil.isContainChinese(mHolder.tv_title.getText().toString()) && mHolder.tv_title.getText().toString().contains("E")){
            String s = MTextUtil.changeEletter(mHolder.tv_title.getText().toString().trim());

            if(s.length() != mHolder.tv_title.getText().toString().length()){
                if(s.length() >15){
                    mHolder.ze_icon_last.setVisibility(View.VISIBLE);
                }else {
                    mHolder.ze_icon_first.setVisibility(View.VISIBLE);
                }
            }
            mHolder.tv_title.setText(s);
        }
        String intro = getList().get(position).getChapter();
        if (!TextUtils.isEmpty(intro)) {
            intro = intro.replaceAll("\n", "")
                    .replaceAll(" ", "")
                    .replaceAll("　", "");
            mHolder.tv_chapter.setText(intro);
        } else {
            intro = "暂无简介";
            mHolder.tv_chapter.setText(intro);
        }
        mHolder.tv_time.setText(TimeUtils.timeStamp2DateC(Long.valueOf(getList().get(position).getTime())));

        mHolder.fl_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChecked(position);
            }
        });
        return convertView;
    }


    private class Holder {
        private FrameLayout fl_check;
        private TextView tv_title;
        private TextView tv_chapter;
        private TextView tv_time;
        private TextView tv_head;
        private CheckBox cb_selected;
        private ImageView ivTopLabel;
        private ImageView ze_icon_first;
        private ImageView ze_icon_last;
    }

    /**
     * 第一个字
     *
     * @return
     */
    public String getHeader(String volName) {
        String header = "";
        if (volName.indexOf("{") != -1 && volName.indexOf("}") != -1) {
            header = CharUtils.match("\\{(.*?)\\}", volName);
            if (header.trim().equals("{}")) {
                if (volName.indexOf("[") != -1 && volName.indexOf("]") != -1) {
                    header = CharUtils.match("\\[(.*?)\\]", volName);
                    if (header.trim().equals("[]")) {
                        header = volName.substring(volName.indexOf("]") + 1, volName.indexOf("]") + 2);
                    }
                } else {
                    header = volName.substring(volName.indexOf("}") + 1, volName.indexOf("}") + 2);
                }
            }
        } else if (volName.indexOf("[") != -1 && volName.indexOf("]") != -1) {
            header = CharUtils.match("\\[(.*?)\\]", volName);
            if (header.trim().equals("[]")) {
                header = volName.substring(volName.indexOf("]") + 1, volName.indexOf("]") + 2);
            }
        } else {
            header = volName.substring(0, 1);
        }

        header = header.replaceAll("\\{", "")
                .replaceAll("\\}", "")
                .replaceAll("\\[", "")
                .replaceAll("\\]", "");
        return header;
    }

    public List<String> getSelectIndexs() {
        return selectIndexs;
    }


    public void setOperationModel(boolean operationModel) {
        isOperationModel = operationModel;
        if (!isOperationModel){
            selectIndexs.clear();
        }
        callbackChecked();
        notifyDataSetChanged();
    }

    public void setCheckedAll(boolean isCheckedAll){
        if (isCheckedAll){
            selectIndexs.clear();
            for (int i = 0; i < getList().size(); i++) {
                selectIndexs.add(String.valueOf(i));
            }
        }else{
            selectIndexs.clear();
        }
        callbackChecked();
        notifyDataSetChanged();
    }

    public void changeChecked(int position) {
        if (selectIndexs.contains(position + "")) {
            selectIndexs.remove(position + "");
        } else {
            selectIndexs.add(position + "");
        }
        callbackChecked();
        notifyDataSetChanged();
    }


    private void callbackChecked() {
        if (checkedChangeCallback != null){
            checkedChangeCallback.checkedChange();
        }
    }

    public boolean getSwipEnableByPosition(int position){
        return isOperationModel ? false : true;
    }


}
