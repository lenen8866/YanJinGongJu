package com.read.scriptures.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.constants.BundleConstants;
import com.read.scriptures.db.VolumeDatabaseHepler;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.Volume;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.VolumeListAdapter;
import com.read.scriptures.EIUtils.ActivityUtil;

import java.util.List;

public class VolumeListActivity extends BaseActivity {

    private ListView mListView;
    private VolumeListAdapter mAdapter;
    private TextView title;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);
        initExtras();
        initViews();
    }

    private void initExtras() {
        mCategory = (Category) getIntent().getParcelableExtra(BundleConstants.PARAM_CATEGORY);
    }

    private void initViews() {
        title = (TextView) findViewById(R.id.tv_title);
        title.setText(mCategory.getCateName());
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        List<Volume> list = new VolumeDatabaseHepler(this).getVolumes(mCategory.getId());
        mAdapter = new VolumeListAdapter(this, list);
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                clickListViewItem(arg2);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    // 列表点击后的响应
    public void clickListViewItem(int index) {
        Bundle bd = new Bundle();
        bd.putParcelable(BundleConstants.PARAM_VOLUME, mAdapter.getItem(index));
        ActivityUtil.next(ATHIS, ChaptersListActivity.class, bd, -1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
