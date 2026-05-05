package com.read.scriptures.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.EIUtils.ActivityUtil;

public class HelpActivity extends BaseActivity implements OnClickListener {
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initViews();
    }



    private void initViews() {
        title = (TextView) findViewById(R.id.tv_title);
        title.setText("关于我们");
        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_clientVersion = (TextView) findViewById(R.id.tv_clientVersion);
        tv_clientVersion.setText("版本号：" + HuDongApplication.getInstance().getVersionName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                ActivityUtil.back(HelpActivity.this);
                break;
            default:
                break;
        }
    }
}
