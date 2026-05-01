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
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.read.scriptures.EIUtils.CustomArrayAdapter;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.ChapterDatabaseHepler;
import com.read.scriptures.db.VolumeDatabaseHepler;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.model.Volume;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.SearchBookListAdapter;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.EIUtils.ThreadPool;
import com.read.scriptures.util.UmShareUtils;
import com.zxl.common.db.sqlite.DbException;
import com.zxl.common.db.sqlite.DbUtils;
import com.zxl.common.db.sqlite.Selector;
import com.zxl.common.db.sqlite.WhereBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchVolumeActivity extends BaseActivity {

    private LinearLayout mLayoutHistory;
    private LinearLayout mLayoutResult;
    private ProgressBar mSearchProgress;
    private EditText mSearchEditText;
    private TextView mResultTextView;
    private ListView mResultListView;

    private VolumeDatabaseHepler mVolumeHepler;
    private ChapterDatabaseHepler mChapterHepler;

    private List<String> mSearchHistoryKeyword;
    private List<Bookmark> mBookmarkList;
    private SearchBookListAdapter mSearchBookListAdapter;

    private String mKeyword;
    private int mSearchType = 4;
    private int mCurrenType = 4;
    private int mListViewIndex;
    private Volume mVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_volume);
        StatusBarUtils.initMainColorStatusBar(this);
        initIntentExtra();
        initDatas();
        initActionBar();
        initViews();
    }


    private void initIntentExtra() {
        mVolume = getIntent().getParcelableExtra(BundleConstants.PARAM_VOLUME);
    }

    private void initDatas() {
        mVolumeHepler = new VolumeDatabaseHepler(this);
        mChapterHepler = new ChapterDatabaseHepler(this);
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
        mSearchEditText = (EditText) findViewById(R.id.et_search);
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
        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(mVolume.getVolName().replaceAll("^\\d{1,}-",""));

        RadioGroup rradioGroup = (RadioGroup) findViewById(R.id.radio_group);
        rradioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_1) {
                    mSearchType = 3;
                } else if (checkedId == R.id.rb_2) {
                    mSearchType = 4;
                } else if (checkedId == R.id.rb_3){
                    mSearchType = 2;
                }
            }
        });
        mSearchProgress = (ProgressBar) findViewById(R.id.search_progress);
        mSearchProgress.setVisibility(View.GONE);
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
        mLayoutResult = (LinearLayout) findViewById(R.id.layout_result);
        mResultTextView = (TextView) findViewById(R.id.tv_result);
        mSearchBookListAdapter = new SearchBookListAdapter(this);
        mResultListView = (ListView) findViewById(R.id.listview_result);
        mResultListView.setAdapter(mSearchBookListAdapter);
        mResultListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrenType != 4) {
                    return true;
                }
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
        if (mSearchType == 1) {
            Volume volume = new Volume();
            volume.setId(mBookmarkList.get(position).getVolumeId());
            volume.setVolName(mBookmarkList.get(position).getVolumeName());
            volume.setChpCount(mBookmarkList.get(position).getChapterCount());
            Bundle bd = new Bundle();
            bd.putParcelable(BundleConstants.PARAM_VOLUME, volume);
            ActivityUtil.next(ATHIS, ChaptersListActivity.class, bd, -1);
        } else {
            Bookmark bookmark = mBookmarkList.get(position);
            Chapter chapter = new Chapter();
            chapter.setIndexId(bookmark.getChapterIndexId());
            chapter.setName(bookmark.getChapterName());
            chapter.setVolumeName(bookmark.getVolumeName());
            chapter.setChapterCount(bookmark.getChapterCount());
            chapter.setVolumeId(bookmark.getVolumeId());
            Bundle bd = new Bundle();
            bd.putParcelable(BundleConstants.PARAM_CHAPTER, chapter);
            if (mSearchType == 2 || mSearchType == 4) {
                bd.putInt(BundleConstants.PARAM_TIPS_POSTION, bookmark.getIndex());
                bd.putString(BundleConstants.PARAM_TIPS_KEYWORD, mKeyword);
            }
            // mSearchBookmarkListAdapter.getList().clear();
            ActivityUtil.next(ATHIS, ChapterReaderActivity.class, bd, -1);
        }
//        Bookmark bookmark = mBookmarkList.get(position);
//        Chapter chapter = new Chapter();
//        boolean delete = false;
//        List<Chapter> list = new ChapterDatabaseHepler(this).getChapterList(mVolume.getId());
//        Iterator<Chapter> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            Chapter c = iterator.next();
//            if (c.getShowName().contains("jieshao")) {
//                delete = true;
//            }
//        }
//        if (delete){
//            chapter.setIndexId(bookmark.getChapterIndexId()-1);
//        }else {
//            chapter.setIndexId(bookmark.getChapterIndexId());
//        }
//        chapter.setName(bookmark.getChapterName());
//        chapter.setVolumeName(bookmark.getVolumeName());
//        chapter.setChapterCount(bookmark.getChapterCount());
//        chapter.setVolumeId(bookmark.getVolumeId());
//        Bundle bd = new Bundle();
//        bd.putParcelable(BundleConstants.PARAM_CHAPTER, chapter);
//        if (mSearchType == 3) {
//            bd.putInt(BundleConstants.PARAM_TIPS_POSTION, bookmark.getIndex());
//            bd.putString(BundleConstants.PARAM_TIPS_KEYWORD, mKeyword);
//        }
//        ActivityUtil.next(ATHIS, ChapterReaderActivity.class, bd, -1);
////        ActivityUtil.backWithResult(ATHIS, Activity.RESULT_OK, bd);
    }

    private void clickSearch() {
        mKeyword = mSearchEditText.getText().toString().trim();
        if (TextUtils.isEmpty(mKeyword)) {
            showToastMsg("请输入搜索关键字");
            return;
        }

//        if (mSearchType != 1 && mKeyword.length() < 2) {
//            showToastMsg("请输入2个或以上关键字");
//            return;
//        }
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
        mSearchProgress.setVisibility(View.VISIBLE);
        mLayoutHistory.setVisibility(View.GONE);
        mLayoutResult.setVisibility(View.VISIBLE);
        mSearchBookListAdapter.setSearchType(mSearchType);
        ThreadPool.runOnNonUIThread(new Runnable() {
            @Override
            public void run() {
                final List<Bookmark> bookmarkList;
                if (mSearchType == 3) {
                    bookmarkList = searchChapterByKeyword(mVolume, mKeyword);
                } else if (mSearchType == 4){
                    bookmarkList = searchContentByKeyword(mVolume, mKeyword);
                }else {
                    bookmarkList = searchTitleByKeyword(mKeyword, mVolume);
                }
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
        mSearchProgress.setVisibility(View.GONE);
        mCurrenType = mSearchType;
        mBookmarkList = bookmarkList;
        mSearchBookListAdapter.setList(mBookmarkList);
        mSearchBookListAdapter.notifyDataSetChanged();
        mResultListView.setSelection(0);
        int length = mBookmarkList.size();
        if (mSearchType == 4) {
            StringBuilder sb = new StringBuilder();
            if (mVolume != null) {
                sb.append("在《<font color='#ff0000'>");
//                sb.append(mVolume.getVolName().replaceAll("^\\d{1,}-","").replaceAll("\\(.*?\\)",""));
                sb.append(matchSearchText(mVolume.getVolName()));
                sb.append("</font>》");
            }
            if (length == 0) {
                mResultTextView.setText(Html.fromHtml(sb.toString() + "中没有搜到结果"));
            } else {
                mResultTextView.setText(Html.fromHtml(sb.toString() + "中共搜索到<font color='#ff0000'>" + length + "</font>个结果"));
            }
        } else {
            if (length == 0) {
                mResultTextView.setText("没有搜到结果");
            } else {
                mResultTextView.setText(Html.fromHtml("共搜索到<font color='#ff0000'>" + length + "</font>个结果"));
            }
        }
    }

    private String matchSearchText(String str){
        String regex1 = "\\{([^}])*\\}";
        Pattern P1 = Pattern.compile(regex1);
        Matcher matcher1 = P1.matcher(str);
        while (matcher1.find()) {
           str = str.replace(matcher1.group(), "");
        }
        String regex2 = "\\[([^}])*\\]";
        Pattern P2 = Pattern.compile(regex2);
        Matcher matcher2 = P2.matcher(str);
        while (matcher2.find()) {
            str = str.replace(matcher2.group(), "");
        }
        String regex3 = "\\(([^}])*\\)";
        Pattern P3 = Pattern.compile(regex3);
        Matcher matcher3 = P3.matcher(str);
        while (matcher3.find()) {
            str = str.replace(matcher3.group(), "");
        }
        return str;
    }

    /**
     * 搜索章节
     *
     * @param volume
     * @param keyWord
     * @return
     */
    private List<Bookmark> searchChapterByKeyword(Volume volume, String keyWord) {
        List<Chapter> volumeList = mChapterHepler.getChaptersByVolumeIdLikeName("" + volume.getId(), keyWord);
        List<Bookmark> bookmarkResultPoints = SearchTextUtil.searchChapterByKeyword(volumeList, keyWord,
                mSearchProgress);
        return bookmarkResultPoints;
    }

    /**
     * 搜索内容
     *
     * @param volume
     * @param keyword
     * @return
     */
    private List<Bookmark> searchContentByKeyword(Volume volume, String keyword) {
        List<Volume> volumeList = mVolumeHepler.getVolumeById(volume.getId());
        List<Bookmark> bookmarkSearchPoint = new ArrayList<Bookmark>();
        List<Chapter> chapterList = null;
        int volumeLength = volumeList.size();
        for (int i = 0; i < volumeLength; i++) {
            Volume vol = volumeList.get(i);
            chapterList = mChapterHepler.getChapterList(vol.getId());
            for (int j = 0; j < chapterList.size(); j++) {
                Chapter chapter = chapterList.get(j);
                Bookmark bookmark = new Bookmark();
                bookmark.setCategroyId(vol.getCategoryId() + "");
                bookmark.setVolumeId(chapter.getVolumeId());
                bookmark.setVolumeName(vol.getVolName());
                bookmark.setChapterIndexId(chapter.getIndexId());
                bookmark.setChapterName(chapter.getShowName());
                bookmark.setChapterFileName(
                        SearchTextUtil.getChapterTxtName("" + chapter.getVolumeId(), chapter.getIndexId() + ""));
                bookmarkSearchPoint.add(bookmark);
            }
        }
        Map<String, Object> searchMap = new HashMap<String, Object>();
        searchMap.put("volumeList", volumeList);
        searchMap.put("volume", volume);
        searchMap.put("rootId", 1);
        searchMap.put("searchTitle", false);
        List<Bookmark> bookmarkResultPoints = SearchTextUtil.searchContentByKeyword(keyword, mSearchProgress, searchMap);
        Iterator<Bookmark> iterator = bookmarkResultPoints.iterator();
        while (iterator.hasNext()){
            Bookmark bookmark = iterator.next();
            if (bookmark.getChapterName().contains("jieshao") || bookmark.getChapterName().contains("注释")){
                iterator.remove();
            }
        }
        return bookmarkResultPoints;
    }

    /**
     * 搜索标题
     *
     * @param
     * @return
     */
    private List<Bookmark> searchTitleByKeyword(String keyword, Volume volume) {
        List<Bookmark> bookmarkResultPoints = searchContentByKeyword(keyword, volume, true);
        return bookmarkResultPoints;
    }

    /**
     * 搜索标题
     *
     * @param keyword
     * @param volume
     * @return
     */
    private List<Bookmark> searchContentByKeyword(String keyword, Volume volume, boolean searchTitle) {
        final long start = System.currentTimeMillis();
        List<Volume> volumeList = null;
        List<Bookmark> bookmarkSearchList = new ArrayList<Bookmark>();
        DbUtils dbUtils = HuDongApplication.getInstance().getDbUtils();
        if (volume != null) {
            try {
                volumeList = dbUtils.findAll(
                        Selector.from(Volume.class).where(WhereBuilder.getInstance("id", "=", volume.getId())));
            } catch (DbException e) {
                LogUtil.error("DbException", e);
            }
        }
        LogUtil.test("volume搜索耗时：" + (System.currentTimeMillis() - start));
        for (Volume volumetemp : volumeList) {
            Bookmark bookmark = new Bookmark();
            bookmark.setVolumeId(volumetemp.getId());
            bookmark.setVolumeName(volumetemp.getVolName().replaceAll("^\\d{1,}-",""));
            bookmark.setChapterIndexId(0);
            bookmark.setChapterName("");
            bookmark.setChapterFileName("");
            bookmarkSearchList.add(bookmark);
        }
        LogUtil.test("其他搜索耗时：" + (System.currentTimeMillis() - start));
        Map<String, Object> searchMap = new HashMap<String, Object>();
        searchMap.put("volumeList", volumeList);
        searchMap.put("volume", volume);
        searchMap.put("rootId", 1);
        searchMap.put("searchTitle", searchTitle);

        List<Bookmark> bookmarkResultList = SearchTextUtil.searchContentByKeyword(keyword, mSearchProgress, searchMap);
        Iterator<Bookmark> bookmarkIterator = bookmarkResultList.iterator();
        while (bookmarkIterator.hasNext()){
            Bookmark bookmark = bookmarkIterator.next();
            if (bookmark.getChapterName().contains("jieshao")){
                bookmarkIterator.remove();
            }
        }
        return bookmarkResultList;
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
        if (mVolume != null) {
            bookmark.setCategroyId(mVolume.getCategoryId() + "");
        }
        switch (menuItem.getItemId()) {
        case 1:
            // 加入书签
            Bundle bd = new Bundle();
            ArrayList<Bookmark> list = new ArrayList<Bookmark>();
            list.add(bookmark);
            bd.putParcelableArrayList(BundleConstants.PARAM_BOOK_MARK_LIST, list);
            ActivityUtil.next(ATHIS, BookmarkEditActivity.class, bd, -1);
            break;
        case 2:
            CommonUtil.callSystemSmsAction(ATHIS, "", bookmark.getContent());
            break;
        case 3:
            StringBuffer copy = new StringBuffer();
            copy.append("《" + bookmark.getVolumeName() + "》");
            copy.append(bookmark.getChapterName());
            copy.append("\n  " + bookmark.getReplaceContent());
            CommonUtil.copy(ATHIS,copy.toString());
            break;
        case 4:
            clickListItem(mListViewIndex);
            break;
        case 5:
            // 分享
            StringBuffer shareSb = new StringBuffer();
            shareSb.append("《" + bookmark.getVolumeName() + "》");
            shareSb.append(bookmark.getChapterName());
            shareSb.append("\n  " + bookmark.getReplaceContent());
            UmShareUtils.shareText(this,shareSb.toString());
            break;
        case 6:
            break;
        }
        return true;
    }

}
