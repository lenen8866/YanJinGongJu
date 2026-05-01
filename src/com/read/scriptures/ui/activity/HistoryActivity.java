package com.read.scriptures.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.bean.HistoryBean;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.DatabaseManager;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.HistoryAdapter;
import com.read.scriptures.EIUtils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryActivity extends BaseActivity {

    @BindView(R.id.rv_history)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    HistoryAdapter historyAdapter;
    List<HistoryBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        initViews();
        initData();
    }

    private void initData() {
        list = DatabaseManager.getHistoryHelper().getAllHistory();
        historyAdapter.setDataList(list);
        showNodata();
    }

    private void showNodata() {
        if (list.size() == 0)
            tv_no_data.setVisibility(View.VISIBLE);
        else
            tv_no_data.setVisibility(View.GONE);
    }

    @Override
    protected boolean isHideActionbar() {
        return true;
    }

    private void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        historyAdapter = new HistoryAdapter(this, list);
        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position, HistoryBean historyBean) {
                Chapter chapter = new Chapter();
                Bundle bd = new Bundle();
                chapter.setName(historyBean.getChapter());
                chapter.setCategoryId(historyBean.getCategoryId());
                chapter.setChapterCount(historyBean.getChapterCount());
                chapter.setContent(historyBean.getContent());
                chapter.setIndexId(historyBean.getIndexId());
                chapter.setParentId(historyBean.getParentId());
                chapter.setVolumeId(historyBean.getVolumeId());
                chapter.setVolumeName(historyBean.getVolumeName());
                bd.putParcelable(BundleConstants.PARAM_CHAPTER, chapter);
                bd.putString(BundleConstants.PARAM_ENTER,"history");
                ActivityUtil.next(HistoryActivity.this, ChapterReaderActivity.class, bd, -1);
                finish();
            }
        });
        recyclerView.setAdapter(historyAdapter);
    }

    @OnClick({R.id.iv_left,R.id.tv_clear})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_clear:
                showNormalDialog();
                break;
        }
    }

    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(HistoryActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您确定清除所有记录吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseManager.getHistoryHelper().clearHistory();
                        list = DatabaseManager.getHistoryHelper().getAllHistory();
                        historyAdapter.setDataList(list);
                        showNodata();
                        //...To-do
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list = DatabaseManager.getHistoryHelper().getAllHistory();
        historyAdapter.setDataList(list);
        showNodata();
    }
}
