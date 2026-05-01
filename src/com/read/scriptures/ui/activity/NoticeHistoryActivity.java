package com.read.scriptures.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.read.scriptures.R;
import com.read.scriptures.adapter.NoticeHistoryAdapter;
import com.read.scriptures.bean.NoticeBean;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.StatusBarUtils;

import org.litepal.LitePal;

import java.util.List;

public class NoticeHistoryActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_notice_history);
        StatusBarUtils.initMainColorStatusBar(this);
        RecyclerView rcv_notice = findViewById(R.id.rcv_notice);
        rcv_notice.setLayoutManager(new LinearLayoutManager(this));
        List<NoticeBean> all = LitePal.findAll(NoticeBean.class);
        NoticeHistoryAdapter noticeHistoryAdapter = new NoticeHistoryAdapter();
        rcv_notice.setAdapter(noticeHistoryAdapter);
        noticeHistoryAdapter.setNewData(all);

        findViewById(R.id.iv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("通知消息");
        noticeHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NoticeBean item = noticeHistoryAdapter.getItem(position);
                showRemark(item.content);
            }
        });
    }

    private void showRemark(String remarks) {
        new AlertDialog.Builder(this).setMessage(remarks).create().show();
    }
}
