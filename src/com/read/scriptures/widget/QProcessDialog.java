package com.read.scriptures.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.util.DownloadUtils;

@SuppressLint("InflateParams")
public class QProcessDialog extends Dialog {
    interface ProcessAction {
        int doAction();
    }

    private ProcessAction action;

    private Activity activity;

    private Runnable dismissAction;

    private ProgressBar progressBar;
    private long sleep = 1l;
    private String title = "";
    private String hint = "";
    private boolean openHint = false;

    private TextView tvShow;

    private TextView tvTitle;

    private TextView tvHint;
    /** 是否想要关闭 */
    private boolean wantToClose = false;
    private Button btnInstallApk;

    /**
     * 演示用
     * 
     * @param activity
     */
    @Deprecated
    public QProcessDialog(Activity activity) {
        super(activity, R.style.custom_dialog);
        this.activity = activity;
        this.sleep = 300;
        this.action = new ProcessAction() {
            @Override
            public int doAction() {
                progressBar.setSecondaryProgress(progressBar.getSecondaryProgress() + 1);
                return progressBar.getProgress() + 5;
            }
        };
    }

    public QProcessDialog(Activity activity, ProcessAction action, long sleep, String title) {
        super(activity, R.style.custom_dialog);
        this.activity = activity;
        this.action = action;
        this.sleep = sleep;
        this.title = title;
    }

    public QProcessDialog(Activity activity, ProcessAction action, String title) {
        super(activity, R.style.custom_dialog);
        this.activity = activity;
        this.action = action;
        this.title = title;
    }

    public QProcessDialog(Activity activity, String title) {
        super(activity, R.style.custom_dialog);
        this.activity = activity;
        this.title = title;
    }

    public QProcessDialog(Activity activity, String title,Boolean openHint,String hint) {
        super(activity, R.style.custom_dialog);
        this.activity = activity;
        this.title = title;
        this.openHint = openHint;
        this.hint = hint;
    }
    @Override
    public void dismiss() {
        super.dismiss();
        tvTitle.setKeepScreenOn(false);
    }

    public Runnable getDismissAction() {
        return dismissAction;
    }

    public int getProgress() {
        if (progressBar == null) {
            return 0;
        }
        return progressBar.getProgress();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public long getSleep() {
        return sleep;
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.process_dialog, null);
        setContentView(view);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        btnInstallApk = (Button) view.findViewById(R.id.btn_apk_install);
        tvTitle = (TextView) view.findViewById(R.id.process_dialog_title);
        tvTitle.setText(title);
        // 背光常亮
        tvTitle.setKeepScreenOn(true);
        //设置提示
        tvHint = (TextView) view.findViewById(R.id.process_hint);
        if (openHint)
            tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(hint);
        // activity.setScreenOn(tvTitle, true);
        tvShow = (TextView) view.findViewById(R.id.process_dialog_show);
        progressBar = (ProgressBar) findViewById(R.id.process_dialog_process);
        btnInstallApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //安装
                DownloadUtils.installApk(activity,QProcessDialog.this);
            }
        });
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = activity.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.6
        lp.height = (int) (d.heightPixels * 0.2); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);

        if (action == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int progress = action.doAction();
                    progressBar.setProgress(progress);
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (progress >= 100) {
                        break;
                    }
                }
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        QProcessDialog.this.dismiss();
//                    }
//                });
            }
        }).start();


    }

    public boolean isWantToClose() {
        return wantToClose;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wantToClose) {
                dismiss();
                if (dismissAction != null) {
                    dismissAction.run();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setDismissAction(Runnable dismissAction) {
        this.dismissAction = dismissAction;
    }

    public void setMaxProgress(int max) {
        if (progressBar == null) {
            return;
        }
        progressBar.setMax(max);

    }

    /**
     * 可以在非UI线程中调用
     * 
     * @param progress
     */
    public void setProgress(int progress) {
        if (progressBar == null) {
            return;
        }
        progressBar.setProgress(progress);
        if (progress >= progressBar.getMax()) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    QProcessDialog.this.dismiss();
//                }
//            });
        }
    }

    /**
     * 可以在非UI线程中调用
     * 
     * @param progress
     */
    public void setSecondaryProgress(int progress) {
        if (progressBar == null) {
            return;
        }
        progressBar.setSecondaryProgress(progress);
    }

    /**
     * 可以在非UI线程中调用 <br/>
     * 设置进度条上显示文字
     * 
     * @param show
     */
    public void setShow(final String show) {
        if (tvShow == null) {
            return;
        }
        tvShow.post(new Runnable() {
            @Override
            public void run() {
                tvShow.setTextColor(Color.BLACK);
                tvShow.setText(show);
            }
        });
    }

    public void setSleep(long sleep) {
        this.sleep = sleep;
    }

    /**
     * 可以在非UI线程中调用 设置标题文字
     * 
     * @param title
     */
    public void setTitle(final String title) {
        if (tvTitle == null) {
            return;
        }
        tvTitle.post(new Runnable() {
            @Override
            public void run() {
                tvTitle.setText(title);
            }
        });
    }

    public void setWantToClose(boolean wantToClose) {
        this.wantToClose = wantToClose;
    }

    /**
     * 可以在非UI线程中调用 <br/>
     * 设置进度条上显示文字
     * 
     * @param show
     */
    public void setWarnShow(final String show, final Thread callback) {
        if (tvShow == null) {
            return;
        }
        tvShow.post(new Runnable() {
            @Override
            public void run() {
                tvShow.setTextColor(Color.RED);
                tvShow.setText(show);
                callback.start();
            }
        });
    }

    public void setBtnInstallApk(boolean isShow) {
       if (isShow){
           btnInstallApk.setVisibility(View.VISIBLE);
       }
    }
}
