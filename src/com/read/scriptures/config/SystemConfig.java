package com.read.scriptures.config;

import com.baidu.tts.client.TtsMode;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.util.OfflineResource;

/**
 * @ClassName: BaseConfig
 * @Description: 应用基本配置参数
 * @author lim
 * @mail lgmshare@gmail.com
 * @date 2014-6-3 上午11:12:20
 */
public class SystemConfig {

    /////////////////// 应用发布时修改////////////////////////
    /** 应用名称 */
    public static final String APP_NAME = "base";
    /** 应用BUILD版本号 */
    public static final String VERSION_BUILD = "1.0";
    /** 应用数据库版本号 */
    public static final int DB_VERSION = 1;
    /** 应用调试模式 */
    public static final boolean DEBUG_MODE = true;
    /** 日志统计模式 */
    public static final boolean DEBUG_ANALYTICS = true;
    /** http连接url */
    public static final String HTTP_BASE_URL = "http://115.29.77.223:8668/shidai/app/api/";
    /** http user agent */
    public static final String USER_AGENT = "huizhuang;shengqi;" + HuDongApplication.getInstance().getVersionName()
            + ";android-phone";
    /////////////////// 应用开发时配置////////////////////////
    /** 客户端的DES 加密的key */
    public static final String MD5_KEY = "shidai123456";
    /** 百度地图AK,应用签名包名绑定 */
    public static final String BAIDU_MAP_AK = "697f50541f8d4779124896681cb6584d";
    /** 微信key */
//    public static final String WX_KEY = "wx3c1726b5a53b8155";
//    public static final String WX_APP_SECRET = "dcc981935412308574acb8736e874aa1";
    public static final String WX_KEY = "wxd8e57656faf5d7e0";
    public static final String WX_APP_SECRET = "aee77822a2578c92e42afdf640a95f8e";
    /** 微信商户号 */
    public static final String WX_STORE_KEY = "1507254851";
    /** 百度语音 */
    public static final String BAIDU_APP_ID = "22569158";
    public static final String BAIDU_APP_KEY = "5V5oacmSUuzdy1zzGRVTANjq";
    public static final String BAIDU_SECRET_KEY = "wbqhSQergCHWAtKuecfiPgkjBmfqxhEC";


    // 修复：原来 TtsMode.MIX 会进行离线鉴权检验（auth()）
    // 离线鉴权需要百度后台登记的包名与 App 包名一致，否则鉴权失败导致所有手机报「首次初始化需要连接网络」
    // 改为 TtsMode.ONLINE （纯在线模式），不走 auth() 鉴权，有网就能用
    public static TtsMode ttsMode = TtsMode.ONLINE;

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    public static String offlineVoice = OfflineResource.VOICE_FEMALE;

    public static final int SPEECH_MODEL_XF = 1;//讯飞
    public static final int SPEECH_MODEL_BAIDU = 2;//百度


    public static int Speech_Model = SPEECH_MODEL_XF;//默认讯飞，模式A（百度）已隐藏
    
    public static String readContent = "";
    /** splash加载延迟等待时间 */
    public static final int SPLASH_TIME_DELAY = 1 * 1000;
    /** 每页加载数据大小 */
    public static final int PAGE_SIZE = 10;
    public static final int READ_MODEL_NORMAL = 0;
    public static final int READ_MODEL_NIGHT = 1;
    public static final int TEXT_MODEL_NORMAL = 0;
    public static final int TEXT_MODEL_FANTI = 1;

    public static final String DEFAULT_READ_BACKGROUND_DEFAULT = "#dcd0ba";
    public static final String DEFAULT_READ_BACKGROUND_NIGHT = "#2b2b2b";
    public static final String DEFAULT_READ_TEXT_COLOR_DEFAULT = "#333333";
    public static final String DEFAULT_READ_TEXT_COLOR_NIGHT = "#bdbdbd";
    public static final String DEFAULT_READ_TEXT_COLOR_KEY_WORD = "#ff0000";
    public static final String DEFAULT_READ_REMARK_TEXT_BACKGROUND_COLOR_KEY_WORD = "#fff59d";

    /**
     * 欢迎界面图片缓存到文件的key
     */
    public static final String WELCOME_IMAGE_KEY = "welcomeImage";
    //zip密码
    public static final String ZIP_PASSWORD = "tQh2DugguuzN1wjZ";


    public static final String SP_SPEACH_MODEL_KEY = "SP_SPEACH_MODEL";

}
