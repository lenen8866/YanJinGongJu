//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.read.scriptures.EIUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public abstract class EIFragment extends Fragment {
    private InputMethodManager mInputMethodManager;
    private View mRootView;

    public EIFragment() {
    }

    protected View findViewById(int var1) {
        return this.mRootView == null ? null : this.mRootView.findViewById(var1);
    }

    protected View getRootView() {
        return this.mRootView;
    }


    protected abstract void initWidget();

    @SuppressLint("WrongConstant")
    public void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.mInputMethodManager = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
        int var4 = this.onObtainLayoutResId();
        if (var4 > 0) {
            mRootView = var1.inflate(var4, var2, false);
            return mRootView;
        } else {
            return super.onCreateView(var1, var2, var3);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWidget();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    protected abstract int onObtainLayoutResId();

    @TargetApi(3)
    protected void toggleSoftInput() {
        if (VERSION.SDK_INT < 3) {
            this.mInputMethodManager.toggleSoftInput(0, 2);
        }

    }
}
