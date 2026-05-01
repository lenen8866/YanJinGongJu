package com.read.scriptures.adapter;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.read.scriptures.R;
import com.read.scriptures.bean.AnswerClickBean;

import java.util.List;

public class AnswerAdapter extends BaseQuickAdapter<AnswerClickBean, BaseViewHolder> {
    public AnswerAdapter() {
        super(R.layout.item_answer);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            AnswerClickBean answerClickBean = (AnswerClickBean) payloads.get(0);
            if (answerClickBean == null) {
                return;
            }
            AnswerClickBean item = getItem(position);
            if (item == null) {
                return;
            }
            showUI(holder);
        }
    }

    private void showUI(BaseViewHolder helper) {
        notifyDataSetChanged();
    }

    private int selectedColor = Color.parseColor("#e6e6e6");
    private int rightAnswerColor = Color.parseColor("#5677FC");
    private int errorAnswerColor = Color.RED;

    @Override
    protected void convert(BaseViewHolder helper, AnswerClickBean item) {
        CardView cv_answer = helper.getView(R.id.cv_answer);
        TextView tv_answer_title = helper.getView(R.id.tv_answer_title);
        TextView tv_answer = helper.getView(R.id.tv_answer);

        tv_answer.setText(item.title);
        switch (helper.getAdapterPosition()) {
            case 0:
                tv_answer_title.setText("A");
                break;
            case 1:
                tv_answer_title.setText("B");
                break;
            case 2:
                tv_answer_title.setText("C");
                break;
            case 3:
                tv_answer_title.setText("D");
                break;
        }

        if (item.selectedAnswerIndex == helper.getAdapterPosition()) {
            cv_answer.setCardBackgroundColor(selectedColor);
            tv_answer.setTextColor(selectedColor);
            tv_answer_title.setTextColor(selectedColor);
        } else {
            cv_answer.setCardBackgroundColor(Color.WHITE);
            tv_answer.setTextColor(Color.BLACK);
            tv_answer_title.setTextColor(Color.BLACK);
        }
        if (item.selectedAnswerIndex == helper.getAdapterPosition()) {
            switch (item.rightAnswer) {
                case 0:
                    break;
                case 1:
                    cv_answer.setCardBackgroundColor(rightAnswerColor);
                    tv_answer.setTextColor(rightAnswerColor);
                    tv_answer_title.setTextColor(Color.WHITE);
                    break;
                case 2:
                    cv_answer.setCardBackgroundColor(errorAnswerColor);
                    tv_answer.setTextColor(errorAnswerColor);
                    tv_answer_title.setTextColor(Color.WHITE);
                    break;
            }
        }
    }
}
