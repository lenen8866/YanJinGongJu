package com.read.scriptures.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.util.StringUtil;

public class QEditDialog extends Dialog {

    TextView save;
    TextView back;
    EditText edit;
    private EditDialogClickListenerInterface clickListenerInterface;
    private Activity activity;

    public interface EditDialogClickListenerInterface {

        public void doConfirm();

        public void doCancel();
    }

    public void setEditDialogClickListener(EditDialogClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    public EditText getEdit() {
        return edit;
    }

    private String old;
    private String save_text;
    private String back_text;

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            btnClick(v);
        }
    };

    public QEditDialog(Activity activity, String old) {
        super(activity, R.style.dialog);
        this.old = old;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_layout);
        save = (TextView) findViewById(R.id.save_btn);
        back = (TextView) findViewById(R.id.back_btn);
        save.setOnClickListener(clickListener);
        back.setOnClickListener(clickListener);
        edit = (EditText) findViewById(R.id.dialog_edit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        edit.setText(old);
        edit.setFocusable(true);
        edit.requestFocus();
        if (!StringUtil.isEmpty(back_text)) {
            back.setText(back_text);
        }
        if (!StringUtil.isEmpty(save_text)) {
            save.setText(save_text);
        }
        openInput();
    }

    /** 软键盘自动弹出或关闭 */
    private void openInput() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void setButtonText(int resId, String text) {
        if (resId == R.id.save_btn) {
            save_text = text;
        } else if (resId == R.id.back_btn) {
            back_text = text;
        }
    }

    public void btnClick(View view) {
        int id = view.getId();
        switch (id) {
        case R.id.save_btn:
            clickListenerInterface.doConfirm();
            break;
        case R.id.back_btn:
            clickListenerInterface.doCancel();
            break;
        }
    }

    public void focus() {
        if (edit != null) {
            edit.requestFocus();
            // edit.callOnClick();
        }
    }

    public String getText() {
        if (edit != null) {
            return edit.getText().toString();
        }
        return "";
    }

}
