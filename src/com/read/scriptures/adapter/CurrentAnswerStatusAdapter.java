package com.read.scriptures.adapter;

import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.QuestionBean;

public class CurrentAnswerStatusAdapter extends BaseQuickAdapter<QuestionBean.DataBean.RowsBean, BaseViewHolder> {
    public CurrentAnswerStatusAdapter() {
        super(R.layout.item_current_answer_status);
    }

    @Override
    protected void convert(BaseViewHolder helper, QuestionBean.DataBean.RowsBean item) {
        LinearLayout ll_answer = helper.getView(R.id.ll_answer);
        TextView tv_answer_title = helper.getView(R.id.tv_answer_title);
        tv_answer_title.setText(helper.getAdapterPosition() + 1 + "");
        if (currentQuestion.id == item.id && !isEnd) {//当前
            ll_answer.setBackgroundResource(R.drawable.bg_status_current_shape);
            tv_answer_title.setTextColor(Color.parseColor("#333333"));
        } else {
            if (item.isLoad) {
                if (item.isRightAnswer) {
                    ll_answer.setBackgroundResource(R.drawable.bg_status_right_shape);
                    tv_answer_title.setTextColor(Color.WHITE);
                } else {
                    ll_answer.setBackgroundResource(R.drawable.bg_status_error_shape);
                    tv_answer_title.setTextColor(Color.WHITE);
                }
            } else {
                tv_answer_title.setTextColor(Color.parseColor("#666666"));
                ll_answer.setBackgroundResource(R.drawable.bg_status_unload_shape);
                tv_answer_title.setTextColor(Color.DKGRAY);
            }
        }
    }

    private QuestionBean.DataBean.RowsBean currentQuestion;

    public void setCurrentQuestion(QuestionBean.DataBean.RowsBean item) {
        currentQuestion = item;
    }

    private boolean isEnd;

    public void setIsEnd(boolean end) {
        isEnd = end;
    }
}
