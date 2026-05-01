package com.read.scriptures.audio;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.music.player.lib.bean.BaseAudioInfo;
import com.read.scriptures.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewSearchAudioChapterAdapter extends BaseQuickAdapter<BaseAudioInfo, BaseViewHolder> {
    public NewSearchAudioChapterAdapter() {
        super(R.layout.item_new_search_audio_book);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseAudioInfo item) {
        CharSequence chapter = matchSearch(item.chapter);
        helper.setText(R.id.tv_book_name, chapter)
                .setText(R.id.tv_author, item.author)
                .setGone(R.id.tv_author, !TextUtils.isEmpty(item.author))
                .setText(R.id.tv_chapter_num, item.cate3_name)
                .setText(R.id.tv_cate_name, item.cate1_name + "-" + item.cate2_name);

        setTextColor(currentItem != null && item.id == currentItem.id,
                helper.getView(R.id.tv_author),
                helper.getView(R.id.tv_chapter_num),
                helper.getView(R.id.tv_cate_name),
                helper.getView(R.id.tv_book_name));
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

    private BaseAudioInfo currentItem;

    public void setCurrentAudio(BaseAudioInfo item) {
        this.currentItem = item;
        notifyDataSetChanged();
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
