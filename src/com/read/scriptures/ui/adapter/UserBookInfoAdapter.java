package com.read.scriptures.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.ui.fragment.UserBookMarkFragment;
import com.read.scriptures.ui.fragment.UserCollectFragment;
import com.read.scriptures.ui.fragment.UserReaderHistoryFragment;

import io.reactivex.annotations.Nullable;

/**
 * Time: 2020/9/15
 * Author: a123
 * Description:
 */
public class UserBookInfoAdapter extends FragmentPagerAdapter {
    private UserReaderHistoryFragment historyFragment;
    private UserCollectFragment userCollectFragment;
    private UserBookMarkFragment userBookMarkFragment;

    public UserBookInfoAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (historyFragment == null) {
                    historyFragment = new UserReaderHistoryFragment();
                }
                return historyFragment;
            case 1:
                if (userCollectFragment == null) {
                    userCollectFragment = new UserCollectFragment();
                }
                return userCollectFragment;
            default:
                if (userBookMarkFragment == null){
                    userBookMarkFragment = new UserBookMarkFragment();
                }
                return userBookMarkFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return HuDongApplication.getInstance().getResources().getString(R.string.user_history);
            case 1:
                return HuDongApplication.getInstance().getResources().getString(R.string.user_collect);
            default:
                return HuDongApplication.getInstance().getResources().getString(R.string.user_remark);
        }
    }
}
