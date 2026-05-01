package com.read.scriptures.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.event.LoginOutEvent;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.UserBookInfoAdapter;
import com.read.scriptures.ui.fragment.UserBookMarkFragment;
import com.read.scriptures.ui.fragment.UserCollectFragment;
import com.read.scriptures.ui.fragment.UserReaderHistoryFragment;
import com.read.scriptures.util.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Time: 2020/9/14
 * Author: a123
 * Description: 用户
 */
public class UserBookInfoActivity extends BaseActivity {

    public static final int OPERATION_TYPE_OPEN_OPERATION = 1;//打开操作
    public static final int OPERATION_TYPE_CHECKED_ALL = 2;//全选
    public static final int OPERATION_TYPE_CANCEL_CHECKED_ALL = 3;//取消全选
    public static final int OPERATION_TYPE_CLOSE_OPERATION = 4;//关闭操作


    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.vp_content)
    ViewPager vpContent;

    @BindView(R.id.tv_history_count)
    TextView tvHistoryCount;
    @BindView(R.id.tv_collect_count)
    TextView tvCollectCount;
    @BindView(R.id.tv_mark_count)
    TextView tvMarkCount;
    @BindView(R.id.fl_history_count)
    FrameLayout flHistoryCount;
    @BindView(R.id.fl_collect_count)
    FrameLayout flCollectCount;
    @BindView(R.id.fl_mark_count)
    FrameLayout flMarkCount;

    UserBookInfoAdapter mUserBookInfoAdapter;
    private int autoIndex = -1;
//    private boolean isNeedAutoSearchMarkKeyword = false;
    private boolean isBack;
    private String keyWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_info);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        StatusBarUtils.initMainColorStatusBar(this);
        initExtras(getIntent());
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initExtras(intent);
        initData();
    }

    private void initExtras(Intent intent) {
        if (getIntent().getExtras() != null) {
            autoIndex = getIntent().getExtras().getInt("index");
            isBack = getIntent().getExtras().getBoolean("isBack");
            keyWords = getIntent().getExtras().getString("keyWords");
//            isNeedAutoSearchMarkKeyword = true;
        }else if (intent.getExtras() != null){
            autoIndex = intent.getExtras().getInt("index");
            isBack = intent.getExtras().getBoolean("isBack");
            keyWords = intent.getExtras().getString("keyWords");
//            isNeedAutoSearchMarkKeyword = true;
        }
    }

    private void initData() {
        if (autoIndex >= 0 && autoIndex < tabLayout.getTabCount()) {
            tabLayout.getTabAt(autoIndex).select();
            //需要设置关键字

        }
        vpContent.setOffscreenPageLimit(3);
    }

    private void initView() {
        tvRight.setVisibility(View.VISIBLE);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.user_history));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.user_collect));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.user_remark));
        tabLayout.setupWithViewPager(vpContent);
        mUserBookInfoAdapter = new UserBookInfoAdapter(getSupportFragmentManager());
        vpContent.setAdapter(mUserBookInfoAdapter);
    }

    private void initListener() {
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvRight.getText().toString().equals("操作")) {
                    tvRight.setText("取消");
                    int operation = OPERATION_TYPE_OPEN_OPERATION;
                    dealWithFragment(operation);
                } else {
                    //退出操作模式
                    exitOperation();
                }

            }
        });
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //切换了fragment
                exitOperation(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void exitOperation() {
        int selectTabPostion = tabLayout.getSelectedTabPosition();
        exitOperation(selectTabPostion);
    }

    private void dealWithFragment(int operation) {
        int selectTabPostion = tabLayout.getSelectedTabPosition();
        Fragment fragment = mUserBookInfoAdapter.getItem(selectTabPostion);
        if (fragment instanceof UserReaderHistoryFragment) {
            UserReaderHistoryFragment historyFragment = (UserReaderHistoryFragment) fragment;
            historyFragment.operation(operation);
        } else if (fragment instanceof UserCollectFragment) {
            UserCollectFragment collectFragment = (UserCollectFragment) fragment;
            collectFragment.operation(operation);
        } else if (fragment instanceof UserBookMarkFragment) {
            UserBookMarkFragment bookMarkFragment = (UserBookMarkFragment) fragment;
            bookMarkFragment.operation(operation);
        }

    }

    private void exitOperation(int postion) {
        tvRight.setText("操作");
        Fragment fragment = mUserBookInfoAdapter.getItem(postion);
        if (fragment instanceof UserReaderHistoryFragment) {
            UserReaderHistoryFragment historyFragment = (UserReaderHistoryFragment) fragment;
            historyFragment.operation(OPERATION_TYPE_CLOSE_OPERATION);
        } else if (fragment instanceof UserCollectFragment) {
            UserCollectFragment collectFragment = (UserCollectFragment) fragment;
            collectFragment.operation(OPERATION_TYPE_CLOSE_OPERATION);
        } else if (fragment instanceof UserBookMarkFragment) {
            UserBookMarkFragment markFragment = (UserBookMarkFragment) fragment;
            markFragment.operation(OPERATION_TYPE_CLOSE_OPERATION);
        }
    }


    @Override
    public void onBackPressed() {
        if (!tvRight.getText().toString().equals("操作")) {
            //处于操作模式中,退出操作模式
            exitOperation();
            return;
        }
        super.onBackPressed();
    }

    public int getAutoIndex() {
        return autoIndex;
    }

    public boolean isBack() {
        return isBack;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setHistoryCount(int count){
        if (count == 0){
            flHistoryCount.setVisibility(View.INVISIBLE);
        }else{
            flHistoryCount.setVisibility(View.VISIBLE);
            tvHistoryCount.setText(count < 100 ? count+"" : "99+");
        }
    }

    public void setCollectCount(int count){
        if (count == 0){
            flCollectCount.setVisibility(View.INVISIBLE);
        }else{
            flCollectCount.setVisibility(View.VISIBLE);
            tvCollectCount.setText(count < 100 ? count+"" : "99+");
        }
    }

    public void setMarkCount(int count){
        if (count == 0){
            flMarkCount.setVisibility(View.INVISIBLE);
        }else{
            flMarkCount.setVisibility(View.VISIBLE);
            tvMarkCount.setText(count < 100 ? count+"" : "99+");
        }
    }

    public boolean isNeedAutoSearchMarkKeyword() {
        if (autoIndex == 2 && isBack && !TextUtils.isEmpty(keyWords)){
            return true;
        }
        return  false;
//        return isNeedAutoSearchMarkKeyword;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(LoginOutEvent loginOutEvent) {
        //登录失效
        this.finish();
    }
}
