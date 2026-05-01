package com.read.scriptures.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.util.DisplayUtil;
import com.read.scriptures.util.SharedPreferencesUtils;

@SuppressLint("InflateParams")
public class CopyAskDialog extends Dialog {

    protected Activity activity;

    protected View llCopyDesc;
    protected TextView tvContent;
    protected CheckBox cbCopyDesc;
    protected TextView btnCancle;
    protected TextView btnCopy;

    protected String content;
    protected boolean mIsChecked;
    protected boolean isHasComent;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHasComent(boolean isHasComent) {
        this.isHasComent = isHasComent;
        if (llCopyDesc!=null) {
            if (isHasComent) {
                llCopyDesc.setVisibility(View.VISIBLE);
            } else {
                llCopyDesc.setVisibility(View.GONE);
            }
        }
    }

    private CopyAskBack callBack;
    public interface CopyAskBack {
        void callBack(boolean isCopyDes);
    }

    public CopyAskDialog(Activity activity, String content, boolean isHasComent, CopyAskBack callBack) {
        super(activity, R.style.custom_dialog);
        this.activity = activity;
        this.content = content;
        this.isHasComent = isHasComent;
        this.callBack = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        inject();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void inject() {
        llCopyDesc = (View) findViewById(R.id.llCopyDesc);
        tvContent = (TextView) findViewById(R.id.tvContent);
        cbCopyDesc = (CheckBox) findViewById(R.id.cbCopyDesc);
        btnCancle = (TextView) findViewById(R.id.btnCancle);
        btnCopy = (TextView) findViewById(R.id.btnCopy);

        if (isHasComent) {
            llCopyDesc.setVisibility(View.VISIBLE);
        } else {
            llCopyDesc.setVisibility(View.GONE);
        }

        mIsChecked = SharedPreferencesUtils.isCopyCheckBox(activity);
        cbCopyDesc.setChecked(mIsChecked);

        tvContent.setText(content);
        cbCopyDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsChecked = isChecked;
                SharedPreferencesUtils.saveCopyCheckBox(activity, isChecked);
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.callBack(isHasComent && mIsChecked);
            }
        });
    }

    private int customHeight;

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.dialog_copy_ask, null);
        setContentView(view);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = activity.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.6
//        if (customHeight == 0) {
//            customHeight = (int) (d.heightPixels * 0.6);
//        }
//        lp.height = (int) (Math.min(d.heightPixels * 0.6, customHeight)); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    public void setCustomHeight(int size) {
        customHeight = (int) (size * DisplayUtil.dp2px(activity, 36) +
                //父控件padding
                DisplayUtil.dp2px(activity, 30)
                + DisplayUtil.dp2px(activity, 18) +
                DisplayUtil.sp2px(activity, 14)); // 高度设置为屏幕的0.6
    }

}
