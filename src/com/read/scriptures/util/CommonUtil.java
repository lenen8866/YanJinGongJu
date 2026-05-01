package com.read.scriptures.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.R;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.ui.activity.ActiveActivity;
import com.read.scriptures.ui.activity.NewRechargeActivity;
import com.read.scriptures.ui.fragment.FragmentActive;
import com.read.scriptures.widget.CustomAlertDialog;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with Android Studio. User : Lim Email: lgmshare@gmail.com Date :
 * 2015/3/16 Time : 11:10 To change this template use File | Settings | File
 * Templates.
 */
public class CommonUtil {

    /**
     * 安装apk文件
     *
     * @param apkPath
     * @param context
     */
    public static void installApk(Context context, String apkPath) {
        File file = new File(apkPath);
        if (!file.exists()) {
            Toast.makeText(context, "未找不到安装文件", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(file);
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 卸载apk文件
     *
     * @param packageName
     * @param context
     */
    public static void uninstallApk(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("package:" + packageName);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 验证手机格式
     *
     * @param mobile
     * @return
     */
    public static boolean isMobileNO(String mobile) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobile)) {
            return false;
        } else {
            return mobile.matches(telRegex);
        }
    }

    /**
     * 验证Email地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 将传入的金额格式化为元,保留两位小数
     *
     * @param amount
     * @return
     */
    public static String formatMoney(double amount) {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("￥##,###.00");
        return df.format(amount);
    }

    /**
     * 将传入的金额（单位分）格式化为元，保留两位小数 格式如：5.00
     *
     * @param s
     * @return
     */
    public static String formatMoney(String s) {
        if (TextUtils.isEmpty(s)) {
            return "0.00";
        }
        double num = Double.parseDouble(s);
        DecimalFormat formater = new DecimalFormat("￥###,##0.00");
        String result = formater.format(num / 100.0d);
        return result;
    }

    /**
     * 将传入的金额（单位元）格式化为元，保留两位小数 格式如：5.00
     *
     * @param s
     * @param len 小数点位数
     * @return
     */
    public static String formatMoney(String s, int len) {
        if (s == null || s.length() < 1) {
            return "";
        }
        NumberFormat formater = null;
        double num = Double.parseDouble(s);
        if (len == 0) {
            formater = new DecimalFormat("###,###");

        } else {
            StringBuffer buff = new StringBuffer();
            buff.append("###,###.");
            for (int i = 0; i < len; i++) {
                buff.append("#");
            }
            formater = new DecimalFormat(buff.toString());
        }
        String result = formater.format(num);
        if (result.indexOf(".") == -1) {
            result = "￥" + result + ".00";
        } else {
            result = "￥" + result;
        }
        return result;
    }

    /**
     * 将传入的金额（单位元）格式化为元（精确到元，取消小数点）
     *
     * @param s
     * @return
     */
    public static String formatMoneyUnitYuan(String s) {
        String str = formatMoney(s, 2);
        return str.substring(0, str.indexOf("."));
    }

    /**
     * android 判断点击的位置是不是在指定的view上
     *
     * @param view
     * @param ev
     * @return
     */
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    /**
     * 调用系统拨号
     *
     * @param context
     * @param phone
     */
    public static void callSystemDialAction(Context context, String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    /**
     * 调用系统拨号
     *
     * @param context
     * @param phone
     */
    public static void callSystemSmsAction(Context context, String phone, String content) {
        content = content.replaceAll("〖(.*?)〗", "");
        content = content.replaceAll("(?<=\\[)(.*?)(?=])", "");
        content = content.replaceAll("(?<=\\{)[^}]*(?=\\})", "");
        content = content.replaceAll("\\[\\]", "");
        content = content.replaceAll("\\{\\}", "");
        Uri uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        context.startActivity(intent);
    }

    /**
     * 调用系统发送短信
     *
     * @param context
     * @param phone
     */
    public static void callSystemSmsAction(Context context, String phone) {
        Uri uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "");
        context.startActivity(intent);
    }

    /**
     * 获取本地ip地址
     *
     * @return
     */
    public String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean isListViewReachBottomEdge(final ListView listView) {
        boolean result = false;
        if (listView != null && listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView
                    .getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result = bottomChildView != null ? (listView.getHeight() >= bottomChildView.getBottom()) : true;
        }
        ;
        return result;
    }

    /**
     * 获取当前版本标示号
     *
     * @param context
     * @return
     */
    public static int getCurrentVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取当前版本号
     *
     * @param context
     * @return
     */
    public static String getCurrentVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 是否挂载了SD卡
     *
     * @return
     */
    public static boolean isHaveExternalStorage() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 半角转全角
     *
     * @param input
     * @return
     */
    public static String ToSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127)
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    /**
     * 全角转换为半角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    public static void copy(Context context, String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    public static void showActivateDialog(final Context context, String levelType) {
        String levelName = AccountManager.getInstance().getLevelName(levelType);
        final CustomAlertDialog aleratDialog = new CustomAlertDialog(context, "-1");
        aleratDialog.setTitle("权限受限");
        aleratDialog.setMessage("加入" + levelName + "后，拥有本功能");
        aleratDialog.setPositiveButton(R.string.activate_pay, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aleratDialog.dismiss();
                ActivityUtil.next((Activity) context, ActiveActivity.class);
            }
        });
        aleratDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aleratDialog.dismiss();
            }
        });
        aleratDialog.show();
    }

    public static void showActivateDialogWithCancelAction(final Context context, String levelType, final Runnable action) {
        final CustomAlertDialog aleratDialog = new CustomAlertDialog(context, "-1");
        aleratDialog.setTitle("资金支持");
        aleratDialog.setMessage("尊敬的用户您好:\n\t\t\t\t我们的软件需要持续的维护，为此，当你点击，这个功能块的时候。我们很希望你能够慷慨解囊，作为资金上的支持，如果你经济上有困难，你可以联系我们，我们也可以免费为你开通。");
        aleratDialog.setPositiveButton("充值会员", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aleratDialog.dismiss();
                Intent intent = new Intent(context, ActiveActivity.class);
                intent.putExtra("vip_recharge_type", 0);
                context.startActivity(intent);
                action.run();
            }
        });
        aleratDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aleratDialog.dismiss();
                action.run();
            }
        });
        aleratDialog.show();
    }

}
