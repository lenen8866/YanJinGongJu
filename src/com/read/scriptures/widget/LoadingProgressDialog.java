package com.read.scriptures.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.read.scriptures.R;

/**
 * @ClassName: LoadingProgressDialog
 * @Description: 加载等待框
 * @author lim
 * @mail lgmshare@gmail.com
 * @date 2014-6-3  上午11:03:42
 */
public class LoadingProgressDialog extends ProgressDialog {

	private TextView content;
	private String message;

	public LoadingProgressDialog(Context context) {
		super(context, R.style.LoadingProgressDialog);
	}

	public LoadingProgressDialog(Context context, String message) {
		super(context, R.style.LoadingProgressDialog);
		this.message = message;
	}

	public LoadingProgressDialog(Context context, int theme, String message) {
		super(context, theme);
		this.message = message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		setCanceledOnTouchOutside(false);
		setInverseBackgroundForced(false);
		content = (TextView) findViewById(R.id.tips_msg);
		setText(message);
	}

	public void setText(String message) {
		if (content == null) {
			this.message = message;
			return;
		}
		
		if (TextUtils.isEmpty(message)) {
			content.setVisibility(View.GONE);
		}else{
			content.setVisibility(View.VISIBLE);
			content.setText(message);
		}
	}

	public void setText(int resId) {
		setText(getContext().getResources().getString(resId));
	}
}