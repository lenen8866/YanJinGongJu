package com.music.player.lib.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.music.player.lib.R;


/**
 * This class is used for creating a XToast.
 *
 * @author Chen Yu
 * @version 1.0
 * @date 2016-08-19
 */
public class XToast {
    private static Toast mToast;

    public static void showToast(Context context, String toastMsg) {
        try {
            if (context == null) {
                return;
            }
            if (TextUtils.isEmpty(toastMsg)) {
                return;
            }
            if (mToast != null) {
                mToast.cancel();
                mToast = null;
            }
            mToast = new Ftoast(context.getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);//自定义
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
            //可能在子线程调了这个方法
        }
    }

    //使用自定义toast
    private static class Ftoast extends Toast {

        public Ftoast(Context context, String msg, int duration) {
            super(context);
            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View root = inflate.inflate(R.layout.xtoast_layout, null);//加载自定义的XML布局
            TextView txtContent = root.findViewById(R.id.txtToast);
            txtContent.setText(msg);

            setDuration(duration);
            setView(root); //这是setView。就是你的自定义View
            //必须设置Gravity.FILL_HORIZONTAL 这个选项，布局文件的宽高才会正常显示
            setGravity(Gravity.CENTER | Gravity.FILL_HORIZONTAL, 0, 0); //这是，放着顶部，然后水平放满屏幕
        }

    }
}