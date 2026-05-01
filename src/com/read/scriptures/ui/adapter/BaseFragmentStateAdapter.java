//package com.read.scriptures.ui.adapter;
//
//
//
//import android.support.annotation.NonNull;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//
//import com.lgmshare.eiframe.ui.adapter.FragmentViewPagerAdapter;
//
//import java.util.ArrayList;
//
//public class BaseFragmentStateAdapter extends FragmentViewPagerAdapter {
//
//    private ArrayList<Fragment> mFragments = new ArrayList<>();
//
//    public BaseFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
//        super(fragmentActivity);
//    }
//
//    public BaseFragmentStateAdapter(@NonNull Fragment fragment) {
//        super(fragment);
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return mFragments.get(position);
//    }
//
//    public void addFragment(Fragment fragment) {
//        mFragments.add(fragment);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mFragments.size();
//    }
//}
