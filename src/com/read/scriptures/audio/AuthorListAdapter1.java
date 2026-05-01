package com.read.scriptures.audio;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;

public class AuthorListAdapter1 extends BaseQuickAdapter<String, BaseViewHolder> {
    public AuthorListAdapter1() {
        super(R.layout.item_author_1);
    }

    public void setCurrentAuthor(String currentAuthor) {
        this.currentAuthor = currentAuthor;
        notifyDataSetChanged();
    }

    public String currentAuthor = "";

    @Override
    protected void convert(BaseViewHolder helper, String author) {
        helper.setText(R.id.tv_title, author)
                .setVisible(R.id.view_line, TextUtils.equals(author, currentAuthor))
        ;
        TextView tv_title = helper.getView(R.id.tv_title);
        if (TextUtils.equals(author, currentAuthor)) {
            tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.main_color));
            tv_title.setTextSize(15);
            tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            tv_title.setTextColor(Color.BLACK);
            tv_title.setTextSize(13);
            tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }
}
