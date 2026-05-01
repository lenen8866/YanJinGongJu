package com.read.scriptures.config;

public class ZConfig {

    /**
     * 服务器地址
     */
//    public static final String URL = //
//            "http://101.200.169.236/index.php?s=/";
    // "http://192.168.0.2";
    /**
     * 欢迎页面接口
     */
//    public static final String WELCOME_URL = "http://101.200.169.236/index.php?s=/Admin/Welcome/welcomePort";


    /**
     * 获取banner接口
     */
//    public static final String GETBANNER = URL + "Admin/Welcome/bannerPort";


    /**
     * 字体列表
     */
    public static final String FONT_LIST = "https://book.sdacn.cn/api/v1/books/fonts";

    /**
     * 购买字体
     */
    public static final String BUY_FONT = "https://book.sdacn.cn/api/v1/books/buy";


    /**
     * 书籍更新(新接口)
     */
//    public static final String BOOKLIBUPDATE = URL + "Admin/Book/bookPort";


//    /**
//     * 官网地址
//     */
//    public static final String OFFICAL_WEBSITE = "http://bbs.sdattg.com";
//
//    /**
//     * 我的手机号
//     */
//    public static final String MY_PHONE = "17084904383";
//
//    /**
//     * 支付URL
//     */
//    public static final String PAY_URL = "http://101.200.169.236/index.php/home/order/pay";
//
//
//    /**
//     * 注册序列号url
//     */
//    public static final String REGISTER_URL = "http://101.200.169.236/index.php?s=/Home/App/judge_user&sequence=";


    /*
     * 分享json
     */
//    public static final String SHARE_URL = "http://101.200.169.236/my/FenXiang_json/share.json";


    //=====================================调整==================================================

    public static final String SERVICE_URL = "https://book.sdacn.cn";

    /**
     * 欢迎页
     */
    public static final String WELCOME_URL = SERVICE_URL + "/api/v1/basis/welcomes";

    /**
     * banner
     */
    public static final String GETBANNER = SERVICE_URL + "/api/v1/basis/carousel";


    /**
     * 获取audio类型
     */
    public static final String GET_AUDIO_CATE = SERVICE_URL + "/api/v1/Multimedia/audiogrouping";

    /**
     * 添加收藏
     */
    public static final String GET_ADD_COLLECT = SERVICE_URL + "/api/v1/user/collect";

    /**
     * 获取合并audio
     */
    public static final String GET_AUDIO_COMBINE = SERVICE_URL + "/api/v1/Multimedia/auidoCombine";

    /**
     * 获取单个audio
     */
    public static final String GET_AUDIO_SINGLE = SERVICE_URL + "/api/v1/multimedia/audiofind";

    /**
     * 获取随机背景
     */
    public static final String GET_RANDOM_BG = SERVICE_URL + "/api/v1/Ks_Video/list";


    /**
     * 获取书本对应章节audio
     */
    public static final String GET_AUDIO_LIST = SERVICE_URL + "/api/v1/multimedia/audiolists";

    /**
     * banner
     */
    public static final String GETSHARE = SERVICE_URL + "/api/v1/Systems/sharetip";

    /**
     * 书籍更新
     */
    public static final String BOOKLIBUPDATE = SERVICE_URL + "/api/v1/books?order=desc";

    /*
     * 免费使用接口url
     */
    public static final String FREE_URL = SERVICE_URL + "/api/v1/systems/freetime";

    /**
     * 微信登录
     */
    public static final String USER_WX_LOGIN = SERVICE_URL + "/index.php/api/v1.wxlogin";

    /*
     * 分享json
     */
    public static final String SHARE_SETTING_URL = SERVICE_URL + "/sharesetting.json";
    /**
     * 后台注册
     */
    public static final String USER_REG = SERVICE_URL + "/api/v1/user/reg";
    /**
     * 后台登录
     */
    public static final String USER_LOGIN = SERVICE_URL + "/api/v1/user/login";
    /**
     * 获取用户信息
     */
    public static final String REFRESH_USER_INFO = SERVICE_URL + "/api/v1/user/userinfo";
    /**
     * 获取会员支付金额信息
     */
    public static final String LEVEL_ACTIVE_AMOUNT_URL = SERVICE_URL + "/api/v1/paying/payment";

    /**
     * 获取推荐的支付方式
     */
    public static final String RECOMMEND_PAY_TYPE_URL = SERVICE_URL + "/api/v1/paying/pay";

    /**
     * 购买下单
     */
    public static final String PAY_NEW_ORDER = SERVICE_URL + "/api/v1/user/buy";

    /**
     * 查询订单详情
     */
    public static final String PAY_ORDER_DETAILS = SERVICE_URL + "/api/v1/paying/detail";

    /**
     * 更新接口
     */
    public static final String UPDATE = SERVICE_URL + "/api/v1/systems/versions";

    /**
     * 无偿捐赠
     */
    public static final String DONATION = SERVICE_URL + "/api/v1/user/donation";

    /**
     * 包名列表接口
     */
    public static final String PACKAGE = SERVICE_URL + "/api/v1/systems/package";

    /**
     * 用户隐私协议
     */
    public static final String H5_PRIVACY_POLICY = "http://sdacn.cn/YinSiXieYi/ysxy.html";
    /**
     * 用户服务协议
     */
    public static final String H5_SERVICE_PROTOCOL = "http://sdacn.cn/YinSiXieYi/fwtl.html";

}
