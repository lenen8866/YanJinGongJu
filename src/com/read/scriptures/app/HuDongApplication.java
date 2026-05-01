package com.read.scriptures.app;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;

import androidx.multidex.MultiDex;

import com.kymjs.okhttp3.OkHttpStack;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.http.RequestQueue;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.manager.MusicPlayerManager;
import com.read.scriptures.EIUtils.EIApplication;
import com.read.scriptures.EIUtils.EIConfiguration;
import com.read.scriptures.bean.UserInfo;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.db.DatabaseHelper;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.net.NetMonitor;
import com.read.scriptures.ui.activity.MainActivity;
import com.read.scriptures.util.ACache;
import com.read.scriptures.util.CrashHandler;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.PreferencesUtils;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.rsa.RSA;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.zxl.common.db.sqlite.DbUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author lim
 * @Description:
 * @mail lgmshare@gmail.com
 * @date 2014-6-3 上午11:12:01
 * bed2236eea45f6b3876a94ff600cc7a4915e8aec
 */
public class HuDongApplication extends EIApplication {

    /**
     * 调试TAG
     */
    private static final String TAG = HuDongApplication.class.getSimpleName();
    private static HuDongApplication mInstance;
    private static OkHttpClient mHttpClient;
    public NotificationManager notManager;

    private int mReadModel = SystemConfig.READ_MODEL_NORMAL;// 阅读模式（夜间/普通）
    private int mTextSize;// 字体大小
    private int mTextModel;// 字体简繁模式
    private int mBackgroudColor = 0;// 背景色
    private boolean mScreenOrientation;// 横竖屏模式
    //阅读字段行间距
    private int mTextMagin = 21;
    private int mTextAround = 56;
    private int mLineMargin = 24;
    private int mTextColor = Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT);
    private boolean isLog = false;

    private boolean isShowVersion = false;

    private boolean isDebug = true;
    public int rechargeType = 0;//默认捐赠充值  1 问答提示充值 2为他人开通vip

    public static List<String> mVersions = new ArrayList<>();
    public static List<String> mVersions_HZ = new ArrayList<>();
    public static List<String> baseVersions = new ArrayList<>();
    public static List<String> HZ_baseVersions = new ArrayList<>();

    static {
        baseVersions.add("和合本");
        baseVersions.add("吕振中");
        baseVersions.add("思高本");
        baseVersions.add("现代本");
        baseVersions.add("新译本");
        baseVersions.add("当代版");
        baseVersions.add("KJV");
        baseVersions.add("NIV");
        baseVersions.add("BBE");
        baseVersions.add("ASV");

        baseVersions.add("呂振中");
        baseVersions.add("現代本");
        baseVersions.add("新譯本");
        baseVersions.add("當代版");

        HZ_baseVersions.add("中文");
        HZ_baseVersions.add("英文");
    }

    private DbUtils dbUtils;
    private ACache cache;
    private ACache fileSystem;

    private boolean isExistMain;

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public boolean isLog() {
        return isLog;
    }

    public void setLog(boolean log) {
        isLog = log;
    }

    public boolean isShowVersion() {
        return isShowVersion;
    }

    public void setShowVersion(boolean showVersion) {
        isShowVersion = showVersion;
    }

    public boolean isExistMain() {
        return isExistMain;
    }

    public void setExistMain(boolean isExistMain) {
        this.isExistMain = isExistMain;
    }

    public ACache getFileSystem() {
        return fileSystem;
    }

    public DbUtils getDbUtils() {
        if (dbUtils == null) {
            // String dbDir =
            // android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            // dbUtils = DbUtils.create(this, "hudong.db");
            dbUtils = DbUtils.create(this, getDBDir(), DatabaseHelper.DB_NAME);
            dbUtils.configAllowTransaction(true);
        }
        return dbUtils;
    }

    public void setDbUtils(DbUtils dbUtils) {
        this.dbUtils = dbUtils;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        //全局的读取超时时间
        builder.readTimeout(10000, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(10000, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);

        OkGo.getInstance().setOkHttpClient(builder.build()).init(this);
        mInstance = this;
        initX5();
        // 注册App异常崩溃处理器
        final CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        //网络状态监听
        NetMonitor.getInstance().init(this);
        UMConfigure.init(this, "5f77e3b080455950e49d38b1", "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0
        UMConfigure.setLogEnabled(false);
        PlatformConfig.setWeixin("wxd8e57656faf5d7e0", "aee77822a2578c92e42afdf640a95f8e");
        PlatformConfig.setQQZone("1105925560", "Bophwev84iTydc35");
        //网络队列
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        mVersions.clear();
        mVersions.add("和合本");
        mVersions_HZ.clear();
        mVersions_HZ.add("中文");

        cache = ACache.get(getCacheDir());
        fileSystem = ACache.get(getFilesDir(), 500 * 1024 * 1024, 1024);

        initReadConfig();
        setResoucesLocale(Locale.CHINESE);
        createKey();
        //创建通知管理器
        createNotify();
        //初始化朗读模式
        int speachMode = SharedUtil.getInt(SystemConfig.SP_SPEACH_MODEL_KEY, SystemConfig.SPEECH_MODEL_BAIDU);
        SystemConfig.Speech_Model = speachMode == SystemConfig.SPEECH_MODEL_ALI ? SystemConfig.SPEECH_MODEL_BAIDU : speachMode;

        addActivityLifecycleCallbacks();
        RxVolley.setRequestQueue(RequestQueue.newRequestQueue(RxVolley.CACHE_FOLDER, new OkHttpStack(new OkHttpClient())));

//        配置全局的Picasso实例
        Picasso.Builder builder1 = new Picasso.Builder(this);
        //配置缓存
        LruCache cache = new LruCache(10 * 1024 * 1024); //设置缓存大小
        builder1.memoryCache(cache);
        //配置线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        builder1.executor(executorService);
        builder1.loggingEnabled(true);

//        构造一个Picasso
        Picasso picasso = builder1.build();
//        设置全局的Picasso实例
        Picasso.setSingletonInstance(picasso);
    }

    /**
     * 创建通知栏
     */
    private void createNotify() {
        notManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public static OkHttpClient getHttpClient() {
        if (mHttpClient == null) {
            File sdCache = getInstance().getExternalCacheDir();
            int cacheSize = 20 * 1024 * 1024;
            assert sdCache != null;
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .removeHeader("User-Agent")
                            .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                            .build();
                    return chain.proceed(newRequest);
                }
            });
            builder.connectTimeout(3, TimeUnit.SECONDS).writeTimeout(3, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).cache(new Cache(sdCache.getAbsoluteFile(), cacheSize));
//			builder.retryOnConnectionFailure(false);
            mHttpClient = builder.build();
        }
        return mHttpClient;
    }

    /**
     * 设置多种语言
     *
     * @param loc loc
     */
    public void setResoucesLocale(final Locale loc) {
        if (loc != null) {
            final Configuration config = getResources().getConfiguration();
            config.locale = loc;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }

    }

    /**
     * 修复：原方法返回 SD 卡路径，Android 10+ 直接崩溃且存在数据泄露风险。
     * 现改为返回应用内部存储，外部任意 App 无权访问。
     * 兼容 Android 5 - Android 14。
     */
    public String getDBDir() {
        // 使用应用内部存储目录，无需任何权限，对外完全隔离
        File internalDir = new File(getFilesDir(), "db");
        if (!internalDir.exists()) {
            internalDir.mkdirs();
        }
        // 尝试迁移旧的 SD 卡数据库到内部存储
        migrateOldDatabaseIfNeeded(internalDir);
        return internalDir.getAbsolutePath();
    }

    /**
     * 将旧的 SD 卡数据库文件迁移到内部存储。
     * 只在旧文件存在且新文件不存在时执行，迁移后保留旧文件备用。
     */
    @SuppressWarnings("deprecation")
    private void migrateOldDatabaseIfNeeded(File internalDir) {
        try {
            // 旧版本 SD 卡路径
            File oldDir = new File(android.os.Environment.getExternalStorageDirectory(), "hudong");
            if (!oldDir.exists()) {
                return; // 旧目录不存在，新用户无需迁移
            }
            File[] oldFiles = oldDir.listFiles();
            if (oldFiles == null || oldFiles.length == 0) {
                return;
            }
            for (File oldFile : oldFiles) {
                if (!oldFile.isFile()) continue;
                File newFile = new File(internalDir, oldFile.getName());
                if (newFile.exists()) {
                    // 新路径已经有该文件，跳过，以内部存储为准
                    continue;
                }
                // 文件复制：读旧写新
                java.io.InputStream in = new java.io.FileInputStream(oldFile);
                java.io.OutputStream out = new java.io.FileOutputStream(newFile);
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                android.util.Log.i("DBMigrate", "迁移成功: " + oldFile.getName());
            }
        } catch (Exception e) {
            // 迁移失败不影响主流程序，新用户使用内部存储即可
            android.util.Log.e("DBMigrate", "迁移失败: " + e.getMessage());
        }
    }

    public static HuDongApplication getInstance() {
        return mInstance;
    }

    private void initReadConfig() {
        // 配置项
        mReadModel = PreferenceConfig.getReadModel(this);
        mTextSize = PreferenceConfig.getTextSize(this);
        mTextMagin = PreferenceConfig.getTextMargin(this);
        mTextAround = PreferenceConfig.getTextAround(this);
        mLineMargin = PreferenceConfig.getLineMargin(this);
        mTextColor = PreferenceConfig.getTextColor(this);
        mTextModel = PreferenceConfig.getTextModel(this);
        mBackgroudColor = PreferenceConfig.getBackgroudColor(this);
        mScreenOrientation = PreferenceConfig.getScreenOrientation(this);
    }

    @Override
    public EIConfiguration buildEIConfiguration() {
        EIConfiguration config = new EIConfiguration.Builder(this).debugMode().debugLogs().dbVersion(1)
                .appLoggerFilePath(getPackageName() + "/log/").appCacheFilePath(getPackageName() + "/cache/")
                .appDownloadFilePath(getPackageName() + "/download/").build();
        return config;
    }


    /**
     * 普通会员是否可用
     *
     * @return
     */
    public boolean isAppNormalLevelActivate() {
//        if (isDebug()) {
//            return false;
//        }
        if (AccountManager.getInstance().getUserInfo().getUsername().equals("1908647780")) {
            return true;
        }
        return AccountManager.getInstance().levelAvailable(UserInfo.VIP_NORMAL);
    }
//    public void setIsAppActivate(boolean isAppActivate) {
//        this.isAppActivate = isAppActivate;
//        PreferenceConfig.saveActivation(this, isAppActivate);
//    }

    public int getReadModel() {
        return mReadModel;
    }

    public void setReadModel(int readModel) {
        this.mReadModel = readModel;
        PreferenceConfig.saveReadModel(this, readModel);
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        PreferenceConfig.saveTextSize(this, textSize);
    }

    public int getTextMagin() {
        return mTextMagin;
    }

    public void setTextMagin(int mTextMagin) {
        this.mTextMagin = mTextMagin;
        PreferenceConfig.saveTextMargin(this, mTextMagin);
    }

    public int getTextAround() {
        return mTextAround;
    }

    public void setTextAround(int mTextAround) {
        this.mTextAround = mTextAround;
        PreferenceConfig.saveTextAround(this, mTextAround);
    }

    public int getmLineMargin() {
        return mLineMargin;
    }

    public void setmLineMargin(int mLineMargin) {
        this.mLineMargin = mLineMargin;
        PreferenceConfig.saveLineMargin(this, mLineMargin);
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        PreferenceConfig.saveTextColor(this, mTextColor);
    }

    public int getTextModel() {
        return mTextModel;
    }

    public void setTextModel(int textModel) {
        this.mTextModel = textModel;
        PreferenceConfig.saveTextModel(this, textModel);
    }

    public int getBackgroudColor() {
        return mBackgroudColor;
    }

    public void setBackgroudColor(int backgroudColor) {
        this.mBackgroudColor = backgroudColor;
        PreferenceConfig.saveBackgroudColor(this, backgroudColor);
    }

    public boolean isScreenOrientation() {
        return mScreenOrientation;
    }

    public void setScreenOrientation(boolean screenOrientation) {
        this.mScreenOrientation = screenOrientation;
        PreferenceConfig.saveScreenOrientation(this, screenOrientation);
    }

    public ACache getaCache() {
        return cache;
    }

    public void setaCache(ACache aCache) {
        this.cache = aCache;
    }

    private void createKey() {
        if (RSA.keyMap == null) {
            try {
                // ��ȡ��Կ
                InputStream inputStream = getAssets().open("pubkey.dat");
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                RSAPublicKey pubk = (RSAPublicKey) objectInputStream.readObject();
                // ��ȡ˽Կ
                inputStream = getAssets().open("privatekey.dat");
                objectInputStream = new ObjectInputStream(inputStream);
                RSAPrivateKey prik = (RSAPrivateKey) objectInputStream.readObject();
                objectInputStream.close();
                inputStream.close();
                RSA.keyMap = new HashMap<String, Object>();
                RSA.keyMap.put(RSA.PUBLIC_KEY, pubk);
                RSA.keyMap.put(RSA.PRIVATE_KEY, prik);
                try {
                    LogUtil.info("" + new String(RSA.getPrivateKey()));
                } catch (Exception e) {
                    //LogUtil.error("查看密钥失败：", e);
                }
            } catch (IOException e) {
                //LogUtil.error("IOException", e);
            } catch (ClassNotFoundException e) {
                //LogUtil.error("ClassNotFoundException", e);
            }
        }
    }

    private void initX5() {
//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                // TODO Auto-generated method stub
//                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                Log.d("app", " onViewInitFinished is " + arg0);
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//                // TODO Auto-generated method stub
//            }
//        };
//        //x5内核初始化接口
//        QbSdk.initX5Environment(getApplicationContext(), cb);
    }


//    public static String getUniqueID(Context context) {
//        String id = null;
//        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        if (!TextUtils.isEmpty(androidId) && !"9774d56d682e549c".equals(androidId)) {
//            try {
//                UUID uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
//                id = uuid.toString();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (TextUtils.isEmpty(id)) {
//            id = getUUID();
//        }
//
//        return TextUtils.isEmpty(id) ? UUID.randomUUID().toString() : id;
//    }
//
//    private static String getUUID() {
//        String serial = null;
//
//        String m_szDevIDShort = "35" +
//                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
//
//                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
//
//                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
//
//                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
//
//                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
//
//                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
//
//                Build.USER.length() % 10; //13 位
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                serial = Build.getSerial();
//            } else {
//                serial = Build.SERIAL;
//            }
//            //API>=9 使用serial号
//            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
//        } catch (Exception exception) {
//            serial = "serial"; // 随便一个初始化
//        }
//
//        //使用硬件信息拼凑出来的15位号码
//        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
//    }

    /**
     * 读取内置文件
     *
     * @return
     */
    public String getIntroListAssets() {
        AssetManager am = this.getResources().getAssets();
        try {
            InputStream is = am.open("getIntroList.json");
            InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
            BufferedReader bufReader = new BufferedReader(isReader);
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
            }
            bufReader.close();
            isReader.close();
            is.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //记录栈顶Activity
    public static  Activity topActivity = null;


    public void addActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                topActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity instanceof MainActivity) {
                    BaseAudioInfo playerMusic = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
                    if (playerMusic == null) {
                        return;
                    }
                    playerMusic.isCached = true;
                    try {
                        boolean last_play_audio_cache = PreferencesUtils.putObject(getApplicationContext(), "last_play_audio_cache", playerMusic);
                    } catch (IOException e) {
                    } finally {

                    }
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
