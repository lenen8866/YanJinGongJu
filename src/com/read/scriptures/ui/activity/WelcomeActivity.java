package com.read.scriptures.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.music.player.lib.util.NetUtil;
import com.music.player.lib.util.XToast;
import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.InitVideoBean;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.db.DatabaseManager;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.manager.HomeDataManager;
import com.read.scriptures.model.ShouYeBean;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.DialogUtils;
import com.read.scriptures.util.DownloadFileUtils;
import com.read.scriptures.util.FileUtil;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.SharedPreferencesUtils;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.SystemUtils;
import com.read.scriptures.util.ThreadUtil;
import com.read.scriptures.util.TimeUtils;
import com.read.scriptures.util.rsa.DES;
import com.read.scriptures.util.rsa.RSA;
import com.read.scriptures.view.CircleTextProgressbar;
import com.read.scriptures.view.CustomView;
import com.read.scriptures.widget.WelcomeBookProgressDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class WelcomeActivity extends BaseActivity {

    private String url = "";
    private int recLen = 3;
    private boolean jump;
    private boolean isFirstResume = true;
    private boolean skip = false;
    private ImageView ivImage;
    private CircleTextProgressbar circleCountDown;
    private List<ShouYeBean> welcomeBeanList = new ArrayList();

    private CustomView videoView;

    // FIX: Handler 改为静态内部类 + WeakReference，避免内存泄漏
    private static class WelcomeHandler extends Handler {
        private final java.lang.ref.WeakReference<WelcomeActivity> mRef;
        WelcomeHandler(WelcomeActivity activity) { mRef = new java.lang.ref.WeakReference<>(activity); }
        @Override
        public void handleMessage(Message msg) {
            WelcomeActivity act = mRef.get();
            if (act == null || act.isDestroyed()) return;
            switch (msg.what) {
                case 2:
                    XToast.showToast(HuDongApplication.getInstance(), "请开启必要的权限");
                    act.exit();
                    break;
                case 3:
                    act.showWelcomeImage();
                    break;
                case 4:
                    PreferenceConfig.saveVersionCode(act, SystemUtils.getVersionName(act));
                    act.goMain();
                    break;
            }
        }
    }
    private final WelcomeHandler handler = new WelcomeHandler(this);
    private InitVideoBean initVideoBean;

    // FIX: 原方法请求了 WRITE/READ_EXTERNAL_STORAGE
    // Android 10+ 应用专属目录无需这两个权限；Android 13+ 上这两个权限已被系统拒绝且不会弹框
    // 现只保留必要的网络权限请求，直接进行资源初始化
    private void requestPermissions() {
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permissions) {
                        initResource();
                    }
                    @Override
                    public void permissionDenied(@NonNull String[] permissions) {
                        // FIX: 网络权限异常时不强制退出，继续初始化
                        initResource();
                    }
                },
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE);
    }

    private void initResource() {
        videoView = findViewById(R.id.videoView);
        ivImage = findViewById(R.id.iv_image);
        circleCountDown = findViewById(R.id.circle_count_down);
        boolean isLog = (boolean) getMetaData("LOG_SWITCH");
        boolean isShowVersion = (boolean) getMetaData("SHOW_VERSION");
        boolean isDebug = (boolean) getMetaData("DEBUG_SWITCH");
        HuDongApplication.getInstance().setLog(isLog);
        HuDongApplication.getInstance().setShowVersion(isShowVersion);
        HuDongApplication.getInstance().setDebug(isDebug);
        DatabaseManager.initDB();
        circleCountDown.setCountdownProgressListener(1, new CircleTextProgressbar.OnCountdownProgressListener() {
            @Override
            public void onProgress(int what, int progress, long progressTime) {
                if (progress <= 0) {
                    circleCountDown.setText("跳过");
                    //倒计时结束
                    if (!skip) {
                        initData();
                    }
                } else {
                    long time = recLen - progressTime / 1000;
                    circleCountDown.setText(time + "");
                }

            }
        });
        circleCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleCountDown.stop();
                circleCountDown.setVisibility(View.INVISIBLE);
                skip = true;
                initData();
                if (videoView.isPlaying()) {
                    videoView.stopPlayback();
                }
            }
        });
        if (!PreferenceConfig.getVersionCode(this).equals(SystemUtils.getVersionName(this))) {
            //第一次启动
            NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/systems/first", null, new NetUtil.CallBack() {
                @Override
                public void onSuccess(String t) {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    initVideoBean = new Gson().fromJson(t, InitVideoBean.class);
                    if (initVideoBean != null && initVideoBean.data != null && initVideoBean.data.open == 1 && !TextUtils.isEmpty(initVideoBean.data.url)) {
                        circleCountDown.setVisibility(View.VISIBLE);
                        circleCountDown.setTextColor(Color.WHITE);
                        circleCountDown.setText(initVideoBean.data.time);
                        int totalTime = parseInt(initVideoBean.data.time);
                        circleCountDown.setTimeMillis(totalTime * 1000);
                        circleCountDown.setCountdownProgressListener(1, new CircleTextProgressbar.OnCountdownProgressListener() {
                            @Override
                            public void onProgress(int what, int progress, long progressTime) {
                                if (progress <= 0) {
                                    circleCountDown.setText("跳过");
                                } else {
                                    long time = totalTime - progressTime / 1000;
                                    circleCountDown.setText(time + "跳过");
                                }
                            }
                        });
                        circleCountDown.start();
                        videoView.setVisibility(View.VISIBLE);
                        ivImage.setVisibility(View.GONE);
                        videoView.setVideoURI(Uri.parse(initVideoBean.data.url));
                        videoView.start();

                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                                    @Override
                                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                                            videoView.setBackgroundColor(Color.TRANSPARENT);
                                        return true;
                                    }
                                });
                            }
                        });
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                initData();
                            }
                        });
                    } else {
                        initData();
                    }
                }

                @Override
                public void onError(String t) {
                    super.onError(t);
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    initData();
                }
            });
        } else {
            initWelcomeInfo(true);
        }
    }

    private int parseInt(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        return Integer.parseInt(time);
    }

    /**
     * 连服务器查询欢迎页
     */
    private void initWelcomeInfo(final boolean isNet) {
        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                //获取本地缓存
                String locJson = SharedPreferencesUtils.getWelcomeInfo(ATHIS);
                if (StringUtil.isNotEmpty(locJson)) {
                    try {
                        welcomeBeanList = JSONObject.parseArray(locJson, ShouYeBean.class);
                        Iterator<ShouYeBean> shouYeBeanterator = welcomeBeanList.iterator();
                        while (shouYeBeanterator.hasNext()) {
                            ShouYeBean shouYeBean = shouYeBeanterator.next();
                            if (TimeUtils.getTime(shouYeBean.getEnddate().getVal()) < TimeUtils.getNowStamp()) {
                                //结束时间小于当前时间（已过期）
                                try {
                                    File file = new File(SystemConstants.APP_PATH + shouYeBean.getPicFileName());
                                    if (file.exists()) {
                                        //删除
                                        file.delete();
                                        //已过期移除
                                        welcomeBeanList.remove(shouYeBean);
                                        continue;
                                    }
                                    welcomeBeanList.remove(shouYeBean);
                                } catch (Exception e) {
                                }
                            }
                        }
                        SharedPreferencesUtils.saveWelcomeInfo(ATHIS, JSONObject.toJSONString(welcomeBeanList));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dealWelcome();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isNet) {
                                goMainActivity();
                            } else {
                                goMainActivity();
                            }
                        }
                    });
                }
            }
        });
    }


    //处理welcome获取的数据
    private void dealWelcome() {
        if (welcomeBeanList == null || welcomeBeanList.isEmpty()) {
            goMainActivity();
            return;
        }
        for (ShouYeBean shouYeBean : welcomeBeanList) {
            //保存图片到本地
            try {
                File file = new File(SystemConstants.APP_PATH + shouYeBean.getPicFileName());
                if (file.exists()) {
//                    已缓存，不用下载
                    shouYeBean.setLocPicFilePath(SystemConstants.APP_PATH + shouYeBean.getPicFileName());
                    continue;
                }
                DownloadFileUtils.downLoadFromUrl(shouYeBean.getPic_url(), shouYeBean.getPicFileName(), SystemConstants.APP_PATH);
                //
                shouYeBean.setLocPicFilePath(SystemConstants.APP_PATH + shouYeBean.getPicFileName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //保存启动页信息
        SharedPreferencesUtils.saveWelcomeInfo(this, JSONObject.toJSONString(welcomeBeanList));
        //通知页面更新
        Message message = new Message();
        message.what = 3;
        handler.sendMessage(message);
    }

    //展示启动页图片
    private void showWelcomeImage() {
        if (welcomeBeanList != null && !welcomeBeanList.isEmpty()) {
            //获取上一次展示的下标
            int showNowIndex = SharedPreferencesUtils.getShowNow(WelcomeActivity.this);
            if (showNowIndex == -1 || showNowIndex >= welcomeBeanList.size() - 1) {
                showNowIndex = 0;
            } else {
                showNowIndex++;
            }
            //展示图片
            int showType = welcomeBeanList.get(showNowIndex).getType();

            switch (showType) {
                case 1:
                    //每次启动都打开
                    //展示图片
                    SharedPreferencesUtils.saveShowNow(WelcomeActivity.this, showNowIndex);
                    displayImage(showNowIndex);
                    break;
                case 2:
                    //每天启动一次
                    if (!SharedPreferencesUtils.getShowDate(WelcomeActivity.this).equals(TimeUtils.getDate())) {//如果不是今天
                        SharedPreferencesUtils.saveShowDate(WelcomeActivity.this, TimeUtils.getDate());
                        SharedPreferencesUtils.saveShowNow(WelcomeActivity.this, showNowIndex);
                        //展示图片
                        displayImage(showNowIndex);
                    } else {
                        goMainActivity();
                    }
                    break;
                default:
                    //
                    goMainActivity();
                    break;
            }
        } else {
            goMainActivity();
        }
    }

    /**
     * 展示图片
     *
     * @param index
     */
    private void displayImage(int index) {
        recLen = Integer.valueOf(welcomeBeanList.get(index).getShowtime());
        url = welcomeBeanList.get(index).getLink();

        Picasso.get()
                .load(StringUtil.isNotEmpty(welcomeBeanList.get(index).getLocPicFilePath()) ? "file://" + welcomeBeanList.get(index).getLocPicFilePath() : welcomeBeanList.get(index).getPic_url())
                .resize(DensityUtil.dip2px(200), DensityUtil.dip2px(230))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap loadedImage, Picasso.LoadedFrom from) {
                        if (loadedImage != null) {
                            ivImage.setImageBitmap(loadedImage);
                        }
                        circleCountDown.setVisibility(View.VISIBLE);
                        circleCountDown.setText(recLen + "");
                        circleCountDown.setTimeMillis(1000 * recLen);
                        circleCountDown.start();
                        HuDongApplication.getInstance().getFileSystem().put(SystemConfig.WELCOME_IMAGE_KEY, loadedImage, 3600);

                        ivImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (StringUtil.isEmpty(url) || !SystemUtils.isOnline(WelcomeActivity.this)) {
                                        return;
                                    }
                                    SystemUtils.jumpToUrl(ATHIS, url);
                                    jump = true;
                                    circleCountDown.stop();
                                    circleCountDown.setText("跳过");
                                    circleCountDown.setProgress(0);
                                } catch (final Exception e) {
                                    XToast.showToast(WelcomeActivity.this, "网页地址未提供，或者无需提供！");
                                }
                            }
                        });
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {//音频封面加载失败
                        goMainActivity();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private void goMainActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }


    /**
     * 检查本地的激活状态
     */
    private void checkLocalActivationState() {
        check();
    }

    private void check() {
        if (SystemUtils.isOnline(ATHIS)) {//有网络访问服务器
            if (AccountManager.getInstance().isLogin()) {
                //重置显示状态
                if (!HuDongApplication.getInstance().isAppNormalLevelActivate() && !SharedUtil.getBoolean(PreferenceConfig.Preference_home_list_type, true)) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_home_list_type, true);
                }
                if (!HuDongApplication.getInstance().isAppNormalLevelActivate() && !SharedUtil.getBoolean(PreferenceConfig.Preference_home_sort_type, true)) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_home_sort_type, true);
                }
                if (!HuDongApplication.getInstance().isAppNormalLevelActivate() && !SharedUtil.getBoolean(PreferenceConfig.Preference_history_search_visible, true)) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_history_search_visible, true);
                }
                if (!HuDongApplication.getInstance().isAppNormalLevelActivate() && !SharedUtil.getBoolean(PreferenceConfig.Preference_short_paragraphs_visible, true)) {
                    SharedUtil.putBoolean(PreferenceConfig.Preference_short_paragraphs_visible, true);
                }
                jumpActivity();
            } else {
                jumpActivity();
            }
        } else {
            jumpActivity();
        }
        try {
            getZipPassWord();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initData() {
        initUMeng();
        long l = System.currentTimeMillis();
        if (!PreferenceConfig.getVersionCode(this).equals(SystemUtils.getVersionName(this))) {
            String[] toast = {getResources().getString(R.string.hint_first_1), getResources().getString(R.string.hint_first_2),
                    getResources().getString(R.string.hint_first_3), getResources().getString(R.string.hint_first_4), getResources().getString(R.string.hint_first_5)};
            //设置书籍识别码,一定是数字类型的，不能有任何非数据的符号出现在里面，包括空格
            String bookCode = "202010011908";
            SharedPreferencesUtils.saveBooklibCode(HuDongApplication.getInstance(), bookCode);
            copyDB(toast);
        } else {
            String filePath = HuDongApplication.getInstance().getDBDir();
            File zipFile = new File(filePath + "/hudong.db");
            InputStream inputStream = null;
            long length = 0;
            try {
                inputStream = getAssets().open("hudong.db");
                length = inputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        inputStream = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (length > 0 && (!zipFile.exists() || zipFile.length() != length)) {
                if (zipFile.exists()) {
                    zipFile.delete();
                }
                String[] toast = {getResources().getString(R.string.hint_first_1), getResources().getString(R.string.hint_first_2),
                        getResources().getString(R.string.hint_first_3), getResources().getString(R.string.hint_first_4), getResources().getString(R.string.hint_first_5)};
                copyDB(toast);
            } else {
                HomeDataManager.getInstance().updateSuccessRefreshHomeCategoryVolumes();
                if (!jump) {//!updateDatabase() &&  447631360
                    goMain();
                }
            }
        }
    }

    private void copyDB(String[] toast) {
        //判断空间是否充足
        long availableSize = getAvailableSize();
        String filePath = HuDongApplication.getInstance().getDBDir();
        File dirFile = new File(filePath);

        File zipFile = new File(filePath + "/hudong.db");
        InputStream inputStream = null;
        long length = 0;
        try {
            inputStream = getAssets().open("hudong.db");
            length = inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (availableSize < length) {
            //空间不足
            DialogUtils.showSureDialog(this, "温馨提示", "您的手机存储空间不足，请先清理空间后使用", "确定", new DialogUtils.onDialogClickListener() {
                @Override
                public void onCancel(Dialog dialog) {
                }

                @Override
                public void onOk(Dialog dialog) {
                    finish();
                }
            });
            return;
        }
        //先删除目录
//        dirFile.delete();

        Random random = new Random();
        int i = random.nextInt(toast.length);
        String hint = toast[i];
        final WelcomeBookProgressDialog dialog = new WelcomeBookProgressDialog(this, "加载中", true, hint);
        if (!dialog.isShowing())
            dialog.show();
        Window mWindow = dialog.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        dialog.setMaxProgress(500);
        ThreadUtil.execute(new Runnable() {
            public void run() {
                ThreadUtil.ProgressThread progressThread = new ThreadUtil.ProgressThread(500, dialog.getProgressBar(), dialog.getProgress(), 33);
                ThreadUtil.execute(progressThread);
                new File(filePath).mkdir();
                FileUtil.copyDBToSD(ATHIS, "hudong.db", zipFile.getAbsolutePath());
//                    Zip4jUtils.uncompressZip4j(filePath + "/hudong.db.zip", filePath, SystemConfig.ZIP_PASSWORD);
                progressThread.setStop(true);
                HomeDataManager.getInstance().updateSuccessRefreshHomeCategoryVolumes();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
                handler.sendEmptyMessage(4);
            }
        });
    }

    // FIX: 原方法用 Environment.getExternalStorageDirectory() 检测 SD 卡空间
    // Android 10+ 访问 SD 卡根目录受限，改为检测内部存储剩余空间
    private long getAvailableSize() {
        // FIX: 使用应用内部存储路径（data 分区）代替 SD 卡根目录
        StatFs sf = new StatFs(android.os.Environment.getDataDirectory().getAbsolutePath());
        long blockSize = sf.getBlockSizeLong();
        long availCount = sf.getAvailableBlocksLong();
        return availCount * blockSize;
    }

    @Override
    public void setIntent(Intent newIntent) {
        ATHIS.setIntent(newIntent);
    }

    private void goMain() {
        //检查登录
        checkLocalActivationState();
    }

    private boolean isJumpNext = false;

    private void jumpActivity() {
        if (isJumpNext == true) {
            return;
        }
        isJumpNext = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //判断是否登录
                if (AccountManager.getInstance().isLogin()) {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //未登录，跳转登录页
                    WeixinLoginActivity.launchAct(WelcomeActivity.this);
                    finish();
                }
            }
        });
    }

    private void getZipPassWord() {
        String rsaPassword = "82ADC881230D1D1C5F85267700373F95F96EDB10A038C518BD4BA0BAE5B3A5E460D530705E7C2570B4C2AC94F2E8EBD2C6E3E85C023202A189F838AEC415388D053423B28AD12AC1ACB61F497DB40DD3BD691C49D1A1883183A8DEC43F8096007F21189FE45B0521DB7F26C0B109A1208444273A24CBA2D6554BD04D704C2A7C";
        byte[] netByteArray = StringUtil.HexString2Bytes(rsaPassword);
        try {
            String passWord = new String(RSA.decryptByPublicKey(netByteArray, RSA.getPublicKey()));
            String des = "";
            if (passWord != null && !"".equals(passWord)) {
                for (int i = 0; i < passWord.length(); i++) {
                    if (passWord.charAt(i) >= 48 && passWord.charAt(i) <= 57) {
                        des += passWord.charAt(i);
                    }
                }
            }
            LogUtil.test("netpassWord：" + des);
            if (!StringUtil.isEmpty(des)) {
                DES.setPassword(des);
            }
            HuDongApplication.getInstance().getFileSystem().put("desPassWord", des);
        } catch (Exception e) {
        }
    }

    private void getVideoData() {
        requestPermissions();
    }

    /**
     * 获取meta-data中的值
     *
     * @param key
     * @return
     */
    public Object getMetaData(String key) {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            bundle.getBoolean("");
            Object value = bundle.get(key);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initUMeng() {
        try {
            ApplicationInfo applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                if (applicationInfo.metaData != null) {
                    int channelId = applicationInfo.metaData.getInt("DISTRIBUTE_CHANNEL", -1);
                    if (channelId == -1)
                        channelId = 0;
                    String appkey = applicationInfo.metaData.getString("UMENG_APPKEY");
//                    MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(getApplicationContext(), appkey, String.valueOf(channelId)));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.enableEncrypt(true);
    }

    protected ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.reset();
        mImmersionBar.init();

        getVideoData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
        if (circleCountDown != null) {
            circleCountDown.stop();
            circleCountDown = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    private int duration = 0;

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
            duration = videoView.getCurrentPosition();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (jump) {
            jump = false;
            goMain();
        } else if (!isFirstResume) {
//            goMain();
        }
        isFirstResume = false;
        if (videoView != null) {
            videoView.start();
            videoView.seekTo(duration);
        }
    }

}
