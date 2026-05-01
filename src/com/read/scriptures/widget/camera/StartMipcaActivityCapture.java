package com.read.scriptures.widget.camera;

import android.app.Activity;
import android.os.Bundle;

import com.google.zxing.Result;
import com.music.player.lib.util.XToast;
import com.read.scriptures.ui.activity.SearchActivity;
import com.read.scriptures.EIUtils.ActivityUtil;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author s00223601
 * @version [版本号, 2015-10-8]
 * @since [产品/模块版本]
 */
public class StartMipcaActivityCapture extends MipcaActivityCapture {


    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnCreate(final Bundle savedInstanceState) {
        setTitleStr("扫描");
        super.doOnCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void childhandleDecode(final Result result) {
        final String resultString = result.getText();
        Bundle bd = new Bundle();
        bd.putString("keyword", resultString);
        ActivityUtil.next(ATHIS, SearchActivity.class, bd, -1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void childTimeOutOpera(final Activity activity) {
        XToast.showToast(activity, "扫描超时");
    }
}
