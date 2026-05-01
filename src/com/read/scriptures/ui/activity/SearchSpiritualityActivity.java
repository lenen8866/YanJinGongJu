package com.read.scriptures.ui.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.read.scriptures.EIUtils.CustomArrayAdapter;
import com.read.scriptures.R;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.model.Spirituality;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.SearchBookListAdapter;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.EIUtils.ThreadPool;
import com.read.scriptures.util.UmShareUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 灵修搜索
 */
public class SearchSpiritualityActivity extends BaseActivity {

    private LinearLayout mLayoutHistory;
    private LinearLayout mLayoutResult;

    private EditText mSearchEditText;
    private TextView mResultTextView;
    private ListView mResultListView;

    private List<String> mSearchHistoryKeyword;
    private List<Bookmark> mBookmarkList;
    private SearchBookListAdapter mSearchBookListAdapter;

    private String mKeyword;
    private int mListViewIndex;
    private Spirituality mSpirituality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_spirituality);
        StatusBarUtils.initMainColorStatusBar(this);
        initIntentExtra();
        initDatas();
        initActionBar();
        initViews();
    }


    private void initIntentExtra() {
        mSpirituality = getIntent().getParcelableExtra(BundleConstants.PARAM_SPIRITUALITY);
    }

    @SuppressWarnings("unchecked")
    private void initDatas() {
        // 初始化搜索历史关键字数据
        mSearchHistoryKeyword = new ArrayList<String>();
        List<String> keyword = null;
        try {
            keyword = (List<String>) PreferencesUtils.getObject(this,"keyword");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (keyword != null) {
            mSearchHistoryKeyword.addAll(keyword);
        }
    }

    private void initActionBar() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSearch();
            }
        });
        mSearchEditText = findViewById(R.id.et_search);
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    clickSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void initViews() {
        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(mSpirituality.getShowName());

        // 搜索历史
        mLayoutHistory = (LinearLayout) findViewById(R.id.layout_history);
        if (mSearchHistoryKeyword != null && mSearchHistoryKeyword.size() > 0) {
            ListView listview_history = (ListView) findViewById(R.id.listview_history);
            listview_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mSearchEditText.setText(mSearchHistoryKeyword.get(position));
                    mSearchEditText.setSelection(mSearchHistoryKeyword.get(position).length());
                    hideInput();
                }
            });
            listview_history.setAdapter(new CustomArrayAdapter(this, R.layout.adapter_search_history_item,
                    R.id.tv_title, mSearchHistoryKeyword));
        } else {
            mLayoutHistory.setVisibility(View.GONE);
        }
        // 搜索结果
        mLayoutResult = findViewById(R.id.layout_result);
        mResultTextView = findViewById(R.id.tv_result);
        mSearchBookListAdapter = new SearchBookListAdapter(this);
        mResultListView = findViewById(R.id.listview_result);
        mResultListView.setAdapter(mSearchBookListAdapter);
        mResultListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mListViewIndex = position;
                return false;
            }
        });
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickListItem(position);
            }
        });
        registerForContextMenu(mResultListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void clickListItem(int position) {
        Bookmark bookmark = mBookmarkList.get(position);
        Spirituality spirituality = new Spirituality();
        spirituality.setId(bookmark.getVolumeId());
        spirituality.setBook(bookmark.getVolumeName());
        spirituality.setDaytime(bookmark.getChapterName());
        Bundle bd = new Bundle();
        bd.putParcelable(BundleConstants.PARAM_SPIRITUALITY, spirituality);
        bd.putInt(BundleConstants.PARAM_TIPS_POSTION, bookmark.getIndex());
        bd.putString(BundleConstants.PARAM_TIPS_KEYWORD, mKeyword);
        ActivityUtil.nextActivityWithClearTop(ATHIS, SpiritualityContentActivity.class, bd, -1, -1);
    }

    private void clickSearch() {
        mKeyword = mSearchEditText.getText().toString().trim();
        if (TextUtils.isEmpty(mKeyword)) {
            showToastMsg("请输入2个或以上关键字");
            return;
        }
        // 保存搜索关键字
        if (mSearchHistoryKeyword.contains(mKeyword)) {
            mSearchHistoryKeyword.remove(mKeyword);
            mSearchHistoryKeyword.add(0, mKeyword);
        } else {
            mSearchHistoryKeyword.add(0, mKeyword);
            if (mSearchHistoryKeyword.size() > 5) {
                mSearchHistoryKeyword.remove(5);
            }
            try {
                PreferencesUtils.putObject(this,"keyword", mSearchHistoryKeyword);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        hideInput();
        showProgressDialog("搜索中");
        mLayoutHistory.setVisibility(View.GONE);
        mLayoutResult.setVisibility(View.VISIBLE);
        mSearchBookListAdapter.setSearchType(4);
        ThreadPool.runOnNonUIThread(new Runnable() {
            @Override
            public void run() {
                final List<Bookmark> bookmarkList = searchByKeyword(mSpirituality, mKeyword);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSearchResult(bookmarkList);
                    }
                });
            }
        });
    }

    /**
     * 显示搜索结果
     *
     * @param bookmarkList
     */
    private void showSearchResult(List<Bookmark> bookmarkList) {
        dismissProgressDialog();
        mBookmarkList = bookmarkList;
        mSearchBookListAdapter.setList(mBookmarkList);
        mSearchBookListAdapter.notifyDataSetChanged();
        mResultListView.setSelection(0);
        int length = mBookmarkList.size();
        if (length == 0) {
            mResultTextView.setText("没有搜到结果");
        } else {
            mResultTextView.setText(Html.fromHtml("共搜索到<font color='#ff0000'>" + length + "</font>个结果"));
        }
    }

    private List<Bookmark> searchByKeyword(Spirituality volumeff, String keyword) {
        List<Bookmark> bookmarkResultPoints = SearchTextUtil.searchSpiritualityContentByKeyword(this, volumeff,
                keyword);
        return bookmarkResultPoints;
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        contextMenu.add(1, 1, 0, "加入书签");
        contextMenu.add(1, 2, 0, "以短信发送");
        contextMenu.add(1, 3, 0, "复制");
        contextMenu.add(1, 4, 0, "跳到");
        contextMenu.add(1, 5, 0, "分享");
        contextMenu.add(1, 6, 0, "取消");
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        // 获取当前被选择的菜单项的信息
        Bookmark bookmark = mBookmarkList.get(mListViewIndex);
        switch (menuItem.getItemId()) {
            case 1:
                // 加入书签
                Bundle bd = new Bundle();
                ArrayList<Bookmark> list = new ArrayList<Bookmark>();
                list.add(bookmark);
                bd.putParcelableArrayList(BundleConstants.PARAM_BOOK_MARK_LIST, list);
                ActivityUtil.next(ATHIS, BookmarkEditActivity.class, bd, -1);
                break;
            case 3:
                String value = bookmark.getReplaceContent();
                value = value.replaceAll("〖(.*?)〗", "");
                value = value.replaceAll("(?<=\\[)(.*?)(?=])", "");
                value = value.replaceAll("(?<=\\{)[^}]*(?=\\})", "");
                value = value.replaceAll("\\[\\]", "");
                value = value.replaceAll("\\{\\}", "");
                CommonUtil.copy(ATHIS, value);
                break;
            case 4:
                clickListItem(mListViewIndex);
                break;
            case 5:
                // 分享
                StringBuffer shareSb = new StringBuffer();
                shareSb.append("《" + bookmark.getVolumeName().substring(0, bookmark.getVolumeName().indexOf("(")).replaceAll("E", "") + "》");
                shareSb.append("\n  " + bookmark.getReplaceContent());
                UmShareUtils.shareText(this, shareSb.toString());
                break;
            case 6:
                break;
        }
        return true;
    }
}
