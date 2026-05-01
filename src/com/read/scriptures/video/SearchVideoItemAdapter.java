package com.read.scriptures.video;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.SearchVideoBean;
import com.read.scriptures.util.SecToTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchVideoItemAdapter extends BaseQuickAdapter<SearchVideoBean.RowsDTO, BaseViewHolder> {
    public SearchVideoItemAdapter() {
        super(R.layout.item_new_search_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchVideoBean.RowsDTO item) {
        CharSequence chapter = matchSearch(item.chapter);
        helper.setText(R.id.tv_book_name, chapter)
                .setText(R.id.tv_author, item.author)
                .setGone(R.id.tv_author, !TextUtils.isEmpty(item.author))
                .setText(R.id.tv_chapter_num, SecToTime.getTimeString(item.duration))
                .setVisible(R.id.tv_cate_name, true)
                .setText(R.id.tv_cate_name, item.cate1_name + "-" + item.cate2_name+ "-" + item.cate3_name);

        setTextColor(false, helper.getView(R.id.tv_author), helper.getView(R.id.tv_chapter_num), helper.getView(R.id.tv_cate_name), helper.getView(R.id.tv_book_name));
    }

    private void setTextColor(boolean isPlay, TextView... views) {
        int color;
        if (isPlay) {
            color = Color.parseColor("#ff9800");
        } else {
            color = Color.parseColor("#333333");
        }
        for (TextView textView : views) {
            textView.setTextColor(color);
        }
    }


    public String searchStr;

    public void setSearchText(String search) {
        this.searchStr = search;
    }

    private SpannableString matchSearch(String str) {
        SpannableString sStr = new SpannableString(str);
        String rx = "[" + searchStr + "\\\\]";
        Pattern p = Pattern.compile(rx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#5677FC")), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return sStr;
    }
}
