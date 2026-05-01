package com.read.scriptures.adapter;

import android.graphics.Color;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;

public class AnswerNumAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public AnswerNumAdapter() {
        super(R.layout.item_answer_num);
    }

    public void setCurrentNum(String currentNum) {
        this.currentNum = currentNum;
        notifyDataSetChanged();
    }

    public String currentNum = "";

    @Override
    protected void convert(BaseViewHolder helper, String str) {
        TextView tv_author = helper.getView(R.id.tv_cate);
        tv_author.setText(str + "题");
        if (TextUtils.equals(str, currentNum)) {
            tv_author.setTextColor(ContextCompat.getColor(mContext, R.color.main_color));
            tv_author.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);
        } else {
            tv_author.setTextColor(Color.BLACK);
            tv_author.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_uncheck, 0);
        }
    }
}
