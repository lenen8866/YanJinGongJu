package com.read.scriptures.video;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.VideoBookBean;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoListAdapter extends BaseQuickAdapter<VideoBookBean.RowsBean, BaseViewHolder> {
    public VideoListAdapter() {
        super(R.layout.item_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoBookBean.RowsBean item) {
        CharSequence chapter = matchSearch(item.cate_name);
        helper.setText(R.id.tv_title, chapter)
                .setText(R.id.tv_count, item.video_count + "")
                .setText(R.id.tv_chapter_num, item.cate_content)
                .setText(R.id.tv_play_tag, TextUtils.equals(item.id, videoPlayEnd) ? "最后观看" : "")
        ;

        ImageView iv_cover = helper.getView(R.id.iv_cover);
        PicassoUtils.loadImage(iv_cover, item.cate_image, R.drawable.video_list_default_bg, DensityUtil.dip2px(110), DensityUtil.dip2px(70));
        helper.setTextColor(R.id.tv_title, Color.parseColor("#000000"));
        helper.setTextColor(R.id.tv_chapter_num, Color.parseColor("#999999"));
    }

    public String searchStr;

    public void setSearchText(String search) {
        this.searchStr = search;
    }

    private SpannableString matchSearch(String str) {
        SpannableString sStr = new SpannableString(str);
        if (TextUtils.isEmpty(searchStr)) {
            return sStr;
        }

        String rx = "[" + searchStr + "\\\\]";
        Pattern p = Pattern.compile(rx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#5677FC")), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return sStr;
    }

    private String videoPlayEnd;

    public void setEndPlayVideo(@Nullable String videoPlayEnd) {
        this.videoPlayEnd = videoPlayEnd;
        notifyDataSetChanged();
    }
}
