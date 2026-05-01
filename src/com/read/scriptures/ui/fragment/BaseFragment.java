package com.read.scriptures.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.music.player.lib.service.MusicPlayerService;
import com.music.player.lib.util.XToast;
import com.read.scriptures.EIUtils.EIFragment;
import com.read.scriptures.util.analytics.AnalyticsUtil;
import com.read.scriptures.widget.LoadingProgressDialog;


public abstract class BaseFragment extends EIFragment {
    protected boolean isVisible;
    /**
     * 调试TAG
     */
    private static String TAG = BaseFragment.class.getSimpleName();

    /**
     * 加载等待框
     */
    private LoadingProgressDialog mLoadingDialog;
    /**
     * Fragment动作监听
     */
    protected OnFragmentHandleListener mOnFragmentHandleListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
        if (activity instanceof OnFragmentHandleListener) {
            mOnFragmentHandleListener = (OnFragmentHandleListener) activity;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtil.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        AnalyticsUtil.onPageEnd(TAG);
    }

    /*     * 显示加载框
     *
     * @param msg
     */
    public void showProgressDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingProgressDialog(getActivity(), msg);
            mLoadingDialog.setOnKeyListener(new OnKeyListener() {
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

    public OnFragmentHandleListener getOnFragmentHandleListener() {
        return mOnFragmentHandleListener;
    }

    public void setOnFragmentHandleListener(OnFragmentHandleListener onFragmentHandleListener) {
        this.mOnFragmentHandleListener = onFragmentHandleListener;
    }

    public interface OnFragmentHandleListener {

        void onSendMessage(String msg);

        void onFragmentChange(int oldPosition, int newPosition);
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {//当可见的时候执行操作
            isVisible = true;
            onVisible();
        } else {//不可见时执行相应的操作
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected abstract void lazyLoad();//子类实现

    protected void onInvisible() {
    }

    protected void showLog(Object obj) {
        Log.d("[lylog]", obj.toString());
    }


    public <T extends View> T findViewById1(int var1) {
        return getRootView() == null ? null : getRootView().findViewById(var1);
    }

    protected void showToast(String str) {
        if (getActivity() != null && !TextUtils.isEmpty(str)) {
            XToast.showToast(getActivity(),str);
        }
    }
}
