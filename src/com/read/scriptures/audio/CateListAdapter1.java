package com.read.scriptures.audio;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.NewAudioBean;

public class CateListAdapter1 extends BaseQuickAdapter<NewAudioBean.RowsBean, BaseViewHolder> {
    public CateListAdapter1() {
        super(R.layout.item_author_1);
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
        notifyDataSetChanged();
    }

    public String currentId = "";

    @Override
    protected void convert(BaseViewHolder helper, NewAudioBean.RowsBean cate) {
        helper.setText(R.id.tv_title, cate.name)
                .setVisible(R.id.view_line, TextUtils.equals(cate.id, currentId))
        ;
        TextView tv_title = helper.getView(R.id.tv_title);
        if (TextUtils.equals(cate.id, currentId)) {
            tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.main_color));
            tv_title.setTextSize(18);
            tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            tv_title.setTextColor(Color.BLACK);
            tv_title.setTextSize(15);
            tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }
}
