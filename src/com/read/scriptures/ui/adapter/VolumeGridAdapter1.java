package com.read.scriptures.ui.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.model.Volume;
import com.read.scriptures.util.CharUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VolumeGridAdapter1 extends BaseQuickAdapter<Volume, BaseViewHolder> {

    private boolean mIsList;

    public VolumeGridAdapter1(List<Volume> volumeList, boolean isList) {
        super(isList ? R.layout.adapter_volume_gv_list_item : R.layout.adapter_volume_gv_grid_item, volumeList);
        mIsList = isList;
    }

    @Override
    protected void convert(BaseViewHolder helper, Volume item) {

//        if (position >= mList.size()) {
//            //补得空数据
//            viewHolder.R.id.tv_head.setText("");
//            viewHolder.tvTitle.setText("");
//        }
        helper.setText(R.id.tv_head, item.getHeader());

        String content = item.getVolName()
                .replaceAll("\\((.*?)\\)", "")
                .replaceAll("\\[(.*?)\\]", "")
                .replaceAll("\\{(.*?)\\}", "");


        if (!TextUtils.isEmpty(content)) {
            String title = content;
            if (title.contains("E") && isContainChinese(title)) {
                title = title.replace(title.charAt(title.length() - 1) + "", "").trim();
                helper.setText(R.id.tv_title, title);
                if (mIsList) {
                    if (title.length() > 15) {
                        helper.setVisible(R.id.ze_icon_last, true);
                    } else {
                        helper.setVisible(R.id.ze_icon, true);
                    }
                    if (helper.getView(R.id.tv_cate).getVisibility() != View.VISIBLE) {
                        helper.setGone(R.id.tv_cate, false);
                    }
                }
            }
        }
        String intro = item.getIntro();
        String author = CharUtils.match("\\((.*?)\\)", item.getVolName());
        if (!TextUtils.isEmpty(author)) {
            author = author.replaceAll("\\(", "").replaceAll("\\)", "");
            helper.setText(R.id.tv_cate, author);
        } else {
            helper.setGone(R.id.tv_cate, false);
        }

        if (!TextUtils.isEmpty(intro)) {
            intro = intro.replaceAll("\n", "").replaceAll(" ", "").replaceAll("　", "");
        } else {
            intro = "暂无简介";
        }
        helper.setText(R.id.tv_intro, intro);

//        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null)
//                    listener.onItemClick((AdapterView<?>) parent, v, position);
//            }
//        });
//        String introduction1 = item.getIntro();
//        String introduction2 = item.getIntroVideoAdd();
//        viewHolder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (longClickListener != null)
//                    longClickListener.onItemLongClick((AdapterView<?>) parent, view, position, introduction1, introduction2);
//                return true;
//            }
//        });
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
