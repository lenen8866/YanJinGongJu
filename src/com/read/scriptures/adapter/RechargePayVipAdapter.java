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
import com.read.scriptures.app.PayVipBean;

public class RechargePayVipAdapter extends BaseQuickAdapter<PayVipBean.DataBean.普通会员Bean, BaseViewHolder> {
    public RechargePayVipAdapter() {
        super(R.layout.item_recharge_tip_dialog);
    }

    @Override
    protected void convert(BaseViewHolder helper, PayVipBean.DataBean.普通会员Bean item) {
        helper.setText(R.id.tv_title, item.day + "天")
                .setVisible(R.id.tv_discounts, !TextUtils.equals(item.payments, item.discount))
                .setText(R.id.tv_price, item.discount)
                .setText(R.id.tv_price_org, item.payments)
                .setTextColor(R.id.tv_title, item.selected ? Color.WHITE : Color.parseColor("#545454"))
        ;

        CardView cv_main = helper.getView(R.id.cv_main);
        TextView tv_title = helper.getView(R.id.tv_title);
        LinearLayout ll_body = helper.getView(R.id.ll_body);
        if (!TextUtils.equals(item.discount, item.payments)) {
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
