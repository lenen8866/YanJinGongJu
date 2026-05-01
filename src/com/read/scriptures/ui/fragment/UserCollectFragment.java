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
import com.read.scriptures.bean.CollectBean;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.DatabaseManager;
import com.read.scriptures.model.Volume;
import com.read.scriptures.ui.activity.ChaptersListActivity;
import com.read.scriptures.ui.activity.UserBookInfoActivity;
import com.read.scriptures.ui.adapter.baike.CollectAdapter;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.util.DisplayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Time: 2020/9/15
 * Author: a123
 * Description: 收藏
 */
public class UserCollectFragment extends BaseFragment implements View.OnClickListener, CollectAdapter.CheckedChangeCallback {

    SwipeMenuListView lvContent;
    LinearLayout llBottom;
    TextView tvOperationTop;
    TextView tvOperationDelete;
    TextView tvOperationAllChecked;
    TextView tvEmpty;


    List<CollectBean> list = new ArrayList<>();
    CollectAdapter mCollectAdapter;

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
        tvEmpty.setText(R.string.collect_empty_hint);


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

        mCollectAdapter = new CollectAdapter(getContext(), list,this);
        lvContent.setAdapter(mCollectAdapter);

        initListener();
        showList();

    }

    private void showList() {
        list = DatabaseManager.getHistoryHelper().getAllCollectBean();
        mCollectAdapter.getList().clear();
        mCollectAdapter.addList(list);
        mCollectAdapter.notifyDataSetChanged();
        showEmptyView();
        if (getActivity() instanceof UserBookInfoActivity){
            ((UserBookInfoActivity)getActivity()).setCollectCount(mCollectAdapter.getCount());
        }
    }

    private void showEmptyView() {
        if(mCollectAdapter.getList().isEmpty()){
            tvEmpty.setVisibility(View.VISIBLE);
        }else{
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
                        CollectBean collectBean = mCollectAdapter.getList().get(position);
                        if (collectBean.getTopIndex() > 0) {
                            //取消置顶
                            collectBean.setTopIndex(0);
                            DatabaseManager.getHistoryHelper().updateCollectTopInfo(collectBean);
                            showList();
                        } else {
                            //置顶
                            int maxTopId = DatabaseManager.getHistoryHelper().getCollectMaxTopIndex();
                            collectBean.setTopIndex(maxTopId + 1);
                            DatabaseManager.getHistoryHelper().updateCollectTopInfo(collectBean);
                            showList();
                        }
                        break;
                    case 1:
                        //删除
                        int volumeId = mCollectAdapter.getList().get(position).getVolumeId();
                        mCollectAdapter.getList().remove(position);
                        mCollectAdapter.notifyDataSetChanged();
                        showEmptyView();
                        //删除数据库信息
                        DatabaseManager.getHistoryHelper().deleteCollect(volumeId + "");
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
                    mCollectAdapter.changeChecked(position);
                    return;
                }
                CollectBean collectBean = mCollectAdapter.getItem(position);

                Volume volume = new Volume();
                volume.setCategoryId(collectBean.getCategoryId());
                volume.setId(collectBean.getVolumeId());
                volume.setParentId(collectBean.getParentId());
                volume.setVolName(collectBean.getVolumeName());
                volume.setIntro(collectBean.getChapter());
                Bundle bd = new Bundle();
                bd.putParcelable(BundleConstants.PARAM_VOLUME, volume);
                ActivityUtil.next(getActivity(), ChaptersListActivity.class, bd, -1);
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
                mCollectAdapter.setCheckedAll(true);
                break;
            case UserBookInfoActivity
                    .OPERATION_TYPE_CANCEL_CHECKED_ALL:
                //取消全选
                mCollectAdapter.setCheckedAll(false);
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
        if (mCollectAdapter == null){
            return;
        }
        mCollectAdapter.setOperationModel(isOperation);
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
                }else{
                    operation(UserBookInfoActivity
                            .OPERATION_TYPE_CANCEL_CHECKED_ALL);
                }
                break;
            case R.id.tv_operation_top:
                //多选置顶、取消置顶
                if (tvOperationTop.getText().toString().equals("置顶")) {
                    //批量置顶
                    //找出已置顶的
                    List<String> selectedPostions = mCollectAdapter.getSelectIndexs();
                    for (String index : selectedPostions) {
                        CollectBean collectBean = mCollectAdapter.getList().get(Integer.valueOf(index));
                        if (collectBean.getTopIndex() > 0) {
                            continue;
                        }
                        //修改为置顶
                        int maxTopId = DatabaseManager.getHistoryHelper().getCollectMaxTopIndex();
                        collectBean.setTopIndex(maxTopId+1);
                        DatabaseManager.getHistoryHelper().updateCollectTopInfo(collectBean);
                    }
                    showList();
                } else {
                    //批量取消置顶
                    List<String> selectedPostions = mCollectAdapter.getSelectIndexs();
                    for (String index : selectedPostions) {
                        CollectBean collectBean = mCollectAdapter.getList().get(Integer.valueOf(index));
                        collectBean.setTopIndex(0);
                        DatabaseManager.getHistoryHelper().updateCollectTopInfo(collectBean);
                    }
                    showList();
                }
                closeOperation();

                break;
            case R.id.tv_operation_delete:
                List<String> selectedPostions = mCollectAdapter.getSelectIndexs();
                String volumeIds = "";
                //多选删除
                for (int i = 0; i < mCollectAdapter.getList().size(); i++) {
                    if (selectedPostions.contains(String.valueOf(i))) {
                        volumeIds = volumeIds + mCollectAdapter.getList().get(i).getVolumeId() + ",";
                    }
                }
                if (volumeIds.endsWith(",")) {
                    volumeIds = volumeIds.substring(0, volumeIds.lastIndexOf(","));
                }

                DatabaseManager.getHistoryHelper().deleteCollect(volumeIds);
                showList();
                closeOperation();
                break;
        }
    }

    private void closeOperation(){
        if (getActivity() instanceof UserBookInfoActivity){
            ((UserBookInfoActivity)getActivity()).exitOperation();
        }
    }

    @Override
    public void checkedChange() {
        boolean isTop = false;
        List<String> selectedPostions = mCollectAdapter.getSelectIndexs();
        Collections.sort(selectedPostions);
        for (String index : selectedPostions) {
            CollectBean collectBean = mCollectAdapter.getList().get(Integer.valueOf(index));
            if (collectBean.getTopIndex() == 0){
                isTop = true;
                break;
            }
        }

        if (isTop || selectedPostions.size() == 0){
            tvOperationTop.setText("置顶");
        }else{
            tvOperationTop.setText("取消置顶");
        }

        if (selectedPostions.size() == mCollectAdapter.getList().size()){
            //已全选
            tvOperationAllChecked.setText("取消全选");
        }else{
            tvOperationAllChecked.setText("全选");
        }

        if (mCollectAdapter.getList().size() > 0){
            tvOperationAllChecked.setEnabled(true);
        }else{
            tvOperationAllChecked.setEnabled(false);
        }

        if (selectedPostions.size() > 0){
            tvOperationTop.setEnabled(true);
            tvOperationDelete.setEnabled(true);

        }else{
            tvOperationTop.setEnabled(false);
            tvOperationDelete.setEnabled(false);
        }

    }
}
