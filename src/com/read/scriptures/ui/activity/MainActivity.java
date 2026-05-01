package com.read.scriptures.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.binioter.guideview.Guide;
import com.binioter.guideview.GuideBuilder;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.toolbox.DiskBasedCache;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.db.CacheManager;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicPlayerEventListener;
import com.music.player.lib.listener.MusicPlayerInfoListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.NetUtil;
import com.music.player.lib.util.SaltUtils;
import com.music.player.lib.util.XToast;
import com.read.scriptures.EIUtils.ActivityUtil;
import com.read.scriptures.R;
import com.read.scriptures.alipay.PayResult;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.audio.AudioPlayActivity;
import com.read.scriptures.audio.NewAudioActivity;
import com.read.scriptures.bean.AssetsStatusBean;
import com.read.scriptures.bean.AssetsVersionBean;
import com.read.scriptures.bean.AudioShareLinkBean;
import com.read.scriptures.bean.BookBean;
import com.read.scriptures.bean.DownloadAudioEvent;
import com.read.scriptures.bean.MainNoticeBean;
import com.read.scriptures.bean.NewAudioChapterData;
import com.read.scriptures.bean.NoticeMsgBean;
import com.read.scriptures.bean.PackageBean;
import com.read.scriptures.bean.RechargeBean;
import com.read.scriptures.bean.RespInfo;
import com.read.scriptures.bean.ShareBean;
import com.read.scriptures.bean.UserInfo;
import com.read.scriptures.bean.UserNetInfo;
import com.read.scriptures.bean.WxpayBean;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.constants.SystemConstants;
import com.read.scriptures.event.LoginOutEvent;
import com.read.scriptures.event.ReStartEvent;
import com.read.scriptures.http.okhttp.HttpCallback;
import com.read.scriptures.http.okhttp.OkHttpUtils;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.manager.LevelInfoManager;
import com.read.scriptures.model.BookResult;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.DownLoadItem;
import com.read.scriptures.model.ShouYeBean;
import com.read.scriptures.model.WelcomeResult;
import com.read.scriptures.net.NetObserver;
import com.read.scriptures.share.wxapi.WXPayUtils;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.FragmentViewPagerAdapterNew;
import com.read.scriptures.ui.adapter.RechargeListAdapter;
import com.read.scriptures.ui.fragment.HomeFragment;
import com.read.scriptures.ui.fragment.NullFragment;
import com.read.scriptures.ui.fragment.StoreFragment;
import com.read.scriptures.util.CommonUtil;
import com.read.scriptures.util.DensityUtil;
import com.read.scriptures.util.DialogUtils;
import com.read.scriptures.util.DownloadFileUtils;
import com.read.scriptures.util.DownloadQueue;
import com.read.scriptures.util.DownloadUtils;
import com.read.scriptures.util.FileUtil;
import com.read.scriptures.util.GsonUtils;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.NetConnectUtil;
import com.read.scriptures.util.NetSocietyShare;
import com.read.scriptures.util.NotOnlineException;
import com.read.scriptures.util.PayUtil;
import com.read.scriptures.util.PicassoUtils;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SharedPreferencesUtils;
import com.read.scriptures.util.SimpleComponent;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.SystemUtils;
import com.read.scriptures.util.ThreadUtil;
import com.read.scriptures.util.TimeUtils;
import com.read.scriptures.util.Utils;
import com.read.scriptures.video.VideoMainActivity;
import com.read.scriptures.view.AudioPlayingView;
import com.read.scriptures.widget.CustomAlertDialog;
import com.read.scriptures.widget.NoScrollViewPager;
import com.read.scriptures.widget.PagerSlidingTabStrip;
import com.read.scriptures.widget.WelcomeProgressDialog;
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static android.graphics.Typeface.BOLD;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends BaseActivity implements HomeFragment.FragmentInteraction, OnClickListener {
    private static final String TAG = "MainActivity";

    //    private static final String[] FRAGMENT_LABLE = new String[]{"书库", "灵修", "百科", "问答", "音频", "视频"};
//    private static final String[] FRAGMENT_LABLE = new String[]{"书库", "音频", "视频", "微店", "更多"};
    private static final String[] FRAGMENT_LABLE = new String[]{"书库", "音频", "视频", "更多"};

    private CustomAlertDialog mCommonAlertDialog;
    private CustomAlertDialog mDownloadBookDialog;
    private NoScrollViewPager mPager;
    private TextView tvVersionName;
    private FrameLayout flBookUpdate;
    private CustomAlertDialog appUpdateDialog;
    private MusicPlayerEventListener musicPlayerEventListener;
    private MusicPlayerInfoListener musicPlayerInfoListener;

    private PagerSlidingTabStrip mIndicator;
    private FragmentViewPagerAdapterNew mAdapter;
    //    private SpiritualityListFragment spiritualityListFragment;
    private HomeFragment homeFragment;
    private int pageIndex = 0;
    private int lastIndex = 0;
    private final int mSearchType = 4;// 搜索类别 1：书籍 2：章节 3：目录 4：内容
    private int mSearchRange = 1;// 搜索范围 1：圣经 2：怀著
    private Category mSearchNode;
    private ImageView iv_back;
    private ImageView iv_left;
    private ImageView iv_recharge_home;

    private ImageView iv_search;
    private AlertDialog shareDialog;
    private final DownLoadItem mDownLoadItem = new DownLoadItem();
    private String shareUrl = "";
    private String shareContent = "";
    private String wxMiniId = "";
    private String wxMiniPath = "";
    private boolean foucs = false;
    public MusicPlayerManager musicPlayerManager;
    private AudioPlayingView fl_view;
    private View touch_view;
    private FrameLayout fl_service;
    private View view_unread;

    private View include_notice;
    private TextView tv_notice_title;
    private TextView tv_notice_content;
    private ImageView iv_notice_close;


    // 优化书籍更新
    private boolean initBookUpdateSuccess = false;
    private int initBookUpdateTimes = 5;
    private Timer timer;

    private int screenWidth;
    private int screenHeight;
    private int viewWidth;
    private int viewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        initAgain();
        musicPlayerManager = MusicPlayerManager.getInstance().init(getApplicationContext());
        musicPlayerManager.initialize(MainActivity.this);
        LevelInfoManager.getInstance();
        fl_view.postDelayed(new Runnable() {
            @Override
            public void run() {
                initMusicPlayerManager();
            }
        }, 2000);

        assetsVersion = PreferencesUtils.getString(MainActivity.this, "assets_is_update", "");
        initAssetsVersion();
        String registrationID = JPushInterface.getRegistrationID(this);
    }

    private String assetsVersion = "";

    private void initAssetsVersion() {
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/systems/ver", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                AssetsVersionBean assetsVersionBean = new Gson().fromJson(t, AssetsVersionBean.class);
                if (assetsVersionBean != null) {
                    PreferencesUtils.putString(MainActivity.this, "assets_is_update", assetsVersionBean.version);
                    if (!TextUtils.equals(assetsVersion, assetsVersionBean.version)) {
                        CacheManager.getInstance().clear();
                        new DiskBasedCache(RxVolley.CACHE_FOLDER).clear();
                    }
                }
            }
        });
    }

    /**
     * 修复：Handler 改为静态内部类 + WeakReference，避免隐式持有 MainActivity 引用导致内存泄漏。
     * 同时加 instanceof 检查，避免 msg.obj 强转 ClassCastException。
     */
    private static class AudioDownloadHandler extends Handler {
        private final java.lang.ref.WeakReference<MainActivity> mRef;

        AudioDownloadHandler(MainActivity activity) {
            mRef = new java.lang.ref.WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity activity = mRef.get();
            // Activity 已销毁则不处理，防止内存泄漏后的空操作
            if (activity == null || activity.isDestroyed()) {
                return;
            }
            // 修复：加 instanceof 检查，防止强转 ClassCastException
            if (!(msg.obj instanceof BaseAudioInfo)) {
                return;
            }
            BaseAudioInfo audioBean = (BaseAudioInfo) msg.obj;
            activity.downloadAudio(
                    String.valueOf(audioBean.id),
                    audioBean.audio_url,
                    audioBean.cate_id,
                    audioBean.chapter
            );
        }
    }

    // 修复：使用静态内部类 Handler，不再持有 MainActivity 强引用
    private final AudioDownloadHandler handler = new AudioDownloadHandler(this);

    AlertDialog musicPlayerErrorDialog = null;

    private void initMusicPlayerManager() {
        musicPlayerManager.setPlayerModel(MusicConstants.MUSIC_MODEL_ORDER);
        //应用播放器配置
        musicPlayerManager.setNotificationEnable(true)
                //常驻进程开关，默认开启
                .setLockForeground(true)
                //设置点击通知栏跳转的播放器界面,需开启常驻进程开关
                .setPlayerActivityName(AudioPlayActivity.class.getCanonicalName());
        String currentAuthor = PreferencesUtils.getString(this, "audio_author");
        musicPlayerManager.setCurrentAuthor(currentAuthor);
        float playSpeed = PreferencesUtils.getFloat(this, "play_speed", 1.0f);
        musicPlayerManager.setSpeed(playSpeed);
        musicPlayerInfoListener = new MusicPlayerInfoListener() {
            @Override
            public void onPlayMusiconInfo(BaseAudioInfo audioBean, int position) {
                audioIsPlayed = true;
                fl_view.show();
                fl_view.setData(audioBean);
            }
        };
        musicPlayerManager.addPlayInfoListener(musicPlayerInfoListener);
        musicPlayerEventListener = new MusicPlayerEventListener() {
            @Override
            public void onMusicPlayerState(int playerState, String msg) {
                fl_view.hideLoading();
                switch (playerState) {
                    case MusicConstants.MUSIC_PLAYER_COMPLETE:
//                        getNextChapter();
//                        musicPlayerManager.setPlayIndex(musicPlayerManager.getBookAllAudio().size()-1);
                        break;
                    case MusicConstants.MUSIC_PLAYER_PREPARE://准备
                        fl_view.showLoading();
                        fl_view.stopAnimation();
                        break;
                    case MusicConstants.MUSIC_PLAYER_ERROR://错误
                        BaseAudioInfo currentPlayerMusic = musicPlayerManager.getCurrentPlayerMusic();
                        if (currentPlayerMusic != null) {
                            commitInvalidPlayUrl(String.valueOf(currentPlayerMusic.id), currentPlayerMusic.audio_url);
                        }
                        fl_view.stopAnimation();
                        if (musicPlayerErrorDialog == null) {
                             musicPlayerErrorDialog = DialogUtils.showSureDialog(HuDongApplication.topActivity, "播放失败", msg, "确定", new DialogUtils.onDialogClickListener() {
                                @Override
                                public void onCancel(Dialog dialog) {
                                }

                                @Override
                                public void onOk(Dialog dialog) {
                                    musicPlayerErrorDialog.dismiss();
                                    musicPlayerErrorDialog = null;
                                }
                            });
                        }
                        break;
                    case MusicConstants.MUSIC_PLAYER_PAUSE://暂停
                    case MusicConstants.MUSIC_PLAYER_STOP://停止
                    case MusicConstants.MUSIC_PLAYER_BUFFER://缓冲
                        fl_view.stopAnimation();
                        break;
                    case MusicConstants.MUSIC_PLAYER_PLAYING://播放中
                        fl_view.startAnimation();
                        if (fl_view.getVisibility() == View.GONE) {
                            fl_view.show();
                        }
                        BaseAudioInfo playerMusic = musicPlayerManager.getCurrentPlayerMusic();
                        handler.removeCallbacksAndMessages(null);
                        Message message = handler.obtainMessage();
                        message.obj = playerMusic;
                        handler.sendMessageDelayed(message, 5000);
                        break;

                }
            }

            @Override
            public void onPrepared(long totalDurtion) {

            }

            @Override
            public void onBufferingUpdate(int percent) {

            }

            @Override
            public void onInfo(int event, int extra) {

            }

            @Override
            public void onPlayMusiconInfo(BaseAudioInfo musicInfo, int position) {
            }

            @Override
            public void onMusicPathInvalid(BaseAudioInfo musicInfo, int position) {

            }

            @Override
            public void onTaskRuntime(long totalDurtion, long current, long alarmResidueDurtion, int bufferProgress) {
                if (current / 1000 >= totalDurtion / 1000) {
                    current = 0;
                }
                if (current >= totalDurtion) {
                    current = 0;
                }
                float progress = ((float) current / totalDurtion) * 100;
                fl_view.setProgress(progress, totalDurtion, current);
            }

            @Override
            public void onPlayerConfig(int playModel, int alarmModel, boolean isToast) {

            }
        };
        fl_view.postDelayed(new Runnable() {
            @Override
            public void run() {
                musicPlayerManager.addOnPlayerEventListener(musicPlayerEventListener);
            }
        }, 1000);

    }

    private void downloadAudio(String id, String audioUrl, String cate_id, String chapter) {
        if (TextUtils.isEmpty(audioUrl)) {
            commitInvalidPlayUrl(id, audioUrl);
            return;
        }
        if (!FileUtil.checkFreeSpace()) {
            showMessage("对不起，内存存储不足");
            return;
        }
        String audioDownloadPath = SaltUtils.getUrl(audioUrl);
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                String videoCachePath = FileUtil.getDiskCachePath(MainActivity.this) + File.separator + id;
                String videoCacheTempPath = FileUtil.getDiskCachePath(MainActivity.this) + File.separator + id + "_tmp";
                NetUtil.downloadAudio(audioDownloadPath, videoCachePath, videoCacheTempPath, new FileCallback(FileUtil.getDiskCachePath(MainActivity.this), id + "_tmp") {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                        File newFile = new File(FileUtil.getDiskCachePath(MainActivity.this), id);
                        if (!newFile.exists()) {
                            response.body().renameTo(newFile);
                        }
                        DownloadAudioEvent downloadAudioEvent = new DownloadAudioEvent();
                        downloadAudioEvent.id = id;
                        downloadAudioEvent.cate_id = cate_id;
                        EventBus.getDefault().post(downloadAudioEvent);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<File> response) {
//                        ToastCenterUtil.showToast(chapter + " 播放错误");
//                        playNext();
                    }
                });
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                showMessage(MainActivity.this, "请开启文件权限");
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    private void commitInvalidPlayUrl(String audioID, String url) {
        if (NetUtil.isNetWorkAvailable(this)) {
            Map<String, String> map = new HashMap<>();
            map.put("type", "0");
            map.put("value", audioID);
            map.put("url", url);
            NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/systems/invalidUrl", map, new NetUtil.CallBack() {
                @Override
                public void onSuccess(String t) {
                    Log.d("tang", "上传数据:" + url);
                }
            });
        }
    }

    /**
     * 播放下一首
     */
    private void playNext() {
        int nextPlayIndex = musicPlayerManager.playNextIndex();
        if (nextPlayIndex == -1) {
            if (NetUtil.isNetWorkAvailable(this)) {//有网络
//                getNextChapter();
            } else {
                playNativeMusic();
            }
        } else {
            musicPlayerManager.startPlayMusic(nextPlayIndex);
        }
    }

    private void playNativeMusic() {
        ArrayList<BaseAudioInfo> dataList = (ArrayList<BaseAudioInfo>) musicPlayerManager.getCurrentPlayList();
        if (dataList != null && !dataList.isEmpty()) {
            ArrayList<BaseAudioInfo> nativeMusic = new ArrayList<>();
            for (BaseAudioInfo itemBean : dataList) {
                boolean isExists = new File(FileUtil.getDiskCachePath(MainActivity.this), String.valueOf(itemBean.id)).exists();
                if (isExists) {
                    nativeMusic.add(itemBean);
                }
            }
            if (nativeMusic.isEmpty()) {
                showMessage("本章无缓存,请链接网络");
                musicPlayerManager.onStop();
                return;
            }
            musicPlayerManager.startPlayMusic(nativeMusic, 0);
            musicPlayerManager.setBookAllAudio(nativeMusic);
        } else {
            showMessage("本章无缓存,请链接网络");
            musicPlayerManager.onStop();
        }
    }

    private void initAgain() {
        StatusBarUtils.initMainColorStatusBar(this);
        PreferencesUtils.putString(getApplicationContext(), "update", "2");
        update(true);
        initActionBar();
        initViews();
        // 性能优化
        // initBookUpdate(true);
        initBookUpdateTask();

        HuDongApplication.getInstance().setExistMain(true);
        if (!SharedPreferencesUtils.getShareReadTimeByDay(HuDongApplication.getInstance()).equals(TimeUtils.getDate())) {
            initShare();
        }
        uninstallPackage();
        checkUserInfo();
        //获取欢迎页广告，下次启动展示
        initWelcomeInfo();
        getAudioShareLink();
        initNotice();
        initAssetsStatus();
        initCacheAudio();
    }


    private ArrayList<BaseAudioInfo> cacheAudioInfo;
    private int inCateIndex = 0;
    private boolean audioIsPlayed = false;

    private void initCacheAudio() {
        try {
            BaseAudioInfo cacheAudioInfo = (BaseAudioInfo) PreferencesUtils.getObject(this, "last_play_audio_cache");
            if (cacheAudioInfo != null) {//说明有最后一次记录
                //获取当前音频，
                getBookAllAudio(cacheAudioInfo);
            }
        } catch (Exception ignored) {

        }
    }

    private void getBookAllAudio(BaseAudioInfo item) {
        // 修复：先检查登录状态和 userInfo 非空，否则 getToken() 必然 NullPointerException
        UserInfo userInfo = AccountManager.getInstance().getUserInfo();
        if (userInfo == null || !AccountManager.getInstance().isLogin()) {
            return;
        }
        showProgressDialog("加载中...");
        Map<String, String> map = new HashMap<>();
        map.put("token", userInfo.getToken());
        map.put("book", String.valueOf(item.cate3_id));
        map.put("author", item.author);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/multimedia3/audioList", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                dismissProgressDialog();
                NewAudioChapterData newAudioChapterData = new Gson().fromJson(t, NewAudioChapterData.class);
                if (newAudioChapterData == null || newAudioChapterData.rows == null || newAudioChapterData.rows.isEmpty()) {
                    return;
                }
                for (int i = 0; i < newAudioChapterData.rows.size(); i++) {
                    BaseAudioInfo baseAudioInfo = newAudioChapterData.rows.get(i);
                    if (TextUtils.equals(item.id + "", String.valueOf(baseAudioInfo.id))) {
                        inCateIndex = i;
                        baseAudioInfo.playDuration = item.playDuration;
                        baseAudioInfo.isCached = true;
                        break;
                    }
                }
                cacheAudioInfo = newAudioChapterData.rows;
            }

            @Override
            public void onError(String t) {
                dismissProgressDialog();
            }
        });
    }


    private boolean assetsStatusShow = true;

    private void initAssetsStatus() {
        NetUtil.get("https://book.sdacn.cn/api/v1.systems/ver", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                try {
                    AssetsStatusBean assetsStatusBean = new Gson().fromJson(t, AssetsStatusBean.class);
                    if (assetsStatusBean == null || TextUtils.isEmpty(assetsStatusBean.status)) {
                        assetsStatusShow = true;
                        return;
                    }
                    if (TextUtils.equals("hidden", assetsStatusBean.status)) {
                        assetsStatusShow = false;
                    } else {
                        assetsStatusShow = true;
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    //https://book.sdacn.cn/api/v1/systems/notice
    private void initNotice() {
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/systems/notice", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                MainNoticeBean mainNoticeBean = new Gson().fromJson(t, MainNoticeBean.class);
                if (mainNoticeBean != null && mainNoticeBean.data != null && !mainNoticeBean.data.isEmpty()) {
                    MainNoticeBean.DataDTO dataDTO0 = mainNoticeBean.data.get(0);
                    parseNoticeInfo(dataDTO0);
                    if (mainNoticeBean.data.size() > 1) {//还有通知
                        MainNoticeBean.DataDTO dataDTO1 = mainNoticeBean.data.get(1);
                        parseNoticeInfo(dataDTO1);
                    }
                }
            }
        });
    }

    private void parseNoticeInfo(MainNoticeBean.DataDTO dataDTO0) {
        if (dataDTO0.type != null) {
            if (dataDTO0.type.val == 0) {//
                int system_notice_view_show = PreferencesUtils.getInt(MainActivity.this, "system_notice_view_show");
                if (system_notice_view_show == dataDTO0.id) {
                    return;
                }
                PreferencesUtils.putInt(MainActivity.this, "system_notice_view_show", dataDTO0.id);
                include_notice.setVisibility(View.VISIBLE);
                tv_notice_title.setText(dataDTO0.title);
                tv_notice_content.setText(dataDTO0.content);
            } else {
                if (!guideViewIsShow()) {
                    return;
                }
                int system_dialog_view_show = PreferencesUtils.getInt(MainActivity.this, "system_dialog_view_show");
                if (system_dialog_view_show == dataDTO0.id) {
                    return;
                }
                PreferencesUtils.putInt(MainActivity.this, "system_dialog_view_show", dataDTO0.id);
                DialogUtils.showNoticeDialog(this, dataDTO0.title, Html.fromHtml(dataDTO0.content), "我知道了", new DialogUtils.onDialogClickListener() {
                    @Override
                    public void onCancel(Dialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onOk(Dialog dialog) {

                    }
                });
            }
        }
    }

    private void getAudioShareLink() {
        NetUtil.get(ZConfig.SERVICE_URL + "/api/v1/Multimedia3/jump", new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                AudioShareLinkBean audioShareLinkBean = new Gson().fromJson(t, AudioShareLinkBean.class);
                // 修复：网络异常时 audioShareLinkBean 或 data 可能为 null，直接取必然 NPE
                if (audioShareLinkBean != null && audioShareLinkBean.data != null) {
                    AccountManager.getInstance().setAudioShareLink(audioShareLinkBean.data);
                }
            }
        });
    }


    private void initWelcomeInfo() {
        if (SystemUtils.isOnline(this)) {
            ThreadUtil.doOnOtherThread(new Runnable() {
                public void run() {
                    String json = NetConnectUtil.getWelContent(MainActivity.this, ZConfig.WELCOME_URL);
                    if (TextUtils.isEmpty(json) || json.equals("null")) {
                        return;
                    }
                    LogUtil.debug("启动页返回：" + json);
                    WelcomeResult welcomeResult = null;
                    try {
                        welcomeResult = JSONObject.parseObject(json, WelcomeResult.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (welcomeResult == null) {
                        return;
                    }
                    List<ShouYeBean> welcomeBeanList = welcomeResult.getData();
                    if (welcomeBeanList == null || welcomeBeanList.isEmpty()) {
                        return;
                    }
                    for (ShouYeBean shouYeBean : welcomeBeanList) {
                        //保存图片到本地
                        try {
                            File file = new File(SystemConstants.APP_PATH + shouYeBean.getPicFileName());
                            if (file.exists()) {
                                //已缓存，不用下载
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
                    SharedPreferencesUtils.saveWelcomeInfo(MainActivity.this, JSONObject.toJSONString(welcomeBeanList));
                }
            });
        }
    }


    /**
     * 重试5次 如果都不成功就放弃 用户体验更好些
     */
    private void initBookUpdateTask() {
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (initBookUpdateSuccess || initBookUpdateTimes <= 0) {
                    timer.cancel();
                    return;
                }
                initBookUpdateTimes = initBookUpdateTimes - 1;
                Log.d(TAG, "initBookUpdate now:" + initBookUpdateTimes);
                initBookUpdate(true);
            }
        }, 0, 3 * 1000); // 立即执行 每个3秒执行一次
    }

    private void initShare() {
        if (SystemUtils.isOnline(ATHIS)) {
            ThreadUtil.doOnOtherThread(new Runnable() {
                public void run() {
                    String json = NetConnectUtil.getContent(ATHIS, ZConfig.SHARE_SETTING_URL, 1);
                    if (StringUtil.isEmpty(json)) {
                        return;
                    }
                    try {
                        final ShareBean shareBean = JSONObject.parseObject(json, ShareBean.class);
                        if (shareBean != null && shareBean.getOpen().equals("true")) {//打开分享
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initShareDialog(shareBean);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private void uninstallPackage() {
        if (SystemUtils.isOnline(ATHIS)) {
            ThreadUtil.doOnOtherThread(new Runnable() {
                public void run() {
                    String json = NetConnectUtil.getContent(ATHIS, ZConfig.PACKAGE, 1);
                    if (StringUtil.isEmpty(json)) {
                        return;
                    }
                    try {
                        final PackageBean packageBean = JSONObject.parseObject(json, PackageBean.class);
                        // 修复：网络数据异常时 packageBean 或 getData() 可能为 null，防止 NPE
                        if (packageBean == null || packageBean.getData() == null) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uninstallPackages.clear();
                                curInstallIndex = -1;
                                for (PackageBean.DataBean datum : packageBean.getData()) {
                                    if (checkApplication(datum.getName())) {
                                        uninstallPackages.add(datum);
                                    }
                                }
                                uninstall();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    List<PackageBean.DataBean> uninstallPackages = new ArrayList<>();
    int curInstallIndex = -1;

    private void uninstall() {
        curInstallIndex++;
        if (uninstallPackages.size() == 0 || curInstallIndex >= uninstallPackages.size())
            return;
        uninstall(uninstallPackages.get(curInstallIndex));
    }


    private void uninstall(PackageBean.DataBean uninstallPackage) {
        DialogUtils.showCenterDialog(this, R.layout.dialog_uninstall_package, DensityUtil.dip2px(this, 320), -2, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                TextView textContent = view.findViewById(R.id.textContent);
//                String content = "用户您好：\n欢迎您使用我们的“研经工具”，为了节约您的手机内存空间，\n以便软件正常使用，我们建议您本手机卸载早期版本以及其他旧版本的软件\n因其软件，会占据您手机的空间/内存,有可能导致本软件功能无法正常使用，\n为此，我们建议您暂时卸载，如果真有需要，可在对应的网站进行下载和更新...\n为此带来不便，请谅解....";
//                SpannableString sStr = new SpannableString(content);
//                List<String> boldTxt = new ArrayList<>();
//                boldTxt.add("卸载早期版本");
//                boldTxt.add("旧版本的软件");
//                boldTxt.add("导致本软件功能无法正常使用");
//                boldTxt.add("建议您暂时卸载");
//                for (String s : boldTxt) {
//                    Pattern P = Pattern.compile(s);
//                    Matcher matcher = P.matcher(sStr);
//                    while (matcher.find()) {
//                        sStr.setSpan(new StyleSpan(BOLD), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    }
//                }
                textContent.setText(uninstallPackage.getNote());
                view.findViewById(R.id.txt_cancel).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        uninstall();
                    }
                });

                view.findViewById(R.id.txt_uninstall).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Uri packageURI = Uri.parse("package:".concat(uninstallPackage.getName()));
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(packageURI);
                        startActivityForResult(intent, 11001);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11001) {
            //卸载结果返回
            uninstall();
        }
    }

    /**
     * 卸载指定包名的应用
     */
    private boolean uninstall(List<String> uninstallPackages) {
        DialogUtils.showCenterDialog(this, R.layout.dialog_uninstall_package, DensityUtil.dip2px(this, 320), -2, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                TextView textContent = view.findViewById(R.id.textContent);
                String content = "用户您好：\n欢迎您使用我们的“研经工具”，为了节约您的手机内存空间，\n以便软件正常使用，我们建议您本手机卸载早期版本以及其他旧版本的软件\n因其软件，会占据您手机的空间/内存,有可能导致本软件功能无法正常使用，\n为此，我们建议您暂时卸载，如果真有需要，可在对应的网站进行下载和更新...\n为此带来不便，请谅解....";
                SpannableString sStr = new SpannableString(content);
                List<String> boldTxt = new ArrayList<>();
                boldTxt.add("卸载早期版本");
                boldTxt.add("旧版本的软件");
                boldTxt.add("导致本软件功能无法正常使用");
                boldTxt.add("建议您暂时卸载");
                for (String s : boldTxt) {
                    Pattern P = Pattern.compile(s);
                    Matcher matcher = P.matcher(sStr);
                    while (matcher.find()) {
                        sStr.setSpan(new StyleSpan(BOLD), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                textContent.setText(sStr);
                view.findViewById(R.id.txt_cancel).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                view.findViewById(R.id.txt_uninstall).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        for (String uninstallPackage : uninstallPackages) {
                            Uri packageURI = Uri.parse("package:".concat(uninstallPackage));
                            Intent intent = new Intent(Intent.ACTION_DELETE);
                            intent.setData(packageURI);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        return true;
    }

    /**
     * 判断该包名的应用是否安装
     *
     * @param packageName
     * @return
     */
    private boolean checkApplication(String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            getPackageManager().getApplicationInfo(packageName,
                    PackageManager.MATCH_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }


    ImageView logo;
    FrameLayout close;
    TextView title;
    TextView content;
    LinearLayout button1;
    LinearLayout button2;
    LinearLayout button3;
    CardView mCardView;

    private void initShareDialog(ShareBean shareBean) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_share, null);
        logo = (ImageView) view.findViewById(R.id.logo);
        title = (TextView) view.findViewById(R.id.title);
        content = (TextView) view.findViewById(R.id.content);
        button1 = (LinearLayout) view.findViewById(R.id.button1);
        button2 = (LinearLayout) view.findViewById(R.id.button2);
        button3 = (LinearLayout) view.findViewById(R.id.button3);
        close = (FrameLayout) view.findViewById(R.id.close);
        mCardView = (CardView) view.findViewById(R.id.cardview);

        if (StringUtil.isEmpty(shareBean.getTitle()) && StringUtil.isEmpty(shareBean.getBody())) {
            //标题和内容都为空
            title.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
        } else if (StringUtil.isEmpty(shareBean.getTitle())) {
            title.setVisibility(View.GONE);
            content.setText("\u3000\u3000" + shareBean.getBody());
            content.setMovementMethod(ScrollingMovementMethod.getInstance());
        } else if (StringUtil.isEmpty(shareBean.getBody())) {
            title.setText(shareBean.getTitle());
            content.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
            title.setText(shareBean.getTitle());
            content.setText("\u3000\u3000" + shareBean.getBody());
            content.setMovementMethod(ScrollingMovementMethod.getInstance());
        }

        if (StringUtil.isEmpty(shareBean.getImg_logo())) {
            logo.setVisibility(View.GONE);
        } else {
            PicassoUtils.loadImage(logo, shareBean.getImg_logo(), R.drawable.ic_launcher);
        }

        if ("1".equals(shareBean.getButton_WeChat_friend())) {
            button1.setVisibility(View.VISIBLE);
        } else {
            button1.setVisibility(View.GONE);
            view.findViewById(R.id.dex_line1).setVisibility(View.GONE);
        }
        if ("1".equals(shareBean.getButton_WeChat_friends())) {
            button2.setVisibility(View.VISIBLE);

        } else {
            button2.setVisibility(View.GONE);
            view.findViewById(R.id.dex_line1).setVisibility(View.GONE);
        }
        if ("1".equals(shareBean.getButton_web())) {
            button3.setVisibility(View.VISIBLE);
        } else {
            button3.setVisibility(View.GONE);
            view.findViewById(R.id.dex_line2).setVisibility(View.GONE);
        }
        foucs = "1".equals(shareBean.getClose());
        shareUrl = shareBean.getUrl();
        shareContent = shareBean.getFriends_content();
        wxMiniId = shareBean.getWx_mini_app_id();
        wxMiniPath = shareBean.getWx_mini_app_path();

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        close.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        shareDialog = builder.create();
        shareDialog.setCanceledOnTouchOutside(false);
        shareDialog.setCancelable(false);
        String[] week = shareBean.getWeek().split(",");
        for (String s : week) {
            if (StringUtil.isEmpty(s)) {
                s = "-1";
            }
            if (TimeUtils.getWeekDay() == Integer.valueOf(s)) {//
                shareDialog.show();
                ViewGroup.LayoutParams linearParams = (ViewGroup.LayoutParams) mCardView.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
                linearParams.height = getWindowManager().getDefaultDisplay().getHeight() / 3;
                mCardView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                SharedPreferencesUtils.saveShareReadTimeByDay(HuDongApplication.getInstance(), TimeUtils.getDate());
                break;
            }
        }
    }

    /**
     * 初始化书籍更新
     */
    private void initBookUpdate(final boolean isOnlySelect) {
        if (SystemUtils.isOnline(this)) {
            final Request request = new Request.Builder()
                    .url(ZConfig.BOOKLIBUPDATE)//请求的url
                    .get()
                    .build();
            HuDongApplication.getHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isOnlySelect) {
                                showMessage(ATHIS, "获取更新信息失败，连接服务器失败");// + "\nmessage:" + e.getMessage() + "\ncause:" + e.getCause());
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        initBookUpdateSuccess = true;
                        try {
                            BookResult result = JSONObject.parseObject(response.body().string(), BookResult.class);
                            Log.e("验证", "onResponse: " + (result.getPagess() == 1));
                            if (result != null && (result.getPagess() == 1)) {//成功
                                boolean update = false;
                                List<BookBean> list = result.getRows();
                                for (BookBean bookUpdateBean : list) {
                                    if (bookUpdateBean.getTypename().getName().contains("书库")) {
                                        String lastCode = SharedPreferencesUtils.getBooklibCode(HuDongApplication.getInstance());
                                        if (Long.valueOf(lastCode) < Long.valueOf(bookUpdateBean.getBookcode())) {//当服务器的序列号大于客户端的时候，提示更新
                                            update = true;
                                        }
                                    }
                                }
                                if (update && isOnlySelect) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            flBookUpdate.setVisibility(View.VISIBLE);
                                        }
                                    });
                                } else if (update && !isOnlySelect) {//说明需要更新
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            flBookUpdate.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    showUpdate(list);
                                } else if (!isOnlySelect) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            flBookUpdate.setVisibility(View.INVISIBLE);
                                            showMessage(ATHIS, "已是最新书籍");
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            flBookUpdate.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            } else {//失败
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isOnlySelect) {
                                            flBookUpdate.setVisibility(View.INVISIBLE);
                                            showMessage(ATHIS, "获取更新信息失败，连接服务器失败");
                                        }
                                    }
                                });

                            }
                        } catch (final Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isOnlySelect) {
                                        showMessage(ATHIS, "获取更新信息失败，连接服务器失败");// + "\nmessage:" + e.getMessage() + "\ncause:" + e.getCause());
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else {
            if (!isOnlySelect) {
                showMessage(ATHIS, "请连接网络");
            }
        }
    }

    private void showUpdate(final List<BookBean> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mDownloadBookDialog) {
                    String lastCode = SharedPreferencesUtils.getBooklibCode(HuDongApplication.getInstance());
                    String serviceCode = list.get(0).getBookcode();
                    mDownloadBookDialog = new CustomAlertDialog(MainActivity.this, "-1");
                    mDownloadBookDialog.setTitle(getString(R.string.txt_update_book));
                    mDownloadBookDialog.setMessage("当前版本" + lastCode + "\n可更新到" + serviceCode + "\n是否更新？");//R.string.txt_are_you_sure_to_update
                    mDownloadBookDialog.setPositiveButton(R.string.update, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDownloadBookDialog.dismiss();
                            if (!HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                                CommonUtil.showActivateDialog(ATHIS, UserInfo.VIP_NORMAL);
                                return;
                            }
                            updateDownload(list);
                        }
                    });
                    mDownloadBookDialog.setNegativeButton(R.string.cancel, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDownloadBookDialog.dismiss();
                        }
                    });
                }
                mDownloadBookDialog.show();
            }
        });
    }

    private void updateDownload(List<BookBean> list) {
        final WelcomeProgressDialog dialog = new WelcomeProgressDialog(this, "下载中");
        dialog.show();
        Window mWindow = dialog.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        dialog.setMaxProgress(100);
        //  downLoadItem.setBookName(jsonItem.getString("name"));
        mDownLoadItem.setBookName(list.get(0).getTypename().getName());
        mDownLoadItem.setSize("0");
        mDownLoadItem.setTitle(list.get(0).getTypename().getName());
        mDownLoadItem.setCategory("name");
        mDownLoadItem.setType(list.get(0).getTypename().getName());
        mDownLoadItem.setCount(1);
        mDownLoadItem.setTime(String.valueOf(list.get(0).getCreate_time()));
        mDownLoadItem.setBookcode(list.get(0).getBookcode());
        mDownLoadItem.setSizeValue(0);
        mDownLoadItem.setProgressBar(dialog.getProgressBar());
        mDownLoadItem.setIntroduction("暂无简介");
        mDownLoadItem.setUrl(list.get(0).getFile());


        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                final DownloadQueue downloadQueue = new DownloadQueue(mDownLoadItem);
                downloadQueue.setType(0);
                try {
                    boolean isSuccess = downloadQueue.downloadFiles(ATHIS, ATHIS, dialog.getTvTitle());
                    if (!isSuccess) {
                        mDownLoadItem.setState(1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                        return;
                    }
                    SharedPreferencesUtils.saveBooklibCode(HuDongApplication.getInstance(), mDownLoadItem.getBookcode());
                    SharedPreferencesUtils.saveBooklibName(HuDongApplication.getInstance(), mDownLoadItem.getBookName());
                    mDownLoadItem.setState(1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SystemConstants.isUpdateBook = true;
                            final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });

                } catch (NotOnlineException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            showMessage(ATHIS, "与服务器连接不正确");
                        }
                    });
                }
            }
        });
    }

    private int mLastX;
    private int mLastY;
    private boolean isLongClick = false;

    protected void initActionBar() {
        mUUid = AccountManager.getInstance().getUserInfo().getUsername();
        tvVersionName = findViewById(R.id.tv_version_name);
        tvVersionName.setText("V" + SystemUtils.getVersionName(this));
        iv_recharge_home = findViewById(R.id.iv_recharge_home);
        flBookUpdate = findViewById(R.id.fl_book_update);
        flBookUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //查询升级接口
                initBookUpdate(false);
            }
        });
        RxView.clicks(iv_recharge_home)
                .throttleFirst(1, TimeUnit.SECONDS)//在一秒内只取第一次点击
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        startActivity(new Intent(MainActivity.this, NewRechargeActivity.class));
                    }
                });

        touch_view = findViewById(R.id.touch_view);
        fl_service = findViewById(R.id.fl_service);
        view_unread = findViewById(R.id.view_unread);
        touch_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideInput();
                return false;
            }
        });
        boolean isServiceOpen = PreferencesUtils.getBoolean(MainActivity.this, "service_is_open", true);
        fl_service.setVisibility(isServiceOpen ? View.VISIBLE : View.GONE);

        if (fl_service.getVisibility() == View.VISIBLE) {
            setLocation();
        }

        fl_service.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://book.sdacn.cn/addons/kefu/index/mobile?fixed_csr=0&token=" + AccountManager.getInstance().getUserInfo().getToken();
                WebViewActivity.launchAct(MainActivity.this, "联系客服", url);
            }
        });

        screenWidth = DensityUtil.getScreenWidth(this);
        screenHeight = DensityUtil.getScreenHeight(this);
        fl_service.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                int offsetX = 0;
                int offsetY = 0;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastX = x;
                        mLastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isLongClick) {
                            return false;
                        }

                        offsetX = x - mLastX;
                        offsetY = y - mLastY;

                        float resultX = v.getX() + offsetX;
                        if (resultX < 0 || resultX > screenWidth - viewWidth) {
                            return false;
                        }

                        float resultY = v.getY() + offsetY;
                        if (resultY < 80 || resultY > screenHeight - viewHeight) {
                            return false;
                        }
                        v.setX(resultX);
                        v.setY(resultY);
                        //重新设置初始坐标
                        mLastX = x;
                        mLastY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        isLongClick = false;
                        touch_view.setBackgroundColor(Color.TRANSPARENT);
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        PreferencesUtils.putInt(MainActivity.this, "service_position_x", (int) v.getX());
                        PreferencesUtils.putInt(MainActivity.this, "service_position_y", (int) v.getY());
                        break;
                }
                //如果是拖拽则消耗事件，否则正常传递即可。
                return false;
            }
        });

        fl_service.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongClick = true;
                touch_view.setBackgroundColor(Color.parseColor("#77000000"));
                v.animate().scaleX(1.5f).scaleY(1.5f).setDuration(100).start();
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(200);
                return true;
            }
        });

        iv_left = (ImageView) findViewById(R.id.btn_img_left);
        iv_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (pageIndex == 3) {
//                    if (NetUtil.isNetWorkAvailable(MainActivity.this)) {
//                        getSignInfo(true);
//                    } else {
//                        showToast("请链接网络~");
//                    }
//                    return;
//                }
                if (!AccountManager.getInstance().levelAvailable(UserInfo.VIP_NORMAL)) {
                    CommonUtil.showActivateDialog(MainActivity.this, UserInfo.VIP_NORMAL);
                    return;
                }
                ActivityUtil.next(ATHIS, UserBookInfoActivity.class);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
        iv_back = (ImageView) findViewById(R.id.btn_back);
        iv_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (pageIndex == 4) {
//                    Intent intent = new Intent(MainActivity.this, CollectAudioListActivity.class);
//                    startActivity(intent);
//                    return;
//                } else
//                if (pageIndex == 3) {
//                    WebViewActivity.launchAct(MainActivity.this, "问答规则", "https://book.sdacn.cn/wenda/index.html");
//                    return;
//                }
                AccountManager.getInstance().refreshUserInfo(null);
                ActivityUtil.next(ATHIS, ActiveActivity.class);
            }
        });
        iv_search = findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (pageIndex == 4) {
//                    if (NetUtil.isNetWorkAvailable(MainActivity.this)) {
//                        Intent intent = new Intent(MainActivity.this, SearchAudioActivity.class);
//                        startActivity(intent);
//                    } else {
//                        showToast("对不起，暂无网络");
//                    }
//                    return;
//                }
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("keyword", "");
                intent.putExtra("type", mSearchType);
                intent.putExtra("range", mSearchRange);
                intent.putExtra("searchNode", mSearchNode);
                intent.putExtra("search", "yes");
                startActivity(intent);
            }
        });
    }

    private void setLocation() {
        int x = PreferencesUtils.getInt(MainActivity.this, "service_position_x", -1);
        int y = PreferencesUtils.getInt(MainActivity.this, "service_position_y", -1);
        if (x != -1 && y != -1) {
            fl_service.post(new Runnable() {
                @Override
                public void run() {
                    viewWidth = fl_service.getWidth();
                    viewHeight = fl_service.getHeight();
                    fl_service.setX(x);
                    fl_service.setY(y);
                }
            });
        }
    }

    public static final String PAY_TYPE_ALIPAY = "alipay";
    public static final String PAY_TYPE_WEICHAT = "wxpay";

    private void showRechargeDialog() {
        DialogUtils.showCenterDialog(MainActivity.this, R.layout.dialog_recharge, DensityUtil.dip2px(MainActivity.this, 320), -2, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                EditText tvEditText = view.findViewById(R.id.et_donate_number);
                TextView tv_confirm_pay = view.findViewById(R.id.tv_confirm_pay);
                CheckBox rb_wx_pay = view.findViewById(R.id.rb_wx_pay);
                CheckBox rb_ali_pay = view.findViewById(R.id.rb_ali_pay);
                RecyclerView rcv_list = view.findViewById(R.id.rcv_list);
                rcv_list.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
                RechargeListAdapter rechargeListAdapter = new RechargeListAdapter();
                rcv_list.setAdapter(rechargeListAdapter);
                rechargeListAdapter.setOnItemClickListener(new RechargeListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ArrayList<RechargeBean> rechargeBeans, int position) {
                        if (lastIndex != -1) {
                            rechargeBeans.get(lastIndex).isSelected = false;
                        }
                        rechargeBeans.get(position).isSelected = true;
                        lastIndex = position;
                        rechargeListAdapter.notifyDataSetChanged();
                        tvEditText.setText(rechargeBeans.get(position).price);
                        tvEditText.setSelection(tvEditText.length());
                    }
                });
                tvEditText.setText(rechargeListAdapter.getRechargeBeans().get(0).price);
                tvEditText.setSelection(tvEditText.length());
                rb_wx_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        rb_wx_pay.setChecked(isChecked);
                        if (isChecked) {
                            rb_ali_pay.setChecked(false);
                        }
                    }
                });
                rb_ali_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        rb_ali_pay.setChecked(isChecked);
                        if (isChecked) {
                            rb_wx_pay.setChecked(false);
                        }
                    }
                });
                view.findViewById(R.id.tv_confirm_pay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String payType = PAY_TYPE_WEICHAT;
                        if (rb_wx_pay.isChecked()) {
                            payType = PAY_TYPE_WEICHAT;
                        } else if (rb_ali_pay.isChecked()) {
                            payType = PAY_TYPE_ALIPAY;
                        } else {
                            showMessage("请选择支付方式!");
                            return;
                        }
                        if (TextUtils.isEmpty(tvEditText.getText().toString().trim())) {
                            showMessage(MainActivity.this, "金额不能为空");
                        } else if (CommonUtil.formatMoney(tvEditText.getText().toString().trim(), 2).equals("￥0.00")) {
                            showMessage(MainActivity.this, "亲，真的捐0元吗？请您慷慨解囊...");
                        } else if (Float.valueOf(tvEditText.getText().toString().trim()) > 999999.99) {
                            showMessage(MainActivity.this, "对不起，单次超过捐款限额！");
                        } else {
                            if (SystemConstants.isActive) {
                                return;
                            }
                            SystemConstants.isActive = true;//正在激活
                            donation(payType, "0", tvEditText.getText().toString().trim());
                        }
                    }
                });
            }
        });
    }

    protected void showMessage(Context activity, String str) {
        if (activity != null && !TextUtils.isEmpty(str)) {
            XToast.showToast(activity, str);
        }
    }

    protected void showMessage(String str) {
        if (!TextUtils.isEmpty(str)) {
            XToast.showToast(this, str);
        }
    }

    protected void showToast(String str) {
        if (!TextUtils.isEmpty(str)) {
            XToast.showToast(this, str);
        }
    }

    private String apliy_fee;
    private String days;
    private String levelType;
    private String mUUid;

    /**
     * 无偿捐赠
     */
    private void donation(final String payType, String days, String money) {
        if (AccountManager.getInstance().getUserInfo() == null) {
            return;
        }
        this.apliy_fee = money;
        this.days = days;
        this.levelType = "";
        if (payType.equals(PAY_TYPE_WEICHAT)) {
            //赋值
            SystemConstants.WX_DAYS = days;
            SystemConstants.WX_MONEY = money;
            SystemConstants.WX_UUID = mUUid;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("money", money);
        params.put("paytype", payType);
        params.put("token", AccountManager.getInstance().getUserInfo().getToken());
        OkHttpUtils.getInstance().post(ZConfig.DONATION, params, new HttpCallback<RespInfo<HashMap<String, String>>>() {
            @Override
            public void onSuccess(final RespInfo<HashMap<String, String>> data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (payType) {
                            case "alipay":
                                String aliJson = data.getData().get("json");
                                payV2ByNet(aliJson);
                                break;
                            case "wxpay":
                                String orderInfoJson = GsonUtils.objectToStr(data);
                                final WxpayBean wxpayBean = JSON.parseObject(orderInfoJson, WxpayBean.class);
                                WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
                                builder.setAppId(wxpayBean.getData().getAppid())
                                        .setPartnerId(wxpayBean.getData().getPartnerid())
                                        .setPrepayId(wxpayBean.getData().getPrepayid())
                                        .setPackageValue("Sign=WXPay")
                                        .setNonceStr(wxpayBean.getData().getNoncestr())
                                        .setSign(wxpayBean.getData().getSign())
                                        .setTimeStamp(wxpayBean.getData().getTimestamp())
                                        .build()
                                        .toWXPayNotSign(MainActivity.this);

                                SystemConstants.isActive = false;
                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(int code, final String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SystemConstants.isActive = false;
                        showMessage(getApplicationContext(), errorMsg);
                    }
                });
            }

            @Override
            public void onFinish() {

            }
        });
    }


    /**
     * 支付宝支付业务
     */
    public void payV2ByNet(final String orderInfo) {
        SystemConstants.isActive = false;
        if (TextUtils.isEmpty(PayUtil.APPID) || TextUtils.isEmpty(PayUtil.RSA_PRIVATE)) {
            new AlertDialog.Builder(MainActivity.this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //

                        }
                    }).show();
            return;
        }
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(MainActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                LogUtil.info("msp", result.toString());

                Message msg = new Message();
                msg.what = PayUtil.SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PayUtil.SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    String momo = payResult.getMemo();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {//支付成功
                        PreferenceConfig.savePayMoney(MainActivity.this, Float.valueOf(apliy_fee));
                        showToast("支付成功");
                        //重新获取激活时间
//                        ThreadUtil.doOnOtherThread(new Runnable() {
//                            @Override
//                            public void run() {
                        AccountManager.getInstance().refreshUserInfo(new AccountManager.IAccountManagerListener() {
                            @Override
                            public void requestResult(boolean isSuccess, String errMsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(MainActivity.this, PayResultActivity.class);
                                        intent.putExtra("result", true);
                                        intent.putExtra("days", days);
                                        intent.putExtra("uuid", mUUid);
                                        intent.putExtra("money", apliy_fee);
                                        intent.putExtra("levelType", levelType);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
//                            }
//                        });
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showToastMsg("支付失败，" + momo);
                        Intent intent = new Intent(MainActivity.this, PayResultActivity.class);
                        intent.putExtra("result", false);
                        intent.putExtra("days", days);
                        intent.putExtra("uuid", mUUid);
                        intent.putExtra("money", apliy_fee);
                        intent.putExtra("levelType", levelType);
                        startActivity(intent);
                    }
                    break;
                }
                default:
                    Intent intent = new Intent(MainActivity.this, PayResultActivity.class);
                    intent.putExtra("result", false);
                    intent.putExtra("days", days);
                    intent.putExtra("uuid", mUUid);
                    intent.putExtra("money", apliy_fee);
                    intent.putExtra("levelType", levelType);
                    startActivity(intent);
                    break;
            }
        }
    };


    protected void initViews() {
        FrameLayout videoFullContainer = (FrameLayout) findViewById(R.id.video_full_container);
        fl_view = findViewById(R.id.fl_view);

        include_notice = findViewById(R.id.include_notice);
        tv_notice_title = findViewById(R.id.tv_notice_title);
        tv_notice_content = findViewById(R.id.tv_notice_content);
        iv_notice_close = findViewById(R.id.iv_notice_close);
        include_notice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = tv_notice_content.getVisibility();
                if (visibility == View.GONE) {
                    tv_notice_title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_notice_tips, 0, R.drawable.icon_show_down, 0);
                    tv_notice_content.setVisibility(View.VISIBLE);
                } else {
                    tv_notice_title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_notice_tips, 0, R.drawable.icon_show_up, 0);
                    tv_notice_content.setVisibility(View.GONE);
                }
            }
        });

        iv_notice_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                include_notice.setVisibility(View.GONE);
            }
        });
        homeFragment = HomeFragment.newInstance();
        homeFragment.setVideoFullContainer(videoFullContainer);
//        spiritualityListFragment = SpiritualityListFragment.newInstance();
        final List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(homeFragment);
//        fragments.add(spiritualityListFragment);
//        fragments.add(new BaikeFragment());
//        fragments.add(new QAFragment());
        fragments.add(new NullFragment());
        fragments.add(new NullFragment());
//        fragments.add(new StoreFragment());
        fragments.add(new NullFragment());

        mAdapter = new FragmentViewPagerAdapterNew(getSupportFragmentManager(), fragments, Arrays.asList(FRAGMENT_LABLE));
        mPager = findViewById(R.id.pager);
        // 设置viewpager内部页面之间的间距
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.common_margin_8));
        // 设置viewpager内部页面间距的drawable
        mPager.setPageMarginDrawable(R.color.gray_f5f5f5);
        mPager.setAdapter(mAdapter);
        mPager.setCanScroll(false);
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(fragments.size());
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setTextColor(getResources().getColor(R.color.white_alph70));
        mIndicator.setViewPager(mPager);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                pageIndex = i;
                if ((i == 2 || i == 1 || i == 3 || i == 4 || i == 5) && !HuDongApplication.getInstance().isAppNormalLevelActivate()) {
                    CommonUtil.showActivateDialogWithCancelAction(ATHIS, UserInfo.VIP_NORMAL, new Runnable() {
                        public void run() {
                            mPager.setCurrentItem(0);
                        }
                    });
                    return;
                }

                if (i == 1) {
                    if (assetsStatusShow) {
                        if (Utils.isInvalidClick(mIndicator, 500)) {
                            mPager.setCurrentItem(lastIndex);
                            return;
                        }
                        Intent intent = new Intent(MainActivity.this, NewAudioActivity.class);
                        try {
                            PreferencesUtils.putObject(MainActivity.this, NewAudioActivity.AUDIO_CACHE_DATA, cacheAudioInfo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        intent.putExtra(NewAudioActivity.AUDIO_CACHE_DATA, cacheAudioInfo);
                        intent.putExtra(NewAudioActivity.AUDIO_IN_CATE_INDEX, inCateIndex);
                        intent.putExtra(NewAudioActivity.AUDIO_IS_PLAYED, audioIsPlayed);
                        startActivity(intent);
                        mPager.setCurrentItem(lastIndex);
                    }
                    return;
                }
                if (i == 2) {
                    if (assetsStatusShow) {
                        if (Utils.isInvalidClick(mIndicator, 500)) {
                            mPager.setCurrentItem(lastIndex);
                            return;
                        }
                        startActivity(new Intent(MainActivity.this, VideoMainActivity.class));
                        mPager.setCurrentItem(lastIndex);
                    }
                    return;
                }
//                if (i == 4) {
                if (i == 3) {
                    mPager.setCurrentItem(lastIndex);
                    showPopWindow();
                    return;
                } else {
                    iv_left.setImageResource(R.drawable.ic_foot);
                    iv_left.setVisibility(View.VISIBLE);
                    iv_back.setImageResource(R.drawable.ic_setting);
                }
                lastIndex = pageIndex;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        if (!guideViewIsShow()) {
            fl_service.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showGuideView();
                }
            }, 1000);
        }
    }

    private void showPopWindow() {
        View popView = getLayoutInflater().inflate(R.layout.pop_event_layout, null);
        TextView tv_title_1 = popView.findViewById(R.id.tv_title_1);
        TextView tv_title_2 = popView.findViewById(R.id.tv_title_2);
        TextView tv_title_3 = popView.findViewById(R.id.tv_title_3);
        int width = DensityUtil.dip2px(this, 75);
        PopupWindow popupWindow = new PopupWindow(popView, width, -2, true);
        tv_title_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ActivityUtil.next(MainActivity.this, LingXiuActivity.class);
            }
        });
        tv_title_2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ActivityUtil.next(MainActivity.this, BaiKe1Activity.class);
            }
        });
        tv_title_3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ActivityUtil.next(MainActivity.this, QuestionActivity.class);
            }
        });

        int[] location = new int[2];
        mIndicator.getLocationOnScreen(location);
        popupWindow.showAsDropDown(mIndicator, -DensityUtil.dip2px(this, 5), 0, Gravity.RIGHT);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.6f; //0.0-1.0
        getWindow().setAttributes(attributes);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams attributes = getWindow().getAttributes();
                attributes.alpha = 1.0f; //0.0-1.0
                getWindow().setAttributes(attributes);
            }
        });

    }

    //是否已经显示引导
    private boolean guideViewIsShow() {
        return PreferencesUtils.getBoolean(this, "is_guide_show", false);
    }

    public void showGuideView() {
        PreferencesUtils.putBoolean(this, "is_guide_show", true);
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(fl_service)
                .setAlpha(100)
                .setHighTargetCorner(20)
                .setHighTargetPadding(5);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {

            }
        });

        builder.addComponent(new SimpleComponent());
        Guide guide = builder.createGuide();
        guide.show(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (SystemConstants.isUpdateBook){
//            initAgain();
//        }
        hideInput();

        //判断登录
        if (!AccountManager.getInstance().isLogin()) {
            WeixinLoginActivity.launchAct(this);
            finish();
            return;
        }
        getUnreadMsg();
        boolean isServiceOpen = PreferencesUtils.getBoolean(MainActivity.this, "service_is_open", true);
        fl_service.setVisibility(isServiceOpen ? View.VISIBLE : View.GONE);
        if (fl_service.getVisibility() == View.VISIBLE) {
            setLocation();
        }
    }

    private void getUnreadMsg() {
        Map<String, String> map = new HashMap<>();
        map.put("token", AccountManager.getInstance().getUserInfo().getToken());
        NetUtil.get("https://book.sdacn.cn/api/kefu/getUnreadMessagesCount", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                try {
                    NoticeMsgBean noticeMsgBean = new Gson().fromJson(t, NoticeMsgBean.class);
                    if (noticeMsgBean != null) {
                        view_unread.setVisibility(noticeMsgBean.data == 0 ? View.GONE : View.VISIBLE);
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    public int getScollY() {
        int scollY = 0;
        switch (pageIndex) {
            case 0:
                int[] location = new int[2];
                mIndicator.getLocationInWindow(location);
                scollY = homeFragment.getScollY();
                return scollY + location[1];
            case 1:
//                return spiritualityListFragment.getScollY();
            default:
                break;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 显示退出提示对话框
     */
    private void showExitAlertDialog() {
        if (null == mCommonAlertDialog && !this.isDestroyed()) {
            mCommonAlertDialog = new CustomAlertDialog(this, "-1");
            mCommonAlertDialog.setTitle(getString(R.string.txt_quit_app));
            mCommonAlertDialog.setMessage(getString(R.string.txt_are_you_sure_to_quit));
            mCommonAlertDialog.setPositiveButton(R.string.ensure, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCommonAlertDialog.dismiss();
                    finish();
                    exit();
                }
            });
            mCommonAlertDialog.setNegativeButton(R.string.cancel, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCommonAlertDialog.dismiss();
                }
            });
        }
        if (!MainActivity.this.isDestroyed()) {
            mCommonAlertDialog.show();
        }
    }


    public void update(final boolean isInit) {
        if (SystemUtils.isOnline(ATHIS)) {
            ThreadUtil.doOnOtherThread(new Runnable() {
                public void run() {
                    final String oldVersion = SystemUtils.getVersionName(ATHIS);
                    String json = NetConnectUtil.getContent(ATHIS, ZConfig.UPDATE, 3);
                    if (StringUtil.isEmpty(json)) {
                        return;
                    }
                    final JSONObject jsonObject = JSON.parseObject(json);
                    if (jsonObject != null && !jsonObject.containsKey("data")) {
                        return;
                    }
                    final JSONObject dataJson = JSON.parseObject(JSON.toJSONString(jsonObject.get("data")));
                    if (dataJson == null) {
                        return;
                    }
                    if (Float.valueOf(oldVersion) > Float.valueOf(dataJson.getString("version_code")))
                        return;
                    if (!oldVersion.equals(dataJson.getString("version_code"))) {//需要更新
                        if (dataJson.getString("is_update").equals("1")) {
                            SharedPreferencesUtils.saveUpdateVersionName(HuDongApplication.getInstance(), dataJson.getString("version_code"));//保存版本号
                            SharedPreferencesUtils.saveUpdateForce(HuDongApplication.getInstance(), true);//需要强制更新
                            SharedPreferencesUtils.saveUpdateVersionContent(HuDongApplication.getInstance(), dataJson.getString("remark"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (null == appUpdateDialog && !MainActivity.this.isDestroyed()) {
                                        appUpdateDialog = new CustomAlertDialog(MainActivity.this, dataJson.getString("web_url"));
                                        appUpdateDialog.setCancelable(false);
                                        appUpdateDialog.setTitle("程序升级");
                                        appUpdateDialog.setMessage("当前：" + oldVersion + "----> " + dataJson.getString("version_code") + "\n\n" + Html.fromHtml(dataJson.getString("remark")));
                                        appUpdateDialog.setPositiveButton("现在更新", new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                appUpdateDialog.dismiss();
                                                ThreadUtil.doOnOtherThread(new Runnable() {
                                                    public void run() {
                                                        try {
                                                            DownloadUtils.downloadApk(MainActivity.this, dataJson.getString("dow_url"));
                                                        } catch (NotOnlineException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        appUpdateDialog.setNegativeButton("", new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                appUpdateDialog.dismiss();
                                            }
                                        });
                                    }
                                    if (appUpdateDialog != null && !appUpdateDialog.isShowing()) {
                                        appUpdateDialog.show();
                                    }

                                }
                            });
                        } else if (dataJson.getString("is_update").equals("0")) {
                            //判断今天是不是首次弹
                            PreferencesUtils.getString(HuDongApplication.getInstance(), "is_update").equals("0");
                            SharedPreferencesUtils.saveUpdateForce(HuDongApplication.getInstance(), false);//需要强制更新
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isInit || (isInit && !SharedPreferencesUtils.getShowUpdateVersionDate(HuDongApplication.getInstance()).equals(TimeUtils.getDate()))) {//如果不是今天//
                                        //一天只展示一次
                                        SharedPreferencesUtils.saveShowUpdateVersionDate(HuDongApplication.getInstance(), TimeUtils.getDate());
                                        if (null == appUpdateDialog && !MainActivity.this.isDestroyed()) {
                                            appUpdateDialog = new CustomAlertDialog(MainActivity.this, dataJson.getString("web_url"));
                                            appUpdateDialog.setCancelable(false);
                                            appUpdateDialog.setTitle("程序升级");
                                            appUpdateDialog.setMessage("当前：" + oldVersion + "----> " + dataJson.getString("version_code") + "\n\n" + Html.fromHtml(dataJson.getString("remark")));
                                            String confrim = "现在更新";
                                            appUpdateDialog.setPositiveButton(confrim, new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    appUpdateDialog.dismiss();
                                                    ThreadUtil.doOnOtherThread(new Runnable() {
                                                        public void run() {
                                                            try {
                                                                DownloadUtils.downloadApk(MainActivity.this, dataJson.getString("dow_url"));
                                                            } catch (NotOnlineException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                            appUpdateDialog.setNegativeButton("明天提醒", new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    appUpdateDialog.dismiss();
                                                }
                                            });
                                        }
                                        if (appUpdateDialog != null && !appUpdateDialog.isShowing()) {
                                            appUpdateDialog.show();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else {
            final String oldVersion = SystemUtils.getVersionName(HuDongApplication.getInstance());
            if (!SharedPreferencesUtils.getUpdateVersionName(HuDongApplication.getInstance()).equals(oldVersion) && SharedPreferencesUtils.getUpdateForce(HuDongApplication.getInstance())) {//版本号不同
                if (null == appUpdateDialog && !MainActivity.this.isDestroyed()) {
                    appUpdateDialog = new CustomAlertDialog(MainActivity.this, "-1");
                    appUpdateDialog.setCancelable(false);
                    appUpdateDialog.setTitle("程序升级");
                    appUpdateDialog.setMessage("对不起，请链接网络后更新APP\n本次更新内容：\n" + SharedPreferencesUtils.getUpdateVersionContent(HuDongApplication.getInstance()));
                    appUpdateDialog.setPositiveButton("确定", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appUpdateDialog.dismiss();
                            HuDongApplication.getInstance().exitApp();
                        }
                    });
                }
            }
        }
    }


    public GSYVideoHelper smallVideoHelper;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (pageIndex == 0 && smallVideoHelper != null) {
                if (smallVideoHelper.isFull()) {
                    smallVideoHelper.backFromFull();
                } else {
                    showExitAlertDialog();
                }
            } else {
                showExitAlertDialog();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
            case R.id.button2:
                String strPackageName = "";
                String strActivityName = "";
                switch (view.getId()) {
                    case R.id.button1:
                        strPackageName = "com.tencent.mm";
                        strActivityName = "com.tencent.mm.ui.tools.ShareImgUI";
                        break;
                    case R.id.button2:
                        strPackageName = "com.tencent.mm";
                        strActivityName = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
                        break;
                }
                if (strPackageName != null) {
                    String strUrl = shareUrl;
                    String strContent = shareContent;
                    if (!NetSocietyShare.share(this, strPackageName, strActivityName, strUrl,
                            strContent, "研经工具")) {
                        showMessage(this, "未安装此应用");
                    }
                }
                foucs = false;
                break;
            case R.id.button3:
                if (StringUtil.isEmpty(wxMiniId)) {
                    showMessage(this, "暂无可打开信息");
                    return;
                }
                strPackageName = "com.tencent.mm";
                if (!NetSocietyShare.goWechatMini(this, strPackageName, wxMiniId, wxMiniPath)) {
                    showMessage(this, "未安装此应用");
                }
                foucs = false;
                break;
            case R.id.close:
                if (shareDialog != null && !foucs) {
                    shareDialog.dismiss();
                } else {
                    showMessage(ATHIS, "亲，这么好的软件，帮忙宣传一下，再关闭吧...");
                }
                break;
        }
    }

    @Override
    public void setRange(int range) {
        mSearchRange = range;
    }

    @Override
    public void setNode(Category node) {
        mSearchNode = node;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 修复：在 onDestroy 主动取消 Timer，防止 TimerTask 内匿名类持有 Activity 导致泄漏
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        musicPlayerManager.removePlayerListener(musicPlayerEventListener);
        musicPlayerManager.removePlayInfoListener(musicPlayerInfoListener);
        musicPlayerManager.unInitialize(this, true);
        fl_view.onActivityDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateUI(LoginOutEvent loginOutEvent) {
        if (loginOutEvent.isTokenInvalid()) {
            if (!StringUtil.isEmpty(loginOutEvent.getErrMsg())) {
                showToast(loginOutEvent.getErrMsg());
            }
        }
        WeixinLoginActivity.launchAct(this);
        this.finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(ReStartEvent loginOutEvent) {
        this.finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(DownloadAudioEvent downloadAudioEvent) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reStart(String tag) {
        switch (tag) {
            case "audio_chapter_no_cache":
                if (fl_view != null) {
                    fl_view.stopAnimation();
                }
                break;
            case "service_is_open":
                fl_service.setVisibility(View.VISIBLE);
                setLocation();
                break;
            case "service_is_hide":
                fl_service.setVisibility(View.GONE);
                break;
            case "audio_chapter_url_error":
                fl_view.hideLoading();
                break;
            case "on_audio_stop":
                if (fl_view != null) {
                    fl_view.hide();
                }
                break;
        }
    }

    @Override
    public void netStatusChange(NetObserver.NetAction action) {
        super.netStatusChange(action);
        if (action.isAvailable()) {
            //网络可用
            AccountManager.getInstance().refreshUserInfo(null);
        } else {
        }
    }

    public void checkUserInfo() {
        if (!guideViewIsShow()) {
            return;
        }
        //判断是否登录，登录了刷新用户信息
        AccountManager.getInstance().refreshUserInfo(new AccountManager.IAccountManagerListener() {
            @Override
            public void requestResult(boolean isSuccess, String errorMsg) {
                UserInfo userInfo = AccountManager.getInstance().getUserInfo();
                List<UserNetInfo.LevelBean> level1 = userInfo.getLevel();
                if (level1 != null && !level1.isEmpty()) {
                    boolean isShow = PreferencesUtils.getBoolean(MainActivity.this, "VIP_END_" + TimeUtils.getDateTag(), false);
                    if (isShow) {
                        return;
                    }
                    UserNetInfo.LevelBean levelBean = level1.get(0);
                    if (TextUtils.equals("v0", levelBean.getVal())) {//非会员
                        free(userInfo);
                        return;
                    }
                    formatVip(level1);
                } else if (userInfo.isFree()) {
                    free(userInfo);
                }
            }
        });
    }

    int rechargeType = -1;

    private void formatVip(List<UserNetInfo.LevelBean> level1) {
        for (UserNetInfo.LevelBean item : level1) {
            long expire = TimeUtils.getTime(item.getTime()) - 10000;//获取到期时间戳
            //获取今天的时间戳
            long now = TimeUtils.getNowStamp();
            if (expire <= now) {//已经到期了，跳过
                continue;
            }
            //三天时间戳
            long threeDaysTime = 3 * 24 * 60 * 60;
            if (expire - now < threeDaysTime) {//快到期
                switch (item.getVal()) {
                    case "v1":
                        rechargeType = 0;
                        break;
                    case "v2":
                        rechargeType = 1;
                        break;
                    case "v3":
                        rechargeType = 2;
                        break;
                }
            }
        }
        if (rechargeType != -1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showVipEndDialog(rechargeType);
                }
            });
        }
    }

    private void free(UserInfo userInfo) {
        if (!userInfo.isFree()) {
            return;
        }
        boolean isShow = PreferencesUtils.getBoolean(MainActivity.this, "FREE_END_" + TimeUtils.getDateTag(), false);
        if (isShow) {
            return;
        }
        //获取到期时间
        long freeActiveTimeEnd = PreferenceConfig.getFreeActiveTimeEnd(HuDongApplication.getInstance());
        if (freeActiveTimeEnd == 0) {
            return;
        }
        //获取今天的时间戳
        long now = TimeUtils.getNowStamp();
        //三天时间戳
        long threeDaysTime = 3 * 24 * 60 * 60;
        if (freeActiveTimeEnd - now < threeDaysTime) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showFreeEndDialog();
                }
            });
        }
    }

    //续期提示
    //肢体您好，我们软件每月需要运营支出，我们迫切的，希望您能在资金上进行应当的支持，若有感动，可以额外的无偿的资金支持，而这个费用，是支持圣工的重要途经！
    private void showVipEndDialog(int rechargeType) {
        PreferencesUtils.putBoolean(this, "VIP_END_" + TimeUtils.getDateTag(), true);
        DialogUtils.showNormalDialog(this, "会员续费提醒", "肢体您好，我们软件每月需要运营支出，我们迫切的，希望您能在资金上进行应当的支持，若有感动，可以额外的无偿的资金支持，而这个费用，是支持圣工的重要途经！", "取消", "续费", new DialogUtils.onDialogClickListener() {
            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onOk(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, ActiveActivity.class);
                intent.putExtra("vip_recharge_type", rechargeType);
                startActivity(intent);
            }
        });
    }

    private void showFreeEndDialog() {
        PreferencesUtils.putBoolean(this, "FREE_END_" + TimeUtils.getDateTag(), true);
        DialogUtils.showFreeEndDialog(this, "免费试用到期", "近期试用是否顺利？相信您在使用的过程中用的习惯，软件真心需要长期的费用作为支持，希望您能理解，当试用期到了的时候，希望您在资金上能支持我们!非常非常感谢！", "继续使用", "圣工奉献", new DialogUtils.onDialogClickListener() {
            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onOk(Dialog dialog) {
                dialog.dismiss();
                ActivityUtil.next(MainActivity.this, NewRechargeActivity.class);
            }
        });
    }
}
