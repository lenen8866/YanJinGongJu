package com.read.scriptures.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.read.scriptures.R;

public class TextRightArrowLayout extends RelativeLayout {

    private Context mContext;
    private TextView mTvtTitle;
    private TextView mTvContent;
    private ImageView mIvArrow;

    public TextRightArrowLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public TextRightArrowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        View rootLayout = LayoutInflater.from(mContext).inflate(R.layout.layout_text_right_arrow, this);
        mTvtTitle = (TextView) rootLayout.findViewById(R.id.tv_title);
        mTvContent = (TextView) rootLayout.findViewById(R.id.tv_content);
        mIvArrow = (ImageView) rootLayout.findViewById(R.id.iv_img_arr);
    }

    public void setTitleAndContent(String title, String info) {
        mTvtTitle.setText(title);
        mTvContent.setText(info);
    }

    public void setTitle(String title) {
        mTvtTitle.setText(title);
    }

    public void setRightImage(Drawable drawable) {
        mIvArrow.setImageDrawable(drawable);
    }

    public void hideContent() {
        mTvContent.setVisibility(View.GONE);
    }

    public void setContent(String info) {
        mTvContent.setText(info);
    }

    public String getContent() {
        return mTvContent.getText().toString().trim();
    }

}
