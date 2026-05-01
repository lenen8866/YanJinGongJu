package com.read.scriptures.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.read.scriptures.R;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.StatusBarUtils;

//百科
public class BaiKe1Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_baike);
        StatusBarUtils.initMainColorStatusBar(this);
        findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
