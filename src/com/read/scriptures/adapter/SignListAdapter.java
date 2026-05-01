package com.read.scriptures.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.SignBean;
import com.read.scriptures.util.TimeUtils;


public class SignListAdapter extends BaseQuickAdapter<SignBean.DataBean.WeekBean, BaseViewHolder> {

    public SignListAdapter() {
        super(R.layout.item_sign_layout);

    }
    /**
     * @param helper
     * @param item
     */
    @Override
    protected void convert(BaseViewHolder helper, SignBean.DataBean.WeekBean item) {
        String value = parseWeek(item.week);
        helper.setText(R.id.tv_title, value);
        helper.setText(R.id.tv_content, item.num + "个币");
        View ll_main = helper.getView(R.id.ll_main);
        ImageView iv_icon = helper.getView(R.id.iv_icon);
        TextView tv_title = helper.getView(R.id.tv_title);
        TextView tv_content = helper.getView(R.id.tv_content);
        if (item.status == 1) {//已签到
            ll_main.setBackgroundResource(R.drawable.item_sign_sign_bg);
            iv_icon.setImageResource(R.drawable.icon_signed1);
            tv_title.setTextColor(Color.parseColor("#ffffff"));
            tv_content.setTextColor(Color.parseColor("#ffffff"));
        } else {
            if (item.date.equals(TimeUtils.getDateSp())) {//今天
                ll_main.setBackgroundResource(R.drawable.item_sign_today_bg);
                iv_icon.setImageResource(R.drawable.icon_sign_money);
                tv_title.setTextColor(Color.parseColor("#ffffff"));
                tv_content.setTextColor(Color.parseColor("#ffffff"));
            } else {
                if (item.time < System.currentTimeMillis() / 1000) {//今天之前
                    ll_main.setBackgroundResource(R.drawable.item_sign_unsign_bg);
                    iv_icon.setImageResource(R.drawable.icon_miss);
                    tv_title.setTextColor(Color.RED);
                    tv_content.setTextColor(Color.RED);
                    helper.setText(R.id.tv_content, "已错过");
                } else {//之后
                    ll_main.setBackgroundResource(R.drawable.item_sign_unsign_bg);
                    iv_icon.setImageResource(R.drawable.icon_sign_money);
                    tv_title.setTextColor(Color.parseColor("#333333"));
                    tv_content.setTextColor(Color.parseColor("#333333"));
                }
            }
        }
    }

    private String parseWeek(String week) {
        switch (week) {
            case "周日":
                return "复活日";
            case "周五":
                return "预备日";
            case "周六":
                return "安息日";
            default:
                return week;
        }
    }
}
