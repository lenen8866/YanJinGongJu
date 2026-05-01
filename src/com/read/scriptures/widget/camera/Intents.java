package com.read.scriptures.widget.camera;

/**
 *
 * 这个类提供的常量发送意图条码扫描器时使用。
 * 这些字符串是有效的API，并且不能被改变。
 * 一些常量的作用俺看也不懂了
 * @author zWX243327
 * @version V100R001C13, 2015-10-9
 * @since V100R001C13
 */
public final class Intents
{
    //私有构造器
    private Intents()
    {
    }
    
    /**
     * 定义扫描框常量
     * @author  s00223601
     * @version  [版本号, 2015-10-8]
     * @since  [产品/模块版本]
     */
    public static final class Scan
    {
        /**
         * 发送此意图打开扫描模式下的条码应用程序，找到一个条形码，并返回结果
         */
        public static final String ACTION = "com.google.zxing.client.android.SCAN";
        
        /**
         * 扫描模式
         */
        public static final String MODE = "SCAN_MODE";
        
        /**
         * 扫描格式
         */
        public static final String SCAN_FORMATS = "SCAN_FORMATS";
        
        /**
         * 字符模式
         */
        public static final String CHARACTER_SET = "CHARACTER_SET";
        
        /**
         * 商品模式
         */
        public static final String PRODUCT_MODE = "PRODUCT_MODE";
        
        /**
         * ONE_D模式
         */
        public static final String ONE_D_MODE = "ONE_D_MODE";
        
        /**
         * 二维码模式
         */
        public static final String QR_CODE_MODE = "QR_CODE_MODE";
        
        /**
         * 数字矩阵模式
         */
        public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";
        
        /**
         * 扫描结果
         */
        public static final String RESULT = "SCAN_RESULT";
        
        /**
         * 结果格式
         */
        public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";
        
        /**
         * 保存历史
         */
        public static final String SAVE_HISTORY = "SAVE_HISTORY";
        
        private Scan()
        {
        }
    }
    
    /**
     * 解码类
     * @author  s00223601
     * @version  [版本号, 2015-10-8]
     * @since  [产品/模块版本]
     */
    public static final class Encode
    {
        /**
         * ACTION
         */
        public static final String ACTION = "com.google.zxing.client.android.ENCODE";
        
        /**
         * DATA
         */
        public static final String DATA = "ENCODE_DATA";
        
        /**
         * TYPE
         */
        public static final String TYPE = "ENCODE_TYPE";
        
        /**
         * FORMAT
         */
        public static final String FORMAT = "ENCODE_FORMAT";
        
        private Encode()
        {
        }
    }
    
    /**
     * SearchBookContents
     * @author  s00223601
     * @version  [版本号, 2015-10-8]
     * @since  [产品/模块版本]
     */
    public static final class SearchBookContents
    {
        /**
         * action
         */
        public static final String ACTION = "com.google.zxing.client.android.SEARCH_BOOK_CONTENTS";
        
        /**
         * 书的编号
         */
        public static final String ISBN = "ISBN";
        
        /**
         * 查询
         */
        public static final String QUERY = "QUERY";
        
        private SearchBookContents()
        {
        }
    }
    
    /**
     * WIFI连接
     * @author  s00223601
     * @version  [版本号, 2015-10-8]
     * @since  [产品/模块版本]
     */
    public static final class WifiConnect
    {
        /**
         * 内部意图用于触发连接到Wi-Fi网络
         */
        public static final String ACTION = "com.google.zxing.client.android.WIFI_CONNECT";
        
        /**
         * SSID
         */
        public static final String SSID = "SSID";
        
        /**
         * 类型
         */
        public static final String TYPE = "TYPE";
        
        /**
         * 密码
         */
        public static final String PASSWORD = "PASSWORD";
        
        private WifiConnect()
        {
        }
    }
    
    /**
     * 分享
     * @author  s00223601
     * @version  [版本号, 2015-10-8]
     * @since  [产品/模块版本]
     */
    public static final class Share
    {
        
        /**
         * action
         */
        public static final String ACTION = "com.google.zxing.client.android.SHARE";
        
        private Share()
        {
        }
    }
}
