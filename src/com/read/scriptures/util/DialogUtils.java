package com.read.scriptures.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.read.scriptures.R;

import java.util.logging.Handler;


/**
 * Created by thbpc on 2018/3/22 0022.
 */

public class DialogUtils {

    public static void showBottomDialog(Activity context, @LayoutRes int res, InitViewsListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.MusicButtomAnimationStyle);
        View view = View.inflate(context, res, null);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!context.isFinishing()) {
            dialog.show();
        }
        listener.setAction(dialog, view);
    }

    public interface InitViewsListener {
        void setAction(Dialog dialog, View view);
    }

    private static Dialog baseDialog(Activity context, int resource, int anim, boolean Cancelable, boolean outside, int gravity, int width, int height, InitViewsListener listener) {
        Dialog baseDialog = new Dialog(context, R.style.dialog1);
        baseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(resource, null);
        baseDialog.setContentView(view);
        if (listener != null) {
            listener.setAction(baseDialog, view);
        }
        baseDialog.setCancelable(Cancelable);//true
        baseDialog.setCanceledOnTouchOutside(outside);//false
        Window window = baseDialog.getWindow();
        window.setWindowAnimations(anim);//R.style.dialog_animation
        window.setGravity(gravity);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        lp.height = height;
        window.setAttributes(lp);
        if (!context.isFinishing()) {
            baseDialog.show();
        }
        return baseDialog;
    }

    public static Dialog showCenterDialog(Activity context, int resource, int width, int height, InitViewsListener listener) {
        return baseDialog(context, resource, 0, true, true, Gravity.CENTER, width, height, listener);
    }


    public static Dialog showBottomDialog(Activity context, int resource, int width, int height, InitViewsListener listener) {
        Dialog dialog = baseDialog(context, resource, R.style.DialogBottomInAnim, true, true, Gravity.BOTTOM, width, height, listener);
        return dialog;
    }


    /**
     * 不带头 确定返回提示框
     *
     * @param context
     * @param listener
     * @return
     */
    public static AlertDialog showDialog(@NonNull Context context, final InitViewsListener listener) {
        return baseDialog(context, true, listener);
    }

    /**
     * @param context
     * @param cancelable
     * @param listener
     * @return
     */
    private static AlertDialog baseDialog(@NonNull Context context, boolean cancelable, final InitViewsListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.dialog).create();
        View view = View.inflate(context, R.layout.notitle_dialog, null);
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        lp.width = DensityUtil.dip2px(context, 259);
        dialogWindow.setAttributes(lp);
        Activity activity = (Activity) context;
        if (!activity.isFinishing()) {
            dialog.show();
            dialog.getWindow().setContentView(view);
            if (listener != null) {
                listener.setAction(dialog, view);
            }
        }
        return dialog;
    }

    /**
     * @param context
     * @param listener
     * @return
     */
    public static AlertDialog showNormalDialog(Context context, String title, String content, String cancel, String ok, final onDialogClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.dialog).create();
        View view = View.inflate(context, R.layout.notitle_dialog, null);
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        lp.width = DensityUtil.dip2px(context, 259);
        dialogWindow.setAttributes(lp);
        Activity activity = (Activity) context;
        if (!activity.isFinishing()) {
            dialog.show();
            dialog.getWindow().setContentView(view);
        }
        TextView dialog_tv_title = view.findViewById(R.id.dialog_tv_title);
        TextView tvContent = view.findViewById(R.id.dialog_tv_content);
        TextView tvCancel = view.findViewById(R.id.dialog_tv_cancel);
        TextView tvOk = view.findViewById(R.id.dialog_tv_ok);
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(cancel)) {
            tvCancel.setText(cancel);
        }
        if (!TextUtils.isEmpty(ok)) {
            tvOk.setText(ok);
        }
        if (!TextUtils.isEmpty(title)) {
            dialog_tv_title.setText(title);
        }
        if (listener != null) {
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOk(dialog);
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(dialog);
                }
            });
        }
        return dialog;
    }


    /**
     * 有倒计时
     *
     * @param context
     * @param listener
     * @return
     */
    public static AlertDialog showFreeEndDialog(Context context, String title, String content, String cancel, String ok, final onDialogClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.dialog).create();
        View view = View.inflate(context, R.layout.notitle_dialog, null);
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        lp.width = DensityUtil.dip2px(context, 259);
        dialogWindow.setAttributes(lp);
        Activity activity = (Activity) context;
        if (!activity.isFinishing()) {
            dialog.show();
            dialog.getWindow().setContentView(view);
        }
        TextView dialog_tv_title = view.findViewById(R.id.dialog_tv_title);
        TextView tvContent = view.findViewById(R.id.dialog_tv_content);
        TextView tvCancel = view.findViewById(R.id.dialog_tv_cancel);
        TextView tvOk = view.findViewById(R.id.dialog_tv_ok);
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(cancel)) {
            tvCancel.setText(cancel);
        }
        if (!TextUtils.isEmpty(ok)) {
            tvOk.setText(ok);
        }
        if (!TextUtils.isEmpty(title)) {
            dialog_tv_title.setText(title);
        }
        if (listener != null) {
            tvCancel.setEnabled(false);
            CountDownTimer timer = new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvCancel.setText(cancel + (millisUntilFinished / 1000) + "s");
                }

                public void onFinish() {
                    tvCancel.setText(cancel);
                    tvCancel.setEnabled(true);
                }
            };
            timer.start();
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOk(dialog);
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(dialog);
                }
            });
        }
        return dialog;
    }

    /**
     * 有倒计时
     *
     * @param context
     * @param listener
     * @return
     */
    public static AlertDialog showNoticeDialog(Context context, String title, String content, String cancel, final onDialogClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.dialog).create();
        View view = View.inflate(context, R.layout.notitle_dialog, null);
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        lp.width = DensityUtil.dip2px(context, 259);
        dialogWindow.setAttributes(lp);
        Activity activity = (Activity) context;
        if (!activity.isFinishing()) {
            dialog.show();
            dialog.getWindow().setContentView(view);
        }
        TextView dialog_tv_title = view.findViewById(R.id.dialog_tv_title);
        TextView tvContent = view.findViewById(R.id.dialog_tv_content);
        TextView tvCancel = view.findViewById(R.id.dialog_tv_cancel);
        TextView tvOk = view.findViewById(R.id.dialog_tv_ok);
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(cancel)) {
            tvCancel.setText(cancel);
        }
        tvOk.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(title)) {
            dialog_tv_title.setText(title);
        }
        if (listener != null) {
            tvCancel.setEnabled(false);
            CountDownTimer timer = new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvCancel.setText(cancel + (millisUntilFinished / 1000) + "s");
                }

                public void onFinish() {
                    tvCancel.setText(cancel);
                    tvCancel.setEnabled(true);
                }
            };
            timer.start();
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOk(dialog);
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(dialog);
                }
            });
        }
        return dialog;
    }

    /**
     * 有倒计时
     *
     * @param context
     * @param listener
     * @return
     */
    public static AlertDialog showNoticeDialog(Context context, String title, CharSequence content, String cancel, final onDialogClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.dialog).create();
        View view = View.inflate(context, R.layout.notitle_dialog, null);
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        lp.width = DensityUtil.dip2px(context, 259);
        dialogWindow.setAttributes(lp);
        Activity activity = (Activity) context;
        if (!activity.isFinishing()) {
            dialog.show();
            dialog.getWindow().setContentView(view);
        }
        TextView dialog_tv_title = view.findViewById(R.id.dialog_tv_title);
        TextView tvContent = view.findViewById(R.id.dialog_tv_content);
        TextView tvCancel = view.findViewById(R.id.dialog_tv_cancel);
        TextView tvOk = view.findViewById(R.id.dialog_tv_ok);
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (!TextUtils.isEmpty(cancel)) {
            tvCancel.setText(cancel);
        }
        tvOk.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(title)) {
            dialog_tv_title.setText(title);
        }
        if (listener != null) {
            tvCancel.setEnabled(false);
            CountDownTimer timer = new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvCancel.setText(cancel + (millisUntilFinished / 1000) + "s");
                }

                public void onFinish() {
                    tvCancel.setText(cancel);
                    tvCancel.setEnabled(true);
                }
            };
            timer.start();
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOk(dialog);
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(dialog);
                }
            });
        }
        return dialog;
    }

    /**
     * @param context
     * @param listener
     * @return
     */
    public static AlertDialog showSureDialog(Context context, String title, String content, String ok, final onDialogClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.dialog).create();
        View view = View.inflate(context, R.layout.notitle_sure_dialog, null);
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        lp.width = DensityUtil.dip2px(context, 259);
        dialogWindow.setAttributes(lp);
        Activity activity = (Activity) context;
        if (!activity.isFinishing()) {
            dialog.show();
            dialog.getWindow().setContentView(view);
        }
        TextView dialog_tv_title = view.findViewById(R.id.dialog_tv_title);
        TextView tvContent = view.findViewById(R.id.dialog_tv_content);
        TextView tvOk = view.findViewById(R.id.dialog_tv_ok);
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(ok)) {
            tvOk.setText(ok);
        }
        if (!TextUtils.isEmpty(title)) {
            dialog_tv_title.setText(title);
        }
        if (listener != null) {
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOk(dialog);
                }
            });
        }
        return dialog;
    }

    public interface onDialogClickListener {
        void onCancel(Dialog dialog);

        void onOk(Dialog dialog);
    }
}
