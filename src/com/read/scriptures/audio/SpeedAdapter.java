package com.read.scriptures.audio;

import android.graphics.Color;
import androidx.core.content.ContextCompat;

import android.text.TextPaint;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;

public class SpeedAdapter extends BaseQuickAdapter<Float, BaseViewHolder> {
    public SpeedAdapter() {
        super(R.layout.item_author_layout);
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public float currentSpeed = 1.0f;

    @Override
    protected void convert(BaseViewHolder helper, Float obj) {
        TextView tv_author = helper.getView(R.id.tv_cate);
        tv_author.setText(obj.toString());
        if (obj == currentSpeed) {
            tv_author.setTextColor(ContextCompat.getColor(mContext,R.color.main_color));
            TextPaint paint = tv_author.getPaint();
            paint.setFakeBoldText(true);
            tv_author.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);
        } else {
            TextPaint paint = tv_author.getPaint();
            paint.setFakeBoldText(false);
            tv_author.setTextColor(Color.BLACK);
            tv_author.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_uncheck, 0);
        }
    }
}
