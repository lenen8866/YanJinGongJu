package com.read.scriptures.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.read.scriptures.R;
import com.read.scriptures.bean.HistoryBean;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.ChapterDatabaseHepler;
import com.read.scriptures.db.DatabaseManager;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.ui.activity.ChapterReaderActivity;
import com.read.scriptures.ui.activity.UserBookInfoActivity;
import com.read.scriptures.ui.adapter.HistoryReaderAdapter;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.util.DisplayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Time: 2020/9/15
 * Author: a123
 * Description: 阅读历史
 */
public class UserReaderHistoryFragment extends BaseFragment implements View.OnClickListener, HistoryReaderAdapter.CheckedChangeCallback {

    SwipeMenuListView lvContent;
    LinearLayout llBottom;
    TextView tvOperationTop;
    TextView tvOperationDelete;
    TextView tvOperationAllChecked;
    TextView tvEmpty;

    List<HistoryBean> list = new ArrayList<>();
    HistoryReaderAdapter mHistoryReaderAdapter;

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initWidget() {// step 1. create a MenuCreator
        lvContent = getRootView().findViewById(R.id.lv_content);
        llBottom = getRootView().findViewById(R.id.ll_bottom);
        tvOperationAllChecked = getRootView().findViewById(R.id.tv_operation_all_checked);
        tvOperationTop = getRootView().findViewById(R.id.tv_operation_top);
        tvOperationDelete = getRootView().findViewById(R.id.tv_operation_delete);
        tvEmpty = getRootView().findViewById(R.id.tv_empty);
        tvEmpty.setText(R.string.history_empty_hint);


        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "share" item
                SwipeMenuItem topItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                topItem.setBackground(getResources().getDrawable(R.color.orange_ff9800));
                // set item width
                topItem.setWidth((int) DisplayUtil.dp2px(getActivity(), 80));//这里设定宽度为80，可以改
                // set item title
                topItem.setTitle("置顶");
                // set item title fontsize
                topItem.setTitleSize(14);
                //set icon
                topItem.setIcon(R.drawable.ic_top_white);
                // set item title font color
                topItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(topItem);

                // create "share" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(getResources().getDrawable(R.color.deep_orange_f4511e));
                // set item width
                deleteItem.setWidth((int) DisplayUtil.dp2px(getActivity(), 80));//这里设定宽度为80，可以改
                // set item title
                deleteItem.setTitle("删除");
                // set item title fontsize
                deleteItem.setTitleSize(14);
                //set icon
                deleteItem.setIcon(R.drawable.ic_white_delete);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);

            }
        };


        lvContent.setMenuCreator(creator);

        mHistoryReaderAdapter = new HistoryReaderAdapter(getContext(), list, this);
        lvContent.setAdapter(mHistoryReaderAdapter);

        initListener();
        showList();

    }

    private void showList() {
        list = DatabaseManager.getHistoryHelper().getAllHistory();
        mHistoryReaderAdapter.getList().clear();
        mHistoryReaderAdapter.addList(list);
        mHistoryReaderAdapter.notifyDataSetChanged();
        showEmptyView();
        if (getActivity() instanceof UserBookInfoActivity){
            ((UserBookInfoActivity)getActivity()).setHistoryCount(mHistoryReaderAdapter.getCount());
        }
    }

    private void showEmptyView() {
        if (mHistoryReaderAdapter.getList().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        lvContent.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //置顶/取消取消置顶
                        HistoryBean historyBean = mHistoryReaderAdapter.getList().get(position);
                        if (historyBean.getTopIndex() > 0) {
                            //取消置顶
                            historyBean.setTopIndex(0);
                            DatabaseManager.getHistoryHelper().updateHistoryTopInfo(historyBean);
                            showList();
                        } else {
                            //置顶
                            int maxTopId = DatabaseManager.getHistoryHelper().getMaxTopIndex();
                            historyBean.setTopIndex(maxTopId + 1);
                            DatabaseManager.getHistoryHelper().updateHistoryTopInfo(historyBean);
                            showList();
                        }
                        break;
                    case 1:
                        //删除
                        int volumeId = mHistoryReaderAdapter.getList().get(position).getVolumeId();
                        mHistoryReaderAdapter.getList().remove(position);
                        mHistoryReaderAdapter.notifyDataSetChanged();
                        showEmptyView();
                        //移除阅读进度
                        PreferenceConfig.removeReadingProgress(getContext(), volumeId);
                        //删除数据库信息
                        DatabaseManager.getHistoryHelper().deleteHistory(volumeId + "");
                        break;

                }
                return false;
            }
        });

        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (llBottom.getVisibility() == View.VISIBLE){
                    //操作模式不能跳转
                    mHistoryReaderAdapter.changeChecked(position);
                    return;
                }

                HistoryBean historyBean = mHistoryReaderAdapter.getItem(position);
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
                List<Chapter> list = new ChapterDatabaseHepler(getContext()).getChapterList(historyBean.getVolumeId());
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getName().equals(chapter.getName())) {
                        chapter.setChapterIndex(list.get(i).getChapterIndex());
                        break;
                    }
                }
                bd.putParcelable(BundleConstants.PARAM_CHAPTER, chapter);
                if (((UserBookInfoActivity)getActivity()).getAutoIndex() == -1) {
                    bd.putString(BundleConstants.PARAM_ENTER, "history");
                }

                ActivityUtil.next(getActivity(), ChapterReaderActivity.class, bd, -1);
                getActivity().finish();
            }
        });


        tvOperationTop.setOnClickListener(this);
        tvOperationDelete.setOnClickListener(this);
        tvOperationAllChecked.setOnClickListener(this);
    }

    @Override
    protected int onObtainLayoutResId() {
        return R.layout.fragment_user_reader_history;
    }

    /**
     * 操作
     *
     * @param operation
     */
    public void operation(int operation) {
        switch (operation) {
            case UserBookInfoActivity
                    .OPERATION_TYPE_OPEN_OPERATION:
                dealWithListOperation(true);
                break;
            case UserBookInfoActivity
                    .OPERATION_TYPE_CHECKED_ALL:
                //全选
                mHistoryReaderAdapter.setCheckedAll(true);
                break;
            case UserBookInfoActivity
                    .OPERATION_TYPE_CANCEL_CHECKED_ALL:
                //取消全选
                mHistoryReaderAdapter.setCheckedAll(false);
                break;
            case UserBookInfoActivity
                    .OPERATION_TYPE_CLOSE_OPERATION:
                //退出操作模式
                dealWithListOperation(false);
                break;
        }

    }

    /**
     * 处理列表
     *
     * @param isOperation
     */
    private void dealWithListOperation(boolean isOperation) {
        if (mHistoryReaderAdapter == null){
            return;
        }
        mHistoryReaderAdapter.setOperationModel(isOperation);
        if (isOperation) {
            //打开操作模式
            llBottom.setVisibility(View.VISIBLE);
            lvContent.smoothCloseMenu();
        } else {
            llBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_operation_all_checked:
                if (tvOperationAllChecked.getText().toString().equals("全选")) {
                    operation(UserBookInfoActivity
                            .OPERATION_TYPE_CHECKED_ALL);
                } else {
                    operation(UserBookInfoActivity
                            .OPERATION_TYPE_CANCEL_CHECKED_ALL);
                }
                break;
            case R.id.tv_operation_top:
                //多选置顶、取消置顶
                if (tvOperationTop.getText().toString().equals("置顶")) {
                    //批量置顶
                    //找出已置顶的
                    List<String> selectedPostions = mHistoryReaderAdapter.getSelectIndexs();
                    for (String index : selectedPostions) {
                        HistoryBean historyBean = mHistoryReaderAdapter.getList().get(Integer.valueOf(index));
                        if (historyBean.getTopIndex() > 0) {
                            continue;
                        }
                        //修改为置顶
                        int maxTopId = DatabaseManager.getHistoryHelper().getMaxTopIndex();
                        historyBean.setTopIndex(maxTopId + 1);
                        DatabaseManager.getHistoryHelper().updateHistoryTopInfo(historyBean);
                    }
                    showList();
                } else {
                    //批量取消置顶
                    List<String> selectedPostions = mHistoryReaderAdapter.getSelectIndexs();
                    for (String index : selectedPostions) {
                        HistoryBean historyBean = mHistoryReaderAdapter.getList().get(Integer.valueOf(index));
                        historyBean.setTopIndex(0);
                        DatabaseManager.getHistoryHelper().updateHistoryTopInfo(historyBean);
                    }
                    showList();
                }
                closeOperation();

                break;
            case R.id.tv_operation_delete:
                List<String> selectedPostions = mHistoryReaderAdapter.getSelectIndexs();
                String volumeIds = "";
                //多选删除
                for (int i = 0; i < mHistoryReaderAdapter.getList().size(); i++) {
                    if (selectedPostions.contains(String.valueOf(i))) {
                        volumeIds = volumeIds + mHistoryReaderAdapter.getList().get(i).getVolumeId() + ",";
                        //移除阅读进度
                        PreferenceConfig.removeReadingProgress(getContext(), mHistoryReaderAdapter.getList().get(i).getVolumeId());
                    }
                }
                if (volumeIds.endsWith(",")) {
                    volumeIds = volumeIds.substring(0, volumeIds.lastIndexOf(","));
                }

                DatabaseManager.getHistoryHelper().deleteHistory(volumeIds);
                showList();
                closeOperation();
                break;
        }
    }

    private void closeOperation() {
        if (getActivity() instanceof UserBookInfoActivity) {
            ((UserBookInfoActivity) getActivity()).exitOperation();
        }
    }

    @Override
    public void checkedChange() {
        boolean isTop = false;
        List<String> selectedPostions = mHistoryReaderAdapter.getSelectIndexs();
        Collections.sort(selectedPostions);
        for (String index : selectedPostions) {
            HistoryBean historyBean = mHistoryReaderAdapter.getList().get(Integer.valueOf(index));
            if (historyBean.getTopIndex() == 0) {
                isTop = true;
                break;
            }
        }

        if (isTop || selectedPostions.size() == 0) {
            tvOperationTop.setText("置顶");
        } else {
            tvOperationTop.setText("取消置顶");
        }

        if (selectedPostions.size() == mHistoryReaderAdapter.getList().size()) {
            //已全选
            tvOperationAllChecked.setText("取消全选");
        } else {
            tvOperationAllChecked.setText("全选");
        }

        if (mHistoryReaderAdapter.getList().size() > 0) {
            tvOperationAllChecked.setEnabled(true);
        } else {
            tvOperationAllChecked.setEnabled(false);
        }

        if (selectedPostions.size() > 0) {
            tvOperationTop.setEnabled(true);
            tvOperationDelete.setEnabled(true);

        } else {
            tvOperationTop.setEnabled(false);
            tvOperationDelete.setEnabled(false);
        }

    }
}
