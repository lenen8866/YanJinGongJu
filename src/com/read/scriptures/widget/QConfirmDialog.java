package com.read.scriptures.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.util.DisplayUtil;
import com.read.scriptures.util.LogUtil;

public class QConfirmDialog extends Dialog {

    private final Context context;

    private final String title;

    private String show;

    private final String confirmButtonText;

    private final String cacelButtonText;

    private ClickListenerInterface clickListenerInterface;

    private TextView tvTitle;

    private TextView tvShow;

    private LinearLayout confirm_button_box;

    private TextView tvConfirm;

    private TextView tvCancel;

    private double scale;

    public double getScale() {
        return scale;
    }

    public QConfirmDialog setScale(final double scale) {
        this.scale = scale;
        return this;
    }

    public interface ClickListenerInterface {

        public void doConfirm();

        public void doCancel();
    }

    public QConfirmDialog(final Context context, final String title, final String confirmButtonText,
            final String cacelButtonText) {
        super(context, R.style.custom_dialog);
        this.context = context;
        this.title = title;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;
    }

    public QConfirmDialog setShow(final String show) {
        this.show = show;
        return this;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        this.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.confirm_dialog, null);
        setContentView(view);
        tvTitle = (TextView) findViewById(R.id.title);
        tvShow = (TextView) findViewById(R.id.show);
        confirm_button_box = (LinearLayout) findViewById(R.id.confirm_button_box);
        tvConfirm = (TextView) findViewById(R.id.confirm);
        tvCancel = (TextView) findViewById(R.id.cancel);

        tvTitle.setText(title);
        tvConfirm.setText(confirmButtonText);
        tvCancel.setText(cacelButtonText);

        tvConfirm.setOnClickListener(new clickListener());
        tvCancel.setOnClickListener(new clickListener());

        double temp = 0.2;
        if (scale != 0) {
            temp = scale;
        }
        final Window dialogWindow = getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        final DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.6
        lp.height = (int) (d.heightPixels * temp); // 高度设置为屏幕的0.6

        final LinearLayout.LayoutParams cLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if ((show != null) && !"".equals(show)) {
            tvShow.setVisibility(View.VISIBLE);
            tvShow.setText(show);
            final android.view.ViewGroup.LayoutParams layoutParams = tvShow.getLayoutParams();
            layoutParams.height = //
                    // height;
                    (int) ((5 * d.heightPixels * temp) / 12);
            tvShow.setLayoutParams(layoutParams);
            tvShow.setMovementMethod(new QScrollingMovementMethod());
            cLayoutParams.setMargins(10, 10, 10, 10);
            confirm_button_box.setLayoutParams(cLayoutParams);
        } else {
            cLayoutParams.setMargins(10, (int) ((d.heightPixels * temp) / 4), 10, 10);
            confirm_button_box.setLayoutParams(cLayoutParams);
        }
        int height = DisplayUtil.getViewHeight(view);
        LogUtil.test("height:" + height);
        lp.height = height;
        if (lp.height < DisplayUtil.dp2px(context, 180)) {
            lp.height = (int) DisplayUtil.dp2px(context, 180);
        }
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(final ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            final int id = v.getId();
            switch (id) {
            case R.id.confirm:
                clickListenerInterface.doConfirm();
                break;
            case R.id.cancel:
                clickListenerInterface.doCancel();
                break;
            }
        }
    };

}