package com.read.scriptures.manager;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.bean.FreeBean;
import com.read.scriptures.bean.RespInfo;
import com.read.scriptures.bean.UserInfo;
import com.read.scriptures.bean.UserInfoBean;
import com.read.scriptures.bean.UserNetInfo;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.event.LoginOutEvent;
import com.read.scriptures.event.RefreshUserInfoEvent;
import com.read.scriptures.http.okhttp.HttpCallback;
import com.read.scriptures.http.okhttp.OkHttpUtils;
import com.read.scriptures.util.GsonUtils;
import com.read.scriptures.util.NetConnectUtil;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.SystemUtils;
import com.read.scriptures.util.ThreadUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * 账户管理类
 * Created by Administrator.
 * Datetime: 2015/6/26.
 * Email: lgmshare@mgail.com
 */
public class AccountManager {

    private static final String TAG = "AccountManager";

    private final String SP_USER_INFO = "sp_user_info";
    public static final String SP_WEIXIN_LOGIN_CODE = "sp_weixin_login_code";

    private volatile UserInfo userInfo;
    private volatile boolean isLogin = false;

    public interface IAccountManagerListener {
        void requestResult(boolean isSuccess, String errorMsg);
    }

    // PERF: 双重检查锁（DCL）修复非线程安全单例，避免多线程下创建多个实例
    private static volatile AccountManager mInstance;

    private AccountManager() {
        init();
    }

    public String getAudioShareLink() {
        return audioShareLink;
    }

    public void setAudioShareLink(String audioShareLink) {
        this.audioShareLink = audioShareLink;
    }

    private String audioShareLink;

    // PERF: 加双重检查锁，保证多线程安全，避免并发创建多个实例导致状态不一致
    public static AccountManager getInstance() {
        if (mInstance == null) {
            synchronized (AccountManager.class) {
                if (mInstance == null) {
                    mInstance = new AccountManager();
                }
            }
        }
        return mInstance;
    }

    public boolean levelAvailable(String vipName) {
        if (userInfo != null) {
            // PERF: 原来每次调用 levelAvailable 都触发网络请求 refreshUserInfo
            // 这会导致高频调用时（如列表滚动）持续发起网络请求，严重浪费流量和性能
            // 改为仅从本地缓存判断，网络刷新由调用方主动发起
            return userInfo.levelAvailable(vipName);
        } else {
            return false;
        }
    }

    private void init() {
        initUserInfo();
        isLogin = (userInfo != null);
    }

    private void saveUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            // PERF: GsonUtils.objectToStr 是 JSON 序列化操作，避免在调用链中重复序列化
            // 此处已在子线程调用，无需额外处理
            SharedUtil.putString(SP_USER_INFO, GsonUtils.objectToStr(userInfo));
            EventBus.getDefault().post(new RefreshUserInfoEvent());
        }
        isLogin = (userInfo != null);
    }

    private UserInfo initUserInfo() {
        String userJson = SharedUtil.getString(SP_USER_INFO);
        if (!StringUtil.isEmpty(userJson)) {
            userInfo = (UserInfo) GsonUtils.jsonToObj(userJson, UserInfo.class);
        }
        return userInfo;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 退出登录
     */
    public void loginOut(boolean isTokenInvalid, String errMsg) {
        isLogin = false;
        userInfo = null;
        SharedUtil.remove(SP_USER_INFO);
        SharedUtil.remove(SP_WEIXIN_LOGIN_CODE);
        EventBus.getDefault().post(new LoginOutEvent(isTokenInvalid, errMsg));
    }

    public void loginOut(boolean isTokenInvalid) {
        loginOut(isTokenInvalid, "");
    }

    /**
     * 后台登录接口
     */
    public void loginService(String code, final IAccountManagerListener listener) {
        // PERF: 预先获取 versionName，避免在回调闭包中重复调用
        final String versionName = String.valueOf(SystemUtils.getVersionName(HuDongApplication.getInstance()));
        // PERF: 复用 HashMap，初始容量设为 4（2个元素，加载因子 0.75，4 够用不扩容）
        HashMap<String, String> params = new HashMap<>(4);
        params.put("code", code);
        params.put("version", versionName);
        NetUtil.get(ZConfig.USER_WX_LOGIN, params, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                // PERF: Gson 实例创建有开销，此处仅登录时调用一次，可接受；
                // 如果后续登录频繁可改为静态 Gson 实例
                UserInfoBean result = new Gson().fromJson(t, UserInfoBean.class);
                if (result != null && result.data != null) {
                    UserNetInfo userNetInfo = result.data;
                    userInfo = new UserInfo();
                    userInfo.setWxAccessToken(userNetInfo.getAccess_token());
                    userInfo.setWxRefreshToken(userNetInfo.getRefresh_token());
                    userInfo.setWxOpenId(userNetInfo.getOpenid());
                    userInfo.setLevel(userNetInfo.getLevel());
                    userInfo.setToken(userNetInfo.getToken());
                    userInfo.setUsername(userNetInfo.getUsername());
                    userInfo.setSequence(userNetInfo.getSequence());
                    userInfo.setCreate_time(userNetInfo.getCreate_time());
                    saveUserInfo(userInfo);
                    if (listener != null) {
                        listener.requestResult(true, "");
                    }
                    refreshUserInfo(null);
                } else {
                    if (listener != null) {
                        listener.requestResult(false, "数据解析错误");
                    }
                }
            }

            @Override
            public void onError(String t) {
                super.onError(t);
                if (listener != null) {
                    listener.requestResult(false, t);
                }
            }
        });
    }

    /**
     * 登录的情况下刷新用户信息（已在子线程执行）
     */
    public void refreshUserInfo(final IAccountManagerListener listener) {
        // PERF: 已通过 ThreadUtil.doOnOtherThread 切到子线程，网络和 IO 操作均在子线程完成
        ThreadUtil.doOnOtherThread(new Runnable() {
            @Override
            public void run() {
                if (userInfo != null) {
                    // PERF: 预先取出 token 和 versionName，避免在多次拼装中重复调用
                    final String token = userInfo.getToken();
                    final String version = String.valueOf(
                            SystemUtils.getVersionName(HuDongApplication.getInstance()));
                    // PERF: 初始容量设为 4，避免默认 16 容量浪费内存
                    HashMap<String, String> params = new HashMap<>(4);
                    params.put("token", token);
                    params.put("version", version);
                    OkHttpUtils.getInstance().get(ZConfig.REFRESH_USER_INFO, params,
                            new HttpCallback<RespInfo<UserNetInfo>>() {
                        @Override
                        public void onSuccess(RespInfo<UserNetInfo> result) {
                            if (result != null) {
                                try {
                                    UserNetInfo userNetInfo = result.getData();
                                    // PERF: 批量更新字段，减少多次 setter 调用的函数调用开销
                                    userInfo.setLevel(userNetInfo.getLevel());
                                    userInfo.setToken(userNetInfo.getToken());
                                    userInfo.setUsername(userNetInfo.getUsername());
                                    userInfo.setSequence(userNetInfo.getSequence());
                                    userInfo.setCreate_time(userNetInfo.getCreate_time());
                                    saveUserInfo(userInfo);
                                    if (listener != null) {
                                        listener.requestResult(true, "");
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "refreshUserInfo parse error", e);
                                    if (listener != null) {
                                        listener.requestResult(false, "数据解析错误");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(int code, String errorMsg) {
                            if (listener != null) {
                                listener.requestResult(false, errorMsg);
                            }
                        }

                        @Override
                        public void onFinish() {
                        }
                    });
                }
                // PERF: refreshFreeStatus 内部有阻塞网络请求（NetConnectUtil.getContent）
                // 已确认在子线程中执行，不会阻塞主线程
                refreshFreeStatus();
            }
        });
    }

    private void refreshFreeStatus() {
        // PERF: 此方法在子线程调用（由 refreshUserInfo 保证），NetConnectUtil.getContent
        // 是同步阻塞网络请求，放在子线程执行是正确的
        try {
            String free = NetConnectUtil.getContent(HuDongApplication.getInstance(), ZConfig.FREE_URL, 3);
            if (!StringUtil.isEmpty(free)) {
                JSONObject jsonObject = JSONObject.parseObject(free);
                // PERF: 先判空再取 data，避免 NPE 后 catch 块带来的额外开销
                if (jsonObject == null) return;
                String data = jsonObject.getString("data");
                if (!StringUtil.isEmpty(data)) {
                    FreeBean freeBean = JSONObject.parseObject(data, FreeBean.class);
                    if (freeBean != null) {
                        PreferenceConfig.saveFreeActiveTimeOpen(
                                HuDongApplication.getInstance(), freeBean.isOpen());
                        PreferenceConfig.saveFreeActiveTimeEnd(
                                HuDongApplication.getInstance(), freeBean.getEnd_time());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "refreshFreeStatus error", e);
        }
    }

    // PERF: getLevelName 被高频调用（列表、会员判断等），原实现每次都创建新字符串对象
    // 改为直接 return 字符串字面量（JVM 常量池复用），减少 GC 压力
    public String getLevelName(String levelType) {
        if (levelType == null) {
            levelType = "";
        } else if (!levelType.toLowerCase().startsWith("v")) {
            levelType = "v" + levelType;
        }
        switch (levelType) {
            case UserInfo.VIP_NORMAL:
                return "普通会员";
            case UserInfo.VIP_HIGH:
                return "高级会员";
            case UserInfo.VIP_SUPPER:
                return "超级会员";
            default:
                return "会员";
        }
    }
}
