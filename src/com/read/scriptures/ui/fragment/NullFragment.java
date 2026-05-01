package com.read.scriptures.ui.fragment;

import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.R;
import com.read.scriptures.bean.AssetsStatusBean;

public class NullFragment extends BaseFragment {
    @Override
    protected void lazyLoad() {
        initAssetsStatus();
    }

    private boolean assetsStatusShow = true;

    private void initAssetsStatus() {
        NetUtil.get("https://book.sdacn.cn/api/v1.systems/ver", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                AssetsStatusBean assetsStatusBean = new Gson().fromJson(t, AssetsStatusBean.class);
                if (TextUtils.isEmpty(assetsStatusBean.status)) {
                    assetsStatusShow = true;
                } else if (TextUtils.equals("hidden", assetsStatusBean.status)) {
                    assetsStatusShow = false;
                } else {
                    assetsStatusShow = true;
                }
                ll_maintenance.setVisibility(assetsStatusShow ? View.GONE : View.VISIBLE);
            }
        });
    }

    View ll_maintenance;

    @Override
    protected void initWidget() {
        ll_maintenance = findViewById1(R.id.ll_maintenance);
    }

    @Override
    protected int onObtainLayoutResId() {
        return R.layout.ft_null;
    }
}
