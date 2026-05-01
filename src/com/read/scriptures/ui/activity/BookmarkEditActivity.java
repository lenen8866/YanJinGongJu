package com.read.scriptures.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.CategoryDatabaseHelper;
import com.read.scriptures.db.HistoryDatabaseHelper;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.model.Category;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.BookmarkEditListViewAdapter;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.StringUtil;

import java.util.List;

/**
 * Created by Administrator.
 * Datetime: 2015/7/10.
 * Email: lgmshare@mgail.com
 */
public class BookmarkEditActivity extends BaseActivity {

    private List<Bookmark> mBookmarks;

    private EditText et_description;

    private TextView title;

    private TextView tvRight;
    private CategoryDatabaseHelper categoryDatabaseHelper;
    private ImageView ivLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_edit);
        StatusBarUtils.initMainColorStatusBar(this);
        categoryDatabaseHelper = new CategoryDatabaseHelper(this);
        initExtras();
        initViews();
    }

    private void initExtras() {
        mBookmarks = getIntent().getParcelableArrayListExtra(BundleConstants.PARAM_BOOK_MARK_LIST);
    }


    private void initViews() {
        ivLeft = findViewById(R.id.iv_left);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        title = (TextView) findViewById(R.id.tv_title);
        title.setText("添加书签");
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvRight = (TextView) findViewById(R.id.tv_right);
        tvRight.setText("书签列表");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("index",2);
                ActivityUtil.next(ATHIS, UserBookInfoActivity.class,bundle,-1);
            }
        });
        TextView tv_num = (TextView) findViewById(R.id.tv_num);
        tv_num.setText("本次添加" + mBookmarks.size() + " 段落");
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new BookmarkEditListViewAdapter(this, mBookmarks));
        et_description = (EditText) findViewById(R.id.et_description);
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = et_description.getText().toString();
                for (int i = 0; i < mBookmarks.size(); i++) {
                    if (!StringUtil.isEmpty(mBookmarks.get(i).getCategroyId())){
                        //查询类型名字
                        Category twoLevelCategory = categoryDatabaseHelper.getCategoryById(Integer.valueOf(mBookmarks.get(i).getCategroyId()));
                        if (twoLevelCategory != null) {
                            Category parentCategory = categoryDatabaseHelper.getCategoryById(twoLevelCategory.getParentId());
                            if (parentCategory != null){
                                String categoryName = parentCategory.getCateName() +"-"+twoLevelCategory.getCateName();
                                mBookmarks.get(i).setCategroyName(categoryName);
                            }
                        }
                    }
                    mBookmarks.get(i).setDescription(description);
                    String[] tags = {"font"};
                    mBookmarks.get(i).setContent(StringUtil.replaceTags(mBookmarks.get(i).getContent(), tags));
                }
                new HistoryDatabaseHelper(ATHIS).addBookmark(mBookmarks);
                showToastMsg("已加入到书签");
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
