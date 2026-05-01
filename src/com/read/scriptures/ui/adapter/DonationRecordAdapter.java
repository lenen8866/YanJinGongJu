package com.read.scriptures.ui.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.DonationRecordBean;

public class DonationRecordAdapter extends BaseQuickAdapter<DonationRecordBean.RowsBean, BaseViewHolder> {
    public DonationRecordAdapter() {
        super(R.layout.item_donation_record);
    }

    @Override
    protected void convert(BaseViewHolder helper, DonationRecordBean.RowsBean item) {
        ImageView iv_avatar = helper.getView(R.id.iv_avatar);
        if (item.v1user != null) {
            Glide.with(mContext)
                    .load(item.v1user.avatar)
                    .placeholder(R.drawable.icon_person)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .error(R.drawable.icon_person)
                    .into(iv_avatar);
            helper.setText(R.id.tv_name, TextUtils.isEmpty(item.v1user.nickname) ? "暂无" : item.v1user.nickname);
        }
        TextView tv_detail = helper.getView(R.id.tv_detail);
        switch (item.pay_type) {
            case "wxpay"://微信
                tv_detail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_wx_pay, 0, 0, 0);
                break;
            case "alipay"://支付宝
                tv_detail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_ali_pay, 0, 0, 0);
                break;
            case "transfer":
                tv_detail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bank_pay, 0, 0, 0);
                break;
        }
        tv_detail.setText(item.goods_type);
        helper.setText(R.id.tv_money, "¥ " + item.price);
        int i = item.cre_dt.indexOf("-");
        helper.setText(R.id.tv_time, item.cre_dt.substring(i+1));
        helper.setVisible(R.id.tv_show_remark, !TextUtils.isEmpty(item.remarks));
        helper.addOnClickListener(R.id.tv_show_remark);
    }
}
