package com.read.scriptures.adapter;

import android.graphics.Color;
import android.graphics.Paint;

import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.RechargeTipBean;

public class RechargeTipAdapter extends BaseQuickAdapter<RechargeTipBean.DataBean, BaseViewHolder> {
    public RechargeTipAdapter() {
        super(R.layout.item_recharge_tip_dialog);
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargeTipBean.DataBean item) {
        helper.setText(R.id.tv_title, item.num + "个")
                .setVisible(R.id.tv_discounts, !TextUtils.equals(item.former, item.price))
                .setText(R.id.tv_price, item.price)
                .setText(R.id.tv_price_org, item.former)
                .setTextColor(R.id.tv_title, item.selected ? Color.WHITE : Color.parseColor("#545454"))
        ;

        CardView cv_main = helper.getView(R.id.cv_main);
        TextView tv_title = helper.getView(R.id.tv_title);
        LinearLayout ll_body = helper.getView(R.id.ll_body);
        if (!TextUtils.equals(item.former, item.price)) {
            TextView tv_price_org = helper.getView(R.id.tv_price_org);
            tv_price_org.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else {
            TextView tv_price_org = helper.getView(R.id.tv_price_org);
            tv_price_org.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        }
        if (item.selected) {
            cv_main.setElevation(10.0f);
            tv_title.setBackgroundColor(Color.parseColor("#5677FC"));
            ll_body.setBackgroundResource(R.drawable.item_recharge_tip_shape);
        } else {
            cv_main.setElevation(2.0f);
            tv_title.setBackgroundColor(0);
            ll_body.setBackgroundResource(0);
        }
    }

}
