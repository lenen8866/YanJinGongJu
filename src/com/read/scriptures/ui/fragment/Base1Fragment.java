package com.read.scriptures.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.read.scriptures.widget.LoadingProgressDialog;


/**
 * Created by d on 2016/5/26.
 */
public abstract class Base1Fragment extends Fragment {

    private LoadingProgressDialog mLoadingDialog;
    public View mBaseView;
    public Activity mContext;
    protected boolean isVisible = false; //是否可见
    private boolean isPrepared = false;//是否初始化完成
    private boolean isFirst = true;//是否第一次加载


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mBaseView == null) {
            mBaseView = inflater.inflate(onObtainLayoutResId(), container, false);
            mContext = getActivity();
        }
        return mBaseView;
    }

    protected View findViewById(int var1) {
        return mBaseView == null ? null : mBaseView.findViewById(var1);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mBaseView == null) {
            return;
        }
        if (getUserVisibleHint()) {
            isVisible = true;
            _lazyLoad();
        } else {
            isVisible = false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        _lazyLoad();
    }

    /**
     * 懒加载
     */
    private void _lazyLoad() {
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        lazyLoad();
        isFirst = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }

    /**
     * 获取界面要加载的布局文件
     *
     * @return
     */
    public abstract int onObtainLayoutResId();

    /**
     * 子类的OnCreate()方法
     */
    public abstract void lazyLoad();

    public abstract void initWidget();

    /**
     * 显示正在加载的进度条
     */
    public void showProgressDialog() {
        showProgressDialog("加载中...");
    }

    /*     * 显示加载框
     *
     * @param msg
     */
    public void showProgressDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingProgressDialog(getActivity(), msg);
            mLoadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        try {
                            dismissProgressDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            });
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.setText(msg);
            mLoadingDialog.show();
        }
    }

    /**
     * 隐藏加载等待框
     */
    public void dismissProgressDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup viewGroup = ((ViewGroup) mBaseView.getParent());
        if (viewGroup != null) {
            viewGroup.removeView(mBaseView);
        }
    }

    public  <T extends View> T findViewById1(int var1) {
        return mBaseView == null ? null : mBaseView.findViewById(var1);
    }

    public void startActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
        isVisible = false;
        isPrepared = false;
        isFirst = true;
        mBaseView = null;
    }
}
