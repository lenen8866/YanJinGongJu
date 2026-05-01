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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.util.DisplayUtil;
import com.read.scriptures.widget.QEditDialog.EditDialogClickListenerInterface;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class QSelectDialog extends Dialog implements OnItemClickListener {

    protected Activity activity;
    protected ListView select_dialog_list;
    protected TextView dialgTitle;
    protected String title;
    protected SelectActions actions;

    public SelectActions getActions() {
        return actions;
    }

    public void setActions(SelectActions actions) {
        this.actions = actions;
    }

    protected SelectDialogListAdapter adapter;
    protected List<SelectDialogShowItem> items = new ArrayList<SelectDialogShowItem>();
    protected boolean noAddItem = false;

    private int customHeight;
    private float fontSize;

    public boolean isNoAddItem() {
        return noAddItem;
    }

    public QSelectDialog setNoAddItem(boolean noAddItem) {
        this.noAddItem = noAddItem;
        return this;
    }

    public interface SelectActions {
        void callBack(SelectDialogShowItem select);

        boolean addItem(SelectDialogShowItem select);

        void removeItem(SelectDialogShowItem select);
    }

    public static abstract class SelectActionSon implements SelectActions {

        public boolean addItem(SelectDialogShowItem select) {
            return false;
        }

        public void removeItem(SelectDialogShowItem select) {
        }
    }

    public QSelectDialog(Activity activity, String title, List<SelectDialogShowItem> items, SelectActions callBack) {
        super(activity, R.style.custom_dialog);
        this.activity = activity;
        this.title = title;
        this.actions = callBack;
        this.items = items;
    }

    /**
     * 作为QFileSelectDialog子类调用
     */
    public QSelectDialog(Activity activity, String title, SelectActions callBack) {
        super(activity, R.style.custom_dialog);
        this.activity = activity;
        this.title = title;
        this.actions = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        inject();
    }

    public void viewClick(View view) {
        if (select_dialog_list != null) {
            select_dialog_list.setSelection(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void inject() {
        dialgTitle = (TextView) findViewById(R.id.select_dialog_title);
        dialgTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewClick(v);
            }
        });
        select_dialog_list = (ListView) findViewById(R.id.select_dialog_list);
        select_dialog_list.setOnItemClickListener(this);
        dialgTitle.setText(title);
        adapter = new SelectDialogListAdapter(activity, items, actions);
        adapter.setFontSize(fontSize);
        adapter.setNoAddItem(noAddItem);
        select_dialog_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.select_dialog, null);
        setContentView(view);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = activity.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.6
        if (customHeight == 0) {
            customHeight = (int) (d.heightPixels * 0.6);
        }
        lp.height = (int) (Math.min(d.heightPixels * 0.6, customHeight)); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    public void setCustomHeight(int size) {
        customHeight = (int) (size * DisplayUtil.dp2px(activity, 36) +
                //父控件padding
                DisplayUtil.dp2px(activity, 30)
                + DisplayUtil.dp2px(activity, 18) +
                DisplayUtil.sp2px(activity, 14)); // 高度设置为屏幕的0.6
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (noAddItem) {
            SelectDialogShowItem select = items.get(position);
            this.dismiss();
            actions.callBack(select);
            return;
        }
        if (position == 0) {
            this.hide();
            final QEditDialog dialog = new QEditDialog(activity, "");
            dialog.setEditDialogClickListener(new EditDialogClickListenerInterface() {
                @Override
                public void doConfirm() {
                    boolean flag = actions.addItem(new SelectDialogShowItem(dialog.getText()));
                    if (flag) {
                        String content = dialog.getText();
                        items.add(new SelectDialogShowItem(content));
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                        QSelectDialog.this.show();
                    } else {
                        // dialog.getEdit().setText("");
                    }
                }

                @Override
                public void doCancel() {
                    dialog.dismiss();
                    QSelectDialog.this.show();
                }
            });
            dialog.show();
        } else {
            SelectDialogShowItem select = items.get(position - 1);
            this.dismiss();
            actions.callBack(select);
        }
    }

    /**
     * 可以在非UI线程中调用 设置标题文字
     *
     * @param title
     */
    public void setTitle(final String title) {
        this.title = title;
        if (dialgTitle == null) {
            return;
        }
        dialgTitle.post(new Runnable() {
            @Override
            public void run() {
                dialgTitle.setVisibility(View.VISIBLE);
                dialgTitle.setText(title);
            }
        });
    }

    public void hideTitle() {
        if (dialgTitle == null) {
            return;
        }
        dialgTitle.post(new Runnable() {
            @Override
            public void run() {
                dialgTitle.setVisibility(View.GONE);
            }
        });
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }
}
