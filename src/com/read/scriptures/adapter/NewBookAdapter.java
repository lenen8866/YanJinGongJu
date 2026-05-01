package com.read.scriptures.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d.lib.slidelayout.SlideLayout;
import com.music.player.lib.manager.MusicPlayerManager;
import com.read.scriptures.R;
import com.read.scriptures.bean.NewBookData;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.PreferencesUtils;

import org.jetbrains.annotations.Nullable;

public class NewBookAdapter extends BaseQuickAdapter<NewBookData.RowsBean, BaseViewHolder> {
    public NewBookAdapter() {
        super(R.layout.item_new_book);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewBookData.RowsBean item) {

        TextView tv_title = helper.getView(R.id.tv_title);
        helper
                .setText(R.id.tv_title, item.name)
                .setText(R.id.tv_chapter_num, item.content)
                .setText(R.id.tv_play_tag, TextUtils.equals(item.id, audioPlayEnd) ? "最后播放" : "")
        ;

        if (TextUtils.equals(author, "全部") && TextUtils.isEmpty(item.author)) {
            helper.setGone(R.id.tv_author, false);
        } else {
            helper.setGone(R.id.tv_author, true);
            helper.setText(R.id.tv_author, TextUtils.equals(author, "全部") ? "作者：" + item.author : "读者：" + author);
        }
        ImageView iv_cover = helper.getView(R.id.iv_cover);
        PicassoUtils.loadImage(iv_cover, item.image, R.drawable.icon_play_deault_bg, DensityUtil.dip2px(60), DensityUtil.dip2px(90));
        if (TextUtils.equals(bookName, item.name) && TextUtils.equals(author, MusicPlayerManager.getInstance().getCurrentAuthor())) {
            helper.setTextColor(R.id.tv_title, Color.parseColor("#ff9800"));
            helper.setTextColor(R.id.tv_chapter_num, Color.parseColor("#ff9800"));
            helper.setTextColor(R.id.tv_author, Color.parseColor("#ff9800"));
        } else {
            helper.setTextColor(R.id.tv_title, Color.parseColor("#000000"));
            helper.setTextColor(R.id.tv_chapter_num, Color.parseColor("#999999"));
            helper.setTextColor(R.id.tv_author, Color.parseColor("#666666"));
        }

        SlideLayout sl_layout = helper.getView(R.id.sl_layout);
        sl_layout.setOpen(item.isOpen, true);
        helper.addOnClickListener(R.id.tv_clear_tag);
        helper.addOnClickListener(R.id.tv_add_tag);
        sl_layout.computeScroll();
        sl_layout.setOnStateChangeListener(new SlideLayout.OnStateChangeListener() {
            @Override
            public void onStateChanged(SlideLayout layout, boolean open) {
                item.isOpen = open;

            }
        });
        if (item.color == -1) {
            item.color = PreferencesUtils.getInt(mContext, "audio_item_tag_color_" + item.id, 0);
        }
        View iv_tag = helper.getView(R.id.iv_tag);
        if (item.color == 0) {
            iv_tag.setBackgroundColor(0);
        } else {
            iv_tag.setBackgroundColor(item.color);
        }
    }

    private String bookName;
    private String author;

    public void setCurrentBook(String bookName) {
        this.bookName = bookName;
        notifyDataSetChanged();
    }

    public void setCurrentAuthor(String author) {
        this.author = author;
        notifyDataSetChanged();
    }

    private String audioPlayEnd;

    public void setAudioEndPlay(@Nullable String audioPlayEnd) {
        this.audioPlayEnd = audioPlayEnd;
        notifyDataSetChanged();
    }
}
