package com.read.scriptures.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.view.NullMenuEditText;

/**
 * 通用自定义样式AlertDialog
 *
 * @author Xun.Zhang
 * @ClassName: CommonAlertDialog.java
 * @date 2014-11-11 上午9:38:40
 */
public class CustomAlertDialog {

    private final AlertDialog mAlertDialog;

    private final TextView mTxtViewTitle;

    private final NullMenuEditText mTxtViewMessage;

    private final Button mBtnPositive;

    private final Button mBtnNegative;

    private final View mViewLine;
    private TextView outUpdateLabel;

    public CustomAlertDialog(final Context context, final String outUrl) {
        mAlertDialog = new AlertDialog.Builder(context, R.style.DialogStyle).create();
        mAlertDialog.show();
        final Window window = mAlertDialog.getWindow();
        window.setContentView(R.layout.dialog_custom);
        outUpdateLabel = window.findViewById(R.id.outUpdateLabel);
        mTxtViewTitle = (TextView) window.findViewById(R.id.txt_dialog_title);
        mTxtViewMessage = (NullMenuEditText) window.findViewById(R.id.txt_dialog_message);
        mBtnPositive = (Button) window.findViewById(R.id.btn_dialog_positive);
        mBtnNegative = (Button) window.findViewById(R.id.btn_dialog_negative);
        mViewLine = window.findViewById(R.id.view_line);
        mAlertDialog.setCanceledOnTouchOutside(false);
        if ("-1".equals(outUrl)||TextUtils.isEmpty(outUrl)) {
            outUpdateLabel.setVisibility(View.GONE);
        }
        outUpdateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(outUrl)) {
                    return;
                }
                final Uri uri = Uri.parse(outUrl);
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(it);
            }
        });


    }


    public void setTitle(final String strTitle) {
        mTxtViewTitle.setText(strTitle);
    }


    public void setCancelable(boolean bol) {
        mAlertDialog.setCancelable(bol);
    }

    public void setMessage(final String strMessage) {
        mTxtViewMessage.setText(strMessage);
    }

    /**
     * 设置按钮（确定）
     *
     * @param resId
     * @param onClickListener
     */
    public void setPositiveButton(final int resId, final View.OnClickListener onClickListener) {
        mBtnPositive.setText(resId);
        mBtnPositive.setOnClickListener(onClickListener);
    }

    /**
     * 设置按钮（确定）
     *
     * @param resStr
     * @param onClickListener
     */
    public void setPositiveButton(final String resStr, final View.OnClickListener onClickListener) {
        mBtnPositive.setText(resStr);
        mBtnPositive.setOnClickListener(onClickListener);
    }

    /**
     * 设置按钮（否定）
     *
     * @param resStr
     * @param onClickListener
     */
    public void setNegativeButton(final String resStr, final View.OnClickListener onClickListener) {
        if (StringUtil.isEmpty(resStr)) {
            mBtnNegative.setVisibility(View.GONE);
            mViewLine.setVisibility(View.GONE);
        } else {
            mBtnNegative.setVisibility(View.VISIBLE);
            mViewLine.setVisibility(View.VISIBLE);
        }
        mBtnNegative.setText(resStr);
        mBtnNegative.setOnClickListener(onClickListener);
    }


    /**
     * 设置按钮（否定）
     *
     * @param onClickListener
     */
    public void setNegativeButton(final int resId, final View.OnClickListener onClickListener) {
        mBtnNegative.setText(resId);
        mBtnNegative.setOnClickListener(onClickListener);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        if (null != mAlertDialog) {
            mAlertDialog.dismiss();
        }
    }

    /**
     * 显示对话框
     */
    public void show() {
        if ((null != mAlertDialog) && !mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    public boolean isShowing() {
        if ((null != mAlertDialog)) {
            return mAlertDialog.isShowing();
        }
        return false;
    }
}
