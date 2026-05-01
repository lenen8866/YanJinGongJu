package com.read.scriptures.adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.music.player.lib.util.SaltUtils;
import com.read.scriptures.R;
import com.read.scriptures.bean.AnswerClickBean;
import com.read.scriptures.bean.QuestionBean;
import com.read.scriptures.ui.activity.StartAnswerActivity;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.PicassoUtils;

import java.util.ArrayList;

public class StartAnswerAdapter extends BaseQuickAdapter<QuestionBean.DataBean.RowsBean, BaseViewHolder> {
    public StartAnswerAdapter() {
        super(R.layout.item_question);
    }

    @Override
    protected void convert(BaseViewHolder helper, QuestionBean.DataBean.RowsBean item) {
        if (!item.isLoad) {
            if (mContext instanceof StartAnswerActivity) {
                ((StartAnswerActivity) mContext).onNextPage(item, helper.getAdapterPosition());
            }
        }
        item.isLoad = true;
        helper.setText(R.id.tv_question_title, (helper.getAdapterPosition() + 1) + "，" + item.title);
        ImageView iv_cover = helper.getView(R.id.iv_cover);
        if (!TextUtils.isEmpty(item.picurl)) {
            iv_cover.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = iv_cover.getLayoutParams();
            layoutParams.width = (int) (DensityUtil.getScreenWidth(mContext) * 0.55);
            PicassoUtils.loadImage(iv_cover, item.picurl, R.drawable.icon_play_deault_bg);
        } else {
            iv_cover.setVisibility(View.GONE);
        }
        helper.addOnClickListener(R.id.iv_repeat);
        RecyclerView recyclerView = helper.getView(R.id.rcv_answer);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        AnswerAdapter answerAdapter = new AnswerAdapter();
        recyclerView.setAdapter(answerAdapter);
        if (item.answerClickBeans == null) {
            item.answerClickBeans = new ArrayList<>();
            for (String str : item.item) {
                AnswerClickBean answerClickBean = new AnswerClickBean();
                answerClickBean.title = str;
                answerClickBean.selectedAnswerIndex = -1;
                item.answerClickBeans.add(answerClickBean);
            }
        }
        answerAdapter.setNewData(item.answerClickBeans);
        answerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isAnswerComplete) {
                    if (mContext instanceof StartAnswerActivity) {
                        ((StartAnswerActivity) mContext).loadAnswerComplete();
                    }
                    return;
                }
                AnswerClickBean answerItem = answerAdapter.getItem(position);
                if (answerItem == null) {
                    return;
                }
                if (!TextUtils.isEmpty(item.selectedAnswer)) {//已答题
                    return;
                }
                item.selectedAnswer = answerItem.title;
                answerItem.selectedAnswerIndex = position;
                answerAdapter.notifyDataSetChanged();
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String rightAnswer = SaltUtils.getUrl(item.correct);
                        if (answerItem.title.equals(rightAnswer)) {
                            answerItem.rightAnswer = 1;
                            item.isRightAnswer = true;
                        } else {
                            answerItem.rightAnswer = 2;
                        }
                        if (mContext instanceof StartAnswerActivity) {
                            ((StartAnswerActivity) mContext).loadNextPage(item, helper.getAdapterPosition(), true);
                        }
                        answerAdapter.notifyDataSetChanged();
                    }
                }, 100);
            }
        });
    }

    private boolean isAnswerComplete = false;

    public void setAnswerComplete(boolean flag) {
        isAnswerComplete = flag;
    }
}
