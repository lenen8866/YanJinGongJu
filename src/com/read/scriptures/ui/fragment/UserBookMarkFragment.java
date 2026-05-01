package com.read.scriptures.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.R;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.ChapterDatabaseHepler;
import com.read.scriptures.db.HistoryDatabaseHelper;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.model.Spirituality;
import com.read.scriptures.ui.activity.ChapterReaderActivity;
import com.read.scriptures.ui.activity.SpiritualityContentActivity;
import com.read.scriptures.ui.activity.UserBookInfoActivity;
import com.read.scriptures.ui.adapter.BookMarkListAdapter;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.SharedPreferencesUtils;
import com.read.scriptures.util.UmShareUtils;
import com.read.scriptures.widget.ClearEditText;
import com.read.scriptures.widget.CopyAskDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Time: 2020/9/17
 * Author: a123
 * Description:
 */
public class UserBookMarkFragment extends BaseFragment
        implements View.OnClickListener, TextWatcher, BookMarkListAdapter.CheckedChangeCallback {

    private SwipeMenuListView lvContent;
    private CheckBox cbSort;
    private TextView tvCount;
    private TextView tvEmpty;
    private ClearEditText etSearch;
    //    private TextView tvSearch;
    private LinearLayout llBottom;
    private TextView tvOperationAllChecked;
    private TextView tvOperationDelete;
    private TextView tvOperationCopy;
    private RelativeLayout rlTop;

    private BookMarkListAdapter mBookMarkListAdapter;
    private HistoryDatabaseHelper mBookmarkDatabaseHepler;
    private ArrayList<Bookmark> list;
    private String orderBy = "desc";
    private int onLongItemIndex;

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initWidget() {
        mBookmarkDatabaseHepler = new HistoryDatabaseHelper(getActivity());

        rlTop = getRootView().findViewById(R.id.rl_top);
        lvContent = getRootView().findViewById(R.id.lv_content);
        cbSort = getRootView().findViewById(R.id.cb_sort);
        tvCount = getRootView().findViewById(R.id.tv_count);
        etSearch = getRootView().findViewById(R.id.et_search);
//        tvSearch = getRootView().findViewById(R.id.tv_search);
        llBottom = getRootView().findViewById(R.id.ll_bottom);
        tvOperationAllChecked = getRootView().findViewById(R.id.tv_operation_all_checked);
        tvOperationDelete = getRootView().findViewById(R.id.tv_operation_delete);
        tvOperationCopy = getRootView().findViewById(R.id.tv_operation_copy);
        tvEmpty = getRootView().findViewById(R.id.tv_empty);

        mBookMarkListAdapter = new BookMarkListAdapter(getActivity(), new ArrayList<Bookmark>(), this);
        lvContent.setAdapter(mBookMarkListAdapter);

        initListener();

        if (((UserBookInfoActivity) getActivity()).isNeedAutoSearchMarkKeyword()) {
            //需要自动填充
            etSearch.setText(((UserBookInfoActivity) getActivity()).getKeyWords());//SharedPreferencesUtils.getMarkSearchKeyword(getContext())
            SharedPreferencesUtils.saveMarkSearchKeyword(getContext(), "");
        }
        searchKeyWords();
    }

    private void initListener() {
//        tvSearch.setOnClickListener(this);

        registerForContextMenu(lvContent);
        tvOperationAllChecked.setOnClickListener(this);
        tvOperationDelete.setOnClickListener(this);
        tvOperationCopy.setOnClickListener(this);
        etSearch.setListener(new ClearEditText.TouchAndTextChangeListener() {
            @Override
            public boolean onTouch(MotionEvent event) {
                return false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchKeyWords();
            }
        });
        cbSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //降序
                    orderBy = "desc";
                } else {
                    //升序
                    orderBy = "asc";
                }
                searchKeyWords();
            }
        });
        lvContent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBookMarkListAdapter.isOperationModel()) {
                    return true;
                }
                onLongItemIndex = position;
//                view.showContextMenu();
                return false;
            }
        });
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBookMarkListAdapter.isOperationModel()) {
                    mBookMarkListAdapter.changeChecked(position);
                    return;
                }
                jump(mBookMarkListAdapter.getItem(position));
            }
        });
    }

    private void searchKeyWords() {
        String keyword = etSearch.getText().toString();
        List<String> keywordList = new ArrayList();
        if (keyword.trim().isEmpty()) {
            keywordList.addAll(Arrays.asList(new String[]{etSearch.getText().toString()}));
            list = mBookmarkDatabaseHepler.getBookmarkList(orderBy, keywordList);
        } else {
            //不为空，根据关键字查询
            String[] keywords = keyword.split(" ");
            List<String> strList = Arrays.asList(keywords);
            for (String searchKeyword : strList) {
                //去重复和空
                if (!searchKeyword.trim().isEmpty() && !keywordList.contains(searchKeyword)) {
                    keywordList.add(searchKeyword);
                }
            }
            list = mBookmarkDatabaseHepler.getBookmarkList(orderBy, keywordList);

        }
        showList(keywordList);
    }

    private void showList(List<String> keywordList) {
        mBookMarkListAdapter.getList().clear();
        mBookMarkListAdapter.addList(list);
        mBookMarkListAdapter.setKeyWord(keywordList);
        mBookMarkListAdapter.notifyDataSetChanged();
        tvCount.setText(list.size() < 100 ? list.size() + "" : "99+");
        tvCount.setVisibility(etSearch.getText().toString().trim().isEmpty() ? View.INVISIBLE : View.VISIBLE);
        showEmptyView();

        if (getActivity() instanceof UserBookInfoActivity) {
            List<Bookmark> bookmarks = mBookmarkDatabaseHepler.getBookmarkList(orderBy, Arrays.asList(new String[]{""}));
            ((UserBookInfoActivity) getActivity()).setMarkCount(bookmarks.size());
        }
    }

    private void showEmptyView() {
        if (mBookMarkListAdapter.getList().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            if (etSearch.getText().toString().trim().isEmpty()) {
                tvEmpty.setText(R.string.mark_list_empty);
            } else {
                tvEmpty.setText(R.string.serach_mark_list_empty);
            }
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected int onObtainLayoutResId() {
        return R.layout.fragment_user_book_remark;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onClick(View v) {
        String ids = "";
        switch (v.getId()) {
            case R.id.tv_operation_copy:
                showCopyAskDialog();
                break;
            case R.id.tv_operation_delete:
                List<String> selectedPostions = mBookMarkListAdapter.getSelectIndexs();
                //多选删除
                for (int i = 0; i < mBookMarkListAdapter.getList().size(); i++) {
                    if (selectedPostions.contains(String.valueOf(i))) {
                        ids = ids + mBookMarkListAdapter.getList().get(i).getId() + ",";
                    }
                }
                if (ids.endsWith(",")) {
                    ids = ids.substring(0, ids.lastIndexOf(","));
                }
                mBookmarkDatabaseHepler.deleteMarkByIds(ids);
                searchKeyWords();
                closeOperation();
                break;
            case R.id.tv_operation_all_checked:
                if (tvOperationAllChecked.getText().toString().equals("全选")) {
                    operation(UserBookInfoActivity
                            .OPERATION_TYPE_CHECKED_ALL);
                } else {
                    operation(UserBookInfoActivity
                            .OPERATION_TYPE_CANCEL_CHECKED_ALL);
                }
                break;
        }
    }

    private CopyAskDialog mCommonAlertDialog;

    private void showCopyAskDialog() {
        if (null == mCommonAlertDialog && getActivity() != null && !getActivity().isDestroyed()) {
            List<String> list = mBookMarkListAdapter.getSelectIndexs();
            if (list == null || list.isEmpty()) {
                showToast("请选择复制内容");
                return;
            }
            mCommonAlertDialog = new CopyAskDialog(getActivity(), "本次复制 " + list.size() + " 条书签", isHasComent(),
                    new CopyAskDialog.CopyAskBack() {
                        @Override
                        public void callBack(boolean isCopyDes) {
                            copyOperate(isCopyDes);
                        }
                    });
        }
        if (getActivity() != null && !getActivity().isDestroyed() && !mCommonAlertDialog.isShowing()) {
            mCommonAlertDialog.setHasComent(isHasComent());
            mCommonAlertDialog.show();
        }
    }

    private void copyOperate(boolean isCopyComment) {
        String copyTxt = getCopyTxt(isCopyComment);
        copyText(copyTxt);
        closeOperation();
        mCommonAlertDialog.dismiss();
    }

    private String getCopyTxt(boolean isCopyComment) {
        String copyStr = "";
        List<String> selectedPostions = mBookMarkListAdapter.getSelectIndexs();
        //多选复制
        for (int i = 0; i < mBookMarkListAdapter.getList().size(); i++) {
            if (selectedPostions.contains(String.valueOf(i))) {
                if (TextUtils.isEmpty(copyStr)) {
                    copyStr = copyStr + mBookMarkListAdapter.getBookMarkContent(mBookMarkListAdapter.getList().get(i));
                } else {
                    copyStr = copyStr + "\n\n" + mBookMarkListAdapter.getBookMarkContent(mBookMarkListAdapter.getList().get(i));
                }

                if (isCopyComment) {
                    if (!TextUtils.isEmpty(mBookMarkListAdapter.getBookMarkDesc(mBookMarkListAdapter.getList().get(i)))) {
                        copyStr = copyStr + "\n〖感受〗" + mBookMarkListAdapter.getBookMarkDesc(mBookMarkListAdapter.getList().get(i));
                    }
                }
            }
        }

        return copyStr;
    }

    private boolean isHasComent() {
        boolean isHasComent = false;
        List<String> selectedPostions = mBookMarkListAdapter.getSelectIndexs();
        //多选复制
        for (int i = 0; i < mBookMarkListAdapter.getList().size(); i++) {
            if (selectedPostions.contains(String.valueOf(i))) {
                if (!TextUtils.isEmpty(mBookMarkListAdapter.getBookMarkDesc(mBookMarkListAdapter.getList().get(i)))) {
                    isHasComent = true;
                }
            }
        }

        return isHasComent;
    }


    private void copyText(String txt) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", txt);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        showToast("内容已复制");
    }

    @Override
    public void checkedChange() {
        List<String> selectedPostions = mBookMarkListAdapter.getSelectIndexs();
        if (selectedPostions.size() == mBookMarkListAdapter.getList().size()) {
            //已全选
            tvOperationAllChecked.setText("取消全选");
        } else {
            tvOperationAllChecked.setText("全选");
        }

        if (mBookMarkListAdapter.getList().size() > 0) {
            tvOperationAllChecked.setEnabled(true);
        } else {
            tvOperationAllChecked.setEnabled(false);
        }

        if (selectedPostions.size() > 0) {
            tvOperationDelete.setEnabled(true);
            tvOperationCopy.setEnabled(true);

        } else {
            tvOperationDelete.setEnabled(false);
            tvOperationCopy.setEnabled(false);
        }

    }

    @Override
    public void jump(Bookmark bookmark) {
        Bundle bd = new Bundle();
        if (bookmark.getType() == 1) {
            Spirituality spirituality = new Spirituality();
            spirituality.setId(bookmark.getVolumeId());
            spirituality.setName(bookmark.getVolumeName());
            spirituality.setDaytime(bookmark.getChapterName());
            spirituality.setBook(bookmark.getVolumeName());
            bd.putParcelable(BundleConstants.PARAM_SPIRITUALITY, spirituality);


            if (((UserBookInfoActivity) getActivity()).getAutoIndex() == -1) {
                bd.putString(BundleConstants.PARAM_ENTER, "mark");
            }

            bd.putInt(BundleConstants.PARAM_TIPS_POSTION, -1);
            bd.putString(BundleConstants.PARAM_TIPS_CONTENT, bookmark.getContent());
            bd.putString(BundleConstants.PARAM_TIPS_KEYWORD, etSearch.getText().toString().trim());

            if (((UserBookInfoActivity) getActivity()).getAutoIndex() == 2) {
                if (((UserBookInfoActivity) getActivity()).isBack()) {
                    bd.putString(BundleConstants.PARAM_ENTER, "mark");
                } else {
                    bd.putString(BundleConstants.PARAM_ENTER, "history");
                }
                ActivityUtil.nextActivityWithClearTop(getActivity(), SpiritualityContentActivity.class, bd);
                if (((UserBookInfoActivity) getActivity()).isBack()) {
                    getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                } else {
                    getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            } else {
                ActivityUtil.nextActivityWithClearTop(getActivity(), SpiritualityContentActivity.class, bd);
            }
        } else {
            SharedPreferencesUtils.saveMarkSearchKeyword(getContext(), etSearch.getText().toString());
            Chapter chapter = new Chapter();
            chapter.setIndexId(bookmark.getChapterIndexId());
            chapter.setName(bookmark.getChapterName());
            chapter.setVolumeName(bookmark.getVolumeName());
            chapter.setChapterCount(bookmark.getChapterCount());
            chapter.setVolumeId(bookmark.getVolumeId());
            List<Chapter> list = new ChapterDatabaseHepler(getActivity()).getChapterList(bookmark.getVolumeId());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equals(chapter.getName())) {
                    chapter.setChapterIndex(list.get(i).getChapterIndex());
                    break;
                }
            }
            bd.putInt(BundleConstants.PARAM_TIPS_POSTION, -1);
            bd.putParcelable(BundleConstants.PARAM_CHAPTER, chapter);
            bd.putString(BundleConstants.PARAM_TIPS_CONTENT, bookmark.getContent());
            bd.putString(BundleConstants.PARAM_TIPS_KEYWORD, etSearch.getText().toString().trim());
            if (((UserBookInfoActivity) getActivity()).getAutoIndex() == -1) {
                bd.putString(BundleConstants.PARAM_ENTER, "mark");
            }
        }
        bd.putString(BundleConstants.PARAM_TIPS_KEYWORD, etSearch.getText().toString().trim());
        if (((UserBookInfoActivity) getActivity()).getAutoIndex() == 2) {
            if (((UserBookInfoActivity) getActivity()).isBack()) {
                bd.putString(BundleConstants.PARAM_ENTER, "mark");
            } else {
                bd.putString(BundleConstants.PARAM_ENTER, "history");
            }
            ActivityUtil.nextActivityWithClearTop(getActivity(), ChapterReaderActivity.class, bd);
            if (((UserBookInfoActivity) getActivity()).isBack()) {
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            } else {
                getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        } else {
            ActivityUtil.nextActivityWithClearTop(getActivity(), ChapterReaderActivity.class, bd);

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        contextMenu.add(1, 1, 0, "以短信发送");
        contextMenu.add(1, 2, 0, "复制");
        contextMenu.add(1, 3, 0, "分享");
        contextMenu.add(1, 4, 0, "取消");
    }


    public boolean onContextItemSelected(MenuItem menuItem) {
        // 获取当前被选择的菜单项的信息
        Bookmark bookmark = mBookMarkListAdapter.getItem(onLongItemIndex);
        switch (menuItem.getItemId()) {
            case 1:
                // 已短信发送
                //以短信方式发送
                CommonUtil.callSystemSmsAction(getActivity(), "", bookmark.getContent());
                break;
            case 2:
                // 复制
                StringBuffer copy = new StringBuffer();
                copy.append("《" + bookmark.getVolumeName().substring(0, bookmark.getVolumeName().indexOf("(")).replaceAll("E", "") + "》");
                copy.append(bookmark.getChapterName());
                copy.append("\n  " + bookmark.getReplaceContent());
                String value = copy.toString();
                value = value.replaceAll("〖(.*?)〗", "");
                value = value.replaceAll("(?<=\\[)(.*?)(?=])", "");
                value = value.replaceAll("(?<=\\{)[^}]*(?=\\})", "");
                value = value.replaceAll("\\[\\]", "");
                value = value.replaceAll("\\{\\}", "");
                CommonUtil.copy(getActivity(), value);
                showToast("复制成功");
                break;
            case 3:
                // 分享
                StringBuffer shareSb = new StringBuffer();
                shareSb.append("《" + bookmark.getVolumeName().substring(0, bookmark.getVolumeName().indexOf("(")).replaceAll("E", "") + "》");
                shareSb.append(bookmark.getChapterName());
                shareSb.append("\n  " + bookmark.getReplaceContent());
                UmShareUtils.shareText(getActivity(), shareSb.toString());
                break;
            case 4:
                // 取消
                break;
        }
        return true;
    }


    @Override
    public void deleteItem(Bookmark bookmark) {
        mBookmarkDatabaseHepler.deleteMarkByIds(bookmark.getId() + "");
        searchKeyWords();
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
                mBookMarkListAdapter.setCheckedAll(true);
                break;
            case UserBookInfoActivity
                    .OPERATION_TYPE_CANCEL_CHECKED_ALL:
                //取消全选
                mBookMarkListAdapter.setCheckedAll(false);
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
        if (mBookMarkListAdapter == null) {
            return;
        }
        mBookMarkListAdapter.setOperationModel(isOperation);
        if (isOperation) {
            //打开操作模式
            llBottom.setVisibility(View.VISIBLE);
            tvCount.setVisibility(View.GONE);
            rlTop.setVisibility(View.GONE);
            lvContent.smoothCloseMenu();
        } else {
            llBottom.setVisibility(View.GONE);
            tvCount.setVisibility(View.VISIBLE);
            rlTop.setVisibility(View.VISIBLE);
        }
    }


    private void closeOperation() {
        if (getActivity() instanceof UserBookInfoActivity) {
            ((UserBookInfoActivity) getActivity()).exitOperation();
        }
    }

}
