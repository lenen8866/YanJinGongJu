package com.read.scriptures.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.FileProvider;
import android.util.Log;

import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.ui.activity.ActiveActivity;
import com.read.scriptures.ui.activity.MainActivity;
import com.read.scriptures.widget.QProcessDialog;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static com.read.scriptures.constants.SystemConstants.APK;

@SuppressLint("SdCardPath")
public class DownloadUtils {
    public static final String TEMP_FILENAME = APK;
    private static final int BUFFER_SIZE = 4096;

    public static void installApk(Activity context, QProcessDialog dialog) {

//        String tempFileName = TEMP_FILENAME;
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.fromFile(new File(getInstallApkFileName(context, tempFileName))),
//                "application/vnd.android.package-archive");
//        context.startActivity(intent);
        checkIsAndroidO(context, dialog);
    }

    private static void install(Activity context, String PATH) {
        Intent installer = new Intent();
        installer.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installer.setDataAndType(FileProvider.getUriForFile(context, HuDongApplication.getInstance().getPackageName() + ".fileprovider", new File(PATH)),
                    "application/vnd.android.package-archive");
        } else {
            installer.setDataAndType(Uri.parse("file://" + PATH),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(installer);
    }

    private static void checkIsAndroidO(Activity context, QProcessDialog dialog) {
        String mFilePath = TEMP_FILENAME;// getInstallApkFileName(context, );//"file://" + mFilePath +"/"+ S_APK_NAME
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = HuDongApplication.getInstance().getPackageManager().canRequestPackageInstalls();
            if (b) {
                install(context, mFilePath);//安装应用的逻辑(写自己的就可以)
                dialog.dismiss();
            } else {
                dialog.setBtnInstallApk(true);
                //请求安装未知应用来源的权限
                Uri packageURI = Uri.parse("package:" + context.getPackageName());
                //注意这个是8.0新API
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                context.startActivityForResult(intent, 10086);
                XToast.showToast(context, "请先允许" + context.getResources().getString(R.string.app_name) + "APP安装未知应用");
            }
        } else {
            install(context, mFilePath);
        }
    }

    public static boolean downloadApk(Activity activity, String baseUrl) throws NotOnlineException {
        if (baseUrl == null) {
            return false;
        }
        int connectionTimeout = 3000;
        int soTimeout = 3000;

        return downloadApk(activity, baseUrl, connectionTimeout, soTimeout);
    }


    private static boolean downloadApk(final Activity activity, String url, int connectionTimeout, int soTimeout) throws NotOnlineException {
        LogUtil.test(String.format("start download: url [%s]", url));

        if (!SystemUtils.isOnline(activity.getApplicationContext())) {
            LogUtil.test("not online");
            throw new NotOnlineException();
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                QProcessDialog dialog = new QProcessDialog(activity, "正在下载安装包…");
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).getActivityValue().put("tempDialog", dialog);
                } else if (activity instanceof ActiveActivity) {
                    ((ActiveActivity) activity).getActivityValue().put("tempDialog", dialog);
                }
                dialog.setSleep(1000);
                dialog.show();
            }
        });


        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
        HttpConnectionParams.setSoTimeout(params, soTimeout);
        params.setBooleanParameter(AllClientPNames.USE_EXPECT_CONTINUE, false);
        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                //失败
                LogUtil.test("" + response.getStatusLine().getStatusCode());
                Object obj = null;
                if (activity instanceof MainActivity) {
                    obj = ((MainActivity) activity).getActivityValue().get("tempDialog");
                } else if (activity instanceof ActiveActivity) {
                    obj = ((ActiveActivity) activity).getActivityValue().get("tempDialog");
                }
                if (obj != null) {
                    final QProcessDialog dialog = (QProcessDialog) obj;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            XToast.showToast(activity, "下载失败");
                        }
                    });
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).getActivityValue().put("tempDialog", null);
                    } else if (activity instanceof ActiveActivity) {
                        ((ActiveActivity) activity).getActivityValue().put("tempDialog", null);
                    }
                }
                return false;
            }
            FileOutputStream output = null;
            InputStream in = null;
            try {
                Object obj = null;
                if (activity instanceof MainActivity) {
                    obj = ((MainActivity) activity).getActivityValue().get("tempDialog");
                } else if (activity instanceof ActiveActivity) {
                    obj = ((ActiveActivity) activity).getActivityValue().get("tempDialog");
                }
                if (obj != null) {
                    QProcessDialog dialog = (QProcessDialog) obj;
                    dialog.setShow("开始下载…");
                }
                File file = new File(TEMP_FILENAME);
                if (file.exists())
                    file.delete();
                else {
                    File parent = file.getParentFile();
                    if (!parent.exists())
                        parent.mkdirs();
                    file.createNewFile();
                }
                output = new FileOutputStream(file, true);
                in = response.getEntity().getContent();
                long total = response.getEntity().getContentLength();
                LogUtil.test("total:" + response.getEntity().getContentLength());
                byte[] b = new byte[BUFFER_SIZE];
                int length = 0;
                long size = 0;
                long totalKb = total / 1024 / 1024;
                while ((length = in.read(b)) != -1) {
                    output.write(b, 0, length);
                    size += length;
                    // LogUtil.test( "Length [" + length +
                    // "]");
                    try {
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).getActivityValue().get("tempDialog");
                        } else if (activity instanceof ActiveActivity) {
                            ((ActiveActivity) activity).getActivityValue().get("tempDialog");
                        }
                        if (obj != null) {
                            QProcessDialog dialog = (QProcessDialog) obj;
//                            dialog.setShow(size / 1024 + "/" + totalKb + "KB");
                            dialog.setShow((size / 1024) / 1024 + "/" + totalKb + "MB");
                            int progress = (int) (size * 100 / total);
                            // LogUtil.test("size:" + size + ",progress:" +
                            // progress);
                            dialog.setProgress(progress);
                        }
                        Thread.sleep(0);
                    } catch (Exception e) {
                        LogUtil.test("interrupted");
                        if (activity instanceof MainActivity) {
                            obj = ((MainActivity) activity).getActivityValue().get("tempDialog");
                        } else if (activity instanceof ActiveActivity) {
                            obj = ((ActiveActivity) activity).getActivityValue().get("tempDialog");
                        }
                        if (obj != null) {
                            final QProcessDialog dialog = (QProcessDialog) obj;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    XToast.showToast(activity, "下载失败");
                                }
                            });
                            if (activity instanceof MainActivity) {
                                ((MainActivity) activity).getActivityValue().put("tempDialog", null);
                            } else if (activity instanceof ActiveActivity) {
                                ((ActiveActivity) activity).getActivityValue().put("tempDialog", null);
                            }
                        }
                        Log.e(DownloadUtils.class.getSimpleName(), "Error", e);
                        return false;
                    }
                }
                if (obj != null) {
                    final QProcessDialog dialog = (QProcessDialog) obj;
                    if (dialog.getProgress() >= 100) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                installApk(activity, dialog);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                    boolean b = HuDongApplication.getInstance().getPackageManager().canRequestPackageInstalls();
//                                    if (!b) {
//                                        dialog.setBtnInstallApk(true);
//                                    } else {
//
//                                        dialog.dismiss();
//                                    }
//                                } else {
//                                    installApk(activity);
//                                    dialog.dismiss();
//                                }
                            }
                        });
                    }
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).getActivityValue().put("tempDialog", null);
                    } else if (activity instanceof ActiveActivity) {
                        ((ActiveActivity) activity).getActivityValue().put("tempDialog", null);
                    }
                }
                LogUtil.test("Size [" + size + "](" + response.getStatusLine() + ")");
            } catch (Exception e) {
                e.printStackTrace();
                Object obj = null;
                if (activity instanceof MainActivity) {
                    obj = ((MainActivity) activity).getActivityValue().get("tempDialog");
                } else if (activity instanceof ActiveActivity) {
                    obj = ((ActiveActivity) activity).getActivityValue().get("tempDialog");
                }
                if (obj != null) {
                    final QProcessDialog dialog = (QProcessDialog) obj;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            XToast.showToast(activity, "下载失败");
                        }
                    });
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).getActivityValue().put("tempDialog", null);
                    } else if (activity instanceof ActiveActivity) {
                        ((ActiveActivity) activity).getActivityValue().put("tempDialog", null);
                    }
                }
                Log.e(DownloadUtils.class.getSimpleName(), "Error", e);
                throw new NotOnlineException(e);
            } finally {
                try {
                    if (output != null) {
                        output.flush();
                        output.close();
                    }
                } catch (Exception e) {
                    // N/A
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e) {
                    // N/A
                }
            }
        } catch (Exception e) {
            Object obj = null;
            if (activity instanceof MainActivity) {
                obj = ((MainActivity) activity).getActivityValue().get("tempDialog");
            } else if (activity instanceof ActiveActivity) {
                obj = ((ActiveActivity) activity).getActivityValue().get("tempDialog");
            }
            if (obj != null) {
                final QProcessDialog dialog = (QProcessDialog) obj;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        XToast.showToast(activity, "下载失败");
                    }
                });
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).getActivityValue().put("tempDialog", null);
                } else if (activity instanceof ActiveActivity) {
                    ((ActiveActivity) activity).getActivityValue().put("tempDialog", null);
                }
            }
            Log.e(DownloadUtils.class.getSimpleName(), "Error", e);
            throw new NotOnlineException(e);
        }
        return true;
    }

    private static String getApkFileName(Context context) {
        return "";
        // context.getPackageName()
        // MessageUtils.getMessage("apk_file_name") + ".apk";
    }

    private static String getInstallApkFileName(Context context, String tempFileName) {
        return "/data/data/" + context.getPackageName() + "/files/" + tempFileName;
    }
}
